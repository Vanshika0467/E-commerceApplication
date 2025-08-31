package com.demo.apierror;

import java.time.LocalDateTime;

public class ApiError {
    private int status;
    private String error;
    private String code;
    private String message;
    private String details;
    private LocalDateTime timestamp;
	public ApiError() {
	}
	public ApiError(int status, String error, String code, String message, String details, LocalDateTime timestamp) {
		super();
		this.status = status;
		this.error = error;
		this.code = code;
		this.message = message;
		this.details = details;
		this.timestamp = timestamp;
	}
	public int getStatus() {
		return status;
	}
	public void setStatus(int status) {
		this.status = status;
	}
	public String getError() {
		return error;
	}
	public void setError(String error) {
		this.error = error;
	}
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	public String getDetails() {
		return details;
	}
	public void setDetails(String details) {
		this.details = details;
	}
	public LocalDateTime getTimestamp() {
		return timestamp;
	}
	public void setTimestamp(LocalDateTime timestamp) {
		this.timestamp = timestamp;
	}

    
}