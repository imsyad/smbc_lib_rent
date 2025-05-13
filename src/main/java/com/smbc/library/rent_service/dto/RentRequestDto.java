package com.smbc.library.rent_service.dto;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDate;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class RentRequestDto implements Serializable {
    @Serial
    private static final long serialVersionUID = -6462424624L;

    private Long bookId;
    private LocalDate startDate;
    private LocalDate endDate;
    private Integer amount;
}
