package com.discover.mobile.common.auth;

import java.util.Calendar;

import com.discover.mobile.common.facade.FacadeFactory;

public class KeepAlive {

	/** Minimum amount of time required before making new bank refresh call. */
	public static final long MIN_TIME_FOR_BANK_REFRESH = 480;

	/** Minimum amount of time required before making new card refresh call. */
	public static final long MIN_TIME_FOR_CARD_REFRESH = 30;

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
			lastBankRefreshTimeInMillis = calendar.getTimeInMillis();
			FacadeFactory.getBankKeepAliveFacade().refreshBankSession();
		}

		if (isCardAuthenticated && isCardRefreshRequired()) {
			lastCardRefreshTimeInMillis = calendar.getTimeInMillis();
			FacadeFactory.getCardKeepAliveFacade().refreshCardSession();
		}
	}

	public static void setCardAuthenticated(boolean isCardAuthenticated) {
		KeepAlive.isCardAuthenticated = isCardAuthenticated;
	}

	public static void setBankAuthenticated(boolean isBankAuthenticated) {
		KeepAlive.isBankAuthenticated = isBankAuthenticated;
	}
}
