package com.danil.participants_qr.dto;

public record ParticipantDto(
        Long Id,
        String firstName,
        String lastName,
        String email
) {
}
