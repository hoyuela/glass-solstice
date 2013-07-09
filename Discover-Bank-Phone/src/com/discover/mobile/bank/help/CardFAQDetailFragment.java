/*
 * © Copyright Solstice Mobile 2013
 */
package com.discover.mobile.bank.help;

import java.util.ArrayList;
import java.util.List;

import android.annotation.SuppressLint;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.discover.mobile.BankMenuItemLocationIndex;
import com.discover.mobile.bank.BankExtraKeys;
import com.discover.mobile.bank.R;
import com.discover.mobile.bank.ui.widgets.BankLayoutFooter;
import com.discover.mobile.bank.ui.widgets.FooterType;
import com.discover.mobile.common.BaseFragment;
import com.google.common.base.Strings;

/**
 * This Fragment displays Card FAQ items for the user to review.
 * To use, pass a BankExtraKey value in the bundle arguments to this Fragment and then this Fragment will display
 * the related content.
 * 
 * @author jthornton
 */
public class CardFAQDetailFragment extends BaseFragment {
	private final static String SCROLL_Y = "a";

	/** The list of FAQ items that will be shown in this Fragment */
	private final List<CardFAQListItem>faqItems = new ArrayList<CardFAQListItem>();

	/**
	 * Setup the Fragment to be shown.
	 */
	@SuppressLint("NewApi")
	@Override
	public View onCreateView(final LayoutInflater inflater, final ViewGroup container, 
			final Bundle savedInstanceState) {

		super.onCreateView(inflater, container, savedInstanceState);
		final View view = inflater.inflate(R.layout.faq_detail_fragment, null);

		final TextView titleLabel = (TextView)view.findViewById(R.id.title);
		titleLabel.setText(getResources().getString(getTitleForFragment()));
		populateFAQItemsToTable((LinearLayout)view.findViewById(R.id.content_table));
		restoreState(savedInstanceState);

		//Disable hardware acceleration for the UI so that the dotted line gets drawn correctly.
		if(android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.HONEYCOMB) {
			view.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
		}

		//Restore the scroll position on rotation change.
		final ScrollView mainScroll = (ScrollView)view.findViewById(R.id.scroll_view);
		mainScroll.post(new Runnable() {
			@Override
			public void run() {
				if(savedInstanceState != null && mainScroll != null){
					mainScroll.scrollTo(0, savedInstanceState.getInt(SCROLL_Y));
				}
			}
		});

		final BankLayoutFooter footer = (BankLayoutFooter) view.findViewById(R.id.bank_footer);
		footer.setCardMode(true);
		footer.setFooterType(FooterType.PROVIDE_FEEDBACK | FooterType.PRIVACY_TERMS);
		
		return view;
	}

	/**
	 * Returns the string resource integer that should be used as the title for this Fragment.
	 * @return the string resource integer that should be used as the title for this Fragment.
	 */
	private int getTitleForFragment() {
		final String faqType = getFAQTypeFromArgBundle();
		int titleResource = 0;

		//Compare the faqType value with the BankExtraKeys and return the string resource value that matches.
		if(!Strings.isNullOrEmpty(faqType)) {
			titleResource = getTitleResource(faqType);
		}

		return titleResource;
	}

	/**
	 * Returns the string resource integer that should be used as the title for this Fragment.
	 * @param faqType - string representing the faq type
	 * @return the string resource integer that should be used as the title for this Fragment.
	 */
	private int getTitleResource(final String faqType){
		int titleResource = 0;

		if(faqType.equals(BankExtraKeys.GENERAL_CARD_FAQ)){
			titleResource = R.string.card_faq_general;
		}else if(faqType.equals(BankExtraKeys.DISCOVER_EXTRAS_CARD_FAQ)){
			titleResource = R.string.card_faq_discover_extras;
		}else if(faqType.equals(BankExtraKeys.TRAVEL_CARD_FAQ)){
			titleResource = R.string.card_faq_travel;
		}else if(faqType.equals(BankExtraKeys.PAYMENTS_AND_TRANS_CARD_FAQ)){
			titleResource = R.string.card_faq_payments_and_trans;
		}else if(faqType.equals(BankExtraKeys.PUSH_TEXT_ALERT_CARD_FAQ)){
			titleResource = R.string.card_faq_push_and_text_alerts;
		}else if(faqType.equals(BankExtraKeys.REFER_FRIEND_CARD_FAQ)){
			titleResource = R.string.card_faq_refer_a_friend;
		}else if(faqType.equals(BankExtraKeys.SEND_MONEY_CARD_FAQ)){
			titleResource = R.string.card_faq_send_money;
		}

		return titleResource;
	}

	/**
	 * Save the list of open and closed list items so that we can restore which items were open or closed when
	 * the Fragment is re-created.
	 */
	@Override
	public void onSaveInstanceState(final Bundle outState) {
		super.onSaveInstanceState(outState);

		if( outState != null && this.getView() != null) {
			outState.putSerializable(BankExtraKeys.PRIMARY_LIST, getSaveStateValues());
			outState.putInt(SCROLL_Y, ((ScrollView)this.getView().findViewById(R.id.scroll_view)).getScrollY());
		}
	}

