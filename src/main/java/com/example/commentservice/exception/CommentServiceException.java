package com.example.commentservice.exception;

public class CommentServiceException extends RuntimeException {
    public CommentServiceException(String message, Exception e) {
        super(message);
    }
}
