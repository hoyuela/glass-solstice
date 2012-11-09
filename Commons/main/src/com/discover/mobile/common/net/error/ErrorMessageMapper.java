package com.discover.mobile.common.net.error;

import android.content.Context;

public interface ErrorMessageMapper<E extends ErrorResponse<E>> {
	
	String mapErrorToMessage(final E errorResponse, final Context resourceContext);
	
}
