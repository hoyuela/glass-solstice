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

	//TODO: Remove below

	/**Used to send a list of details to the detail screen from the table*/
	public static final String DATA_LIST = "data";

	/**Used to send the selected index of the detail to the detail screen*/
	public static final String DATA_SELECTED_INDEX = "index";

	/**Used to send the selected category to the detail screen*/
	public static final String CATEGORY_SELECTED = "category-selected";

	/**Used to send the sorting of the table to the detail fragment and back*/
	public static final String SORT_ORDER = "sort-order";

	/**Used to signify if the table title was expanded*/
	public static final String TITLE_EXPANDED = "expanded";

	/**Used to signify sort set up*/
	public static final int SORT_DATE_DESC = 0;
	public static final int SORT_DATE_ASC = 1;
	public static final int SORT_DESCRIP_DESC = 2;
	public static final int SORT_DESCRIP_ASC = 3;
	public static final int SORT_AMOUNT_DESC = 4;
	public static final int SORT_AMOUNT_ASC = 5;

	//TODO: Remove Above

	/**Title View*/
	protected TextView title;

	/**List shown to the user*/
	protected LinearLayout list;

	/**Empty messageTextView*/
	protected TextView empty;

	/**Linear layout holding the sort filters*/
	protected LinearLayout filters;

	/**Divider Line for the table*/
	protected View line;

	/**Activity context*/
	protected Context context;

	/**
	 * Constructor for the layout
	 * @param context - activity context
	 * @param attrs - layout attributes
	 */
	public BankTable(final Context context, final AttributeSet attrs){
		super(context, attrs);
		final RelativeLayout view = (RelativeLayout) LayoutInflater.from(context).inflate(R.layout.bank_table, null);

		this.title = (TextView) view.findViewById(R.id.table_title);
		this.list = (LinearLayout) view.findViewById(R.id.list);
		this.empty = (TextView) view.findViewById(R.id.empty);
		this.filters = (LinearLayout) view.findViewById(R.id.sort_filters);
		this.line = view.findViewById(R.id.top_line);
		this.context = context;

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
	 * Show the items in the list.
	 */
	public abstract void showItems();

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
		this.list.addView(item);
	}

	/**
	 * Show the no data message
	 */
	protected void showNoDataMessage(){
		this.title.setVisibility(View.GONE);
		this.empty.setVisibility(View.VISIBLE);
		this.filters.setVisibility(View.GONE);
		this.line.setVisibility(View.GONE);
		this.list.setVisibility(View.GONE);
		this.empty.setText(this.getResources().getString(getNoItemsMessage()));
	}

	/**
	 * Show has data view
	 */
	protected void showDataView(){
		this.title.setVisibility(View.VISIBLE);
		this.empty.setVisibility(View.GONE);
		this.filters.setVisibility(View.VISIBLE);
		this.line.setVisibility(View.VISIBLE);
		this.list.setVisibility(View.VISIBLE);
	}

	/**
	 * Clear all views from the list
	 */
	protected void clearList(){
		this.list.removeAllViews();
	}

	/**
	 * Get the current sort information
	 */
	protected int getSortState(){
		return 0;
	}
}