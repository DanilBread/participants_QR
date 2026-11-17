package com.danil.participants_qr.exceptions;

import java.util.UUID;

public class QrCodeNotFoundException extends RuntimeException {
    public QrCodeNotFoundException(UUID token) {
        super(String.format("Токен: %s не найден!", token));
    }
}
