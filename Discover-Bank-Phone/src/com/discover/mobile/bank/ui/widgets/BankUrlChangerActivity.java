package com.discover.mobile.bank.ui.widgets;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ListView;

import com.discover.mobile.bank.R;

public class BankUrlChangerActivity extends Activity implements OnClickListener{

	private BankUrlChangerAdapter adapter;

	private Button button1;

	private Button button2;

	@Override
	protected void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.bank_url_changer_activity);

		adapter = new BankUrlChangerAdapter(this, BankUrlChangerPreferences.getSites());
		final ListView listView = (ListView) findViewById(R.id.urlSiteList);
		button2 = (Button) findViewById(R.id.delete);
		button1 = (Button) findViewById(R.id.addUrlButton);

		button1.setOnClickListener(this);
		button2.setOnClickListener(this);

		listView.setDivider(getResources().getDrawable(R.drawable.gray_gradient_square));
		listView.setAdapter(adapter);
	}

	@Override
	public void onClick(final View v) {
		final Button clicked = (Button) v;
		if(clicked.getText().equals("Delete")){
			adapter.setDeleting(true);
			adapter.notifyDataSetChanged();
			button1.setText("Delete");
			button2.setText("Cancel");
		}else if(clicked.getText().equals("Add")){
			final Intent add = new Intent(this, BankUrlChangerModifySiteActivity.class);
			startActivity(add);
		}else if(clicked.getText().equals("Delete")){

		}else if(clicked.getText().equals("Cancel")){
			adapter.setDeleting(false);
			adapter.notifyDataSetChanged();
			button1.setText("Add");
			button2.setText("Delete");
		}

	}
}
