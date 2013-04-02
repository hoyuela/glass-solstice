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

	/**Text view for the first label*/
	private final TextView label1;

	/**Text view for the second label*/
	private final TextView label2;

	/**Text view for the third label*/
	private final TextView label3;

	/**Message label*/
	private final TextView message;

	/**Table line*/
	private final View line;

	/**
	 * Constructor for the layout
	 * @param context - context of the actiivty
	 * @param attrs - attributes to apply to the layout
	 */
	public TableTitles(final Context context, final AttributeSet attrs) {
		super(context, attrs);

		final View mainView = LayoutInflater.from(context).inflate(R.layout.table_titles, null);

		label1 = (TextView) mainView.findViewById(R.id.filter_text_1);
		label2 = (TextView) mainView.findViewById(R.id.filter_text_2);
		label3 = (TextView) mainView.findViewById(R.id.filter_text_3);
		message = (TextView) mainView.findViewById(R.id.message);
		line = mainView.findViewById(R.id.top_line);

		addView(mainView);
	}

	/**
	 * Set the text of the first filter
	 * @param text - text to set the filter to
	 */
	public void setLabel1(final String text){
		label1.setText(text);
	}

	/**
	 * Set the text of the second filter
	 * @param text - text to set the filter to
	 */
	public void setLabel2(final String text){
		label2.setText(text);
	}

	/**
	 * Set the text of the third filter
	 * @param text - text to set the filter to
	 */
	public void setLabel3(final String text){
		label3.setText(text);
	}

	/**
	 * Set the message text of the table title and show the message
	 * @param message - message to set in the text view
	 */
	public void setMessage(final String message){
		this.message.setText(message);
		this.message.setVisibility(View.VISIBLE);
		label1.setVisibility(View.GONE);
		label2.setVisibility(View.GONE);
		label3.setVisibility(View.GONE);
		line.setVisibility(View.GONE);
	}

	/**
	 * Hide the headers of the table
	 */
	public void hideFilters(){
		label1.setVisibility(View.GONE);
		label2.setVisibility(View.GONE);
		label3.setVisibility(View.GONE);
		line.setVisibility(View.GONE);
	}

	/**
	 * Hide the message
	 */
	public void hideMessage(){
		label1.setVisibility(View.VISIBLE);
		label2.setVisibility(View.VISIBLE);
		label3.setVisibility(View.VISIBLE);
		line.setVisibility(View.VISIBLE);
		message.setVisibility(View.GONE);
	}
}
