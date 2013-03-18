package com.discover.mobile.bank.deposit;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.util.TypedValue;
import android.widget.RelativeLayout;

import com.discover.mobile.bank.R;
import com.discover.mobile.bank.ui.widgets.SelectListItem;

public class BankDepositListGenerator {
	
	private BankDepositListGenerator() {
		
	}
	
	public static RelativeLayout createSelectListItem(final Context context, final String topText, final String bottomText) {
		final SelectListItem item = new SelectListItem(context);
		item.showImage(false);
		item.setTopLabelText(topText);
		item.setMiddleLabelText(bottomText);
		
		return item;
	}
	
	public static RelativeLayout createSelectListItem(final Context context, final String topText) {
		final SelectListItem item = new SelectListItem(context);
		item.showImage(false);
		item.setTopLabelText(topText);
		item.getTopLabel().setTextSize(TypedValue.COMPLEX_UNIT_SP, 17);
		item.showMiddleLabel(false);
	
		return item;
	}
	
	public static RelativeLayout createAccountItem(final Context context, final String topText, final String bottomText) {
		final SelectListItem item = (SelectListItem)createSelectListItem(context,topText,bottomText);
		final int padding = (int) context.getResources().getDimension(R.dimen.forms_inner_padding);
		item.getMainLayout().setPadding(padding, padding, padding, 0);
		item.showDivider(false);	
		return item;
	}
	
	public static RelativeLayout createAmountItem(final Context context, final String topText, final String bottomText) {
		final SelectListItem item = (SelectListItem)createSelectListItem(context,topText,bottomText);
		item.showDivider(false);	
		return item;
	}
	
	public static RelativeLayout createNoteItem(final Context context, final String topText) {
		final SelectListItem item = new SelectListItem(context);
		item.showImage(false);
		final String top = context.getResources().getString(R.string.bank_deposit_received_note);
	    String bottom = context.getResources().getString(R.string.bank_deposit_received_notetext);
		bottom = bottom.replace("{0}", topText);
		item.setTopLabelText(top);
		item.getTopLabel().setTextSize(TypedValue.COMPLEX_UNIT_SP, 17);	
		item.setMiddleLabelText(bottom);
		item.getMiddleLabel().setSingleLine(false);
		item.getMiddleLabel().setMaxLines(10);
		item.getMiddleLabel().setTextSize(TypedValue.COMPLEX_UNIT_SP, 17);	
		return item;
	}
	
	public static List<RelativeLayout> getDepositConfirmationList(final Context context, final Object item) {
		final List<RelativeLayout> list = new ArrayList<RelativeLayout>();
		final String confirmation = context.getResources().getString(R.string.bank_deposit_received_confirmation);
		
		list.add(createAccountItem(context, "Account ending in 1234", "Discover Online Checking"));
		list.add(createAmountItem(context, "Amount", "$X,XXX.XX"));
		list.add(createSelectListItem(context, confirmation +"000000000"));
		list.add(createNoteItem(context, "XX:XX PM" ));
		
		return list;
	}
}
