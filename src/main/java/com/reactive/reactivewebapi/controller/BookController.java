package com.reactive.reactivewebapi.controller;

import com.reactive.reactivewebapi.common.dto.ItemDTO;
import com.reactive.reactivewebapi.common.request.AddBookWebRequest;
import com.reactive.reactivewebapi.common.request.UpdateBookWebRequest;
import com.reactive.reactivewebapi.common.response.BaseWebResponse;
import com.reactive.reactivewebapi.common.response.BookWebResponse;
import com.reactive.reactivewebapi.service.BookService;
import io.reactivex.Single;
import io.reactivex.schedulers.Schedulers;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.async.DeferredResult;

import java.net.URI;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping("/v1/books")
public class BookController {

    @Autowired
    private BookService bookService;

    @PostMapping
    public Single<ResponseEntity<BaseWebResponse>> addBook(@RequestBody AddBookWebRequest addBookWebRequest) {
        return bookService.addBook(addBookWebRequest)
                .subscribeOn(Schedulers.io())
                .map(s -> ResponseEntity
                        .created(URI.create("/v1/books/" + s))
                        .body(BaseWebResponse.successNoData()));
    }

    @PutMapping
    public Single<ResponseEntity<BaseWebResponse>> updateBook(
            @RequestBody UpdateBookWebRequest updateBookWebRequest,
            @RequestParam int bookId) {
        return bookService.updateBook(bookId, updateBookWebRequest)
                .subscribeOn(Schedulers.io())
                .toSingle(() -> ResponseEntity
                        .ok(BaseWebResponse.successNoData()));

    }

    @GetMapping
    public DeferredResult<ResponseEntity<BaseWebResponse<List<BookWebResponse>>>> getAllBooks(
            @RequestParam(value = "limit", defaultValue = "5") int limit,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "includeShipping", defaultValue = "false") boolean shippingInfo,
            @RequestParam(value = "includeInvoice", defaultValue = "false") boolean invoiceInfo)

    {
        log.info("Started processing asynchronous request");
        final DeferredResult<ResponseEntity<BaseWebResponse<List<BookWebResponse>>>> deferredResult = new DeferredResult<>();

        ResponseEntity<BaseWebResponse<List<BookWebResponse>>> baseWebResponseResponseEntity = bookService.getAllBooks(limit, page, shippingInfo, invoiceInfo)
                .subscribeOn(Schedulers.io())
                .map(books -> ResponseEntity.ok((BaseWebResponse.successWithData(convertToWebResponse(books)))))
                .blockingGet();

        deferredResult.setResult(baseWebResponseResponseEntity);
        return deferredResult;
    }

    private List<BookWebResponse> convertToWebResponse(List<ItemDTO> items) {
        return items
                .stream()
                .map(this::toBookWebResponse)
                .collect(Collectors.toList());
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
