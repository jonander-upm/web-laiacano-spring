package com.laiacano.core.data.exceptions;

public class UnauthorizedException extends RuntimeException {
    private static final String DESCRIPTION = "Unauthorized User";

    public UnauthorizedException(String detail) {
        super(DESCRIPTION + ". " + detail);
    }
}
