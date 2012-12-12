package com.discover.mobile.common;

import java.io.Serializable;

public class SerializableData implements Serializable {
	
	private static final long serialVersionUID = -4149333001479617305L;
	
	private String userId; //$NON-NLS-1$
	private boolean saveState;
	
	public SerializableData() {
		this.userId = "";
		this.saveState = false;
	}
	
	public String getUserId() {
		if (userId == null){
			return "";
		}
		else {
			return userId;
		}
	}
	
	public boolean getSaveState() {
		return saveState;
	}
	
	public void setSaveState(boolean saveState) {
		this.saveState = saveState;
	}
	
	public void setUserId(String userId) {
		this.userId = userId;
	}
}

