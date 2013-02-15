package com.discover.mobile.bank.ui.table;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RelativeLayout;

import com.discover.mobile.bank.R;

/**
 * Titles for the table
 * @author jthornton
 *
 */
public class TableTitles extends RelativeLayout{

	public TableTitles(final Context context, final AttributeSet attrs) {
		super(context, attrs);

		final View mainView = LayoutInflater.from(context).inflate(R.layout.table_titles, null);

		addView(mainView);
	}
}
