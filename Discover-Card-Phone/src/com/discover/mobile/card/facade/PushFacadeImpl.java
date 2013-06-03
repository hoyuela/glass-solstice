/**
 * Copyright Solstice-Mobile 2013
 */
package com.discover.mobile.card.facade;

import com.discover.mobile.common.BaseFragmentActivity;
import com.discover.mobile.common.facade.PushFacade;

import com.discover.mobile.card.services.push.PushNotificationService;

/**
 * The impl class for the push facade
 * 
 * @author ekaram
 * 
 */
public class PushFacadeImpl implements PushFacade {

    
	@Override
    public void getXtifyRegistrationStatus(final BaseFragmentActivity callingActivity) {

    }

    @Override
    public void startXtifySDK(final BaseFragmentActivity callingActivity) {
        new PushNotificationService().start(callingActivity);

    }

	

}
