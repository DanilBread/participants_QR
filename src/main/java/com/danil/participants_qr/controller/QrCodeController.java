package com.danil.participants_qr.controller;

import com.danil.participants_qr.dto.ParticipantDto;
import com.danil.participants_qr.dto.QrCodeDto;
import com.danil.participants_qr.dto.ScanRequest;
import com.danil.participants_qr.service.QrCodeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

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
    public String scanner(@Valid @RequestBody ScanRequest request) {
        ParticipantDto p = qrCodeService.scan(request);
        return String.format("""
                Доступ разрешен!
                Участник: %s %s %s
                """, p.firstName(), p.secondName(), p.lastName());
    }

    @DeleteMapping("/used/{id}")
    public void deleteUsed(@PathVariable Long id) {
        qrCodeService.deleteAllUsedByParticipantId(id);
    }
}
