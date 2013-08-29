package com.discover.mobile.card.miles;

import java.util.ArrayList;

import android.view.View;
import android.view.View.OnClickListener;

import com.discover.mobile.card.R;
import com.discover.mobile.card.common.utils.Utils;
import com.discover.mobile.common.nav.section.ClickComponentInfo;
import com.discover.mobile.common.nav.section.ComponentInfo;
import com.discover.mobile.common.nav.section.GroupComponentInfo;

/**
 * ©2013 Discover Bank
 * 
 ** This class contains the subsections under the Payments menu in the sliding
 * nav menu.
 * 
 * @author CTS
 * 
 * @version 1.0
 */
public class MilesInfo extends GroupComponentInfo {

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
        
        if(Utils.isOptionAvailable(R.string.sub_section_title_sign_up_for_miles)){
            componentInfo.add(new ClickComponentInfo(R.string.sub_section_title_sign_up_for_miles,
                    false, new View.OnClickListener() {
                @Override
                public void onClick(final View v) {

                }
            }));
        }
        
        if(Utils.isOptionAvailable(R.string.sub_section_title_miles_extras)){
            componentInfo.add(    new ClickComponentInfo(R.string.sub_section_title_miles_extras, false,
                    new View.OnClickListener() {
                @Override
                public void onClick(final View v) {

                }
            }));
        }
        if(Utils.isOptionAvailable(R.string.sub_section_title_miles_refer_a_friend)){
            componentInfo.add(  new ClickComponentInfo(R.string.sub_section_title_miles_refer_a_friend,
                    false, new View.OnClickListener() {
                @Override
                public void onClick(final View v) {

                }
            }));
        }
        
      
        setComponentArray(componentInfo);
        componentInfo.clear();
       
    }
    /**
     * Constructor
     */
    public MilesInfo() {
        super(R.string.section_title_miles, componentInfoArray );
        componentInfoArray = null ;
    }
    
    /*    13.5  Changes */
    class MilesComponentInfo extends ClickComponentInfo {

        public MilesComponentInfo(final int titleResource,
                final OnClickListener listener) {
            super(titleResource, false, listener);
        }

    }
}
