package com.danil.participants_qr.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CreateParticipantRequest(

        @NotBlank(message = "Фамилия обязательна")
        @Size(max = 100, message = "Фамилия не длиннее 100 символов")
        String firstName,

        @NotBlank(message = "Имя обязательно")
        @Size(max = 100, message = "Имя не длиннее 100 символов")
        String secondName,

        @NotBlank(message = "Отчество обязательно")
        @Size(max = 100, message = "Отчество не длиннее 100 символов")
        String lastName,

        @NotBlank(message = "Email обязателен")
        @Email(message = "Некорректный email")
        @Size(max = 255, message = "Email не длиннее 255 символов")
        String email
) {}
