package com.discover.mobile.card.phonegap.plugins;

import java.util.ArrayList;

import org.apache.cordova.api.CallbackContext;
import org.apache.cordova.api.CordovaPlugin;
import org.apache.cordova.api.PluginResult;
import org.apache.cordova.api.PluginResult.Status;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.CursorLoader;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.provider.BaseColumns;
import android.provider.ContactsContract;
import android.widget.Toast;

import com.discover.mobile.card.common.utils.Utils;

/**
 * ContactsListPlugin serves as Interface bridge for Contacts related operation
 * between Cordova & Native
 * 
 * @author CTS
 * 
 * @version 1.0
 */
@TargetApi(Build.VERSION_CODES.HONEYCOMB)
@SuppressLint("NewApi")
public class ContactsListPlugin extends CordovaPlugin {

    static final String TAG = "ContactsViewPlugin";

    static final String showContacts = "showContacts";
    static final int PICK_CONTACT = 5;
    static final int PICK_CREDENTIAL = 6;
    static final String PHONE = "PHONE";
    static final String EMAIL = "EMAIL";
    static final String ALL = "ALL";

    boolean showEmail = true;
    boolean showPhone = true;

    JSONArray jsonArray;

    CallbackContext mCallBackContext = null;

    @Override
    public boolean execute(final String action, final String rawArgs,
            final CallbackContext callbackContext) throws JSONException {
        PluginResult result = null;
        mCallBackContext = callbackContext;

        jsonArray = new JSONArray(rawArgs);
        if (action.equals(showContacts)) {
            Utils.log(TAG, "inside ContactListPlugin");
            result = new PluginResult(Status.NO_RESULT);
            result.setKeepCallback(true);

            try {
                String queryType = (String) jsonArray.get(0);
                Utils.log(TAG, "1");
                if (queryType.equals(PHONE)) {
                    Utils.log(TAG, "2");
                    showEmail = false;
                } else if (queryType.equals(EMAIL)) {
                    Utils.log(TAG, "3");
                    showPhone = false;
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

            cordova.setActivityResultCallback(ContactsListPlugin.this);
            Uri contactsContractUri = ContactsContract.Contacts.CONTENT_URI;
            Intent intent = new Intent(Intent.ACTION_PICK, contactsContractUri);
            cordova.getActivity().startActivityForResult(intent, PICK_CONTACT);

        } else {
            return false;
        }
        return true;
    }

    @SuppressWarnings("deprecation")
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    String queryContentUriWork(final Uri contactData,
            final ArrayList<String> numbers, final ArrayList<String> emails,
            String name) {

        Cursor c = null;
        if (Build.VERSION.SDK_INT >= 11) {
            CursorLoader cursorLoader = new CursorLoader(cordova.getActivity());
            cursorLoader.setUri(contactData);
            c = cursorLoader.loadInBackground();
        } else {
            c = cordova.getActivity().managedQuery(contactData, null, null,
                    null, null); // manageQuery self-releases object

        }
        if (c.moveToFirst()) {
            String ContactID = c.getString(c.getColumnIndex(BaseColumns._ID));

            String hasPhone = c
                    .getString(c
                            .getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER));

            if (Integer.parseInt(hasPhone) == 1) {
                Cursor phoneCursor = null;
                if (Build.VERSION.SDK_INT >= 11) {
                    CursorLoader cursorLoader = new CursorLoader(
                            cordova.getActivity());
                    cursorLoader
                            .setUri(ContactsContract.CommonDataKinds.Phone.CONTENT_URI);
                    cursorLoader
                            .setSelection(ContactsContract.CommonDataKinds.Phone.CONTACT_ID
                                    + "='" + ContactID + "'");
                    phoneCursor = cursorLoader.loadInBackground();
                } else {
                    phoneCursor = cordova.getActivity().managedQuery(
                            ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                            null,
                            ContactsContract.CommonDataKinds.Phone.CONTACT_ID
                                    + "='" + ContactID + "'", null, null);
                }
                while (phoneCursor.moveToNext()) {
                    String number = phoneCursor
                            .getString(phoneCursor
                                    .getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                    numbers.add(number);
                }
            }
            // get email address
            Cursor emailCur = null;

            if (Build.VERSION.SDK_INT >= 11) {
                CursorLoader cursorLoader = new CursorLoader(
                        cordova.getActivity());
                cursorLoader
                        .setUri(ContactsContract.CommonDataKinds.Email.CONTENT_URI);
                cursorLoader
                        .setSelection(ContactsContract.CommonDataKinds.Email.CONTACT_ID
                                + "='" + ContactID + "'");
                emailCur = cursorLoader.loadInBackground();
            } else {
                emailCur = cordova.getActivity().managedQuery(
                        ContactsContract.CommonDataKinds.Email.CONTENT_URI,
                        null,
                        ContactsContract.CommonDataKinds.Email.CONTACT_ID
                                + "='" + ContactID + "'", null, null);
            }
            while (emailCur.moveToNext()) {
                // This would allow you get several email addresses
                // if the email addresses were stored in an array
                String email = emailCur
                        .getString(emailCur
                                .getColumnIndex(ContactsContract.CommonDataKinds.Email.DATA));
                emails.add(email);
                // String emailType = emailCur.getString(
                // emailCur.getColumnIndex(ContactsContract.CommonDataKinds.Email.TYPE));
            }

            name = c.getString(c
                    .getColumnIndexOrThrow(ContactsContract.Contacts.DISPLAY_NAME));
            // Log.v(TAG, "name: " + name);
        }
        return name;
    }

    @Override
    public void onActivityResult(final int reqCode, final int resultCode,
            final Intent data) {
        Utils.log(TAG, "inside onActivityResult....");
        switch (reqCode) {
        case PICK_CONTACT:
            if (resultCode == Activity.RESULT_OK) {
                Uri contactData = data.getData();
                ArrayList<String> numbers = new ArrayList<String>();
                ArrayList<String> emails = new ArrayList<String>();
                String name = null;

                name = queryContentUriWork(contactData, numbers, emails, name);

                if (numbers.size() > 0 || emails.size() > 0) {
                    Intent intent = new Intent(cordova.getActivity()
                            .getApplicationContext(), ContactViewResult.class);
                    intent.putExtra("name", name);
                    if (showPhone) {
                        intent.putExtra("phone", numbers);
                    }
                    if (showEmail) {
                        intent.putExtra("email", emails);
                    }
                    intent.putExtra("contact_uri", contactData.toString());
                    cordova.setActivityResultCallback(ContactsListPlugin.this);
                    cordova.getActivity().startActivityForResult(intent,
                            PICK_CREDENTIAL);
                } else {
                    Uri contactsContractUri = ContactsContract.Contacts.CONTENT_URI;
                    Intent intent = new Intent(Intent.ACTION_PICK,
                            contactsContractUri);
                    cordova.setActivityResultCallback(ContactsListPlugin.this);
                    cordova.getActivity().startActivityForResult(intent,
                            PICK_CONTACT);

                    CharSequence text = "No email addresses or phone numbers are available for this contact";
                    int duration = Toast.LENGTH_LONG;
                    // Fix for defect 95878
                    Toast toast = Toast.makeText(cordova.getActivity()
                            .getApplicationContext(), text, duration);
                    toast.show();
                    JSONObject jsonObject = new JSONObject();
                    try {
                        jsonObject.put("name", name);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            } else {
                // don't do anything
            }
            break;
        case PICK_CREDENTIAL:
            if (resultCode == Activity.RESULT_OK) {

                String type = data.getStringExtra("type");
                String value = data.getStringExtra("value");
                String name1 = data.getStringExtra("name");

                JSONObject jsonObject = new JSONObject();
                try {
                    jsonObject.put("type", type);
                    jsonObject.put("value", value);
                    jsonObject.put("name", name1);

                    final PluginResult pluginResult = new PluginResult(
                            PluginResult.Status.OK, jsonObject);
                    pluginResult.setKeepCallback(true);
                    mCallBackContext.sendPluginResult(pluginResult);

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            } else if (resultCode == Activity.RESULT_CANCELED) {
                // Log.v(TAG, "resultCode: " + resultCode);
                Uri contactsContractUri = ContactsContract.Contacts.CONTENT_URI;
                Intent intent = new Intent(Intent.ACTION_PICK,
                        contactsContractUri);
                cordova.setActivityResultCallback(ContactsListPlugin.this);
                cordova.getActivity().startActivityForResult(intent,
                        PICK_CONTACT);

            }
            break;
        default:
            break;
        }
    }

}
