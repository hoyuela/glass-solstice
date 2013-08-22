package com.discover.mobile.card.common.utils;



import com.discover.mobile.card.CardMenuItemLocationIndex;
import com.discover.mobile.card.navigation.CardNavigationRootActivity;
import com.discover.mobile.card.navigation.CordovaWebFrag;
import com.discover.mobile.card.phonegap.plugins.JQMResourceMapper;

public class FragmentActionBarMenuTitleUtil {

	private CardNavigationRootActivity activity;
	private final String TAG = this.getClass().getSimpleName();
	private JQMResourceMapper jqmResourceMapper;
	private CordovaWebFrag cordovaWebFrag;
	private CardMenuItemLocationIndex mCardMenuLocation;
	

	/**
	 * Return the integer value of the string that needs to be displayed in the
	 * title
	 */
	public FragmentActionBarMenuTitleUtil(CardNavigationRootActivity activity) {
		this.activity = activity;
		cordovaWebFrag = (activity)
                .getCordovaWebFragInstance();
		  mCardMenuLocation = CardMenuItemLocationIndex
	                .getInstance(activity);
	}

	public int getActionBarTitle() {
		final String m_title = (activity).getActionBarTitle();
		Utils.log(TAG, "getActionBarTitle n title is " + m_title);
		if (null != m_title) {
			jqmResourceMapper = JQMResourceMapper.getInstance();

			return jqmResourceMapper.getTitleStringId(m_title);
		} else {
			return -1;
		}
	}

	public int getGroupMenuLocation(int titleName) {
		Utils.log(TAG, "inside getGroupMenuLocation ");
		int tempId = getActionBarTitle();
		final String m_title = (activity)
				.getActionBarTitle();

		if (null != m_title) {

			if (!m_title.equalsIgnoreCase(activity.getResources().getString(
					titleName))
					&& tempId == -1) {
				if (null != cordovaWebFrag.getM_currentLoadedJavascript()) {

					Utils.log(TAG, "m_currentLoadedJavascript is "
							+ cordovaWebFrag.getM_currentLoadedJavascript());
					jqmResourceMapper = JQMResourceMapper.getInstance();

					tempId = jqmResourceMapper.getTitleStringId(cordovaWebFrag
							.getM_currentLoadedJavascript());
					return mCardMenuLocation.getMenuGroupLocation(tempId);
				}
			}
		}
		return mCardMenuLocation.getMenuGroupLocation(tempId);
	}

	public int getSectionMenuLocation(int titleName) {
		Utils.log(TAG, "inside getSectionMenuLocation");
		int tempId = getActionBarTitle();
		final String m_title = (activity)
				.getActionBarTitle();

		if (null != m_title) {

			if (!m_title.equalsIgnoreCase(activity.getResources().getString(
					titleName))
					&& tempId == -1) {
				if (null != cordovaWebFrag.getM_currentLoadedJavascript()) {

					Utils.log(TAG, "m_currentLoadedJavascript is "
							+ cordovaWebFrag.getM_currentLoadedJavascript());
					jqmResourceMapper = JQMResourceMapper.getInstance();

					tempId = jqmResourceMapper.getTitleStringId(cordovaWebFrag
							.getM_currentLoadedJavascript());
					return mCardMenuLocation.getMenuSectionLocation(tempId);
				}
			}
		}

		return mCardMenuLocation.getMenuSectionLocation(tempId);
	}
}
