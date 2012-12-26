package com.discover.mobile.push.manage;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.res.Resources;
import android.os.Build;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.discover.mobile.R;
import com.discover.mobile.RoboSherlockFragment;
import com.discover.mobile.common.callback.AsyncCallback;
import com.discover.mobile.common.callback.GenericAsyncCallback;
import com.discover.mobile.common.push.manage.GetNotificationPreferences;
import com.discover.mobile.common.push.manage.PostNotificationPreferences;
import com.discover.mobile.common.push.manage.PostPreferencesDetail;
import com.discover.mobile.common.push.manage.PreferencesDetail;
import com.discover.mobile.common.push.manage.PushNotificationPrefsDetail;
import com.xtify.sdk.api.XtifySDK;

/**
 * Fragment that is the push notification manage screen.  Uses the push save header, 
 * push toggle item and push save item.  It will compile them into the list.
 * 
 * @author jthornton
 *
 */
public class PushManageFragment extends RoboSherlockFragment{
	
	/**Push prefs retrieved from the server*/
	private PushNotificationPrefsDetail prefs;
	
	private boolean isTextEnabled = false;

	private boolean isPushEnabled = false;
	
	private ImageView enableText;
	
	private ImageView enablePush;
	
	private TextView phoneNumber;
	
	private PushManageHeaderItem manageHeader;
	
	private LinearLayout manageList;
	
	private PushManageHeaderItem monitorHeader;
	
	private LinearLayout monitorList;
	
	private PushManageHeaderItem maximizeHeader;
	
	private LinearLayout maximizeList;
	
	private Resources res;
	
	private Context context;
	
	private List<PushManageCategoryItem> categoriesList;
	
	private PushManageSaveView saveItem;
	
