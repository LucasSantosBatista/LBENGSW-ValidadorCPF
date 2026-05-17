package com.ifsp.edu.exception;

public class CpfInvalidException extends RuntimeException {
	private static final long serialVersionUID = 1L;

	public CpfInvalidException(String message) {
		super(message);
	}

	public CpfInvalidException(String message, Throwable cause) {
		super(message, cause);
	}
}
