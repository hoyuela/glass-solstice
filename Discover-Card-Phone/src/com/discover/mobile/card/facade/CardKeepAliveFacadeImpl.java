/**
 * 
 */
package com.discover.mobile.card.facade;

import android.content.Context;

import com.discover.mobile.card.common.sessiontimer.PageTimeOutUtil;
import com.discover.mobile.common.facade.CardKeepAliveFacade;

/**
 * @author 328073
 *
 */
public class CardKeepAliveFacadeImpl implements CardKeepAliveFacade{

	PageTimeOutUtil pagetimeout;
	@Override
	public void refreshCardSession(final Context context) {

		pagetimeout = new PageTimeOutUtil(context);
		pagetimeout.keepSessionAlive();
	}

}
