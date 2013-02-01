package com.discover.mobile.bank;

import javax.annotation.Nonnull;

import android.app.Activity;

import com.discover.mobile.AsyncCallbackBuilderLibrary;
import com.discover.mobile.ErrorHandlerUi;
import com.discover.mobile.alert.ModalAlertWithOneButton;
import com.discover.mobile.common.AccountType;
import com.discover.mobile.common.Globals;
import com.discover.mobile.common.auth.bank.BankLoginData;
import com.discover.mobile.common.auth.bank.BankLoginDetails;
import com.discover.mobile.common.auth.bank.BankSchema;
import com.discover.mobile.common.auth.bank.CreateBankLoginCall;
import com.discover.mobile.common.auth.bank.strong.BankStrongAuthAnswerDetails;
import com.discover.mobile.common.auth.bank.strong.BankStrongAuthDetails;
import com.discover.mobile.common.auth.bank.strong.CreateStrongAuthRequestCall;
import com.discover.mobile.common.callback.AsyncCallback;
import com.discover.mobile.common.callback.GenericCallbackListener.SuccessListener;
import com.discover.mobile.common.customer.bank.Customer;
import com.discover.mobile.common.customer.bank.CustomerServiceCall;
import com.discover.mobile.common.net.HttpHeaders;
import com.discover.mobile.common.net.NetworkServiceCallCondition;
import com.discover.mobile.common.net.NetworkServiceCallQueue;
import com.discover.mobile.common.net.NetworkServiceCallQueue.EventType;
import com.discover.mobile.common.net.error.ErrorResponse;
import com.discover.mobile.common.net.json.bank.Address;
import com.discover.mobile.common.net.json.bank.PhoneNumber;
import com.discover.mobile.error.ErrorHandlerFactory;
import com.discover.mobile.login.LoginActivity;
import com.discover.mobile.navigation.Navigator;
import com.google.common.base.Strings;




/**
 * Utility class used to construct NetworkServiceCall<> objects used for invoking Bank related web-service API.
 * 
 * @author henryoyuela
 *
 */
public class BankServiceCallFactory {
	private static BankServiceCallFactory instance = new BankServiceCallFactory();
	//TODO: Remove this code in sprint 2:
	private static Customer customer;
	//TODO: Remove this code in Sprint 2
	
	private BankServiceCallFactory() {
		
	}
	
	
	/**
	 * TODO: Placeholder for handling the downloading of customer information
	 * @author henryoyuela
	 *
	 * @param <TYPE>
	 */
	public class NavigateToActivity<TYPE> implements SuccessListener<TYPE> {
		private final Activity mActivity;
		
		private NavigateToActivity(Activity activity) {
			mActivity = activity;
		}
		
		@Override
		public CallbackPriority getCallbackPriority() {
			return CallbackPriority.MIDDLE;
		}
	
		@Override
		public void success(TYPE value) {
			Navigator.navigateToHomePage(mActivity);
			BankServiceCallFactory.customer = (Customer)value;
		}
	};

	/**
	 * This class handles the 200 OK to a POST to the web-service API /api/auth/strongauth.
	 * 
	 * {
	 *		"status" : "ALLOW", 
	 *		"links" : {
	 *			"self" : {
	 *				"ref" : "https://www.discoverbank.com/api/auth/strongauth", "allowed" : [ "GET", "POST" ]
	 *		
	 * 			}
	 * 		}
	 * }
	 * @author henryoyuela
	 *
	 */
	public class StrongAuthResponseHandler implements SuccessListener<BankStrongAuthDetails> {
		private final Activity mActivity;
		
		private StrongAuthResponseHandler(Activity activity) {
			mActivity = activity;
		}
		
 		@Override
 		public CallbackPriority getCallbackPriority() {
 			return CallbackPriority.MIDDLE;
 		}

 		@Override
 		public void success(BankStrongAuthDetails value) {
 			if( !BankStrongAuthDetails.ALLOW_STATUS.equals(value.status ) ) {
 				Navigator.navigateToStrongAuth(mActivity, value.question, value.questionId);
 			} else {
 				Navigator.navigateToHomePage(mActivity);
 			}
 		}
 	};

