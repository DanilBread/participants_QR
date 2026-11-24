package com.danil.participants_qr.service;

import com.danil.participants_qr.dto.request.CreateParticipantRequest;
import com.danil.participants_qr.exceptions.EmailAlreadyUsedException;
import com.danil.participants_qr.exceptions.ParticipantIdNotFoundException;
import com.danil.participants_qr.model.Participant;
import com.danil.participants_qr.model.QrCode;
import com.danil.participants_qr.repository.ParticipantRepository;
import com.danil.participants_qr.repository.QrCodeRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ParticipantServiceTest {

    @Mock
    private ParticipantRepository repository;

    @Mock
    private QrCodeRepository qrCodeRepository;

    @Mock
    private QrCodeService qrCodeService;

    @InjectMocks
    private ParticipantService service;

    private Participant participant() {
        Participant participant = new Participant();
        participant.setId(1L);
        participant.setFirstName("First");
        participant.setSecondName("Second");
        participant.setLastName("Last");
        participant.setEmail("test@test.com");
        return participant;
    }

    private CreateParticipantRequest request() {
        return new CreateParticipantRequest(
                "First",
                "Second",
                "Last",
                "test@test.com"
        );
    }

    private QrCode qrCode(Participant participant) {
        QrCode qrCode = new QrCode();
        qrCode.setId(UUID.randomUUID());
        qrCode.setParticipant(participant);
        return qrCode;
    }

    @Test
    void getById_shouldReturnParticipantDto() {
        when(repository.findById(1L)).thenReturn(Optional.of(participant()));

        assertNotNull(service.getById(1L));
        verify(repository).findById(1L);
        verify(repository, times(1)).findById(1L);
    }

    @Test
    void getById_shouldExceptionWhenParticipantNotFound() {
        when(repository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ParticipantIdNotFoundException.class,
                () -> service.getById(99L));
        verify(repository, times(1)).findById(99L);
    }

    @Test
    void findAllParticipant_shouldUseAscendingSort() {
        when(repository.findAll(any(Pageable.class))).thenReturn(Page.empty());

        assertNotNull(service.findAllParticipant(0, 10, "id", "asc"));
        verify(repository, times(1)).findAll(any(Pageable.class));
    }

    @Test
    void findAllParticipant_shouldUseDescendingSort() {
        when(repository.findAll(any(Pageable.class))).thenReturn(Page.empty());

        assertNotNull(service.findAllParticipant(0, 10, "id", "desc"));
        verify(repository, times(1)).findAll(any(Pageable.class));
    }

    @Test
    void saveParticipant_shouldSaveAndReturnResponse() {
        when(repository.save(any(Participant.class))).thenReturn(participant());

        assertNotNull(service.saveParticipant(request()));
        verify(repository, times(1)).save(any(Participant.class));
    }

    @Test
    void updateParticipant_shouldUpdateWhenEmailIsFree() {
        when(repository.findById(1L)).thenReturn(Optional.of(participant()));
        when(repository.existsByEmailAndIdNot("test@test.com", 1L)).thenReturn(false);

        assertNotNull(service.updateParticipant(1L, request()));

        verify(repository, times(1)).findById(1L);
        verify(repository, times(1)).existsByEmailAndIdNot("test@test.com", 1L);
    }

    @Test
    void updateParticipant_shouldThrowWhenParticipantIdNotFound() {
        when(repository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ParticipantIdNotFoundException.class,
                () -> service.updateParticipant(99L, request()));

        verify(repository, times(1)).findById(99L);
        verify(repository, never()).existsByEmailAndIdNot(anyString(), anyLong());
    }

    @Test
    void updateParticipant_shouldThrowWhenEmailAlreadyUsed() {
        when(repository.findById(1L)).thenReturn(Optional.of(participant()));
        when(repository.existsByEmailAndIdNot("test@test.com", 1L)).thenReturn(true);

        assertThrows(
                EmailAlreadyUsedException.class,
                () -> service.updateParticipant(1L, request())
        );

        verify(repository, times(1)).findById(1L);
        verify(repository, times(1)).existsByEmailAndIdNot("test@test.com", 1L);

    }

    @Test
    void deleteParticipant_shouldDeleteWhenExists() {
        when(repository.existsById(1L)).thenReturn(true);

        service.deleteParticipant(1L);

        verify(repository, times(1)).existsById(1L);
        verify(qrCodeRepository, times(1)).deleteByParticipantId(1L);
        verify(repository, times(1)).deleteById(1L);
    }

    @Test
    void deleteParticipang_shouldThrowWhenParticipantIdNotFound() {
        when(repository.existsById(99L)).thenReturn(false);

        assertThrows(ParticipantIdNotFoundException.class,
                () -> service.deleteParticipant(99L)
        );

        verify(repository, times(1)).existsById(99L);
        verify(qrCodeRepository, never()).deleteByParticipantId(anyLong());
        verify(repository, never()).deleteById(anyLong());
    }

    @Test
    void register_shouldRegisterParticipantAndIssueQrCode() {
        CreateParticipantRequest req = request();
        Participant savedParticipant = participant();
        QrCode qrCode = qrCode(savedParticipant);

        when(repository.existsByEmail(req.email())).thenReturn(false);
        when(repository.save(any(Participant.class))).thenReturn(savedParticipant);
        when(qrCodeService.issue(any(Participant.class))).thenReturn(qrCode);

        assertNotNull(service.register(req));

        verify(repository, times(1)).existsByEmail(req.email());
        verify(repository, times(1)).save(any(Participant.class));
        verify(qrCodeService, times(1)).issue(any(Participant.class));
    }

    @Test
    void register_shouldThrowWhenEmailAlreadyExists() {
        when(repository.existsByEmail("test@test.com")).thenReturn(true);

        assertThrows(EmailAlreadyUsedException.class,
                () -> service.register(request())
        );

        verify(repository, times(1)).existsByEmail("test@test.com");
        verify(repository, never()).save(any(Participant.class));
        verify(qrCodeService, never()).issue(any(Participant.class));
    }
}
