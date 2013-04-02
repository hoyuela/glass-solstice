package com.discover.mobile.common.auth;

import java.util.Calendar;

import com.discover.mobile.common.facade.FacadeFactory;

/**
 * This class contains contains logic to handle the keep alive services for both
 * Card and Bank. Users are expected to update whether or not Bank and Card are
 * authenticated or unauthenticated.
 */
public class KeepAlive {

	/** Minimum amount of time (ms) required before making bank refresh call. */
	public static final long MIN_TIME_FOR_BANK_REFRESH = 480000; // 8 mins

	/** Minimum amount of time (ms) required before making card refresh call. */
	public static final long MIN_TIME_FOR_CARD_REFRESH = 30000; // 30 secs
	
	/** This is used to set the last call time back if a request fails due to network service. */
	private static final long BANK_REFRESH_TIMEOUT_RESET = 15000; // 15 secs
	
	/** This is used to set the last call time back if a request fails due to network service. */
	private static final long CARD_REFRESH_TIMEOUT_RESET = 5000; // 5 secs

	/** Tracks the last time a Card refresh request was made. */
	private static long lastCardRefreshTimeInMillis = 0;

	/** Tracks the last time a Bank refresh request was made. */
	private static long lastBankRefreshTimeInMillis = 0;

	/** Used to decide if a refresh call is needed for Card */
	private static boolean isCardAuthenticated = false;

	/** Used to decide if a refresh call is needed for Bank */
	private static boolean isBankAuthenticated = false;

	/**
	 * Checks to see if the last bank refresh call period is greater than the
	 * minimum allowed.
	 * 
	 * @return true if time period is greater than
	 *         {@code BankUrlManager.MIN_TIME_FOR_BANK_REFRESH}, false
	 *         otherwise.
	 */
	private static boolean isBankRefreshRequired() {
		final long currentTime = Calendar.getInstance().getTimeInMillis();

		if ((currentTime - lastBankRefreshTimeInMillis) > MIN_TIME_FOR_BANK_REFRESH) {
			return true;
		}
		return false;
	}

	/**
	 * Checks to see if the last card refresh call period is greater than the
	 * minimum allowed.
	 * 
	 * @return true if time period is greater than
	 *         {@code BankUrlManager.MIN_TIME_FOR_CARD_REFRESH}, false
	 *         otherwise.
	 */
	private static boolean isCardRefreshRequired() {
		final long currentTime = Calendar.getInstance().getTimeInMillis();

		if ((currentTime - lastCardRefreshTimeInMillis) > MIN_TIME_FOR_CARD_REFRESH) {
			return true;
		}
		return false;
	}

	/**
	 * Checks to see if refresh service calls are necessary for Bank and Card
	 * sessions. If so, it will request the facades to perform the relevant
	 * call.
	 */
	public static void checkForRequiredSessionRefresh() {
		Calendar calendar = Calendar.getInstance();

		if (isBankAuthenticated && isBankRefreshRequired()) {
			FacadeFactory.getBankKeepAliveFacade().refreshBankSession();
		}

		if (isCardAuthenticated && isCardRefreshRequired()) {
			FacadeFactory.getCardKeepAliveFacade().refreshCardSession();
		}
	}

	/**
	 * Resets the timer for making session refresh calls. This should be called
	 * any time a successful network call is made that utilizes the Bank
	 * session.
	 */
	public static void updateLastBankRefreshTime() {
		final long currentTime = Calendar.getInstance().getTimeInMillis();
		lastBankRefreshTimeInMillis = currentTime;
	}

	/**
	 * Resets the timer for making session refresh calls. This should be called
	 * any time a successful network call is made that utilizes the Card
	 * session.
	 */
	public static void updateLastCardRefreshTime() {
		final long currentTime = Calendar.getInstance().getTimeInMillis();
		lastCardRefreshTimeInMillis = currentTime;
	}

	/**
	 * Used to reset the last refresh time in an instance where a call
	 * {@code checkForRequiredSessionRefresh()} has failed for Bank. Sets the
	 * last call time back by {@code BANK_REFRESH_TIMEOUT_RESET} to prevent too
	 * many calls to refresh session.
	 */
	public static void resetLastBankRefreshTime() {
		lastBankRefreshTimeInMillis = lastBankRefreshTimeInMillis + BANK_REFRESH_TIMEOUT_RESET;
	}

	/**
	 * Used to reset the last refresh time in an instance where a call
	 * {@code checkForRequiredSessionRefresh()} has failed for Card. Sets the
	 * last call time back by {@code CARD_REFRESH_TIMEOUT_RESET} to prevent too
	 * many calls to refresh session.
	 */
	public static void resetLastCardRefreshTime() {
		lastCardRefreshTimeInMillis = lastCardRefreshTimeInMillis + CARD_REFRESH_TIMEOUT_RESET;
	}

	/**
	 * Tells KeepAlive whether or not refresh calls should be made for this
	 * session. Be sure to set to {@code false} during Card logout or a dead
	 * session.
	 * 
	 * @param isCardAuthenticated
	 */
	public static void setCardAuthenticated(boolean isCardAuthenticated) {
		KeepAlive.isCardAuthenticated = isCardAuthenticated;
	}

	/**
	 * Tells KeepAlive whether or not refresh calls should be made for this
	 * session. Be sure to set to {@code false} during Bank logout or a dead
	 * session.
	 * 
	 * @param isBankAuthenticated
	 */
	public static void setBankAuthenticated(boolean isBankAuthenticated) {
		KeepAlive.isBankAuthenticated = isBankAuthenticated;
	}
}
