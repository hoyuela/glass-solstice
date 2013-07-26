package com.discover.mobile.bank.statements;

import java.io.Serializable;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Statement implements Serializable {

	/**
	 * Auto-generate UID for serialization and deserialization
	 */
	private static final long serialVersionUID = 4235195670760045668L;
	
	@JsonProperty("id")
	public String id;
	
	@JsonProperty("name")
	public String name;
	
	@JsonProperty("statementDate")
	public String statementDate;
	
	@JsonProperty("attachments")
	public List<StatementAttachments> attachments;
}
