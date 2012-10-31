namespace("dfs.crd.reg");
dfs.crd.reg.shared = {};
dfs.crd.reg.shared.constant = {};
dfs.crd.reg.shared.util = {};
dfs.crd.reg.shared.http = {};

/********  Registration Specific Constants *********/
dfs.crd.reg.shared.constant.url = {
    //EXTERNAL URLS
    FORGOT_UID : RESTURL + "reg/v1/user/id",
    FORGOT_PASSWORD_AUTH : RESTURL + "reg/v1/user/pwd/auth",
    FORGOT_PASSWORD : RESTURL + "reg/v1/user/pwd",
    FORGOT_BOTH_AUTH : RESTURL + "reg/v1/user/reg/auth",
    FORGOT_BOTH : RESTURL + "reg/v1/user/reg",
    SA_CHECK : RESTURL + "reg/v1/user/sa/check", 
    //ERROR URLS
    ERROR_TXT : BASEURL + "/json/registration/error.json",       
    //INTERNAL URLS
    HOME : "../achome/cardHome",
    FORGOT_BOTH_CONFIRMATION : "forgotBothConfirmation",
    FORGOT_PASSWORD_CONFIRMATION : "forgotPasswordConfirmation",
    FORGOT_UID_CONFIRMATION : "forgotUserIdConfirmation",
    FORGOT_PASSWORD_STEP2 : "forgotPasswordStep2",
    FORGOT_BOTH_STEP2 : "forgotBothStep2"
};

dfs.crd.reg.shared.constant.cache = {
    //cache for forgot uid confirmation to load (contains forgot uid response data)
    FORGOT_UID_CONFIRMATION_RESP : "FORGOT_UID_CONFIRMATION_RESP",
    //cache for forgot password confirmation to load (contains forgot password step 2 response data)
    FORGOT_PASSWORD_CONFIRMATION_RESP : "FORGOT_PASSWORD_CONFIRMATION_RESP",
    //cache for forgot both confirmation to load (contains forgot both step 2 response data)
    FORGOT_BOTH_CONFIRMATION_RESP : "FORGOT_BOTH_CONFIRMATION_RESP",
    //cache for forgot password step2 to load (contains forgot pass step 1 request data)
    FORGOT_PASSWORD_STEP1_REQ : "FORGOT_PASSWORD_STEP1_REQ",
    //cache for forgot both step2 to load (contains forgot both step 1 request data)
    FORGOT_BOTH_STEP1_REQ : "FORGOT_BOTH_STEP1_REQ",
    FORGOT_PASSWORD_STEP2_REQ : "FORGOT_PASSWORD_STEP2_REQ",
    FORGOT_BOTH_STEP2_REQ : "FORGOT_BOTH_STEP2_REQ",
    FORGOT_BOTH_TITLE : "FORGOT_BOTH_TITLE",
    ERROR_TEXT : "ERROR_TEXT"
};

dfs.crd.reg.shared.constant.cache.removeAll = function() {
	for (var key in dfs.crd.reg.shared.constant.cache) {
		var obj = dfs.crd.reg.shared.constant.cache[key];
		globalCache[obj] = null;
	}
};

dfs.crd.reg.shared.constant.cache.removeAllButErrorText = function() {
	for (var key in dfs.crd.reg.shared.constant.cache) {
		var obj = dfs.crd.reg.shared.constant.cache[key];
		if (obj === dfs.crd.reg.shared.constant.cache.ERROR_TEXT) {
			//don't remove
		} else {
			globalCache[obj] = null;
		}
	}
};

dfs.crd.reg.shared.constant.fieldLevelDefaultError = "You have entered invalid information. Carefully review your information and please try again.";

