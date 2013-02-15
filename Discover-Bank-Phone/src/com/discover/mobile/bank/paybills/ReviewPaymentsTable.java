package com.discover.mobile.bank.paybills;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.discover.mobile.bank.DynamicDataFragment;
import com.discover.mobile.bank.R;
import com.discover.mobile.bank.ui.table.SortableTable;
import com.discover.mobile.bank.ui.table.TableTitles;
import com.discover.mobile.common.BaseFragment;

public class ReviewPaymentsTable extends BaseFragment implements DynamicDataFragment, SortableTable{

	private ToggleButton scheduled;

	private ToggleButton completed;

	private ToggleButton canceled;

	/** Create the view
	 * @param inflater - inflater to inflate the layout
	 * @param container - container holding the group
	 * @param savedInstanceState - state of the fragment
	 */
	@Override
	public View onCreateView(final LayoutInflater inflater, final ViewGroup container,
			final Bundle savedInstanceState) {

		final View view = inflater.inflate(R.layout.review_payments_header, null);
		final TableTitles tableTitle = (TableTitles)view.findViewById(R.id.table_titles);
		scheduled = (ToggleButton) view.findViewById(R.id.toggle_left);
		completed = (ToggleButton) view.findViewById(R.id.toggle_middle);
		canceled  = (ToggleButton) view.findViewById(R.id.toggle_right);

		scheduled.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(final View v) {
				toggleButton(0);

			}

		});

		completed.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(final View v) {
				toggleButton(1);

			}

		});

		canceled.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(final View v) {
				toggleButton(2);

			}

		});

		tableTitle.setFragment(this);
		tableTitle.setFilterOneText("Date");
		tableTitle.setFilterTwoText("Payee");
		tableTitle.setFilterThreeText("Amount");
		return view;
	}

	@Override
	public void sortOnFilterOne(final boolean descending) {
		Toast.makeText(getActivity(), "Sort Filter 1", 3000).show();

	}

	@Override
	public void sortOnFilterTwo(final boolean descending) {
		Toast.makeText(getActivity(), "Sort Filter 2", 3000).show();
	}

	@Override
	public void sortOnFilterThree(final boolean descending) {
		Toast.makeText(getActivity(), "Sort Filter 3", 3000).show();
	}

	@Override
	public void handleReceivedData(final Bundle bundle) {
		// TODO Auto-generated method stub

	}

	/**
	 * Toggle the buttons look and feel
	 * @param checked - toggle button that is checked
	 * @param notChecke - toggle button that is not checked
	 * @param isPosted - boolean to set is posted equal to
	 */
	public void toggleButton(final int checked){
		switch(checked){
		case 0: 
			scheduled.setTextColor(getResources().getColor(R.color.white));
			completed.setTextColor(getResources().getColor(R.color.body_copy));
			canceled.setTextColor(getResources().getColor(R.color.body_copy));
			scheduled.setBackgroundDrawable(getResources().getDrawable(R.drawable.toggle_left_on));
			completed.setBackgroundDrawable(getResources().getDrawable(R.drawable.toggle_mid_off));
			canceled.setBackgroundDrawable(getResources().getDrawable(R.drawable.toggle_right_off));
			break;
		case 1: 
			scheduled.setTextColor(getResources().getColor(R.color.body_copy));
			completed.setTextColor(getResources().getColor(R.color.white));
			canceled.setTextColor(getResources().getColor(R.color.body_copy));
			scheduled.setBackgroundDrawable(getResources().getDrawable(R.drawable.toggle_left_off));
			completed.setBackgroundDrawable(getResources().getDrawable(R.drawable.toggle_mid_on));
			canceled.setBackgroundDrawable(getResources().getDrawable(R.drawable.toggle_right_off));
			break;
		case 2: 
			scheduled.setTextColor(getResources().getColor(R.color.body_copy));
			completed.setTextColor(getResources().getColor(R.color.body_copy));
			canceled.setTextColor(getResources().getColor(R.color.white));
			scheduled.setBackgroundDrawable(getResources().getDrawable(R.drawable.toggle_left_off));
			completed.setBackgroundDrawable(getResources().getDrawable(R.drawable.toggle_mid_off));
			canceled.setBackgroundDrawable(getResources().getDrawable(R.drawable.toggle_right_on));
			break;
		}
	}

	@Override
	public int getActionBarTitle() {
		return R.string.review_payments_title;
	}
}
