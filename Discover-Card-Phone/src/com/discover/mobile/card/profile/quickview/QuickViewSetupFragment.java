package com.discover.mobile.card.profile.quickview;

import java.io.Serializable;
import java.util.HashMap;

import org.json.JSONObject;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.discover.mobile.card.CardMenuItemLocationIndex;
import com.discover.mobile.card.R;
import com.discover.mobile.card.common.CardEventListener;
import com.discover.mobile.card.common.SessionCookieManager;
import com.discover.mobile.card.common.net.error.CardErrorBean;
import com.discover.mobile.card.common.net.error.CardErrorCallbackListener;
import com.discover.mobile.card.common.net.error.CardErrorResponseHandler;
import com.discover.mobile.card.common.net.service.WSAsyncCallTask;
import com.discover.mobile.card.common.net.service.WSConstant;
import com.discover.mobile.card.common.net.service.WSRequest;
import com.discover.mobile.card.common.net.utility.NetworkUtility;
import com.discover.mobile.card.common.sharedata.CardShareDataStore;
import com.discover.mobile.card.common.utils.Utils;
import com.discover.mobile.card.error.CardErrorHandlerUi;
import com.discover.mobile.card.navigation.CardMenuInterface;
import com.discover.mobile.card.navigation.CardNavigationRootActivity;
import com.discover.mobile.common.BaseFragment;
import com.discover.mobile.common.DiscoverActivityManager;
import com.discover.mobile.common.net.HttpHeaders;
import com.fasterxml.jackson.annotation.JsonProperty;

/***
 * Quick view Setting Fragment. On click of menu Profile->FastView This Fragment
 * is loaded. Extended from {@link BaseFragment}
 * 
 * @author 352686
 * 
 */
public class QuickViewSetupFragment extends BaseFragment {

	private ImageView toggleImage;
	private String new_token = "";
	private View mainView;
	private static final String REFERER = "cardHome-pg";

	protected String LOG_TAG = "QuickViewSetupFragment";
	private boolean quickviewOn;

