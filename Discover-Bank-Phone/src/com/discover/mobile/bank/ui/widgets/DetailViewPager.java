package com.discover.mobile.bank.ui.widgets;

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
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.discover.mobile.bank.DynamicDataFragment;
import com.discover.mobile.bank.R;
import com.discover.mobile.bank.util.FragmentOnBackPressed;
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
public abstract class DetailViewPager extends BaseFragment implements DynamicDataFragment, FragmentOnBackPressed{
	private final String TAG = DetailViewPager.class.getSimpleName();

	/** The View Pager*/
	private ViewPager viewPager;

	/** The text label to the left of the next/previous buttons that identifies the kind of transaction visible*/
	private TextView titleLabel;

	/**Boolean used to determine if the fragment is loading more*/
	private boolean isLoadingMore = false;

	/** 
	 * The text label that is used to show an error if a user is viewing a scheduled transaction and is not
	 * the primary account holder.
	 */
	private TextView jointAccountWarning;

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
	 * If the user is not the primary account holder they will not be able to edit scheduled
	 * payments. They will see the detail item but not the edit/delete buttons and will be shown
	 * a message.
	 * @return if the user is the primary account holder.
	 */
	protected abstract boolean isUserPrimaryHolder();

	/**
	 * Initiate a server call to load more data.
	 */
	protected abstract void loadMore(final String url);

	/**
	 * Asks the sub class what the current type of the data is.
	 */
	protected abstract boolean isFragmentEditable(final int position);

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
		updateViewPagerState(viewPager.getCurrentItem());

		return mainView; 
	}

	/**
	 * @return the viewPager
	 */
	public ViewPager getViewPager() {
		return viewPager;
	}

	/**
	 * If back is pressed we need to make sure that the sliding drawer is unlocked.
	 */
	@Override
	public void onBackPressed() {
		slidingMenu.setTouchModeAbove(SlidingMenu.TOUCHMODE_FULLSCREEN);
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
		viewPager = (ViewPager)mainView.findViewById(R.id.view_pager);
		slidingMenu = ((SlidingFragmentActivity)this.getActivity()).getSlidingMenu();

		//Access the views that are inside of the nav_buttons layout inside of our layout.
		final RelativeLayout mainBar = (RelativeLayout)mainView.findViewById(R.id.nav_buttons);
		previousViewButton = (ImageView)mainBar.findViewById(R.id.previous_button);
		nextViewButton = (ImageView)mainBar.findViewById(R.id.next_button);
		titleLabel = (TextView)mainBar.findViewById(R.id.title);
		jointAccountWarning = (TextView)mainBar.findViewById(R.id.joint_account_warning_label);

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
				updateViewPagerState(position);
			}

			@Override
			public void onPageScrolled(final int arg0, final float arg1, final int arg2) {}
			@Override
			public void onPageScrollStateChanged(final int arg0) {}
		});

	}

	/**
	 * Updates the state of the view paget by updating the title label, locking or unlocking the sliding
	 * drawer menu, loading more data if needed, and enableing or disabling the next and previous buttons.
	 * @param position
	 */
	private void updateViewPagerState(final int position) {
		updateTitleLabel(getTitleForFragment(position));
		updateSlidingDrawerLock(position);
		loadMoreIfNeeded(position);
		updateNavigationButtons(position);
		updateScheduledPaymentWarning(position);
	}

	/**
	 * 
	 * @param position
	 */
	private void updateScheduledPaymentWarning(final int position) {

		if(isUserPrimaryHolder() || !isFragmentEditable(position)){
			jointAccountWarning.setVisibility(View.GONE);
		}else if(isFragmentEditable(position)){
			final String accountWarningText = this.getActivity().getString(R.string.non_primary_joint_account_warning);
			// FIXME Need to know how we are getting the account holder's name to put in here.
			final String accountHolderName = "Account Holder";
			final String formattedWarningText = String.format(accountWarningText, accountHolderName);
			jointAccountWarning.setText(formattedWarningText);
			jointAccountWarning.setVisibility(View.VISIBLE);
		}
	}

	/**
	 * If the position provided is not zero, or, the start of the list, then lock the sliding drawer
	 * so that the swiping action will not open it, otherwise unlock it.
	 * @param position
	 */
	private void updateSlidingDrawerLock(final int position) {
		if(isCurrentPositionAtStart()){
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

		if(isCurrentPositionAtStart()) {
			previousViewButton.setEnabled(false);
		}else{
			previousViewButton.setEnabled(true);
		}

		if(isCurrentPositionAtEnd()) {
			nextViewButton.setEnabled(false);
		} else {
			nextViewButton.setEnabled(true);
		}

		if(getViewCount() < 2){
			nextViewButton.setVisibility(View.INVISIBLE);
			previousViewButton.setVisibility(View.INVISIBLE);
		}
	}

	/**
	 * Checks to see if the view pager is currently at the last visible fragment.
	 * @return if the view pager is at the last visible fragment.
	 */
	private boolean isCurrentPositionAtEnd(){
		return viewPager.getCurrentItem() == (getViewCount() - 1);
	}

	/**
	 * Checks to see if the view pager is at the first visible fragment.
	 * @return if the view pager is at the first visible fragment.
	 */
	private boolean isCurrentPositionAtStart() {
		return viewPager.getCurrentItem() == 0;
	}

	/**
	 * If we reach the end of the list of elements, load more if possible.
	 * @param position
	 */
	protected abstract void loadMoreIfNeeded(final int position);

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
	 * Set if the fragment is loading more
	 * @param isLoadingMore - if the fragment is loading more
	 */
	@Override
	public void setIsLoadingMore(final boolean isLoadingMore){
		this.isLoadingMore = isLoadingMore;
	}

	/**
	 * Get if the fragment is loading more
	 * @return isLoadingMore - if the fragment is loading more
	 */
	@Override
	public boolean getIsLoadingMore(){
		return isLoadingMore;
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
