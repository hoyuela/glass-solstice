package com.discover.mobile.bank.ui.widgets;

import android.app.Activity;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.discover.mobile.bank.R;
import com.google.common.base.Strings;

/**
 * Activity used to modify/create a new site;
 * @author jthornton
 *
 */
public class BankUrlChangerModifySiteActivity extends Activity{

	/**Field containing the name of the site*/
	private TextView titleField;

	/**Filed containing the url of the site*/
	private TextView urlField;

	private BankUrlSite site;

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
			final int urlNumber = bundle.getInt(BankUrlSite.URL_NUMBER);
			titleField.setText(text);
			urlField.setText(link);
			site = new BankUrlSite(link, text, urlNumber, true);
		}
	}

	/**
	 * Close this page (called from xml)
	 * @param v - view calling this method
	 */
	public void close(final View v){
		finish();
	}

	/**
	 * Add this page (called from xml)
	 * @param v - view calling this method
	 */
	public void addUrl(final View v){
		final String title = titleField.getText().toString();
		final String url = urlField.getText().toString();
		if(!Strings.isNullOrEmpty(title) && !Strings.isNullOrEmpty(url)){
			if(null == site){
				BankUrlChangerPreferences.addSite(this, new BankUrlSite(url, title, true));
			}else{
				BankUrlChangerPreferences.editSite(this, new BankUrlSite(url, title, site.urlNumber, true), site);
			}
			Toast.makeText(this, "Your site has been added.", Toast.LENGTH_SHORT).show();
			final Intent intent = new Intent(getApplicationContext(), BankUrlChangerWidget.class);
			intent.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
			final int[] ids = AppWidgetManager.getInstance(getApplicationContext())
					.getAppWidgetIds(new ComponentName(getApplicationContext(), BankUrlChangerWidget.class));
			intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS,ids);
			getApplicationContext().sendBroadcast(intent);

			close(v);
		}
	}
}
