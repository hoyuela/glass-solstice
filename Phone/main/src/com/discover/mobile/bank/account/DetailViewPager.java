package com.discover.mobile.bank.account;

import java.util.List;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.discover.mobile.BaseFragment;
import com.discover.mobile.R;
import com.discover.mobile.bank.account.ViewPagerFragmentFactory.ActivityItem;
import com.slidingmenu.lib.SlidingMenu;
import com.slidingmenu.lib.app.SlidingFragmentActivity;

/**
 * The detail view pager is responsible for presenting a set of data in a swipe-able detail view.
 * This class accepts a list of Activity Items that can be presented in detail view. A detail view
 * adapter needs to exist for each ActivityItem type to be displayed properly.
 * 
 * @author scottseward
 *
 */
public abstract class DetailViewPager extends BaseFragment {
	private final String TAG = DetailViewPager.class.getSimpleName();
	
	private int ORIGINAL_TOUCH_MODE;
	
	private ViewPager viewPager;
	private ViewPagerFragmentAdapter viewPagerAdapter;
	
	private TextView titleLabel;
	
	private Button previousViewButton;
	private Button nextViewButton;
	
	private SlidingMenu slidingMenu;
	
	@Override
	public abstract int getActionBarTitle();
	/**
	 * Returns a fully constructed Fragment ready to be shown in the ViewPager
	 * @return a Fragment ready to be displayed in the ViewPager.
	 */
	protected abstract Fragment getDetailItem(final int position);
	/**
	 * Returns the number of views that can be presented by the ViewPager
	 * @return the number of views that can be presented by the ViewPager.
	 */
	protected abstract int getViewCount();
	protected abstract List<ActivityItem> getDataSet();
	
	/**
	 * Can be overridden by a subclass to set the initial view position of the ViewPager.
	 * @return the position in the data set that should be presented first.
	 */
	protected int getInitialViewPosition(){
		return 0;
	};

	@Override
	public View onCreateView(final LayoutInflater inflater, final ViewGroup container, final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		final View mainView = inflater.inflate(R.layout.account_item_detail_view, null);
		
		loadAllViewsFrom(mainView);		
		setupClickListeners();
		setupViewPager();

		return mainView; 
	}
	
	/**
	 * Reset the touch mode of the sliding menu so that it will 
	 */
	@Override
	public void onPause() {
		super.onPause();
		//Restore the original touch mode for the sliding menu when this fragment is paused.
		slidingMenu.setTouchModeAbove(ORIGINAL_TOUCH_MODE);
	}
	
	public void updateTitleLabel(final int titleTextResource) {
		titleLabel.setText(titleTextResource);
	}
	
	/**
	 * Get all of the views in the layout that we will need to have access to.
	 * @param mainView the inflated layout that contians views that we want to access.
	 */
	private void loadAllViewsFrom(final View mainView) {
		previousViewButton = (Button)mainView.findViewById(R.id.previous_button);
		nextViewButton = (Button)mainView.findViewById(R.id.next_button);
		viewPager = (ViewPager)mainView.findViewById(R.id.view_pager);
		slidingMenu = ((SlidingFragmentActivity)this.getActivity()).getSlidingMenu();
		titleLabel = (TextView)mainView.findViewById(R.id.title);
	}
	
	/**
	 * Setup the ViewPager to accept a collection of fragments to show.
	 */
	private void setupViewPager() {
		ORIGINAL_TOUCH_MODE = slidingMenu.getTouchModeAbove();
		viewPagerAdapter = new ViewPagerFragmentAdapter(getFragmentManager(), getDataSet());
		viewPager.setAdapter(viewPagerAdapter);
		viewPager.setOffscreenPageLimit(2);
		viewPager.setCurrentItem(getInitialViewPosition());
		viewPager.setOnPageChangeListener(new OnPageChangeListener() {
			
			@Override
			public void onPageSelected(final int position) {
				updateTitleLabel(TransactionFragmentFactory.getTitleForData(getDataSet().get(position)));

				if(position == 0){
					slidingMenu.setTouchModeAbove(ORIGINAL_TOUCH_MODE);
				}else{
					slidingMenu.setTouchModeAbove(SlidingMenu.TOUCHMODE_NONE);
				}
			}
			
			@Override
			public void onPageScrolled(final int arg0, final float arg1, final int arg2) {
				
			}
			
			@Override
			public void onPageScrollStateChanged(final int arg0) {
				
			}
		});
		//Disable the sliding menu if the current item is not the first item.
		if(viewPager.getCurrentItem() > 0)
			slidingMenu.setTouchModeAbove(SlidingMenu.TOUCHMODE_NONE);

	}
	
	/**
	 * Does the setup for the next and previous buttons so that when they are clicked, they will increment
	 * or decrement the index of the view pager to show a different fragment.
	 */
	private void setupClickListeners() {
		final int MINUS_ONE = -1;
		final int PLUS_ONE = 1;
		
		previousViewButton.setOnClickListener(getOnClickListenerToAdjustPageIndexBy(MINUS_ONE));
		nextViewButton.setOnClickListener(getOnClickListenerToAdjustPageIndexBy(PLUS_ONE));
	}
	
	/**
	 * Returns an OnClickListener that will change the view of the view pager by the provided index.
	 * @param adjustIndexBy the value that will be used to adjust the index of the view pager upon clicking.
	 * @return a new OnClickListener that will adjust the index of the view pager.
	 */
	private OnClickListener getOnClickListenerToAdjustPageIndexBy(final int adjustIndexBy) {
		OnClickListener listener = null;
		
		if(viewPager != null){
			listener = new OnClickListener() {
				@Override
				public void onClick(final View v) {
					viewPager.setCurrentItem(viewPager.getCurrentItem() + adjustIndexBy);
				}
			};
		}else{
			listener = new OnClickListener() {
				@Override
				public void onClick(final View v) {
					Log.e(TAG, "ERROR: Click listener was initialized with no ViewPager");
				}
			};
		}
		return listener;
	}
	
	public static class ViewPagerFragmentAdapter extends FragmentStatePagerAdapter {

		List<ActivityItem> activityItems;
		
		public ViewPagerFragmentAdapter(final android.support.v4.app.FragmentManager fragmentManager, final List<ActivityItem> activityItems) {
			super(fragmentManager);
			this.activityItems = activityItems;
		}

		@Override
		public Fragment getItem(final int position) {
			return TransactionFragmentFactory.getFragmentForData(activityItems.get(position));
		}

		@Override
		public int getCount() {
			return activityItems.size();
		}
		
	}
		
	
}
