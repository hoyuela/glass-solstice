package com.discover.mobile.bank.payees;

import java.util.List;

import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.TextView;

import com.discover.mobile.BankMenuItemLocationIndex;
import com.discover.mobile.bank.R;
import com.discover.mobile.bank.error.BankErrorHandlerDelegate;
import com.discover.mobile.bank.framework.BankServiceCallFactory;
import com.discover.mobile.bank.help.HelpMenuListFactory;
import com.discover.mobile.bank.navigation.BankNavigationRootActivity;
import com.discover.mobile.bank.paybills.BankSelectPayee;
import com.discover.mobile.bank.services.error.BankErrorResponse;
import com.discover.mobile.bank.services.payee.AddPayeeDetail;
import com.discover.mobile.bank.services.payee.SearchPayeeResult;
import com.discover.mobile.bank.ui.fragments.BankOneButtonFragment;
import com.discover.mobile.bank.ui.table.ViewPagerListItem;
import com.discover.mobile.bank.ui.widgets.BankHeaderProgressIndicator;
import com.discover.mobile.common.help.HelpWidget;
import com.discover.mobile.common.ui.modals.ModalAlertWithOneButton;
import com.discover.mobile.common.ui.modals.ModalDefaultOneButtonBottomView;
import com.discover.mobile.common.ui.modals.ModalDefaultTopView;
import com.google.common.base.Strings;

/**
 * Base class with functionality that is common for rendering a UI Layout and binding with the data model
 * used for the Add Payee application feature.
 * 
 * @author henryoyuela
 *
 */
abstract class BankAddPayeeFragment extends BankOneButtonFragment implements BankErrorHandlerDelegate {
	/**
	 * Reference to a PayeeSearchResult passed in via a bundle from BankSearchSelectPayeeFragment.
	 */
	protected SearchPayeeResult payeeSearchResult;
	/**
	 * Reference to a AddPayeeDetail object used to hold the information of the Payee that will be added.
	 */
	protected AddPayeeDetail detail = new AddPayeeDetail();
	/**
	 * Reference to bundle provided in onCreateView or via getArguments() depending on what created the fragment.
	 */
	protected Bundle bundle = null;
	/**
	 * Flag set to true if updating  a payee, and set to false if adding a new payee
	 */
	protected boolean isUpdate;
	/**
	 * Key used for storing the detail data member in a bundle when onSaveInstanceState() is called.
	 */
	final private static String KEY_PAYEE_DETAIL = "new-payee";
	/**
	 * Key used for storing the payeeSearchResult data member in a bundle when onSaveInstanceState() is called.
	 */
	final private static String KEY_SEARCH_RESULT = "search-result";
		
	
	@Override
	public View onCreateView(final LayoutInflater inflater, final ViewGroup container,
			final Bundle savedInstanceState) {

		/**Check if an Unverified Managed Payee was passed from Add Payee - Step 3 BankSearchSelectPayeeFragment*/
		bundle = this.getArguments();
			
		if( null != savedInstanceState ) {
			detail = (AddPayeeDetail)bundle.getSerializable(KEY_PAYEE_DETAIL);
			payeeSearchResult = (SearchPayeeResult)bundle.getSerializable(KEY_SEARCH_RESULT);
		} else {
			initializeData( bundle );
		}
		
		final View view = super.onCreateView(inflater, container, savedInstanceState);

		/**initialize content to be displayed on fragment*/
		initializeUi(view);


		return view;
	}
	
	/**
	 * Method used to initialize the views in the layout used for this fragment.
	 * 
	 * @param mainView Reference to the parent view provided in onCreateView
	 */
	protected void initializeUi(final View mainView) {
		/**Hide top note by default**/
		final TextView topNote = (TextView)mainView.findViewById(R.id.top_note_text);
		topNote.setVisibility(View.GONE);
		hideBottomNote();

		/**Setup Progress Indicator to show Payment Details and Payment Scheduled, on step 1, and hide step 2 **/
		BankHeaderProgressIndicator progressIndicator = getProgressIndicator();
		progressIndicator.initialize(0);
		progressIndicator.hideStepTwo();
		progressIndicator.setTitle(R.string.bank_payee_details, R.string.bank_payee_added, R.string.bank_payee_added);

		if( isUpdate ) {
			getActionButton().setText(R.string.bank_save_payee);
		} else {
			getActionButton().setText(R.string.bank_add_payee);
		}

		getActionLink().setText(R.string.bank_add_cancel);
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
	protected boolean canProceed() {
		boolean ret = true;
		List<?> content = getContent();
		
		/**Iterate through each BankEditDetail object and make sure their editable field validates correctly*/
		if( content != null){
			for(final Object element : content) {
				if( element instanceof BankEditDetail) {
					((BankEditDetail)element).enableValidation(true);
					
					ret = ((BankEditDetail)element).getEditableField().isValid();
					
					((BankEditDetail)element).enableValidation(false);
					
					if( !ret ) {					
						break;
					}
				}
			}
		}
		
		return ret;
	}

	/**
	 * Shows inline errors for all BankEditDetail objects if fields do not validate correctly.
	 */
	protected void updateFieldsAppearance() {
		List<?> content = getContent();
		/**Iterate through each BankEditDetail and ensure it validates correctly otherwise show inline errors*/
		if( content != null){
			for(final Object element : content) {
				if( element instanceof BankEditDetail) {
					/**Enable validation to be able to show inline errors and validate text*/
					((BankEditDetail)element).enableValidation(true);
					
					((BankEditDetail)element).setEditMode(false);
					((BankEditDetail)element).getEditableField().updateAppearanceForInput();
					
					/**Disable validation to improve performance while typing*/
					((BankEditDetail)element).enableValidation(false);
				}
			}
		}
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
			
			BankServiceCallFactory.createAddPayeeRequest(getPayeeDetail(), isUpdate).submit();
		} else {
			
			/**Scroll screen to top first field with inline error*/
			final int firstItemWithError = getFindError();
			if( firstItemWithError >= 0 ) {
				getFieldDetail(firstItemWithError).getEditableField().requestFocus();
			}
			
			updateFieldsAppearance();			
		}
	}
	
