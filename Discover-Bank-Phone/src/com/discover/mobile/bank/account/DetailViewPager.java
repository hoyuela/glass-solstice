package com.discover.mobile.bank.account;

import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.discover.mobile.bank.BankServiceCallFactory;
import com.discover.mobile.bank.R;
import com.discover.mobile.common.BaseFragment;
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
		
	/** The View Pager*/
	private ViewPager viewPager;
	
	/** The text label to the left of the next/previous buttons that identifies the kind of transaction visible*/
	private TextView titleLabel;
	
	/** The next and previous buttons that can change the visible Fragment*/
	private ImageView previousViewButton;
	private ImageView nextViewButton;
	
	/** A reference to the sliding menu so we can easily toggle the swipe to open setting*/
	private SlidingMenu slidingMenu;
	
	/** Requires any subclass to define what title label to use for the ViewPager action bar*/
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
	
	/**
	 * Must be overridden by a subclass to set the initial view position of the ViewPager.
	 * @return the position in the data set that should be presented first.
	 */
	protected abstract int getInitialViewPosition();

	/**
	 * Will return the title for the current Fragment so that transactions can be identified better.
	 * @return a String resource for the title of the current Fragment/data
	 */
	protected abstract int getTitleForFragment(final int position);
	
	/**
	 * Inflates the main View for the ViewPager and initializes the ViewPager and buttons.
	 */
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
	 * @return the viewPager
	 */
	public ViewPager getViewPager() {
		return viewPager;
	}
	
	/**
	 * Updates the text of the label to the left of the next/previous buttons.
	 * @param titleTextResource a String resource to use as the title label for a transaction.
	 */
	public void updateTitleLabel(final int titleTextResource) {
		titleLabel.setText(titleTextResource);
	}
	
	/**
	 * Get all of the views in the layout that we will need to have access to.
	 * @param mainView the inflated layout that contians views that we want to access.
	 */
	private void loadAllViewsFrom(final View mainView) {
		previousViewButton = (ImageView)mainView.findViewById(R.id.previous_button);
		nextViewButton = (ImageView)mainView.findViewById(R.id.next_button);
		viewPager = (ViewPager)mainView.findViewById(R.id.view_pager);
		slidingMenu = ((SlidingFragmentActivity)this.getActivity()).getSlidingMenu();
		titleLabel = (TextView)mainView.findViewById(R.id.title);
	}
	
	/**
	 * Setup the ViewPager to accept a collection of fragments to show.
	 */
	private void setupViewPager() {
		final ViewPagerFragmentAdapter viewPagerAdapter = new ViewPagerFragmentAdapter(getFragmentManager());
		viewPager.setAdapter(viewPagerAdapter);
		viewPager.setOffscreenPageLimit(2);
		viewPager.setCurrentItem(getInitialViewPosition());
		updateTitleLabel(getTitleForFragment(getInitialViewPosition()));

		viewPager.setOnPageChangeListener(new OnPageChangeListener() {
			
			@Override
			public void onPageSelected(final int position) {
				updateTitleLabel(getTitleForFragment(position));
				updateSlidingDrawerLock(position);
				loadMoreIfNeeded(position);
				updateNavigationButtons(position);
			}
			
			@Override
			public void onPageScrolled(final int arg0, final float arg1, final int arg2) {}
			@Override
			public void onPageScrollStateChanged(final int arg0) {}
		});
		
		//Disable the sliding menu if the current item is not the first item.
		if(viewPager.getCurrentItem() > 0)
			slidingMenu.setTouchModeAbove(SlidingMenu.TOUCHMODE_NONE);

	}
	
	/**
	 * If the position provided is not zero, or, the start of the list, then lock the sliding drawer
	 * so that the swiping action will not open it, otherwise unlock it.
	 * @param position
	 */
	private void updateSlidingDrawerLock(final int position) {
		if(position == 0){
			slidingMenu.setTouchModeAbove(SlidingMenu.TOUCHMODE_FULLSCREEN);
		}else{
			slidingMenu.setTouchModeAbove(SlidingMenu.TOUCHMODE_NONE);
		}
	}
	
	/**
	 * Disables and enables the next and previous buttons based on a passes position value.
	 * Intended to disabled the previous button when we are at the start or disable the 
	 * next button if we are at the end and cannot load any more.
	 * @param position
	 */
	protected void updateNavigationButtons(final int position) {
		if(position == 0) {
			previousViewButton.setEnabled(false);
		}else{
			previousViewButton.setEnabled(true);
		}
		
		if(position == (getViewCount() - 1))
			nextViewButton.setEnabled(false);
		else
			nextViewButton.setEnabled(true);
			
	}
	
	/**
	 * If we reach the end of the list of elements, load more if possible.
	 * @param position
	 */
	private void loadMoreIfNeeded(final int position) {
		if((getViewCount() - 1) == position){
			BankServiceCallFactory.createGetActivityServerCall("/api/accounts/1/activity?status=posted").submit();
		}
	}
	/**
	 * Does the setup for the next and previous buttons so that when they are clicked, they will increment
	 * or decrement the index of the view pager to show a different fragment.
	 */
	private void setupClickListeners() {
		final int minusOne = -1;
		final int plusOne = 1;
		
		previousViewButton.setOnClickListener(getOnClickListenerToAdjustPageIndexBy(minusOne));
		nextViewButton.setOnClickListener(getOnClickListenerToAdjustPageIndexBy(plusOne));
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
	/**
	 * The FragmentStatePagerAdapter that is used to send Fragments to the ViewPager.
	 * @author scottseward
	 *
	 */
	public class ViewPagerFragmentAdapter extends FragmentStatePagerAdapter {


		@Override
		public Parcelable saveState() {
			return null;
		}

		public ViewPagerFragmentAdapter(final android.support.v4.app.FragmentManager fragmentManager) {
			super(fragmentManager);
		}

		@Override
		public Fragment getItem(final int position) {
			return getDetailItem(position);
		}

		@Override
		public int getCount() {
			return getViewCount();
		}
		
		
		
	}
		
}
