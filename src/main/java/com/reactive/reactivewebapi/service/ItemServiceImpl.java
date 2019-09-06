package com.reactive.reactivewebapi.service;

import com.reactive.reactivewebapi.common.dto.ItemResponseDTO;
import com.reactive.reactivewebapi.configuration.ConfigurationService;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
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
public class ItemServiceImpl implements ItemService {

    @Autowired
    RestTemplate restTemplate;

    @Autowired
    ConfigurationService configurationService;

    @Override
    public Observable<ItemResponseDTO> getItemDetails(Long itemId) {


        ObservableOnSubscribe<ItemResponseDTO> source = new ObservableOnSubscribe<ItemResponseDTO>() {
            @Override
            public void subscribe(ObservableEmitter<ItemResponseDTO> emitter) throws Exception {

                ItemResponseDTO itemResponseDTO = restTemplate.getForEntity(
                        UriComponentsBuilder
                                .fromUriString(configurationService.getItemEndPoint())
                                .toUriString(), ItemResponseDTO.class).getBody();

                emitter.onNext(itemResponseDTO);
                emitter.onComplete();
            }
        };


        return Observable.<ItemResponseDTO>create(source)
                .doOnNext(c -> log.info("Item details were retrieved successfully."))
                .onErrorReturn(new Function<Throwable, ItemResponseDTO>() {
                    @Override
                    public ItemResponseDTO apply(Throwable throwable) {
                        return new ItemResponseDTO();
                    }
                })
                .subscribeOn(Schedulers.io());
    }
}
