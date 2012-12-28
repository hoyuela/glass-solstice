package com.discover.mobile.push.history;

import java.util.ArrayList;
import java.util.List;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.discover.mobile.R;
import com.discover.mobile.RoboSherlockFragment;
import com.discover.mobile.common.callback.AsyncCallback;
import com.discover.mobile.common.callback.GenericAsyncCallback;
import com.discover.mobile.common.push.history.GetAlertHistory;
import com.discover.mobile.common.push.history.NotificationDetail;
import com.discover.mobile.common.push.history.NotificationListDetail;
import com.discover.mobile.push.manage.PushManageFragment;

public class PushHistoryFragment extends RoboSherlockFragment{
	
	/**Left/Right padding of the items*/
	private static final int PADDING_LR = 14;
	
	/**Top/Bottom padding of the items*/
	private static final int PADDING_TB = 28;
	
	public static final int ALERT_AMOUNT_TO_GET = 10;
	
	private static final String SPACE = " ";
	
	private List<NotificationDetail> notifications;
	
	private LinearLayout list;
	
	private TextView loadMore;
	
	private int currentIndex;
	
	private TextView olderAlerts;
	
	private TextView noAlerts;
	 
	@Override
	public View onCreateView(final LayoutInflater inflater, final ViewGroup container, final Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		final View mainView = inflater.inflate(R.layout.push_history_layout, null);
		final TextView manage = (TextView) mainView.findViewById(R.id.manage);
		manage.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(final View v) {
				showManageFragment();
			}
		});
		
		loadMore = (TextView) mainView.findViewById(R.id.load_more);
		loadMore.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(final View v) {
				getAlertHistory(currentIndex, currentIndex + ALERT_AMOUNT_TO_GET);
			}
		});
	
		list = (LinearLayout) mainView.findViewById(R.id.history_list);
		olderAlerts = (TextView) mainView.findViewById(R.id.older_alerts);
		noAlerts = (TextView) mainView.findViewById(R.id.no_alerts);
		notifications = new ArrayList<NotificationDetail>();
		getAlertHistory(0, ALERT_AMOUNT_TO_GET);
		return mainView;
	}
	
	public void showManageFragment(){
		this.getSherlockActivity().getSupportFragmentManager()
		.beginTransaction()
		.replace(R.id.navigation_content, new PushManageFragment())
		.addToBackStack(PushHistoryFragment.class.getSimpleName())
		.commit();
	}
	
	public void getAlertHistory(final int begin, final int end){
		currentIndex = end;
		final AsyncCallback<NotificationListDetail> callback = 
				GenericAsyncCallback.<NotificationListDetail>builder(this.getActivity())
				.showProgressDialog(getResources().getString(R.string.push_progress_get_title), 
									getResources().getString(R.string.push_progress_registration_loading), 
									true)
				.withSuccessListener(new PushHistorySuccessListener(this))
				.withErrorResponseHandler(new PushHistoryErrorHandler())
				.build();
		
		new GetAlertHistory(getActivity(), callback, begin, end).submit();
	}
	
	public void addToList(final NotificationListDetail details){
		if(notifications.isEmpty()){
			showNoAlertsView();
		}else{
			notifications.addAll(details.notifications);
			list.setVisibility(View.VISIBLE);
			updateList(details);
			
			if(details.notifications.size() < ALERT_AMOUNT_TO_GET){
				loadMore.setVisibility(View.INVISIBLE);
			}
		}
	}

	private void showNoAlertsView() {
		list.setVisibility(View.GONE);
		loadMore.setVisibility(View.GONE);
		olderAlerts.setVisibility(View.GONE);
		noAlerts.setVisibility(View.VISIBLE);
	}

	private void updateList(final NotificationListDetail details) {
		for(NotificationDetail detail : details.notifications){
			list.addView(createListItem(detail));
		}
	}
	
	private PushHistoryItem createListItem(final NotificationDetail detail){
		final PushHistoryItem item = new PushHistoryItem(this.getActivity(), null, this);
		final String[] dateString = detail.sentDate.split(SPACE);
		item.setNotificationId(detail.messageId);
		item.setMessageReadStatus(detail.messageReadInd);
		
		if(detail.messageReadInd.equals(NotificationDetail.READ)){
			item.setItemRead();
		}
		
		item.setText(detail.subject);
		item.setActionViewText(detail.getActionButtonText());
		item.setExpandedText(detail.text);
		item.setDeepLinkPage(detail.getPageCode());
		item.setDate(dateString[0]);
		item.setTime(dateString[1] + SPACE + dateString[2]);
		item.setBackgroundDrawable(this.getActivity().getResources().getDrawable(R.drawable.notification_list_item));
		item.setPadding(PADDING_LR, PADDING_TB, PADDING_LR ,PADDING_TB);
		return item;
	}

	@Override
	public int getActionBarTitle() {
		return R.string.push_alert_history_title;
	}
}
