package com.discover.mobile.common.auth;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * InputValidator is used to store validation methods for checking if certain user inputs
 * meet certain criteria for validity.
 * It works by having a large amount of local boolean variables that store the state of
 * past validation checks. These local variables are set by running methods associated with an
 * instantiation of a InputValidator object. 
 * 
 * In example, with a new InputValidator object you can the isEmailValid(String email) to
 * check an email address against the local regular expression for valididty. The isEmailValid
 * method will return true or false based on the input, then you can also later check what
 * the result was by accessing the wasEmailValid member variable.
 * @author scottseward
 *
 */
public class InputValidator {
	private static Pattern emailPattern;
	private final static String EMAIL_PATTERN = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"
									+ "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";
	
	private static Matcher matcher;
	private static Pattern pattern;
	public boolean 
	wasDobMonthValid, wasDobYearValid, wasDobDayValid, 
	wasAccountNumberValid, wasSsnValid, wasEmailValid, 
	wasUidValid, wasPassValid, didPassesMatch, didIdsMatch, didPassAndIdMatch,
	wasCardExpMonthValid, wasCardExpYearValid;
	
	private final static int CARD_NUMBER_LENGTH_OK = 16;
	private final static String CARD_NUMBER_PREFIX = "6011";
	
	private final static int YEAR_MIN_VALUE = 1890;
	private final static int YEAR_MAX_VALUE = 2100;
	
	private final static int DAY_MAX_VALUE = 31;
	private final static int DAY_MIN_VALUE = 1;
	
	private final static int MONTH_MAX_VALUE = 11;
	private final static int MONTH_MIN_VALUE = 0;
	
	public InputValidator(){
		emailPattern = Pattern.compile(EMAIL_PATTERN);
		clear();
	}
	
	/**
	 * Sets all local boolean variables associated with past validation checks to false.
	 */
	public void clear(){
		wasSsnValid = false;
		wasDobMonthValid = false;
		wasDobYearValid = false;
		wasDobDayValid = false;
		wasAccountNumberValid = false;
		wasPassValid = false;
		wasUidValid = false;
		wasCardExpMonthValid = false;
		wasCardExpYearValid = false;
		didPassesMatch = false;
		didIdsMatch = false;
		didPassAndIdMatch = false;
	}

	public boolean wasForgotPasswordInfoComplete(){
		return wasSsnValid & wasDobMonthValid & wasDobMonthValid & wasDobDayValid &
				wasUidValid & wasCardExpMonthValid & wasCardExpYearValid;
	}
	
	public boolean wasAccountInfoComplete(){
		//bitwise operators FTW
		return wasSsnValid & wasDobMonthValid & wasDobYearValid & wasDobDayValid &
				wasAccountNumberValid & wasCardExpMonthValid & wasCardExpYearValid;
	}
	
	public boolean wasAccountTwoInfoComplete(){
		return !didPassAndIdMatch &&
				didPassesMatch &&
				didIdsMatch &&
				wasEmailValid &&
				wasUidValid; 
	}
	
	public boolean doPassAndIdMatch(final String pass, final String id){
		if(pass != null && id != null){
			didPassAndIdMatch = pass.equals(id);
		}
		else
			didPassAndIdMatch = false;
		
		return didPassAndIdMatch;
	}
	
	public static boolean isEmailValid(final String email){
		//See if we have a xxx@xxx.xxx style string
		if(email== null){return false;}
		
		if(pattern == null)
			pattern = Pattern.compile(EMAIL_PATTERN);
		
		matcher = pattern.matcher(email);
		return matcher.matches();
		
	}
	
	public boolean doPassesMatch(final String pass1, final String pass2){
		if(pass1 != null && pass2 != null)
			didPassesMatch = pass1.equals(pass2);
		else
			didPassesMatch = false;
		
		return didPassesMatch;
	}
	
	public boolean doIdsMatch(final String id1, final String id2){
		if(id1 != null && id2 != null)
			didIdsMatch = id1.equals(id2);
		else
			didIdsMatch = false;
		
		return didIdsMatch;
	}
	
	public static boolean isCardAccountNumberValid(final String cardAccountNumber){	
		return cardAccountNumber.startsWith(CARD_NUMBER_PREFIX) && cardAccountNumber.length() == CARD_NUMBER_LENGTH_OK 
				&& !cardAccountNumber.contains(" ");
	}
	
	public static boolean isPasswordValid(final String inputSequence){	
		boolean isPassValid    = false;
		boolean hasGoodLength  = false;
		boolean hasUpperCase   = false;
		boolean hasLowerCase   = false;
		boolean hasNonAlphaNum = false;
		boolean hasNumber 	   = false;
				
		//Check length of input.
		if(inputSequence.length() >= 8 && inputSequence.length() <= 32)
			hasGoodLength = true;
					
		//A password must have at least 1 letter and 1 number and cannot be 'password'
		//but password doesn't have a number...
			for(int i = 0; i < inputSequence.length(); ++i){

				if(Character.isLowerCase(inputSequence.charAt(i))){
					hasLowerCase = true;
				}
				else if (Character.isUpperCase(inputSequence.charAt(i))){
					hasUpperCase = true;
				}
				else if (Character.isDigit(inputSequence.charAt(i))){
					hasNumber = true;
				}				
				else if(!Character.isLetterOrDigit(inputSequence.charAt(i))){
					hasNonAlphaNum = true;
				}
			}
			
			isPassValid = (hasUpperCase || hasLowerCase) && 
					hasGoodLength && 
					hasNumber;
			
			return isPassValid;
	}
	
	public static boolean isUserIdValid(final String uid){
		boolean isUserIdValid = false;
		if(    !uid.equals("Credit Card User ID") &&
			   uid.length() >= 6 && 
			   uid.length() <= 16 &&
			   !uid.contains(" ") &&
			   !uid.contains("`") &&
			   !uid.contains("'") &&
			   !uid.contains("\"")&&
			   !uid.contains("\\") &&
			   !uid.startsWith("6011")){
				isUserIdValid = true;
			}
		else
			isUserIdValid = false;	
		
		return isUserIdValid;
	}
	
	/**
	 * Determines if a given String value represents a valid day value.
	 * i.e. true if 1 <= day <= 31
	 * @param day A String representation of a day of the month.
	 * @return returns true if the day is a valid day.
	 */
	public static boolean isDayValid(final int day) {
		return isValueBoundedBy(day, DAY_MIN_VALUE, DAY_MAX_VALUE);	
	}
	
	/**
	 * Determines if a given String value represents a valid month value.
	 * i.e. true if 1 <= month <= 12
	 * @param day A String representation of a value of the month.
	 * @return returns true if the month is a valid month.
	 */
	public static boolean isMonthValid(final int month) {
		return isValueBoundedBy(month, MONTH_MIN_VALUE, MONTH_MAX_VALUE);
	}
	
	public static boolean isYearValid(final int year) {
		return isValueBoundedBy(year, YEAR_MIN_VALUE, YEAR_MAX_VALUE);
	}
	
	public static boolean isValueBoundedBy(final int value, final int min, final int max){
		if(value >= min && value <= max)
			return true;
		else
			return false;
	}
}
