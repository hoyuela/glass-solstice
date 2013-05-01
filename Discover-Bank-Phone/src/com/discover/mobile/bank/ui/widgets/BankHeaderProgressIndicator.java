package com.discover.mobile.bank.ui.widgets;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.discover.mobile.bank.R;
import com.discover.mobile.common.DiscoverActivityManager;

/**
 * Class defines a widget used to display the progress of a work-flow for a user. This widge
 * allows up to 3 steps to a work-flow. 
 * 
 * @author henryoyuela
 *
 */
public class BankHeaderProgressIndicator extends RelativeLayout implements OnClickListener {
	private static final int MAX_POSITION = 3;
	
	public BankHeaderProgressIndicator(final Context context) {
		super(context);

	}
	
	public BankHeaderProgressIndicator(final Context context, final AttributeSet attrs) {
		super(context, attrs);
	}

	public BankHeaderProgressIndicator(final Context context, final AttributeSet attrs, final int defStyle) {
		super(context, attrs, defStyle);
	}
	
	/**
	 * Method used to provide layout during creation of widget. In addition it setup
	 * any event listeners for controls in the view.
	 */
	protected void inflateHeader() {
		final LayoutInflater inflater = (LayoutInflater) getContext()
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		
		inflater.inflate(R.layout.bank_header_progress, this);
		
	}

	/**
	 * Initiates the header and sets the current position for Change Password
	 * @param position - Current position between 0-2
	 */
	public void initialize(final int position) {
		inflateHeader();
		setTitle(R.string.enter_info, R.string.create_password,R.string.confirm);
		setPosition(position);
	}


	/**
	 * Sets the titles for the header
	 */
	public void setTitle(final int title1, final int title2, final int title3) {
		final TextView step1 = (TextView) findViewById(R.id.step1_text);	
		final TextView step2 = (TextView) findViewById(R.id.step2_text);
		final TextView step3 = (TextView) findViewById(R.id.step3_text);
		
		step1.setText(getResources().getString(title1));
		step2.setText(getResources().getString(title2));
		step3.setText(getResources().getString(title3));
	}
	
	/**
	 * Sets up the current position of the header
	 * @param position - number between 0-2
	 */
	public void setPosition(final int position){
		final TextView step1 = (TextView) findViewById(R.id.step1_text);
		final TextView step2 = (TextView) findViewById(R.id.step2_text);
		final TextView step3 = (TextView) findViewById(R.id.step3_text);
		final ImageView step1Confirm = (ImageView)findViewById(R.id.step1_confirm);
		final ImageView step2Confirm = (ImageView)findViewById(R.id.step2_confirm);
		final ImageView step1Indicator = (ImageView)findViewById(R.id.step1_indicator);
		final ImageView step2Indicator = (ImageView)findViewById(R.id.step2_indicator);
		final ImageView step3Indicator =(ImageView)findViewById(R.id.step3_indicator);
		final RelativeLayout step2Layout = (RelativeLayout)findViewById(R.id.step2);
		
		if (position > 1 ){
			step1Confirm.setVisibility(View.VISIBLE);
			step1Indicator.setVisibility(View.INVISIBLE);
		} else if( position <= 1) {
			step1.setTextAppearance(getContext(), R.style.selected_status_indicator_text);
			step1Indicator.setVisibility(View.VISIBLE);
			step1Confirm.setVisibility(View.INVISIBLE);
		}
		
		if (position > 2 ){
			step2Confirm.setVisibility(View.VISIBLE);
			step2Indicator.setVisibility(View.INVISIBLE);
		} else if( position == 2) {
			step2.setTextAppearance(getContext(), R.style.selected_status_indicator_text);
			step2Indicator.setVisibility(View.VISIBLE);
			step2Confirm.setVisibility(View.GONE);
		}
		
		if( position == MAX_POSITION || step2Layout.getVisibility() != View.VISIBLE ) {
			step3.setTextAppearance(getContext(), R.style.selected_status_indicator_text);
			step3Indicator.setVisibility(View.VISIBLE);
		} else {
			step3Indicator.setVisibility(View.INVISIBLE);
		}
		
	}
	
	
	/**
	 * Hides the second state in the bread crumb 
	 */
	public void hideStepTwo(){
		final RelativeLayout step2Layout = (RelativeLayout)findViewById(R.id.step2);
		final ImageView step2Indicator = (ImageView)findViewById(R.id.step2_indicator);
		
		step2Layout.setVisibility(View.GONE);
		
		if( step2Indicator.getVisibility() == View.VISIBLE ) {
			setPosition(MAX_POSITION);
		}
	}
	
	/**
	 * Click listener for any views within this widget
	 */
	@Override
	public void onClick(final View arg0) {
		
		//This will be defined once the help landing page has been defined		
		final CharSequence text = "Help Under Development";
		final int duration = Toast.LENGTH_SHORT;

		final Toast toast = Toast.makeText(DiscoverActivityManager.getActiveActivity(), text, duration);
		toast.show();
		
	}
}
