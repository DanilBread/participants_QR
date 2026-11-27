package com.danil.participants_qr.controller;

import com.danil.participants_qr.dto.request.ScanRequest;
import com.danil.participants_qr.dto.response.CreateQrCodeResponse;
import com.danil.participants_qr.dto.response.ParticipantDto;
import com.danil.participants_qr.dto.response.QrCodeDto;
import com.danil.participants_qr.dto.response.QrCodeResponse;
import com.danil.participants_qr.exceptions.InvalidQrCodeException;
import com.danil.participants_qr.exceptions.ParticipantIdNotFoundException;
import com.danil.participants_qr.exceptions.QrCodeAlreadyExistsException;
import com.danil.participants_qr.service.QrCodeService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(QrCodeController.class)
public class QrCodeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private QrCodeService qrCodeService;


    private static final String BASE_URL = "/api/v1/qr_codes";

    private ParticipantDto participantDto() {
        return new ParticipantDto(
                1L,
                "First",
                "Second",
                "Last",
                "test@test.com"
        );
    }

    private String scanBody(UUID token) {
        return "{\"token\": \"%s\"}".formatted(token);
    }

    @Test
    void findAllQrCodes_shouldReturn200() throws Exception {
        QrCodeDto qrCodeDto = new QrCodeDto(UUID.randomUUID(), 1L, Instant.now(), null, null);
        QrCodeResponse page = new QrCodeResponse(
                List.of(qrCodeDto),
                0,
                10,
                1,
                1,
                true
        );

        when(qrCodeService.findAllQrCodes(0, 10, "id", "asc"))
                .thenReturn(page);

        mockMvc.perform(get(BASE_URL)
                        .param("pageNo", "0")
                        .param("pageSize", "10")
                        .param("sortBy", "id")
                        .param("sortDir", "asc"))
                .andExpect(status().isOk());

        verify(qrCodeService).findAllQrCodes(0, 10, "id", "asc");
    }

    @Test
    void scanner_shouldReturn200AndAccessGranted() throws Exception {
        UUID token = UUID.randomUUID();
        when(qrCodeService.scan(new ScanRequest(token))).thenReturn(participantDto());

        mockMvc.perform(post(BASE_URL + "/scan")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(scanBody(token)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessGranted").value(true))
                .andExpect(jsonPath("$.message").value("Доступ разрешен"))
                .andExpect(jsonPath("$.participant.id").value(1))
                .andExpect(jsonPath("$.participant.firstName").value("First"));

        verify(qrCodeService, times(1)).scan(new ScanRequest(token));
    }

    @Test
    void scanner_shouldReturn400WhenTokenIsNotUuid() throws Exception {
        mockMvc.perform(post(BASE_URL + "/scan")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"token\": \"abc\"}"))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(qrCodeService);
    }

    @Test
    void scanner_shouldReturn400WhenTokenIsMissing() throws Exception {
        mockMvc.perform(post(BASE_URL + "/scan")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(qrCodeService);
    }

    @Test
    void create_shouldReturn201() throws Exception {
        CreateQrCodeResponse response = new CreateQrCodeResponse("QR-код успешно создан", participantDto());
        when(qrCodeService.createQrCodeForParticipant(1L)).thenReturn(response);

        mockMvc.perform(post(BASE_URL + "/create/1"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.participant.id").value(1));

        verify(qrCodeService).createQrCodeForParticipant(1L);
    }

    @Test
    void create_shouldReturn404WhenParticipantNotFound() throws Exception {
        when(qrCodeService.createQrCodeForParticipant(99L))
                .thenThrow(new ParticipantIdNotFoundException(99L));

        mockMvc.perform(post(BASE_URL + "/create/99"))
                .andExpect(status().isNotFound());
    }

    @Test
    void create_shouldReturn409WhenActiveCodeExists() throws Exception {
        when(qrCodeService.createQrCodeForParticipant(1L))
                .thenThrow(new QrCodeAlreadyExistsException(1L));

        mockMvc.perform(post(BASE_URL + "/create/1"))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message").exists());
    }

    @Test
    void deleteUsed_shouldReturn204() throws Exception {
        mockMvc.perform(delete(BASE_URL + "/used/1"))
                .andExpect(status().isNoContent());

        verify(qrCodeService).deleteAllUsedByParticipantId(1L);
    }

    @Test
    void deleteUsed_shouldReturn404WhenParticipantNotFound() throws Exception {
        doThrow(new ParticipantIdNotFoundException(99L))
                .when(qrCodeService).deleteAllUsedByParticipantId(99L);

        mockMvc.perform(delete(BASE_URL + "/used/99"))
                .andExpect(status().isNotFound());
    }

}
