package com.discover.mobile.bank.ui.widgets;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.discover.mobile.bank.R;
import com.discover.mobile.common.DiscoverActivityManager;
import com.discover.mobile.common.nav.HeaderProgressIndicator;

/**
 * Class defines a widget used to display the progress of a work-flow for a user. This widge
 * allows up to 3 steps to a work-flow. It is a sub-class of HeaderProgressIndicator with a
 * help icon that is clickable.
 * 
 * @author henryoyuela
 *
 */
public class BankHeaderProgressIndicator extends HeaderProgressIndicator implements OnClickListener {
	/**
	 * Reference to help icon displayed which is meant to be clickable
	 */
	protected ImageView helpView;
	
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
	@Override
	protected void inflateHeader() {
		final LayoutInflater inflater = (LayoutInflater) getContext()
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

		inflater.inflate(R.layout.bank_header_progress, this);
		step1 = (TextView) findViewById(R.id.step1_title);
		step1Confirm = (ImageView) findViewById(R.id.step1_confirm);
		indicator1 = (ImageView) findViewById(R.id.first_indicator);
		
		step2 = (TextView) findViewById(R.id.step2_title);
		step2Confirm = (ImageView) findViewById(R.id.step2_confirm);
		indicator2 = (ImageView) findViewById(R.id.middle_indicator);
		
		step3 = (TextView) findViewById(R.id.step3_title);
		indicator3 = (ImageView) findViewById(R.id.last_indicator);

		helpView = (ImageView)findViewById(R.id.help_view);
		helpView.setOnClickListener(this);
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

	/**
	 * @return the helpView
	 */
	public ImageView getHelpView() {
		return helpView;
	}
}
