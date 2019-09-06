package com.reactive.reactivewebapi.apiHandler;

import com.reactive.reactivewebapi.common.dto.ItemResponseDTO;
import io.reactivex.functions.Function;
import org.springframework.stereotype.Component;

@Component
public class ItemServiceApiErrorHandler implements Function<Throwable, ItemResponseDTO> {

    @Override
    public ItemResponseDTO apply(Throwable throwable) {
        return new ItemResponseDTO();
    }
}
