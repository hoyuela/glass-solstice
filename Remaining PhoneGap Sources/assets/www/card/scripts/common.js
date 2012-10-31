/**
 * This .js file includes the common functionality for the Application.
 * The data storage and retrieval from the global data storage array is also provided.
 * The navigation procedure for the application is also maintained in this js.
 */

/** **CONSTANTS and Global Variables*** */
var acLiteModeFlag = false;
var incentiveCode =null;
var incentiveTypeCode=null;
var optionCode =null;
var cardType =null;
var currentTab = "cardHome"; 
var globalCache = [];
var modeCodeT;
var timer=null;
var TIMEOUT_PERIOD=600000;
var cpEvent=new Object();
var globalEarnRewardAmount='0.00';
var globalLastFourAcctNbr;
var globalIncentiveTypeCode;
var applicationMode;
var getPrevPage;
var hasLogedOut=false;
var isSessionValid = false; // Push Notification var
var lastPushCountTime = 0;    // Push Notification var
var toPageName='';
var sectoken=null;
var fromPageName=''; 
var globalMinimumPaymentDue; 
var globalLastStatementBalance;
var globalCurrentBalance;
var globalPaymentDueDate;
var globalEarnRewardAmount;
var lastRestCallTime;
var slideType  = "slidec";
var deviceType=null;
var deviceVersion=null;
var vOne=null;
var dfsKey=null;
var projectBeyondCard = false;
var strongAuthLockedUnloadExecute=false;
var trafficSource; //refer a friend variable used for reporting in EDS
var locked=false;
/** **CONSTANTS and Global Variables End ** */

/** Name Space**/
var dfs = dfs || {};
dfs.crd = dfs.crd || {};
/** **/

function onBodyLoad ()
{
	try{
		document.addEventListener("deviceready",onDeviceReady,false);
	}catch(err){
//		showSysException(err);
	}
}

function onDeviceReady(){
	try{
		cordova.exec(null, null, "SplashScreen", "hide", []);
		deviceType=device.platform;
        deviceVersion=device.version;
        if (deviceType=="iPhone Simulator") deviceType="iPhone";
        if (deviceType=="iPad Simulator") deviceType="iPad";
		if (deviceType == "Android"){
            // Push Notification change Begin
			window.plugins.XtifySDK.start(ReceivedPushNotification, function (error) {
                                          var someVar = 'Error occurred while starting Xtify SDK.';
                                          dfs.crd.lilo.globalPushError = true;
                                          //alert(someVar);
                                          });
            var fail = function(error) {
            	//alert(error);
            };
            window.plugins.XtifySDK.setNotifIcon("icon", fail);
			// Push Notification change End
            
			//document.addEventListener("backbutton", onBackKeyDown, false);
			//document.addEventListener("menubutton", onMenuKeyDown, false);
			//document.addEventListener("searchbutton", onSearchKeyDown, false);
		}else{
			var cd = ChildBrowser.install();
		}
		manageTransition()
	}catch(err){
		showSysException(err);
	}
}

function manageTransition()
{	
try{
	if(isEmpty(deviceVersion)){
		slideType  = "slidec"; 
		return;}
	var deviceVer = parseInt(deviceVersion);
	switch (deviceType){		
	case "Android":
			if (deviceVer < 4){
				slideType  = "none";
			}
		break;
	case "iPhone":
	case "iPad": 
			if (deviceVer < 5){
				slideType  = "none";
			}	
		break;
	default:
		slideType  = "slidec";
		break;
	}
}catch(err){}
}

function overrideTransition(pageName)
{
	try{
        if (navigator.userAgent.match(/Android/) && (jQuery.inArray(pageName, noTransitionPages)!=-1 || jQuery.inArray(globalCache['FROM_PAGE'], noTransitionPages)!=-1))
		{
			return "none";
		}else
		{
			return slideType;
		} 
        
	}catch(err){}
}

function onOffline()
{
	navigator.notification.alert('Your Internet connection has been lost, please check you Network Settings',function dissmissAlert() {},'Discover','OK');
}

//function  getVoneCookie()
//{
//    alert(YES);
//    try{
//		CookieManager.prototype.getCookie(function successToken (arg) {vOne = arg; alert(vOne)}, null, "v1st");
//	} catch(err){
//	}
//}

//function onBackKeyDown () {
//if($.mobile.activePage.is('#login-pg')){
//e.preventDefault();
//navigator.app.exitApp();
//}
//else {
//navigator.app.backHistory();
//}
//}
function provideFeedBack () // Provide Feedback in Child Browser
{
	try{
        var referer = $.mobile.activePage.attr('id');
        
        if (!isEmpty(vOne) && !isEmpty(dfsKey))
        {
            customVar = vOne+"|"+dfsKey+"|DiscoverMobileVersion="+APPVER;
            opinionLabURL = shareURL+"&referer=https://mobileapp.discover.com/"+referer+"&custom_var="+customVar;
        }
        else
        {
            opinionLabURL=shareURL+"&referer=https://mobileapp.discover.com/"+referer+"&custom_var=DiscoverMobileVersion="+APPVER;
        }
		window.plugins.childBrowser.showWebPage(opinionLabURL);
	}catch(err){
		
	}
}

function onChildBrowserClose(width)
{
    if (width>0) $('[data-role="header"],[data-role="footer"]').css("width", width+"px !important");
}
 
/*
 * Gets the data from the global data storage Associative Array, the dataName is
 * the key for the array
 */
function getDataFromCache(dataName)
{  
	return globalCache[dataName] ;
}

/*
 * Puts the Data into the global data storage Associative, the key is the
 * dataName and the data associated with the key is the DataObject.
 */
function putDataToCache(dataName,dataObject)
{
	globalCache[dataName]= dataObject;
}

/*
 * To be used to set the Global array to null when we logout of the app.
 */
