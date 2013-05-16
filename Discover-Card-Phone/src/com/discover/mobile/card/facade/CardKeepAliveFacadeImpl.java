/**
 * 
 */
package com.discover.mobile.card.facade;

import android.content.Context;

import com.discover.mobile.common.facade.CardKeepAliveFacade;

import com.discover.mobile.card.common.sessiontimer.PageTimeOutUtil;

/**
 * @author 328073
 * 
 */
public class CardKeepAliveFacadeImpl implements CardKeepAliveFacade {
    PageTimeOutUtil pagetimeout;

    @Override
    public void refreshCardSession(Context context) {
        pagetimeout = new PageTimeOutUtil(context);
        pagetimeout.keepSessionAlive();
    }
}
