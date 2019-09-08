package com.reactive.reactivewebapi.respository;

import org.springframework.data.domain.Pageable;
import reactor.core.publisher.Flux;

public interface ReactiveRepository<T> {

    Flux<T> findAll(Pageable pageable);
}
