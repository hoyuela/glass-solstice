/*
 * � Copyright Solstice Mobile 2013
 */
package com.discover.mobile.bank.atm;

import java.util.List;
import java.util.Locale;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.discover.mobile.bank.R;
import com.discover.mobile.bank.framework.BankServiceCallFactory;
import com.discover.mobile.bank.services.atm.AtmDetail;
import com.discover.mobile.bank.services.atm.AtmServiceHelper;
import com.discover.mobile.bank.util.BankAtmUtil;

/**
 * Adapter for the ATM list detail screen.
 * @author jthornton
 *
 */
public class AtmListAdapter  extends ArrayAdapter<List<AtmDetail>>{

	/**Inflater used to inflate layouts*/
	private final LayoutInflater inflater;

	/**ATMs retrieved from the server*/
	private List<AtmDetail> results;

	/*Fragment currently displayed*/
	private final AtmListFragment fragment;

	/**Activity context*/
	private final Context context;

	/**
	 * Constuctor for the adapter
	 * @param context - activity context
	 * @param textViewResourceId - resource
	 * @param items - items to set in the adapter
	 * @param fragment - fragment using the adapter
	 */
	public AtmListAdapter(final Context context, final int textViewResourceId, final AtmListFragment fragment) {
		super(context, textViewResourceId);
		inflater = LayoutInflater.from(context);
		this.fragment =fragment;
		this.context = context;
	}

	/**
	 * Get the view
	 * @param postion - current position
	 * @param view - current view
	 * @param parent - parent view group
	 */
	@Override
	public View getView(final int position, View view, final ViewGroup parent){
		ItemViewHolder holder = null;

		/**If the details is empty show the message*/
		if(null == results || results.isEmpty()){
			fragment.showFooterMessage();
			view = fragment.getFooter();
			return view;
		}

		/**At the end of the list try loading more*/
		if(position == results.size()){
			view = fragment.getFooter();
			return view;
		}

		final AtmDetail detail = results.get(position);
		if(detail == null){return view;}

		/**If the view is null, create a new one*/
		if(null == view || !(view.getTag() instanceof ItemViewHolder)){
			holder = new ItemViewHolder();
			view = inflater.inflate(R.layout.bank_atm_detail_item, null);
			holder = updateViewHolder(holder, view);
			/**Else reuse the old one*/
		}else{
			holder = (ItemViewHolder) view.getTag();
		}

		setUpClickableItems(holder, detail);
		displayItems(holder, detail, position);
		return view;
	}

	/**
	 * Update holder display
	 * @param holder - view holder
	 * @param detail - atm detail
	 * @param postion - position in the list
	 */
	public void displayItems(final ItemViewHolder holder, final AtmDetail detail, final int position){
		/**Update the display values*/
		if(!detail.isAtmSearchargeFree()){
			holder.title.setCompoundDrawablesWithIntrinsicBounds(
					context.getResources().getDrawable(R.drawable.atm_drk_pin_sm), null, null, null);
		}else{
			holder.title.setCompoundDrawablesWithIntrinsicBounds(
					context.getResources().getDrawable(R.drawable.atm_orange_pin_sm), null, null, null);
		}
		holder.title.setText("  " + (position+1) + ". " + detail.locationName);
		holder.address.setText(detail.address1);
		holder.address2.setText(detail.city + ", " + detail.state + " " + detail.postalCode);
		final String distance = String.format(Locale.US, "%.2f", detail.distanceFromUser);
		holder.distance.setText(distance + " M");

		if(detail.atmHrs.equalsIgnoreCase(AtmDetail.UNKNOWN)){
			holder.hours.setVisibility(View.INVISIBLE);
			holder.hoursLabel.setVisibility(View.INVISIBLE);
		}else{
			holder.hours.setText(detail.atmHrs.replace("Sat", "\nSat"));
		}

		//Expand the view if it was previously expanded
		if(detail.isExpanded){
			holder.expand.setImageDrawable(context.getResources().getDrawable(R.drawable.atm_collapse_icon));
			holder.bottom.setVisibility(View.VISIBLE);
		}else{
			holder.expand.setImageDrawable(context.getResources().getDrawable(R.drawable.atm_expand_icon));
			holder.bottom.setVisibility(View.GONE);
		}
		showServices(holder, detail);
		if(holder.numFeatures == 0){
			holder.serviceLabel.setVisibility(View.INVISIBLE);	
		}
	}

