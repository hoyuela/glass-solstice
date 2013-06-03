/*
 * � Copyright Solstice Mobile 2013
 */
package com.discover.mobile.bank.atm;

import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.View;
import android.view.View.OnClickListener;

import com.discover.mobile.bank.R;
import com.discover.mobile.bank.ui.modals.AtmLocatorHelpModalTop;
import com.discover.mobile.common.DiscoverApplication;
import com.discover.mobile.common.ui.modals.SimpleContentModal;
import com.discover.mobile.common.ui.modals.SimpleNoButtonModal;
import com.discover.mobile.common.ui.modals.SimpleTwoButtonModal;

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
	public static BankModalAlertWithTwoButtons getSettingsModal(final Context context, final LocationFragment fragment){
		final ModalDefaultTopView top  = new ModalDefaultTopView(context, null);
		final ModalBottomTwoButtonView bottom = new ModalDefaultTwoButtonBottomView(context, null);
		final BankModalAlertWithTwoButtons modal = new BankModalAlertWithTwoButtons(context, top, bottom);
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
	private static BankModalAlertWithTwoButtons getLocationModal(final Context context, 
			final LocationFragment fragment, final int titleId, final int contentId) {
		final ModalDefaultTopView top = new ModalDefaultTopView(context, null);
		final ModalTwoButtonWhiteBottom bottom = new ModalTwoButtonWhiteBottom(context, null);
		final BankModalAlertWithTwoButtons modal = new BankModalAlertWithTwoButtons(context, top, bottom);
		top.setTitle(titleId);
		top.setContent(contentId);
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

	/**
	 * Get the modal that will ask the user if they would like to allow
	 * the app to use their current location
	 * @param context - activity context
	 * @param fragment - fragment using the modal
	 * @return the modal that will ask the user if they would like to allow
	 * the app to use their current location
	 */
	public static BankModalAlertWithTwoButtons getLocationAcceptanceModal(final Context context, 
			final LocationFragment fragment){
		final AtmGetLocationModal top = new AtmGetLocationModal(context, null);
		final BankModalAlertWithTwoButtons modal = new BankModalAlertWithTwoButtons(context, top, null);
		final String content = context.getString(R.string.atm_location_modal_content);
		top.getContentView().setText(Html.fromHtml(content));
		top.getContentView().setMovementMethod(LinkMovementMethod.getInstance());
		top.getAllow().setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(final View v){
				if(fragment instanceof AtmMapFragment) {
					((AtmMapFragment)fragment).searchCurrentLocation();
				}
				saveUserChoice();
				modal.dismiss();
			}
		});
		top.getDontAllow().setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(final View v){
				modal.dismiss();
				fragment.setLocationStatus(LocationFragment.NOT_USING_LOCATION);
			}
		});
		return modal;
	}

	private static void saveUserChoice() {
		DiscoverApplication.getLocationPreference().setUserAcceptedModal();
	}

	/**
	 * Get the modal that will alert the user to failing getting the users current location and ask them to retry.
	 * @param context - activity context
	 * @param fragment - fragment using the modal
	 * @return modal that will alert the user to failing to get the users current location and ask them to retry.
	 */
	public static BankModalAlertWithTwoButtons getCurrentLocationFailModal(final Context context, 
			final LocationFragment fragment){
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
		final ModalAlertWithOneButton modal = 
				getSimpleResultsModal(context, R.string.atm_no_results_title, R.string.atm_no_results_content);
		modal.showErrorIcon(true);
		modal.getHelpFooter().setToDialNumberOnClick(R.string.atm_no_results_number);
		return modal;
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
	public static ModalAlertWithOneButton getAtmLocatorHelpModal(final AtmMapFragment fragment){
		final AtmLocatorHelpModalTop top  = new AtmLocatorHelpModalTop(fragment.getActivity(), null);
		final ModalAlertWithOneButton modal = new ModalAlertWithOneButton(fragment.getActivity(), top, null);
		modal.setOnDismissListener(new OnDismissListener(){
			@Override
			public void onDismiss(final DialogInterface dialog) {
				fragment.setHelpModalShowing(false);

			}
		});

		return modal;
	}
}