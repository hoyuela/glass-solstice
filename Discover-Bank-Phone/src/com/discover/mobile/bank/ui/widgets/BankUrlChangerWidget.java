package com.discover.mobile.bank.ui.widgets;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.widget.RemoteViews;

import com.discover.mobile.bank.R;

/**
 * URL changer widget.  This is the widget that displays on the home screen.
 * @author jthornton
 *
 */
public class BankUrlChangerWidget extends AppWidgetProvider{

	@Override
	public void onUpdate(final Context context, final AppWidgetManager appWidgetManager, final int[] appWidgetIds) {
		final int n = appWidgetIds.length;

		for (int i=0; i<n; i++) {

			//Create an intent to hook up the service so the list can be populated
			final Intent intent = new Intent(context, BankUrlChangerService.class);
			intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetIds[i]);
			intent.setData(Uri.parse(intent.toUri(Intent.URI_INTENT_SCHEME)));

			//Get the remote views and  set up the list
			final RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.bank_url_changer_widget_layout);
			views.setRemoteAdapter(R.id.url_list, intent);

			//Set up a fill in intent that will be used to mock up a click on the list items
			final Intent clickIntent = new Intent(BankUrlChangerService.CHANGE_URL_INTENT);
			views.setPendingIntentTemplate(R.id.url_list, 
					PendingIntent.getBroadcast(context, 0, clickIntent, PendingIntent.FLAG_UPDATE_CURRENT));

			//Set up the intent to launch the manage activity
			final Intent manageIntent = new Intent(context, BankUrlChangerActivity.class);
			views.setOnClickPendingIntent(R.id.manage_urls, 
					PendingIntent.getActivity(context, 0, manageIntent, PendingIntent.FLAG_UPDATE_CURRENT));

			appWidgetManager.updateAppWidget(appWidgetIds[i], views);
		}

		super.onUpdate(context, appWidgetManager, appWidgetIds);
	}
}
