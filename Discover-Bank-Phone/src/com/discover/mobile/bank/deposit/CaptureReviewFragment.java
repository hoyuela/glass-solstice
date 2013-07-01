package com.discover.mobile.bank.deposit;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.discover.mobile.bank.BankExtraKeys;
import com.discover.mobile.bank.R;
import com.discover.mobile.bank.error.BankErrorHandler;
import com.discover.mobile.bank.error.BankErrorHandlerDelegate;
import com.discover.mobile.bank.error.BankExceptionHandler;
import com.discover.mobile.bank.framework.BankConductor;
import com.discover.mobile.bank.framework.BankNetworkServiceCallManager;
import com.discover.mobile.bank.payees.BankEditDetail;
import com.discover.mobile.bank.services.account.Account;
import com.discover.mobile.bank.services.deposit.DepositDetail;
import com.discover.mobile.bank.services.deposit.SubmitCheckDepositCall;
import com.discover.mobile.bank.services.error.BankError;
import com.discover.mobile.bank.services.error.BankErrorCodes;
import com.discover.mobile.bank.services.error.BankErrorResponse;
import com.discover.mobile.bank.ui.modals.AreYouSureGoBackModal;
import com.discover.mobile.bank.util.BankStringFormatter;
import com.discover.mobile.common.DiscoverActivityManager;
import com.discover.mobile.common.net.NetworkServiceCall;
import com.discover.mobile.common.utils.StringUtility;
import com.google.common.base.Strings;

/**
 * This is the fragment responsible for showing the user the details
 * of their check deposit before submitting it to the server.
 * This fragment only loads content that already exists, such as
 * the account that is getting the deposit, the amount that was entered
 * in a previous fragment and images that are loaded from the device storage.
 * 
 * @author scottseward
 *
 */
public class CaptureReviewFragment extends BankDepositBaseFragment implements BankErrorHandlerDelegate {	

	/**
	 * The cell in the table that has the amount for check deposit in it 
	 * we need a reference to it so we can set inline error.
	 */
	private BankEditDetail amountDetail;	
	/**
	 * The cell in the table that has the check images in it 
	 * we need a reference to it so we can refresh it on resume
	 */
	private ReviewCheckDepositTableCell checkImageCell;
	
	/**
	 * Reference to bundle provided in onCreateView or via getArguments() depending on what created the fragment.
	 */
	private Bundle bundle = null;
	/**
	 * Key used to store in-line error for the image cell which shows the captured images for checks
	 */
	private static final String IMAGE_CELL_ERROR_KEY = "image" + KEY_ERROR_EXT;

	private int depositAmount = 0;
	
	/**The identifier for  the check deposit capture acitivty during a retake.*/
	private final int RETAKE_ACTION = 1;

	private Account account = null;

	/**
	 * Inflate and setup the UI for this fragment.
	 */
	@Override
	public View onCreateView(final LayoutInflater inflater, final ViewGroup container,
			final Bundle savedInstanceState){
		setRetainInstance(true);
		bundle = ( null != savedInstanceState ) ? savedInstanceState : getArguments();

		/**Store bundle provided to restore state of fragment onResume*/
		if( null != bundle ) {
			account = (Account)bundle.getSerializable(BankExtraKeys.DATA_LIST_ITEM);
			depositAmount = bundle.getInt(BankExtraKeys.AMOUNT);
		}

		final View view = super.onCreateView(inflater, container, savedInstanceState);	

		//Set button text labels.
		setButtonText(R.string.deposit_now);
		setLinkText(R.string.cancel_text);

		/**Hide controls that are not needed*/
		hideBottomNote();
		getPageTitleView().setVisibility(View.GONE);

		/**Hide top note as it is not needed for this view**/
		final TextView topNote = (TextView)view.findViewById(R.id.top_note_text);
		topNote.setVisibility(View.GONE);

		setupRetakeLinks(view);
		return view;
	}

	/**
	 * Refresh the check image cell.
	 * This is required for when a user retakes an image to ensure that
	 * this fragment stays up to date with the curent check images.s
	 */
	@Override
	public void onResume() {
		super.onResume();

		restoreState();
		checkImageCell.loadImages();

		/**Check if an exception occurred  that needs to be handled*/
		handlePendingSocketException();

		/**Check if a successful response was received*/
		handlePendingConfirmation();
	}
	
	@Override
	public void onPause() {
		super.onPause();
		if(bundle == null){
			bundle = new Bundle();
		}
		
		onSaveInstanceState(bundle);
	}

