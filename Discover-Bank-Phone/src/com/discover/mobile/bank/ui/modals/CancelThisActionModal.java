package com.discover.mobile.bank.ui.modals;

import android.app.Activity;
import android.view.View;
import android.view.View.OnClickListener;

import com.discover.mobile.bank.R;
import com.discover.mobile.bank.framework.BankConductor;
import com.discover.mobile.common.BaseFragment;
import com.discover.mobile.common.DiscoverActivityManager;
import com.discover.mobile.common.ui.modals.ModalAlertWithOneButton;
import com.discover.mobile.common.ui.modals.ModalDefaultOneButtonBottomView;
import com.discover.mobile.common.ui.modals.ModalDefaultTopView;
import com.discover.mobile.common.ui.modals.SimpleContentModal;
/**
 * This class creates and shows a modal dialog for when a user is canceling a workflow.
 * @author scottseward
 *
 */
public class CancelThisActionModal implements BaseFragmentModal {

	private BaseFragment baseFragment = null;
	private OnClickListener cancelAction = null;
	
	private int bodyText = R.string.cancel_this_action_content;
	private int titleText = R.string.cancel_this_action;
	private int buttonText = R.string.cancel_this_action;
	
	/**
	 * Create a modal that will show on the current Fragment. Upon hitting the action button in the modal,
	 * the user will be taken back to the account summary page.
	 * 
	 * @param baseFragmentThatSupportsShowCustomAlertDialog
	 */
	public CancelThisActionModal(final BaseFragment baseFragmentThatSupportsShowCustomAlertDialog) {
		baseFragment = baseFragmentThatSupportsShowCustomAlertDialog;
	}
	
	/**
	 * Set the action to be performed in addition to navigating back to the account summary page upon
	 * the button in the modal being pressed.
	 * 
	 * @param onClick an OnClickListener that will perform some action in its onClick method.
	 */
	public final void setOnConfirmAction(final OnClickListener onClick) {
		cancelAction = onClick;
	}
	
	/**
	 * Display the cancel this action modal.
	 */
	@Override
	public final void showModal() {			
		final Activity currentActivity = DiscoverActivityManager.getActiveActivity();
		
		if(currentActivity != null) {
			final String questionMark = "?";
			final SimpleContentModal cancelModal = new SimpleContentModal(currentActivity);
			String title = currentActivity.getResources().getString(titleText);
			
			if(!title.endsWith(questionMark)) {
				title += questionMark;
			}
			cancelModal.hideNeedHelpFooter();
			cancelModal.setTitle(title);
			cancelModal.setContent(bodyText);
			cancelModal.setButtonText(buttonText);
			
			cancelModal.getButton().setOnClickListener(new OnClickListener() {
				
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

	/**
	 * Set the text that will be shown in the body of the modal from a String resource.
	 */
	@Override
	public void setModalBodyText(final int modalTextResource) {
		this.bodyText = modalTextResource;
	}
	
	/**
	 * Set the text of the button in the modal to that of a specified String resource. 
	 */
	@Override
	public void setButtonText(final int buttonTextResource) {
		this.buttonText = buttonTextResource;
	}

	/**
	 * Set the title of the modal to that of the specified String resource.
	 */
	@Override
	public void setTitleTextResource(final int titleTextResource) {
		this.titleText = titleTextResource;
	}
}
