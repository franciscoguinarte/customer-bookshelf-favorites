package com.ancora.customerbookshelf.dto.brasilapi;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class DimensionsDTO {
    private Double width;
    private Double height;
    private String unit;
}
