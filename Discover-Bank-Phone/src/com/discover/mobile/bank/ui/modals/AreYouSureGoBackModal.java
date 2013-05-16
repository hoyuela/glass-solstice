package com.discover.mobile.bank.ui.modals;

import android.app.Activity;
import android.view.View;
import android.view.View.OnClickListener;

import com.discover.mobile.bank.R;
import com.discover.mobile.bank.navigation.BankNavigationRootActivity;
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
	private Class<?> popTillFragment = null;
	private boolean overridePop = false;
	
	private int titleText = R.string.are_you_sure_title;
	private int bodyText = R.string.are_you_sure_cancel_body;
	private int buttonText = R.string.continue_text;
	
	/**
	 * Create a modal that will pop the back stack once upon dismissal.
	 * 
	 * @param baseFragment
	 */
	public AreYouSureGoBackModal(final BaseFragment baseFragment) {
		this.baseFragment = baseFragment;
	}
	
	/**
	 * Create a modal that will pop the back stack until the specified fragmetn is reached.
	 * @param baseFragment 
	 * @param popTillFragment a class that represents a fragment in the backstack.
	 */
	public AreYouSureGoBackModal(final BaseFragment baseFragment, final Class<?> popTillFragment) {
		this.baseFragment = baseFragment;
		this.popTillFragment = popTillFragment;
	}

	/**
	 * Create a modal that will perform the given OnClickListener action then pop the backstack once.
	 * 
	 * @param baseFragment
	 * @param onButtonClick an OnClickListener that will provide additional functionality to be performed on
	 * click of the button in the modal.
	 */
	public AreYouSureGoBackModal(final BaseFragment baseFragment, final OnClickListener onButtonClick) {
		this.baseFragment = baseFragment;
		userClickAction = onButtonClick;
	}
	
	/**
	 * Create a modal that will perform the given OnClickListener action then pop the backstack until the specified
	 * fragment class is reached.
	 * 
	 * @param baseFragment
	 * @param popTillFragment a class that represents a fragment in the backstack.
	 * @param onButtonClick an OnClickListener that will provide additional functionality to be performed on
	 * click of the button in the modal.
	 */
	public AreYouSureGoBackModal(final BaseFragment baseFragment, final Class<?> popTillFragment, 
																final OnClickListener onButtonClick) {
		this.baseFragment = baseFragment;
		this.userClickAction = onButtonClick;
		this.popTillFragment = popTillFragment;
	}
	
	/**
	 * Will set the modal to not perform a backstack pop. This value is ignored if a popTillFragment was passed
	 * into the constructor.
	 * 
	 * @param overridePop
	 */
	public final void setOverridePop(final boolean overridePop) {
		this.overridePop = overridePop;
	}
	
	/**
	 * Set the body text to the given String resource.
	 */
	@Override
	public final void setModalBodyText(final int modalTextResource) {
		this.bodyText = modalTextResource;
	}
	
	/**
	 * Set the text of the button in the dialog to the specified String resource.
	 */
	@Override
	public final void setButtonText(final int buttonTextResource) {
		this.buttonText = buttonTextResource;
	}
	
	/**
	 * Set the text of the title in the Fragment to that of the String resource provided.
	 */
	@Override
	public final void setTitleTextResource(final int titleTextResource) {
		this.titleText = titleTextResource;
	}
	
	/**
	 * Display the modal dialog on screen.
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
			
			bottom.setButtonText(buttonText);
			
			top.hideNeedHelpFooter();
			String title = baseFragment.getResources().getString(titleText);
			if(!title.endsWith("?")) {
				title += "?";
			}
			
			top.setTitle(title);
			top.setContent(bodyText);
			
			final ModalAlertWithOneButton cancelModal = new ModalAlertWithOneButton(currentActivity, top, bottom);
			
			bottom.getButton().setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(final View v) {
					if(userClickAction != null) {
						userClickAction.onClick(v);
					}
					
					cancelModal.dismiss();
					performBackstackPopping();
				}
			});
			baseFragment.showCustomAlertDialog(cancelModal);
		}
	}
	
	/**
	 * Perform the backstack popping action based on given parameters to this modal.
	 */
	private void performBackstackPopping() {
		final Activity activity = baseFragment.getActivity();

		if(activity != null && activity instanceof BankNavigationRootActivity) {
			final BankNavigationRootActivity navActivity = (BankNavigationRootActivity)activity;
			
			if(popTillFragment != null) {
				navActivity.popTillFragment(popTillFragment);
			} else if (!overridePop) {
				navActivity.getSupportFragmentManager().popBackStackImmediate();
			}
		}
	}
}
