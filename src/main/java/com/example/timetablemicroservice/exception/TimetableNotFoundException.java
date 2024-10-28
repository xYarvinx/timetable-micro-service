package com.example.timetablemicroservice.exception;

import org.springframework.http.HttpStatus;

public class TimetableNotFoundException extends ApiException{
    public TimetableNotFoundException(String message) {
        super(message, HttpStatus.NOT_FOUND);
    }
}
