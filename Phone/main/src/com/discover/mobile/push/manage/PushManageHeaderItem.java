package com.discover.mobile.push.manage;

import android.content.Context;
import android.content.res.Resources;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.discover.mobile.R;

/**
 * Push header item.  This will contain the text that is displayed to the user as well
 * as it will own the list associated with it so that it can be hidden.
 *  
 * @author jthornton
 *
 */
public class PushManageHeaderItem extends RelativeLayout{
	
	/**List associated with the list*/
	private LinearLayout list;
	
	/**TextView signifying that the elements should be hidden*/
	private TextView hide;

	/**String representing the item needs to be hidden*/
	private final String hideString; 

	/**String representing the item should be shown*/
	private final String showString;
	
	/**Title of the header*/
	private TextView title;
	
	/**
	 * Constructor of the class
	 * @param context - activity context
	 * @param attrs - attributes to give to the layout
	 */
	public PushManageHeaderItem(final Context context, final AttributeSet attrs) {
		super(context, attrs);
		
		final RelativeLayout mainView = (RelativeLayout) LayoutInflater.from(context)
                .inflate(R.layout.push_manage_header_item, null);
		final Resources res = context.getResources();
		hideString = res.getString(R.string.hide_notification_list);
		showString = res.getString(R.string.show_notification_list);
		
		hide = (TextView) mainView.findViewById(R.id.hide_view);
		title = (TextView) mainView.findViewById(R.id.title);
		
		mainView.removeAllViews();
		addView(hide);
		addView(title);
		setClickListener();
	}
	
	/**
	 * Set the text of the header
	 * @param header - the text to be set in the header
	 */
	public void setHeader(final String header){
		title = (TextView) findViewById(R.id.title);
		title.setText(header);
	}
	
	/**
	 * Set the on click listener of the show/hide button
	 */
	private void setClickListener() {
		this.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(final View v) {
				final String text = hide.getText().toString();
				if(hideString.equals(text)){
					hide.setText(showString);
					list.setVisibility(View.GONE);
				}else{
					hide.setText(hideString);
					list.setVisibility(View.VISIBLE);
				}
			}
		});
	}

	/**
	 * Set the list associated with this header
	 * @param list - the list associated with this header
	 */
	public void setList(final LinearLayout list){
		this.list = list;
	}
}
