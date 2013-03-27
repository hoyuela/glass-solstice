package com.discover.mobile.card.services.push.history;

import java.io.Serializable;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * List of notifications received from the server in the get alert history call
 * @author jthornton
 *
 */
public class NotificationListDetail implements Serializable{

	/**Unique ID*/
	private static final long serialVersionUID = 7191563195254487777L;
	
	/**List of notifications*/
	@JsonProperty("notifications")
	public List<NotificationDetail> notifications;
}
