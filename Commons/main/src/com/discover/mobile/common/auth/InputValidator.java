package com.discover.mobile.common.auth;


public class InputValidator {
	
	public InputValidator(){
		//empty
	}
	
	public static boolean validateCredentials(final String uid, final String pass){
		return (validateUID(uid) && validatePassword(pass));
	}
	
	public static boolean validateUID(final String uid){
		//Validate length
		if(    !uid.equals("Credit Card User ID") &&
			   uid.length() >= 6 && 
			   uid.length() <= 16 &&
			   !uid.contains(" ") &&
			   !uid.contains("`") &&
			   !uid.contains("'") &&
			   !uid.contains("\"")&&
			   !uid.contains("\\")){
				return true;
			}
		return false;
	}
	
	public static boolean validatePassword(final String pass){
		if(		pass.length() >= 6 && 
				pass.length() <= 32 &&
				!pass.equals("Credit Card Password"))
				{
			return true;
		}
		return false;
	}
}
