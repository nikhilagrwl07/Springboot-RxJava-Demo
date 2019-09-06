package com.reactive.reactivewebapi.service;

import com.reactive.reactivewebapi.common.dto.ShippingResponseDTO;
import com.reactive.reactivewebapi.configuration.ConfigurationService;
import io.reactivex.Observable;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.functions.Function;
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

    @Override
    public Observable<ShippingResponseDTO> getShippingDetails(Long itemId) {

        ObservableOnSubscribe<ShippingResponseDTO> source = channel -> {

            ShippingResponseDTO shippingResponseDTO = restTemplate.getForEntity(
                    UriComponentsBuilder
                            .fromUriString(configurationService.getShippingEndPoint())
                            .toUriString(), ShippingResponseDTO.class)
                    .getBody();

            channel.onNext(shippingResponseDTO);
            channel.onComplete();
        };

        return Observable.<ShippingResponseDTO>create(source)
                .doOnNext(c -> log.info("Shipping details were retrieved successfully."))
                .onErrorReturn(new Function<Throwable, ShippingResponseDTO>() {
                    @Override
                    public ShippingResponseDTO apply(Throwable throwable) {
                        return new ShippingResponseDTO();
                    }
                })
                .subscribeOn(Schedulers.io());

    }
}
