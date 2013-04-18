package com.discover.mobile.card.phonegap.plugins;

import java.util.Calendar;

import org.apache.cordova.api.CallbackContext;
import org.apache.cordova.api.CordovaPlugin;
import org.apache.cordova.api.PluginResult;
import org.json.JSONArray;
import org.json.JSONException;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.provider.CalendarContract.Events;
import android.text.format.Time;
import android.util.Log;

// To use some of the methods from EventManagerPlugin change the 
// os from 2.2 to 4.0
@SuppressLint("NewApi")
public class EventManagerPlugin extends CordovaPlugin {

    static final String TAG = "EventManagerPlugin";
    static final String invokeEventHandler = "invokeEventHandler";
    static final String showCalender = "showCalendar";
    static final String createEvent = "createEvent";
    static final String retrieveEvents = "retrieveEvents";

    CallbackContext callbackContext1;
    JSONArray jsonArray;

    @Override
    public boolean execute(String action, String rawArgs,
            CallbackContext callbackContext) throws JSONException {
        jsonArray = new JSONArray(rawArgs);
        callbackContext1 = callbackContext;
        PluginResult result = new PluginResult(PluginResult.Status.OK);

        if (action.equals(createEvent)) {
            result = new PluginResult(PluginResult.Status.NO_RESULT);
            // Looper.prepare();
            AlertDialog.Builder build = new AlertDialog.Builder(
                    EventManagerPlugin.this.cordova.getActivity());
            build.setPositiveButton("Create Event",
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            EventManagerPlugin.this.interfacedCreationOfEvent();
                        }
                    });
            build.setNegativeButton("Cancel",
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            // do nothing, need this for ui error
                        }
                    });
            AlertDialog alertDialog = build.create();
            alertDialog.show();
            // Looper.loop();
            result = new PluginResult(PluginResult.Status.NO_RESULT);
            result.setKeepCallback(true);
            callbackContext.sendPluginResult(result);
            return true;
        } else if (action.equals(showCalender)) {
            Calendar tempCal = Calendar.getInstance();
            tempCal.set(2012, 06, 9);
            Intent calendarIntent = new Intent();
            calendarIntent.putExtra("beginTime", tempCal.getTimeInMillis());
            calendarIntent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT
                    | Intent.FLAG_ACTIVITY_SINGLE_TOP
                    | Intent.FLAG_ACTIVITY_NEW_TASK);
            calendarIntent.setClassName("com.android.calendar",
                    "com.android.calendar.AgendaActivity");
            this.cordova.getActivity().startActivity(calendarIntent);
            return true;
        } else if (action.equals(retrieveEvents)) {
            // Run query
            if (Build.VERSION.SDK_INT >= 14) {
                Cursor cur = null;
                ContentResolver cr = this.cordova.getActivity()
                        .getContentResolver();
                Uri.Builder builder = Events.CONTENT_URI.buildUpon();
                cur = cr.query(builder.build(), // uri
                        new String[] { Events._ID, Events.TITLE,
                                Events.DTSTART, Events.DTEND, Events.ALL_DAY,
                                Events.DESCRIPTION }, // projection
                        "description = ?", // selection
                        null, // selection args
                        "startDay ASC, startMinute ASC"); // sort order
                while (cur.moveToNext()) {
                    @SuppressWarnings("unused")
                    // unfinished code
                    String title = null;
                    @SuppressWarnings("unused")
                    long eventID = 0;
                    @SuppressWarnings("unused")
                    long beginVal = 0;

                    Log.v(TAG, "cur: " + cur);
                    // Get the field values
                    // eventID = cur.getLong(PROJECTION_ID_INDEX);
                    // beginVal = cur.getLong(PROJECTION_BEGIN_INDEX);
                    // title = cur.getString(PROJECTION_TITLE_INDEX);

                    // Do something with the values.
                }
            }
            return true;
        } else {
            result = new PluginResult(PluginResult.Status.INVALID_ACTION);
            callbackContext.sendPluginResult(result);
            return false;
        }
    }

    // below api level 14
    // ** this method does not work on emulator
    void interfacedCreationOfEvent() {
        try {
            JSONArray startDateComponents = null;
            JSONArray endDateComponents = null;
            String title = null;
            String description = null;
            String location = null;

            try {
                startDateComponents = jsonArray.getJSONArray(0);
            } catch (JSONException e) {
            }
            try {
                endDateComponents = jsonArray.getJSONArray(1);
            } catch (JSONException e) {
            }
            try {
                if (!jsonArray.getString(2).equals("null")) {
                    title = jsonArray.getString(2);
                }
            } catch (JSONException e) {
            }
            try {
                if (!jsonArray.getString(3).equals("null")) {
                    description = jsonArray.getString(3);
                }
            } catch (JSONException e) {
            }
            try {
                if (!jsonArray.getString(4).equals("null")) {
                    location = jsonArray.getString(4);
                }
            } catch (JSONException e) {
            }

            Calendar beginTime = Calendar.getInstance();
            Calendar endTime = Calendar.getInstance();

            if (startDateComponents != null) {
                try {
                    Log.v(TAG, "startDateComponents" + startDateComponents);
                    beginTime.set(startDateComponents.getInt(0),
                            startDateComponents.getInt(1),
                            startDateComponents.getInt(2),
                            startDateComponents.getInt(3),
                            startDateComponents.getInt(4));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            if (endDateComponents != null) {
                try {
                    Log.v(TAG, "endDateComponents" + endDateComponents);
                    endTime.set(endDateComponents.getInt(0),
                            endDateComponents.getInt(1),
                            endDateComponents.getInt(2),
                            endDateComponents.getInt(3),
                            endDateComponents.getInt(4));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            long startMillis = 0;
            long endMillis = 0;

            Intent intent = new Intent(Intent.ACTION_EDIT);
            intent.setType("vnd.android.cursor.item/event");
            if (startDateComponents != null) {
                startMillis = beginTime.getTimeInMillis();
                intent.putExtra(/* Events.DTSTART */"beginTime", startMillis);
            }
            if (endDateComponents != null) {
                endMillis = endTime.getTimeInMillis();
                intent.putExtra(/* Events.DTEND */"endTime", endMillis);
            }
            if (title != null) {
                intent.putExtra(Events.TITLE, title);
            }
            if (description != null) {
                intent.putExtra(Events.DESCRIPTION, description);
            }
            if (location != null) {
                intent.putExtra(Events.EVENT_LOCATION, location);
            }

            this.cordova.getActivity().startActivity(intent);

        } catch (Exception e) {
            Log.v(TAG, "e: " + e);
        }
    }

    // api level 14
    PluginResult programaticCreationOfEvent(PluginResult result,
            JSONArray data, String callbackID) {
        JSONArray startDateComponents = null;
        JSONArray endDateComponents = null;
        String title = null;
        String description = null;
        String location = null;

        try {
            startDateComponents = data.getJSONArray(0);
            endDateComponents = data.getJSONArray(1);
            title = data.getString(2);
            description = data.getString(3);
            location = data.getString(4);
        } catch (JSONException e) {
            result = new PluginResult(
                    PluginResult.Status.ILLEGAL_ACCESS_EXCEPTION);
            return result;
        }

        long calID = 3;
        long startMillis = 0;
        long endMillis = 0;
        Calendar beginTime = Calendar.getInstance();
        Calendar endTime = Calendar.getInstance();

        try {
            beginTime.set(startDateComponents.getInt(0),
                    startDateComponents.getInt(1) - 1,
                    startDateComponents.getInt(2),
                    startDateComponents.getInt(3),
                    startDateComponents.getInt(4));
            endTime.set(endDateComponents.getInt(0),
                    endDateComponents.getInt(1) - 1,
                    endDateComponents.getInt(2), endDateComponents.getInt(3),
                    endDateComponents.getInt(4));
        } catch (JSONException e) {
            result = new PluginResult(
                    PluginResult.Status.ILLEGAL_ACCESS_EXCEPTION);
            return result;
        }

        startMillis = beginTime.getTimeInMillis();
        endMillis = endTime.getTimeInMillis();

        ContentResolver cr = this.cordova.getActivity().getContentResolver();
        ContentValues values = new ContentValues();
        values.put(Events.DTSTART, startMillis); // start
        values.put(Events.DTEND, endMillis); // end
        values.put(Events.TITLE, title); // title
        values.put(Events.DESCRIPTION, description); // description
        values.put(Events.EVENT_LOCATION, location);
        values.put(Events.CALENDAR_ID, calID); // call id
        values.put(Events.EVENT_TIMEZONE, Time.getCurrentTimezone());
        if (Build.VERSION.SDK_INT >= 14) { // remove warning (already
                                           // conditioned)
            Uri uri = cr.insert(Events.CONTENT_URI, values); // content uri
            // reminder insert

            // instable reminders insert above api level 14
            try {
                String calendarUriBase = getCalendarUriBase();
                Uri REMINDERS_URI = Uri.parse(calendarUriBase + "reminders");
                values = new ContentValues();
                values.put("event_id", Long.parseLong(uri.getLastPathSegment()));
                values.put("method", 1);
                values.put("minutes", 10);
                cr.insert(REMINDERS_URI, values);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return result;
    }

    private String getCalendarUriBase() {

        String calendarUriBase = null;
        Uri calendars = Uri.parse("content://calendar/calendars");
        Cursor managedCursor = null;
        try {
            CursorLoader cursorLoader = new CursorLoader(this.cordova
                    .getActivity().getApplicationContext());
            cursorLoader.setUri(calendars);
            managedCursor = cursorLoader.loadInBackground();
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (managedCursor != null) {
            calendarUriBase = "content://calendar/";
        } else {
            calendars = Uri.parse("content://com.android.calendar/calendars");
            try {
                CursorLoader cursorLoader = new CursorLoader(this.cordova
                        .getActivity().getApplicationContext());
                cursorLoader.setUri(calendars);
                managedCursor = cursorLoader.loadInBackground();
            } catch (Exception e) {
            }
            if (managedCursor != null) {
                calendarUriBase = "content://com.android.calendar/";
            }
        }

        return calendarUriBase;
    }
}
