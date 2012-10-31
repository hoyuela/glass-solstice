package com.discoverfinancial.mobile.plugins;

import java.util.ArrayList;

import org.apache.cordova.api.Plugin;
import org.apache.cordova.api.PluginResult;
import org.apache.cordova.api.PluginResult.Status;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.discoverfinancial.mobile.DiscoverMobileActivity;


import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;
import android.widget.Toast;


public class ContactsViewPlugin extends Plugin {

	static final String TAG = "ContactsViewPlugin";

	static final String INVOKE_CONTACTS = "invokecontacts";
	static final int PICK_CONTACT = 5;
	static final int PICK_CREDENTIAL = 6;

	private String callback;

	@Override
	public PluginResult execute(String action, JSONArray data, String callbackId) {
		this.callback = callbackId;
		PluginResult result = null;		
		if (action.equals(INVOKE_CONTACTS)) {	
			result = new PluginResult(Status.NO_RESULT);
			result.setKeepCallback(true);
			Uri contactsContractUri = ContactsContract.Contacts.CONTENT_URI;
			Intent intent = new Intent(Intent.ACTION_PICK, contactsContractUri);
			this.ctx.startActivityForResult((Plugin) this, intent, PICK_CONTACT);
		} else {
			result = new PluginResult(Status.INVALID_ACTION);
		}
		return result;
	}
	
	String queryContentUriWork(Uri contactData, ArrayList<String> numbers, ArrayList<String> emails, String name) {
		Cursor c = this.ctx.managedQuery(contactData, null, null, null, null); // manageQuery self-releases object
		if (c.moveToFirst()) {
			String ContactID = c.getString(c
					.getColumnIndex(ContactsContract.Contacts._ID));
			String hasPhone = c.getString(c.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER));

			if (Integer.parseInt(hasPhone) == 1) {
				Cursor phoneCursor = this.ctx.managedQuery(
						ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
						null,
						ContactsContract.CommonDataKinds.Phone.CONTACT_ID
						+ "='" + ContactID + "'", null,
						null);
				while (phoneCursor.moveToNext()) {
					String number = phoneCursor
					.getString(phoneCursor
							.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
					numbers.add(number);
				}
			}
			// get email address
			Cursor emailCur = this.ctx.managedQuery( 
					ContactsContract.CommonDataKinds.Email.CONTENT_URI, 
					null,
					ContactsContract.CommonDataKinds.Email.CONTACT_ID + "='" + ContactID + "'", null,null); 
			while (emailCur.moveToNext()) { 
				// This would allow you get several email addresses
				// if the email addresses were stored in an array
				String email = emailCur.getString(
						emailCur.getColumnIndex(ContactsContract.CommonDataKinds.Email.DATA));
				emails.add(email);
				//String emailType = emailCur.getString(
				//            emailCur.getColumnIndex(ContactsContract.CommonDataKinds.Email.TYPE)); 
			} 

			name = c.getString(c.getColumnIndexOrThrow(ContactsContract.Contacts.DISPLAY_NAME));
			//Log.v(TAG, "name: " + name);
		}
		return name;
	}

	@Override
	public void onActivityResult(int reqCode, int resultCode, Intent data) {
		switch(reqCode) {
		case PICK_CONTACT:
			if (resultCode == Activity.RESULT_OK) {
				Uri contactData = data.getData();
				ArrayList<String> numbers = new ArrayList<String>();
				ArrayList<String> emails = new ArrayList<String>();
				String name = null;
				
				name = queryContentUriWork(contactData, numbers, emails, name);
				
				if (numbers.size() > 0 || emails.size() > 0) {
					Intent intent = new Intent(this.ctx.getApplicationContext(), ContactViewResult.class);
					intent.putExtra("name", name);
					intent.putExtra("phone", numbers);
					intent.putExtra("email", emails);
					intent.putExtra("contact_uri", contactData.toString());
					this.ctx.startActivityForResult(this, intent, PICK_CREDENTIAL);
				} else {
					Uri contactsContractUri = ContactsContract.Contacts.CONTENT_URI;
					Intent intent = new Intent(Intent.ACTION_PICK, contactsContractUri);
					this.ctx.startActivityForResult((Plugin) this, intent, PICK_CONTACT);
					((DiscoverMobileActivity)this.ctx).overridePendingTransition(0, 0);
					
					CharSequence text = "No email addresses or phone numbers are available for this contact";
					int duration = Toast.LENGTH_LONG;
					Toast toast = Toast.makeText(this.ctx.getApplicationContext(), text, duration);
					toast.show();
					//Log.v(TAG, "name: " + name);
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
			//Log.v(TAG, "data: " + data);
			if (resultCode == Activity.RESULT_OK) {

				String type = data.getStringExtra("type");
				String value = data.getStringExtra("value");
				String name1 = data.getStringExtra("name");

				JSONObject jsonObject = new JSONObject();
				try {
					jsonObject.put("type", type);
					jsonObject.put("value", value);
					jsonObject.put("name", name1);
				} catch (JSONException e) {
					e.printStackTrace();
				}
				this.success(new PluginResult(PluginResult.Status.OK, jsonObject), this.callback);

			} else if (resultCode == Activity.RESULT_CANCELED) {
				//Log.v(TAG, "resultCode: " + resultCode);
				Uri contactsContractUri = ContactsContract.Contacts.CONTENT_URI;
				Intent intent = new Intent(Intent.ACTION_PICK, contactsContractUri);
				this.ctx.startActivityForResult((Plugin) this, intent, PICK_CONTACT);
				
				DiscoverMobileActivity actualCtx = (DiscoverMobileActivity) this.ctx;
				actualCtx.overridePendingTransition(0, 0);
			} else {
				this.error(new PluginResult(PluginResult.Status.NO_RESULT), this.callback);
			}

			break;
		default:
			break;
		}
	}


}
