package com.discover.mobile.card.services.push.history;

import java.io.Serializable;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Detail object for letting the server know that a notification has been read
 * @author jthornton
 *
 */
public class PostReadDetail implements Serializable{
	
	/**Unique identifier*/
	private static final long serialVersionUID = 8183770766319973175L;

	/**Static string for marking an item read*/
	public static final String MARK_READ = "markRead";
	
	/**Action attribute for the JSon obect*/
	@JsonProperty("action")
	public String action;
	
	/**Ids to mark read*/
	@JsonProperty("reqId")
	public List<String> messageId;

}
