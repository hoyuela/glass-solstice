package com.discover.mobile.bank.ui.table;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.discover.mobile.bank.R;

/**
 * Titles for the table
 * @author jthornton
 *
 */
public class TableTitles extends RelativeLayout{

	private final TextView label1;

	private final TextView label2;

	private final TextView label3;

	public TableTitles(final Context context, final AttributeSet attrs) {
		super(context, attrs);

		final View mainView = LayoutInflater.from(context).inflate(R.layout.table_titles, null);

		label1 = (TextView) mainView.findViewById(R.id.filter_text_1);
		label2 = (TextView) mainView.findViewById(R.id.filter_text_2);
		label3 = (TextView) mainView.findViewById(R.id.filter_text_3);

		addView(mainView);
	}

	public void setLabel1(final String text){
		label1.setText(text);
	}

	public void setLabel2(final String text){
		label2.setText(text);
	}

	public void setLabel3(final String text){
		label3.setText(text);
	}
}
