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
			final LayoutInflater inflater = context.getLayoutInflater();
			row = inflater
					.inflate(R.layout.spinner_account_view, parent, false);
		}

		final Account item = data.get(position);

		if (item != null ) {
			final TextView accountNumber = (TextView) row
					.findViewById(R.id.account_number_list);
			final TextView accountName = (TextView) row
					.findViewById(R.id.account_name_list);
			final String accountNumberPrefix = context
					.getString(R.string.schedule_pay_spinner_body);

			if (accountNumber != null && item.accountNumber != null
					&& !item.accountNumber.ending.equals("")) {
				accountNumber.setText(accountNumberPrefix + " "
						+ item.accountNumber.ending);
			}
			if (accountName != null && item.nickname != null) {
				accountName.setText(item.nickname);
			}
		}

		return row;
	}
}