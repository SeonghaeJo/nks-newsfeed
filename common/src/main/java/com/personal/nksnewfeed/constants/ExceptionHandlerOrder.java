package com.personal.nksnewfeed.constants;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.core.Ordered;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ExceptionHandlerOrder {

    public static final int BUSINESS_EXCEPTION = Ordered.HIGHEST_PRECEDENCE;
    public static final int METHOD_ARGUMENT_NOT_VALID_EXCEPTION = Ordered.HIGHEST_PRECEDENCE + 1;
    public static final int GLOBAL_EXCEPTION = Ordered.LOWEST_PRECEDENCE;
}
