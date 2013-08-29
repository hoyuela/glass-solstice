package com.discover.mobile.card.profile;

import java.util.ArrayList;

import android.view.View;
import android.view.View.OnClickListener;

import com.discover.mobile.card.R;
import com.discover.mobile.card.common.utils.Utils;
import com.discover.mobile.card.passcode.PasscodeLandingFragment;
import com.discover.mobile.card.profile.quickview.QuickViewSetupFragment;
import com.discover.mobile.common.nav.section.ClickComponentInfo;
import com.discover.mobile.common.nav.section.ComponentInfo;
import com.discover.mobile.common.nav.section.FragmentComponentInfo;
import com.discover.mobile.common.nav.section.GroupComponentInfo;

/**
 * This class contains the subsections under the profile and settings menu in
 * the sliding nav menu.
 * 
 * @author jthornton
 * 
 */
public class ProfileAndSettingsSectionInfo extends GroupComponentInfo {

 /*    13.5  Changes */
    
    public static ArrayList<ComponentInfo> componentInfo = new ArrayList<ComponentInfo>();
    public  static ComponentInfo componentInfoArray[] ;
    
    
    public static void setComponentArray(final ArrayList<ComponentInfo> clickComponentInfos){
        componentInfoArray = new ComponentInfo[clickComponentInfos.size()];
        int i = 0 ;
        for(ComponentInfo component : clickComponentInfos){
            componentInfoArray[i]=component ;
            i++;
        }
    }
    
    
    public  static void setComponentList(){
        
        if(Utils.isOptionAvailable(R.string.sub_section_title_passcode)){
            componentInfo.add( new FragmentComponentInfo(R.string.sub_section_title_passcode,
                    PasscodeLandingFragment.class));
        }
        
        if(Utils.isOptionAvailable(R.string.sub_section_title_fast_view)){
            componentInfo.add( new FragmentComponentInfo(R.string.sub_section_title_fast_view,
                    QuickViewSetupFragment.class));
        }
        if(Utils.isOptionAvailable(R.string.sub_section_title_manage_alerts)){
            componentInfo.add( new ClickComponentInfo(
                    R.string.sub_section_title_manage_alerts, false,
                    new View.OnClickListener() {
                        @Override
                        public void onClick(final View v) {

                        }
                    }));
        }
        if(Utils.isOptionAvailable(R.string.sub_section_title_alert_history)){
            componentInfo.add( new ClickComponentInfo(
                    R.string.sub_section_title_alert_history, false,
                    new View.OnClickListener() {
                        @Override
                        public void onClick(final View v) {

                        }
                    }));
        }
        if(Utils.isOptionAvailable(R.string.sub_section_title_create_cash_pin)){
            componentInfo.add(   new ClickComponentInfo(
                    R.string.sub_section_title_create_cash_pin, false,
                    new View.OnClickListener() {
                        @Override
                        public void onClick(final View v) {

                        }
                    }));
        }
      
        setComponentArray(componentInfo);
        componentInfo.clear();
       
    }
   
    /**
     * Construct the sub menu and add all the subsections under the profile and
     * settings tip level item
     */
    public ProfileAndSettingsSectionInfo() {
        super(R.string.section_title_profile_and_settings, componentInfoArray);
        componentInfoArray = null ;

    }
    
    /*    13.5  Changes */

    class ProfileAndSettingsComponentInfo extends ClickComponentInfo {

        public ProfileAndSettingsComponentInfo(final int titleResource,
                final OnClickListener listener) {
            super(titleResource, false, listener);
        }

    }

}
