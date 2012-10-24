package com.discover.mobile.common.auth;

import com.fasterxml.jackson.annotation.JsonProperty;

public class AccountDetails {
	
	public String availableCredit;
	public String currentBalance;
	public String lastPaymentAmount;
	public String lastPaymentDate;
	public String minimumPaymentDue;
	public String outageModeVal;
	public String paymentDueDate;  // TODO Find out how Jackson maps Dates
	
	@JsonProperty("primaryCardmember")
	public PrimaryCardMember primaryCardMember;
	
	public static class PrimaryCardMember {
		public String nameOnCard;
		public String emailAddress;
	}
	
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
	public boolean acLiteOutageMode;
	public boolean cardProductGroupOutageMode;
	public boolean paperlessOutageMode;
	public boolean rewardOutage;
	public boolean smcOutageMode;

}
