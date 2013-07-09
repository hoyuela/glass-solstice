package com.discover.mobile.bank.atm;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.widget.EditText;

import com.discover.mobile.bank.R;

/**
 * Simple extension of an edit text.  This notifies the search bar
 * that the back button was pressed to dismiss the keyboard.
 * 
 * @author jthornton
 *
 */
public class AtmSearchEditText extends EditText {

	/**Search bar holding the edit text*/
	private AtmLocatorMapSearchBar bar;

	/**Image shown in the left drawable location*/
	private Drawable locationImage;

	/**
	 * Constructor for the class
	 * @param context
	 * @param attrs
	 * @param defStyle
	 */
	public AtmSearchEditText(final Context context, final AttributeSet attrs, final int defStyle) {
		super(context, attrs, defStyle);
		initUI();
	}

	/**
	 * Constructor for the class
	 * @param context
	 * @param attrs
	 */
	public AtmSearchEditText(final Context context, final AttributeSet attrs) {
		super(context, attrs);
		initUI();
	}

	/**
	 * Constructor for the class
	 * @param context
	 */
	public AtmSearchEditText(final Context context) {
		super(context);
		initUI();
	}

	/**
	 * Init the UI so that it can be used
	 */
	private void initUI(){
		locationImage = getResources().getDrawable(R.drawable.atm_current_location_button);
		final Drawable magnifyingImage = getResources().getDrawable(R.drawable.magnifying_glass);
		setCompoundDrawablesWithIntrinsicBounds(magnifyingImage, null, locationImage, null);
	}

	/**
	 * Set the search view so that it can be notified when the keyboard
	 * is being hidden.
	 * @param searchBar
	 */
	public void setSearchView(final AtmLocatorMapSearchBar searchBar){
		bar = searchBar;
	}

	@Override
	public boolean onKeyPreIme(final int keyCode, final KeyEvent event) {
		//Check to see if the back button is pressed
		if (event.getKeyCode() == KeyEvent.KEYCODE_BACK && null != bar) {
			//Clear the focus of the bar if it is clicked.
			bar.clearSearchFocus();
			return true;
		}
		return super.onKeyPreIme(keyCode, event);
	}

	/**
	 * Determines if the MotionEvent touch region was on the right drawable.
	 * @param event The MotionEvent that will be used to check where the user pressed on the screen.
	 * @return Returns true if the user pressed in the region of the right drawable in the EditText field.
	 */
	public boolean isTouchRegionValid(final MotionEvent event){
		return event.getX() > 
		getWidth() - getPaddingRight() - locationImage.getIntrinsicWidth();
	}
}
