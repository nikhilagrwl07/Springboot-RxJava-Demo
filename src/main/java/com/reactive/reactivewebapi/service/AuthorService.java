package com.reactive.reactivewebapi.service;

import com.reactive.reactivewebapi.common.request.AddAuthorRequest;
import io.reactivex.Single;

public interface AuthorService {
    public Single<Long> addAuthor(AddAuthorRequest addAuthorRequest);
}
