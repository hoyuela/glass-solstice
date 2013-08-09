package com.discover.mobile.bank.ui.widgets;

public class BankUrlSite {
	public String link;
	public String title;
	public int urlNumber;
	public boolean selected;

	public BankUrlSite(final String link, final String title, final int urlNumber) {
		this.link = link;
		this.title = title;
		selected = false;
	}
}
