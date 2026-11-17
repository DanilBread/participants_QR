package com.danil.participants_qr.service;

import com.danil.participants_qr.dto.ParticipantDto;
import com.danil.participants_qr.dto.QrCodeDto;
import com.danil.participants_qr.dto.ScanRequest;
import com.danil.participants_qr.exceptions.InvalidQrCodeException;
import com.danil.participants_qr.exceptions.ParticipantNotFoundException;
import com.danil.participants_qr.model.Participant;
import com.danil.participants_qr.model.QrCode;
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
    public String scan(ScanRequest request) {
        UUID token = request.token();
        int countRows = repository.markUsed(token, Instant.now());
        if (countRows == 0) {
            throw new InvalidQrCodeException(token);
        }

        Participant participant = repository.findParticipantByCodeId(token)
                .orElseThrow(ParticipantNotFoundException::new);
        issue(participant);
        ParticipantDto p = ParticipantMapper.toDto(participant);
        return String.format("""
                Вход разрешен!
                Участник закрытого клуба: %s %s %s
                """, p.firstName(), p.secondName(), p.lastName());
    }
}
