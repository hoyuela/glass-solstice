package com.discover.mobile.bank.transfer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.discover.mobile.BankMenuItemLocationIndex;
import com.discover.mobile.bank.BankExtraKeys;
import com.discover.mobile.bank.R;
import com.discover.mobile.bank.deposit.BankSelectAccountComparable;
import com.discover.mobile.bank.framework.BankConductor;
import com.discover.mobile.bank.navigation.BankNavigationRootActivity;
import com.discover.mobile.bank.services.account.Account;
import com.discover.mobile.bank.services.account.AccountList;
import com.discover.mobile.bank.ui.table.ListItemGenerator;
import com.discover.mobile.bank.ui.table.ReverseViewPagerListItem;
import com.discover.mobile.bank.ui.table.ViewPagerListItem;
import com.discover.mobile.bank.util.FragmentOnBackPressed;
import com.discover.mobile.common.BaseFragment;
import com.discover.mobile.common.utils.StringUtility;
import com.google.common.base.Strings;

/**
 * This class allows a user to select accounts to transfer money between.
 * It constructs at most two tables of accounts, internal and external and is required to show the user
 * their previous choices and prevent them from making certain decisions, such as
 * selecting two external accounts.
 *
 * @author scottseward
 *
 */
public class BankTransferSelectAccount extends BaseFragment implements FragmentOnBackPressed {
	private static final String TAG = BankTransferSelectAccount.class.getSimpleName();

	private final Account[] selectedAccounts = new Account[2];

	private static final int INTERNAL_ACCOUNT = 0;
	private static final int EXTERNAL_ACCOUNT = 1;

	private AccountList internalAccounts = null;
	private AccountList externalAccounts = null;

	private boolean useMyBackPress = true;

	@Override
	public View onCreateView(final LayoutInflater inflater, final ViewGroup container,
			final Bundle savedInstanceState) {
		
		final View mainView = inflater.inflate(R.layout.bank_transfer_select_account, null);
		setTitleFromArguments(mainView);

		loadSelectedAccounts();

		populateAccounts(INTERNAL_ACCOUNT, mainView);
		populateAccounts(EXTERNAL_ACCOUNT, mainView);

		if(isOtherAccountExternalAccount() && getTotalAccountSize() > 2){
			showExternalAccountWarning(mainView);
		}

		return mainView;
	}

	/**
	 * Set the title of the page based on what kind of account the user is selecting.
	 * @param mainView the view which contains the title label.
	 */
	private void setTitleFromArguments(final View mainView) {
		final Bundle args = getArguments();
		if(args != null) {
			final String titleText = getResources().getString(args.getInt(BankExtraKeys.TITLE_TEXT));
			((TextView)mainView.findViewById(R.id.page_title)).setText(titleText);
		}
	}

	/**
	 * Populates a table on screen with available accounts.
	 * @param accountType either an internal or external account.
	 * @param mainView the view containing the tables to populate.
	 */
	private void populateAccounts(final int accountType, final View mainView) {
		final Bundle args = getArguments();

		if (args != null) {
			final AccountList accountList = getAccounts(accountType);
			
			if(INTERNAL_ACCOUNT == accountType){
				Collections.sort(accountList.accounts, new BankSelectAccountComparable());
			}
			
			if(accountList != null && getAccountSize(accountList) > 0) {
				populateTableWithAccounts(accountType, accountList, mainView);
			}else {
				hideTable(accountType, mainView);
			}
		}
	}

	/**
	 * Replaces the external account table with a warning text that notifies the user that they cannot
	 * select two external accounts to transfer between. This text is clickable and permits the user
	 * to change their selected external account.
	 * @param mainView
	 */
	private void showExternalAccountWarning(final View mainView) {
		final TextView editOtherAccountLink = (TextView)mainView.findViewById(R.id.change_other_account_link);

		if(isToAccountScreen()){
			editOtherAccountLink.setText(Html.fromHtml(getString(R.string.change_transfer_from_account)));
		} else{
			editOtherAccountLink.setText(Html.fromHtml(getString(R.string.change_transfer_to_account)));
		}
		
		editOtherAccountLink.setOnClickListener(pushReselectClickListener);

		mainView.findViewById(R.id.external_accounts_title).setVisibility(View.VISIBLE);
		editOtherAccountLink.setVisibility(View.VISIBLE);

	}

