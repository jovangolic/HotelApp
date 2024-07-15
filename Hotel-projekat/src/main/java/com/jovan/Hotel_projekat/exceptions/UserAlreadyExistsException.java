package com.jovan.Hotel_projekat.exceptions;



public class UserAlreadyExistsException extends RuntimeException {
	public UserAlreadyExistsException(String message) {
        super(message);
    }
}
