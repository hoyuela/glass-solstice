package com.discover.mobile.bank.framework;

import java.io.Serializable;

import android.app.Activity;

import com.discover.mobile.bank.BankPhoneAsyncCallbackBuilder;
import com.discover.mobile.bank.login.LoginActivity;
import com.discover.mobile.bank.navigation.BankNavigationRootActivity;
import com.discover.mobile.bank.services.AcceptTermsService;
import com.discover.mobile.bank.services.account.Account;
import com.discover.mobile.bank.services.account.AccountList;
import com.discover.mobile.bank.services.account.GetCustomerAccountsServerCall;
import com.discover.mobile.bank.services.account.activity.GetActivityServerCall;
import com.discover.mobile.bank.services.account.activity.ListActivityDetail;
import com.discover.mobile.bank.services.atm.AtmResults;
import com.discover.mobile.bank.services.atm.AtmServiceHelper;
import com.discover.mobile.bank.services.atm.Directions;
import com.discover.mobile.bank.services.atm.GetAtmDetailsCall;
import com.discover.mobile.bank.services.atm.GetDirectionsServiceCall;
import com.discover.mobile.bank.services.auth.BankLoginData;
import com.discover.mobile.bank.services.auth.BankLoginDetails;
import com.discover.mobile.bank.services.auth.CreateBankLoginCall;
import com.discover.mobile.bank.services.auth.strong.BankStrongAuthAnswerDetails;
import com.discover.mobile.bank.services.auth.strong.BankStrongAuthDetails;
import com.discover.mobile.bank.services.auth.strong.CreateStrongAuthRequestCall;
import com.discover.mobile.bank.services.customer.Customer;
import com.discover.mobile.bank.services.customer.CustomerServiceCall;
import com.discover.mobile.bank.services.customer.Eligibility;
import com.discover.mobile.bank.services.deposit.AccountLimits;
import com.discover.mobile.bank.services.deposit.GetAccountLimits;
import com.discover.mobile.bank.services.payee.AddPayeeDetail;
import com.discover.mobile.bank.services.payee.AddPayeeServiceCall;
import com.discover.mobile.bank.services.payee.GetPayeeServiceCall;
import com.discover.mobile.bank.services.payee.ListPayeeDetail;
import com.discover.mobile.bank.services.payee.ManagePayeeServiceCall;
import com.discover.mobile.bank.services.payee.PayeeDetail;
import com.discover.mobile.bank.services.payee.SearchPayeeResultList;
import com.discover.mobile.bank.services.payee.SearchPayeeServiceCall;
import com.discover.mobile.bank.services.payment.CreatePaymentCall;
import com.discover.mobile.bank.services.payment.CreatePaymentDetail;
import com.discover.mobile.bank.services.payment.DeletePaymentServiceCall;
import com.discover.mobile.bank.services.payment.GetPayBillsTermsAndConditionsCall;
import com.discover.mobile.bank.services.payment.GetPaymentsServiceCall;
import com.discover.mobile.bank.services.payment.ListPaymentDetail;
import com.discover.mobile.bank.services.payment.PayBillsTermsAndConditionsDetail;
import com.discover.mobile.bank.services.payment.PaymentDetail;
import com.discover.mobile.common.DiscoverActivityManager;
import com.discover.mobile.common.callback.AsyncCallback;
import com.discover.mobile.common.error.ErrorHandlerUi;
import com.discover.mobile.common.framework.ServiceCallFactory;
import com.discover.mobile.common.net.NetworkServiceCall;

/**
 * Utility class used to construct NetworkServiceCall<> objects used for invoking Bank related web-service API.
 * 
 * @author henryoyuela
 *
 */
public class BankServiceCallFactory  implements ServiceCallFactory {

	public BankServiceCallFactory() {

	}

	/**
	 * Used to construct a CustomerServiceCall object for invoking the
	 * Bank - Customer Service API found at ./api/customers/current. The callee
	 * will only have to call submit on the constructed object to trigger the
	 * HTTP request.
	 * 
	 * @return Returns the constructed CustomerServiceCall
	 */
	public static CustomerServiceCall createCustomerDownloadCall() {
		final Activity activity = DiscoverActivityManager.getActiveActivity();

		/**
		 * Create an AsyncCallback using the default builder created for Bank related web-service HTTP requests
		 */
		final AsyncCallback<Customer> callback =
				BankPhoneAsyncCallbackBuilder.createDefaultCallbackBuilder(Customer.class, activity, (ErrorHandlerUi) activity)
				.build();

		return  new CustomerServiceCall(activity, callback);
	}

