package com.discover.mobile.section.home;

import java.util.ArrayList;
import java.util.List;

import roboguice.RoboGuice;
import roboguice.inject.InjectView;
import android.opengl.Visibility;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.discover.mobile.R;
import com.discover.mobile.BaseFragment;
import com.discover.mobile.common.CommonMethods;
import com.discover.mobile.common.CurrentSessionDetails;
import com.discover.mobile.common.auth.AccountDetails;
import com.discover.mobile.views.GeneralListItemAdapter;
import com.discover.mobile.views.GeneralListItemModel;

public class HomeSummaryFragment extends BaseFragment {

	private View view;

	@Override
	public View onCreateView(final LayoutInflater inflater,
			final ViewGroup container, final Bundle savedInstanceState) {
		view = inflater.inflate(R.layout.section_account_summary_landing, null);

		setupHomeElements();

		return view;
	}

	/**
	 * Builds the four elements (Current balance, last statement, cashback
	 * balance, & cashback offer) on the Home view.
	 */
	private void setupHomeElements() {

		final AccountDetails accountDetails = CurrentSessionDetails
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
	private void setupCurrentBalanceElement(AccountDetails accountDetails) {
		LinearLayout currentBalance = (LinearLayout) view
				.findViewById(R.id.home_current_balance);

		// main content
		((TextView) currentBalance.findViewById(R.id.title))
				.setText(R.string.current_balance);
		((TextView) currentBalance.findViewById(R.id.content_text))
				.setText(getString(R.string.dollar_sign)
						+ accountDetails.currentBalance);

		// subsection
		((TextView) currentBalance.findViewById(R.id.bottom_bar_label))
				.setText(getString(R.string.credit_available));
		((TextView) currentBalance.findViewById(R.id.bottom_bar_value))
				.setText(getString(R.string.dollar_sign)
						+ accountDetails.availableCredit);

		// Pay button
		((TextView) currentBalance.findViewById(R.id.blue_button_text))
				.setText(getString(R.string.pay_blue_button_text));

	}

	/**
	 * Formats and fills-out the element associated with the user's Last
	 * Statement Balance.
	 * 
	 * @param accountDetails
	 */
	private void setupLastStatementElement(AccountDetails accountDetails) {
		LinearLayout lastStatement = (LinearLayout) view
				.findViewById(R.id.home_last_statement);

		// main content
		((TextView) lastStatement.findViewById(R.id.title))
				.setText(R.string.last_statement_balance);
		((TextView) lastStatement.findViewById(R.id.content_text))
				.setText(getString(R.string.dollar_sign)
						+ accountDetails.statementBalance);

		// subsection
		((TextView) lastStatement.findViewById(R.id.bottom_bar_label))
				.setText(formatMinPaymentTitle(accountDetails));
		((TextView) lastStatement.findViewById(R.id.bottom_bar_value))
				.setText(getString(R.string.dollar_sign)
						+ accountDetails.minimumPaymentDue);

		// Pay button
		((TextView) lastStatement.findViewById(R.id.blue_button_text))
				.setText(R.string.view_blue_button_text);
	}

	/**
	 * Formats and fills-out the element associated with the user's
	 * Cashback/Miles Bonus Balance.
	 * 
	 * @param accountDetails
	 */
	private void setupBonusBalance(AccountDetails accountDetails) {
		LinearLayout bonusBalance = (LinearLayout) view
				.findViewById(R.id.home_bonus_balance);

		// main content
		((TextView) bonusBalance.findViewById(R.id.title))
				.setText(R.string.cashback_bonus_balance);
		((TextView) bonusBalance.findViewById(R.id.content_text))
				.setText(getString(R.string.dollar_sign)
						+ accountDetails.earnRewardAmount);

		// subsection
		((TextView) bonusBalance.findViewById(R.id.bottom_bar_label))
				.setText(getString(R.string.newly_earned));
		((TextView) bonusBalance.findViewById(R.id.bottom_bar_value))
				.setText(getString(R.string.dollar_sign)
						+ accountDetails.newlyEarnedRewards);

		// Pay button
		((TextView) bonusBalance.findViewById(R.id.blue_button_text))
				.setText(R.string.redeem_blue_button_text);
	}

	/**
	 * Formats and fills-out the element associated with the user's
	 * Cashback/Miles Bonus Offer.
	 * 
	 * @param accountDetails
	 */
	private void setupBonusOffer(AccountDetails accountDetails) {
//		RelativeLayout bonusOffer = (RelativeLayout) view
//				.findViewById(R.id.home_bonus_offer);

//		LinearLayout subSection = (LinearLayout) bonusOffer
//				.findViewById(R.id.general_list_item_subsection);
//		View divider = (View) bonusOffer
//				.findViewById(R.id.general_list_item_separator_line);
//		View vertDivider = (View) bonusOffer
//				.findViewById(R.id.divider_line);
		
//		subSection.setVisibility(View.GONE);
//		divider.setVisibility(View.GONE);
	}

	/**
	 * Formats the subtext String associated with the Last Statement. This
	 * String is dynamic due to the date.
	 * 
	 * @param accountDetails
	 * @return Formatted title with due date.
	 */
	private String formatMinPaymentTitle(AccountDetails accountDetails) {
		StringBuilder sb = new StringBuilder();
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
