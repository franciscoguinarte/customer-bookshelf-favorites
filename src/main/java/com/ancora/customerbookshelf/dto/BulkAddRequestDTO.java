package com.ancora.customerbookshelf.dto;

import lombok.Data;

import java.util.List;

@Data
public class BulkAddRequestDTO {
    private List<String> isbns;
}
