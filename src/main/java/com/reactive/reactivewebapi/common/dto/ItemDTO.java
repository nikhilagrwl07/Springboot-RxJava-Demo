package com.reactive.reactivewebapi.common.dto;

import lombok.Data;

@Data
public class ItemDTO {

    private Long id;
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

    public ItemDTO() {
    }

    public ItemDTO(long id, String isbn, double price, String title, String authorName) {
        this.id = id;
        this.isbn = isbn;
        this.price = price;
        this.title = title;
        this.authorName = authorName;
    }

    public void setItemResponseDTO(ItemResponseDTO itemResponseDTO) {
        this.id = Long.valueOf(itemResponseDTO.getId());
        this.isbn = itemResponseDTO.getIsbn();
        this.price = itemResponseDTO.getPrice();
        this.title = itemResponseDTO.getDescription();
    }


    public void setItemShippingDTO(ShippingResponseDTO shippingResponseDTO) {
        this.shippingStatus = shippingResponseDTO.getStatus();
        this.destination = shippingResponseDTO.getDestinaton();
    }

    public void setItemInvoiceDTO(InvoiceResponseDTO invoiceResponseDTO) {
        this.totalPrice = invoiceResponseDTO.getTotalPrice();
        this.salesTax = invoiceResponseDTO.getSalesTax();
        this.mrp = invoiceResponseDTO.getMrp();
    }
}
