package com.danil.participants_qr.dto.request;

import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record ScanRequest(
        @NotNull
        UUID token
) {}
