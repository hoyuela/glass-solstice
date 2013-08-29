package com.discover.mobile.card.services.auth;

import java.io.Serializable;
import java.util.ArrayList;

import com.discover.mobile.common.Struct;
import com.fasterxml.jackson.annotation.JsonProperty;

/***
 * POJO Class for Account Details
 * 
 * @author CTS
 * 
 * @version 1.0
 */
@Struct
public class AccountDetails implements Serializable {

    private static final long serialVersionUID = 586314752787142118L;

    public String availableCredit;
    public String currentBalance;
    public String lastPaymentAmount;
    public String lastPaymentDate;
    public String minimumPaymentDue;
    public String outageModeVal;
    public String paymentDueDate;

    @JsonProperty("primaryCardmember")
    public PrimaryCardMember primaryCardMember;

    @JsonProperty("mailingAddress")
    public MailingAddress mailingAddress;
    public String statementBalance;
    public String earnRewardAmount;
    public String lastFourAcctNbr;
    public String rewardType;
    public String cardType;
    public String incentiveCode;
    public String incentiveTypeCode;
    public String cardProductGroupCode;
    public String optionCode;
    public String newlyEarnedRewards;
    public String statementsTransaction;
    public String payLoadSSOText;

    public boolean acLiteOutageMode;
    public boolean cardProductGroupOutageMode;
    public boolean paperlessOutageMode;
    public boolean rewardOutage;
    public boolean smcOutageMode;
    public boolean isSSNMatched;
    public boolean isSSOUser;
    
    /*    13.5  Changes */
    @JsonProperty("hideScreens")
    public ArrayList<String> hideScreens;
    /*    13.5  Changes */

    @Struct
    public static class PrimaryCardMember {
        public String nameOnCard;
        public String emailAddress;
    }

    @Struct
    public static class MailingAddress {
        public String city;
        public String firstName;
        public String lastName;
        public String line1;
        public String line2;
        public String middleName;
        public String postalCode;
        public String state;
    }

}
