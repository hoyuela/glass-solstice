/** Name Space* */
dfs.crd.sct = dfs.crd.sct || {};
/** * */
/***************Tagging Spec - Discover Card Mobile - Start*******************/
//Links to Download Apps

dfs.crd.sct.trackVersionUpgrade = function() {
	try{
	var downLoadType = getClientPlat();
	s.linkTrackVars = s.linkTrackVars + ',prop1';
	s.prop1 = 'DiscoverCard:Mobile:'+downLoadType+'';
	s.tl(this, 'o', 'App Download');
	s.manageVars("clearVars");
	 }catch(err){
	        //showSysException(err);
	    }
}

//Mobile Payment Start
function paymentStep1PostSC() {
	try{
	s.events = "event62";
	s.eVar5 = "DiscoverCard:Payments";
	 }catch(err){
	        //showSysException(err);
	    }
}

//Mobile Payment Complete
function paymentStep3PostSC() {
	try{
	var paymentStep3Data = getDataFromCache("MAKEPAYMENTTHREE");
	s.events="event17,event18"; 
	s.products=";;;;event17="+paymentStep3Data.paymentAmount+""; 
	s.eVar19=""+paymentStep3Data.confirmationNumber+"";
	s.eVar20="<DaysOut>";
	s.eVar21="<PaymentMethod>"; 
	s.eVar5="DiscoverCard:Payments:<PaymentType>"; 
	s.eVar45="<NumberAccountsUsed>";
	 }catch(err){
	        //showSysException(err);
	    }
}

//Payment Cancellation Page - Complete
function confirmCancelPaymentSCVariables(){
	try{
	s.eVar5="DiscoverCard:Payments:CancelPayment";
	 }catch(err){
	        //showSysException(err);
	    }
}

//Mobile Rewards Home Page

function redemptionLandingSCVariables(){
	try{
	s.events="event3"; 
	s.products="CashbackBonus;CBB:eCertificate,CashbackBonus;CBB:StatementCredit,CashbackBonus;CBB:DirectDeposit";
	 }catch(err){
	        //showSysException(err);
	    }
}


//function browse_all_ecertPostSC(){
//	s.events="event3"; 
//	s.products="CashbackBonus;CBB:<ProductID>";
//}

//Mobile Rewards - Statement Credit Page
function statementCredit1PostSC(){
	try{
	s.events="event3"; 
	s.products="CashbackBonus;CBB:CRD1";
	 }catch(err){
	        //showSysException(err);
	    }
}

//Mobile Rewards - Direct Deposit Page
function directDeposit1PostSC(){
	try{
	s.events="event3"; 
	s.products="CashbackBonus;CBB:EFT1";
	 }catch(err){
	        //showSysException(err);
	    }
}
//Mobile Rewards - Browse eCertificate Partner
function redeemPartner1PostSC(){	
	try{
	var partnerDetails=getDataFromCache("REDEEMPARTNERDATA");
	s.events="event3";
	if(!isEmpty(partnerDetails)){
	s.products="CashbackBonus;CBB:" + partnerDetails.modeCode;
	}
	 }catch(err){
	        //showSysException(err);
	    }
}

//Mobile Rewards - Complete Order - Cash Paid
function redeemPartnerECT3PostSC(){
	try{
	var merchantDetailsToPOST=getDataFromCache('REDEEMSELCETED');
	var updatedPostDetails=merchantDetailsToPOST.updatedPostDetails;
	s.events="purchase,event53";
	if(!isEmpty(updatedPostDetails)){
	s.products="CashbackBonus;CBB:" + updatedPostDetails.modeCode +";" + updatedPostDetails.orderQty +";" + updatedPostDetails.redemptionAmt;
	event53=updatedPostDetails.redeemedAmt; 
	s.purchaseID=updatedPostDetails.orderId;
	}
	 }catch(err){
	        //showSysException(err);
	    }
}

//Mobile Rewards - Enroll in Cashback Programs
function cashbackBonusSignup3PostSC(){
	try{
	s.eVar5="DiscoverCard:CashbackBonus:<ProgramName>";	
	 }catch(err){
	        //showSysException(err);
	    }
}

//Mobile Money Messenger - Start
function sendMoneyLandingPostSC(){
	try{
	s.events="event64";
	s.eVar5="DiscoverCard:MoneyMessenger";	
	 }catch(err){
	        //showSysException(err);
	    }
}

//Mobile Money Messenger - Complete

function sendMoney3PostSC(){
	try{
	var sendMoney3Data = getDataFromCache("SENDMONEY_SUMMARY");
	s.events="event48,event49";
	s.products=";;;;event49="+sendMoney3Data.amount+"";
	s.eVar5="DiscoverCard:MoneyMessenger";
	killDataFromCache("SENDMONEY_SUMMARY");
	 }catch(err){
	        //showSysException(err);
	    }
}

//Mobile Cash PIN Change
function personalizeCashPin1PostSC(){
	try{
	s.events="event9";
	s.eVar5="DiscoverCard:UpdateCashPIN";
	 }catch(err){
	        //showSysException(err);
	    }
}
/***************Tagging Spec - Discover Card Mobile - End*******************/

/**************Tagging Spec - Discover Card Mobile R4 - Start**************/
//Cancel Update Button
dfs.crd.sct.cancelLink = function() {
	try{
	s.linkTrackVars = s.linkTrackVars + ',prop1';
	s.prop1='DiscoverCard:Mobile:CancelUpdate';
	s.tl(this,'o','Cancel Update');
	s.manageVars("clearVars");
	}catch(err){
        //showSysException(err);
    }
}

