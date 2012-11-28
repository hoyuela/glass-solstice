package com.discover.mobile.common.auth;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class InputValidator {
	private Pattern emailPattern;
	private final static String EMAIL_PATTERN = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"
									+ "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";
	public boolean 
	wasDobMonthValid, wasDobYearValid, wasDobDayValid, 
	wasAccountNumberValid, wasSsnValid, wasEmailValid, 
	wasUidValid, wasPassValid, didPassesMatch, didIdsMatch, didPassAndIdMatch,
	wasCardExpMonthValid, wasCardExpYearValid
	;
	
	public InputValidator(){
		emailPattern = Pattern.compile(EMAIL_PATTERN);
		clear();
	}
	
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
		return !didPassAndIdMatch & didPassesMatch & didIdsMatch & wasEmailValid & wasUidValid; 
	}
	
	public boolean doPassAndIdMatch(final String pass, final String id){
		if(pass != null && id != null){
			didPassAndIdMatch = pass.equals(id);
		}
		else
			didPassAndIdMatch = false;
		
		return didPassAndIdMatch;
	}
	
	
	public boolean isEmailValid(final String email){
		//See if we have a xxx@xxx.xxx style string
		//Need some regular expressions up in here!
		final Matcher matcher = emailPattern.matcher(email);
		
		wasEmailValid = matcher.matches();
		
		return wasEmailValid;

	}
	
	public boolean isSsnValid(final String ssn){
		if(ssn.length() == 4)
			wasSsnValid = true;
		else
			wasSsnValid = false;

		return wasSsnValid;
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
	
	public boolean isDobDayValid(final String day){
		if(!"Day".equals(day) && day != null)
			wasDobDayValid = true;
		else
			wasDobDayValid = false;
		
		return wasDobDayValid;
			
	}
	public boolean isDobMonthValid(final String month){
		if(!"Month".equals(month) && month != null)
			wasDobMonthValid = true;
		else
			wasDobMonthValid = false;
		
		return wasDobMonthValid;
		
	}
	public boolean isDobYearValid(final String year){		
		if(!"Year".equals(year) && year.length() == 4)
			wasDobYearValid = true;
		else
			wasDobYearValid = false;	
		
		return wasDobYearValid;
	}
	
	public boolean isLoginCredentialsValid(final String uid, final String pass){
		return isUidValid(uid) && isPassValid(pass);
	}
	
	public boolean isCardAccountNumberValid(final String cardAccountNumber){
		if(		cardAccountNumber.startsWith("6011") &&
				cardAccountNumber.length() == 16)
			wasAccountNumberValid = true;
		else
			wasAccountNumberValid = false;	
		
		return wasAccountNumberValid;
	}
	
	public boolean isCardExpMonthValid(final String cardExpMonth){
		if(!"Month".equals(cardExpMonth) && cardExpMonth != null)
			wasCardExpMonthValid = true;
		else
			wasCardExpMonthValid = false;	
		
		return wasCardExpMonthValid;
	}
	
	public boolean isCardExpYearValid(final String cardExpYear){
		if( !"YYYY".equals(cardExpYear) && 
				cardExpYear != null && 
				cardExpYear.length() == 4)
			 wasCardExpYearValid = true;
		else
			 wasCardExpYearValid = false;
		
		return wasCardExpYearValid;
	}
	
	public boolean isPassValid(final String inputSequence){		
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
			
			wasPassValid = (hasUpperCase || hasLowerCase) && 
					hasGoodLength && 
					hasNumber;
			
			return wasPassValid;
	}
	
	public boolean isUidValid(final String uid){

		if(    !uid.equals("Credit Card User ID") &&
			   uid.length() >= 6 && 
			   uid.length() <= 16 &&
			   !uid.contains(" ") &&
			   !uid.contains("`") &&
			   !uid.contains("'") &&
			   !uid.contains("\"")&&
			   !uid.contains("\\") &&
			   !uid.startsWith("6011")){
				wasUidValid = true;
			}
		else
			wasUidValid = false;	
		
		return wasUidValid;
	}
}
