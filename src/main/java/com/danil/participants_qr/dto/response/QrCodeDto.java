package com.danil.participants_qr.dto.response;

import jakarta.validation.constraints.NotNull;

import java.time.Instant;
import java.util.UUID;

public record QrCodeDto(
        @NotNull
        UUID token,

        @NotNull
        Long participantId,

        @NotNull
        Instant createdAt,

        @NotNull
        Instant usedAt,

        @NotNull
        Instant revokedAt
) {
}
