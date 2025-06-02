package com.gh.exception;

public class IDNotFoundException extends Exception {
	public IDNotFoundException() {
		this("This is BookNotFoundException");
	}
	
	public IDNotFoundException(String message) {
		super(message);
	}
}
