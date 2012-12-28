package com.discover.mobile.push.history;

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

import com.discover.mobile.R;
import com.discover.mobile.common.callback.AsyncCallback;
import com.discover.mobile.common.callback.GenericAsyncCallback;
import com.discover.mobile.common.push.history.NotificationDetail;
import com.discover.mobile.common.push.history.PostNotificationRead;
import com.discover.mobile.common.push.history.PostReadDetail;

public class PushHistoryItem extends RelativeLayout{
	
	final Activity activity;
	
	private String id;
	
	private String deepLinkPage;
	
	private String messageReadStatus;
	
	final ImageView deleteBox;
	
	final TextView dateView;
	
	final TextView timeView;
	
	final TextView textView;
	
	final TextView actionView;
	
	final TextView expandedTextView;
	
	final TextView collapseView;

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
				.build();
		
		new PostNotificationRead(activity, callback, detail).submit();
	}

	public void toggleCollapseView(){
		if(collapseView.getText().toString().equals(activity.getResources().getString(R.string.show_more_text))){
			collapseView.setText(R.string.show_less_text);
			expandedTextView.setVisibility(View.VISIBLE);
		} else{
			collapseView.setText(R.string.show_more_text);
			expandedTextView.setVisibility(View.GONE);
		}
	}
	
	public void setItemRead(){
		dateView.setTypeface(null, Typeface.NORMAL);
		timeView.setTypeface(null, Typeface.NORMAL);
		textView.setTypeface(null, Typeface.NORMAL);
		actionView.setTypeface(null, Typeface.NORMAL);
		expandedTextView.setTypeface(null, Typeface.NORMAL);
		collapseView.setTypeface(null, Typeface.NORMAL);
	}
	
	public void setDate(final String date){
		dateView.setText(date);
	}
	
	public void setNotificationId(final String id){
		this.id = id;
	}
	
	public String getNotificationId(){
		return id;
	}
	
	public void setTime(final String time){
		timeView.setText(time);
	}
	
	public void setText(final String text){
		textView.setText(text);
	}
	
	public void setExpandedText(final String expandedText){
		expandedTextView.setText(expandedText);
	}
	
	public void setActionViewText(final String actionViewText){
		actionView.setText(actionViewText);
	}

	public String getDeepLinkPage() {
		return deepLinkPage;
	}

	public void setDeepLinkPage(final String deepLinkPage) {
		this.deepLinkPage = deepLinkPage;
	}

	public String getMessageReadStatus() {
		return messageReadStatus;
	}

	public void setMessageReadStatus(final String messageReadStatus) {
		this.messageReadStatus = messageReadStatus;
	}
}
