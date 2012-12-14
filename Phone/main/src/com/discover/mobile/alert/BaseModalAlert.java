package com.discover.mobile.alert;

import android.app.AlertDialog;
import android.content.Context;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.discover.mobile.R;

public abstract class BaseModalAlert extends AlertDialog{

	protected TextView title;
	
	protected TextView content;
		
	protected ImageView divider;
	
	protected Button ok; 
	
	protected TextView cancel;
	
	protected final Resources res;
	
	protected ImageView checkbox;
	
	protected boolean isTextChecked = false;
	
	
	protected BaseModalAlert(final Context context) {
		super(context);
		res = context.getResources();
	}

	@Override
	public void onCreate(final Bundle savedInstanceState){
		
	}

	public void toggleCheckBox(){
		toggleBox(isTextChecked);
		isTextChecked = (isTextChecked) ? false : true;
	}
	
	public void toggleBox(final boolean isChecked){
		if(isChecked){
			checkbox.setBackgroundDrawable(res.getDrawable(R.drawable.gray_gradient_square));
			checkbox.setImageDrawable(res.getDrawable(R.drawable.transparent_square));
		} else{
			checkbox.setBackgroundDrawable(res.getDrawable(R.drawable.black_gradient_square));
			checkbox.setImageDrawable(res.getDrawable(R.drawable.white_check_mark));
		}
	}
	
	public void showDefaultViews(){
		title.setVisibility(View.VISIBLE);
		content.setVisibility(View.VISIBLE);
		divider.setVisibility(View.VISIBLE);
		ok.setVisibility(View.VISIBLE);
		cancel.setVisibility(View.VISIBLE);
	}
	
	public void hideAll(){
		title.setVisibility(View.GONE);
		content.setVisibility(View.GONE);
		divider.setVisibility(View.GONE);
		ok.setVisibility(View.GONE);
		cancel.setVisibility(View.GONE);
	}
	
	public void setTitle(final String titleString){
		title.setText(titleString);
	}
	
	public void setContent(final String contentString){
		content.setText(contentString);
	}
	
	public void setOkButtonText(final String okString){
		ok.setText(okString);
	}
	
	public void setCancelButtonText(final String cancelString){
		cancel.setText(cancelString);
	}
	
	public void setOkClickListener(final View.OnClickListener listener){
		ok.setOnClickListener(listener);
	}
	
	public void setCancelClickListener(final View.OnClickListener listener){
		cancel.setOnClickListener(listener);
	}
	
	public boolean getShowAgain(){
		return isTextChecked;
	}
}	
