package com.discover.mobile.bank.ui.widgets;

import java.util.List;

import android.app.Activity;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ListView;

import com.discover.mobile.bank.R;

/**
 * Activity that is launched when the home screen widget manage URL button is clicked
 * @author jthornton
 *
 */
public class BankUrlChangerActivity extends Activity implements OnClickListener{

	/**Adapter of used to display the list items*/
	private BankUrlChangerAdapter adapter;

	/**Buttons in the layout*/
	private Button button1, button2;

	/**Strings used to set the text of buttons*/
	private String add, delete, cancel;

	@Override
	protected void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.bank_url_changer_activity);

		final Resources res = getResources();
		add = res.getString(R.string.bank_url_changer_add);
		delete = res.getString(R.string.bank_url_changer_delete);
		cancel = res.getString(R.string.bank_url_changer_cancel);

		adapter = new BankUrlChangerAdapter(this, BankUrlChangerPreferences.getSites(this));
		final ListView listView = (ListView) findViewById(R.id.url);
		button2 = (Button) findViewById(R.id.delete);
		button1 = (Button) findViewById(R.id.add);

		button1.setOnClickListener(this);
		button2.setOnClickListener(this);

		listView.setDivider(getResources().getDrawable(R.drawable.gray_gradient_square));
		listView.setAdapter(adapter);
	}

	@Override
	public void onResume(){
		super.onResume();

		adapter.setData(BankUrlChangerPreferences.getSites(this));
		adapter.notifyDataSetChanged();
	}

	/**
	 * Click listener triggered when one of the buttons is clicked
	 * @param v - button clicked
	 */
	@Override
	public void onClick(final View v) {
		final Button clicked = (Button) v;
		if(clicked.getText().equals(delete) && v.getId() == button2.getId()){
			showListDelete();
		}else if(clicked.getText().equals(add)){
			final Intent newActivity = new Intent(this, BankUrlChangerModifySiteActivity.class);
			startActivity(newActivity);
		}else if(clicked.getText().equals(delete) && v.getId() == button1.getId()){
			final List<BankUrlSite> sites = adapter.getDeleteSites();
			for(final BankUrlSite site : sites){
				BankUrlChangerPreferences.deleteSite(this, site);
			}
			adapter.setData(BankUrlChangerPreferences.getSites(this));
			resetLayout();

			final Intent intent = new Intent(getApplicationContext(), BankUrlChangerWidget.class);
			intent.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
			final int[] ids = AppWidgetManager.getInstance(getApplicationContext())
					.getAppWidgetIds(new ComponentName(getApplicationContext(), BankUrlChangerWidget.class));
			intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS,ids);
			getApplicationContext().sendBroadcast(intent);
		}else if(clicked.getText().equals(cancel)){
			resetLayout();
		}

	}

	/**
	 * Setup the layout to be the way it was when it started
	 */
	private void resetLayout() {
		adapter.setDeleting(false);
		adapter.notifyDataSetChanged();
		button1.setText(add);
		button2.setText(delete);
	}

	/**
	 * Set up the activity for items to be deleted
	 */
	private void showListDelete() {
		adapter.setDeleting(true);
		adapter.notifyDataSetChanged();
		button1.setText(delete);
		button2.setText(cancel);
	}
}
