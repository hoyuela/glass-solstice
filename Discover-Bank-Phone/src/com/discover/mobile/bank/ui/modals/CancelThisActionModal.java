package com.discover.mobile.bank.ui.modals;

import android.app.Activity;
import android.view.View;
import android.view.View.OnClickListener;

import com.discover.mobile.bank.R;
import com.discover.mobile.bank.framework.BankConductor;
import com.discover.mobile.common.BaseFragment;
import com.discover.mobile.common.ui.modals.ModalAlertWithOneButton;
import com.discover.mobile.common.ui.modals.ModalDefaultOneButtonBottomView;
import com.discover.mobile.common.ui.modals.ModalDefaultTopView;
/**
 * This class creates and shows a modal dialog for when a user is canceling a workflow.
 * @author scottseward
 *
 */
public class CancelThisActionModal implements BaseFragmentModal {

	private BaseFragment baseFragment = null;
	private OnClickListener cancelAction = null;
	
	public CancelThisActionModal(final BaseFragment baseFragmentThatSupportsShowCustomAlertDialog) {
		baseFragment = baseFragmentThatSupportsShowCustomAlertDialog;
	}
	
	public final void setOnConfirmAction(final OnClickListener onClick) {
		cancelAction = onClick;
	}
	
	/**
	 * Display the cancel this action modal.
	 */
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
			
			bottom.setButtonText(R.string.cancel_this_action);
		
			top.hideNeedHelpFooter();
			top.setTitle(baseFragment.getResources().getString(R.string.cancel_this_action) + "?");
			top.setContent(R.string.cancel_this_action_content);
			
			final ModalAlertWithOneButton cancelModal = new ModalAlertWithOneButton(currentActivity, top, bottom);
			
			bottom.getButton().setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(final View v) {
					if(cancelAction != null) {
						cancelAction.onClick(v);
					}
					
					cancelModal.dismiss();
					BankConductor.navigateToHomePage(true);			
				}
			});

			baseFragment.showCustomAlertDialog(cancelModal);
		}
	}
}
