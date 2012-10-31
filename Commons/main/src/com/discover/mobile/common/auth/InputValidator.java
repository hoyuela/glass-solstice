package com.discover.mobile.common.auth;



public class InputValidator {
	public boolean 
	wasDobMonthValid, wasDobYearValid, wasDobDayValid, 
	wasAccountNumberValid, wasSsnValid, wasEmailValid, 
	wasUidValid, wasPassValid, didPassesMatch, didIdsMatch, didPassAndIdMatch,
	wasCardExpMonthValid, wasCardExpYearValid
	;
	
	public InputValidator(){
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
	
	public boolean wasAccountInfoComplete(){
		//bitwise operators FTW
		return wasSsnValid & wasDobMonthValid & wasDobMonthValid & wasDobDayValid &
				wasAccountNumberValid & wasCardExpMonthValid & wasCardExpYearValid;
	}
	
	public boolean wasAccountTwoInfoComplete(){
		return !didPassAndIdMatch & didPassesMatch & didIdsMatch & wasEmailValid; 
	}
	
	public boolean doPassAndIdMatch(String pass, String id){
		if(pass != null && id != null){
			didPassAndIdMatch = pass.equals(id);
		}
		else
			didPassAndIdMatch = false;
		
		return didPassAndIdMatch;
	}
	
	
	public boolean isEmailValid(String email){
		wasEmailValid = true;
		
		return wasEmailValid;
	}
	
	public boolean isSsnValid(String ssn){
		if(ssn.length() == 4)
			wasSsnValid = true;
		else
			wasSsnValid = false;

		return wasSsnValid;
	}
	
	public boolean doPassesMatch(String pass1, String pass2){
		if(pass1 != null && pass2 != null)
			didPassesMatch = pass1.equals(pass2);
		else
			didPassesMatch = false;
		
		return didPassesMatch;
	}
	
	public boolean doIdsMatch(String id1, String id2){
		if(id1 != null && id2 != null)
			didIdsMatch = id1.equals(id2);
		else
			didIdsMatch = false;
		
		return didIdsMatch;
	}
	
	public boolean isDobDayValid(String day){
		if(!"Day".equals(day) && day != null)
			wasDobDayValid = true;
		else
			wasDobDayValid = false;
		
		return wasDobDayValid;
			
	}
	public boolean isDobMonthValid(String month){
		if(!"Month".equals(month) && month != null)
			wasDobMonthValid = true;
		else
			wasDobMonthValid = false;
		
		return wasDobMonthValid;
		
	}
	public boolean isDobYearValid(String year){		
		if(!"Year".equals(year) && year.length() == 4)
			wasDobYearValid = true;
		else
			wasDobYearValid = false;	
		
		return wasDobYearValid;
	}
	
	public boolean isLoginCredentialsValid(String uid, String pass){
		return (isUidValid(uid) && isPassValid(pass));
	}
	
	public boolean isCardAccountNumberValid(String cardAccountNumber){
		if(		cardAccountNumber.startsWith("6011") &&
				cardAccountNumber.length() == 16)
			wasAccountNumberValid = true;
		else
			wasAccountNumberValid = false;	
		
		return wasAccountNumberValid;
	}
	
	public boolean isCardExpMonthValid(String cardExpMonth){
		if(!"Month".equals(cardExpMonth) && cardExpMonth != null)
			wasCardExpMonthValid = true;
		else
			wasCardExpMonthValid = false;	
		
		return wasCardExpMonthValid;
	}
	
	public boolean isCardExpYearValid(String cardExpYear){
		if( !"Year".equals(cardExpYear) && cardExpYear != null )
			 wasCardExpYearValid = true;
		else
			 wasCardExpYearValid = false;
		
		return wasCardExpYearValid;
	}
	
	public boolean isPassValid(String pass){
		if(		pass.length() >= 6 && 
				pass.length() <= 32 &&
				!pass.equals("Credit Card Password"))
				{
			wasPassValid = true;
		}
		else 
			wasPassValid = false;	
		
		return wasPassValid;
	}
	
	public boolean isUidValid(String uid){

		if(    !uid.equals("Credit Card User ID") &&
			   uid.length() >= 6 && 
			   uid.length() <= 16 &&
			   !uid.contains(" ") &&
			   !uid.contains("`") &&
			   !uid.contains("'") &&
			   !uid.contains("\"")&&
			   !uid.contains("\\")){
				wasUidValid = true;
			}
		else
			wasUidValid = false;	
		
		return wasUidValid;
	}
}
