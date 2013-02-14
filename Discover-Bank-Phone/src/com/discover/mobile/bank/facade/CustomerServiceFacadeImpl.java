/**
 * Copyright Solstice-Mobile 2013
 */
package com.discover.mobile.bank.facade;

import com.discover.mobile.bank.help.CustomerServiceSectionInfo;
import com.discover.mobile.common.facade.CustomerServiceFacade;
import com.discover.mobile.common.nav.section.GroupComponentInfo;

/**
 * @author ekaram
 *
 */
public class CustomerServiceFacadeImpl implements CustomerServiceFacade {
	
	
	/* (non-Javadoc)
	 * @see com.discover.mobile.common.delegates.CustomerServiceFacade#getCustomerServiceSection()
	 */
	@Override
	public GroupComponentInfo getCustomerServiceSection() {
		return new CustomerServiceSectionInfo();
	}
	


}
