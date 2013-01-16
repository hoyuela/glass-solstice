package com.discover.mobile.common.customui;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.discover.mobile.common.R;


/**
 * An abstract class to handle input fields with specific input types.
 * Allows easy input restriction, validating input, showing of error labels, and error states.
 * 
 * This parent class handles the attachment of an error label to hide and show, the default and 
 * error appearance of a field, it is up to the children to implement what isValid means.
 * 
 * @author scottseward
 *
 */
public abstract class ValidatedInputField extends EditText{

	/** Default and error drawables for input fields */
	protected final static int FIELD_DEFAULT_APPEARANCE = R.drawable.edit_text_default;
	protected final static int FIELD_ERROR_APPEARANCE = R.drawable.edit_text_red;
		
	private int EMS_FOCUSED;
	private int EMS_NOT_FOCUSED;
	
	private ValidatedInputField mSearchText;
	
	/**Default date picker ems size*/
	protected static final int DATE_PICKER_EMS_LENGTH = 11;

	/**A shared input filter to be used when changing the max input length of a field.*/
	InputFilter[] filterArray = new InputFilter[1];

	/**
	 * Drawables that are used in the right compound drawable locations.
	 */
	protected Drawable redX = null;
	protected Drawable grayX = null;
	protected Drawable downArrow = null;
	
	protected boolean isInErrorState = false;
	
	protected boolean needsToAdjustSizeDynamically = false;
	
	/**
	 * If an error label is provided, this will be shown and hidden on 
	 * error states.
	 */
	protected TextView errorLabel;
	protected abstract int getEMSFocusedLength();
	protected abstract int getEMSNotFocusedLength();
	
	/**
	 * Default constructor
	 * @param context the context of use for the EditText.
	 */ 
	public ValidatedInputField(final Context context) {
		super(context);	
		basicSetup();
	}
	
	public ValidatedInputField(final Context context, final AttributeSet attrs) {
		super(context, attrs);	
		basicSetup();
	}
	
	public ValidatedInputField(final Context context, final AttributeSet attrs, final int defStyle) {
		super(context, attrs, defStyle);
		basicSetup();
	}
	
	/**
	 * Called upon creation of a ValidatedInputField child. Sets up the text changed and
	 * focus changed listeners and sets the default appearance and input restrictions on the field.
	 */
	protected void basicSetup() {
		setupFocusChangedListener();
		setupTextChangedListener();
		setupInputRestrictions();
		setupDefaultAppearance();
		setupEMSLength();
		mSearchText = this;
		setupRightDrawableTouchRegion();
	}
	
	/**
	 * Sets the EMS length to either a default value or what is returned by a subclass.
	 */
	private void setupEMSLength() {
		EMS_FOCUSED = getEMSFocusedLength();
		EMS_NOT_FOCUSED = getEMSNotFocusedLength();
	
		this.setEms(EMS_NOT_FOCUSED);
		this.setMaxEms(EMS_NOT_FOCUSED);
	}
	
	/**
	 * Adjusts the EMS length of the input field during runtime.
	 * 
	 * @param newEmsLength a EMS length that will be used for EMS and maxEMS.
	 */
	private void setNewEms(final int newEmsLength) {
		this.setEms(newEmsLength);
		this.setMaxEms(newEmsLength);
	}
	/**
	 * Set the default appearance so that we dont have to do it in XML.
	 */
	protected void setupDefaultAppearance() {
		this.setBackgroundResource(FIELD_DEFAULT_APPEARANCE);
		this.setTextColor(getResources().getColor(R.color.field_copy));
	}
	
	/**
	 * The TextLabel to be used to show an error. It is hidden or shown
	 * based on the state of the input field. This class is not responsible
	 * for altering the text that is in the label.
	 * @param label A text label to show on input error.
	 */
	public void attachErrorLabel(final TextView label) {
		this.errorLabel = label;
	}
	
	/**
	 * Set the visibility of the attached error label to GONE.
	 */
	protected void hideErrorLabel() {
		if(errorLabel != null)
			this.errorLabel.setVisibility(View.GONE);
	}
	
	/**
	 * Set the visibility of the attached error label to VISIBLE.
	 */
	protected void showErrorLabel() {
		if(errorLabel != null)
			this.errorLabel.setVisibility(View.VISIBLE);
	}
	
