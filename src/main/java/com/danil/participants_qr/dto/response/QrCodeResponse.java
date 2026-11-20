package com.danil.participants_qr.dto.response;

import com.danil.participants_qr.model.QrCode;

import java.util.List;

public record QrCodeResponse(
        List<QrCodeDto> qrCodes,
        int pageNo,
        int pageSize,
        long totalElements,
        int totalPages,
        boolean last
) {}
