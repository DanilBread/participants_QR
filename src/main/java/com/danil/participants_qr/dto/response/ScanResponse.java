package com.danil.participants_qr.dto.response;

public record ScanResponse(
        boolean accessGranted,
        String message,
        ParticipantDto participant
) {
}
