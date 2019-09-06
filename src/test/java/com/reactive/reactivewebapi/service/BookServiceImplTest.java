package com.reactive.reactivewebapi.service;

import com.reactive.reactivewebapi.common.dto.InvoiceResponseDTO;
import com.reactive.reactivewebapi.common.dto.ItemDTO;
import com.reactive.reactivewebapi.common.dto.ItemResponseDTO;
import com.reactive.reactivewebapi.common.dto.ShippingResponseDTO;
import com.reactive.reactivewebapi.common.request.AddBookWebRequest;
import com.reactive.reactivewebapi.entity.Author;
import com.reactive.reactivewebapi.entity.Book;
import com.reactive.reactivewebapi.exception.BookNotFoundException;
import com.reactive.reactivewebapi.respository.AuthorRepository;
import com.reactive.reactivewebapi.respository.BookRepository;
import io.reactivex.Observable;
import io.reactivex.observers.TestObserver;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@RunWith(SpringRunner.class)
public class BookServiceImplTest {

    @Mock
    BookRepository bookRepository;

    @Mock
    AuthorRepository authorRepository;

    @InjectMocks
    BookServiceImpl bookService;

    @Mock
    ItemService itemService;

    @Mock
    ShippingService shippingService;

    @Mock
    InvoiceService invoiceService;

    Author author;

