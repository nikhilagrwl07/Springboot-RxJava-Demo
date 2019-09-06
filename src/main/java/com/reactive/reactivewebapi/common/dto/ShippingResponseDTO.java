package com.reactive.reactivewebapi.common.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ShippingResponseDTO {
    private String id;
    private String status;
    private String destinaton;
}
