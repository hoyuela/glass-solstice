package com.discover.mobile.card.payments;

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
 * This class contains the subsections under the Payments menu in the sliding
 * nav menu.
 * 
 * @author CTS
 * 
 * @version 1.0
 */
public class PaymentsSectionInfo extends GroupComponentInfo {

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
    
    
    public  static void setComponentList(final Boolean cardTypeDBC){
        
        if(Utils.isOptionAvailable(R.string.sub_section_title_make_a_payment)){
            componentInfo.add( new ClickComponentInfo(R.string.sub_section_title_make_a_payment,
                    false, new View.OnClickListener() {
                @Override
                public void onClick(final View v) {

                }
            }));
        }
        
        if(Utils.isOptionAvailable(R.string.sub_section_title_manage_payments)){
            componentInfo.add(   new ClickComponentInfo(R.string.sub_section_title_manage_payments,
                    false, new View.OnClickListener() {
                @Override
                public void onClick(final View v) {

                }
            }));
        }
        if(Utils.isOptionAvailable(R.string.sub_section_title_manage_bank_accounts)){
            componentInfo.add( new ClickComponentInfo(R.string.sub_section_title_manage_bank_accounts,
                    false, new View.OnClickListener() {
                @Override
                public void onClick(final View v) {

                }
            }));
        }
        if(Utils.isOptionAvailable(R.string.sub_section_title_send_money) && (cardTypeDBC==false)){
            componentInfo.add( new ClickComponentInfo(R.string.sub_section_title_send_money, false,
                    new View.OnClickListener() {
                @Override
                public void onClick(final View v) {

                }
            }));
        }
        if(Utils.isOptionAvailable(R.string.sub_section_title_send_money_history ) && (cardTypeDBC==false) ){
            componentInfo.add(  new ClickComponentInfo(R.string.sub_section_title_send_money_history,
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
     * 
     */
    public PaymentsSectionInfo() {
        super(R.string.section_title_payments, componentInfoArray);
        componentInfoArray = null ;
    }

    /*    13.5  Changes */
    class PaymentsComponentInfo extends ClickComponentInfo {

        public PaymentsComponentInfo(final int titleResource,
                final OnClickListener listener) {
            super(titleResource, false, listener);
        }

    }
}
