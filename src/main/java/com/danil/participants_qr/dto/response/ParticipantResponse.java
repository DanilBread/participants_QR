package com.danil.participants_qr.dto.response;

import java.util.List;

public record ParticipantResponse(
        List<ParticipantDto> participants,
        int pageNo,
        int pageSize,
        long totalElements,
        int totalPages,
        boolean last
) {}
