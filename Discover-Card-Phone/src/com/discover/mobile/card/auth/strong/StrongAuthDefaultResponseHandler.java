package com.discover.mobile.card.auth.strong;

import android.content.Context;

import com.discover.mobile.card.R;
import com.discover.mobile.card.common.ui.modals.EnhancedContentModal;
import com.discover.mobile.card.common.utils.Utils;
import com.discover.mobile.common.DiscoverActivityManager;
import com.discover.mobile.common.nav.NavigationRootActivity;

public abstract class StrongAuthDefaultResponseHandler implements
		StrongAuthListener {

	@Override
    public abstract void onStrongAuthSucess(final Object data);

    @Override
    public void onStrongAuthSkipped(final Object data) {
    	
    	final Context context = DiscoverActivityManager.getActiveActivity();
		final EnhancedContentModal modal = new EnhancedContentModal(context, 
				R.string.E_SA_SKIPPED_TITLE, 
				R.string.E_SA_SKIPPED_CONTENT, 
				R.string.close_text,
				new Runnable(){
					@Override
					public void run() {
						DiscoverActivityManager.getActiveActivity().onBackPressed();
					}}
				);
		modal.setGrayButton();
		modal.hideNeedHelpFooter();
		((NavigationRootActivity)context).showCustomAlert(modal);
    }
    
    @Override
	public void onStrongAuthError(Object data) {
    	onStrongAuthSkipped(data);
	}

	@Override
	public void onStrongAuthNotEnrolled(Object data) {
    	onStrongAuthSkipped(data);
	}

    @Override
    public void onStrongAuthCardLock(final Object data) {
    	final Context context = DiscoverActivityManager.getActiveActivity();
		final EnhancedContentModal modal = new EnhancedContentModal(context, 
				R.string.E_T_4031401_LOCKOUT, 
				R.string.E_1402_LOCKOUT, 
				R.string.close_text,
				new Runnable(){
					@Override
					public void run() {
						Utils.logoutUser(DiscoverActivityManager.getActiveActivity(), false);
					}}
				);
		modal.setGrayButton();
		modal.hideNeedHelpFooter();
		((NavigationRootActivity)context).showCustomAlert(modal);
    }

}
