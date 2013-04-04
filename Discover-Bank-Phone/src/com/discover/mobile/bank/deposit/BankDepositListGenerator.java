package com.discover.mobile.bank.deposit;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.graphics.Typeface;
import android.util.TypedValue;
import android.widget.RelativeLayout;

import com.discover.mobile.bank.R;
import com.discover.mobile.bank.framework.BankUser;
import com.discover.mobile.bank.services.account.Account;
import com.discover.mobile.bank.services.deposit.DepositDetail;
import com.discover.mobile.bank.ui.widgets.SelectListItem;
import com.discover.mobile.bank.util.BankStringFormatter;

/**
 * Utility class with static methods used to generate the table displayed in BankDepositConfirmFragment.
 * This class is not instantiable.
 * 
 * @author henryoyuela
 *
 */
public class BankDepositListGenerator {
	
	/**
	 * Unused constructor, this class only has static methods.
	 */
	private BankDepositListGenerator() {
		
	}
	
	/**
	 * Instantiates a SelectListItem object with a top and bottom label.
	 * 
	 * @param context Reference to the current Context of the application used to instantiate UI objects.
	 * @param topText Text to be displayed in the top portion of the SelectListItem control.
	 * @param bottomText Text to be displayed in the bottom portion of the SelectListItem control
	 * @return Instance of a newly created SelectListItem object.
	 */
	public static RelativeLayout createSelectListItem(final Context context, final String topText, final String bottomText) {
		final SelectListItem item = new SelectListItem(context);
		item.showImage(false);
		item.setTopLabelText(topText);
		item.setMiddleLabelText(bottomText);
		
		return item;
	}
	
	/**
	 * Instantiates a SelectListItem object with a top lable and no bottom label.
	 * 
	 * @param context Reference to the current Context of the application used to instantiate UI objects.
	 * @param topText Text to be displayed in the top portion of the SelectListItem control.
	 *
	 * @return Instance of a newly created SelectListItem object.
	 */
	public static RelativeLayout createSelectListItem(final Context context, final String topText) {
		final SelectListItem item = new SelectListItem(context);
		item.showImage(false);
		item.setTopLabelText(topText);
		item.getTopLabel().setTextSize(TypedValue.COMPLEX_UNIT_SP, 17);
		item.showMiddleLabel(false);
	
		return item;
	}
	
	/**
	 * Creates a SelectListItem with the Account Information as required to be displayed for the
	 * Check Deposit Confirmation Page.
	 * 
	 * @param context Reference to the current Context of the application used to instantiate UI objects.
	 * @param accountID ID of the account to use for displaying account information in cell.
	 * @return Reference to newly created SelectListItem object.
	 */
	public static RelativeLayout createAccountItem(final Context context, final int accountID ) {
		final Account account = BankUser.instance().getAccount( Integer.toString(accountID) );
		
		/**Top text shows account number*/
		String topText = context.getResources().getString(R.string.account_ending_in) +" ";
		topText +=	account.accountNumber.ending;
		
		/**Bottom Text displays account nickname*/
		final String bottomText = account.nickname;
		

		final SelectListItem item = (SelectListItem)createSelectListItem(context,topText,bottomText);
		final int padding = (int) context.getResources().getDimension(R.dimen.forms_inner_padding);
		item.getMainLayout().setPadding(padding, padding, padding, 0);
		item.showDivider(false);	
		return item;
	}
	
	/**
	 * Creates a SelectListItem with the Amount Information as required to be displayed for the Check
	 * Deposit Confirmation Page.
	 * 
	 * @param context Reference to the current Context of the application used to instantiate UI objects.
	 * @param amount The amount that was deposited for the check
	 * @return Reference to newly created SelectListItem object.
	 */
	public static RelativeLayout createAmountItem(final Context context, final int amount) {
		/**top text shows the amount text*/
		final String topText = context.getString(R.string.bank_deposit_amount);
		
		/**bottom text shows the actual amount deposited*/
		final String bottomText = BankStringFormatter.convertCentsToDollars(amount);
		
		final SelectListItem item = (SelectListItem)createSelectListItem(context,topText,bottomText);
		item.showDivider(false);	
		return item;
	}
	
	/**
	 * Creates a SelectListItem with the Check Deposit Confirmation Information as required to be 
	 * displayed for the Check Deposit Confirmation Page.
	 * 
	 * @param context Reference to the current Context of the application used to instantiate UI objects.
	 * @param confirmation The confirmation number that was generated and provided from server after a successful check deposit
	 * @return Reference to newly created SelectListItem object.
	 */
	public static RelativeLayout createConfirmationItem(final Context context, final String confirmation) {	
		final String title = context.getResources().getString(R.string.bank_deposit_received_confirmation);
		final String text = title +" " +confirmation;
		
		final SelectListItem item = (SelectListItem)createSelectListItem(context, text);
		return item;
	}
	
	/**
	 * Creates a SelectListItem with the Note Information as required to be displayed for the Check
	 * Deposit Confirmation Page.
	 * 
	 * @param context Reference to the current Context of the application used to instantiate UI objects.
	 * @param topText Text to be displayed in the top portion of the SelectListItem control.
	 * @return Reference to newly created SelectListItem object.
	 */
	public static RelativeLayout createNoteItem(final Context context) {
		final SelectListItem item = new SelectListItem(context);
		item.showImage(false);
		final String top = context.getResources().getString(R.string.bank_deposit_received_note);
	    final String bottom = context.getResources().getString(R.string.bank_deposit_received_notetext);
		
		item.setTopLabelText(top);
		item.getTopLabel().setTextSize(TypedValue.COMPLEX_UNIT_SP, 17);	
		item.setMiddleLabelText(bottom);
		item.getMiddleLabel().setTypeface(null,Typeface.NORMAL);
		item.getMiddleLabel().setSingleLine(false);
		item.getMiddleLabel().setMaxLines(10);
		item.getMiddleLabel().setTextSize(TypedValue.COMPLEX_UNIT_SP, 17);	
		return item;
	}
	
	/**
	 * Generates a list of RelativeLayouts to be displayed in the content table for the Bank Check Deposit
	 * Confirmation Page.
	 * 
	 * @param context Reference to the current Context of the application used to instantiate UI objects.
	 * @param item TO BE Defined
	 * @return List of Relative Layouts
	 */
	public static List<RelativeLayout> getDepositConfirmationList(final Context context, final DepositDetail depositDetail) {
		final List<RelativeLayout> list = new ArrayList<RelativeLayout>();
		
		list.add(createAccountItem(context, depositDetail.account));
		list.add(createAmountItem(context,  depositDetail.amount.value));
		list.add(createConfirmationItem(context, depositDetail.confirmation));
		list.add(createNoteItem(context));
		
		return list;
	}
}
