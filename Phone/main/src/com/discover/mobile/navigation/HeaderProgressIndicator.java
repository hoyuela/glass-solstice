package com.discover.mobile.navigation;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.discover.mobile.R;


/**
 * Custom view for displaying the header navigation. This is used to display to the user what
 * position they are at in the process.
 * 
 * @author ajleeds
 *
 */
public class HeaderProgressIndicator extends RelativeLayout {

	private TextView step1;
	private TextView step2;
	private TextView step3;
	private ImageView step1Confirm;
	private ImageView step2Confirm;
	private TextView indicator1;
	private TextView indicator2;
	private TextView indicator3;
	

	public HeaderProgressIndicator(Context context) {
		super(context);
	}

	public HeaderProgressIndicator(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public HeaderProgressIndicator(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	/**
	 * Initiates the header and sets the current position for Change Password
	 * @param position - Current position between 0-2
	 */
	public void initChangePasswordHeader(int position) {
		inflateHeader();
		setTitle("Enter Info", "Change Password", "Confirmation");
		setPosition(position);
	}

	private void inflateHeader() {
		LayoutInflater inflater = (LayoutInflater) getContext()
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

		inflater.inflate(R.layout.header_progress_indication, this);
		step1 = (TextView) findViewById(R.id.step1_title);
		step1Confirm = (ImageView) findViewById(R.id.step1_confirm);
		indicator1 = (TextView) findViewById(R.id.first_indicator);
		
		step2 = (TextView) findViewById(R.id.step2_title);
		step2Confirm = (ImageView) findViewById(R.id.step2_confirm);
		indicator2 = (TextView) findViewById(R.id.middle_indicator);
		
		step3 = (TextView) findViewById(R.id.step3_title);
		indicator3 = (TextView) findViewById(R.id.last_indicator);

	}

	/**
	 * Sets the titles for the header
	 */
	private void setTitle(String title1, String title2, String title3) {
		step1.setText(title1);
		step2.setText(title2);
		step3.setText(title3);
	}
	
	/**
	 * Sets up the current position of the header
	 * @param position - number between 0-2
	 */
	private void setPosition(int position){
		if (position == 1){
			step1Confirm.setVisibility(View.VISIBLE);
		}
		if (position == 2){
			step1Confirm.setVisibility(View.VISIBLE);
			step2Confirm.setVisibility(View.VISIBLE);
		}
		setIndicatorVisibility(position);
	}
	
	private void setIndicatorVisibility(int position){
		if (position == 0){
			indicator1.setVisibility(View.VISIBLE);
			indicator2.setVisibility(View.INVISIBLE);
			indicator3.setVisibility(View.INVISIBLE);
		}else if (position == 1){
			indicator1.setVisibility(View.INVISIBLE);
			indicator2.setVisibility(View.VISIBLE);
			indicator3.setVisibility(View.INVISIBLE);
		}else {
			indicator1.setVisibility(View.INVISIBLE);
			indicator2.setVisibility(View.INVISIBLE);
			indicator3.setVisibility(View.VISIBLE);
		}
	}
}
