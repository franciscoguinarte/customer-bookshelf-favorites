package com.ancora.customerbookshelf.dto;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class BookDTO {
    private String isbn;
    private String title;
    private String subtitle;
    private List<String> authors;
    private String publisher;
    private String synopsis;
    private DimensionsDTO dimensions;
    private Integer year;
    private String format;
    private Integer pageCount;
    private List<String> subjects;
    private String location;
    private Double retailPrice;
    private String coverUrl;
    private String provider;
}
