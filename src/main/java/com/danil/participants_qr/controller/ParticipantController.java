package com.danil.participants_qr.controller;

import com.danil.participants_qr.dto.request.CreateParticipantRequest;
import com.danil.participants_qr.dto.request.UpdateParticipantRequest;
import com.danil.participants_qr.dto.response.CreateParticipantResponse;
import com.danil.participants_qr.dto.response.ParticipantDto;
import com.danil.participants_qr.dto.response.RegisterParticipantResponse;
import com.danil.participants_qr.service.ParticipantService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.DeleteMapping;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/v1/participants")
@RequiredArgsConstructor
public class ParticipantController {

    private final ParticipantService participantService;

    @GetMapping()
    public List<ParticipantDto> findAllParticipant() {
        return participantService.findAllParticipant();
    }

    @GetMapping("/{id}")
    public ResponseEntity<ParticipantDto> getParticipant(@PathVariable Long id) {
        return ResponseEntity.status(200).body(participantService.getById(id));
    }

    @PostMapping("/save_participant")
    public ResponseEntity<CreateParticipantResponse> saveParticipant(@Valid @RequestBody CreateParticipantRequest request) {
        CreateParticipantResponse response = participantService.saveParticipant(request);
        URI location = URI.create("/api/v1/participants/" + response.participant().id());
        return ResponseEntity.status(201).location(location).body(response);
    }

    @PostMapping("/register")
    public ResponseEntity<RegisterParticipantResponse> register(@Valid @RequestBody CreateParticipantRequest request) {
        RegisterParticipantResponse response = participantService.register(request);
        URI location = URI.create("/api/v1/participants/" + response.participant().id());
        return ResponseEntity.status(201).location(location).body(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ParticipantDto> updateParticipant(@PathVariable Long id,
                                 @Valid @RequestBody UpdateParticipantRequest request) {
        return ResponseEntity.status(200).body(participantService.updateParticipant(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        participantService.deleteParticipant(id);
        return ResponseEntity.status(204).build();
    }
}
