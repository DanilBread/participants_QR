package com.danil.participants_qr.dto.response;

public record CreateQrCodeResponse(
        String message,
        ParticipantDto participant
) {
}
