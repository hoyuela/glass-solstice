/* Description:
 * This .js file includes the functionality and information for the account
 * related screens. This provides the data population for the account home and
 * the account pages.
 */

/*********************Card Home Page Start******************* */

/** Name Space**/
dfs.crd.achome = dfs.crd.achome || {};
/** **/

/*  This function will be called pagebeforechangeevent of cardHome(e is the event objet)****/ 
function cardHomeLoad()
{
	try{
		dfs.crd.achome.populateACHome('ACHOME');
		// getDataAsync();
        
        // Begin Push Notification code
        
        // call the message count and registration Async services
		//dfs.crd.achome.pushRegistrationServiceAsync(); moved to page show event in common.js
        //dfs.crd.achome.pushNewMessageCountAsync();  moved to page show event in common.js
        // End Push Notification code
        
	}catch(err)
	{
		showSysException(err);
	}
}

/* Begin Push Notification code
$("#cardHome-pg").live('pageshow', function(e,data){
                        try{
                           dfs.crd.achome.pushRegistrationServiceAsync(); // for showing the override user message after cardhome is loaded
                           }
                        catch(err)
                           {
                           showSysException(err);
                           }
                           });
// End Push Notification code*/


dfs.crd.achome.shouldShowSSOToggle = function(cardHome) {
	// Returns true if:
	// -- cardHome is populated
	// -- cardHome.isSSNMatched boolean
	// -- cardHome.isSSOUser is boolean
	// -- cardHome.payLoadSSOText is a string

	
	// Verify that the objects are populated and of the correct type
	if (jQuery.isEmptyObject(cardHome)) {
		return false;
	}
	
	if (typeof(cardHome.isSSOUser) !== 'boolean') {
		return false;
	}
	
	// Temporary fix until isSSNMatched is available in prod.  Once it is, remove this whole if-block
	if (typeof(cardHome.isSSNMatched) === 'undefined') {
		cardHome.isSSNMatched = true;
	}	
	
	if (typeof(cardHome.isSSNMatched) !== 'boolean') {
		return false;
	}
	
	if (typeof(cardHome.payLoadSSOText) !== 'string') {
		return false;
	}
	
	return cardHome.isSSOUser;
}

/*
 * Description:Function called on "pagebeforeshow" for cardHome.
 */
dfs.crd.achome.populateACHome = function(pageName)
{	
	try{
	
		var cardHome = dfs.crd.achome.getACHomeData(pageName);
		if (!jQuery.isEmptyObject(cardHome)){
			if (dfs.crd.achome.shouldShowSSOToggle(cardHome)) {
				$(".ssoToggle").show();
			
				if (dfs.crd.sa.doSSOCheck("../achome/cardHome")) {
					return;
				}
			}
			dfs.crd.achome.populateAchomePageDivs(cardHome, pageName);
		}
			
		//}
	}catch(err)
	{
		showSysException(err);
	}
}

/* Begin Push Notification Code
 * Description: Function to call Registration Status Service asynchronously to find Alternate / Other User
 */
