package com.reactive.reactivewebapi.service;

import com.reactive.reactivewebapi.common.dto.ItemResponseDTO;
import com.reactive.reactivewebapi.common.dto.ShippingResponseDTO;
import io.reactivex.Observable;

public interface ShippingService {

    /**
     * @param itemId id of item for fetching price details of item
     * @return ShippingResponseDTO is item's shipping information
     */
    public Observable<ShippingResponseDTO> getShippingDetails(Long itemId);
}
