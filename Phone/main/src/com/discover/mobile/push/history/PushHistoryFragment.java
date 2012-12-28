package com.discover.mobile.push.history;

import java.util.ArrayList;
import java.util.List;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.discover.mobile.R;
import com.discover.mobile.RoboSherlockFragment;
import com.discover.mobile.common.callback.AsyncCallback;
import com.discover.mobile.common.callback.GenericAsyncCallback;
import com.discover.mobile.common.push.history.GetAlertHistory;
import com.discover.mobile.common.push.history.NotificationDetail;
import com.discover.mobile.common.push.history.NotificationListDetail;

public class PushHistoryFragment extends RoboSherlockFragment{
	
	public static final int ALERT_AMOUNT_TO_GET = 10;
	
	private List<NotificationDetail> notifications;
	
	@Override
	public View onCreateView(final LayoutInflater inflater, final ViewGroup container, final Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		final View mainView = inflater.inflate(R.layout.push_history_layout, null);
	
		notifications = new ArrayList<NotificationDetail>();
		getAlertHistory(0, ALERT_AMOUNT_TO_GET);
		return mainView;
	}
	
	public void getAlertHistory(final int begin, final int end){
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
		notifications.addAll(details.notifications);
	}

	@Override
	public int getActionBarTitle() {
		return R.string.push_alert_history_title;
	}

}
