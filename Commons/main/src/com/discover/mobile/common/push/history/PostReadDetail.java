package com.discover.mobile.common.push.history;

import java.io.Serializable;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

public class PostReadDetail implements Serializable{
	
	private static final long serialVersionUID = 8183770766319973175L;

	public static final String MARK_READ = "markRead";
	
	@JsonProperty("action")
	public String action;
	
	@JsonProperty("reqId")
	public List<String> messageId;

}
