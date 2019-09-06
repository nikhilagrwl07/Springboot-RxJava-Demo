package com.reactive.reactivewebapi.apiHandler;

import com.reactive.reactivewebapi.common.dto.InvoiceResponseDTO;
import io.reactivex.functions.Function;
import org.springframework.stereotype.Component;

@Component
public class InvoiceServiceApiErrorHandler implements Function<Throwable, InvoiceResponseDTO> {

    @Override
    public InvoiceResponseDTO apply(Throwable throwable) {
        return new InvoiceResponseDTO();
    }
}