/*
 * © Copyright Solstice Mobile 2013
 */
package com.discover.mobile.bank.atm;

import android.content.Context;
import android.view.View;
import android.view.View.OnClickListener;

import com.discover.mobile.bank.R;
import com.discover.mobile.common.ui.modals.ModalAlertWithTwoButtons;
import com.discover.mobile.common.ui.modals.ModalBottomTwoButtonView;
import com.discover.mobile.common.ui.modals.ModalDefaultTopView;
import com.discover.mobile.common.ui.modals.ModalDefaultTwoButtonBottomView;

/**
 * Modal factory for the ATM locator features. Will create modals to the 
 * specifications.
 * 
 * @author jthornton
 *
 */
public final class AtmModalFactory{

	/**
	 * Private constructor so that the class cannot be instantiated.
	 */
	private AtmModalFactory() {}

	/**
	 * Get the modal that will ask the user if they would like to enable
	 * their location services
	 * @param context - activity context
	 * @param fragment - fragment using the modal
	 * @return the modal that will ask the user if they would like to enable
	 * their location services
	 */
	public static ModalAlertWithTwoButtons getSettingsModal(final Context context, final LocationFragment fragment){
		final ModalDefaultTopView top  = new ModalDefaultTopView(context, null);
		final ModalBottomTwoButtonView bottom = new ModalDefaultTwoButtonBottomView(context, null);
		final ModalAlertWithTwoButtons modal = new ModalAlertWithTwoButtons(context, top, bottom);
		top.setTitle(R.string.atm_location_modal_service_title);
		top.setContent(R.string.atm_location_modal_service_content);
		top.showErrorIcon(false);
		top.hideNeedHelpFooter();
		bottom.setOkButtonText(R.string.atm_location_modal_settings);
		bottom.setCancelButtonText(R.string.atm_location_modal_cancel);
		bottom.getOkButton().setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(final View v){
				fragment.launchSettings();
				modal.dismiss();
			}
		});
		bottom.getCancelButton().setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(final View v){
				modal.dismiss();
				fragment.setLocationStatus(LocationFragment.NOT_USING_LOCATION);
				fragment.showNoLocation();
			}
		});
		return modal;
	}

	/**
	 * Get the modal that will ask the user if they would like to allow
	 * the app to use their current location
	 * @param context - activity context
	 * @param fragment - fragment using the modal
	 * @return the modal that will ask the user if they would like to allow
	 * the app to use their current location
	 */
	public static ModalAlertWithTwoButtons getLocationAcceptanceModal(final Context context, final LocationFragment fragment){
		final ModalDefaultTopView top = new ModalDefaultTopView(context, null);
		final ModalBottomTwoButtonView bottom = new ModalDefaultTwoButtonBottomView(context, null);
		final ModalAlertWithTwoButtons modal = new ModalAlertWithTwoButtons(context, top, bottom);
		top.setTitle(R.string.atm_location_modal_title);
		top.setContent(R.string.atm_location_modal_content);
		top.showErrorIcon(false);
		top.hideNeedHelpFooter();
		bottom.setOkButtonText(R.string.atm_location_modal_allow);
		bottom.setCancelButtonText(R.string.atm_location_modal_decline);
		bottom.getOkButton().setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(final View v){
				fragment.setLocationStatus(LocationFragment.SEARCHING);
				fragment.getLocation();
				modal.dismiss();
			}
		});
		bottom.getCancelButton().setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(final View v){
				modal.dismiss();
				fragment.setLocationStatus(LocationFragment.NOT_USING_LOCATION);
			}
		});
		return modal;
	}
}