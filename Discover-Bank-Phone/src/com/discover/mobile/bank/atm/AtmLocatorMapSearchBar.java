/*
 * © Copyright Solstice Mobile 2013
 */
package com.discover.mobile.bank.atm;

import android.content.Context;
import android.os.Bundle;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.discover.mobile.bank.R;
import com.discover.mobile.bank.ui.Animator;
import com.discover.mobile.bank.ui.ExpandCollapseAnimation;
import com.discover.mobile.common.ui.toggle.DiscoverToggleSwitch;

/**
 * View holding the search bar for the map view of ATM locator
 * @author jthornton
 *
 */
public class AtmLocatorMapSearchBar extends RelativeLayout{

	/**Box containing the search text*/
	private final AtmSearchEditText searchBox;

	/**Filter layout*/
	private final LinearLayout filterLayout;

	/**Filter toggle view*/
	private final DiscoverToggleSwitch filterToggle;

	/**Imageview that when clicked will hide the search bar*/
	private final ImageView hide;

	/**Imageview that when clicked will show the search bar*/
	private final ImageView show;

	/**Imageview that when clicked will show the search bar*/
	private final ImageView searchDummy;

	/**Search layout*/
	private final RelativeLayout searchLayout;

	/**Boolean that is true if the search is expanded*/
	private boolean isSearchExpanded = true;

	/**Fragment used to do searches*/
	private AtmMapSearchFragment fragment;

	/**Integer Duration of the expand collapse animation*/
	private static final int DURATION = 300;

	/**
	 * Keys for saving the bundle
	 */	
	private static final String FILTER_STATE = "filterState";
	private static final String SEARCH_BOX_STATE = "searchBoxState";
	private static final String SEARCH_BOX_FOCUSED = "searchBoxFocused";
	private static final String SEARCH_TEXT = "searchText";

	/**
	 * Constructor for the class
	 * @param context
	 * @param attrs
	 */
	public AtmLocatorMapSearchBar(final Context context, final AttributeSet attrs) {
		super(context, attrs);

		final View view = LayoutInflater.from(context).inflate(R.layout.atm_locator_map_search_bar, null);

		searchLayout = (RelativeLayout) view.findViewById(R.id.search_bar);
		hide = (ImageView) view.findViewById(R.id.search_show);
		show = (ImageView) view.findViewById(R.id.search_expand);
		filterLayout = (LinearLayout) view.findViewById(R.id.filter_bar);
		searchBox = (AtmSearchEditText)view.findViewById(R.id.search_box);
		filterToggle = (DiscoverToggleSwitch) view.findViewById(R.id.filter_enable);
		searchDummy = (ImageView) view.findViewById(R.id.help_dummy);

		setupToggles(context);
		setupSearchBox(context);
		addView(view);
	}

