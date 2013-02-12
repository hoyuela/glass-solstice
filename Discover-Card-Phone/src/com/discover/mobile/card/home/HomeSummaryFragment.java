package com.discover.mobile.card.home;

import java.text.NumberFormat;
import java.util.Locale;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.discover.mobile.card.CardSessionContext;
import com.discover.mobile.card.R;
import com.discover.mobile.common.AccountType;
import com.discover.mobile.common.BaseFragment;
import com.discover.mobile.common.CommonMethods;
import com.discover.mobile.common.Globals;
import com.discover.mobile.common.auth.AccountDetails;

public class HomeSummaryFragment extends BaseFragment {

	/* (non-Javadoc)
	 * @see com.discover.mobile.BaseFragment#onResume()
	 */
	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
	}

	private View view;

	@Override
	public View onCreateView(final LayoutInflater inflater,
			final ViewGroup container, final Bundle savedInstanceState) {
		view = inflater.inflate(R.layout.section_account_summary_landing, null);
		if (Globals.getCurrentAccount().equals(AccountType.CARD_ACCOUNT)){
			setupHomeElements();
			showActionBarLogo();
		}
		return view;
	}

	@Override
	public void onPause() {
		super.onPause();

		hideActionBarLogo();
	}

	/**
	 * Builds the four elements (Current balance, last statement, cashback
	 * balance, & cashback offer) on the Home view.
	 */
	private void setupHomeElements() {

		final AccountDetails accountDetails = CardSessionContext
				.getCurrentSessionDetails().getAccountDetails();

		setupCurrentBalanceElement(accountDetails);
		setupLastStatementElement(accountDetails);
		setupBonusBalance(accountDetails);
		setupBonusOffer(accountDetails);
	}

	/**
	 * Formats and fills-out the element associated with the user's Current
	 * Balance.
	 * 
	 * @param accountDetails
	 */
	private void setupCurrentBalanceElement(final AccountDetails accountDetails) {
		final LinearLayout currentBalance = (LinearLayout) view
				.findViewById(R.id.home_current_balance);
		final double currBalance = getDoublevalue(accountDetails.currentBalance);
		final double credAvailable = getDoublevalue(accountDetails.availableCredit);

		// main content
		((TextView) currentBalance.findViewById(R.id.title))
		.setText(R.string.current_balance);
		((TextView) currentBalance.findViewById(R.id.content_text))
		.setText(NumberFormat.getCurrencyInstance(Locale.US).format(currBalance));

		// subsection
		((TextView) currentBalance.findViewById(R.id.bottom_bar_label))
		.setText(getString(R.string.credit_available));
		((TextView) currentBalance.findViewById(R.id.bottom_bar_value))
		.setText(NumberFormat.getCurrencyInstance(Locale.US).format(
				credAvailable));

		// View button
		((TextView) currentBalance.findViewById(R.id.blue_button_text))
		.setText(getString(R.string.view_blue_button_text));

	}

	/**
	 * Formats and fills-out the element associated with the user's Last
	 * Statement Balance.
	 * 
	 * @param accountDetails
	 */
	private void setupLastStatementElement(final AccountDetails accountDetails) {
		final LinearLayout lastStatement = (LinearLayout) view
				.findViewById(R.id.home_last_statement);

		if(null == accountDetails.minimumPaymentDue){
			lastStatement.setVisibility(View.GONE);
			return;
		}
		final double lastBalance = getDoublevalue(accountDetails.statementBalance);
		final double minPayment = getDoublevalue(accountDetails.minimumPaymentDue);

		// main content
		((TextView) lastStatement.findViewById(R.id.title))
		.setText(R.string.last_statement_balance);
		((TextView) lastStatement.findViewById(R.id.content_text))
		.setText(NumberFormat.getCurrencyInstance(Locale.US).format(lastBalance));

		// subsection
		((TextView) lastStatement.findViewById(R.id.bottom_bar_label))
		.setText(formatMinimumPaymentTitle(accountDetails));
		((TextView) lastStatement.findViewById(R.id.bottom_bar_value))
		.setText(NumberFormat.getCurrencyInstance(Locale.US).format(minPayment));

		// Pay button
		((TextView) lastStatement.findViewById(R.id.blue_button_text))
		.setText(R.string.pay_blue_button_text);
	}

	private double getDoublevalue(final String amount){
		if(null == amount){
			return 0.00;
		} else{
			return Double.valueOf(amount);
		}
	}

	/**
	 * Formats and fills-out the element associated with the user's
	 * Cashback/Miles Bonus Balance.
	 * 
	 * @param accountDetails
	 */
	private void setupBonusBalance(final AccountDetails accountDetails) {
		final LinearLayout bonusBalance = (LinearLayout) view
				.findViewById(R.id.home_bonus_balance);

		// main content & subsection
		final boolean isCashback = CommonMethods.isCashbackCard(accountDetails);

		if (isCashback) {

			final double cashBonus = Double.valueOf(accountDetails.earnRewardAmount);
			final double newlyEarned = Double.valueOf(accountDetails.newlyEarnedRewards);

			((TextView) bonusBalance.findViewById(R.id.title))
			.setText(R.string.cashback_bonus_balance);
			((TextView) bonusBalance.findViewById(R.id.content_text))
			.setText(NumberFormat.getCurrencyInstance(Locale.US).format(
					cashBonus));

			((TextView) bonusBalance.findViewById(R.id.bottom_bar_label))
			.setText(getString(R.string.newly_earned));
			((TextView) bonusBalance.findViewById(R.id.bottom_bar_value))
			.setText(NumberFormat.getCurrencyInstance(Locale.US).format(
					newlyEarned));
		} else {
			((TextView) bonusBalance.findViewById(R.id.title))
			.setText(R.string.miles_balance);
			((TextView) bonusBalance.findViewById(R.id.content_text))
			.setText(CommonMethods
					.insertCommas(accountDetails.earnRewardAmount));

			((TextView) bonusBalance.findViewById(R.id.bottom_bar_label))
			.setText(getString(R.string.newly_earned));
			((TextView) bonusBalance.findViewById(R.id.bottom_bar_value))
			.setText(CommonMethods
					.insertCommas(accountDetails.newlyEarnedRewards));
		}

		// Redeem button
		if (!CommonMethods.isEscapeCard(accountDetails)) {
			((TextView) bonusBalance.findViewById(R.id.blue_button_text))
			.setText(R.string.redeem_blue_button_text);
		} else {
			((TextView) bonusBalance.findViewById(R.id.blue_button_text))
			.setVisibility(View.GONE);
		}
	}

	/**
	 * Formats and fills-out the element associated with the user's
	 * Cashback/Miles Bonus Offer.
	 * 
	 * @param accountDetails
	 */
	private void setupBonusOffer(final AccountDetails accountDetails) {
		final RelativeLayout bonusOffer = (RelativeLayout) view
				.findViewById(R.id.home_bonus_offer);

		// main content
		final boolean isCashback = CommonMethods.isCashbackCard(accountDetails);
		if (isCashback) {
			((TextView) bonusOffer.findViewById(R.id.title))
			.setText(R.string.cashback_bonus_offer);
		} else {
			((TextView) bonusOffer.findViewById(R.id.title))
			.setText(R.string.miles_offer);
		}

		((TextView) bonusOffer.findViewById(R.id.content_text))
		.setText(getString(R.string.cashback_miles_offer_subtext));

		// SignUp button
		((TextView) bonusOffer.findViewById(R.id.blue_button_text))
		.setText(R.string.sign_up_blue_button_text);
	}

	/**
	 * Formats the subtext String associated with the Last Statement. This
	 * String is dynamic due to the date.
	 * 
	 * @param accountDetails
	 * @return Formatted title with due date.
	 */
	private String formatMinimumPaymentTitle(final AccountDetails accountDetails) {
		if(null == accountDetails.paymentDueDate){return getString(R.string.min_payment_due);}
		final StringBuilder sb = new StringBuilder();
		sb.append(getString(R.string.min_payment_due));
		sb.append(' ');
		sb.append(accountDetails.paymentDueDate.substring(0, 2));
		sb.append('/');
		sb.append(accountDetails.paymentDueDate.substring(2, 4));
		sb.append(':');

		return sb.toString();
	}

	/**
	 * Return the integer value of the string that needs to be displayed in the
	 * title
	 */
	@Override
	public int getActionBarTitle() {
		return R.string.account_summary_title;
	}
}
