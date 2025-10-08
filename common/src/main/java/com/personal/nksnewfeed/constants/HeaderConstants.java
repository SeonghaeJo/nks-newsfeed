package com.personal.nksnewfeed.constants;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class HeaderConstants {

    public static final String TRACE_ID = "X-Trace-Id";
    public static final String REQUEST_ID = "X-Request-Id";
    public static final String USER_ID = "X-User-Id";
    public static final String COMPONENT = "X-Component";
}