package com.discover.mobile.common.auth;


public class InputValidator {
	private String uid, pass;
	
	public InputValidator(String uid, String pass){
		this.uid = uid;
		this.pass = pass;
	}
	
	public boolean isCredentialsValid(){
		return (validateUID(uid) && validatePassword(pass));
	}
	
	public boolean isPassValid(){
		return validatePassword(pass);
	}
	
	public boolean isUidValid(){
		return validateUID(uid);
	}
	
	public void setUid(String uid){
		this.uid = uid;
	}
	
	public void setPass(String pass){
		this.pass = pass;
	}
	
	private boolean validateUID(final String uid){
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
	
	private boolean validatePassword(final String pass){
		if(		pass.length() >= 6 && 
				pass.length() <= 32 &&
				!pass.equals("Credit Card Password"))
				{
			return true;
		}
		return false;
	}
}