function clearGlobalCache()
{
	globalCache = null
	globalCache = [];
	bottomNav = [];
	moreNav = [];
	globalEarnRewardAmount=0;
	dfs.crd.pymt.validDays = new Array();
	dfs.crd.pymt.afterConfirmFlag = null;
	dfs.crd.pymt.messageAftrConfrm =null;
	dfs.crd.pymt.afterMakePayFlag = null;
	dfs.crd.pymt.messageAftrmakePay=null;
	dfs.crd.pymt.outstandingbalance = null;
	dfs.crd.pymt.selectvar = null;
    
    // Push Notification change Begin
    dfs.crd.lilo.otherUser=false;
    isSessionValid = false;
    pushRedirection = false;
    dfs.crd.push.manage.userOverride = null;
    dfs.crd.lilo.registrationServiceCalled = false;
    dfs.crd.lilo.pushNewMsgCount=0; 
    lastPushCountTime = 0;
    // Push Notification change End
}

/*
 * To be used for setting the data for the key "dataname", in the global data
 * storage Associative Array to null , basically to be called when updating the
 * Dependencies.
 */
function killDataFromCache(dataName)
{
	globalCache[dataName] = null;
}

function navToCBBRedeem()
{
	navigation("../rewards/redemptionLanding")
}

function navToCBBSignUp()
{
	navigation("../rewards/cashbackBonusSignup1")
}

function navToMilesRedeem()
{
	navigation("../rewards/milesRedeem")
}

function navToMilesSignUp()
{
	navigation("../rewards/milesSignup1")
}

function navMakePayment(){
	navigation("../payments/paymentStep1")
}

function navPaymentSummary(){
	navigation("../payments/paymentsSummary")
}

/*
 * Navigation function for the app, pageName+.html changes the page.
 */
function navigation(pageName,pageHashArg) 
{
	try{
		var pageHash=true;

		// sets the reverse property in changePage as true or false
		var transFlag = false;
		// if the call is from footerNavbar then the pageName is of form
		// "pageName-tabName" else its just pageName
		var pgName = pageName.split("-");
		// the length is more than 1 only in case of a call being made from
		// footerNavbar

		if (pgName.length > 1) 
		{
			var newTabArray = [];
			newTabArray[0] = 'home';
			newTabArray[1] = 'account';
			newTabArray[2] = 'payments';
			newTabArray[3] = 'cashback';
			newTabArray[4] = 'miles';
			newTabArray[5] = 'more';
			newTabArray[6] = 'sendmoney';
			newTabArray[7] = 'expdiscover';
			if (newTabArray.indexOf(currentTab) <= newTabArray.indexOf(pgName[1])) {
				transFlag = true;
			}
			currentTab = pgName[1];
		}

		if(typeof pageHashArg == 'boolean'){
			if(!pageHashArg){
				pageHash=false;
			}
		}
        $('html, body, .ui-page').animate({ scrollTop: 0 }, 0);
		$.mobile.changePage((pgName[0] + ".html"), {
			reverse : transFlag,
			transition : slideType,
			reload : "true",
			changeHash: pageHash
		});
	}catch(err){
		showSysException(err);
	}
}

/*
 * Gets the data for the Dynamic part of an HTML. For anypage with a dynamic
 * part, we create a Hard Coded JSON for Dynamic part to be added to the HTML
 * page.
 */

function getPageContent(fileName,contentName,cardType,cardOption,incentiveType,incentiveCode)
{
	try{
		var actualContent=null;
		var jsonData ;
		contentJson = getContentJson(fileName);

		if (cardType!=""){cardTypeSuffix="_"+cardType;}
		if (cardOption!=""){cardOptionSuffix="_"+cardOption;}
		if (incentiveType!=""){incentiveTypeSuffix="_"+incentiveType;}
		if (incentiveCode!=""){incentiveCodeSuffix="_"+incentiveCode;}

		actualContent = contentName+ cardTypeSuffix +cardOptionSuffix+incentiveTypeSuffix + incentiveCodeSuffix;
		if (! contentExists(actualContent,contentJson))
		{	
			actualContent = contentName+cardTypeSuffix+cardOptionSuffix+incentiveTypeSuffix;
			if (!contentExists(actualContent,contentJson))
			{	
				actualContent = contentName+cardTypeSuffix+incentiveTypeSuffix+incentiveCodeSuffix ;
				if (!contentExists(actualContent,contentJson))
				{ 
					actualContent = contentName+cardOptionSuffix +incentiveTypeSuffix+ incentiveCodeSuffix;
					if (! contentExists(actualContent,contentJson))
					{	
						actualContent = contentName+cardTypeSuffix+cardOptionSuffix;
						if (!contentExists(actualContent,contentJson))
						{	
							actualContent = contentName+cardTypeSuffix+incentiveTypeSuffix;
							if (!contentExists(actualContent,contentJson))
							{ 
								actualContent = contentName+cardOptionSuffix+incentiveTypeSuffix;
								if (!contentExists(actualContent,contentJson))
								{ 
									actualContent = contentName+incentiveTypeSuffix + incentiveCodeSuffix;
									if (! contentExists(actualContent,contentJson))
									{	
										actualContent = contentName+incentiveTypeSuffix;
										if (!contentExists(actualContent,contentJson))
										{	
											actualContent = contentName+cardTypeSuffix;
											if (!contentExists(actualContent,contentJson))
											{ 
												actualContent = contentName+cardOptionSuffix;
												if (!contentExists(actualContent,contentJson))
												{	
													actualContent = contentName;
													if (!contentExists(actualContent,contentJson))
													{ 
														actualContent = null;
													}
												}
											}
										}
									}
								}
							}
						}
					}
				}
			}
		}
		jsonData = contentJson[actualContent];
		return jsonData;
	}catch(err){
		showSysException(err);
	}
}