	/**
	 * TODO call createLoginCall(creds, false); here instead 
	 * Used to construct a CreateBankLoginCall object for invoking the Bank - Authentication Service API found at
	 * ./api/auth/token. The callee will only have to call submit on the constructed object to trigger the
	 * HTTP request.
	 * 
	 * @param credentials Holds User-name and Password for authenticating with the Bank Authentication Server
	 * @return A NetworkServiceCallQueue populated with the NetworkServiceCall<> required to successfully login to
	 * 			the Bank Service
	 */
	public static CreateBankLoginCall createLoginCall(final BankLoginDetails credentials ) {
		return createLoginCall(credentials, false);
		
//		final LoginActivity activity = (LoginActivity) DiscoverActivityManager.getActiveActivity();
//
//		//Build the handler for the response to the Bank authentication request
//		final AsyncCallback<BankLoginData> callback =
//				BankPhoneAsyncCallbackBuilder.createDefaultCallbackBuilder(BankLoginData.class, activity, activity)
//				.build();
//
//		//Create the NetworkServieCall<> for authenticating with the Bank Authentication Server
//		final CreateBankLoginCall loginCall =  new CreateBankLoginCall(activity, callback, credentials);
//
//		return loginCall;
	}
	
	/**
	 * Used to construct a CreateBankLoginCall object for invoking the Bank - Authentication Service API found at
	 * ./api/auth/token. The callee will only have to call submit on the constructed object to trigger the
	 * HTTP request.
	 * 
	 * @param credentials
	 * @param skipSSO true if the Login call should inform the service to skip SSO, false if SSO should be checked.
	 * @return
	 */
	public static CreateBankLoginCall createLoginCall(final BankLoginDetails credentials, final boolean skipSSO) {
		final LoginActivity activity = (LoginActivity) DiscoverActivityManager.getActiveActivity();
		
		//Build the handler for the response to the Bank authentication request
		final AsyncCallback<BankLoginData> callback =
				BankPhoneAsyncCallbackBuilder.createDefaultCallbackBuilder(BankLoginData.class, activity, activity)
				.build();

		//Create the NetworkServieCall<> for authenticating with the Bank Authentication Server
		final CreateBankLoginCall loginCall =  new CreateBankLoginCall(activity, callback, credentials, skipSSO);

		return loginCall;
	}
	
	/**
	 * Constructs a CreateSSOLoginCall for authenticating an SSO user against Bank. 
	 * @param credentials
	 * @return
	 */
//	public static CreateSSOLoginCall createSSOLoginCall(final BankSSOLoginDetail credentials) {
//		// TODO Callback
//		
//		// TODO Call
//		
//		return loginCall;
//	}

	/**
	 * Used to construct a CreatePaymentCall NetworkServiceCall for making a
	 * payment to a payee. API found at ./api/payments/. The CreatePaymentCall
	 * created here is used to POST the details needed to make a payment.
	 * 
	 * @param payment
	 * @return
	 */
	public static CreatePaymentCall createMakePaymentCall(final CreatePaymentDetail paymentDetails) {
		final BankNavigationRootActivity activity = (BankNavigationRootActivity) DiscoverActivityManager.getActiveActivity();

		final AsyncCallback<PaymentDetail> callback = BankPhoneAsyncCallbackBuilder
				.createDefaultCallbackBuilder(PaymentDetail.class, activity,
						activity).build();

		final CreatePaymentCall paymentCall = new CreatePaymentCall(activity, callback, paymentDetails);
		return paymentCall;
	}
	
