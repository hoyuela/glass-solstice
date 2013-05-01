package com.discover.mobile.common.net.json;

import java.io.IOException;
import java.io.OutputStream;

import com.discover.mobile.common.net.RequestBodySerializer;

public class JsonMappingRequestBodySerializer implements RequestBodySerializer {
	
	@Override
	public boolean canSerialize(final Object body) {
		return body != null;
	}
	
	@Override
	public void serializeBody(final Object body, final OutputStream outputStream) throws IOException {
		if(body == null){
			throw new AssertionError("cannot handle null bodies");
		}
		
		JacksonObjectMapperHolder.mapper.writeValue(outputStream, body);
	}
	
	@Override
	public String getContentType() {
		// TODO make named constant in more common location
		return "application/json";
	}
	
}
