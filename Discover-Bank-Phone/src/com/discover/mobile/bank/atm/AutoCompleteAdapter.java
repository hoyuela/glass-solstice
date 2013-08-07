package com.discover.mobile.bank.atm;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;

import com.discover.mobile.bank.services.atm.autocomplete.Prediction;
import com.discover.mobile.bank.services.atm.autocomplete.PredictionsList;
import com.discover.mobile.common.net.json.JacksonObjectMapperHolder;

import android.content.Context;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.Filterable;

/*
 * This class is used to pull in auto complete locations for atm searching.
 * This class makes a HTTP request to the google places auto complete api
 * author Julian Dale
 */
public class AutoCompleteAdapter extends ArrayAdapter<Prediction> implements Filterable{

	/**results from google places query*/
	private ArrayList<Prediction> resultList;
	/**Static string the holds the url and parameters to the places api web service
	 * end of the string is intentionally left blank so that input text
	 * can easily be appended to the end.
	 */
	private static final String PLACE_URL ="https://maps.googleapis.com/maps/api/place/autocomplete/json?"
			+"key=AIzaSyB3APDriNXk_x-KHyphlUHOu7XykHNhYGQ"
			+"&types=geocode"
			+"&language=en"
			+"&sensor=true"
			+"&components=country:us"
			+"&input=";
	
	
	public AutoCompleteAdapter(Context context, int textViewResourceId) {
		super(context, textViewResourceId);
		resultList = new ArrayList<Prediction>();
	}

	/**
	 * @return number of predictions found from call
	 */
	 @Override
    public int getCount() {
        return resultList.size();
    }

	 /**
	  * Returns the prediction object at the specified index
	  * @param index of item in list to be returned
	  */
    @Override
    public Prediction getItem(int index) {
        return resultList.get(index);
    }
	
    /**
     * Returns the filter used to manage capturing and responding to user input.
     */
	 @Override
    public Filter getFilter() {
        Filter filter = new Filter() {
        	/**
        	 * This function perfoms the http call to places api
        	 * @see android.widget.Filter#performFiltering(java.lang.CharSequence)
        	 */
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
            	FilterResults filterResults = new FilterResults();
                if (constraint != null) {
                    // Retrieve the autocomplete results.
                    PredictionsList completeList = MakeAutoCompleteServiceCall(constraint.toString());
                    //if list is not null, then set the result list to new data
                    if(null != completeList) {
                    	resultList = (ArrayList<Prediction>) completeList.predicationList;
                    }
                    // Assign the data to the FilterResults,
                    //this allows the adapter to automatically update its prediction list
                    filterResults.values = resultList;
                    filterResults.count = resultList.size();
                }
                return filterResults;
            }
            /**
             * aysnc call back that responds to new filter results
             */
            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                if (results != null && results.count > 0) {
                    //tell the adapter to update what it is showing
                	notifyDataSetChanged();
                }
                else {
                	//no relevant results to show
                    notifyDataSetInvalidated();
                }
            }};
        return filter;
    }
	 
	 /**
	  * Function that makes the HTTP connection to places api and returns a list of predictions
	  * in the event of an error, the function will return null in the case of an error with HTTP request
	  * @param input - partial location user has typed in for auto complete
	  * @return list of predictions 
	  */
	 private PredictionsList MakeAutoCompleteServiceCall(final String input) {
		 
		 try {
			 //create url string
			String urlString = PLACE_URL+URLEncoder.encode(input, "UTF-8");
			URL url = new URL(urlString);
			HttpURLConnection connection = (HttpURLConnection) url.openConnection();
			InputStream body = connection.getInputStream();
			//pass input stream to the object mapper to parse json to object
			PredictionsList list = JacksonObjectMapperHolder.getMapper().readValue(body, PredictionsList.class);
			return list;
		} catch (MalformedURLException e) {
			return null;
		} catch (IOException e) {
			return null;
		}

	 }
}
