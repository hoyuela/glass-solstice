package com.discover.mobile.views;

import javax.annotation.Nullable;

import com.discover.mobile.common.Struct;

@Struct
public class GeneralListItemModel {
	
	public int titleTextRes;
	public String contentTextRes;
	
	public @Nullable BottomBarModel bottomBarModel;
	public @Nullable ActionButtonModel actionButtonModel;

	@Struct
	public static class BottomBarModel {
		
		public String labelTextRes;
		public String valueTextRes;
		
	}

	@Struct
	public static class ActionButtonModel {
		
		public int buttonTextRes;
		
	}

}
