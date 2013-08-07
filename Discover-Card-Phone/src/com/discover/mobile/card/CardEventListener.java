package com.discover.mobile.card;

import android.content.Intent;
import android.net.Uri;
import android.os.Handler;

import com.discover.mobile.card.privacyterms.PrivacyTermsStatement;
import com.discover.mobile.common.BaseActivity;
import com.discover.mobile.common.error.ErrorHandler;

/**
 * Activity written for navigate to the browser via the url
 * <p>
 * extended from {@link BaseActivity}
 * </p>
 * 
 * @author CTS
 * 
 * @version 1.0
 * 
 */
public class CardEventListener extends BaseActivity {

    public static final String METHOD_SCHEME = "cardprivacystatements";
    private static final String PRIVACY_STATEMENT = "navigateToMobilePrivacyStatement";

    @Override
    public void onResume() {
        super.onResume();

        navigateTo(getIntent().getData());
    }

    /**
     * Method used to navigate to the browser via the url stored in the data
     * object. The data object is expected to have a string with the scheme
     * com.discover.mobile://. This scheme is replaced with https and the user
     * is prompted with a modal that they will be leaving the application.
     * 
     * @param data
     *            Holds the URL used to open the device default browser.
     * 
     * @return Runnable that will open
     */
    public void navigateTo(final Uri data) {
        if (data != null) {
            // Method scheme is used to call a method defined in the application
            if (data.getScheme().equalsIgnoreCase(METHOD_SCHEME)) {
                final String method = data.getSchemeSpecificPart();
                if (method.contains(PRIVACY_STATEMENT)) {
                    this.finish();
                    final long halfSecond = 500;
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            Intent privacyStatement = new Intent(
                                    CardEventListener.this,
                                    PrivacyTermsStatement.class);
                            startActivity(privacyStatement);
                        }
                    }, halfSecond);
                }
            }

        }
    }

    @Override
    public ErrorHandler getErrorHandler() {

        return null;
    }
}
