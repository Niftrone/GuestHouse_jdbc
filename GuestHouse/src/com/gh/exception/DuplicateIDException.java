package com.gh.exception;

public class DuplicateIDException extends Exception {
	public DuplicateIDException() {
		this("This is DuplicateISBNException");
	}
	
	public DuplicateIDException(String message) {
		super(message);
	}
}
