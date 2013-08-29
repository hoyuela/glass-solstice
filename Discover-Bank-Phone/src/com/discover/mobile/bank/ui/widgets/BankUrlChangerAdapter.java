package com.discover.mobile.bank.ui.widgets;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.discover.mobile.bank.R;

public class BankUrlChangerAdapter extends ArrayAdapter<List<BankUrlSite>>{

	private final LayoutInflater inflater;

	private final List<BankUrlSite> sites;

	private boolean isDeleting = false;

	private List<BankUrlSite> deleteSites;

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
		final BankUrlSite site = sites.get(position);


		final TextView title = (TextView) view.findViewById(R.id.title);
		final TextView link = (TextView) view.findViewById(R.id.link);
		final ImageView box = (ImageView) view.findViewById(R.id.delete_box);
		title.setText(site.title);
		link.setText(site.link);

		view.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(final View v) {
				Toast.makeText(getContext(), "Item Hit" + site.title, Toast.LENGTH_SHORT).show();
				final Intent intent = new Intent(getContext(), BankUrlChangerModifySiteActivity.class);
				final Bundle bundle = new Bundle();
				bundle.putString(BankUrlSite.TITLE, site.title);
				bundle.putString(BankUrlSite.LINK, site.link);
				intent.putExtras(bundle);
				getContext().startActivity(intent);
			}
		});

		if(isDeleting){
			box.setVisibility(View.VISIBLE);
			if(isSiteInDeleteList(site)){
				box.setImageDrawable(getContext().getResources().getDrawable(R.drawable.white_check_mark));
			}else{
				box.setImageDrawable(getContext().getResources().getDrawable(R.drawable.transparent_square));
			}
			box.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(final View v) {
					if(isSiteInDeleteList(site)){
						box.setImageDrawable(getContext().getResources().getDrawable(R.drawable.transparent_square));
						deleteSites.remove(site);
					}else{
						box.setImageDrawable(getContext().getResources().getDrawable(R.drawable.white_check_mark));
						deleteSites.add(site);
					}
				}
			});
		}else{
			box.setVisibility(View.GONE);
		}



		return view;
	}

	private boolean isSiteInDeleteList(final BankUrlSite site){
		boolean isInList = false;
		for(final BankUrlSite temp : deleteSites){
			if(temp.isEqualTo(site)){
				isInList = true;
			}
		}
		return isInList;
	}

	/**
	 * Get the amount of items being displayed total
	 * @return the amount of items being displayed total
	 */
	@Override
	public int getCount(){
		return sites.size();
	}
	public boolean isDeleting() {
		return isDeleting;
	}
	public void setDeleting(final boolean isDeleting) {
		if(isDeleting && null == deleteSites){
			deleteSites = new ArrayList<BankUrlSite>();
		}
		deleteSites.clear();
		this.isDeleting = isDeleting;
	}
}
