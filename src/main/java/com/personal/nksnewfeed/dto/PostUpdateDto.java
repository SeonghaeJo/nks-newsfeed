package com.personal.nksnewfeed.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record PostUpdateDto(
        @NotBlank
        @Size(max = 1000)
        String content
) {}