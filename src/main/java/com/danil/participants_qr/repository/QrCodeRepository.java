//package com.danil.participants_qr.repository;
//
//import com.danil.participants_qr.model.QrCode;
//import org.springframework.data.jpa.repository.JpaRepository;
//import org.springframework.data.jpa.repository.Modifying;
//import org.springframework.data.jpa.repository.Query;
//import org.springframework.data.repository.query.Param;
//
//import java.time.Instant;
//import java.util.UUID;
//
//public interface QrCodeRepository extends JpaRepository<QrCode, UUID> {
//    @Modifying
//    @Query("""
//            update QrCode q set q.usedAt = :now
//            where q.id = :id
//                and q.usedAt is null
//                and q.revokeAt is null
//            """
//    )
//    int makrUsed(@Param("id") UUID id, @Param("now") Instant now);
//
//}