	/**
	 * Returns a boolean array that contains the open state of every item in the list.
	 * @return a boolean array with open states of all items in the list.
	 */
	private boolean[] getSaveStateValues() {
		final boolean openList[] = new boolean[faqItems.size()];

		//Find all faqItems that are open, then update the boolean value in the array to true.
		for(int i = 0; i < openList.length; ++i){
			if(faqItems.get(i).isOpen()){
				openList[i] = true;
			}
		}

		return openList;
	}

	/**
	 * Restores the state of the faqItems expand state. It opens items that were previously open.
	 * @param savedInstanceState a Bundle which contains a boolean array with open and close states.
	 */
	protected void restoreState(final Bundle savedInstanceState) {
		if(savedInstanceState != null) {
			final boolean[] openStates = savedInstanceState.getBooleanArray(BankExtraKeys.PRIMARY_LIST);

			//Restore the open and close states of the list items
			for(int i = 0; i < openStates.length; ++i) {
				if(openStates[i] && i < faqItems.size()){
					faqItems.get(i).openItem();
				}
			}
		}
	}

	/**
	 * Gets the list of FAQ items to display and then populates the FAQ list with those items.
	 * @param contentTable a LinearLayout that will hold the FAQ items.
	 */
	private void populateFAQItemsToTable(final LinearLayout contentTable) {
		//Get the String values for the FAQ items.
		final String[] listItems = getFAQListItems();
		//Cache the length value.
		final int listItemLength = listItems.length;

		//Clear items in list if any
		faqItems.clear();

		//The loop is incremented by 2 each time because we are grabbing data two values at a time.
		for(int i = 0; i < listItemLength && (i + 1) < listItemLength; i += 2) {
			final CardFAQListItem listItem = new CardFAQListItem(getActivity());

			//Hide the first divider line.
			if(i == 0){
				listItem.hideDivider();
			}

			//Get the FAQ list data in pairs. They are stored in title,body,title,body..etc. order.
			listItem.setTitle(listItems[i]);
			listItem.setBody(listItems[i + 1]);

			faqItems.add(listItem);
		}

		//To be sure that the only content in the table is the FAQ items, remove everything else first.
		contentTable.removeAllViews();

		for(final CardFAQListItem item : faqItems){
			if(item != null){
				contentTable.addView(item);
			}
		}
	}

	/**
	 * Returns an array of Strings for the FAQ type of this Fragment. This is determined from the title resource value.
	 * There should be a String array defined for each possible title for this Fragment.
	 * @return an array of Strings
	 */
	private String[] getFAQListItems() {
		String[] content = {};
		final String faqType = getFAQTypeFromArgBundle();
		final Resources res = getResources();

		if(!Strings.isNullOrEmpty(faqType)) {
			if(faqType.equals(BankExtraKeys.GENERAL_CARD_FAQ)){
				content = res.getStringArray(R.array.card_general_faq_array);
			}else if(faqType.equals(BankExtraKeys.DISCOVER_EXTRAS_CARD_FAQ)){
				content = res.getStringArray(R.array.card_extra_faq_array);
			}else if(faqType.equals(BankExtraKeys.TRAVEL_CARD_FAQ)){
				content = res.getStringArray(R.array.card_travel_faq_array);
			}else if(faqType.equals(BankExtraKeys.PAYMENTS_AND_TRANS_CARD_FAQ)){
				content = res.getStringArray(R.array.card_payments_trans_faq_array);
			}else if(faqType.equals(BankExtraKeys.PUSH_TEXT_ALERT_CARD_FAQ)){
				content = res.getStringArray(R.array.card_push_text_faq_array);
			}else if(faqType.equals(BankExtraKeys.REFER_FRIEND_CARD_FAQ)){
				content = res.getStringArray(R.array.card_refer_friend_faq_array);
			}else if(faqType.equals(BankExtraKeys.SEND_MONEY_CARD_FAQ)){
				content = res.getStringArray(R.array.card_send_money_faq_array);
			}
		}

		return content;
	}

	/**
	 * Returns the String value of the FAQ_TYPE that exists in the getArguments() bundle.
	 * @return the String value of the FAQ_TYPE that exists in the getArguments() bundle.
	 */
	private String getFAQTypeFromArgBundle() {
		String faqType = "";
		final Bundle args = getArguments();

		if(args != null){
			faqType = args.getString(BankExtraKeys.FAQ_TYPE);
		}

		return faqType;
	}

	@Override
	public int getActionBarTitle() {
		return R.string.faq_title;
	}


	@Override
	public int getGroupMenuLocation() {
		return BankMenuItemLocationIndex.CUSTOMER_SERVICE_GROUP;
	}

	@Override
	public int getSectionMenuLocation() {
		return BankMenuItemLocationIndex.FREQUENTLY_ASKED_QUESTIONS;
	}
}
