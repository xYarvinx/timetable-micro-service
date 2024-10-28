package com.example.timetablemicroservice.exception;

import org.springframework.http.HttpStatus;

public class InvalidDataException  extends ApiException{
    public InvalidDataException(String message) {
        super(message, HttpStatus.BAD_REQUEST);
    }
}
