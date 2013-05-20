package com.discover.mobile.card.phonegap.plugins;

import java.util.ArrayList;

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

import com.discover.mobile.card.R;

public class ContactViewResult extends ExpandableListActivity {

    static final String TAG = "ContactViewResult";

    String name = null;
    ArrayList<CharSequence> phones = null;
    ArrayList<CharSequence> emails = null;

    ProfileInfoAdapter adapter = null;

    static final int PHONE = 0;
    static final int EMAIL = 1;

    TextView textview_name;

    // ImageView imageView;

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.contactviewresult);
        textview_name = (TextView) findViewById(R.id.contactviewresult_name);
        name = this.getIntent().getStringExtra("name");
        if (name != null) {
            textview_name.setText(name);
        }

        final String contactData = this.getIntent().getStringExtra(
                "contact_uri");
        Log.v(TAG, "contactData: " + contactData);

        phones = this.getIntent().getCharSequenceArrayListExtra("phone");
        emails = this.getIntent().getCharSequenceArrayListExtra("email");
        adapter = new ProfileInfoAdapter(this);
        setListAdapter(adapter);

        getExpandableListView().expandGroup(0);
        getExpandableListView().expandGroup(1);

    }

    @Override
    public boolean onChildClick(final ExpandableListView parent, final View v,
            final int groupPosition, final int childPosition, final long id) {
        final Intent data = new Intent();
        switch (groupPosition) {
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

    @Override
    public void onBackPressed() {
        this.setResult(Activity.RESULT_CANCELED);
        finish();
    }

    class ProfileInfoAdapter extends BaseExpandableListAdapter {

        Context context;
        LayoutInflater inflater;

        public ProfileInfoAdapter(final Context context) {
            inflater = LayoutInflater.from(context);
            this.context = context;
        }

        @Override
        public Object getChild(final int arg0, final int arg1) {
            return null;
        }

        @Override
        public long getChildId(final int groupPosition, final int childPosition) {
            return 0;
        }

        @Override
        public View getChildView(final int groupPosition,
                final int childPosition, final boolean isLastChild,
                final View convertView, final ViewGroup parent) {
            final View view = inflater.inflate(
                    R.layout.contactviewresult_childcell, null);
            final TextView textView = (TextView) view
                    .findViewById(R.id.contactviewresult_childcell_text);
            if (groupPosition == 0) {
                textView.setText(phones.get(childPosition));
            } else if (groupPosition == 1) {
                textView.setText(emails.get(childPosition));
            }
            return view;
        }

        @Override
        public int getChildrenCount(final int groupPosition) {
            if (groupPosition == 0) {
                return phones.size();
            } else if (groupPosition == 1) {
                return emails.size();
            }
            return 0;
        }

        @Override
        public Object getGroup(final int groupPosition) {
            return null;
        }

        @Override
        public int getGroupCount() {
            return 2;
        }

        @Override
        public long getGroupId(final int groupPosition) {
            return groupPosition;
        }

        @Override
        public View getGroupView(final int groupPosition,
                final boolean isExpanded, final View convertView,
                final ViewGroup parent) {
            final View view = inflater.inflate(
                    android.R.layout.simple_expandable_list_item_1, null);
            view.setBackgroundColor(Color.LTGRAY);

            final TextView textView = (TextView) view
                    .findViewById(android.R.id.text1);
            textView.setPadding(120, 8, 0, 8); // left, top, right, bottom
            textView.setTextColor(Color.DKGRAY);
            if (groupPosition == 0) {
                textView.setText("Phone Number(s)");
            } else if (groupPosition == 1) {
                textView.setText("Email(s)");
            }
            return view;
        }

        @Override
        public boolean hasStableIds() {
            return false;
        }

        @Override
        public boolean isChildSelectable(final int groupPosition,
                final int childPosition) {
            return true;
        }

    }

}
