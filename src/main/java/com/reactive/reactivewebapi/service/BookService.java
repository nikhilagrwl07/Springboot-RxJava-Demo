package com.reactive.reactivewebapi.service;

import com.reactive.reactivewebapi.common.dto.ItemDTO;
import com.reactive.reactivewebapi.common.request.AddBookWebRequest;
import com.reactive.reactivewebapi.common.request.UpdateBookWebRequest;
import com.reactive.reactivewebapi.entity.Book;
import io.reactivex.Completable;
import io.reactivex.Single;

import java.util.List;

public interface BookService {

    public Single<Long> addBook(AddBookWebRequest addBookWebRequest);

    Single<List<ItemDTO>> getAllBooks(int limit, int page, boolean shippingInfo, boolean invoiceInfo);

    public Completable updateBook(int bookId, UpdateBookWebRequest updateBookWebRequest);
}
