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

public class PushManageToogleItem extends RelativeLayout{

	private ImageView textAlert;

	private ImageView pushAlert;

	private TextView headerView;

	private TextView titleView;
	
	private final Resources res;

	public PushManageToogleItem(Context context, AttributeSet attrs) {
		super(context, attrs);
		
		res = context.getResources();
		final RelativeLayout mainView = (RelativeLayout)LayoutInflater.from(context).inflate(R.layout.push_manage_toggle_item, null);
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
			public void onClick(View v) {
				final ImageView toggleImage = (ImageView) v;
				//FIXME: Item disappears
				if(toggleImage.getBackground().equals(res.getDrawable(R.drawable.black_gradient_square))){
					toggleImage.setBackgroundDrawable(res.getDrawable(R.drawable.gray_gradient_square));
					toggleImage.setBackgroundResource(-1);
				}
				else{
					toggleImage.setBackgroundDrawable(res.getDrawable(R.drawable.black_gradient_square));
					toggleImage.setBackgroundResource(R.drawable.white_check_mark);
				}
			}
			
		};
	}

	
	public void setHeader(String header){
		headerView.setText(header);
	}
	
	public void setText(String text){
		if(text.isEmpty()){
			titleView.setVisibility(View.GONE);
		}
		titleView.setText(text);
	}
	
	public boolean isTextAlertEnabled(){
		return isItemChecked(textAlert);
	}
	
	public boolean isPushAlertEnabled(){
		return isItemChecked(pushAlert);
	}
	
	private boolean isItemChecked(ImageView view){
		return (view.getDrawable().equals(getResources().getDrawable(R.drawable.white_check_mark)));
	}

}
