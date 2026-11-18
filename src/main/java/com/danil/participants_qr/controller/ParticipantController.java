package com.danil.participants_qr.controller;

import com.danil.participants_qr.dto.CreateParticipantRequest;
import com.danil.participants_qr.dto.ParticipantDto;
import com.danil.participants_qr.dto.RegisterParticipantResponse;
import com.danil.participants_qr.dto.UpdateParticipantRequest;
import com.danil.participants_qr.service.ParticipantService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

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
    public ParticipantDto getParticipant(@PathVariable Long id) {
        return participantService.getById(id);
    }

    @PostMapping("/save_participant")
    public String saveParticipant(@RequestBody ParticipantDto participantDto) {
        participantService.saveParticipant(participantDto);
        return "Участник успешно сохранен";
    }

    @PostMapping("/register")
    public RegisterParticipantResponse register(@Valid @RequestBody CreateParticipantRequest request) {
        return participantService.register(request);
    }

    @PutMapping("/{id}")
    public ParticipantDto updateParticipant(@PathVariable Long id,
                                 @Valid @RequestBody UpdateParticipantRequest request) {
        return participantService.updateParticipant(id, request);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        participantService.deleteParticipant(id);
    }
}
