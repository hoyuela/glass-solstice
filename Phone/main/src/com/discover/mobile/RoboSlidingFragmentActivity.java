package com.discover.mobile;

import java.util.HashMap;
import java.util.Map;

import roboguice.RoboGuice;
import roboguice.activity.event.OnActivityResultEvent;
import roboguice.activity.event.OnConfigurationChangedEvent;
import roboguice.activity.event.OnContentChangedEvent;
import roboguice.activity.event.OnCreateEvent;
import roboguice.activity.event.OnDestroyEvent;
import roboguice.activity.event.OnNewIntentEvent;
import roboguice.activity.event.OnPauseEvent;
import roboguice.activity.event.OnRestartEvent;
import roboguice.activity.event.OnResumeEvent;
import roboguice.activity.event.OnStartEvent;
import roboguice.activity.event.OnStopEvent;
import roboguice.event.EventManager;
import roboguice.inject.ContentViewListener;
import roboguice.inject.RoboInjector;
import roboguice.util.RoboContext;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.Window;
import android.view.WindowManager.LayoutParams;

import com.discover.mobile.common.SharedPreferencesWrapper;
import com.google.inject.Inject;
import com.google.inject.Key;
import com.slidingmenu.lib.SlidingMenu;
import com.slidingmenu.lib.app.SlidingFragmentActivity;

/**
 * Base class for anything that is going to use the sliding fragment or the action bar.
 * This also provides a subset of of methods that can be used.
 * 
 * @author jthornton
 *
 */
public abstract class RoboSlidingFragmentActivity extends SlidingFragmentActivity implements RoboContext {
	
	/**Fragment that is currently being shown to the user*/
	protected Fragment currentFragment;
	
    protected EventManager eventManager;
    protected HashMap<Key<?>,Object> scopedObjects = new HashMap<Key<?>, Object>();
    
 //   @Inject ContentViewListener ignored; // BUG find a better place to put this
    
    @Override
    public void onCreate(final Bundle savedInstanceState) {
        final RoboInjector injector = RoboGuice.getInjector(this);
        eventManager = injector.getInstance(EventManager.class);
        injector.injectMembersWithoutViews(this);
        super.onCreate(savedInstanceState);
        eventManager.fire(new OnCreateEvent(savedInstanceState));
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        eventManager.fire(new OnRestartEvent());
    }

    @Override
    protected void onStart() {
        super.onStart();
        eventManager.fire(new OnStartEvent());
    }

    @Override
    protected void onResume() {
        super.onResume();
        eventManager.fire(new OnResumeEvent());
    }

    @Override
    protected void onPause() {
        super.onPause();
        eventManager.fire(new OnPauseEvent());
    }

    @Override
    protected void onNewIntent( final Intent intent ) {
        super.onNewIntent(intent);
        eventManager.fire(new OnNewIntentEvent());
    }

    @Override
    protected void onStop() {
        try {
            eventManager.fire(new OnStopEvent());
        } finally {
            super.onStop();
        }
    }

    @Override
    protected void onDestroy() {
        try {
            eventManager.fire(new OnDestroyEvent());
        } finally {
            try {
                RoboGuice.destroyInjector(this);
            } finally {
                super.onDestroy();
            }
        }
    }

    @Override
    public void onConfigurationChanged(final Configuration newConfig) {
        final Configuration currentConfig = getResources().getConfiguration();
        super.onConfigurationChanged(newConfig);
        eventManager.fire(new OnConfigurationChangedEvent(currentConfig, newConfig));
    }

    @Override
    public void onContentChanged() {
        super.onContentChanged();
        RoboGuice.getInjector(this).injectViewMembers(this);
        eventManager.fire(new OnContentChangedEvent());
    }

    @Override
    protected void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        eventManager.fire(new OnActivityResultEvent(requestCode, resultCode, data));
    }

    @Override
    public Map<Key<?>, Object> getScopedObjectMap() {
        return scopedObjects;
    }
	
	/**
	 * Sets the fragment seen by the user
	 * @param fragment - fragment to be shown
	 */
	private void setVisibleFragment(final Fragment fragment) {
		this.currentFragment = fragment;
		getSupportFragmentManager()
				.beginTransaction()
				.replace(R.id.navigation_content, fragment)
				//Adds the class name and fragment to the back stack
				.addToBackStack(fragment.getClass().getSimpleName())
				.commit();
		hideSlidingMenuIfVisible();
	}	
	
	/**
	 * Set the current fragment that is being shown
	 * @param fragment - fragment that is currently shown
	 */
	public void setCurrentFragment(final RoboSherlockFragment fragment){
		this.currentFragment = fragment;
	}    
	
	/**
	 * Make the fragment visible
	 * @param fragment - fragment to be made visible
	 */
	public void makeFragmentVisible(final Fragment fragment) {
		setVisibleFragment(fragment);
		hideSlidingMenuIfVisible();
	}
	
	/**
	 * Hides the sliding menu is it is currently visible
	 */
	private void hideSlidingMenuIfVisible() {
		final SlidingMenu slidingMenu = getSlidingMenu();
		if(slidingMenu.isBehindShowing())
			slidingMenu.showAbove();
	}

    /**
     * Save a boolean value to the shared preferences
     * @param key - key of the value to store
     * @param value - boolean value 
     */
    public void saveToSharedPrefs(final String key, final boolean value){
    	SharedPreferencesWrapper.saveToSharedPrefs(this, key, value);
    }
    
    /**
     * Get a boolean value to the shared preferences
     * @param key - key of the value to get
     * @param defaultValue - default boolean value 
     */
    public boolean getValueFromSharedPrefs(final String key, final boolean defaultValue){
    	return SharedPreferencesWrapper.getValueFromSharedPrefs(this, key, defaultValue);
    }
    
    /**
     * Save a string value to the shared preferences
     * @param key - key of the value to store
     * @param value - boolean value 
     */
    public void saveToSharedPrefs(final String key, final String value){
    	SharedPreferencesWrapper.saveToSharedPrefs(this, key, value);
    }
    
    /**
     * Get a boolean value to the shared preferences
     * @param key - key of the value to get
     * @param defaultValue - default string value 
     */
    public String getValueFromSharedPrefs(final String key, final String defaultValue){
    	return SharedPreferencesWrapper.getValueFromSharedPrefs(this, key, defaultValue);
    }
    
    /**
     * Go back to the previous screen
     */
	public void goBack(){
		onBackPressed();
	} 
    
	/**
     * Show a modal alert dialog for the activity
     * @param alert - the modal alert to be shown
     */
    public void showAlert(final AlertDialog alert){
    	alert.requestWindowFeature(Window.FEATURE_NO_TITLE);
		alert.show();
		alert.getWindow().setLayout(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
    }
}
