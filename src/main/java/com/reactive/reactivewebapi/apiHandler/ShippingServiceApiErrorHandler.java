package com.reactive.reactivewebapi.apiHandler;

import com.reactive.reactivewebapi.common.dto.ShippingResponseDTO;
import io.reactivex.functions.Function;
import org.springframework.stereotype.Component;

@Component
public class ShippingServiceApiErrorHandler implements Function<Throwable, ShippingResponseDTO> {

    @Override
    public ShippingResponseDTO apply(Throwable throwable) {
        return new ShippingResponseDTO();
    }
}