dfs.crd.reg.shared.constant.error = {
	DEFAULT : {
		title : "Problem with registration info",
		message : "Problem with registration info"
	},	
    //Default based on http status code
    '4xx' : {
        message : "We're sorry. The Credit Card Account Number and/or Password does not match our records, or you are not registered. Please try again."
    },
    '5xx' : {
        message : "Unknown error with the server, please try again later."
    },
    '1006' : {
    	newPage : true,
        title : "Secure Credit Card Log In",
        message : errorCodeMap["5031006"]
    },
    '1007' : {
    	newPage : true,
        title : "Secure Credit Card Log In",
        message : errorCodeMap["5031007"]
    },
    '1008' : {
    	newPage : true,
        title : "Secure Credit Card Log In",
        message : errorCodeMap["5031108"]
    },
    //Auth related
    '1101' : {
    	newPage : true,
        title : "Secure Credit Card Log In",
    	message : "You Have Exceeded the Maximum Number of Unsuccessful Attempts. Please contact Customer Service at <a href='tel:1-877-742-7822'>1-877-742-7822</a> for assistance."
    },
    '1102' : {
    	newPage : true,
        title : "Your Request Could Not Be Completed",
        message : "Due to special conditions related to your account, we could not complete your request. Please contact Customer Service at <a href='tel:1-877-742-7822'>1-877-742-7822</a> for more information or assitance."
    },
    '1103' : {
    //one more login attempt
        title : "Secure Credit Card Log In",
        message : errorCodeMap["4011103"]
    },
    '1104' : {
    	newPage : true,
        title : "Secure Credit Card Log In",
        message : errorCodeMap["4031104"]
    },
    '1105' : {
    	newPage : true,
        title : "Secure Credit Card Log In",
        message : errorCodeMap["4031105"]
    },
    '1106' : {
    	newPage : true,
        title : "Secure Credit Card Log In",
        message : errorCodeMap["4031106"]
    },
    '1107' : {
    	newPage : true,
        title : "Secure Credit Card Log In",
        message : errorCodeMap["4011107"]
    },
    //Strongauth related
    '1401' : {
        newPage : true,
        title : "Enhanced Account Security",
        message : errorCodeMap["4031401_UNLOCKED"]
    },
    //SA 
    '1402' : {
        newPage : true,
        title : "Enhanced Account Security",
        message : errorCodeMap["4031401_LOCKOUT"]
    },
    //registration specific
    /*'1901' : {
        message : "Invalid user input"
    },*/
    '1902' : {
        message : "User IDs do not match."
    },
    '1903' : {
        message : "Passwords do not match."
    },
    '1904' : {
        message : "Email is required and must be a valid address."
    },
    //SAMs
    '1905' : {
        newPage : true,
        title : "We're Sorry",
        message : "As a Sam's Club Discover Cardmember, you cannot register for the Account Center access at Discovercard.com. To manage your account you may <a href='http://www.samsclub.com'>register on the Sam's Club Website.</a><br/><br/>Please contact Sam's Customer Service at <a href='tel:1-866-221-1126'>1-866-221-1126</a> for more information or assistance."
    },
    '1906' : {
        //message : "Problem authenticating registration info."
        message : "You have entered invalid information.  Carefully review your information and please try again."
    },
    '1907' : {
        newPage : true,
        title : "Your Request Could Not Be Completed",
        message : "There was a problem authenticating your registration information."
    },
    //Problem resetting password
    '1908' : {
        newPage : true,
        title : "Your Request Could Not Be Completed",
        message : "The information you entered was not valid.<br/><br/>If you feel that you have received this message in error return to the <a href='../registration/forgotPasswordStep1.html'>Forgot Your Password page</a>, and enter your information again."
    },
    '1909' : {
        message : "Problem retrieving account information"
    },
    '1910' : {
    	newPage : true,
    	title : "Secure Credit Card Log In",
    	message : "You Have Exceeded the Maximum Number of Unsuccessful Attempts. Please contact Customer Service at <a href='tel:1-877-742-7822'>1-877-742-7822</a> for assistance."
    },
    '1911' : {
        //message : "Invalid external status"
        newPage : true,
        title : "Your Request Could Not Be Completed",
        message : "Due to special conditions related to your account, we could not complete your request. Please contact Customer Service at <a href='tel:1-877-742-7822'>1-877-742-7822</a> for more information or assitance."
    },
    '1912' : {
    	//prohibited list    	
    	newPage : true,
        title : "Your Request Could Not Be Completed",
        message : "Please contact Customer Service at <a href='tel:1-800-347-4996'>1-800-347-4996</a> for additional information about this message.."
    },
    '1913' : {
        //message : "Invalid Online Status"
        newPage : true,
        title : "Your Request Could Not Be Completed",
        message : "Please contact Customer Service at <a href='tel:1-877-742-7822'>1-877-742-7822</a> for more information or assistance."
    },
    '1914' : {
        //message : "Non auth buyer cannot register"
        newPage : true,
        title : "Contact Customer Service",
        message : "At this time, only the primary cardholder can register for and access Account Center.  For more assistance, please contact Customer Service at <a href='tel:1-877-742-7822'>1-877-742-7822</a>."
    },
    '1915' : {
        //message : "Rewards outage"
        newPage : true,
        title : "We're Sorry",
        message : "Your request cannot be completed because the rewards application is currently under maintenance.We apologize for the inconvenience. Please try again later or contact Customer Service at <a href='tel:1-800-347-2683'>1-800-DISCOVER (1-800-347-2683)</a>."
    },
    '1916' : {
        //message : "Failed security"
        message : "You have entered invalid information. Carefully review your information and please try again."
    },
    '1917' : {
        message : "You have one more log in attempt today. For security purposes, we limit the number of attempts to enter your information. If you think you are receiving this message in error, call <a href='tel:1-877-742-7822'>1-877-742-7822</a>."
    },
    '1918' : {
        message : "Your Request Could Not Be Completed. The information entered was not valid. If you feel that you have received this message in error, return to the Forgot User ID/Password page and try again."
    },
    '1919' : {
        message : dfs.crd.reg.shared.constant.fieldLevelDefaultError,
        fieldLevelMessage : {
            userId : "User ID and password must not be the same."
        }
    },
    '1920' : {
        message : dfs.crd.reg.shared.constant.fieldLevelDefaultError,
        fieldLevelMessage : {
            userId : "User ID must not look like your Social Security number. Please choose another one."
        }
    },
    '1921' : {
        message : dfs.crd.reg.shared.constant.fieldLevelDefaultError,
        fieldLevelMessage : {
            userId : "User ID is already in use. Please choose another one."
        }
    },
    '1922' : {
        newPage : true,
        title : "Secure Credit Card Log In",
        message : "Your Discover Card Account Number has changed. In order to access your account through the mobile channel, you must first log in to the Account Center at www.discover.com. For more information, please contact Customer Service at <a href='tel:1-877-742-7822'>1-877-742-7822.</a> "
    },
    '1923' : {
        message : dfs.crd.reg.shared.constant.fieldLevelDefaultError,
        fieldLevelMessage : {
            userId : "The User ID is empty. Please enter a User ID."
        }
    },
    '1924' : {
        message : dfs.crd.reg.shared.constant.fieldLevelDefaultError,
        fieldLevelMessage : {
            userId : "The User ID is invalid. Please enter a valid User ID."
        }
    },
    '1925' : {
        message : dfs.crd.reg.shared.constant.fieldLevelDefaultError,
        fieldLevelMessage : {
            password : "The password is empty. Please enter a password."
        }
    },
    '1926' : {
         message : dfs.crd.reg.shared.constant.fieldLevelDefaultError,
        fieldLevelMessage : {
            password : "The password is invalid. Please enter a valid password."
        }
    },
    NOTENROLLED : {
        newPage : true,
        title : "Enhanced Account Security",
        message : errorCodeMap["4031401_NOTENROLLED"]//'"Please log into the account center at www.discover.com to setup your security questions."
    },
    LOCKOUT : {
        newPage : true,
        title : "Enhanced Account Security",
        message : errorCodeMap["4031401_LOCKOUT"]//"For security purposes, your online account has been locked.<br/><br/>Please call Discover Customer Service at <a href='tel:1-888-251-8003'>1-888-251-8003</a> for information about accessing your account online."
    }
};

dfs.crd.reg.shared.constant.error.DENY = dfs.crd.reg.shared.constant.error.LOCKOUT;
dfs.crd.reg.shared.constant.error.UNLOCKED = dfs.crd.reg.shared.constant.error.NOTENROLLED;

/**
 * HTTP related functions to make the ajax calls
 */ 
dfs.crd.reg.shared.http.getErrorTxt = function() {
 	// Make AJAX call to Web Server to fetch the error text
 	var errorTxt = null;
 	var fullURL = dfs.crd.reg.shared.constant.url.ERROR_TXT;
 	$.ajax ({    
		type : "GET",
 		url : fullURL,
		async : false,
  		dataType : 'json',
		headers :prepareGetHeader(),                  
		success : function(responseData, status, jqXHR) {
			//put data into cache
			errorTxt = responseData;
       	},
    	error : function(jqXHR, textStatus, errorThrown) {
   			if (!isEmpty(cpEvent)) { cpEvent.preventDefault(); }
		}
 	});
 	return errorTxt;	
};//==>getErrorTxt

