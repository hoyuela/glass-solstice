
/* Name Space */
namespace("dfs.crd.push");
dfs.crd.push.reg = dfs.crd.push.reg || {};

function pushTermsAndConditionsLoad()
{
	try{		
		//populatePushTermsAndConditions('PUSH_TERMS_CONDITIONS_DETAILS');
        
        /*  Click T&C Cancel 
            Call Push Registration POST Service along with status PENDING
         */
        $('#tnc-cancel').click(function()
                               {
                               dfs.crd.push.reg.postStatusForPushRegistration("P");
                               //navigation('../achome/cardHome');
                               });
        
        /*  Click T&C Accept 
         Call Push Registration POST Service along with status ACCEPTED
         */
        
        $('#tnc-accept').click(function()
                               {
                               dfs.crd.push.reg.postStatusForPushRegistration("Y");
                               //navigation('../pushNotification/manageAlerts');
                               });
        

        
	}catch(err){
		showSysException(err);
	}
}



 
dfs.crd.push.reg.postStatusForPushRegistration = function (registrationStatus) 
{
	try{
		var newDate = new Date();	
		var PUSHREGISTRATIONURL = RESTURL + "contact/v1/registration/status?" + newDate +"";
		
		var dataJSON = {
			"vid":vendorID,
			"deviceOS":getClientPlat(),
			"osVersion":getOsVersion(),
			"deviceID":getDeviceID(),
			"regStatus":"P"
		};
		var dataJSONString = JSON.stringify(dataJSON);
		//alert (dataJSONString);  
        showSpinner();
		$.ajax({
			type : "POST",
			url : PUSHREGISTRATIONURL,
			async : false,
			dataType : 'json',
			data :dataJSONString,
			headers:preparePostHeader(),
			success : function(response, status, jqXHR) {
              hideSpinner();
				if (jqXHR.status != 200 & jqXHR.status != 204) {
					var code=getResponseStatusCode(jqXHR);
					errorHandler(code,'','pushTermsAndConditions');
				} else {
					
              
                        var resultCode = response["resultCode"];
              
                        /*/ JSON File read - needs to be removed once calling the service
                        var pushRegistrationPostResponse = new Object();
                        pushRegistrationPostResponse = getContentJson("pushRegistrationPostResponse");  
                        if (jQuery.isEmptyObject(pushRegistrationPostResponse)){
                            return;
                        }
                        
                        var resultCode = pushRegistrationPostResponse["resultCode"];*/
              
                        // Store the resultCode U (update or ReInstall) or C (create or Install) in global variable for future use
                        if (resultCode == "U"){
                            dfs.crd.lilo.appReinstall = true;
                        }
                        
                        // set the current VendorID in local storage which indicates T&C shown
                        localStorage.setItem("VID", vendorID);
                        //alert("VID : " + vendorID);
                            
                        if (registrationStatus == "Y"){
                            // Navigate to Manage Notification Page if user selected Yes
                                navigation('../pushNotification/manageAlertsOverride');
                        } else if (registrationStatus == "P")
                            {
                                // Otherwise Navigate to Achome page 
                                navigation('../achome/cardHome');
                            }
               
                        hideSpinner();
              
				}
			},
			error : function(jqXHR, textStatus, errorThrown) {
                hideSpinner();
				var code=getResponseStatusCode(jqXHR);
				//errorHandler(code,'','pushRegistrationPost');
                dfs.crd.lilo.globalPushError = true;
                navigation('../achome/cardHome');
			}
		});
		//return postData;
	}catch(err){
		showSysException(err);
	}
}




