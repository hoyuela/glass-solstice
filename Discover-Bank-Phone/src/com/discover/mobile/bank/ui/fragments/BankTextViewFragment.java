package com.discover.mobile.bank.ui.fragments;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.discover.mobile.BankMenuItemLocationIndex;
import com.discover.mobile.bank.BankExtraKeys;
import com.discover.mobile.bank.R;
import com.discover.mobile.bank.ui.widgets.BankLayoutFooter;
import com.discover.mobile.common.BaseFragment;

/**
 * Class used to displayed Google's Terms 
 * of use as required by the Google Play API https://developers.google.com/maps/documentation/android/intro.
 * 
 * @author henryoyuela
 *
 */
@SuppressLint("NewApi")
public class BankTextViewFragment extends BaseFragment {
	public static final String KEY_TEXT = "text-content";
	public static final String KEY_TITLE = "text-title";
	public static final String KEY_USE_HTML = "text-html";
	public static final String SHOW_FOOTER = "show-footer";

	@Override
	public View onCreateView(final LayoutInflater inflater, final ViewGroup container,
			final Bundle savedInstanceState) {
		final View view = inflater.inflate(R.layout.bank_textview_layout, null);

		/**Set Page title of the fragment*/
		final TextView pageTitle = (TextView)view.findViewById(R.id.page_title);

		final BankLayoutFooter footer = (BankLayoutFooter) view.findViewById(R.id.bank_footer);
		
		final boolean isCard = this.getArguments().getBoolean(BankExtraKeys.CARD_MODE_KEY, true);
		if (isCard) {	
			footer.setCardMode(isCard);
		}
		
		final boolean showFooter = this.getArguments().getBoolean(SHOW_FOOTER, false);
		if( !showFooter) {
			footer.setVisibility(View.INVISIBLE);
		}

		/**Populate text view text with google's terms of use*/
		final TextView content = (TextView)view.findViewById(R.id.content_text_view);
		if(this.getArguments().containsKey(KEY_USE_HTML)){
			content.setText(Html.fromHtml(this.getArguments().getString(KEY_TEXT)));
			pageTitle.setText(Html.fromHtml(this.getArguments().getString(KEY_TITLE)));
			content.setMovementMethod(LinkMovementMethod.getInstance());
		}else{
			content.setText(this.getArguments().getString(KEY_TEXT));
			pageTitle.setText(this.getArguments().getString(KEY_TITLE));
		}

		//Disable hardware acceleration for the UI so that the dotted line gets drawn correctly.
		if(android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.HONEYCOMB) {
			view.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
		} 
				
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
