package com.danil.participants_qr.service;

import com.danil.participants_qr.dto.response.ParticipantDto;
import com.danil.participants_qr.dto.response.QrCodeDto;
import com.danil.participants_qr.dto.request.ScanRequest;
import com.danil.participants_qr.exceptions.InvalidQrCodeException;
import com.danil.participants_qr.exceptions.ParticipantIdNotFoundException;
import com.danil.participants_qr.exceptions.ParticipantNotFoundException;
import com.danil.participants_qr.mapper.ParticipantMapper;
import com.danil.participants_qr.mapper.QrCodeMapper;
import com.danil.participants_qr.model.Participant;
import com.danil.participants_qr.model.QrCode;
import com.danil.participants_qr.repository.ParticipantRepository;
import com.danil.participants_qr.repository.QrCodeRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class QrCodeService {
    private final QrCodeRepository repository;
    private final ParticipantRepository participantRepository;

    public List<QrCodeDto> findAllQrCodes() {
        return repository.findAll().stream()
                .map(QrCodeMapper::toDto)
                .toList();
    }

    @Transactional
    public QrCode issue(Participant participant) {
        repository.revokeActive(participant.getId(), Instant.now());

        QrCode code = new QrCode();
        code.setParticipant(participant);

        return repository.save(code);
    }

    @Transactional
    public ParticipantDto scan(ScanRequest request) {
        UUID token = request.token();
        int countRows = repository.markUsed(token, Instant.now());
        if (countRows != 1) {
            throw new InvalidQrCodeException();
        }

        Participant participant = repository.findParticipantByCodeId(token)
                .orElseThrow(() -> new ParticipantNotFoundException());
        issue(participant);

        return ParticipantMapper.toDto(participant);
    }

    @Transactional
    public void deleteAllUsedByParticipantId(Long id) {
        if (!participantRepository.existsById(id)) {
            throw new ParticipantIdNotFoundException(id);
        }
        repository.deleteAllUsedByParticipantId(id);
    }
}
