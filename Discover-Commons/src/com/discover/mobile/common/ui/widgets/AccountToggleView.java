package com.discover.mobile.common.ui.widgets;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.discover.mobile.common.AccountType;
import com.discover.mobile.common.Globals;
import com.discover.mobile.common.R;
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

	public AccountToggleView(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.context = context;

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
		this.cardEnding = (TextView) view.findViewById(R.id.acct_toggle_card_subtext);
		this.cardName = (TextView) view.findViewById(R.id.acct_toggle_card_text);

		this.indicator.setId(ID_INDICATOR);
		this.isShown = false;

		this.setVisibility(View.INVISIBLE);
		
		CardInfoForToggle cardInfo = FacadeFactory.getCardFacade().getCardInfoForToggle();

		cardName.setText(cardInfo.getCardAccountName());
		cardEnding.setText(context.getResources().getString(
				R.string.account_ending_in)
				+ " " +cardInfo.getCardEndingDigits());

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
	public void setCardNameAndEnding(String name, String endingDigits) {
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
	public void setIndicatorPosition(int left, int top, int iconWidth,
			int iconHeight) {

		indicator.setPadding(left + (iconWidth / 3),
				top + (iconHeight / 2), 0, 0);

		RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(
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
		Animation fade = null;
		
		if (!isShown) {
			fade = AnimationUtils.loadAnimation(context, R.anim.fade_in_animation_large);
			this.setVisibility(View.VISIBLE);
			this.startAnimation(fade);
			isShown = true;
		} else {
			fade = AnimationUtils.loadAnimation(context, R.anim.fade_out_animation_large);
			this.setVisibility(View.INVISIBLE);
			this.startAnimation(fade);
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
		public void onClick(View arg0) {
			if (Globals.getCurrentAccount().equals(AccountType.CARD_ACCOUNT)) {
				// Switch to Bank
				bankCheck.setVisibility(View.VISIBLE);
				cardCheck.setVisibility(View.INVISIBLE);
				FacadeFactory.getCardLoginFacade().toggleLoginToBank();
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
		public void onClick(View arg0) {
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
		public void onClick(View arg0) {
			toggleVisibility();
		}

	}
}
