package com.discover.mobile.card.phonegap.plugins;

import java.util.ArrayList;

import com.discover.mobile.card.R;


import android.app.Activity;
import android.app.ExpandableListActivity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.TextView;

public class ContactViewResult extends ExpandableListActivity {

	static final String TAG = "ContactViewResult";

	String name = null;
	ArrayList<CharSequence> phones = null;
	ArrayList<CharSequence> emails = null;

	ProfileInfoAdapter adapter = null;

	static final int PHONE = 0;
	static final int EMAIL = 1;

	TextView textview_name;
	//ImageView imageView;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.contactviewresult);

		//imageView = (ImageView) findViewById(R.id.contactviewresult_imageview);
		textview_name = (TextView) findViewById(R.id.contactviewresult_name);		
		name = this.getIntent().getStringExtra("name");
		if (name != null) {
			textview_name.setText(name);
		}

		String contactData = this.getIntent().getStringExtra("contact_uri");
		Log.v(TAG, "contactData: " + contactData);

		phones = this.getIntent().getCharSequenceArrayListExtra("phone");
		emails = this.getIntent().getCharSequenceArrayListExtra("email");
		adapter = new ProfileInfoAdapter(this);
		setListAdapter(adapter);

		getExpandableListView().expandGroup(0);
		getExpandableListView().expandGroup(1);

	}	

	public boolean onChildClick (ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
		Intent data = new Intent();
		switch(groupPosition) {
		case PHONE:
			data.putExtra("value", phones.get(childPosition));
			setResult(Activity.RESULT_OK);
			data.putExtra("type", "phone");
			data.putExtra("name", name);
			this.setResult(Activity.RESULT_OK, data);
			finish();
			break;
		case EMAIL:
			data.putExtra("value", emails.get(childPosition));
			setResult(Activity.RESULT_OK);
			data.putExtra("type", "email");
			data.putExtra("name", name);
			this.setResult(Activity.RESULT_OK, data);
			finish();
			break;
		default: 
			break;
		}

		return true;    		
	}

	public void onBackPressed() {
		this.setResult(Activity.RESULT_CANCELED);
		finish();
	}

	class ProfileInfoAdapter extends BaseExpandableListAdapter {

		Context context;
		LayoutInflater inflater;
		public ProfileInfoAdapter(Context context) {
			inflater = LayoutInflater.from(context);
			this.context = context;
		}


		public Object getChild(int arg0, int arg1) {
			return null;
		}


		public long getChildId(int groupPosition, int childPosition) {
			return 0;
		}


		public View getChildView(int groupPosition, int childPosition,
				boolean isLastChild, View convertView, ViewGroup parent) {
			View view = inflater.inflate(R.layout.contactviewresult_childcell, null);
			TextView textView = (TextView) view.findViewById(R.id.contactviewresult_childcell_text);
			if (groupPosition == 0) {
				textView.setText(phones.get(childPosition));
			} else if (groupPosition == 1) {
				textView.setText(emails.get(childPosition));
			}
			return view;
		}


		public int getChildrenCount(int groupPosition) {
			if (groupPosition == 0) {
				return phones.size();
			} else if (groupPosition == 1) {
				return emails.size();
			}
			return 0;
		}


		public Object getGroup(int groupPosition) {
			return null;
		}


		public int getGroupCount() {
			return 2;
		}


		public long getGroupId(int groupPosition) {
			return groupPosition;
		}


		public View getGroupView(int groupPosition, boolean isExpanded,
				View convertView, ViewGroup parent) {
			View view = inflater.inflate(android.R.layout.simple_expandable_list_item_1, null);
			view.setBackgroundColor(Color.LTGRAY);

			TextView textView = (TextView) view.findViewById(android.R.id.text1);
			textView.setPadding(120, 8, 0, 8); // left, top, right, bottom
			textView.setTextColor(Color.DKGRAY);
			if (groupPosition == 0) {
				textView.setText("Phone Number(s)");
			} else if (groupPosition == 1) {
				textView.setText("Email(s)");
			}
			return view;
		}


		public boolean hasStableIds() {
			return false;
		}


		public boolean isChildSelectable(int groupPosition, int childPosition) {
			return true;
		}

	}

}
