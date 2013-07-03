/*
 * � Copyright Solstice Mobile 2013
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
import com.discover.mobile.bank.paybills.SimpleChooseListItem;
import com.discover.mobile.common.BaseFragment;

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

		list = (LinearLayout) view.findViewById(R.id.list);

		initLayout();

		return view;
	}

	/**
	 * This method will take the loaded list of frequencies and fill
	 * the list on the screen with the data.
	 */
	private void initLayout() {

		final String[] text = this.getResources().getStringArray(R.array.transfer_frequency_strings);
		final String[] codes = this.getResources().getStringArray(R.array.transfer_frequency_codes);
		list.removeAllViews();

		// Create a list item for each frequency and add to the View.
		for(int i = 0; i < text.length; i++){
			SimpleChooseListItem item = null;
			
			if (i == 0) {
				item = createFirstListItem(text[i], codes[i]);
			} else {
				item = createListItem(text[i], codes[i]);
			}
			
			list.addView(item);
		}
	}

	/**
	 * Creates a {@link SimpleChooseListItem} item with a solid stroke on the bottom.
	 *
	 * @param string - {@link String} text to display.
	 * @param code - {@link String} value associated with the item.
	 * @return {@link SimpleChooseListItem} with an {@link OnClickListener} attached.
	 */
	private SimpleChooseListItem createListItem(final String string, final String code) {
		final SimpleChooseListItem item = new SimpleChooseListItem(
				this.getActivity(), null, code, string, R.layout.simple_choose_item_stroke_bottom);
		item.setOnClickListener(getOnClickListener(string, code));
		return item;
	}
	
	/**
	 * Creates a {@link SimpleChooseListItem} with a solid stroke on the top and bottom.
	 *
	 * @param string - {@link String} text to display.
	 * @param code - {@link String} value associated with the item.
	 * @return {@link SimpleChooseListItem} with an {@link OnClickListener} attached.
	 */
	private SimpleChooseListItem createFirstListItem(final String string, final String code) {
		final SimpleChooseListItem item = new SimpleChooseListItem(
				this.getActivity(), null, code, string);
		item.setOnClickListener(getOnClickListener(string, code));
		return item;
	}

	/**
	 * Creates an {@link OnClickListener} for selecting a frequency.
	 * 
	 * @param string - {@link String} displayed on the clicked item.
	 * @param code - {@link String} value associated with the clicked item.
	 * @return {@link OnClickListener} to attach to a frequency.
	 */
	protected OnClickListener getOnClickListener(final String string, final String code) {
		return new OnClickListener(){
			@Override
			public void onClick(final View v) {
				Bundle bundle = getArguments();

				if(bundle == null) {
					bundle = new Bundle();
				}

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