function getPageContentMin(fileName,contentName,incentiveType,incentiveCode)
{
	try{
		var actualContent=null;
		var jsonData;
		contentJson = getContentJson(fileName); 
		if (incentiveType!=""){incentiveTypeSuffix="_"+incentiveType;}
		if (incentiveCode!=""){incentiveCodeSuffix="_"+incentiveCode;}

		actualContent = contentName+incentiveTypeSuffix + incentiveCodeSuffix;
		if (! contentExists(actualContent,contentJson))
		{	
			actualContent = contentName+incentiveTypeSuffix;
			if (!contentExists(actualContent,contentJson))
			{	
				actualContent = contentName;
				if (!contentExists(actualContent,contentJson))
				{ 
					actualContent = null;
				}
			}
		}
		jsonData = contentJson[actualContent];
		return jsonData;
	}catch(err){
		showSysException(err);
	}
}


function getPageContentCn(fileName,contentName)
{
	try{
		var actualContent=null;
		var jsonData ;
		contentJson = getContentJson(fileName); 
		actualContent = contentName;
		if (!contentExists(actualContent,contentJson))
		{ 
			actualContent = null;
		}
		jsonData = contentJson[actualContent];
		return jsonData;
	}catch(err){
		showSysException(err);
	}
}


/*
 * Gets the JSON file and gets the JSON form the file.
 */
function getContentJson(fileName)
{
	try{
		var contentJson = new Object();
		var file = "../../json/"+fileName+".json";
		$.ajax({
			type : "GET",
			url : file,
			dataType : 'json',
			async: false,
			success : function(response, status, jqXHR) {
				contentJson=response; 
			},
			error : function(jqXHR, textStatus, errorThrown) {
				// handleError(jqXHR,textStatus,errorThrown);
			}
		});
		return contentJson;
	}catch(err){
		showSysException(err);
	}
}

/*
 * Checks if the data for id i.e. contentName, is available in the JSON i.e.
 * contentJson
 */
function contentExists(contentName,contentJson)
{
	try{
		if (contentJson[contentName] != null)
		{
			return true
		}else
		{
			return false
		}
	}catch(err){
		showSysException(err);
	}

}

/*
 * Replaces the actual data value i.e. contentData, in the dynamic part to be
 * added to a HTML page.
 */
function parseContent(htmlText,contentData)
{
	try{
		for (var i in contentData) {
			var index="!~"+i+"~!";
			htmlText=htmlText.replace(index,contentData[i]);
		}
		return htmlText;
	}catch(err){
		showSysException(err);
	}
}

/**
 * This method formats the numeric values like amount or miles balance in comma separated format..
 */
function numberWithCommas(x) {  
	try{
		return x.toString().replace(/\B(?=(\d{3})+(?!\d))/g, ",");
	}catch(err){
		showSysException(err);
	}
}

Date.prototype.defaultView = function() {
	var dd = this.getDate();
	if (dd < 10)
		dd = '0' + dd;
	var mm = this.getMonth() + 1;
	if (mm < 10)
		mm = '0' + mm;
	var yy = this.getFullYear();
	//yy=yy.toString().slice(2)
	return String(mm + "\/" + dd + "\/" + yy)
}

var today = new Date();
var display_date = today.defaultView();




/**
 *	This function works for handling errors that occur throughout the application 
 */
function errorHandler(errorCode,customErrorMessage,menuHglt){
	try{
		var errorMsg;	
		if (!isEmpty(customErrorMessage)) {
			errorMsg=customErrorMessage;
		}else{
			var staticMessage=errorCodeMap[errorCode];
			if(!isEmpty(staticMessage)) {
				errorMsg=staticMessage;
			}else{		
				errorMsg=errorCodeMap['0'];
			}
		}

		if (!isEmpty(errorMsg)){
			cpEvent.preventDefault();
		}

		$('#pageError-pg').live('pagebeforeshow',function(){
			document.getElementById('pageError_errorMessage').innerHTML=errorMsg;
			showBottomMenu('pageError-pg',menuHglt);
		});	

		navigation('../common/pageError');
	}catch(err){
		showSysException(err);
	}
}

function showSysException(err){
	try{
		alert(err.message);

		/* if (!jQuery.isEmptyObject(err)){
			cpEvent.preventDefault();
		}
		messageToDisplay=err.message;	

		var elementForDialog = '<div id="continue_cancel"></div>';
		$(elementForDialog).simpledialog2({
			mode : 'button',
			headerText : 'System Exception!',
			buttonPrompt : messageToDisplay,
			buttons :{
				'CLOSE' :{
					theme : "c",
					click : 
						function(){
						return;
					}
				}
			}
		}); */
	}catch(err){
		alert(err.message);
	}
}


function getResponseStatusCode(jqXHR){
	var errorCode=0;
	try{
		var responseText=jqXHR.responseText;
		if(!isEmpty(responseText)){
			if(responseText.substr(0,1) == '{'){
				var customErrorStatusCode = jQuery.parseJSON(responseText);
				if (customErrorStatusCode.hasOwnProperty("status")){
					errorCode = customErrorStatusCode.status;		
				} 
			}else{
				return 0;
			}

		}
	}catch(err){
		showSysException(err);
	}
	return errorCode;
}

function getResponseFromTxt(jqXHR,code){
	try{
		var responseText=jqXHR.responseText;
		if(!isEmpty(responseText)){
			if(responseText.substr(0,1) == '{'){
				var responseTxt = jQuery.parseJSON(responseText);
				if (responseTxt.hasOwnProperty(code)){
                    return responseTxt[code];
				}
			}else{
				return "";
			}
            
		}
	}catch(err){
		showSysException(err);
	}
	return errorCode;
}