//Cancel Forgot User ID Page
dfs.crd.sct.cancelForgotUserIdLink = function() {
	try{
	s.linkTrackVars=s.linkTrackVars+',prop1';
	 s.prop1='DiscoverCard:Mobile:CancelIDUpdate'; 
	 s.tl(this,'o','Cancel ID Update');
	 s.manageVars("clearVars");
	}catch(err){
        //showSysException(err);
    }
}

//Forgot Password Screen:  Step 1 :  Cancel
dfs.crd.sct.cancelForgotPasswordStep1Link = function() {
	try{
	s.linkTrackVars=s.linkTrackVars+',prop1';
	s.prop1='DiscoverCard:Mobile:CancelPasswordUpdateStep1'; 
	s.tl(this,'o','Cancel Password Update');
	s.manageVars("clearVars");
	}catch(err){
        //showSysException(err);
    }
}

//Forgot Password Screen:  Step 2 : Cancel
dfs.crd.sct.cancelForgotPasswordStep2Link = function() {
	try{
	s.linkTrackVars=s.linkTrackVars+',prop1';
	s.prop1='DiscoverCard:Mobile:CancelPasswordUpdateStep2'; 
	s.tl(this,'o','Cancel Password Update');
	s.manageVars("clearVars");
	}catch(err){
        //showSysException(err);
    }
}

//Forgot Password Screen:  Step 2 :  Password Strength Icon
dfs.crd.sct.passwordStrengthIconLink = function() {
	try{
	s.linkTrackVars=s.linkTrackVars+',prop1';
	s.prop1='DiscoverCard:Mobile:PasswordStrength'; 
	s.tl(this,'o','Password Strength');
	s.manageVars("clearVars");
	}catch(err){
        //showSysException(err);
    }
}

//Forgot Both User ID and Password: Step 1 : Cancel
dfs.crd.sct.cancelForgotBothStep1Link = function() {
	try{
	s.linkTrackVars=s.linkTrackVars+',prop1';
	s.prop1='DiscoverCard:Mobile:CancelIDPasswordUpdateStep1';
	s.tl(this,'o','Cancel IDPassword Update');
	s.manageVars("clearVars");
	}catch(err){
        //showSysException(err);
    }
}

//Forgot Both User ID and Password: Step 2 : Cancel
dfs.crd.sct.cancelForgotBothStep2Link = function() {
	try{
	s.linkTrackVars=s.linkTrackVars+',prop1';
	s.prop1='DiscoverCard:Mobile:CancelIDPasswordUpdateStep2'; 
	s.tl(this,'o','Cancel IDPassword Update');
	s.manageVars("clearVars");
	}catch(err){
        //showSysException(err);
    }
}

//Forgot Both User ID and Password: Step 2 : Password Strength Icon
dfs.crd.sct.forgotBothPasswordStrengthIconLink = function() {
	try{
	s.linkTrackVars=s.linkTrackVars+',prop1';
	s.prop1='DiscoverCard:Mobile:PasswordStrength';  
	s.tl(this,'o','Password Strength');
	s.manageVars("clearVars");
	}catch(err){
        //showSysException(err);
    }
}

//Forced Update Modal
function forceUpgradeSCVariables(){
	try{
	s.pageName="/mobile/app/forcedUpdate";
	}catch(err){
        //showSysException(err);
    }
}

//Optional Update Modal
function optionalUpgradeSCVariables(){
	try{
	s.pageName="/mobile/app/optionalUpdate";
	}catch(err){
        //showSysException(err);
    }
}

//Forgot User ID/Password Screen
function forgotUserIdOrPasswordMenuSCVariables(){
	try{
	s.pageName="/mobile/app/forgotLoginCredentials";
	}catch(err){
        //showSysException(err);
    }
}

//Forgot User ID Screen
function forgotUserIdSCVariables(){
	try{
	s.pageName="/mobile/app/forgotUserID";
	s.events="event22";
	s.eVar5="forgotUserID";
	}catch(err){
        //showSysException(err);
    }
}

//Submit Forgot User ID Page
function forgotUserIdConfirmationSCVariables(){
	try{
	s.eVar5="DiscoverCard:Mobile:ForgotUserID";
	s.events="event9";
	}catch(err){
        //showSysException(err);
    }
}

//Forgot Password Screen: Step 1
function forgotPasswordStep1SCVariables(){
	try{
	s.pageName="/mobile/app/forgotPassword/Step1";
	s.events="event22";
	s.eVar5="forgotPassword";
	}catch(err){
        //showSysException(err);
    }
}

//Forgot Password Screen: Step 2
function forgotPasswordStep2SCVariables(){
	try{
	s.pageName="/mobile/app/forgotPassword/Step2";
	}catch(err){
        //showSysException(err);
    }
}

//Forgot Password Screen: Step 2 :  Submit
function forgotPasswordConfirmationSCVariables(){
	try{
	s.eVar5="DiscoverCard:Mobile:ForgotPassword";
	}catch(err){
        //showSysException(err);
    }
}

//Forgot Both User ID and Password: Step 1
function forgotBothStep1SCVariables(){
	try{
	s.pageName="/mobile/app/forgotIDPassword/Step1";
	s.events="event22";
	s.eVar5="forgotIDPassword";
	}catch(err){
        //showSysException(err);
    }
}

//Forgot Both User ID and Password: Step 2
function forgotBothStep2SCVariables(){
	try{
	s.pageName="/mobile/app/forgotIDPassword/Step2";
	}catch(err){
        //showSysException(err);
    }
}

