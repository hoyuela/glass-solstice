package com.discover.mobile.card.services.auth.strong;

import java.io.Serializable;
import java.util.List;

import com.discover.mobile.common.Struct;

import com.fasterxml.jackson.annotation.JacksonInject;
import com.fasterxml.jackson.annotation.JsonProperty;

public class StrongAuthCreateUserDetails implements Serializable{

    /**
     * 
     */
    private static final long serialVersionUID = 8337407865219906281L;
    
    public String strongAuthStatus ;  
    @JsonProperty("saQuestion1")
    public StrongAuthItem1 saQuestion1 ;
    @JsonProperty("saQuestion2")
    public StrongAuthItem2 saQuestion2 ;
    @JsonProperty("saQuestion3")
    public StrongAuthItem3 saQuestion3 ;
    
    
    @Struct
    public static class StrongAuthItem1
    {
        @JsonProperty("Q1.1")
        public String Q11 ;
        @JsonProperty("Q1.2")
        public String Q12 ;
        @JsonProperty("Q1.3")
        public String Q13 ;
        @JsonProperty("Q1.4")
        public String Q14 ;
        @JsonProperty("Q1.5")
        public String Q15 ;
        
        @JsonProperty("Q1.6")
        public String Q16 ;
        @JsonProperty("Q1.7")
        public String Q17 ;
        @JsonProperty("Q1.8")
        public String Q18 ;
        @JsonProperty("Q1.9")
        public String Q19 ;
        @JsonProperty("Q1.10")
        public String Q110 ;
    }

    @Struct
    public static class StrongAuthItem2
    {
        @JsonProperty("Q2.1")
        public String Q21 ;
        @JsonProperty("Q2.2")
        public String Q22 ;
        @JsonProperty("Q2.3")
        public String Q23 ;
        @JsonProperty("Q2.4")
        public String Q24 ;
        @JsonProperty("Q2.5")
        public String Q25 ;
        
        @JsonProperty("Q2.6")
        public String Q26 ;
        @JsonProperty("Q2.7")
        public String Q27 ;
        @JsonProperty("Q2.8")
        public String Q28 ;
        @JsonProperty("Q2.9")
        public String Q29 ;
        @JsonProperty("Q2.10")
        public String Q210 ;
    }
    @Struct
    public static class StrongAuthItem3
    {   
        @JsonProperty("Q3.1")
        public String Q31 ;
        @JsonProperty("Q3.2")
        public String Q32 ;
        @JsonProperty("Q3.3")
        public String Q33 ;
        @JsonProperty("Q3.4")
        public String Q34 ;
        @JsonProperty("Q3.5")
        public String Q35 ;
        
        @JsonProperty("Q3.6")
        public String Q36 ;
        @JsonProperty("Q3.7")
        public String Q37 ;
        @JsonProperty("Q3.8")
        public String Q38 ;
        @JsonProperty("Q3.9")
        public String Q39 ;
        @JsonProperty("Q3.10")
        public String Q310 ;
    }

}
