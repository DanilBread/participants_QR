package com.danil.participants_qr.dto.response;

public record ParticipantDto(
        Long id,
        String firstName,
        String secondName,
        String lastName,
        String email
) {
}
