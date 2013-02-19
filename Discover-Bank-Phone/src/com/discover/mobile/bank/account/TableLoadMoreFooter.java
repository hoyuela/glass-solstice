package com.discover.mobile.bank.account;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.discover.mobile.bank.R;

/**
 * Load more footer for the tables.
 * @author jthornton
 *
 */
public class TableLoadMoreFooter extends RelativeLayout{

	/**View that shows the loading*/
	private final View load;

	/**View that shows the go to top*/
	private final TextView go;

	/**Empty view for the message*/
	private final TextView empty;

	/**Constructor for the class
	 * @param context - activity context
	 * @param attrs - attributes to apply to the layouts
	 */
	public TableLoadMoreFooter(final Context context, final AttributeSet attrs) {
		super(context, attrs);

		final View view = LayoutInflater.from(context).inflate(R.layout.table_load_more_footer, null);

		load = view.findViewById(R.id.load_more);
		go = (TextView) view.findViewById(R.id.go_to_top);
		empty = (TextView) view.findViewById(R.id.message);
		hideAll();
		addView(view);
	}

	/**
	 * Show the loading bar
	 */
	public void showLoading(){
		load.setVisibility(View.GONE);
		load.startAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.rotate_full_circle));
		go.setVisibility(View.INVISIBLE);
		empty.setVisibility(View.INVISIBLE);
	}

	/**
	 * Show the more loading view
	 */
	public void showDone(){
		load.setVisibility(View.GONE);
		load.clearAnimation();
		go.setVisibility(View.VISIBLE);
		empty.setVisibility(View.INVISIBLE);
	}

	/**
	 * Show the list is empty view
	 * @param message
	 */
	public void showEmpty(final String message){
		load.setVisibility(View.GONE);
		load.clearAnimation();
		go.setVisibility(View.INVISIBLE);
		empty.setVisibility(View.VISIBLE);
		empty.setText(message);
	}

	/**
	 * Show the hide all view
	 */
	public void hideAll(){
		load.setVisibility(View.GONE);
		load.clearAnimation();
		go.setVisibility(View.GONE);
		empty.setVisibility(View.GONE);
	}

	/**
	 * @return the load
	 */
	public View getLoad() {
		return load;
	}

	/**
	 * @return the top
	 */
	public TextView getGo() {
		return go;
	}
}
