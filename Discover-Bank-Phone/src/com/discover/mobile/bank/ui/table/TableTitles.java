package com.discover.mobile.bank.ui.table;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.discover.mobile.bank.R;

/**
 * Titles for the table
 * @author jthornton
 *
 */
public class TableTitles extends RelativeLayout{

	private final RelativeLayout filter1;

	private final RelativeLayout filter2;

	private final RelativeLayout filter3;

	private final ImageView filterImage1;

	private final ImageView filterImage2;

	private final ImageView filterImage3;

	private final TextView filterText1;

	private final TextView filterText2;

	private final TextView filterText3;

	private boolean isDescendingOne = true;

	private boolean isDescendingTwo = false;

	private boolean isDescendingThree = false;

	private SortableTable fragment;

	public TableTitles(final Context context, final AttributeSet attrs) {
		super(context, attrs);

		final View mainView = LayoutInflater.from(context).inflate(R.layout.table_titles, null);

		filter1 = (RelativeLayout) mainView.findViewById(R.id.filter_1);
		filter2 = (RelativeLayout) mainView.findViewById(R.id.filter_2);
		filter3 = (RelativeLayout) mainView.findViewById(R.id.filter_3);
		filterText1 = (TextView) mainView.findViewById(R.id.filter_text_1);
		filterText2 = (TextView) mainView.findViewById(R.id.filter_text_2);
		filterText3 = (TextView) mainView.findViewById(R.id.filter_text_3);
		filterImage1 = (ImageView) mainView.findViewById(R.id.filter_image_1);
		filterImage2 = (ImageView) mainView.findViewById(R.id.filter_image_2);
		filterImage3 = (ImageView) mainView.findViewById(R.id.filter_image_3);

		filter1.setOnClickListener(getFilterOneListener());
		filter2.setOnClickListener(getFilterTwoListener());
		filter3.setOnClickListener(getFilterThreeListener());

		addView(mainView);
	}

	public void setFilterOneText(final String text){
		filterText1.setText(text);
	}

	public void setFilterTwoText(final String text){
		filterText2.setText(text);
	}

	public void setFilterThreeText(final String text){
		filterText3.setText(text);
	}


	public OnClickListener getFilterOneListener(){
		return new OnClickListener(){
			@Override
			public void onClick(final View v) {
				isDescendingOne = !isDescendingOne;
				isDescendingTwo = false;
				isDescendingThree = false;
				filterText1.setTypeface(null, Typeface.BOLD);
				filterText2.setTypeface(null, Typeface.NORMAL);
				filterText3.setTypeface(null, Typeface.NORMAL);
				filterImage1.setVisibility(View.VISIBLE);
				filterImage1.setBackgroundDrawable(getResources().getDrawable(getImage(isDescendingOne)));
				filterImage2.setVisibility(View.INVISIBLE);
				filterImage3.setVisibility(View.INVISIBLE);
				fragment.sortOnFilterOne(isDescendingOne);
			}
		};
	}

	public OnClickListener getFilterTwoListener(){
		return new OnClickListener(){
			@Override
			public void onClick(final View v) {
				isDescendingTwo = !isDescendingTwo;
				isDescendingOne = false;
				isDescendingThree = false;
				filterText2.setTypeface(null, Typeface.BOLD);
				filterText1.setTypeface(null, Typeface.NORMAL);
				filterText3.setTypeface(null, Typeface.NORMAL);
				filterImage2.setVisibility(View.VISIBLE);
				filterImage2.setBackgroundDrawable(getResources().getDrawable(getImage(isDescendingTwo)));
				filterImage1.setVisibility(View.INVISIBLE);
				filterImage3.setVisibility(View.INVISIBLE);
				fragment.sortOnFilterTwo(isDescendingTwo);
			}
		};
	}

	public OnClickListener getFilterThreeListener(){
		return new OnClickListener(){
			@Override
			public void onClick(final View v) {
				isDescendingThree = !isDescendingThree;
				isDescendingOne = false;
				isDescendingTwo = false;
				filterText3.setTypeface(null, Typeface.BOLD);
				filterText1.setTypeface(null, Typeface.NORMAL);
				filterText2.setTypeface(null, Typeface.NORMAL);
				filterImage3.setVisibility(View.VISIBLE);
				filterImage3.setBackgroundDrawable(getResources().getDrawable(getImage(isDescendingThree)));
				filterImage1.setVisibility(View.INVISIBLE);
				filterImage2.setVisibility(View.INVISIBLE);
				fragment.sortOnFilterThree(isDescendingThree);
			}
		};
	}

	protected int getImage(final boolean isDescending){
		return (isDescending) ? R.drawable.orange_arrow_down : R.drawable.orange_arrow_up;
	}

	public void setFilterOneDisabled(){
		disableFilter(filter1);
	}

	public void setFilterTwoDisabled(){
		disableFilter(filter2);
	}

	public void setFilterThreeDisabledd(){
		disableFilter(filter3);
	}

	private void disableFilter(final RelativeLayout filter){
		filter.setOnClickListener(null);
	}

	/**
	 * @return the fragment
	 */
	public SortableTable getFragment() {
		return fragment;
	}

	/**
	 * @param fragment the fragment to set
	 */
	public void setFragment(final SortableTable fragment) {
		this.fragment = fragment;
	}



}
