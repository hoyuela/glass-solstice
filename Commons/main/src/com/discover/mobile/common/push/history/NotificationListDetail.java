package com.discover.mobile.common.push.history;

import java.io.Serializable;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

public class NotificationListDetail implements Serializable{

	private static final long serialVersionUID = 7191563195254487777L;
	
	@JsonProperty("notifications")
	public List<NotificationDetail> notifications;
}