	/**
	 * Set up the clickable items in the view
	 * @param holder - view holder
	 * @param detail - atm detail
	 */
	public void setUpClickableItems(final ItemViewHolder holder, final AtmDetail detail){
		holder.top.setOnClickListener(getExpandClickListener(holder.expand, holder.bottom, detail));

		holder.directions.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(final View v){
				final AtmServiceHelper helper = new AtmServiceHelper(
						detail.address1 + " " + detail.city + " " + detail.state + " " + detail.postalCode, 
						fragment.getCurrentAddress());
				BankAtmUtil.launchNavigation(helper);
			}
		});

		holder.email.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(final View v){
				final AtmServiceHelper helper = new AtmServiceHelper(
						detail.address1 + " " + detail.city + " " + detail.state + " " + detail.postalCode, 
						fragment.getCurrentAddress());
				BankServiceCallFactory.createGetDirectionsCall(helper).submit(); 
			}
		});

		holder.report.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(final View v){
				fragment.reportAtm(Integer.toString(detail.id));
			}
		});
	}

	/**
	 * Show Services in the correct order
	 * @param holder - view holder
	 * @param detail - atm detail
	 */
	private void showServices(final ItemViewHolder holder, final AtmDetail detail){
		//Set up the service features
		if(detail.braille.equalsIgnoreCase(AtmDetail.HAS_FEATURE)){
			getServiceView(holder, AtmDetail.BRAILLE);
		}
		if(detail.restricted.equalsIgnoreCase(AtmDetail.HAS_FEATURE)){
			getServiceView(holder, AtmDetail.RESTRICTED);
		}
		if(detail.walkUp.equalsIgnoreCase(AtmDetail.HAS_FEATURE)){
			getServiceView(holder, AtmDetail.DRIVE_UP);
		}

		if(detail.driveUp.equalsIgnoreCase(AtmDetail.HAS_FEATURE)){
			getServiceView(holder, AtmDetail.WALK_UP);
		}

		if(detail.atmHrs.equalsIgnoreCase(AtmDetail.ALWAYS)){
			getServiceView(holder, AtmDetail.ALL_HOURS);
		}
	}

	/**
	 * Update all the views in the view holder
	 * @param holder - view holder
	 * @param view - view holding the items
	 */
	public ItemViewHolder updateViewHolder(final ItemViewHolder holder, final View view){
		holder.expand = (ImageView) view.findViewById(R.id.expand);
		holder.title = (TextView) view.findViewById(R.id.title);
		holder.address = (TextView) view.findViewById(R.id.address);
		holder.address2 = (TextView) view.findViewById(R.id.address2);
		holder.distance = (TextView) view.findViewById(R.id.distance);
		holder.bottom = (RelativeLayout) view.findViewById(R.id.bottom);
		holder.hours = (TextView) view.findViewById(R.id.hours);
		holder.hoursLabel = (TextView) view.findViewById(R.id.hours_label);
		holder.serviceLabel = (TextView) view.findViewById(R.id.services_label);
		holder.service1 = (TextView) view.findViewById(R.id.service1);
		holder.service2 = (TextView) view.findViewById(R.id.service2);
		holder.service3 = (TextView) view.findViewById(R.id.service3);
		holder.service4 = (TextView) view.findViewById(R.id.service4);
		holder.service5 = (TextView) view.findViewById(R.id.service5);
		holder.email = (Button) view.findViewById(R.id.email);
		holder.directions = (ImageView) view.findViewById(R.id.directions);
		holder.report = (TextView) view.findViewById(R.id.report);
		holder.top = (RelativeLayout) view.findViewById(R.id.top);
		return holder;
	}

	/**
	 * Get the text view the service should be in
	 */
	public void getServiceView(final ItemViewHolder holder, final String text){

		switch(holder.numFeatures){
		case 0:
			holder.numFeatures ++;
			holder.service5.setText(text);
			holder.service5.setVisibility(View.VISIBLE);
			break;
		case 1:
			holder.numFeatures ++;
			holder.service4.setText(text);
			holder.service4.setVisibility(View.VISIBLE);
			break;
		case 2:
			holder.numFeatures ++;
			holder.service3.setText(text);
			holder.service3.setVisibility(View.VISIBLE);
			break;
		case 3:
			holder.numFeatures ++;
			holder.service2.setText(text);
			holder.service2.setVisibility(View.VISIBLE);
			break;
		case 4:
			holder.numFeatures ++;
			holder.service1.setText(text);
			holder.service1.setVisibility(View.VISIBLE);
			break;
		default:
			holder.numFeatures ++;
			holder.service5.setText(text);
			holder.service5.setVisibility(View.VISIBLE);
			break;
		}
	}

	/**
	 * Get the amount of items being displayed total
	 * @return the amount of items being displayed total
	 */
	@Override
	public int getCount(){
		if(null == results){
			return 1;
		}else{
			return results.size() + 1;
		}
	}

	public void setData(final List<AtmDetail> atms) {
		results = atms;		
	}

	public OnClickListener getExpandClickListener(final ImageView expand, final View bottom, final AtmDetail atm){
		return new OnClickListener(){
			@Override
			public void onClick(final View v) {
				if(bottom.getVisibility() == View.GONE){
					expand.setImageDrawable(context.getResources().getDrawable(R.drawable.atm_collapse_icon));
					bottom.setVisibility(View.VISIBLE);
					atm.isExpanded = true;
				}else{
					expand.setImageDrawable(context.getResources().getDrawable(R.drawable.atm_expand_icon));
					bottom.setVisibility(View.GONE);
					atm.isExpanded = false;
				}
			}	
		};
	}

	/**
	 * Private class that holds view information
	 * @author jthornton
	 *
	 */
	private class ItemViewHolder {
		public RelativeLayout top;
		public ImageView expand;
		public TextView title;
		public TextView address;
		public TextView address2;
		public TextView distance;
		public RelativeLayout bottom;
		public TextView hours;
		public TextView hoursLabel;
		public TextView serviceLabel;;
		public TextView service1;
		public TextView service2;
		public TextView service3;
		public TextView service4;
		public TextView service5;
		public Button email;
		public ImageView directions;
		public TextView report;
		public int numFeatures = 0;
	}
}
