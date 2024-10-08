package com.example.bookinventory.controller;

import com.example.bookinventory.model.Book;
import com.example.bookinventory.repository.BookRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.hamcrest.CoreMatchers.is;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc(addFilters = false)
class BookControllerTest {
    @Autowired
    MockMvc mockMvc;
    @Autowired
    private BookRepository bookRepository;
    @Autowired
    ObjectMapper objectMapper;

    private static final String API_ENDPOINT = "/api/books";
    private Book book1, book2;
    private List<Book> bookList = new ArrayList<>();

    @BeforeEach
    void setUp() {
        bookRepository.deleteAll();
        book1 = Book.builder().title("Harry Potter").author("JK Rowling").build();
        book2 = Book.builder().title("Book 2").author("kai").build();
        bookList.add(book1);
        bookList.add(book2);
    }

    @Test
    @DisplayName("JUNIT test: get all books")
    void getAllBooks() throws Exception {
        bookRepository.saveAll(bookList);
        ResultActions resultActions = mockMvc.perform(get(API_ENDPOINT));
        resultActions.andExpect(status().isOk())
                .andDo(print())
                .andExpect(jsonPath("$.size()", is(bookList.size())));
    }

    @Test
    @DisplayName("JUNIT test: get book by ID")
    void getBookById() throws Exception {
        bookRepository.save(book1);
        ResultActions resultActions = mockMvc.perform(get(API_ENDPOINT + "/{id}", book1.getId()));
        resultActions.andExpect(status().isOk())
                .andDo(print())
                .andExpect(jsonPath("$.title").value(book1.getTitle()))
                .andExpect(jsonPath("$.author").value(book1.getAuthor()))
                .andExpect(result -> assertTrue(result.getResponse().getContentAsString().contains(book1.getTitle())));
    }

    @Test
    @DisplayName("JUNIT test: create book")
    void createBook() throws Exception {
        String requestBody = objectMapper.writeValueAsString(book1);
        ResultActions resultActions = mockMvc.perform(post(API_ENDPOINT)
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody));
        resultActions.andExpect(status().isCreated())
                .andDo(print())
                .andExpect(jsonPath("$.title").value(book1.getTitle()))
                .andExpect(jsonPath("$.author").value(book1.getAuthor()))
                .andExpect(result -> assertNotNull(result.getResponse().getContentAsString()))
                .andExpect(result -> assertTrue(result.getResponse().getContentAsString().contains(book1.getTitle())));
    }

    @Test
    @DisplayName("JUNIT test: update book by id")
    void updateBook() throws Exception {
        bookRepository.save(book1);
        Book updateBook1 = bookRepository.findById(book1.getId()).get();
        updateBook1.setTitle("Updated book title");
        updateBook1.setAuthor("updated author");

        String requestBody = objectMapper.writeValueAsString(updateBook1);
        // act -  action or behaviour to test
        ResultActions resultActions = mockMvc.perform(put(API_ENDPOINT.concat("/{id}"), updateBook1.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody));

        // assert - verify the output
        resultActions.andExpect(status().isOk())
                .andDo(print())
                .andExpect(jsonPath("$.title").value(updateBook1.getTitle()))
                .andExpect(jsonPath("$.author").value(updateBook1.getAuthor()));

    }

    @Test
    @DisplayName("JUNIT test: delete book by id")
    void deleteBook() throws Exception {
        bookRepository.save(book1);
        Book deleteBook1 = bookRepository.findById(book1.getId()).get();
        String expectedResponse = String.format("%s deleted successfully", deleteBook1.getTitle());
        // act -  action or behaviour to test
        ResultActions resultActions = mockMvc.perform(delete(API_ENDPOINT.concat("/{id}"), deleteBook1.getId())
                .contentType(MediaType.APPLICATION_JSON));

        // assert - verify the output
        resultActions.andExpect(status().isOk())
                .andDo(print())
//                 Checking that the response body matches the expected message
                .andExpect(result -> assertEquals(expectedResponse, result.getResponse().getContentAsString()));
    }
}