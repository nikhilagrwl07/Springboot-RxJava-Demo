package com.reactive.reactivewebapi.service.impl;

import com.reactive.reactivewebapi.common.request.AddAuthorRequest;
import com.reactive.reactivewebapi.entity.Author;
import com.reactive.reactivewebapi.respository.AuthorRepository;
import com.reactive.reactivewebapi.service.AuthorService;
import io.reactivex.Single;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AuthorServiceImpl implements AuthorService {

    @Autowired
    AuthorRepository authorRepository;

    @Override
    public Single<Long> addAuthor(AddAuthorRequest addAuthorRequest) {
        return Single.create(emitter -> {
            Author savedAuthor = authorRepository.save(new Author(addAuthorRequest.getName()));
            emitter.onSuccess(savedAuthor.getId());
        });
    }
}
