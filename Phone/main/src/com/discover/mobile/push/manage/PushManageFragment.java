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
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.discover.mobile.BaseFragment;
import com.discover.mobile.R;
import com.discover.mobile.common.callback.AsyncCallback;
import com.discover.mobile.common.callback.GenericAsyncCallback;
import com.discover.mobile.common.push.manage.GetNotificationPreferences;
import com.discover.mobile.common.push.manage.PostNotificationPreferences;
import com.discover.mobile.common.push.manage.PostPrefDetail;
import com.discover.mobile.common.push.manage.PostPreferencesDetail;
import com.discover.mobile.common.push.manage.PreferencesDetail;
import com.discover.mobile.common.push.manage.PushNotificationPrefsDetail;
import com.discover.mobile.navigation.NavigationRootActivity;
import com.discover.mobile.utils.CommonUtils;
import com.xtify.sdk.api.XtifySDK;

/**
 * Fragment that is the push notification manage screen.  Uses the push save header, 
 * push toggle item and push save item.  It will compile them into the list.
 * 
 * @author jthornton
 *
 */
public class PushManageFragment extends BaseFragment{
	
	/**Static string representing a - */
	private static final String HYPEN = "-";
	
	/**Push prefs retrieved from the server*/
	private PushNotificationPrefsDetail prefs;
	
	/**Boolean holding the state of the master text toggle switch*/
	private boolean isMasterTextEnabled = false;

	/**Boolean holding the state of the master push toggle switch*/
	private boolean isMasterPushEnabled = false;
	
	/**ImageView representing the state of the master text toggle switch*/
	private ImageView enableText;

	/**ImageView representing the state of the master push toggle switch*/
	private ImageView enablePush;
	
	/**TextView holding the phone number*/
	private TextView phoneNumber;
	
	/**Header holding the manage you account list*/
	private PushManageHeaderItem manageHeader;
	
	/**Linear layout holding a list of toggle items for the manage account*/
	private LinearLayout manageList;

	/**Header holding the monitor your spending list*/
	private PushManageHeaderItem monitorHeader;

	/**Linear layout holding a list of toggle items for the monitor your spending*/
	private LinearLayout monitorList;

	/**Header holding the rewards list*/
	private PushManageHeaderItem maximizeHeader;

	/**Linear layout holding a list of toggle items for the rewards*/
	private LinearLayout maximizeList;
	
	/**Application resources*/
	private Resources res;
	
	/**Activity context*/
	private Context context;
	
	/**List of all the categories being displayed*/
	private List<PushManageCategoryItem> categoriesList;
	
	/**Relative layout representing the save item*/
	private PushManageSaveView saveItem;
	
	/**Factory for creating the push manage items*/
	private PushManageItemFactory factory;
	
	/**Hidden field used to show messages*/
	private TextView hiddenField;
	
	/**
	 * Create the view
	 * @param inflater - inflater that will inflate the layout
	 * @param container - parent layout
	 * @param savedInstanceState - bundle holding the state of the fragment
	 */
	@Override
	public View onCreateView(final LayoutInflater inflater, final ViewGroup container,
			final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		final View mainView = inflater.inflate(R.layout.push_manage_main_layout, null);
		extractClassLayouts(mainView);
		setClickHandlers(mainView);
		createHeaders();
		setListsInHeader();
		return mainView;
	}
	
	/**
	 * Set the click handlers on the items that need them
	 * @param mainView - view holding the items that need the click handlers
	 */
	private void setClickHandlers(final View mainView) {
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
	}

	/**
	 * Extract the class layouts 
	 * @param mainView - view holding the class layouts
	 */
	private void extractClassLayouts(final View mainView) {
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
		hiddenField = (TextView)mainView.findViewById(R.id.hidden_field);
		context = this.getActivity();
		res = context.getResources();	
	}

	/**
	 * Set the master text switch active
	 */
	private void setMasterTextSwitchActive(){
		isMasterTextEnabled = true;
		toggleSwitch(enableText, isMasterTextEnabled);
	}
	
	/**
	 * Set the master push switch active
	 */
	private void setMasterPushSwitchActive(){
		isMasterPushEnabled = true;
		toggleSwitch(enablePush, isMasterPushEnabled);
	}
	
	/**
	 * Toggle the master push enable switch of and on
	 */
	protected void togglePushSwitch() {
		isMasterPushEnabled = (isMasterPushEnabled) ? false : true;
		toggleSwitch(enablePush, isMasterPushEnabled);
	}
	
