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
	
	private static int _previousMainIndex = 0;
	private static int _mainIndex = 0;
	private static int _previousSubIndex = 1;
	private static int _subIndex = 1;
	
	/**
	 * This is a utility class and should not have a public or default constructor.
	 */
	private NavigationIndex() {
		throw new UnsupportedOperationException();
	}
	
	public static void setSubIndex(final int index){
		if (index == -1 && _subIndex != -1){
			_previousSubIndex = _subIndex;
		}
		_subIndex = index;
	}
	
	public static int getMainIndex(){
		return _mainIndex;
	}

	public static void setIndex(final int index){
		if (index == _previousMainIndex){
			_subIndex = _previousSubIndex;
		}else {
			/**
			 * Only set the previous main index if sub is valid. Otherwise don't set it. 
			 * This is needed so that when flipping around in the menu and coming back 
			 * to the original option, the sub gets highlighting correctly.
			 */
			if (_subIndex != -1){
				_previousMainIndex = _mainIndex;
			}
			//Set - 1 so that no sub menu is highlighted when a main is expanded. 
			setSubIndex(-1);
		}
		_mainIndex = index;
	}
	
	public static int getSubIndex() {
		return _subIndex;
	}
	
	/**
	 * Called at login in order to clear the navigation state
	 */
	public static void clearAll(){
		_previousMainIndex = 0;
		_previousSubIndex = 0;
		_mainIndex = 0;
		_subIndex = 0;
	}

}
