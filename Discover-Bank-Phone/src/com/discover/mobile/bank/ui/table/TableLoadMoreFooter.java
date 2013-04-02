package com.discover.mobile.bank.ui.table;

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

	/**Divider line*/
	private final View line;

	/**Privacy and terms text*/
	private final TextView privacy;

	/**Constructor for the class
	 * @param context - activity context
	 * @param attrs - attributes to apply to the layouts
	 */
	public TableLoadMoreFooter(final Context context, final AttributeSet attrs) {
		super(context, attrs);

		final View view = LayoutInflater.from(context).inflate(R.layout.table_load_more_footer, null);

		load = view.findViewById(R.id.load_more);
		go = (TextView) view.findViewById(R.id.go_to_top);
		line = view.findViewById(R.id.line);
		privacy =  (TextView) view.findViewById(R.id.privacy_footer);

		hideAll();
		addView(view);
	}

	/**
	 * Show the loading bar
	 */
	public void showLoading(){
		load.setVisibility(View.VISIBLE);
		load.startAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.rotate_full_circle));
		go.setVisibility(View.VISIBLE);
		line.setVisibility(View.VISIBLE);
	}

	/**
	 * Show the more loading view
	 */
	public void showDone(){
		load.setVisibility(View.GONE);
		load.clearAnimation();
		go.setVisibility(View.VISIBLE);
		line.setVisibility(View.VISIBLE);
		privacy.setVisibility(View.VISIBLE);
	}

	/**
	 * Show the list is empty view
	 * @param message
	 */
	public void showEmpty(){
		load.setVisibility(View.GONE);
		load.clearAnimation();
		go.setVisibility(View.GONE);
		line.setVisibility(View.GONE);
		privacy.setVisibility(View.GONE);
	}

	/**
	 * Show the hide all view
	 */
	public void hideAll(){
		load.setVisibility(View.GONE);
		load.clearAnimation();
		go.setVisibility(View.GONE);
		line.setVisibility(View.GONE);
		privacy.setVisibility(View.GONE);
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