/**
* Registration's resource to check if strongauth is required
*/  
dfs.crd.reg.shared.http.saCheck = function(callbackPage) {
    try {
    	showSpinner();    	
        $.ajax({
            type : "GET",
            url : dfs.crd.reg.shared.constant.url.SA_CHECK,
            //async : false,
            dataType : 'json',
            headers : prepareGetHeader(),
            success : function(data, textStatus, jqXHR) {
                var regResponseData = data;
                /*console.log("ResponseData: " + regResponseData, regResponseData)
                console.log("status: ", textStatus)
                console.log("jqXHR: ", jqXHR)*/
                //strongauth required? no
                hideSpinner();
                navigation(callbackPage);
            },
            error : function(jqXHR, textStatus, errorThrown) {
                hideSpinner();
                dfs.crd.reg.shared.util.checkHeaderForStrongAuth(jqXHR, callbackPage);
            }
        });
    } catch(err) {
        showSysException(err);
    }
};

/**
 * Handles post for both Forgot Both step1 and Forgot Pass step1
 **/
dfs.crd.reg.shared.http.step1Post = function(url, requestBody, nav, cacheName, cacheData) {
    try {
    	//dfs.crd.reg.shared.util.clearNonRegCache();
	    showSpinner();
	    var headers = preparePostHeader({
	    		'X-DID' : getDID(),
                'X-OID' : getOID(),
                'X-SID' : getSID()
                });
	    
	    $.ajax({
	        type : "POST",
	        timeout : 30000,
	        url : url,
	        data : requestBody,
	        dataType : 'json',
	        headers : headers,
	        success : function(data, textStatus, jqXHR) {	
	            if (textStatus === "success") {
	                if (cacheName != null) {
	                    putDataToCache(cacheName, cacheData);
	                }
	                getSecToken();//sets the security token
	                	                               
	                //cache already cleared during step 1 submit, pull account data so menu can be populated correctly.
	                //dfs.crd.achome.populateACHome('ACHOME');	                	                
	                dfs.crd.reg.shared.http.saCheck('../registration/' + nav);
	            }	            
	            hideSpinner();
	        },
	        error : function(jqXHR, textStatus, errorThrown) {	              
	            dfs.crd.reg.shared.util.processErrorResponse(jqXHR);
	            hideSpinner();
	        }
	    });
    } catch(err) {
        showSysException(err);
    }
};

dfs.crd.reg.shared.http.forgotUserIdSubmit = function(dcrdBasicAuthHeader, event) {
    try {
    	//dfs.crd.reg.shared.util.clearNonRegCache();
    	showSpinner();
    	//prevent the default event from occuring and bubbling up
	    event.preventDefault();
	    event.stopPropagation();
	    
        $.ajax({
            type : "GET",
            url : dfs.crd.reg.shared.constant.url.FORGOT_UID,
            //async : false,
            dataType : 'json',
            headers : prepareGetHeader({
                'Authorization' : dcrdBasicAuthHeader,
                'X-Override-UID' : 'true',
                'X-DID' : getDID(),
                'X-OID' : getOID(),
                'X-SID' : getSID()
                }),
            success : function(data, textStatus, jqXHR) {
            	putDataToCache(dfs.crd.reg.shared.constant.cache.FORGOT_UID_CONFIRMATION_RESP, data);
            	navigation(dfs.crd.reg.shared.constant.url.FORGOT_UID_CONFIRMATION);
                hideSpinner();
            },
            error : function(jqXHR, textStatus, errorThrown) {  
                dfs.crd.reg.shared.util.processErrorResponse(jqXHR);
                hideSpinner();
            }
        });
    } catch(err) {
        showSysException(err);
    }
};
//==> submit

dfs.crd.reg.shared.http.step2Post = function(event, formData, postURL) {
	try {
		showSpinner();
        event.preventDefault();
        event.stopPropagation();
        
        //figure out if we are coming from the forgot password or forgot both flow and use data accordingly
		var flow;
		if (postURL === dfs.crd.reg.shared.constant.url.FORGOT_BOTH) {
			flow = {
				readCache : dfs.crd.reg.shared.constant.cache.FORGOT_BOTH_STEP1_REQ,
				writeCache : dfs.crd.reg.shared.constant.cache.FORGOT_BOTH_CONFIRMATION_RESP,
				callbackNav : dfs.crd.reg.shared.constant.url.FORGOT_BOTH_CONFIRMATION		
			}
		} else if (postURL === dfs.crd.reg.shared.constant.url.FORGOT_PASSWORD) {
			flow = {
				readCache : dfs.crd.reg.shared.constant.cache.FORGOT_PASSWORD_STEP1_REQ,
				writeCache : dfs.crd.reg.shared.constant.cache.FORGOT_PASSWORD_CONFIRMATION_RESP,
				callbackNav : dfs.crd.reg.shared.constant.url.FORGOT_PASSWORD_CONFIRMATION		
			}	
		} else {
			//if invalid flow show error
			dfs.crd.reg.shared.util.showErrorPage(dfs.crd.reg.shared.constant.error.DEFAULT.title, dfs.crd.reg.shared.constant.error.DEFAULT.message);
			return;
		}
		
		var formData = dfs.crd.reg.shared.util.readCacheAndMerge(formData, flow.readCache);
		
		//if invalid cache show error
		if (isEmpty(formData)) {
			dfs.crd.reg.shared.util.showErrorPage(dfs.crd.reg.shared.constant.error.DEFAULT.title, dfs.crd.reg.shared.constant.error.DEFAULT.message);		
			return;
		}
		
        var requestBody = JSON.stringify(formData);
	
		$.ajax({
            type : "POST",            
            timeout : 30000,
            url : postURL,
            data : requestBody,
            dataType : 'json',
            headers : preparePostHeader(),
            success : function(data, textStatus, jqXHR) {
                //if (textStatus === "success") {
                    putDataToCache(flow.writeCache, data);
                    navigation(flow.callbackNav);
                //}
                hideSpinner();
            },
            error : function(jqXHR, textStatus, errorThrown) {       
                dfs.crd.reg.shared.util.processErrorResponse(jqXHR);
                hideSpinner();
            }
        });
    } catch(err) {
        showSysException(err);
    }
} 
/*dfs.crd.reg.shared.util.clearNonRegCache = function(){
	var tmp = getDataFromCache(dfs.crd.reg.shared.constant.cache.FORGOT_BOTH_TITLE);
	clearGlobalCache();
	putDataToCache(dfs.crd.reg.shared.constant.cache.FORGOT_BOTH_TITLE, tmp);
};*/

