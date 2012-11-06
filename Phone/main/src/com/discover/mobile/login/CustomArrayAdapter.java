package com.discover.mobile.login;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.discover.mobile.R;

public class CustomArrayAdapter extends ArrayAdapter<String>{
	
	private final Context context;
	  private final String[] values;

	  public CustomArrayAdapter(Context context, String[] values) {
	    super(context, R.layout.single_list_item_with_disclosure_indicator, values);
	    this.context = context;
	    this.values = values;
	  }

	  @Override
	  public View getView(int position, View convertView, ViewGroup parent) {
	    LayoutInflater inflater = (LayoutInflater) context
	        .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	    View rowView = inflater.inflate(R.layout.single_list_item_with_disclosure_indicator, parent, false);
	    TextView textView = (TextView) rowView.findViewById(R.id.tv);
	    textView.setText(values[position]);
	    // Change the icon for Windows and iPhone
	   
	    return rowView;
	  }
	} 