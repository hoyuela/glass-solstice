package com.discover.mobile.bank.ui.modals;

/**
 * An interface to be used by modals that are intended to be used by the BaseFragment.
 * @author scottseward
 *
 */
public interface BaseFragmentModal {	
	void showModal();
		
	void setModalBodyText(final int modalTextResource);
	
	void setButtonText(final int buttonTextResource);
	
	void setTitleTextResource(final int titleTextResource);
}
