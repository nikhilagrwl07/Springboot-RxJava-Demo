package com.reactive.reactivewebapi.controller;

import com.reactive.reactivewebapi.common.dto.ItemDTO;
import com.reactive.reactivewebapi.common.response.BookWebResponse;
import com.reactive.reactivewebapi.service.BookService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.scheduler.Schedulers;


@Slf4j
@RestController
@RequestMapping("/reactive/v1/books")
public class BookReactiveController {

    @Autowired
    private BookService bookService;


    @GetMapping
    public Flux<ResponseEntity<BookWebResponse>> getAllBooksReactive(
            @RequestParam(value = "limit", defaultValue = "5") int limit,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "includeShipping", defaultValue = "false") boolean shippingInfo,
            @RequestParam(value = "includeInvoice", defaultValue = "false") boolean invoiceInfo) {
        log.info("Started processing asynchronous request");

        return bookService.getAllBooksUsingFlux(limit, page, shippingInfo, invoiceInfo)
                .subscribeOn(Schedulers.single())
                .map(book -> {
                    BookWebResponse bookWebResponse = toBookWebResponse(book);
                    return new ResponseEntity<>(bookWebResponse, HttpStatus.OK);
                })
                .defaultIfEmpty(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    private BookWebResponse toBookWebResponse(ItemDTO item) {
        BookWebResponse bookWebResponse = new BookWebResponse();
        bookWebResponse.setAuthorName(item.getAuthorName());
        bookWebResponse.setIsbn(item.getIsbn());
        bookWebResponse.setPrice(item.getPrice());
        bookWebResponse.setTitle(item.getTitle());


        bookWebResponse.setShippingStatus(item.getShippingStatus());
        bookWebResponse.setDestination(item.getDestination());

        bookWebResponse.setTotalPrice(item.getTotalPrice());
        bookWebResponse.setSalesTax(item.getSalesTax());
        bookWebResponse.setMrp(item.getMrp());
        return bookWebResponse;
    }
}
