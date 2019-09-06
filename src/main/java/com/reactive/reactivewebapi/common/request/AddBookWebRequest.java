package com.reactive.reactivewebapi.common.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class AddBookWebRequest {
    private String title;
    private String isbn;
    private String authorId;
    private Double price;

}