    Book book;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);

        author = new Author(2L, "author1");

        book = Book.builder().id(1L).isbn("isbn1").price(10.99).author(author).build();
    }

    @Test
    public void addBook_Success_ReturnSingleSavedBookId() {

        //when
        when(authorRepository.findById(anyLong())).thenReturn(Optional.of(author));
        when(bookRepository.save(any(Book.class))).thenReturn(book);

        //action
        AddBookWebRequest addBookWebRequest = new AddBookWebRequest(book.getTitle(),
                book.getIsbn(), String.valueOf(author.getId()), book.getPrice());

        bookService.addBook(addBookWebRequest)
                .test()
                .assertComplete()
                .assertNoErrors()
                .assertValue(book.getId())
                .awaitTerminalEvent();

        // verify
        InOrder inOrder = inOrder(authorRepository, bookRepository);
        inOrder.verify(authorRepository, times(1)).findById(anyLong());
        inOrder.verify(bookRepository, times(1)).save(any(Book.class));
    }

    @Test
    public void addBook_Failure_ThrowException() {

        //when
        when(authorRepository.findById(anyLong())).thenThrow(new BookNotFoundException("Author not found"));
        when(bookRepository.save(any(Book.class))).thenReturn(book);

        //action
        AddBookWebRequest addBookWebRequest = new AddBookWebRequest(book.getTitle(),
                book.getIsbn(), String.valueOf(author.getId()), book.getPrice());

        bookService.addBook(addBookWebRequest)
                .test()
                .assertNotComplete()
                .assertError(BookNotFoundException.class)
                .awaitTerminalEvent();

        // verify
        InOrder inOrder = inOrder(authorRepository, bookRepository);
        inOrder.verify(authorRepository, times(1)).findById(anyLong());
        inOrder.verify(bookRepository, never()).save(any(Book.class));
    }

    @Test
    public void getAllBooks_Success_ShippingAndInvoiceServicesNotInvoked() {

        //setup
        Author author1 = new Author(1L, "author1");
        Author author2 = new Author(2L, "author2");
        Book firstBook = new Book(1L, "123", 10.99, "title1", author1);
        Book secondBook = new Book(2L, "234", 15.99, "title2", author2);
        List<Book> allBooks = Arrays.asList(firstBook, secondBook);

        ItemResponseDTO firstBookDto = new ItemResponseDTO(String.valueOf(firstBook.getId()), firstBook.getTitle(), firstBook.getPrice(), firstBook.getIsbn());
        ItemResponseDTO secondBookDto = new ItemResponseDTO(String.valueOf(secondBook.getId()), secondBook.getTitle(), secondBook.getPrice(), secondBook.getIsbn());

        //when
        when(bookRepository.findAll(any(PageRequest.class))).thenReturn(new PageImpl<>(allBooks));
        when(itemService.getItemDetails(firstBook.getId())).thenReturn(Observable.just(firstBookDto));
        when(itemService.getItemDetails(secondBook.getId())).thenReturn(Observable.just(secondBookDto));

        //action
        TestObserver<List<ItemDTO>> testObserver = bookService.getAllBooks(5, 0, false, false).test();
        testObserver.awaitTerminalEvent();

        // verify
        testObserver.assertValue(bookResponses -> bookResponses.get(0).getId().equals(
                Long.valueOf(firstBookDto.getId()))
                &&
                bookResponses.get(1).getId().equals(Long.valueOf(secondBookDto.getId())));


        verify(bookRepository, times(1)).findAll(any(PageRequest.class));
        verify(itemService, times(2)).getItemDetails(anyLong());
    }


    @Test
    public void getAllBooks_Success_ShippingServiceThrowing500AndInvoiceServices200() {

        //setup
        Author author1 = new Author(1L, "author1");
        Author author2 = new Author(2L, "author2");
        Book firstBook = new Book(1L, "123", 10.99, "title1", author1);
        Book secondBook = new Book(2L, "234", 15.99, "title2", author2);
        List<Book> allBooks = Arrays.asList(firstBook, secondBook);

        ItemResponseDTO firstBookDto = new ItemResponseDTO(String.valueOf(firstBook.getId()), firstBook.getTitle(), firstBook.getPrice(), firstBook.getIsbn());
        ItemResponseDTO secondBookDto = new ItemResponseDTO(String.valueOf(secondBook.getId()), secondBook.getTitle(), secondBook.getPrice(), secondBook.getIsbn());
        ShippingResponseDTO nullShippingDto = new ShippingResponseDTO();
        InvoiceResponseDTO firstInvoiceResponseDTO = new InvoiceResponseDTO(String.valueOf(firstBook.getId()), firstBook.getPrice(), 2.99, 8.0);
        InvoiceResponseDTO secondInvoiceResponseDTO = new InvoiceResponseDTO(String.valueOf(secondBook.getId()), secondBook.getPrice(), 2.99, 13.0);

        //when
        when(bookRepository.findAll(any(PageRequest.class))).thenReturn(new PageImpl<>(allBooks));
        when(itemService.getItemDetails(firstBook.getId())).thenReturn(Observable.just(firstBookDto));
        when(itemService.getItemDetails(secondBook.getId())).thenReturn(Observable.just(secondBookDto));

        // shipping service returning null itemId in case of any exception(4XX or 5XX)
        when(shippingService.getShippingDetails(firstBook.getId())).thenReturn(Observable.just(nullShippingDto));
        when(shippingService.getShippingDetails(secondBook.getId())).thenReturn(Observable.just(nullShippingDto));

        // shipping service returning correct response (Response Code - 2XX)
        when(invoiceService.getInvoiceDetails(firstBook.getId())).thenReturn(Observable.just(firstInvoiceResponseDTO));
        when(invoiceService.getInvoiceDetails(secondBook.getId())).thenReturn(Observable.just(secondInvoiceResponseDTO));

        //action
        TestObserver<List<ItemDTO>> testObserver = bookService.getAllBooks(5, 0, true, true).test();
        testObserver.awaitTerminalEvent();

        // verify
        testObserver.assertValue(itemDTO ->
                itemDTO.get(0).getId().equals(Long.valueOf(firstBookDto.getId()))
                        &&
                        itemDTO.get(1).getId().equals(Long.valueOf(secondBookDto.getId()))
                        &&
                        itemDTO.get(0).getShippingStatus() == null
                        &&
                        itemDTO.get(0).getDestination() == null
                        &&
                        itemDTO.get(0).getTotalPrice().equals(firstBookDto.getPrice())
                        &&
                        itemDTO.get(1).getTotalPrice().equals(secondBookDto.getPrice())
        );

        verify(bookRepository, times(1)).findAll(any(PageRequest.class));
        verify(itemService, times(2)).getItemDetails(anyLong());
    }


    @Test
    public void getAllBooks_Success_withLimitAndPage() {

        //setup
        Author author1 = new Author(1L, "author1");
        Author author2 = new Author(2L, "author2");
        Book firstBook = new Book(1L, "123", 10.99, "title1", author1);
        Book secondBook = new Book(2L, "234", 15.99, "title2", author2);
        ItemResponseDTO firstBookDto = new ItemResponseDTO(String.valueOf(firstBook.getId()), firstBook.getTitle(), firstBook.getPrice(), firstBook.getIsbn());
        ItemResponseDTO secondBookDto = new ItemResponseDTO(String.valueOf(secondBook.getId()), secondBook.getTitle(), secondBook.getPrice(), secondBook.getIsbn());
        List<Book> allBooks = Collections.singletonList(secondBook);
        int page = 1;
        int limit = 1;
        PageRequest pageRequest = PageRequest.of(page, limit);

        //when
        when(bookRepository.findAll(pageRequest)).thenReturn(new PageImpl<>(allBooks));
        when(itemService.getItemDetails(firstBook.getId())).thenReturn(Observable.just(firstBookDto));
        when(itemService.getItemDetails(secondBook.getId())).thenReturn(Observable.just(secondBookDto));

        //action
        TestObserver<List<ItemDTO>> testObserver = bookService.getAllBooks(limit, page, false, false).test();
        testObserver.awaitTerminalEvent();

        // verify
        testObserver.assertValue(bookResponses -> bookResponses.get(0).getId().equals(secondBook.getId()));

        verify(bookRepository, times(1)).findAll(pageRequest);
        verify(itemService, times(1)).getItemDetails(anyLong());
    }


}
