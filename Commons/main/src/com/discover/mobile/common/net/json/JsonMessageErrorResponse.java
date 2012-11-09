package com.discover.mobile.common.net.json;

import java.io.Serializable;
import java.util.List;

import com.discover.mobile.common.net.error.ErrorResponse;
import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.base.Objects;

@JsonAutoDetect(fieldVisibility=Visibility.NONE, setterVisibility=Visibility.NONE,
		isGetterVisibility=Visibility.NONE, getterVisibility=Visibility.NONE)
public class JsonMessageErrorResponse extends ErrorResponse {
	
	private static final long serialVersionUID = 5979837443360856714L;

	@JsonProperty("status")
	private int messageStatusCode;
	
	@JsonProperty
	private String message;
	
	@JsonProperty
	private List<Data> data;
	
	public int getMessageStatusCode() {
		return messageStatusCode;
	}
	
	public String getMessage() {
		return message;
	}
	
	@Override
	public String toString() {
		return Objects.toStringHelper(this)
				.add("httpStatusCode", getHttpStatusCode())
				.add("messageStatusCode", messageStatusCode)
				.add("message", message).toString();
	}
	
	public static class Data implements Serializable {
		private static final long serialVersionUID = 6370942047530497928L;
		
		public String saStatus;
	}
}
