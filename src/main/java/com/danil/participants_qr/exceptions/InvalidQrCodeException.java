package com.danil.participants_qr.exceptions;

import java.util.UUID;

public class InvalidQrCodeException extends RuntimeException {
    public InvalidQrCodeException() {
        super("QR-код не найден или уже был использован!");
    }
}
