package com.discover.mobile.bank.paybills;

import android.os.Bundle;

/**
 * Saves the Bundle for SchedulePayment during a rotation.
 */
public class SchedulePaymentSingleton {

	private static volatile SchedulePaymentSingleton instance = null;
	
	private Bundle schedulePaymentBundle;
	
	private SchedulePaymentSingleton() {
		schedulePaymentBundle = null;
	}
	
	public static SchedulePaymentSingleton getInstance() {
		if(instance == null) {
			instance = new SchedulePaymentSingleton();
		}
		return instance;
	}
	
	public void setState(Bundle bundle) {
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
