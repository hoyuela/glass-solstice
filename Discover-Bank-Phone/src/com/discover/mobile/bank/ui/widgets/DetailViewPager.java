package com.discover.mobile.bank.ui.widgets;

import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.discover.mobile.bank.BankExtraKeys;
import com.discover.mobile.bank.DynamicDataFragment;
import com.discover.mobile.bank.R;
import com.discover.mobile.common.BaseFragment;
import com.discover.mobile.common.help.HelpWidget;
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
public abstract class DetailViewPager extends BaseFragment implements DynamicDataFragment{
	private static final String TAG = DetailViewPager.class.getSimpleName();

	/** The View Pager*/
	private ViewPager viewPager;

	/** The text label to the left of the next/previous buttons that identifies the kind of transaction visible*/
	private TextView titleLabel;

	/**Boolean used to determine if the fragment is loading more*/
	private boolean isLoadingMore = false;

	/** Requires any subclass to define what title label to use for the ViewPager action bar*/
	@Override
	public abstract int getActionBarTitle();

	/**
	 * Returns a fully constructed Fragment ready to be shown in the ViewPager
	 * @return a Fragment ready to be displayed in the ViewPager.
	 */
	protected abstract Fragment getDetailItem(final int position);

	/**
	 * Abstract Method to be implemented for the help menu
	 */
	protected abstract void helpMenuOnClick(HelpWidget help);

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
	 * @param position Refers to the index of the data item in the list
	 * @return if the user is the primary account holder.
	 */
	protected abstract boolean isUserPrimaryHolder(int position);

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
		setRetainInstance(true);
		super.onCreate(savedInstanceState);
		final View mainView = inflater.inflate(R.layout.account_item_detail_view, null);

		/**Help icon setup*/
		final HelpWidget help = (HelpWidget) mainView.findViewById(R.id.help);
		helpMenuOnClick(help);

		final Bundle args = getArguments();
		isLoadingMore = args.getBoolean(BankExtraKeys.IS_LOADING_MORE);

		loadAllViewsFrom(mainView);		
		setupNavButtons(mainView);

		return mainView;
	}

	/**
	 * If back is pressed we need to make sure that the sliding drawer is unlocked.
	 */
	@Override
	public void onPause() {
		final SlidingMenu slidingMenu = ((SlidingFragmentActivity)this.getActivity()).getSlidingMenu();
		slidingMenu.setTouchModeAbove(SlidingMenu.TOUCHMODE_FULLSCREEN);

		final Bundle args = getArguments();
		args.putBoolean(BankExtraKeys.IS_LOADING_MORE, isLoadingMore);

		super.onPause();
	}

	@Override
	public void onStop() {
		if(viewPager != null){
			viewPager.removeAllViews();
			viewPager.setAdapter(null);
		}
		super.onStop();
	}

	@Override
	public void onResume() {
		super.onResume();
		setupViewPager();
		updateViewPagerState(viewPager.getCurrentItem());
		updateSlidingDrawerLock();
	}

	/**
	 * Updates the text of the label to the left of the next/previous buttons.
	 * @param titleTextResource a String resource to use as the title label for a transaction.
	 */
	public void updateTitleLabel(final int titleTextResource) {
		if( titleTextResource != 0 ) {
			titleLabel.setText(titleTextResource);
		} else {
			titleLabel.setVisibility(View.INVISIBLE);
		}
	}

	/**
	 * Get all of the views in the layout that we will need to have access to.
	 * @param mainView the inflated layout that contians views that we want to access.
	 */
	private void loadAllViewsFrom(final View mainView) {
		viewPager = (ViewPager)mainView.findViewById(R.id.view_pager);

		//Access the views that are inside of the nav_buttons layout inside of our layout.
		final RelativeLayout mainBar = (RelativeLayout)mainView.findViewById(R.id.nav_buttons);
		titleLabel = (TextView)mainBar.findViewById(R.id.title);

	}

	protected ViewPager getViewPager() {
		return viewPager;
	}

	private void setupNavButtons(final View mainView) {
		((ImageView)mainView.findViewById(R.id.next_button)).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(final View v) {
				showNextFragment(v);
			}
		});

		((ImageView)mainView.findViewById(R.id.previous_button)).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(final View v) {
				showPreviousFragment(v);
			}
		});
	}

	/**
	 * Setup the ViewPager to accept a collection of fragments to show.
	 */
	private void setupViewPager() {
		viewPager.setAdapter(new ViewPagerFragmentAdapter(getChildFragmentManager()));
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
		updateSlidingDrawerLock();
		loadMoreIfNeeded(position);
		updateNavigationButtons();
	}


	/**
	 * If the position provided is not zero, or, the start of the list, then lock the sliding drawer
	 * so that the swiping action will not open it, otherwise unlock it.
	 * @param position
	 */
	private void updateSlidingDrawerLock() {
		final SlidingFragmentActivity currentActivity = (SlidingFragmentActivity)getActivity();
		if(currentActivity != null) {
			final SlidingMenu slidingMenu = currentActivity.getSlidingMenu();
			if(slidingMenu != null) {
				if(isCurrentPositionAtStart()) {
					slidingMenu.setTouchModeAbove(SlidingMenu.TOUCHMODE_FULLSCREEN);
				} else {
					slidingMenu.setTouchModeAbove(SlidingMenu.TOUCHMODE_NONE);
				}
			}
		}
	}

	/**
	 * Disables and enables the next and previous buttons based on a passes position value.
	 * Intended to disabled the previous button when we are at the start or disable the 
	 * next button if we are at the end and cannot load any more.
	 * @param position
	 */
	protected void updateNavigationButtons() {
		final View mainView = this.getView();
		if(isCurrentPositionAtStart()) {
			mainView.findViewById(R.id.previous_button).setEnabled(false);
		}else{
			mainView.findViewById(R.id.previous_button).setEnabled(true);
		}

		if(isCurrentPositionAtEnd()) {
			mainView.findViewById(R.id.next_button).setEnabled(false);
		} else {
			mainView.findViewById(R.id.next_button).setEnabled(true);
		}

		if(getViewCount() < 2){
			mainView.findViewById(R.id.next_button).setVisibility(View.INVISIBLE);
			mainView.findViewById(R.id.previous_button).setVisibility(View.INVISIBLE);
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

	public void showNextFragment(final View view) {
		if(viewPager != null) {
			viewPager.setCurrentItem(viewPager.getCurrentItem() + 1);
		}
	}

	public void showPreviousFragment(final View view) {
		if(viewPager != null) {
			viewPager.setCurrentItem(viewPager.getCurrentItem() - 1);
		}
	}

	/**
	 * Set if the fragment is loading more
	 * @param isLoadingMore - if the fragment is loading more
	 */
	@Override
	public void setIsLoadingMore(final boolean isLoadingMore){
		this.isLoadingMore = isLoadingMore;
	}

	protected void resetViewPagerAdapter() {
		final PagerAdapter temp = getViewPager().getAdapter();
		final int position = getViewPager().getCurrentItem();
		getViewPager().setAdapter(null);
		getViewPager().setAdapter(temp);
		getViewPager().setCurrentItem(position);
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

		public ViewPagerFragmentAdapter(final FragmentManager fragmentManager) {
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

		/**
		 * This is overridden so that the view pager does not save any pages upon pause.
		 * If this is not overridden then when coming back to a view pager, the pages are not visible.
		 */
		@Override
		public Parcelable saveState() {
			return null;
		}
	}

}
