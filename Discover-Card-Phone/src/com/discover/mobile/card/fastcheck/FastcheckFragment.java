package com.discover.mobile.card.fastcheck;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

import android.content.Context;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.discover.mobile.card.R;
import com.discover.mobile.card.account.summary.SimpleListItem;
import com.discover.mobile.card.account.summary.SimpleListItemFactory;
import com.discover.mobile.card.common.CardEventListener;
import com.discover.mobile.card.common.net.error.CardErrorBean;
import com.discover.mobile.card.common.net.service.WSAsyncCallTask;
import com.discover.mobile.card.common.net.service.WSRequest;
import com.discover.mobile.card.common.net.utility.NetworkUtility;
import com.discover.mobile.card.common.utils.Utils;
import com.discover.mobile.common.BaseFragment;
import com.discover.mobile.common.facade.LoginActivityInterface;
import com.discover.mobile.common.nav.NavigationRootActivity;
import com.discover.mobile.common.net.json.JacksonObjectMapperHolder;
import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.slidingmenu.lib.SlidingMenu;
import com.slidingmenu.lib.SlidingMenu.OnCloseListener;
import com.slidingmenu.lib.SlidingMenu.OnOpenListener;

public class FastcheckFragment extends BaseFragment implements
		CardEventListener, OnOpenListener, OnCloseListener {

	private final String TAG = FastcheckFragment.class.getSimpleName();
	private static final long explicitRefreshInterval = 10L * 1000L; // 10 seconds
	private static final String CASH_BACK_CODE = "CBB";
	private static final String MILES_CODE = "MI2";
	private static final int NORMAL_RESULT_PAGE = 0;
	private static final int CANNOT_ACCESS_RESULT_PAGE = 1;
	private static final int TECH_DIFF_RESULT_PAGE = 2;
	private static final int NO_FASTCHECK_TOKEN_RESULT_PAGE = 3;
	private static final int INITIAL_RESULT_PAGE = -1;

	// data bean
	private FastcheckDetail fastcheckDetail;
	private Calendar lastUpdateTimeCal;
	private int resultPage = INITIAL_RESULT_PAGE;

	// ui elements
	private LinearLayout fastcheckList;
	private TextView fastcheckErrorMsg;
	private RelativeLayout view;

	@Override
	public void onActivityCreated(final Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		//TODO, comment out
		try {
			String tmp = FastcheckUtil.encrypt("cSlOmxS6f63tYWYWEomAI6YCcqXOlF+mw/0OwwX8yCSxfAmcOCaaeudPzdppqaFHTWsJCPlpuzg2oFRBiJ8aFg==");
			FastcheckUtil.storeFastcheckToken(getActivity(), tmp);
		} catch (Exception e) {}
		
	}

	@Override
	public View onCreateView(final LayoutInflater inflater,
			final ViewGroup container, final Bundle savedInstanceState) {
		view = (RelativeLayout) inflater.inflate(R.layout.fastcheck_fragment,
				null);
		
		((NavigationRootActivity) getActivity()).getSlidingMenu()
				.setOnOpenListener(this);
		((NavigationRootActivity) getActivity()).getSlidingMenu()
				.setOnCloseListener(this);
		
		return view;

	}

	@Override
	public void onStart() {
		super.onStart();
		boolean isFastcheckHidden = (((NavigationRootActivity) getActivity())
				.getSlidingMenu().getTouchModeAbove() == SlidingMenu.TOUCHMODE_NONE);
		Log.d(TAG, "onStart() invoked, isFastcheckHidden " + isFastcheckHidden);
	}

	@Override
	public void onOpen() {
		boolean isFastcheckHidden = (((NavigationRootActivity) getActivity())
				.getSlidingMenu().getTouchModeAbove() == SlidingMenu.TOUCHMODE_NONE);
		if (!isFastcheckHidden)
			getFastcheckData(false);
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

	
	private boolean timeToMakeAnotherCall() {
		if (lastUpdateTimeCal == null)
			return true;
		long currentMs = Calendar.getInstance().getTime().getTime();
		long lastUpdateMs = lastUpdateTimeCal.getTime().getTime();
		if ((currentMs - lastUpdateMs) <= explicitRefreshInterval)
			return false;
		else
			return true;
	}

	private void getFastcheckData(boolean spinOnNoFetch) {

		if (!timeToMakeAnotherCall()) {
			if (spinOnNoFetch) {
				Utils.showSpinner(getActivity(), "Discover",
						"Retrieving Quick view...");
				Handler handler = new Handler();
				handler.postDelayed(new Runnable() {
					public void run() {
						Utils.hideSpinner();
					}
				}, 500);
			}
			showPreviousPage();
			return;
		}
		try {
			String encryptedDeviceToken = FastcheckUtil.readFastcheckToken(getActivity());
			String deviceToken = null;
			try {
				deviceToken = FastcheckUtil.decrypt(encryptedDeviceToken);
			} catch (Exception e) {
				Log.e(TAG, "getFastcheckData() gets IOException during FastcheckUtil.decrypt()" + e.getMessage());
				showFastcheckTechDiff();
			}
			
			if (deviceToken == null || deviceToken.length() != 88) {
				Log.e(TAG, "getFastcheckData(), token is NULL or length NOT 88" );
				showFastcheckTechDiff();
			} else 
				Log.d(TAG, "getFastcheckData(), token is " + deviceToken + ", length is " + deviceToken.length());
			
			

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
			JacksonObjectMapperHolder.getMapper().writeValue(baos,
					new FastcheckToken(deviceToken));
			request.setInput(baos.toByteArray());

			// Making the call
			WSAsyncCallTask serviceCall = new WSAsyncCallTask(getActivity(),
					new FastcheckDetail(), "Discover",
					"Retrieving Quick view...", this);
			serviceCall.execute(request);
			
		} catch (JsonGenerationException e) {
			Log.e(TAG, "getFastcheckData() gets JsonGenerationException " + e.getMessage());
			lastUpdateTimeCal = Calendar.getInstance();
			resultPage = TECH_DIFF_RESULT_PAGE;
			fastcheckDetail = null;
			showFastcheckTechDiff();
		} catch (JsonMappingException e) {
			Log.e(TAG, "getFastcheckData() gets JsonMappingException " + e.getMessage());
			lastUpdateTimeCal = Calendar.getInstance();
			resultPage = TECH_DIFF_RESULT_PAGE;
			fastcheckDetail = null;
			showFastcheckTechDiff();
		} catch (IOException e) {
			Log.e(TAG, "getFastcheckData() gets IOException " + e.getMessage());
			lastUpdateTimeCal = Calendar.getInstance();
			resultPage = TECH_DIFF_RESULT_PAGE;
			fastcheckDetail = null;
			showFastcheckTechDiff();
		}
	}

	
	private String formatTimeStamp() {
		SimpleDateFormat sdf = new SimpleDateFormat("hh:mm aa");
		String lastUpdateTimeStr = sdf.format(lastUpdateTimeCal.getTime());
		sdf = new SimpleDateFormat("yyyyMMdd");
		int lastUpdateDateInt = Integer.parseInt(sdf.format(lastUpdateTimeCal
				.getTime()));
		int todayInt = Integer.parseInt(sdf.format(Calendar.getInstance()
				.getTime()));
		if (lastUpdateDateInt == todayInt) {
			return new StringBuffer("\nUpdated today at ").append(
					lastUpdateTimeStr).toString();
		} else {
			sdf = new SimpleDateFormat("MM/dd/yy");
			String lastUpdateDateStr = sdf.format(lastUpdateTimeCal.getTime());
			return new StringBuffer("Updated ").append(lastUpdateDateStr)
					.append(" at ").append(lastUpdateTimeStr).toString();
		}
	}

	private String formatSalutation() {
		StringBuffer sb = new StringBuffer("Good ");
		if (Calendar.getInstance().get(Calendar.HOUR_OF_DAY) < 12)
			sb.append("morning, ").append(
					fastcheckDetail.getCardmemberFirstName());
		else if (Calendar.getInstance().get(Calendar.HOUR_OF_DAY) < 18)
			sb.append("afternoon, ").append(
					fastcheckDetail.getCardmemberFirstName());
		else
			sb.append("evening, ").append(
					fastcheckDetail.getCardmemberFirstName());
		return sb.toString();
	}

	private SimpleListItem createFastcheckTitleListItem(final Context context) {
		final DisplayFastcheckTitleListItem item = new DisplayFastcheckTitleListItem(
				context, null);
		item.setLabel(formatTimeStamp());
		item.setValue(formatSalutation());
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
		final DisplayFastcheckRewardListItem item = new DisplayFastcheckRewardListItem(
				context, null);
		item.setLabel("Cashback Bonus cannot be updated at this time.");
		item.hideAction();
		item.hideValue();
		return item;
	}

	private void showFastcheckError() {

		Context context = getActivity().getApplicationContext();
		fastcheckList = (LinearLayout) view.findViewById(R.id.fastcheck_list);
		RelativeLayout layout = (RelativeLayout) LayoutInflater.from(context)
				.inflate(R.layout.fastcheck_display_error_body, null);
		fastcheckErrorMsg = (TextView) layout
				.findViewById(R.id.fastcheck_error_msg);
		fastcheckErrorMsg
				.setText("Due to special condition related to your account, this page is not available.\n\nPlease contact customer service.");
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

	private void showFastcheckTechDiff() {

		Context context = getActivity().getApplicationContext();
		fastcheckList = (LinearLayout) view.findViewById(R.id.fastcheck_list);
		RelativeLayout layout = (RelativeLayout) LayoutInflater.from(context)
				.inflate(R.layout.fastcheck_display_error_body, null);
		fastcheckErrorMsg = (TextView) layout
				.findViewById(R.id.fastcheck_error_msg);
		fastcheckErrorMsg
				.setText("We're currently experiencing techincally difficult. We apologize for any incovenience.");
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
	
	private void showFastcheckNoTokenErrorPage() {

		Context context = getActivity().getApplicationContext();
		fastcheckList = (LinearLayout) view.findViewById(R.id.fastcheck_list);
		RelativeLayout layout = (RelativeLayout) LayoutInflater.from(context)
				.inflate(R.layout.fastcheck_display_error_body, null);
		fastcheckErrorMsg = (TextView) layout
				.findViewById(R.id.fastcheck_error_msg);
		fastcheckErrorMsg
				.setText("You recently requested to disable your quick view settings. If you'd like to enable this option, plese login and navigate to " +
						"\"Quick View\" under Profile and Settings.");
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
	
	private void showFastcheckErrorPage(String errorMsg) {
		Context context = getActivity().getApplicationContext();
		fastcheckList = (LinearLayout) view.findViewById(R.id.fastcheck_list);
		RelativeLayout layout = (RelativeLayout) LayoutInflater.from(context)
				.inflate(R.layout.fastcheck_display_error_body, null);
		fastcheckErrorMsg = (TextView) layout
				.findViewById(R.id.fastcheck_error_msg);
		fastcheckErrorMsg
				.setText(errorMsg);
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
		DecimalFormat df = new DecimalFormat("###,###,##0.00");
		return "$" + df.format(money);
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
				res.getString(R.string.account_summary_credit_available),
				formatMoney(fastcheckDetail.getAvailableCredit())));

		if (fastcheckDetail.isAcLiteOutageMode()
				|| fastcheckDetail.isRewardsOutage())
			fastcheckList.addView(createFastcheckRewardListItem(context));
		else if (CASH_BACK_CODE.equals(fastcheckDetail.getIncentiveTypeCode()))
			fastcheckList.addView(SimpleListItemFactory.createItem(context,
					res.getString(R.string.account_summary_cash_back_bonus),
					formatMoney(fastcheckDetail.getEarnRewardAmount())));
		else if (MILES_CODE.equals(fastcheckDetail.getIncentiveTypeCode()))
			fastcheckList.addView(SimpleListItemFactory.createItem(context,
					res.getString(R.string.account_summary_miles_bonus),
					fastcheckDetail.getEarnRewardAmount()));

	}

	@Override
	public void onSuccess(Object data) {
		lastUpdateTimeCal = Calendar.getInstance();
		resultPage = NORMAL_RESULT_PAGE;
		fastcheckDetail = ((FastcheckDetail) data);
		populateList();
	}

	private void showPreviousPage() {
		switch (resultPage) {
		case NORMAL_RESULT_PAGE:
			if (fastcheckDetail != null)
				populateList();
			else
				showFastcheckTechDiff();
			break;
		case CANNOT_ACCESS_RESULT_PAGE:
			showFastcheckError();
			break;
		case TECH_DIFF_RESULT_PAGE:
			showFastcheckTechDiff();
			break;
		}
	}

	@Override
	public void OnError(Object data) {
		CardErrorBean cardErrorBean = (CardErrorBean) data;
		Log.d(TAG, "onError() error code is " + cardErrorBean.getErrorCode());
		Log.d(TAG, "onError() error code is " + cardErrorBean.getErrorMessage());
		
		if ("403".equals(cardErrorBean.getErrorCode())) {
			lastUpdateTimeCal = Calendar.getInstance();
			resultPage = CANNOT_ACCESS_RESULT_PAGE;
			fastcheckDetail = null;
			showFastcheckError();
		} else if ("429".equals(cardErrorBean.getErrorCode())) {
			lastUpdateTimeCal = Calendar.getInstance();
			if (resultPage == INITIAL_RESULT_PAGE)
				resultPage = TECH_DIFF_RESULT_PAGE;
			showPreviousPage();
		} else if (cardErrorBean.getErrorMessage().indexOf(
				"Received authentication challenge is null") >= 0) {
			Log.e(TAG, "OnError() gets 401 type of error msg " + cardErrorBean.getErrorMessage());
			lastUpdateTimeCal = Calendar.getInstance();
			resultPage = NO_FASTCHECK_TOKEN_RESULT_PAGE;
			fastcheckDetail = null;
			FastcheckUtil.storeFastcheckToken(getActivity(), null); // nullify invalid token
			showFastcheckNoTokenErrorPage();
		} else {
			lastUpdateTimeCal = Calendar.getInstance();
			resultPage = TECH_DIFF_RESULT_PAGE;
			fastcheckDetail = null;
			showFastcheckTechDiff();
		}
		

	}

}
