package com.discover.mobile.bank.payees;

import java.util.List;

import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.discover.mobile.BankMenuItemLocationIndex;
import com.discover.mobile.bank.BankExtraKeys;
import com.discover.mobile.bank.R;
import com.discover.mobile.bank.error.BankErrorHandlerDelegate;
import com.discover.mobile.bank.framework.BankServiceCallFactory;
import com.discover.mobile.bank.navigation.BankNavigationRootActivity;
import com.discover.mobile.bank.services.payee.AddPayeeDetail;
import com.discover.mobile.bank.services.payee.SearchPayeeResult;
import com.discover.mobile.bank.ui.fragments.BankOneButtonFragment;
import com.discover.mobile.bank.ui.table.ViewPagerListItem;
import com.discover.mobile.common.net.error.bank.BankError;
import com.discover.mobile.common.net.error.bank.BankErrorResponse;
import com.discover.mobile.common.ui.modals.ModalAlertWithTwoButtons;
import com.discover.mobile.common.ui.modals.ModalDefaultTopView;
import com.discover.mobile.common.ui.modals.ModalDefaultTwoButtonBottomView;
import com.google.common.base.Strings;

/**
 * Fragment class used to display the Add Payee - Payee Details Page Step 4 of the Add Payee workflow. 
 * Details Page Step 4 of the Add Payee workflow. 
 * 
 * The layout of this fragment will change depending on whether the user selected to add a 
 * Verfied Managed Payee or an Un-verified Managed Payee. 
 *
 * If the user select a verified payee, then a SearchPayeeResult is passed to this fragment via a bundle.
 * The fragment can read the SearchPayeeResult using BankExtraKey.DATA_LIST_ITEM. The fragment will also
 * display a message to the user indicating that they have selected a verified payee. The layout for this 
 * case will consists of the following input fields:
 * 
 * 		Payee Name
 * 		Nickname
 * 		Account#
 * 		Re-Enter Account#
 * 		Zip Code
 * 
 * If the user selected to Enter Payee Details then they have chosen to enter a potentially unverified Payee.
 * In this case the fields are:
 * 
 * 		Payee Name
 * 		Nickname
 * 		Phone Number
 * 		Address Line 1
 * 		Address Line 2 
 * 		City
 * 		State
 * 		Zip Code
 * 		Account# / Memo
 * 
 * The user will have the option to click on a help button, feedback button, an Add Payee Button, 
 * and a cancel button.
 * 
 * @author henryoyuela
 *
 */
public class BankAddPayeeFragment extends BankOneButtonFragment implements BankErrorHandlerDelegate {
	/**
	 * Reference to a PayeeSearchResult passed in via a bundle from BankSearchSelectPayeeFragment.
	 */
	private SearchPayeeResult payeeSearchResult;
	/**
	 * Reference to a AddPayeeDetail object used to hold the information of the Payee that will be added.
	 */
	private AddPayeeDetail detail = new AddPayeeDetail();
	/**
	 * Reference to bundle provided in onCreateView or via getArguments() depending on what created the fragment.
	 */
	private Bundle bundle = null;
	/**
	 * Key used for storing the detail data member in a bundle when onSaveInstanceState() is called.
	 */
	final private static String KEY_PAYEE_DETAIL = "new-payee";
	/**
	 * Key used for storing the payeeSearchResult data member in a bundle when onSaveInstanceState() is called.
	 */
	final private static String KEY_SEARCH_RESULT = "search-result";
	/**
	 * String appended to a string to determine whether a field has an error on rotation
	 */
	final private static String KEY_ERROR_EXT = ".hasError";
	
	private enum ManagedPayeeFields {
		PayeeName,
		PayeeNickName,
		PayeeAccountNumber,
		PayeeAccountNumberConfirmed,
		PayeeZipCode
	}

