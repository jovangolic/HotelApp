package com.jovan.Hotel_projekat.exceptions;




public class RoleAlreadyExistException extends RuntimeException {
	public RoleAlreadyExistException(String message) {
        super(message);
    }
}
