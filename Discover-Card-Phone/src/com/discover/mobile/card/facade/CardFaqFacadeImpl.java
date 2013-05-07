/*
 * © Copyright Solstice Mobile 2013
 */
package com.discover.mobile.card.facade;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.discover.mobile.card.CardMenuItemLocationIndex;
import com.discover.mobile.card.help.FAQDetailFragment;
import com.discover.mobile.card.help.FAQExtraKeys;
import com.discover.mobile.card.help.FAQLandingPageFragment;
import com.discover.mobile.card.help.LoggedOutFAQActivity;
import com.discover.mobile.common.BaseFragment;
import com.discover.mobile.common.BaseFragmentActivity;
import com.discover.mobile.common.DiscoverActivityManager;
import com.discover.mobile.common.facade.CardFaqFacade;
import com.discover.mobile.common.nav.NavigationRootActivity;

public class CardFaqFacadeImpl implements CardFaqFacade{

	@Override
	public void launchCardFaq() {
		final Activity currentActivity = DiscoverActivityManager.getActiveActivity();
		if(currentActivity instanceof NavigationRootActivity) {
			final NavigationRootActivity activity = (NavigationRootActivity)DiscoverActivityManager.getActiveActivity();
			final BaseFragment current = activity.getCurrentContentFragment();

			/**Check if user is already viewing FAQ*/
			if(current.getGroupMenuLocation() != CardMenuItemLocationIndex.CUSTOMER_SERVICE_GROUP && 
					current.getSectionMenuLocation() != CardMenuItemLocationIndex.FAQ_SECTION){
				activity.makeFragmentVisible(new FAQLandingPageFragment());
			} else if(!(current instanceof FAQDetailFragment) && !(current instanceof FAQLandingPageFragment)) {
				activity.makeFragmentVisible(new FAQLandingPageFragment());
			} else {
				activity.hideSlidingMenuIfVisible();
			}
		} else{
			final Intent loggedOutFAQ = new Intent(currentActivity, LoggedOutFAQActivity.class);
			currentActivity.startActivity(loggedOutFAQ);
		}
	}

	@Override
	public void navigateToCardFaqDetail(final String faqType) {
		final Bundle extras = new Bundle();
		extras.putString(FAQExtraKeys.FAQ_TYPE, faqType);
		final FAQDetailFragment faqDetail = new FAQDetailFragment();
		faqDetail.setArguments(extras);
		((BaseFragmentActivity)DiscoverActivityManager.getActiveActivity()).makeFragmentVisible(faqDetail);
	}
}
