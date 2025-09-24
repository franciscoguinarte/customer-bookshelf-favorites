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

        DimensionsDTO dimensionsDTO = Optional.ofNullable(book.getDimensions())
                .map(d -> DimensionsDTO.builder()
                        .width(d.getWidth())
                        .height(d.getHeight())
                        .unit(d.getUnit())
                        .build())
                .orElse(null);

        return BookDTO.builder()
                .isbn(book.getIsbn())
                .title(book.getTitle())
                .subtitle(book.getSubtitle())
                .authors(book.getAuthors())
                .publisher(book.getPublisher())
                .synopsis(book.getSynopsis())
                .dimensions(dimensionsDTO)
                .year(book.getYear())
                .format(book.getFormat())
                .pageCount(book.getPageCount())
                .subjects(book.getSubjects())
                .location(book.getLocation())
                .retailPrice(book.getRetailPrice())
                .coverUrl(book.getCoverUrl())
                .provider(book.getProvider())
                .build();
    }
}