	@Override
	public View onCreateView(final LayoutInflater inflater, final ViewGroup container,
			final Bundle savedInstanceState) {

		/**Check if an Unverified Managed Payee was passed from Add Payee - Step 3 BankSearchSelectPayeeFragment*/
		bundle = this.getArguments();
		if( null != savedInstanceState ) {
			bundle = savedInstanceState;
			detail = (AddPayeeDetail)savedInstanceState.getSerializable(KEY_PAYEE_DETAIL);
			payeeSearchResult = (SearchPayeeResult)savedInstanceState.getSerializable(KEY_SEARCH_RESULT);
		} else if( null != bundle &&  null != bundle.getSerializable(BankExtraKeys.DATA_LIST_ITEM)) {
			payeeSearchResult =  (SearchPayeeResult)bundle.getSerializable(BankExtraKeys.DATA_LIST_ITEM);	
			detail.name = payeeSearchResult.name;
			detail.verified = true;
			detail.merchantNumber = payeeSearchResult.merchantNumber;
			detail.isZipRequired = payeeSearchResult.isZipRequired();
		} 

		final View view = super.onCreateView(inflater, container, savedInstanceState);

		/**initialize content to be displayed on fragment*/
		initialize(view);


		return view;
	}

	/**
	 * Method used to initialize the views in the layout used for this fragment.
	 * 
	 * @param mainView Reference to the parent view provided in onCreateView
	 */
	protected void initialize(final View mainView) {
		/**Hide top note as it is not needed for this view**/
		final TextView topNote = (TextView)mainView.findViewById(R.id.top_note_text);
		topNote.setVisibility(View.GONE);

		/**Setup Progress Indicator to show Payment Details and Payment Scheduled, on step 1, and hide step 2 **/
		progressIndicator.initChangePasswordHeader(0);
		progressIndicator.hideStepTwo();
		progressIndicator.setTitle(R.string.bank_payee_details, R.string.bank_payee_added, R.string.bank_payee_added);

		/**Make the note title/text visible to the user if Verified Payee*/
		if( null != payeeSearchResult) {
			noteTitle.setText(R.string.bank_verified_payees_address);
			noteTitle.setVisibility(View.VISIBLE);
			noteTextMsg.setText(R.string.bank_verified_payees_address_msg);
			noteTextMsg.setVisibility(View.VISIBLE);
		} else {
			noteTitle.setVisibility(View.GONE);
			noteTextMsg.setVisibility(View.GONE);
		}

		actionButton.setText(R.string.bank_add_payee);

		actionLink.setText(R.string.bank_add_cancel);
		
		//Need Help and feedback footer not required for this view
		final View footer = mainView.findViewById(R.id.provide_feedback);
		if( footer != null ) {
			footer.setVisibility(View.GONE);
		}
	}


	@Override
	public int getActionBarTitle() {
		return R.string.bank_manage_payees;
	}

	/**
	 * Method used to validate all input fields to make sure they meet the 
	 * criteria associated with each at creation. Refer to PayeeDetailListGenerator
	 * for the criteria associated with each BankEditDetail object.
	 * 
	 * @return True if all fields validate correctly, false otherwise.
	 */
	private boolean canProceed() {
		boolean ret = true;

		/**Iterate through each BankEditDetail object and make sure their editable field validates correctly*/
		if( content != null){
			for(final Object element : content) {
				if( element instanceof BankEditDetail) {
					ret = ((BankEditDetail)element).getEditableField().isValid();

					if( !ret ) {					
						break;
					}
				}
			}
		}

		/**Make sure account number matches re-enter account number*/
		if( ret ) {
			ret = doAcctNumbersMatch();
		}

		return ret;
	}

	/**
	 * Method used to check if values entered by the user in account # and re-entered account #  match.
	 * 
	 * @return Returns true account numbers match, false otherwise.
	 */
	private boolean doAcctNumbersMatch() {
		final BankEditDetail acctNum = ((BankEditDetail)content.get(ManagedPayeeFields.PayeeAccountNumber.ordinal()));
		final BankEditDetail acctConfirm = ((BankEditDetail)content.get(ManagedPayeeFields.PayeeAccountNumberConfirmed.ordinal()));

		final String accountNum =  acctNum.getEditableField().getText().toString();;
		final String accountMatch =  acctConfirm.getEditableField().getText().toString();

		return accountNum.equals(accountMatch); 
	}

