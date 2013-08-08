package com.discover.mobile.card.services.auth.forgot;

import java.io.Serializable;

import com.discover.mobile.card.services.auth.registration.AccountInformationDetails;
import com.discover.mobile.common.Struct;

/**
 * POGO Class for forgot password module
 * 
 * @author CTS
 * 
 * @version 1.0
 */
@Struct
public class ForgotPasswordTwoDetails extends AccountInformationDetails
        implements Serializable {

    private static final long serialVersionUID = 5094796037192037185L;

    public String password;
    public String passwordConfirm;

}
