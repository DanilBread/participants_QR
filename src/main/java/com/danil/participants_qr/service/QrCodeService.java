package com.danil.participants_qr.service;

import com.danil.participants_qr.dto.response.CreateQrCodeResponse;
import com.danil.participants_qr.dto.response.ParticipantDto;
import com.danil.participants_qr.dto.response.QrCodeDto;
import com.danil.participants_qr.dto.request.ScanRequest;
import com.danil.participants_qr.dto.response.QrCodeResponse;
import com.danil.participants_qr.exceptions.InvalidQrCodeException;
import com.danil.participants_qr.exceptions.ParticipantIdNotFoundException;
import com.danil.participants_qr.exceptions.ParticipantNotFoundException;
import com.danil.participants_qr.exceptions.QrCodeAlreadyExistsException;
import com.danil.participants_qr.mapper.ParticipantMapper;
import com.danil.participants_qr.mapper.QrCodeMapper;
import com.danil.participants_qr.model.Participant;
import com.danil.participants_qr.model.QrCode;
import com.danil.participants_qr.repository.ParticipantRepository;
import com.danil.participants_qr.repository.QrCodeRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class QrCodeService {
    private final QrCodeRepository repository;
    private final ParticipantRepository participantRepository;

    public QrCodeResponse findAllQrCodes(int pageNo, int pageSize, String sortBy, String sortDir) {
        Sort sort = sortDir.equalsIgnoreCase(Sort.Direction.ASC.name()) ? Sort.by(sortBy).ascending() :
                Sort.by(sortBy).descending();

        Pageable pageable = PageRequest.of(pageNo, pageSize, sort);
        Page<QrCode> qrCodes = repository.findAll(pageable);

        List<QrCodeDto> qrCodeDtoList = qrCodes.stream()
                .map(QrCodeMapper::toDto)
                .toList();

        QrCodeResponse qrCodeResponse = new QrCodeResponse(
                qrCodeDtoList,
                qrCodes.getNumber(),
                qrCodes.getSize(),
                qrCodes.getTotalElements(),
                qrCodes.getTotalPages(),
                qrCodes.isLast()
        );

        return qrCodeResponse;
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

    @Transactional
    public QrCode create(Participant participant) {
        QrCode qrCode = new QrCode();
        qrCode.setParticipant(participant);
        return repository.save(qrCode);
    }

    @Transactional
    public CreateQrCodeResponse createQrCodeForParticipant(Long id) {
        Participant participant = participantRepository.findById(id)
                .orElseThrow(() -> new ParticipantIdNotFoundException(id));

        if (repository.existsByParticipantIdAndUsedAtIsNullAndRevokedAtIsNull(id)) {
            throw new QrCodeAlreadyExistsException(id);
        }

        create(participant);

        CreateQrCodeResponse response = new CreateQrCodeResponse(
            "QR-код успешно создан",
                ParticipantMapper.toDto(participant)
        );

        return response;
    }
}