	/**
	 * Shows inline errors for all BankEditDetail objects if fields do not validate correctly.
	 */
	private void updateFieldsAppearance() {
		/**Iterate through each BankEditDetail and ensure it validates correctly otherwise show inline errors*/
		if( content != null){
			for(final Object element : content) {
				if( element instanceof BankEditDetail) {
					((BankEditDetail)element).setEditMode(false);
					((BankEditDetail)element).getEditableField().updateAppearanceForInput();
				}
			}
		}

		/**Verify if account numbers entered by the user's match otherwise show inline error*/
		if( !doAcctNumbersMatch() ) {
			final BankEditDetail acctConfirm = ((BankEditDetail)content.get(ManagedPayeeFields.PayeeAccountNumberConfirmed.ordinal()));

			/**Show non-matching acct# error inline*/
			acctConfirm.showErrorLabel(R.string.bank_nonmatching_acct);
		}

	}

	/**
	 * Generates an AddPayeeDetail object using the text values stored in each BankEditDetail that is
	 * part of the content list.
	 * 
	 * @return Reference to an AddPayeeDetail object with information of the Payee that is to be added.
	 */
	private AddPayeeDetail getPayeeDetail() {
		if( content != null ) {
			final BankEditDetail nickName =  ((BankEditDetail)content.get(ManagedPayeeFields.PayeeNickName.ordinal())); 
			final BankEditDetail acctNum = ((BankEditDetail)content.get(ManagedPayeeFields.PayeeAccountNumber.ordinal()));
			final BankEditDetail name = ((BankEditDetail)content.get(ManagedPayeeFields.PayeeName.ordinal()));
			final BankEditDetail acctConfirm = ((BankEditDetail)content.get(ManagedPayeeFields.PayeeAccountNumberConfirmed.ordinal()));

			detail.name = name.getText();
			detail.nickName = nickName.getText();
			detail.accountNumber =  acctNum.getText();
			detail.accountNumberConfirmed = acctConfirm.getText();

			/**If Zip is required then set zip for the payee being added*/
			if(payeeSearchResult != null && payeeSearchResult.isZipRequired() ) {
				final BankEditDetail zip = ((BankEditDetail)content.get(ManagedPayeeFields.PayeeZipCode.ordinal()));

				detail.zip =  zip.getText();
				detail.isZipRequired = true;
			} else {
				detail.isZipRequired = false;
			}
		}

		return detail;
	}

	/**
	 * Action Button onClick() Handler, which triggers the request to Add a Payee using
	 * the information returned by getPayeeDetail method. All fields must validate correctly
	 * in order for the service call to be made, otherwise inline errors are shown for
	 * each field with invalid content.
	 */
	@Override
	protected void onActionButtonClick() {
		if( canProceed() ) {
			clearErrors();
			
			BankServiceCallFactory.createAddPayeeRequest(getPayeeDetail()).submit();
		} else {
			updateFieldsAppearance();
		}
	}

	/**
	 * Method used to handle when the user clicks on cancel at the bottom of the screen.
	 */
	@Override
	protected void onActionLinkClick() {
		// Create a one button modal to notify the user that they are cancelling the Add Payee transaction
		final ModalDefaultTopView cancelModalTopView = new ModalDefaultTopView(getActivity(), null);

		cancelModalTopView.setTitle(R.string.bank_cancel_title);
		cancelModalTopView.setContent(R.string.bank_cancel_msg);

		final ModalDefaultTwoButtonBottomView cancelModalButtons = new ModalDefaultTwoButtonBottomView(
				getActivity(), null);
		cancelModalButtons
		.setCancelButtonText(R.string.bank_cancel_noaction);
		cancelModalButtons
		.setOkButtonText(R.string.bank_cancel_yesaction);

		final ModalAlertWithTwoButtons cancelModal = new ModalAlertWithTwoButtons(
				getActivity(), cancelModalTopView, cancelModalButtons);
		((BankNavigationRootActivity) getActivity())
		.showCustomAlert(cancelModal);

		/**
		 * Hide the need help footer for the modal.
		 */
		final ModalDefaultTopView topView = (ModalDefaultTopView)cancelModal.getTop();
		topView.hideNeedHelpFooter();
		
		cancelModalButtons.getOkButton().setOnClickListener(
				new OnClickListener() {
					@Override
					public void onClick(final View v) {
						cancelModal.dismiss();
						/**
						 * Pop all fragments till we reach BankManagePayee. 
						 * Checking if the Activity is null due to a crash occuring when the modal is open
						 * and you tap yes quickly the activity is null and the app will crash. 
						 */
						if ((BankNavigationRootActivity) getActivity() != null)
							((BankNavigationRootActivity) getActivity()).popTillFragment(BankManagePayee.class);
					}
				});

		cancelModalButtons.getCancelButton().setOnClickListener(
				new OnClickListener() {
					@Override
					public void onClick(final View v) {
						cancelModal.dismiss();
					}
				});
	}

