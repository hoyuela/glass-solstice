package com.discover.mobile.bank.account;

import android.content.Context;
import android.text.SpannableString;
import android.text.style.ImageSpan;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.discover.mobile.bank.BankExtraKeys;
import com.discover.mobile.bank.BankUser;
import com.discover.mobile.bank.R;
import com.discover.mobile.bank.services.account.Account;
import com.discover.mobile.bank.ui.Animator;
import com.discover.mobile.bank.ui.table.TableTitles;

/**
 * Header displayed at the top of the activity table view screen
 * @author jthornton
 *
 */
public class AccountActivityHeader extends RelativeLayout{

	/**Layout view*/
	private final View view;

	/**Value holding the checking name*/
	private final TextView checking;

	/**Value holding the available balance*/
	private final TextView availableBalance;

	/**Value holding the current balance*/
	private final TextView currentBalance;

	/**Title of the header*/
	private final TextView title;

	/**Posted activity toggle button*/
	private final ToggleButton postedButton;

	/**Scheduled activity toggle button*/
	private final ToggleButton scheduledButton;

	/**Boolean set to true if the user id viewing posted activity*/
	private boolean isPosted = true;

	/**Boolean set to true if the header is expanded*/
	private boolean isHeaderExpanded = false;

	/**Current int representing the sort order*/
	private int sortOrder = BankExtraKeys.SORT_DATE_DESC;

	/**Table titles in the view*/
	private final TableTitles titles;

	/**View holding labels*/
	private final RelativeLayout labels;

	/**Collapse amimation*/
	private final Animation collapse;

	/**Expand animation*/
	private final Animation expand;

	/**Current Account*/
	private final Account account;

	/**
	 * Constructor of the class
	 * @param context - activity context
	 * @param attrs - attributes to give to the layout
	 */
	public AccountActivityHeader(final Context context, final AttributeSet attrs) {
		super(context, attrs);

		view = LayoutInflater.from(context).inflate(R.layout.bank_account_activity_header, null);
		checking = (TextView)view.findViewById(R.id.value1);
		availableBalance = (TextView)view.findViewById(R.id.value2);
		currentBalance = (TextView)view.findViewById(R.id.value3);
		title = (TextView) view.findViewById(R.id.title_text);
		postedButton = (ToggleButton) view.findViewById(R.id.posted_button);
		scheduledButton = (ToggleButton) view.findViewById(R.id.scheduled_button);
		labels = (RelativeLayout) view.findViewById(R.id.header_labels);

		titles = (TableTitles) view.findViewById(R.id.table_titles);

		titles.setLabel1(this.getResources().getString(R.string.recent_activity_date));
		titles.setLabel2(this.getResources().getString(R.string.recent_activity_description));
		titles.setLabel3(this.getResources().getString(R.string.recent_activity_amount));

		collapse = Animator.collapse(labels);
		expand = Animator.expand(labels);
		collapse.setAnimationListener(new AnimationListener(){

			@Override
			public void onAnimationEnd(final Animation animation) { 
				changeVisibility(View.GONE);
			}

			@Override
			public void onAnimationRepeat(final Animation animation) { }

			@Override
			public void onAnimationStart(final Animation animation) { }

		});

		expand.setAnimationListener(new AnimationListener(){

			@Override
			public void onAnimationEnd(final Animation animation) { 	
			}

			@Override
			public void onAnimationRepeat(final Animation animation) { }

			@Override
			public void onAnimationStart(final Animation animation) { 
				changeVisibility(View.VISIBLE);
			}

		});

		title.setOnClickListener(getImageOnClickListener());
		account = BankUser.instance().getCurrentAccount();

		addAccount();
		setHeaderExpanded(isHeaderExpanded);
		setSelectedCategory(isPosted);
		addView(view);
	}

	/**
	 * Add the account and display it 
	 * @param account - account to add
	 */
	public void addAccount(){
		if(null == account){return;}
		title.setText(account.nickname);
		checking.setText(account.accountNumber.formatted);
		availableBalance.setText(account.balance.formatted);
		currentBalance.setText(account.balance.formatted);
		setSpan(R.drawable.drk_blue_arrow_down);
	}

	/**
	 * Set the image span
	 */
	private void setSpan(final int res){
		final ImageSpan imagespan = new ImageSpan(this.getContext(), res, ImageSpan.ALIGN_BASELINE); 
		final SpannableString text = new SpannableString(account.nickname + "  ");
		text.setSpan(imagespan, text.length()-1, text.length(), SpannableString.SPAN_INCLUSIVE_INCLUSIVE);
		title.setText(text);
	}

