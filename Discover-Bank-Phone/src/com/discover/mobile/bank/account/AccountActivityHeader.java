package com.discover.mobile.bank.account;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.discover.mobile.bank.BankExtraKeys;
import com.discover.mobile.bank.R;
import com.discover.mobile.bank.framework.BankUser;
import com.discover.mobile.bank.services.account.Account;
import com.discover.mobile.bank.services.json.Money;
import com.discover.mobile.bank.services.json.Percentage;
import com.discover.mobile.bank.ui.Animator;
import com.discover.mobile.bank.ui.ExpandCollapseAnimation;
import com.discover.mobile.bank.ui.table.TableTitles;
import com.discover.mobile.bank.ui.widgets.StatusMessageView;
import com.discover.mobile.bank.util.BankStringFormatter;
import com.discover.mobile.common.ui.table.TableButtonGroup;
import com.discover.mobile.common.ui.table.TableHeaderButton;
import com.google.common.base.Strings;

/**
 * Header displayed at the top of the activity table view screen
 * @author jthornton
 *
 */
public class AccountActivityHeader extends RelativeLayout{

	/**Integer values representing the locations of the buttons in the group*/
	private static final int ACTIVITY_BUTTON = 0;
	private static final int SCHEDULED_BUTTON = 1;
	private static final int EXPAND_ANIMATION_DURATION = 500;

	/**Layout view*/
	private final View view;

	/**Value holding the checking name*/
	private final TextView checking;

	/**Value holding the available balance*/
	private final TextView availableBalance;

	/**Available balance Label*/
	private final TextView availableBalanceLabel;

	/**VAlue holding the type of account in header*/
	private final TextView type;
	
	/**Label to show APY**/
	private final TextView apyOrPmtLabel;
	
	/**Value of current APY rate value*/
	private final TextView apyOrPmtValue;

	/**Label to show interest rate**/
	private final TextView interestRateOrTotalDueLabel;
	
	/**Value of current interest rate*/
	private final TextView interestRateOrTotalDueValue;
	
	/** Label to show interest last statement description**/
	private final TextView lastIntOrLastPmtLbl;
	
	/** Text view that shows the interest last statement */
	private final TextView lastIntOrLastPmtVal;
	
	/** Label to show YTD description **/
	private final TextView interestYTDLabel;
	
	/** Text view that shows the YTD value */
	private final TextView interestYTDValue;
	
	/** Label to show Maturity Date description **/
	private final TextView maturityDate;
	
	/** Text view that shows the M value */
	private final TextView maturityDateValue;
	
	/** Reference to line break displayed to separate information */
	private final View lineBreak;
	
	/**Current Balance Label*/
	private final TextView currentBalanceLabel;

	/**Value holding the current balance*/
	private final TextView currentBalance;

	/**Title of the header*/
	private final TextView title;

	/**Group holding the buttons*/
	final TableButtonGroup group;

	/**Boolean set to true if the user id viewing posted activity*/
	private boolean isPosted = true;

	/**Boolean set to true if the header is expanded*/
	private boolean isHeaderExpanded = false;

	/**Current int representing the sort order*/
	private int sortOrder = BankExtraKeys.SORT_DATE_DESC;

	/**Table titles in the view*/
	private final TableTitles titles;

	/**View holding labels*/
	private final RelativeLayout labels;

	/**Current Account*/
	private Account account;

	/**Status Message View*/
	private final StatusMessageView status;

