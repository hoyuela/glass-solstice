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
import com.discover.mobile.card.common.utils.FastcheckUtil;
import com.discover.mobile.card.common.utils.FragmentActionBarMenuTitleUtil;
import com.discover.mobile.card.common.utils.Utils;
import com.discover.mobile.card.error.CardErrorHandlerUi;
import com.discover.mobile.card.navigation.CardMenuInterface;
import com.discover.mobile.card.navigation.CardNavigationRootActivity;
import com.discover.mobile.card.services.auth.AccountDetails;
import com.discover.mobile.common.BaseFragment;
import com.discover.mobile.common.DiscoverActivityManager;
import com.discover.mobile.common.analytics.AnalyticsPage;
import com.discover.mobile.common.analytics.TrackingHelper;
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
	private TextView qvinfoTextView, qvfaqTextView;
	private String new_token = "";
	private View mainView;
	private static final String REFERER = "cardHome-pg";
	private static final String MILES = "MI2";
	private static final String NOR = "NOR";
	private static final String SML = "SML";
	protected final String LOG_TAG = "QuickViewSetupFragment";
	private boolean quickviewOn;
	private String TAG = this.getClass().getSimpleName();

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

		// Change CBB/MILES Texts
		final ImageView faqImage = (ImageView) mainView
				.findViewById(R.id.faqimage);
		qvinfoTextView = (TextView) mainView.findViewById(R.id.quick_view_info);
		qvfaqTextView = (TextView) mainView.findViewById(R.id.quick_view_faq);
		if (getCardType().equalsIgnoreCase(MILES)
				|| getCardType().equalsIgnoreCase(SML)) {
			faqImage.setBackgroundResource(R.drawable.quickviewsetupmile);
		} else if (getCardType().equalsIgnoreCase(NOR)) {
			faqImage.setBackgroundResource(R.drawable.quickviewsetup_dbc_corp);
		}
		setQVInfoText();

		toggleImage.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				try {
					updateQuickViewStatus();
				} catch (Exception e) {
					CardErrorBean bean = new CardErrorBean(QuickViewSetupFragment.this.
							getString(R.string.fast_check_error_tech_diff), true);
					CardErrorResponseHandler cardErrorResHandler = new CardErrorResponseHandler(
							(CardErrorHandlerUi) getActivity());
					cardErrorResHandler.handleCardError((CardErrorBean) bean,
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
			}
		});

		qvfaqTextView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				((CardMenuInterface) getActivity())
						.sendNavigationTextToPhoneGapInterface("Qv Faq");
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

				String decryptedToken = null;
				if (save_token != null)
					try {
						decryptedToken = FastcheckUtil.decrypt(save_token);
					} catch (Exception e) {
						FastcheckUtil.storeFastcheckToken(getActivity(), null); // nullify
																				// invalid
																				// token
						CardErrorBean bean = new CardErrorBean(
								QuickViewSetupFragment.this
										.getString(R.string.fast_check_error_tech_diff),
								true);
						CardErrorResponseHandler cardErrorResHandler = new CardErrorResponseHandler(
								(CardErrorHandlerUi) getActivity());
						cardErrorResHandler.handleCardError(
								(CardErrorBean) bean,
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

				if (null != decryptedToken) {
					checkBindingStatus(decryptedToken);
				} else {
					// analytics code
					TrackingHelper
							.trackPageView(AnalyticsPage.QUICKVIEW_SETUP_OFF);
					showQVwithOffState();
				}
			} catch (Exception e) {
				CardErrorBean bean = new CardErrorBean(QuickViewSetupFragment.this.
						getString(R.string.fast_check_error_tech_diff), true);
				CardErrorResponseHandler cardErrorResHandler = new CardErrorResponseHandler(
						(CardErrorHandlerUi) getActivity());
				cardErrorResHandler.handleCardError((CardErrorBean) bean,
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
		} else
			return null;

		final Button yesButton = (Button) mainView.findViewById(R.id.yes);
		yesButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				try {
					showQVwithOnState();
				} catch (Exception e) {
					CardErrorBean bean = new CardErrorBean(QuickViewSetupFragment.this.
							getString(R.string.fast_check_error_tech_diff), true);
					CardErrorResponseHandler cardErrorResHandler = new CardErrorResponseHandler(
							(CardErrorHandlerUi) getActivity());
					cardErrorResHandler.handleCardError((CardErrorBean) bean,
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

			}
		});

		final TextView nothanks = (TextView) mainView.findViewById(R.id.thnks);
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
	 * Sets QuickView InfoText According to Card Type
	 */
	private void setQVInfoText() {
		if (getCardType().equalsIgnoreCase(MILES)
				|| getCardType().equalsIgnoreCase(SML)) {
			qvinfoTextView.setText(R.string.quick_view_info_miles);
		} else if (getCardType().equalsIgnoreCase(NOR)) {
			qvinfoTextView.setText(R.string.quick_view_info_dbc_corp);
		} else {
			qvinfoTextView.setText(R.string.quick_view_info);
		}
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
		setQVInfoText();
		quickviewOn = false;
	}

	/**
	 * Loads quick view setup page with toggle button in off state
	 * 
	 * @throws Exception
	 */
	private void showQVwithOnState() throws Exception {
		mainView.findViewById(R.id.qvsetup_main_relative_view).setVisibility(
				View.VISIBLE);
		mainView.findViewById(R.id.qvalready_main_relative_view).setVisibility(
				View.GONE);
		toggleImage.setBackgroundResource(R.drawable.swipe_on);
		setQVInfoText();
		quickviewOn = false;
		qvinfoTextView.setText(R.string.quick_view_info_on);
		updateQuickViewStatus();
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
							qvinfoTextView.setText(R.string.quick_view_info_on);
							// analytics code
							TrackingHelper
									.trackPageView(AnalyticsPage.QUICKVIEW_SETUP_ON);
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
	 * Initialize and set action to Footer Items privacy menu & Term condition
	 * 
	 * @param mainView
	 */
	private void setFooter(View mainView) {
		final TextView provideFeedback = (TextView) mainView
				.findViewById(R.id.provide_feedback_button);
		provideFeedback.setTextColor(getActivity().getResources().getColor(
				R.color.footer_link));
		provideFeedback.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Utils.createProvideFeedbackDialog(getActivity(), REFERER);

			}
		});
		final TextView termsOfUse = (TextView) mainView
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
			StringBuffer urlStringBuffer = new StringBuffer();
			urlStringBuffer.append(URL);
			urlStringBuffer.append("?");
			urlStringBuffer.append(getActivity().getString(
					R.string.quick_view_unbind));

			URL = urlStringBuffer.toString();
			headers.put(HttpHeaders.XHttpMethodOveride,
					WSConstant.METHOD_DELETE);
			try {
				obj.put(WSConstant.DEVICETOKEN_COOKIE, FastcheckUtil
						.decrypt(FastcheckUtil
								.readFastcheckToken(getActivity())));
			} catch (Exception e) {
				CardErrorBean bean = new CardErrorBean(
						QuickViewSetupFragment.this
								.getString(R.string.fast_check_error_tech_diff),
						true);
				CardErrorResponseHandler cardErrorResHandler = new CardErrorResponseHandler(
						(CardErrorHandlerUi) getActivity());
				cardErrorResHandler.handleCardError((CardErrorBean) bean,
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
							setQVInfoText();
							quickviewOn = false;

						} else {

							try {
								FastcheckUtil.storeFastcheckToken(
										getActivity(),
										FastcheckUtil.encrypt(new_token));
							} catch (Exception e) {
								CardErrorBean bean = new CardErrorBean(e
										.getMessage(), true);
								CardErrorResponseHandler cardErrorResHandler = new CardErrorResponseHandler(
										(CardErrorHandlerUi) getActivity());
								cardErrorResHandler
										.handleCardError((CardErrorBean) bean);
							}
							toggleImage
									.setBackgroundResource(R.drawable.swipe_on);
							qvinfoTextView.setText(R.string.quick_view_info_on);
							quickviewOn = true;
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

	/**
	 * Return the integer value of the string that needs to be displayed in the
	 * title
	 */
	@Override
	public int getActionBarTitle() {
		FragmentActionBarMenuTitleUtil barMenuTitleUtil = new FragmentActionBarMenuTitleUtil(
				((CardNavigationRootActivity) getActivity()));
		return barMenuTitleUtil.getActionBarTitle();
	}

	/**
	 * Return GrupMenuLocation
	 */
	@Override
	public int getGroupMenuLocation() {
		Utils.log(TAG, "inside getGroupMenuLocation ");
		FragmentActionBarMenuTitleUtil barMenuTitleUtil = new FragmentActionBarMenuTitleUtil(
				((CardNavigationRootActivity) getActivity()));
		return barMenuTitleUtil
				.getGroupMenuLocation(R.string.sub_section_title_fast_view);
	}

	/**
	 * Return selected Menu Location
	 */
	@Override
	public int getSectionMenuLocation() {
		Utils.log(TAG, "inside getSectionMenuLocation");
		FragmentActionBarMenuTitleUtil barMenuTitleUtil = new FragmentActionBarMenuTitleUtil(
				((CardNavigationRootActivity) getActivity()));
		return barMenuTitleUtil
				.getSectionMenuLocation(R.string.sub_section_title_fast_view);
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

	/**
	 * This method returns us the current card Type.
	 * 
	 * @return String Card Type.
	 */
	private String getCardType() {
		String accType = null;
		CardShareDataStore dataStore = CardShareDataStore
				.getInstance(getActivity());
		AccountDetails accData = (AccountDetails) dataStore
				.getValueOfAppCache(getResources().getString(
						R.string.account_details));
		if (null != accData) {
			accType = accData.incentiveTypeCode;
		}
		return accType;
	}
}