	/**
	 * Used to construct a CustomerServiceCall object for invoking the 
	 * Bank - Customer Service API found at ./api/customers/current. The callee
	 * will only have to call submit on the constructed object to trigger the
	 * HTTP request.
	 * 
	 * @param activity Reference to the activity invoking the service call
	 * 
	 * @return Returns the constructed CustomerServiceCall
	 */
	public static CustomerServiceCall createCustomerDownloadCall(final @Nonnull Activity activity ) {
		/**
		 * Create an AsyncCallback using the default builder created for Bank related web-service HTTP requests
		 */
		final AsyncCallback<Customer> callback = 
				AsyncCallbackBuilderLibrary.createDefaultBankBuilder(Customer.class, activity, (ErrorHandlerUi) activity, false)
					.withSuccessListener(instance.new NavigateToActivity<Customer>(activity))
					.build();

		return  new CustomerServiceCall(activity, callback);	
	}
	
	/**
	 * This function is used to construct a NetworkServiceCallQueue populated with the NetworkServiceCall<> 
	 * objects to authenticate and login a user to access Bank Services. The calling Activity will be responsible 
	 * for calling submit to start the process. The first HTTP request will consists of authenticating with the 
	 * Bank Authentication Server using a CreateBankLoginCall. Upon receiving a 200 OK response to the 
	 * authentication request, a request to download customer information will be sent. After downloading the 
	 * required information, the application navigate to the home landing page.
	 * 
	 * @param credentials Holds Username and Password for authenticating with the Bank Authentication Server
	 * @param activity Reference to the activity invoking the service call
	 * @return A NetworkServiceCallQueue populated with the NetworkServiceCall<> required to successfully login to 
	 * 			the Bank Service
	 */
	public static NetworkServiceCallQueue createLoginCall(final BankLoginDetails credentials, final LoginActivity activity ) {
		//Create success listener for CreateBankLoginCall 
		SuccessListener<BankLoginData> loginSuccessListener = new SuccessListener<BankLoginData>() {	
			@Override
			public CallbackPriority getCallbackPriority() {
				return CallbackPriority.MIDDLE;
			}

			@Override
			public void success(BankLoginData value) {
				//Set logged in to be able to save user name in persistent storage
				Globals.setLoggedIn(true);
				
				//TODO Need to set a current session object.
				//Update current account based on user logged in and account type
				activity.updateAccountInformation(AccountType.BANK_ACCOUNT);
			}
		};
		
		NetworkServiceCallCondition<BankStrongAuthDetails> condition = new NetworkServiceCallCondition<BankStrongAuthDetails>() {
			private boolean mIsCallable = false;
			
			@Override
			public boolean isCallable() {
				return mIsCallable;
			}
			
			@Override
			public boolean handleFailure(ErrorResponse<?> arg0) {
				String wwwAuthenticateValue = arg0.getConnection().getHeaderField(HttpHeaders.Authentication);
				
				if( !Strings.isNullOrEmpty(wwwAuthenticateValue) ) {
					//Check if strong auth challenge
					mIsCallable = ( wwwAuthenticateValue.contains(BankSchema.BANKSA));
				} 
				
				//Execute error handler if not handled by NetworkServiceQueue
				if( !mIsCallable ) {
					BankBaseErrorResponseHandler handler = new BankBaseErrorResponseHandler(activity);
					handler.handleFailure(arg0);
				}
				
				return false;
			}
		};
		
		//Build the handler for the response to the Bank authentication request
		final AsyncCallback<BankLoginData> callback = 
				AsyncCallbackBuilderLibrary.createDefaultBankBuilder(BankLoginData.class, activity, activity, false)
					.withSuccessListener(loginSuccessListener)
					.build();
		
		//Create the NetworkServieCall<> for authenticating with the Bank Authentication Server
		CreateBankLoginCall loginCall =  new CreateBankLoginCall(activity, callback, credentials);
		//Create the NetworkServiceCall<> for downloading customer information after successfully authenticating
		CustomerServiceCall customerCall = createCustomerDownloadCall(activity);
		//Create the NetworkServiceCall<> for sending a Strong Auth request to download question
		CreateStrongAuthRequestCall strongAuthCall = createStrongAuthRequest(activity);
		
		//Create the queue which will link the NetworkServiceCall<> objects
		NetworkServiceCallQueue serviceCallQueue = new NetworkServiceCallQueue(activity, loginCall);
		serviceCallQueue.enqueue(customerCall, NetworkServiceCallQueue.EventType.Success);
		serviceCallQueue.enqueue(strongAuthCall, EventType.Error, condition);
		
		return serviceCallQueue;	
	}
	
