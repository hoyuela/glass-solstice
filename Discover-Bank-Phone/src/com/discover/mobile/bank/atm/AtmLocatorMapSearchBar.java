/*
 * © Copyright Solstice Mobile 2013
 */
package com.discover.mobile.bank.atm;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
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

/**
 * View holding the search bar for the map view of ATM locator
 * @author jthornton
 *
 */
public class AtmLocatorMapSearchBar extends RelativeLayout{

	/**Box containing the search text*/
	private final EditText searchBox;

	/**Filter layout*/
	private final LinearLayout filterLayout;

	/**Filter toggle view*/
	private final ImageView filterToggle;

	/**Boolean determining if the filter is on*/
	private boolean isFilterOn = true;

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
		searchBox = (EditText)view.findViewById(R.id.search_box);
		filterToggle = (ImageView) view.findViewById(R.id.filter_enable);
		searchDummy = (ImageView) view.findViewById(R.id.help_dummy);


		setupToggles(context);
		setupSearchBox(context);
		addView(view);
	}

	/**
	 * Setup the toggles
	 */
	private void setupToggles(final Context context){
		hide.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(final View v) {
				if(isSearchExpanded){
					show.setVisibility(View.VISIBLE);
					searchLayout.startAnimation(Animator.createSlideToLeftAnimation(context, searchLayout));
					isSearchExpanded = false;
					hide.setBackgroundDrawable(getResources().getDrawable(R.drawable.drk_blue_arrow_left_square));
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

		filterToggle.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(final View v) {
				if(isFilterOn){
					filterToggle.setBackgroundDrawable(getResources().getDrawable(R.drawable.swipe_off));
					isFilterOn = false;
				}else{
					filterToggle.setBackgroundDrawable(getResources().getDrawable(R.drawable.swipe_on));
					isFilterOn = true;
				}

			}

		});
	}

	/**
	 * Set up the search box and its listeners
	 */
	private void setupSearchBox(final Context context){
		final Drawable locationImage = getResources().getDrawable(R.drawable.atm_current_location_button);
		final Drawable magnifyingImage = getResources().getDrawable(R.drawable.magnifying_glass);
		final Drawable clearImage = getResources().getDrawable(R.drawable.atm_searchbar_gray_x);

		searchBox.addTextChangedListener(new TextWatcher() {
			@Override
			public void afterTextChanged(final Editable s) {
				if(searchBox.getText().toString().isEmpty()){
					searchBox.setCompoundDrawablesWithIntrinsicBounds(magnifyingImage, null, locationImage, null);
				}else{
					searchBox.setCompoundDrawablesWithIntrinsicBounds(magnifyingImage, null, clearImage, null);
				}
			}

			@Override
			public void beforeTextChanged(final CharSequence s, final int start, final int count, final int after){}

			@Override
			public void onTextChanged(final CharSequence s, final int start, final int before, final int count) {}
		});

		searchBox.setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(final View v, final MotionEvent event) {
				if (isTouchRegionValid(event) && !searchBox.getText().toString().isEmpty()) {
					searchBox.setText("");
				}else if(isTouchRegionValid(event)){
					searchBox.setText(fragment.getCurrentLocationAddress());
				}

				return false;
			}

			/**
			 * Determines if the MotionEvent touch region was on the right drawable.
			 * @param event The MotionEvent that will be used to check where the user pressed on the screen.
			 * @return Returns true if the user pressed in the region of the right drawable in the EditText field.
			 */
			private boolean isTouchRegionValid(final MotionEvent event){
				if(searchBox.getText().toString().isEmpty()){
					return event.getX() > searchBox.getWidth() - searchBox.getPaddingRight() - locationImage.getIntrinsicWidth();
				}else{
					return event.getX() > searchBox.getWidth() - searchBox.getPaddingRight() - clearImage.getIntrinsicWidth();
				}

			}
		});

		searchBox.setOnFocusChangeListener(new OnFocusChangeListener(){

			@Override
			public void onFocusChange(final View v, final boolean hasFocus) {
				if(hasFocus){
					filterLayout.startAnimation(Animator.expand(filterLayout));
				}else{
					filterLayout.startAnimation(Animator.collapseAndHide(filterLayout));
				}
			}

		});

		searchBox.setOnEditorActionListener(new EditText.OnEditorActionListener() {
			@Override
			public boolean onEditorAction(final TextView arg0, final int arg1, final KeyEvent arg2) {
				if (arg1 == EditorInfo.IME_ACTION_DONE) {
					searchBox.clearFocus();
					filterLayout.setVisibility(View.GONE);
					final InputMethodManager manager = 
							(InputMethodManager)context.getSystemService(Context.INPUT_METHOD_SERVICE);
					manager.hideSoftInputFromWindow(searchBox.getWindowToken(), 0);
					if(null != fragment){
						fragment.performSearch(searchBox.getText().toString());
					}
					return true;
				}
				return false;
			}
		});
	}

	public void showListView(){
		searchDummy.setVisibility(View.GONE);
		hide.setVisibility(View.GONE);
	}

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
		return isFilterOn;
	}

	/**
	 * Save all the data in a bundle
	 */
	public void saveState(final Bundle outState){
		outState.putBoolean(FILTER_STATE, isFilterOn);
		outState.putBoolean(SEARCH_BOX_STATE, isSearchExpanded);
		outState.putBoolean(SEARCH_BOX_FOCUSED, searchBox.isFocused());
		outState.putString(SEARCH_TEXT, searchBox.getText().toString());
	}

	/**
	 * Restore the state of the view
	 */
	public void restoreState(final Bundle bundle){
		isFilterOn = bundle.getBoolean(FILTER_STATE, true);		
		filterToggle.setBackgroundDrawable(getResources().getDrawable(
				(isFilterOn) ? R.drawable.swipe_on : R.drawable.swipe_off));
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
}
