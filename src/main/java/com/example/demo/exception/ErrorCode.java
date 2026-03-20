package com.example.demo.exception;

public enum ErrorCode {
    // Common errors
    BAD_REQUEST(400, "Bad Request"),
    UNAUTHORIZED(401, "Unauthorized"),
    FORBIDDEN(403, "Forbidden"),
    NOT_FOUND(404, "Not Found"),
    METHOD_NOT_ALLOWED(405, "Method Not Allowed"),
    INTERNAL_SERVER_ERROR(500, "Internal Server Error"),

    // Business-specific errors
    USER_NOT_FOUND(404, "UserDO not found"),
    USER_ALREADY_EXISTS(409, "UserDO with this username or email already exists"),
    VALIDATION_ERROR(400, "Validation Failed");

    private final int status;
    private final String message;

    ErrorCode(int status, String message) {
        this.status = status;
        this.message = message;
    }

    public int getStatus() {
        return status;
    }

    public String getMessage() {
        return message;
    }
}