	/**
	 * Sets a focus changed listener that will validate and update the appearance of the input field
	 * upon focus change.
	 */
	protected void setupFocusChangedListener() {
		this.setOnFocusChangeListener(new OnFocusChangeListener() {
			
			@Override
			public void onFocusChange(final View v, final boolean hasFocus) {
				clearRightDrawable();

				//If Lost Focus
				if( !hasFocus ){
					setNewEms(EMS_NOT_FOCUSED);
					updateAppearanceForInput();
					if(!isInErrorState) {
						clearErrors();
						clearRightDrawable();
					}
				}
				//If Selected/Has Focus
				else {
					setNewEms(EMS_FOCUSED);
					setRightDrawableGrayX();
					if(isInErrorState)
						setRightDrawableRedX();
				}
					
			}
			
		});
	}
	
	/**Sets the right drawable to the gray X image*/
	private void setRightDrawableGrayX() {
		this.setCompoundDrawablesWithIntrinsicBounds(null, null, getGrayX(), null);
	}
	
	/**Sets the right drawable to the red X image*/
	protected void setRightDrawableRedX() {
		this.setCompoundDrawablesWithIntrinsicBounds(null, null, getRedX(), null);
	}
	
	/**Clears the right drawable so that no image is present*/
	protected void clearRightDrawable() {
		this.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null);
	}

	
	/**
	 * Sets a text changed listener to listen for new input. Validates
	 * the input in real time, so that if the field has been 
	 * previously marked as an error, it will turn 'normal' as soon as
	 * the input reaches a valid state.
	 */
	protected void setupTextChangedListener(){
		this.addTextChangedListener(new TextWatcher() {

			@Override
			public void afterTextChanged(final Editable s) {
				if(isValid())
					clearErrors();
			}

			@Override
			public void beforeTextChanged(final CharSequence s, final int start, final int count,
					final int after){/*Intentionally Empty*/}

			@Override
			public void onTextChanged(final CharSequence s, final int start, final int before,
					final int count) {/*Intentionally Empty*/}
			
		});
	}
	
	/**
	 * If a red X is to be used at the right drawable location (on error of account # or username)
	 * Load it from the resources so that the calling class can use the image.
	 */
	protected Drawable getRedX() {
		if(redX == null)
			redX = getResources().getDrawable(R.drawable.x_red);
		return redX;
	}
	
	protected Drawable getGrayX() {
		if(grayX == null)
			grayX = getResources().getDrawable(R.drawable.x_gray);
		return grayX;
	}
	
	/**
	 * If a down arrow is to be used at the right drawable location (for a date picker)
	 * Load it from the resources so that the calling class can use the image.
	 */
	protected Drawable getDownArrow() {
		if(downArrow == null)
			downArrow = getResources().getDrawable(R.drawable.down_arrow);
		return downArrow;
	}
	/**
	 * Update the error state of the field.
	 * If the input field is valid - hide the error state. If not, 
	 * show the error state.
	 */
	public void updateAppearanceForInput() {
		if(isValid())
			clearErrors();
		else
			setErrors();
	}

	/**
	 * Meant to be overridden in sublcass to setup input restrictions on
	 * things such as length, if its a password etc.
	 */
	protected void setupInputRestrictions(){}
	
	/**
	 * Check to see if the current edit text has a valid input state.
	 * @return returns true if the current input is valid.
	 */
	protected abstract boolean isValid();
	
	/**
	 * Sets the error state of the EditText. This usually includes things like
	 * changing the highlighting color and showing an error label. 
	 */
	protected void setErrors(){
		showErrorLabel();
		setRightDrawableRedX();
		this.setBackgroundResource(FIELD_ERROR_APPEARANCE);
		isInErrorState = true;
	}
	
	/**
	 * Clears the error state of the EditText. This usually includes setting the
	 * highlighting to default and hiding error labels.
	 */
	protected void clearErrors(){
		hideErrorLabel();
		setRightDrawableGrayX();
		this.setBackgroundResource(FIELD_DEFAULT_APPEARANCE);
		isInErrorState = false;
	}
	
	/**
	 * Set the text field to clear itself if the user presses a right drawable in the 
	 * input field.
	 */
	private void setupRightDrawableTouchRegion() {
		
		/**
		 * Touch listener so that when tapping the X the text is cleared.
		 */
		this.setOnTouchListener(new OnTouchListener() {
			
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				if (mSearchText.getCompoundDrawables()[2] == null) {
					return false;
				}
				if (event.getAction() != MotionEvent.ACTION_UP) {
					return false;
				}
				if (event.getX() > mSearchText.getWidth()
						- mSearchText.getPaddingRight()
						- getRedX().getIntrinsicWidth()) {
					mSearchText.setText("");
					mSearchText.clearErrors();
					mSearchText.setRightDrawableGrayX();
				}
				return false;
			}
		});
	}
	
}
