package com.discover.mobile.bank.ui.fragments;

import android.os.Bundle;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.discover.mobile.BankMenuItemLocationIndex;
import com.discover.mobile.bank.R;
import com.discover.mobile.bank.help.HelpMenuListFactory;
import com.discover.mobile.common.BaseFragment;
import com.discover.mobile.common.Globals;
import com.discover.mobile.common.help.HelpWidget;
import com.discover.mobile.common.utils.CommonUtils;

/**
 * Class used to displayed Google's Terms 
 * of use as required by the Google Play API https://developers.google.com/maps/documentation/android/intro.
 * 
 * @author henryoyuela
 *
 */
public class BankTextViewFragment extends BaseFragment {
	public static final String KEY_TEXT = "text-content";
	public static final String KEY_TITLE = "text-title";
	public static final String KEY_USE_HTML = "text-html";

	@Override
	public View onCreateView(final LayoutInflater inflater, final ViewGroup container,
			final Bundle savedInstanceState) {
		final View view = inflater.inflate(R.layout.bank_textview_layout, null);

		/**Set Page title of the fragment*/
		final TextView pageTitle = (TextView)view.findViewById(R.id.page_title);

		/**Help icon setup*/
		final HelpWidget help = (HelpWidget) view.findViewById(R.id.help);
		if( !Globals.isLoggedIn() ) {
			help.showHelpItems(HelpMenuListFactory.instance().getLoggedOutHelpItems());
		} else {
			help.showHelpItems(HelpMenuListFactory.instance().getAccountHelpItems());
		}

		/**Populate text view text with google's terms of use*/
		final TextView content = (TextView)view.findViewById(R.id.content_text_view);
		if(this.getArguments().containsKey(KEY_USE_HTML)){
			content.setText(Html.fromHtml(this.getArguments().getString(KEY_TEXT)));
			pageTitle.setText(Html.fromHtml(this.getArguments().getString(KEY_TITLE)));
		}else{
			content.setText(this.getArguments().getString(KEY_TEXT));
			pageTitle.setText(this.getArguments().getString(KEY_TITLE));
		}

		CommonUtils.fixBackgroundRepeat(view);
		return view;
	}

	@Override
	public int getActionBarTitle() {
		return R.string.bank_terms_privacy_n_terms;
	}

	@Override
	public int getGroupMenuLocation() {
		return BankMenuItemLocationIndex.PRIVACY_AND_TERMS_GROUP;
	}

	@Override
	public int getSectionMenuLocation() {
		return 0;
	}
}
