package com.discover.mobile.push.manage;

import com.discover.mobile.common.push.manage.PostPrefDetail;

/**
 * Interface to bind the toggle items to
 * @author jthornton
 *
 */
public interface PushManageCategoryItem {

	/**
	 * Get the push preference detail
	 * @param isMasterPushEnabled - if the master push switch is on
	 * @return the push preference detail
	 */
	public PostPrefDetail getPushPreferencesDetail(final boolean isMasterPushEnabled);
	
	/**
	 * Get the text preference detail
	 * @param isMasterTextEnabled - if the master text switch is on
	 * @return the text preference detail
	 */
	public PostPrefDetail getTextPreferencesDetail(final boolean isMasterTextEnabled);
	
	/**
	 * Set the category of the item
	 * @param category - the category assigned to this item
	 */
	public void setCategory(final String category);

	/**
	 * Get the category of the item
	 * @return - the category of the item
	 */
	public String getCategory();
	
	/**
	 * Set if the push item is supposed to be checked
	 * @param isChecked - true if the push item is supposed to be checked
	 */
	public void setPushChecked(final boolean isChecked);
	
	/**
	 * Set if the text item is supposed to be checked
	 * @param isChecked - true if the text item is supposed to be checked
	 */
	public void setTextChecked(final boolean isChecked);
	
	/**
	 * Set if the text preference was already set active by what was received from the server
	 * @param isAlreadySet - if the text preference was already set active by what was received from the server
	 */
	public void setWasTextAlreadySet(final boolean isAlreadySet);
	
	/**
	 * Check to see if the category is valid
	 * @return if the category is valid
	 */
	public boolean isValid();
}
