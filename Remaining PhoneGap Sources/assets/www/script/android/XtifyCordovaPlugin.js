var customData;
var vendorID;
var deviceID;
var deviceOS;
var osVersion;
var pageCode;
var reqID;
var pushRedirection = false;



var XtifySDK = function() {
}

XtifySDK.prototype.start = function(successCallback, failureCallback) {
	return Cordova.exec(successCallback, failureCallback, 'XtifyCordovaPlugin',
			'startSdk', [ {
				'successCallback' : successCallback.name
			} ]);
};

XtifySDK.prototype.getXid = function(successCallback, failureCallback) {
	return Cordova.exec(successCallback, failureCallback, 'XtifyCordovaPlugin',
			'getXid', []);
};

XtifySDK.prototype.isRegistered = function(successCallback, failureCallback) {
	return Cordova.exec(successCallback, failureCallback, 'XtifyCordovaPlugin',
			'isRegistered', []);
};

XtifySDK.prototype.setNotifIcon = function(iconName, failureCallback) {
	return Cordova.exec(null, failureCallback, 'XtifyCordovaPlugin',
			'setNotifIcon', [ {
				'notifIconName' : iconName
			} ]);
};

Cordova.addConstructor(function() {
	Cordova.addPlugin('XtifySDK', new XtifySDK());
	PluginManager.addService("XtifyCordovaPlugin",
			"com.xtify.cordova.XtifyCordovaPlugin");
});



//document.addEventListener('deviceready', function () {
//	alert("devicerReady pn.js");
//   window.plugins.XtifySDK.start(
//   ReceivedPushNotification, function (error) {
//	   var someVar = 'Error occurred while starting Xtify SDK.';
//	   alert(someVar);
//   });
//   alert("deviceReady pn.js 2");
//}, false);


/*function isRegistered() {
    //window.plugins.
    XtifySDK.isRegistered(
                          function() {
                          var someVar = 'Device is registered';
                          alert(someVar);
                          },
                          function(errorId) {
                          var someVar = null;
                          if (errorId == "inProgress") {
                          someVar = "Registration in progress.";
                          } else {
                          someVar = "Error: " + errorId;
                          }
                          alert(someVar);
                          });
}*/


function getXidFromNative() {
    //window.plugins.
    window.plugins.XtifySDK.isRegistered(
                                         function() {
                                         var someVar = 'Device is registered';
                                         //alert(someVar);
                                         window.plugins.XtifySDK.getXid(function(xid) {
                                                                        vendorID = xid;
                                                                        //alert(vendorID);
                                                                        }, function(error) {
                                                                        var someVar = "Error: " + error;
                                                                        vendorID= "Device registered, but error while retrieving Vendor ID";
                                                                        //alert(someVar);
                                                                        });
                                         },
                                         function(errorId) {
                                         var someVar = null;
                                         if (errorId == "inProgress") {
                                         someVar = "Registration couldn't be completed";
                                         } else {
                                         someVar = "Registration Error: " + errorId;
                                         }
                                         //alert(someVar);
                                         
                                         window.plugins.XtifySDK.getXid(function(xid) {
                                                                        vendorID = xid;
                                                                        //alert(vendorID);
                                                                        }, function(error) {
                                                                        var someVar = "Error: " + error;
                                                                        vendorID= "Device could not be registered with C2DM, please setup google account";
                                                                        //alert(vendorID + " " + someVar);
                                                                        });
                                         });
}


function ReceivedPushNotification(customData)
{
    
    customData = unescape(customData["customKey"]);
    //appState = unescape(appState);
    
    // retrive the messageID or reqID and pageCode or formCode from the custom data
    //reqID = customData.split(",")[0].split("=")[1];
    //pageCode = customData.split(",")[1].split("=")[1];
    
    // parse the custom data to retrieve the pagecode and msgID
    var keyName = customData.split(",")[1].split("=")[0];
    
    switch(keyName.toLowerCase()){
        case "pagecode":
            pageCode =  customData.split(",")[1].split("=")[1];
            break;
        case "reqid":
            reqID =  customData.split(",")[1].split("=")[1];
            break;
    }
    
    keyName = customData.split(",")[0].split("=")[0];
    
    switch(keyName.toLowerCase()){
        case "pagecode":
            pageCode =  customData.split(",")[0].split("=")[1];
            break;
        case "reqid":
            reqID =  customData.split(",")[0].split("=")[1];
            break;
    }
    
    // pushRedirection = true; Should be uncommented to turn on offline push
    
    //alert("Navigating to Push Redirection, formCode: " + pageCode + " msgID: " + reqID);
    
    // TBD call async MarMessageRead method to indicate the message has been read
    
    // TBD reduce the badge count by one 
    
    var listToBeMarkRead = [];
    listToBeMarkRead.push(reqID);                                                                                       
    
    if(isSessionValid)
    {   var statusReturned = dfs.crd.push.alert.postAlertHistoryMessage('markRead',listToBeMarkRead);
        dfs.crd.achome.pushNewMessageCountAsync(); // for showing the new message count in the header
    }
    
    redirectPushUser();
}


function redirectPushUser()
{
    // check for valid session and redirect the user to that page from push alert, otherwise redirect to the login page
    if(isSessionValid)
    {
        if (!dfs.crd.lilo.otherUser)
        {
            //alert("inside redirection");
            // set pushRedirection as false since redirection happened
            pushRedirection = false;
            
            switch (pageCode) {
                case "acstmt":
                    navigation('../statements/statements');
                    break;
                case "payment":
                    navigation('../payments/paymentsLanding');
                    break;
                case "payhist":
                    navigation('../payments/paymentsHistory');
                    break;
                case "acact":
                    navigation('../statements/accountActivity');
                    break;
                case "cbbrem":
                    navigation('../rewards/cashbackBonusSignup1');
                    break;
                case "redeemcbb":
                    navigation('../rewards/redemptionLanding');
                    break;
                case "redeemmiles":
                    navigation('../rewards/milesRedeem');
                    break;   
                default:
                    navigation('../pushNotification/alertHistory');
                    break;
            }
        }
    }
    else
    {
        //setTimeout(alert("wait"), 30000);
        dfs.crd.lilo.goToCardLogin();
    }
}


/*function getXidFromNative() {
	//alert("in getXID");
	window.plugins.XtifySDK.getXid(function(xid) {
                                   
                                   vendorID = xid;
                                   //alert(vendorID);
                                   }, function(error) {
                                   var someVar = "Error: " + error;
                                   vendorID= "no xid";
                                   //alert(someVar);
                                   });
}*/