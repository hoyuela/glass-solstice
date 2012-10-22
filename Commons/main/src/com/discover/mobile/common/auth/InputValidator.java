package com.discover.mobile.common.auth;


public class InputValidator {
	
	public InputValidator(){
		//empty
	}
	
	public boolean validateCredentials(String uid, String pass){
		return (validateUID(uid) && validatePassword(pass));
	}
	
	public boolean validateUID(String uid){
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
		else
			return false;
	}
	
	public boolean validatePassword(String pass){
		if(		pass.length() >= 6 && 
				pass.length() <= 32 &&
				!pass.equals("Credit Card Password"))
				{
			return true;
		}
		else{
			return false;
		}
	}
}
