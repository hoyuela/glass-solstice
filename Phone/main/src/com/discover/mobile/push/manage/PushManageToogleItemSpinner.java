package com.discover.mobile.push.manage;

import java.text.NumberFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.discover.mobile.R;
import com.discover.mobile.common.push.manage.PostPrefDetail;
import com.discover.mobile.common.push.manage.PostPrefParam;
import com.discover.mobile.common.push.manage.PostPreferencesDetail;
import com.discover.mobile.common.push.manage.PushManageCategoryParamDetail;

/**
 * View created to be used in the push notification manage layout.
 * This is the toggle item along with the information associated with it.
 * 
 * @author jthornton
 *
 */
public class PushManageToogleItemSpinner extends BasePushManageToggleItem {
	
	/**Tag for error logging*/
	private static final String TAG = PushManageToogleItemSpinner.class.getSimpleName();

	/**Category of the item*/
	private String category;

	/**TitleView of the item*/
	private TextView titleView;
	
	/**Spinner holding the viable amounts*/
	private Spinner amountSpinner;
	
	/**Context of the activity*/
	private Context context;
	
	/**
	 * Constructor of the class
	 * @param context - activity context
	 * @param attrs - layout attributes
	 */
	public PushManageToogleItemSpinner(final Context context, final AttributeSet attrs) {
		super(context, attrs);
		this.context = context;
		final RelativeLayout mainView = 
				(RelativeLayout)LayoutInflater.from(context).inflate(R.layout.push_manage_toggle_item_spinner, null);
		setHeaderView((TextView)mainView.findViewById(R.id.header));
		titleView = (TextView) mainView.findViewById(R.id.sub_header);
		super.setPushToggleView((ImageView) mainView.findViewById(R.id.push_toggle_view));
		super.setTextToggleView((ImageView) mainView.findViewById(R.id.text_toggle_view));
		amountSpinner = (Spinner) mainView.findViewById(R.id.amount_spinner);
		final TextView amountText = (TextView) mainView.findViewById(R.id.amount_spinner_text);
		
		
		super.getTextToggleView().setOnClickListener(getToggleListener());
		super.getPushToggleView().setOnClickListener(getToggleListener());
		
		
		final TextView textAlertText = (TextView)mainView.findViewById(R.id.text_enable_text);
		final TextView pushAlertText = (TextView)mainView.findViewById(R.id.push_enable_text);
		
		mainView.removeAllViews();
		addView(getTextToggleView());
		addView(getPushToggleView());
		addView(textAlertText);
		addView(pushAlertText);
        addView(getHeaderView());
        addView(titleView);
        addView(amountSpinner);
        addView(amountText);
	}
	
	/**
	 * Set the spinner drop down values
	 * @param values - the values to be displayed in the spinner
	 */
	public void setSpinnerDropdown(final List<String> values){
		final ArrayAdapter<String> adapter = new ArrayAdapter<String>(context, 
																	  R.layout.push_simple_spinner_view,
																	  values);
		adapter.setDropDownViewResource(R.layout.push_simple_spinner_view);
		amountSpinner.setAdapter(adapter);
	}
	
	/**
	 * Set the header text
	 * @param header - string to be shown in the header
	 */
	public void setHeader(final String header){
		getHeaderView().setText(header);
	}
	
	/**
	 * Set the text of the item
	 * @param text - the text of the item
	 */
	public void setText(final String text){
		if(text.isEmpty()){
			titleView.setVisibility(View.GONE);
		}
		titleView.setText(text);
	}

	/**
	 * Get the category of the item
	 * @return - the category of the item
	 */
	public String getCategory() {
		return category;
	}

	/**
	 * Set if the push item is supposed to be checked
	 * @param isChecked - true if the push item is supposed to be checked
	 */
	public void setCategory(final String category) {
		this.category = category;
	}
	
	/**
	 * Get the amount in the spinner
	 * @return the amount in the spinner
	 */
	private String getAmount(){
		final String amount = amountSpinner.getSelectedItem().toString();
		String number = Integer.toString(0);
		if(null == amount){return number;}
		try {
			number =  NumberFormat.getCurrencyInstance().parse(amount).toString();
		} catch (ParseException e) {
			Log.e(TAG, "Error parsing string "+ amount + " , reason: " + e.getMessage());
		}	
		return number;
	}
	
	/**
	 * Set if the push item is supposed to be checked
	 * @param isChecked - true if the push item is supposed to be checked
	 */
	@Override
	public void setPushChecked(final boolean isChecked) {
		super.setPushAlertBox(isChecked);
	}

	/**
	 * Set if the text item is supposed to be checked
	 * @param isChecked - true if the text item is supposed to be checked
	 */
	@Override
	public void setTextChecked(final boolean isChecked) {
		super.setTextAlertBox(isChecked);
	}

	/**
	 * Get the push preference detail
	 * @param isMasterPushEnabled - if the master push switch is on
	 * @return the push preference detail
	 */
	@Override
	public PostPrefDetail getPushPreferencesDetail(final boolean isMasterPushEnabled) {		
		final PostPrefDetail detail = new PostPrefDetail();
		detail.prefTypeCode = this.getCategory();
		if(isMasterPushEnabled && isPushAlertEnabled()){
			detail.accepted = PostPreferencesDetail.ACCEPT;
		} else if(!isMasterPushEnabled && isPushAlertEnabled()){
			detail.accepted = PostPreferencesDetail.PENDING;
		} else{
			detail.accepted = PostPreferencesDetail.DECLINE;
		}
		detail.categoryId = PostPrefDetail.PUSH_PARAM;
		final List<PostPrefParam> params = new ArrayList<PostPrefParam>();
		final PostPrefParam param = new PostPrefParam();
		param.code = PushManageCategoryParamDetail.AMOUNT_CODE;
		param.value = getAmount();
		params.add(param);
		detail.params = params;
		return detail;
	}

	/**
	 * Get the text preference detail
	 * @param isMasterTextEnabled - if the master text switch is on
	 * @return the text preference detail
	 */
	@Override
	public PostPrefDetail getTextPreferencesDetail(final boolean isMasterTextEnabled) {
		final PostPrefDetail detail = new PostPrefDetail();
		detail.prefTypeCode = this.getCategory();
		if(isMasterTextEnabled && isTextAlertEnabled() && isWasTextAlreadySet()){
			detail.accepted = PostPreferencesDetail.ACCEPT;
		} else if(isMasterTextEnabled && isTextAlertEnabled() && !isWasTextAlreadySet()){
			detail.accepted = PostPreferencesDetail.PENDING;
		} else if(!isMasterTextEnabled && isTextAlertEnabled()){
			detail.accepted = PostPreferencesDetail.PENDING;
		} else{
			detail.accepted = PostPreferencesDetail.DECLINE;
		}
		detail.categoryId = PostPrefDetail.TEXT_PARAM;
		final List<PostPrefParam> params = new ArrayList<PostPrefParam>();
		final PostPrefParam param = new PostPrefParam();
		param.code = PostPrefParam.AMOUNT_CODE;
		param.value = getAmount();
		params.add(param);
		detail.params = params;
		return detail;
	}
}

