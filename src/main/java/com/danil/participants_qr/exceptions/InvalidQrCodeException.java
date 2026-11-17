package com.danil.participants_qr.exceptions;

import java.util.UUID;

public class InvalidQrCodeException extends RuntimeException {
    public InvalidQrCodeException(UUID token) {
        super(String.format("""
                Вход запрещен!
                qr-код %s недействителен
                """, token));
    }
}
