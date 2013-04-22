package com.discover.mobile.card.auth.strong;


/**
 * ©2013 Discover Bank
 * 
 * This is listener to facilitate strong authentication callbacks
 * 
 * @author CTS
 * 
 * @version 1.0
 */
public interface StrongAuthListener {

    public void onStrongAuthSucess(Object data);

    public void onStrongAuthError(Object data);

    public void onStrongAuthCardLock(Object data);
    
    public void onStrongAuthSkipped(Object data);
    
    public void onStrongAuthNotEnrolled(Object data);
}
