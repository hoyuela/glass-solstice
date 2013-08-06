package com.discover.mobile.bank.ui;

import java.util.List;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.discover.mobile.bank.R;
import com.discover.mobile.bank.services.account.Account;
import com.discover.mobile.common.utils.StringUtility;

public class AccountAdapter extends ArrayAdapter<Account> {

	private final Activity context;
	private List<Account> data = null;

	public AccountAdapter(final Activity context, final int textViewResourceId,
			final List<Account> accounts) {
		super(context, textViewResourceId, accounts);
		this.context = context;
		this.data = accounts;
	}

	@Override
	public View getView(final int position, final View convertView, final ViewGroup parent) {
		return super.getView(position, convertView, parent);
	}

	@Override
	public View getDropDownView(final int position, final View convertView, final ViewGroup parent) {
		View row = convertView;
		
		if (row == null) {
			// Inflate default dropdown background
			final LayoutInflater inflater = context.getLayoutInflater();
			row = inflater.inflate(R.layout.common_balance_dropdown_item, parent, false);
		}

		final Account item = data.get(position);

		if (item != null) {
			final TextView accountName = (TextView) row.findViewById(R.id.common_dropdown_item_title);
			final TextView accountNumber = (TextView) row.findViewById(R.id.common_dropdown_item_subtitle);
			final TextView balance = (TextView)row.findViewById(R.id.common_dropdown_balance_label);
			final String accountNumberPrefix = context.getString(R.string.schedule_pay_spinner_body);

			// Display Account name
			if (accountName != null && item.nickname != null) {
				accountName.setText(item.nickname);
			}
			
			// Display Account number
			if (accountNumber != null && item.accountNumber != null && !item.accountNumber.ending.isEmpty()) {
				String accountNumberEnding = accountNumberPrefix + StringUtility.SPACE + item.accountNumber.ending; 
				accountNumber.setText(accountNumberEnding);
			}
			
			if (balance != null && item.balance != null) {
				balance.setText(item.balance.formatted);
				final int color = (item.balance.value < 0) ? R.color.error_indicator : R.color.field_copy;
				balance.setTextColor(context.getResources().getColor(color));
			}
		}

		return row;
	}
}