//Forgot Both User ID and Password: Step 2 : Submit
function forgotBothConfirmationSCVariables(){
	try{
	s.eVar5="DiscoverCard:Mobile:ForgotIDPassword";
	s.events="event9";
	}catch(err){
        //showSysException(err);
    }
}

//Search Transactions 
function searchTransSCVariables(){
	try{
	s.eVar3=s.prop3="CardMobile:TransactionSearch";
	s.eVar27=s.prop27="TransactionSearch";
	s.event="event21";
	}catch(err){
        //showSysException(err);
    }
}
//Search Transactions - use filters
function searchTransFiltersSCVariables(dateOption,amountOption,catgOption){
	try{
	s.eVar53='Category:'+catgOption+'|Time:'+dateOption+'|Amount:'+amountOption+'';
	}catch(err){
        //showSysException(err);
    }
}
/*******************Tagging Spec - Discover Card Mobile R4 - End****************/



/*Tagging Spec - Push Notifications - Handset & Tablet*/
//Track count of clicks on the [!] button
dfs.crd.sct.pushAlertNotification = function()
{
	try{
		if ( isEmpty(s.pageName) && (!isEmpty(toPageName))){
			s.pageName=toPageName;
			}
		s.linkTrackVars=s.linkTrackVars+',prop1';
		s.prop1=s.pageName+':AlertsButtonClick';
		s.tl(this,'o','Alerts Button Click');
		s.manageVars("clearVars");
	}catch(err){
        //showSysException(err);
    }	
}

//New Notifications Exist
function newNotificationExist()
{
	try{
	if ( isEmpty(s.pageName) && (!isEmpty(toPageName))){
			s.pageName=toPageName;
			}
	s.prop1=s.pageName+':NewNotificationsExist';
	}catch(err){
        //showSysException(err);
    }	
}

//All Notifications Read
function allNotificationRead()
{
try{
	if ( isEmpty(s.pageName) && (!isEmpty(toPageName))){
			s.pageName=toPageName;
			}
	s.prop1=s.pageName+':AllNotificationsRead';	
	}catch(err){
        //showSysException(err);
    }
}

//Track clicks and from what page for this global (i) FAQ button
dfs.crd.sct.globalFaqLinks = function()
{
	try{
	if ( isEmpty(s.pageName) && (!isEmpty(toPageName))){
			s.pageName=toPageName;
			}
	s.linkTrackVars=s.linkTrackVars+',prop1';
	s.prop1=s.pageName+':GlobalFAQClick'; 
	s.tl(this,'o','Global FAQ Button Click');
	s.manageVars("clearVars");
	}catch(err){
        //showSysException(err);
    }
}

//Enable Tracking Notifications Click
dfs.crd.sct.trackNotificationClick = function()
{
	try{
	if ( isEmpty(s.pageName) && (!isEmpty(toPageName))){
			s.pageName=toPageName;
			}
	s.linkTrackVars=s.linkTrackVars+',prop1';
	s.prop1=s.pageName+':EnableTrackingNotifications'; 
	s.tl(this,'o','Enable Tracking Notifications Click');
	s.manageVars("clearVars");
	}catch(err){
        //showSysException(err);
    }
}

//Disable Tracking Notifications Click
dfs.crd.sct.disableNotificationClick = function()
{
	try{
	if ( isEmpty(s.pageName) && (!isEmpty(toPageName))){
			s.pageName=toPageName;
			}
	s.linkTrackVars=s.linkTrackVars+',prop1';
	s.prop1=s.pageName+':DisableTrackingNotifications'; 
	s.tl(this,'o','Disable Tracking Notifications Click');
	s.manageVars("clearVars");
	}catch(err){
        //showSysException(err);
    }
}

//Text Messages Terms & Conditions
dfs.crd.sct.textTermsNConditions = function()
{
	try{
	if ( isEmpty(s.pageName) && (!isEmpty(toPageName))){
			s.pageName=toPageName;
			}
	s.linkTrackVars=s.linkTrackVars+',prop1';
	s.prop1=s.pageName+':Text Messages Terms and Conditions';
	s.tl(this,'o','Text Messages Terms and Conditions Click');
	s.manageVars("clearVars");
	}catch(err){
        //showSysException(err);
    }
}

//Device Alerts Terms & Conditions
dfs.crd.sct.deviceTermsNConditions = function()
{
	try{
	if ( isEmpty(s.pageName) && (!isEmpty(toPageName))){
			s.pageName=toPageName;
			}
	s.linkTrackVars=s.linkTrackVars+',prop1';
	s.prop1=s.pageName+':Device Terms and Conditions';
	s.tl(this,'o','Device Terms and Conditions Click');
	s.manageVars("clearVars");
	}catch(err){
        //showSysException(err);
    }
}

