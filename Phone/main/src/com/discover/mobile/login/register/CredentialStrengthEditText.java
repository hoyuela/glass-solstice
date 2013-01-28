package com.discover.mobile.login.register;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.text.InputFilter;
import android.text.InputType;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import com.discover.mobile.R;
import com.discover.mobile.common.analytics.AnalyticsPage;
import com.discover.mobile.common.analytics.TrackingHelper;
import com.discover.mobile.common.auth.InputValidator;
import com.discover.mobile.common.customui.ValidatedInputField;

/**
 * Editable View which validates the text entered with a password or user id strength algorithm.
 * 
 * Must call setCredentialType() and setOtherCredential().
 * 
 * Here is an example of how to include this control in the layout of a view:
 *     <com.discover.mobile.login.register.CredentialStrengthEditText
 *       android:id="@+id/passwordTxtView" 
 *       android:layout_width="fill_parent"
 *       android:layout_height="wrap_content"
 *       android:layout_marginTop="100dp"
 *       android:maxWidth="100dp"
 *       android:ems="32"
 *       android:maxEms="32"/>
 *       
 * @author henryoyuela
 * 
 */
public class CredentialStrengthEditText extends ValidatedInputField {
	/** Used to print logs within this class **/
	private static final String TAG = CredentialStrengthEditText.class.getName();
	/**
	 * Special value used to indicate to the class to use the Password rules for
	 * evaluating strength of entered text
	 **/
	public static final int PASSWORD = 0;
	/**
	 * Special value used to indicate to the class to use the User ID rules for
	 * evaluating strength of entered text
	 **/
	public static final int USERID = PASSWORD + 1;
	/**
	 * Maximum length for a password
	 */
	public static final int MAX_PSWD_LENGTH=32;

	/**
	 * Maximum length for userid
	 */
	public static final int MAX_USERID_LENGTH=16;
	private static final int PASSWORD_STRENGTH_HELP = 0;
	private static final int UID_STRENGTH_HELP = 1;
	
	/**
	 * Used for painting strength meter in onDraw method
	 */
	private Paint mPaint = new Paint();
	/**
	 * Bitmap used to indicate that the text entered qualifies as a strong
	 * password
	 **/
	private Bitmap mStrongImage = null;
	/**
	 * Bitmap used to indicate that the text entered qualifies as a moderate
	 * strength password
	 **/
	private Bitmap mModerateImage = null;
	/**
	 * Bitmap used to indicate that the text entered qualifies as a weak
	 * strength password
	 **/
	private Bitmap mWeakImage = null;
	/**
	 * Bitmap used to indicate that no text to be evaluated
	 **/
	private Bitmap mNoTextImage = null;
	
	/**
	 * Bitmap used to indicate an invalid input.
	 */
	private Bitmap mInvalidImage = null;
	/**
	 * Bitmap rendered by EditText onDraw method based on strength of entered
	 * text (rules vary based on whether id or password)
	 **/
	private Bitmap mStrengthMeter;
	/**
	 * Contains the boundaries used to determine whether a help guide should be
	 * opened
	 **/
	private Rect mTouchRegion = new Rect();
	/**
	 * Used for drawing when OnDraw is called
	 */
	private Rect mRect = new Rect();
	
	/**
	 * Contains what rules to use for evaluating strength of entered text. By
	 * default set to password
	 **/
	private int mCredentialType = PASSWORD;

	public CredentialStrengthEditText(Context context, AttributeSet attrs,
			int defStyle) {
		super(context, attrs, defStyle);
	}

	public CredentialStrengthEditText(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public CredentialStrengthEditText(Context context) {
		super(context);
	}

	/**
	 * Returns USERID or PASSWORD depending on what rule is being used to
	 * evaluate strength of entered text.
	 * 
	 * @return USERID or PASSWORD
	 */
	public int getCredentialType() {
		return mCredentialType;
	}
	
	/**Inherited method from ValidatedInputField, overridden to disable*/
	@Override
	protected void setupFocusChangedListener() {
		this.setOnFocusChangeListener(new OnFocusChangeListener() {
			
			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				if(!hasFocus && !isValid()){
					loadResources();
					mStrengthMeter = mInvalidImage;
				}
			}
		});
	}
	
