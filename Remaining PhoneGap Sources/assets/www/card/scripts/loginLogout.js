dfs.crd.lilo = dfs.crd.lilo || {};
dfs.crd.lilo.userLogOut=false;
//dfs.crd.lilo.isRemembered=false;

// Push Notification Code Begin

dfs.crd.lilo.otherUser=false; // to find if user, logged in, is not associated with that device
dfs.crd.lilo.appReinstall=false; // to find if the App was reinstalled - Future Use
dfs.crd.lilo.registrationServiceCalled=false; // to find if the Push Registration Service was called
dfs.crd.lilo.pushNewMsgCount=0; // to find the count of new push message since last time visit to history
dfs.crd.lilo.globalPushError = false; // to find if the Push Registration service fails and to show in ACHome page

// Push Notification Code End

// Added to cause Logout on hitting Back on ACHOME page
function cardLoginLoad()
{
	try {
    if (fromPageName=="cardHome")
    {
        cpEvent.preventDefault();
        dfs.crd.lilo.logOutUser("LOGOUT");
        return;
    }
    clearGlobalCache();	
	} catch (err) {
        showSysException(err);
    }
}

dfs.crd.lilo.isUserRemembered = function(uid)
{
    //alert(uid);
	
}

// Begin Push Notification Code

/* Check Local Storage for Device Registration Status. 
 Return True and save the Vendor ID in global variable if the device is alreday registered and cash is not cleared. Otherwise Return False */

dfs.crd.lilo.isDeviceRegistered = function()
{
	try{
        //return false; // for testing, TBD to be removed
		if (localStorage.getItem("VID") != null)
        {
            //current vendor ID validation with previous vendor ID in case there is any app reinstall
            //if (vendorID == localStorage.getItem("VID"))
			//{   
            vendorID = localStorage.getItem("VID");
            //alert("VID is present and same as previous");
            return true;
            //} 
		}
		return false;
	}catch(err)
	{
		showSysException(err);
	}
}

// End Push Notification Code

dfs.crd.lilo.goToBankLogin = function ()
{
     try {
        window.location.href = BANK_URL;
    } catch (err) {
        showSysException(err);
    }
}

dfs.crd.lilo.setRememberID = function(userName,rememberID)
{
	try{
		if (rememberID == "yes") 
		{
			if (!( ((/^\d+$/).test(userName)) && (userName.length == 16) && ((/^6011/).test(userName)) ))
			{
				//localStorage.setItem("USER", userName);
                KeyValue.prototype.setEncryptedKeyValue(function success(){}, function failure(){},"USER",userName);
			}
            
		} 
		else 
		{
            KeyValue.prototype.setEncryptedKeyValue(function success(){}, function failure(){},"USER","");
		}
	}catch(err)
	{
		showSysException(err);
	}
}

dfs.crd.lilo.logOutUser = function(eventForLogout)
{		
	try{
		clearGlobalCache();	
		clearHistory(); // Added to push Android app. into the background on logout
        hideSpinner();
		try
		{
			var newDate = new Date();
			var LOGOUTURL= RESTURL+"session/v1/delete";
			$.ajax({
				type : "POST",
				url : LOGOUTURL,
				headers : preparePostHeader(),
				success : 
					function(response, status, jqXHR) 
					{
					clearSecToken();
					},

					error : 
						function(jqXHR, textStatus, errorThrown) 
						{
						}
			});
		}catch(err){
			showSysException(err);
		}

		var messageForLogout="";

		if (eventForLogout=="TIMEOUT")
		{
			messageForLogout="Your session has expired due to Inactivity." +
			"<span>Please login again.</span>";
		}
		else if (eventForLogout == "NETWORK")
		{
			messageForLogout = "";
		}
		else
		{
			dfs.crd.lilo.userLogOut=true;
			messageForLogout="You are now logged out. " +
			"<span>Thank you for using Discover Mobile </span>";
		}

		$("#login-pg").live("pagebeforeshow",function callBackGoToHome() {
			var logoutDiv=$("#logoutDiv");
			logoutDiv.html(messageForLogout);
			logoutDiv.css("display", "block");
		});


		navigation("../../../index");
        if(deviceType == "Android")
        {
            ClearHistory.prototype.clearHistory();
        }
		hasLogedOut=true;
		dfs.crd.lilo.unbindEventHandlers();
	}catch(err)
	{
		showSysException(err);
	}
}

