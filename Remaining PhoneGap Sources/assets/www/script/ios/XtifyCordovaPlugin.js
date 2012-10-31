var customData;
var appState;
var sdkVersion;
var vendorID = "" ;//"123412341234123412341234";
var deviceLocation;
var badgeCount;
var returnCode;
var deviceID;
var deviceOS;
var osVersion;
var pageCode; // = "payment";
var reqID;
var pushRedirection = false;

function ReceivedPushNotification(customData,appState)
{
    customData = unescape(customData);
    appState = unescape(appState);
    
    //alert("custom data: " + customData);
    
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
    
    /*if(appState == "Active")
    {   
        alert("Navigating to Push Redirection, pageCode: " + pageCode + " msgID: " + reqID + " AppState: " + appState);
    }*/
    
    // TBD call async MarMessageRead method to indicate the message has been read
    var listToBeMarkRead = [];
    listToBeMarkRead.push(reqID);  
    
    if(isSessionValid)
    {   var statusReturned = dfs.crd.push.alert.postAlertHistoryMessage('markRead',listToBeMarkRead);
        dfs.crd.achome.pushNewMessageCountAsync(); // for showing the new message count in the header
    }
    
    //alert("Post Status: " + statusReturned);
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


function getXtifySDKVersion(sdkVersion){
    sdkVersion = sdkVersion;
}
function getCustomDataForLastPush() {
    
    callNativeFunction(
                       ["GetData"] ,
                       
                       function(result) {
                       customData= unescape(result);
                       },
                       
                       function(error) {
                       customData= "no custom data";
                       }
                       );  
    
}

function getXidFromNative() {
    try
    {
    callXidNativeFunction(
                          ["GetXid"] ,
                          function(result) {
                          //alert("XID Success");
                          vendorID= result;
                          },
                          
                          function(error) {
                          //alert("XID error: " + error);
                          vendorID= "no xid";
                          }
                          );  
    }catch(error){
        //alert("Push Notification service is unavailable. Error: " + error);
    }
}

function getLocationFromNative() {
    
    callLocationNativeFunction(
                               ["GetLocation"] ,
                               function(result) {
                               deviceLocation = result;
                               },
                               
                               function(error) {
                               deviceLocation = "no location";
                               }
                               );  
    
}

function setBadgeToNative() {
    var val = badgeCount;
    setBadgeCountNativeFunction(
                               [val] ,
                               function(result) {
                               returnCode = result;
                               },
                               
                               function(error) {
                               returnCode = "Error setting badge";
                               }
                               );  
}
function getBadgeFromNative() {
    getBadgeCountNativeFunction(
                                ["GetBadge"] ,
                                function(result) {
                                badgeCount = result;
                                },
                                    
                                function(error) {
                                badgeCount = "Error getting badge";
                                }
                                );  
    
}


function clearNotificationsAndBadgeNative() {
    clearNotificationsAndBadge(
                                ["clearNotifsBadge"] ,
                                function(result) {
                                returnCode = result;
                                },
                                
                                function(error) {
                                returnCode = "Error clearing notifications and badge";
                                }
                                );  
    
}

function callNativeFunction(types, success, fail) {
    return Cordova.exec(success, fail, "XtifyCordovaPlugin", "print", types);
}    

function callXidNativeFunction(types, success, fail) {
    try{
        return Cordova.exec(success, fail, "XtifyCordovaPlugin", "printXid", types);
    }
    catch(err)
    {
        //alert(err);
    }
}

function callLocationNativeFunction(types, success, fail) {
    return Cordova.exec(success, fail, "XtifyCordovaPlugin", "printLocation", types);
}    

function setBadgeCountNativeFunction(types, success, fail){
    Cordova.exec(success, fail, "XtifyCordovaPlugin", "setSpringBoardBadgeCount", types);
}

function getBadgeCountNativeFunction(types, success, fail){
    Cordova.exec(success, fail, "XtifyCordovaPlugin", "getSpringBoardBadgeCount", types);
}

function clearNotificationsAndBadge(types, success, fail){
    Cordova.exec(success, fail, "XtifyCordovaPlugin", "clearNotifications", types);
}