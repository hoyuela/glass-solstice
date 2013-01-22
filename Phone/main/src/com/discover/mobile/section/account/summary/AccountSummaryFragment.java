package com.discover.mobile.section.account.summary;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.discover.mobile.BaseFragment;
import com.discover.mobile.R;
import com.discover.mobile.alert.ModalAlertWithOneButton;
import com.discover.mobile.alert.ModalDefaultOneButtonBottomView;
import com.discover.mobile.alert.ModalDefaultTopView;
import com.discover.mobile.common.CurrentSessionDetails;
import com.discover.mobile.common.account.summary.GetLatePaymentWarning;
import com.discover.mobile.common.account.summary.GetLatePaymentWarningText;
import com.discover.mobile.common.account.summary.LatePaymentWarningDetail;
import com.discover.mobile.common.account.summary.LatePaymentWarningTextDetail;
import com.discover.mobile.common.auth.AccountDetails;
import com.discover.mobile.common.callback.AsyncCallback;
import com.discover.mobile.common.callback.GenericAsyncCallback;

/**
 * Fragment holding the account summary information for both miles and cashback users
 * @author jthornton
 *
 */
public class AccountSummaryFragment extends BaseFragment {
	
	/**List holding the summary rows*/
	private LinearLayout accountSummaryList;
	
	/**Button used to make a payment*/
	private Button makePayment;
	
	/**Hyperlink used to provide feedback*/
	private TextView feedback;
	
	/**Account information object*/
	private AccountDetails info;
	
	/**Static string representing the dollar sign*/
	private static final String DOLLAR = "$";
	
	/**Static string representing the date divider*/
	private static final String DATE_DIV = "/";
	
	/**Static string representing the cash back bonus incentive type code*/
	private static final String CASH_BACK_CODE = "CBB";

	/**Static string representing the miles incentive type code*/
	private static final String MILES_CODE = "MI2";
	
	/**Index of the string where the month begins*/
	private static final int MONTH_BEGIN = 0;

	/**Index of the string where the month ends*/
	private static final int MONTH_END = 2;
	
	/**Index of the string where the day begins*/
	private static final int DAY_BEGIN = 3;
	
	/**Index of the string where the day end*/
	private static final int DAY_END = 5;

	/**Index of the string where the year begins*/
	private static final int YEAR_BEGIN = 6;

	/**Index of the string where the year ends*/
	private static final int YEAR_END = 8;
	
	/**Static string representing a 0 values balance*/
	private static final String NO_BALANCE = "0.00";
	
	/**Static static string representing an empty string*/
	private static final String EMPTY = "";
	
	/**Object holding all of the possible strings to be displayed in the modal*/
	private LatePaymentWarningTextDetail text;
	
	/**Details to be put into the string displayed in the modal*/
	private LatePaymentWarningDetail details;
	
	/**
	 * Dialog used to show progress, note this has to be done here because their are two
	 * calls made back to back. So this will need to keep track of when to show the dialog.
	 */
	private ProgressDialog dialog;
	
	/**Activity context*/
	private Context context;
	