	/**
	 * Create the view
	 * 
	 * @param inflater
	 *            - inflater that will inflate the layout
	 * @param container
	 *            - parent layout
	 * @param savedInstanceState
	 *            - bundle holding the state of the fragment
	 */
	@Override
	public View onCreateView(final LayoutInflater inflater,
			final ViewGroup container, final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mainView = inflater.inflate(R.layout.quick_view_settings, null);

		toggleImage = (ImageView) mainView.findViewById(R.id.quick_toggle);
		toggleImage.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				try {
					updateQuickViewStatus();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
		CardShareDataStore mCardStoreData = CardShareDataStore.getInstance(this
				.getActivity().getApplicationContext());

		/**
		 * back pressed Logic: Issue: if error occurred in checkBindingStatus
		 * then error dialog comes and on OK pressed home summary fragment
		 * appeared. if we pressed back on home summary again onCreateView
		 * method calls and webservice get called.
		 * 
		 * solution: create flag in cache and set it in activities onBackPressed
		 * method check is added before calling this.
		 * 
		 */
		boolean backpressed = (Boolean) mCardStoreData
				.getValueOfAppCache("onBackPressed");
		if (!backpressed) {
			try {
				String save_token = FastcheckUtil
						.readFastcheckToken(getActivity());
				if (null != save_token) {
					checkBindingStatus(save_token);
				} else {
					showQVwithOffState();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else
			return null;

		Button yesButton = (Button) mainView.findViewById(R.id.yes);
		yesButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				showQVwithOffState();

			}
		});

		TextView nothanks = (TextView) mainView.findViewById(R.id.thnks);
		nothanks.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				removeQVFragment();
			}
		});
		setFooter(mainView);
		return mainView;

	}

	/**
	 * Remove QuickView Fragments from Application's Fragment History Stack
	 */
	protected void removeQVFragment() {
		Activity activity = DiscoverActivityManager.getActiveActivity();
		if (activity instanceof CardNavigationRootActivity) {
			activity.onBackPressed();
		}

	}

	/**
	 * Loads quick view setup page with toggle button in off state
	 */
	private void showQVwithOffState() {
		mainView.findViewById(R.id.qvsetup_main_relative_view).setVisibility(
				View.VISIBLE);
		mainView.findViewById(R.id.qvalready_main_relative_view).setVisibility(
				View.GONE);
		toggleImage.setBackgroundResource(R.drawable.swipe_off);
		quickviewOn = false;
	}

	/**
	 * call Binding webservice and check the token is register to server
	 * 
	 * @param save_token
	 * @throws Exception
	 */
	private void checkBindingStatus(String save_token) throws Exception {
		WSRequest request = new WSRequest();
		String URL = NetworkUtility.getWebServiceUrl(getActivity(),
				R.string.get_quick_view_status);

		final HashMap<String, String> headers = request.getHeaderValues();

		headers.put(WSConstant.DCSESSION_COOKIE, SessionCookieManager
				.getInstance(getActivity()).getDcsession());
		headers.put(WSConstant.SECTOKEN_COOKIE, SessionCookieManager
				.getInstance(getActivity()).getSecToken());
		headers.put(WSConstant.PMDATA_COOKIE,
				SessionCookieManager.getInstance(getActivity()).getPmData());
		headers.put(WSConstant.STRONGTAUTHSVCS_COOKIE, SessionCookieManager
				.getInstance(getActivity()).getSTRONGAUTHSVCS());

		JSONObject obj = new JSONObject();
		obj.put(WSConstant.DEVICETOKEN_COOKIE, save_token);

		request.setInput(obj.toString().getBytes());
		request.setHeaderValues(headers);
		request.setUrl(URL);
		request.setMethodtype("POST");
		final WSAsyncCallTask serviceCall = new WSAsyncCallTask(getActivity(),
				new StatusResponse(), "Discover", "Loading...",
				new CardEventListener() {
					@Override
					public void onSuccess(Object data) {
						StatusResponse response = ((StatusResponse) data);
						if (response.deviceBound.equalsIgnoreCase("true")) {
							if ((mainView.findViewById(
									R.id.qvalready_main_relative_view)
									.getVisibility() == View.VISIBLE))
								mainView.findViewById(
										R.id.qvalready_main_relative_view)
										.setVisibility(View.GONE);
							if ((mainView.findViewById(
									R.id.qvsetup_main_relative_view)
									.getVisibility() == View.GONE))
								mainView.findViewById(
										R.id.qvsetup_main_relative_view)
										.setVisibility(View.VISIBLE);
							quickviewOn = true;
							toggleImage
									.setBackgroundResource(R.drawable.swipe_on);
						} else {
							if ((mainView.findViewById(
									R.id.qvsetup_main_relative_view)
									.getVisibility() == View.VISIBLE))
								mainView.findViewById(
										R.id.qvsetup_main_relative_view)
										.setVisibility(View.GONE);
							if ((mainView.findViewById(
									R.id.qvalready_main_relative_view)
									.getVisibility() == View.GONE))
								mainView.findViewById(
										R.id.qvalready_main_relative_view)
										.setVisibility(View.VISIBLE);
						}
					}

					@Override
					public void OnError(Object data) {
						CardErrorResponseHandler cardErrorResHandler = new CardErrorResponseHandler(
								(CardErrorHandlerUi) getActivity());
						cardErrorResHandler.handleCardError(
								(CardErrorBean) data,
								new CardErrorCallbackListener() {

									@Override
									public void onButton2Pressed() {
										removeQVFragment();

									}

									@Override
									public void onButton1Pressed() {
										removeQVFragment();

									}
								});
					}
				});
		serviceCall.execute(request);

	}

	/***
	 * Initialize and set action to Footer Items privacy menu & Term);s
	 * conditcardErrorResHandler.handleCardError( (CardErrorBean) data, new
	 * CardErrorCallbackListener() {
	 * 
	 * @Override public void onButton2Pressed() { removeQVFragment(); }
	 * @Override public void onButton1Pressed() {
	 *           DiscoverModalManager.clearActiveModal(); removeQVFragment(); }
	 *           }ion
	 * 
	 * @param mainView
	 */
	private void setFooter(View mainView) {
		TextView provideFeedback = (TextView) mainView
				.findViewById(R.id.provide_feedback_button);
		provideFeedback.setTextColor(getActivity().getResources().getColor(
				R.color.footer_link));
		provideFeedback.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Utils.createProvideFeedbackDialog(getActivity(), REFERER);

			}
		});
		TextView termsOfUse = (TextView) mainView
				.findViewById(R.id.privacy_terms);
		termsOfUse.setTextColor(getActivity().getResources().getColor(
				R.color.footer_link));
		termsOfUse.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {

				((CardMenuInterface) getActivity())
						.sendNavigationTextToPhoneGapInterface(getString(R.string.privacy_terms_title));

			}
		});
	}

	/**
	 * Webservice call to on user click on toggle button
	 * 
	 * @throws Exception
	 */
	protected void updateQuickViewStatus() throws Exception {
		WSRequest request = new WSRequest();

		String URL = NetworkUtility.getWebServiceUrl(getActivity(),
				R.string.quick_view);

		final HashMap<String, String> headers = new HashMap<String, String>();
		headers.put(WSConstant.DCSESSION_COOKIE, SessionCookieManager
				.getInstance(getActivity()).getDcsession());
		headers.put(WSConstant.PMDATA_COOKIE,
				SessionCookieManager.getInstance(getActivity()).getPmData());
		headers.put(WSConstant.STRONGTAUTHSVCS_COOKIE, SessionCookieManager
				.getInstance(getActivity()).getSTRONGAUTHSVCS());
		JSONObject obj = new JSONObject();
		if (quickviewOn) {
			URL = URL + "?"
					+ getActivity().getString(R.string.quick_view_unbind);
			headers.put(HttpHeaders.XHttpMethodOveride,
					WSConstant.METHOD_DELETE);
			obj.put(WSConstant.DEVICETOKEN_COOKIE,
					FastcheckUtil.readFastcheckToken(getActivity()));
		} else {
			new_token = FastcheckUtil.genClientBindingToken();
			obj.put(WSConstant.DEVICETOKEN_COOKIE, new_token);
		}
		request.setInput(obj.toString().getBytes());
		request.setHeaderValues(headers);
		request.setUrl(URL);
		request.setMethodtype("POST");

		final WSAsyncCallTask serviceCall = new WSAsyncCallTask(getActivity(),
				null, "Discover", "Loading...", new CardEventListener() {
					@Override
					public void onSuccess(Object data) {
						Utils.log(LOG_TAG, "On Sucess()");
						if (quickviewOn) {
							toggleImage
									.setBackgroundResource(R.drawable.swipe_off);
							FastcheckUtil.storeFastcheckToken(getActivity(),
									null);
							quickviewOn = false;

						} else {
							toggleImage
									.setBackgroundResource(R.drawable.swipe_on);
							FastcheckUtil.storeFastcheckToken(getActivity(),
									new_token);
							quickviewOn = true;
						}
					}

					@Override
					public void OnError(Object data) {
						CardErrorResponseHandler cardErrorResHandler = new CardErrorResponseHandler(
								(CardErrorHandlerUi) getActivity());
						cardErrorResHandler
								.handleCardError((CardErrorBean) data);

					}
				});
		serviceCall.execute(request);

	}

	/**
	 * set Fastview in title bar
	 */
	@Override
	public int getActionBarTitle() {
		return R.string.sub_section_title_fast_view;
	}

	/**
	 * return selected group menu
	 */
	@Override
	public int getGroupMenuLocation() {
		return CardMenuItemLocationIndex.PROFILE_AND_SETTINGS_GROUP;
	}

	/**
	 * return selected index
	 */
	@Override
	public int getSectionMenuLocation() {
		return CardMenuItemLocationIndex.FAST_ACCESS_SECTION;
	}

	/**
	 * StatusResponse POJO Class created to parse web service response
	 * 
	 * @author 352686
	 * 
	 */
	static class StatusResponse implements Serializable {
		private static final long serialVersionUID = 419682319585845456L;
		@JsonProperty
		public String deviceBound;
	}

}
