package com.danil.participants_qr.dto;

public record RegisterParticipantResponse(
        ParticipantDto participant,
        QrCodeDto qrCode
) {}