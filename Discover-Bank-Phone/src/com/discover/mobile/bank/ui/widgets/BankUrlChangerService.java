package com.discover.mobile.bank.ui.widgets;

import java.util.List;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import com.discover.mobile.bank.R;

/**
 * Service used to be associated with the remote views
 * @author jthornton
 *
 */
@SuppressLint("NewApi")
public class BankUrlChangerService extends RemoteViewsService{

	/**String used get the new url from the intent when the receiver gets the intent*/
	public static final String NEW_BASE_URL = "NEW_BASE_URL";

	/**String used to send the intent  to the url changer*/
	public static final String CHANGE_URL_INTENT = "com.discover.mobile.CHANGE_URL_BROADCAST_INTENT";

	/**
	 * Called when the widget needs to populate itself
	 * @param intent - intent used to launch the widget
	 */
	@Override
	public RemoteViewsFactory onGetViewFactory(final Intent intent) {
		return new BankUrlChangerViewsFactory(getApplicationContext(), BankUrlChangerPreferences.getSites());
	}

	/**
	 * Factory used to create the remote views that will display in the home screen widget
	 * 
	 * @author jthornton
	 *
	 */
	private class BankUrlChangerViewsFactory implements RemoteViewsFactory{

		/**List of sites that will populate the widget*/
		private final List<BankUrlSite> sites;

		/**Context used to display the data*/
		private final Context context;

		/**
		 * Constrtor for the class
		 * @param context - context used to create the widget
		 * @param sites - list of sites that will be displayed
		 */
		public BankUrlChangerViewsFactory(final Context context, final List<BankUrlSite> sites){
			this.sites = sites;
			this.context = context;
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
			final RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.bank_url_changer_list_item);
			final BankUrlSite site = sites.get(position);
			if(null != site){
				views.setTextViewText(R.id.title, site.title);
				views.setTextViewText(R.id.link, site.link);
				final Intent intent = new Intent(CHANGE_URL_INTENT);
				intent.putExtra(NEW_BASE_URL, site.link);
				views.setOnClickFillInIntent(R.id.bank_url_layout, intent);
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
