package com.danil.participants_qr.repository;

import com.danil.participants_qr.model.Participant;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.ArrayList;
import java.util.List;

public interface ParticipantRepository extends JpaRepository<Participant, Long> {
    boolean existsByEmailAndIdNot(String email, Long id);
    boolean existsByEmail(String email);
}
