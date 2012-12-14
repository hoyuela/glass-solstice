package com.discover.mobile;

import roboguice.RoboGuice;
import android.os.Bundle;
import android.view.View;

import com.actionbarsherlock.app.SherlockFragment;
import com.discover.mobile.navigation.NavigationRootActivity;

/**
 * Base fragment that all of the fragments in the app should extend
 * @author jthornton
 *
 */
public abstract class RoboSherlockFragment extends SherlockFragment{
	
	/**
	 * Create the fragment
	 * @param savedInstanceState - bundle containing saved state of the fragment
	 */
    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setActionBarTitle(getActionBarTitle());
        RoboGuice.getInjector(getActivity()).injectMembersWithoutViews(this);
    }
    
    /**
     * Create the view
     * @param view - view of the fragment
     * @param savedInstanceState - bundle containing saved state of the fragment
     */
    @Override
    public void onViewCreated(final View view, final Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        RoboGuice.getInjector(getActivity()).injectViewMembers(this);
    }
    
    /**
     * When the app resumes, make sure the fragment shows the same
     */
    @Override
    public void onResume(){
    	super.onResume();
    	final NavigationRootActivity activity = (NavigationRootActivity)this.getActivity();
    	activity.setCurrentFragment(this);
    }
    
    /**
	 * Set the title in the action bar for display
	 * @param title - title to show in the display
	 */
    public void setActionBarTitle(final int title){
    	final LoggedInRoboActivity activity = (LoggedInRoboActivity)this.getActivity();
    	activity.setActionBarTitle(activity.getResources().getString(title));
    }
    
    /**
     * 
     * @return
     */
    public abstract int getActionBarTitle();
	
}