dfs.crd.achome.pushRegistrationServiceAsync = function()
{
    // Check regServCalld to find if Registration Status Service was called in login flow, if true ignore the call, else call async
    try
    {
        // set the XID to vendorID once again in case it was not retrieved properly
        if(vendorID == "")
        {
           getXidFromNative();
        }
        
        if (!dfs.crd.lilo.registrationServiceCalled && vendorID != ""){
            //alert("reg-service-async");
            var newDate = new Date();	
            var PUSHREGISTRATIONURL = RESTURL + "contact/v1/registration/status?" + newDate +"&vid="+ vendorID + "";
            //alert(PUSHREGISTRATIONURL + "asynchronously from achome");
            
            $.ajax({
                   type : "GET",
                   url : PUSHREGISTRATIONURL,
                   //async : false,
                   dataType : 'json',
                   headers:prepareGetHeader(),
                   
                   success : function(responseData, status, jqXHR) {
                   
                   if (jqXHR.status != 200) {
                   e.preventDefault();
                   var code=getResponseStatusCode(jqXHR);
                   //errorHandler(code,'','cardHome');
                   } else {
                   
                   var resultCode = responseData["resultCode"];
                   
                   // set registrationServiceCalled to true, so that achome page doesn't call the serv again
                   
                   dfs.crd.lilo.registrationServiceCalled = true;
                   //alert(resultCode);
                   if (resultCode == "F"){
                   // VID has changed and not matched with the database, call the POST registration service to save the new VID 
                   dfs.crd.push.reg.postStatusForPushRegistration("");
                   } else
                   {
                   // Else if resultcode is O, set otherUser as true in Cache
                    if (resultCode == "O")
                    { 
                    dfs.crd.lilo.otherUser = true;
                    //alert("lilo other user: " + otherUser);
                    var errorString = "Changes have been made to push alerts. Update your settings to continue receiving alerts";
                    $("#global-errors").addClass('redtext boldtext').html(errorString).show();  
						trackNoAlertsSetError();
                    } 
                   
                   // set the current VendorID in local storage
                    localStorage.setItem("VID", vendorID);
                    }
                   retreivePushCount();
                   
                   }
                   },
                   error : function(jqXHR, textStatus, errorThrown) {
                   hideSpinner();
                   var code=getResponseStatusCode(jqXHR);
                   }
                   })
        }
        else
            retreivePushCount();
    }catch(err)
	{
		//showSysException(err);
	}
}


/* Description: Function to call Get New Message Count Service asynchronously
 */
dfs.crd.achome.pushNewMessageCountAsync = function()
{
    try
    {
        var newDate = new Date();	
        var PUSHNEWMESSAGECOUNTURL = RESTURL + "msghist/v1/notification/newcount?" + newDate;
        //alert(PUSHNEWMESSAGECOUNTURL + "asynchronously msg count from achome");
        
        var boolAsync = true;
        
        var deviceVer = parseInt(deviceVersion);
        if(deviceType=="Android" && deviceVer>=4) 
            boolAsync=false;
            
        
        $.ajax({
               type : "GET",
               url : PUSHNEWMESSAGECOUNTURL,
               async : boolAsync,
               dataType : 'json',
               headers:prepareGetHeader(),
               success : function(responseData, status, jqXHR) {
               if (!validateResponse(responseData,"pushMessageCounterValidation")){ // Pen Test Validation
            	   errorHandler("SecurityTestFail","","");
            	   return;
				   }               
               if (jqXHR.status != 200) {
               e.preventDefault();
               var code=getResponseStatusCode(jqXHR);
               //errorHandler(code,'','cardHome');
               } 
               else 
               {
               dfs.crd.lilo.pushNewMsgCount = responseData["newMsgCount"];
               
               //alert("Header new msg count: " + pushNewMsgCount);
               
               dfs.crd.achome.populatePushCountDivs();
               
               // reset the counter once message count service is called
               lastPushCountTime = new Date().getTime();
			   retreivePushCount();
               }
               },
               error : function(jqXHR, textStatus, errorThrown) {
               hideSpinner();
               var code=getResponseStatusCode(jqXHR);
			   retreivePushCount();
               //errorHandler(code,'','cardHome');
               }
               });
    }catch(err)
	{
		//showSysException(err);
	}
}