//Track click of checkbox for Text Alerts and all category names
dfs.crd.sct.trackTxtCheckboxClicks = function(alertName)
{
	try{	
	if ( isEmpty(s.pageName) && (!isEmpty(toPageName))){
			s.pageName=toPageName;
			}	
	s.linkTrackVars=s.linkTrackVars+',prop1';
	s.prop1=s.pageName+':TextAlerts+:'+alertName+' Click';
	s.tl(this,'o','Text<Checkbox> Click');
	s.manageVars("clearVars");
	}catch(err){
        //showSysException(err);
    }
}
//Track click of checkbox for Device Alerts and all category names
dfs.crd.sct.trackDeviceCheckboxClicks = function(alertName)
{
	try{
	if ( isEmpty(s.pageName) && (!isEmpty(toPageName))){
			s.pageName=toPageName;
			}
	s.linkTrackVars=s.linkTrackVars+',prop1';
	s.prop1=s.pageName+':DeviceAlerts+:'+alertName+' Click';
	s.tl(this,'o','Device<Checkbox> Click');
	s.manageVars("clearVars");
	}catch(err){
        //showSysException(err);
    }
}
//Track instance of collapsing of menu items on Device Alerts History Page
dfs.crd.sct.trackMenuCollapseClick = function(TabID)
{
	try{
	if ( isEmpty(s.pageName) && (!isEmpty(toPageName))){
			s.pageName=toPageName;
			}
	s.linkTrackVars=s.linkTrackVars+',prop40';
	s.prop40=s.pageName+':'+TabID+'';
	s.tl(this,'o','Tab Click');
	s.manageVars("clearVars");
	}catch(err){
        //showSysException(err);
    }
}

//inline error tracking - Manage Text And Device Alerts Page
function trackInlineError()
{
	try{
	s.prop10="ManageTXTAlerts|ProcessNotComplete";		
	}catch(err){
        //showSysException(err);
    }
}

//Track instance of Expanding/collapsing of Description on Manage Text And Device Alerts Page
dfs.crd.sct.trackDescriptionCollapseClick = function(tabID)
{
	try{
	if ( isEmpty(s.pageName) && (!isEmpty(toPageName))){
			s.pageName=toPageName;
			}
	s.linkTrackVars=s.linkTrackVars+',prop40';
	s.prop40=s.pageName+':'+tabID+'';
	s.tl(this,'o','Tab Click');
	s.manageVars("clearVars");
	}catch(err){
        //showSysException(err);
    }
}

//inline error tracking - Device Alerts History - No New Alerts
function trackNoNewAlerts()
{
	try{
	s.prop10="DeviceAlertsHistory|NoNewAlerts";
	}catch(err){
        //showSysException(err);
    }
}

//inline error tracking - Device Alerts History - Alerts Not Set Up
function trackNoAlertsSetError()
{
	try{
	s.prop10="DeviceAlertsHistory|AlertsNotSetUp";
	}catch(err){
        //showSysException(err);
    }
}

//inline error tracking - Device Alerts History - Changes Not Saved
function trackchangesNotSavedError()
{
	try{
	s.prop10="DeviceAlertsHistory|ChangesNotSaved";
	}catch(err){
        //showSysException(err);
    }
}
//inline error tracking - Device Alerts History - Message Deleted
function trackmsgDeletedError()
{
  try{
  s.prop10="DeviceAlertsHistory|MessageDeleted";
  }catch(err){
        //showSysException(err);
    }
}


/*****Tagging Spec - Discover Card Mobile R5 - Handset & Tablet - Start****/
//Refer a Friend - Confirmation Overlay (Track by Type)
dfs.crd.sct.rafConfirmationOverlay = function(type) {
	try{
		if ( isEmpty(s.pageName) && (!isEmpty(toPageName))){
			s.pageName=toPageName;
			}
		s.linkTrackVars=s.linkTrackVars+',prop1';
		s.prop1=s.pageName+':ReferAFriendConfirmation:'+type; 
		s.tl(this,'o','ReferAFriend:Confirmation');
		s.manageVars("clearVars");
	 }catch(err){
	        //showSysException(err);
	    }
}

//Help with Redemption (Question Button) Click
dfs.crd.sct.redemptionHelpQuestionIcon = function() {
	try{
		if ( isEmpty(s.pageName) && (!isEmpty(toPageName))){
			s.pageName=toPageName;
			}
		s.linkTrackVars=s.linkTrackVars+',prop1';
		s.prop1=s.pageName+':MobileRedemptionHelp'; 
		s.tl(this,'o','MobileRedemption:Help');
		s.manageVars("clearVars");
	 }catch(err){
	        //showSysException(err);
	    }
}

//Cashback Bonus FAQ - Expand/Contract Menu Items
dfs.crd.sct.cashbackBonusFAQ = function(cbbfaqnum) {
	try{
	if ( isEmpty(s.pageName) && (!isEmpty(toPageName))){
			s.pageName=toPageName;
			}
     // s.prop40='';
	  s.linkTrackVars=s.linkTrackVars+',prop40';
	  s.prop40=s.pageName+ ":" +cbbfaqnum; 
	  s.tl(this,'o','Tab Click');
	  s.manageVars("clearVars"); 
	 }catch(err){
	        //showSysException(err);
	    }
}

//Redemption - Best Value - Tab Tracking
dfs.crd.sct.redemptionBestValueTabTracking = function(a) {
	try{
	if ( isEmpty(s.pageName) && (!isEmpty(toPageName))){
	s.pageName=toPageName;
	}
    s.linkTrackVars=s.linkTrackVars+',prop40'; 
    s.prop40=s.pageName+':' + a; 
    s.tl(this,'o','Tab Click');
    s.manageVars("clearVars"); 
	 }catch(err){
	        //showSysException(err);
	    }
}

//Redemption - Step 1 (All Types)
function redeemPartner1SCVariables(){	
	try{
			var selectedECTPartnerForRedemption=getDataFromCache("REDEEMSELCETED");
			s.events="scAdd,scOpen"; 
			if(!isEmpty(selectedECTPartnerForRedemption)){
			s.products="CashbackBonus;CBB:"+selectedECTPartnerForRedemption.modeCode +"," +selectedECTPartnerForRedemption.selectedQuantity +","+selectedECTPartnerForRedemption.selectedModeAmount;
			}
			}catch(err){
	        //showSysException(err);
	    }
}