	/**
	 * Get image on click listener
	 * @return image on click listener
	 */
	public OnClickListener getImageOnClickListener(){
		return new OnClickListener(){
			@Override
			public void onClick(final View v){
				if(availableBalance.getVisibility() == View.VISIBLE){
					setSpan(R.drawable.drk_blue_arrow_down);
					labels.startAnimation(collapse);
					setHeaderExpanded(false);
				}else{
					setSpan(R.drawable.drk_blue_arrow_up);
					labels.startAnimation(expand);
					setHeaderExpanded(true);
				}
			}
		};

	}

	/**
	 * Change the visibility of the items under the account title
	 * @param visibility - the visibility of the items under the account title
	 */
	public void changeVisibility(final int visibility){
		view.findViewById(R.id.lable1).setVisibility(visibility);
		view.findViewById(R.id.lable2).setVisibility(visibility);
		view.findViewById(R.id.lable3).setVisibility(visibility);
		availableBalance.setVisibility(visibility);
		currentBalance.setVisibility(visibility);
		checking.setVisibility(visibility);
	}

	/**
	 * Get the help button in the header
	 * @return the help button in the header
	 */
	public ImageButton getHelp(){
		return (ImageButton) view.findViewById(R.id.help);
	}

	/**
	 * Toggle the buttons look and feel
	 * @param checked - toggle button that is checked
	 * @param notChecke - toggle button that is not checked
	 * @param isPosted - boolean to set is posted equal to
	 */
	public void toggleButton(final ToggleButton checked, final ToggleButton notChecked, final boolean isPosted){
		checked.setTextColor(getResources().getColor(R.color.white));
		notChecked.setTextColor(getResources().getColor(R.color.body_copy));
		if(isPosted){
			notChecked.setBackgroundDrawable(getResources().getDrawable(R.drawable.toggle_right_off));
			checked.setBackgroundDrawable(getResources().getDrawable(R.drawable.toggle_left_on));
		}else{
			notChecked.setBackgroundDrawable(getResources().getDrawable(R.drawable.toggle_left_off));
			checked.setBackgroundDrawable(getResources().getDrawable(R.drawable.toggle_right_on));
		}
		notChecked.setChecked(false);
		this.setPosted(isPosted);
	}

	/**
	 * @return the postedButton
	 */
	public ToggleButton getPostedButton() {
		return postedButton;
	}

	/**
	 * @return the scheduledButton
	 */
	public ToggleButton getScheduledButton() {
		return scheduledButton;
	}

	/**
	 * @return the isPosted
	 */
	public boolean isPosted() {
		return isPosted;
	}

	/**
	 * @param isPosted the isPosted to set
	 */
	public void setPosted(final boolean isPosted) {
		this.isPosted = isPosted;
	}

	/**
	 * @return the isHeaderExpanded
	 */
	public boolean isHeaderExpanded() {
		return isHeaderExpanded;
	}

	/**
	 * @param isHeaderExpanded the isHeaderExpanded to set
	 */
	public void setHeaderExpanded(final boolean isHeaderExpanded) {
		this.isHeaderExpanded = isHeaderExpanded;
		if(isHeaderExpanded){
			AccountActivityHeader.this.changeVisibility(View.VISIBLE);
			setSpan(R.drawable.drk_blue_arrow_up);
		}else{
			AccountActivityHeader.this.changeVisibility(View.GONE);
			setSpan(R.drawable.drk_blue_arrow_down);
		}
	}

	/**
	 * Get the sort order
	 * @return the sort order
	 */
	public int getSortOrder(){
		return sortOrder;
	}

	/**
	 * Set the sort order
	 * @param order - sort order to set
	 */
	public void setSortOrder(final int order){
		sortOrder = order;
	}

	/**
	 * Return the selected category
	 * @return the selected category
	 */
	public boolean getSelectedCategory() {
		return isPosted;
	}

	/**
	 * Set the selected category
	 * @param the selected category
	 */
	public void setSelectedCategory(final boolean isPosted) {
		this.isPosted = isPosted;
		if(isPosted){
			toggleButton(postedButton, scheduledButton, isPosted);
		}else{
			toggleButton(scheduledButton, postedButton, isPosted);			
		}
	}
}