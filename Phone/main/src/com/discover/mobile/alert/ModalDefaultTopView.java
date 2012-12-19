package com.discover.mobile.alert;

import android.content.Context;
import android.content.res.Resources;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.discover.mobile.R;

/**
 * Default top view to be displayed in the alert modal.  This contains a title as well
 * as some content text to be displayed under the text view.
 * 
 * @author jthornton
 *
 */
public class ModalDefaultTopView extends LinearLayout implements ModalTopView{
	
	/**Resources for showing strings*/
	private Resources res;
	
	/**View that holds the title*/
	private TextView title;
	
	/**View that holds the content text*/
	private TextView text;
	

	/**
	 * Constructor for the view
	 * @param context - activity context
	 * @param attrs - attributes to apply to the layout
	 */
	public ModalDefaultTopView(final Context context, final AttributeSet attrs) {
		super(context, attrs);
		
		final RelativeLayout mainView = (RelativeLayout) LayoutInflater.from(context)
                .inflate(R.layout.modal_default_top_view, null);
		
		res = context.getResources();
		
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
	 * Set the content of the view
	 * @param resource - int representing the resource to be displayed
	 */
	@Override
	public void setContent(final int resource) {
		text.setText(res.getString(resource));
	}

}