	/**
	 * The click listener that will direct the user to the other select account screen so that they can change
	 * their external account if they wish.
	 */
	private final OnClickListener pushReselectClickListener = new OnClickListener() {

		@Override
		public void onClick(final View v) {
			final Bundle args = getArguments();
			flipTitleBundle();
			args.putBoolean(BankExtraKeys.SHOULD_NAVIGATE_BACK, true);

			BankConductor.navigateToSelectTransferAccount(args);
		}
	};

	/**
	 * Because we need to possibly navigate between two different instances of this fragment, we need a way
	 * to change the presented title of the screen. This method manages the title value that is stored in 
	 * the bundle of this Fragment.
	 */
	private void flipTitleBundle() {
		final Bundle args = getArguments();
		int title = 0;

		if(isToAccountScreen()){
			title = R.string.from;
		}else{
			title = R.string.to;
		}
		
		args.putInt(BankExtraKeys.TITLE_TEXT, title);
	}

	/**
	 * 
	 * @return if this screen is a 'to' account screen.
	 */
	private boolean isToAccountScreen() {
		boolean isToScreen = false;
		final Bundle args = getArguments();
		int titleRes = 0;
		if(args != null){
			titleRes = args.getInt(BankExtraKeys.TITLE_TEXT);
		}
		if(titleRes == R.string.to){
			isToScreen = true;
		}
		
		return isToScreen;
	}

	/**
	 * Populates the content tables on the screen with the appropriate accounts.
	 * @param accountType
	 * @param accountList
	 * @param mainView
	 */
	private void populateTableWithAccounts(final int accountType, final AccountList accountList, final View mainView) {
		final LinearLayout table = (LinearLayout)mainView.findViewById(getResourceFor(accountType));

		if(table != null){
			for(final Account account : accountList.accounts) {
				addAccountToTable(account, table);
			}
		}
	}
	
	/**
	 * Add a given account to a given table.
	 * @param account an Account object that can be added to the table.
	 * @param table a table to add an Account object to.
	 */
	private void addAccountToTable(final Account account, final LinearLayout table) {
		final boolean canAddAccount = account.isTransferEligible() || account.isExternalAccount();
		final boolean wontAllowTwoExternals = !(account.isExternalAccount() && isOtherAccountExternalAccount()) ;
		final boolean accountShouldBeAdded =  wontAllowTwoExternals || getTotalAccountSize() <= 2;

		if(canAddAccount && accountShouldBeAdded) {
			table.addView(getListItemFromAccount(account));
		}
	}

	/**
	 * Checks to see if any selected account is an external account.
	 *
	 * @return if any selected account is an external account.
	 */
	private boolean isOtherAccountExternalAccount() {
		boolean hasExternalAccount = false;
		final Account other = getOtherSelectedAccount();
		if(other != null){
			hasExternalAccount = other.isExternalAccount();
		}
		return hasExternalAccount;
	}

	/**
	 * Returns the previously selected account with respect to the kind of screen we are on.
	 * @return the 'from' account if we are on 'to' or the 'to' account if we are on 'from'
	 */
	private Account getOtherSelectedAccount() {
		Account otherSelectedAccount = null;

		if(isToAccountScreen()){
			otherSelectedAccount = selectedAccounts[1];
		}else{
			otherSelectedAccount = selectedAccounts[0];
		}
		
		return otherSelectedAccount;
	}

	/**
	 * Returns the resource ID for the content table associated with an account type.
	 * @param accountType either an internal or external account.
	 * @return the resource ID for the content table associated with an account type.
	 */
	private int getResourceFor(final int accountType) {
		int resourceId = 0;

		switch(accountType) {
			case INTERNAL_ACCOUNT :
				resourceId = R.id.content_table_internal_accounts;
				break;
			case EXTERNAL_ACCOUNT :
				resourceId = R.id.content_table_external_accounts;
				break;
			default :
				Log.e(TAG, "Unable to get specified resource during getResourceFor method.");
				break;
		}

		return resourceId;
	}

	/**
	 * Returns an object containing a List of Account objects.
	 * @param accountType either an internal or external account.
	 * @return an object containing a List of Account objects.
	 */
	private AccountList getAccounts(final int accountType) {
		AccountList accountList = null;
		final Bundle args = getArguments();

		switch(accountType) {
			case INTERNAL_ACCOUNT :
				accountList = (AccountList)args.getSerializable(BankExtraKeys.INTERNAL_ACCOUNTS);
				break;
			case EXTERNAL_ACCOUNT :
				accountList = (AccountList)args.getSerializable(BankExtraKeys.EXTERNAL_ACCOUNTS);
				break;
			default :
				break;
		}

		return accountList;
	}

