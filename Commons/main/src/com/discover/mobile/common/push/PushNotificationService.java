package com.discover.mobile.common.push;

import roboguice.inject.ContextSingleton;
import roboguice.inject.InjectResource;
import android.content.Context;

import com.google.inject.Inject;
import com.xtify.sdk.api.XtifySDK;

@ContextSingleton
public class PushNotificationService {
	
	@InjectResource(name="com.discover.mobile:string/push_key")
	private String pushKey;
	
	@InjectResource(name="com.discover.mobile:string/push_id")
	private String projectId;
	
	@Inject
	private Context currentContext;
	
	@Inject
	private NotificationSingleton notificationSingleton;

	public void start() {
		XtifySDK.start(currentContext, pushKey, projectId);
	}
}
