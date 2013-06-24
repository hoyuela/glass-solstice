package com.discover.mobile.card.passcode.model.json;

import java.io.Serializable;

public class Status implements Serializable {

	private boolean accountHasPasscode;
	private boolean deviceBoundToThisAccount;
	private int deviceCount;
	public boolean isAccountHasPasscode() {
		return accountHasPasscode;
	}
	public void setAccountHasPasscode(boolean accountHasPasscode) {
		this.accountHasPasscode = accountHasPasscode;
	}
	public boolean isDeviceBoundToThisAccount() {
		return deviceBoundToThisAccount;
	}
	public void setDeviceBoundToThisAccount(boolean deviceBoundToThisAccount) {
		this.deviceBoundToThisAccount = deviceBoundToThisAccount;
	}
	public int getDeviceCount() {
		return deviceCount;
	}
	public void setDeviceCount(int deviceCount) {
		this.deviceCount = deviceCount;
	}
	@Override
	public String toString() {
		return "Status [accountHasPasscode=" + accountHasPasscode
				+ ", deviceBoundToThisAccount=" + deviceBoundToThisAccount
				+ ", deviceCount=" + deviceCount + "]";
	}
	
}
