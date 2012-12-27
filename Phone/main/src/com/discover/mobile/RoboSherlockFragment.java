package com.discover.mobile;

import roboguice.RoboGuice;
import android.app.AlertDialog;
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
        setActionBarTitle(getActionBarTitle());
    }
    
    /**
	 * Make the fragment visible
	 * @param fragment - fragment to be made visible
	 */
    public void makeFragmentVisible(final RoboSherlockFragment fragment){
    	final NavigationRootActivity activity = (NavigationRootActivity)this.getActivity();
    	activity.makeFragmentVisible(fragment);
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
     * Show a modal alert dialog for the fragment
     * @param alert - the modal alert to be shown
     */
    public void showAlertDialog(final AlertDialog alert){
    	final RoboSlidingFragmentActivity activity= (RoboSlidingFragmentActivity)this.getActivity();
    	activity.showCustomAlert(alert);
    }
    
    /**
     * Get the resource id of the string that should be shown in the action bar
     * @return the resource id of the string that should be shown in the action bar
     */
    public abstract int getActionBarTitle();
    
    /**
     * Save a boolean value to the shared preferences
     * @param key - key of the value to store
     * @param value - boolean value 
     */
    public void saveToSharedPrefs(final String key, final boolean value){
    	final RoboSlidingFragmentActivity activity= (RoboSlidingFragmentActivity)this.getActivity();
    	activity.saveToSharedPrefs(key, value);
    }
    
    /**
     * Get a boolean value to the shared preferences
     * @param key - key of the value to get
     * @param defaultValue - default boolean value 
     */
    public boolean getValueFromSharedPrefs(final String key, final boolean defaultValue){
    	final RoboSlidingFragmentActivity activity= (RoboSlidingFragmentActivity)this.getActivity();
    	return activity.getValueFromSharedPrefs(key, defaultValue);
    }
    
    /**
     * Save a string value to the shared preferences
     * @param key - key of the value to store
     * @param value - boolean value 
     */
    public void saveToSharedPrefs(final String key, final String value){
    	final RoboSlidingFragmentActivity activity= (RoboSlidingFragmentActivity)this.getActivity();
    	activity.saveToSharedPrefs(key, value);
    }
    
    /**
     * Get a boolean value to the shared preferences
     * @param key - key of the value to get
     * @param defaultValue - default string value 
     */
    public String getValueFromSharedPrefs(final String key, final String defaultValue){
    	final RoboSlidingFragmentActivity activity= (RoboSlidingFragmentActivity)this.getActivity();
    	return activity.getValueFromSharedPrefs(key, defaultValue);
    }
	
}
