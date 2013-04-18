package com.discover.mobile.card.auth.strong;


/**
 * ©2013 Discover Bank
 * 
 * TODO: Class description
 * 
 * @author CTS
 * 
 * @version 1.0
 */
public interface StrongAuthListener {

    public void onStrongAuthSucess(Object data);

    public void onStrongAuthError(Object data);

    public void onStrongAuthCardLock(Object data);
}
