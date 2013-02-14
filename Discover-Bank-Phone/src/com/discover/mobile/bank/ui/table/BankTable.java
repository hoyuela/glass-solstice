package com.discover.mobile.bank.ui.table;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.discover.mobile.bank.R;

/**
 * Abstract table that holds items.
 * @author jthornton
 *
 */
public abstract class BankTable extends RelativeLayout{

	/**Title View*/
	private final TextView title;

	/**List shown to the user*/
	private final LinearLayout list;

	/**Empty messageTextView*/
	private final TextView empty;

	/**Linear layout holding the sort filters*/
	private final LinearLayout filters;

	/**Divider Line for the table*/
	private final View bottomLine;

	/**Divider Line for the table*/
	private final View topLine;

	/**
	 * Constructor for the layout
	 * @param context - activity context
	 * @param attrs - layout attributes
	 */
	public BankTable(final Context context, final AttributeSet attrs){
		super(context, attrs);
		final RelativeLayout view = (RelativeLayout) LayoutInflater.from(context).inflate(R.layout.bank_table, null);

		title = (TextView) view.findViewById(R.id.table_title);
		list = (LinearLayout) view.findViewById(R.id.list);
		empty = (TextView) view.findViewById(R.id.empty);
		filters = (LinearLayout) view.findViewById(R.id.sort_filters);
		topLine = view.findViewById(R.id.top_line);
		bottomLine = view.findViewById(R.id.bottom_line);

		addView(view);
	}

	/**
	 * Set the title in the text view
	 * @param title - title to be displayed
	 */
	public void setTitle(final String title){
		this.title.setText(title);
	}

	/**
	 * Get the string resource of the no items string
	 * @return - the string resource of the no items string
	 */
	public abstract int getNoItemsMessage();

	/**
	 * Add an item to the list
	 * @param item - item to add to the list
	 */
	protected void addItem(final BankTableItem item){
		list.addView(item);
	}

	/**
	 * Show the no data message
	 */
	protected void showNoDataMessage(){
		title.setVisibility(View.GONE);
		empty.setVisibility(View.VISIBLE);
		filters.setVisibility(View.GONE);
		topLine.setVisibility(View.GONE);
		bottomLine.setVisibility(View.GONE);
		list.setVisibility(View.GONE);
		empty.setText(this.getResources().getString(getNoItemsMessage()));
	}

	/**
	 * Show has data view
	 */
	protected void showDataView(){
		title.setVisibility(View.VISIBLE);
		empty.setVisibility(View.GONE);
		filters.setVisibility(View.VISIBLE);
		bottomLine.setVisibility(View.VISIBLE);
		list.setVisibility(View.VISIBLE);
		topLine.setVisibility(View.VISIBLE);
	}

	/**
	 * Clear all views from the list
	 */
	public void clearList(){
		list.removeAllViews();
	}
}