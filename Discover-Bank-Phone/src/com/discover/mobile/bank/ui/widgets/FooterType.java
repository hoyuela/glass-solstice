package com.discover.mobile.bank.ui.widgets;

public final class FooterType {
	/** Category code to identify an Privacy and Terms Footer Type **/
	public static final int PRIVACY_TERMS = 1;
	
	/** Category code to identify a Feedback Footer Type **/
	public static final int PROVIDE_FEEDBACK=2;
	
	/** Category code to identify a Need Help Footer Type **/
	public static final int NEED_HELP = 4;
	
	/** Category code to identify a Copyright Footer Type**/
	public static final int COPYRIGHT = 8;
	
	/** Category used to identify all levels **/
	public static final int ALL =  PRIVACY_TERMS | PROVIDE_FEEDBACK | NEED_HELP | COPYRIGHT;

	
	
	private FooterType(final int value) {
	}

	
}
