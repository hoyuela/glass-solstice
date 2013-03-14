/*
 * Copyright Solstice 2013
 */
package com.discover.mobile.common.framework;

import java.io.Serializable;
import java.util.HashMap;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;

import com.discover.mobile.common.BaseFragmentActivity;
import com.discover.mobile.common.DiscoverActivityManager;
import com.discover.mobile.common.IntentExtraKey;

import com.discover.mobile.common.net.NetworkServiceCall;
/**
 * Conductor - The user agent has two jobs. The first job is to get the data by
 * either interacting with the current session cache or by submitting a network
 * service call to get data. To get the cached data, if there is any, the
 * conductor will make a call to the cache manager and ask for data. If no data
 * is present in the cache, the conductor will then interact with the service
 * call manager to get the service call and then make the call. The second job
 * is to manage the data received from the network manager. Upon receiving data
 * back from the network manager the conductor will cache the data if needed and
 * then it will place the data into a bundle and forward the bundle to the
 * navigator.
 * 
 * 
 */
public abstract class Conductor   {
	
	public static final String TAG = Conductor.class.getSimpleName();
	
	protected CacheManager cacheMgr = CacheManager.instance();

	/**
	 * A map to manage the caller's requested destination and the network
	 * service call
	 * 
	 * the key is the networkServiceCall's hashcode
	 */
	@SuppressWarnings("rawtypes")
	protected HashMap<Integer, DestinationDetails> destinationMap = new HashMap<Integer, DestinationDetails>();

	/** the service call factory used to create the call object */
	protected ServiceCallFactory serviceCallFactory;

	/**
	 * provides the card/bank specific service call factory impl class
	 * 
	 * note: this class is expected to create callbacks supplying the conductor
	 * as a success listener
	 * 
	 * @param serviceCallFactory
	 */
	public Conductor(ServiceCallFactory pServiceCallFactory) {
		serviceCallFactory = pServiceCallFactory;
	}

	/**
	 * Must provide the service call factory
	 */
	@SuppressWarnings("unused")
	private Conductor() {
	}
	
	

	/**
	 * Navigates to the given fragment. 1. checks to see if fragment requires
	 * data 2. requests data from cache manager 3. makes service call if cache
	 * data unavailable 4. navigates to class
	 * 
	 * @param fragmentClass
	 *            - the destination class
	 * assumes no payload required for service call, if necessary 
	 * 
	 */
	public void launchFragment(Class<Fragment> fragmentClass){
		launchFragment(fragmentClass, null, null);
	}
	
	/**
	 * Navigates to the given fragment. 1. checks to see if fragment requires
	 * data 2. requests data from cache manager 3. makes service call if cache
	 * data unavailable 4. navigates to class
	 * 
	 * @param activityClass
	 *            - the destination class
	 * assumes no payload required for service call, if necessary 
	 *           
	 */
	public void launchActivity(Class<Activity> activityClass){ 
		launchActivity(activityClass, null, null);
	}
	
	/**
	 * Navigates to the given fragment. 
	 * <pre>
	 * 1. checks to see if fragment requires data 
	 * 2. requests data from cache manager 
	 * 3. makes service call if cache data unavailable 
	 * 4. navigates to class
	 * 
	 * @param fragmentClass
	 *            - the destination class
	 * @param payload  - the payload for the service call, if necessary
	 * @param bundle
	 *            - bundle to pass on when navigating.
	 */
	public void launchFragment(Class<Fragment> fragmentClass, Serializable payload, Bundle bundle) {
		@SuppressWarnings("rawtypes")
		Class cacheObjReq = lookupCacheRequiredForDestination(fragmentClass);
		if (cacheObjReq == null) {
			// no data required, don't perform the service call; just navigate
			navigateToFrament(fragmentClass, bundle);
		} else {
			Object o = cacheMgr.getObjectFromCache(cacheObjReq);
			if (o == null) {
				// cache is null, let's make the call

				// call payload in the bundle
				@SuppressWarnings("unchecked")
				NetworkServiceCall<?> call = serviceCallFactory.createServiceCall(cacheObjReq,payload);
				// associate the destination with the call
				destinationMap.put(call.hashCode(), new DestinationDetails(
						DestinationType.FRAGMENT, fragmentClass, bundle));
				call.submit();
			}
		}
	}

