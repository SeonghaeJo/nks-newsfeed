package com.personal.nksnewfeed.constants;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Component {
    API_GATEWAY("api-gateway"),
    USER_API("user-api"),
    POST_API("post-api"),
    POST_FANOUT_API("post-fanout-api"),
    NEWSFEED_API("newsfeed-api"),
    FRIEND_API("friend-api"),
    UNKNOWN("unknown");

    private final String value;

    public static Component fromValue(final String value) {
        if (value == null) {
            return UNKNOWN;
        }
        for (final Component component : values()) {
            if (component.value.equalsIgnoreCase(value)) {
                return component;
            }
        }
        return UNKNOWN;
    }
}