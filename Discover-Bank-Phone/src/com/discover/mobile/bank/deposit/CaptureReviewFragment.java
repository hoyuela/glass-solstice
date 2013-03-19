package com.discover.mobile.bank.deposit;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.discover.mobile.bank.BankExtraKeys;
import com.discover.mobile.bank.R;
import com.discover.mobile.bank.framework.BankConductor;
import com.discover.mobile.bank.payees.BankEditDetail;
import com.discover.mobile.bank.services.account.Account;
import com.discover.mobile.bank.util.BankStringFormatter;
import com.discover.mobile.common.ui.modals.ModalAlertWithTwoButtons;
import com.discover.mobile.common.ui.modals.ModalDefaultTopView;
import com.discover.mobile.common.ui.modals.ModalDefaultTwoButtonBottomView;

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
public class CaptureReviewFragment extends BankDepositBaseFragment {	
	
	/**
	 * The cell in the table that has the check images in it 
	 * we need a reference to it so we can refresh it on resume
	 */
	private ReviewCheckDepositTableCell checkImageCell;
	
	int depositAmount = 0;
	Account account = null;
	
	/**
	 * Inflate and setup the UI for this fragment.
	 */
	@Override
	public View onCreateView(final LayoutInflater inflater, final ViewGroup container,
			final Bundle savedInstanceState){

		final Bundle arguments = getArguments();
	
		if(arguments != null) {
			account = (Account)arguments.getSerializable(BankExtraKeys.DATA_LIST_ITEM);
			depositAmount = arguments.getInt(BankExtraKeys.AMOUNT);
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
		
		final LinearLayout footer = (LinearLayout)view.findViewById(R.id.footer_layout);
		footer.setVisibility(View.GONE);

		setupSubmitLink();
		setupCancelLink();
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
	}
	
	private Activity getThisActivity() {
		return getActivity();
	}
	
	private final int depositSubmitActivity = 1;
	private void setupSubmitLink() {
		actionButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(final View v) {
				final Intent depositSubmission = new Intent(getThisActivity(), DepositSubmissionActivity.class);
				final Bundle extras = getThisActivity().getIntent().getExtras();
				if(extras != null)
					depositSubmission.putExtras(extras);
				startActivityForResult(depositSubmission, depositSubmitActivity);
			}
		});
	}
	
	private void setupCancelLink() {
		actionLink.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(final View v) {
				final Activity currentActivity = getActivity();
				final ModalDefaultTopView modalTopView = new ModalDefaultTopView(currentActivity, null);

				final ModalDefaultTwoButtonBottomView bottom = new ModalDefaultTwoButtonBottomView(currentActivity, null);
				bottom.setOkButtonText(R.string.do_not_go_back);
				bottom.setCancelButtonText(R.string.go_back_and_cancel);
				
				modalTopView.setTitle(R.string.cancel_deposit_title);
				modalTopView.setContent(R.string.cancel_deposit_content);
				
				modalTopView.getHelpFooter().show(false);
				
				final ModalAlertWithTwoButtons modal = new ModalAlertWithTwoButtons(currentActivity, modalTopView, bottom);
				bottom.getOkButton().setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(final View v) {
						modal.dismiss();
						onBackPressed();
					}
				});

				modal.show();
			}
		});
	}
		
	@Override
	public void onBackPressed() {
		final Activity currentActivity = getActivity();
		final ModalDefaultTopView modalTopView = new ModalDefaultTopView(currentActivity, null);
		final ModalDefaultTwoButtonBottomView bottom = new ModalDefaultTwoButtonBottomView(currentActivity, null);
		
		bottom.setOkButtonText(R.string.continue_text);
		bottom.setCancelButtonText(R.string.cancel_text);
		
		modalTopView.setTitle(R.string.are_you_sure_title);
		modalTopView.setContent(R.string.cancel_deposit_content);
		
		modalTopView.getHelpFooter().show(false);
		
		final ModalAlertWithTwoButtons modal = new ModalAlertWithTwoButtons(currentActivity, modalTopView, bottom);
		bottom.getOkButton().setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(final View v) {
				modal.dismiss();
			}
		});

		modal.show();
	}

	@Override
	protected int getProgressIndicatorStep() {
		return 1;
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
		BankEditDetail detailObject = new BankEditDetail(currentActivity);
		
		detailObject.getDividerLine().setVisibility(View.GONE);
		final String endingIn = getResources().getString(R.string.account_ending_in);
		
		detailObject.getTopLabel().setText(endingIn + " " + account.accountNumber.ending);
		detailObject.getMiddleLabel().setText(account.nickname);
		detailObject.getMiddleLabel().setSingleLine(false);
		detailObject.getMiddleLabel().setMaxLines(2);
		detailObject.getView().setOnFocusChangeListener(null);
		detailObject.getView().setOnClickListener(new OnClickListener() {
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
		content.add(detailObject);
		
		/**These BankEditDetail objects override the onClick
		 * focusChange listeners of its super class.*/
		detailObject = new BankEditDetail(currentActivity);
		
		detailObject.getView().setOnClickListener(new OnClickListener() {
			
			/** On click we need to go back to the enter amount Fragment.*/
			@Override
			public void onClick(final View v) {
				final Bundle adjustAmountBundle = getArguments();
				adjustAmountBundle.putBoolean(BankExtraKeys.REENTER_AMOUNT, true);

				BankConductor.navigateToCheckDepositWorkFlow(adjustAmountBundle);
			}
		});
		
		detailObject.setOnFocusChangeListener(null);
		detailObject.getTopLabel().setText("Amount");
		detailObject.getMiddleLabel().setText(BankStringFormatter.convertCentsToDollars(depositAmount));
		content.add(detailObject);
		
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
		// TODO Auto-generated method stub

	}

	@Override
	protected void onActionLinkClick() {
		// TODO Auto-generated method stub

	}

}
