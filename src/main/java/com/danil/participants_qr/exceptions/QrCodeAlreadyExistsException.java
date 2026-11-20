package com.danil.participants_qr.exceptions;

public class QrCodeAlreadyExistsException extends RuntimeException {
    public QrCodeAlreadyExistsException(Long participantId) {
        super(String.format("У участника id: %d уже есть активный QR-код", participantId));
    }
}