	/**
	 * Method checks if a socket timeout occurred, if so navigates the user to 
	 * CheckDepositErrorFragment.
	 */
	private void handlePendingSocketException() {
		final BankExceptionHandler exceptionHandler = BankExceptionHandler.getInstance();

		/**Check if a socket timeout exception occurred*/
		if( exceptionHandler.getLastException() != null &&
				exceptionHandler.getLastSender() != null &&
				exceptionHandler.getLastSender() instanceof SubmitCheckDepositCall ) {

			/**Clear the last exception occurred to avoid the back press not working*/
			exceptionHandler.clearLastException();
			
			//Check if network connection available
			if( BankNetworkServiceCallManager.isNetworkConnected() ) {
				BankConductor.navigateToCheckDepositWorkFlow(null, BankDepositWorkFlowStep.DepositError);  
			} else {
				((BankErrorHandler)BankErrorHandler.getInstance()).handleNoConnection();
			}			
		}
	}

	/**
	 * Method checks if a successful response was recevied for SubmitCheckDepositCall if so
	 * navigate to confirmation page.
	 */
	private void handlePendingConfirmation() {
		final NetworkServiceCall<?> networkServiceCall = BankNetworkServiceCallManager.getInstance().getLastServiceCall();

		/**Verify that network service call is not null, was a check deposit submit, and this transaction is not complete*/
		if (networkServiceCall != null && 
				networkServiceCall instanceof SubmitCheckDepositCall ) {	

			final SubmitCheckDepositCall submitDepositCall = (SubmitCheckDepositCall)networkServiceCall;

			/**check if this service call has already been handled if so then ignore*/
			if( !submitDepositCall.isHandled() && null != submitDepositCall.getResult()) {
				submitDepositCall.setHandled(true);

				//Mark the scheduled activity dirty so that it is refreshed
				account.scheduled = null;
				//Mark the posted activity dirty so that it is refreshed
				account.posted = null;

				//Navigate to Check Deposit Confirmation Page
				final Bundle confirmationBundle = new Bundle();
				confirmationBundle.putSerializable(BankExtraKeys.DATA_LIST_ITEM, submitDepositCall.getResult());
				BankConductor.navigateToCheckDepositWorkFlow(confirmationBundle, BankDepositWorkFlowStep.Confirmation);	
			}	
		}
	}

	private Activity getThisActivity() {
		return getActivity();
	}

	/**
	 * Show a modal warning upon back press to alert the user that if they go back, they will be losing all
	 * of their information.
	 */
	@Override
	public void onBackPressed() {
		final AreYouSureGoBackModal modal = new AreYouSureGoBackModal(this, BankDepositSelectAccount.class, 
				new OnClickListener() {
					@Override
					public void onClick(final View v) {
						CheckDepositCaptureActivity.deleteBothImages(DiscoverActivityManager.getActiveActivity());
					}
				});
		
		modal.setModalBodyText(R.string.deposit_go_back_content);
		
		modal.showModal();
	}

	/**
	 * Disable the default back press from the activity and use only the method implemented in this class
	 * for onBackPressed.
	 */
	@Override
	public boolean isBackPressDisabled() {
		return true;
	}

	@Override
	protected int getProgressIndicatorStep() {
		return 2;
	}

	/**
	 * Returns the list of data to be shown in the content table on this fragment.
	 * One of the super classes is responsible for calling this method when 
	 * it needs to request data to display.
	 */
	@Override
	protected List<RelativeLayout> getRelativeLayoutListContent() {
		final Activity currentActivity = getActivity();
		final List<RelativeLayout> content = new ArrayList<RelativeLayout>();

		if( account != null ) {
			/**These BankEditDetail objects override the onClick
			 * focusChange listeners of its super class.*/
			final BankEditDetail accountDetail = new BankEditDetail(currentActivity);

			accountDetail.getDividerLine().setVisibility(View.INVISIBLE);
			accountDetail.getTopLabel().setText(BankStringFormatter.getAccountEndingInString(account.accountNumber.ending));
			accountDetail.getMiddleLabel().setText(account.nickname);
			accountDetail.getMiddleLabel().setSingleLine(false);
			accountDetail.getMiddleLabel().setMaxLines(2);
			accountDetail.getEditableField().setVisibility(View.GONE); // Ensure editable field cannot be selected
			accountDetail.getView().setOnFocusChangeListener(null);
			accountDetail.getView().setOnClickListener(new OnClickListener() {
				/**
				 * On click of this list item, go back to select an account.
				 * There is no need to change the Bundle because select account is the first
				 * step of the checkDepositWorkflow and that Fragment will handle coming
				 * back here when an account is selected.
				 */
				@Override
				public void onClick(final View v) {
					final Bundle args = getArguments();
					args.putBoolean(BankExtraKeys.RESELECT_ACCOUNT, true);
					args.putInt(BankExtraKeys.AMOUNT, depositAmount);
					BankConductor.navigateToCheckDepositWorkFlow(args, BankDepositWorkFlowStep.SelectAccount);
				}
			});
			content.add(accountDetail);

			/**These BankEditDetail objects override the onClick
			 * focusChange listeners of its super class.*/
			amountDetail = new BankEditDetail(currentActivity);

			amountDetail.getView().setOnClickListener(new OnClickListener() {

				/** On click we need to go back to the enter amount Fragment.*/
				@Override
				public void onClick(final View v) {
					final Bundle adjustAmountBundle = getArguments();
					adjustAmountBundle.putBoolean(BankExtraKeys.REENTER_AMOUNT, true);
					BankConductor.navigateToCheckDepositWorkFlow(adjustAmountBundle, BankDepositWorkFlowStep.SelectAmount);
				}
			});

			final String amount = getResources().getString(R.string.amount);
			amountDetail.setOnFocusChangeListener(null);
			amountDetail.getTopLabel().setText(amount);
			amountDetail.getEditableField().setVisibility(View.GONE); // Ensure editable cannot be selected
			amountDetail.getMiddleLabel().setText(BankStringFormatter.convertCentsToDollars(depositAmount));
			content.add(amountDetail);

			checkImageCell = new ReviewCheckDepositTableCell(currentActivity);

			content.add(checkImageCell);
		}
		return content;
	}