/**
* Reads from specified cache and merges with past in object.  If cache is empty return null since the purpose is to get a good merge.
*/
dfs.crd.reg.shared.util.readCacheAndMerge = function(dataToMerge, cacheLoc) {
	var cacheData = getDataFromCache(cacheLoc);
	
	if (isEmpty(cacheData)) {
		return null;			
	}
	$.extend(dataToMerge, cacheData);
	return dataToMerge;
};

/**
* Populates form elements from cache, used when navigating back from strength meter page 
**/
dfs.crd.reg.shared.util.populateFormFromCache = function(cacheLoc) {
    //retrieve form data from cache
    var formData = getDataFromCache(cacheLoc);
    if (!isEmpty(formData)) {
        //remove form data from cache
        killDataFromCache(cacheLoc);
        //loop through form data
        for (var key in formData) {
            var obj = formData[key];
            //look up form element
            var $e = $("[name=" + key + "]");
            //set value
            $e.val(obj);
            if (!isEmpty(obj)) {
                //fire error conditions and meters
                $e.blur();
                $e.keyup();
            }
        }
    }
};


/**
* Called by saCheck if a server error is returned.  
* Inspects http status codes and response headers to determine if a challenge should happen.<b> 
* If not determines which stronguath error should be displyaed.
* If status is ALLOW or SKIPPED it continues navigation on to callback page
**/
dfs.crd.reg.shared.util.checkHeaderForStrongAuth = function(jqXHR, callbackPage) {
    var saAuth = jqXHR.getResponseHeader("WWW-Authenticate");
	
    if (jqXHR.status === 401 && saAuth !== null && saAuth.indexOf("challenge") >= 0) {
        dfs.crd.sa.checkForStrongAuth(callbackPage)
    } else if (jqXHR.status === 403) {
        var jsonResp = jQuery.parseJSON(jqXHR.responseText);

		//loop through each element of the data array
        jQuery.each(jsonResp.data, function() {
            if (!isEmpty(this.saStatus)) {
                dfs.crd.reg.shared.util.showSpecificError(this.saStatus);
            }
        });
    } else if (jqXHR.status === 200 || (saAuth !== null && saAuth.indexOf("skipped") >= 0)) {
        //ALLOW or SKIPPED, let them continue
        if (!isEmpty(cpEvent)) { cpEvent.preventDefault(); }
        //don't pass false to navigation or else page won't be stored in history
        navigation(callbackPage);
    } else {
        //handle unknown HTTP status
        dfs.crd.reg.shared.util.showSpecificError(jqXHR.status);
    }
};


/**
* Displays a new error page with given title and message
**/
dfs.crd.reg.shared.util.showErrorPage = function(title, message) {
    $('body').delegate("#publicError-pg", 'pagebeforeshow', function() {
        $('#errorLabel').html(title);
        $('#errorDiv').html(message);
    });
    $.mobile.changePage('../common/publicError.html');
};

/**
*  Displays the specified error text inline right below the header 
*/
dfs.crd.reg.shared.util.showInlineError = function(errorText) {
	var $headerError = $(".inline-error-msg:first");
	$headerError.html(errorText);
	$headerError.show();
};

/**
* Loops through each field in the errorFieldLevel object and sets the inline error to the custom message.
**/
dfs.crd.reg.shared.util.showInlineFieldError = function (errorFieldLevel) {
	if (!isEmpty(errorFieldLevel)) {
		$.each(errorFieldLevel, function(key, value) { 
		    var $existingError = $('[name=' + key + ']').parent().children('.inline-error-msg');
		    $existingError.html(value);
		    //make sure to turn back inline error back to "Invalid Value" for next use
               $existingError.show();
		});
	}	
};

/**
* Fetches error text from server and caches it.  If it's already been cached don't fetch again.
*/
dfs.crd.reg.shared.util.getErrorText = function() {
	var errorText = getDataFromCache(dfs.crd.reg.shared.constant.cache.ERROR_TEXT);
	if (isEmpty(errorText)) {
		//console.log("Fetch error text from server");
		errorText = dfs.crd.reg.shared.http.getErrorTxt();
		putDataToCache(dfs.crd.reg.shared.constant.cache.ERROR_TEXT, errorText);
	} else {
		//console.log("Don't fetch error text");
	}
	return errorText;
}

/**
* Displays the error page/messaging based on error code
* Called by processErrorResponse and other functions that have a handle on a specific error condition
*/
dfs.crd.reg.shared.util.showSpecificError = function(errorCode) {
	var serverErrorText = dfs.crd.reg.shared.util.getErrorText();
	
	var error;
	//determine whether we will use error text from the server or the app
	if (!isEmpty(serverErrorText) && !isEmpty(serverErrorText[errorCode])) {
		//use server
		error = serverErrorText[errorCode];	
		//console.log("using server error");	
	} else {
		//use local
		error = dfs.crd.reg.shared.constant.error[errorCode];
		//console.log("using local error");	
	}
	
	if (isEmpty(error)) {
		dfs.crd.reg.shared.util.showInlineError(dfs.crd.reg.shared.constant.error.DEFAULT.message);
		return;
	}
	
	if (!isEmpty(error.newPage) && error.newPage === true) {
		//show new page
		dfs.crd.reg.shared.util.showErrorPage(error.title, error.message);
	} else {
		//show inline errors
		dfs.crd.reg.shared.util.showInlineError(error.message)				
		dfs.crd.reg.shared.util.showInlineFieldError(error.fieldLevelMessage);
	}
}

/**
 * All standard registration error responses should call this function, it handles displaying
 * the proper inline error message or forwarding to the proper error page
 */
