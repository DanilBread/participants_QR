package com.danil.participants_qr.controller;

import com.danil.participants_qr.dto.ParticipantDto;
import com.danil.participants_qr.dto.QrCodeDto;
import com.danil.participants_qr.dto.ScanRequest;
import com.danil.participants_qr.service.QrCodeService;
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

    @GetMapping("/scan/{token}")
    public String scaner(ScanRequest request) {
        return qrCodeService.scan(request);
    }
}
