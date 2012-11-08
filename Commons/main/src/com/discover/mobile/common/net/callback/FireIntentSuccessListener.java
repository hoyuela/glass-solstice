package com.discover.mobile.common.net.callback;

import static com.discover.mobile.common.ReferenceUtility.safeGetReferenced;

import java.lang.ref.WeakReference;

import javax.annotation.Nonnull;

import android.app.Activity;
import android.content.Intent;

import com.discover.mobile.common.net.callback.GenericCallbackListener.SuccessListener;

class FireIntentSuccessListener<V> implements SuccessListener<V> {
	
	private final WeakReference<Activity> activityRef;
	private final Class<?> intentTargetClass;
	
	FireIntentSuccessListener(final @Nonnull Activity activity, final @Nonnull Class<?> intentTargetClass) {
		activityRef = new WeakReference<Activity>(activity);
		this.intentTargetClass = intentTargetClass;
	}

	@Override
	public Order getOrder() {
		return Order.LAST;
	}

	@Override
	public void success(final V value) {
		final Activity activity = safeGetReferenced(activityRef);
		if(activity != null) {
			final Intent successIntent = new Intent(activity, intentTargetClass);
			activity.startActivity(successIntent);
		}
	}
	
}
