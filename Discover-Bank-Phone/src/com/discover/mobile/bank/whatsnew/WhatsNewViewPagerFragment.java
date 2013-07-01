package com.discover.mobile.bank.whatsnew;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.discover.mobile.bank.R;

/**
 * Fragment used to display a single what's new page. This will take an image
 * that is passed in via the arguments and display it in the fragment.
 * 
 * @author jthornton
 *
 */
public class WhatsNewViewPagerFragment extends Fragment{

	/**Key to get the drawable out of the arguments bundle*/
	public static final String DRAWABLE = "drawable";

	/**
	 * Create the view and inflate the layout
	 * @param inflater - inflater used to inflate the view
	 * @param container - container containing the fragment
	 * @param savedInstanceState - saved state of the fragment 
	 */
	@Override
	public View onCreateView (final LayoutInflater inflater, final ViewGroup container,
			final Bundle savedInstanceState) {
		final ViewGroup view = (ViewGroup) inflater.inflate(R.layout.bank_whats_new_view_pager, container, false);
		final ImageView image = (ImageView) view.findViewById(R.id.whats_new_image);

		final Bundle bundle = getArguments();
		image.setImageResource(bundle.getInt(DRAWABLE));

		return view;
	}
}