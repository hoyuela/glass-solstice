package com.discover.mobile.push;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.discover.mobile.R;
import com.discover.mobile.common.push.manage.PreferencesDetail;
import com.discover.mobile.common.push.manage.PushManageCategoryParamDetail;

public class PushManageToogleItemSpinner extends BasePushManageToggleItem {

	private String category;

	private TextView titleView;
	
	private Spinner amountSpinner;
	
	private Context context;

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
	
	public void setSpinnerDropdown(final List<String> values){
		final ArrayAdapter<String> adapter = new ArrayAdapter<String>(context, 
																	  R.layout.push_simple_spinner_view,
																	  values);
		adapter.setDropDownViewResource(R.layout.push_simple_spinner_view);
		amountSpinner.setAdapter(adapter);
	}
	

	public void setHeader(final String header){
		getHeaderView().setText(header);
	}
	
	public void setText(final String text){
		if(text.isEmpty()){
			titleView.setVisibility(View.GONE);
		}
		titleView.setText(text);
	}

	public String getCategory() {
		return category;
	}

	public void setCategory(final String category) {
		this.category = category;
	}
	
	private String getAmount(){
		final String amount = amountSpinner.getSelectedItem().toString();
		if(amount.contains("$")){
			return amount.substring(1, amount.length());
		}else{
			return amount;
		}
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
		if(!isPushAlertEnabled()){return null;}
		
		final PreferencesDetail detail = new PreferencesDetail();
		detail.prefTypeCode = this.getCategory();
		detail.accepted = (isMasterTextEnabled) ? PreferencesDetail.ACCEPTED : PreferencesDetail.PENDING; 
		detail.categoryId = PreferencesDetail.PUSH_PARAM;
		final List<PushManageCategoryParamDetail> params = new ArrayList<PushManageCategoryParamDetail>();
		final PushManageCategoryParamDetail param = new PushManageCategoryParamDetail();
		param.code = PushManageCategoryParamDetail.AMOUNT_CODE;
		param.value = getAmount();
		params.add(param);
		detail.params = params;
		return detail;
	}
}

