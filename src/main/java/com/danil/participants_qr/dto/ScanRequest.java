package com.danil.participants_qr.dto;

import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record ScanRequest(
        @NotNull UUID token
) {}
