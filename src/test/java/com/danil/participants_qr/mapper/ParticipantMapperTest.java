package com.danil.participants_qr.mapper;


import com.danil.participants_qr.dto.request.CreateParticipantRequest;
import com.danil.participants_qr.dto.response.ParticipantDto;
import com.danil.participants_qr.model.Participant;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class ParticipantMapperTest {

    @Test
    void toDto_shouldMapParticipantToDto() {
        Participant participant = new Participant();
        participant.setId(1L);
        participant.setFirstName("First");
        participant.setSecondName("Second");
        participant.setLastName("Last");
        participant.setEmail("test@test.com");

        ParticipantDto participantDto = ParticipantMapper.toDto(participant);

        assertNotNull(participantDto);
        assertEquals(1L, participantDto.id());
        assertEquals("First", participantDto.firstName());
        assertEquals("Second", participantDto.secondName());
        assertEquals("Last", participantDto.lastName());
        assertEquals("test@test.com", participantDto.email());
    }

    @Test
    void toEntity_shouldMapDtoToParticipant() {
        CreateParticipantRequest request = new CreateParticipantRequest(
                "First",
                "Second",
                "Last",
                "test@test.com"
        );

        Participant participant = ParticipantMapper.toEntity(request);

        assertNotNull(participant);
        assertEquals("First", participant.getFirstName());
        assertEquals("Second", participant.getSecondName());
        assertEquals("Last", participant.getLastName());
        assertEquals("test@test.com", participant.getEmail());

    }
}
