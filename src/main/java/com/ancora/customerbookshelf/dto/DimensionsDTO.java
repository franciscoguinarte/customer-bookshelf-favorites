package com.ancora.customerbookshelf.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class DimensionsDTO {
    private Double width;
    private Double height;
    private String unit;
}