	/**
	 * Set click listeners on the retake labels so that when they are clicked the check deposit capture
	 * activity is started with the retake parameter set.
	 * @param view the view that contains the retake labels.
	 */
	private void setupRetakeLinks(final View view) {
		if( null != view ) {
			final TextView retakeFrontLabel = (TextView)view.findViewById(R.id.retake_front_label);
			final TextView retakeBackLabel = (TextView)view.findViewById(R.id.retake_back_label);

			if( retakeFrontLabel != null && retakeBackLabel != null) {
				retakeFrontLabel.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(final View view) {
						retake(CheckDepositCaptureActivity.RETAKE_FRONT);
					}
				});

				retakeBackLabel.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(final View v) {
						retake(CheckDepositCaptureActivity.RETAKE_BACK);
					}
				});
			}
		}
	}

	/**
	 * Initiate the retake process where one picture will be retaken and then the user will be sent back here.
	 * @param type the picture to retake in the capture activity.
	 */
	public void retake(final int type) {
		final Intent retakePic = new Intent(getActivity(), CheckDepositCaptureActivity.class);
		retakePic.putExtra(BankExtraKeys.RETAKE_PICTURE, type);
		startActivityForResult(retakePic, RETAKE_ACTION);
	}

	/**
	 * On result of the capture activity we may need to clear any errors that are on screen.
	 */
	@Override
	public void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
		if(requestCode == RETAKE_ACTION) {
			if(resultCode == Activity.RESULT_OK) {
					
				final String key = 
						(amountDetail != null) ? 
								amountDetail.getTopLabel().getText().toString() : StringUtility.EMPTY;
				final Bundle args = getArguments();
				/*
				 * Reset all errors in both bundles in case this method is called and a rotation
				 * occurs immediately, before onResume can finish.
				 */
				if(args != null) {
					args.putString(key + KEY_ERROR_EXT, StringUtility.EMPTY);
					args.putString(IMAGE_CELL_ERROR_KEY, StringUtility.EMPTY);		
				}
				//Clear the bundle that is used to restore error states after rotation.
				if(bundle != null) {
					bundle.putString(key + KEY_ERROR_EXT, StringUtility.EMPTY);
					bundle.putString(IMAGE_CELL_ERROR_KEY, StringUtility.EMPTY);
				}
				//Clear the error text that is in text labels on the screen before hiding them.
				showAmountError(StringUtility.EMPTY);
				showImageError(StringUtility.EMPTY);
				clearErrors();						
			}
		}
	}

	@Override
	protected void onActionButtonClick() {
		sendDeposit();
	}

	/**
	 * Method used to start the DepositSubmissionActivity and send a request to submit a check deposit. The
	 * DepositSubmissionActivity will send request at launch.
	 */
	public void sendDeposit() {
		clearErrors();

		final Intent depositSubmission = new Intent(getThisActivity(), DepositSubmissionActivity.class);
		final Bundle extras = getArguments();
		if(extras != null) {
			depositSubmission.putExtras(extras);
		}
		final int activityId = 1;
		
		startActivityForResult(depositSubmission, activityId);
	}
	
	@Override
	protected void onActionLinkClick() {
		final AreYouSureGoBackModal cancelModal = new AreYouSureGoBackModal(this, BankDepositSelectAccount.class, 
				new OnClickListener() {
					@Override
					public void onClick(final View v) {
						CheckDepositCaptureActivity.deleteBothImages(DiscoverActivityManager.getActiveActivity());
					}
				}); 
		
		cancelModal.setTitleTextResource(R.string.cancel_deposit_title);
		cancelModal.setModalBodyText(R.string.cancel_deposit_content);
		cancelModal.setButtonText(R.string.cancel_this_deposit);
		
		cancelModal.showModal();
	}

	@Override
	public boolean handleError(final BankErrorResponse msgErrResponse) {
		/**flag to indicate whether the error has been handled*/
		boolean handled = false;

		/**Set Inline Errors*/
		for( final BankError error : msgErrResponse.errors ) {
			/**Check if error was because of a duplicate check*/
			if( !Strings.isNullOrEmpty(error.code) && 
					(error.code.equals(BankErrorCodes.ERROR_CHECK_DUPLICATE)  ||
							error.code.equals(BankErrorCodes.ERROR_CHECK_DUPLICATE_EX ))) {
				BankConductor.navigateToCheckDepositWorkFlow(null, BankDepositWorkFlowStep.DuplicateError);
				handled = true;
			}
			/**Check if it is an inline error*/
			else if( !Strings.isNullOrEmpty(error.message) ) {
				/**Notify user that they have an inline error at top of page*/
				showGeneralError( getActivity().getResources().getString(R.string.bank_deposit_error_notify) );

				/**Show inline error under amount field*/
				if( !Strings.isNullOrEmpty(error.name) &&
						error.name.equals(DepositDetail.AMOUNT_FIELD) ) {		
					amountDetail.getEditableField().showErrorLabelNoFocus(error.message);
				}
				/**Show inline error under check image cell*/
				else {
					checkImageCell.showErrorLabel(error.message);
				}

				/**Notify caller that error has been handled so no further error handling is required*/
				handled = true;
			} 
		}
		return handled;
	}


	/**
	 * Method used to clear any inline errors on the page.
	 */
	public void clearErrors() {
		clearGeneralError();
		amountDetail.getEditableField().clearErrors();
		checkImageCell.clearError();
	}

	/**
	 * Method used to store the state of the fragment and support orientation change
	 */
	@Override
	public void onSaveInstanceState(final Bundle outState) {
		super.onSaveInstanceState(outState);

		/**Check that onCreateView was called otherwise rotation data will get wiped out*/
		if( bundle != null ) {
			/**Store values stored in each field*/
			outState.putSerializable(BankExtraKeys.DATA_LIST_ITEM, account);
			outState.putInt(BankExtraKeys.AMOUNT, depositAmount);

			/**Store error shown at bottom of amount field*/
			if( amountDetail != null && amountDetail.getEditableField().isInErrorState ) {
				final String key = amountDetail.getTopLabel().getText().toString();
				outState.putString(key + KEY_ERROR_EXT, 
										amountDetail.getEditableField().getErrorLabel().getText().toString());
			}

			/**Store error shown at bottom of captured image field*/
			if(checkImageCell != null && checkImageCell.getErrorLabel().getVisibility() == View.VISIBLE ) {
				outState.putString(IMAGE_CELL_ERROR_KEY, checkImageCell.getErrorLabel().getText().toString());
			}
		} else {
			outState.putAll(getArguments());
		}
	}

	/**
	 * Restores the widget's states from before rotation.
	 * 
	 * @param savedInstanceState
	 *            the Bundle from which the data is loaded.
	 */
	public void restoreState() {
		if( bundle != null  ) {	
			
			final String key = (amountDetail != null) ? amountDetail.getTopLabel().getText().toString() : "";
			final String amountError = bundle.getString(key +KEY_ERROR_EXT);
			final String imageError = bundle.getString(IMAGE_CELL_ERROR_KEY);

			/**Handle display of inline error asyncronously*/
			new Handler().post(new Runnable() {
				@Override
				public void run() {			
					showAmountError(amountError);
					showImageError(imageError);
				}
			});
		}
	}
	
	/**
	 * Show error text underneth the amount.
	 * @param amountError a String to display as an error
	 */
	private void showAmountError(final String amountError) {
		/**If has amount in-line error then show it on rotation */
		if(!Strings.isNullOrEmpty(amountError) ) {
			showGeneralError( getActivity().getResources().getString(R.string.bank_deposit_error_notify) );
			amountDetail.getEditableField().showErrorLabelNoFocus(amountError);
		}
	}
	
	/**
	 * Show error text beneath the image cells.
	 * @param imageError a String to display as an error.
	 */
	private void showImageError(final String imageError) {
		/**If has an image cell in-line error then show it on rotation*/
		if(!Strings.isNullOrEmpty(imageError) ) {
			final Activity activity =  getActivity();
			if( activity != null && checkImageCell != null) {
				showGeneralError( activity.getResources().getString(R.string.bank_deposit_error_notify) );
				checkImageCell.showErrorLabel(imageError);
			}
		}
	}

}