	/**Duration of the status message*/
	private final int STATUS_MESSAGE_DURATION = 5000;


	
	/**
	 * Constructor of the class
	 * @param context - activity context
	 * @param attrs - attributes to give to the layout
	 */
	public AccountActivityHeader(final Context context, final AttributeSet attrs) {
		super(context, attrs);

		view = LayoutInflater.from(context).inflate(R.layout.bank_account_activity_header, null);
		checking = (TextView)view.findViewById(R.id.value1);
		availableBalance = (TextView) view.findViewById(R.id.value3);
		availableBalanceLabel = (TextView) view.findViewById(R.id.lable3);
		currentBalanceLabel = (TextView) view.findViewById(R.id.lable2);
		currentBalance = (TextView) view.findViewById(R.id.value2);
		interestRateOrTotalDueValue = (TextView) view.findViewById(R.id.interest_or_totaldue_value);
		interestRateOrTotalDueLabel = (TextView) view.findViewById(R.id.interest_or_totaldue_label);
		title = (TextView) view.findViewById(R.id.title_text);
		group = (TableButtonGroup) view.findViewById(R.id.buttons);
		labels = (RelativeLayout) view.findViewById(R.id.header_labels);
		type = (TextView)view.findViewById(R.id.lable1);
		titles = (TableTitles) view.findViewById(R.id.table_titles);
		status = (StatusMessageView) view.findViewById(R.id.status);
		apyOrPmtLabel = (TextView) view.findViewById(R.id.apy_or_pmt_label);
		apyOrPmtValue = (TextView) view.findViewById(R.id.apy_or_pmt_value);
		lastIntOrLastPmtLbl = (TextView) view.findViewById(R.id.lastinterest_or_lastpmt_label);
		lastIntOrLastPmtVal = (TextView) view.findViewById(R.id.lastinterest_or_lastpmt_value);
		interestYTDLabel = (TextView) view.findViewById(R.id.ytd_label);
		interestYTDValue = (TextView) view.findViewById(R.id.ytd_last_value);
		maturityDate = (TextView) view.findViewById(R.id.maturity_date_label);
		maturityDateValue = (TextView) view.findViewById(R.id.maturity_date_value);
		lineBreak = view.findViewById(R.id.line_break);
		
		titles.setLabel1(getResources().getString(R.string.recent_activity_date));
		titles.setLabel2(getResources().getString(R.string.recent_activity_description));
		titles.setLabel3(getResources().getString(R.string.recent_activity_amount));

		title.setOnClickListener(getTitleOnClickListener());
		account = BankUser.instance().getCurrentAccount();

		refreshAccountInfo();
		setSelectedCategory(isPosted);

		labels.getLayoutParams().height = 0;
		labels.setVisibility(View.GONE);
		addView(view);
	}

	/**
	 * Add the account and display it 
	 * @param account - account to add
	 */
	public final void refreshAccountInfo(){
		if(null == account){return;}

		hideAllInfo();

		title.setText(account.nickname);
		type.setText(account.getFormattedName());
		checking.setText(account.accountNumber.formatted);	

		/** If current balance is not provided do not show */
		showMoney(currentBalanceLabel, currentBalance, account.currentBalance);

		/** Available Balance Should be hidden for CDs and Personal Loans. */
		if (!account.type.equalsIgnoreCase(Account.ACCOUNT_LOAN) && !account.type.equalsIgnoreCase(Account.ACCOUNT_CD)) {
			showMoney(availableBalanceLabel, availableBalance, account.balance);
		} else if (account.type.equalsIgnoreCase(Account.ACCOUNT_LOAN)) {
			availableBalanceLabel.setText(R.string.activity_original_balance);
			showMoney(availableBalanceLabel, availableBalance, account.originalBalance);
		}
		
		/** Only show maturity date if a CD */
		if (account.type.equalsIgnoreCase(Account.ACCOUNT_CD)) {
			showDate(maturityDate, maturityDateValue, account.matureDatae);
		}

		boolean showLineBreak = false;

		/** Show interest rates and additional info for MMA, CD, and Savings Accounts only */
		if (account.getGroupCategory().equalsIgnoreCase(Account.ACCOUNT_SAVINGS)) {

			showLineBreak |= showPercentage(apyOrPmtLabel, apyOrPmtValue, account.apy);

			showLineBreak |= showPercentage(interestRateOrTotalDueLabel, interestRateOrTotalDueValue, account.interestRate);

			showLineBreak |= showMoney(lastIntOrLastPmtLbl, lastIntOrLastPmtVal, account.interestEarnedLastStatement);
			
			showLineBreak |= showMoney(interestYTDLabel, interestYTDValue, account.interestYearToDate);

		}
		/** Show additional info for personal loans */
		else if (account.getGroupCategory().equalsIgnoreCase(Account.ACCOUNT_LOAN)) {
			apyOrPmtLabel.setText(R.string.activity_pmt_due_date);
			showLineBreak |= showDate(apyOrPmtLabel, apyOrPmtValue, account.nextPaymentDueDate);

			interestRateOrTotalDueLabel.setText(R.string.activity_pmt_due_date);
			showLineBreak |= showMoney(interestRateOrTotalDueLabel, interestRateOrTotalDueValue, account.totalAmountDue);

			lastIntOrLastPmtLbl.setText(R.string.activity_last_pmt);
			showLineBreak |= showMoney(lastIntOrLastPmtLbl, lastIntOrLastPmtVal, account.lastPaymentReceivedAmount);
		}

		/** Show the line break only if any additional info was displayed */
		if (showLineBreak) {
			lineBreak.setVisibility(View.VISIBLE);
		} else {
			lineBreak.setVisibility(View.GONE);
		}
	}

