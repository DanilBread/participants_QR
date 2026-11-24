package com.danil.participants_qr.exceptions;

public class ParticipantNotFoundException extends RuntimeException {
    public ParticipantNotFoundException() {
        super("Участник не найден!");
    }
}
