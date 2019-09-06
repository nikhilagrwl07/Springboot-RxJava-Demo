package com.reactive.reactivewebapi.service;

import com.reactive.reactivewebapi.common.dto.ItemResponseDTO;
import io.reactivex.Observable;

public interface ItemService {

    /**
     * @param itemId id of item for fetching price details of item
     * @return ItemResponseDTO is item complete object
     */
    public Observable<ItemResponseDTO> getItemDetails(Long itemId);
}
