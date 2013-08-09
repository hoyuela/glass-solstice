package com.discover.mobile.bank.ui.widgets;

import java.util.List;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;

import com.discover.mobile.bank.R;
import com.discover.mobile.bank.login.LoginActivity;

public class BankUrlChangerWidget extends AppWidgetProvider{


	private static final String NEW_BASE_URL = "NEW_BASE_URL";
	final String CHANGE_URL_INTENT = "com.discover.mobile.CHANGE_URL_BROADCAST_INTENT";
	private final int[] ids = new int[]{R.id.url0, R.id.url1, R.id.url2, R.id.url3, R.id.url4, R.id.url5, R.id.url6, R.id.url7};
	private List<BankUrlSite> sites;
	@Override
	public void onUpdate(final Context context, final AppWidgetManager appWidgetManager, final int[] appWidgetIds) {
		final int N = appWidgetIds.length;
		// Perform this loop procedure for each App Widget that belongs to this provider
		for (int i=0; i<N; i++) {
			final int appWidgetId = appWidgetIds[i];

			// Create an Intent to launch ExampleActivity
			final Intent intent = new Intent(context, LoginActivity.class);


			// Get the layout for the App Widget and attach an on-click listener
			// to the button
			final RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.bank_url_changer_widget_layout);

			sites = BankUrlChangerPreferences.getSites();

			for(int url = 0; url < sites.size()-1; url++){
				final Intent clickIntent = new Intent(CHANGE_URL_INTENT);
				clickIntent.putExtra(NEW_BASE_URL, sites.get(url).link);
				views.setTextViewText(ids[url], sites.get(url).title);
				views.setOnClickPendingIntent(ids[url], PendingIntent.getBroadcast(context, 0, clickIntent, 0));
			}


			final Intent clickIntent = new Intent(CHANGE_URL_INTENT);
			clickIntent.putExtra(NEW_BASE_URL, sites.get(sites.size()-1).link);
			views.setTextViewText(ids[sites.size()-1], sites.get(sites.size()-1).title);
			views.setOnClickPendingIntent(ids[sites.size()-1], PendingIntent.getBroadcast(context, 0, clickIntent, 0));

			//views.setOnClickPendingIntent(R.id.button, pendingIntent);
			views.setOnClickPendingIntent(R.id.manage_urls, PendingIntent.getActivity(context, 0, intent, 0));
			// Tell the AppWidgetManager to perform an update on the current app widget
			appWidgetManager.updateAppWidget(appWidgetId, views);
		}
	}
}
