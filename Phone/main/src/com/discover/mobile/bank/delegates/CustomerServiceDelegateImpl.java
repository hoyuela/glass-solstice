/**
 * Copyright Solstice-Mobile 2013
 */
package com.discover.mobile.bank.delegates;

import com.discover.mobile.bank.help.CustomerServiceSectionInfo;
import com.discover.mobile.common.delegates.CustomerServiceDelegate;
import com.discover.mobile.common.nav.section.GroupComponentInfo;

/**
 * @author ekaram
 *
 */
public class CustomerServiceDelegateImpl implements CustomerServiceDelegate {
	
	
	/* (non-Javadoc)
	 * @see com.discover.mobile.common.delegates.CustomerServiceDelegate#getCustomerServiceSection()
	 */
	@Override
	public GroupComponentInfo getCustomerServiceSection() {
		return new CustomerServiceSectionInfo();
	}
	


}
