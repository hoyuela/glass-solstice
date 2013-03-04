package com.discover.mobile.bank.ui.fragments;

import java.util.List;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.discover.mobile.bank.R;
import com.discover.mobile.bank.ui.table.ListItemGenerator;
import com.discover.mobile.bank.ui.table.ViewPagerListItem;
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
	private LinearLayout contentTable;
	protected final ListItemGenerator generator = new ListItemGenerator(DiscoverActivityManager.getActiveActivity());

	protected abstract int getFragmentLayout();
	protected abstract void loadListItemsTo(final LinearLayout contentTable);
	protected void customSetup(final View mainView) {
		
	}

	@Override
	public View onCreateView(final LayoutInflater inflater, final ViewGroup container,
			final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		final View mainView = inflater.inflate(getFragmentLayout(), null);
		contentTable = (LinearLayout)mainView.findViewById(R.id.content_table);
		
		loadListItemsTo(contentTable);
		customSetup(mainView);
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