dfs.crd.reg.shared.util.processErrorResponse = function(jqXHR) {
	
	//if there is no status in the response text then show default error based on http status code
	if (isEmpty(jqXHR.responseText) || isEmpty(jQuery.parseJSON(jqXHR.responseText).status)) {
		//parses the response for the http status class (4 or 5) and use if exists
		var httpStatusClass = jqXHR.status.toString().charAt(0);
		if (!isEmpty(dfs.crd.reg.shared.constant.error[httpStatusClass + "xx"])) {
			dfs.crd.reg.shared.util.showSpecificError(httpStatusClass + "xx");				
		} else {
			//if no custom status exists show default error
			dfs.crd.reg.shared.util.showInlineError(dfs.crd.reg.shared.constant.error.DEFAULT.message);
		}
		return;
	}

	//process application status code from response text
	var responseData = jQuery.parseJSON(jqXHR.responseText);
	
	//show appropriate error based on status code
	dfs.crd.reg.shared.util.showSpecificError(responseData.status);
};

/**
* Populates the confirmation page for all registration flows
**/	
dfs.crd.reg.shared.util.populateConfirmation = function(idEmail, idLast4, idUID, cacheLocation) {
	var responseData = getDataFromCache(cacheLocation);	
	if (isEmpty(responseData)) {
		//console.log("Response data is empty", responseData);
		return;
	} else {
		//console.log("Valid response data:", responseData);
	}
	
	var email = responseData.email;
	var last4 = responseData.acctLast4;
	var userId = responseData.userId;

	if (isEmpty(email)) {
		email = "&nbsp;";
	}		
	
	$("#" + idEmail).html(email);
	$("#" + idLast4).html(last4);
	$("#" + idUID).html(userId);
};
	
	
/**
 *Shared form validation
 */
