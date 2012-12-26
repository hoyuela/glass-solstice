package com.discover.mobile.push.manage;

import com.discover.mobile.common.push.manage.PreferencesDetail;

public interface PushManageCategoryItem {

	public PreferencesDetail getPushPreferencesDetail(final boolean isMasterPushEnabled);
	
	public PreferencesDetail getTextPreferencesDetail(final boolean isMasterTextEnabled);
	
	public void setCategory(final String category);

	public String getCategory();
	
	public void setPushChecked(final boolean isChecked);
	
	public void setTextChecked(final boolean isChecked);
}
