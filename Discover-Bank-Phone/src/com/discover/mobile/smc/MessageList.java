package com.discover.mobile.smc;

import java.io.Serializable;
import java.util.List;

/**
 * POJO that represents the list of messages returned from the server
 * @author juliandale
 *
 */

public class MessageList implements Serializable {

	/**
	 * auto-generated serial UID
	 */
	private static final long serialVersionUID = 3672127514433826049L;

	/**List of the returned messages*/
	public List<MessageListItem> messages;
	
}
