package com.reactive.reactivewebapi.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.reactive.reactivewebapi.common.dto.InvoiceResponseDTO;
import com.reactive.reactivewebapi.common.dto.ItemDTO;
import com.reactive.reactivewebapi.common.dto.ShippingResponseDTO;
import com.reactive.reactivewebapi.common.request.AddBookWebRequest;
import com.reactive.reactivewebapi.exception.BookNotFoundException;
import com.reactive.reactivewebapi.service.BookService;
import io.reactivex.Single;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;

import java.util.Collections;

import static com.reactive.reactivewebapi.common.ErrorCode.AUTHOR_NOT_FOUND;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.Matchers.nullValue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@WebMvcTest(value = BookController.class)
public class BookControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @MockBean
    BookService bookService;

    AddBookWebRequest addBookWebRequest;

    @Before
    public void setUp() throws Exception {
        addBookWebRequest = AddBookWebRequest.builder()
                .authorId("author100")
                .isbn("isbn1")
                .price(99.99)
                .title("Title1").build();
    }

    @Test
    public void AddBook_Success_expect201Created() throws Exception {

        // when
        when(bookService.addBook(any(AddBookWebRequest.class)))
                .thenReturn(Single.just(1L));

        // action
        MvcResult mvcResult = mockMvc.perform(post("/v1/books")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(addBookWebRequest)))
                .andReturn();

        //verify
        mockMvc.perform(asyncDispatch(mvcResult))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.errorCode", nullValue()))
                .andExpect(jsonPath("$.data", nullValue()));

        verify(bookService, times(1)).addBook(any(AddBookWebRequest.class));

    }

    @Test
    public void AddBook_Failure_expect400() throws Exception {

        // when
        when(bookService.addBook(any(AddBookWebRequest.class)))
                .thenReturn(Single.error(new BookNotFoundException("Author not found")));

        // action
        MvcResult mvcResult = mockMvc.perform(post("/v1/books")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(addBookWebRequest)))
                .andReturn();

        //verify
        mockMvc.perform(asyncDispatch(mvcResult))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.errorCode", equalTo(AUTHOR_NOT_FOUND.toString())))
                .andExpect(jsonPath("$.data", nullValue()))
                .andDo(MockMvcResultHandlers.print());

        verify(bookService, times(1)).addBook(any(AddBookWebRequest.class));
    }

    @Test
    public void GetAllBook_Success_limitAndPageNotSpecified() throws Exception {

        // setup
        ItemDTO itemDTO = new ItemDTO(1L, "123", 10.99, "title", "author1");

        // when
        when(bookService.getAllBooks(anyInt(), anyInt(), anyBoolean(), anyBoolean()))
                .thenReturn(Single.just(Collections.singletonList(itemDTO)));
        // action
        MvcResult mvcResult = mockMvc.perform(get("/v1/books")
                .contentType(MediaType.APPLICATION_JSON))
                .andReturn();

        //verify
        mockMvc.perform(asyncDispatch(mvcResult))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.errorCode", nullValue()))
                .andExpect(jsonPath("$.data[0].isbn", equalTo(itemDTO.getIsbn())))
                .andExpect(jsonPath("$.data[0].price", equalTo(itemDTO.getPrice())))
                .andExpect(jsonPath("$.data[0].title", equalTo(itemDTO.getTitle())))
                .andExpect(jsonPath("$.data[0].authorName", equalTo(itemDTO.getAuthorName())));

        verify(bookService, times(1)).getAllBooks(anyInt(), anyInt(), anyBoolean(), anyBoolean());
    }

    @Test
    public void GetAllBook_Success_limitAndPageDefaultWithShippingAndInvoiceDetails() throws Exception {

        // setup
        ShippingResponseDTO shippingResponseDTO = new ShippingResponseDTO("1", "Shipped", "desc1");
        InvoiceResponseDTO invoiceResponseDTO = new InvoiceResponseDTO("1", 12.99, 2.99, 10.0);
        ItemDTO itemDTO = new ItemDTO(1L, "123", 10.99, "title", "author1");
        itemDTO.setItemShippingDTO(shippingResponseDTO);
        itemDTO.setItemInvoiceDTO(invoiceResponseDTO);

        //when
        when(bookService.getAllBooks(5, 0, true, true))
                .thenReturn(Single.just(Collections.singletonList(itemDTO)));

        // action
        MvcResult mvcResult = mockMvc.perform(
                get("/v1/books")
                        .param("includeShipping", "true")
                        .param("includeInvoice", "true")
                        .contentType(MediaType.APPLICATION_JSON))
                .andReturn();

        //verify
        mockMvc.perform(asyncDispatch(mvcResult))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.errorCode", nullValue()))
                .andExpect(jsonPath("$.data[0].isbn", equalTo(itemDTO.getIsbn())))
                .andExpect(jsonPath("$.data[0].price", equalTo(itemDTO.getPrice())))
                .andExpect(jsonPath("$.data[0].title", equalTo(itemDTO.getTitle())))
                .andExpect(jsonPath("$.data[0].authorName", equalTo(itemDTO.getAuthorName())))
                .andExpect(jsonPath("$.data[0].shippingStatus", equalTo(itemDTO.getShippingStatus())))
                .andExpect(jsonPath("$.data[0].destination", equalTo(itemDTO.getDestination())))
                .andExpect(jsonPath("$.data[0].totalPrice", equalTo(itemDTO.getTotalPrice())))
                .andExpect(jsonPath("$.data[0].salesTax", equalTo(itemDTO.getSalesTax())))
                .andExpect(jsonPath("$.data[0].mrp", equalTo(itemDTO.getMrp())));

        verify(bookService, times(1)).getAllBooks(5, 0, true, true);
    }


}
