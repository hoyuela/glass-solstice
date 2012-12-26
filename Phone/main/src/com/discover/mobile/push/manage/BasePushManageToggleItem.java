package com.discover.mobile.push.manage;

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
	
	private boolean wasTextAlreadySet = false;

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
					toggleTextBox(!isTextChecked);
				} else{
					togglePushBox(!isPushChecked);
				}
			}
			
		};
	}
	
	protected void toggleTextBox(final boolean isChecked){
		toggleBox(textAlert, isChecked);
		isTextChecked = isChecked;
	}
	
	protected void togglePushBox(final boolean isChecked){
		toggleBox(pushAlert, isChecked);
		isPushChecked = isChecked;
	}
	
	protected void toggleBox(final ImageView toggleImage, final boolean isChecked){
		if(!isChecked){
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
		isTextChecked = isEnabled;
		toggleBox(textAlert, isEnabled);
	}
	
	public void setPushAlertBox(final boolean isEnabled){
		isTextChecked = isEnabled;
		toggleBox(pushAlert, isEnabled);
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

	public void setHeaderView(final TextView headerView) {
		this.headerView = headerView;
	}

	public boolean isWasTextAlreadySet() {
		return wasTextAlreadySet;
	}

	public void setWasTextAlreadySet(final boolean wasTextAlreadySet) {
		this.wasTextAlreadySet = wasTextAlreadySet;
	}
}
