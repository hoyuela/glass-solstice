package com.discover.mobile.common.auth;

import java.io.Serializable;

import com.discover.mobile.common.Struct;
import com.fasterxml.jackson.annotation.JsonProperty;

@Struct
public class AccountDetails implements Serializable {

	private static final long serialVersionUID = 5560948851652492739L;
	
	public String availableCredit;
	public String currentBalance;
	public String lastPaymentAmount;
	public String lastPaymentDate;
	public String minimumPaymentDue;
	public String outageModeVal;
	public String paymentDueDate;  // TODO Find out how Jackson maps Dates
	
	@JsonProperty("primaryCardmember")
	public PrimaryCardMember primaryCardMember;
	
	public String statementBalance;
	public String earnRewardAmount;
	public String lastFourAcctNbr;
	public String rewardType;
	public String cardType;
	public String incentiveCode;
	public String incentiveTypeCode;
	public String cardImage;
	public String cardProductGroupCode;
	public String optionCode;
	public String newlyEarnedRewards;
	public String statementsTransaction;
	
	public ContactInfo contactInfo;
	
	public boolean acLiteOutageMode;
	public boolean cardProductGroupOutageMode;
	public boolean paperlessOutageMode;
	public boolean rewardOutage;
	public boolean smcOutageMode;

	@Struct
	public static class PrimaryCardMember {
		public String nameOnCard;
		public String emailAddress;
	}

	@Struct
	public static class ContactInfo {
		public String firstName;
		public String middleName;
		public String lastName;
		public String line1;
		public String line2;
		public String city;
		public String state;
		public String postalCode;
	}
}