	public static CreateStrongAuthRequestCall createStrongAuthRequest(final @Nonnull Activity activity) {
	 	
	 	/**
		 * Create an AsyncCallback using the default builder created for Bank related web-service HTTP requests
		 */
		final AsyncCallback<BankStrongAuthDetails>  callback = 
				AsyncCallbackBuilderLibrary.createDefaultBankBuilder(BankStrongAuthDetails.class, 
						activity, (ErrorHandlerUi) activity, false)
					.withSuccessListener(instance.new StrongAuthResponseHandler (activity))
					.build();
		
		 return new CreateStrongAuthRequestCall(activity, callback);
	}

	//TODO: Remove this
	public static void displayCustomerInformation() {
		StringBuilder builder = new StringBuilder();

		builder.append("E-mail: \n");
		builder.append(customer.email);
		builder.append("\n");
		builder.append("ID: \n");
		builder.append(customer.id);
		builder.append("\n");
		builder.append("Name: \n");
		builder.append("Family Name:" +customer.name.familyName +"\n");
		builder.append("Formatted Name:" +customer.name.formatted +"\n");
		builder.append("Middle:" +customer.name.middleName +"\n");
		builder.append("Type:" +customer.name.type);
		builder.append("\n\n");
		
		for (Address address : customer.addresses) {
			builder.append("Address: \n");
			builder.append("Locality:" +address.locality +"\n");
			builder.append("Postal Code:" +address.postalCode +"\n");
			builder.append("Region: " +address.region +"\n");
			builder.append("Address: " +address.streetAddress +"\n");
			builder.append("Type: " +address.type +"\n");
			builder.append("Formatted: " +address.formatted +"\n");
		}
		builder.append("\n");
		
		builder.append("Links: \n");
		for (String key : customer.links.keySet()) {
			builder.append(key +"\n" );
			builder.append(customer.links.get(key).url +"\n");
			for(Object method : customer.links.get(key).method) {
				builder.append(method +"\n");
			}
			builder.append("\n");
		}
		builder.append("\n");
		
		for (PhoneNumber phone : customer.phoneNumbers) {
			builder.append("Phone Number: \n");
			builder.append(phone.number +"\n");
			builder.append(phone.type +"\n");
			builder.append("\n");
		}
		builder.append("\n");
		
		ModalAlertWithOneButton alert = ErrorHandlerFactory.getInstance().createErrorModal("Customer Info Download", builder.toString());
		ErrorHandlerFactory.showCustomAlert(alert);
	}
	
	public static CreateStrongAuthRequestCall createStrongAuthRequest(final @Nonnull Activity activity, final BankStrongAuthAnswerDetails details) {
	 	/**
		 * Create an AsyncCallback using the default builder created for Bank related web-service HTTP requests
		 */
		final AsyncCallback<BankStrongAuthDetails>  callback = 
				AsyncCallbackBuilderLibrary.createDefaultBankBuilder(BankStrongAuthDetails.class, 
						activity, (ErrorHandlerUi) activity, true)
					.withSuccessListener(instance.new StrongAuthResponseHandler (activity))
					.build();
		
		 return new CreateStrongAuthRequestCall(activity, callback, details);
	}


}
