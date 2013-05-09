/*
 * © Copyright Solstice Mobile 2013
 */
package com.discover.mobile.bank.help;

import android.annotation.SuppressLint;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TextView;

import com.discover.mobile.BankMenuItemLocationIndex;
import com.discover.mobile.bank.BankExtraKeys;
import com.discover.mobile.bank.R;
import com.discover.mobile.bank.framework.BankConductor;
import com.discover.mobile.common.BaseFragment;
import com.discover.mobile.common.utils.CommonUtils;

/**
 * This is the landing page for the Card FAQ section.
 * This page displays all CArd FAQ categories for a card user.
 * 
 * @author jthornton
 *
 */
public class CardFAQLandingPageFragment extends BaseFragment {

	/**
	 * Setup the view using a default adapter for the list. 
	 */
	@SuppressLint("NewApi")
	@Override
	public View onCreateView(final LayoutInflater inflater, final ViewGroup container,
			final Bundle savedInstanceState) {
		final View view = inflater.inflate(R.layout.faq_landing_page, null);

		final String[] values = getResources().getStringArray(R.array.card_faq_sections);

		final LinearLayout faqList = (LinearLayout)view.findViewById(R.id.faq_list);

		//Build the linear layout table.
		for(int i = 0; i < values.length; ++i) {

			if(i > 0){ 
				insertDividerLine(faqList);
			}

			//Set the text of the section
			final RelativeLayout item = (RelativeLayout)inflater.inflate(R.layout.single_item_table_cell, null);
			final TextView label = (TextView)item.findViewById(android.R.id.text1);
			label.setText(values[i]);
			item.setOnClickListener(getListClickListener(item));
			((TextView)item.findViewById(android.R.id.text1)).setTextColor(getResources().getColor(R.color.blue_link));

			//Add the constructed list item to the table.
			faqList.addView(item);

			view.findViewById(R.id.bank_footer).setVisibility(View.GONE);

			view.findViewById(R.id.more_faq_text).setVisibility(View.VISIBLE);

			final TextView link = (TextView)view.findViewById(R.id.more_faq_link);
			link.setVisibility(View.VISIBLE);
			link.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(final View v) {
					BankConductor.navigateToBrowser("https://www.discover.com/credit-cards/help-center/faqs");
				}
			});
		}

		//Disable hardware acceleration for the UI so that the dotted line gets drawn correctly.
		if(android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.HONEYCOMB) {
			view.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
		} else {
			// Tiled background is often broken for older devices
			CommonUtils.fixBackgroundRepeat(view.findViewById(R.id.faq_layout));
		}

		return view;
	}

	/**
	 * Place a divider line at the next available position in the linear layout.
	 * @param view a linear layout to add a divider line to.
	 */
	private void insertDividerLine(final LinearLayout view) {
		final View divider = new View(getActivity(), null);
		divider.setBackgroundResource(R.drawable.table_dotted_line);
		final LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT, 1);

		view.addView(divider, params);
	}

	/**
	 * A click listener that when onClick is called, navigates to a specific FAQ section based
	 * on the title of the listItem.
	 * @param listItem a listItem that is used to indicate a FAQ section.
	 * @return an OnClickListener that will navigate to a FAQ section.
	 */
	private OnClickListener getListClickListener(final RelativeLayout listItem) {
		return new OnClickListener() {

			@Override
			public void onClick(final View v) {
				final TextView cellText = (TextView)listItem.findViewById(android.R.id.text1);
				decideWhereToNavigateFromSectionTitle(cellText.getText().toString());
			}
		};
	}

	/**
	 * Navigates to a FAQ section based on a title.
	 * @param title a FAQ section title that corresponds to a FAQ detail page.
	 */
	private void decideWhereToNavigateFromSectionTitle(final String title) {
		final Resources res = getResources();

		if(title.equals(res.getString(R.string.card_faq_general))){
			BankConductor.navigateToCardFaqDetail(BankExtraKeys.GENERAL_CARD_FAQ);
		}else if(title.equals(res.getString(R.string.card_faq_discover_extras))){
			BankConductor.navigateToCardFaqDetail(BankExtraKeys.DISCOVER_EXTRAS_CARD_FAQ);
		}else if(title.equals(res.getString(R.string.card_faq_travel))){
			BankConductor.navigateToCardFaqDetail(BankExtraKeys.TRAVEL_CARD_FAQ);
		}else if(title.equals(res.getString(R.string.card_faq_payments_and_trans))){
			BankConductor.navigateToCardFaqDetail(BankExtraKeys.PAYMENTS_AND_TRANS_CARD_FAQ);
		}else if(title.equals(res.getString(R.string.card_faq_push_and_text_alerts))){
			BankConductor.navigateToCardFaqDetail(BankExtraKeys.PUSH_TEXT_ALERT_CARD_FAQ);
		}else if(title.equals(res.getString(R.string.card_faq_refer_a_friend))){
			BankConductor.navigateToCardFaqDetail(BankExtraKeys.REFER_FRIEND_CARD_FAQ);
		}else if(title.equals(res.getString(R.string.card_faq_send_money))){
			BankConductor.navigateToCardFaqDetail(BankExtraKeys.SEND_MONEY_CARD_FAQ);
		}
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