function getResponsErrorData(jqXHR){
	var errorMessageData;
	try{
		var responseText=jqXHR.responseText;
		if(!isEmpty(responseText)){
			if(responseText.substr(0,1) == '{'){
				var customErrorStatusCode = jQuery.parseJSON(responseText);
				if (customErrorStatusCode.hasOwnProperty("data")){
					errorMessageData = customErrorStatusCode.data;		
				}	
			}else{
				return errorMessageData;
			}

		}
	}catch(err){
		showSysException(err);
	}
	return errorMessageData;
}





/*
 * Sends the User from CardHOme page to the Login Page, on Click of the back
 * button from CardHome.
 */
function backToLogin(pageName){
	try{
		navigation(pageName);		
	}catch(err){
		showSysException(err);
	}
}



/****************************Footer & Header integration start*****************************/
var bottomNav = new Array();
var moreNav = new Array();

function showHeader()
{
	//var headerHTML="<div class='back-button'><a data-rel='back'><img src='../../images/backButton.png'/> </a></a></div><div class='logo-img'><img src='../../images/handset-login-logo.png' alt='discover logo' /></div><div class</div> <div class='logout-button'><img src='../../images/logout-button.png' onclick='logOutUser(\"LOGOUT\")' /></div>" ;
	//$(".cmn_header").html(headerHTML);
}

function showFootNotes(ui)
{
	//var footNotesHTML="<a href'#' id='footer-links'>Provide Feedback</a><p id='footer-text'>&copy;2012 Discover Bank, Member FDIC</p>"
	//document.getElementById(ui+'_footnotes').innerHTML=footNotesHTML;

}

function showBottomMenu(ui,activeNav)
{
	try{
		getMenuItems();
		//return populateBottomNavItems(ui,activeNav)
		return populateBottomNavItems(ui,getPgHlt(activeNav));
	}catch(err){
		showSysException(err);
	}
}

function getMenuItems()
{
	try{
		if (bottomNav.length < 1)
		{
			var menuConfig= new Object();
//			menuConfig = getContentJson("menuConfig");  
//			if (jQuery.isEmptyObject(menuConfig)){
//				return;
//			}
			
			menuConfig=getDataFromCache("menuConfig");
			if (jQuery.isEmptyObject(menuConfig)) {
				menuConfig =getContentJson("menuConfig"); 
				putDataToCache("menuConfig",menuConfig);
			}
			
			var navArray = new Array();
			navArray = menuConfig["bottomnavitems"].split(",");
			for (var key in navArray) 
			{
				var navObj=menuConfig[navArray[key]];
				if (isValidMenuItem(incentiveTypeCode,incentiveCode,optionCode,navObj))
				{
					if (bottomNav.length < 4)
					{
						bottomNav.push(navArray[key]);
					}
					else
					{
						moreNav.push(navArray[key]);
					}
				}
			}      
		}
	}catch(err){
		showSysException(err);
	}
}

function populateBottomNavItems(ui,activeNav)
{
	try{
		var menuItems= new Object();
		var footerHTML="";
//		menuItems = getContentJson("menuItems");
//		if(jQuery.isEmptyObject(menuItems)){
//			return;
//		}
		
		menuItems=getDataFromCache("menuItems");
		if (jQuery.isEmptyObject(menuItems)) {
			menuItems =getContentJson("menuItems"); 
			putDataToCache("menuItems",menuItems);
		}
		
		footerHTML="<ul>"
			for (var key in bottomNav) 
			{
				if (activeNav== bottomNav[key])
				{
					footerHTML+="<li>"+menuItems[bottomNav[key]+"_ACTIVE"]+"</li>";
				}
				else
				{
					footerHTML+="<li>"+menuItems[bottomNav[key]]+"</li>";
				}
			} 

		if(activeNav=="MR")
		{
			footerHTML+="<li>"+menuItems["MR_ACTIVE"]+"</li>";
		}else 
		{
			footerHTML+="<li>"+menuItems["MR"]+"</li>";
		}
		$(".mnu_footer").html(footerHTML)
	}catch(err){
		showSysException(err);
	}
}




function populateMoreNavItems()
{
}

function isValidMenuItem(incentiveType,incentiveCode,optionCode,objNavItem)
{
	try{
		if (jQuery.isEmptyObject(objNavItem))
		{
			return true;
		}
		if (incentiveType=="") { incentiveType=="~";}
		if (incentiveCode=="") { incentiveCode="~";}
		if (optionCode=="") { optionCode="~";}
		if ((objNavItem["its"].search(incentiveType)!= -1 )&& (objNavItem["ics"].search(incentiveCode) != -1 )&& (objNavItem["docs"].search(optionCode) == -1))
		{
			return true;
		}
		else
		{
			return false;
		}
	}catch(err){
		showSysException(err);
	}
}

function showMnuHglt(menuitem)
{
    try{
        $('#pg-footer li div').removeClass('ui-btn-active');
        $('#pg-footer li div#'+menuitem).addClass('ui-btn-active');
    }catch(err){}
}

function showHM()
{
    showMnuHglt("home");
	navigation('../achome/cardHome');
}
function showAC()
{
    showMnuHglt("account");
	navigation('../achome/accountLanding');
}
function showPY()
{
    showMnuHglt("payments");
	navigation('../payments/paymentsLanding');
}
function showCB()
{
    showMnuHglt("cashback");
	navigation('../rewards/cashbackBonusLanding');
}
function showMI()
{
    showMnuHglt("miles");
	navigation('../rewards/milesHome');
}
function showMM()
{
    showMnuHglt("sendmoney");
	navigation('../p2p/sendMoneyLanding');
}
function showED()
{
    showMnuHglt("expdiscover");
	navigation('../exploreDiscover/exploreDiscoverLanding');
}
function showCS()
{
	navigation('../custormerService/customerServiceLanding');
}
function showMP()
{
	navigation('../profile/profileLanding');
}
function showMR()
{
    showMnuHglt("more");
	navigation('../common/moreLanding');
}
/****************************Footer integration end*****************************/