	/**
	 * Method used to hide all account information displayed by this header.
	 */
	private void hideAllInfo() {
		availableBalance.setVisibility(View.GONE);
		availableBalanceLabel.setVisibility(View.GONE);
		currentBalanceLabel.setVisibility(View.GONE);
		currentBalance.setVisibility(View.GONE);
		apyOrPmtLabel.setVisibility(View.GONE);
		apyOrPmtValue.setVisibility(View.GONE);
		interestRateOrTotalDueLabel.setVisibility(View.GONE);
		interestRateOrTotalDueValue.setVisibility(View.GONE);
		lastIntOrLastPmtLbl.setVisibility(View.GONE);
		lastIntOrLastPmtVal.setVisibility(View.GONE);
		interestYTDLabel.setVisibility(View.GONE);
		interestYTDValue.setVisibility(View.GONE);
		maturityDate.setVisibility(View.GONE);
		maturityDateValue.setVisibility(View.GONE);
	}

	/**
	 * Method used to display Percentage information in a text view. In addition it makes the views visible if valid
	 * information provided, otherwise sets them to gone, so that they are not seen in the event the information is not
	 * provided.
	 * 
	 * @param textLabel
	 *            TextView used to describe the information displayed in textValue
	 * @param textValue
	 *            TextView used to display the formatted data in value
	 * @param value
	 *            Reference to a Money object
	 * 
	 * @return True if the data in value is valid and is displayed, false otherwise.
	 */
	private boolean showPercentage(final TextView textLabel, final TextView textValue, final Percentage value) {
		boolean shown = false;

		if (value != null && !Strings.isNullOrEmpty(value.formatted)) {
			textValue.setText(value.formatted);
			textLabel.setVisibility(View.VISIBLE);
			textValue.setVisibility(View.VISIBLE);

			shown = true;
		} else {
			textLabel.setVisibility(View.GONE);
			textValue.setVisibility(View.GONE);
		}

		return shown;
	}

	/**
	 * Method used to display Money information in a text view. In addition it makes the views visible if valid
	 * information provided, otherwise sets them to gone, so that they are not seen in the event the information is not
	 * provided.
	 * 
	 * @param textLabel
	 *            TextView used to describe the information displayed in textValue
	 * @param textValue
	 *            TextView used to display the formatted data in value
	 * @param value
	 *            Reference to a Money object
	 * 
	 * @return True if the data in value is valid and is displayed, false otherwise.
	 */
	private boolean showMoney(final TextView textLabel, final TextView textValue, final Money value) {
		boolean shown = false;

		if (value != null && !Strings.isNullOrEmpty(value.formatted)) {
			textValue.setText(value.formatted);
			textLabel.setVisibility(View.VISIBLE);
			textValue.setVisibility(View.VISIBLE);
			shown = true;
		} else {
			textLabel.setVisibility(View.GONE);
			textValue.setVisibility(View.GONE);
		}

		return shown;
	}
	
	/**
	 * Method used to display Date information in a text view. In addition it makes the views visible if valid
	 * information provided, otherwise sets them to gone, so that they are not seen in the event the information is not
	 * provided.
	 * 
	 * @param textLabel
	 *            TextView used to describe the information displayed in textValue
	 * @param textValue
	 *            TextView used to display the formatted data in value
	 * @param value
	 *            Reference to a Money object
	 * 
	 * @return True if the data in value is valid and is displayed, false otherwise.
	 */

	private boolean showDate(final TextView textLabel, final TextView textValue, final String value) {
		boolean shown = false;

		if (!Strings.isNullOrEmpty(value)) {
			textValue.setText(BankStringFormatter.convertFromISO8601Date(value));
			textLabel.setVisibility(View.VISIBLE);
			textValue.setVisibility(View.VISIBLE);

			shown = true;
		} else {
			textLabel.setVisibility(View.GONE);
			textValue.setVisibility(View.GONE);
		}

		return shown;
	}

	/**
	 * Get image on click listener
	 * @return image on click listener
	 */
	public final OnClickListener getTitleOnClickListener(){
		return new OnClickListener(){
			@Override
			public void onClick(final View v){
				if (isHeaderExpanded()) {
					// Collapse
					collapse(true);
				}else{
					// Expand
					expand(true);
				}
			}
		};

	}

