/*
 * � Copyright Solstice Mobile 2013
 */
package com.discover.mobile.bank.atm;

import android.content.Context;
import android.view.View;
import android.view.View.OnClickListener;

import com.discover.mobile.bank.R;
import com.discover.mobile.bank.ui.modals.AtmLocatorHelpModalTop;
import com.discover.mobile.bank.ui.modals.ModalTwoButtonWhiteBottom;
import com.discover.mobile.common.ui.modals.ModalAlertWithOneButton;
import com.discover.mobile.common.ui.modals.ModalAlertWithTwoButtons;
import com.discover.mobile.common.ui.modals.ModalBottomTwoButtonView;
import com.discover.mobile.common.ui.modals.ModalDefaultOneButtonBottomView;
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
	 * Get the modal that includes the location allow / decline buttons
	 * (and their respective listeners).
	 * @param context - activity context
	 * @param fragment - fragment using the modal
	 * @param titleId - R String ID for the title of the modal
	 * @param contentId - R String ID for the modal message
	 * @return the modal that includes the location allow / decline buttons.
	 */
	private static ModalAlertWithTwoButtons getLocationModal(final Context context, 
			final LocationFragment fragment, final int titleId, final int contentId) {
		final ModalDefaultTopView top = new ModalDefaultTopView(context, null);
		final ModalTwoButtonWhiteBottom bottom = new ModalTwoButtonWhiteBottom(context, null);
		final ModalAlertWithTwoButtons modal = new ModalAlertWithTwoButtons(context, top, bottom);
		top.setTitle(titleId);
		top.setContent(contentId);
		top.showErrorIcon(false);
		top.hideNeedHelpFooter();
		bottom.setOkButtonText(R.string.atm_location_modal_allow);
		bottom.setCancelButtonText(R.string.atm_location_modal_decline);
		bottom.getCancelButton().setBackgroundDrawable(context.getResources().getDrawable(R.drawable.white_button));
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

	/**
	 * Get the modal that will ask the user if they would like to allow
	 * the app to use their current location
	 * @param context - activity context
	 * @param fragment - fragment using the modal
	 * @return the modal that will ask the user if they would like to allow
	 * the app to use their current location
	 */
	public static ModalAlertWithTwoButtons getLocationAcceptanceModal(final Context context, final LocationFragment fragment){
		return getLocationModal(context, fragment, 
				R.string.atm_location_modal_title, R.string.atm_location_modal_content);
	}

	/**
	 * Get the modal that will alert the user to failing getting the users current location and ask them to retry.
	 * @param context - activity context
	 * @param fragment - fragment using the modal
	 * @return the modal that will alert the user to failing getting the users current location and ask them to retry.
	 */
	public static ModalAlertWithTwoButtons getCurrentLocationFailModal(final Context context, final LocationFragment fragment){
		return getLocationModal(context, fragment, 
				R.string.atm_location_timeout_title, R.string.atm_location_timeout_text);
	}

	
	/**
	 * Get the modal that includes one "OK" button that simply dismisses the modal.
	 * @param context - activity context
	 * @param titleId - R String ID for the title of the modal
	 * @param contentId - R String ID for the modal message
	 * @return a one button modal that displays a message and simply dismisses.
	 */
	private static ModalAlertWithOneButton getSimpleResultsModal(final Context context, final int titleId, 
			final int contentId) {
		final ModalDefaultTopView top  = new ModalDefaultTopView(context, null);
		final ModalDefaultOneButtonBottomView bottom = new ModalDefaultOneButtonBottomView(context, null);
		final ModalAlertWithOneButton modal = new ModalAlertWithOneButton(context, top, bottom);
		top.setTitle(titleId);
		top.setContent(contentId);
		top.showErrorIcon(false);
		top.hideNeedHelpFooter();
		bottom.getButton().setText(R.string.atm_no_results_button);
		bottom.getButton().setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(final View v){
				modal.dismiss();
			}
		});

		return modal;
	}
	

	/**
	 * Get the modal that informs the users that there were no results
	 * @param context - activity context
	 * @param fragment - fragment using the modal
	 * @return the modal that will ask the user if they would like to allow
	 * the app to use their current location
	 */
	public static ModalAlertWithOneButton getNoResultsModal(final Context context){
		return getSimpleResultsModal(context, R.string.atm_no_results_title, R.string.atm_no_results_content);
	}

	/**
	 * Get the modal that will inform the user that they have entered an invalid address
	 * @param context - activity context
	 * @param fragment - fragment using the modal
	 * @return the modal that will ask the user if they would like to allow
	 * the app to use their current location
	 */
	public static ModalAlertWithOneButton getInvalidAddressModal(final Context context){
		return getSimpleResultsModal(context, R.string.atm_no_results_title, R.string.atm_no_results_content);
	}

	/**
	 * Get the modal that will inform the user that they have entered an invalid address
	 * @param context - activity context
	 * @param fragment - fragment using the modal
	 * @return the modal that will ask the user if they would like to allow
	 * the app to use their current location
	 */
	public static ModalAlertWithOneButton getAtmLocatorHelpModal(final Context context){
		final AtmLocatorHelpModalTop top  = new AtmLocatorHelpModalTop(context, null);
		final ModalAlertWithOneButton modal = new ModalAlertWithOneButton(context, top, null);

		return modal;
	}
}