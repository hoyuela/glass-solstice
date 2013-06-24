package com.discover.mobile.card.passcode.model.json;

import java.io.Serializable;

public class VerifySyntax implements Serializable{
	public boolean isValid;

	public boolean isValid() {
		return isValid;
	}

	public void setValid(boolean isValid) {
		this.isValid = isValid;
	}

	@Override
	public String toString() {
		return "VerifySyntax [isValid=" + isValid + "]";
	}

	
}
