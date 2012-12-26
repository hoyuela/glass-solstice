package com.discover.mobile.push.manage;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.discover.mobile.R;
import com.discover.mobile.common.push.manage.PostPreferencesDetail;
import com.discover.mobile.common.push.manage.PreferencesDetail;

/**
 * View created to be used in the push notification manage layout.
 * This is the toggle item along with the information associated with it.
 * 
 * @author jthornton
 *
 */
public class PushManageToogleItemSimple extends BasePushManageToggleItem {

	private String category;

	private TextView titleView;

	public PushManageToogleItemSimple(final Context context, final AttributeSet attrs) {
		super(context, attrs);
		
		final RelativeLayout mainView = 
				(RelativeLayout)LayoutInflater.from(context).inflate(R.layout.push_manage_toggle_item_simple, null);
		setHeaderView((TextView)mainView.findViewById(R.id.header));
		titleView = (TextView) mainView.findViewById(R.id.sub_header);
		super.setPushToggleView((ImageView) mainView.findViewById(R.id.push_toggle_view));
		super.setTextToggleView((ImageView) mainView.findViewById(R.id.text_toggle_view));
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
		return detail;
	}
}
