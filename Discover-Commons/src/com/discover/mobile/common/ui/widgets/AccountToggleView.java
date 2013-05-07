package com.discover.mobile.common.ui.widgets;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.discover.mobile.common.AccountType;
import com.discover.mobile.common.Globals;
import com.discover.mobile.common.R;
import com.discover.mobile.common.auth.KeepAlive;
import com.discover.mobile.common.facade.FacadeFactory;
import com.discover.mobile.common.ui.CardInfoForToggle;

/**
 * Widget that allows the user to toggle between accounts.
 * 
 * @author Samuel Frank
 * 
 */
public class AccountToggleView extends RelativeLayout {

	private final static int ID_INDICATOR = 2;

	private final Context context;

	private final View view;

	private final RelativeLayout bubble;
	private final RelativeLayout cardSection;
	private final RelativeLayout bankSection;

	private final ImageView indicator;
	private final ImageView dismissX;
	private final ImageView cardCheck;
	private final ImageView bankCheck;

	private final TextView cardName;
	private final TextView cardEnding;

	/** true if widget is shown, false otherwise. */
	private boolean isShown;

	/** Used to prevent redrawing of the indicator */
	private boolean isIndicatorDrawn = false;

	public AccountToggleView(final Context context, final AttributeSet attrs) {
		super(context, attrs);
		this.context = context;

		view = View.inflate(context, R.layout.account_toggle, this);
		indicator = (ImageView) view
				.findViewById(R.id.acct_toggle_indicator);
		bubble = (RelativeLayout) view
				.findViewById(R.id.acct_toggle_bubble);
		cardSection = (RelativeLayout) view
				.findViewById(R.id.acct_toggle_card_section);
		bankSection = (RelativeLayout) view
				.findViewById(R.id.acct_toggle_bank_section);
		dismissX = (ImageView) view.findViewById(R.id.acct_toggle_x);
		cardCheck = (ImageView) view
				.findViewById(R.id.acct_toggle_card_check);
		bankCheck = (ImageView) view
				.findViewById(R.id.acct_toggle_bank_check);
		cardEnding = (TextView) view.findViewById(R.id.acct_toggle_card_subtext);
		cardName = (TextView) view.findViewById(R.id.acct_toggle_card_text);

		indicator.setId(ID_INDICATOR);
		isShown = false;

		this.setVisibility(View.INVISIBLE);

		if(KeepAlive.getCardAuthenticated()){
			CardInfoForToggle cardInfo;
			if(null == Globals.getCardLastFour()){
				cardInfo = new CardInfoForToggle(Globals.getCardName(), Globals.getCardLastFour());
			}else{
				cardInfo = FacadeFactory.getCardFacade().getCardInfoForToggle(this.context);
				Globals.setCardLastFour(cardInfo.getCardEndingDigits());
				Globals.setCardName(cardInfo.getCardAccountName());
			}

			if(null != cardInfo){
				cardName.setText(cardInfo.getCardAccountName());
				cardEnding.setText(context.getResources().getString(
						R.string.account_ending_in)
						+ " " +cardInfo.getCardEndingDigits());
			}
		}

		setAccountType();
		setupListeners();
	}

	/**
	 * Sets the name of the card and its ending digits on this widget.
	 * 
	 * @param name
	 *            name of card.
	 * @param endingDigits
	 *            card's ending digits.
	 */
	public void setCardNameAndEnding(final String name, final String endingDigits) {
		Globals.setCardLastFour(endingDigits);
		Globals.setCardName(name);
		cardName.setText(name);
		cardEnding.setText(context.getResources().getString(
				R.string.account_ending_in)
				+ " " + endingDigits);
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
	public void setIndicatorPosition(final int left, final int top, final int iconWidth,
			final int iconHeight) {

		indicator.setPadding(left + (iconWidth / 3),
				top + (iconHeight / 3) + 5, 0, 0);

		final RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(
				RelativeLayout.LayoutParams.WRAP_CONTENT,
				RelativeLayout.LayoutParams.WRAP_CONTENT);
		lp.addRule(RelativeLayout.BELOW, indicator.getId());
		lp.setMargins(0, -3, 0, 0);
		bubble.setLayoutParams(lp);

		isIndicatorDrawn = true;
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
		bankSection.setOnClickListener(new BankListener());
		cardSection.setOnClickListener(new CardListener());
	}

	/**
	 * Sets the checkmarks based on the account type.
	 */
	private void setAccountType() {
		// Bank Account
		if (Globals.getCurrentAccount().equals(AccountType.BANK_ACCOUNT)) {
			bankCheck.setVisibility(View.VISIBLE);
			cardCheck.setVisibility(View.INVISIBLE);
		}
		// Card Account
		else {
			bankCheck.setVisibility(View.INVISIBLE);
			cardCheck.setVisibility(View.VISIBLE);
		}
	}

	/**
	 * Once the indicator is placed and drawn, there is no need to redraw it.
	 * @return true if indicator is drawn, false otherwise.
	 */
	public boolean hasIndicatorBeenDrawn() {
		return isIndicatorDrawn;
	}

	/**
	 * Listener for Bank Section
	 */
	private class BankListener implements OnClickListener {
		@Override
		public void onClick(final View arg0) {
			if (Globals.getCurrentAccount().equals(AccountType.CARD_ACCOUNT)) {
				// Switch to Bank
				bankCheck.setVisibility(View.VISIBLE);
				cardCheck.setVisibility(View.INVISIBLE);
				FacadeFactory.getCardLoginFacade().toggleLoginToBank(context);
			} else {
				// Dismiss; we're at Bank
				view.setVisibility(View.INVISIBLE);
			}
		}
	}

	/**
	 * Listener for Card Section
	 */
	private class CardListener implements OnClickListener {
		@Override
		public void onClick(final View arg0) {
			if (Globals.getCurrentAccount().equals(AccountType.BANK_ACCOUNT)) {
				// Switch to Card
				bankCheck.setVisibility(View.INVISIBLE);
				cardCheck.setVisibility(View.VISIBLE);
				FacadeFactory.getCardLoginFacade().toggleToCard(context);
			} else {
				// Dismiss; we're at Card
				view.setVisibility(View.INVISIBLE);
			}
		}
	}

	/**
	 * Listener that will hide the view when called.
	 */
	private class HideListener implements OnClickListener {

		@Override
		public void onClick(final View arg0) {
			view.setVisibility(View.INVISIBLE);
			isShown = false;
		}

	}
}