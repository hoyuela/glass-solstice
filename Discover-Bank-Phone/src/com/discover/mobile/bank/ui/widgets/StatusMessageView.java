package com.discover.mobile.bank.ui.widgets;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.discover.mobile.bank.R;
import com.discover.mobile.bank.util.TimedViewController;

/**
 * Utility helper class used to display a view for a fixed amount of time in milliseconds, before
 * it's visibility is set from View.VISIBLE to View.GONE.
 * 
 * How to use this class:
 * 
 * Declare the class in the xml layout:
 * <com.discover.mobile.bank.ui.widgets.StatusMessageView
 *       android:id="@+id/status_message"
 *       android:layout_width="wrap_content"
 *     	 android:layout_height="wrap_content"
 *       android:layout_alignParentTop="true"
 *       />
 *	
 * Use the following code to programmatically start the timer that will show the view and then hide
 * it after the elapse time:
 *		final StatusMessageView status = (StatusMessageView)this.getView().findViewById(R.id.status_message);
 *		status.setText(R.string.bank_pmt_deleted);
 *		status.showAndHide(5000);
 *	
 * @author henryoyuela
 *
 */
public class StatusMessageView extends RelativeLayout {
	public StatusMessageView(final Context context) {
		super(context);
		
		inflateLayout();
	}
	
	public StatusMessageView(final Context context, final AttributeSet attrs) {
		super(context, attrs);
		
		inflateLayout();
	}

	public StatusMessageView(final Context context, final AttributeSet attrs, final int defStyle) {
		super(context, attrs, defStyle);
		
		inflateLayout();
	}

	protected void inflateLayout() {
		final LayoutInflater inflater = (LayoutInflater) getContext()
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

		inflater.inflate(R.layout.bank_status_view, this);
		
		this.setVisibility(View.GONE);
	}
	
	/**
	 * Method used to set the text of the status message that is displayed
	 * 
	 * @param text Resource identifier for the string that is to be displayed in the Status view
	 */
	public void setText(final int text) {
		final TextView statusText = (TextView)this.findViewById(R.id.status_text);
		statusText.setText(this.getResources().getString(text));
	}
	
	/**
	 * Method used to show the status view for the amount of time specified in delay (in milliseconds).
	 * 
	 * @param delay Specifies the delay in milliseconds to show the status view before setting 
	 * 		  its visibility to "gone".
	 */
	public void showAndHide(final int delay) {
		final TimedViewController tvc = new  TimedViewController(this, delay);
		tvc.start();
	}
	
}
