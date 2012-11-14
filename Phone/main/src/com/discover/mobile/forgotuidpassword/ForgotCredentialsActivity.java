package com.discover.mobile.forgotuidpassword;

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
import com.discover.mobile.login.register.ForgotBothAccountInformationActivity;
import com.discover.mobile.login.register.ForgotPasswordAccountInformationActivity;

@ContentView(R.layout.forgot_login)
public class ForgotCredentialsActivity extends RoboListActivity {
	
	private static final String TAG = ForgotCredentialsActivity.class.getSimpleName();
	
	private final ArrayAdapter<Option> optionAdapter = new ArrayAdapter<Option>(this,
			R.layout.single_list_item_with_disclosure_indicator, R.id.tv, Option.values());
	
	@Override
	public void onCreate(final Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		
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
