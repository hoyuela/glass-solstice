package com.discover.mobile.help;

import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.TextView;

import com.discover.mobile.R;
import com.discover.mobile.common.CommonMethods;

/**
 * Utility class used to wrap the Need Help footer at the bottom of every page or modal. Provides
 * functionals to set the help number dynamically and to show or hide the footer on a page.
 * 
 * @author henryoyuela
 *
 */
public class NeedHelpFooter 
{
	/**View that holds the content text*/
	private TextView helpNumberTxtVw;

	/**
	 * Default Constructor Not Used
	 */
	@SuppressWarnings("unused")
	private NeedHelpFooter() {
		
	}
	
	/**
	 * 
	 * @param rootView - View that contains the footer text views.
	 */
	public NeedHelpFooter(final ViewGroup rootView) {

		helpNumberTxtVw = (TextView)rootView.findViewById(R.id.help_number_label);

	}
	
	/**
	 * Used to set the help number displayed in the footer
	 * 
	 * @param helpNumber Resource id of the help number in the resource file
	 */
	public void setToDialNumberOnClick(final int helpNumber) {
		
		helpNumberTxtVw.setText(helpNumberTxtVw.getContext().getResources().getString(helpNumber));
		
		helpNumberTxtVw.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(final View v) {
				CommonMethods.dialNumber(helpNumberTxtVw.getText().toString(), helpNumberTxtVw.getContext());
			}
		});
	}

	/**
	 * Used to show or hide the view on the page or modal it is displayed on.
	 * 
	 * @param show True to display the footer, false otherwise.
	 */
	public void show(final boolean show) {
		final ViewGroup parentView = (ViewGroup) helpNumberTxtVw.getParent();

		if( show ) {
			parentView.setVisibility(View.VISIBLE);
		} else {
			parentView.setVisibility(View.GONE);
		}
	}
}
