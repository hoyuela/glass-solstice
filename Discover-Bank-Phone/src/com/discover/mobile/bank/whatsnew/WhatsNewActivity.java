package com.discover.mobile.bank.whatsnew;

import java.util.ArrayList;
import java.util.List;

import android.content.res.Resources;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.discover.mobile.bank.R;
import com.discover.mobile.bank.framework.BankConductor;
import com.discover.mobile.common.DiscoverActivityManager;

/**
 * Activity for the what's new content.  This will be shown on the first time a user logs into the application.
 * After that, the user will not see this page again. This is meant to be reusable for future releases.
 * 
 * To updated this go to the strings_bank_whats_new.xml file and update the following:
 * 
 * bank_whats_new_drawables - Array containing the images to be displayed in the adapter.  The top
 * 							  reference is the left most image.
 * bank_whats_new_pages - Number of pages that will be displayed.  This should match the number of
 * 						  entries in the drawable array.
 * 
 * @author jthornton
 *
 */
public class WhatsNewActivity extends FragmentActivity{

	/**Key to get the location of the view pager out of the bundle*/
	private static final String PAGER_LOCATION = "location";

	/**View pager holding the content that is displayed to the user*/
	private ViewPager mPager;

	/**List of ImageViews that are used to indicate what page the user is on*/
	private List<ImageView> indicatorsList;

	/**The currently selected ImageView*/
	private ImageView selected;

	/**Number of pages that will be displayed in the view pager*/
	private int numPages;

	/**The pager adapter, which provides the pages to the view pager widget.*/
	private WhatsNewViewPagerAdapter mPagerAdapter;

	/**
	 * Create the activity. 
	 * @param savedInstanceState - saved state of the bundle
	 */
	@Override
	protected void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.bank_whats_new_content_view);
		final ImageButton button = (ImageButton) findViewById(R.id.close);
		numPages = getResources().getInteger(R.integer.bank_whats_new_pages);
		int currentIndex = 0;
		mPager = (ViewPager) findViewById(R.id.pager);


		mPagerAdapter = new WhatsNewViewPagerAdapter(this, getSupportFragmentManager());
		mPager.setAdapter(mPagerAdapter);

		button.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(final View v) {
				BankConductor.navigateToHomePage();
			}
		});

		if(null != savedInstanceState){
			currentIndex = savedInstanceState.getInt(PAGER_LOCATION, 0);
		}

		setUpIndicators(numPages, currentIndex);
	}

	/**
	 * Set up the indicators at the bottom of the what's new screen
	 * @param numPages - number of pages that the view pager has
	 * @param selectedIndex - the selected index of the view pager
	 */
	private void setUpIndicators(final int numPages, final int selectedIndex){
		final Resources res = getResources();
		final LinearLayout indicators = (LinearLayout) findViewById(R.id.page_indicators);
		final int margin = res.getDimensionPixelSize(R.dimen.bank_gray_circle_margin);
		final int imageSize = res.getDimensionPixelSize(R.dimen.bank_gray_circle_size);
		indicatorsList = new ArrayList<ImageView>();
		for(int i = 0; i < numPages; i++){
			final ImageView image = new ImageView(this);
			final LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(imageSize, imageSize);
			params.setMargins(margin, 0, margin, 0);
			if(selectedIndex == numPages - 1 - i){
				selected = image;
				image.setBackgroundResource(R.drawable.bank_dark_gray_circle);
			}else{
				image.setBackgroundResource(R.drawable.bank_light_gray_circle);
			}
			indicators.addView(image, params);
			indicatorsList.add(image);
		}
	}

	/**
	 * Resume the activity.
	 */
	@Override
	public void onResume(){
		super.onResume();

		//Set this activity as the active activity
		DiscoverActivityManager.setActiveActivity(this);

		/**
		 * Set the on page change listener.  This needs to be done here
		 * so that when the view pager resumes its state it won't call the
		 * onPageSelected method (which causes improper highlighting).
		 */
		mPager.setOnPageChangeListener(new OnPageChangeListener(){

			@Override
			public void onPageScrollStateChanged(final int arg0) {}

			@Override
			public void onPageScrolled(final int arg0, final float arg1, final int arg2) {}

			@Override
			public void onPageSelected(final int pageNumber) {
				indicatorsList.get(numPages - pageNumber - 1).setBackgroundResource(R.drawable.bank_dark_gray_circle);
				selected.setBackgroundResource(R.drawable.bank_light_gray_circle);
				selected = indicatorsList.get(numPages - 1 - pageNumber);
			}
		});
	}

	/**
	 * Save the state of the activity.
	 * @param outState - bundle to save the state the activity
	 */
	@Override
	public void onSaveInstanceState(final Bundle outState){
		outState.putInt(PAGER_LOCATION, mPager.getCurrentItem());
		super.onSaveInstanceState(outState);
	}

	/**
	 * Handle when the back button is pressed.
	 */
	@Override
	public void onBackPressed() {
		BankConductor.navigateToHomePage();
	}
}
