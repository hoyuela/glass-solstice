package com.discover.mobile.bank.ui.widgets;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.discover.mobile.bank.R;

public class BankUrlChangerAdapter extends ArrayAdapter<String>{

	private final String[] urls = new String[]{"asys", "msto", "prod"};

	/**Inflater used to inflate layouts*/
	private final LayoutInflater inflater;

	public BankUrlChangerAdapter(final Context context) {
		super(context, R.layout.bank_url_changer_list_item);
		inflater = LayoutInflater.from(context);
	}

	/**
	 * Get the view
	 * @param postion - current position
	 * @param view - current view
	 * @param parent - parent view group
	 */
	@Override
	public View getView(final int position, final View view, final ViewGroup parent){
		View displayView;

		if(null == view){
			displayView = inflater.inflate(R.layout.bank_url_changer_list_item, null);
		}else{
			displayView = view;
		}

		final TextView displayName = (TextView) displayView.findViewById(R.id.url_name);
		displayName.setText(urls[position]);
		return displayView;
	}

}
