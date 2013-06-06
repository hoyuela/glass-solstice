
/* 13.3  Changes */

package com.discover.mobile.card.whatsnew;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.LinearLayout;

import com.discover.mobile.card.R;
import com.discover.mobile.card.common.sharedata.CardShareDataStore;
import com.discover.mobile.card.common.utils.Utils;
import com.discover.mobile.card.navigation.CardNavigationRootActivity;
import com.discover.mobile.card.services.auth.AccountDetails;

public class WhatsNewActivity extends Activity implements WhatsNewConstants{
	/** Called when the activity is first created. */
	LinearLayout layout;
	public Bundle extras;
	public int noOfPages=0;
	public String cardType=null;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.whats_new_view_pager);
		
		cardType = getCardType();

		extras = getIntent().getExtras();
		CirclePageIndicator mIndicator = (CirclePageIndicator) findViewById(R.id.indicator);
		mIndicator.setBackgroundResource(R.drawable.light_gray_bkgrd);
		ViewPager viewPager = (ViewPager) findViewById(R.id.pager);
		MyPagerAdapter adapter = new MyPagerAdapter(this);
		viewPager.setAdapter(adapter);
		 // FIX: for not showing page Indicator when there is only one page
		if(noOfPages>1){
			mIndicator.setViewPager(viewPager);
		}

		Button closeButton = (Button) findViewById(R.id.whats_new_close_button);
		closeButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				//This method has written generically
				navigateToRoot();
			}
		});
		Utils.isSpinnerShow = true;
		Utils.hideSpinner();
	}
	
	/**
	 * This method returns us the current card Type.
	 * @return String Card Type.
	 */
	private String getCardType() {
		
		Context context = getApplicationContext();
		String accType=null;
		CardShareDataStore dataStore = CardShareDataStore.getInstance(context);
		AccountDetails accData = (AccountDetails) dataStore
				.getValueOfAppCache(getResources().getString(
						R.string.account_details));
		
		if (null != accData) {
			accType = accData.incentiveTypeCode;
		}
		return accType;
	}

	/**
	 * This method navigates control to root activity after login.
	 */
	public void navigateToRoot(){
		WhatsNewActivity act = WhatsNewActivity.this;
		act.finish();
		Intent intent = new Intent(WhatsNewActivity.this,
				CardNavigationRootActivity.class);
		intent.putExtras(act.extras);
		WhatsNewActivity.this.startActivity(intent);
	}
	
	@Override
	public void onBackPressed() {
		//This method has written generically
		navigateToRoot();
	}

	private class MyPagerAdapter extends PagerAdapter {

		private ArrayList<View> views;

		public MyPagerAdapter(Context context) {
			views = new ArrayList<View>();
			views.add(new WhatsNewAvailable(context, QUICK_VIEW_INFO,cardType));
			noOfPages = getCount();
		}

		@Override
		public void destroyItem(View view, int arg1, Object object) {
			((ViewPager) view).removeView((LinearLayout) object);
		}

		@Override
		public void finishUpdate(View arg0) {

		}

		@Override
		public int getCount() {
			return views.size();
		}

		@Override
		public Object instantiateItem(View view, int position) {
			View myView = views.get(position);
			((ViewPager) view).addView(myView);
			return myView;
		}

		@Override
		public boolean isViewFromObject(View view, Object object) {
			return view == object;
		}

		@Override
		public void restoreState(Parcelable arg0, ClassLoader arg1) {

		}

		@Override
		public Parcelable saveState() {
			return null;
		}

		@Override
		public void startUpdate(View arg0) {

		}
	}
}