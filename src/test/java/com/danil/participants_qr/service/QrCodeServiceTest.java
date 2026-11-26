package com.danil.participants_qr.service;

import com.danil.participants_qr.dto.request.ScanRequest;
import com.danil.participants_qr.dto.response.ParticipantDto;
import com.danil.participants_qr.exceptions.InvalidQrCodeException;
import com.danil.participants_qr.exceptions.ParticipantIdNotFoundException;
import com.danil.participants_qr.exceptions.ParticipantNotFoundException;
import com.danil.participants_qr.exceptions.QrCodeAlreadyExistsException;
import com.danil.participants_qr.model.Participant;
import com.danil.participants_qr.model.QrCode;
import com.danil.participants_qr.repository.ParticipantRepository;
import com.danil.participants_qr.repository.QrCodeRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class QrCodeServiceTest {
    @Mock
    private QrCodeRepository repository;

    @Mock
    private ParticipantRepository participantRepository;

    @InjectMocks
    private QrCodeService service;

    private Participant participant() {
        Participant participant = new Participant();
        participant.setId(1L);
        participant.setFirstName("First");
        participant.setSecondName("Second");
        participant.setLastName("Last");
        participant.setEmail("test@test.com");
        return participant;
    }

    private QrCode qrCode() {
        QrCode qrCode = new QrCode();
        qrCode.setParticipant(participant());
        qrCode.setId(UUID.randomUUID());
        return qrCode;
    }

    @Test
    void findAllQrCodes_shouldUseAscendingSort() {
        when(repository.findAll(any(Pageable.class))).thenReturn(Page.empty());

        assertNotNull(service.findAllQrCodes(0,10,"id","asc"));
        verify(repository, times(1)).findAll(any(Pageable.class));
    }

    @Test
    void findAllQrCodes_shouldUseDescendingSort() {
        when(repository.findAll(any(Pageable.class))).thenReturn(Page.empty());

        assertNotNull(service.findAllQrCodes(0, 10, "id", "desc"));
        verify(repository, times(1)).findAll(any(Pageable.class));
    }

    @Test
    void deleteAllUsedByParticipantId_shouldDeleteWhenExists() {
        when(participantRepository.existsById(1L)).thenReturn(true);

        service.deleteAllUsedByParticipantId(1L);

        verify(participantRepository, times(1)).existsById(1L);
        verify(repository, times(1)).deleteAllUsedByParticipantId(1L);
    }

    @Test
    void deleteAllUsedByParticipantId_ThrowWhenParticipantIdNotFound() {
        when(participantRepository.existsById(99L)).thenReturn(false);

        assertThrows(ParticipantIdNotFoundException.class,
                () -> service.deleteAllUsedByParticipantId(99L));

        verify(participantRepository, times(1)).existsById(99L);
        verify(repository, never()).deleteAllUsedByParticipantId(99L);
    }

    @Test
    void issue_shouldRevokeActiveCodeAndSaveNewOne() {
        Participant participant = participant();

        service.issue(participant);

        ArgumentCaptor<QrCode> captor = ArgumentCaptor.forClass(QrCode.class);
        InOrder inOrder = inOrder(repository);
        inOrder.verify(repository).revokeActive(eq(1L), any(Instant.class));
        inOrder.verify(repository).save(captor.capture());

        assertEquals(participant, captor.getValue().getParticipant());
    }

    @Test
    void scan_ShouldReturnParticipantAndIssueNewCode() {
        UUID token = UUID.randomUUID();
        Participant participant = participant();
        when(repository.markUsed(eq(token), any(Instant.class))).thenReturn(1);
        when(repository.findParticipantByCodeId(token)).thenReturn(Optional.of(participant));

        ParticipantDto result = service.scan(new ScanRequest(token));

        assertNotNull(result);
        assertEquals("First",result.firstName());
        verify(repository).revokeActive(eq(1L), any(Instant.class));
        verify(repository).save(any(QrCode.class));
    }

    @Test
    void scan_shouldThrowWhenCodeIsInvalid() {
        UUID token = UUID.randomUUID();
        when(repository.markUsed(eq(token), any(Instant.class))).thenReturn(0);

        assertThrows(InvalidQrCodeException.class,
                () -> service.scan(new ScanRequest(token)));

        verify(repository, never()).findParticipantByCodeId(any());
        verify(repository, never()).save(any(QrCode.class));
    }

    @Test
    void scan_shouldThrowWhenParticipantNotFound() {
        UUID token = UUID.randomUUID();
        when(repository.markUsed(eq(token), any(Instant.class))).thenReturn(1);
        when(repository.findParticipantByCodeId(token)).thenReturn(Optional.empty());

        assertThrows(ParticipantNotFoundException.class,
                () -> service.scan(new ScanRequest(token)));

        verify(repository, never()).save(any(QrCode.class));
    }

    @Test
    void create_shouldSaveQrCodeForParticipant() {
        Participant participant = participant();
        when(repository.save(any(QrCode.class))).thenAnswer(inv -> inv.getArgument(0));

        QrCode result = service.create(participant);

        ArgumentCaptor<QrCode> captor = ArgumentCaptor.forClass(QrCode.class);
        verify(repository).save(captor.capture());
        assertEquals(participant, captor.getValue().getParticipant());
        assertEquals(participant, result.getParticipant());
        verify(repository, never()).revokeActive(any(), any());
    }

    @Test
    void createQrCodeForParticipant_shouldCreateWhenNoActiveCode() {
        when(participantRepository.findById(1L)).thenReturn(Optional.of(participant()));
        when(repository.existsByParticipantIdAndUsedAtIsNullAndRevokedAtIsNull(1L)).thenReturn(false);
        when(repository.save(any(QrCode.class))).thenAnswer(inv -> inv.getArgument(0));

        var response = service.createQrCodeForParticipant(1L);

        assertNotNull(response);
        verify(repository, times(1)).save(any(QrCode.class));
    }

    @Test
    void createQrCodeForParticipant_shouldThrowWhenActiveCodeExists() {
        when(participantRepository.findById(1L)).thenReturn(Optional.of(participant()));
        when(repository.existsByParticipantIdAndUsedAtIsNullAndRevokedAtIsNull(1L)).thenReturn(true);

        assertThrows(QrCodeAlreadyExistsException.class,
                () -> service.createQrCodeForParticipant(1L));

        verify(repository, never()).save(any(QrCode.class));
    }
}
