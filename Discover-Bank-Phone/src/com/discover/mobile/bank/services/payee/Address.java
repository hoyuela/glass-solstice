package com.discover.mobile.bank.services.payee;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonProperty;
/**
 * Object for holding an Address JSON object.
 * 
 * Example JSON
 * 
 * {
	"type" : "work",
	"streetAddress" : "2600 Lake Cook Road",
	"extendedAddress" : "RW1 3C AB12 (E6)",
	"locality" : "Riverwoods",
	"region" : "Illinois",
	"postalCode" : "60015",
	"formatted": "2600 Lake Cook Road\nRW1 3C AN12 (E6)\nRiverwoods Illinois 60015"
	}

 * @author scottseward
 *
 */
public class Address implements Serializable{
	private static final long serialVersionUID = 809568805843663733L;
	@JsonProperty("type")
	public String type;
	
	@JsonProperty("streetAddress")
	public String streetAddress;
	
	@JsonProperty("extendedAddress")
	public String extendedAddress;
	
	@JsonProperty("locality")
	public String locality;
	
	@JsonProperty("region")
	public String region;
	
	@JsonProperty("postalCode")
	public String postalCode;
	
	@JsonProperty("formatted")
	public String formattedAddress;
	
}
