package com.discover.mobile.push.manage;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;

import android.content.Context;

import com.discover.mobile.R;
import com.discover.mobile.common.push.manage.PreferencesDetail;
import com.discover.mobile.common.push.manage.PushNotificationPrefsDetail;

/**
 * Factory for creating push manage items
 * @author jthornton
 *
 */
public class PushManageItemFactory {

	/**Left/Right padding of the items*/
	private static final int PADDING_LR = 14;
	
	/**Top/Bottom padding of the items*/
	private static final int PADDING_TB = 28;
	
	/**Activity context*/
	private Context context;
	
	/**Current push notification prefs*/
	private PushNotificationPrefsDetail prefs;
	
	/**Fragment holding these items*/
	final PushManageFragment fragment;
	
	/**
	 * Constructor for the factory
	 * @param context - activity context
	 * @param prefs - current push prefs
	 * @param fragment - fagment holding these items
	 */
	public PushManageItemFactory(final Context context,
			final PushNotificationPrefsDetail prefs,
			final PushManageFragment fragment){
		this.context = context;
		this.prefs = prefs;
		this.fragment = fragment;
	}
	
	/**
	 * Create a simple push manage item
	 * @param category - category to give to the item
	 * @param header - header to put in the item
	 * @param text - text to put in the item
	 * @return a simple push manage item
	 */
	public PushManageCategoryItem createItem(
			final String category, 
			final String header, 
			final String text ){
		
		final PushManageToogleItemSimple view = new PushManageToogleItemSimple(context, null);
		view.setCategory(category);
		view.setHeader(header);
		view.setText(text);
		view.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.notification_list_item));
		view.setPadding(PADDING_LR, PADDING_TB, PADDING_LR ,PADDING_TB);
		view.setPushChecked(isParamEnabled(prefs, category, PreferencesDetail.PUSH_PARAM));
		view.setTextChecked(isParamEnabled(prefs, category, PreferencesDetail.TEXT_PARAM));
		view.setWasTextAlreadySet(isParamEnabled(prefs, category, PreferencesDetail.TEXT_PARAM));
		view.setFragment(fragment);
		return view;
	}
	
	/**
	 * Create an edit text push manage item 
	 * @param category - category to give to the item
	 * @param header - header to put in the item
	 * @param definedAmount - defined amount to put in the amount box
	 * @param minAmount - minimum amount to show to the user
	 * @return an edit text push manage item 
	 */
	public PushManageCategoryItem createItem(
			final String category, 
			final String header, 
			final int definedAmount, 
			final int minAmount){
		
		final PushManageToggleItemEditText view = new PushManageToggleItemEditText(context, null);
		view.setCategory(category);
		view.setHeader(header);
		view.setAmount(definedAmount);
		view.setMinimumAmountText(minAmount);
		view.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.notification_list_item));
		view.setPadding(PADDING_LR, PADDING_TB, PADDING_LR ,PADDING_TB);
		view.setPushChecked(isParamEnabled(prefs, category, PreferencesDetail.PUSH_PARAM));
		view.setTextChecked(isParamEnabled(prefs, category, PreferencesDetail.TEXT_PARAM));
		view.setWasTextAlreadySet(isParamEnabled(prefs, category, PreferencesDetail.TEXT_PARAM));
		view.setFragment(fragment);
		return view;
		
	}
	
	/**
	 * Create a spinner push manage item 
	 * @param category - category to give to the item
	 * @param header - header to put in the item
	 * @param text - text to put in the item
	 * @param displayValues - values to display
	 * @return a spinner push manage item 
	 */
	public PushManageCategoryItem createItem(
			final String category, 
			final String header,
			final String text,
			final List<Integer> displayValues){
		
		final PushManageToogleItemSpinner view = new PushManageToogleItemSpinner(context, null);
		view.setCategory(category);
		view.setHeader(header);
		view.setText(text);
		view.setSpinnerDropdown(convertFromIntArray(displayValues));
		view.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.notification_list_item));
		view.setPadding(PADDING_LR, PADDING_TB, PADDING_LR ,PADDING_TB);
		view.setPushChecked(isParamEnabled(prefs, category, PreferencesDetail.PUSH_PARAM));
		view.setTextChecked(isParamEnabled(prefs, category, PreferencesDetail.TEXT_PARAM));
		view.setWasTextAlreadySet(isParamEnabled(prefs, category, PreferencesDetail.TEXT_PARAM));
		view.setFragment(fragment);
		return view;
	}
	
	/**
	 * Check to see if the pref is enabled
	 * @param prefs - current prefs
	 * @param category - category of the pref
	 * @param paramType - type of pref
	 * @return if the param is enabled
	 */
	private boolean isParamEnabled(final PushNotificationPrefsDetail prefs, 
			final String category, 
			final String paramType){
		
		final List<PreferencesDetail> items = prefs.remindersEnrollResults.preferences;
		boolean isEnabled = false;
		for(PreferencesDetail item : items){
			if(category.equalsIgnoreCase(item.prefTypeCode) && paramType.equals(item.categoryId)){
				isEnabled = true;
			}
		}
		return isEnabled;
	}
	
	/**
	 * Convert from a string array of values to an int array
	 * @param displayValues - current list of values
	 * @return int list of values
	 */
	private ArrayList<String> convertFromIntArray(final List<Integer> displayValues){
		final ArrayList<String> strings = new ArrayList<String>();
		if(null == displayValues || displayValues.isEmpty()){return strings;}
		for(Integer i : displayValues){
			strings.add(NumberFormat.getCurrencyInstance().format(i));
		}
		return strings;
	}
}
