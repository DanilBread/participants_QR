package com.danil.participants_qr.controller;

import com.danil.participants_qr.dto.response.ParticipantDto;
import com.danil.participants_qr.dto.response.QrCodeDto;
import com.danil.participants_qr.dto.request.ScanRequest;
import com.danil.participants_qr.dto.response.ScanResponse;
import com.danil.participants_qr.service.QrCodeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@RestController
@RequestMapping("/api/v1/qr_codes")
@RequiredArgsConstructor
public class QrCodeController {
    private final QrCodeService qrCodeService;

    @GetMapping()
    public List<QrCodeDto> findAllQrCodes() {
        return qrCodeService.findAllQrCodes();
    }

    @PostMapping("/scan")
    public ResponseEntity<ScanResponse> scanner(@Valid @RequestBody ScanRequest request) {
        ParticipantDto participant = qrCodeService.scan(request);
        return ResponseEntity.status(200).body(new ScanResponse(true, "Доступ разрешен", participant));
    }

    @DeleteMapping("/used/{id}")
    public ResponseEntity<Void> deleteUsed(@PathVariable Long id) {
        qrCodeService.deleteAllUsedByParticipantId(id);
        return ResponseEntity.status(204).build();
    }
}
