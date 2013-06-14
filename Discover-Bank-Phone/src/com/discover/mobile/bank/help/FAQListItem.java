package com.discover.mobile.bank.help;

import java.io.Serializable;

import android.content.Context;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.discover.mobile.bank.R;


public class FAQListItem extends RelativeLayout implements Serializable {
	private static final long serialVersionUID = -8955167090262077757L;

	public FAQListItem(final Context context) {
		super(context);
		doSetup(context);
	}
	public FAQListItem(final Context context, final AttributeSet attrs) {
		super(context, attrs);
		doSetup(context);
	}
	public FAQListItem(final Context context, final AttributeSet attrs, final int defStyle) {
		super(context, attrs, defStyle);
		doSetup(context);
	}

	private void doSetup(final Context context) {
		final RelativeLayout view = getInflatedLayout(context);
		view.findViewById(R.id.faq_layout).setOnClickListener(expandClickListener);
		addView(view);
	}

	/**
	 * Returns the inflated layout for this list item.
	 * @param context the calling context.
	 * @return the inflated layout for this list item.
	 */
	private RelativeLayout getInflatedLayout(final Context context) {
		return (RelativeLayout)LayoutInflater.from(context).inflate(R.layout.faq_list_item, null);
	}

	/**
	 * Set the body text of this item.
	 * @param bodyText a String to use as the body text of this item.
	 */
	public void setBody(final String bodyText) {
		final TextView bodyLabel = (TextView)findViewById(R.id.faq_section_detail);

		if(bodyLabel != null){ 
			bodyLabel.setText(bodyText);
			bodyLabel.setText(Html.fromHtml(bodyText));
			bodyLabel.setMovementMethod(LinkMovementMethod.getInstance());
		}
	}

	/**
	 * Set the text of this list item's body content.
	 * @param title a String to use as the title content of this item.
	 */
	public void setTitle(final String title) {
		final TextView titleLabel = (TextView)findViewById(R.id.faq_section_title);

		if(titleLabel != null){
			titleLabel.setText(title);
		}
	}

	/**
	 * This is a click listener that allows the list items to expand and collapse upon clicking.
	 */
	protected final OnClickListener expandClickListener = new OnClickListener() {

		@Override
		public void onClick(final View v) {
			final String currentToggleSymbol = ((TextView)findViewById(R.id.expand_indicator)).getText().toString();
			toggleRowExpansion(currentToggleSymbol);
		}
	};

	private void toggleRowExpansion(final String toggleValue) {
		if(isOpen()){ 
			closeItem();
		}else{
			openItem();
		}
	}

	public boolean isOpen() {
		final TextView indicator = (TextView)findViewById(R.id.expand_indicator);
		final String openState = getResources().getString(R.string.hypen);
		final String currentState = indicator.getText().toString();

		return currentState.equals(openState); 
	}

	/**
	 * Expand the current faq list item.
	 * Sets the visibility of the detail item to be visible and changes the expand indicator to a minus sign.
	 */
	public void openItem() {
		findViewById(R.id.faq_section_detail).setVisibility(View.VISIBLE);
		((TextView)findViewById(R.id.expand_indicator)).setText(getResources().getString(R.string.hypen));
	}

	public void hideDivider() {
		final View view = findViewById(R.id.divider);

		if(view != null){
			view.setVisibility(View.GONE);
		}
	}

	/**
	 * Collapses the current faq list item.
	 * Sets the visibility of the detail item to GONE and changes the expand indicator to a plus sign.
	 */
	public void closeItem() {
		findViewById(R.id.faq_section_detail).setVisibility(View.GONE);
		((TextView)findViewById(R.id.expand_indicator)).setText(getResources().getString(R.string.plus));
	}
}
