package com.discover.mobile.card.fastcheck;

import java.io.Serializable;

/**
 * ©2013 Discover Bank
 * 
 * FastcheckDetail POJO Class for saving web service response
 * 
 * @author CTS
 * 
 * @version 1.0
 * 
 * 
 */
public class FastcheckDetail implements Serializable {

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

    public FastcheckDetail(final String aName, final String anAvailableCredit,
            final String aCurrentBalance, final String anEarnRewardAmount,
            final String anIncentiveTypeCode, final String aCardImage,
            final String anIncentiveCode) {
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

    public void setCardImage(final String cardImage) {
        this.cardImage = cardImage;
    }

    public String getIncentiveCode() {
        return incentiveCode;
    }

    public void setIncentiveCode(final String incentiveCode) {
        this.incentiveCode = incentiveCode;
    }

    public String getCardmemberFirstName() {
        return cardmemberFirstName;
    }

    public void setCardmemberFirstName(final String cardmemberFirstName) {
        this.cardmemberFirstName = cardmemberFirstName;
    }

    public boolean isRewardsOutage() {
        return rewardsOutage;
    }

    public void setRewardsOutage(final boolean rewardsOutage) {
        this.rewardsOutage = rewardsOutage;
    }

    public boolean isAcLiteOutageMode() {
        return acLiteOutageMode;
    }

    public void setAcLiteOutageMode(final boolean acLiteOutageMode) {
        this.acLiteOutageMode = acLiteOutageMode;
    }

    public String getAvailableCredit() {
        return availableCredit;
    }

    public void setAvailableCredit(final String availableCredit) {
        this.availableCredit = availableCredit;
    }

    public String getCurrentBalance() {
        return currentBalance;
    }

    public void setCurrentBalance(final String currentBalance) {
        this.currentBalance = currentBalance;
    }

    public String getEarnRewardAmount() {
        return earnRewardAmount;
    }

    public void setEarnRewardAmount(final String earnRewardAmount) {
        this.earnRewardAmount = earnRewardAmount;
    }

    public String getOutageModeVal() {
        return outageModeVal;
    }

    public void setOutageModeVal(final String outageModeVal) {
        this.outageModeVal = outageModeVal;
    }

    public String getIncentiveTypeCode() {
        return incentiveTypeCode;
    }

    public void setIncentiveTypeCode(final String incentiveTypeCode) {
        this.incentiveTypeCode = incentiveTypeCode;
    }

    public static long getSerialversionuid() {
        return serialVersionUID;
    }

}
