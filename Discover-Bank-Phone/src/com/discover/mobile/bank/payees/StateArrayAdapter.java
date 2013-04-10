package com.discover.mobile.bank.payees;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.discover.mobile.bank.R;

/**
 * Class derived form ArrayAdapter<> for displaying U.S. states within a UI view.
 * 
 * @author henryoyuela
 *
 */
public class StateArrayAdapter extends ArrayAdapter<State> {

	public StateArrayAdapter(final Context context) {
		super(context, R.layout.push_simple_spinner_view, State.STATES);
	
	}

	@Override
	public View getView(final int position, final View convertView, final ViewGroup parent) {
		return super.getView(position, convertView, parent);
	}

	@Override
	public View getDropDownView(final int position, final View convertView, final ViewGroup parent) {
		View row = convertView;
		if (row == null) {
			final LayoutInflater inflater = (LayoutInflater)getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			row = inflater.inflate(R.layout.bank_state_view, parent, false);
		}
		
		final State item = getItem(position);

		if (row != null && item != null ) {
			final TextView state = (TextView) row.findViewById(R.id.state);

			state.setText(item.toString());
		}

		return row;
	}

}
