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
	
	/**
	 * To create a AreYouSureGoBackModal provide a reference to a BaseFragment and an optional click listener
	 * for the button in the modal. The button in the modal will perform a fragment stack pop along with this 
	 * provided click listeners action.
	 * 
	 * @param baseFragment the base fragment that needs to launch this modal.
	 */
	public AreYouSureGoBackModal(final BaseFragment baseFragment) {
		this.baseFragment = baseFragment;
	}
	
	public AreYouSureGoBackModal(final BaseFragment baseFragment, final Class<?> popTillFragment) {
		this.baseFragment = baseFragment;
		this.popTillFragment = popTillFragment;
	}
	
	/**
	 * To create a AreYouSureGoBackModal provide a reference to a BaseFragment and an optional click listener
	 * for the button in the modal. The button in the modal will perform a fragment stack pop along with this 
	 * provided click listeners action.
	 * 
	 * @param baseFragment the base fragment that needs to launch this modal.
	 * @param onButtonClick an additional onClick action to be performed when the button in the modal is pressed.
	 */
	public AreYouSureGoBackModal(final BaseFragment baseFragment, final Class<?> popTillFragment, final OnClickListener onButtonClick) {
		this.baseFragment = baseFragment;
		this.userClickAction = onButtonClick;
		this.popTillFragment = popTillFragment;
	}
	
	public boolean getOverridePop() {
		return overridePop;
	}
	
	public void setOverridePop(final boolean overridePop) {
		this.overridePop = overridePop;
	}
	
	/**
	 * To create a AreYouSureGoBackModal provide a reference to a BaseFragment and an optional click listener
	 * for the button in the modal. The button in the modal will perform a fragment stack pop along with this 
	 * provided click listeners action.
	 * 
	 * @param baseFragment the base fragment that needs to launch this modal.
	 * @param onButtonClick an additional onClick action to be performed when the button in the modal is pressed.
	 */
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
