package com.discover.mobile.common.nav;


/**
 * This class holds on to the index's for the menu navigation. It's kept separate from 
 * the listview so that on rotation change the correct index is used. When a main menu item 
 * is selected set the sub to -1 so that no sub row is highlighted. We have to keep track 
 * of a previous and current in order to know when bouncing around the menu what the last 
 * selected option was. 
 * 
 * @author ajleeds
 *
 */
public final class NavigationIndex {
	
	private static int previousMainIndex = 0;
	private static int mainIndex = 0;
	private static int previousSubIndex = 1;
	private static int subIndex = 1;
	
	/**
	 * This is a utility class and should not have a public or default constructor.
	 */
	private NavigationIndex() {
		throw new UnsupportedOperationException();
	}
	
	public static void setSubIndex(final int index){
		if (index == -1 && subIndex != -1){
			previousSubIndex = subIndex;
		}
		subIndex = index;
	}
	
	public static int getMainIndex(){
		return mainIndex;
	}

	public static void setIndex(final int index){
		if (index == previousMainIndex){
			subIndex = previousSubIndex;
		}else {
			/**
			 * Only set the previous main index if sub is valid. Otherwise don't set it. 
			 * This is needed so that when flipping around in the menu and coming back 
			 * to the original option, the sub gets highlighting correctly.
			 */
			if (subIndex != -1){
				previousMainIndex = mainIndex;
			}
			//Set - 1 so that no sub menu is highlighted when a main is expanded. 
			setSubIndex(-1);
		}
		mainIndex = index;
	}
	
	public static int getSubIndex() {
		return subIndex;
	}
	
	/**
	 * Called at login in order to clear the navigation state
	 */
	public static void clearAll(){
		previousMainIndex = 0;
		previousSubIndex = 0;
		mainIndex = 0;
		subIndex = 0;
	}

}
