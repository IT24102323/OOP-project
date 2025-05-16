package com.bookstore.model.review;

import java.util.Date;


public enum ReviewType {
    STANDARD("Standard User Review"),
    VERIFIED("Verified Purchase Review"),
    GUEST("Guest Review");

    private final String displayName;

    ReviewType(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
