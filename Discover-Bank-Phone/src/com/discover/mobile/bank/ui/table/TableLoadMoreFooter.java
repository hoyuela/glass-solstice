package com.discover.mobile.bank.ui.table;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.discover.mobile.bank.R;
import com.discover.mobile.bank.framework.BankConductor;
import com.discover.mobile.bank.help.PrivacyTermsType;
import com.discover.mobile.bank.ui.DiscoverOrangeSpinner;

/**
 * Load more footer for the tables.
 * @author jthornton
 *
 */
public class TableLoadMoreFooter extends RelativeLayout implements OnClickListener{

	/**View that shows the loading*/
	private final DiscoverOrangeSpinner load;

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

		load = (DiscoverOrangeSpinner)view.findViewById(R.id.load_more);
		go = (TextView) view.findViewById(R.id.go_to_top);
		line = view.findViewById(R.id.line);
		privacy =  (TextView) view.findViewById(R.id.privacy_footer);

		privacy.setOnClickListener(this);
		
		hideAll();
		addView(view);
	}

	/**
	 * Show the loading bar
	 */
	public void showLoading(){
		load.setVisibility(View.VISIBLE);
		load.startAnimation();
		go.setVisibility(View.VISIBLE);
		line.setVisibility(View.VISIBLE);
	}

	/**
	 * Show the more loading view
	 */
	public void showDone(){
		load.setVisibility(View.GONE);
		load.stopAnimation();
		go.setVisibility(View.VISIBLE);
		line.setVisibility(View.VISIBLE);
		
		/** Right Justify Privacy & Terms */
		final RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) privacy.getLayoutParams();
		params.addRule(RelativeLayout.CENTER_HORIZONTAL, 0);
		privacy.setLayoutParams(params);
	}

	/**
	 * Show the hide all view
	 */
	public final void hideAll(){		
		load.setVisibility(View.GONE);
		load.stopAnimation();
		go.setVisibility(View.GONE);
		line.setVisibility(View.GONE);
	}
	
	/**
	 * Centers the Privacy & Terms textView
	 */
	public void centerPrivacyText() {
		/** Center Justify Privacy & Terms */
		final RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) privacy.getLayoutParams();
		params.addRule(RelativeLayout.CENTER_HORIZONTAL, 1);
		params.setMargins(0, 0, 0, 0);
		privacy.setLayoutParams(params);
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

	@Override
	public void onClick(final View sender) {
		if( sender.getId() == privacy.getId() ) {
			BankConductor.navigateToPrivacyTerms(PrivacyTermsType.LandingPage);
		}
		
	}
}
