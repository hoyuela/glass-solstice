package com.discover.mobile.card.fastcheck;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.DateFormatSymbols;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;

import android.content.Context;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Handler;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.text.util.Linkify;
import android.util.Log;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.discover.mobile.card.R;
import com.discover.mobile.card.account.summary.SimpleListItem;
import com.discover.mobile.card.account.summary.SimpleListItemFactory;
import com.discover.mobile.card.common.CardEventListener;
import com.discover.mobile.card.common.net.error.CardErrorBean;
import com.discover.mobile.card.common.net.service.WSAsyncCallTask;
import com.discover.mobile.card.common.net.service.WSRequest;
import com.discover.mobile.card.common.net.utility.NetworkUtility;
import com.discover.mobile.card.common.sharedata.CardShareDataStore;
import com.discover.mobile.card.common.utils.FastcheckUtil;
import com.discover.mobile.card.common.utils.Utils;
import com.discover.mobile.common.BaseFragment;
import com.discover.mobile.common.analytics.AnalyticsPage;
import com.discover.mobile.common.analytics.TrackingHelper;
import com.discover.mobile.common.facade.LoginActivityInterface;
import com.discover.mobile.common.nav.NavigationRootActivity;
import com.discover.mobile.common.net.json.JacksonObjectMapperHolder;
import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.slidingmenu.lib.SlidingMenu;
import com.slidingmenu.lib.SlidingMenu.OnCloseListener;
import com.slidingmenu.lib.SlidingMenu.OnOpenListener;