// populates the push count in the header and AC Home banner 
dfs.crd.achome.populatePushCountDivs = function()
{
    var activePage = $.mobile.activePage.attr('id');
    
    // For showing the New Message Count Header in Card Home page
    if (dfs.crd.lilo.pushNewMsgCount == 0)
    {
        
        // hide the notification message if current page is cardHome
        if(activePage == "cardHome-pg")
        {
            $("#new-message-count-div").hide();
        }
        
        // hide the header badge icon
        $(".number-of-new-messages").hide();
        
        // If otherUser is false, Reset the iOS BAdge Count
		var deviceT="";
        if (!isEmpty(deviceType)) deviceT=deviceType.toLowerCase();     //Change

        if (dfs.crd.lilo.registrationServiceCalled && !dfs.crd.lilo.otherUser && deviceT!="android")
        {
            clearNotificationsAndBadgeNative();
        }
		allNotificationRead();
    }
    else
    {
        
        $("#push_alert_icon").addClass("notification-header-icon");
        
        // set the value of new message count in the Header Badge Icon and show them
        if(dfs.crd.lilo.pushNewMsgCount > 99)
            $(".number-of-new-messages").html("99+").show();
        else
            $(".number-of-new-messages").html(dfs.crd.lilo.pushNewMsgCount).show();
        
        $(".notification-header-icon").show();
        $("#alert_history_page_link").show();
        
        // show the notification message if current page is cardHome
        if(activePage == "cardHome-pg")
        {
            var newAlertCountString = "You have " + dfs.crd.lilo.pushNewMsgCount + " new push alerts."
            /*$("#new-message-count-text").html(newAlertCountString);
            $("#new-message-count-div").show();*/
        }
        
        // If othetUser is false, Update the iOS Badge Count
		var deviceT="";
        if (!isEmpty(deviceType)) deviceT=deviceType.toLowerCase();     //Change

        if (dfs.crd.lilo.registrationServiceCalled && !dfs.crd.lilo.otherUser && deviceT!="android")
        {
            getBadgeFromNative();
            if(badgeCount != dfs.crd.lilo.pushNewMsgCount)
            {
                badgeCount = dfs.crd.lilo.pushNewMsgCount;
                setBadgeToNative();
            }
        }
		newNotificationExist();
    }	
}

// End Push Notification Code


/*
 * Description:Makes the Webservice call @return : response JSON data
 */
dfs.crd.achome.getACHomeData = function(pageId)
{
	try
	{		
		var cardHome = getDataFromCache(pageId);
		if (jQuery.isEmptyObject(cardHome)) 
		{
			var cardHome;				
			HybridControl.prototype.getAccountDetails(function successAccountDetail (arg) {cardHome = arg;}, null);
			cardHome = $.parseJSON(cardHome);	
			putDataToCache(pageId, cardHome);
		}
		return cardHome;
	}catch(err)
	{
		showSysException(err);
	}
}

/*
 * Description:Populates the page divs for the Card Home page. Sets the Dynamic content on
 * the Card Home page. Check for null and blank value for newlyEarnedRewards
 * field Append rewards_info and rewards_offer to html <li> divs Click event
 * enable on footer icons
 * date compared for infantDate "00000000"
 */
