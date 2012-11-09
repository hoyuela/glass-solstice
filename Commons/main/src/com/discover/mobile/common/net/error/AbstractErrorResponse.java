package com.discover.mobile.common.net.error;

import com.google.common.base.Objects;

public abstract class AbstractErrorResponse<E extends AbstractErrorResponse<E>> implements ErrorResponse<E> {
	
	private static final long serialVersionUID = 8305572864293562105L;
	
	private int httpStatusCode;
	
	@Override
	public final int getHttpStatusCode() {
		return httpStatusCode;
	}
	
	void setHttpStatusCode(final int httpStatusCode) {
		this.httpStatusCode = httpStatusCode;
	}
	
	public final boolean isClientError() {
		return httpStatusCode >= 400 && httpStatusCode < 500;
	}
	
	public final boolean isServerError() {
		return httpStatusCode >= 500 && httpStatusCode < 600;
	}
	
	@Override
	public String toString() {
		return Objects.toStringHelper(this)
				.add("httpStatusCode", httpStatusCode).toString();
	}
	
}
