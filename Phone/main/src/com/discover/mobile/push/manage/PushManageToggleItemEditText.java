package com.discover.mobile.push.manage;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.discover.mobile.R;
import com.discover.mobile.common.push.manage.PostPrefDetail;
import com.discover.mobile.common.push.manage.PostPrefParam;
import com.discover.mobile.common.push.manage.PostPreferencesDetail;
import com.discover.mobile.common.push.manage.PreferencesDetail;

/**
 * An item that can be toggled and that has an edit text in the layout
 * @author jthornton
 *
 */
public class PushManageToggleItemEditText extends BasePushManageToggleItem {

	/**Category of the item*/
	private String category;
	
	/**Box holding the amount the user specified*/
	private EditText amountBox;
	
	/**TextView holding the minimum amount text*/
	private TextView minAmount;
	
	/**String representing a $*/
	private final static String DOLLAR = "$";
	
	/**String representing the end of a dollar string*/
	private final static String DOLLAR_ENDING = ".00";

	/**
	 * Constructor of the class
	 * @param context - activity context
	 * @param attrs - layout attributes
	 */
	public PushManageToggleItemEditText(final Context context, final AttributeSet attrs) {
		super(context, attrs);
		
		final RelativeLayout mainView = 
				(RelativeLayout)LayoutInflater.from(context).inflate(R.layout.push_manage_toggle_item_edit_text, null);
		setHeaderView((TextView)mainView.findViewById(R.id.header));
		super.setPushToggleView((ImageView) mainView.findViewById(R.id.push_toggle_view));
		super.setTextToggleView((ImageView) mainView.findViewById(R.id.text_toggle_view));
		amountBox = (EditText) mainView.findViewById(R.id.amount_box);
		minAmount = (TextView) mainView.findViewById(R.id.minimum_amount);
		final TextView amountText = (TextView) mainView.findViewById(R.id.amount_text_box_view);
		
		
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
        addView(amountBox);
        addView(amountText);
        addView(minAmount);
	}
	
	/**
	 * Set the header text
	 * @param header - string to be shown in the header
	 */
	public void setHeader(final String header){
		getHeaderView().setText(header);
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
	 * Set the current amount of the users setting
	 * @param amount - the current amount of the users setting
	 */
	public void setAmount(final String amount){
		this.amountBox.setText(DOLLAR+amount);
	}
	
	/**
	 * Get the amount the user put in the box
	 * @return - the amount put in the box
	 */
	private String getAmount(){
		final String amount = amountBox.getText().toString();
		if(amount.contains(DOLLAR)){
			return amount.substring(1, amount.length());
		}else{
			return amount;
		}
	}
	
	/**
	 * Set the minimum amount text
	 * @param amount - the amount to put in the current string
	 */
	public void setMinimumAmountText(final String amount){
		final String currentText = minAmount.getText().toString();
		minAmount.setText(currentText + DOLLAR + amount + DOLLAR_ENDING);
	}
	
	/**
	 * Hide the minimum amount text view
	 */
	public void hideMinimumAmount(){
		minAmount.setVisibility(View.GONE);
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
		param.code = PostPrefParam.AMOUNT_CODE;
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
		detail.categoryId = PreferencesDetail.TEXT_PARAM;
		final List<PostPrefParam> params = new ArrayList<PostPrefParam>();
		final PostPrefParam param = new PostPrefParam();
		param.code = PostPrefParam.AMOUNT_CODE;
		param.value = getAmount();
		params.add(param);
		detail.params = params;
		return detail;
	}
}