public class FastcheckFragment extends BaseFragment implements
		CardEventListener, OnOpenListener, OnCloseListener, OnClickListener {

	private final String TAG = FastcheckFragment.class.getSimpleName();
	private static final long explicitRefreshInterval = 10L * 1000L; // 10 seconds
	private static final String CASH_BACK_CODE = "CBB";
	private static final String DBC_CASH_BACK_CODE = "SBC";
	private static final String MILES_CODE = "MI2";
	private static final String DBC_MILES_CODE = "SML";
	private static final int NORMAL_RESULT_PAGE = 0;
	private static final int CANNOT_ACCESS_RESULT_PAGE = 1;
	private static final int TECH_DIFF_RESULT_PAGE = 2;
	private static final int NO_FASTCHECK_TOKEN_RESULT_PAGE = 3;
	private static final int MAINTENANCE_RESULT_PAGE = 4;
	private static final int INITIAL_RESULT_PAGE = -1;
	private static final String TIME_ONLY_DISPLAY_FORMAT = "h:mma";
	private static final String DATE_ONLY_DISPLAY_FORMAT = "MM/dd/yy";
	private static final String CASH_REWARDS_FORMAT = "#,##0.00";
	private static final String MILE_REWARDS_FORMAT = "#,##0";
		
	// data bean
	private FastcheckDetail fastcheckDetail;
	private Calendar lastUpdateTimeCal;
	private int resultPage = INITIAL_RESULT_PAGE;
	private String fastcheckMaintenanceMsg;

	// ui elements
	private LinearLayout fastcheckList;
	private TextView fastcheckErrorMsg;
	private ScrollView view;
	
	// listeners
	private GestureDetector gestureDetector;
    private OnTouchListener gestureListener;

	@Override
	public void onActivityCreated(final Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
	}

	@Override
	public View onCreateView(final LayoutInflater inflater,
			final ViewGroup container, final Bundle savedInstanceState) {
			
		view = (ScrollView) inflater.inflate(R.layout.fastcheck_fragment,
				null);
		
		((NavigationRootActivity) getActivity()).getSlidingMenu()
				.setOnOpenListener(this);
		((NavigationRootActivity) getActivity()).getSlidingMenu()
				.setOnCloseListener(this);
			
		gestureDetector = new GestureDetector((NavigationRootActivity) getActivity(), new SwipeGestureDetector());
		gestureListener = new OnTouchListener() {
	        @Override
			public boolean onTouch(final View v, final MotionEvent event) {
	            return gestureDetector.onTouchEvent(event);
	        }
	    };
	    view.setOnClickListener(this);
	    view.setOnTouchListener(gestureListener);
	
		return view;
	}

	@Override
	public void onOpen() {
		boolean isFastcheckHidden = (((NavigationRootActivity) getActivity())
				.getSlidingMenu().getTouchModeAbove() == SlidingMenu.TOUCHMODE_NONE);
		if (!isFastcheckHidden) {
			TrackingHelper.trackPageView(AnalyticsPage.QUICKVIEW_VIEW);
			getFastcheckData(false);
		}
	}

	 @Override
	 public void onClose() {
		 if (resultPage == NO_FASTCHECK_TOKEN_RESULT_PAGE)
			 ((LoginActivityInterface)getActivity()).hideFastcheck();
		 
	 }
	
	
	@Override
	public int getActionBarTitle() {
		return BaseFragment.NO_TITLE;
	}

	@Override
	public int getGroupMenuLocation() {
		return -1;
	}

	@Override
	public int getSectionMenuLocation() {
		return -1;
	}

	
	private boolean timeToMakeAnotherCall(Context context, Resources res) {
		long lastUpdateMs = 0L;
		if (lastUpdateTimeCal == null) {
			CardShareDataStore cardShareDataStoreObj = CardShareDataStore
	                .getInstance(context);
			Long cachedLastUpdateTimeInMS = (Long)cardShareDataStoreObj.getValueOfAppCache(res
	                .getString(R.string.fast_check_last_retrieval_time_in_ms));
			if (cachedLastUpdateTimeInMS == null) return true;
			else {
				lastUpdateMs = cachedLastUpdateTimeInMS.longValue();
				lastUpdateTimeCal = Calendar.getInstance();
				lastUpdateTimeCal.setTimeInMillis(lastUpdateMs);
			}
		} else lastUpdateMs = lastUpdateTimeCal.getTimeInMillis();
		long currentMs = Calendar.getInstance().getTimeInMillis();
		
		if ((currentMs - lastUpdateMs) <= explicitRefreshInterval && 
				(currentMs - lastUpdateMs) >= 0)
			return false;
		else
			return true;
	}

	private void getFastcheckData(boolean spinOnNoFetch) {
		Context context = getActivity().getApplicationContext();
		final Resources res = context.getResources();
		final CardShareDataStore cardShareDataStoreObj = CardShareDataStore
                .getInstance(context);
		if (!timeToMakeAnotherCall(context, res)) {
			if (spinOnNoFetch) {
				Utils.showSpinner(getActivity(), res.getString(R.string.fast_check_spinner_msg_part1),
						res.getString(R.string.fast_check_spinner_msg_part2));
				Handler handler = new Handler();
				handler.postDelayed(new Runnable() {
					public void run() {
						Utils.hideSpinner();
					}
				}, 500);
			}
			// orientation change fix 
			if (resultPage == INITIAL_RESULT_PAGE) checkOrientationChange(res, cardShareDataStoreObj);
				
			showPreviousPage(res);
			return;
		}
		
		String encryptedDeviceToken = FastcheckUtil.readFastcheckToken(getActivity());
		String deviceToken = null;
		try {
			deviceToken = FastcheckUtil.decrypt(encryptedDeviceToken);
		} catch (Exception e) {
			 if (Log.isLoggable(TAG, Log.ERROR))
				 Log.e(TAG, "getFastcheckData() gets Exception during FastcheckUtil.decrypt()" + e.getMessage());
			resultPage = TECH_DIFF_RESULT_PAGE;
			FastcheckUtil.storeFastcheckToken(getActivity(), null); // nullify invalid token
			showFastcheckErrorPage(res.getString(R.string.fast_check_error_tech_diff));
			return;
		}
			
		if (deviceToken == null || deviceToken.length() != 88) {
			 if (Log.isLoggable(TAG, Log.ERROR))
				 Log.e(TAG, "getFastcheckData(), token is NULL or length is not proper" );
			if (deviceToken != null) FastcheckUtil.storeFastcheckToken(getActivity(), null); // nullify invalid token that is not long in 88
			showFastcheckErrorPage(res.getString(R.string.fast_check_error_tech_diff));
			return;
		} 
		
		// Setting the headers available for the service
		WSRequest request = new WSRequest();
		HashMap<String, String> headers = request.getHeaderValues();
		request.setHeaderValues(headers);

		// Setting url
		String url = NetworkUtility.getWebServiceUrl(getActivity(),
				R.string.fastcheck_url);
		request.setUrl(url);

		// Setting POST and body
		request.setMethodtype("POST");
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
			
		try {
			JacksonObjectMapperHolder.getMapper().writeValue(baos, new FastcheckToken(deviceToken));
		} catch (JsonGenerationException e) {
			 if (Log.isLoggable(TAG, Log.ERROR))
				 Log.e(TAG, "getFastcheckData() gets JsonGenerationException " + e.getMessage());
			resultPage = TECH_DIFF_RESULT_PAGE;
			showFastcheckErrorPage(res.getString(R.string.fast_check_error_tech_diff));
			return;
		} catch (JsonMappingException e) {
			 if (Log.isLoggable(TAG, Log.ERROR))
				 Log.e(TAG, "getFastcheckData() gets JsonMappingException " + e.getMessage());
			resultPage = TECH_DIFF_RESULT_PAGE;
			showFastcheckErrorPage(res.getString(R.string.fast_check_error_tech_diff));
			return;
		} catch (IOException e) {
			 if (Log.isLoggable(TAG, Log.ERROR))
				 Log.e(TAG, "getFastcheckData() gets IOException " + e.getMessage());
			resultPage = TECH_DIFF_RESULT_PAGE;
			showFastcheckErrorPage(res.getString(R.string.fast_check_error_tech_diff));
			return;
		}
			
		request.setInput(baos.toByteArray());

		// Making the call
		WSAsyncCallTask serviceCall = new WSAsyncCallTask(getActivity(),
				new FastcheckDetail(), res.getString(R.string.fast_check_spinner_msg_part1),
				res.getString(R.string.fast_check_spinner_msg_part2), this);
		serviceCall.execute(request);
		
	}

	
	private String formatTimeStamp() {
		SimpleDateFormat sdf = new SimpleDateFormat(TIME_ONLY_DISPLAY_FORMAT, Locale.US);
		DateFormatSymbols symbols = new DateFormatSymbols();
        symbols.setAmPmStrings(new String[] { "am", "pm" });
        sdf.setDateFormatSymbols(symbols);
		String lastUpdateTimeStr = sdf.format(lastUpdateTimeCal.getTime());
		sdf = new SimpleDateFormat("yyyyMMdd", Locale.US);
		int lastUpdateDateInt = Integer.parseInt(sdf.format(lastUpdateTimeCal
				.getTime()));
		int todayInt = Integer.parseInt(sdf.format(Calendar.getInstance()
				.getTime()));
		if (lastUpdateDateInt == todayInt) {
			return new StringBuffer("\nUpdated today at ").append(
					lastUpdateTimeStr).toString();
		} else {
			sdf = new SimpleDateFormat(DATE_ONLY_DISPLAY_FORMAT, Locale.US);
			String lastUpdateDateStr = sdf.format(lastUpdateTimeCal.getTime());
			return new StringBuffer("Updated ").append(lastUpdateDateStr)
					.append(" at ").append(lastUpdateTimeStr).toString();
		}
	}

	private String formatSalutation(final Context context) {
		final Resources res = context.getResources();
		StringBuffer sb = new StringBuffer();
		if (Calendar.getInstance().get(Calendar.HOUR_OF_DAY) < 12)
			sb.append(res.getString(R.string.fast_check_salutation_good_morning));
		else if (Calendar.getInstance().get(Calendar.HOUR_OF_DAY) < 18)
			sb.append(res.getString(R.string.fast_check_salutation_good_afternoon));
		else
			sb.append(res.getString(R.string.fast_check_salutation_good_evening));
		String tmp = fastcheckDetail.getCardmemberFirstName();
		if (tmp != null) tmp = tmp.substring(0, 1) + tmp.substring(1).toLowerCase(Locale.US);
		sb.append(" ");
		sb.append(tmp);
		return sb.toString();
	}

	private SimpleListItem createFastcheckTitleListItem(final Context context) {
		final DisplayFastcheckTitleListItem item = new DisplayFastcheckTitleListItem(
				context, null);
		item.setLabel(formatTimeStamp());
		item.setValue(formatSalutation(context));
		item.hideAction();
		item.getBackButton().setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				((NavigationRootActivity) getActivity()).getSlidingMenu()
						.toggle();
			}
		});
		item.getRefreshButton().setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				getFastcheckData(true);
			}
		});

		return item;
	}

	private SimpleListItem createFastcheckRewardListItem(final Context context) {
		final Resources res = context.getResources();
		final DisplayFastcheckRewardListItem item = new DisplayFastcheckRewardListItem(
				context, null);
		item.setLabel(res.getString(R.string.fast_check_rewards_outage_msg));
		item.hideAction();
		item.hideValue();
		return item;
	}

	private void showFastcheckErrorPage(String errorMsg) {
		showFastcheckErrorPage(errorMsg, false);
	}
	
	private void showFastcheckErrorPage(String errorMsg, boolean processLink) {
		Context context = getActivity();
		fastcheckList = (LinearLayout) view.findViewById(R.id.fastcheck_list);
		RelativeLayout layout = (RelativeLayout) LayoutInflater.from(context)
				.inflate(R.layout.fastcheck_display_error_body, null);
		fastcheckErrorMsg = (TextView) layout
				.findViewById(R.id.fastcheck_error_msg);
		if (processLink) {
			fastcheckErrorMsg.setText(Html.fromHtml(errorMsg));
			Linkify.addLinks(fastcheckErrorMsg, Linkify.PHONE_NUMBERS);
			fastcheckErrorMsg.setMovementMethod(LinkMovementMethod.getInstance());
		} else {
			fastcheckErrorMsg.setText(errorMsg);
		}
		Button backButtonOnTechDiff = (Button) layout
				.findViewById(R.id.fastcheck_display_back_button);
		backButtonOnTechDiff.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				((NavigationRootActivity) getActivity()).getSlidingMenu()
						.toggle();
			}
		});
		fastcheckList.removeAllViews();
		fastcheckList.addView(layout);
	}
	
	private String formatMoney(String aString) {
		double money = Double.parseDouble(aString);
		DecimalFormat df = new DecimalFormat(CASH_REWARDS_FORMAT);
		return "$" + df.format(money);
	}
	
	private String formatMile(String aString) {
		double mile = Double.parseDouble(aString);
		DecimalFormat df = new DecimalFormat(MILE_REWARDS_FORMAT);
		return df.format(mile);
	}
	
	private void updateCacheAndTimestamp(FastcheckDetail aFastcheckDetail, String aMsg) {
		Context context = getActivity().getApplicationContext();
		final Resources res = context.getResources();
		final CardShareDataStore cardShareDataStoreObj = CardShareDataStore
                .getInstance(context);
		fastcheckDetail = aFastcheckDetail;
		cardShareDataStoreObj.addToAppCache(res
                .getString(R.string.fast_check_detail_databean), fastcheckDetail);
		lastUpdateTimeCal = Calendar.getInstance();
		Long tmpLastUpdateTimeInMS = Long.valueOf(lastUpdateTimeCal.getTimeInMillis());
		cardShareDataStoreObj.addToAppCache(res
                .getString(R.string.fast_check_last_retrieval_time_in_ms), tmpLastUpdateTimeInMS);
		fastcheckMaintenanceMsg = aMsg;
		cardShareDataStoreObj.addToAppCache(res
                .getString(R.string.fast_check_maintenance_msg), fastcheckMaintenanceMsg);
	}
		

	/**
	 * Populate the linear layout
	 */
	private void populateList() {

		Context context = getActivity().getApplicationContext();
		final Resources res = context.getResources();

		fastcheckList = (LinearLayout) view.findViewById(R.id.fastcheck_list);
		fastcheckList.removeAllViews();
		fastcheckList.addView(createFastcheckTitleListItem(getActivity()));

		fastcheckList.addView(SimpleListItemFactory.createItem(context,
				res.getString(R.string.account_summary_current_balance),
				formatMoney(fastcheckDetail.getCurrentBalance())));

		fastcheckList.addView(SimpleListItemFactory.createItem(context,
				res.getString(R.string.fast_check_credit_line_available),
				formatMoney(fastcheckDetail.getAvailableCredit())));

		if (fastcheckDetail.isAcLiteOutageMode()
				|| fastcheckDetail.isRewardsOutage())
			fastcheckList.addView(createFastcheckRewardListItem(context));
		else if (CASH_BACK_CODE.equals(fastcheckDetail.getIncentiveTypeCode()) || DBC_CASH_BACK_CODE.equals(fastcheckDetail.getIncentiveTypeCode())) {
			SimpleListItem cbbList= SimpleListItemFactory.createItem(context,
					Html.fromHtml(res.getString((R.string.fast_check_cashback_bonus))),
					formatMoney(fastcheckDetail.getEarnRewardAmount()));
			//cbbList.getLabel().setTypeface(null, Typeface.ITALIC);
			fastcheckList.addView(cbbList);
		} else if (MILES_CODE.equals(fastcheckDetail.getIncentiveTypeCode()) || DBC_MILES_CODE.equals(fastcheckDetail.getIncentiveTypeCode()))
			fastcheckList.addView(SimpleListItemFactory.createItem(context,
					res.getString(R.string.account_summary_miles_bonus),
					formatMile(fastcheckDetail.getEarnRewardAmount())));

	}
	
	private void checkOrientationChange(Resources res, CardShareDataStore cardShareDataStoreObj) {
		fastcheckDetail = (FastcheckDetail)cardShareDataStoreObj
			.getValueOfAppCache(res.getString(R.string.fast_check_detail_databean));
		fastcheckMaintenanceMsg = (String)cardShareDataStoreObj
			.getValueOfAppCache(res.getString(R.string.fast_check_maintenance_msg));
		if (fastcheckDetail != null) resultPage = NORMAL_RESULT_PAGE;
		else if (fastcheckMaintenanceMsg != null) resultPage = MAINTENANCE_RESULT_PAGE;
		else resultPage = TECH_DIFF_RESULT_PAGE;				
	}

	@Override
	public void onSuccess(Object data) {
		resultPage = NORMAL_RESULT_PAGE;
		updateCacheAndTimestamp((FastcheckDetail)data, null);
		populateList();
	}

	private void showPreviousPage(Resources res) {
		switch (resultPage) {
		case NORMAL_RESULT_PAGE:
			if (fastcheckDetail != null)
				populateList();
			else
				showFastcheckErrorPage(res.getString(R.string.fast_check_error_tech_diff));
			break;
		case CANNOT_ACCESS_RESULT_PAGE:
			showFastcheckErrorPage(res.getString(R.string.fast_check_error_cannot_access), true);
			break;
		case MAINTENANCE_RESULT_PAGE:
			if (fastcheckMaintenanceMsg != null)
				showFastcheckErrorPage(fastcheckMaintenanceMsg, true);
			else 
				showFastcheckErrorPage(res.getString(R.string.fast_check_error_tech_diff));
			break;
		case TECH_DIFF_RESULT_PAGE:
			showFastcheckErrorPage(res.getString(R.string.fast_check_error_tech_diff));
			break;
		}
	}

	@Override
	public void OnError(Object data) {
		Context context = getActivity().getApplicationContext();
		final Resources res = context.getResources();
		final CardShareDataStore cardShareDataStoreObj = CardShareDataStore
                .getInstance(context);
		CardErrorBean cardErrorBean = (CardErrorBean) data;
		
		if (cardErrorBean != null && Log.isLoggable(TAG, Log.ERROR)) {
			Log.e(TAG, "onError() error code is " + cardErrorBean.getErrorCode());
			Log.e(TAG, "onError() error msg is " + cardErrorBean.getErrorMessage());
		}
		
		if (cardErrorBean == null || cardErrorBean.getErrorCode() == null) {
			resultPage = TECH_DIFF_RESULT_PAGE;
			updateCacheAndTimestamp(null, null);
			showFastcheckErrorPage(res.getString(R.string.fast_check_error_tech_diff));
		} else if (cardErrorBean.getErrorCode().startsWith("100")) {
			showFastcheckErrorPage(res.getString(R.string.E_100));
		} else if (cardErrorBean.getErrorCode().startsWith("403")) {
			resultPage = CANNOT_ACCESS_RESULT_PAGE;
			updateCacheAndTimestamp(null, null);
			showFastcheckErrorPage(res.getString(R.string.fast_check_error_cannot_access), true);
		} else if (cardErrorBean.getErrorCode().startsWith("429")) {
			// orientation change fix begin
			if (resultPage == INITIAL_RESULT_PAGE) checkOrientationChange(res, cardShareDataStoreObj);
			showPreviousPage(res);
		} else if ( cardErrorBean.getErrorCode().startsWith("401")
					|| (cardErrorBean.getErrorMessage()!=null && cardErrorBean.getErrorMessage().indexOf("Received authentication challenge is null")>=0) ){
			resultPage = NO_FASTCHECK_TOKEN_RESULT_PAGE;
			FastcheckUtil.storeFastcheckToken(getActivity(), null); // nullify invalid token
			updateCacheAndTimestamp(null, null);
			showFastcheckErrorPage(res.getString(R.string.fast_check_error_no_token));
		} else if (cardErrorBean.getErrorCode().startsWith("503")) {
			resultPage = MAINTENANCE_RESULT_PAGE;
			updateCacheAndTimestamp(null, cardErrorBean.getErrorMessage());
			showFastcheckErrorPage(cardErrorBean.getErrorMessage(), true);
		} else {
			resultPage = TECH_DIFF_RESULT_PAGE;
			updateCacheAndTimestamp(null, null);
			showFastcheckErrorPage(res.getString(R.string.fast_check_error_tech_diff));
		}
	}
	
	public void onLeftSwipe() {}
	
	public void onRightSwipe() {
		 ((NavigationRootActivity) getActivity()).getSlidingMenu().toggle();
	}
	
	
	
	private class SwipeGestureDetector extends SimpleOnGestureListener {
	    private static final int SWIPE_MIN_DISTANCE = 50;
	    private static final int SWIPE_MAX_OFF_PATH = 200;
	    private static final int SWIPE_THRESHOLD_VELOCITY = 200;

	    @Override
	    public boolean onFling(final MotionEvent e1, final MotionEvent e2, final float velocityX,
	            final float velocityY) {
	        try {
	            final float diffAbs = Math.abs(e1.getY() - e2.getY());
	            final float diff = e1.getX() - e2.getX();

	            if (diffAbs > SWIPE_MAX_OFF_PATH)
	                return false;

	            // Left swipe
	            if (diff > SWIPE_MIN_DISTANCE
	                    && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
	                onLeftSwipe();
	            } 
	            // Right swipe
	            else if (-diff > SWIPE_MIN_DISTANCE
	                    && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
	                onRightSwipe();
	            }
	        } catch (final Exception e) {
	            if (Log.isLoggable(TAG, Log.ERROR)) Log.e(TAG, "onFling() Error on gestures");
	        }
	        return false;
	    }
	}
	
	@Override
	public void onClick(final View v) {
		v.setSoundEffectsEnabled(false);
	}

}