	@Override
	public View onCreateView(final LayoutInflater inflater, final ViewGroup container,
			final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		final View mainView = inflater.inflate(R.layout.push_manage_main_layout, null);
		manageHeader = (PushManageHeaderItem)mainView.findViewById(R.id.manage_you_account_header);
		manageList = (LinearLayout)mainView.findViewById(R.id.manage_account_list_preferences);
		monitorHeader = (PushManageHeaderItem)mainView.findViewById(R.id.monitor_spending_list_header);
		monitorList = (LinearLayout)mainView.findViewById(R.id.monitor_spending_list_preferences);
		maximizeHeader = (PushManageHeaderItem)mainView.findViewById(R.id.maximize_rewards_list_header);
		maximizeList = (LinearLayout)mainView.findViewById(R.id.maximize_rewards_list_preferences);
		phoneNumber = (TextView)mainView.findViewById(R.id.phone_number);
		enableText = (ImageView)mainView.findViewById(R.id.toggle_enable_texts);
		enablePush = (ImageView)mainView.findViewById(R.id.toggle_enable_push);
		saveItem = (PushManageSaveView)mainView.findViewById(R.id.save_view);
		final TextView termsLaunch = (TextView) mainView.findViewById(R.id.clickable_view);
		
		final Button save = (Button) mainView.findViewById(R.id.notification_save_button);
		save.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(final View v) {
				savePreferences();
			}
		});
		
		enablePush.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(final View v) {
				togglePushSwitch();
			}
		});
		
		enableText.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(final View v) {
				toggleTextSwitch();
			}	
		});
		
		termsLaunch.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(final View v) {
				showTermsAndConditions();
			}	
		});
		
		context = this.getActivity();
		
		res = context.getResources();
		createHeaders();
		setListsInHeader();
		
		return mainView;
	}
	

	/**
	 * Toggle the master push enable switch of and on
	 */
	protected void togglePushSwitch() {
		isPushEnabled = (isPushEnabled) ? false : true;
		toggleSwitch(enablePush, isPushEnabled);
	}
	
	/**
	 * Toggle the master text enable switch of and on
	 */
	protected void toggleTextSwitch() {
		isTextEnabled = (isTextEnabled) ? false : true;
		toggleSwitch(enableText, isTextEnabled);
		
	}
	
	/**
	 * Toggle a switch according to its current state
	 * @param image - imageview of the state to switch
	 * @param isEnabled - if the image view is currently enabled
	 */
	private void toggleSwitch(final ImageView image, final boolean isEnabled){
		if(isEnabled){
			image.setBackgroundDrawable(res.getDrawable(R.drawable.swipe_on));
		}else{
			image.setBackgroundDrawable(res.getDrawable(R.drawable.swipe_off));
		}
	}
	
	/**
	 * On resume of the page make sure the fragment gets an updated version of the prefs
	 */
	@Override
	public void onResume(){
		super.onResume();
		categoriesList = new ArrayList<PushManageCategoryItem>();
		
		final AsyncCallback<PushNotificationPrefsDetail> callback = 
				GenericAsyncCallback.<PushNotificationPrefsDetail>builder(this.getActivity())
				.showProgressDialog(getResources().getString(R.string.push_progress_get_title), 
									getResources().getString(R.string.push_progress_registration_loading), 
									true)
				.withSuccessListener(new GetPushPrefsSuccessListener(this))
				.withErrorResponseHandler(new GetPushPrefsErrorResponseHandler())
				.build();
		
		new GetNotificationPreferences(this.context, callback).submit();
	}
	
	/**
	 * Associate the correct linear layout with it's header
	 */
	private void setListsInHeader() {
		manageHeader.setList(manageList);
		monitorHeader.setList(monitorList);
		maximizeHeader.setList(maximizeList);	
	}
	
	/**
	 * Create a list from the information
	 * @param list - list to put the items in
	 * @param categories - list of categories to associate with the items
	 * @param headers - list of headers to display in the items
	 * @param texts - list of text values to display in the items
	 */
	private void createList(final LinearLayout list, 
			final String[] categories, 
			final String[] headers, 
			final String[] texts){
		
		final int lengthOfHeaders = headers.length;
		
		for(int i = 0; i < lengthOfHeaders; i++) {
			final PushManageToogleItemSimple item = createSimpleTextItem(categories[i], headers[i], texts[i]);
			if(showCategory(item.getCategory())){
				list.addView(item);
			}
			categoriesList.add(item);
		}
	}
	
	private void createMonitorList() {
		final PushManageToggleItemEditText purchase = createEditTextItem(
				res.getString(R.string.purchase_amount_category), 
				res.getString(R.string.purchase_amount_header),
				prefs.remindersEnrollResults.tamtDefAmt,
				prefs.remindersEnrollResults.tamtMinAmt);		
		
		final PushManageToggleItemEditText balance = createEditTextItem(
				res.getString(R.string.balance_amount_category), 
				res.getString(R.string.balance_amount_header),
				prefs.remindersEnrollResults.balanceDefAmt,
				prefs.remindersEnrollResults.balanaceMinAmt);
		
		final PushManageToogleItemSpinner creditLine = createSpinnerItem(
				res.getString(R.string.credit_line_ammount_category), 
				res.getString(R.string.credit_line_ammount_header),
				res.getString(R.string.credit_line_ammount_text),
				prefs.remindersEnrollResults.crltAmtOptions);
		
		if(showCategory(purchase.getCategory())){
			monitorList.addView(purchase);
		}
		categoriesList.add(purchase);
		if(showCategory(balance.getCategory())){
			monitorList.addView(balance);
		}
		categoriesList.add(balance);
		if(showCategory(creditLine.getCategory())){
			monitorList.addView(creditLine);
		}
		categoriesList.add(creditLine);
		
	}
	
	
	private void createRewardsList() {
		final PushManageToogleItemSimple rewardsReminder = createSimpleTextItem(
																res.getString(R.string.rewards_reminder_category),
																res.getString(R.string.rewards_reminder_header),
																res.getString(R.string.rewards_reminder_text));
		
		final PushManageToggleItemEditText cashBackBonus = createEditTextItem(
																res.getString(R.string.cashback_bonus_category),
																res.getString(R.string.cashback_bonus_header),
																prefs.remindersEnrollResults.mrrwDefAmt,
																prefs.remindersEnrollResults.mrrwMinAmt);
		cashBackBonus.hideMinimumAmount();
		if(showCategory(rewardsReminder.getCategory())){
			maximizeList.addView(rewardsReminder);
		}
		categoriesList.add(rewardsReminder);
		if(showCategory(cashBackBonus.getCategory())){
			maximizeList.addView(cashBackBonus);
		}
		categoriesList.add(cashBackBonus);
		
	}
	
	private boolean showCategory(final String category){
		return prefs.remindersEnrollResults.codesToDisplay.contains(category);
	}

	private boolean isParamEnabled(final String category, final String paramType){
		final List<PreferencesDetail> items = prefs.remindersEnrollResults.preferences;
		boolean isEnabled = false;
		for(PreferencesDetail item : items){
			if(category.equalsIgnoreCase(item.prefTypeCode) && paramType.equals(item.categoryId)){
				isEnabled = true;
			}
		}
		return isEnabled;
	}
	
	private PushManageToogleItemSimple createSimpleTextItem(final String category, 
			final String header, 
			final String text ){
		
		final PushManageToogleItemSimple view = new PushManageToogleItemSimple(context, null);
		view.setCategory(category);
		view.setHeader(header);
		view.setText(text);
		view.setBackgroundDrawable(res.getDrawable(R.drawable.notification_list_item));
		//FIXME: Pull these out into a dimensions file
		view.setPadding(14, 28, 14 ,28);
		view.setPushChecked(isParamEnabled(category, PreferencesDetail.PUSH_PARAM));
		view.setTextChecked(isParamEnabled(category, PreferencesDetail.TEXT_PARAM));
		view.setWasTextAlreadySet(isParamEnabled(category, PreferencesDetail.TEXT_PARAM));
		return view;
	}
	
	
	private PushManageToggleItemEditText createEditTextItem(final String category, 
			final String header, 
			final int definedAmount, 
			final int minAmount){
		
		final PushManageToggleItemEditText view = new PushManageToggleItemEditText(context, null);
		view.setCategory(category);
		view.setHeader(header);
		view.setAmount(Integer.toString(definedAmount));
		view.setMinimumAmountText(Integer.toString(minAmount));
		view.setBackgroundDrawable(res.getDrawable(R.drawable.notification_list_item));
		//FIXME: Pull these out into a dimensions file
		view.setPadding(14, 28, 14 ,28);
		view.setPushChecked(isParamEnabled(category, PreferencesDetail.PUSH_PARAM));
		view.setTextChecked(isParamEnabled(category, PreferencesDetail.TEXT_PARAM));
		view.setWasTextAlreadySet(isParamEnabled(category, PreferencesDetail.TEXT_PARAM));
		return view;
	}
	
	private PushManageToogleItemSpinner createSpinnerItem(final String category, 
			final String header,
			final String text,
			final List<Integer> displayValues){
		
		final PushManageToogleItemSpinner view = new PushManageToogleItemSpinner(context, null);
		view.setCategory(category);
		view.setHeader(header);
		view.setText(text);
		view.setSpinnerDropdown(convertFromIntArray(displayValues));
		//TODO: check to see if that is in the list of categories to show
		view.setBackgroundDrawable(res.getDrawable(R.drawable.notification_list_item));
		//FIXME: Pull these out into a dimensions file
		view.setPadding(14, 28, 14 ,28);
		view.setPushChecked(isParamEnabled(category, PreferencesDetail.PUSH_PARAM));
		view.setTextChecked(isParamEnabled(category, PreferencesDetail.TEXT_PARAM));
		view.setWasTextAlreadySet(isParamEnabled(category, PreferencesDetail.TEXT_PARAM));
		return view;
	}
	
	private ArrayList<String> convertFromIntArray(final List<Integer> displayValues){
		final ArrayList<String> strings = new ArrayList<String>();
		for(Integer i : displayValues){
			strings.add("$ " + Integer.toString(i));
		}
		return strings;
	}

	private void createHeaders() {
		manageHeader.setHeader(res.getString(R.string.manage_your_account_title));
		monitorHeader.setHeader(res.getString(R.string.monitor_your_spending_title));
		maximizeHeader.setHeader(res.getString(R.string.maximize_your_rewards_title));	
	}
	
	private String toPhoneNumber(final String number){
		return number.substring(0, 3) + "-" + number.substring(3, 6) + "-" + number.substring(6, 10);
	}
	
	/**
	 * Clear all the lists so that they can be repopulated
	 */
	private void clearLists() {
		this.manageList.removeAllViews();
		this.maximizeList.removeAllViews();
		this.monitorList.removeAllViews();
	}

	public void showTermsAndConditions(){
		makeFragmentVisible(new PushTermsAndConditionsFragment());
	}
	
	/**
	 * Return the integer value of the string that needs to be displayed in the title
	 */
	@Override
	public int getActionBarTitle() {
		return R.string.manage_push_fragment_title;
	}
	
	public void setPrefs(final PushNotificationPrefsDetail detail){
		this.prefs = detail;
	}
	
	public void displayPrefs(){
		if(null == this.prefs){return;}
		clearLists();
		categoriesList.clear();
		phoneNumber.setText(toPhoneNumber(prefs.remindersEnrollResults.phoneNumber));
		createList(manageList, 
				res.getStringArray(R.array.manage_you_accounts_categories),
				res.getStringArray(R.array.manage_your_accounts_headers),
				res.getStringArray(R.array.manage_your_accounts_text));
		createMonitorList();
		createRewardsList();;
		//hideSavebar();
	}

	public void savePreferences(){
		final AsyncCallback<PostPreferencesDetail> callback = 
				GenericAsyncCallback.<PostPreferencesDetail>builder(this.getActivity())
				.showProgressDialog(getResources().getString(R.string.push_progress_get_title), 
									getResources().getString(R.string.push_progress_registration_loading), 
									true)
				.withSuccessListener(new PostPrefsSuccessListener(this))
				//TODO:  Handle this error appropriately
				.withErrorResponseHandler(new PushPrefsErrorHandler(this))
				.build();
		
		new PostNotificationPreferences(this.context, callback, getPreferences()).submit();
	}
	
	private PostPreferencesDetail getPreferences(){
		final TelephonyManager telephonyManager =
				(TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
		final PostPreferencesDetail post = new PostPreferencesDetail();
		final String version = Build.VERSION.RELEASE;
		final String phone = phoneNumber.getText().toString().replaceAll("-", "");
		
		post.setVid(XtifySDK.getXidKey(context));
		post.setOs(PostPreferencesDetail.DEFAULT_OS);
		
		if(null != version){
			post.setOsVersion(version);
		} else{
			post.setOsVersion(PostPreferencesDetail.DEFAULT_VERSION);
		}
		post.setDeviceID(telephonyManager.getDeviceId());
		post.setRegStatus(PostPreferencesDetail.ACCEPT);
		post.setAccntOverrideInd(PostPreferencesDetail.OVERRIDE_YES);
		post.setPhoneNumber(phone);
		post.setCarrier(getCarrier());
		post.setPrefs(getPrefs());
		return post;
	}
	


	private List<PreferencesDetail> getPrefs() {
		final List<PreferencesDetail> newPrefs = new ArrayList<PreferencesDetail>();
		final boolean textIsEnabled = (this.isTextEnabled && (null != prefs.remindersEnrollResults.phoneNumber));
		for(PushManageCategoryItem item : categoriesList){
			newPrefs.add(item.getTextPreferencesDetail(textIsEnabled));
			newPrefs.add(item.getPushPreferencesDetail(isPushEnabled));
		}
		return newPrefs;
	}


	private String getCarrier() {
		// TODO Auto-generated method stub
		return null;
	}


	public void hideSavebar() {
		saveItem.setVisibility(View.GONE);
	}
}