	/**
	 * Toggle the master text enable switch of and on
	 */
	protected void toggleTextSwitch() {
		isMasterTextEnabled = (isMasterTextEnabled) ? false : true;
		toggleSwitch(enableText, isMasterTextEnabled);
		
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
			final PushManageCategoryItem item = 
					factory.createItem(categories[i], headers[i], texts[i]);
			if(showCategory(item.getCategory())){
				list.addView((RelativeLayout)item);
				manageHeader.setVisibility(View.VISIBLE);
			}
			categoriesList.add(item);
		}
	}
	
	/**
	 * Create the list for the monitor your spending
	 */
	private void createMonitorList() {
		final PushManageCategoryItem purchase = factory.createItem(
				res.getString(R.string.purchase_amount_category), 
				res.getString(R.string.purchase_amount_header),
				prefs.remindersEnrollResults.tamtDefAmt,
				prefs.remindersEnrollResults.tamtMinAmt);		
		
		final PushManageCategoryItem balance = factory.createItem(
				res.getString(R.string.balance_amount_category), 
				res.getString(R.string.balance_amount_header),
				prefs.remindersEnrollResults.balanceDefAmt,
				prefs.remindersEnrollResults.balanaceMinAmt);
		
		final PushManageCategoryItem creditLine = factory.createItem(
				res.getString(R.string.credit_line_ammount_category), 
				res.getString(R.string.credit_line_ammount_header),
				res.getString(R.string.credit_line_ammount_text),
				prefs.remindersEnrollResults.crltAmtOptions);
		
		if(showCategory(purchase.getCategory())){
			monitorList.addView((RelativeLayout)purchase);
			monitorHeader.setVisibility(View.VISIBLE);
		}
		categoriesList.add(purchase);
		if(showCategory(balance.getCategory())){
			monitorList.addView((RelativeLayout)balance);
			monitorHeader.setVisibility(View.VISIBLE);
		}
		categoriesList.add(balance);
		if(showCategory(creditLine.getCategory())){
			monitorList.addView((RelativeLayout)creditLine);
			monitorHeader.setVisibility(View.VISIBLE);
		}
		categoriesList.add(creditLine);
	}
	
	/**
	 * Create the rewards list
	 */
	private void createRewardsList() {
		final PushManageCategoryItem rewardsReminder = factory.createItem(
				res.getString(R.string.rewards_reminder_category),
				res.getString(R.string.rewards_reminder_header),
				res.getString(R.string.rewards_reminder_text));
		
		final PushManageCategoryItem cashBackBonus = factory.createItem(
				res.getString(R.string.cashback_bonus_category),
				res.getString(R.string.cashback_bonus_header),
				prefs.remindersEnrollResults.mrrwDefAmt,
				prefs.remindersEnrollResults.mrrwMinAmt);
		((PushManageToggleItemEditText)cashBackBonus).hideMinimumAmount();
		if(showCategory(rewardsReminder.getCategory())){
			maximizeList.addView((RelativeLayout)rewardsReminder);
			maximizeHeader.setVisibility(View.VISIBLE);
		}
		categoriesList.add(rewardsReminder);
		if(showCategory(cashBackBonus.getCategory())){
			maximizeList.addView((RelativeLayout)cashBackBonus);
			maximizeHeader.setVisibility(View.VISIBLE);
		}
		categoriesList.add(cashBackBonus);
		
	}
	
	/**
	 * Determine if the category should be shown
	 * @param category - category in question
	 * @return if the category should be shown
	 */
	private boolean showCategory(final String category){
		return prefs.remindersEnrollResults.codesToDisplay.contains(category);
	}

	/**
	 * Create the list headers
	 */
	private void createHeaders() {
		manageHeader.setHeader(res.getString(R.string.manage_your_account_title));
		monitorHeader.setHeader(res.getString(R.string.monitor_your_spending_title));
		maximizeHeader.setHeader(res.getString(R.string.maximize_your_rewards_title));	
	}
	
	/**
	 * Clear all the lists so that they can be repopulated
	 */
	private void clearLists() {
		this.manageList.removeAllViews();
		this.maximizeList.removeAllViews();
		this.monitorList.removeAllViews();
	}

	/**
	 * Show the terms and conditions
	 */
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
	
	/**
	 * Set the preferences
	 * @param detail - preferences to set 
	 */
	public void setPrefs(final PushNotificationPrefsDetail detail){
		this.prefs = detail;
	}
	
	/**
	 * Display the preferences in their lists so that they can be modified
	 */
	public void displayPrefs(){
		if(null == this.prefs){return;}
		factory = new PushManageItemFactory(context, prefs, this);
		clearLists();
		categoriesList.clear();
		phoneNumber.setText(CommonUtils.toPhoneNumber(prefs.remindersEnrollResults.phoneNumber));
		createList(manageList, 
				res.getStringArray(R.array.manage_you_accounts_categories),
				res.getStringArray(R.array.manage_your_accounts_headers),
				res.getStringArray(R.array.manage_your_accounts_text));
		createMonitorList();
		createRewardsList();
		hideSavebar();
		setUpMasterToggles();
	}

	/**
	 * Sets up the master toggle switches
	 */
	private void setUpMasterToggles() {
		for(PreferencesDetail item: prefs.remindersEnrollResults.preferences){
			if(item.accepted.equals(PreferencesDetail.ACCEPTED)){
				if(item.categoryId.equals(PreferencesDetail.PUSH_PARAM)){
					setMasterPushSwitchActive();
				}else{
					setMasterTextSwitchActive();
				}	
			}
		}
	}

	/**
	 * Save the preferences 
	 */
	public void savePreferences(){
		final AsyncCallback<PostPreferencesDetail> callback = 
				GenericAsyncCallback.<PostPreferencesDetail>builder(this.getActivity())
				.showProgressDialog(getResources().getString(R.string.push_progress_get_title), 
									getResources().getString(R.string.push_progress_registration_loading), 
									true)
				.withSuccessListener(new PostPrefsSuccessListener(this))
				.withErrorResponseHandler(new PushPrefsErrorHandler((NavigationRootActivity)this.getActivity()))
				.build();
		
		new PostNotificationPreferences(this.context, callback, getPreferences()).submit();
	}
	
	/**
	 * Get the preferences so that they can be posted to the server
	 * @return - the preferences so that they can be posted to the server
	 */
	private PostPreferencesDetail getPreferences(){
		final TelephonyManager telephonyManager =
				(TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
		final PostPreferencesDetail post = new PostPreferencesDetail();
		final String version = Build.VERSION.RELEASE;
		final String phone = phoneNumber.getText().toString().replaceAll(HYPEN, "");
		
		post.vid = XtifySDK.getXidKey(context);
		post.os = PostPreferencesDetail.DEFAULT_OS;
		
		if(null != version){
			post.osVersion = version;
		} else{
			post.osVersion = PostPreferencesDetail.DEFAULT_VERSION;
		}
		post.deviceID = telephonyManager.getDeviceId();
		post.regStatus = PostPreferencesDetail.ACCEPT;
		post.accntOverrideInd = PostPreferencesDetail.OVERRIDE_YES;
		post.phoneNumber = phone;
		post.carrier = getCarrier();
		post.prefs = getPrefs();
		return post;
	}

	/**
	 * Get the preference items
	 * @return the preference items
	 */
	private List<PostPrefDetail> getPrefs() {
		final List<PostPrefDetail> newPrefs = new ArrayList<PostPrefDetail>();
		final boolean textIsEnabled = (this.isMasterTextEnabled && (null != prefs.remindersEnrollResults.phoneNumber));
		for(PushManageCategoryItem item : categoriesList){
			newPrefs.add(item.getTextPreferencesDetail(textIsEnabled));
			newPrefs.add(item.getPushPreferencesDetail(isMasterPushEnabled));
		}
		return newPrefs;
	}

	/**
	 * Get the carrier so that the server can be updated
	 * @return the carrier so that the server can be updated
	 */
	private String getCarrier() {
		// TODO: Solve this values are below:
		//<option value="message.alltel.com">Alltel</option>
        //<option value="txt.att.net">AT&T</option>
        //<option value="myboostmobile.com">Boost Mobile</option>
        //<option value="messaging.nextel.com">Nextel</option>
        //<option value="messaging.sprintpcs.com">Sprint</option>
        //<option value="tmomail.net">T-Mobile</option>
        //<option value="email.uscc.net">U.S. Cellular</option>
        //<option value="vtext.com">Verizon/Amp'd</option>
        //<option value="vmobl.com">Virgin Mobile</option>
		return null;
	}
	
	/**
	 * Show the save bar
	 */
	public void showSaveBar(){
		saveItem.setVisibility(View.VISIBLE);
		hideSuccessSave();
	}

	/**
	 * Hide the save bar
	 */
	public void hideSavebar() {
		saveItem.setVisibility(View.GONE);
	}

	/**
	 * Show the successful save
	 */
	public void showSuccessSave() {
		hiddenField.setVisibility(View.VISIBLE);
	}
	
	/**
	 * Hide the successful save
	 */
	public void hideSuccessSave() {
		hiddenField.setVisibility(View.GONE);
	}
}
