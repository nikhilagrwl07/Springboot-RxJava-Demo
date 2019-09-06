package com.reactive.reactivewebapi.controller;

import com.reactive.reactivewebapi.common.request.AddAuthorRequest;
import com.reactive.reactivewebapi.common.response.BaseWebResponse;
import com.reactive.reactivewebapi.service.AuthorService;
import io.reactivex.Single;
import io.reactivex.schedulers.Schedulers;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;

@Slf4j
@RestController
@RequestMapping("/v1/authors")
public class AuthorController {

    @Autowired
    private AuthorService authorService;

    @PostMapping
    public Single<ResponseEntity<BaseWebResponse>> addAuthor(@RequestBody AddAuthorRequest addAuthorRequest) {
        return authorService.addAuthor(addAuthorRequest)
                .subscribeOn(Schedulers.io())
                .map(s ->  ResponseEntity
                        .created(URI.create("/v1/authors/" + s))
                        .body(BaseWebResponse.successNoData()));
    }
}
