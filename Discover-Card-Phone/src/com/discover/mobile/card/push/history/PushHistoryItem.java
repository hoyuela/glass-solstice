package com.discover.mobile.card.push.history;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.graphics.Typeface;
import android.support.v4.app.Fragment;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.discover.mobile.card.R;
import com.discover.mobile.card.services.push.history.NotificationDetail;
import com.discover.mobile.card.services.push.history.PostNotificationRead;
import com.discover.mobile.card.services.push.history.PostReadDetail;
import com.discover.mobile.common.callback.AsyncCallback;
import com.discover.mobile.common.callback.GenericAsyncCallback;
import com.discover.mobile.common.error.BaseExceptionFailureHandler;

/**
 * Class used to hold information about one notification object
 * @author jthornton
 *
 */
public class PushHistoryItem extends RelativeLayout{
	
	/**Activity using this item*/
	final Activity activity;
	
	/**Id of the notification*/
	private String id;
	
	/**Page to be used in deep linking*/
	private String deepLinkPage;
	
	/**Indicator for if the notification has been read*/
	private String messageReadStatus;
	
	/**Image view for the delete checkbox*/
	final ImageView deleteBox;
	
	/**View holding the date*/
	final TextView dateView;
	
	/**View holding the time*/
	final TextView timeView;
	
	/**View holding the subject*/
	final TextView textView;
	
	/**View holding the action text*/
	final TextView actionView;
	
	/**View holding more text*/
	final TextView expandedTextView;
	
	/**View that will trigger showing/hiding text*/
	final TextView collapseView;

	/**
	 * Create the item
	 * @param context - application context
	 * @param attrs - attributes to apply to the item
	 * @param fragment - fragment using the item
	 */
	public PushHistoryItem(final Context context, final AttributeSet attrs, final Fragment fragment){
		super(context, attrs);
		this.activity = fragment.getActivity();
		final RelativeLayout mainView = 
				(RelativeLayout)LayoutInflater.from(context).inflate(R.layout.push_history_item, null);
		
		deleteBox = (ImageView)mainView.findViewById(R.id.delete_box);
		dateView = (TextView)mainView.findViewById(R.id.date_view);
		timeView = (TextView)mainView.findViewById(R.id.time);
		textView = (TextView)mainView.findViewById(R.id.text);
		actionView = (TextView)mainView.findViewById(R.id.action_view);
		expandedTextView = (TextView)mainView.findViewById(R.id.expanded_text);
		collapseView = (TextView)mainView.findViewById(R.id.collapse_view);
	
		collapseView.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(final View v) {
				toggleCollapseView();
				if(messageReadStatus.equals(NotificationDetail.UNREAD)){
					markItemRead();
				}
			}
		});

		addView(mainView);

	}
	
	/**
	 * Mark an item read by contacting the server
	 */
	public void markItemRead(){
		setMessageReadStatus(NotificationDetail.READ);
		final PostReadDetail detail = new PostReadDetail();
		final List<String> messageIds = new ArrayList<String>();
		
		detail.action = PostReadDetail.MARK_READ;
		messageIds.add(id);
		detail.messageId = messageIds;
		
		final AsyncCallback<PostReadDetail> callback = 
				GenericAsyncCallback.<PostReadDetail>builder(activity)
				.showProgressDialog(getResources().getString(R.string.push_progress_get_title), 
									getResources().getString(R.string.push_progress_registration_loading), 
									true)
				.withSuccessListener(new ReadNotificationSucessListener(this))
				.withErrorResponseHandler(new ReadNotificationErrorHandler())
				.withExceptionFailureHandler(new BaseExceptionFailureHandler())
				.build();
		
		new PostNotificationRead(activity, callback, detail).submit();
	}

	/**
	 * Toggle the expandable view
	 */
	public void toggleCollapseView(){
		if(collapseView.getText().toString().equals(activity.getResources().getString(R.string.show_more_text))){
			collapseView.setText(R.string.show_less_text);
			expandedTextView.setVisibility(View.VISIBLE);
		} else{
			collapseView.setText(R.string.show_more_text);
			expandedTextView.setVisibility(View.GONE);
		}
	}
	
	/**
	 * Set the layout to look like the item has been read
	 */
	public void setItemRead(){
		dateView.setTypeface(null, Typeface.NORMAL);
		timeView.setTypeface(null, Typeface.NORMAL);
		textView.setTypeface(null, Typeface.NORMAL);
		actionView.setTypeface(null, Typeface.NORMAL);
		expandedTextView.setTypeface(null, Typeface.NORMAL);
		collapseView.setTypeface(null, Typeface.NORMAL);
	}
	
	/**
	 * Set the date in the date view
	 * @param date - date to be set
	 */
	public void setDate(final String date){
		dateView.setText(date);
	}
	
	/**
	 * Set the notification id
	 * @param id - id of the notification
	 */
	public void setNotificationId(final String id){
		this.id = id;
	}
	
	/**
	 * Get the notification id
	 * @return the notification id
	 */
	public String getNotificationId(){
		return id;
	}
	
	/**
	 * Set the time in the time view
	 * @param time - time to be set
	 */
	public void setTime(final String time){
		timeView.setText(time);
	}
	
	/**
	 * Set the text in the text view
	 * @param text - text to set
	 */
	public void setText(final String text){
		textView.setText(text);
	}
	
	/**
	 * Set the text in the expanded text view
	 * @param expandedText - text to set
	 */
	public void setExpandedText(final String expandedText){
		expandedTextView.setText(expandedText);
	}
	
	/**
	 * Set the text in the action view
	 * @param actionViewText text to set
	 */
	public void setActionViewText(final String actionViewText){
		actionView.setText(actionViewText);
	}

	/**
	 * Get the deep linking page
	 * @return the deep linking page
	 */
	public String getDeepLinkPage() {
		return deepLinkPage;
	}

	/**
	 * Set the deep linking page
	 * @param deepLinkPage - the page to set
	 */
	public void setDeepLinkPage(final String deepLinkPage) {
		this.deepLinkPage = deepLinkPage;
	}

	/**
	 * Get the status of whether or not the notification has been read
	 * @return the status of whether or not the notification has been read
	 */
	public String getMessageReadStatus() {
		return messageReadStatus;
	}

	/**
	 * Set the status of whether or not the notification has been read
	 * @param messageReadStatus - status to set
	 */
	public void setMessageReadStatus(final String messageReadStatus) {
		this.messageReadStatus = messageReadStatus;
	}
}
