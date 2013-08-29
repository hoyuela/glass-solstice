package com.discover.mobile.card.account;

import java.util.ArrayList;

import android.view.View;
import android.view.View.OnClickListener;

import com.discover.mobile.card.R;
import com.discover.mobile.card.common.utils.Utils;
import com.discover.mobile.common.nav.section.ClickComponentInfo;
import com.discover.mobile.common.nav.section.ComponentInfo;
import com.discover.mobile.common.nav.section.GroupComponentInfo;

/**
 * AccountSectionInfo adds Section & sub section of "Accounts" LHS Menu
 * 
 * extends {@link GroupComponentInfo}
 * 
 * @author CTS
 * 
 * @version 1.0
 * 
 */
public final class AccountSectionInfo extends GroupComponentInfo {

	 /*    13.5  Changes */

    public static ArrayList<ComponentInfo> componentInfo = new ArrayList<ComponentInfo>();
    public  static ComponentInfo componentInfoArray[] ;
    
    public AccountSectionInfo() {
        super(R.string.section_title_account,componentInfoArray  );
        componentInfoArray=null;
    }
    
    public static void setComponentArray(final ArrayList<ComponentInfo> clickComponentInfos){
        componentInfoArray = new ComponentInfo[clickComponentInfos.size()];
        int i = 0 ;
        for(ComponentInfo component : clickComponentInfos){
            componentInfoArray[i]=component ;
            i++;
        }
    }
    
    
    public  static void setComponentList(){
        
        if(Utils.isOptionAvailable(R.string.sub_section_title_account_summary)){
            componentInfo.add(new ClickComponentInfo(R.string.sub_section_title_account_summary,
                    false, new View.OnClickListener() {
                @Override
                public void onClick(final View v) {

                }
            }));
        }
        
        if(Utils.isOptionAvailable(R.string.sub_section_title_recent_activity)){
            componentInfo.add(new ClickComponentInfo(
                    R.string.sub_section_title_recent_activity, false,
                    new View.OnClickListener() {
                        @Override
                        public void onClick(final View v) {

                        }
                    }));
        }
        if(Utils.isOptionAvailable(R.string.sub_section_title_search_transaction)){
            componentInfo.add(new ClickComponentInfo(
                    R.string.sub_section_title_search_transaction, false,
                    new View.OnClickListener() {
                        @Override
                        public void onClick(final View v) {

                        }
                    }) );
        }
        if(Utils.isOptionAvailable(R.string.sub_section_title_statements)){
            componentInfo.add( new ClickComponentInfo(
                    R.string.sub_section_title_statements, false,
                    new View.OnClickListener() {
                        @Override
                        public void onClick(final View v) {

                        }
                    }));
        }
      
        setComponentArray(componentInfo);
        componentInfo.clear();
       
    }
  
    
    
    
    /* 13.3 Changes start */
    // Change Left Nav in case of Corporate card
/*    public AccountSectionInfo(final Boolean corporateCard) {
        super(R.string.section_title_account,

        new ClickComponentInfo(R.string.sub_section_title_account_summary,
                false, new View.OnClickListener() {
                    @Override
                    public void onClick(final View v) {

                    }
                }), new ClickComponentInfo(
                R.string.sub_section_title_recent_activity, false,
                new View.OnClickListener() {
                    @Override
                    public void onClick(final View v) {

                    }
                }), new ClickComponentInfo(
                R.string.sub_section_title_search_transaction, false,
                new View.OnClickListener() {
                    @Override
                    public void onClick(final View v) {

                    }
                }));
    }*/

    /* 13.3 Changes end */

    
    /*    13.5  Changes */

    class AccountComponentInfo extends ClickComponentInfo {

        public AccountComponentInfo(final int titleResource,
                final OnClickListener listener) {
            super(titleResource, false, listener);
        }

    }

}