	/**
	 * Used to construct a CreateStrongAuthRequestCall NetworkServiceCall for invoking the Bank - Authentication
	 * Service API found at ./api/auth/strongauth. The CreateStrongAuthRequestCall created by this method is used
	 * to POST an answer to a question downloaded prior. The callee will only have to call submit on the constructed 
	 * object to trigger the HTTP request.
	 * 
	 * @param details Contains the answer and the question ID the answer is for to be sent in the body of the request
	 * @return Reference to the created CreateStrongAuthRequestCall.
	 */
	public static CreateStrongAuthRequestCall createStrongAuthRequest(final BankStrongAuthAnswerDetails details) {
		final Activity activity = DiscoverActivityManager.getActiveActivity();

		/**
		 * Create an AsyncCallback using the default builder created for Bank related web-service HTTP requests
		 */
		final AsyncCallback<BankStrongAuthDetails>  callback =
				BankPhoneAsyncCallbackBuilder.createDefaultCallbackBuilder(BankStrongAuthDetails.class,
						activity, (ErrorHandlerUi) activity)
						.build();

		return new CreateStrongAuthRequestCall(activity, callback, details);
	}

	/**
	 * Used to construct a CreateStrongAuthRequestCall NetworkServiceCall for invoking the Bank - Authentication
	 * Service API found at ./api/auth/strongauth. The CreateStrongAuthRequestCall created by this method is used
	 * to download a question and its id. The callee will only have to call submit on the constructed object to trigger the
	 * HTTP request.
	 * 
	 * @return Reference to the created CreateStrongAuthRequestCall.
	 */
	public static CreateStrongAuthRequestCall createStrongAuthRequest() {
		final Activity activity = DiscoverActivityManager.getActiveActivity();

		/**
		 * Create an AsyncCallback using the default builder created for Bank related web-service HTTP requests
		 */
		final AsyncCallback<BankStrongAuthDetails>  callback =
				BankPhoneAsyncCallbackBuilder.createDefaultCallbackBuilder(BankStrongAuthDetails.class,
						activity, (ErrorHandlerUi) activity)
						.build();

		return new CreateStrongAuthRequestCall(activity, callback);
	}

	/**
	 * Create the service call to get the payees for a user
	 * @return the service call to get the payees for a user
	 */
	public static GetPayeeServiceCall createGetPayeeServiceRequest() {

		final Activity activity = DiscoverActivityManager.getActiveActivity();

		final AsyncCallback<ListPayeeDetail>  callback =
				BankPhoneAsyncCallbackBuilder.createDefaultCallbackBuilder(ListPayeeDetail.class,
						activity, (ErrorHandlerUi) activity)
						.build();

		return new GetPayeeServiceCall(activity, callback);
	}

	/**
	 * Create the service call to get the payees for a user
	 * @return the service call to get the payees for a user
	 */
	public static GetPayeeServiceCall createGetPayeeServiceRequest(final boolean isChainCall) {

		final Activity activity = DiscoverActivityManager.getActiveActivity();

		final AsyncCallback<ListPayeeDetail>  callback =
				BankPhoneAsyncCallbackBuilder.createDefaultCallbackBuilder(ListPayeeDetail.class,
						activity, (ErrorHandlerUi) activity)
						.build();

		return new GetPayeeServiceCall(activity, callback, isChainCall);
	}

	/**
	 * Create a POST request to tell the Bank APIs that the user has accepted a terms and conditions 
	 * that was presented to them.
	 * @param Eligibility object with the link to use to enroll for a specific service.
	 * 
	 * @return a POST request to accept bill pay terms and conditions.
	 */
	public static AcceptTermsService createAcceptTermsRequest(final Eligibility eligibility) {
		final Activity activity = DiscoverActivityManager.getActiveActivity();

		final AsyncCallback<Object> callback = 
				BankPhoneAsyncCallbackBuilder.createDefaultCallbackBuilder(Object.class, activity, null).build();

		return new AcceptTermsService(activity, callback, eligibility);
	}

	/**
	 * Create the service call to get the payees for a user
	 * @return the service call to get the payees for a user
	 */
	public static ManagePayeeServiceCall createManagePayeeServiceRequest() {

		final Activity activity = DiscoverActivityManager.getActiveActivity();

		final AsyncCallback<ListPayeeDetail>  callback =
				BankPhoneAsyncCallbackBuilder.createDefaultCallbackBuilder(ListPayeeDetail.class,
						activity, (ErrorHandlerUi) activity)
						.build();

		return new ManagePayeeServiceCall(activity, callback);
	}

