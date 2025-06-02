package com.gh.exception;

import java.sql.SQLException;

public class DMLException extends SQLException {
	public DMLException() {
		this("This is DMLException");
	}
	
	public DMLException(String message) {
		super(message);
	}
}
