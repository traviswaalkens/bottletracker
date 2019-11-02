package com.tlw.bottletracker.exception;

public class UnknownMessageTypeException extends RuntimeException {

	private static final long serialVersionUID = 92857235L;

	public UnknownMessageTypeException(String message) {
		super(message);
	}
}
