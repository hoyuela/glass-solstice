package com.discover.mobile.bank.statements;

import java.io.Serializable;
import java.util.List;

import org.joda.time.DateTime;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Statement implements Serializable, Comparable<Statement> {

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

	
	
	@Override
	public int compareTo(Statement another) {
		DateTime otherDate = new DateTime(another.statementDate);
		DateTime thisDate = new DateTime(this.statementDate);
		return thisDate.compareTo(otherDate);
	}

}