	/**
	 * Create a service call to retrieve the terms and conditions for Pay Bills from the Bank APIs.
	 * @return the service call that will retrieve the terms and conditions for Pay Bills from the Bank APIs.
	 */
	public static GetPayBillsTermsAndConditionsCall createGetPayBillsTermsAndConditionsCall() {
		final Activity activity = DiscoverActivityManager.getActiveActivity();

		final AsyncCallback<PayBillsTermsAndConditionsDetail> callback = 
				BankPhoneAsyncCallbackBuilder.createDefaultCallbackBuilder(PayBillsTermsAndConditionsDetail.class,
						activity, (ErrorHandlerUi) activity)
						.build();
		return new GetPayBillsTermsAndConditionsCall(activity, callback);
	}

	/**
	 * Create the service call to get the account activity.
	 * @param url - URL to be used to get the activity
	 * @return the service call to get the account activity
	 */
	public static GetActivityServerCall createGetActivityServerCall(final String url){
		final Activity activity = DiscoverActivityManager.getActiveActivity();

		final AsyncCallback<ListActivityDetail>  callback =
				BankPhoneAsyncCallbackBuilder.createDefaultCallbackBuilder(ListActivityDetail.class,
						activity, (ErrorHandlerUi) activity)
						.build();

		return new GetActivityServerCall(activity, callback, url);
	}

	/**
	 * Creates a GetCustomerAccountsServerCall<> object used to download the Account Summary 
	 * using the Bank Accounts Service API.
	 * 
	 * @return Reference to the GetCustomerAccountsServerCall object created.
	 */
	public static GetCustomerAccountsServerCall createGetCustomerAccountsServerCall() {
		final Activity activity = DiscoverActivityManager.getActiveActivity();

		final AsyncCallback<AccountList>  callback =
				BankPhoneAsyncCallbackBuilder.createDefaultCallbackBuilder(AccountList.class,
						activity, (ErrorHandlerUi) activity)
						.build();

		return new GetCustomerAccountsServerCall(activity, callback);
	}

	/**
	 * Creates a GetCustomerAccountsServerCall<> object used to download the Account Summary 
	 * using the Bank Accounts Service API.
	 * 
	 * @param Specify url to use for GetPaymentsServiceCall. Refer to the class definition for the supported queries.
	 * @return Reference to the GetCustomerAccountsServerCall object created.
	 */
	public static GetPaymentsServiceCall createGetPaymentsServerCall(final String url) {
		final Activity activity = DiscoverActivityManager.getActiveActivity();

		final AsyncCallback<ListPaymentDetail>  callback =
				BankPhoneAsyncCallbackBuilder.createDefaultCallbackBuilder(ListPaymentDetail.class,
						activity, (ErrorHandlerUi) activity)
						.build();

		return new GetPaymentsServiceCall(activity, callback, url);
	}

	/**
	 * Creates a DeletePaymentServiceCall object used to delete a Scheduled Payment from
	 * a users Bank Account using the service API DELETE /api/payments/{id}.
	 * 
	 * @param pmt Reference to a PaymentDetail with information about the Scheduled Payment being deleted.
	 * 
	 * @return Reference to the DeletePaymentServiceCall object created.
	 */
	public static DeletePaymentServiceCall createDeletePaymentServiceCall(final PaymentDetail pmt) {
		final Activity activity = DiscoverActivityManager.getActiveActivity();

		final AsyncCallback<PaymentDetail>  callback =
				BankPhoneAsyncCallbackBuilder.createDefaultCallbackBuilder(PaymentDetail.class,
						activity, (ErrorHandlerUi) activity)
						.build();

		return new DeletePaymentServiceCall(activity, callback, pmt);
	}

	/**
	 * Creates a SearchPayeeServiceCall object used to send a request to download a list of Verified Managed Payees 
	 * which contain the string found in the name parameter. An HTTP GET Request is sent
	 * to /api/payees/search upon calling submit() on the SearchPayeeServiceCall created.
	 * 
	 * @param name Holds a string with the name of payees to be matched against at the server.
	 * 
	 * @return Reference to the SearchPayeeServiceCall object created.
	 */
	public static SearchPayeeServiceCall createPayeeSearchRequest(final String name) {
		final Activity activity = DiscoverActivityManager.getActiveActivity();

		final AsyncCallback<SearchPayeeResultList>  callback =
				BankPhoneAsyncCallbackBuilder.createDefaultCallbackBuilder(SearchPayeeResultList.class,
						activity, (ErrorHandlerUi) activity)
						.build();

		return new SearchPayeeServiceCall(activity, callback, name);
	}

