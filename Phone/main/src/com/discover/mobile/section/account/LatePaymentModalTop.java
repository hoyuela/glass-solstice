package com.discover.mobile.section.account;

import android.content.Context;
import android.content.res.Resources;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ScrollView;
import android.widget.TextView;

import com.discover.mobile.R;
import com.discover.mobile.alert.ModalTopView;

/**
 * Late payment modal top view.  This contains a title as well
 * as some content text to be displayed under the text view.
 * 
 * @author jthornton
 *
 */
public class LatePaymentModalTop extends ScrollView implements ModalTopView {
	
	/**Resources for showing strings*/
	private Resources res;
	
	/**View that holds the title*/
	private TextView title;
	
	/**View that holds the content text*/
	private TextView text;
	
	/**View holding payment due date*/
	private TextView dueDate;
	

	/**
	 * Constructor for the view
	 * @param context - activity context
	 * @param attrs - attributes to apply to the layout
	 */
	public LatePaymentModalTop(final Context context, final AttributeSet attrs) {
		super(context, attrs);
		
		final View mainView = (View) LayoutInflater.from(context)
                .inflate(R.layout.modal_late_payment_warning_top, null);
		
		res = context.getResources();
		
		dueDate = (TextView) mainView.findViewById(R.id.payment_date);
		title = (TextView) mainView.findViewById(R.id.modal_alert_title);
		text = (TextView) mainView.findViewById(R.id.modal_alert_text);
		
		addView(mainView);
	}

	/**
	 * Set the title of the view
	 * @param resource - int representing the resource to be displayed
	 */
	@Override
	public void setTitle(final int resource) {
		title.setText(res.getString(resource));	
	}
	
	/**
	 * Set the date of the payment due date field
	 * @param date - date to set
	 */
	public void setPaymentDate(final String date){
		dueDate.setText(date);
	}

	/**
	 * Set the content of the view
	 * @param resource - int representing the resource to be displayed
	 */
	@Override
	public void setContent(final int resource) {
		text.setText(res.getString(resource));
	}

	/**
	 * Set the content of the view with dynamic text 
	 * 
	 * DO NOT USE WITH STATIC TEXT, PLEASE USE INT METHOD 
	 * and pull from resource file
	 * 
	 * @param resource - string representing the resource to be displayed
	 */
	public void setDynamicContent(final String content) {
		text.setText(content);
	}

}
