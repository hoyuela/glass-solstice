/**
 * Copyright Solstice-Mobile 2013
 */
package com.discover.mobile.common.facade;

import com.discover.mobile.common.nav.section.GroupComponentInfo;

/**
 * A facade for handling customer service, which is a shared component, accessible via the nav bar
 * 
 * @author ekaram
 *
 */
public interface CustomerServiceFacade {

	/**
	 * Returns the customer service section used to generate the nav bar
	 * @return
	 */
	public GroupComponentInfo getCustomerServiceSection();
}
