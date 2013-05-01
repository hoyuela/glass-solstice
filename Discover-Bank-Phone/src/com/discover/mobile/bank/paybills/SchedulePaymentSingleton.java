package com.discover.mobile.bank.paybills;

import android.os.Bundle;

/**
 * Saves the Bundle for SchedulePayment during a rotation.
 */
public final class SchedulePaymentSingleton {

	private static volatile SchedulePaymentSingleton instance = null;
	
	private Bundle schedulePaymentBundle = null;
	
	private SchedulePaymentSingleton() {
		// Constructor only to be accessed by getInstance.
	}
	
	public static SchedulePaymentSingleton getInstance() {
		if(instance == null) {
			instance = new SchedulePaymentSingleton();
		}
		return instance;
	}
	
	public void setState(final Bundle bundle) {
		schedulePaymentBundle = null;
		if(bundle != null) {
			schedulePaymentBundle = new Bundle(bundle);
		}
	}
	
	public Bundle getState() {
		return schedulePaymentBundle;
	}
	
	public void destroy() {
		schedulePaymentBundle = null;
		instance = null;
	}
}
