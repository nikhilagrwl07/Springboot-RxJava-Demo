package com.reactive.reactivewebapi.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.reactive.reactivewebapi.common.request.AddAuthorRequest;
import com.reactive.reactivewebapi.service.AuthorService;
import io.reactivex.Single;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.hamcrest.Matchers.nullValue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.asyncDispatch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@WebMvcTest(value = AuthorController.class)
public class AuthorControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @MockBean
    AuthorService authorService;

    @Test
    public void AddAuthor_Success_expect201Created() throws Exception {

        // when
        when(authorService.addAuthor(any(AddAuthorRequest.class)))
                .thenReturn(Single.just(1L));

        // action
        MvcResult mvcResult = mockMvc.perform(post("/v1/authors")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(new AddAuthorRequest("AuthorName1"))))
                .andReturn();

        //verify
        mockMvc.perform(asyncDispatch(mvcResult))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.errorCode", nullValue()))
                .andExpect(jsonPath("$.data", nullValue()));

        verify(authorService, times(1)).addAuthor(any(AddAuthorRequest.class));

    }

}
