package com.discover.mobile.help;

import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.TextView;

import com.discover.mobile.R;
import com.discover.mobile.common.CommonMethods;

public class NeedHelpFooter 
{
	/**View that holds the content text*/
	private TextView helpNumberTxtVw;

	
	public NeedHelpFooter(ViewGroup rootView) {

		helpNumberTxtVw = (TextView)rootView.findViewById(R.id.help_number_label);

	}
	
	/**
	 * 
	 * @param helpNumber
	 */
	public void setToDialNumberOnClick(int helpNumber) {
		
		helpNumberTxtVw.setText(helpNumberTxtVw.getContext().getResources().getString(helpNumber));
		
		helpNumberTxtVw.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				CommonMethods.dialNumber(helpNumberTxtVw.getText().toString(), helpNumberTxtVw.getContext());
			}
		});
	}

	/**
	 * 
	 * @param show
	 */
	public void show(boolean show) {
		ViewGroup parentView = (ViewGroup) helpNumberTxtVw.getParent();

		if( show ) {
			parentView.setVisibility(View.VISIBLE);
		} else {
			parentView.setVisibility(View.GONE);
		}
	}
}
