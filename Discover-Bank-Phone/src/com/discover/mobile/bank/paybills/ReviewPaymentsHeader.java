package com.discover.mobile.bank.paybills;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.ToggleButton;

import com.discover.mobile.bank.R;

public class ReviewPaymentsHeader extends RelativeLayout{

	private final ToggleButton scheduled;

	private final ToggleButton completed;

	private final ToggleButton canceled;

	public ReviewPaymentsHeader(final Context context, final AttributeSet attrs) {
		super(context, attrs);

		final View view = LayoutInflater.from(context).inflate(R.layout.review_payments_header, null);
		view.findViewById(R.id.table_titles);
		scheduled = (ToggleButton) view.findViewById(R.id.toggle_left);
		completed = (ToggleButton) view.findViewById(R.id.toggle_middle);
		canceled  = (ToggleButton) view.findViewById(R.id.toggle_right);

		scheduled.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(final View v) {
				toggleButton(0);

			}

		});

		completed.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(final View v) {
				toggleButton(1);

			}

		});

		canceled.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(final View v) {
				toggleButton(2);

			}

		});

		addView(view);
	}


	/**
	 * Toggle the buttons look and feel
	 * @param checked - toggle button that is checked
	 * @param notChecke - toggle button that is not checked
	 * @param isPosted - boolean to set is posted equal to
	 */
	public void toggleButton(final int checked){
		switch(checked){
		case 0: 
			scheduled.setTextColor(getResources().getColor(R.color.white));
			completed.setTextColor(getResources().getColor(R.color.body_copy));
			canceled.setTextColor(getResources().getColor(R.color.body_copy));
			scheduled.setBackgroundDrawable(getResources().getDrawable(R.drawable.toggle_left_on));
			completed.setBackgroundDrawable(getResources().getDrawable(R.drawable.toggle_mid_off));
			canceled.setBackgroundDrawable(getResources().getDrawable(R.drawable.toggle_right_off));
			break;
		case 1: 
			scheduled.setTextColor(getResources().getColor(R.color.body_copy));
			completed.setTextColor(getResources().getColor(R.color.white));
			canceled.setTextColor(getResources().getColor(R.color.body_copy));
			scheduled.setBackgroundDrawable(getResources().getDrawable(R.drawable.toggle_left_off));
			completed.setBackgroundDrawable(getResources().getDrawable(R.drawable.toggle_mid_on));
			canceled.setBackgroundDrawable(getResources().getDrawable(R.drawable.toggle_right_off));
			break;
		case 2: 
			scheduled.setTextColor(getResources().getColor(R.color.body_copy));
			completed.setTextColor(getResources().getColor(R.color.body_copy));
			canceled.setTextColor(getResources().getColor(R.color.white));
			scheduled.setBackgroundDrawable(getResources().getDrawable(R.drawable.toggle_left_off));
			completed.setBackgroundDrawable(getResources().getDrawable(R.drawable.toggle_mid_off));
			canceled.setBackgroundDrawable(getResources().getDrawable(R.drawable.toggle_right_on));
			break;
		}
	}

}
