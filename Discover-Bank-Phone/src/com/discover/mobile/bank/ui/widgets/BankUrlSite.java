package com.discover.mobile.bank.ui.widgets;

public class BankUrlSite {
	public static final String TITLE = "title";
	public static final String LINK = "url";

	public String link;
	public String title;
	public int urlNumber;
	public boolean selected;

	public BankUrlSite(final String link, final String title, final int urlNumber) {
		this.link = link;
		this.title = title;
		this.urlNumber = urlNumber;
		selected = false;
	}

	public boolean isEqualTo(final BankUrlSite site){
		return link == site.link && title == site.title && urlNumber == site.urlNumber;
	}
}
