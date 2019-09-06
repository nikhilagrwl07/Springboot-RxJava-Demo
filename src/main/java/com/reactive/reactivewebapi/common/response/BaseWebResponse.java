package com.reactive.reactivewebapi.common.response;

import com.reactive.reactivewebapi.common.ErrorCode;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class BaseWebResponse<T> {
    private ErrorCode errorCode;
    private T data;

    // Success with empty response body
    public static BaseWebResponse successNoData() {
        return BaseWebResponse
                .builder()
                .build();
    }

    // Success with response body
    public static <T> BaseWebResponse<T> successWithData(T data) {
        return BaseWebResponse.<T>builder()
                .data(data)
                .build();
    }

    public static BaseWebResponse error(ErrorCode errorCode) {
        return BaseWebResponse.builder()
                .errorCode(errorCode)
                .build();
    }


}
