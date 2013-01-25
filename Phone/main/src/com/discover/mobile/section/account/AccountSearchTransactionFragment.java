package com.discover.mobile.section.account;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.discover.mobile.BaseFragment;
import com.discover.mobile.R;

public class AccountSearchTransactionFragment extends BaseFragment {

	ToggleButton dateLeft, dateMid, dateRight, amtLeft, amtMid, amtRight;

	@Override
	public View onCreateView(final LayoutInflater inflater,
			final ViewGroup container, final Bundle savedInstanceState) {

		final View view = inflater.inflate(R.layout.transaction_search_landing,
				null);

		setupDateToggleListeners(view);

		return view;
	}

	/**
	 * Creates the various listeners for Date and Amount selection.
	 */
	private void setupDateToggleListeners(View v) {
		RadioGroup dateGroup = (RadioGroup) v
				.findViewById(R.id.transaction_date_selector);
		RadioGroup amtGroup = (RadioGroup) v
				.findViewById(R.id.transaction_amount_selector);

		RelativeLayout dateFrom = (RelativeLayout) v
				.findViewById(R.id.date_from_element);
		RelativeLayout dateTo = (RelativeLayout) v
				.findViewById(R.id.date_to_element);
		RelativeLayout amtFrom = (RelativeLayout) v
				.findViewById(R.id.amount_from_element);
		RelativeLayout amtTo = (RelativeLayout) v
				.findViewById(R.id.amount_to_element);

		dateLeft = (ToggleButton) dateGroup.findViewById(R.id.toggle_left);
		dateMid = (ToggleButton) dateGroup.findViewById(R.id.toggle_middle);
		dateRight = (ToggleButton) dateGroup.findViewById(R.id.toggle_right);

		amtLeft = (ToggleButton) amtGroup.findViewById(R.id.toggle_left);
		amtMid = (ToggleButton) amtGroup.findViewById(R.id.toggle_middle);
		amtRight = (ToggleButton) amtGroup.findViewById(R.id.toggle_right);

		createDateToggleListener(dateLeft, dateGroup, dateFrom, dateTo);
		createDateToggleListener(dateMid, dateGroup, dateFrom, dateTo);
		createDateToggleListener(dateRight, dateGroup, dateFrom, dateTo);

		createAmountToggleListener(amtLeft, amtGroup, amtFrom, amtTo);
		createAmountToggleListener(amtMid, amtGroup, amtFrom, amtTo);
		createAmountToggleListener(amtRight, amtGroup, amtFrom, amtTo);
	}

	/**
	 * Return the integer value of the string that needs to be displayed in the
	 * title
	 */
	@Override
	public int getActionBarTitle() {
		return R.string.search_transactions_title;
	}

	/**
	 * Creates the Click listeners for the Date ToggleButtons.
	 * 
	 * @param toggleButton
	 *            the toggleButton for which to create the listener.
	 * @param group
	 *            the RadioGroup to which the ToggleButton belongs.
	 */
	private void createDateToggleListener(final ToggleButton toggleButton,
			final RadioGroup group, final RelativeLayout fromField,
			final RelativeLayout toField) {

		toggleButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(final View v) {

				toggleById(group, toggleButton.getId());

				switch (toggleButton.getId()) {
				case R.id.toggle_left:
					fromField.setVisibility(View.GONE);
					toField.setVisibility(View.GONE);
					break;
				case R.id.toggle_middle:
					fromField.setVisibility(View.VISIBLE);
					((TextView) fromField.findViewById(R.id.date_input_title))
							.setText(getResources().getString(
									R.string.chooser_date_title));
					toField.setVisibility(View.INVISIBLE);
					break;
				case R.id.toggle_right:
					fromField.setVisibility(View.VISIBLE);
					((TextView) fromField.findViewById(R.id.date_input_title))
							.setText(getResources().getString(
									R.string.chooser_from_title));
					toField.setVisibility(View.VISIBLE);
					((TextView) toField.findViewById(R.id.date_input_title))
							.setText(getResources().getString(
									R.string.chooser_to_title));
					break;
				}
			}

		});
	}

	/**
	 * Creates the Click listeners for the Date ToggleButtons.
	 * 
	 * @param toggleButton
	 *            the toggleButton for which to create the listener.
	 * @param group
	 *            the RadioGroup to which the ToggleButton belongs.
	 */
	private void createAmountToggleListener(final ToggleButton toggleButton,
			final RadioGroup group, final RelativeLayout fromField,
			final RelativeLayout toField) {

		toggleButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(final View v) {

				toggleById(group, toggleButton.getId());

				switch (toggleButton.getId()) {
				case R.id.toggle_left:
					fromField.setVisibility(View.GONE);
					toField.setVisibility(View.GONE);
					break;
				case R.id.toggle_middle:
					fromField.setVisibility(View.VISIBLE);
					((TextView) fromField.findViewById(R.id.amount_input_title))
							.setText(getResources().getString(
							
									R.string.chooser_amount_title));
					toField.setVisibility(View.INVISIBLE);
					break;
				case R.id.toggle_right:
					fromField.setVisibility(View.VISIBLE);
					((TextView) fromField.findViewById(R.id.amount_input_title))
							.setText(getResources().getString(
									R.string.chooser_from_title));
					toField.setVisibility(View.VISIBLE);
					((TextView) toField.findViewById(R.id.amount_input_title))
							.setText(getResources().getString(
									R.string.chooser_to_title));
					break;
				}
			}

		});
	}

	/**
	 * Toggles the clicked button and untoggles the rest in the group.
	 * 
	 * @param group
	 *            the group to which the ToggleButton belongs
	 * @param checkedId
	 *            the id of the ToggleButton
	 */
	public void toggleById(final RadioGroup group, final int checkedId) {

		for (int i = 0; i < group.getChildCount(); i++) {
			if (group.getChildAt(i) instanceof ToggleButton) {
				final ToggleButton view = (ToggleButton) group.getChildAt(i);
				if (view.getId() == checkedId) {
					view.setChecked(true);
					view.setTextColor(getResources().getColor(R.color.white));
				} else {
					view.setChecked(false);
					view.setTextColor(getResources().getColor(
							R.color.field_copy));
				}
			}
		}
	}

}
