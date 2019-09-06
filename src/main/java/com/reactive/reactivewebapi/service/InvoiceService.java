package com.reactive.reactivewebapi.service;

import com.reactive.reactivewebapi.common.dto.InvoiceResponseDTO;
import com.reactive.reactivewebapi.common.dto.ItemResponseDTO;
import io.reactivex.Observable;

public interface InvoiceService {

    /**
     * @param itemId id of item for fetching invoice details of item
     * @return InvoiceResponseDTO is item's invoice information
     */
    public Observable<InvoiceResponseDTO> getInvoiceDetails(Long itemId);
}
