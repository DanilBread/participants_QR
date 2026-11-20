
package com.danil.participants_qr.repository;

import com.danil.participants_qr.model.Participant;
import com.danil.participants_qr.model.QrCode;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

public interface QrCodeRepository extends JpaRepository<QrCode, UUID> {

    @Modifying
    @Query("delete from QrCode q where q.participant.id = :participantId")
    void deleteByParticipantId(@Param("participantId") Long participantId);

    @Modifying
    @Query("""
            delete from QrCode q
            where q.participant.id = :participantId
                and q.usedAt is not null
            """)
    void deleteAllUsedByParticipantId(@Param("participantId") Long participantId);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("""
            update QrCode q set q.usedAt = :now
            where q.id = :id
              and q.usedAt is null
              and q.revokedAt is null
            """)
    int markUsed(@Param("id") UUID id, @Param("now") Instant now);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("""
            update QrCode q set q.usedAt = :now
            where q.participant.id = :participantId
              and q.usedAt is null
              and q.revokedAt is null
            """)
    int revokeActive(@Param("participantId") Long participantId,
                     @Param("now") Instant now);

    @Query("select q.participant from QrCode q where q.id = :id")
    Optional<Participant> findParticipantByCodeId(@Param("id") UUID id);

    boolean existsByParticipantIdAndUsedAtIsNullAndRevokedAtIsNull(Long participantId);
}

