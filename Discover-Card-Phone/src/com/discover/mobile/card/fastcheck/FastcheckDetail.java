package com.discover.mobile.card.fastcheck;

import java.io.Serializable;

public class FastcheckDetail implements Serializable {

    /**
	 * 
	 */
    private static final long serialVersionUID = 171317039250648494L;

    public String cardmemberFirstName;
    public String availableCredit;
    public String currentBalance;
    public String earnRewardAmount;
    public String outageModeVal;
    public String incentiveTypeCode;
    public String cardImage;
    public String incentiveCode;
    public boolean rewardsOutage;
    public boolean acLiteOutageMode;

    public FastcheckDetail() {
        super();
    }

    public FastcheckDetail(String aName, String anAvailableCredit,
            String aCurrentBalance, String anEarnRewardAmount,
            String anIncentiveTypeCode, String aCardImage,
            String anIncentiveCode) {
        super();
        cardmemberFirstName = aName;
        availableCredit = anAvailableCredit;
        currentBalance = aCurrentBalance;
        earnRewardAmount = anEarnRewardAmount;
        incentiveTypeCode = anIncentiveTypeCode;
        cardImage = aCardImage;
        incentiveCode = anIncentiveCode;
    }

    public String getCardImage() {
        return cardImage;
    }

    public void setCardImage(String cardImage) {
        this.cardImage = cardImage;
    }

    public String getIncentiveCode() {
        return incentiveCode;
    }

    public void setIncentiveCode(String incentiveCode) {
        this.incentiveCode = incentiveCode;
    }

    public String getCardmemberFirstName() {
        return cardmemberFirstName;
    }

    public void setCardmemberFirstName(String cardmemberFirstName) {
        this.cardmemberFirstName = cardmemberFirstName;
    }

    public boolean isRewardsOutage() {
        return rewardsOutage;
    }

    public void setRewardsOutage(boolean rewardsOutage) {
        this.rewardsOutage = rewardsOutage;
    }

    public boolean isAcLiteOutageMode() {
        return acLiteOutageMode;
    }

    public void setAcLiteOutageMode(boolean acLiteOutageMode) {
        this.acLiteOutageMode = acLiteOutageMode;
    }

    public String getAvailableCredit() {
        return availableCredit;
    }

    public void setAvailableCredit(String availableCredit) {
        this.availableCredit = availableCredit;
    }

    public String getCurrentBalance() {
        return currentBalance;
    }

    public void setCurrentBalance(String currentBalance) {
        this.currentBalance = currentBalance;
    }

    public String getEarnRewardAmount() {
        return earnRewardAmount;
    }

    public void setEarnRewardAmount(String earnRewardAmount) {
        this.earnRewardAmount = earnRewardAmount;
    }

    public String getOutageModeVal() {
        return outageModeVal;
    }

    public void setOutageModeVal(String outageModeVal) {
        this.outageModeVal = outageModeVal;
    }

    public String getIncentiveTypeCode() {
        return incentiveTypeCode;
    }

    public void setIncentiveTypeCode(String incentiveTypeCode) {
        this.incentiveTypeCode = incentiveTypeCode;
    }

    public static long getSerialversionuid() {
        return serialVersionUID;
    }

}