dfs.crd.lilo.errorHandlerLogin = function(errorCode, customErrorMessage,strongAuthOrUserID,label)
{
	try{
		var errorMsg;	
		if (!isEmpty(customErrorMessage)) {
			errorMsg=customErrorMessage;
		}
		if(isEmpty(label)){
			label="Secure Credit Card Log In";
		}
		var jsonErrorMsg = errorCodeMap[errorCode]; 
		if (isEmpty(jsonErrorMsg)) {
			errorCode = 0;
		}
		errorCode = ""+errorCode;
		switch (errorCode){		
		case "401":
		case "4011103":
		case "4011107":
			var errorLoginInline=$("#errorLoginInline");
			var uid=$("#uid");
			var pwd=$("#pwd");
			errorLoginInline.css("display", "block");
			errorLoginInline.html(errorCodeMap[errorCode]);
			uid.val("");
			pwd.val("");
			$("#flip-b").val("no").slider("refresh");
			uid.addClass("errormsg");
			pwd.addClass("errormsg");
			break;
		case "503"      :
		case "4031101":
		case "4031102":
		case "4031104":
		case "4031105":
		case "4031106":		
		case "500":
		case "400":
		case "403":
		case "0":
        case "4031401_LOCKOUT":         
		case "4031401_NOTENROLLED":
		case "4031402":
		case "4031401_NOTENROLLED":
		case "4031401_DENY":
		case "4031401_UNLOCKED":
		case "4031401_UNVERIFIED":
		case "5031006":	
		case "5031007":	
		case "5031008":
		case "5031108": 
		case "1633"	  :
			$("#loginErrorPage-Pg").live("pagebeforeshow",function(){
				if (label == null | label == undefined)
				{
					$("#errorLabel").html("Secure Credit Card Log In");
				}
				else
				{
					$("#errorLabel").html(label);
				}
				$("#globalErrorDiv").html(errorCodeMap[errorCode]);
				if (errorCode=="4031105" ||errorCode=="5031008"||errorCode=="5031007" ||errorCode=="5031006"){
				$("#globalErrorDiv").html(errorMsg);
			}
			});	
			navigation("../common/loginErrorPage");
			
			break;         	
		default:
			$("#loginErrorPage-Pg").live("pagebeforeshow",function(){
				$("#errorLabel").html("Secure Credit Card Log In");
                $("#globalErrorDiv").html("Unknown Error: " + errorCode);
			});		
		navigation("../common/loginErrorPage");
		break;
		}
	}catch(err)
	{
		showSysException(err);
	}
}

