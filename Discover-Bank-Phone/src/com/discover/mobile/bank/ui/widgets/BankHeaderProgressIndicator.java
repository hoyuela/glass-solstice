package com.discover.mobile.bank.ui.widgets;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.hardware.SensorManager;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.OrientationEventListener;
import android.view.Surface;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.discover.mobile.bank.R;
import com.discover.mobile.common.DiscoverActivityManager;

/**
 * Class defines a widget used to display the progress of a work-flow for a user. This widge
 * allows up to 3 steps to a work-flow. It is a sub-class of HeaderProgressIndicator with a
 * help icon that is clickable.
 * 
 * @author henryoyuela
 *
 */
public class BankHeaderProgressIndicator extends RelativeLayout implements OnClickListener {
	protected TextView step1;
	protected TextView step2;
	protected TextView step3;
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
	protected void inflateHeader() {
		final LayoutInflater inflater = (LayoutInflater) getContext()
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

		inflater.inflate(R.layout.bank_header_progress, this);
		step1 = (TextView) findViewById(R.id.step_1);	
		step2 = (TextView) findViewById(R.id.step_2);
		step3 = (TextView) findViewById(R.id.step_3);
		

		helpView = (ImageView)findViewById(R.id.help_view);
		helpView.setOnClickListener(this);
		
		createOrientationListener();
	}

	/**
	 * Initiates the header and sets the current position for Change Password
	 * @param position - Current position between 0-2
	 */
	public void initChangePasswordHeader(final int position) {
		inflateHeader();
		setTitle(R.string.enter_info, R.string.create_password,R.string.confirm);
		setPosition(position);
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
		final Drawable confirm = this.getContext().getResources().getDrawable(R.drawable.gray_check_mark);
		final Drawable indicator = this.getContext().getResources().getDrawable(R.drawable.orange_down_arrow);
		
		switch( position ) {
		case 0:
			step1.setCompoundDrawablesWithIntrinsicBounds(null, indicator, null, null);
			step1.setPadding(0, 0, 0, 0);
			step2.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null);
			step3.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null);
			break;
		case 1:
			step1.setCompoundDrawablesWithIntrinsicBounds(confirm, null, null, null);
			step2.setCompoundDrawablesWithIntrinsicBounds(null, indicator, null, null);
			step2.setPadding(0, 0, 0, 0);
			step3.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null);
			break;
		case 2:
			step1.setCompoundDrawablesWithIntrinsicBounds(confirm, null, null, null);
			step2.setCompoundDrawablesWithIntrinsicBounds(confirm, null, null, null);
			step3.setCompoundDrawablesWithIntrinsicBounds(null, indicator, null, null);
			step3.setPadding(0, 0, 0, 0);
			break;
		default:
			step1.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null);
			step2.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null);
			step3.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null);
			break;
		}
	}
	
	
	/**
	 * Hides the second state in the bread crumb 
	 */
	public void hideStepTwo(){
		step2.setVisibility(View.GONE);
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
	
	
	/**
	 * Create the orientation changed listener
	 * @return the orientation changed listener
	 */
	public OrientationEventListener createOrientationListener() {
		final OrientationEventListener ret = new OrientationEventListener(this.getContext(), SensorManager.SENSOR_DELAY_NORMAL) {
			@Override
			public void onOrientationChanged(final int arg0) {
				final LinearLayout.LayoutParams step2Params = (android.widget.LinearLayout.LayoutParams) step2.getLayoutParams();
				final int rotation = ((WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay().getOrientation();
				
				/**Check if there are three steps in breadcrumb*/
				if( step2.getVisibility() != View.GONE) {
			
					if( rotation == Surface.ROTATION_90 ) {
						final int dimen = (int) getContext().getResources().getDimension(R.dimen.breadcrumb_step2_landscape_padding);
						step2Params.setMargins(dimen, 0, dimen, 0);
						step2.setLayoutParams(step2Params);
					} else {
						final int dimen = (int) getContext().getResources().getDimension(R.dimen.breadcrumb_step2_portrait_padding);
						step2Params.setMargins(dimen, 0, dimen, 0);
						step2.setLayoutParams(step2Params);
					}
				}
				/** Handle only two steps in breadcrumb*/
				else {
					if( rotation == Surface.ROTATION_180 ) {
						//Set margins for step 2
						
					} else if( rotation == Surface.ROTATION_90) {
						
						
						
					}
				}
			}
		};

		ret.enable();
		return ret;  
	}

}
