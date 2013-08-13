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


	final String CHANGE_URL_INTENT = "com.discover.mobile.CHANGE_URL_BROADCAST_INTENT";
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
			sites.toString();
			//
			//			for(int url = 0; url < sites.size()-1; url++){
			//				final Intent temp = new Intent(CHANGE_URL_INTENT);
			//				temp.putExtra(NEW_BASE_URL, sites.get(url).link);
			//				views.setTextViewText(ids[url], sites.get(url).title);
			//				views.setOnClickFillInIntent(ids[url], temp);
			//			}
			//
			//			final Intent clickIntent = new Intent(CHANGE_URL_INTENT);
			//			clickIntent.putExtra(NEW_BASE_URL, "dsafsdf");
			//			views.setTextViewText(ids[sites.size()-1], sites.get(sites.size()-1).title);
			//			views.setOnClickFillInIntent(ids[sites.size()-1], clickIntent);

			//views.setOnClickPendingIntent(R.id.button, pendingIntent);
			views.setOnClickPendingIntent(R.id.manage_urls, PendingIntent.getActivity(context, 0, intent, 0));
			// Tell the AppWidgetManager to perform an update on the current app widget
			appWidgetManager.updateAppWidget(appWidgetId, views);
		}

		super.onUpdate(context, appWidgetManager, appWidgetIds);
	}
}
