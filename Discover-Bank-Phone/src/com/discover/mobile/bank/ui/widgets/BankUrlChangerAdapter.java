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

import com.discover.mobile.bank.R;

/**
 * Adapter used to display the data in the manage URL home screen.
 * @author jthornton
 *
 */
public class BankUrlChangerAdapter extends ArrayAdapter<List<BankUrlSite>>{

	/**Inflater used to inflate layouts*/
	private final LayoutInflater inflater;

	/**List of sites being displayed*/
	private List<BankUrlSite> sites;

	/**Boolean set to true if the adapter is in delete mode*/
	private boolean isDeleting = false;

	/**List of sites that need to be deleted*/
	private List<BankUrlSite> deleteSites;

	/**Alpha of the views that should look disabled*/
	private static final float DISABLED_ALPHA = .25f;

	/**
	 * Constructor of the class
	 * @param context - activity context
	 * @param sites - list of sites to be initially displayed
	 */
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
	public View getView(final int position, final View view, final ViewGroup parent){
		View convertView;
		if(null == view){
			convertView = inflater.inflate(R.layout.bank_url_site_entry_layout, null);
		}else{
			convertView = view;
		}
		final BankUrlSite site = sites.get(position);


		final TextView title = (TextView) convertView.findViewById(R.id.title);
		final TextView link = (TextView) convertView.findViewById(R.id.link);
		final ImageView box = (ImageView) convertView.findViewById(R.id.delete_box);
		title.setText(site.title);
		link.setText(site.link);

		convertView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(final View v) {
				if(site.canBeEdited && !isDeleting){
					launchEditActivity(site);
				}else if(site.canBeEdited){
					toggleDeleted(site, box);
				}
			}
		});

		setUpBoxDisplay(box, site);
		return convertView;
	}

	/**
	 * Set up how the check boxes should look
	 * @param box - check box
	 * @param site - site linked to that box
	 */
	private void setUpBoxDisplay(final ImageView box, final BankUrlSite site) {
		if(isDeleting){
			box.setVisibility(View.VISIBLE);
			if(isSiteInDeleteList(site)){
				box.setImageDrawable(getContext().getResources().getDrawable(R.drawable.white_check_mark));
			}else if(!site.canBeEdited){
				box.setEnabled(false);
				box.setAlpha(DISABLED_ALPHA);
			}else{
				box.setImageDrawable(getContext().getResources().getDrawable(R.drawable.transparent_square));
			}

			box.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(final View v) {
					toggleDeleted(site, box);
				}
			});
		}else{
			box.setVisibility(View.GONE);
		}

	}

	/**
	 * Launched the edit activity so the url can be changed
	 * @param site - site to be edited
	 */
	private void launchEditActivity(final BankUrlSite site){
		final Intent intent = new Intent(getContext(), BankUrlChangerModifySiteActivity.class);
		final Bundle bundle = new Bundle();
		bundle.putString(BankUrlSite.TITLE, site.title);
		bundle.putString(BankUrlSite.LINK, site.link);
		intent.putExtras(bundle);
		getContext().startActivity(intent);
	}

	/**
	 * Toggle the delete state of an item
	 * @param site - site in question
	 * @param box - box showing the state
	 */
	private void toggleDeleted(final BankUrlSite site, final ImageView box){
		if(site.canBeEdited){
			if(isSiteInDeleteList(site)){
				box.setImageDrawable(getContext().getResources().getDrawable(R.drawable.transparent_square));
				deleteSites.remove(site);
			}else{
				box.setImageDrawable(getContext().getResources().getDrawable(R.drawable.white_check_mark));
				deleteSites.add(site);
			}
		}
	}

	/**
	 * Check to see if a site is in the delete list
	 * @param site - site in question
	 * @return if the site in question is in the delete list
	 */
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

	/**
	 * @return is the adapter is in delete mode
	 */
	public boolean isDeleting() {
		return isDeleting;
	}

	/**
	 * Set the deleting state of the adapter
	 * @param isDeleting - boolean to set the state to
	 */
	public void setDeleting(final boolean isDeleting) {
		if(isDeleting && null == deleteSites){
			deleteSites = new ArrayList<BankUrlSite>();
		}
		deleteSites.clear();
		this.isDeleting = isDeleting;
	}

	/**
	 * Set the data in the adapter
	 * @param sites - list of data to be shown
	 */
	public void setData(final List<BankUrlSite> sites){
		this.sites = sites;
	}

	/**
	 * Get the sites that are in the delete list
	 * @return the sites that are in the delete list
	 */
	public List<BankUrlSite> getDeleteSites(){
		return deleteSites;
	}

}