dfs.crd.lilo.authenticateUser = function()
{
	try{
        // Push Notification Change - to retrieve Vendor ID
        getXidFromNative();
        
		if (dfs.crd.lilo.validateLoginParams(user,password,rememberID))
		{
			var newDate = new Date();	
			var LILOURL = RESTURL+"acct/v1/account?"+newDate+"";
			showSpinner();
			$.ajax({
				type : "GET",
				url : LILOURL,
				dataType : "json",
                timeout: 30000,
				headers :authHeader,				
				success : function(response, status, jqXHR){
					getSecToken();
                    CookieManager.prototype.getCookie(function successToken (arg) {dfsKey = arg}, null, "dfsedskey");
					dfs.crd.lilo.userLogOut = false;	
					dfs.crd.lilo.setRememberID(user, rememberID);										
					if (jQuery.isEmptyObject(response)){
						var errorCode="1633";
						dfs.crd.lilo.errorHandlerLogin(errorCode, "");
					}else{
						putDataToCache("ACHOME", response);
                        getDataAsync();
                   
                        // Begin Push Notification Code
                   
                        // save card type and data in global variables
                        incentiveCode = response["incentiveCode"];
                        incentiveTypeCode = response["incentiveTypeCode"];
                        optionCode =  response["optionCode"];
                        cardType = response["cardType"];
                        applicationMode = response["outageModeVal"];
                   
                        // setting the session variable true after successful authentication
                        isSessionValid = true;
                        // Check Local Storage for Device Registration Status, check if the VID retrieved is same with previous stored one
                        if (dfs.crd.lilo.isDeviceRegistered())
                        {
                            dfs.crd.lilo.gotoACHome(false);
                            // Call Registration Status REST service in achome page asynchronously
                        }
                        else
                        {
                        // if vendorID is null, then XID could not be retrieved, Xtify service issue, go to CardHome page
                        if (vendorID == "")
                        {
                            dfs.crd.lilo.gotoACHome(true);
                        }
                        else
                        {
                        // Call Registration Status Synchronously and save the status in local storage
                        var newDate = new Date();	
                        var PUSHREGISTRATIONURL = RESTURL + "contact/v1/registration/status?" + newDate +"&vid="+ vendorID + "";
                        //alert(PUSHREGISTRATIONURL + "synchronously from login");
                   
                        showSpinner();
                        $.ajax({
                          type : "GET",
                          url : PUSHREGISTRATIONURL,
                          async : false,
                          dataType : 'json',
                          headers:prepareGetHeader(),
                          
                          success : function(responseData, status, jqXHR) 
                          {
                          hideSpinner();
                          if (jqXHR.status != 200) {
                          var code=getResponseStatusCode(jqXHR);
                          } else 
                          {
                          var resultCode = responseData["resultCode"];
                          
                          // set registrationServiceCalled to true,so that achome page doesn't need to call the serv asyc
                          dfs.crd.lilo.registrationServiceCalled = true;
                          
                          // Navigate to T&C Page if the call returns false
                          if (resultCode == "F"){
                               navigation('../pushNotification/pushTermsAndConditions');
                          } else
                          {
                          // Else if resultcode is O, set otherUser as true in Cache
                          if (resultCode == "O")
                          { 
                          dfs.crd.lilo.otherUser = true;
                          //alert("lilo other user: " + otherUser);
                          } 
                          // set the current VendorID in local storage
                          localStorage.setItem("VID", vendorID);
                          // Navigate to Achome page if the call returns T or O
                          dfs.crd.lilo.gotoACHome(false);
                          }  
                          hideSpinner();
                          }
                          },
                          error : function(jqXHR, textStatus, errorThrown) 
                          {
                          hideSpinner();
                          var code=getResponseStatusCode(jqXHR);
                          //errorHandler(code,'','pushRegistrationStatus');
                          dfs.crd.lilo.gotoACHome(true);
                          }
                          });
                        }
                        }
                        // End Push Notification Code
					}					
				},

				error : function(jqXHR, textStatus, errorThrown){
					hideSpinner();
					var errorCode = "";				
				    var userIdToken = [];
                    var label;
					var userIdDisplayText;
					try
					{
					var stausCode=getResponseStatusCode(jqXHR);
						var errorMsgData=getResponsErrorData(jqXHR);
						errorCode = jqXHR.status+stausCode;
						if( 0 != stausCode ){
							switch(stausCode){
							case "1105":
								if(!isEmpty(errorMsgData)){
									if(errorMsgData[0].hasOwnProperty("userid")){
										userIdToken["UserIdToken"] = "<br/>Your User id is:"+errorMsgData[0].userid;
										var userIdDisplay = errorCodeMap["4031105_WITHUSERID"];
										userIdDisplayText = parseContent(userIdDisplay,userIdToken);
									}
								}else{
									userIdToken["UserIdToken"] = "";
									var userIdDisplay =	errorCodeMap["4031105"];
									userIdDisplayText = parseContent(userIdDisplay,userIdToken);
								}
								break;
							case "1401":
								if(!isEmpty(errorMsgData)){					
									var saStatusCode="";
									if(errorMsgData[0].hasOwnProperty("status")){
										saStatusCode=errorMsgData[0].status;
									}else if(errorMsgData[0].hasOwnProperty("saStatus")){
										saStatusCode=errorMsgData[0].saStatus;
									}
									errorCode=errorCode+"_"+saStatusCode;
									label=titlelabel[errorCode];								
								}
								break;
							case "1402":
								if(!isEmpty(errorMsgData)){				
									label=titlelabel[errorCode];								
								}
								break;
							case "1006":
							case "1007":
							  userIdDisplayText=jqXHR.getResponseHeader("Location");							  
							  var splitMsg = userIdDisplayText.split("//");
							  userIdDisplayText = splitMsg[1];
                              splitMsg=maintMsg.split("|~|");
                              maintMsg=splitMsg[0];
                              userIdDisplayText=maintMsg;
                              label="Secure Credit Card Login";
                              break;
								}
						}
					}catch(e){
						errorCode=jqXHR.status;
					}
					dfs.crd.lilo.errorHandlerLogin(errorCode, userIdDisplayText,"",label);
				}

			});
		}
	}catch(err){
		showSysException(err);
	}
}

// Begin Push Notification
dfs.crd.lilo.gotoACHome = function(error)
{
    dfs.crd.lilo.globalPushError = error;
    
    if (pushRedirection && (!dfs.crd.lilo.otherUser))
        redirectPushUser();
    else
        navigation('../achome/cardHome');
}

