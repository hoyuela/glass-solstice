package com.discover.mobile.bank.ui.widgets;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.discover.mobile.bank.R;
import com.discover.mobile.bank.framework.BankConductor;
import com.discover.mobile.bank.help.PrivacyTermsType;
import com.discover.mobile.bank.util.BankNeedHelpFooter;

public class BankLayoutFooter extends LinearLayout implements OnClickListener {
	private TextView divider;
	private Button provideFeedback;
	private Button privacyTerms;
	private TextView copyRight;
	private BankNeedHelpFooter helpFooter;
	private String provideFeedbackUrl;

	/**Set to true when the footer needs to send the user to card pages*/
	private boolean cardMode = false;

	public BankLayoutFooter(final Context context) {
		super(context);

		initialize(context);
	}

	public BankLayoutFooter(final Context context, final AttributeSet attrs) {
		super(context, attrs);

		initialize(context);
		updateAttributes(context, attrs);

	}

	@SuppressLint("NewApi")
	public BankLayoutFooter(final Context context, final AttributeSet attrs,
			final int defStyle) {
		super(context, attrs, defStyle);

		initialize(context);
		updateAttributes(context, attrs);
	}

	private LinearLayout getInflatedLayout(final Context context) {
		return (LinearLayout) LayoutInflater.from(context).inflate(
				R.layout.bank_layout_footer, null);
	}

	private void updateAttributes(final Context context, final AttributeSet attrs) {
		final TypedArray a = context
				.obtainStyledAttributes(
						attrs,
						R.styleable.com_discover_mobile_bank_ui_widgets_BankLayoutFooter);

		final int N = a.getIndexCount();
		for (int i = 0; i < N; ++i) {
			final int attr = a.getIndex(i);
			switch (attr) {
				case R.styleable.com_discover_mobile_bank_ui_widgets_BankLayoutFooter_footerType:
					final int footerType = a.getInt(attr,FooterType.PRIVACY_TERMS);
					this.setFooterType(footerType);
					break;
				case R.styleable.com_discover_mobile_bank_ui_widgets_BankLayoutFooter_helpNumber:
					final String helpNumber = a.getString(attr);
					helpFooter.setToDialNumberOnClick(helpNumber);
					break;
				case R.styleable.com_discover_mobile_bank_ui_widgets_BankLayoutFooter_provideFeedbackUrl:
					provideFeedbackUrl = a.getString(attr);
					break;
			}
		}
		a.recycle();

	}

	public void setHelpNumber(final String value ) {
		helpFooter.setToDialNumberOnClick(value);
	}

	public void promptForHelp(final boolean value) {
		helpFooter.promptForHelp(value);
	}

	private void initialize(final Context context) {
		this.addView(getInflatedLayout(context));

		/** Load UI Widgets */
		divider = (TextView) this.findViewById(R.id.divider);
		provideFeedback = (Button) this.findViewById(R.id.provide_feedback_button);
		privacyTerms = (Button) this.findViewById(R.id.privacy_terms);
		copyRight = (TextView)this.findViewById(R.id.discover_copyright);

		/**
		 * Create footer that will listen when user taps on Need Help Number to
		 * dial
		 */
		helpFooter = new BankNeedHelpFooter(this);
		helpFooter.setToDialNumberOnClick(com.discover.mobile.bank.R.string.bank_need_help_number_text);

		/** Set Click Listeners */
		divider.setOnClickListener(this);
		provideFeedback.setOnClickListener(this);
		privacyTerms.setOnClickListener(this);

		/**Default to privacy terms*/
		setFooterType(FooterType.PRIVACY_TERMS);

	}

	public final void setFooterType(final int type) {
		/** Check if Need Help Footer should be shown */
		if (FooterType.NEED_HELP == (type & FooterType.NEED_HELP)) {
			helpFooter.show(true);
		} else {
			helpFooter.show(false);
		}

		/** Check if Provide Feedback Should be shown */
		if (FooterType.PROVIDE_FEEDBACK == (type & FooterType.PROVIDE_FEEDBACK)) {
			provideFeedback.setVisibility(View.VISIBLE);
		} else {
			provideFeedback.setVisibility(View.GONE);
		}

		/** Check if Privacy & Terms Should be shown */
		if (FooterType.PRIVACY_TERMS == (type & FooterType.PRIVACY_TERMS)) {
			privacyTerms.setVisibility(View.VISIBLE);
		} else {
			privacyTerms.setVisibility(View.GONE);
		}

		/** Check if Copyright Should be shown */
		if (FooterType.COPYRIGHT == (type & FooterType.COPYRIGHT)) {
			copyRight.setVisibility(View.VISIBLE);
		} else {
			copyRight.setVisibility(View.GONE);
		}

		/**
		 * Check if divider should be shwon between provide back and privacy and
		 * terms
		 */
		if (provideFeedback.getVisibility() == View.VISIBLE
				&& privacyTerms.getVisibility() == View.VISIBLE) {
			divider.setVisibility(View.VISIBLE);
		} else {
			divider.setVisibility(View.GONE);
		}
	}

	@Override
	public void onClick(final View sender) {
		if (provideFeedback.getId() == sender.getId()) {
			BankConductor.navigateToFeedback(cardMode, provideFeedbackUrl);
		} else if (privacyTerms.getId() == sender.getId()) {
			if(!cardMode){
				BankConductor.navigateToPrivacyTerms(PrivacyTermsType.LandingPage);
			}else{
				BankConductor.navigateToCardPrivacyAndTermsLanding();
			}
		}
	}

	/**
	 * @return the cardMode
	 */
	public boolean isCardMode() {
		return cardMode;
	}

	/**
	 * @param cardMode the cardMode to set
	 */
	public void setCardMode(final boolean cardMode) {
		this.cardMode = cardMode;
	}

	
	public String getProvideFeedbackUrl() {
		return provideFeedbackUrl;
	}

	
	public void setProvideFeedbackUrl(String provideFeedbackUrl) {
		this.provideFeedbackUrl = provideFeedbackUrl;
	}
}