dfs.crd.achome.populateAchomePageDivs = function(responseData, pageId)
{     
	try{
		var newlyEarnedVaue=null;           
		$('#back-btn').html("");
		var reward_info_ContentData = [];
		var defaultVal = "$0.00";
		incentiveCode = responseData["incentiveCode"];
        //console.log("incentiveCode:"+incentiveCode);
		incentiveTypeCode = responseData["incentiveTypeCode"];
        // console.log("incentiveTypeCode:"+incentiveTypeCode);
		optionCode =  responseData["optionCode"];
        // console.log("optionCode:"+optionCode);
		cardType = responseData["cardType"];
        //console.log("cardType:"+cardType);
		applicationMode = responseData["outageModeVal"];
        // console.log("applicationMode:"+applicationMode);
		projectBeyondCard = isCardProjectBeyond(incentiveCode,incentiveTypeCode,optionCode,cardType);
		if(applicationMode == null){
			applicationMode = "";
		}
        
        // Begin Push Notification Code to show the error or override user status on the top
        $("#global-errors").hide();
        $("#new-message-count-div").hide();
        
        // hide the header badge icon
        $(".notification-header-icon").hide();
        $("#alert_history_page_link").hide();
        
        if (dfs.crd.lilo.globalPushError)
        {
            var errorString = "The notification service is unavailable";
            //$("#global-errors").addClass('redtext boldtext').html(errorString).show();
        }
        
        if (dfs.crd.lilo.otherUser)
        {
            var errorString = "Changes have been made to push alerts. Update your settings to continue receiving alerts";
            $("#global-errors").addClass('redtext boldtext').html(errorString).show();        
			trackNoAlertsSetError();
        }
        
        // End Push Notification 
        
		$("#card-info").html("Discover Card Ending "+responseData["lastFourAcctNbr"]);
		$("#primaryCardmember").html("<h1>"+responseData["primaryCardmember"].nameOnCard+"</h1>");

		if((responseData["statementBalance"]) < 0){
					$("#cardHome_statementBalance").html("-$"+ splitNegativeBalance(responseData["statementBalance"]));
					}
			else{
		$("#cardHome_statementBalance").html("$"+ numberWithCommas(responseData["statementBalance"]));
			}
			
		if((responseData["currentBalance"]) < 0){
					$("#cardHome_currentBalance").html("-$"+ splitNegativeBalance(responseData["currentBalance"]));
					}
			else{
		$("#cardHome_currentBalance").html("$"+ numberWithCommas(responseData["currentBalance"]));
			}	

		if (responseData["paymentDueDate"] != null
				&& responseData["paymentDueDate"] != '00000000') 
		{
			var dueDate = formatDate(responseData["paymentDueDate"]);
			var payDueDate = convertDate(dueDate);
			$("#cardHome_minpay").html("Min. Payment Due <span class='dueDate'>"+ payDueDate+":</span>");
		}else
		{
			$("#cardHome_minpay").html("Min. Payment Due :");
		}
		if(!isEmpty(responseData["minimumPaymentDue"])){
			$("#cardHome_minimumPaymentDue").html("$"+ numberWithCommas(responseData["minimumPaymentDue"]));
		}else{
			$("#cardHome_minimumPaymentDue").html(defaultVal);
		}
		if(!isEmpty(responseData["availableCredit"])){
			if((responseData["availableCredit"]) < 0){
					$("#cardHome_availableCredit").html("-$"+ splitNegativeBalance(responseData["availableCredit"]));
					}
			else{
			$("#cardHome_availableCredit").html("$"+ numberWithCommas(responseData["availableCredit"]));
			}	
		}else{
			$("#cardHome_availableCredit").html(defaultVal);
		}
//		Incorporated on 26-03-2012 
		if (!(isACLite(applicationMode)  || isRewardOutage(applicationMode))){           
			if (accountEarnsCBB(responseData["incentiveTypeCode"]) || accountEarnsMiles(responseData["incentiveTypeCode"])) 
			{   var rewards_info = getPageContentMin("cardHome", "rewards_info",
					responseData["incentiveTypeCode"],responseData["incentiveCode"]);
            
            if (responseData["earnRewardAmount"] != null
					&& responseData["earnRewardAmount"] != "") 
            {
                reward_info_ContentData['earnRewardAmount'] = numberWithCommas(responseData["earnRewardAmount"]);
            } else 
            {
                reward_info_ContentData['earnRewardAmount'] = '0.00';
            }
            if (responseData["newlyEarnedRewards"] != null
					&& responseData["newlyEarnedRewards"] != "") 
			{
				newlyEarnedVaue = numberWithCommas(responseData["newlyEarnedRewards"]);
			} else 
			{
				newlyEarnedVaue = '0.00';
			}
			if (accountEarnsCBB(responseData["incentiveTypeCode"]))
			{
				var newlyEarned = getPageContentCn("cardHome","newly_earned_cbb_line");
				var newlyEarned_ContentData=[];
				newlyEarned_ContentData["newlyEarnedRewards"]=newlyEarnedVaue;
				newlyEarned = parseContent(newlyEarned, newlyEarned_ContentData);
				reward_info_ContentData['newlyEarnedRewardsLine']=newlyEarned;
			}
			else if (accountEarnsMiles(responseData["incentiveTypeCode"]))
			{
				var newlyEarned = getPageContentCn("cardHome","newly_earned_miles_line");
				var newlyEarned_ContentData=[];
				newlyEarned_ContentData["newlyEarnedRewards"]=newlyEarnedVaue;
				newlyEarned = parseContent(newlyEarned, newlyEarned_ContentData);
				reward_info_ContentData['newlyEarnedRewardsLine']=newlyEarned; 
			}
			var rewards_info = getPageContent("cardHome", "rewards_info",responseData["cardType"],responseData["optionCode"],responseData["incentiveTypeCode"],responseData["incentiveCode"]);
			reward_info_ContentData['earnRewardAmount'] = numberWithCommas(responseData["earnRewardAmount"]);
			rewards_info = parseContent(rewards_info, reward_info_ContentData);

			var rewards_offer = getPageContent("cardHome", "rewards_offer",responseData["cardType"],responseData["optionCode"],responseData["incentiveTypeCode"],responseData["incentiveCode"]);
			var cardhomeList = $('#cardHome_List'); 
			
			cardhomeList.append(rewards_info);
			cardhomeList.append(rewards_offer);
			}
		}else
		{     
			var errorMessage = errorCodeMap['acLiteOutageMode'];
			$(".error-box").css('display','block');
			$(".errormsg").css('display','block');
			$("#errorCardHomeDiv").html(errorMessage);
		}

		// setting global values
		if(!isEmpty(responseData["earnRewardAmount"])){
			globalEarnRewardAmount=numberWithCommas(responseData["earnRewardAmount"]);
		}
		globalLastFourAcctNbr=responseData["lastFourAcctNbr"];
		globalIncentiveTypeCode=responseData["incentiveTypeCode"];
	}catch(err)
	{
		showSysException(err)
	}
}


