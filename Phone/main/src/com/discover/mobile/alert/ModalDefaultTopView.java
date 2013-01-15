package com.discover.mobile.alert;

import android.content.Context;
import android.content.res.Resources;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;

import com.discover.mobile.R;

/**
 * Default top view to be displayed in the alert modal.  This contains a title as well
 * as some content text to be displayed under the text view.
 * 
 * @author jthornton
 *
 */
public class ModalDefaultTopView extends ScrollView implements ModalTopView{
	
	/**Resources for showing strings*/
	private Resources res;
	
	/**Optional error image to be placed to the left of the title*/
	private ImageView errorImage;
	
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
		
		final View mainView = (View) LayoutInflater.from(context)
                .inflate(R.layout.modal_default_top_view, null);
		
		res = context.getResources();
		
		errorImage = (ImageView) mainView.findViewById(R.id.error_icon);
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
	
	/**
	 * Show an error icon to the left of the modal dialog title.
	 * @param isError - tells the dialog to show an error icon or not.
	 */
	public void showErrorIcon(final boolean isError) {
		if(isError) {
			errorImage.setVisibility(View.VISIBLE);
		}else {
			errorImage.setVisibility(View.GONE);
		}
		
	}
	/**
	 * Set the content of the view with dynamic text 
	 * 
	 * DO NOT USE WITH STATIC TEXT, PLEASE USE INT METHOD 
	 * and pull from resource file
	 * 
	 * @param resource - int representing the resource to be displayed
	 */
	public void setDynamicContent(final String content) {
		text.setText(content);
	}

}
