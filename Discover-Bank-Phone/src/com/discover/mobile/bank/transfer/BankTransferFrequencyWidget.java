/*
 * © Copyright Solstice Mobile 2013
 */
package com.discover.mobile.bank.transfer;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.discover.mobile.BankMenuItemLocationIndex;
import com.discover.mobile.bank.BankExtraKeys;
import com.discover.mobile.bank.R;
import com.discover.mobile.bank.framework.BankConductor;
import com.discover.mobile.bank.help.HelpMenuListFactory;
import com.discover.mobile.bank.paybills.SimpleChooseListItem;
import com.discover.mobile.common.BaseFragment;
import com.discover.mobile.common.help.HelpWidget;

/**
 * Widget used to determine how often the user would like to have
 * the funds transfer happen.
 *
 * @author jthornton
 *
 */
public class BankTransferFrequencyWidget extends BaseFragment{

	/**Layout holding the payees*/
	private LinearLayout list;

	/**
	 * Create the view
	 * @param inflater - inflater to inflate the layout
	 * @param container - container holding the group
	 * @param savedInstanceState - state of the fragment
	 */
	@Override
	public View onCreateView(final LayoutInflater inflater, final ViewGroup container, final Bundle savedInstanceState) {
		final View view = inflater.inflate(R.layout.bank_frequency_widget, null);

		final HelpWidget help = (HelpWidget) view.findViewById(R.id.help);
		help.showHelpItems(HelpMenuListFactory.instance().getBankTransferHelpItems());
		list = (LinearLayout) view.findViewById(R.id.list);

		initLayout();

		return view;
	}

	/**
	 * This method will take the loaded list of payees and fill
	 * the list on the screen with the data.
	 */
	private void initLayout() {

		final String[] text = this.getResources().getStringArray(R.array.transfer_frequency_strings);
		final String[] codes = this.getResources().getStringArray(R.array.transfer_frequency_codes);
		list.removeAllViews();

		for(int i = 0; i < text.length; i++){
			SimpleChooseListItem item = null;
			if(text.length == 1) {
				item = createSingleListItem(text[i], codes[i]);
				list.addView(item);
			} else if (i == 0) {
				item = createFirstListItem(text[i], codes[i]);
				list.addView(item);
			} else if (i == text.length - 1) {
				item = createLastListItem(text[i], codes[i]);
				list.addView(item);
			} else {
				item = createListItem(text[i], codes[i]);
				list.addView(item);
			}
			item.setTextBlue(getActivity());
		}
	}

	/**
	 * Create a single choose list item for a middle entry - dashed on top, no bottom stroke
	 *
	 * @param detail
	 *            - detail used to show the text and associate to the item
	 * @return the single choose list item
	 */
	private SimpleChooseListItem createListItem(final String string, final String code) {
		final SimpleChooseListItem item = new SimpleChooseListItem(
				this.getActivity(), null, code, string,
				R.layout.simple_choose_item_dash);
		item.setOnClickListener(getOnClickListener(string, code));
		return item;
	}

	/**
	 * Create a single choose list item for the bottom entry - solid stroke on bottom
	 *
	 * @param detail
	 *            - detail used to show the text and associate to the item
	 * @return the single choose list item
	 */
	private SimpleChooseListItem createLastListItem(final String string, final String code) {
		final SimpleChooseListItem item = new SimpleChooseListItem(
				this.getActivity(), null, code, string);
		item.setBackgroundAsBottomItem(getActivity());
		item.setOnClickListener(getOnClickListener(string, code));
		return item;
	}

	/**
	 * Create a single choose list item for a top entry - solid stroke on top
	 *
	 * @param detail
	 *            - detail used to show the text and associate to the item
	 * @return the single choose list item
	 */
	private SimpleChooseListItem createFirstListItem(final String string, final String code) {
		final SimpleChooseListItem item = new SimpleChooseListItem(
				this.getActivity(), null, code, string);
		item.setBackgroundAsTopItem(getActivity());
		item.setOnClickListener(getOnClickListener(string, code));
		return item;
	}

	/**
	 * Create a single choose list item for a single list entry - solid stroke on all sides.
	 *
	 * @param detail
	 *            - detail used to show the text and associate to the item
	 * @return the single choose list item
	 */
	private SimpleChooseListItem createSingleListItem(final String string, final String code) {
		final SimpleChooseListItem item = new SimpleChooseListItem(
				this.getActivity(), null, code, string);
		item.setOnClickListener(getOnClickListener(code, string));
		return item;
	}

	/**
	 * Get the click listener for when a list item it clicked
	 * @param detail - detail that needs to be passed
	 * @return the click listener for when a list item it clicked
	 */
	protected OnClickListener getOnClickListener(final String string, final String code) {
		return new OnClickListener(){
			@Override
			public void onClick(final View v) {
				Bundle bundle = getArguments();

				if(bundle == null)
					bundle = new Bundle();

				bundle.putString(BankExtraKeys.FREQUENCY_CODE, code);
				bundle.putString(BankExtraKeys.FREQUENCY_TEXT, string);
				BankConductor.navigateBackFromTransferWidget(bundle);
			}
		};
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
}
