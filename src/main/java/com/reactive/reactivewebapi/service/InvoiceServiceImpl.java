package com.reactive.reactivewebapi.service;

import com.reactive.reactivewebapi.common.dto.InvoiceResponseDTO;
import com.reactive.reactivewebapi.common.dto.ShippingResponseDTO;
import com.reactive.reactivewebapi.configuration.ConfigurationService;
import io.reactivex.Observable;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;


@Slf4j
@Service
public class InvoiceServiceImpl implements InvoiceService {

    @Autowired
    RestTemplate restTemplate;

    @Autowired
    ConfigurationService configurationService;


    @Override
    public Observable<InvoiceResponseDTO> getInvoiceDetails(Long itemId) {

        ObservableOnSubscribe<InvoiceResponseDTO> source = emitter -> {

            ResponseEntity<InvoiceResponseDTO> invoiceResponseDTO = restTemplate.getForEntity(
                    UriComponentsBuilder
                            .fromUriString(configurationService.getInvoiceEndPoint())
                            .toUriString(), InvoiceResponseDTO.class);

            emitter.onNext(invoiceResponseDTO.getBody());
            emitter.onComplete();
        };

        return Observable.<InvoiceResponseDTO>create(source)
                .doOnNext(c -> log.info("Invoice details were retrieved successfully."))
                .onErrorReturn(new Function<Throwable, InvoiceResponseDTO>() {
                    @Override
                    public InvoiceResponseDTO apply(Throwable throwable) {
                        return new InvoiceResponseDTO();
                    }
                })
                .subscribeOn(Schedulers.io());


    }
}
