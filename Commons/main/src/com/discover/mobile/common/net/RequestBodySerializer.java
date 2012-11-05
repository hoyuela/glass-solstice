package com.discover.mobile.common.net;

import java.io.IOException;
import java.io.OutputStream;

public interface RequestBodySerializer {
	
	boolean canSerialize(Object body);
	void serializeBody(Object body, OutputStream outputStream) throws IOException;
	String getContentType();
	
}
