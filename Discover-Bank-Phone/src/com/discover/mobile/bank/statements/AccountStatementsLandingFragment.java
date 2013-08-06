package com.discover.mobile.bank.statements;

import java.util.ArrayList;
import java.util.List;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.actionbarsherlock.internal.widget.IcsAdapterView;
import com.actionbarsherlock.internal.widget.IcsAdapterView.OnItemSelectedListener;
import com.actionbarsherlock.internal.widget.IcsSpinner;
import com.discover.mobile.bank.R;
import com.discover.mobile.bank.framework.BankUser;
import com.discover.mobile.bank.paybills.SimpleChooseListItem;
import com.discover.mobile.bank.ui.fragments.BankOneButtonFragment;
import com.discover.mobile.bank.ui.table.ViewPagerListItem;
import com.discover.mobile.common.BaseFragment;
import com.discover.mobile.common.DiscoverActivityManager;
import com.discover.mobile.common.DiscoverModalManager;
import com.discover.mobile.common.utils.StringUtility;
import com.google.common.primitives.Ints;

import org.joda.time.DateTime;
import org.joda.time.Period;
/*
 * This class shows the statements landing page for a particular 
 * account.  At current moment, this class is only used to test navigation it
 * does not actually display any statements.
 */
public class AccountStatementsLandingFragment extends BaseFragment {

	/**List of statements to filter and display to user*/
	private StatementList statements;
	/**Main content view. List of statements goes in here*/
	private  LinearLayout contentLayout;
	/**key for bundle to restore the state of the spinner*/
	private static final String SPINNER_INDEX = "spinner_index";
	
	/**Key for retrieveing account name from bundle*/
	public static final String ACCOUNT_NAME_KEY = "ACCOUNT_NAME";
	/**key for retrieving accoutn id from bundle*/
	public static final String ACCOUNT_ID_KEY = "ACCOUNT_ID";
	/**static string for the options list*/
	private static final String PAST_SIX_MONTHS = "Past 6 Months";
	
	@Override
	public View onCreateView(final LayoutInflater inflater, final ViewGroup container,
			final Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.bank_statements_landing, null);
		//display the account name to the user
		Bundle args = getArguments();
		String accountTitle = args.getString(ACCOUNT_NAME_KEY, StringUtility.EMPTY);
		TextView titleView = (TextView) view.findViewById(R.id.page_title);
		titleView.setText(accountTitle);
		//create the drop down
		ArrayAdapter<String> aa = new ArrayAdapter<String>(
													DiscoverActivityManager.getActiveActivity(),
													R.layout.bank_dropdown_selection_view_large,
													getSpinnerList()
													);
		IcsSpinner dropDown = (IcsSpinner) view.findViewById(R.id.date_range_spinner);
		dropDown.setAdapter(aa);
		dropDown.setOnItemSelectedListener(itemSelectedListener);
		//set the statement list to correct account list
		String accountId = getArguments().getString(ACCOUNT_ID_KEY);
		statements = BankUser.instance().getAccountsToStatementsMap().get(accountId);
		statements.orderStatements();
		//build the list of statements
		contentLayout = (LinearLayout) view.findViewById(R.id.statement_list);
		//restore the state of the spinner, which will update the state of the list
		if( null != savedInstanceState && savedInstanceState.containsKey(SPINNER_INDEX)) {
			int index = savedInstanceState.getInt(SPINNER_INDEX);
			dropDown.setSelection(index);
		}
		return view;
		
	}

	/*
	 * Save the state of the spinner selection to restore
	 */
	@Override 
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		IcsSpinner dropDown = (IcsSpinner) getView().findViewById(R.id.date_range_spinner);
		int index = dropDown.getSelectedItemPosition();
		outState.putInt(SPINNER_INDEX, index);
	}
	
	
	
	/*
	 * set title to "view statements"
	 */
	@Override
	public int getActionBarTitle() {
		return R.string.statements_action_title;
	}

	@Override
	public int getGroupMenuLocation() {
		return 0;
	}

	@Override
	public int getSectionMenuLocation() {
		return 2;
	}
	
	/**
	 * Construct the list of options for the drop down
	 * @return list of values for the ICS spinner
	 */
	private ArrayList<String> getSpinnerList() {
		final ArrayList<String> options = new ArrayList<String>();
		options.add(PAST_SIX_MONTHS);
		DateTime date = new DateTime();
		options.add(String.valueOf(date.getYear()));
		for(int i = 1; i < 7; i++) {
			options.add(String.valueOf(date.minusYears(i).getYear()));
		}
		return options;
	}
	
	/**
	 * Add the available statements to list ot display to the user
	 * @param list of statements that will be shown 
	 */
	private void buildStatementList(List<Statement> statementList) {
		contentLayout.removeAllViews();
		for( Statement s : statementList) {
			SimpleChooseListItem sView = new SimpleChooseListItem(
																DiscoverActivityManager.getActiveActivity(),
																null,
																s,
																s.name
																	);
			//need to set onclick listener to download the statement
			contentLayout.addView(sView);
		}
		
	}

	/**
	 * anonymous listener to handler the ics spinner events
	 */
	private OnItemSelectedListener itemSelectedListener = new OnItemSelectedListener() {

		@Override
		public void onItemSelected(IcsAdapterView<?> parent, View view,
				int position, long id) {
			if( null == view ) {
				return;
			}
			String item = ((TextView) view).getText().toString();
			//try to parse the integer value of the year
			Integer year = Ints.tryParse(item);
			if (null == year) {
				//find the past six months
				showLastSixMonths();
			} else {
				//find the statements from that year
				showStatementFromYear(year);
			}
		}

		@Override
		public void onNothingSelected(IcsAdapterView<?> parent) {
			//intentionally left blank.
		}
		
	};
	

	/**
	 * filters the statement list to show only the past six months of statements
	 * and adds those statements to the list to display to the user
	 */
	private void showLastSixMonths () {
		ArrayList<Statement> lastSixList = new ArrayList<Statement>();
		DateTime currentDate = new DateTime();
		DateTime sixMonthsAgo = currentDate.minusMonths(7);
		DateTime statementDate = null;
		for (Statement s : statements.statementList) {
			statementDate = new DateTime(s.statementDate);
			if(statementDate.isBefore(currentDate) && statementDate.isAfter(sixMonthsAgo)){
				lastSixList.add(s);
			} else if (statementDate.isAfter(currentDate)) {
				break;
			}
		}
		buildStatementList(lastSixList);
	}
	
	/**
	 * filter the list and show the available statements from the 
	 * supplied
	 * @param year statements need to be from
	 */
	private void showStatementFromYear(final Integer year) {
		ArrayList<Statement> yearList = new ArrayList<Statement>();
		DateTime statementDateTime = null;
		for(Statement s : statements.statementList) {
			statementDateTime = new DateTime(s.statementDate);
			if ( statementDateTime.getYear() > year) {
				break;
			}else if(statementDateTime.getYear() == year) {
				yearList.add(s);
			}
		}
		buildStatementList(yearList);
	}
	
	
}
