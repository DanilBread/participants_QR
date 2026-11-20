package com.danil.participants_qr.controller;

import com.danil.participants_qr.dto.response.CreateQrCodeResponse;
import com.danil.participants_qr.dto.response.ParticipantDto;
import com.danil.participants_qr.dto.request.ScanRequest;
import com.danil.participants_qr.dto.response.QrCodeResponse;
import com.danil.participants_qr.dto.response.ScanResponse;
import com.danil.participants_qr.service.QrCodeService;
import com.danil.participants_qr.utils.AppConstants;
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
import org.springframework.web.bind.annotation.RequestParam;


@RestController
@RequestMapping("/api/v1/qr_codes")
@RequiredArgsConstructor
public class QrCodeController {
    private final QrCodeService qrCodeService;

    @GetMapping()
    public ResponseEntity<QrCodeResponse> findAllQrCodes(
            @RequestParam(value = "pageNo", defaultValue = AppConstants.DEFAULT_PAGE_NUMBER,
                    required = false) int pageNo,
            @RequestParam(value = "pageSize", defaultValue = AppConstants.DEFAULT_PAGE_SIZE,
                    required = false) int pageSize,
            @RequestParam(value = "sortBy", defaultValue = AppConstants.DEFAULT_SORT_BY,
                    required = false) String sortBy,
            @RequestParam(value = "sortDir", defaultValue = AppConstants.DEFAULT_SORT_DIRECTION,
                    required = false) String sortDir
    ) {
        QrCodeResponse response = qrCodeService.findAllQrCodes(pageNo, pageSize, sortBy, sortDir);
        return ResponseEntity.status(200).body(response);
    }

    @PostMapping("/scan")
    public ResponseEntity<ScanResponse> scanner(@Valid @RequestBody ScanRequest request) {
        ParticipantDto participant = qrCodeService.scan(request);
        return ResponseEntity.status(200).body(new ScanResponse(true, "Доступ разрешен", participant));
    }

    @PostMapping("/create/{id}")
    public ResponseEntity<CreateQrCodeResponse> create(@PathVariable Long id) {
        return ResponseEntity.status(201).body(qrCodeService.createQrCodeForParticipant(id));
    }

    @DeleteMapping("/used/{id}")
    public ResponseEntity<Void> deleteUsed(@PathVariable Long id) {
        qrCodeService.deleteAllUsedByParticipantId(id);
        return ResponseEntity.status(204).build();
    }
}
