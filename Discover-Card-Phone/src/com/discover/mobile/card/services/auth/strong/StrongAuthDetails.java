package com.discover.mobile.card.services.auth.strong;

import java.io.Serializable;

import com.discover.mobile.common.Struct;

/**
 * POGO Class for Strong auth web service
 * 
 * @author CTS
 * 
 * @version 1.0
 */
@Struct
public class StrongAuthDetails implements Serializable {

    private static final long serialVersionUID = -8151822672320966293L;

    public String questionId;
    public String questionText;

}
