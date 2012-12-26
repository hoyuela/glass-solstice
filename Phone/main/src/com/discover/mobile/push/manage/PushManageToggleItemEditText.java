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
import com.discover.mobile.common.push.manage.PostPrefParam;
import com.discover.mobile.common.push.manage.PostPreferencesDetail;
import com.discover.mobile.common.push.manage.PreferencesDetail;

public class PushManageToggleItemEditText extends BasePushManageToggleItem {

	private String category;
	
	private EditText amountBox;
	
	private TextView minAmount;

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
	

	public void setHeader(final String header){
		getHeaderView().setText(header);
	}

	public String getCategory() {
		return category;
	}

	public void setCategory(final String category) {
		this.category = category;
	}
	
	public void setAmount(final String amount){
		this.amountBox.setText("$"+amount);
	}
	
	private String getAmount(){
		final String amount = amountBox.getText().toString();
		if(amount.contains("$")){
			return amount.substring(1, amount.length());
		}else{
			return amount;
		}
	}
	
	public void setMinimumAmountText(final String amount){
		final String currentText = minAmount.getText().toString();
		minAmount.setText(currentText + "$" + amount + ".00");
	}
	
	public void hideMinimumAmount(){
		minAmount.setVisibility(View.GONE);
	}

	@Override
	public void setPushChecked(final boolean isChecked) {
		super.setPushAlertBox(isChecked);
	}

	@Override
	public void setTextChecked(final boolean isChecked) {
		super.setTextAlertBox(isChecked);
	}


	@Override
	public PreferencesDetail getPushPreferencesDetail(final boolean isMasterPushEnabled) {
		final PreferencesDetail detail = new PreferencesDetail();
		detail.prefTypeCode = this.getCategory();
		if(isMasterPushEnabled && isPushAlertEnabled()){
			detail.accepted = PostPreferencesDetail.ACCEPT;
		} else if(!isMasterPushEnabled && isPushAlertEnabled()){
			detail.accepted = PostPreferencesDetail.PENDING;
		} else{
			detail.accepted = PostPreferencesDetail.DECLINE;
		}
		detail.categoryId = PreferencesDetail.PUSH_PARAM;
		final List<PostPrefParam> params = new ArrayList<PostPrefParam>();
		final PostPrefParam param = new PostPrefParam();
		param.code = PostPrefParam.AMOUNT_CODE;
		param.value = getAmount();
		params.add(param);
		detail.params = params;
		return detail;
	}


	@Override
	public PreferencesDetail getTextPreferencesDetail(final boolean isMasterTextEnabled) {
		final PreferencesDetail detail = new PreferencesDetail();
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