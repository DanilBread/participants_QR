package com.danil.participants_qr.dto.response;

public record RegisterParticipantResponse(
        ParticipantDto participant,
        QrCodeDto qrCode
) {}