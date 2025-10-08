package com.personal.nksnewfeed.constants;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class MdcConstants {

    public static final String REQUEST_ID = "requestId";
    public static final String TRACE_ID = "traceId";
    public static final String USER_ID = "userId";
    public static final String COMPONENT = "component";
    public static final String CLIENT_COMPONENT = "clientComponent";
}