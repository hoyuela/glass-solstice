/*
 * © Copyright Solstice Mobile 2013
 */
package com.discover.mobile.bank.util;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Html;

import com.discover.mobile.bank.BankExtraKeys;
import com.discover.mobile.bank.R;
import com.discover.mobile.bank.atm.AtmLocatorActivity;
import com.discover.mobile.bank.services.atm.AtmServiceHelper;
import com.discover.mobile.bank.services.atm.Directions;
import com.discover.mobile.bank.services.atm.LegDetail;
import com.discover.mobile.bank.services.atm.StepDetail;
import com.discover.mobile.common.DiscoverActivityManager;

/**
 * Email utility class used for interacting with the email intent
 * @author jthornton
 *
 */
public final class BankAtmUtil {

	/**String used for entering an end of line and a new line*/
	private static final String DOUBLE_ENTER = "\n\n";

	/**String for the type of message being sent*/
	private static final String TYPE = "text/message";

	/**Maps base url*/
	private static final String MAP_URL = "http://maps.google.com/maps?";

	/**Source address constant*/
	private static final String SOURCE_ADDRESS = "saddr=";

	/**Destination address constant*/
	private static final String DEST_ADDRESS = "&daddr=";

	/**
	 * Class constructor
	 */
	private BankAtmUtil(){}

	/**
	 * Get the full list of directions
	 * @param leg - leg detail to get the directions from
	 * @param copyright  - copyright returned from the service
	 * @return the full list of directions
	 */
	private static String getDirections(final LegDetail leg){
		final StringBuilder builder = new StringBuilder();

		for(final StepDetail step : leg.steps){
			builder.append(Html.fromHtml(step.html).toString());
			builder.append(DOUBLE_ENTER);
		}

		return builder.toString();
	}

	/**
	 * Create the email so that it can an intent can be launched to send an email
	 * @param bundle - bundle to extract the email details from
	 */
	public static void sendDirectionsEmail(final Bundle bundle){
		final AtmLocatorActivity activity = (AtmLocatorActivity)DiscoverActivityManager.getActiveActivity();
		final Directions directions = (Directions)bundle.getSerializable(BankExtraKeys.DATA_LIST_ITEM);
		final Intent i = new Intent(Intent.ACTION_SEND);  
		final LegDetail leg = directions.routes.get(0).legs.get(0);
		final String content = getDirections(leg);
		final StringBuilder builder = new StringBuilder(content);
		builder.append("Link to directions: " + MAP_URL+SOURCE_ADDRESS);
		builder.append(leg.startAddress.replace(" ", "+"));
		builder.append(DEST_ADDRESS);
		builder.append(leg.endAddress.replaceAll(" ", "+") + DOUBLE_ENTER);
		builder.append(directions.routes.get(0).copyrights);
		i.setType(TYPE);
		i.putExtra(Intent.EXTRA_SUBJECT, 
				String.format(activity.getString(R.string.atm_email_directions), leg.startAddress, leg.endAddress)); 
		i.putExtra(Intent.EXTRA_TEXT, builder.toString()); 
		activity.startActivity(i);
	}

	/**
	 * Launch the navigation activity
	 * @param helper - helper containing the address information
	 */
	public static void launchNavigation(final AtmServiceHelper helper){
		final StringBuilder builder = new StringBuilder();
		builder.append(MAP_URL+SOURCE_ADDRESS);
		builder.append(helper.getTo().replace(" ", "+"));
		builder.append(DEST_ADDRESS);
		builder.append(helper.getFrom().replaceAll(" ", "+"));
		final Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(builder.toString()));
		DiscoverActivityManager.getActiveActivity().startActivity(intent);
	}
}
