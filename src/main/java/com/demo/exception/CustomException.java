package com.demo.exception;

public class CustomException extends RuntimeException {
	
	private String message;
	private String resourcename;
	private Long id;
	public CustomException() {
		
	}
	public CustomException(String message, String resourcename, long quantity) {
		super();
		this.message = message;
		this.resourcename = resourcename;
		this.id = quantity;
	}
	
	

}
