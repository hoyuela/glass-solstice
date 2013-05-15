package com.discover.mobile.bank.ui.modals;

import android.app.Activity;
import android.view.View;
import android.view.View.OnClickListener;

import com.discover.mobile.bank.R;
import com.discover.mobile.common.BaseFragment;
import com.discover.mobile.common.ui.modals.ModalAlertWithOneButton;
import com.discover.mobile.common.ui.modals.ModalDefaultOneButtonBottomView;
import com.discover.mobile.common.ui.modals.ModalDefaultTopView;
/**
 * This modal is used when a user is pressing the back button during a workflow that has a lot of user inputted data.
 * It accepts an OnClickListener in the constructor that will be called when the modal's continue button is pressed.
 * 
 * @author scottseward
 *
 */
public class AreYouSureGoBackModal implements BaseFragmentModal {
	private BaseFragment baseFragment = null;
	private OnClickListener userClickAction = null;
	
	public AreYouSureGoBackModal(final BaseFragment baseFragment, final OnClickListener onButtonClick) {
		this.baseFragment = baseFragment;
		userClickAction = onButtonClick;
	}
	
	@Override
	public final void showModal() {
		Activity currentActivity = null;
		
		if(baseFragment != null) {
			currentActivity = baseFragment.getActivity();
		}
		
		if(currentActivity != null) {
			final ModalDefaultTopView top = new ModalDefaultTopView(baseFragment.getActivity(), null);
			final ModalDefaultOneButtonBottomView bottom = 
									new ModalDefaultOneButtonBottomView(currentActivity, null);
			
			bottom.setButtonText(R.string.continue_text);
			
			top.hideNeedHelpFooter();
			top.setTitle(baseFragment.getResources().getString(R.string.are_you_sure_title));
			top.setContent(R.string.are_you_sure_cancel_body);
			
			final ModalAlertWithOneButton cancelModal = new ModalAlertWithOneButton(currentActivity, top, bottom);
			
			bottom.getButton().setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(final View v) {
					cancelModal.dismiss();
					if(userClickAction != null) {
						userClickAction.onClick(v);
					}
				}
			});

			baseFragment.showCustomAlertDialog(cancelModal);
		}
	}
}