	/**
	 * Method Not Used
	 */
	@Override
	protected List<ViewPagerListItem> getViewPagerListContent() {
		return null;
	}

	/**
	 * Method used to generate a list of RelativeLayouts that display the information
	 * stored in the detail data member on the layout hosted by this fragment.
	 */
	@Override
	protected List<RelativeLayout> getRelativeLayoutListContent() {
		return PayeeDetailListGenerator.getPayeeDetailList(getActivity(), detail);
	}

	/**
	 * Method used to store the state of the fragment and support orientation change
	 */
	@Override
	public void onSaveInstanceState(final Bundle outState) {
		super.onSaveInstanceState(outState);

		final AddPayeeDetail curPayeeDetail = getPayeeDetail();
		if( null != curPayeeDetail ) {
			outState.putSerializable(KEY_PAYEE_DETAIL, curPayeeDetail);
		}

		if( null != payeeSearchResult) {
			outState.putSerializable(KEY_SEARCH_RESULT, payeeSearchResult);
		}
		
		/**Store the state of the editable fields, to re-open keyboard on orientation change if necessary*/
		for( final Object object : this.content) {
			if( object instanceof BankEditDetail ) {
				final BankEditDetail item = (BankEditDetail)object;
				
				final boolean hasFocus = item.getEditableField().hasFocus();
				final String key = item.getTopLabel().getText().toString();
				outState.putBoolean(key, hasFocus );
				
				/**If has an error then show it on rotation */
				if( item.getEditableField().isInErrorState ) {
					outState.putString(key +KEY_ERROR_EXT, item.getEditableField().getErrorLabel().getText().toString());
				}
				
				if( generalError.getVisibility() == View.VISIBLE ) {
					outState.putString(KEY_ERROR_EXT, generalError.getText().toString());
				}
			}
		}
	}

	/**
	 * Restores the widget's states from before rotation.
	 * 
	 * @param savedInstanceState
	 *            the Bundle from which the data is loaded.
	 */
	public void restoreState() {
		if( detail != null ) {
			final BankEditDetail name = getFieldDetail(ManagedPayeeFields.PayeeName);
			final BankEditDetail nickName =  getFieldDetail(ManagedPayeeFields.PayeeNickName); 
			final BankEditDetail acctNum = getFieldDetail(ManagedPayeeFields.PayeeAccountNumber);
			final BankEditDetail acctConfirm = getFieldDetail(ManagedPayeeFields.PayeeAccountNumberConfirmed);

			name.setText(detail.name);
			nickName.setText(detail.nickName);
			acctNum.setText(detail.accountNumber);
			acctConfirm.setText(detail.accountNumberConfirmed);

			if(detail.isZipRequired){
				final BankEditDetail zip = getFieldDetail(ManagedPayeeFields.PayeeZipCode);
				zip.setText(detail.zip);
			}	
			
			/**Restore the state of the editable fields and re-open keyboard if either of them had focus*/
			if( bundle != null  ) {
				for( final Object object : this.content) {
					if( object instanceof BankEditDetail ) {
						
						final BankEditDetail item = (BankEditDetail)object;
						final String key = item.getTopLabel().getText().toString();
						final boolean hasFocus  = bundle.getBoolean(key, false);
						final String errorString = bundle.getString(key +KEY_ERROR_EXT);
						final String genError = bundle.getString(KEY_ERROR_EXT);
						
						
						if( hasFocus || !Strings.isNullOrEmpty(errorString) || 
							(!Strings.isNullOrEmpty(genError) && generalError.getVisibility() == View.VISIBLE) ) {
							/** 
							 * Have to execute the setting of the editable field to edit mode asyncronously otherwise
							 * the keyboard doesn't open.
							 */
							new Handler().postDelayed(new Runnable() {
								@Override
								public void run() {
									if( hasFocus ) {
										/**Setting edit mode to true makes editable field visible and opens keyboard*/
										item.setEditMode(hasFocus);
									}
									
									/**If has an error then show it on rotation */
									if( !Strings.isNullOrEmpty(errorString) ) {
										
										item.setEditModeNoFocus(true);
										
										item.getEditableField().showErrorLabel(errorString);
									}
									
									showGeneralError(genError);
								}
							}, 1000);
						}
					}
				}
			}
			
		} 
		
		bundle = null;
	}