	/**
	 * Creates a AddPayeeServiceCall object used to add a payee to the session user's account via
	 * an HTTP POST request to the Bank Web-service API "Add a Payee" using the url /api/payees.
	 * 
	 * @param value - Holds information about the Payee being added to the session user's account
	 * 
	 * @return Reference to the AddPayeeServiceCall object created.
	 */
	public static AddPayeeServiceCall createAddPayeeRequest(final AddPayeeDetail value) {
		final Activity activity = DiscoverActivityManager.getActiveActivity();

		final AsyncCallback<PayeeDetail>  callback =
				BankPhoneAsyncCallbackBuilder.createDefaultCallbackBuilder(PayeeDetail.class,
						activity, (ErrorHandlerUi) activity)
						.build();

		return new AddPayeeServiceCall(activity, callback, value);
	}

	/**
	 * Creates a GetAtmDetailsCall object used to get the information about atms close to a user
	 * via an HTTP GET.  API is /api/atmLocator/SearchGeocodedLocation.xml
	 * 
	 * @param value - Holds information about the query string for the search of the call
	 * 
	 * @return Reference to the GetAtmDetailsCall object created.
	 */
	public static GetAtmDetailsCall createGetAtmServiceCall(final AtmServiceHelper helper){
		final Activity activity = DiscoverActivityManager.getActiveActivity();

		final AsyncCallback<AtmResults>  callback =
				BankPhoneAsyncCallbackBuilder.createDefaultCallbackBuilder(AtmResults.class,
						activity, (ErrorHandlerUi) activity)
						.build();

		return new GetAtmDetailsCall(activity, callback, helper);
	}

	/**
	 * Creates a GetAtmDetailsCall object used to get the information about atms close to a user
	 * via an HTTP GET.  API is /api/atmLocator/SearchGeocodedLocation.xml
	 * 
	 * @param value - Holds information about the query string for the search of the call
	 * 
	 * @return Reference to the GetAtmDetailsCall object created.
	 */
	public static GetDirectionsServiceCall createGetDirectionsCall(final AtmServiceHelper helper){
		final Activity activity = DiscoverActivityManager.getActiveActivity();

		final AsyncCallback<Directions>  callback =
				BankPhoneAsyncCallbackBuilder.createDefaultCallbackBuilder(Directions.class,
						activity, (ErrorHandlerUi) activity)
						.build();

		return new GetDirectionsServiceCall(activity, callback, helper);
	}

	/**
	 * Creates a GetAccountLimits object used to fetch deposit limits for an Acoount via
	 * an HTTP GET request to the Bank Web-service API "Get Account Limits" using the url 
	 * /api/deposits/limits/{id}.
	 * 
	 * @param value - Holds information about the Account whose deposit's limits are being retrieved.
	 * 
	 * @return Reference to the GetAccountLimits object created.
	 */
	public static GetAccountLimits createGetAccountLimits(final Account account) {
		final Activity activity = DiscoverActivityManager.getActiveActivity();

		final AsyncCallback<AccountLimits>  callback =
				BankPhoneAsyncCallbackBuilder.createDefaultCallbackBuilder(AccountLimits.class,
						activity, (ErrorHandlerUi) activity)
						.build();

		return new GetAccountLimits(activity, callback, account);
	}

	@Override
	public NetworkServiceCall<?> createServiceCall(@SuppressWarnings("rawtypes") final Class cacheObject,
			final Serializable payload) {

		final Activity activity = DiscoverActivityManager.getActiveActivity();

		if ( cacheObject == PayeeDetail.class ) { 

			final AsyncCallback<PayeeDetail>  callback =
					BankPhoneAsyncCallbackBuilder.createDefaultCallbackBuilder(PayeeDetail.class,
							activity, (ErrorHandlerUi) activity)
							.build();

			return new AddPayeeServiceCall(activity, callback, (AddPayeeDetail) payload);

		}
		// FIXME : Add the rest in here!!! 
		return null;
	}


}
