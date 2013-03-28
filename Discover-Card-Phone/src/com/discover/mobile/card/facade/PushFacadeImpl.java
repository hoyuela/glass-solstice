/**
 * Copyright Solstice-Mobile 2013
 */
package com.discover.mobile.card.facade;

import com.discover.mobile.card.R;
import com.discover.mobile.card.navigation.CardNavigationRootActivity;
import com.discover.mobile.card.push.register.PushRegistrationStatusErrorHandler;
import com.discover.mobile.card.push.register.PushRegistrationStatusSuccessListener;
import com.discover.mobile.card.services.push.PushNotificationService;
import com.discover.mobile.card.services.push.registration.GetPushRegistrationStatus;
import com.discover.mobile.card.services.push.registration.PushRegistrationStatusDetail;
import com.discover.mobile.common.BaseActivity;
import com.discover.mobile.common.callback.AsyncCallback;
import com.discover.mobile.common.callback.GenericAsyncCallback;
import com.discover.mobile.common.error.BaseExceptionFailureHandler;
import com.discover.mobile.common.facade.PushFacade;
/**
 * The impl class for the push facade 
 * @author ekaram
 *
 */
public class PushFacadeImpl implements PushFacade{

	@Override
	public void getXtifyRegistrationStatus(final BaseActivity callingActivity) {
		final AsyncCallback<PushRegistrationStatusDetail> callback = 
				GenericAsyncCallback.<PushRegistrationStatusDetail>builder(callingActivity)
				.showProgressDialog(callingActivity.getResources().getString(R.string.push_progress_get_title), 
						callingActivity.getResources().getString(R.string.push_progress_registration_loading), 
									true)
				.withSuccessListener(new PushRegistrationStatusSuccessListener())
				.withErrorResponseHandler(new PushRegistrationStatusErrorHandler(callingActivity))
				.withExceptionFailureHandler(new BaseExceptionFailureHandler())
				.launchIntentOnSuccess(CardNavigationRootActivity.class)
				.finishCurrentActivityOnSuccess(callingActivity)
				//FIXME need to clear these text fields.  determine best way with facade
			//	.clearTextViewsOnComplete(idField, passField)
				.build();
	
		new GetPushRegistrationStatus(callingActivity, callback).submit();
	}

	@Override
	public void startXtifySDK(final BaseActivity callingActivity) {
		new PushNotificationService ().start(callingActivity) ;
		
	}

}
