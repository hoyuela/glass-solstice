package com.discover.mobile.bank.account;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.discover.mobile.bank.ui.table.ViewPagerListItem;

public class ListViewPagerListItem implements Serializable{

	private static final long serialVersionUID = -460653038917782931L;
	public List<ViewPagerListItem> viewPagerItemList = new ArrayList<ViewPagerListItem>();
}
