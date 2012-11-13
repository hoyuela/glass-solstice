package com.discover.mobile.login;

import android.content.Context;
import android.widget.ArrayAdapter;

import com.discover.mobile.R;

@Deprecated
public class CustomArrayAdapter extends ArrayAdapter<String> {
	
//	private final Context context;
//	private final String[] values;
	
	public CustomArrayAdapter(final Context context, final String[] values) {
		super(context, R.layout.single_list_item_with_disclosure_indicator, R.id.tv, values);
//		this.context = context;
//		this.values = values;
	}
	
//	@Override
//	public View getView(final int position, final View convertView, final ViewGroup parent) {
//		final LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
//		final View rowView = inflater.inflate(R.layout.single_list_item_with_disclosure_indicator, parent, false);
//		final TextView textView = (TextView) rowView.findViewById(R.id.tv);
//		textView.setText(values[position]);
//		// Change the icon for Windows and iPhone
//		
//		return rowView;
//	}
}
