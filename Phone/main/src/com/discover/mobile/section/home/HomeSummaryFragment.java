package com.discover.mobile.section.home;

import java.util.ArrayList;
import java.util.List;

import roboguice.RoboGuice;
import roboguice.inject.InjectView;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.discover.mobile.R;
import com.discover.mobile.RoboSherlockFragment;
import com.discover.mobile.common.CurrentSessionDetails;
import com.discover.mobile.common.auth.AccountDetails;
import com.discover.mobile.views.GeneralListItemAdapter;
import com.discover.mobile.views.GeneralListItemModel;

public class HomeSummaryFragment extends RoboSherlockFragment {
	
	private final List<GeneralListItemModel> accountSummaryListItems = new ArrayList<GeneralListItemModel>();
	
	@InjectView(R.id.account_summary_items)
	private ListView accountSummaryList;

	@Override
	public void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		buildAccountSummaryListItems();
	}
	
	@Override
	public View onCreateView(final LayoutInflater inflater, final ViewGroup container,
			final Bundle savedInstanceState) {
		final View view = inflater.inflate(R.layout.section_account_summary_landing, null);
		
		final GeneralListItemAdapter generalListItemAdapter = 
				new GeneralListItemAdapter(getActivity(), accountSummaryListItems);
		
		/**
		 * Clearing the adapter before populating with new data. Since onCreateView will get called 
		 * every time you return to the home screen, this will allow the list to get updated with new data.
		 */
		generalListItemAdapter.clear();
		
		buildAccountSummaryListItems();
		ListView listView = (ListView) view.findViewById(R.id.account_summary_items);
		listView.setAdapter(generalListItemAdapter);
				
		return view;
	}

	private void buildAccountSummaryListItems() {
		
		final AccountDetails accountDetails = CurrentSessionDetails.getCurrentSessionDetails().getAccountDetails();
		if (accountDetails != null) {
			
			GeneralListItemModel generalListItem = createCurrentBalanceItem(accountDetails);
			accountSummaryListItems.add(generalListItem);
			generalListItem = createLastStatementBalanceItem(accountDetails);
			accountSummaryListItems.add(generalListItem);
			generalListItem = createCashbackBonusBalanceItem(accountDetails);
			accountSummaryListItems.add(generalListItem);
			
		} else {
			Log.e("test", "account details equals null");
		}
		
	}
	
	private GeneralListItemModel createCurrentBalanceItem(final AccountDetails accountDetails) {
		return new GeneralListItemModel() {{
			titleTextRes = R.string.current_balance;
			contentTextRes = getString(R.string.dollar_sign) + accountDetails.currentBalance;
			
			bottomBarModel = new BottomBarModel() {{
				labelTextRes = getString(R.string.credit_available);
				valueTextRes = getString(R.string.dollar_sign) + accountDetails.availableCredit;
			}};
			
			actionButtonModel = new ActionButtonModel() {{
				buttonTextRes = R.string.pay_blue_button_text;
			}};
		}};
	}
	
	private GeneralListItemModel createLastStatementBalanceItem(final AccountDetails accountDetails) {
		return new GeneralListItemModel() {{
			titleTextRes = R.string.last_statement_balance;
			contentTextRes = getString(R.string.dollar_sign) + accountDetails.statementBalance;
			
			bottomBarModel = new BottomBarModel() {{
				labelTextRes = getString(R.string.min_payment_due) + accountDetails.paymentDueDate;
				valueTextRes = getString(R.string.dollar_sign) + accountDetails.minimumPaymentDue;
			}};
			
			actionButtonModel = new ActionButtonModel() {{
				buttonTextRes = R.string.view_blue_button_text;
			}};
		}};
	}
	
	private GeneralListItemModel createCashbackBonusBalanceItem(final AccountDetails accountDetails) {
		return new GeneralListItemModel() {{
			titleTextRes = R.string.cashback_bonus_balance;
			contentTextRes = getString(R.string.dollar_sign) + accountDetails.earnRewardAmount;
			
			bottomBarModel = new BottomBarModel() {{
				labelTextRes = getString(R.string.newly_earned);
				valueTextRes = getString(R.string.dollar_sign) + accountDetails.newlyEarnedRewards;
			}};
			
			actionButtonModel = new ActionButtonModel() {{
				buttonTextRes = R.string.redeem_blue_button_text;
			}};
		}};
	}

	/**
	 * Return the integer value of the string that needs to be displayed in the title
	 */
	@Override
	public int getActionBarTitle() {
		return R.string.account_summary_title;
	}
}
