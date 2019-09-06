package com.reactive.reactivewebapi.common.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class InvoiceResponseDTO {
    private String id;
    private Double totalPrice;
    private Double salesTax;
    private Double mrp;
}
