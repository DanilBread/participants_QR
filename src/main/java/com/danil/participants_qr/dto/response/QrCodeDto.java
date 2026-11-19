package com.danil.participants_qr.dto.response;

import java.time.Instant;
import java.util.UUID;

public record QrCodeDto(
        UUID token,
        Long participantId,
        Instant createdAt,
        Instant usedAt,
        Instant revokedAt
) {
}
