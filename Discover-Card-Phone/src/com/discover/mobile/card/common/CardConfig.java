package com.discover.mobile.card.common;

public class CardConfig {
	private static CardConfig config = null;

	private CardConfig() {

	}

	public static CardConfig getCardConfig() {
		if (null == config)
			config = new CardConfig();
		return config;
	}

}
