package com.reactive.reactivewebapi.service.impl;

import com.reactive.reactivewebapi.apiHandler.ShippingServiceApiErrorHandler;
import com.reactive.reactivewebapi.common.dto.ShippingResponseDTO;
import com.reactive.reactivewebapi.configuration.ConfigurationService;
import com.reactive.reactivewebapi.service.ShippingService;
import io.reactivex.Observable;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.schedulers.Schedulers;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

@Slf4j
@Service
public class ShippingServiceImpl implements ShippingService {

    @Autowired
    RestTemplate restTemplate;

    @Autowired
    private ConfigurationService configurationService;

    @Autowired
    ShippingServiceApiErrorHandler shippingServiceApiErrorHandler;

    @Override
    public Observable<ShippingResponseDTO> getShippingDetails(Long itemId) {

        ObservableOnSubscribe<ShippingResponseDTO> source = channel -> {

            long startTime = System.currentTimeMillis();
            ShippingResponseDTO shippingResponseDTO = restTemplate.getForEntity(
                    UriComponentsBuilder
                            .fromUriString(configurationService.getShippingEndPoint())
                            .toUriString(), ShippingResponseDTO.class)
                    .getBody();

            channel.onNext(shippingResponseDTO);
            channel.onComplete();

            long endTime = System.currentTimeMillis();
            long executeTime = endTime - startTime;
            log.info("Response time of ShippingService : {} milliseconds", executeTime);
        };

        return Observable.<ShippingResponseDTO>create(source)
                .doOnNext(c -> log.info("Shipping details were retrieved successfully."))
                .onErrorReturn(shippingServiceApiErrorHandler)
                .subscribeOn(Schedulers.io());

    }
}