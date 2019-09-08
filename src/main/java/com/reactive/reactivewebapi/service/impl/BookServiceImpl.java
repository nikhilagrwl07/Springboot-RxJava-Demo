package com.reactive.reactivewebapi.service.impl;

import com.reactive.reactivewebapi.common.dto.InvoiceResponseDTO;
import com.reactive.reactivewebapi.common.dto.ItemDTO;
import com.reactive.reactivewebapi.common.dto.ItemResponseDTO;
import com.reactive.reactivewebapi.common.dto.ShippingResponseDTO;
import com.reactive.reactivewebapi.common.request.AddBookWebRequest;
import com.reactive.reactivewebapi.common.request.UpdateBookWebRequest;
import com.reactive.reactivewebapi.entity.Author;
import com.reactive.reactivewebapi.entity.Book;
import com.reactive.reactivewebapi.exception.AuthorNotFoundException;
import com.reactive.reactivewebapi.exception.BookNotFoundException;
import com.reactive.reactivewebapi.respository.AuthorRepository;
import com.reactive.reactivewebapi.respository.BookReactiveRepository;
import com.reactive.reactivewebapi.respository.BookRepository;
import com.reactive.reactivewebapi.service.BookService;
import com.reactive.reactivewebapi.service.InvoiceService;
import com.reactive.reactivewebapi.service.ItemService;
import com.reactive.reactivewebapi.service.ShippingService;
import io.reactivex.Completable;
import io.reactivex.Observable;
import io.reactivex.Single;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
public class BookServiceImpl implements BookService {

    @Autowired
    private AuthorRepository authorRepository;

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    ItemService itemService;

    @Autowired
    ShippingService shippingService;

    @Autowired
    InvoiceService invoiceService;

    @Autowired
    BookReactiveRepository bookReactiveRepository;

    @Override
    public Single<Long> addBook(AddBookWebRequest addBookWebRequest) {
        return Single.create(emitter -> {

            // finding author by id
            Optional<Author> authorOptional = authorRepository.findById(Long.valueOf(addBookWebRequest.getAuthorId()));

            if (!authorOptional.isPresent()) {
                emitter.onError(new AuthorNotFoundException("Author not found"));
            } else {

                // Create book save request
                Book book = Book.builder()
                        .author(authorOptional.get())
                        .isbn(addBookWebRequest.getIsbn())
                        .price(addBookWebRequest.getPrice())
                        .title(addBookWebRequest.getTitle())
                        .build();

                emitter.onSuccess(bookRepository.save(book).getId());
            }
        });
    }

    @Override
    public Flux<ItemDTO> getAllBooksUsingFlux(int limit, int page, boolean shippingInfo, boolean invoiceInfo) {

        long startTime = System.currentTimeMillis();
        Flux<Book> booksFlux = bookReactiveRepository.findAll(PageRequest.of(page, limit));

        return booksFlux
                .map(book -> {
                    ItemDTO itemDTO = extractItemDetails(shippingInfo, invoiceInfo, book);
                    long endTime = System.currentTimeMillis();
                    long executeTime = endTime - startTime;
                    log.info("Request take time: {} milliseconds", executeTime);
                    return itemDTO;
                });
    }


    @Override
    public Single<List<ItemDTO>> getAllBooks(int limit, int page, boolean shippingInfo, boolean invoiceInfo) {

        return Single.create(emitter -> {

            long startTime = System.currentTimeMillis();
            List<Book> allBooks = bookRepository.findAll(PageRequest.of(page, limit)).getContent();

            List<ItemDTO> allItems = new ArrayList<>();

            allBooks.parallelStream().forEach(book -> {
                ItemDTO itemDTO = extractItemDetails(shippingInfo, invoiceInfo, book);
                allItems.add(itemDTO);
            });

            emitter.onSuccess(allItems);
            long endTime = System.currentTimeMillis();
            long executeTime = endTime - startTime;
            log.info("Request take time: {} milliseconds", executeTime);
        });
    }

