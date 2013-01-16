package com.discover.mobile.section.account;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.discover.mobile.R;

public class SimpleListItem extends RelativeLayout{
	
	private final Context context;
	
	private final TextView label;
	
	private final TextView value;
	
	private final TextView action;
	

	public SimpleListItem(final Context context, final AttributeSet attrs) {
		super(context, attrs);
		
		final RelativeLayout layout = 
				(RelativeLayout)LayoutInflater.from(context).inflate(R.layout.simple_list_item, null);
		
		label = (TextView)layout.findViewById(R.id.balance_label);
		value = (TextView)layout.findViewById(R.id.balance_value);
		action = (TextView)layout.findViewById(R.id.action_text);

		this.context = context;
		
		addView(layout);
	}
	
	public void hideAction(){
		action.setVisibility(View.INVISIBLE);
	}
	
	public void setLabel(final String label){
		this.label.setText(label);
	}
	
	public void setAction(final String action){
		this.action.setText(action);
	}
	
	public void setValue(final String value){
		this.value.setText(value);
	}
	
	public void setActionHandler(final OnClickListener listener){
		action.setOnClickListener(listener); 
	}
}
