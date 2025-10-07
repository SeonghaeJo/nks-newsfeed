package com.personal.nksnewfeed.exception;

import lombok.Getter;

@Getter
public class MissingHeaderException extends RuntimeException {

    private final String headerName;

    public MissingHeaderException(final String headerName) {
        super("Required header is missing: " + headerName);
        this.headerName = headerName;
    }
}