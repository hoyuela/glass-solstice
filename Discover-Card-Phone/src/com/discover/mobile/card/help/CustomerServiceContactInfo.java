package com.discover.mobile.card.help;

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
 * This class contains the subsections under the CustomerService menu in the
 * sliding nav menu.
 * 
 * @author CTS
 * 
 * @version 1.0
 */
public class CustomerServiceContactInfo extends GroupComponentInfo {

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
        
        if(Utils.isOptionAvailable(R.string.sub_section_title_contact_us)){
            componentInfo.add(new ClickComponentInfo(R.string.sub_section_title_contact_us, false,
                    new View.OnClickListener() {
                @Override
                public void onClick(final View v) {

                }
            }));
        }
        
        if(Utils.isOptionAvailable(R.string.sub_section_title_faq_hidearray)){
            componentInfo.add( new ClickComponentInfo(R.string.sub_section_title_faq, false,
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
     * Constructor
     */
    public CustomerServiceContactInfo() {
        super(R.string.section_title_customer_service,  componentInfoArray);
        componentInfoArray = null ;
    }
    
    /*    13.5  Changes */


    class CustomerServiceContactComponentInfo extends ClickComponentInfo {

        public CustomerServiceContactComponentInfo(final int titleResource,
                final OnClickListener listener) {
            super(titleResource, false, listener);
        }
    }

}