/****************************Page Beforechange*****************************/

$(document).bind( 'pagebeforechange', function( e, data ){
	try{

		if (!(typeof data.toPage === "string" )) {
			cpEvent=e;
            var pageName=toPage(data);
            toPageName=pageName;
			if(!jQuery.isEmptyObject(data.options.fromPage)){      

				if(data.options.fromPage.jqmData('url')=== data.toPage.jqmData('url')){ 
					cpEvent.preventDefault();
                    unLockUI();
					hideSpinner();
					return;
				}
				fromPageName=fromPage(data);
				globalCache['FROM_PAGE']=fromPageName;
                 try{
                 if (window[fromPageName+"Unload"]())
                 {
                 hideSpinner();
                 if(cpEvent.isDefaultPrevented()) unLockUI();
                 //cpEvent.preventDefault();
                 return;
                 }
                 }catch(err){}
            }else globalCache['FROM_PAGE'] = null;
              try{
				if (globalCache['FROM_PAGE']!=null){
				data.options.transition=overrideTransition(pageName);
				}
				window[pageName+"Load"]();
                if(cpEvent.isDefaultPrevented()) {
					unLockUI();
					hideSpinner(); 
					}
			 }catch(err){
			}		

			if(menuPage(pageName)){
				showBottomMenu(pageName,pageName);
                 }
             hideSpinner();
        }else
        {
        lockUI();
        $('html, body, .ui-page').animate({ scrollTop: 0 }, 0);
        showSpinnerPageBeforeChange();
        }
		
	}catch(err){
        unLockUI();
		showSysException(err);
	}
});

/**************************** ajaxComplete event *****************************/
$(document).ajaxComplete(function(e, xhr, options){
	if  (options.url.indexOf("/cardsvcs/acs") >= 0) lastRestCallTime = new Date().getTime();
})


function getPgHlt(pageName){
	try{
		var strHlt=pageMenuHglt[pageName];
		if(isEmpty(strHlt)){
			return;
		}
		var arrHlt = strHlt.split('|');
		for (var key in arrHlt){ 
			if(jQuery.inArray(arrHlt[key],bottomNav) > -1){
				return arrHlt[key];
			}
		}

		if(jQuery.inArray("MR",arrHlt) > -1){
			return "MR";
		}
	}catch(err){
		showSysException(err);
	}
}

function menuPage(pageName)
{
	try{
		if((pageName=="login-pg")||(pageName=="cardLogin")||(pageName=="exploreDiscover")||(pageName=="customerService")||(pageName=="index")||(pageName=="strongAuthFirstQues")|| (pageName=="optionalUpgrade")|| (pageName=="loginErrorPage"))
		{
			return false;

		}else
		{
			return true;
		}
	}catch(err){
		showSysException(err);
	}
}

function toPage(data)
{
	try{
		var url=data.toPage.jqmData('url');
		var file = url.substring(url.lastIndexOf('/')+1);
		var filename = file.split('.');
		return filename[0];
	}catch(err){
		showSysException(err);
	}
}
/**
 * NEw function by Rijesh
 * @param data
 * @returns
 */
function fromPage(data)
{
	try{
		var url=data.options.fromPage.jqmData('url');
		var file = url.substring(url.lastIndexOf('/')+1);
		var filename = file.split('.');
		return filename[0];
	}catch(err){
		showSysException(err);
	}
}


/*****************************Timeout******************************************/
function timeStartAfterLogin()
{
	clearTimeout(timer);
	timer=setTimeout("timeActionAfterLogin()",TIMEOUT_PERIOD);    
	//async call to a RESful service to reset the expiration time    
}

function timeActionAfterLogin()
{
	dfs.crd.lilo.logOutUser("TIMEOUT");
}

function timeStartBeforeLogin()
{
	clearTimeout(timer);
	timer=setTimeout("timeActionBeforeLogin()",TIMEOUT_PERIOD);
}

function timeActionBeforeLogin()
{
	navigation('../../../index');
}

$('[data-role=page]').live('pageshow', function(e,data){
	try{
        hideSpinner();               
        var activePage=$.mobile.activePage.attr('id');
        switch (activePage){
		case "login-pg":
			clearTimeout(timer);
			break;
		case "exploreDiscover-pg":	case "commonBenefits-pg":	case "commonRewardsSeeDetails-pg":
		case "commonPrivacyPolicy-pg":	case "commonSecurity-pg":	case "customerService-pg":
		case "coCustomerServiceFaqsOptimized-pg":	case "forceUpgrade-pg":	case "loginErrorPage-Pg":
		case "optionalUpgrade-pg":	case "cardLogin-pg":	case "publicError-pg":	case "forgot-both-step1-pg":
		case "forgot-uid-or-password-menu-pg":	case "forgot-uid-pg":	case "forgot-password-step1-pg":
		case "forgot-password-step2-pg":	case "forgot-both-step2-pg":
			//timeStartBeforeLogin();
			break;
		default:
			//adding page specific time out
			if(!isEmpty(toPageName)){
				var mobuleSpectTimeOutPeriod=moduleSpecTimeOut[toPageName];
				if(!isEmpty(mobuleSpectTimeOutPeriod)){
					TIMEOUT_PERIOD=mobuleSpectTimeOutPeriod;				
				}else{
					TIMEOUT_PERIOD="600000";				
				}
				keepSessionAlive();
				timeStartAfterLogin();
			}
		break;
		}
    unLockUI();
    
    // push change for showing the override user message after cardhome is loaded
    if (activePage == "cardHome-pg")
        dfs.crd.achome.pushRegistrationServiceAsync(); 
                     
    /*if (activePage != "login-pg" && activePage != "cardLogin-pg")
        dfs.crd.achome.pushNewMessageCountAsync(); // Push change for showing the new message count in the header for all application pages*/
    
    // Push change for showing the new message count in the header for all application pages   
    switch(activePage){
        case	"login-pg": case	"cardLogin-pg": case	"exploreDiscover-pg": case	"commonBenefits-pg": case	"commonPrivacyPolicy-pg":
        case	"commonSecurity-pg":    case	"customerService-pg":   case	"coCustomerServiceFaqsOptimized-pg":    
        case	"commonRewardsSeeDetails-pg":   case	"loginErrorPage-Pg":    case	"forceUpgrade-pg":  case	"optionalUpgrade-pg":
        case	"forgot-uid-or-password-menu-pg":   case	"forgot-both-step1-pg": case	"forgot-password-step1-pg":
        case	"forgot-uid-pg":    case "forgot-password-step2-pg":    case "forgot-both-step2-pg":  case "cardHome-pg":
            break;
        default:
            //dfs.crd.achome.pushNewMessageCountAsync();
            retreivePushCount();
            break;
    }
                           
    postSiteCatalyst(activePage);		//site catalyst
                           
	}catch(err){
        unLockUI();
		showSysException(err);
	}
});