	/**
	 * Navigates to the given fragment. 1. checks to see if fragment requires
	 * data 2. requests data from cache manager 3. makes service call if cache
	 * data unavailable 4. navigates to class
	 * 
	 * @param activityClass
	 *            - the destination class
	 * @param payload - the optional payload required to make a service call if necessary
	 * @param bundle
	 * 				- an optional bundle to pass to the destination
	 *           
	 */
	public void launchActivity(Class<Activity> activityClass, Serializable payload, Bundle bundle) {
		@SuppressWarnings("rawtypes")
		Class cacheObjReq = lookupCacheRequiredForDestination(activityClass);
		if (cacheObjReq == null) {
			// no data required, don't perform the service call; just navigate
			navigateToActivity(activityClass, bundle);
		} else {
			Object o = cacheMgr.getObjectFromCache(cacheObjReq);
			if (o == null) {
				// cache is null, let's make the call

				// call payload in the bundle
				@SuppressWarnings("unchecked")
				NetworkServiceCall<?> call = serviceCallFactory.createServiceCall(cacheObjReq,payload);
				// associate the destination with the call
				destinationMap.put(call.hashCode(), new DestinationDetails(
						DestinationType.ACTIVITY, activityClass, bundle));
				call.submit();
			}
		}
	}

	/**
	 * reusable navigate method
	 * 
	 * @param destClass
	 * @param bundle
	 */
	protected void navigateToFrament(
			@SuppressWarnings("rawtypes") Class destClass, Bundle bundle) {
		Fragment fragment;
		try {
			fragment = (Fragment) destClass.newInstance();
		} catch (Exception e) {
			throw new RuntimeException(
					"Unable to instantiate to supplied fragment!  Please ensure public no-arg constructor");
		}
		if (bundle != null) {
			fragment.setArguments(bundle);
		}
		((BaseFragmentActivity) DiscoverActivityManager.getActiveActivity())
				.makeFragmentVisible(fragment);
	}

	/**
	 * reusable navigate method
	 * 
	 * @param destClass
	 * @param bundle
	 */
	protected void navigateToActivity(
			@SuppressWarnings("rawtypes") Class destClass, Bundle bundle) {
		Activity activeActivity = DiscoverActivityManager.getActiveActivity();
		final Intent intent = new Intent(activeActivity, destClass);
		if (bundle != null)
			intent.putExtras(bundle);
		activeActivity.startActivity(intent);
		activeActivity.finish();
	}

	/**
	 * For a given activity or fragment, this provides the Class of the cache
	 * json object required to query the cache manager
	 * 
	 * @param c
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	public abstract Class lookupCacheRequiredForDestination(Class c);

	

	/**
	 * Navigates to the caller's original destination.  Puts the service call results 
	 * into the bundle for the destination activity/fragment
	 * 
	 * @param sender
	 * @param value
	 */
	public void success(NetworkServiceCall<?> sender, Serializable value) {

		// let's navigate !
		DestinationDetails destinationDetails = destinationMap.get(sender.hashCode());
		
		if ( destinationDetails == null ){
			Log.w(TAG,"Created a network service call without using the Conductor Pattern!  Sender: " + sender.getClass().getSimpleName());
			return;
		}
		
		// stuff the result into the bundle where the caller can find it 
		Bundle bundle = destinationDetails.getDestBundle();
		if ( bundle == null ){ 
			bundle = new Bundle();
		}
		bundle.putSerializable(IntentExtraKey.SERVICE_RESULT,value);
		
		if (destinationDetails.getDestType() == DestinationType.FRAGMENT) {
			navigateToFrament(destinationDetails.getDestFragment(),
					bundle);
		} else {
			navigateToActivity(destinationDetails.getDestFragment(),
					bundle);
		}

	}

	

	/**
	 * rest of class is for inner workings
	 */

	/**
	 * An enum for easy identification
	 * 
	 * @author ekaram
	 * 
	 */
	public enum DestinationType {
		ACTIVITY, FRAGMENT;
	}

	/**
	 * Inner class just used for managing getting caller information to the
	 * onSuccess Method
	 * 
	 * @author ekaram
	 * 
	 */
	public class DestinationDetails {
		@SuppressWarnings("rawtypes")
		public Class destFragment;
		public DestinationType destType;
		public Bundle destBundle;

		/**
		 * @return the destFragment
		 */
		@SuppressWarnings("rawtypes")
		public Class getDestFragment() {
			return destFragment;
		}

		/**
		 * @return the destBundle
		 */
		public Bundle getDestBundle() {
			return destBundle;
		}

		/**
		 * @return the destType
		 */
		public DestinationType getDestType() {
			return destType;
		}

		@SuppressWarnings("rawtypes")
		public DestinationDetails(DestinationType destinationType,
				Class destFragment, Bundle destBundle) {
			this.destBundle = destBundle;
			this.destFragment = destFragment;
			this.destType = destinationType;
		}

	}

	/**
	 * A way to retrieve the bundle for the specified call
	 * @param callClass
	 * @return
	 */
	public Bundle getBundleForCall(int callHashCode) {
		DestinationDetails destDetails =  this.destinationMap.get(callHashCode);
		if ( destDetails != null ){ 
			return destDetails.getDestBundle();
		}
		return null;
	}

}
