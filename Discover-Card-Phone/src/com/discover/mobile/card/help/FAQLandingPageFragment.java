/*
 * � Copyright Solstice Mobile 2013
 */
package com.discover.mobile.card.help;

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

import com.discover.mobile.card.CardMenuItemLocationIndex;
import com.discover.mobile.card.R;
import com.discover.mobile.common.BaseFragment;
import com.discover.mobile.common.facade.FacadeFactory;
import com.discover.mobile.common.utils.CommonUtils;

public class FAQLandingPageFragment extends BaseFragment {

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

			//Add the constructed list item to the table.
			faqList.addView(item);

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
			FacadeFactory.getCardFaqFacade().navigateToCardFaqDetail(FAQExtraKeys.GENERAL);
		}else if(title.equals(res.getString(R.string.card_faq_discover_extras))){
			FacadeFactory.getCardFaqFacade().navigateToCardFaqDetail(FAQExtraKeys.DISCOVER_EXTRAS);
		}else if(title.equals(res.getString(R.string.card_faq_travel))){
			FacadeFactory.getCardFaqFacade().navigateToCardFaqDetail(FAQExtraKeys.TRAVEL);
		}else if(title.equals(res.getString(R.string.card_faq_payments_and_trans))){
			FacadeFactory.getCardFaqFacade().navigateToCardFaqDetail(FAQExtraKeys.PAYMENTS_AND_TRANS);
		}else if(title.equals(res.getString(R.string.card_faq_push_and_text_alerts))){
			FacadeFactory.getCardFaqFacade().navigateToCardFaqDetail(FAQExtraKeys.PUSH_TEXT_ALERT);
		}else if(title.equals(res.getString(R.string.card_faq_refer_a_friend))){
			FacadeFactory.getCardFaqFacade().navigateToCardFaqDetail(FAQExtraKeys.REFER_FRIEND);
		}else if(title.equals(res.getString(R.string.card_faq_send_money))){
			FacadeFactory.getCardFaqFacade().navigateToCardFaqDetail(FAQExtraKeys.SEND_MONEY);
		}
	}

	@Override
	public int getActionBarTitle() {
		return R.string.card_faq_title;
	}

	@Override
	public int getGroupMenuLocation() {
		return CardMenuItemLocationIndex.CUSTOMER_SERVICE_GROUP;
	}

	@Override
	public int getSectionMenuLocation() {
		return CardMenuItemLocationIndex.FAQ_SECTION;
	}
}
