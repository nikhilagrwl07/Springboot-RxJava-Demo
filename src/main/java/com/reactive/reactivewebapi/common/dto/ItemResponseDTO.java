package com.reactive.reactivewebapi.common.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ItemResponseDTO {
    private String id;
    private String description;
    private Double price;
    private String isbn;
}
