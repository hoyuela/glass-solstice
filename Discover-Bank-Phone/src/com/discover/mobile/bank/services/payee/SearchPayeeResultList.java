package com.discover.mobile.bank.services.payee;

import java.io.Serializable;
import java.util.List;

/**
 * Class used for holding information for a Search Result list sent from the Search Payee Bank API Web-service
 * after making a search request using the SearchPayeeResult class.
 * 
 * The following is an example of an Search Result  JSON response:
 * [{
 *	"name": "VERIZON COMMUNICATIONS",
 *	"nickName": "VERIZON COMMUNICATIONS",
 *	"merchantNumber": "19",
 *	"isZipRequired": "true"
 * },
 * {
 *	 "name": "VERIZON WIRELESS",
 *	 "merchantNumber": "VERIZON WIRELESS",
 *	 "id": "2082",
 *	 "isZipRequired": "true"
 * }]
 *
 * @author henryoyuela
 */
public class SearchPayeeResultList implements Serializable {
	/**
	 * Auto-generated serial UID which is used to serialize and de-serialize SearchPayeeResultList objects
	 */
	private static final long serialVersionUID = -9109473831261390848L;
	
	
	public List<SearchPayeeResult> results;
}
