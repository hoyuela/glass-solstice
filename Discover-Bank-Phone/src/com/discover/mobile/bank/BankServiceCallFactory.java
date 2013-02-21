package com.discover.mobile.bank;

import android.app.Activity;

import com.discover.mobile.bank.login.LoginActivity;
import com.discover.mobile.bank.services.account.AccountList;
import com.discover.mobile.bank.services.account.GetCustomerAccountsServerCall;
import com.discover.mobile.bank.services.account.activity.GetActivityServerCall;
import com.discover.mobile.bank.services.account.activity.ListActivityDetail;
import com.discover.mobile.bank.services.auth.BankLoginData;
import com.discover.mobile.bank.services.auth.BankLoginDetails;
import com.discover.mobile.bank.services.auth.CreateBankLoginCall;
import com.discover.mobile.bank.services.auth.strong.BankStrongAuthAnswerDetails;
import com.discover.mobile.bank.services.auth.strong.BankStrongAuthDetails;
import com.discover.mobile.bank.services.auth.strong.CreateStrongAuthRequestCall;
import com.discover.mobile.bank.services.customer.Customer;
import com.discover.mobile.bank.services.customer.CustomerServiceCall;
import com.discover.mobile.bank.services.payee.GetPayeeServiceCall;
import com.discover.mobile.bank.services.payee.ListPayeeDetail;
import com.discover.mobile.bank.services.payee.ManagePayeeServiceCall;
import com.discover.mobile.bank.services.payee.SearchPayeeResultList;
import com.discover.mobile.bank.services.payee.SearchPayeeServiceCall;
import com.discover.mobile.bank.services.payment.DeletePaymentServiceCall;
import com.discover.mobile.bank.services.payment.GetPaymentsServiceCall;
import com.discover.mobile.bank.services.payment.ListPaymentDetail;
import com.discover.mobile.bank.services.payment.PaymentDetail;
import com.discover.mobile.common.DiscoverActivityManager;
import com.discover.mobile.common.callback.AsyncCallback;
import com.discover.mobile.common.error.ErrorHandlerUi;

/**
 * Utility class used to construct NetworkServiceCall<> objects used for invoking Bank related web-service API.
 * 
 * @author henryoyuela
 *
 */
public class BankServiceCallFactory {

	private BankServiceCallFactory() {

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
	 * Used to construct a CreateBankLoginCall object for invoking the Bank - Authentication Service API found at
	 * ./api/auth/token. The callee will only have to call submit on the constructed object to trigger the
	 * HTTP request.
	 * 
	 * @param credentials Holds User-name and Password for authenticating with the Bank Authentication Server
	 * @return A NetworkServiceCallQueue populated with the NetworkServiceCall<> required to successfully login to
	 * 			the Bank Service
	 */
	public static CreateBankLoginCall createLoginCall(final BankLoginDetails credentials ) {
		final LoginActivity activity = (LoginActivity) DiscoverActivityManager.getActiveActivity();

		//Build the handler for the response to the Bank authentication request
		final AsyncCallback<BankLoginData> callback =
				BankPhoneAsyncCallbackBuilder.createDefaultCallbackBuilder(BankLoginData.class, activity, activity)
				.build();

		//Create the NetworkServieCall<> for authenticating with the Bank Authentication Server
		final CreateBankLoginCall loginCall =  new CreateBankLoginCall(activity, callback, credentials);

		return loginCall;
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
	public static ManagePayeeServiceCall createManagePayeeServiceRequest() {

		final Activity activity = DiscoverActivityManager.getActiveActivity();

		final AsyncCallback<ListPayeeDetail>  callback =
				BankPhoneAsyncCallbackBuilder.createDefaultCallbackBuilder(ListPayeeDetail.class,
						activity, (ErrorHandlerUi) activity)
						.build();

		return new ManagePayeeServiceCall(activity, callback);
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
	 * 
	 * @param pmt
	 * @return
	 */
	public static DeletePaymentServiceCall createDeletePaymentServiceCall(final PaymentDetail pmt) {
		final Activity activity = DiscoverActivityManager.getActiveActivity();

		final AsyncCallback<PaymentDetail>  callback =
				BankPhoneAsyncCallbackBuilder.createDefaultCallbackBuilder(PaymentDetail.class,
						activity, (ErrorHandlerUi) activity)
						.build();

		return new DeletePaymentServiceCall(activity, callback, pmt);
	}

	public static SearchPayeeServiceCall createPayeeSearchRequest(final String name) {
		final Activity activity = DiscoverActivityManager.getActiveActivity();

		final AsyncCallback<SearchPayeeResultList>  callback =
				BankPhoneAsyncCallbackBuilder.createDefaultCallbackBuilder(SearchPayeeResultList.class,
						activity, (ErrorHandlerUi) activity)
						.build();

		return new SearchPayeeServiceCall(activity, callback, name);
	}
	
}
