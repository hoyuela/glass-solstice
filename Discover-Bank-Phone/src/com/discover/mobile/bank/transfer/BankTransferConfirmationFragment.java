package com.discover.mobile.bank.transfer;

import java.util.ArrayList;
import java.util.List;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.discover.mobile.bank.BankExtraKeys;
import com.discover.mobile.bank.R;
import com.discover.mobile.bank.framework.BankConductor;
import com.discover.mobile.bank.services.transfer.TransferDetail;
import com.discover.mobile.bank.ui.table.ListItemGenerator;

/**
 * This is the confirmation screen for a bank transfer.
 * A user will be navigated to this screen if they have successfully scheduled a transfer.
 * 
 * @author scottseward
 *
 */
public class BankTransferConfirmationFragment extends BankTransferBaseFragment {
	private TransferDetail successDetail = null;

	@Override
	public View onCreateView(final LayoutInflater inflater, final ViewGroup container, final Bundle savedInstanceState) {
		loadBundleData(getArguments());
		loadBundleData(savedInstanceState);
		
		final View view = super.onCreateView(inflater, container, savedInstanceState);
		
		actionButton.setText(R.string.schedule_another_transfer);
		actionLink.setText(R.string.view_account_summary);
		
	
		showSuccessfulTransferTitle(view);
		
		return view;
	}
	
	/**
	 * Show the title note on the screen that a users transfer was a success.
	 */
	private void showSuccessfulTransferTitle(final View view) {
		final TextView successTitle = (TextView)view.findViewById(R.id.success_note_text);
		
		successTitle.setText(R.string.transfer_success_title);
		successTitle.setVisibility(View.VISIBLE);

	}
	
	/**
	 * Access a given bundle and retrieve data that we want from it.
	 * @param bundle
	 */
	private void loadBundleData(final Bundle bundle) {
		if(bundle != null) {
			successDetail = (TransferDetail)bundle.getSerializable(BankExtraKeys.TRANSFER_SUCCESS_DATA);
		}
	}
	
	/**
	 * Save the success data to the bundle for configuration changes.
	 */
	@Override
	public void onSaveInstanceState(final Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putSerializable(BankExtraKeys.TRANSFER_SUCCESS_DATA, successDetail);
	}
	
	@Override
	protected List<RelativeLayout> getRelativeLayoutListContent() {
		final ListItemGenerator generator = new ListItemGenerator(this.getActivity());
		final List<RelativeLayout> list = new ArrayList<RelativeLayout>();
		
		list.addAll(generator.getTransferConfirmationList(successDetail));
		
		return list;
	}
	
	/**
	 * When the action button is clicked, we start the whole process over.
	 */
	@Override
	protected void onActionButtonClick() {
		final Bundle navBackBundle = new Bundle();
		navBackBundle.putBoolean(BankExtraKeys.SHOULD_NAVIGATE_BACK, true);
		BankConductor.navigateToTransferMoneyLandingPage(navBackBundle);
	}

	/**
	 * Navigate the user back to the home page.
	 */
	@Override
	protected void onActionLinkClick() {
		BankConductor.navigateToHomePage();
	}
	
	/**
	 * Set the title breadcrumb position to the last step.
	 */
	@Override
	protected int getProgressIndicatorStep() {
		return 3;
	}
	
}
