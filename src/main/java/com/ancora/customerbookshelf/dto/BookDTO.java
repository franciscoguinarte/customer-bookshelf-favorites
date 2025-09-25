package com.ancora.customerbookshelf.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class BookDTO {
    private String isbn;
    private String title;
    private String subtitle;
    private String author;
    private String publisher;
    private Integer year;
    private String format;
    private Integer pageCount;
    private String subject;
    private Double retailPrice;
}
