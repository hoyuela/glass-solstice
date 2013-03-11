package com.discover.mobile.bank.services;

import java.util.Timer;
import java.util.TimerTask;

public class KeepAliveTimer {

	private final Timer timer;
	private final TimerTask bankTask;

	public KeepAliveTimer() {
		timer = new Timer();
		bankTask = new TimerTask() {

			@Override
			public void run() {
				// TODO Auto-generated method stub

			}
		};
	}

	/**
	 * Cancels associated tasks, flags them for garbage collection, and finally cancels
	 * the timer.
	 */
	public void cancel() {
		bankTask.cancel();
		timer.purge();
		timer.cancel();
	}
	
	/**
	 *  
	 */
	private class BankTimerTask extends TimerTask {

		@Override
		public void run() {
			// TODO Auto-generated method stub
			
		}		
	}
	
	private class CardTimerTask extends TimerTask {

		@Override
		public void run() {
			// TODO Auto-generated method stub
			
		}
		
	}

}
