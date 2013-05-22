package com.discover.mobile.card.common.net.error;

import java.io.Serializable;
import java.util.List;

import com.discover.mobile.common.Struct;

/**
 * 
 * �2013 Discover Bank
 * 
 * holding error response parse data
 * 
 * @author CTS
 * 
 * @version 1.0
 */

@Struct
public class CardErrorResponse implements Serializable {

    private static final long serialVersionUID = 6845281116644082207L;

    public String status;
    public String message;

    public List<Data> data;

    @Struct
    public static class Data implements Serializable{

        /**
         * 
         */
        private static final long serialVersionUID = 2892504726702613380L;
        public String status;
        public String saStatus;
        public String userid;
        public String questionId;
        public String questionText;
        public boolean isSSOUidDLinkable;
        public boolean isSSOUser;
        public boolean isSSNMatched;
    }

}
