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
import com.discover.mobile.bank.payees.BankEditDetail;

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
	
	/**
	 * Inflate and setup the UI for this fragment.
	 */
	@Override
	public View onCreateView(final LayoutInflater inflater, final ViewGroup container,
			final Bundle savedInstanceState){
		final View view = super.onCreateView(inflater, container, savedInstanceState);
		
		progressIndicator.setPosition(1);
		
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
	
	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub

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
		BankEditDetail detailObject = new BankEditDetail(currentActivity) {
			@Override
			public void onFocusChange(final View arg0, final boolean arg1) {
				
			}
			@Override
			public void onClick(final View view) {
				
			}
		};
		detailObject.getTopLabel().setText("Account ending in 1234");
		detailObject.getMiddleLabel().setText("Discover Online Checking");
		detailObject.setOnClickListener(null);
		detailObject.setOnFocusChangeListener(null);
		content.add(detailObject);
		
		/**These BankEditDetail objects override the onClick
		 * focusChange listeners of its super class.*/
		detailObject = new BankEditDetail(currentActivity) {
			@Override
			public void onFocusChange(final View arg0, final boolean arg1) {
				
			}
			@Override
			public void onClick(final View view) {
				
			}
		};
		detailObject.setOnClickListener(null);
		detailObject.setOnFocusChangeListener(null);
		detailObject.getTopLabel().setText("Amount");
		detailObject.getMiddleLabel().setText("$55.55");
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
