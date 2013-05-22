package com.discover.mobile.card.push.register;

import android.content.Context;
import android.widget.Toast;

import com.discover.mobile.card.R;
import com.discover.mobile.card.common.CardEventListener;
import com.discover.mobile.card.common.utils.Utils;
import com.discover.mobile.card.navigation.CardNavigationRootActivity;
import com.discover.mobile.card.push.manage.PushManageFragment;
import com.discover.mobile.card.services.push.GetPushData;
import com.discover.mobile.card.services.push.PostPushRegistration;
import com.discover.mobile.common.BaseFragment;
import com.discover.mobile.common.LoggedInRoboActivity;
import com.discover.mobile.common.nav.NavigationRootActivity;
import com.slidingmenu.lib.SlidingMenu;

/**
 * The base class for any piece of UI that is going to register the vendor id with Discover's server
 * @author jthornton
 *
 */
public abstract class BasePushRegistrationUI extends BaseFragment implements PushRegistrationUI{
		
	/**String representing that the user opted into the alerts*/
	public static final String ACCEPT = "Y"; //$NON-NLS-1$
	
	/**String representing that the user did not want to opt into the alerts*/
	public static final String DECLINE = "P"; //$NON-NLS-1$
	public final String LOG_TAG = BasePushRegistrationUI.class.getSimpleName();

	/**
	 * Registers the device, user and vender id with Discover's server
	 * @param regStatus - string representing if the user accepted the terms.
	 * @throws Exception 
	 */
	protected void registerWithDiscover(final String regStatus, final String venderId) throws Exception{
		final Context context = this.getActivity();
		
		PostPushRegistration postPushRegistration = new PostPushRegistration(context, new CardEventListener()
		{
			
			@Override
			public void onSuccess(Object data)
			{
				GetPushData data2 = (GetPushData) data;
				Utils.log(LOG_TAG, "--Response Data -- "+data2.resultCode);
				setStatus(regStatus);
			}
			
			@Override
			public void OnError(Object data)
			{
				Toast.makeText(getActivity(), data.toString(), Toast.LENGTH_LONG).show();
			}
		});
		
		postPushRegistration.sendRequest(venderId, regStatus);
	}
	
	/**
	 * Swap out this fragment and replace it with the push manage fragment so that the user can manage his/her alerts
	 */
	
	public void setStatus(String regStatus)
	{
		((NavigationRootActivity)this.getActivity()).getSlidingMenu().setTouchModeAbove(SlidingMenu.TOUCHMODE_FULLSCREEN);
		((LoggedInRoboActivity)this.getActivity()).enableMenuButton();
		if(regStatus.equalsIgnoreCase(ACCEPT))
		{
			((CardNavigationRootActivity)getActivity()).sendNavigationTextToPhoneGapInterface(getString(R.string.sub_section_title_manage_alerts));
		}
		else
		{
			getActivity().onBackPressed();
		}
	}
	public void changeToAcceptScreen(final String tag) {
		this.makeFragmentVisible(new PushManageFragment());
	}
	
}
