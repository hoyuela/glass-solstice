package com.discover.mobile.common.net;

import java.io.IOException;
import java.io.OutputStream;

public class StringRequestBodySerializer implements RequestBodySerializer {
	
	@Override
	public boolean canSerialize(final Object body) {
		return body == null || body instanceof String;
	}
	
	@Override
	public void serializeBody(final Object body, final OutputStream outputStream) throws IOException {
		if(body == null){
			return;
		}
		
		if(!(body instanceof String)){
			throw new AssertionError("body was not a string; it was: " + body.getClass());
		}
		
		// TODO consider specifying a charset
		final byte[] bodyBytes = ((String)body).getBytes();
		outputStream.write(bodyBytes);
	}
	
	@Override
	public String getContentType() {
		return null;
	}
	
}