//Redemption - Verify Page (All Types)
function redeemPartnerECT2PostSC(){	
	try{
			var selectedECTPartnerForRedemption=getDataFromCache("REDEEMSELCETED");
			s.events="scCheckout"; 
			if(!isEmpty(selectedECTPartnerForRedemption)){
			s.products="CashbackBonus;CBB:"+selectedECTPartnerForRedemption.modeCode +"," +selectedECTPartnerForRedemption.selectedQuantity +","+selectedECTPartnerForRedemption.selectedModeAmount;
			}
	 }catch(err){
	        //showSysException(err);
	    }
}

//Redemption - Complete (Discover Gift Card ONLY)
function giftcard3PostSC(){	
	try{
			s.events="purchase";
			var giftCardPostData = getDataFromCache("GIFTDATATOPOST");
			if(!isEmpty(giftCardPostData)){
				s.products="CashbackBonus;CBB:DGC,"+giftCardPostData.redemptionAmt;
				s.purchaseID=""+giftCardPostData.orderId+""; 
				s.eVar48=""+giftCardPostData.designCode+"";
			}
	 }catch(err){
	        //showSysException(err);
	    }
}

//Refer a Friend - Share Now Clicks
dfs.crd.sct.rafShareNow = function(SiteName) {
	try{
		if ( isEmpty(s.pageName) && (!isEmpty(toPageName))){
	s.pageName=toPageName;
	}
		s.linkTrackVars= s.linkTrackVars+',events,eVar8,prop8,prop13';
		s.events='event6'; 
		s.linkTrackEvents='event6'; 
		s.eVar8=s.prop8=''+SiteName+'';
		s.prop13=s.pageName;
		s.tl(this,'o','Refer a Friend Content Share');
		s.manageVars("clearVars"); 
	}catch(err){
        //showSysException(err);
    }
}

//Refer a Friend - Share Again Clicks
dfs.crd.sct.rafShareAgain = function(SiteName) {
	try{
	if ( isEmpty(s.pageName) && (!isEmpty(toPageName))){
	s.pageName=toPageName;
	}
		s.linkTrackVars= s.linkTrackVars+',events,eVar8,prop8,prop13';
		s.events='event6';
		s.linkTrackEvents='event6';
		s.eVar8=s.prop8=''+SiteName+'';
		s.prop13=s.pageName; 
		s.tl(this,'o','Refer a Friend Share Again');
		s.manageVars("clearVars"); 
	}catch(err){
        //showSysException(err);
    }
}

//Redemption Overview Overlay Click
dfs.crd.sct.redemptionOverviewOverlay = function() {
	try{
	if ( isEmpty(s.pageName) && (!isEmpty(toPageName))){
	s.pageName=toPageName;
	}
		s.linkTrackVars=s.linkTrackVars+',prop1';
		s.prop1=s.pageName+':MobileRedemptionOverview'; s.tl(this,'o','MobileRedemption:Overview');
		s.manageVars("clearVars"); 
	}catch(err){
        //showSysException(err);
    }
}

//Error - Partner Gift Cards/eCertificates - less than $20 available to redeem
function errorPartnerGiftCardSCVariables(){	
	try{
			s.prop10="MobileRedemption|PGC-Ecert-LessThan$20ToRedeem";
			}catch(err){
	        //showSysException(err);
	    }
}

//Error - Discover Gift Cards - less than $20 available to redeem
function errorDiscoverGiftCardSCVariables(){	
	try{
			s.prop10="MobileRedemption|DGC-LessThan$20ToRedeem";
			}catch(err){
	        //showSysException(err);
	    }
}

//Error Tracking - Gift Card - First/Last Name not Valid
function discoverGiftCardNameValidationSCVariables(){	
	try{
			s.prop10="MobileRedemption|DGC-InvalidName";
			}catch(err){
	        //showSysException(err);
	    }
}

//Error Tracking - Gift Cards - Unable to Redeem (Regulation)
function errorGiftCardRedemptionSCVariables(){	
	try{
			s.prop10="MobileRedemption|DGC-UnableToRedeem(Regulatory)";
			}catch(err){
	        //showSysException(err);
	    }
}

//Error Tracking - Refer a Friend - Delinquent Account
function referFriendDelinquentSCVariables(){	
	try{
			s.prop10="ReferAFriend|DelinquentAccount";
			}catch(err){
	        //showSysException(err);
	    }
}
//Redemption - Browse Partner
function redemptionLandingPostSC(){	
	try{
		s.events="event3";	
	    allPartersData = getDataFromCache("ALLPARTNERS");
	    var allPartnersLength = allPartersData.partners.length;
		var lowerOffset = 0;
		var productValue="";
		if(!isEmpty(allPartersData)){
			for(var key in allPartersData.partners){
				if(lowerOffset < allPartnersLength){
			var productId = allPartersData.partners[lowerOffset].modeCode;
			productValue += "CashBackBonus;CBB:"+productId+",";	
			s.products = productValue;
			lowerOffset++;			
				}
				}			
	}
		}catch(err){
        //showSysException(err);
    }
}
/*****Tagging Spec - Discover Card Mobile R5 - Handset & Tablet - End****/

/***********************EDO Tagging Spec - Mobile - Start*******************/
//Tab Switching between Extras & History
dfs.crd.sct.extrasHistoryTabSwitch = function(tabName) {
	try{
	if ( isEmpty(s.pageName) && (!isEmpty(toPageName))){
	s.pageName=toPageName;
	}
		s.linkTrackVars=s.linkTrackVars+',prop40';
		s.prop40=s.pageName+':'+tabName+'';
		s.tl(this,'o','Tab Click');
		s.manageVars("clearVars"); 
	}catch(err){
        //showSysException(err);
    }
}

