package com.discover.mobile.bank.account;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.discover.mobile.bank.R;

/**
 * Widget that allows the user to toggle between accounts.
 * 
 * @author Samuel Frank
 *
 */
public class AccountToggleView extends RelativeLayout {

	private final static int ID_INDICATOR = 2;
	public final static int ID_ICON = 3;

	private final View view;

	private final RelativeLayout bubble;
	private final RelativeLayout cardSection;
	private final RelativeLayout bankSection;

	private final ImageView indicator;
	private final ImageView dismissX;
	private final ImageView cardCheck;
	private final ImageView bankCheck;

	private boolean isShown;

	public AccountToggleView(Context context, AttributeSet attrs) {
		super(context, attrs);

		this.view = View.inflate(context, R.layout.account_toggle, this);
		this.indicator = (ImageView) view
				.findViewById(R.id.acct_toggle_indicator);
		this.bubble = (RelativeLayout) view
				.findViewById(R.id.acct_toggle_bubble);
		this.cardSection = (RelativeLayout) view
				.findViewById(R.id.acct_toggle_card_section);
		this.bankSection = (RelativeLayout) view
				.findViewById(R.id.acct_toggle_bank_section);
		this.dismissX = (ImageView) view.findViewById(R.id.acct_toggle_x);
		this.cardCheck = (ImageView) view
				.findViewById(R.id.acct_toggle_card_check);
		this.bankCheck = (ImageView) view
				.findViewById(R.id.acct_toggle_bank_check);

		this.indicator.setId(ID_INDICATOR);
		this.isShown = false;

		this.setVisibility(View.INVISIBLE);

		setupListeners();
	}

	/**
	 * Measures and draws the position of the indicator according to the given
	 * parameters.
	 * 
	 * @param left
	 *            left position of the icon
	 * @param top
	 *            top position of the icon
	 * @param iconWidth
	 *            width of icon
	 * @param iconHeight
	 *            height of icon
	 */
	public void setIndicatorPosition(int left, int top, int iconWidth,
			int iconHeight) {

		indicator.setPadding(left + (iconWidth / 3),
				top + (iconHeight / 3) + 5, 0, 0);

		RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(
				RelativeLayout.LayoutParams.WRAP_CONTENT,
				RelativeLayout.LayoutParams.WRAP_CONTENT);
		lp.addRule(RelativeLayout.BELOW, indicator.getId());
		lp.setMargins(0, -17, 0, 0);
		bubble.setLayoutParams(lp);
	}

	/**
	 * Toggles the visibility of this view.
	 */
	public void toggleVisibility() {
		if (!isShown) {
			this.setVisibility(View.VISIBLE);
			isShown = true;
		} else {
			this.setVisibility(View.INVISIBLE);
			isShown = false;
		}
	}

	/**
	 * Setup various listeners associated with this widget.
	 */
	private void setupListeners() {
		dismissX.setOnClickListener(new HideListener());
		bankSection.setOnClickListener(new HideListener());
		cardSection.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				bankCheck.setVisibility(View.INVISIBLE);
				cardCheck.setVisibility(View.VISIBLE);
				// TODO Call CardFacade.switch to Bank?
			}
		});
	}

	/**
	 * Listener that will hide the view when called.
	 */
	private class HideListener implements OnClickListener {

		@Override
		public void onClick(View arg0) {
			view.setVisibility(View.INVISIBLE);
			isShown = false;
		}

	}

}
