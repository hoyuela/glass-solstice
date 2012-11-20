package com.discover.mobile.common;

import java.lang.ref.WeakReference;

import javax.annotation.Nullable;

public final class ReferenceUtility {
	
	@SuppressWarnings("hiding")
	public static @Nullable <R> R safeGetReferenced(final @Nullable WeakReference<R> ref) {
		if(ref == null)
			return null;
		
		return ref.get();
	}
	
	private ReferenceUtility() {
		throw new UnsupportedOperationException("This class is non-instantiable");
	}
	
}
