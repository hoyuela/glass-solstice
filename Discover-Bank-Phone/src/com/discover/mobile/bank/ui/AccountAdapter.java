package com.discover.mobile.bank.ui;

import java.util.ArrayList;
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

	private Activity context;
	List<Account> data = null;

	public AccountAdapter(Activity context, int textViewResourceId,
			List<Account> accounts) {
		super(context, textViewResourceId, accounts);
		this.context = context;
		this.data = accounts;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		return super.getView(position, convertView, parent);
	}

	@Override
	public View getDropDownView(int position, View convertView, ViewGroup parent) {
		View row = convertView;
		if (row == null) {
			LayoutInflater inflater = context.getLayoutInflater();
			row = inflater
					.inflate(R.layout.spinner_account_view, parent, false);
		}

		Account item = data.get(position);

		if (item != null) {
			TextView accountNumber = (TextView) row
					.findViewById(R.id.account_number_list);
			TextView accountName = (TextView) row
					.findViewById(R.id.account_name_list);
			String accountNumberPrefix = context
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