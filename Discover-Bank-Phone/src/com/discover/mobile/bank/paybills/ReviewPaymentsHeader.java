package com.discover.mobile.bank.paybills;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.ToggleButton;

import com.discover.mobile.bank.R;
import com.discover.mobile.bank.ui.table.TableTitles;

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

	/**Scheduled toggle button*/
	private final ToggleButton scheduled;

	/**Completed toggle button*/
	private final ToggleButton completed;

	/**Cancelled toggle button*/
	private final ToggleButton canceled;

	/**Table titles in the view*/
	private final TableTitles titles;

	/**Current category being displayed*/
	private int currentCategory;

	/**
	 * Constructor for the layout
	 * @param context - activity context
	 * @param attrs - attributes to apply to the layout
	 */
	public ReviewPaymentsHeader(final Context context, final AttributeSet attrs) {
		super(context, attrs);

		final View view = LayoutInflater.from(context).inflate(R.layout.review_payments_header, null);
		view.findViewById(R.id.table_titles);
		scheduled = (ToggleButton) view.findViewById(R.id.toggle_left);
		completed = (ToggleButton) view.findViewById(R.id.toggle_middle);
		canceled  = (ToggleButton) view.findViewById(R.id.toggle_right);
		titles = (TableTitles) view.findViewById(R.id.table_titles);

		titles.setLabel1(this.getResources().getString(R.string.review_payments_filter_one));
		titles.setLabel2(this.getResources().getString(R.string.review_payments_filter_two));
		titles.setLabel3(this.getResources().getString(R.string.review_payments_filter_three));
		addView(view);
	}

	/**
	 * Set the scheduled button selected and unselect the other buttons
	 */
	public void selectScheduledButton(){
		scheduled.setTextColor(getResources().getColor(R.color.white));
		completed.setTextColor(getResources().getColor(R.color.body_copy));
		canceled.setTextColor(getResources().getColor(R.color.body_copy));
		scheduled.setBackgroundDrawable(getResources().getDrawable(R.drawable.toggle_left_on));
		completed.setBackgroundDrawable(getResources().getDrawable(R.drawable.toggle_mid_off));
		canceled.setBackgroundDrawable(getResources().getDrawable(R.drawable.toggle_right_off));
	}

	/**
	 * Set the completed button selected and unselect the other buttons
	 */
	public void selectCompletedButton(){
		scheduled.setTextColor(getResources().getColor(R.color.body_copy));
		completed.setTextColor(getResources().getColor(R.color.white));
		canceled.setTextColor(getResources().getColor(R.color.body_copy));
		scheduled.setBackgroundDrawable(getResources().getDrawable(R.drawable.toggle_left_off));
		completed.setBackgroundDrawable(getResources().getDrawable(R.drawable.toggle_mid_on));
		canceled.setBackgroundDrawable(getResources().getDrawable(R.drawable.toggle_right_off));
	}

	/**
	 * Set the canceled button selected and unselect the other buttons
	 */
	public void selectCanceledButton(){
		scheduled.setTextColor(getResources().getColor(R.color.body_copy));
		completed.setTextColor(getResources().getColor(R.color.body_copy));
		canceled.setTextColor(getResources().getColor(R.color.white));
		scheduled.setBackgroundDrawable(getResources().getDrawable(R.drawable.toggle_left_off));
		completed.setBackgroundDrawable(getResources().getDrawable(R.drawable.toggle_mid_off));
		canceled.setBackgroundDrawable(getResources().getDrawable(R.drawable.toggle_right_on));
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
	public ToggleButton getScheduled() {
		return scheduled;
	}

	/**
	 * @return the completed
	 */
	public ToggleButton getCompleted() {
		return completed;
	}

	/**
	 * @return the canceled
	 */
	public ToggleButton getCanceled() {
		return canceled;
	}
}