dfs.crd.reg.shared.util.formValidation = (function() {
    //=============================================
    //Private values
    //=============================================
    var ccNumPattern = /^6011\d{12}$/;
    var ccIINPattern = /^6011/;
    var fourDigitPattern = /^[0-9]{4}$/;
    var passwordPattern = /^(?=.*\d)(?=.*[a-z]).{8,32}$/i;
    var uidInvalidCharPattern = /(\s)|(\\)|(\')|(\")|(\`)/;
    var emailPattern = /^\s*[\w\-\+_]+(?:\.[\w\-\+_]+)*\@[\w\-\+_]+\.[\w\-\+_]+(?:\.[\w\-\+_]+)*\s*$/;
    var existingPasswordLengthMin = 5;
    var passwordLengthMax = 32;
    var currentForm;
    var validForm = false;

    /**
     *Display inline error message for specified field
     */
    var setRequiredField = function(el, skipOverride) {
        var $existingError = el.parent().children('.inline-error-msg');  
        el.addClass('required');           
        
        if (el.get(0).tagName === 'SELECT') {      
            var theme="f";
            el.parent().parent().find('.ui-btn')
               .removeClass('ui-btn-up-a ui-btn-up-b ui-btn-up-c ui-btn-up-d ui-btn-up-e ui-btn-up-f ui-btn-hover-a ui-btn-hover-b ui-btn-hover-c ui-btn-hover-d ui-btn-hover-e ui-btn-hover-f')
               .addClass('ui-btn-up-' + theme)
               .attr('data-theme', theme);
            //don't do anything with inline error if SELECT
            //$existingError = el.parent().parent().parent().children('.inline-error-msg');
        }
        
        if (isEmpty(skipOverride) || skipOverride === false) {
        	$existingError.html("Invalid value");
        }
        $existingError.show();
    };

    /**
     *Remove inline error message from specified field
     */
    var clearRequiredField = function(el) {
        var $existingError = el.parent().children('.inline-error-msg');  
        el.removeClass('required');
        
        if (el.get(0).tagName === 'SELECT') {   
            var theme="d";
            el.parent().parent().find('.ui-btn')
               .removeClass('ui-btn-up-a ui-btn-up-b ui-btn-up-c ui-btn-up-d ui-btn-up-e ui-btn-up-f ui-btn-hover-a ui-btn-hover-b ui-btn-hover-c ui-btn-hover-d ui-btn-hover-e ui-btn-hover-f')
               .addClass('ui-btn-up-' + theme)
               .attr('data-theme', theme);
            //don't do anything with inline error if SELECT
            //$existingError = el.parent().parent().parent().children('.inline-error-msg');            
        }
        
        $existingError.hide();
    };

    /**
     *Sets validForm to eithe true or false based on form status
     */
    var isValidForm = function() {
        var invalidSelector = $('.invalid');
        if (!isEmpty(currentForm)) {
            invalidSelector = $(currentForm + ' .invalid');
        }            
        
        if (invalidSelector.length) {            
            validForm = false;
        } else {
            validForm = true;
        }
        return validForm;
    };

    /**
     * Called during onBlur to dispaly invalid input message for failed validations
     * func - one of the validate... functions to call using the field input
     */
    var requiredField = function(func, $field1, $field2) {
        if (isEmpty($field2)) {
            func($field1);
        } else {
            func($field1, $field2);
        }

        if ($field1.hasClass('invalid')) {
        	//pass true so error text doesn't update during this call, only update error text when set from the keyup validation
            setRequiredField($field1, true);
        } else {
            clearRequiredField($field1);
        }
    };

    /**
     *Called by confirmUserIdHandler and confirmPasswordHandler to verify confirm field matched original
     */
    var requireUserOrPassAndConfirmationEqual = function(func, $field, $confirmField) {
        func($field, $confirmField);
        if (($confirmField.val().length) && $confirmField.val() === $field.val()) {
            clearRequiredField($confirmField);
        } else {
           setRequiredField($confirmField);
        }
    };

    /**
     *Helper function to help with validation of all passed in fields
     */
    var validateField = function(isValid, $field) {
        if (isValid) {
            $field.removeClass('invalid');
            clearRequiredField($field);
        } else if (!$field.hasClass('invalid')) {
            $field.addClass('invalid');
            setRequiredField($field);
        } else {
            //console.log("Field is invalid and already has flag", $field)
        }
        isValidForm();
    };

    /**
     *Interest function as it does a lot more logic than normal
     * Used by password and uid validation to make sure both original and confirmation fields are in sync.
     *
     */
    var validateUserOrPasswordEquality = function(isValid, $field1, $field2, isUserIdCompare) {
        var invalidField1 = function() {
            if (!$field1.hasClass('invalid')) {
                $field1.addClass('invalid');
            }
        };

        if ($field1.val().length >= 6) {
            if (isValid) {
                $field1.removeClass('invalid');
                clearRequiredField($field1);
            } else {
            	//custom check just for user id compare when 6011
            	/*if (isUserIdCompare && $field1.val().match(ccIINPattern)) {
            		invalidField1();
            		var $existingError = $field1.parent().children('.inline-error-msg');
        			$existingError.html("User ID must not look like an account number. Please choose a User ID that is not a 16-digit number starting with '6011'.");
                    setRequiredField($field1, true);
            	} else {*/
	                invalidField1();
	                setRequiredField($field1);
               // }
            }

            // Note: If User changes the original field to a different value, the confirm field should be invalid.
            if ($field2.val.length && $field1.val() !== $field2.val()) {
                if (!$field2.hasClass('invalid')) {
                    $field2.addClass('invalid');
                    setRequiredField($field1);
                }
            }
            // Note: If User changes original field to match the confirm field, the confirm field should be valid.
            // Note: If user changes the confirm field the behavior already exists to check validity.
            else if ($field2.val.length && $field1.val() === $field2.val()) {
                $field2.removeClass('invalid');
                clearRequiredField($field1);
            }
        } else {
            invalidField1();
        }
        isValidForm();
    };

    //=============================================
    //Public functions
    //=============================================
    
    //call to reset form state for every new form
    var setCurrentForm = function(form) {
        validForm = false;
        currentForm = form;
    };
    
    //=============================================
    //Validation function
    //=============================================

    /**
     *Discover card account number (forgot uid)
     */
    var validateCcNum = function($ccNum) {
        var ccNumValue = $ccNum.val();
        var isValid = ccNumValue.match(ccNumPattern);
        validateField(isValid, $ccNum);
    };

    /**
     *Existing account center password (forgot uid)
     */
    var validateAccountCenterPassword = function($accountCenterPassword) {
        var passwordText = $accountCenterPassword.val();
        var isValid = (passwordText.length >= existingPasswordLengthMin && passwordText.length <= passwordLengthMax);
        validateField(isValid, $accountCenterPassword);
    };

    /**
     *Existing user id or discover card account number (forgot pass)
     */
    // 6011123456789012 or foo12 or bar-1 or userA but not 6011user or some special characters below
    var validateCcOrAccountId = function($ccOrAccountId) {
        var ccOrAccountIdValue = $ccOrAccountId.val();
        var isValid = (ccOrAccountIdValue.match(ccNumPattern) || !(ccOrAccountIdValue.length <= existingPasswordLengthMin || ccOrAccountIdValue.match(uidInvalidCharPattern) || ccOrAccountIdValue.match(ccIINPattern)));
        validateField(isValid, $ccOrAccountId);
    };

    /**
     *Card expiration month (forgot pass and forgot both)
     */
    var validateExpirationMonth = function($expMonth) {
        var isValid = !($expMonth.prop("selectedIndex") === 0);
        validateField(isValid, $expMonth);
    };

    /**
     *Card expiration year (forgot pass and forgot both)
     */
    var validateExpirationYear = function($expYear) {
        var isValid = !($expYear.prop("selectedIndex") === 0);
        validateField(isValid, $expYear);
    };

    /**
     *Cardmember birth date (forgot pass and forgot both)
     */
    // Check card expiration year
    var validateBirthDate = function($birthDate) {
        var isValid = !($birthDate.prop("selectedIndex") === 0);
        validateField(isValid, $birthDate);
    };

    /**
     *Cardmember birth month (forgot pass and forgot both)
     */
    // Check card expiration year
    var validateBirthMonth = function($birthMonth) {
        var isValid = !($birthMonth.prop("selectedIndex") === 0);
        validateField(isValid, $birthMonth);
    };

    /**
     *Cardmember birth year (forgot pass and forgot both)
     */
    var validateDobYear = function($dobYear) {
        $dobYear.keyup(function() {
            this.value = this.value.replace(/\D/g, '');
        });
        var isValid = ($dobYear.val().match(fourDigitPattern));
        validateField(isValid, $dobYear);
    };

    /**
     *Cardmember last 4 ssn (forgot pass and forgot both)
     */
    var validateSsnEnding = function($ssnEnding) {
        $ssnEnding.keyup(function() {
            this.value = this.value.replace(/\D/g, '');
        });

        var isValid = ($ssnEnding.val().match(fourDigitPattern));
        validateField(isValid, $ssnEnding);
    };

    var validateConfirmPassword = function($passwordField, $confirmPasswordField) {
        if (($confirmPasswordField.val().length) && $confirmPasswordField.val() === $passwordField.val()) {
            $confirmPasswordField.removeClass('invalid');
            clearRequiredField($confirmPasswordField);
        } else {
            if (!$confirmPasswordField.hasClass('invalid')) {
                $confirmPasswordField.addClass('invalid');
                setRequiredField($confirmPasswordField); //resolves QC Defect 76182
            }
        }
        isValidForm();
    };

    var validateConfirmUserId = function($userIdField, $confirmUserIdField) {
        if (($confirmUserIdField.val().length) && $confirmUserIdField.val() === $userIdField.val()) {
            $confirmUserIdField.removeClass('invalid');
            clearRequiredField($confirmUserIdField);
        } else {
            if (!$confirmUserIdField.hasClass('invalid')) {
                $confirmUserIdField.addClass('invalid');
                setRequiredField($confirmUserIdField);
            }
        }
        isValidForm();
    };

    /**
     *Cardmember email (forgot both)
     */
    var validateEmail = function($emailAddressField) {
        var isValid = ($emailAddressField.val().match(emailPattern));
        validateField(isValid, $emailAddressField);
    };

    /**
     *Cardmember password creation (forgot both and forgot pass step2)
     */
    var validatePassword = function($passwordField, $confirmPasswordField) {
        var isValid = ($passwordField.val().match(passwordPattern));
        validateUserOrPasswordEquality(isValid, $passwordField, $confirmPasswordField, false);
    };

    /**
     *Cardmember uid creation (forgot both step2)
     */
    var validateUserId = function($userIdField, $confirmUserIdField) {
        var isValid = !($userIdField.val().match(uidInvalidCharPattern) || $userIdField.val().match(ccIINPattern));            
        validateUserOrPasswordEquality(isValid, $userIdField, $confirmUserIdField, true);
    };

    //=============================================
    //Handler functions
    //=============================================
    var ccNumHandlers = function($ccNum) {
        $ccNum.keyup(function() {
            validateCcNum($ccNum)
        });
        $ccNum.blur(function() {
            requiredField(validateCcNum, $ccNum);
        });
    };
    
    var accountCenterPasswordHandlers = function($accountCenterPassword) {
        $accountCenterPassword.keyup(function() {
            dfs.crd.reg.shared.util.formValidation.validateAccountCenterPassword($accountCenterPassword)
        });
        $accountCenterPassword.blur(function() {
            requiredField(validateAccountCenterPassword, $accountCenterPassword);
        });
    };    

    var ccOrAccountIdHandlers = function($ccOrAccountId) {
        $ccOrAccountId.keyup(function() {
            dfs.crd.reg.shared.util.formValidation.validateCcOrAccountId($ccOrAccountId)
        });
        $ccOrAccountId.blur(function() {
            requiredField(validateCcOrAccountId, $ccOrAccountId);
        });
    };

    var onChangeExpirationMonth = function($expMonth) {
        $expMonth.change(function() {
            dfs.crd.reg.shared.util.formValidation.validateExpirationMonth($expMonth);
        });
    };

    var onChangeExpirationYear = function($expYear) {
        $expYear.change(function() {
            dfs.crd.reg.shared.util.formValidation.validateExpirationYear($expYear);
        });
    };

    var onChangeBirthMonth = function($birthMonth) {
        $birthMonth.change(function() {
            dfs.crd.reg.shared.util.formValidation.validateBirthMonth($birthMonth);
        });
    };

    var onChangeBirthDate = function($birthDate) {
        $birthDate.change(function() {
            dfs.crd.reg.shared.util.formValidation.validateBirthDate($birthDate);
        });
    };

    var ssnEndingHandlers = function($ssnEnding) {
        $ssnEnding.keyup(function() {
            dfs.crd.reg.shared.util.formValidation.validateSsnEnding($ssnEnding)
        });
        $ssnEnding.blur(function() {
            requiredField(validateSsnEnding, $ssnEnding);
        });
    };

    var dobYearHandlers = function($dobYear) {
        $dobYear.keyup(function() {
            dfs.crd.reg.shared.util.formValidation.validateDobYear($dobYear)
        });
        $dobYear.blur(function() {
            requiredField(validateDobYear, $dobYear);
        });
    };

    var userIdHandlers = function($userIdField, $confirmUserIdField) {
        $userIdField.keyup(function() {
            dfs.crd.reg.shared.util.formValidation.validateUserId($userIdField, $confirmUserIdField)
        });
        $userIdField.blur(function() {
            requiredField(validateUserId, $userIdField, $confirmUserIdField);
        });
    };

    var confirmUserIdHandlers = function($userIdField, $confirmUserIdField) {
        $confirmUserIdField.keyup(function() {
            dfs.crd.reg.shared.util.formValidation.validateConfirmUserId($userIdField, $confirmUserIdField)
        });
        $confirmUserIdField.blur(function() {
            requireUserOrPassAndConfirmationEqual(validateConfirmUserId, $userIdField, $confirmUserIdField);
        });
    };

    var passwordHandlers = function($passwordField, $confirmPasswordField) {
        $passwordField.keyup(function() {
            dfs.crd.reg.shared.util.formValidation.validatePassword($passwordField, $confirmPasswordField)
        });
        $passwordField.blur(function() {
            requiredField(validatePassword, $passwordField, $confirmPasswordField);
        });
    };

    var confirmPasswordHandlers = function($passwordField, $confirmPasswordField) {
        $confirmPasswordField.keyup(function() {
            validateConfirmPassword($passwordField, $confirmPasswordField);
        });
        $confirmPasswordField.blur(function() {
            requireUserOrPassAndConfirmationEqual(validateConfirmPassword, $passwordField, $confirmPasswordField);
        });
    };

    var emailHandlers = function($emailAddressField) {
        $emailAddressField.keyup(function() {
            dfs.crd.reg.shared.util.formValidation.validateEmail($emailAddressField)
        });
        $emailAddressField.blur(function() {
            requiredField(validateEmail, $emailAddressField);
        });
    };
   
   
    //FORM VALIDATION
    var forgotUIDForceValidation = function($ccNum, $accountCenterPassword) {
        requiredField(validateCcNum, $ccNum);
        requiredField(validateAccountCenterPassword, $accountCenterPassword);
    };
    
    var forgotPasswordStep1Validation = function($ccOrAccountId, $expMonth, $expYear, $birthMonth, $birthDate, $ssnEnding, $dobYear) {
        requiredField(validateCcOrAccountId, $ccOrAccountId);
        requiredField(validateExpirationMonth, $expMonth);
        requiredField(validateExpirationYear, $expYear);
        requiredField(validateBirthMonth, $birthMonth);
        requiredField(validateBirthDate, $birthDate);
        requiredField(validateSsnEnding, $ssnEnding);
        requiredField(validateDobYear, $dobYear);    
    };
    
    var forgotPasswordStep2Validation = function($passwordField, $confirmPasswordField) {
        requiredField(validatePassword, $passwordField, $confirmPasswordField);
        requireUserOrPassAndConfirmationEqual(validateConfirmPassword, $passwordField, $confirmPasswordField);
    };
    
    var forgotBothStep1Validation = function($ccNum, $expMonth, $expYear, $birthMonth, $birthDate, $ssnEnding, $dobYear) {        
        requiredField(validateCcNum, $ccNum);
        requiredField(validateExpirationMonth, $expMonth);
        requiredField(validateExpirationYear, $expYear);
        requiredField(validateBirthMonth, $birthMonth);
        requiredField(validateBirthDate, $birthDate);
        requiredField(validateSsnEnding, $ssnEnding);
        requiredField(validateDobYear, $dobYear);     
    };
    
    var forgotBothStep2Validation = function($userIdField, $confirmUserIdField, $passwordField, $confirmPasswordField, $emailAddressField) {
        requiredField(validateUserId, $userIdField, $confirmUserIdField);
        requireUserOrPassAndConfirmationEqual(validateConfirmUserId, $userIdField, $confirmUserIdField);  
        requiredField(validatePassword, $passwordField, $confirmPasswordField);
        requireUserOrPassAndConfirmationEqual(validateConfirmPassword, $passwordField, $confirmPasswordField);
        requiredField(validateEmail, $emailAddressField);        
    };

    return {
        //----------------
        //VALIDATION CALLS
        //----------------
        //forgot user id
        validateCcNum : validateCcNum,
        validateAccountCenterPassword : validateAccountCenterPassword,
        //FP step 1
        validateCcOrAccountId : validateCcOrAccountId,
        //FP or FB step1
        validateExpirationMonth : validateExpirationMonth,
        validateExpirationYear : validateExpirationYear,
        validateBirthMonth : validateBirthMonth,
        validateBirthDate : validateBirthDate,
        validateSsnEnding : validateSsnEnding,
        validateDobYear : validateDobYear,
        //FP or FB step2
        validatePassword : validatePassword,
        validateConfirmPassword : validateConfirmPassword,
        //forgot both step 2
        validateUserId : validateUserId,
        validateConfirmUserId : validateConfirmUserId,
        validateEmail : validateEmail,

        //--------------
        //EVENT HANDLERS
        //--------------
        ccNumHandlers : ccNumHandlers,
        accountCenterPasswordHandlers : accountCenterPasswordHandlers,
        //FP step 1
        ccOrAccountIdHandlers : ccOrAccountIdHandlers,
        //FP or FB step1
        ssnEndingHandlers : ssnEndingHandlers,
        dobYearHandlers : dobYearHandlers,
        onChangeExpirationMonth : onChangeExpirationMonth,
        onChangeExpirationYear : onChangeExpirationYear,
        onChangeBirthMonth : onChangeBirthMonth,
        onChangeBirthDate : onChangeBirthDate,
        //FP or FB step2
        passwordHandlers : passwordHandlers,
        confirmPasswordHandlers : confirmPasswordHandlers,
        //forgot both step 2
        userIdHandlers : userIdHandlers,
        confirmUserIdHandlers : confirmUserIdHandlers,
        emailHandlers : emailHandlers,

        //================
        //Force validation
        //================
        forgotUIDForceValidation : forgotUIDForceValidation,
        forgotPasswordStep1Validation : forgotPasswordStep1Validation,
        forgotPasswordStep2Validation : forgotPasswordStep2Validation,
        forgotBothStep1Validation : forgotBothStep1Validation,
        forgotBothStep2Validation : forgotBothStep2Validation,

        //valid form flag
        setCurrentForm : setCurrentForm,
        isValidForm : isValidForm
        //form submit handler
        //onFormSubmit : onFormSubmit
    }
})(); 


/**
 * Registraiton utility functions
**/
dfs.crd.reg.shared.util.InputStrengthMeter = function(inputContainer, inputName, isUserIdField, uiserIDsel){

// InputStrength Meter
// takes 3 arguments:
// - the inputContainer (selector of the containing element)
// - the inputName (selector of the field itself)
// - isUserIdField (boolean, leave blank for password, set to true for UserID strength meter)
//InputStrengthMeter : function(inputContainer, inputName, isUserIdField, uiserIDsel) {
	var that = this, config = {
		'strengthLabels' : ['Not Valid', 'Moderate', 'Strong'],
		'strengthClass' : ['low', 'medium', 'high']
	}, $container = $(inputContainer), $strengthLabel = $container.find('.strength-label'), $strengthMeter = $container.find('.strength-meter'), $input = $container.find(inputName);
	$userIDCompareInput = $(uiserIDsel);

	var setStrengthLabel = function(str) {
		$strengthLabel.text(str);
	};

	var setStrengthClass = function(classStr) {
		$strengthMeter.removeClass().addClass('strength-meter').addClass(classStr);
	};

	var resetStrength = function() {
		setStrengthLabel('');
		setStrengthClass();
	};

	var updateStrength = function(inputText, strength) {
		var i;
		for (i = 0; i < strength; i++) {
			setStrengthLabel(config.strengthLabels[i]);
			setStrengthClass(config.strengthClass[i]);
		}
	};

	var checkPasswordStrength = function(inputText) {
		var strength = 0, diversity = 0;
		specialCharacters = /(\~)|(\!)|(\@)|(\#)|(\$)|(\%)|(\^)|(\&)|(\*)|(\-)|(\_)|(\+)|(\=)|(\[)|(\])|(\{)|(\})|(\|)|(\:)|(\;)|(\,)|(\.)|(\<)|(\>)|(\?)|(\/)/;

		if (!inputText) {
			resetStrength();
			return false;
		}

		if (inputText === $userIDCompareInput.val()) {
			strength = 1;
			updateStrength(inputText, strength);
			return false;
		}

		if (inputText.length > 0) {
			strength = 1;
		}

		if (inputText.length >= 8) {
			if (inputText.match(/[a-z]|[A-Z]/) && inputText.match(/[\d+]/)) {
				strength++;

				if (inputText.match(/[a-z]|[A-Z]/) && inputText.match(/\d+/) && inputText.match(specialCharacters)) {
					strength++;
				}
			} else {
				strength = 1;
			}
		} else {
			strength = 1;
		}

		// add test for if matches card
		updateStrength(inputText, strength);

	};

	var checkUserIdStrength = function(inputText) {
		var strength = 0, diversity = 0, specialCharacters = /(\~)|(\!)|(\@)|(\#)|(\$)|(\%)|(\^)|(\&)|(\*)|(\-)|(\_)|(\+)|(\=)|(\[)|(\])|(\{)|(\})|(\|)|(\:)|(\;)|(\,)|(\.)|(\<)|(\>)|(\?)|(\/)/;

		if (!inputText) {
			resetStrength();
			return false;
		}

		if (inputText.length > 0) {
			strength = 1;
		}

		if (inputText.length >= 6) {
			if (inputText.match(/[a-z]/) || inputText.match(/[\d+]/)) {
				strength++;

				if (inputText.match(/[a-z]/) && inputText.match(/\d+/) && inputText.match(specialCharacters)) {
					strength++;
				}

				// The inputText is not-valid under these conditions:
				// If input has whitespaces, backslashes, back-ticks, and single or double quotes.
				// or...  if it contains a string of numbers 16 characters long starting in 6011.
				if (inputText.match(/(\s)|(\\)|(\')|(\")|(\`)/) || inputText.match(/^6011/)) {
					strength = 1;
				}
			} else {
				strength = 1;
			}
		} else {
			strength = 1;
		}

		// add test for if matches card
		updateStrength(inputText, strength);

	};

	var observeInput = function() {
		if (!isUserIdField) {
			$input.bind("keyup", function() {
				checkPasswordStrength(this.value);
			});
		} else {
			$input.bind("keyup", function() {
				this.value = this.value.toLowerCase();
				checkUserIdStrength(this.value);
			});
			
			$userIdConfirm = $('input[name="userIdConfirm"]');
			$userIdConfirm.bind("keyup", function() {
				this.value = this.value.toLowerCase();
			});
		}
	};

	var init = function() {
		observeInput();
	}();

};
//==>InputStrengthMeter
