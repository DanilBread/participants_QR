package com.danil.participants_qr.exceptions;


public class ParticipantIdNotFoundException extends RuntimeException {
    public ParticipantIdNotFoundException(Long id) {
        super(String.format("Участник с id: %d не найден!", id));
    }
}
