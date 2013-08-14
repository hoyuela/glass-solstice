package com.discover.mobile.card.passcode.model.json;

import java.io.Serializable;

public class VerifySyntax implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = -8867630135579798587L;
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
