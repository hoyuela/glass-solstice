package com.discover.mobile.bank.account;

import java.util.List;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.discover.mobile.bank.BankExtraKeys;
import com.discover.mobile.bank.R;
import com.discover.mobile.bank.services.account.activity.ActivityDetail;
import com.discover.mobile.common.DiscoverActivityManager;

/**
 * This is a specific style of detail item.
 * It calls the appropriate methods from the DetailItem class to fill the content table
 * with list items and data.
 * 
 * @author scottseward
 *
 */
public abstract class DetailFragment extends Fragment {
	public LinearLayout contentTable;
	private ListItemGenerator generator;

	protected abstract int getFragmentLayout();

	@Override
	public View onCreateView(final LayoutInflater inflater, final ViewGroup container,
			final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		generator = new ListItemGenerator(DiscoverActivityManager.getActiveActivity());
		
		final View mainView = inflater.inflate(getFragmentLayout(), null);
		contentTable = (LinearLayout)mainView.findViewById(R.id.content_table);
		final Bundle bundle = getArguments();
		final ActivityDetail item = (ActivityDetail)bundle.getSerializable(BankExtraKeys.DATA_LIST_ITEM);
		
		loadListElementsToLayoutFromList(contentTable, generator.getDetailTransactionList(item));
		
		return mainView;
	}
	
	/**
	 * Loads a list of ViewPagerListItems into a LinearLayout to construct a table of items.
	 * @param layout a LinearLayout that will show a list of ViewPagerListItems
	 * @param elementList a list of ViewPagerListItems
	 */
	public void loadListElementsToLayoutFromList(final LinearLayout layout, final List<ViewPagerListItem> elementList){
		if(layout != null && elementList != null){
			for(final ViewPagerListItem element : elementList)
				layout.addView(element);
		}
	}
}