package com.discover.mobile.common;

import java.lang.ref.Reference;

import javax.annotation.Nullable;

public final class ReferenceUtility {
	
	@SuppressWarnings("hiding")
	public static @Nullable <R> R safeGetReferenced(final @Nullable Reference<R> ref) {
		if(ref == null)
			return null;
		
		return ref.get();
	}
	
	private ReferenceUtility() {
		throw new UnsupportedOperationException("This class is non-instantiable"); //$NON-NLS-1$
	}
	
}
