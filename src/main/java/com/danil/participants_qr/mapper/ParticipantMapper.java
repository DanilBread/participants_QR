package com.danil.participants_qr.mapper;

import com.danil.participants_qr.dto.request.CreateParticipantRequest;
import com.danil.participants_qr.dto.response.ParticipantDto;
import com.danil.participants_qr.model.Participant;

public class ParticipantMapper {
    public static ParticipantDto toDto(Participant p) {
        return new ParticipantDto(
                p.getId(),
                p.getFirstName(),
                p.getSecondName(),
                p.getLastName(),
                p.getEmail()
        );
    }

    public static Participant toEntity(CreateParticipantRequest dto) {
        Participant p = new Participant();
        p.setFirstName(dto.firstName());
        p.setSecondName(dto.secondName());
        p.setLastName(dto.lastName());
        p.setEmail(dto.email());
        return p;
    }
}
