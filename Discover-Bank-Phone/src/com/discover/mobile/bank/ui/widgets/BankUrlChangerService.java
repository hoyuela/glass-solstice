package com.discover.mobile.bank.ui.widgets;

import java.util.List;

import android.annotation.SuppressLint;
import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import com.discover.mobile.bank.R;

@SuppressLint("NewApi")
public class BankUrlChangerService extends RemoteViewsService{

	@Override
	public RemoteViewsFactory onGetViewFactory(final Intent intent) {
		return new BankUrlChangerViewsFactory(getApplicationContext(), BankUrlChangerPreferences.getSites(), intent);
	}

	private class BankUrlChangerViewsFactory implements RemoteViewsFactory{

		private final List<BankUrlSite> sites;

		private final Context context;

		private final int appWidgetId;

		public BankUrlChangerViewsFactory(final Context context, final List<BankUrlSite> sites, final Intent intent){
			this.sites = sites;
			this.context = context;
			appWidgetId = intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID);
		}

		@Override
		public int getCount() {
			return sites.size();
		}

		@Override
		public long getItemId(final int position) {
			return position;
		}

		@Override
		public RemoteViews getLoadingView() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public RemoteViews getViewAt(final int position) {

			final String NEW_BASE_URL = "NEW_BASE_URL";
			final String CHANGE_URL_INTENT = "com.discover.mobile.CHANGE_URL_BROADCAST_INTENT";
			final RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.bank_url_changer_list_item);
			final BankUrlSite site = sites.get(position);
			if(null != site){
				views.setTextViewText(R.id.url_name, site.title);
				final Intent intent = new Intent(CHANGE_URL_INTENT);
				intent.putExtra(NEW_BASE_URL, site.link);
				views.setOnClickFillInIntent(R.id.url_name, intent);
			}
			return views;
		}

		@Override
		public int getViewTypeCount() {
			return 1;
		}

		@Override
		public boolean hasStableIds() {
			return true;
		}

		@Override
		public void onCreate() {
			// Do nothing
		}

		@Override
		public void onDataSetChanged() {
			// Do nothing
		}

		@Override
		public void onDestroy() {
			// Do nothing
		}

	}

}
