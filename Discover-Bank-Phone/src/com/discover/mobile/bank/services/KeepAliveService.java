package com.discover.mobile.bank.services;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Initializes two tasks, bank and card, to consistently refresh their session
 * by hitting their respective "keep alive" services.
 * 
 * @author Samuel Frank Smith
 * 
 */
public class KeepAliveService {

	private final Timer timer;
	private final BankTimerTask bankTask;
	private final CardTimerTask cardTask;

	private final static int BANK_UPDATE_INTERVAL = 780;
	private final static int CARD_UPDATE_INTERVAL = 20;

	public KeepAliveService() {
		timer = new Timer();
		bankTask = new BankTimerTask();
		cardTask = new CardTimerTask();
	}

	/**
	 * Cancels associated tasks, flags them for garbage collection, and finally
	 * cancels the timer.
	 */
	public void cancel() {
		if (bankTask != null) {
			bankTask.cancel();
		}
		if (cardTask != null) {
			cardTask.cancel();
		}
		if (timer != null) {
			timer.purge();
			timer.cancel();
		}
	}

	/**
	 * Schedules the Keep Alive tasks to execute with their interval between
	 * each execution time, not fixed.
	 */
	public void scheduleAndRun() {
		timer.schedule(bankTask, 0, BANK_UPDATE_INTERVAL);
		timer.schedule(cardTask, 0, CARD_UPDATE_INTERVAL);
	}

	/**
	 *  Task that will make the call to refresh the Bank session.
	 */
	private class BankTimerTask extends TimerTask {

		@Override
		public void run() {
			// TODO Make a call to service.

			
			// Remember, all UI updates need to be ran on UI thread. Cheers.
		}
	}

	/**
	 * Task that will make the call to Card's facade that will refresh the session.
	 */
	private class CardTimerTask extends TimerTask {

		@Override
		public void run() {
			// TODO Auto-generated method stub

		}

	}

}
