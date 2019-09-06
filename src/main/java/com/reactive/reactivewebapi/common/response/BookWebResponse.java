package com.reactive.reactivewebapi.common.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BookWebResponse {

    private String isbn;
    private Double price;
    private String title;

    // Author info
    private String authorName;

    // Shipping info
    private String shippingStatus;
    private String destination;

    // Invoice info
    private Double totalPrice;
    private Double salesTax;
    private Double mrp;
}
