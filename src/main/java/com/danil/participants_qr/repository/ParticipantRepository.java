package com.danil.participants_qr.repository;

import com.danil.participants_qr.dto.ParticipantDto;
import com.danil.participants_qr.model.Participant;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.ArrayList;
import java.util.List;

public interface ParticipantRepository extends JpaRepository<Participant, Long> {
    List<Participant> PARTICIPANT_LIST = new ArrayList<>();


}