	/**
	 * Method used to return the index of the first item with an inline error
	 * 
	 * @return -1 if no line errors, otherwise the index of the item with an inline error.
	 */
	protected int getFindError() {
		int firstItemWithError = -1;
		List<?> content = getContent();
		
		/**Iterate through each BankEditDetail and ensure it validates correctly otherwise show inline errors*/
		if( content != null) {
			for( int i = 0; i < content.size(); i++ ) {
				final BankEditDetail detail = (BankEditDetail) content.get(i);
				
				detail.enableValidation(true);
				
				final boolean isValid = !detail.getEditableField().isValid();
				
				detail.enableValidation(false);
				
				if( isValid ) {
					firstItemWithError = i;
					break;
				}
			}
		}
		
		return firstItemWithError;
	}
	
	/**
	 * Method used to handle when the user clicks on cancel at the bottom of the screen.
	 */
	@Override
	protected void onActionLinkClick() {
		cancel();
	}
	
	/**
	 * Method used to cancel the Add payee workflow. Displays a modal to receive confirmation
	 * from user to canel or not.
	 */
	public void cancel() {
		final ModalDefaultOneButtonBottomView bottom = new ModalDefaultOneButtonBottomView(this.getActivity(), null);
		final ModalDefaultTopView top = new ModalDefaultTopView(this.getActivity(), null);
		final ModalAlertWithOneButton cancelModal = new ModalAlertWithOneButton(this.getActivity(), top, bottom);
		
		top.setTitle(R.string.bank_cancel_title);
		top.setContent(R.string.cancel_this_action_content);
		top.hideNeedHelpFooter();
		
		bottom.setButtonText(R.string.cancel_this_action);
		bottom.getButton().setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(final View v) {
				cancelModal.dismiss();
			    
				/**
				 * Pop all fragments till we reach BankManagePayee. 
				 * Checking if the Activity is null due to a crash occuring when the modal is open
				 * and you tap yes quickly the activity is null and the app will crash. 
				 */
				if ((BankNavigationRootActivity) getActivity() != null) {
					/**Check if user navigated to this screen from Manage Payees*/
					final boolean handled = ((BankNavigationRootActivity) getActivity()).popTillFragment(BankManagePayee.class);
					
					/**If was unable to navigate to Manage Payees then navigate to BankSelect Payee*/
					if( !handled ) {
						((BankNavigationRootActivity) getActivity()).popTillFragment(BankSelectPayee.class);
					}
				}
			}

		});
			
		this.showCustomAlertDialog(cancelModal);
	}


	/**
	 * Method Not Used
	 */
	@Override
	protected List<ViewPagerListItem> getViewPagerListContent() {
		return null;
	}
	
	@Override
	public void onPause() {
		super.onPause();
		
		onSaveInstanceState(getArguments());
	}

	/**
	 * Method used to store the state of the fragment and support orientation change
	 */
	@Override
	public void onSaveInstanceState(final Bundle outState) {
		super.onSaveInstanceState(outState);

		/**
		 * Use arguments for handling pause because the values are not cleared when fragment is rotated
		 * and it is not in the foreground. onSaveInstanceState gets called even if fragment is not in foreground.
		 */
		Bundle arguments = this.getArguments();
		if( arguments == null ) {
			arguments = outState;
		}
		
		/**Check if bundle is set to a value, if it is then fragment was paused while page was is in foreground*/
		final AddPayeeDetail curPayeeDetail = getPayeeDetail();
		if( bundle != null && null != curPayeeDetail ) {
			arguments.putSerializable(KEY_PAYEE_DETAIL, curPayeeDetail);
		}

		/**Check if bundle is set to a value, if it is then fragment was paused while page was is in foreground*/
		if( bundle != null && null != payeeSearchResult) {
			arguments.putSerializable(KEY_SEARCH_RESULT, payeeSearchResult);
		}

		if( arguments != null ) {
			List<?> content = getContent();
			/**Store the state of the editable fields, to re-open keyboard on orientation change if necessary*/
			if( content != null ) {
				for( final Object object : content) {
					if( object instanceof BankEditDetail ) {
						final BankEditDetail item = (BankEditDetail)object;
						
						final boolean hasFocus = item.getEditableField().hasFocus();
						final String key = item.getTopLabel().getText().toString();
						arguments.putBoolean(key, hasFocus );
						
						/**If has an error then show it on rotation */
						if( item.getEditableField().isInErrorState ) {
							arguments.putString(key +KEY_ERROR_EXT, item.getEditableField().getErrorLabel().getText().toString());
						}
						
						if( getGeneralError().getVisibility() == View.VISIBLE ) {
							arguments.putString(KEY_ERROR_EXT, getGeneralError().getText().toString());
						}
					}
				}
			}
		}
	}

	protected void updateUi() {
		/**Restore the state of the editable fields and re-open keyboard if either of them had focus*/
		if( bundle != null  ) {
			for( final Object object : getContent()) {
				if( object instanceof BankEditDetail ) {
					
					final BankEditDetail item = (BankEditDetail)object;
					final String key = item.getTopLabel().getText().toString();
					final boolean hasFocus  = bundle.getBoolean(key, false);
					final String errorString = bundle.getString(key +KEY_ERROR_EXT);
					final String genError = bundle.getString(KEY_ERROR_EXT);
					
					
					if( hasFocus || !Strings.isNullOrEmpty(errorString) || 
						(!Strings.isNullOrEmpty(genError) && getGeneralError().getVisibility() == View.VISIBLE) ) {
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
									
									/**Enable validation to be able to show in-line errors and validate text*/
									item.enableValidation(true);
																		
									item.getEditableField().showErrorLabel(errorString);
									
									/**Disable validation to improve performance while typing*/
									item.enableValidation(false);
								}
								
								showGeneralError(genError);
							}
						}, 1000);
					}
				}
			}
		}
	}

	@Override
	public void onResume() {
		super.onResume();
		
		restoreState(bundle);
		
		updateUi();
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
		cancel();
	}

	@Override
	public boolean isBackPressDisabled() {
		return true;
	}
	
	/**
	 * Method used to clear any inline errors on the page.
	 */
	public void clearErrors() {
		clearGeneralError();
		
		for( final Object object : getContent()) {
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
	public void setErrorString(final int field, final String text) {
		List<?> content = getContent();
		if( content != null && field < content.size() ) {
			final BankEditDetail detail = getFieldDetail(field);
			
			if( detail != null ) {
				detail.setEditModeNoFocus(true);
				
				detail.getEditableField().showErrorLabel(text);
			}
		}
	}
	
	/**
	 * Method used to read a BankEditDetail widget from the content table.
	 * 
	 * @param field Specifies what widget to read from the content table
	 * 
	 * @return Returns the widget from the content table on the page specified by the field identifier.
	 */
	public BankEditDetail getFieldDetail(final int field)  {
		BankEditDetail ret = null;
		List<?> content = getContent();
		
		if( content != null && field < content.size() ) {
			ret =  ((BankEditDetail)content.get(field)); 
		}
		
		
		return ret;
	}

	
	@Override
	protected void helpMenuOnClick(final HelpWidget help) {
		help.showHelpItems(HelpMenuListFactory.instance().getPayBillsHelpItems());
		
	}
	
	/**
	 * Method used to set the text for a field.
	 * 
	 * @param index Index to the field in the content layout whose text is to be changed
	 * @param text Reference to a string used to set the field found with the index specified.
	 */
	public void setFieldText( final int index, final String text ) {
		final BankEditDetail field = getFieldDetail(index);
		
		if( null != text && field != null) {
			field.setText(text);
		}
	}

	/**
	 * Method used to get the text for a field.
	 * 
	 * @param index Index to the field in the content layout whose text is to be retrieved
	 */
	public String getFieldText( final int index ) {
		String ret = "";
		
		final BankEditDetail field = getFieldDetail(index);
		
		if(  field != null) {
			ret = field.getText().toString();
		}
		
		return ret;
	}
	
	@Override
	public boolean handleError(final BankErrorResponse msgErrResponse) {
		/**Scroll screen to top first field with inline error*/
		final int firstItemWithError = getFindError();
		if( firstItemWithError >= 0 ) {
			getFieldDetail(firstItemWithError).getEditableField().requestFocus();
		}
		
		return false;
	}
	
	/**
	 * Method called in onCreateView to initialize the data model.
	 * 
	 * @param bundle Reference to the bundle retrieved via getArguments();
	 */
	protected abstract void initializeData( Bundle bundle );
	
	/**
	 * Generates an AddPayeeDetail object using the text values stored in each BankEditDetail that is
	 * part of the content list.
	 * 
	 * @return Reference to an AddPayeeDetail object with information of the Payee that is to be added.
	 */
	protected abstract AddPayeeDetail getPayeeDetail();
	
	/**
	 * Restores the widget's states from before rotation.
	 * 
	 * @param savedInstanceState the Bundle from which the data is loaded.
	 */
	protected abstract void restoreState(final Bundle bundle);
}
