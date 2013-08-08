package com.discover.mobile.card.services.auth.registration;

import com.discover.mobile.common.Struct;

/**
 * 
 * POGO Class for Registration web services
 * 
 * @author CTS
 * 
 * @version 1.0
 */
@Struct
public class RegistrationConfirmationDetails extends AccountInformationDetails {

    private static final long serialVersionUID = 4196814231958589918L;

    // Step Two Strings
    public String userId;
    public String email;
    public String acctLast4;
}
