package com.discover.mobile.bank.paybills;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RelativeLayout;

import com.discover.mobile.bank.R;
import com.discover.mobile.bank.ui.table.TableTitles;
import com.discover.mobile.bank.ui.widgets.StatusMessageView;
import com.discover.mobile.common.ui.table.TableButtonGroup;
import com.discover.mobile.common.ui.table.TableHeaderButton;

/**
 * Header for the review payments list.  This contains the controls for the buttons.
 * @author jthornton
 *
 */
public class ReviewPaymentsHeader extends RelativeLayout{

	/**Int value the represents at the list is showing scheduled payments*/
	public static final int SCHEDULED_PAYMENTS = 0;

	/**Int value the represents at the list is showing completed payments*/
	public static final int COMPLETED_PAYMENTS = 1;

	/**Int value the represents at the list is showing canceled payments*/
	public static final int CANCELED_PAYMENTS = 2;

	/**Duration the message to show*/
	public static final int DURATION = 5000;

	/**Table titles in the view*/
	private final TableTitles titles;

	/**Group holding the buttons*/
	final TableButtonGroup group;

	/**Current category being displayed*/
	private int currentCategory;

	/**Status message*/
	private final StatusMessageView status;

	/**
	 * Constructor for the layout
	 * @param context - activity context
	 * @param attrs - attributes to apply to the layout
	 */
	public ReviewPaymentsHeader(final Context context, final AttributeSet attrs) {
		super(context, attrs);

		final View view = LayoutInflater.from(context).inflate(R.layout.review_payments_header, null);
		titles = (TableTitles) view.findViewById(R.id.table_titles);
		status = (StatusMessageView) view.findViewById(R.id.status);
		group = (TableButtonGroup) view.findViewById(R.id.buttons);
		titles.setLabel1(getResources().getString(R.string.review_payments_filter_one));
		titles.setLabel2(getResources().getString(R.string.review_payments_filter_two));
		titles.setLabel3(getResources().getString(R.string.review_payments_filter_three));
		addView(view);
	}

	/**
	 * Set the scheduled button selected and unselect the other buttons
	 */
	public void selectScheduledButton(){
		group.setButtonSelected(SCHEDULED_PAYMENTS);
	}

	/**
	 * Set the completed button selected and unselect the other buttons
	 */
	public void selectCompletedButton(){
		group.setButtonSelected(COMPLETED_PAYMENTS);
	}

	/**
	 * Set the canceled button selected and unselect the other buttons
	 */
	public void selectCanceledButton(){
		group.setButtonSelected(CANCELED_PAYMENTS);
	}

	/**
	 * Show the status message
	 */
	public void showStatusMessage(){
		status.setText(R.string.review_payments_scheduled_deleted);
		status.showAndHide(DURATION);
	}

	/**
	 * @return the currentCategory
	 */
	public int getCurrentCategory() {
		return currentCategory;
	}

	/**
	 * @param currentCategory the currentCategory to set
	 */
	public void setCurrentCategory(final int currentCategory) {
		this.currentCategory = currentCategory;
		if(currentCategory == ReviewPaymentsHeader.SCHEDULED_PAYMENTS){
			selectScheduledButton();
		}else if(currentCategory == ReviewPaymentsHeader.COMPLETED_PAYMENTS){
			selectCompletedButton();
		}else{
			selectCanceledButton();
		}
	}

	/**
	 * @return the scheduled
	 */
	public TableHeaderButton getScheduled() {
		return group.getButton(SCHEDULED_PAYMENTS);
	}

	/**
	 * @return the completed
	 */
	public TableHeaderButton getCompleted() {
		return group.getButton(COMPLETED_PAYMENTS);
	}

	/**
	 * @return the canceled
	 */
	public TableHeaderButton getCanceled() {
		return group.getButton(CANCELED_PAYMENTS);
	}

	/**
	 * Set the message in the table titles
	 * @param - message to set in the titles
	 */
	public void setMessage(final String message){
		titles.setMessage(message);
	}

	/**
	 * Clear the message in the titles
	 */
	public void clearMessage(){
		titles.hideMessage();
	}

	/**
	 * Set the observers to the group
	 * @param observer - observer of the buttons
	 */
	public void setGroupObserver(final OnClickListener observer){
		group.addObserver(observer);
	}

	/**
	 * Remove the listeners attached to the buttons.  Essentially relay the message to the
	 * group to unregister the observer.
	 */
	public void removeListeners(){
		group.removeObserver();
	}

}
