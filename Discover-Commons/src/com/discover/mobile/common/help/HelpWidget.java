/*
 * © Copyright Solstice Mobile 2013
 */
package com.discover.mobile.common.help;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.RelativeLayout;

import com.discover.mobile.common.R;

/**
 * Help menu widget that can be utilized to show menu the menu drop down and 
 * give the items the ability to be clicked on.
 * 
 * To use the widget place the following xml at the end of a relative layout.
 *     <com.discover.mobile.common.help.HelpWidget
 *        android:id="@+id/help"
 *        android:layout_width="match_parent"
 *        android:layout_height="wrap_content" />
 * 
 * By placing this at the end of a relative layout this will allow the dropdown
 * to be displayed over all of the other views.
 * 
 * Then to add items to the menu, create a list of items that need to be shown
 * and then call the showHelpItems method.
 * 
 * @author jthornton
 *
 */
public class HelpWidget extends RelativeLayout{

	/**Image of the help button*/
	private final ImageButton help;

	/**View holding everything that can be expanded*/
	private final RelativeLayout expandableView;

	/**List containing the help icons*/
	private final ListView list;

	/**Adapter attached to the list*/
	private final HelpAdapter adapter;

	/**
	 * Constructor for the help widget
	 * @param context - activity context
	 * @param attrs - attributes to be applied to the layout
	 */
	public HelpWidget(final Context context, final AttributeSet attrs) {
		super(context, attrs);
		final View view = LayoutInflater.from(context).inflate(R.layout.help_widget, null);

		help = (ImageButton) view.findViewById(R.id.help);
		expandableView = (RelativeLayout) view.findViewById(R.id.help_list);
		list = (ListView) view.findViewById(R.id.help_list_view);

		adapter = new HelpAdapter(context, R.layout.help_list_item, new ArrayList<HelpItemGenerator>());
		list.setAdapter(adapter);

		help.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(final View v) {
				if(expandableView.getVisibility() == View.INVISIBLE){
					expandableView.setVisibility(View.VISIBLE);
				}else{

					expandableView.setVisibility(View.INVISIBLE);

				}
			}
		});

		addView(view);
	}

	/**
	 * Show the help items in the menu so that they can be clicked on.
	 * This will show all the items in the list in the order that the
	 * items are in the list.
	 * @param items - list of items to be shown in the menu
	 */
	public void showHelpItems(final List<HelpItemGenerator> items){
		if(null == items || items.isEmpty()){return;}
		adapter.setData(items);
		adapter.notifyDataSetChanged();
	}
}
