package com.reactive.reactivewebapi.initializer;

import com.reactive.reactivewebapi.entity.Author;
import com.reactive.reactivewebapi.entity.Book;
import com.reactive.reactivewebapi.respository.AuthorRepository;
import com.reactive.reactivewebapi.respository.BookRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

@Component
@Profile("local")
public class BooksDataInitializer implements CommandLineRunner {

    @Autowired
    AuthorRepository authorRepository;

    @Autowired
    BookRepository bookRepository;

    @Override
    public void run(String... args) throws Exception {
        setUpInitialData();
    }

    private void setUpInitialData() {

        Author firstAuthor = new Author(1L, "Nikhil");
        Author secondAuthor = new Author(2L, "Ritu");

        authorRepository.saveAll(Arrays.asList(firstAuthor, secondAuthor));


        List<Book> allBooks = Arrays.asList(
                new Book(1L, "123", 10.99, "Title1", firstAuthor),
                new Book(2L, "234", 100.99, "Title2", secondAuthor),
                new Book(3L, "345", 80.99, "Title3", secondAuthor));

        bookRepository.saveAll(allBooks);
    }
}
