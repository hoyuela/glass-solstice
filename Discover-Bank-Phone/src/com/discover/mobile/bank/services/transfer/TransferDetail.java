package com.discover.mobile.bank.services.transfer;

import java.io.Serializable;

import android.content.Context;

import com.discover.mobile.bank.R;
import com.discover.mobile.bank.services.account.Account;
import com.discover.mobile.bank.services.json.Money;
import com.fasterxml.jackson.annotation.JsonProperty;

public class TransferDetail implements Serializable {

	/**Variable sent to the server if the transfer is to continue until cancelled*/
	public static final String UNTIL_CANCELLED = "continue_until_cancelled";

	/**Variable sent to the server if the transfer is to continue until a date*/
	public static final String UNTIL_DATE = "continue_until_a_set_end_date";

	/**Variable sent to the server if the transfer is to continue until a number of transactions have been made*/
	public static final String UNTIL_COUNT = "continue_until_a_set_number_of_transfers_have_been_made";

	/**Variable sent to the server if the transfer is to continue until a dollar amount*/
	public static final String UNTIL_AMOUNT = "continue_until_a_set_dollar_amount_has_been_transferred";

	/**Variable used to signify a one time transfer*/
	public static final String ONE_TIME_TRANSFER = "one_time_transfer";
	
	/**
	 * These values are used with the intention of identifying inline error responses from the server.
	 */
	public static final String AMOUNT = "amount.value";
	public static final String FROM_ACCOUNT = "fromAccount";
	public static final String ID = "id";
	public static final String TO_ACCOUNT = "toAccount";
	public static final String SEND_DATE = "sendDate";
	public static final String DELIVER_BY_DATE = "deliverBy";
	public static final String FREQUENCY = "frequency";
	public static final String DURATION_TYPE = "durationType";
	public static final String DURATION_VALUE = "durationValue";
	
	private static final long serialVersionUID = 3220773738601798470L;

	@JsonProperty(ID)
	public String id;

	@JsonProperty(FROM_ACCOUNT)
	public Account fromAccount;

	@JsonProperty(TO_ACCOUNT)
	public Account toAccount;

	@JsonProperty("amount")
	public Money amount;

	@JsonProperty(SEND_DATE)
	public String sendDate;

	@JsonProperty(DELIVER_BY_DATE)
	public String deliverBy;

	@JsonProperty(FREQUENCY)
	public String frequency;

	@JsonProperty(DURATION_TYPE)
	public String durationType;

	@JsonProperty(DURATION_VALUE)
	public String durationValue;
		
	/**
	 * Method used to receive a formatted string based on the frequency of this
	 * instances frequency value. he application is expected to contain an array list of strings
	 * with the name transfer_frequency and transfer_frequency_strings which can be read 
	 * from it's resource file.
	 * 
	 * @param context Reference to a Context object requesting the formatted string. Typically an activity.
	 * 				  
	 * @return Frequency formatted string
	 */
	public String getFormattedFrequency(final Context context) {
		return getFormattedFrequency(context, this.frequency);
	}
	
	/**
	 * Method used to receive a formatted string based on the frequency of this
	 * instance. The application is expected to contain an array list of strings
	 * with the name transfer_frequency and transfer_frequency_strings which can be read 
	 * from it's resource file.
	 * 
	 * @param context Reference to a Context object requesting the formatted string. Typically an activity.
	 * 				 
	 * @param frequency String with the value of the frequency whose formatted value is being retrieved.
	 * @return Frequency formatted string
	 */
	public static String getFormattedFrequency(final Context context, final String frequency) {
		final String[] frequencyCodes = context.getResources().getStringArray(R.array.transfer_frequency_codes);
		final String[] formattedFrequency = context.getResources().getStringArray(R.array.transfer_frequency_strings);
	
		String value = frequency;
		
		for(int i = 0; i < frequencyCodes.length; ++i) {
			if( frequencyCodes[i].equalsIgnoreCase(frequency) ){
				value = formattedFrequency[i];
			}
		}
		
		return value;
	}
	
	/**
	 * Method used to receive a formatted string based on the durationType of this
	 * instance. The application is expected to contain an array list of strings
	 * with the name duration_type and duration_type_strings which can be read 
	 * from it's resource file.
	 * 
	 * @param context Reference to a Context object requesting the formatted string. Typically an activity.
	 * 				  
	 * @return Duration formatted string
	 */
	public String getFormattedDuration(final Context context) {
		return getFormattedFrequency(context, this.durationType);
	}
	
	/**
	 * Method used to receive a formatted string based on the durationType of this
	 * instance. The application is expected to contain an array list of strings
	 * with the name duration_type and duration_type_strings which can be read 
	 * from it's resource file.
	 * 
	 * @param context Reference to a Context object requesting the formatted string. Typically an activity.
	 * 	
	 * @return Duration formatted string
	 */
	public static String getFormattedDuration(final Context context, final String durationType) {
		final String[] durationTypes = context.getResources().getStringArray(R.array.duration_type);
		final String[] formattedDurations = context.getResources().getStringArray(R.array.duration_type_strings);
	
		String value = durationType;
		
		for(int i = 0; i < durationTypes.length; ++i) {
			if(durationTypes[i].equalsIgnoreCase(durationType)){
				value = formattedDurations[i];
			}
		}
		
		return value;
	}
}
