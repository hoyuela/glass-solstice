package com.discover.mobile.card.passcode.menu;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.discover.mobile.card.CardMenuItemLocationIndex;
import com.discover.mobile.card.R;
import com.discover.mobile.card.auth.strong.StrongAuthDefaultResponseHandler;
import com.discover.mobile.card.auth.strong.StrongAuthHandler;
import com.discover.mobile.card.common.CardEventListener;
import com.discover.mobile.card.common.ui.modals.EnhancedContentModal;
import com.discover.mobile.card.common.ui.modals.EnhancedTwoButtonModal;
import com.discover.mobile.card.common.utils.Utils;
import com.discover.mobile.card.home.HomeSummaryFragment;
import com.discover.mobile.card.passcode.request.DeletePasscodeRequest;
import com.discover.mobile.card.passcode.update.PasscodeUpdateStep1Fragment;
import com.discover.mobile.common.BaseFragment;
import com.discover.mobile.common.DiscoverActivityManager;
import com.discover.mobile.common.analytics.AnalyticsPage;
import com.discover.mobile.common.analytics.TrackingHelper;
import com.discover.mobile.common.nav.NavigationRootActivity;
import com.discover.mobile.common.utils.PasscodeUtils;

public class PasscodeMenuFragment extends BaseFragment {
	static final String TRACKING_PAGE_NAME = "PasscodeMenu";
	private static String TAG = "PasscodeMenuFragment";
	
	@Override
	public int getActionBarTitle() {
        return R.string.sub_section_title_passcode;
	}

	protected class NavigateACHomeAction implements Runnable {
		public NavigateACHomeAction() {}
		@Override
		public void run() {
			makeFragmentVisible(new HomeSummaryFragment());
		}
	}
	
	protected class DisableCompleteAction implements Runnable {
		public DisableCompleteAction() {}
		@Override
		public void run() {
			PasscodeUtils pUtils = new PasscodeUtils(getActivity().getApplicationContext());
			new DeletePasscodeRequest(getActivity(), pUtils.getPasscodeToken()).loadDataFromNetwork(new DisableRequestListener());
		}
	}
	
	@Override
	public int getGroupMenuLocation() {
		return CardMenuItemLocationIndex.PROFILE_AND_SETTINGS_GROUP;
	}

	@Override
	public int getSectionMenuLocation() {
		return CardMenuItemLocationIndex.PASSCODE_SECTION;
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (!this.isStopping) {
			TrackingHelper.trackPageView(AnalyticsPage.PASSCODE_MENU);
		}
	}

	@Override
	public View onCreateView(final LayoutInflater inflater,
			final ViewGroup container, final Bundle savedInstanceState) {
		final View view = inflater.inflate(R.layout.passcode_menu,
				null);
	
		final ListView listview = (ListView) view.findViewById(R.id.listview);
		String[] values = new String[] { "Change Passcode", "Disable Passcode" };
		final ArrayList<String> list = new ArrayList<String>();
		for (int i = 0; i < values.length; ++i) {
		      list.add(values[i]);
		    }
		final StableArrayAdapter adapter = new StableArrayAdapter(getActivity(),
				R.layout.passcode_menu_item, list);
		listview.setAdapter(adapter);
		
		listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {

		      @Override
		      public void onItemClick(AdapterView<?> parent, final View view,
		          int position, long id) {
		    	  if (id == 0) {
		    		  final StrongAuthHandler authHandler = new StrongAuthHandler(
		    				  PasscodeMenuFragment.this.getActivity(),
		    				  new UpdatePasscodeStrongAuthFlow(), false);
		    		  authHandler.strongAuth();
		    	  } else if (id == 1) {
		    		  final Context context = DiscoverActivityManager.getActiveActivity();
		    		  final EnhancedTwoButtonModal modal = new EnhancedTwoButtonModal(context, 
		    				  R.string.passcode_dialog_disable_are_you_sure_title, 
		    				  R.string.passcode_dialog_disable_are_you_sure_content, 
		    				  R.string.yes,
		    				  R.string.no,
		    				  new DisableCompleteAction(), null);
		    		  modal.hideNeedHelpFooter();
		    		  ((NavigationRootActivity)context).showCustomAlert(modal);
		    	  }
		      }
		    });

		Utils.setFooter(view, getActivity());
		return view;
	}
	
	private final class UpdatePasscodeStrongAuthFlow extends StrongAuthDefaultResponseHandler {
		@Override
		public void onStrongAuthSucess(Object data) {
			makeFragmentVisible(new PasscodeUpdateStep1Fragment());
		}
	}
	
	private class StableArrayAdapter extends ArrayAdapter<String> {
	    HashMap<String, Integer> mIdMap = new HashMap<String, Integer>();

	    public StableArrayAdapter(Context context, int textViewResourceId,
	        List<String> objects) {
	    	super(context, R.layout.passcode_menu_item, R.id.passcode_menu_item_tv, objects);

	      for (int i = 0; i < objects.size(); ++i) {
	        mIdMap.put(objects.get(i), i);
	      }
	    }

	    @Override
	    public long getItemId(int position) {
	      String item = getItem(position);
	      return mIdMap.get(item);
	    }

	    @Override
	    public boolean hasStableIds() {
	      return true;
	    }
	  }
	
	private final class DisableRequestListener implements CardEventListener {
		@Override
		public void OnError(Object data) {
//			passcodeResponse(false);
			//TODO error verbage
		}

		@Override
		public void onSuccess(Object data) {
			PasscodeUtils pUtils = new PasscodeUtils(getActivity().getApplicationContext());
			pUtils.deletePasscodeToken();
			final Activity activeActivity = DiscoverActivityManager.getActiveActivity();
			TrackingHelper.trackPageView(AnalyticsPage.PASSCODE_DISABLE_OVERLAY);
			final EnhancedContentModal modal = new EnhancedContentModal(activeActivity, 
					R.string.passcode_dialog_disabled_title, 
					R.string.passcode_dialog_disabled_content, 
					R.string.home_text,
					new NavigateACHomeAction(),
					new NavigateACHomeAction());
			modal.hideNeedHelpFooter();
			((NavigationRootActivity)activeActivity).showCustomAlert(modal);
		}
	};
	
	private boolean isStopping = false;
	public void onStop() {
		super.onStop();
		this.isStopping = true;
	}
	
	public void onResume() {
		super.onResume();
		this.isStopping = false;
	}
}