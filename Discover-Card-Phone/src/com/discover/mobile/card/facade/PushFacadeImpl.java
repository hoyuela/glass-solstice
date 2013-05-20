/**
 * Copyright Solstice-Mobile 2013
 */
package com.discover.mobile.card.facade;

import android.app.Activity;

import com.discover.mobile.common.BaseActivity;
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
    public void getXtifyRegistrationStatus(final BaseActivity callingActivity) {

    }

    @Override
    public void startXtifySDK(final Activity callingActivity) {
        new PushNotificationService().start(callingActivity);

    }

}