	/**
	 * Setup the toggles
	 */
	private void setupToggles(final Context context){
		filterToggle.setChecked(true);
		hide.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(final View v) {
				if(isSearchExpanded){
					show.setVisibility(View.VISIBLE);
					clearSearchFocus();
					searchLayout.startAnimation(Animator.createSlideToLeftAnimation(context, searchLayout));
					isSearchExpanded = false;
				}
			}

		});

		show.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(final View v) {
				if(!isSearchExpanded){
					searchLayout.startAnimation(Animator.createSlideToRightAnimation(context, searchLayout));
					isSearchExpanded = true;
					searchLayout.setVisibility(View.VISIBLE);
					show.setVisibility(View.GONE);
				}
			}

		});
	}

	/**
	 * Set up the search box and its listeners
	 */
	private void setupSearchBox(final Context context){
		searchBox.setSearchView(this);
		searchBox.setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(final View v, final MotionEvent event) {
				if(event.getAction() == MotionEvent.ACTION_UP){
					if(searchBox.isTouchRegionValid(event)){
						clearSearchFocus();
						fragment.startCurrentLocationSearch();
						return true;
					}
				}
				return false;
			}
		});

		searchBox.setOnFocusChangeListener(new OnFocusChangeListener(){

			@Override
			public void onFocusChange(final View v, final boolean hasFocus) {
				if(hasFocus){
					filterLayout.startAnimation(new ExpandCollapseAnimation(filterLayout, true, DURATION));
				}
				if(searchBox.getText().toString().isEmpty() && !hasFocus){
					searchBox.setHint(R.string.atm_location_search_help);
				}else{
					searchBox.setHint(null);
				}
			}

		}); 

		searchBox.setOnEditorActionListener(new EditText.OnEditorActionListener() {
			@Override
			public boolean onEditorAction(final TextView arg0, final int arg1, final KeyEvent arg2) {
				if (arg1 == EditorInfo.IME_ACTION_DONE || arg1 == EditorInfo.IME_ACTION_SEARCH) {
					clearSearchFocus();
					if(null != fragment){
						fragment.performSearch(searchBox.getText().toString());
					}
					return true;
				}
				return false;
			}
		});
	}

	/**
	 * Clear the focus of the edit text on the search field
	 */
	protected void clearSearchFocus(){
		searchBox.clearFocus();
		final InputMethodManager manager = 
				(InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
		manager.hideSoftInputFromWindow(searchBox.getWindowToken(), 0);
		
		if (filterLayout.isShown()) {
			filterLayout.startAnimation(new ExpandCollapseAnimation(filterLayout, false, DURATION));
		}
	}

	/**
	 * Set up the search bar to show with the list
	 */
	public void showListView(){
		show.setVisibility(View.GONE);
		searchLayout.setVisibility(View.VISIBLE);
		searchDummy.setVisibility(View.GONE);
		hide.setVisibility(View.GONE);
	}

	/**
	 * Set up the search bar to show with the map
	 */
	public void showMapView(){
		searchDummy.setVisibility(View.INVISIBLE);
		hide.setVisibility(View.VISIBLE);
	}

	/**
	 * @return the fragment
	 */
	public AtmMapSearchFragment getFragment() {
		return fragment;
	}

	/**
	 * @param fragment the fragment to set
	 */
	public void setFragment(final AtmMapSearchFragment fragment) {
		this.fragment = fragment;
	}

	/**
	 * @return the isFilterOn
	 */
	public boolean isFilterOn() {
		return filterToggle.isChecked();
	}

	/**
	 * Save all the data in a bundle
	 */
	public void saveState(final Bundle outState){
		outState.putBoolean(FILTER_STATE, filterToggle.isChecked());
		outState.putBoolean(SEARCH_BOX_STATE, isSearchExpanded);
		outState.putBoolean(SEARCH_BOX_FOCUSED, searchBox.isFocused());
		outState.putString(SEARCH_TEXT, searchBox.getText().toString());
	}

	/**
	 * Restore the state of the view
	 */
	public void restoreState(final Bundle bundle){
		filterToggle.setChecked(bundle.getBoolean(FILTER_STATE, true));		
		isSearchExpanded = bundle.getBoolean(SEARCH_BOX_STATE, true);
		if(isSearchExpanded){
			show.setVisibility(View.GONE);
			searchLayout.setVisibility(View.VISIBLE);
		}else{
			show.setVisibility(View.VISIBLE);
			searchLayout.setVisibility(View.GONE);
		}
		searchBox.setText(bundle.getString(SEARCH_TEXT));
		if(bundle.getBoolean(SEARCH_BOX_FOCUSED, false)){
			searchBox.requestFocusFromTouch();
		}
	}

	/**
	 * Hide the search bar
	 */
	public void hideBar(){
		show.setVisibility(View.VISIBLE);
		searchLayout.startAnimation(Animator.createSlideToLeftAnimation(getContext(), searchLayout));
		isSearchExpanded = false;
	}

	/**
	 * Show the search bar
	 */
	public void showBar(){
		searchLayout.startAnimation(Animator.createSlideToRightAnimation(getContext(), searchLayout));
		isSearchExpanded = true;
		searchLayout.setVisibility(View.VISIBLE);
		show.setVisibility(View.GONE);
	}

	/**
	 * @return the isSearchExpanded
	 */
	public boolean isSearchExpanded() {
		return isSearchExpanded;
	}

	/**
	 * @param isSearchExpanded the isSearchExpanded to set
	 */
	public void setSearchExpanded(final boolean isSearchExpanded) {
		this.isSearchExpanded = isSearchExpanded;
	}
}
