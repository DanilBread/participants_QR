package com.danil.participants_qr.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CreateParticipantRequest(
        @NotBlank @Size(max = 100) String firstName,
        @NotBlank @Size(max = 100) String secondName,
        @NotBlank @Size(max = 100) String lastName,
        @NotBlank @Email @Size(max = 255) String email
) {
}
