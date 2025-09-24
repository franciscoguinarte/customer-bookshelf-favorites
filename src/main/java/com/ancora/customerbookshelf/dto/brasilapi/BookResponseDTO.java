package com.ancora.customerbookshelf.dto.brasilapi;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class BookResponseDTO {
    private String isbn;
    private String title;
    private String subtitle;
    private List<String> authors;
    private String publisher;
    private String synopsis;
    private DimensionsDTO dimensions;
    private Integer year;
    private String format;
    @JsonProperty("page_count")
    private Integer pageCount;
    private List<String> subjects;
    private String location;
    @JsonProperty("retail_price")
    private Double retailPrice;
    @JsonProperty("cover_url")
    private String coverUrl;
    private String provider;
}
