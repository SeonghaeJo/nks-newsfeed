package com.personal.nksnewfeed.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record PostCreateDto(
        @NotNull
        Long userId,
        
        @NotBlank
        @Size(max = 1000)
        String content
) {}