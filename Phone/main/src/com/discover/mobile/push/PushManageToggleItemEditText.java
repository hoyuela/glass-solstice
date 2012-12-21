package com.discover.mobile.push;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.discover.mobile.R;
import com.discover.mobile.common.push.manage.PreferencesDetail;
import com.discover.mobile.common.push.manage.PushManageCategoryParamDetail;

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
		if(!isPushAlertEnabled()){return null;}
		
		final PreferencesDetail detail = new PreferencesDetail();
		detail.prefTypeCode = this.getCategory();
		detail.accepted = (isMasterPushEnabled) ? PreferencesDetail.ACCEPTED : PreferencesDetail.PENDING; 
		detail.categoryId = PreferencesDetail.PUSH_PARAM;
		final List<PushManageCategoryParamDetail> params = new ArrayList<PushManageCategoryParamDetail>();
		final PushManageCategoryParamDetail param = new PushManageCategoryParamDetail();
		param.code = PushManageCategoryParamDetail.AMOUNT_CODE;
		param.value = getAmount();
		params.add(param);
		detail.params = params;
		return detail;
	}


	@Override
	public PreferencesDetail getTextPreferencesDetail(final boolean isMasterTextEnabled) {
		if(!isTextAlertEnabled()){return null;}
		
		final PreferencesDetail detail = new PreferencesDetail();
		detail.prefTypeCode = this.getCategory();
		detail.accepted = (isMasterTextEnabled) ? PreferencesDetail.ACCEPTED : PreferencesDetail.PENDING; 
		detail.categoryId = PreferencesDetail.TEXT_PARAM;
		final List<PushManageCategoryParamDetail> params = new ArrayList<PushManageCategoryParamDetail>();
		final PushManageCategoryParamDetail param = new PushManageCategoryParamDetail();
		param.code = PushManageCategoryParamDetail.AMOUNT_CODE;
		param.value = getAmount();
		params.add(param);
		detail.params = params;
		return detail;
	}
}