//All Sort Functionality
dfs.crd.sct.extrasSortFunctionality = function(SortType) {
	try{
	if ( isEmpty(s.pageName) && (!isEmpty(toPageName))){
	s.pageName=toPageName;
	}
		s.linkTrackVars=s.linkTrackVars+',prop1';
		s.prop1=s.pageName+':'+SortType+'';
		s.tl(this,'o','Discover Extras Sort:'+SortType+'');
		s.manageVars("clearVars"); 
	}catch(err){
        //showSysException(err);
    }
}

//Select Grid View Vs. List View
dfs.crd.sct.extrasGridListSwitch = function(TabName) {
	try{
	if ( isEmpty(s.pageName) && (!isEmpty(toPageName))){
	s.pageName=toPageName;
	}
		s.linkTrackVars=s.linkTrackVars+',prop40'; 
		s.prop40=s.pageName+':'+TabName+''; 
		s.tl(this,'o','Tab Click');
		s.manageVars("clearVars"); 
	}catch(err){
        //showSysException(err);
    }
}

//"No Offers Available" Page
function noOfferSCVariables(){
	try{
		s.pageName='/EDO/NoOffersAvailable';	
 }catch(err){
        //showSysException(err);
    }
}

//Technical Difficulties Page
function techIssueSCVariables(){
	try{
		s.pageName='/EDO/TechnicalDifficulties'	
 }catch(err){
        //showSysException(err);
    }
}

//View Offer
function edoDetailPostSC(){
	try{
		s.events="event3"; 
		var edoDetails = getDataFromCache('EDO');
		if (!jQuery.isEmptyObject(edoDetails)) {
			var edoDetailsCampaignId = edoDetails.extras[lowerEDOOffset].campaignId;
			if (isEmpty(edoDetailsCampaignId)) {
				edoDetailsCampaignId = "";
			}
			s.products="CashbackBonus;Extras:"+edoDetailsCampaignId+"";	
		}		
 }catch(err){
        //showSysException(err);
    }
}

//Save Offer to Photos Click
dfs.crd.sct.saveOfferToPhotos = function() {
	try{
		s.linkTrackVars+=',events,products'; 
		s.events='event';
		s.linkTrackEvents='event68';
		var edoDetails = getDataFromCache('EDO');
		if (!jQuery.isEmptyObject(edoDetails)) {
			var edoDetailsCampaignId = edoDetails.extras[lowerEDOOffset].campaignId;
			if (isEmpty(edoDetailsCampaignId)) {
				edoDetailsCampaignId = "";
			}
			s.products="CashbackBonus;Extras:"+edoDetailsCampaignId+"";	
		}
		s.tl(this,'o','CBB Extras Save Photo');
		s.manageVars("clearVars"); 
	}catch(err){
        //showSysException(err);
    }
}

//View Map Click
dfs.crd.sct.viewMap = function() {
	try{
	if ( isEmpty(s.pageName) && (!isEmpty(toPageName))){
	s.pageName=toPageName;
	}
		s.linkTrackVars=s.linkTrackVars+',prop1';
		s.prop1=s.pageName+':ViewMap';
		s.tl(this,'o','CBB Extras Mobile View Map Click');
		s.manageVars("clearVars"); 
	}catch(err){
        //showSysException(err);
    }
}
//Add to Calendar Click
dfs.crd.sct.addToCalendar = function() {
	try{
	if ( isEmpty(s.pageName) && (!isEmpty(toPageName))){
	s.pageName=toPageName;
	}
		s.linkTrackVars=s.linkTrackVars+',prop1';
		s.prop1=s.pageName+':AddToCalendar'; 
		s.tl(this,'o','CBB Extras Mobile Add To Calendar Click');
		s.manageVars("clearVars"); 
	}catch(err){
        //showSysException(err);
    }
}
/************************EDO Tagging Spec - Mobile  - End**************************/
/************************Tagging Spec - 13.4 - Start**************************/

//Add btn clicked on "No bank accounts setup" page
dfs.crd.sct.noAccountsSetupAddBtn = function() {
try{
	s.linkTrackVars=s.linkTrackVars+',prop1';
	s.prop1='MBA_NO_ACCT_SETUP_ADD_ACCOUNT_BTN';
	s.tl(this,'o','Manage Bank Account - NoAcSetUp-Add Account Btn');
	s.manageVars("clearVars"); 
	}catch(err){
        //showSysException(err);
    }
}

//"Features Unavailable" page
function featuresUnavailableSCVariables(){
try{
	s.pageName="/ManageBankAccount/FeaturesUnavailable";
	}catch(err){
        //showSysException(err);
    }
}

//Inline error tracking page
function  inlineErrorTrackingSCVariables(isRoutingInlineErr,isAccNumbrInlineErr){
try{
	var inlineErrMsg = "";
	if(isRoutingInlineErr && isAccNumbrInlineErr)
		inlineErrMsg = "Bank Routing Number must be 9 digits. , Bank Account Number and Confirm Bank Account didn't match.";
	else if(isRoutingInlineErr)
		inlineErrMsg = "Bank Routing Number must be 9 digits.";
	else if(isAccNumbrInlineErr)
		inlineErrMsg = "Bank Account Number and Confirm Bank Account didn't match.";
	s.prop10=inlineErrMsg;
	s.tl(this,'o','Inline Errors');
	s.manageVars("clearVars"); 
	}catch(err){
        //showSysException(err);
    }
}