    private ItemDTO extractItemDetails(boolean shippingInfo, boolean invoiceInfo, Book book) {

        // fetch latest price by item bean api
        Observable<ItemResponseDTO> itemResponseDTOObservable = itemService.getItemDetails(book.getId());

        // fetch shipping related information
        Observable<ShippingResponseDTO> shippingResponseDTOObservable = shippingService.getShippingDetails(book.getId());

        // fetch invoice related information
        Observable<InvoiceResponseDTO> invoiceResponseDTOObservable = invoiceService.getInvoiceDetails(book.getId());

        // create new ItemDTO object
        ItemDTO item = new ItemDTO();

        if (shippingInfo && invoiceInfo) {
            return Observable.zip(itemResponseDTOObservable, shippingResponseDTOObservable, invoiceResponseDTOObservable,
                    (itemResponseDTO, shippingResponseDTO, invoiceResponseDTO) -> {

                        item.setAuthorName(book.getAuthor().getName());
                        setItemDetailsInItemDTO(item, itemResponseDTO);
                        setItemShippingDetailsInItemDTO(item, shippingResponseDTO);
                        setItemInvoiceDetailsInItemDTO(item, invoiceResponseDTO);
                        return item;
                    }).blockingLast();

        } else if (shippingInfo) {
            return Observable.zip(itemResponseDTOObservable, shippingResponseDTOObservable,
                    (itemResponseDTO, shippingResponseDTO) -> {

                        item.setAuthorName(book.getAuthor().getName());
                        setItemDetailsInItemDTO(item, itemResponseDTO);
                        setItemShippingDetailsInItemDTO(item, shippingResponseDTO);
                        return item;
                    }).blockingLast();

        } else if (invoiceInfo) {
            return Observable.zip(itemResponseDTOObservable, invoiceResponseDTOObservable,
                    (itemResponseDTO, invoiceResponseDTO) -> {

                        item.setAuthorName(book.getAuthor().getName());
                        setItemDetailsInItemDTO(item, itemResponseDTO);
                        setItemInvoiceDetailsInItemDTO(item, invoiceResponseDTO);
                        return item;
                    }).blockingLast();
        } else {
            ItemResponseDTO itemResponseDTO = itemResponseDTOObservable.blockingLast();
            item.setAuthorName(book.getAuthor().getName());
            item.setItemResponseDTO(itemResponseDTO);
            return item;
        }
    }

    private void setItemInvoiceDetailsInItemDTO(ItemDTO itemDTO, InvoiceResponseDTO invoiceResponseDTO) {

        // in case of exception id will be null
        if (invoiceResponseDTO.getId() == null) {
            return;
        }

        itemDTO.setItemInvoiceDTO(invoiceResponseDTO);
    }

    private void setItemShippingDetailsInItemDTO(ItemDTO itemDTO, ShippingResponseDTO shippingResponseDTO) {

        // in case of exception id will be null
        if (shippingResponseDTO.getId() == null) {
            return;
        }

        itemDTO.setItemShippingDTO(shippingResponseDTO);
    }

    private void setItemDetailsInItemDTO(ItemDTO itemDTO, ItemResponseDTO itemResponseDTO) {

        // in case of exception id will be null
        if (itemResponseDTO.getId() == null) {
            return;
        }

        itemDTO.setItemResponseDTO(itemResponseDTO);

    }

    @Override
    public Completable updateBook(int bookId,
                                  UpdateBookWebRequest updateBookWebRequest) {

        return Completable.create(emitter -> {

            Optional<Book> book = bookRepository.findById((long) bookId);

            if (!book.isPresent()) {
                emitter.onError(new BookNotFoundException("Book not found"));
            } else {

                Observable<ItemResponseDTO> itemDetails = itemService.getItemDetails((long) bookId);

                itemDetails.subscribe(itemResponseDTO -> {

                    // create new book object
                    Book currentBook = book.get();
                    currentBook.setIsbn(updateBookWebRequest.getIsbn());
                    currentBook.setTitle(updateBookWebRequest.getTitle());
                    currentBook.setPrice(itemResponseDTO.getPrice());

                    // save newly created book object
                    bookRepository.save(currentBook);
                    emitter.onComplete();

                });
            }
        });
    }
}
