package com.discover.mobile.bank.help;

import android.content.Context;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.text.util.Linkify;
import android.util.AttributeSet;
import android.widget.TextView;

import com.discover.mobile.bank.R;


public class CardFAQListItem extends FAQListItem{

	private static final long serialVersionUID = -6678508654693985002L;

	public CardFAQListItem(final Context context) {
		super(context);
	}
	public CardFAQListItem(final Context context, final AttributeSet attrs) {
		super(context, attrs);
	}
	public CardFAQListItem(final Context context, final AttributeSet attrs, final int defStyle) {
		super(context, attrs, defStyle);
	}

	/**
	 * Set the body text of this item.
	 * @param bodyText a String to use as the body text of this item.
	 */
	@Override
	public void setBody(final String bodyText) {
		final TextView bodyLabel = (TextView)findViewById(R.id.faq_section_detail);

		if(bodyLabel != null){ 
			bodyLabel.setText(Html.fromHtml(bodyText));
			bodyLabel.setMovementMethod(LinkMovementMethod.getInstance());
			bodyLabel.setLinksClickable(true);
			bodyLabel.setLinkTextColor(this.getContext().getResources().getColor(R.color.blue_link));
			Linkify.addLinks(bodyLabel, Linkify.ALL);
		}
	}

	/**
	 * Set the text of this list item's body content.
	 * @param title a String to use as the title content of this item.
	 */
	@Override
	public void setTitle(final String title) {
		final TextView titleLabel = (TextView)findViewById(R.id.faq_section_title);

		if(titleLabel != null){
			titleLabel.setText(Html.fromHtml(title));
		}
	}
}
