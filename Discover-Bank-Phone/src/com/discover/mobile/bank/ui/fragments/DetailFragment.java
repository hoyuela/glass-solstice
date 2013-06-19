package com.discover.mobile.bank.ui.fragments;

import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.discover.mobile.bank.BankExtraKeys;
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
	/** Reference to the handler which was created to load the content during the creation of this fragment */
	private Handler handler;
	/**
	 * Reference to the runnable provided to the handler data member used to load the content asynchronously during the
	 * creation of this fragment
	 */
	private Runnable runnable;

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
		
		loadContent(getDelay());

		return mainView;
	}
	
	/**
	 * Method used to calculate the delay to use to load the content displayed in this fragment. The delay is based off
	 * of the position of this fragment within the view pager. This method depends on the POSITION key being provided
	 * via the bundle provided via getArguments().
	 * 
	 * @return Returns the delay to be used for loading this fragment in the hosting view pager.
	 */
	private int getDelay() {
		final Bundle bundle = getArguments();
		int delay = 200;

		if( bundle != null ) {
			delay *= (bundle.getInt(BankExtraKeys.POSITION, 1) + 1);
		}
		
		return delay;
	}
	
	/**
	 * Method used to display the content on this fragment.
	 * 
	 * @param delay
	 *            The delay specified is used to determine how long to wait before the content is displayed. If 0
	 *            displays immediately.
	 */
	private void loadContent(final int delay) {
		if (delay > 0) {
			/**
			 * A reference of handler and runnable are held such to be cancellable when the fragment is being stopped
			 * during rotation or when being destroyed.
			 */
			handler = new Handler();
			runnable = new Runnable() {
				@Override
				public void run() {
					creatorTask = new LayoutCreatorTask();
					creatorTask.execute();
				}
			};
			handler.postDelayed(runnable, delay);
		} else {
			creatorTask = new LayoutCreatorTask();
			creatorTask.execute();
		}
	}

	/**
	 * If the async task has not completed when the Fragment gets stopped, we need to cancel it.
	 */
	@Override
	public void onStop() {
		super.onStop();
		
		/** Stop loader threads / async task used to load the content of this fragment */
		if (handler != null && runnable != null) {
			handler.removeCallbacks(runnable);
		}
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
		private View subView;
		private RelativeLayout mainViewRelativeLayout;

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
