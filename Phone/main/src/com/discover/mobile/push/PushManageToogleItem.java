package com.discover.mobile.push;

import android.content.Context;
import android.content.res.Resources;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.discover.mobile.R;

/**
 * View created to be used in the push notification manage layout.
 * This is the toggle item along with the information associated with it.
 * 
 * @author jthornton
 *
 */
public class PushManageToogleItem extends RelativeLayout{

	private ImageView textAlert;

	private ImageView pushAlert;

	private TextView headerView;

	private TextView titleView;
	
	private final Resources res;
	
	private boolean isTextChecked = false;
	
	private boolean isPushChecked = false;

	public PushManageToogleItem(final Context context, final AttributeSet attrs) {
		super(context, attrs);
		
		res = context.getResources();
		final RelativeLayout mainView = 
				(RelativeLayout)LayoutInflater.from(context).inflate(R.layout.push_manage_toggle_item, null);
		headerView = (TextView)mainView.findViewById(R.id.header);
		titleView = (TextView) mainView.findViewById(R.id.sub_header);
		pushAlert = (ImageView) mainView.findViewById(R.id.push_toggle_view);
		textAlert = (ImageView) mainView.findViewById(R.id.text_toggle_view);
		
		pushAlert.setOnClickListener(getToggleListener());
		textAlert.setOnClickListener(getToggleListener());
		
		
		final TextView textAlertText = (TextView)mainView.findViewById(R.id.text_enable_text);
		final TextView pushAlertText = (TextView)mainView.findViewById(R.id.push_enable_text);
		
		mainView.removeAllViews();
		addView(textAlert);
		addView(pushAlert);
		addView(textAlertText);
		addView(pushAlertText);
        addView(headerView);
        addView(titleView);
	}
	
	private View.OnClickListener getToggleListener(){
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

	public void setHeader(final String header){
		headerView.setText(header);
	}
	
	public void setText(final String text){
		if(text.isEmpty()){
			titleView.setVisibility(View.GONE);
		}
		titleView.setText(text);
	}
	
	public boolean isTextAlertEnabled(){
		return isTextChecked;
	}
	
	public boolean isPushAlertEnabled(){
		return isPushChecked;
	}
	
	public void toggleTextBox(){
		toggleBox(textAlert, isTextChecked);
		isTextChecked = (isTextChecked) ? false : true;
	}
	
	public void togglePushBox(){
		toggleBox(pushAlert, isPushChecked);
		isPushChecked = (isPushChecked) ? false : true;
	}
	
	public void toggleBox(final ImageView toggleImage, final boolean isChecked){
		if(isChecked){
			toggleImage.setBackgroundDrawable(res.getDrawable(R.drawable.gray_gradient_square));
			toggleImage.setImageDrawable(res.getDrawable(R.drawable.transparent_square));
		} else{
			toggleImage.setBackgroundDrawable(res.getDrawable(R.drawable.black_gradient_square));
			toggleImage.setImageDrawable(res.getDrawable(R.drawable.white_check_mark));
		}
	}

}
