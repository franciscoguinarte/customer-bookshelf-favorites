package com.ancora.customerbookshelf.dto;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class FavoritesSummaryDTO {
    private long count;
    private List<String> mostFrequentThemes;
    private List<String> mostFrequentAuthors;
}