//Manage Payments btn clicked on "Features Unavailable" page
dfs.crd.sct.managePaymentsBtn = function() {
try{
	s.linkTrackVars=s.linkTrackVars+',prop1';
	s.prop1='MBA_FEATURES_UNAVAIL_MANAGE_PAYMENTS_BTN';
	s.tl(this,'o','Manage Bank Account - FeatureUnavailable-ManagePayments Btn');
	s.manageVars("clearVars"); 
	}catch(err){
        //showSysException(err);
    }
}

//We're sorry" page when maximum number of banks have been added
function maximumBanksReachedSCVariables(){
try{
	s.pageName="/ManageBankAccount/Accounts/Sorry";
	}catch(err){
        //showSysException(err);
    }
}

//Manage Bank Account btn clicked on "We're sorry" page when maximum number of banks have been added
dfs.crd.sct.manageBankAccountBtn = function() {
try{
	s.linkTrackVars=s.linkTrackVars+',prop1';
	s.prop1='MBA_WERE_SORRY_MBA_BANK_ACCOUNTS_BTN';
	s.tl(this,'o','Manage Bank Account - Sorry_MBA Bank Accounts Btn');
	s.manageVars("clearVars"); 
	}catch(err){
        //showSysException(err);
    }
}

//Add bank account btn clicked
dfs.crd.sct.addBankAccount = function(badAccountsAlertValue) {
try{
	s.linkTrackVars=s.linkTrackVars+',prop1';
	if(badAccountsAlertValue == "few" || badAccountsAlertValue == "all"){ //Global alert in case of bad accounts
		s.prop1='MBA_GLOBAL_ALERT_ADD_ACCOUNT_BTN';
		s.tl(this,'o','Manage Bank Account - Global Alert Add Account Btn');
	}else{
		s.prop1='MBA_ACCOUNTS_ADD_ACCOUNT_BTN';
		s.tl(this,'o','Manage Bank Account - Accounts_Add Account Btn');
	}
	s.manageVars("clearVars");
	}catch(err){
        //showSysException(err);
    }
}

//Confirm page - Add bank account btn clicked
dfs.crd.sct.confirmAddBankAccountBtn = function(accountType) {
try{
	s.linkTrackVars=s.linkTrackVars+',prop1';
	s.eVar43=accountType;
	s.prop1='MBA_STEP1_ADD_ACCOUNT_BTN';
	s.tl(this,'o','Manage Bank Account - Step1 Add Account Btn');
	s.manageVars("clearVars"); 
	}catch(err){
        //showSysException(err);
    }
}

//Add bank account btn clicked on confirmation page after a bank is added
dfs.crd.sct.addBtnOnAddBankAccountConfirmationPage = function() {
try{
	s.linkTrackVars=s.linkTrackVars+',prop1';
	s.prop1='MBA_STEP3_ADD_ACCOUNT_BTN';
	s.tl(this,'o','Manage Bank Account - Step3 Add Account Btn');
	s.manageVars("clearVars"); 
	}catch(err){
        //showSysException(err);
    }
}

//Add bank account btn clicked on confirmation page after a bank is updated
dfs.crd.sct.addBtnOnEditBankAccountConfirmationPage = function() {
try{
	s.linkTrackVars=s.linkTrackVars+',prop1';
	s.prop1='MBA_EDIT_ACCT_CONFIRM_ADD_ACCOUNT_BTN';
	s.tl(this,'o','Manage Bank Account - Edit_Ac - Confirm Account Btn');
	s.manageVars("clearVars"); 
	}catch(err){
        //showSysException(err);
    }
}

//Edit bank account btn clicked on details page
dfs.crd.sct.editBankAccount = function() {
try{
	s.linkTrackVars=s.linkTrackVars+',prop1';
	s.prop1='MBA_ACCOUNT_DETAILS_EDIT_BTN';
	s.tl(this,'o','Manage Bank Account - Account Details-Edit Btn');
	s.manageVars("clearVars"); 
	}catch(err){
        //showSysException(err);
    }
}

//Edit bank account btn clicked on update page
dfs.crd.sct.updateEditedBankAccountDetails = function() {
try{
	s.linkTrackVars=s.linkTrackVars+',prop1';
	s.prop1='MBA_EDIT_ACCT_UPDATE_ACCOUNT_BTN';
	s.tl(this,'o','Manage Bank Account - Edit_Ac - Update Account Btn');
	s.manageVars("clearVars"); 
	}catch(err){
        //showSysException(err);
    }
}

//Remove bank account btn clicked on details page
dfs.crd.sct.removeBankAccount = function() {
try{
	s.linkTrackVars=s.linkTrackVars+',prop1';
	s.prop1='MBA_ACCOUNT_DETAILS_REMOVE_ACCOUNT_TXT';
	s.tl(this,'o','Manage Bank Account - AcDetails-Remove Account Txt');
	s.manageVars("clearVars"); 
	}catch(err){
        //showSysException(err);
    }
}

//Remove bank account btn clicked on removeAccountConfirmPending page
dfs.crd.sct.removeBtnOnRemoveAccountConfirmPendingPage = function() {
try{
	s.linkTrackVars=s.linkTrackVars+',prop1';
	s.prop1='MBA_PEND_PYMTS_REMOVE_ACCOUNT_BTN';
	s.tl(this,'o','Manage Bank Account - Pend_Payments-Remove Ac Btn');
	s.manageVars("clearVars"); 
	}catch(err){
        //showSysException(err);
    }
}

