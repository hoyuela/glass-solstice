package com.discover.mobile.card.miles;

import android.util.Log;

import com.discover.mobile.card.CardMenuItemLocationIndex;
import com.discover.mobile.card.navigation.CardNavigationRootActivity;
import com.discover.mobile.card.phonegap.plugins.JQMResourceMapper;
import com.discover.mobile.common.BaseFragment;

public class RedeemMilesFragment extends BaseFragment{

	private JQMResourceMapper jqmResourceMapper;
	protected static final String TAG = "RedeemMilesFragment";
	 
	@Override
	public int getActionBarTitle() {
		String m_title = ((CardNavigationRootActivity)getActivity()).getActionBarTitle();
		Log.v(TAG, "getActionBarTitle n title is " + m_title);
        if (null != m_title) {
            jqmResourceMapper = JQMResourceMapper.getInstance();

            return jqmResourceMapper.getTitleStringId(m_title);
        } else
            return -1;
	}

	@Override
	public int getGroupMenuLocation() {
		return CardMenuItemLocationIndex.REDEEM_MILES_GROUP;
	}

	@Override
	public int getSectionMenuLocation() {
		return CardMenuItemLocationIndex.REDEEM_MILES_SECTION;
	}

}
