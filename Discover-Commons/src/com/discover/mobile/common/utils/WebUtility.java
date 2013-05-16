package com.discover.mobile.common.utils;

import android.webkit.WebView;

public final class WebUtility {
	
	private WebUtility() {
		throw new AssertionError();
	}
	
	/** Calculates the % of scroll of the WebView content. */
	public static float calculateProgression(WebView view) {
	    final float top = view.getTop();
	    final float height = view.getContentHeight();
	    final float scrollY = view.getScrollY();
	    return (scrollY - top) / height;
	}
	
	/** Scrolls a WebView a specified percent amount. */
	public static void scroll(WebView view, float percent) {
		float height = view.getContentHeight() - view.getTop();
        float positionWebView = height * percent;
        int positionY = Math.round(view.getTop() + positionWebView);
        view.scrollTo(0, positionY);
	}
	
	/** Scrolls a WebView a specified percent amount after a given delay (ms).*/
	public static void scrollAfterDelay(final WebView view, final float percent, int delay) {
		view.postDelayed(new Runnable() {
            @Override
            public void run() {
                scroll(view, percent);
            }
        }, delay);
	}
	
}
