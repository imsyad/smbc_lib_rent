package com.smbc.library.rent_service.dto;

import java.io.Serial;
import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateBookDto implements Serializable {

    @Serial
    private static final long serialVersionUID = 153113351L;
    private String name;
    private String author;
    private Integer total;
}
