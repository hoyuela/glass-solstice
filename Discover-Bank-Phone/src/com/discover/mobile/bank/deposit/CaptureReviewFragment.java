package com.discover.mobile.bank.deposit;

import java.net.SocketTimeoutException;
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
import com.discover.mobile.bank.error.BankErrorHandlerDelegate;
import com.discover.mobile.bank.error.BankExceptionHandler;
import com.discover.mobile.bank.framework.BankConductor;
import com.discover.mobile.bank.navigation.BankNavigationRootActivity;
import com.discover.mobile.bank.payees.BankEditDetail;
import com.discover.mobile.bank.services.account.Account;
import com.discover.mobile.bank.services.deposit.DepositDetail;
import com.discover.mobile.bank.services.deposit.SubmitCheckDepositCall;
import com.discover.mobile.bank.util.BankStringFormatter;
import com.discover.mobile.common.DiscoverActivityManager;
import com.discover.mobile.common.help.HelpWidget;
import com.discover.mobile.common.net.error.bank.BankError;
import com.discover.mobile.common.net.error.bank.BankErrorResponse;
import com.discover.mobile.common.ui.modals.ModalAlertWithTwoButtons;
import com.discover.mobile.common.ui.modals.ModalDefaultTopView;
import com.discover.mobile.common.ui.modals.ModalDefaultTwoButtonBottomView;
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
	 * The cell in the table that has the account selected for check deposit in it 
	 * we need a reference to it so we can set inline error.
	 */
	private BankEditDetail accountDetail;
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
	private final static String IMAGE_CELL_ERROR_KEY = "image" +KEY_ERROR_EXT;
	
	
	private final int depositSubmitActivityId = 1;

	int depositAmount = 0;
	Account account = null;
	
	/**
	 * Inflate and setup the UI for this fragment.
	 */
	@Override
	public View onCreateView(final LayoutInflater inflater, final ViewGroup container,
			final Bundle savedInstanceState){

		bundle = ( null != savedInstanceState ) ? savedInstanceState : getArguments();
	
		/**Store bundle provided to restore state of fragment onResume*/
		if( null != bundle ) {
			account = (Account)bundle.getSerializable(BankExtraKeys.DATA_LIST_ITEM);
			depositAmount = bundle.getInt(BankExtraKeys.AMOUNT);
		}
		
		final View view = super.onCreateView(inflater, container, savedInstanceState);	
		
		//Set button text labels.
		actionButton.setText(R.string.deposit_now);
		actionLink.setText(R.string.cancel_text);
		
		/**Hide controls that are not needed*/
		noteTitle.setVisibility(View.GONE);
		noteTextMsg.setVisibility(View.GONE);
		feedbackLink.setVisibility(View.GONE);
		pageTitle.setVisibility(View.GONE);
		
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
		
		if(checkImageCell != null)
			checkImageCell.loadImages(getActivity());
		
		restoreState();
		
		/*Check if an exception occurred  that needs to be handled*/
		handlePendingSocketException();
	}
	
	/**
	 * Method checks if a socket timeout occurred, if so navigates the user to 
	 * CheckDepositErrorFragment.
	 */
	private void handlePendingSocketException() {
		final BankExceptionHandler exceptionHandler = BankExceptionHandler.getInstance();
		
		/**Check if a socket timeout exception occurred*/
		if( exceptionHandler.getLastException() != null && 
			exceptionHandler.getLastException() instanceof SocketTimeoutException &&
			exceptionHandler.getLastSender() != null &&
			exceptionHandler.getLastSender() instanceof SubmitCheckDepositCall ) {
			
			/**Clear the last exception occurred to avoid the back press not working*/
			exceptionHandler.clearLastException();
			
			final Bundle bundle = new Bundle();
			bundle.putBoolean(CheckDepositErrorFragment.class.getSimpleName(), true);
			BankConductor.navigateToCheckDepositWorkFlow(bundle);		
		}
	}
	
	private Activity getThisActivity() {
		return getActivity();
	}
	
	private OnClickListener dismissModalOnClickListener(final ModalAlertWithTwoButtons modal) {
		return new OnClickListener() {
			
			@Override
			public void onClick(final View v) {
				modal.dismiss();
			}
		};
	}
	
	private OnClickListener getCancelDepositWorkflowClickListener(final ModalAlertWithTwoButtons modal) {
		return new OnClickListener() {
			 
			@Override
			public void onClick(final View v) {
				cancelCheckDepositWorkflow();
			    modal.dismiss();
			}
		};	
	}
	
	/**
	 * Show a modal warning upon back press to alert the user that if they go back, they will be losing all
	 * of their information.
	 */
	@Override
	public void onBackPressed() {
		BankNavigationRootActivity currentActivity = null;
		if(DiscoverActivityManager.getActiveActivity() instanceof BankNavigationRootActivity) {
			currentActivity = (BankNavigationRootActivity)DiscoverActivityManager.getActiveActivity();
		}
		
		if(currentActivity != null) {
			final ModalDefaultTopView modalTopView = new ModalDefaultTopView(currentActivity, null);
			final ModalDefaultTwoButtonBottomView bottom = new ModalDefaultTwoButtonBottomView(currentActivity, null);
			
			bottom.setOkButtonText(R.string.continue_text);
			bottom.setCancelButtonText(R.string.cancel_text);
			
			modalTopView.setTitle(R.string.are_you_sure_title);
			modalTopView.setContent(R.string.cancel_deposit_content);
			
			modalTopView.getHelpFooter().show(false);
			
			final ModalAlertWithTwoButtons modal = new ModalAlertWithTwoButtons(currentActivity, modalTopView, bottom);
			bottom.getOkButton().setOnClickListener(getCancelDepositWorkflowClickListener(modal));
			
			bottom.getCancelButton().setOnClickListener(dismissModalOnClickListener(modal));
		
			modal.show();
		}
	}
	
	/**
	 * Navigates back to step 1 of the check deposit work flow and deletes any cached images.
	 */
	private void cancelCheckDepositWorkflow() {
		final BankNavigationRootActivity activity = 
				(BankNavigationRootActivity)DiscoverActivityManager.getActiveActivity();
	    activity.popTillFragment(BankDepositSelectAccount.class);
	    CheckDepositCaptureActivity.deleteBothImages(activity);
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
		
		/**These BankEditDetail objects override the onClick
		 * focusChange listeners of its super class.*/
		final BankEditDetail accountDetail = new BankEditDetail(currentActivity);
		
		accountDetail.getDividerLine().setVisibility(View.GONE);
		accountDetail.getTopLabel().setText(BankStringFormatter.getAccountEndingInString(account.accountNumber.ending));
		accountDetail.getMiddleLabel().setText(account.nickname);
		accountDetail.getMiddleLabel().setSingleLine(false);
		accountDetail.getMiddleLabel().setMaxLines(2);
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
				BankConductor.navigateToCheckDepositWorkFlow(args);
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

				BankConductor.navigateToCheckDepositWorkFlow(adjustAmountBundle);
			}
		});
		
		final String amount = getResources().getString(R.string.amount);
		amountDetail.setOnFocusChangeListener(null);
		amountDetail.getTopLabel().setText(amount);
		amountDetail.getMiddleLabel().setText(BankStringFormatter.convertCentsToDollars(depositAmount));
		content.add(amountDetail);
		
		checkImageCell = new ReviewCheckDepositTableCell(currentActivity);
		content.add(checkImageCell);
		
		return content;
	}
	
	/**
	 * Set click listeners on the retake labels so that when they are clicked the check deposit capture
	 * activity is started with the retake parameter set.
	 * @param view the view that contains the retake labels.
	 */
	private void setupRetakeLinks(final View view) {
		final TextView retakeFrontLabel = (TextView)view.findViewById(R.id.retake_front_label);
		final TextView retakeBackLabel = (TextView)view.findViewById(R.id.retake_back_label);
		
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

	/**
	 * Initiate the retake process where one picture will be retaken and then the user will be sent back here.
	 * @param type the picture to retake in the capture activity.
	 */
	public void retake(final int type) {
		final Intent retakePic = new Intent(getActivity(), CheckDepositCaptureActivity.class);
		retakePic.putExtra(BankExtraKeys.RETAKE_PICTURE, type);
		startActivity(retakePic);
	}
	
	@Override
	protected void onActionButtonClick() {
		clearErrors();
		
		final Intent depositSubmission = new Intent(getThisActivity(), DepositSubmissionActivity.class);
		final Bundle extras = getArguments();
		if(extras != null)
			depositSubmission.putExtras(extras);
		
		startActivityForResult(depositSubmission, depositSubmitActivityId);
	}

	@Override
	protected void onActionLinkClick() {
		final Activity currentActivity = getActivity();
		final ModalDefaultTopView modalTopView = new ModalDefaultTopView(currentActivity, null);

		final ModalDefaultTwoButtonBottomView bottom = new ModalDefaultTwoButtonBottomView(currentActivity, null);
		bottom.setOkButtonText(R.string.do_not_go_back);
		bottom.setCancelButtonText(R.string.go_back_and_cancel);
		
		modalTopView.setTitle(R.string.cancel_deposit_title);
		modalTopView.setContent(R.string.cancel_deposit_content);
		
		modalTopView.getHelpFooter().show(false);
		
		final ModalAlertWithTwoButtons modal = new ModalAlertWithTwoButtons(currentActivity, modalTopView, bottom);
		bottom.getOkButton().setOnClickListener(dismissModalOnClickListener(modal));
		bottom.getCancelButton().setOnClickListener(getCancelDepositWorkflowClickListener(modal));
		modal.show();
	}

	@Override
	public boolean handleError(final BankErrorResponse msgErrResponse) {
		/**flag to indicate whether the error has been handled*/
		boolean handled = false;
		
		/**Set Inline Errors*/
		for( final BankError error : msgErrResponse.errors ) {
			if( !Strings.isNullOrEmpty(error.name) ) {
				/**Notify user that they have an inline error at top of page*/
				showGeneralError( getActivity().getResources().getString(R.string.bank_deposit_error_notify) );
				
				/**Show inline error under amount field*/
				if( error.name.equals(DepositDetail.AMOUNT_FIELD) ) {		
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
		
		/**Store values stored in each field*/
		outState.putSerializable(BankExtraKeys.DATA_LIST_ITEM, account);
		outState.putInt(BankExtraKeys.AMOUNT, depositAmount);
		
		/**Store error shown at bottom of amount field*/
		if( amountDetail.getEditableField().isInErrorState ) {
			final String key = amountDetail.getTopLabel().getText().toString();
			outState.putString(key +KEY_ERROR_EXT, amountDetail.getEditableField().getErrorLabel().getText().toString());
		}
		
		/**Store error shown at bottom of captured image field*/
		if( checkImageCell.getErrorLabel().getVisibility() == View.VISIBLE ) {
			outState.putString(IMAGE_CELL_ERROR_KEY, checkImageCell.getErrorLabel().getText().toString());
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
			final String key = amountDetail.getTopLabel().getText().toString();
			final String amountError = bundle.getString(key +KEY_ERROR_EXT);
			final String imageError = bundle.getString(IMAGE_CELL_ERROR_KEY);
			
			/**Handle display of inline error asyncronously*/
			new Handler().postDelayed(new Runnable() {
				@Override
				public void run() {			
					/**If has amount in-line error then show it on rotation */
					if(!Strings.isNullOrEmpty(amountError) ) {
						showGeneralError( getActivity().getResources().getString(R.string.bank_deposit_error_notify) );
						amountDetail.getEditableField().showErrorLabelNoFocus(amountError);
					}
					
					/**If has an image cell in-line error then show it on rotation*/
					if(!Strings.isNullOrEmpty(imageError) ) {
						showGeneralError( getActivity().getResources().getString(R.string.bank_deposit_error_notify) );
						checkImageCell.showErrorLabel(imageError);
					}
				}
			}, 1000);
		}
	}

	@Override
	protected void helpMenuOnClick(final HelpWidget help) {
		// TODO Auto-generated method stub
		
	}
}
