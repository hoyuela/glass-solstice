package com.discover.mobile.common.analytics;

public interface AnalyticsPage {
	
	// used
	static final String STARTING = "login-pg";
	
	static final String CARD_LOGIN = "cardLogin-pg";
	static final String LOGIN_ERROR = "loginErrorPage-Pg";
	static final String FORGOT_PASSWORD_MENU = "forgot-uid-or-password-menu-pg";
	static final String FORGOT_BOTH_STEP1 = "forgot-both-step1-pg";
	static final String FORGOT_BOTH_STEP2 = "forgot-both-step2-pg";
	static final String FORGOT_BOTH_CONFIRMATION = "forgot-both-confirmation-pg";
	static final String FORGOT_PASSWORD_STEP1 = "forgot-password-step1-pg";
	static final String FORGOT_PASSWORD_STEP2 = "forgot-password-step2-pg";
	static final String FORGOT_PASSWORD_CONFIRMATION = "forgot-password-confirmation-pg";
	static final String FORGOT_UID = "forgot-uid-pg";
	static final String FOROGT_UID_CONFIRMATION = "forgot-uid-confirmation-pg";
	
	static final String PASSWORD_STRENGTH_HELP = "password-strength-meter-pg";
	static final String UID_STRENGTH_HELP = "uid-strength-meter-pg";
	
	static final String ACCOUNT_LANDING = "accountLanding-pg";
	static final String ACCOUNT_LOCKED = "logInAccLocked-pg";
	
	static final String FORCED_UPGRADE = "forceUpgrade-pg";
	static final String OPTIONAL_UPGRADE = "optionalUpgrade-pg";
	
	/**Analytic for going to the push alert history*/
	static final String PUSH_ALERT_HISTORY = "alertHistory-pg";
	
	/**Analytic for going to the external email alert*/
	static final String PUSH_EXTERNAL_EMAIL_ALERTS = "externalEmailAlerts-pg";

	/**Analytic for going to the push FAQ page*/
	static final String PUSH_FAQ = "faqDeviceAlerts-pg";

	/**Analytic for going to the push manage alerts page*/
	static final String PUSH_MANAGE_ALERTS = "manageAlerts-pg";

	/**Analytic for going to the push manage alert override*/
	static final String PUSH_MANAGE_ALERTS_OVERRIDE = "manageAlertsOverride-pg";

	/**Analytic for going to the push what now page */
	static final String PUSH_WHAT_NOW = "manageAlertsTermsAndConditions-pg";

	/**Analytic for going to the push diagnostic page*/
	static final String PUSH_DIAGONSTIC = "pushDiagonstic-pg";

	/**Analytic for going to the push terms and conditions page*/
	static final String PUSH_TERMS_AND_CONDITIONS = "pushTermsAndConditions-pg";
	
	/**Analytic for going to the enroll profile screen*/
	static final String PROFILE_ENROLL = "profileEnroll-pg";

	
	//not used yet
	static final String ACCOUNT_CREDIT_LINE_AVAILABLE = "accountCreditLineAvail-pg";
	static final String ACCOUNT_SUMMARY = "accountSummary-pg";
	static final String CARD_HOME = "cardHome-pg";
	static final String CARD_HOME_ESSENTIAL_CARD = "cardHomeEssencialCard-pg";
	static final String CORPORATE_DBC_L = "corporateDBC_L-pg";
	static final String CORPORATE_DBCL = "corporateDBCL-pg";
	
}