	/**
	 * Create the view
	 * @param inflater - inflater to inflate the layout
	 * @param container - container holding the group
	 * @param savedInstanceState - state of the fragment
	 */
	@Override
	public View onCreateView(final LayoutInflater inflater, final ViewGroup container,
			final Bundle savedInstanceState) {
		final View view = inflater.inflate(R.layout.account_summary, null);

		context = this.getActivity();
		accountSummaryList = (LinearLayout)view.findViewById(R.id.summary_list);
		makePayment = (Button)view.findViewById(R.id.make_payament);
		makePayment.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(final View v) {
				//TODO:  Implement this listener when the payment story is worked on
			}			
		});
		
		feedback = (TextView)view.findViewById(R.id.provide_feedback_button);
		feedback.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(final View v) {
				//TODO:  Implement this listener when the payment story is worked on
			}			
		});
		
		info = CurrentSessionDetails.getCurrentSessionDetails().getAccountDetails();
		
		if(NO_BALANCE.equals(info.currentBalance)){
			makePayment.setEnabled(false);
		}
		populateList();
		return view;
	}
	
	/**
	 * Populate the linear layout
	 */
	public void populateList(){
		final Resources res = this.getResources();
		
		accountSummaryList.addView(SimpleListItemFactory.createItem(context,
				res.getString(R.string.account_summary_current_balance), 
				convertToDollars(info.currentBalance)));
		
		accountSummaryList.addView(SimpleListItemFactory.createItem(context,
				res.getString(R.string.account_summary_last_statement), 
				convertToDollars(info.lastPaymentAmount)));
		
		final String minPaymentString = res.getString(R.string.account_summary_minimum_payment);
		accountSummaryList.addView(SimpleListItemFactory.createItem(context,
				String.format(minPaymentString, getDateString(info.paymentDueDate)), 
				convertToDollars(info.minimumPaymentDue),
				res.getString(R.string.account_summary_learn_more),
				getPaymentActionHandler()));
		
		final String lastPaymentString = res.getString(R.string.account_summary_last_payment);
		accountSummaryList.addView(SimpleListItemFactory.createItem(context,
				String.format(lastPaymentString, getDateString(info.lastPaymentDate)), 
				convertToDollars(info.lastPaymentAmount)));
		
		accountSummaryList.addView(SimpleListItemFactory.createItem(context,
				res.getString(R.string.account_summary_credit_available), 
				convertToDollars(info.availableCredit),
				res.getString(R.string.account_summary_learn_more),
				getCreditLineActionHandler()));
		
		if(NO_BALANCE.equals(info.statementBalance)){return;}
		
		if(CASH_BACK_CODE.equals(info.incentiveTypeCode)){
			accountSummaryList.addView(SimpleListItemFactory.createItem(context,
					res.getString(R.string.account_summary_cash_back_bonus), 
					convertToDollars(info.earnRewardAmount)));
		} else if(MILES_CODE.equals(info.incentiveTypeCode)){
			accountSummaryList.addView(SimpleListItemFactory.createItem(context,
					res.getString(R.string.account_summary_miles_bonus), 
					info.earnRewardAmount));
		}
		
	}

	/**
	 * Get the date string to be displayed
	 * @param dateString - string holding the date
	 * @return the string to be displayed 
	 */
	private String getDateString(final String dateString){
		return dateString.substring(MONTH_BEGIN, MONTH_END) + DATE_DIV + dateString.substring(YEAR_BEGIN, YEAR_END);
	}
	
	/**
	 * Get the date string to be displayed
	 * @param dateString - string holding the date
	 * @return the string to be displayed 
	 */
	private String getLongDateString(final String dateString){
		return dateString.substring(MONTH_BEGIN, MONTH_END) + DATE_DIV + 
				dateString.substring(DAY_BEGIN, DAY_END) + DATE_DIV +
				dateString.substring(YEAR_BEGIN, YEAR_END);
	}
	
	/**
	 * Convert the string amount to a dollar amount
	 * @param dollar - dollar amount
	 * @return the dollar amount in string form
	 */
	private String convertToDollars(final String dollar){
		return DOLLAR + dollar;
	}
	
	/**
	 * Return the integer value of the string that needs to be displayed in the title
	 */
	@Override
	public int getActionBarTitle() {
		return R.string.account_summary_title;
	}
	
	/**
	 * Return the credit line click handler
	 * @return the credit line click handler
	 */
	public OnClickListener getCreditLineActionHandler(){
		return new OnClickListener(){
			@Override
			public void onClick(final View v) {
				showCreditLineModal();
			}	
		};
	}
	
	/**
	 * Return the late payment warning click handler
	 * @return the late payment warning click handler
	 */
	public OnClickListener getPaymentActionHandler(){
		return new OnClickListener(){
			@Override
			public void onClick(final View v) {
				getLatePaymentInformation();
			}	
		};
	}
	
	/**
	 * Show the credit line modal
	 */
	protected void showCreditLineModal(){
		final ModalDefaultTopView top = new ModalDefaultTopView(context, null);
		final ModalDefaultOneButtonBottomView bottom = new ModalDefaultOneButtonBottomView(context, null);
		final ModalAlertWithOneButton modal = new ModalAlertWithOneButton(context, top, bottom);
		top.hideNeedHelpFooter();
		top.setTitle(R.string.credit_line_modal_title);
		top.setContent(R.string.credit_line_modal_text);
		top.showErrorIcon(false);
		bottom.setButtonText(R.string.account_summary_modal_button);
		bottom.getButton().setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(final View v) {modal.dismiss();}
		});
		super.showCustomAlertDialog(modal);
	}
	
	/**
	 * Get the late payment information (details only)
	 */
	protected void getLatePaymentInformation(){
		dialog = ProgressDialog.show(context, 
				getResources().getString(R.string.push_progress_get_title), 
				getResources().getString(R.string.push_progress_registration_loading), 
				true);
		
		//If this call has already been made, don't make it again
		if(null == details){
			final AsyncCallback<LatePaymentWarningDetail> callback = 
					GenericAsyncCallback.<LatePaymentWarningDetail>builder(this.getActivity())
					.withSuccessListener(new LatePaymentWarningSuccessListener(this))
					.withErrorResponseHandler(null)
					.build();
			
			new GetLatePaymentWarning(getActivity(), callback).submit();
			
		} else{
			getLatePaymentTextInformation();
		}	
	}
	
	/**
	 * Get the late payment text options (text only)
	 */
	protected void getLatePaymentTextInformation(){
		//If this call has already been made, don't make it again
		if(null == text){
			final AsyncCallback<LatePaymentWarningTextDetail> callback = 
					GenericAsyncCallback.<LatePaymentWarningTextDetail>builder(this.getActivity())
					.withSuccessListener(new LatePaymentWarningTextSuccessListener(this))
					.withErrorResponseHandler(null)
					.build();
			
			new GetLatePaymentWarningText(getActivity(), callback).submit();
		} else{
			showLatePaymentModal();
		}
	}
	
	/**
	 * Set the late payment detail object
	 * @param detail- the populate detail object
	 */
	public void prepLatePaymentModalInfo(final LatePaymentWarningDetail detail){
		this.details = detail;
	}
	
	/**
	 * Show the late payment warning modal
	 */
	protected void showLatePaymentModal(){
		dialog.dismiss();
		final String content = getContentString();
		final LatePaymentModalTop top = new LatePaymentModalTop(context, null);
		final ModalDefaultOneButtonBottomView bottom = new ModalDefaultOneButtonBottomView(context, null);
		final ModalAlertWithOneButton modal = new ModalAlertWithOneButton(context, top, bottom);
		top.setPaymentDate(getLongDateString(info.paymentDueDate));
		top.setDynamicContent(content);
		bottom.setButtonText(R.string.account_summary_modal_button);
		bottom.getButton().setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(final View v) {modal.dismiss();}
		});
		super.showCustomAlertDialog(modal);
	}

	/**
	 * Get the fully populated content string.  Include replacing the variable text
	 * @return the fully populate content string
	 */
	private String getContentString() {
		String content = text.getStringFromCode(details.penaltyAPRCode);
		
		if(null != content){
			content = content.replace(LatePaymentWarningTextDetail.START, EMPTY);
			content = content.replace(LatePaymentWarningTextDetail.PURCH_PENALTY_APR, 
					details.merchantAPR + LatePaymentWarningTextDetail.VARIABLE_END);
			
			content = content.replace(LatePaymentWarningTextDetail.PENALTY, details.aprTitle);
			content = content.replace(LatePaymentWarningTextDetail.LATE_FEE, details.lateFee);
		}
		return content;
	}

	/**
	 * Store the object that holds the detail information
	 * @param detail - populated text detail
	 */
	public void storeInfoStrings(final LatePaymentWarningTextDetail detail) {
		this.text = detail;	
	}
}
