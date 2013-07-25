package com.discover.mobile.card.passcode;

import android.os.Bundle;
import android.util.Log;

import com.discover.mobile.card.CardMenuItemLocationIndex;
import com.discover.mobile.card.R;
import com.discover.mobile.common.BaseFragment;

/**
 * NO LONGER USED.  Has logic in case accidentally called to leverage the new
 * PasscodeRouter. Can't be deleted as it's a placeholder for CardNavigationRootActivity.
 * Fragment (no UI) that detects which page should be shown next
 * @author StevenI7
 *
 */
public class PasscodeLandingFragment extends BaseFragment {
	private static String TAG = "PasscodeLandingFragment";
	
	@Override
	public void onCreate(final Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
        Log.v(TAG, "onCreate");
        new PasscodeRouter(this).getStatusAndRoute();
	}

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
	
}