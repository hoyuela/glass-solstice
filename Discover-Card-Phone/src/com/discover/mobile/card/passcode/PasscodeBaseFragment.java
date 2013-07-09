package com.discover.mobile.card.passcode;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentManager;
import android.text.Editable;
import android.text.Html;
import android.text.TextWatcher;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.discover.mobile.card.CardMenuItemLocationIndex;
import com.discover.mobile.card.R;
import com.discover.mobile.card.common.sharedata.CardShareDataStore;
import com.discover.mobile.card.common.ui.modals.EnhancedContentModal;
import com.discover.mobile.card.home.HomeSummaryFragment;
import com.discover.mobile.card.passcode.event.OnPasscodeErrorEventListener;
import com.discover.mobile.card.passcode.event.OnPasscodeSubmitEventListener;
import com.discover.mobile.card.passcode.event.OnPasscodeSuccessEventListener;
import com.discover.mobile.card.services.auth.AccountDetails;
import com.discover.mobile.common.BaseFragment;
import com.discover.mobile.common.DiscoverActivityManager;
import com.discover.mobile.common.nav.NavigationRootActivity;
import com.discover.mobile.common.utils.PasscodeUtils;

public abstract class PasscodeBaseFragment extends BaseFragment implements View.OnKeyListener, OnPasscodeErrorEventListener,
OnPasscodeSubmitEventListener, OnPasscodeSuccessEventListener {

	@Override
	public int getActionBarTitle() {
        return R.string.sub_section_title_passcode;
	}

	@Override
	public int getGroupMenuLocation() {
		return CardMenuItemLocationIndex.PROFILE_AND_SETTINGS_GROUP;
	}

	@Override
	public int getSectionMenuLocation() {
		return CardMenuItemLocationIndex.PASSCODE_SECTION;
	}
	
	public void printFragmentsInBackStack() {

		final FragmentManager fragManager = getActivity().getSupportFragmentManager();
		final int fragCount = fragManager.getBackStackEntryCount();
		if (fragCount > 0) {
			for (int i = 0; i < fragCount; i++) {
				if (null != fragManager.getBackStackEntryAt(i).getName())
					Log.v(TAG, fragManager
							.getBackStackEntryAt(i).getName());
			}
		}
	}
	
	protected Context mContext;

	protected int validationDelay = 1000;

	// private static final String digits = "0123456789";
	private static final int[] fieldIds;
	// protected TextView[] fieldTVs = new TextView[4];
	protected EditText[] fieldTVs = new EditText[4];
	protected EditText holdFocus;
	private static String TAG = "PasscodeBaseFragment";
	protected ImageView validationIV;
	protected TextView headerTV;
	protected TextView passcodeGuidelinesTV;
	// device tokens
	public static final String PREFS_HAS_SEEN_OVERVIEW = "hasUserSeenOverview";

	public static final int KEY_DELETE = 67;

	protected PasscodeUtils pUtils;
	protected boolean isStopping = false;

	static {
		fieldIds = new int[4];
		fieldIds[0] = R.id.passcode01;
		fieldIds[1] = R.id.passcode02;
		fieldIds[2] = R.id.passcode03;
		fieldIds[3] = R.id.passcode04;
	}

	private void setupAllFields() {
		setupPasscodeField(0);
		setupPasscodeField(1);
		setupPasscodeField(2);
		setupSubmit();
	}
	
	protected void passcodeResponse(boolean isSuccess) {
		Log.v(TAG, "PasscodeResponse");
		if (isSuccess) {
			guiValidationSuccess();
			new Handler().postDelayed(new Runnable() {
				public void run() {
					onPasscodeSuccessEvent();
					guiValidationReset();
					//call clear after success so success can leverage getPasscodeString()
					clearAllFields();
				}
			}, validationDelay);
		} else {
			guiValidationError();
			new Handler().postDelayed(new Runnable() {
				public void run() {
					onPasscodeErrorEvent();
					guiValidationReset();
					clearAllFields();
				}
			}, validationDelay);
		}
	}
	
	private void setupSubmit() {
		Log.v(TAG, "Setup Submit");
		TextView localTextView = fieldTVs[3];
		// for hardware keys
		localTextView.setOnKeyListener(this);
		localTextView.setTransformationMethod(PasswordTransformationMethod
				.getInstance());
		// for software keyboards
		localTextView.addTextChangedListener(new TextWatcher() {
			// Logic to mask input and go to next item
			public void afterTextChanged(Editable paramAnonymousEditable) {
				Log.v(TAG, "Submit action fired");

				// ensure this field passed validation (i.e. not comma or bad
				// character)
				if (!validatePasscodeField(3, paramAnonymousEditable)) {
					return;
				}

				// move focus to dummy field
				holdFocus.requestFocus();

				// dynamic success/error functions
				onPasscodeSubmitEvent();
			}

			// REQUIRED EVEN THOUGHT LEFT EMPTY
			public void beforeTextChanged(
					CharSequence paramAnonymousCharSequence,
					int paramAnonymousInt1, int paramAnonymousInt2,
					int paramAnonymousInt3) {
			}

			// REQUIRED EVEN THOUGHT LEFT EMPTY
			public void onTextChanged(CharSequence paramAnonymousCharSequence,
					int paramAnonymousInt1, int paramAnonymousInt2,
					int paramAnonymousInt3) {
			}

		});
	}

	protected void clearAllFields() {
		TextView[] arrayOfTextView = fieldTVs;
		int i = arrayOfTextView.length;
		for (int j = 0; j < i; j++) {
			clearField(arrayOfTextView[j]);
		}
		fieldTVs[0].requestFocus();
	}

	// remove text from field
	private void clearField(TextView paramTextView) {
//		Log.v(TAG, "Calling clearField()");
		paramTextView.setText("");
	}

	// deletes most recent input
	private TextView deleteLatestInput() {
		for (int i = fieldTVs.length - 1; i >= 0; i--) {
			if (fieldTVs[i].length() > 0) {
				clearField(fieldTVs[i]);
				return fieldTVs[i];
			}
		}
		return fieldTVs[0];
	}

	// advances input to next field
	private TextView advanceInput(int currentIndex) {
		if (currentIndex < fieldTVs.length - 1) {
			return fieldTVs[currentIndex + 1];
		} else if (currentIndex < 0) {
			return fieldTVs[0];
		} else {
			return fieldTVs[fieldTVs.length - 1];
		}
	}
	
	
	@Override
	public void onCreate(final Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		pUtils = new PasscodeUtils(this.getActivity().getApplicationContext());
	}
	
	public void onStop() {
		super.onStop();
		Log.v(TAG, "Stop");
		this.isStopping = true;
	}
	
	public void onResume() {
		super.onResume();
//		Log.v(TAG, "Resume");
		// this also helps when back button navigates to resume previous
		// activity
		clearAllFields();
		// show keyboard
		InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.showSoftInput(fieldTVs[0], InputMethodManager.SHOW_IMPLICIT);
	}

	@Override
	public View onCreateView(final LayoutInflater inflater,
			final ViewGroup container, final Bundle savedInstanceState) {
		final View view = inflater.inflate(R.layout.passcode_base_activity,
				null);
		validationIV = ((ImageView) view.findViewById(R.id.validation));
		validationIV.setVisibility(View.INVISIBLE);
		passcodeGuidelinesTV = ((TextView) view.findViewById(R.id.passcodeGuidelines));
		passcodeGuidelinesTV
				.setOnClickListener(new View.OnClickListener() {
					public void onClick(View v) {
						showPasscodeGuidelines();
//						dialogHelper(MODAL_PASSCODE_GUIDELINES, "Close", false, new NoNavigateAction());
					}
				});
		passcodeGuidelinesTV.setVisibility(View.INVISIBLE);
		headerTV = ((TextView) view.findViewById(R.id.headerTV));
		holdFocus = (EditText) view.findViewById(R.id.holdFocus);

		mContext = getActivity();
		for (int i = 0; i < 4; i++) {
			fieldTVs[i] = ((EditText) view.findViewById(fieldIds[i]));
		}
		
		//TODO sgoff0 - look into, seems to stop the problem of double submit on removing fragment from back stack
//		if (!isStopping) {
			setupAllFields();
			clearAllFields();
//		}
		return view;
	}

	protected void showPasscodeGuidelines() {
//		dialogHelper(MODAL_PASSCODE_GUIDELINES, "Close", false, new NoNavigateAction());
		final Context context = DiscoverActivityManager.getActiveActivity();
		final EnhancedContentModal modal = new EnhancedContentModal(context, 
				R.string.passcode_dialog_guidelines_title, 
				R.string.passcode_dialog_guidelines_content, 
				R.string.ok);
		modal.hideNeedHelpFooter();
		((NavigationRootActivity)context).showCustomAlert(modal);
	}
	
	public boolean onKey(View paramView, int paramInt, KeyEvent paramKeyEvent) {
		// TODO understand better what this does
		if (paramKeyEvent.getAction() == 0) {
			return false;
		}
		// delete key
		if (paramInt == KEY_DELETE) {
			deleteLatestInput().requestFocus();
		}
		return super.getActivity().onKeyUp(paramInt, paramKeyEvent);
	}

	// returns input fields as string
	protected String getPasscodeString() {
//		Log.v(TAG, "Calling passcodeString()");
		String retVal = "";
		for (int i = 0; i < fieldTVs.length; i++) {
			retVal += fieldTVs[i].getText();
		}
		return retVal;
	}

	protected boolean isPasscodeValidLocally(String passcode) {
		return PasscodeUtils.isPasscodeValidLocally(passcode);
	}

	/*
	 * Returns true if at id paramInt is valid 0-9. If not it clears the field
	 * and returns false.
	 */
	protected boolean validatePasscodeField(int paramInt, Editable paramEditable) {
		TextView localTextView = PasscodeBaseFragment.this.fieldTVs[paramInt];
		// if transformation method isn't password set it anyways
		if (!(localTextView.getTransformationMethod() instanceof PasswordTransformationMethod)) {
			localTextView.setTransformationMethod(PasswordTransformationMethod
					.getInstance());
		}

		// validate input is exactly 1 character and 0-9
		if (isCharNumeric(paramEditable)) {
			advanceInput(paramInt).requestFocus();
			return true;
		} else if (isCharEmpty(paramEditable)) {
			// do nothing
			return false;
		} else {
			// invalid input
			clearField(localTextView);
			return false;
		}
	}

	/*
	 * Executed on the first 3 input fields to condition them handles password
	 * masking handles navigating to next item
	 */
	protected void setupPasscodeField(final int paramInt) {
//		Log.v(TAG, "Entering setup Passcode");
		TextView localTextView = fieldTVs[paramInt];
		localTextView.setOnKeyListener(this);
		localTextView.setTransformationMethod(PasswordTransformationMethod
				.getInstance());
		localTextView.addTextChangedListener(new TextWatcher() {
			// Logic to mask input and go to next item
			public void afterTextChanged(Editable paramAnonymousEditable) {
				validatePasscodeField(paramInt, paramAnonymousEditable);
			}

			// REQUIRED EVEN THOUGHT LEFT EMPTY
			public void beforeTextChanged(
					CharSequence paramAnonymousCharSequence,
					int paramAnonymousInt1, int paramAnonymousInt2,
					int paramAnonymousInt3) {
			}

			// REQUIRED EVEN THOUGHT LEFT EMPTY
			public void onTextChanged(CharSequence paramAnonymousCharSequence,
					int paramAnonymousInt1, int paramAnonymousInt2,
					int paramAnonymousInt3) {
			}

		});
	}

	private boolean isCharEmpty(CharSequence paramCharSequence) {
		return (paramCharSequence.length() == 0);
	}

	private boolean isCharNumeric(CharSequence paramCharSequence) {
		if (paramCharSequence.length() != 1) {
			return false;
		}
		return new String("1234567890").contains(paramCharSequence);
	}

	// Validation
	private void guiValidationSuccess() {
		for (int i = 0; i < 4; i++) {
			fieldTVs[i].setBackgroundDrawable(getResources().getDrawable(R.drawable.rectangle_green));
		}
		validationIV.setImageResource(R.drawable.tick_green);
		validationIV.setVisibility(View.VISIBLE);
	}

	private void guiValidationError() {
		for (int i = 0; i < 4; i++) {
			fieldTVs[i].setBackgroundDrawable(getResources().getDrawable(R.drawable.rectangle_red));
		}
		validationIV.setImageResource(R.drawable.x_red);
		validationIV.setVisibility(View.VISIBLE);
	}
	
	private void guiValidationReset(){
		for (int i = 0; i < 4; i++) {
			fieldTVs[i].setBackgroundDrawable(getResources().getDrawable(R.drawable.rectangle));
		}
		validationIV.setVisibility(View.INVISIBLE);
	}

	protected String getPasscodeToken() {
		return pUtils.getPasscodeToken();
	}

	protected void deletePasscodeToken() {
		pUtils.deletePasscodeToken();
	}

	protected void createPasscodeToken(String token) {
		pUtils.createPasscodeToken(token);
	}

	protected class NavigateACHomeAction implements Runnable {
		public NavigateACHomeAction() {}
		@Override
		public void run() {
			makeFragmentVisible(new HomeSummaryFragment());
		}
	}
	
	protected class NavigatePasscodeLandingAction implements Runnable {
		public NavigatePasscodeLandingAction() {}
		@Override
		public void run() {
			makeFragmentVisible(new PasscodeLandingFragment());
		}
	}
	
	protected class NoNavigateAction implements Runnable {
		public NoNavigateAction() {}
		@Override
		public void run() {
			//don't navigate anywhere
		}
	}
	
	protected void setHeaderText(int stringId) {
		headerTV
				.setText(Html.fromHtml(getResources().getString(stringId)));
	}

//	protected void slideTransition() {
//		getActivity().overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
//	}
	
	protected void storeFirstName() {
//		PasscodeUtils pUtils = new PasscodeUtils(getActivity().getApplicationContext());
		final CardShareDataStore cardShareDataStoreObj = CardShareDataStore
                .getInstance(getActivity());
		AccountDetails accountDetails = (AccountDetails) cardShareDataStoreObj
                .getValueOfAppCache(getActivity().getString(R.string.account_details));
		String fname = "";
		if (accountDetails != null){
			if (accountDetails.mailingAddress != null && accountDetails.mailingAddress.firstName != null) {
				fname = accountDetails.mailingAddress.firstName;
			} else if (accountDetails.primaryCardMember != null && accountDetails.primaryCardMember.nameOnCard != null) {
				fname = accountDetails.primaryCardMember.nameOnCard;
				String[] splitName = fname.split(" ");
				if (splitName.length > 0) {
					fname = splitName[0];
				}
			}
		}
		pUtils.storeFirstName(fname);
	}
	
}