/*********************Card Home Page End******************************/


/*********************** Account Summary Page Start*************************/

function accountSummaryLoad()
{	try{
	dfs.crd.achome.populateAccountSummary('ACHOME');
}catch(err)
{
	showSysException(err)
}
}

dfs.crd.achome.populateAccountSummary = function(pageName)
{	try{
	var achome = dfs.crd.achome.getACHomeData(pageName);
	if (!jQuery.isEmptyObject(achome)){
		dfs.crd.achome.populateAccountSummaryPageDivs(achome, pageName);
	}
}catch(err)
{
	showSysException(err)
}
}

/*
 *Description: 
 *Populates the page divs for the Card Home page.
 *Date compared for infantDate "00000000"
 *Depending upon card type ,incentiveTypeCode ,incentiveCode the dynamic
 * part will be decided.
 */
dfs.crd.achome.populateAccountSummaryPageDivs = function(responseData, pageId) 
{     
	try{
		var defaultVal = "$0.00";
		if(!isEmpty(responseData["statementBalance"])){
			if((responseData["statementBalance"]) < 0){
					$("#accountSummary_statementBalance").html("-$"+ splitNegativeBalance(responseData["statementBalance"]));
					}
			else{
			$("#accountSummary_statementBalance").html("$"+ numberWithCommas(responseData["statementBalance"]));
			}
		}else{
			$("#accountSummary_statementBalance").html(defaultVal);
		}
		if(!isEmpty(responseData["currentBalance"])){
			if((responseData["currentBalance"]) < 0){
					$("#accountSummary_currentBalance").html("-$"+ splitNegativeBalance(responseData["currentBalance"]));
					}
			else{
			$("#accountSummary_currentBalance").html("$"+ numberWithCommas(responseData["currentBalance"]));
			}
		}else{
			$("#accountSummary_currentBalance").html(defaultVal);
		}
		if(!(responseData["currentBalance"]>0)){
			deactiveBtn("accSumm_makePayBtn");
		}
		
		if (typeof responseData["paymentDueDate"] != undefined && responseData["paymentDueDate"] != null
				&& responseData["paymentDueDate"] != '00000000') 
		{
			var dueDate = formatDate(responseData["paymentDueDate"]);
			var payDueDate = convertDate(dueDate);
			$("#accountSummary_minpay").append( payDueDate);			
			if(responseData["incentiveTypeCode"] == 'NOR' || responseData["cardType"] != "000001" ||  responseData["incentiveTypeCode"] == 'SBC'){
				$('#accountSummary_rewardsDiv').find('li:eq(2) a').html("");
			}			
		}    
		if(!isEmpty(responseData["minimumPaymentDue"])){
			$("#accountSummary_minimumPaymentDue").html( "$"+ numberWithCommas(responseData["minimumPaymentDue"]));
		}else{
			$("#accountSummary_minimumPaymentDue").html(defaultVal);			
		}
		if (!isEmpty(responseData["lastPaymentDate"]) &&  responseData["lastPaymentAmount"] > 0) 
		{     var lastPayDate = formatDate(responseData["lastPaymentDate"]);
			  var lastpaymentDate = calculateDuration(lastPayDate);
		if(lastpaymentDate > 210)
		{     
			$("#lastpaymentLine").css("display", "none");
		}else
		{
			$("#lastpaymentLine").css("display", "block");
			var lastDate = convertDate(lastPayDate);
			$("#accountSummary_lastpay").html( "Last Payment "+ lastDate);
			if(!isEmpty(responseData["lastPaymentAmount"])){
				$("#accountSummary_lastPaymentAmount").html( "$"+ numberWithCommas(responseData["lastPaymentAmount"]));
			}else{
				$("#accountSummary_lastPaymentAmount").html(defaultVal);
			}
		}
		}else 
		{     
			$("#lastpaymentLine").css("display", "none");
		} 
		if(!isEmpty(responseData["availableCredit"])){
			if((responseData["availableCredit"]) < 0){
				$("#accountSummary_availableCredit").html("-$"+ splitNegativeBalance(responseData["availableCredit"]));
				}
			else{
			$("#accountSummary_availableCredit").html( "$"+ numberWithCommas(responseData["availableCredit"]));
				}	
		}else{
			$("#accountSummary_availableCredit").html(defaultVal);			
		}

		if (!(isACLite(applicationMode)  || isRewardOutage(applicationMode))){
			if ((accountEarnsCBB(incentiveTypeCode)) || (accountEarnsMiles(incentiveTypeCode))) 
			{     
				var rewards_info = getPageContent("cardHome", "account_info",responseData["cardType"],responseData["optionCode"],responseData["incentiveTypeCode"],responseData["incentiveCode"]);
				var reward_info_ContentData = [];
				reward_info_ContentData['earnRewardAmount'] = numberWithCommas(responseData["earnRewardAmount"]);
				rewards_info = parseContent(rewards_info, reward_info_ContentData);
				$("#accountSummary_rewardsDiv").append( rewards_info);
			}
		}
	}catch(err)
	{
		showSysException(err)
	}

}

/*********************** Account Summary Page End**************************/

dfs.crd.achome.creditLineLearnMoreLoad = function() 
{	
}

dfs.crd.achome.creditLineLearnMoreLoad = function(){}

// TODO - Move this
$(".ssoToggle .navtabsStyle").live("click", function() {
	var clickedTab = $(this);
	if (!clickedTab.hasClass("selectedTab")) {
		clickedTab.parent().find(".selectedTab").removeClass("selectedTab");
		clickedTab.addClass("selectedTab");

		if (clickedTab.hasClass("bankTab")) {
			showSpinner();
			window.setTimeout(function() {
				try
				{
					killDataFromCache('SSO_DATA');
					var cardHome = dfs.crd.achome.getACHomeData('SSO_DATA');
					if (dfs.crd.achome.shouldShowSSOToggle(cardHome)){
						dfs.crd.lilo.goToBankSSO(cardHome.payLoadSSOText, cardHome.isSSNMatched);
					} else {
						hideSpinner();
					}
				}
				catch(err)
				{
					hideSpinner();
					showSysException(err);
				}		
			}, 100);
		}
	}
});