function keepSessionAlive() {
	// fire KeepSessionAlive if last rest call is 30 seconds ago
	var now = new Date().getTime();
	if ( (now - lastRestCallTime)/1000 >= 30 ) 
        dfs.crd.lilo.resetServerTimeOut();
}


function retreivePushCount() {
	// fire retreivePushCount if last rest call is 60 seconds ago
	var now = new Date().getTime();
    
	if ( (now - lastPushCountTime)/1000 >= 60 ) 
        dfs.crd.achome.pushNewMessageCountAsync();
    else
        dfs.crd.achome.populatePushCountDivs();
}

function postSiteCatalyst(pageName)
{
    try{
        s.pageName=pageName;
        s.t();
    }catch(err){
        //showSysException(err);
	}
}

function moreLandingLoad(){
	try{
		getMenuItems();
		var menuItems= new Object();
		var moreHTML="";
		menuItems = getContentJson("menuItems");
		moreHTML="<ul data-role='listview' data-theme='d' data-inset='true' id='list1' class='account-list-view ui-listview ui-listview-inset ui-corner-all ui-shadow'>"
			for (var key in moreNav) 
			{
				moreHTML+=menuItems[moreNav[key]+"_MORE"];
			} 
		moreHTML+="</ul>";
		$("#showMoreLinks").html(moreHTML);
		//$(".mnu_more").trigger("create");
	}catch(err){
		showSysException(err);
	}
}

function notEmpty(o) {
	return (o != null && o !== "");
}

function isEmpty(o) {
	return (o == null || o === "");
}

//same as isEmpty but already used all over the place
function isVarEmpty(val){
	return isEmpty(val);
}

function base64EncodeGenerator(input){
	try{
		if(!isEmpty(input)){
			var keyStr = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/=";
			var output='';
			var chr1, chr2, chr3;
			var enc1, enc2, enc3, enc4;
			var i = 0;
			while (i < input.length) {
				chr1 = input.charCodeAt(i++);
				chr2 = input.charCodeAt(i++);
				chr3 = input.charCodeAt(i++);
				enc1 = chr1 >> 2;
				enc2 = ((chr1 & 3) << 4) | (chr2 >> 4);
				enc3 = ((chr2 & 15) << 2) | (chr3 >> 6);
				enc4 = chr3 & 63;
				if (isNaN(chr2)) {
					enc3 = enc4 = 64;
				} else if (isNaN(chr3)) {
					enc4 = 64;
				}
				output+=keyStr.charAt(enc1) + keyStr.charAt(enc2) + keyStr.charAt(enc3) + keyStr.charAt(enc4);
			}
			return output;	
		}
	}catch(err){
		showSysException(err);
	}
}

function getAuthorizationString(uidAndPassd){
	try{
		var authorizationString='';
		if(!isEmpty(uidAndPassd)){
			var base64Encode=base64EncodeGenerator(uidAndPassd);
			if(!isEmpty(base64Encode)){
				authorizationString="DCRDBasic "+base64Encode;
			}
		}
		return authorizationString;
	}catch(err){
		showSysException(err);
	}
}


function isACLite(applicationMode){
	try{
		var appMode = applicationMode.split(",");
		if(jQuery.inArray("ACL",appMode) > -1){
	               acLiteModeFlag = true;
			return true;
		}else{
			return false;
		}
	}catch(err){
		showSysException(err);
	}
}

function isCardProdGrp(applicationMode){
	try{
		var appMode = applicationMode.split(",");
		if(jQuery.inArray("CPG",appMode) > -1){	
			return true;
		}else{
			return false;
		}
	}catch(err){
		showSysException(err);
	}
}

function isRewardOutage(applicationMode){
	try{
		var appMode = applicationMode.split(",");
		if(jQuery.inArray("ROM",appMode) > -1){
			return true;
		}else{
			return false;
		}
	}catch(err){
		showSysException(err);
	}
}

function isPaperLessOutage(applicationMode){
	try{
		var appMode = applicationMode.split(",");
		if(jQuery.inArray("POM",appMode) > -1){
			return true;
		}else{
			return false;
		}
	}catch(err){
		showSysException(err);
	}
}

function isSmcOutage (applicationMode){
	try{
		var appMode = applicationMode.split(",");
		if(jQuery.inArray("SOM",appMode) > -1){

			return true;
		}else{
			return false;
		}
	}catch(err){
		showSysException(err);
	}
}



