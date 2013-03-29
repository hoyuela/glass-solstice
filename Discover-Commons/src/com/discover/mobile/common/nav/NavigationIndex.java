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
public class NavigationIndex {
	
	private static int PREVIOUS_MAIN_INDEX = 0;
	private static int MAIN_INDEX = 0;
	private static int PREVIOUS_SUB_INDEX = 1;
	private static int SUB_INDEX = 1;
	
	public static void setSubIndex(final int index){
		if (index == -1 && SUB_INDEX != -1){
			PREVIOUS_SUB_INDEX = SUB_INDEX;
		}
		SUB_INDEX = index;
	}
	
	public static int getMainIndex(){
		return MAIN_INDEX;
	}

	public static void setIndex(final int index){
		if (index == PREVIOUS_MAIN_INDEX){
			SUB_INDEX = PREVIOUS_SUB_INDEX;
		}else {
			PREVIOUS_MAIN_INDEX = MAIN_INDEX;
			//Set - 1 so that no sub menu is highlighted when a main is expanded. 
			setSubIndex(-1);
		}
		MAIN_INDEX = index;
	}
	
	public static int getSubIndex() {
		return SUB_INDEX;
	}
	
	/**
	 * Called at login in order to clear the navigation state
	 */
	public static void clearAll(){
		PREVIOUS_MAIN_INDEX = 0;
		PREVIOUS_SUB_INDEX = 0;
		MAIN_INDEX = 0;
		SUB_INDEX = 0;
	}

}
