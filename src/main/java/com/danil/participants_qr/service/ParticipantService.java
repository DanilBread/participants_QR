package com.danil.participants_qr.service;

import com.danil.participants_qr.dto.request.CreateParticipantRequest;
import com.danil.participants_qr.dto.request.UpdateParticipantRequest;
import com.danil.participants_qr.dto.response.CreateParticipantResponse;
import com.danil.participants_qr.dto.response.ParticipantDto;
import com.danil.participants_qr.dto.response.RegisterParticipantResponse;
import com.danil.participants_qr.exceptions.EmailAlreadyUsedException;
import com.danil.participants_qr.exceptions.ParticipantIdNotFoundException;
import com.danil.participants_qr.mapper.ParticipantMapper;
import com.danil.participants_qr.mapper.QrCodeMapper;
import com.danil.participants_qr.model.Participant;
import com.danil.participants_qr.model.QrCode;
import com.danil.participants_qr.repository.ParticipantRepository;
import com.danil.participants_qr.repository.QrCodeRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ParticipantService {
    private final ParticipantRepository repository;
    private final QrCodeRepository qrCodeRepository;
    private final QrCodeService qrCodeService;

    public ParticipantDto getById(Long id) {
        Participant participant = repository.findById(id).orElseThrow();
        return ParticipantMapper.toDto(participant);
    }

    public List<ParticipantDto> findAllParticipant() {
        return repository.findAll().stream()
                .map(ParticipantMapper::toDto)
                .toList();
    }

    public CreateParticipantResponse saveParticipant(CreateParticipantRequest request) {
        Participant saved = repository.save(ParticipantMapper.toEntity(request));
        return new CreateParticipantResponse(ParticipantMapper.toDto(saved));
    }

    @Transactional
    public ParticipantDto updateParticipant(Long id, UpdateParticipantRequest request) {
        Participant participant = repository.findById(id)
                .orElseThrow(() -> new ParticipantIdNotFoundException(id));

        if (repository.existsByEmailAndIdNot(request.email(), id)) {
            throw new EmailAlreadyUsedException(request.email());
        }

        participant.setFirstName(request.firstName());
        participant.setSecondName(request.secondName());
        participant.setLastName(request.lastName());
        participant.setEmail(request.email());

        return ParticipantMapper.toDto(participant);
    }

    @Transactional
    public void deleteParticipant(Long id) {
        if (!repository.existsById(id)) {
            throw new ParticipantIdNotFoundException(id);
        }
        qrCodeRepository.deleteByParticipantId(id);
        repository.deleteById(id);
    }

    @Transactional
    public RegisterParticipantResponse register(CreateParticipantRequest request) {
        if (repository.existsByEmail(request.email())) {
            throw new EmailAlreadyUsedException(request.email());
        }

        Participant participant = new Participant();
        participant.setFirstName(request.firstName());
        participant.setSecondName(request.secondName());
        participant.setLastName(request.lastName());
        participant.setEmail(request.email());
        repository.save(participant);

        QrCode code = qrCodeService.issue(participant);
        return new RegisterParticipantResponse(
                ParticipantMapper.toDto(participant),
                QrCodeMapper.toDto(code)
        );
    }
}
