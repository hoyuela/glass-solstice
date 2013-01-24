package com.discover.mobile.section.account;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.RadioGroup;
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
//		RadioGroup amtGroup = (RadioGroup) v.findViewBy

		dateLeft = (ToggleButton) dateGroup.findViewById(R.id.toggle_left);
		dateMid = (ToggleButton) dateGroup.findViewById(R.id.toggle_middle);
		dateRight = (ToggleButton) dateGroup.findViewById(R.id.toggle_right);

		dateLeft.setOnClickListener(new DateToggleListener());
		dateMid.setOnClickListener(new DateToggleListener());
		dateRight.setOnClickListener(new DateToggleListener());
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
	 * Listener for Date Toggler
	 * 
	 * @author sam
	 * 
	 */
	private class DateToggleListener implements OnClickListener {

		@Override
		public void onClick(View v) {

			if (!checkForValidSelection(v.getId())) {
				return;
			}

			switch (v.getId()) {

			case R.id.toggle_left:
				selectToggleButton(R.id.toggle_left);
				deselectToggleButton(R.id.toggle_middle);
				deselectToggleButton(R.id.toggle_right);
				break;
			case R.id.toggle_middle:
				selectToggleButton(R.id.toggle_middle);
				deselectToggleButton(R.id.toggle_left);
				deselectToggleButton(R.id.toggle_right);
				break;
			case R.id.toggle_right:
				selectToggleButton(R.id.toggle_right);
				deselectToggleButton(R.id.toggle_middle);
				deselectToggleButton(R.id.toggle_left);
				break;
			}
		}
		

		/**
		 * This will ensure a button already selected is not unselected.
		 * 
		 * @param id
		 *            resource id of the clicked button
		 * 
		 * @return true if not already selected, false otherwise.
		 */
		private boolean checkForValidSelection(int id) {
			if (id == dateLeft.getId() && !dateLeft.isChecked()) {
				dateLeft.setChecked(true);
				return false;
			}
			if (id == dateRight.getId() && !dateRight.isChecked()) {
				dateRight.setChecked(true);
				return false;
			}
			if (id == dateMid.getId() && !dateMid.isChecked()) {
				dateMid.setChecked(true);
				return false;
			}
			return true;
		}

		/**
		 * 
		 * @param id
		 */
		private void selectToggleButton(int id) {

			if (R.id.toggle_right == id) {
				// Show both date boxes TODO
				dateRight.setTextColor(getResources().getColor(R.color.white));
			}
			if (R.id.toggle_middle == id) {
				// Show single date box TODO
				dateMid.setTextColor(getResources().getColor(R.color.white));
			}
			if (R.id.toggle_left == id) {
				dateLeft.setTextColor(getResources().getColor(R.color.white));
			}
		}

		/**
		 * 
		 * @param id
		 */
		private void deselectToggleButton(int id) {

			if (R.id.toggle_right == id) {
				// hide both date boxes TODO
				dateRight.setChecked(false);
				dateRight.setTextColor(getResources().getColor(
						R.color.field_copy));
			}
			if (R.id.toggle_middle == id) {
				// hide single date box TODO
				dateMid.setChecked(false);
				dateMid.setTextColor(getResources()
						.getColor(R.color.field_copy));
			}
			if (R.id.toggle_left == id) {
				dateLeft.setChecked (false);
				dateLeft.setTextColor(getResources().getColor(
						R.color.field_copy));
			}
		}
	}
}
