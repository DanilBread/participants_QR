package com.danil.participants_qr.service;

import com.danil.participants_qr.dto.QrCodeDto;
import com.danil.participants_qr.model.QrCode;

public class QrCodeMapper {
    public static QrCodeDto toDto(QrCode qr) {
        return new QrCodeDto(
                qr.getId(),
                qr.getParticipant().getId(),
                qr.getCreatedAt(),
                qr.getUsedAt(),
                qr.getRevokedAt()
        );
    }
}
