package com.deeplearning.aitrash.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor
@Builder
public class UploadImageDTO{
    private byte[] imageSource;
    private String contentType;
    private double percentCan;
    private double percentGlass;
    private double percentPlastic;
}
