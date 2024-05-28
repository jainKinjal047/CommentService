package com.example.commentservice.exception;

public class ReactionNotFoundException extends RuntimeException{
    public ReactionNotFoundException(String message) {
        super(message);
    }
}
