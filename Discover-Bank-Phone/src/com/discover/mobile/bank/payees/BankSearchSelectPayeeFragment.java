package com.discover.mobile.bank.payees;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.discover.mobile.BankMenuItemLocationIndex;
import com.discover.mobile.bank.BankExtraKeys;
import com.discover.mobile.bank.R;
import com.discover.mobile.bank.framework.BankConductor;
import com.discover.mobile.bank.help.HelpMenuListFactory;
import com.discover.mobile.bank.paybills.SimpleChooseListItem;
import com.discover.mobile.bank.services.payee.SearchPayeeResult;
import com.discover.mobile.bank.services.payee.SearchPayeeResultList;
import com.discover.mobile.common.BaseFragment;
import com.discover.mobile.common.help.HelpWidget;

/**
 * Fragment that will be used in the fourth step of the Add Payee Work-flow.
 * This will only be shown if the user is enrolled and eligible for payments.
 * It will display the name of the payee and a button to add a new payee.
 * When features are clicked:
 * 
 * Add Payee - start add payee work flow
 * Provide feedback - start the provide feedback flow
 * Select Payee - go to Add Payee where the Payee selected is pre-populated
 * Enter Payee - go to Add Payee where the user will have to enter all information including Payee
 * 
 * @author hoyuela
 *
 */
public class BankSearchSelectPayeeFragment extends BaseFragment implements OnClickListener{

	/**Key to read from a bundle the search criteria used to generate a list of results*/
	public static final String SEARCH_ITEM = "search";

	/**
	 * Search criteria used to generate the list of payees displayed to the user
	 */
	private String searchCriteria;
	/**
	 * List of payees
	 * */
	private SearchPayeeResultList search;
	/**
	 * Layout holding the payees
	 * */
	private LinearLayout payeesList;
	/**
	 * Button used to open a help guide when clicked on
	 */
	private ImageButton helpButton;
	/**
	 * TextView used to display message to user either "Matches For" or "There are no matches for"
	 */
	private TextView matches;
	/**
	 * TextView which displays what search criteria was used to run the Payees Search
	 */
	private TextView searchName;
	/**
	 * 
	 */
	private Bundle bundle = null;
	/**
	 * Control used to navigate the user to add an unmanaged payee
	 */
	private SimpleChooseListItem enterPayeeDetails;
			
	/**
	 * Create the view
	 * @param inflater - inflater to inflate the layout
	 * @param container - container holding the group
	 * @param savedInstanceState - state of the fragment
	 */
	@Override
	public View onCreateView(final LayoutInflater inflater, final ViewGroup container,
			final Bundle savedInstanceState) {

		final View view = inflater.inflate(R.layout.bank_select_search_payee, null);
		bundle = (savedInstanceState != null) ? savedInstanceState : this.getArguments();

		/**Help icon setup*/
		final HelpWidget help = (HelpWidget) view.findViewById(R.id.help);
		help.showHelpItems(HelpMenuListFactory.instance().getPayBillsHelpItems());

		/**Linear Layout which holds the results for the Payees Search**/
		payeesList = (LinearLayout)view.findViewById(R.id.payee_list);

		/**TextView whose text will dynamically be changed depending on whether you have search results or not*/
		matches = (TextView)view.findViewById(R.id.matches);

		/**TextView which shows the user what the search criteria was used to generate the list of Payees*/
		searchName = (TextView)view.findViewById(R.id.search_name);

		/**Custom Widget used to navigate to the Add Payees fragment*/
		enterPayeeDetails = (SimpleChooseListItem)view.findViewById(R.id.enter_payee_details);
		enterPayeeDetails.setTitleText(R.string.bank_enter_payee_details);
		enterPayeeDetails.setOnClickListener(this);
		
		/**Check whether bundle is empty*/
		if( bundle != null) {
			searchCriteria = bundle.getString(SEARCH_ITEM);

			/**Generate list of payees found using the search criteria read from bundle*/
			loadListFromBundle(bundle);
		} 

		/**Set text to show what the criteria was used to generate the list of payees found*/
		searchName.setText(" \"" +searchCriteria +"\"");

		return view;
	}

	/**
	 * Save the state of the current fragment
	 * @param outState - bundle to save the state in.
	 */
	@Override
	public void onSaveInstanceState(final Bundle outState){
		super.onSaveInstanceState(outState);

		/**If bundle is empty then load values fetched values from get arguments*/
		if( bundle == null ) {
			bundle = this.getArguments();
		}
		
		/**Store everything stored in bundle when fragment was created*/
		if( bundle != null ) {
			outState.putAll(bundle);
		} 
	}

	/**
	 * Extract the data from the bundles and then display it.
	 * @param bundle - bundle containing the data to be displayed.
	 */
	public void loadListFromBundle(final Bundle bundle){
		search = (SearchPayeeResultList)bundle.getSerializable(BankExtraKeys.PAYEES_LIST);
		if(null == search || null == search.results || search.results.isEmpty()) {
			matches.setText(R.string.bank_no_matches_for);
			payeesList.setVisibility(View.GONE);
		}else{
			matches.setText(R.string.bank_matches_for);
			payeesList.setVisibility(View.VISIBLE);

			payeesList.removeAllViews();
			for(final SearchPayeeResult result : search.results){
				payeesList.addView(createListItem(result));
			}
		}
	}

	/**
	 * Create a single choose list item
	 * @param detail - detail used to show the text and associate to the item
	 * @return the single choose list item
	 */
	private SimpleChooseListItem createListItem(final SearchPayeeResult result){
		final SimpleChooseListItem item =  new SimpleChooseListItem(this.getActivity(), null, result, result.name, false);
		item.setOnClickListener(this);
		return item;
	}


	/**
	 * Set the title in the action bar.
	 */
	@Override
	public int getActionBarTitle() {
		return R.string.bank_manage_payees;
	}

	/**
	 * Click handler for all widgets hosted by this view
	 */
	@Override
	public void onClick(final View sender) {
		
		/**Help button was clicked**/
		if( sender == helpButton ) {
			final CharSequence text = "Help Under Development";
			final int duration = Toast.LENGTH_SHORT;

			final Toast toast = Toast.makeText(this.getActivity(), text, duration);
			toast.show();
		}
		else if( sender instanceof SimpleChooseListItem ) {
			/**Enter Payee Details was clicked to add Unmanaged Payee*/
			if( sender.getId() ==  enterPayeeDetails.getId()) {
				BankConductor.navigateToAddPayee(BankAddUnmanagedPayeeFragment.class, new Bundle());
			}
			/**A list item with a Managed Verified Payee Name was clicked*/
			else {
				final SimpleChooseListItem item = (SimpleChooseListItem)sender;
				final SearchPayeeResult result = (SearchPayeeResult)item.getItem();
	
				final Bundle bundle = new Bundle();
				bundle.putSerializable(BankExtraKeys.DATA_LIST_ITEM, result);
	
				BankConductor.navigateToAddPayee(BankAddManagedPayeeFragment.class, bundle);
			}
		}
	}

	@Override
	public int getGroupMenuLocation() {
		return BankMenuItemLocationIndex.PAY_BILLS_GROUP;
	}

	@Override
	public int getSectionMenuLocation() {
		return BankMenuItemLocationIndex.MANAGE_PAYEES_SECTION;
	}

}

