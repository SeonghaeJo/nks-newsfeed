package com.personal.nksnewfeed.constants;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.core.Ordered;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class FilterOrder {

    public static final int REQUEST_ID_FILTER = Ordered.HIGHEST_PRECEDENCE;
    public static final int TRACE_ID_FILTER = Ordered.HIGHEST_PRECEDENCE + 1;
    public static final int JWT_AUTHENTICATION_FILTER = Ordered.HIGHEST_PRECEDENCE + 2;
}