//Cancel btn clicked on removeAccountConfirmPending page
dfs.crd.sct.cancelOnRemoveAccountConfirmPendingPage = function() {
try{
	s.linkTrackVars=s.linkTrackVars+',prop1';
	s.prop1='MBA_PEND_PYMTS_DO_NOT_REMOVE_ACCOUNT_TXT';
	s.tl(this,'o','Manage Bank Account - Pend_Payments-Do Not Remove Ac Txt');
	s.manageVars("clearVars"); 
	}catch(err){
        //showSysException(err);
    }
}

//Remove bank account btn clicked on removeAccountConfirmNoPending page
dfs.crd.sct.removeBtnOnRemoveAccountConfirmNoPendingPage = function() {
try{
	s.linkTrackVars=s.linkTrackVars+',prop1';
	s.prop1='MBA_NO_PEND_PYMTS_REMOVE_ACCOUNT_BTN';
	s.tl(this,'o','Manage Bank Account - NoPend_Payments-Remove Ac Btn');
	s.manageVars("clearVars"); 
	}catch(err){
        //showSysException(err);
    }
}

//Cancel btn clicked on removeAccountConfirmNoPending page
dfs.crd.sct.cancelOnRemoveAccountConfirmNoPendingPage = function() {
try{
	s.linkTrackVars=s.linkTrackVars+',prop1';
	s.prop1='MBA_NO_PEND_PYMTS_DO_NOT_REMOVE_ACCOUNT_TXT';
	s.tl(this,'o','Manage Bank Account - NoPend_Payments-Do Not Remove Ac Txt');
	s.manageVars("clearVars"); 
	}catch(err){
        //showSysException(err);
    }
}

//Add bank account btn clicked on confirmation page after a bank is removed
dfs.crd.sct.addBtnOnRemoveBankAccountConfirmationPage = function() {
try{
	s.linkTrackVars=s.linkTrackVars+',prop1';
	s.prop1='MBA_REMOVE_CONFIRM_ADD_ACCOUNT_BTN';
	s.tl(this,'o','Manage Bank Account - Removal Confirmed - Add Account Btn');
	s.manageVars("clearVars"); 
	}catch(err){
        //showSysException(err);
    }
}

dfs.crd.sct.onClickMakePaymentPaySummBtn = function() {
 try{
	s.linkTrackVars=s.linkTrackVars+',prop1';
	s.prop1='MANAGE_PYMTS_SUMMARY_MAP_BTN';
	s.tl(this,'o','Manage Payments - Summary Map Btn');	 
 }catch(err){
     //showSysException(err);
 }
}

dfs.crd.sct.onClickEligibleForEditBtn = function() {
	 try{
		 s.linkTrackVars=s.linkTrackVars+',prop1';
		 s.prop1='MANAGE_PYMTS_EDIT_EDIT_BTN';
		 s.tl(this,'o','Manage Payments - Edit Btn');
	 }catch(err){
	     //showSysException(err);
	 }
	}

dfs.crd.sct.onClickEligibleForEditCancelBtn = function() {
	 try{
		 s.linkTrackVars=s.linkTrackVars+',prop1';
		 s.prop1='MANAGE_PYMTS_EDIT_CANCEL_PYMT_TXT';
		 s.tl(this,'o','Manage Payments - Edit-Cancel Pymnt txt');

	 }catch(err){
	     //showSysException(err);
	 }
	}

dfs.crd.sct.onClickEligibleForEditVerifyConfmBtn = function() {
	 try{
		 s.linkTrackVars=s.linkTrackVars+',prop1';
		 s.prop1='MANAGE_PYMTS_VERIFY_CONFIRM_BTN';
		 s.tl(this,'o','Manage Payments - Verify Confirm Btn');
	 }catch(err){
	     //showSysException(err);
	 }
	}

dfs.crd.sct.onClickEditVerifyConfmManagePayBtn = function() {
	 try{
		 s.linkTrackVars=s.linkTrackVars+',prop1';
		 s.prop1='MANAGE_PYMTS_CONFIRM_MANAGE_PYMTS_BTN';
		 s.tl(this,'o','Manage Payments - Confirm Manage Pymnt Btn');
	 }catch(err){
	     //showSysException(err);
	 }
	}

dfs.crd.sct.onClickEligibleForEditCancelConfmBtn = function() {
	 try{
		s.linkTrackVars=s.linkTrackVars+',prop1';
		 s.prop1='MANAGE_PYMTS_VERIFY_CANCEL_PYMT_BTN';
		 s.tl(this,'o','Manage Payments - Verify Cancel Pymnt Btn');
	 }catch(err){
	     //showSysException(err);
	 }
	}

dfs.crd.sct.onClickEligibleForEditDoNotCancelConfmBtn = function() {
	 try{
		 s.linkTrackVars=s.linkTrackVars+',prop1';
		 s.prop1='MANAGE_PYMTS_VERIFY_DO_NOT_CANCEL_TXT';
		 s.tl(this,'o','Manage Payments - Verify Do Not Cancel Txt');
	 }catch(err){
	     //showSysException(err);
	 }
	}

dfs.crd.sct.onClickEligibleForEditReviewEditPage = function(selectedPayMode) {
	 try{
		 s.eVar59=selectedPayMode;
	 }catch(err){
	     //showSysException(err);
	 }
}


dfs.crd.sct.onClickEligibleForEditVerifyPage = function(amountSelected) {
	 try{
		 s.eVar59=amountSelected;
	 }catch(err){
	     //showSysException(err);
	 }
}
/************************Tagging Spec - 13.4  - End**************************/