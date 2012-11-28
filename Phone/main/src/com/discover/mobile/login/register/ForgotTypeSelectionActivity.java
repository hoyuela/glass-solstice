package com.discover.mobile.login.register;

import roboguice.activity.RoboListActivity;
import roboguice.inject.ContentView;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.discover.mobile.R;
import com.discover.mobile.common.analytics.AnalyticsPage;
import com.discover.mobile.common.analytics.TrackingHelper;

@ContentView(R.layout.register_forgot_type_select)
public class ForgotTypeSelectionActivity extends RoboListActivity {
	
	private ArrayAdapter<Option> optionAdapter;

	@Override
	public void onCreate(final Bundle savedInstanceState){
		super.onCreate(savedInstanceState);

		optionAdapter = new ArrayAdapter<Option>(this, R.layout.register_forgot_type_select_option_item, R.id.tv,
				Option.values());
		
		TrackingHelper.trackPageView(AnalyticsPage.FORGOT_PASSWORD_MENU);
	}
	
	/**
	 * Using onResume so that if a user presses the back button
	 * the list fields will get reset instead of staying blue.
	 */
	@Override
	public void onResume(){
		super.onResume();
		
		setListAdapter(optionAdapter);
	}

	@Override
	protected void onListItemClick(final ListView l, final View v, final int position, final long id) {
		final Option selection = optionAdapter.getItem(position);
		final Class<?> intentClass = selection.getIntentClass();
		startActivity(new Intent(this, intentClass));
	}
	
	private static enum Option {
		USER_ID("Forgot User ID", ForgotUserIdActivity.class),
		PASSWORD("Forgot Password", ForgotPasswordAccountInformationActivity.class),
		BOTH("Forgot Both", ForgotBothAccountInformationActivity.class);
		
		private final String ITEM_TEXT;
		private final Class<?> INTENT_CLASS;
		
		Option(final String itemText, final Class<? extends Activity> intentClass) {
			ITEM_TEXT = itemText;
			INTENT_CLASS = intentClass;
		}
		
		@Override
		public String toString() {
			return ITEM_TEXT;
		}
		
		private Class<?> getIntentClass() {
			return INTENT_CLASS;
		}
	}
	
}
