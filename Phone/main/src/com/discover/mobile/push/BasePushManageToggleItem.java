package com.discover.mobile.push;

import android.content.Context;
import android.content.res.Resources;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.discover.mobile.R;


public abstract class BasePushManageToggleItem extends RelativeLayout implements PushManageCategoryItem{

	private TextView headerView;
	
	private ImageView textAlert;

	private ImageView pushAlert;
	
	private boolean isTextChecked = false;
	
	private boolean isPushChecked = false;

	private final Resources res;
	
	public BasePushManageToggleItem(final Context context, final AttributeSet attrs) {
		super(context, attrs);
		
		res = context.getResources();
	}
	
	protected View.OnClickListener getToggleListener(){
		return new OnClickListener(){

			@Override
			public void onClick(final View v) {
				final ImageView toggleImage = (ImageView) v;
				if(toggleImage.getId() == textAlert.getId()){
					toggleTextBox();
				} else{
					togglePushBox();
				}
			}
			
		};
	}
	
	protected void toggleTextBox(){
		toggleBox(textAlert, isTextChecked);
		isTextChecked = (isTextChecked) ? false : true;
	}
	
	protected void togglePushBox(){
		toggleBox(pushAlert, isPushChecked);
		isPushChecked = (isPushChecked) ? false : true;
	}
	
	protected void toggleBox(final ImageView toggleImage, final boolean isChecked){
		if(isChecked){
			toggleImage.setBackgroundDrawable(res.getDrawable(R.drawable.gray_gradient_square));
			toggleImage.setImageDrawable(res.getDrawable(R.drawable.transparent_square));
		} else{
			toggleImage.setBackgroundDrawable(res.getDrawable(R.drawable.black_gradient_square));
			toggleImage.setImageDrawable(res.getDrawable(R.drawable.white_check_mark));
		}
	}
	
	public boolean isTextAlertEnabled(){
		return isTextChecked;
	}
	
	public boolean isPushAlertEnabled(){
		return isPushChecked;
	}
	
	public void setTextAlertBox(final boolean isEnabled){
		toggleBox(textAlert, isEnabled);
		isTextChecked = isEnabled;
	}
	
	public void setPushAlertBox(final boolean isEnabled){
		toggleBox(pushAlert, isEnabled);
		isTextChecked = isEnabled;
	}	
	
	protected ImageView getTextToggleView(){
		return textAlert;
	}

	protected ImageView getPushToggleView(){
		return pushAlert;
	}
	
	protected void setTextToggleView(final ImageView textToggle){
		this.textAlert = textToggle;
	}
	
	protected void setPushToggleView(final ImageView pushToggle){
		this.pushAlert = pushToggle;
	}
	
	protected Resources getRes(){
		return res;
	}

	public TextView getHeaderView() {
		return headerView;
	}

	public void setHeaderView(TextView headerView) {
		this.headerView = headerView;
	}
}
