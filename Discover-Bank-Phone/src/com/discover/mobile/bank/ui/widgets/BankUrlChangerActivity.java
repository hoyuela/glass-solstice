package com.discover.mobile.bank.ui.widgets;

import android.app.Activity;
import android.os.Bundle;
import android.widget.ListView;

import com.discover.mobile.bank.R;

public class BankUrlChangerActivity extends Activity{

	@Override
	protected void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.bank_url_changer_activity);

		final BankUrlChangerAdapter adapter = new BankUrlChangerAdapter(this, BankUrlChangerPreferences.getSites());
		final ListView listView = (ListView) findViewById(R.id.urlSiteList);
		listView.setAdapter(adapter);

	}
}