function redirectToPageAfterConfirm(eventElement) {

	try{	
		var EXTERNAL_PROD_URL="https://www.discover.com/";
        var EXTERNAL_PROD_CARD_URL="https://www.discovercard.com/";

        var APPLYDISCOVERCARDURL =EXTERNAL_PROD_URL+"credit-cards/mobile/index.html";
        var PROVIDEFEEDBACKURL ="https://secure.opinionlab.com/ccc01/o.asp?id=PsfavqlI&referer=https://tsys.discovercard.com/cardmembersvcs/mobile/app/achome/homepage&custom_var=89B92CDAB8AEC71B%7C7295";
        var REGISTERNOWURL=EXTERNAL_PROD_CARD_URL+"cardmembersvcs/registration/reg/goto?forwardName=reghome";
        var MYBENIFITURL=EXTERNAL_PROD_CARD_URL+"mybenefits";
        var PRIVACYPOLICYURL=EXTERNAL_PROD_CARD_URL+"customer-service/privacy-policies/?gcmpgn=0801_gf_pp_txt";
        var SECURITYURL=EXTERNAL_PROD_CARD_URL+"/customer-service/security/?gcmpgn=0801_gf_se_txt";
        var FAQURL=EXTERNAL_PROD_URL+"credit-cards/help-center/faqs/";
        var DISCOVERLINK=EXTERNAL_PROD_CARD_URL+"cardmembersvcs/loginlogout/app/ac_main?link=/cardmembersvcs/rewards/app/redeem?ICMPGN=ACH_TAB_CBB_BTN_RDM";
        var manageBankInfomation=EXTERNAL_PROD_CARD_URL+"cardmembersvcs/loginlogout/app/ac_main?link=/cardmembersvcs/epay/app/bankInfo";
        var manageCurrentDayPayment=EXTERNAL_PROD_CARD_URL+"cardmembersvcs/mobile/app/ems/link?pageName=managePay";
        var BANK_ACCOUNT_URL_FOR_DIRECT_DEPOSIT=HREF_URL+"cardmembersvcs/loginlogout/app/ac_main?link=/cardmembersvcs/rewards/app/browseInvest?modeCode=EFT1&view=cash";
        var IPHONEAPPURL="http://itunes.apple.com/app/discovermobile/id338010821?mt=8";
        var ANDROIDAPPURL="http://market.android.com/search?q=pname:com.discoverfinancial.mobile";


		switch(eventElement){
		case "Add Bank Account":
			window.open(BANK_ACCOUNT_URL_FOR_DIRECT_DEPOSIT);
			break;
		case "Register Now":
			window.open(REGISTERNOWURL);
			break;
		case "Apply Now":
			window.open(APPLYDISCOVERCARDURL);
			break;
		case "Provide Feedback":
			window.open(PROVIDEFEEDBACKURL,'_self',false);			
			break;
		case "mybenefits":
			window.open(MYBENIFITURL);			
			break;
		case "privacyPolicy":
			window.open(PRIVACYPOLICYURL);
			break;
		case "security":
			window.open(SECURITYURL);
			break;
		case "faq":
			window.open(FAQURL);			
			break;
		case "DISCOVERLINK":
			window.open(DISCOVERLINK);			
			break;
		case "manageBankInfomation":
			window.open(manageBankInfomation);			
			break;
		case "manageCurrentDayPayment":
			window.open(manageCurrentDayPayment);			
			break;

		case "updateAppVersion":
			if (navigator.userAgent.match(/iPhone/i))
				window.open(IPHONEAPPURL);
			else
				window.open(ANDROIDAPPURL);
			break;	
		}
	}catch(err){
		showSysException(err);
	}
}

//Remove all page history when on Landing page to push the app. into background on hitting hardware Back button

function clearHistory () {
	try{
		while (jQuery.mobile.urlHistory.stack.length > 1) {
			jQuery.mobile.urlHistory.stack.pop();
		}
		jQuery.mobile.urlHistory.activeIndex = 0;
	}catch(err){
		showSysException(err);
	}
}

//Code to handle sectoken - START
function getSecToken()
{
	try{
		CookieManager.prototype.getCookie(function successToken (arg) {sectoken = arg;}, null, "sectoken");
	}catch(err){
		// needs to handle the exception
	}
}

function clearSecToken ()
{
	sectoken = null;
}

//Code to handle sectoken - END
/* Function to check Internet Connection before every service call-  TO BE UPDATED */
function checkNetworkStat(){
//	try{
//	return true;
//	var intStatus= navigator.network.connection.type;
//	if (intStatus == "unknown" || intStatus == "none" || intStatus == "" || intStatus== null)
//	{
//	navigator.notification.alert("The Internet connection appears to be offline at this moment. In order to login and access all the features of the application, you will need Internet connectivity. Please check your network settings and try again once the connection has been restored", null,"Unable to Connect to Discover", "OK" );
//	return false;
//	}
//	return true;
//	}catch(err){
//	showSysException(err);
//	}
}

function namespace(namespaceString) {
	try{
		var parts = namespaceString.split('.'),
		parent = window,
		currentPart = '';    

		for(var i = 0, length = parts.length; i < length; i++) {
			currentPart = parts[i];
			parent[currentPart] = parent[currentPart] || {};
			parent = parent[currentPart];
		}
		return parent;
	}catch(err){
		showSysException(err);
	}
}

function getDID () {
	try{
		var did ;
		if (deviceType == "Android")
		{
			did = "%&(()!12["+ Custom.getDeviceId();
		}
		else
		{
			did	 = "%&(()!12["+ device.uuid;
		}

		did = Sha256.hash(did);
		return did;
	}catch(err){
		//showSysException(err);
	}
}

function getSID () {
	try{
		var sid 
		if (deviceType == "Android")
		{
			sid = "%&(()!12[" + Custom.getSimSerialNumber();
		}
		else
		{
			sid= "%&(()!12[" + "iPhone OS";
		}

		sid = Sha256.hash(sid);
		return sid;
	}catch(err){
		//showSysException(err);
	}
}

