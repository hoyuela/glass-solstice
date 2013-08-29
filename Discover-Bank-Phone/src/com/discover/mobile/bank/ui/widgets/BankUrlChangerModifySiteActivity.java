package com.discover.mobile.bank.ui.widgets;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.discover.mobile.bank.R;

public class BankUrlChangerModifySiteActivity extends Activity{

	private TextView titleField;

	private TextView urlField;

	@Override
	protected void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.bank_url_site_add_entry_layout);

		titleField = (TextView) findViewById(R.id.name);
		urlField = (TextView) findViewById(R.id.url);

		final Bundle bundle = getIntent().getExtras();
		if(null != bundle){
			final String text = bundle.getString(BankUrlSite.TITLE);
			final String link = bundle.getString(BankUrlSite.LINK);
			titleField.setText(text);
			urlField.setText(link);
		}
	}

	public void close(final View v){
		finish();
	}

	public void addUrl(final View v){
		Toast.makeText(this, "Adding URL", Toast.LENGTH_SHORT).show();
	}
}
