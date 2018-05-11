package com.fugu.support.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class SupportTicketResponse {
	@SerializedName("statusCode")
	@Expose
	private Integer statusCode;
	@SerializedName("message")
	@Expose
	private String message;

	public Integer getStatusCode() {
		return statusCode;
	}

	public void setStatusCode(Integer statusCode) {
		this.statusCode = statusCode;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}
}
