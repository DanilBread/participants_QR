package com.danil.participants_qr.exceptions;

public class EmailAlreadyUsedException extends RuntimeException {
    public EmailAlreadyUsedException(String email) {
        super(String.format("Email: %s уже занят!", email));
    }
}
