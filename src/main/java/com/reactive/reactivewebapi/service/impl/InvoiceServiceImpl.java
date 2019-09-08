package com.reactive.reactivewebapi.service.impl;

import com.reactive.reactivewebapi.apiHandler.InvoiceServiceApiErrorHandler;
import com.reactive.reactivewebapi.common.dto.InvoiceResponseDTO;
import com.reactive.reactivewebapi.configuration.ConfigurationService;
import com.reactive.reactivewebapi.service.InvoiceService;
import io.reactivex.Observable;
import io.reactivex.ObservableOnSubscribe;
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

    @Autowired
    InvoiceServiceApiErrorHandler invoiceServiceApiErrorHandler;

    @Override
    public Observable<InvoiceResponseDTO> getInvoiceDetails(Long itemId) {

        ObservableOnSubscribe<InvoiceResponseDTO> source = emitter -> {

            long startTime = System.currentTimeMillis();
            ResponseEntity<InvoiceResponseDTO> invoiceResponseDTO = restTemplate.getForEntity(
                        UriComponentsBuilder.fromUriString(configurationService.getInvoiceEndPoint()).toUriString(),
                        InvoiceResponseDTO.class);

            emitter.onNext(invoiceResponseDTO.getBody());
            emitter.onComplete();
            long endTime = System.currentTimeMillis();
            long executeTime = endTime - startTime;
            log.info("Response time of InvoiceService : {} milliseconds", executeTime);
        };

        return Observable.<InvoiceResponseDTO>create(source)
                .doOnNext(c -> log.info("Invoice details were retrieved successfully. item id - {} ", c.getId()))
                .onErrorReturn(invoiceServiceApiErrorHandler)
                .subscribeOn(Schedulers.io());


    }
}
