package com.discover.mobile.bank.ui.widgets;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import com.discover.mobile.bank.R;

public class BankUrlChangerAdapter extends ArrayAdapter<List<BankUrlSite>>{

	private final LayoutInflater inflater;

	private final List<BankUrlSite> sites;

	public BankUrlChangerAdapter(final Context context, final List<BankUrlSite> sites) {
		super(context, R.id.title);
		inflater = LayoutInflater.from(context);
		this.sites = sites;
	}
	/**
	 * Get the view
	 * @param postion - current position
	 * @param view - current view
	 * @param parent - parent view group
	 */
	@Override
	public View getView(final int position, View view, final ViewGroup parent){

		view = inflater.inflate(R.layout.bank_url_site_entry_layout, null);


		return view;
	}

	/**
	 * Get the amount of items being displayed total
	 * @return the amount of items being displayed total
	 */
	@Override
	public int getCount(){
		return sites.size();
	}
}
