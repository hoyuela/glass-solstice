package com.discover.mobile.bank;

import android.app.Activity;

import com.discover.mobile.AsyncCallbackBuilderLibrary;
import com.discover.mobile.ErrorHandlerUi;
import com.discover.mobile.common.auth.bank.BankLoginData;
import com.discover.mobile.common.auth.bank.BankLoginDetails;
import com.discover.mobile.common.auth.bank.CreateBankLoginCall;
import com.discover.mobile.common.auth.bank.strong.BankStrongAuthAnswerDetails;
import com.discover.mobile.common.auth.bank.strong.BankStrongAuthDetails;
import com.discover.mobile.common.auth.bank.strong.CreateStrongAuthRequestCall;
import com.discover.mobile.common.bank.payee.GetPayeeServiceCall;
import com.discover.mobile.common.bank.payee.ListPayeeDetail;
import com.discover.mobile.common.callback.AsyncCallback;
import com.discover.mobile.common.callback.GenericCallbackListener.SuccessListener;
import com.discover.mobile.common.customer.bank.Customer;
import com.discover.mobile.common.customer.bank.CustomerServiceCall;
import com.discover.mobile.common.net.NetworkServiceCall;
import com.discover.mobile.login.LoginActivity;
import com.discover.mobile.navigation.Navigator;

/**
 * Utility class used to construct NetworkServiceCall<> objects used for invoking Bank related web-service API.
 * 
 * @author henryoyuela
 *
 */
public class BankServiceCallFactory {
	private static BankServiceCallFactory instance = new BankServiceCallFactory();

	private static Customer customer;


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
		final Activity activity = BankActivityManager.getActiveActivity();

		/**
		 * Create an AsyncCallback using the default builder created for Bank related web-service HTTP requests
		 */
		final AsyncCallback<Customer> callback =
				AsyncCallbackBuilderLibrary.createDefaultBankBuilder(Customer.class, activity, (ErrorHandlerUi) activity)
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
		final LoginActivity activity = (LoginActivity) BankActivityManager.getActiveActivity();

		//Build the handler for the response to the Bank authentication request
		final AsyncCallback<BankLoginData> callback =
				AsyncCallbackBuilderLibrary.createDefaultBankBuilder(BankLoginData.class, activity, activity)
				.build();

		//Create the NetworkServieCall<> for authenticating with the Bank Authentication Server
		final CreateBankLoginCall loginCall =  new CreateBankLoginCall(activity, callback, credentials);

		return loginCall;
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
		final Activity activity = BankActivityManager.getActiveActivity();

		/**
		 * Create an AsyncCallback using the default builder created for Bank related web-service HTTP requests
		 */
		final AsyncCallback<BankStrongAuthDetails>  callback =
				AsyncCallbackBuilderLibrary.createDefaultBankBuilder(BankStrongAuthDetails.class,
						activity, (ErrorHandlerUi) activity)
						.build();

		return new CreateStrongAuthRequestCall(activity, callback);
	}

	/**
	 * Used to construct a CreateStrongAuthRequestCall NetworkServiceCall for invoking the Bank - Authentication
	 * Service API found at ./api/auth/strongauth. The CreateStrongAuthRequestCall created by this method is used
	 * to POST an answer to a question downloaded prior. The callee will only have to call submit on the constructed object to trigger the
	 * HTTP request.
	 * 
	 * @param details Contains the answer and the question ID the answer is for to be sent in the body of the request.
	 * @return Reference to the created CreateStrongAuthRequestCall.
	 */
	public static CreateStrongAuthRequestCall createStrongAuthRequest(final BankStrongAuthAnswerDetails details) {
		final Activity activity = BankActivityManager.getActiveActivity();

		/**
		 * Create an AsyncCallback using the default builder created for Bank related web-service HTTP requests
		 */
		final AsyncCallback<BankStrongAuthDetails>  callback =
				AsyncCallbackBuilderLibrary.createDefaultBankBuilder(BankStrongAuthDetails.class,
						activity, (ErrorHandlerUi) activity)
						.build();

		return new CreateStrongAuthRequestCall(activity, callback, details);
	}

	/**
	 * 
	 * @return
	 */
	public static GetPayeeServiceCall createGetPayeeServiceRequest(){
		final Activity activity = BankActivityManager.getActiveActivity();

		final AsyncCallback<ListPayeeDetail>  callback =
				AsyncCallbackBuilderLibrary.createDefaultBankBuilder(ListPayeeDetail.class,
						activity, (ErrorHandlerUi) activity)
						.build();

		return new GetPayeeServiceCall(activity, callback);
	}

	/**
	 * TODO: Define once account summary is completed
	 * @return
	 */
	public static NetworkServiceCall<BankLoginData> createAccountDownloadCall() {
		BankActivityManager.getActiveActivity();

		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * TODO: Placeholder for handling the downloading of customer information, this will be removed
	 * in sprint 2
	 * @author henryoyuela
	 *
	 * @param <TYPE>
	 */
	public class NavigateToActivity<TYPE> implements SuccessListener<TYPE> {
		private final Activity mActivity;

		private NavigateToActivity(final Activity activity) {
			this.mActivity = activity;
		}

		@Override
		public CallbackPriority getCallbackPriority() {
			return CallbackPriority.MIDDLE;
		}

		@Override
		public void success(final NetworkServiceCall<?> sender, final TYPE value) {
			Navigator.navigateToHomePage(this.mActivity);
			BankServiceCallFactory.customer = (Customer)value;
		}
	};
}
