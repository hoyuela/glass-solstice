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
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.EditText;

import com.discover.mobile.R;
import com.discover.mobile.common.analytics.AnalyticsPage;
import com.discover.mobile.common.analytics.TrackingHelper;

/**
 * Editable View which validates the text entered with a password or user id strength algorithm.
 * 
 * Must call setCredentialType() and setOtherCredential().
 * @author henryoyuela
 * 
 */
public class CredentialStrengthEditText extends EditText {
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
	 * Used for painting strength meter in onDraw method
	 */
	private Paint mPaint = new Paint();
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
	 * Bitmap used to indicate that the text entered qualifies as a strong
	 * password
	 **/
	private Bitmap mStrongImage = BitmapFactory.decodeResource(
			this.getResources(), R.drawable.strength_meter_green);
	/**
	 * Bitmap used to indicate that the text entered qualifies as a moderate
	 * strength password
	 **/
	private Bitmap mModerateImage = BitmapFactory.decodeResource(
			this.getResources(), R.drawable.strength_meter_yellow);
	/**
	 * Bitmap used to indicate that the text entered qualifies as a weak
	 * strength password
	 **/
	private Bitmap mWeakImage = BitmapFactory.decodeResource(
			this.getResources(), R.drawable.strength_meter_red);
	/**
	 * Bitmap used to indicate that no text to be evaluated
	 **/
	private Bitmap mNoTextImage = BitmapFactory.decodeResource(
			this.getResources(), R.drawable.strength_meter_grey);
	/**
	 * Bitmap rendered by EditText onDraw method based on strength of entered
	 * text (rules vary based on whether id or password)
	 **/
	private Bitmap mStrengthMeter = mNoTextImage;
	/**
	 * Contains the boundaries used to determine whether a help guide should be
	 * opened
	 **/
	private Rect mTouchRegion = new Rect();
	/**
	 * Contains what rules to use for evaluating strength of entered text. By
	 * default set to password
	 **/
	private int mCredentialType = PASSWORD;
	/*
	 * Contains a reference to an instance of PasswordStrengthEditText which refers to User ID text, if this instance is for password and vice-versa otherwise.
	 */
	private CredentialStrengthEditText mOtherCredential;

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

	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
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
		mStrengthMeter = mNoTextImage;
		this.setBackgroundResource(R.drawable.text_field_gray);
	}
	
	/**
	 * Used to set reference to other credential to make sure they do not match with this instance.
	 * Example, password cannot match user id and vice-versa
	 * 
	 * @param other Reference to other credential
	 */
	public void setOtherCredential(CredentialStrengthEditText other) {
		mOtherCredential = other;
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
		super.onTextChanged(text, start, lengthBefore, lengthAfter);
		
		//Verify level of strength of the text entered by the user
		switch (mCredentialType) {
		case PASSWORD:
			updateStrengthMeterForPass(text);
			break;
		case USERID:
			updateStrengthMeterForUID(text);
			break;
		default:
			Log.v(TAG, "Credential type not specified");
			break;
		}

		
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
		
		if( mOtherCredential != null ) {
			String thisTxt = this.getText().toString();
			String otherTxt = mOtherCredential.getText().toString();
			passAndIdMatch = thisTxt.equals(otherTxt);
		}

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

		/*TODO: Waiting on Visual Asset
		if (inputSequence.toString().startsWith("6011")) {
			looksLikeActNum = true;
			showLabelWithStringResource(errorMessageLabel, R.string.invalid_value);
		}
		*/

		/*
		 * Meets minimum requirements and combines a variation of letters,
		 * numbers, and special characters.
		 */
		if (!passAndIdMatch && !looksLikeActNum && !hasInvalidChar
				&& hasGoodLength && (hasLowerCase || hasUpperCase)
				&& hasNonAlphaNum && hasNumber) {
			mStrengthMeter = mStrongImage;
			setBackgroundResource(R.drawable.text_field_green);
		}
		/*
		 * Meets minimum requirements but does not include a variation of
		 * letters, numbers, and special characters.
		 */
		else if (!passAndIdMatch && !looksLikeActNum && !hasInvalidChar
				&& hasGoodLength) {
			mStrengthMeter = mModerateImage;
			setBackgroundResource(R.drawable.text_field_yellow);
		}
		/*
		 * Does not meet minimum requirements (not 6-16 characters, looks like
		 * an account number, or uses spaces or the following characters:
		 * (`)(')(")(\))
		 */
		else {
			mStrengthMeter = mWeakImage;
			setBackgroundResource(R.drawable.text_field_red);
		}
		
		//Force an onDraw
		this.invalidate();

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
		
		if( mOtherCredential != null ) {
			String thisTxt = this.getText().toString();
			String otherTxt = mOtherCredential.getText().toString();
			passAndIdMatch = thisTxt.equals(otherTxt);
		}

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
			setBackgroundResource(R.drawable.text_field_green);
		}
		/*
		 * Meets minimum requirements but does not include a variation of upper
		 * case letters, lower case letters, numbers, and special characters.
		 */
		else if (!passAndIdMatch && hasGoodLength && hasNumber
				&& (hasUpperCase || hasLowerCase)) {
			mStrengthMeter = mModerateImage;
			setBackgroundResource(R.drawable.text_field_yellow);
		}
		/*
		 * Does not meet minimum requirements (not 8-32 characters, does not
		 * contain at least 1 letter and 1 number, or is the word "password").
		 */
		else {
			mStrengthMeter = mWeakImage;
			setBackgroundResource(R.drawable.text_field_red);
		}
		

	}

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

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);

		//Calculate Position of Strength Meter
		float xoffset = 14;
		float yoffset = 1.25f;
		float padding = 4;
		float h = getHeight() - padding;
		float w = mStrengthMeter.getWidth() * h / getHeight();
		float x = getRight() - w - xoffset;
		float y = this.getScrollY() + yoffset;
		
		
		//Paint Strength Meter
		Bitmap scaledSM = Bitmap.createScaledBitmap(mStrengthMeter, (int) w, (int) h, true);
		canvas.drawBitmap(scaledSM, x, y, mPaint);

		// Define touch region based on current control size
		mTouchRegion.left = (int) getRight() - 60;
		mTouchRegion.top = 0;
		mTouchRegion.right = mTouchRegion.left + 60;
		mTouchRegion.bottom = (int) h;
		
		Log.v(TAG, "Right=" +x);
	}

}
