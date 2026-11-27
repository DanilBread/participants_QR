package com.danil.participants_qr.controller;

import com.danil.participants_qr.dto.response.*;
import com.danil.participants_qr.exceptions.EmailAlreadyUsedException;
import com.danil.participants_qr.exceptions.ParticipantIdNotFoundException;
import com.danil.participants_qr.mapper.QrCodeMapper;
import com.danil.participants_qr.model.QrCode;
import com.danil.participants_qr.service.ParticipantService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.doThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ParticipantController.class)
class ParticipantControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ParticipantService participantService;

    private static final String BASE_URL = "/api/v1/participants";


    private static final String VALID_BODY = """
            {
              "firstName": "First",
              "secondName": "Second",
              "lastName": "Last",
              "email": "test@test.com"
            }
            """;

    private ParticipantDto participantDto() {
        return new ParticipantDto(1L, "First", "Second", "Last", "test@test.com");
    }

    private QrCodeDto qrCodeDto() {
        return new QrCodeDto(
                UUID.randomUUID(),
                1L,
                Instant.now(),
                null,
                null
        );
    }

    @Test
    void findAllParticipant_shouldReturn200() throws Exception {
        ParticipantResponse page = new ParticipantResponse(
                List.of(participantDto()), 0, 10, 1L, 1, true);
        when(participantService.findAllParticipant(0, 10, "id", "asc")).thenReturn(page);

        mockMvc.perform(get(BASE_URL)
                        .param("pageNo", "0")
                        .param("pageSize", "10")
                        .param("sortBy", "id")
                        .param("sortDir", "asc"))
                .andExpect(status().isOk());

        verify(participantService).findAllParticipant(0, 10, "id", "asc");
    }

    @Test
    void getParticipant_shouldReturn200AndBody() throws Exception {
        when(participantService.getById(99L)).thenThrow(new ParticipantIdNotFoundException(99L));

        mockMvc.perform(get(BASE_URL + "/99"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").exists());
    }

    @Test
    void getParticipant_shouldReturn404WhenNotFound() throws Exception{
        when(participantService.getById(99L)).thenThrow(new ParticipantIdNotFoundException(99L));

        mockMvc.perform(get(BASE_URL + "/99"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").exists());
    }

    @Test
    void saveParticipant_shouldReturn201AndLocation() throws Exception {
        CreateParticipantResponse response =
                new CreateParticipantResponse(participantDto());
        when(participantService.saveParticipant(any())).thenReturn(response);

        mockMvc.perform(post(BASE_URL + "/save_participant")
                        .contentType(String.valueOf(MediaType.APPLICATION_JSON))
                        .content(VALID_BODY))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "/api/v1/participants/1"))
                .andExpect(jsonPath("$.participant.id").value(1));
    }

    @Test
    void saveParticipant_shouldReturn400WhenBodyInvalid() throws Exception{
        String invalidBody = """
                {
                "firstName" : ""
                "secondName" : "Second"
                "lastName" : "Last"
                "email" : "not_email"
                }
                """;

        mockMvc.perform(post(BASE_URL + "/save_participant")
                        .contentType(String.valueOf(MediaType.APPLICATION_JSON))
                        .content(invalidBody))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").exists());

        verifyNoInteractions(participantService);
    }

    @Test
    void register_shouldReturn201AndLocation() throws Exception {
        QrCodeDto qrCodeDto = qrCodeDto();
        RegisterParticipantResponse response =
                new RegisterParticipantResponse(participantDto(), qrCodeDto);
        when(participantService.register(any())).thenReturn(response);

        mockMvc.perform(post(BASE_URL + "/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(VALID_BODY))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "/api/v1/participants/1"))
                .andExpect(jsonPath("$.participant.id").value(1))
                .andExpect(jsonPath("$.qrCode.token").value(qrCodeDto.token().toString()));
    }

    @Test
    void register_shouldReturn409WhenEmailAlreadyUsed() throws Exception{
        when(participantService.register(any()))
                .thenThrow(new EmailAlreadyUsedException("test@test.com"));

        mockMvc.perform(post(BASE_URL + "/register")
                        .contentType(String.valueOf(MediaType.APPLICATION_JSON))
                        .content(VALID_BODY))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message").exists());
    }

    @Test
    void updateParticipant_shouldReturn200() throws Exception {
        when(participantService.updateParticipant(eq(1L), any())).thenReturn(participantDto());

        mockMvc.perform(put(BASE_URL + "/1")
                        .contentType(String.valueOf(MediaType.APPLICATION_JSON))
                        .content(VALID_BODY))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    void updateParticipant_shouldReturn404WhenNotFound() throws Exception {
        when(participantService.updateParticipant(eq(99L), any()))
                .thenThrow(new ParticipantIdNotFoundException(99L));

        mockMvc.perform(put(BASE_URL + "/99")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(VALID_BODY))
                .andExpect(status().isNotFound());
    }

    @Test
    void delete_shouldReturn204() throws Exception {
        mockMvc.perform(delete(BASE_URL + "/1"))
                .andExpect(status().isNoContent());

        verify(participantService, times(1)).deleteParticipant(1L);
    }

    @Test
    void delete_shouldReturn404WhenNotFound() throws Exception {
        doThrow(new ParticipantIdNotFoundException(99L))
                .when(participantService).deleteParticipant(99L);

        mockMvc.perform(delete(BASE_URL + "/99"))
                .andExpect(status().isNotFound());
    }
}
