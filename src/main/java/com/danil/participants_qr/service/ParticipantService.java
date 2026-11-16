package com.danil.participants_qr.service;

import com.danil.participants_qr.dto.ParticipantDto;
import com.danil.participants_qr.model.Participant;
import com.danil.participants_qr.repository.ParticipantRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ParticipantService {
    private final ParticipantRepository repository;

    public ParticipantDto getById(Long id) {
        Participant participant = repository.findById(id).orElseThrow();
        return ParticipantMapper.toDto(participant);
    }

    public List<ParticipantDto> findAllParticipant() {
        return repository.findAll().stream()
                .map(ParticipantMapper::toDto)
                .toList();
    }
}
