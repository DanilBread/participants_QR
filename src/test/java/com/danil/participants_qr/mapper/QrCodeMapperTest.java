package com.danil.participants_qr.mapper;

import com.danil.participants_qr.dto.response.QrCodeDto;
import com.danil.participants_qr.model.Participant;
import com.danil.participants_qr.model.QrCode;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class QrCodeMapperTest {

    @Test
    void toDto_shouldQrCodeToDto() {
        UUID id = UUID.randomUUID();
        Participant participant = new Participant();
        Instant instant = Instant.now();

        QrCode qrCode = new QrCode();
        qrCode.setId(id);
        qrCode.setParticipant(participant);
        qrCode.setCreatedAt(instant);
        qrCode.setUsedAt(instant);
        qrCode.setRevokedAt(instant);

        QrCodeDto qrCodeDto = QrCodeMapper.toDto(qrCode);

        assertNotNull(qrCodeDto);
        assertEquals(id, qrCodeDto.token());
        assertEquals(participant.getId(), qrCodeDto.participantId());
        assertEquals(instant, qrCodeDto.createdAt());
        assertEquals(instant, qrCodeDto.usedAt());
        assertEquals(instant, qrCodeDto.createdAt());
    }
}