	/**
	 * @return the postedButton
	 */
	public TableHeaderButton getPostedButton() {
		return group.getButton(ACTIVITY_BUTTON);
	}

	/**
	 * @return the scheduledButton
	 */
	public TableHeaderButton getScheduledButton() {
		return group.getButton(SCHEDULED_BUTTON);
	}

	/**
	 * @return the isPosted
	 */
	public boolean isPosted() {
		return isPosted;
	}

	/**
	 * @param isPosted the isPosted to set
	 */
	public void setPosted(final boolean isPosted) {
		this.isPosted = isPosted;
	}

	/**
	 * @return the isHeaderExpanded
	 */
	public boolean isHeaderExpanded() {
		return isHeaderExpanded;
	}

	/**
	 * Method used to expand this class's layout to show all information for the represented account.
	 */
	public final void expand(final boolean animate) {

		if (!isHeaderExpanded) {
			final View arrow = findViewById(R.id.arrow);

			if (animate) {
				arrow.startAnimation(Animator.createRotationAnimation(true, EXPAND_ANIMATION_DURATION + 100));
				labels.startAnimation(new ExpandCollapseAnimation(labels, true, EXPAND_ANIMATION_DURATION));
			} else {
				arrow.startAnimation(Animator.createRotationAnimation(true, 0));
				labels.startAnimation(new ExpandCollapseAnimation(labels, true, 0));
			}
		}

		this.isHeaderExpanded = true;
	}

	/**
	 * Method used to collapse this class's layout to hide all account information.
	 */
	public final void collapse(final boolean animate) {

		if(isHeaderExpanded){
			final View arrow = findViewById(R.id.arrow);
			if (animate) {
				arrow.startAnimation(Animator.createRotationAnimation(false, EXPAND_ANIMATION_DURATION + 100));
				labels.startAnimation(new ExpandCollapseAnimation(labels, false, EXPAND_ANIMATION_DURATION));
			} else {
				arrow.startAnimation(Animator.createRotationAnimation(false, 0));
				labels.startAnimation(new ExpandCollapseAnimation(labels, true, 0));
			}
		}

		this.isHeaderExpanded = false;
	}



	/**
	 * Get the sort order
	 * @return the sort order
	 */
	public int getSortOrder(){
		return sortOrder;
	}

	/**
	 * Set the sort order
	 * @param order - sort order to set
	 */
	public void setSortOrder(final int order){
		sortOrder = order;
	}

	/**
	 * Return the selected category
	 * @return the selected category
	 */
	public boolean getSelectedCategory() {
		return isPosted;
	}

	/**
	 * Set the message in the table titles
	 * @param - message to set in the titles
	 */
	public void setMessage(final String message){
		titles.setMessage(message);
	}

	/**
	 * Clear the message in the titles
	 */
	public void clearMessage(){
		titles.hideMessage();
	}

	/**
	 * Set the selected category
	 * @param the selected category
	 */
	public final void setSelectedCategory(final boolean isPosted) {
		setPosted(isPosted);
		if(isPosted){
			group.setButtonSelected(ACTIVITY_BUTTON);
		}else{
			group.setButtonSelected(SCHEDULED_BUTTON);
		}
	}

	/**
	 * Displays a message and animates it away.
	 * 
	 * @param messageToDisplay - the String to display.
	 */
	public void showStatusMessage(final String messageToDisplay){
		status.setText(messageToDisplay);
		status.showAndHide(STATUS_MESSAGE_DURATION);
	}

	/**
	 * Method used to retrieve the account associated with the header.
	 * 
	 * @return Reference to an account object type
	 */
	public Account getAccount() {
		return account;
	}

	/**
	 * Method used to set the account data information that is to be displayed by this UI element.
	 * 
	 * @param account
	 *            - Reference to an Account object.
	 */
	public void setAccount(final Account account) {
		this.account = account;

		refreshAccountInfo();
	}

	/**
	 * Set the observers to the group
	 * @param observer - observer of the buttons
	 */
	public void setGroupObserver(final OnClickListener observer){
		group.addObserver(observer);
	}

	/**
	 * Check to see if the posted button is currently selected
	 * @return if the posted button is currently selected
	 */
	public boolean isPostedSelected(){
		return getPostedButton().isSelected();
	}

	/**
	 * Notify the group of buttons that the current
	 * observer should be removed. This helps prevent memory leaks.
	 */
	public void removeListeners(){
		group.removeObserver();
	}



}
