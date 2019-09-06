package com.reactive.reactivewebapi.common.request;

import lombok.Data;

@Data
public class UpdateBookWebRequest {
    private String isbn;
    private String title;
}
