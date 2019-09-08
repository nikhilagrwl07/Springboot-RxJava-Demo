package com.reactive.reactivewebapi.respository;

import com.reactive.reactivewebapi.entity.Book;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;

import java.time.Duration;

@Component
public class BookReactiveRepository implements ReactiveRepository<Book> {

    private final static long DEFAULT_DELAY_IN_MS = 10;

    @Autowired
    BookRepository bookRepository;

    @Override
    public Flux<Book> findAll(Pageable pageable) {
        return Flux.fromIterable(bookRepository.findAll()).log();
    }

    private Flux<Book> withDelay(Flux<Book> bookFlux) {
        return Flux
                .interval(Duration.ofMillis(DEFAULT_DELAY_IN_MS))
                .zipWith(bookFlux, (i, user) -> user);
    }
}