	/**
	 *  
	 * @return the internal accounts that were provided to this screen through the Bundle or a new empty list
	 * if none were found.
	 */
	private AccountList getInternalAccounts() {
		if(internalAccounts == null) {
			internalAccounts = getAccounts(INTERNAL_ACCOUNT);
			if(internalAccounts == null){
				internalAccounts = getNewAccountList();
			}
		}

		return internalAccounts;
	}

	/**
	 * 
	 * @return the external accounts that were provided to this screen through the Bundle or a new empty list
	 * if none were found.
	 */
	private AccountList getExternalAccounts() {
		if(externalAccounts == null) {
			externalAccounts = getAccounts(EXTERNAL_ACCOUNT);
			if(externalAccounts == null){
				externalAccounts = getNewAccountList();
			}
		}

		return externalAccounts;
	}

	/**
	 * 
	 * @return a new AccountList object.
	 */
	private AccountList getNewAccountList() {
		final AccountList temp = new AccountList();
		temp.accounts = new ArrayList<Account>();
		return temp;
	}

	/**
	 * Loads to local references all previously selected accounts.
	 */
	private void loadSelectedAccounts() {
		final Bundle args = getArguments();
		
		if(args != null) {
			final Account[] temp = (Account[])args.getSerializable(BankExtraKeys.DATA_SELECTED_INDEX);
			 
			if(temp != null){
				for(int i = 0; i < temp.length; ++i){
					 selectedAccounts[i] = temp[i];
				}
			}
		}

	}

	@Override
	public void onResume() {
		super.onResume();
		loadSelectedAccounts();
	}

