package com.discover.mobile.bank.ui.widgets;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.widget.RemoteViews;
import android.widget.Toast;

import com.discover.mobile.bank.R;

public class BankUrlChangerWidget extends AppWidgetProvider{


	final String CHANGE_URL_INTENT = "com.discover.mobile.CHANGE_URL_BROADCAST_INTENT";

	@Override
	public void onUpdate(final Context context, final AppWidgetManager appWidgetManager, final int[] appWidgetIds) {
		final int n = appWidgetIds.length;
		// Perform this loop procedure for each App Widget that belongs to this provider
		for (int i=0; i<n; i++) {

			final Intent intent = new Intent(context, BankUrlChangerService.class);
			intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetIds[i]);
			intent.setData(Uri.parse(intent.toUri(Intent.URI_INTENT_SCHEME)));

			final RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.bank_url_changer_widget_layout);
			views.setRemoteAdapter(R.id.url_list, intent);

			final Intent clickIntent = new Intent(CHANGE_URL_INTENT);

			views.setPendingIntentTemplate(R.id.url_list, 
					PendingIntent.getBroadcast(context, 0, clickIntent, PendingIntent.FLAG_UPDATE_CURRENT));


			appWidgetManager.updateAppWidget(appWidgetIds[i], views);
		}

		super.onUpdate(context, appWidgetManager, appWidgetIds);
	}

	@Override
	public void onReceive(final Context context, final Intent intent) {
		final String NEW_BASE_URL = "NEW_BASE_URL";

		/**Read new base url from bundle*/
		final String newUrl = intent.getStringExtra(NEW_BASE_URL);

		Toast.makeText(context, newUrl, Toast.LENGTH_SHORT).show();
		super.onReceive(context, intent);
	}
}
