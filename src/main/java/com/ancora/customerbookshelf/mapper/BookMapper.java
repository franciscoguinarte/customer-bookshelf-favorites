package com.ancora.customerbookshelf.mapper;

import com.ancora.customerbookshelf.dto.BookDTO;
import com.ancora.customerbookshelf.dto.DimensionsDTO;
import com.ancora.customerbookshelf.model.Book;

import java.util.Optional;

public class BookMapper {

    public static BookDTO toDTO(Book book) {
        if (book == null) {
            return null;
        }

        String author = (book.getAuthors() != null && !book.getAuthors().isEmpty()) ? book.getAuthors().get(0) : null;
        String subject = (book.getSubjects() != null && !book.getSubjects().isEmpty()) ? book.getSubjects().get(0) : null;

        return BookDTO.builder()
                .isbn(book.getIsbn())
                .title(book.getTitle())
                .subtitle(book.getSubtitle())
                .author(author)
                .publisher(book.getPublisher())
                .year(book.getYear())
                .format(book.getFormat())
                .pageCount(book.getPageCount())
                .subject(subject)
                .retailPrice(book.getRetailPrice())
                .build();
    }
}