	/**
	 * Returns a constructed table cell with the information from an account object.
	 * @param account an Account object which contains infomation to show on screen.
	 * @param generator a ListItemGenerator instance.
	 * @return a constructed table cell with the information from an account object.
	 */
	private ViewPagerListItem getListItemFromAccount(final Account account) {
		final ListItemGenerator generator = new ListItemGenerator(this.getActivity());
		final ReverseViewPagerListItem item = generator.getReverseTwoItemCell(R.string.empty, StringUtility.EMPTY);

		item.getTopLabel().setSingleLine(false);
		item.getTopLabel().setMaxLines(2);
		item.getTopLabel().setText(account.nickname);
		
		if(account.accountNumber != null){
			item.getMiddleLabel().setText(getAccountEndingTextForAccount(account.accountNumber.ending));
		}
		
		item.getBalanceView().setText((account.balance != null) ? account.balance.formatted : StringUtility.EMPTY);
		
		final int color = (null != account.balance && account.balance.value < 0) ? R.color.error_indicator : R.color.field_copy;
		item.getBalanceView().setTextColor(getResources().getColor(color));
		
		item.getBalanceView().setVisibility((account.balance != null) ? View.VISIBLE : View.GONE);
		
		if((account.equals(getOtherSelectedAccount()) && getTotalAccountSize() > 2) ||
			(account.equals(getOtherSelectedAccount()) && getTotalAccountSize() == 1)) { 
			item.getTopLabel().setTextColor(getResources().getColor(R.color.field_copy));
			item.setOnClickListener(null);
		}else{
			item.getTopLabel().setTextColor(getResources().getColor(R.color.black));
		
			item.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(final View v) {
						selectAccount(account);
				}
			});
		}
		
		return item;
	}

	/**
	 * 
	 * @return the size of external and internal accounts combined.
	 */
	private int getTotalAccountSize() {
		return getAccountSize(getExternalAccounts()) + getAccountSize(getInternalAccounts());
	}

	/**
	 * Selects a given account
	 * @param account
	 */
	private void selectAccount(final Account account) {

		if(isToAccountScreen()){
			selectedAccounts[0] = account;
		}else{
			selectedAccounts[1] = account;
		}
		
		twoAccountAutoSelect();

		final Bundle args = getArguments();
		args.putSerializable(BankExtraKeys.DATA_SELECTED_INDEX, selectedAccounts);

		final boolean shouldGoBackToSelectAccount = args.getBoolean(BankExtraKeys.SHOULD_NAVIGATE_BACK);

		if(shouldGoBackToSelectAccount) {
			args.putBoolean(BankExtraKeys.SHOULD_NAVIGATE_BACK, false);
			if(isToAccountScreen()){
				args.putInt(BankExtraKeys.TITLE_TEXT, R.string.from);
			}else{
				args.putInt(BankExtraKeys.TITLE_TEXT, R.string.to);
			}
			
			this.getFragmentManager().popBackStack();
		}else{
			BankConductor.navigateBackFromTransferSelectAccount(args);
		}
	}

	/**
	 * If there are only two accounts to choose from, this method will select the account that was not selected by
	 * the user, so that we can auto fill one account field.
	 */
	private void twoAccountAutoSelect() {

		if(getTotalAccountSize() == 2) {
			if(isToAccountScreen()) {
				selectedAccounts[1] = getFirstAccountThatIsNotThisAccount(selectedAccounts[0]);
			}
			else {
				selectedAccounts[0] = getFirstAccountThatIsNotThisAccount(selectedAccounts[1]);
			}
		}
	}

	/**
	 * 
	 * @param account 
	 * @return the first Account in either external or internal accounts that is not the account in the parameter.
	 */
	private Account getFirstAccountThatIsNotThisAccount(final Account account) {
		Account otherAccount = null;
		final List<Account> allAccounts = new ArrayList<Account>();

		if(getInternalAccounts() != null){
			allAccounts.addAll(getInternalAccounts().accounts);
		}
		if(getExternalAccounts() != null){
			allAccounts.addAll(getExternalAccounts().accounts);
		}

		for(final Account item : allAccounts){
			if(!item.equals(account) && item.isTransferEligible() && 
					!(isOtherAccountExternalAccount() && item.isExternalAccount())) {
				otherAccount = item;
			}
		}

		return otherAccount;
	}

	/**
	 * 
	 * @param accountList
	 * @return the number of transfer eligible accounts in the given list.
	 */
	private int getAccountSize(final AccountList accountList) {
		int size = 0;
		if(accountList != null && accountList.accounts != null){
			for(final Account account : accountList.accounts){
				if(account.isTransferEligible()){
					size++;
				}
			}
		}

		return size;
	}

	/**
	 * Returns a String in the format of "Account Ending in xxxx" where xxxx is the last 4 digits of an account number.
	 * @param accountEndingNumber a String representation of the last 4 digits of an account number.
	 * @return a formatted String such as "Account Ending in xxxx".
	 */
	private String getAccountEndingTextForAccount(final String accountEndingNumber) {
		final StringBuilder accountEndingIn = new StringBuilder();
		final String prefix = getResources().getString(R.string.ending_in);

		if(!Strings.isNullOrEmpty(accountEndingNumber) && !Strings.isNullOrEmpty(prefix)) {
			accountEndingIn.append(prefix);
			accountEndingIn.append(" ");
			accountEndingIn.append(accountEndingNumber);
		}

		return accountEndingIn.toString();
	}

	/**
	 * Given a specified table type value, this method will hide that table and its title label.
	 * @param tableType the type of table to hide. A constant integer defined in this class.
	 * @param containingView the view which contains the table.
	 */
	private void hideTable(final int tableType, final View containingView) {
		switch(tableType) {
			case INTERNAL_ACCOUNT :
				containingView.findViewById(R.id.content_table_internal_accounts).setVisibility(View.GONE);
				containingView.findViewById(R.id.internal_accounts_title).setVisibility(View.GONE);
				break;
			case EXTERNAL_ACCOUNT :
				containingView.findViewById(R.id.external_accounts_layout).setVisibility(View.GONE);
				break;
			default :
				Log.e(TAG, "Unsupported table type to hide for hideTable(int, View) method.");
				break;
		}
	}

	@Override
	public int getActionBarTitle() {
		return R.string.transfer_money;
	}

	@Override
	public int getGroupMenuLocation() {
		return BankMenuItemLocationIndex.TRANSFER_MONEY_GROUP;
	}

	@Override
	public int getSectionMenuLocation() {
		return BankMenuItemLocationIndex.TRANSFER_MONEY_GROUP;
	}

	/**
	 * Navigates back to either a previous instance of the select account screen, or back to step one of transfer
	 * money. Depending on if the user pressed the change external account link or not.
	 */
	@Override
	public void onBackPressed() {
		final Bundle args = getArguments();
		args.putSerializable(BankExtraKeys.DATA_SELECTED_INDEX, selectedAccounts);

		if(args.getBoolean(BankExtraKeys.SHOULD_NAVIGATE_BACK)) {
			args.putBoolean(BankExtraKeys.SHOULD_NAVIGATE_BACK, false);
			flipTitleBundle();
			useMyBackPress = false;
			((BankNavigationRootActivity)this.getActivity()).onBackPressed();
		}else{
			BankConductor.navigateBackFromTransferSelectAccount(args);
		}
	}

	@Override
	public boolean isBackPressDisabled() {
		return useMyBackPress;
	}

}
