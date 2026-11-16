package com.danil.participants_qr.controller;

import com.danil.participants_qr.dto.ParticipantDto;
import com.danil.participants_qr.service.ParticipantService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/participants")
@RequiredArgsConstructor
public class ParticipantController {

    private final ParticipantService service;

    @GetMapping()
    public List<ParticipantDto> findAllParticipant(){
        return service.findAllParticipant();
    }

    @GetMapping("/{id}")
    public ParticipantDto getParticipant(@PathVariable Long id){
        return service.getById(id);
    }

}
