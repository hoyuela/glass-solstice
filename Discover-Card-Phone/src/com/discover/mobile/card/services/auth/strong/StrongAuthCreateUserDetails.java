package com.discover.mobile.card.services.auth.strong;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;

/**
 * @author 228218
 *
 */
public class StrongAuthCreateUserDetails implements Serializable {

    /**
     * 
     */
    private static final long serialVersionUID = 8337407865219906281L;
    
  /*  13.4 Code CleanUp Chnages Start*/

    public String strongAuthStatus;

    @JsonProperty("saQuestion1")
    public JsonNode saQuestion1;

    @JsonProperty("saQuestion2")
    public JsonNode saQuestion2;

    @JsonProperty("saQuestion3")
    public JsonNode saQuestion3;

    /*  13.4 Code CleanUp Chnages End*/
}
