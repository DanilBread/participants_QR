package com.danil.participants_qr.dto;

import com.danil.participants_qr.model.Participant;
import jakarta.validation.constraints.NotNull;

import java.time.Instant;
import java.util.UUID;

public record QrCodeDto(
        UUID token,
        Long participant_id,
        Instant createdAt,
        Instant usedAt,
        Instant revokedAt
) {
}
