package com.discover.mobile.bank.ui.fragments;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.discover.mobile.bank.R;

/**
 * This is a specific style of detail item.
 * It calls the appropriate methods from the DetailItem class to fill the content table
 * with list items and data.
 * 
 * @author scottseward
 *
 */
public abstract class DetailFragment extends Fragment {
	private View mainView;
	private LayoutCreatorTask creatorTask;

	/**
	 * Returns the layout to be used by the current Fragment.
	 * @return the layout to be used by the current Fragment.
	 */
	protected abstract int getFragmentLayout();
	
	/**
	 * The method that should be used to add data to a layout, change labels, add listeners etc.
	 * It is done in the background with an AsyncTask.
	 * @param fragmentView the inflated layout of the layout provided by getFragmentLayout().
	 */
	protected abstract void setupFragmentLayout(final View fragmentView);

	/**
	 * Inflate a very simple layout with a progress spinner that will be replaced with
	 * actual content when it gets loaded in loadItemDataToScreen.
	 */
	@Override
	public View onCreateView(final LayoutInflater inflater, final ViewGroup container,
			final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mainView = inflater.inflate(R.layout.spinner_layout, null);
		
		creatorTask = new LayoutCreatorTask();
		creatorTask.execute();
		return mainView;
	}
	
	/**
	 * If the async task has not completed when the Fragment gets stopped, we need to cancel it.
	 */
	@Override
	public void onStop() {
		super.onStop();
		
		if(creatorTask != null){
			creatorTask.cancel(true);
		}
	}
	
	
	/**
	 * An AsyncTask class that inflates and populates a layout with data then inserts that layout
	 * into the default layout for a DetailFragment.
	 * This exists to free up the UI thread and provide a much smoother user experience. Most
	 * of the heavy work is done in the background thanks to this class.
	 * @author scottseward
	 *
	 */
	public class LayoutCreatorTask extends AsyncTask<Void, Void, Void> {
		View subView;
		RelativeLayout mainViewRelativeLayout;

		/**
		 * During background thread processing, inflate the layout that will be used,
		 * and insert the data from the ActivityDetail item into the parts of the layout
		 * that need to present it.
		 */
		@Override
		protected Void doInBackground(final Void... params) {
		    final LayoutInflater inflater = getLayoutInflater(null);
			subView = inflater.inflate(getFragmentLayout(), null);
			
			setupFragmentLayout(subView);
			
			mainViewRelativeLayout = (RelativeLayout)mainView.findViewById(R.id.main_layout);
			return null;
		}
		
		/**
		 * When the layout is ready to be shown, remove all views in the main relative layout
		 * which is just a progress spinner, then add the view that was constructed in the 
		 * background.
		 */
		@Override
		protected void onPostExecute(final Void result) {
			if(mainViewRelativeLayout != null){
				mainViewRelativeLayout.removeAllViews();
				mainViewRelativeLayout.addView(subView);
			}
		}
		
	}
	
}
