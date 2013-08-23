package com.discover.mobile.card.common.uiwidget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.discover.mobile.card.R;


/**
 * Custom view for displaying the header navigation. This is used to display to the user what
 * position they are at in the process.
 * 
 * @author ajleeds
 *
 */
public class HeaderThreeStepProgressIndicator extends HeaderProgressIndicator {


	public HeaderThreeStepProgressIndicator(final Context context) {
		super(context);
	}

	public HeaderThreeStepProgressIndicator(final Context context, final AttributeSet attrs) {
		super(context, attrs);
	}

	public HeaderThreeStepProgressIndicator(final Context context, final AttributeSet attrs, final int defStyle) {
		super(context, attrs, defStyle);
	}


	protected void inflateHeader() {
		final LayoutInflater inflater = (LayoutInflater) getContext()
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

		inflater.inflate(R.layout.header_progress_indication, this);
		step1 = (TextView) findViewById(R.id.step1_title);
		step1Confirm = (ImageView) findViewById(R.id.step1_confirm);
		indicator1 = (ImageView) findViewById(R.id.first_indicator);
		
		step2 = (TextView) findViewById(R.id.step2_title);
		step2Confirm = (ImageView) findViewById(R.id.step2_confirm);
		indicator2 = (ImageView) findViewById(R.id.middle_indicator);
		
		step3 = (TextView) findViewById(R.id.step3_title);
		indicator3 = (ImageView) findViewById(R.id.last_indicator);

	}

	/**
	 * Sets the titles for the header
	 */
	public void setTitle(final int title1, final int title2, final int title3) {
		step1.setText(getResources().getString(title1));
		step2.setText(getResources().getString(title2));
		step3.setText(getResources().getString(title3));
	}
	
	/**
	 * Sets up the current position of the header
	 * @param position - number between 0-2
	 */
	public void setPosition(final int position){
		if (position >= 1 ){
			//If the layout for step 1 is visible then set the confirm image to visible
			if( step1.getVisibility() == View.VISIBLE ) {
				step1Confirm.setVisibility(View.VISIBLE);
			}
		}
		if (position >= 2 ){
			//If the layout for step 2 is visible then set the confirm image to visible
			if( step2.getVisibility() == View.VISIBLE) {
				step2Confirm.setVisibility(View.VISIBLE);
			}	
		}
		setIndicatorVisibility(position);
	}
	
	private void setIndicatorVisibility(final int position){
		if (position == 0){
			step1.setTextAppearance(getContext(), R.style.selected_status_indicator_text);
			indicator1.setVisibility(View.VISIBLE);
			indicator2.setVisibility(View.INVISIBLE);
			indicator3.setVisibility(View.INVISIBLE);
		}else if (position == 1){
			step2.setTextAppearance(getContext(), R.style.selected_status_indicator_text);
			step1.setTextAppearance(getContext(), R.style.status_indicator_text);
			indicator1.setVisibility(View.INVISIBLE);
			indicator2.setVisibility(View.VISIBLE);
			indicator3.setVisibility(View.INVISIBLE);
		}else {
			step3.setTextAppearance(getContext(), R.style.selected_status_indicator_text);
			step2.setTextAppearance(getContext(), R.style.status_indicator_text);
			indicator1.setVisibility(View.INVISIBLE);
			indicator2.setVisibility(View.INVISIBLE);
			indicator3.setVisibility(View.VISIBLE);
		}
	}
	
	/**
	 * Hides the second state in the bread crumb 
	 */
	public void hideStepTwo(){
		step2.setVisibility(View.GONE);
		step2Confirm.setVisibility(View.GONE);
	}
	
	
	
}
