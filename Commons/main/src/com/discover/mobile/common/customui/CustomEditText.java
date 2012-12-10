package com.discover.mobile.common.customui;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.discover.mobile.common.R;

public class CustomEditText extends RelativeLayout{
	
private EditText field;

	public CustomEditText(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.setBackgroundDrawable(getResources().getDrawable(R.drawable.plain_edit_text));
		final LinearLayout mainView = (LinearLayout) LayoutInflater.from(context)
                .inflate(R.layout.custom_edit_text, null);
		
		field = (EditText)mainView.findViewById(R.id.editText1);
		final ImageView icon = (ImageView)mainView.findViewById(R.id.imageView1);
		
		mainView.removeAllViews();
		field.setTextColor(getResources().getColor(R.color.title));
		
		addView(field);
		addView(icon);
	}
	
	public void setText(String text) {
		field.setText(text);
	}
	
	public String getText() {
		return field.getText().toString();
	}
	
	public EditText getField() {
		return field;
	}

	

	
}