	@Override
	public void onResume() {
		super.onResume();
		restoreState();
	}

	@Override
	public int getGroupMenuLocation() {
		return BankMenuItemLocationIndex.PAY_BILLS_GROUP;
	}

	@Override
	public int getSectionMenuLocation() {
		return BankMenuItemLocationIndex.MANAGE_PAYEES_SECTION;
	}

	@Override
	public void onBackPressed() {
		//Nothing To Do Here
	}

	@Override
	public boolean handleError(final BankErrorResponse msgErrResponse) {
		for( final BankError error : msgErrResponse.errors ) {
			if( !Strings.isNullOrEmpty(error.name) ) {
				/**Check if error is for Payee field*/
				if( error.name.equals(AddPayeeDetail.NAME_FIELD) ) {
					setErrorString(ManagedPayeeFields.PayeeName,error.message);
				}
				/**Check if error is for amount field*/
				else if( error.name.equals(AddPayeeDetail.NICKNAME_FIELD)) {
					setErrorString(ManagedPayeeFields.PayeeNickName, error.message);
				}
				/**Check if error is for Payment method field*/
				else if( error.name.equals(AddPayeeDetail.ACCOUNT_NUMBER_FIELD)) {
					setErrorString(ManagedPayeeFields.PayeeAccountNumber,error.message);
					setErrorString(ManagedPayeeFields.PayeeAccountNumberConfirmed, error.message);
				}
				/**Check if error is for Deliver by field*/
				else if( error.name.equals(AddPayeeDetail.BILLING_POSTAL_CODE_FIELD) ) {
					setErrorString(ManagedPayeeFields.PayeeZipCode,error.message);
				}
				/**Show error at the top of the screen */
				else {
					showGeneralError(error.message);
				}
			}
		}
		return true;
	}
	
	/**
	 * Method used to clear any inline errors on the page.
	 */
	public void clearErrors() {
		clearGeneralError();
		
		for( final Object object : this.content) {
			if( object instanceof BankEditDetail ) {
				final BankEditDetail detail = (BankEditDetail) object;
				detail.getEditableField().clearErrors();
				detail.setEditMode(false);
			}
		}
		
	}
	
	/**
	 * Sets the inline error string for the field specified in the field identifier.
	 * 
	 * @param field An identifier that specifies what field is required from the list.
	 * @param text The inline error text is to be applied to the field
	 */
	public void setErrorString(final ManagedPayeeFields field, final String text) {
		final BankEditDetail detail = getFieldDetail(field);
		
		if( detail != null ) {
			detail.setEditModeNoFocus(true);
			
			detail.getEditableField().showErrorLabel(text);
		}
	}
	
	/**
	 * Method used to read a BankEditDetail widget from the content table.
	 * 
	 * @param field Specifies what widget to read from the content table
	 * 
	 * @return Returns the widget from the content table on the page specified by the field identifier.
	 */
	public BankEditDetail getFieldDetail(final ManagedPayeeFields field)  {
		BankEditDetail ret = null;
		
		if( content != null ) {
			ret =  ((BankEditDetail)content.get(field.ordinal())); 
		}
		
		
		return ret;
	}

}
