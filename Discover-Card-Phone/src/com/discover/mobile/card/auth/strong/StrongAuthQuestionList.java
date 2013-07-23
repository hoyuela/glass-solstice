package com.discover.mobile.card.auth.strong;

import java.util.List;

import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.WindowManager.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckedTextView;
import android.widget.ListView;
import android.widget.TextView;

import com.discover.mobile.card.R;
import com.discover.mobile.card.common.utils.Utils;
import com.discover.mobile.card.privacyterms.PrivacyTermsLanding;

public class StrongAuthQuestionList extends ListActivity implements
        OnClickListener {

    private int lastselectedQuestion1;
    private Button logout;
    private TextView privacyTerms, provideFeedback;
    private Boolean isQuestionSelected = false;
    private Intent strongAuthEnterInfoActivity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.securityquestionlist);
        /*
         * if (android.os.Build.VERSION.SDK_INT >= 11) { ActionBar actionBar =
         * getActionBar(); actionBar.hide(); }
         */

        final ListView listView = getListView();
        logout = (Button) findViewById(R.id.logout_button);
        privacyTerms = (TextView) findViewById(R.id.privacy_terms);
        provideFeedback = (TextView) findViewById(R.id.provide_feedback_button);
        handlingClickEvents();

        strongAuthEnterInfoActivity = getIntent();
        int questionGroup = getIntent().getIntExtra("Questiongroup", 1);
        final List<String> questions = getIntent().getStringArrayListExtra(
                "Questions");
        final List<String> questionsId = getIntent().getStringArrayListExtra(
                "QuestionsId");
        lastselectedQuestion1 = getIntent().getIntExtra("lastSelectedQuestion",
                0);

        setListAdapter(new SecurityQuestions1SpinnerAdapter(this, questions,
                questionsId));

        listView.setTextFilterEnabled(true);
        listView.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(final AdapterView<?> parent,
                    final View view, final int position, final long id) {
                isQuestionSelected = true;
                /*
                 * View currentClickedView = listView.getChildAt(position);
                 * CheckedTextView currentClickedCheckView = (CheckedTextView)
                 * currentClickedView.findViewById(R.id.checkedTextView1);
                 * currentClickedCheckView.setChecked(true);
                 * 
                 * if(lastselectedQuestion1!=position) { View
                 * previousClickedView =
                 * listView.getChildAt(lastselectedQuestion1); CheckedTextView
                 * previousClickedCheckView = (CheckedTextView)
                 * previousClickedView.findViewById(R.id.checkedTextView1);
                 * previousClickedCheckView.setChecked(false); }
                 */

                /*
                 * Handler handler = new Handler(); handler.postDelayed(new
                 * Runnable() { public void run() {
                 * strongAuthEnterInfoActivity.putExtra("selectedQuestion",
                 * questions.get(position));
                 * strongAuthEnterInfoActivity.putExtra("selectedQuestionId",
                 * questionsId.get(position));
                 * strongAuthEnterInfoActivity.putExtra("lastSelectedPosition",
                 * position); setResult(RESULT_OK, strongAuthEnterInfoActivity);
                 * finish(); } }, 5000);
                 */

                strongAuthEnterInfoActivity.putExtra("selectedQuestion",
                        questions.get(position));
                strongAuthEnterInfoActivity.putExtra("selectedQuestionId",
                        questionsId.get(position));
                strongAuthEnterInfoActivity.putExtra("lastSelectedPosition",
                        position);
                setResult(RESULT_OK, strongAuthEnterInfoActivity);
                finish();

            }
        });

    }

    @Override
    protected void onResume() {
        // TODO Auto-generated method stub
        super.onResume();
        StrongAuthQuestionList.this.getWindow().setSoftInputMode(
                LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
    }

    private void handlingClickEvents() {
        // TODO Auto-generated method stub
        logout.setOnClickListener(this);
        privacyTerms.setOnClickListener(this);
        provideFeedback.setOnClickListener(this);
    }

    @Override
    public void onBackPressed() {
        // TODO Auto-generated method stub
        super.onBackPressed();
        if (!isQuestionSelected) {
            setResult(RESULT_CANCELED);
            finish();
        }
        isQuestionSelected = false;
    }

    @Override
    public void onClick(View v) {
        // TODO Auto-generated method stub
        if (v.getId() == R.id.privacy_terms) {
            // FacadeFactory.getBankFacade().navToCardPrivacyTerms();
            Intent privacyTerms = new Intent(StrongAuthQuestionList.this,
                    PrivacyTermsLanding.class);
            startActivity(privacyTerms);
        } else if (v.getId() == R.id.provide_feedback_button) {
            Utils.createProvideFeedbackDialog(this, "strongAuthEnroll-pg");
        } else if (v.getId() == R.id.logout_button) {

            // Changes for 13.4 start
            Utils.logoutUser(this, false);

        }
    }

    class SecurityQuestions1SpinnerAdapter extends ArrayAdapter<String> {

        ViewHolder holder;
        ViewHolder viewHolder;
        Context context;
        List<String> idList;

        class ViewHolder {
            public CheckedTextView securityQuestionRadio;
        }

        public SecurityQuestions1SpinnerAdapter(Context context,
                List<String> list, List<String> idList) {
            super(context, android.R.layout.select_dialog_item, list);
            this.context = context;
            this.idList = idList;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            if (convertView == null) {
                LayoutInflater inflater = (LayoutInflater) context
                        .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = inflater.inflate(R.layout.questioncustomview,
                        null);

                viewHolder = new ViewHolder();
                viewHolder.securityQuestionRadio = (CheckedTextView) convertView
                        .findViewById(R.id.checkedTextView1);
                convertView.setTag(viewHolder);
            }

            holder = (ViewHolder) convertView.getTag();
            holder.securityQuestionRadio.setText(getItem(position).toString());
            viewHolder.securityQuestionRadio.setTextSize(14);
            if (lastselectedQuestion1 == position) {
                holder.securityQuestionRadio.setChecked(true);
            } else {
                holder.securityQuestionRadio.setChecked(false);
            }
            return convertView;
        }
    }

}
