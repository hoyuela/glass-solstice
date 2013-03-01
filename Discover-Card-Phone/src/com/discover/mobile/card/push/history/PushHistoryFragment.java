package com.discover.mobile.card.push.history;

import java.util.ArrayList;
import java.util.List;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.discover.mobile.card.CardMenuItemLocationIndex;
import com.discover.mobile.card.CardSessionContext;
import com.discover.mobile.card.R;
import com.discover.mobile.card.push.manage.PushManageFragment;
import com.discover.mobile.card.services.push.history.GetAlertHistory;
import com.discover.mobile.card.services.push.history.NotificationDetail;
import com.discover.mobile.card.services.push.history.NotificationListDetail;
import com.discover.mobile.common.BaseFragment;
import com.discover.mobile.common.callback.AsyncCallback;
import com.discover.mobile.common.callback.GenericAsyncCallback;
import com.discover.mobile.common.error.BaseExceptionFailureHandler;

public class PushHistoryFragment extends BaseFragment{

	/**Left/Right padding of the items*/
	private static final int PADDING_LR = 14;

	/**Top/Bottom padding of the items*/
	private static final int PADDING_TB = 28;

	/**Static int for the amount of notifications to get from the server*/
	private static final int ALERT_AMOUNT_TO_GET = 10;

	/**Key to get the rotated value from the bundle*/
	private static final String ROTATED = "rotated";

	/**Key to get the current index from the bundle*/
	private static final String INDEX = "index";

	/**String representing a space*/
	private static final String SPACE = " ";

	/**List of notifications retrieved from the server*/
	private List<NotificationDetail> notifications;

	/**List displaying the notifications*/
	private LinearLayout list;

	/**Text view used for loading more notifications*/
	private TextView loadMore;

	/**Current index for of the notifications on the server*/
	private int currentIndex = 0;

	/**Text view that hold the older alerts text*/
	private TextView olderAlerts;

	/**Text view holding the no alerts text*/
	private TextView noAlerts;

	/**
	 * Create the view
	 * @param inflater - inflater that will inflate the layout
	 * @param container - container holding the fragment
	 * @param savedInstanceState - bundle holding the state of the app
	 * @return the view
	 */
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
				getAlertHistory(currentIndex, ALERT_AMOUNT_TO_GET);
			}
		});

		list = (LinearLayout) mainView.findViewById(R.id.history_list);
		olderAlerts = (TextView) mainView.findViewById(R.id.older_alerts);
		noAlerts = (TextView) mainView.findViewById(R.id.no_alerts);
		notifications = new ArrayList<NotificationDetail>();

		if(null != savedInstanceState){
			resumeFragment(savedInstanceState);
		} else{
			getAlertHistory(0, ALERT_AMOUNT_TO_GET);
		}

		return mainView;
	}

	/**
	 * Save the state of the fragment
	 * @param outState - bundle to put the state in
	 */
	@Override
	public void onSaveInstanceState(final Bundle outState){
		outState.putBoolean(ROTATED, true);
		CardSessionContext.getCurrentSessionDetails().setNotifications(notifications);
		outState.putInt(INDEX, currentIndex);
		super.onSaveInstanceState(outState);
	}

	/**
	 * Resume the fragment
	 * @param savedInstanceState - bundle holding the state of the fragment
	 */
	private void resumeFragment(final Bundle savedInstanceState) {
		currentIndex = savedInstanceState.getInt(INDEX);
		notifications = CardSessionContext.getCurrentSessionDetails().getNotifications();
		final NotificationListDetail details = new NotificationListDetail();
		details.notifications = notifications;
		addToList(details);
		CardSessionContext.getCurrentSessionDetails().getNotifications().clear();
	}

	/**
	 * Replace this fragment with the manage alerts fragment
	 */
	public void showManageFragment(){
		this.getSherlockActivity().getSupportFragmentManager()
		.beginTransaction()
		.replace(R.id.navigation_content, new PushManageFragment())
		.addToBackStack(PushHistoryFragment.class.getSimpleName())
		.commit();
	}

	/**
	 * Get some of the alert history
	 * @param begin - index to start at
	 * @param amount - number of notifications to get
	 */
	public void getAlertHistory(final int begin, final int amount){
		currentIndex += amount;
		final AsyncCallback<NotificationListDetail> callback = 
				GenericAsyncCallback.<NotificationListDetail>builder(this.getActivity())
				.showProgressDialog(getResources().getString(R.string.push_progress_get_title), 
						getResources().getString(R.string.push_progress_registration_loading), 
						true)
						.withSuccessListener(new PushHistorySuccessListener(this))
						.withErrorResponseHandler(new PushHistoryErrorHandler())
						.withExceptionFailureHandler(new BaseExceptionFailureHandler())
						.build();

		new GetAlertHistory(getActivity(), callback, begin, amount).submit();
	}

	/**
	 * Add the new details to the list
	 * @param details details to add to the list
	 */
	public void addToList(final NotificationListDetail details){
		if(null == details || null == details.notifications){
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

	/**
	 * Show the no alerts view 
	 */
	private void showNoAlertsView() {
		list.setVisibility(View.GONE);
		loadMore.setVisibility(View.GONE);
		olderAlerts.setVisibility(View.GONE);
		noAlerts.setVisibility(View.VISIBLE);
	}

	/**
	 * Update the list from the list of items
	 * @param details - list of items that will be used to updated the list
	 */
	private void updateList(final NotificationListDetail details) {
		for(final NotificationDetail detail : details.notifications){
			list.addView(createListItem(detail));
		}
	}

	/**
	 * Create a push history item
	 * @param detail - detail to create the item from
	 * @return the push history item
	 */
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

	/**
	 * Set the action bar title
	 */
	@Override
	public int getActionBarTitle() {
		return R.string.push_alert_history_title;
	}

	@Override
	public int getGroupMenuLocation() {
		return CardMenuItemLocationIndex.PROFILE_AND_SETTINGS_GROUP;
	}

	@Override
	public int getSectionMenuLocation() {
		return CardMenuItemLocationIndex.ALERTS_HISTORY_SECTION;
	}
}
