package com.discover.mobile.common;

import java.io.Serializable;
/**
 * A Serializable object.
 * 
 * This class is used to store a user ID and the state of the save-user-ID button on the home screen.
 * It could be extended easily to support more. It is written to and loaded from file, by the UserIdPersistence class.
 * 
 * @author scottseward
 *
 */
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
		if(userId != null)
			this.userId = userId;
		else
			this.userId = "";
	}
}

