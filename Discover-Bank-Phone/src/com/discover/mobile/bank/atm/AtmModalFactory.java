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
	public static SimpleTwoButtonModal getSettingsModal(final Context context, final LocationFragment fragment){
		final SimpleTwoButtonModal modal = new SimpleTwoButtonModal(context);
		modal.setTitle(R.string.atm_location_modal_service_title);
		modal.setContent(R.string.atm_location_modal_service_content);
		modal.showErrorIcon(false);
		modal.hideNeedHelpFooter();
		modal.setOkButtonText(R.string.atm_location_modal_settings);
		modal.setCancelButtonText(R.string.atm_location_modal_cancel);
		modal.getOkButton().setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(final View v){
				fragment.launchSettings();
				modal.dismiss();
			}
		});
		modal.getCancelButton().setOnClickListener(new OnClickListener(){
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
	private static SimpleTwoButtonModal getLocationModal(final Context context, 
			final LocationFragment fragment, final int titleId, final int contentId) {
		final SimpleTwoButtonModal modal = new SimpleTwoButtonModal(context);
		modal.setTitle(titleId);
		modal.setContent(contentId);
		modal.showErrorIcon(false);
		modal.hideNeedHelpFooter();
		modal.setOkButtonText(R.string.atm_location_modal_allow);
		modal.setCancelButtonText(R.string.atm_location_modal_decline);
		modal.getOkButton().setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(final View v){
				fragment.setLocationStatus(LocationFragment.SEARCHING);
				fragment.getLocation();
				modal.dismiss();
			}
		});
		modal.getCancelButton().setOnClickListener(new OnClickListener(){
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
	public static SimpleTwoButtonModal getLocationAcceptanceModal(final Context context, 
			final LocationFragment fragment){
		final SimpleTwoButtonModal modal = new SimpleTwoButtonModal(context);
		final String content = context.getString(R.string.atm_location_modal_content);
		modal.setTitle(R.string.atm_location_modal_title);
		modal.setContent(Html.fromHtml(content));
		modal.getContentView().setMovementMethod(LinkMovementMethod.getInstance());
		modal.setOkButtonText(R.string.atm_location_modal_allow);
		modal.setCancelButtonText(R.string.atm_location_modal_decline);
		modal.hideNeedHelpFooter();
		modal.getOkButton().setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(final View v){
				if(fragment instanceof AtmMapFragment) {
					((AtmMapFragment)fragment).searchCurrentLocation();
				}
				saveUserChoice();
				modal.dismiss();
			}
		});
		modal.getCancelButton().setOnClickListener(new OnClickListener(){
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
	public static SimpleTwoButtonModal getCurrentLocationFailModal(final Context context, 
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
	private static SimpleContentModal getSimpleResultsModal(final Context context, final int titleId, 
			final int contentId) {
		final SimpleContentModal modal = new SimpleContentModal(context);
		modal.setTitle(titleId);
		modal.setContent(contentId);
		modal.showErrorIcon(false);
		modal.hideNeedHelpFooter();
		modal.getButton().setText(R.string.atm_no_results_button);
		modal.getButton().setOnClickListener(new OnClickListener(){
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
	public static SimpleContentModal getNoResultsModal(final Context context){
		final SimpleContentModal modal = 
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
	public static SimpleContentModal getInvalidAddressModal(final Context context){
		return getSimpleResultsModal(context, R.string.atm_no_results_title, R.string.atm_no_results_content);
	}

	/**
	 * Get the modal that will inform the user that they have entered an invalid address
	 * @param context - activity context
	 * @param fragment - fragment using the modal
	 * @return the modal that will ask the user if they would like to allow
	 * the app to use their current location
	 */
	public static SimpleNoButtonModal getAtmLocatorHelpModal(final AtmMapFragment fragment){
		final AtmLocatorHelpModalTop top  = new AtmLocatorHelpModalTop(fragment.getActivity(), null);
		final SimpleNoButtonModal modal = new SimpleNoButtonModal(fragment.getActivity(), top);
		modal.setOnDismissListener(new OnDismissListener(){
			@Override
			public void onDismiss(final DialogInterface dialog) {
				fragment.setHelpModalShowing(false);

			}
		});

		return modal;
	}
}