package com.discover.mobile.bank.transfer;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.discover.mobile.bank.R;
import com.discover.mobile.bank.payees.BankEditDetail;
import com.discover.mobile.bank.ui.table.AmountListItem;

public class BankTransferStepOneFragment extends BankTransferBaseFragment {
	private final String TAG = BankTransferStepOneFragment.class.getSimpleName();

	@Override
	public View onCreateView(final LayoutInflater inflater, final ViewGroup container, 
			final Bundle savedInstanceState) {
		final View view = super.onCreateView(inflater, container, savedInstanceState);
		
		/**Hide controls that are not needed*/
		actionButton.setText(R.string.schedule_transfer);
		actionLink.setText(R.string.cancel_text);
		noteTitle.setVisibility(View.GONE);
		noteTextMsg.setVisibility(View.GONE);
		helpFooter.show(false);
		feedbackLink.setVisibility(View.GONE);
		
		/**Hide top note as it is not needed for this view**/
		final TextView topNote = (TextView)view.findViewById(R.id.top_note_text);
		topNote.setVisibility(View.GONE);

		return view;
	}
	
	@Override
	protected int getProgressIndicatorStep() {
		return 0;
	}
	
	@Override
	protected List<RelativeLayout> getRelativeLayoutListContent() {
		final FragmentActivity currentActivity = this.getActivity();
		final int expectedSize = 5;
		final List<RelativeLayout>content = new ArrayList<RelativeLayout>(expectedSize);
		
		content.add(getFromListItem(currentActivity));
		content.add(getToListItem(currentActivity));
		content.add(getAmountListItem(currentActivity));
		content.add(getFrequencyListItem(currentActivity));
		content.add(getSendOnListItem(currentActivity));

		return content;
	}
	
	private BankEditDetail getFromListItem(final Activity currentActivity) {
		final BankEditDetail fromListItem = new BankEditDetail(currentActivity);
		fromListItem.getDividerLine().setVisibility(View.GONE);
		fromListItem.getEditableField().setVisibility(View.GONE);
		fromListItem.getTopLabel().setText(R.string.from);
		fromListItem.getMiddleLabel().setText(R.string.select_account);
		fromListItem.getView().setOnFocusChangeListener(null);
		fromListItem.getView().setOnClickListener(null);
		fromListItem.getErrorLabel().setVisibility(View.GONE);
		return fromListItem;
	}
	
	private BankEditDetail getToListItem(final Activity currentActivity) {
		final BankEditDetail toListItem = getFromListItem(currentActivity);
		toListItem.getDividerLine().setVisibility(View.VISIBLE);
		toListItem.getTopLabel().setText(R.string.to);
		
		return toListItem;
	}
	
	private AmountListItem getAmountListItem(final Activity currentActivity) {
		final AmountListItem amountListItem = new AmountListItem(currentActivity);

		return amountListItem;	
	}
	
	private BankEditDetail getFrequencyListItem(final Activity currentActivity) {
		final BankEditDetail frequencyListItem = new BankEditDetail(currentActivity);
		frequencyListItem.getTopLabel().setText(R.string.frequency);
		frequencyListItem.getMiddleLabel().setText(R.string.one_time);
		frequencyListItem.getView().setOnFocusChangeListener(null);
		frequencyListItem.getView().setOnClickListener(null);
		frequencyListItem.getEditableField().setVisibility(View.GONE);
		frequencyListItem.getErrorLabel().setVisibility(View.GONE);
		return frequencyListItem;
	}
	
	private BankEditDetail getSendOnListItem(final Activity currentActivity) {
		final BankEditDetail sendOnListItem = new BankEditDetail(currentActivity);
		
		sendOnListItem.getTopLabel().setText(R.string.send_on);
		sendOnListItem.getMiddleLabel().setText(R.string.select_a_date);
		sendOnListItem.getView().setOnClickListener(null);
		sendOnListItem.getView().setOnFocusChangeListener(null);
		sendOnListItem.getEditableField().setVisibility(View.GONE);
		sendOnListItem.getErrorLabel().setVisibility(View.GONE);
		return sendOnListItem;
	}

}
