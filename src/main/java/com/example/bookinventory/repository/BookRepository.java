package com.example.bookinventory.repository;

import com.example.bookinventory.model.Book;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BookRepository extends JpaRepository<Book,Long> {
}
