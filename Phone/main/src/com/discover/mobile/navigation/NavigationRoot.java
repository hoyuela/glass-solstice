package com.discover.mobile.navigation;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;

interface NavigationRoot {
	
	FragmentManager getSupportFragmentManager();
	void replaceMainFragment(Fragment newFragment, boolean closeMenu);
	
}