function getOID () {
	try{
		var oid;
		if (deviceType == "Android")
		{ 
			oid = "%&(()!12[" + device.uuid;
		}
		else
		{
			oid = "%&(()!12[" + device.version; //using PG API
		}
		oid = Sha256.hash(oid);
		return oid;
	}catch(err){
		//showSysException(err);
	}
}

// Begin Push Notification Change to add a method to retreive unhashed device OS & Device ID
function getOsVersion () {
	try{
		var oid;
		if (device.platform == "Android")
		{ 
			oid = device.version;
		}
		else
		{
			oid = device.version; //using PG API
		}

		if(oid.length == 1)
			oid = oid + ".0";

        	//alert("OS Version: " + oid);

		return oid;
	}catch(err){
		//showSysException(err)
	}
}

function getDeviceID () {
	try{
		var did ;
		if (device.platform == "Android")
		{
			did = Custom.getDeviceId();
		}
		else
		{
			did	 = device.uuid;
		}
        //alert("Device ID: " + did);
		return did;
	}catch(err){
		//showSysException(err)
	}
}

// End Push Notification change

//Added below code to make request headers completely accurate 
function getClientPlat (){
        try{
             return deviceType;
        }catch(err){
		
        }
}

/* prepare header */
function preparePostHeader(customHeaders) {
	try{
		var postHeader = {
				'X-Application-Version' : APPVER,
				'X-Client-Platform' : getClientPlat(),
				'Content-Type' : 'application/json',
				'X-SEC-Token' : sectoken
		};
		if (!isEmpty(customHeaders)) {	
			$.extend(postHeader, customHeaders);
		}
		return postHeader;
	}catch(err){
		showSysException(err);
	}
}

function prepareGetHeader(customHeaders) {
	try{
		var getHeader = {
				'X-Application-Version' : APPVER,
				'Content-Type' : 'application/json',
				'X-Client-Platform' : getClientPlat()
		};
		if (!isEmpty(customHeaders)) {	
			$.extend(getHeader, customHeaders);
		}
		return getHeader;
	}catch(err){
		showSysException(err);
	}
}

/**
 * This function is executed after Login to populate any data 
 * that should be pre-fetched asynchronously
 */
function getDataAsync(){
	try{
		dfs.crd.stmt.shared.ajax.getIdentifiers(true);
		dfs.crd.rwds.getRewardsPCCDataAsync();
	}catch(err){}
}

function isCardProjectBeyond(incentiveCode,incentiveTypeCode,optionCode,cardType){
	try{
      if(incentiveCode == "000016" && incentiveTypeCode == "CBB" && cardType == "000001" && optionCode == ("31" || "32" || "33"))
            return true;
      else 
            return false;
		}catch(err){
	showSysException(err);
	}
}

/* Network Check Plugin */
function networkCheck()
{
    try
    {
        NetworkConnectivity.prototype.isConnectionAvailable(function success(status){ 
                                                            if (status=="false"){
                                                            globalCache['NoNetworkShown']="Yes";
                                                            cpEvent.preventDefault();
                                                            navigator.notification.alert('The Internet connection appears to be offline at this moment. In order to login and access the features of the application, you will need Internet connectivity. Please check your device settings and try again once the connection has been restored',function dissmissAlert() {
                                                                                         if ($.mobile.activePage.attr('id')!="login-pg")
                                                                                         {
                                                                                         dfs.crd.lilo.logOutUser("NETWORK");
                                                                                         globalCache['NoNetworkShown']="No";
                                                                                         }
                                                                                         },'Discover','OK');
                                                           
                                                            }
                                                            }, null)
    }catch(err){}
}

$(document).ajaxSend(function(e, jqxhr, settings) {
                     try{
                     if(settings.url.indexOf("http") != -1 && (settings.url.indexOf("session/v1/delete")== -1 && globalCache['NoNetworkShown']!="Yes")){
                     networkCheck();
                     }
                     }catch(err){}
                     });

/* 401 handling */
$(document).ajaxError(function(e, jqXhr, settings, exception){
                      try{
					  hideSpinner();   
                      var custStatusCode=getResponseStatusCode(jqXhr);
                      if (jqXhr.status=== 401 && custStatusCode===0 && jqXhr.getResponseHeader("WWW-Authenticate")==="DCRDBasic" && settings.url.indexOf("session/v1/delete")== -1 && settings.url.indexof("reg/v1/user/id") == -1 && settings.url.indexOf("msghist/v1/notification")== -1 && isEmpty(settings.headers.Authorization))                      
                      {
                      navigator.notification.alert('As a security measure, we ended your session after extended inactivity. This helps protect your personal Discover card account information.  When you are ready to access your account information, simply log in again.',function dissmissAlert() {
                                                   if ($.mobile.activePage.attr('id')!="login-pg")
                                                   {
                                                   dfs.crd.lilo.logOutUser();
                                                   }},'Discover','OK');
                      }
                      if (jqXhr.status=== 503 && (custStatusCode==="1006" || custStatusCode==="1007") && settings.url.indexOf("session/v1/delete")== -1 && settings.url.indexOf("session/preauthcheck")== -1 && !(settings.url.indexOf("acct/v1/account")!= -1 && !isEmpty(settings.headers.Authorization)))
                      
                      {
                      
                      if (settings.url.indexof("/reg/v1/") != -1) {
                      	//return so standard logic is skipped when coming from registration
                      	return;
                      }
                      var maintMsg=jqXhr.getResponseHeader("Location");
                      var splitMsg = maintMsg.split("|~|");
                      maintMsg = splitMsg[1]; 
                      navigator.notification.alert(maintMsg,function dissmissAlert() {
                                                   if ($.mobile.activePage.attr('id')!="login-pg")
                                                   {
                                                   dfs.crd.lilo.logOutUser("NETWORK");
                                                   }},'Discover','OK');
                      }
                    }
                      catch(err){}
                      });
$.ajaxSetup({
             timeout: 30000
});			 
					  