	/**Inherited method from ValidatedInputField, overridden to disable*/
	@Override
	protected void setupTextChangedListener(){
		//Do nothing
	}
	
	/**
	 * Sets what rules to use to evaluate strength of the text entered. In addition,
	 * will set the max length of characters that can be entered for the text field.
	 * 
	 * @param mCredentialType
	 *            value should be either PASSWORD or USERID
	 */
	public void setCredentialType(int type) {
		this.mCredentialType = type;
		
		//Set maximum length of characters allowed
		InputFilter[] filters = new InputFilter[1];
		switch(mCredentialType) {
		case PASSWORD:
			filters[0]=new InputFilter.LengthFilter(MAX_PSWD_LENGTH);
			setFilters(filters);
			break;
		case USERID:
			filters[0]=new InputFilter.LengthFilter(MAX_USERID_LENGTH);
			setFilters(filters);
			break;
		default:
			Log.v(TAG, "Invalid credential type");
			break;
		}	
		
		this.setSingleLine();
		
		if(type == PASSWORD)
			this.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
		else if(type == USERID)
			this.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);

		mNoTextImage = BitmapFactory.decodeResource(this.getResources(), R.drawable.strength_meter_grey);
		mStrengthMeter = mNoTextImage;
		this.setBackgroundResource(R.drawable.edit_text_default);		
	}
	

	/**
	 * Called to open user id help guide (StrengthBarHelpActivity)
	 */
	private void showIdStrengthBarHelp() {
		final Intent uidHelpScreen = new Intent(this.getContext(), StrengthBarHelpActivity.class);
		uidHelpScreen.putExtra("ScreenType", "id");
		TrackingHelper.trackPageView(AnalyticsPage.UID_STRENGTH_HELP);

		Activity activity = (Activity) this.getContext();
		if (null != activity) {
			activity.startActivityForResult(uidHelpScreen, UID_STRENGTH_HELP);
		} else {
			Log.v(TAG, "Context not provided");
		}
	}

	/**
	 * Called to open password id help guide (StrengthBarHelpActivity)
	 */
	public void showPasswordStrengthBarHelp() {
		final Intent passwordHelpScreen = new Intent(this.getContext(), StrengthBarHelpActivity.class);
		passwordHelpScreen.putExtra("ScreenType", "pass");
		TrackingHelper.trackPageView(AnalyticsPage.PASSWORD_STRENGTH_HELP);

		Activity activity = (Activity) this.getContext();
		if (null != activity) {
			activity.startActivityForResult(passwordHelpScreen, PASSWORD_STRENGTH_HELP);
		} else {
			Log.v(TAG, "Context not provided");
		}
	}
	
	@Override
	public void onTextChanged(CharSequence text,int start,int lengthBefore,int lengthAfter) {	
		loadResources();
		if(lengthAfter > 0)
			isInDefaultState = false;
		
		super.onTextChanged(text, start, lengthBefore, lengthAfter);
		if(isInDefaultState)
			{/*dont update the appearance in case the field is empty and gets 'updated'
			with empty text through orientation change or otherwise*/}
		else 			
			updateAppearanceForInput();

	}
	
	/**
	 * Sets what image to be displayed to indicate to the user the strength of password entered
	 * 
	 * @param inputSequence Contains the updated text entered
	 */
	private void updateStrengthMeterForUID(final CharSequence inputSequence) {

		boolean hasGoodLength = false;
		boolean hasUpperCase = false;
		boolean hasLowerCase = false;
		boolean hasNonAlphaNum = false;
		boolean hasInvalidChar = false;
		boolean hasNumber = false;
		boolean looksLikeActNum = false;
		boolean passAndIdMatch = false;

		// Check length of input.
		if (inputSequence.length() >= 6 && inputSequence.length() <= MAX_USERID_LENGTH)
			hasGoodLength = true;

		for (int i = 0; i < inputSequence.length(); ++i) {

			if (Character.isLowerCase(inputSequence.charAt(i))) {
				hasLowerCase = true;
			} else if (Character.isUpperCase(inputSequence.charAt(i))) {
				hasUpperCase = true;
			} else if (Character.isDigit(inputSequence.charAt(i))) {
				hasNumber = true;
			} else if (inputSequence.charAt(i) == '\\'
					|| inputSequence.charAt(i) == '`'
					|| inputSequence.charAt(i) == '\''
					|| inputSequence.charAt(i) == '\"'
					|| inputSequence.charAt(i) == ' ') {
				hasInvalidChar = true;
			} else if (!Character.isLetterOrDigit(inputSequence.charAt(i))) {
				hasNonAlphaNum = true;
			}
		}

		if (inputSequence.toString().startsWith("6011")) {
			looksLikeActNum = true;
		}
		

		/*
		 * Meets minimum requirements and combines a variation of letters,
		 * numbers, and special characters.
		 */
		if (!passAndIdMatch && !looksLikeActNum && !hasInvalidChar
				&& hasGoodLength && (hasLowerCase || hasUpperCase)
				&& hasNonAlphaNum && hasNumber) {
			mStrengthMeter = mStrongImage;
			setBackgroundResource(R.drawable.edit_text_green);
		}
		/*
		 * Meets minimum requirements but does not include a variation of
		 * letters, numbers, and special characters.
		 */
		else if (!passAndIdMatch && !looksLikeActNum && !hasInvalidChar
				&& hasGoodLength) {
			mStrengthMeter = mModerateImage;
			setBackgroundResource(R.drawable.edit_text_yellow);
		}
		/*
		 * Does not meet minimum requirements (not 6-16 characters, looks like
		 * an account number, or uses spaces or the following characters:
		 * (`)(')(")(\))
		 */
		else {
			mStrengthMeter = mWeakImage;
			setBackgroundResource(R.drawable.edit_text_red);
		}
		
		//Force an onDraw
		this.invalidate();

	}
	
	@Override
	public void setupDefaultAppearance() {	
		super.setupDefaultAppearance();
		loadResources();

		scaleAllImages(this.getHeight(), this.getWidth());
		this.setmStrengthMeter(mNoTextImage);
	}

	/**
	 * Sets what image to be displayed to indicate to the user the strength of user id entered
	 * 
	 * @param inputSequence Contains the updated text entered
	 */
	private void updateStrengthMeterForPass(final CharSequence inputSequence) {
		boolean hasGoodLength = false;
		boolean hasUpperCase = false;
		boolean hasLowerCase = false;
		boolean hasNonAlphaNum = false;
		boolean hasNumber = false;
		boolean passAndIdMatch = false;

		// Check length of input.
		if (inputSequence.length() >= 8 && inputSequence.length() <= MAX_PSWD_LENGTH)
			hasGoodLength = true;

		// A password must have at least 1 letter and 1 number and cannot be
		// 'password'
		// but password doesn't have a number...
		for (int i = 0; i < inputSequence.length(); ++i) {

			if (Character.isLowerCase(inputSequence.charAt(i))) {
				hasLowerCase = true;
			} else if (Character.isUpperCase(inputSequence.charAt(i))) {
				hasUpperCase = true;
			} else if (Character.isDigit(inputSequence.charAt(i))) {
				hasNumber = true;
			} else if (!Character.isLetterOrDigit(inputSequence.charAt(i))) {
				hasNonAlphaNum = true;
			}
		}

		final boolean hasUpperAndLowerAndNum = hasLowerCase && hasUpperCase
				&& hasNumber;
		/*
		 * Meets minimum requirements and combines upper case letters, lower
		 * case letters, numbers, and special characters.
		 */
		if (!passAndIdMatch && hasGoodLength && hasUpperAndLowerAndNum
				&& hasNonAlphaNum) {
			mStrengthMeter = mStrongImage;
			setBackgroundResource(R.drawable.edit_text_green);
		}
		/*
		 * Meets minimum requirements but does not include a variation of upper
		 * case letters, lower case letters, numbers, and special characters.
		 */
		else if (!passAndIdMatch && hasGoodLength && hasNumber
				&& (hasUpperCase || hasLowerCase)) {
			mStrengthMeter = mModerateImage;
			setBackgroundResource(R.drawable.edit_text_yellow);
		}
		/*
		 * Does not meet minimum requirements (not 8-32 characters, does not
		 * contain at least 1 letter and 1 number, or is the word "password").
		 */
		else {
			mStrengthMeter = mWeakImage;
			setBackgroundResource(R.drawable.edit_text_red);
		}
		
		this.invalidate();

	}

	/**
	 * Force the strength meter icon to display 'invalid' text. Useful when the field is already
	 * invalid and someone wants to make the 'invalid' text display.
	 */
	public void setStrengthMeterInvalid() {
		mStrengthMeter = mInvalidImage;
		this.setBackgroundResource(R.drawable.edit_text_red);
	}
	
	/**
	 * Call an update to the strength meter based on the current input.
	 */
	@Override
	public void updateAppearanceForInput() {
		if(this.length() > 0)
			isInDefaultState = false;

		if(mCredentialType == PASSWORD)
			updateStrengthMeterForPass(this.getText().toString());
		else
			updateStrengthMeterForUID(this.getText().toString());
	}
	
	/**
	 * This function is a Android View function that has been overridden to detect when the user 
	 * taps the region of the strength meter that is meant to open a help guide.
	 * 
	 * @params  The motion event which provides details of where the user last touched
	 * @returns True if the event was handled, false otherwise. If the user does not tap within the touchable region, 
	 * then the base class result is returned; otherwise, false is returned to avoid opening the keyboard.
	 */
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		boolean ret = false;
		
		// Check if touchable region was tapped by user
		if (event.getAction() == MotionEvent.ACTION_DOWN) {
			final int x = (int) event.getX();
			final int y = (int) event.getY();

			if (x >= mTouchRegion.left && x <= mTouchRegion.right
					&& y >= mTouchRegion.top && y <= mTouchRegion.bottom) {

				// Open the right user guide based on the credential type value
				switch (mCredentialType) {
				case PASSWORD:
					showPasswordStrengthBarHelp();
					break;
				case USERID:
					showIdStrengthBarHelp();
					break;
				default:
					Log.v(TAG, "Credential type not specified");
					break;
				}

				ret = true;

			}
		}

		if (!ret) {
			ret = super.onTouchEvent(event);
		} else {
			ret = false;
		}

		return ret;
	}
	
	/**
	 * Overridden to paint strength meter over the text field
	 */
	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);

		//Fetch information on canvas position within the control
		canvas.getClipBounds(mRect);
		if(mStrengthMeter != null){
			//Calculate Position of Strength Meter
			float yoffset = 1;
			float xoffset = 1;

			float x = this.getWidth() - mStrengthMeter.getWidth() + mRect.left + xoffset;
			float y = this.getScrollY() - yoffset;
				
			//Draw Strength meter in the tail of the EditText
			canvas.drawBitmap(mStrengthMeter, x, y, mPaint);
		}
		this.invalidate();
	}
	
	/**
	 * Overridden to adjust the strength meter image to the size of the text field
	 */
	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		super.onSizeChanged(w, h, oldw, oldh);
		
		loadResources();
		//Temporarily Cache Strength Meter images in its original size
		Bitmap oldStrongImage = mStrongImage;
		Bitmap oldModerateImage = mModerateImage;
		Bitmap oldWeakImage = mWeakImage;
		Bitmap oldNoTextImage = mNoTextImage;
		
		//Scale Strength meter Images to fit within the EditText 
		scaleAllImages(h, w);
		
		//Set current strength meter image in its scaled version
		if( oldStrongImage.equals(mStrengthMeter)) {
			mStrengthMeter = mStrongImage;
		} else if (oldModerateImage.equals(mStrengthMeter) ) {
			mStrengthMeter = mModerateImage;
		} else if( oldWeakImage.equals(mStrengthMeter)) {
			mStrengthMeter = mWeakImage;
		} else if(oldNoTextImage.equals(mStrengthMeter)) {
			mStrengthMeter = mNoTextImage;
		}
		
		//Add padding to the right that is equal to the width of the strength meter image,
		//so that if the text entered is long enough to reach to the strength meter
		//it does not go under the strength meter image, and instead begins to scroll to the right
		if(this != null && mStrengthMeter != null && this.getPaddingRight() < mStrengthMeter.getWidth() ) {
			this.setPadding(this.getPaddingLeft(), this.getPaddingTop(), this.getPaddingRight() + mStrengthMeter.getWidth(), this.getPaddingBottom());
		}
	}
	
	/**
	 * Load all image resources if they have not already been loaded.
	 */
	public void loadResources() {
		//Load All Imgaes
		if(mInvalidImage == null)
			mInvalidImage = BitmapFactory.decodeResource(this.getResources(), R.drawable.strength_meter_invalid_red);
		if(mStrengthMeter == null)
			mStrengthMeter = BitmapFactory.decodeResource(this.getResources(), R.drawable.edit_text_default);
		if(mStrongImage == null)
			mStrongImage = BitmapFactory.decodeResource(this.getResources(), R.drawable.strength_meter_green);
		if(mModerateImage == null)
			mModerateImage = BitmapFactory.decodeResource(this.getResources(), R.drawable.strength_meter_yellow);
		if(mWeakImage == null)
			mWeakImage = BitmapFactory.decodeResource(this.getResources(), R.drawable.strength_meter_red);
		if(mNoTextImage == null)
			mNoTextImage = BitmapFactory.decodeResource(this.getResources(), R.drawable.strength_meter_grey);
	}
	/**
	 * Scale Strength Meter images to fit within the textfield
	 * @param height The height to use for scaling the images
	 * @param width  The width to use for scaling the images
	 */
	private void scaleAllImages(int height, int width) {
		//Compensate for the size of the stroke around the field.

		height += 5;
		width += 1;
		
		loadResources();
		
		//Scale Images
		mNoTextImage = scaleImage(mNoTextImage, height, width);
		mStrongImage = scaleImage(mStrongImage, height, width);
		mModerateImage = scaleImage(mModerateImage, height, width);
		mWeakImage = scaleImage(mWeakImage, height, width);
		mInvalidImage = scaleImage(mInvalidImage, height, width);
		
		// Define touch region based on current control size
		mTouchRegion = new Rect();
		
		mTouchRegion.left = (int) getRight() - 60;
		mTouchRegion.top = 0;
		mTouchRegion.right = mTouchRegion.left + 60;
		mTouchRegion.bottom = (int) this.getHeight();
		
	}
	
	/**
	 * Utility function to scale an image
	 * 
	 * @param bitmap Reference to the image to be scaled
	 * @param height The height to use for scaling the image
	 * @param width The width to use for scaling the image
	 * @return
	 */
	public Bitmap scaleImage(Bitmap bitmap, int height, int width) {
		float padding = 4;
		float h = height - padding;
		float w = bitmap.getWidth() * h / height;
		
		return Bitmap.createScaledBitmap(bitmap, (int) w, (int) h, true);
		
	}

	/**
	 * Returns true if the current input is valid.
	 */
	@Override
	public boolean isValid() {
		String currentInput = this.getText().toString();
		boolean isValid = false;
		
		if(mCredentialType == PASSWORD)
			isValid = InputValidator.isPasswordValid(currentInput);
		else
			isValid = InputValidator.isUserIdValid(currentInput);
		
		return isValid;
		
	}

	/**
	 * @return the mStrengthMeter
	 */
	public Bitmap getmStrengthMeter() {
		return mStrengthMeter;
	}

	/**
	 * @param mStrengthMeter the mStrengthMeter to set
	 */
	public void setmStrengthMeter(Bitmap mStrengthMeter) {
		this.mStrengthMeter = mStrengthMeter;
	}
}