// End Push Notification

//reset the server time out (update) - added by Ryan

//Application-Version and Service Unavailable check
dfs.crd.lilo.preLoginCheck = function(){
    try{
               error : function(jqXHR, textStatus, errorThrown){
                    hideSpinner();
                    var errorCode=""+jqXHR.status;
                    var statusCode=getResponseStatusCode(jqXHR);
                    if(statusCode != 0){
                        errorCode=errorCode+statusCode;
                    }
               switch(errorCode){
               case "4031002":
                    var upgradeMsg=getResponseFromTxt(jqXHR,"message");
					$("#forceUpgrade-pg").live("pagebeforeshow",function(){ 
						$("#sccl").html("Update Now to Access Your Credit Card Account");
						$(".errormsg").html(upgradeMsg);
					});			
               
                    localStorage.removeItem("VersionInfoMsg");
                    localStorage.removeItem("VersionInfoMsgDate");
                    localStorage.removeItem("showVersionUpgradeMsgCounter");
                    navigation("card/html/common/forceUpgrade");
                    break; 
               case "5031006":
               case "5031007":
               var maintMsg=jqXHR.getResponseHeader("Location");
               var splitMsg = maintMsg.split("//");
               maintMsg = splitMsg[1];
               splitMsg=maintMsg.split("|~|");
               maintMsg=splitMsg[0];
               dfs.crd.lilo.errorHandlerPreLogin(errorCode,maintMsg);
               break; 
			   default	:
					dfs.crd.lilo.errorHandlerPreLogin(errorCode);
					break;
                }				
               }
               
               });
    }catch(err){
        showSysException(err);
    }
}

//Application-Version and Service Unavailable check
dfs.crd.lilo.cancelVersionUpdate = function(){
	localStorage.removeItem("showVersionUpgradeMsgCounter");
	var thirtyDaysFromToday = new Date((new Date()).getTime() + 30*86400000);						
	localStorage.setItem("VersionInfoMsgDate", thirtyDaysFromToday);
	dfs.crd.lilo.goToCardLogin(false,true);
}

dfs.crd.lilo.showOptionalUpgradePage = function(headerVersionInfo){
    
	$("#optionalUpgrade-pg").live("pagebeforeshow",function(){
		$("#sccl").html("New Version of App Available");		
		$("#optionalUpgradeMsg").html(headerVersionInfo);
	});
	navigation("card/html/common/optionalUpgrade");
}

$("#login-pg").live("pageinit", function(event, ui) {
                    var status=getParameterByName("status")
                    if (status=="timeout")
                    {
                    messageForLogout="Your session has expired due to Inactivity." +
                    "<span>Please login again.</span>";
                    }
                    else if (status=="logout")
                    {
                    messageForLogout="You are now logged out. " +
                    "<span>Thank you for using Discover Mobile </span>";
                    }
                    else
                    {
                    messageForLogout=""
                    }
                    var logoutDiv=$("#logoutDiv");
                    logoutDiv.html(messageForLogout);
                    logoutDiv.css("display", "block");
                    });

function getParameterByName(name) {
    var match = RegExp("[?&]" + name + "=([^&]*)").exec(window.location.search);
    return match && decodeURIComponent(match[1].replace(/\+/g, " "));
}

dfs.crd.lilo.errorHandlerPreLogin = function(errorCode,custErrorMsg)
{
	try{
		errorCode = ""+errorCode;
        var errorMsg = "";
        if (isEmpty(custErrorMsg))
        {
            errorMsg = errorCodeMap[errorCode];
        }else
        {
            errorMsg=custErrorMsg;
        }
		var label = "Secure Credit Card Log In";
		switch (errorCode){
		case "0":
        case "5031108":        
		case "5031006":	
		case "5031007":
			$("#loginErrorPage-Pg").live("pagebeforeshow",function(){				
				$("#errorLabel").html(label);  
				$("#globalErrorDiv").html(errorMsg);
			});			
			break; 
		default:
			$("#loginErrorPage-Pg").live("pagebeforeshow",function(){
			if(!isEmpty(errorMsg)){
			$("#errorLabel").html(label);
			$("#globalErrorDiv").html(errorMsg);
			}else{
				$("#errorLabel").html(label);
                $("#globalErrorDiv").html("Unknown Error: " + errorCode);
				}
			});			
		break;
		}
		navigation("card/html/common/loginErrorPage");
		}catch(err)
	{
		showSysException(err);
	}
}