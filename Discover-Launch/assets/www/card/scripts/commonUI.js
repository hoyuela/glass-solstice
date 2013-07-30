/* This function update hover image of back button and logout */
/*$('.back-button div.back_btn_normal, .logout-button div.logout_over_normal').live("click", function(){
	try {
		$(this).css("visibility","hidden");
	}catch(err)	{ showSysException(err) }
});*/
/* This function is to show tap effect on list view */
/*$('.account-list-view li a').live("click", function(){
	try {
	$(this).parent().parent().parent().addClass("activetab");
	}catch(err)	{ showSysException(err)	}
});
$('.cardhome_ul a .account-list-view li').live("click", function(){
	try {
$('.account-list-view li').live("click", function(){
	$(this).removeClass("activetab").addClass("activetab");
	}catch(err)	{ showSysException(err)	}
});*/

/* set default trasition and set transition none if transitionFallbacks  */
$("body").live('pageinit', function () {
	try {
		$("div").find('[data-id="myfooter"]').each(function(index){
		   $(this).addClass("pg-footer");
		});
		/* Some of the HTML pages are not having data-id attribute set to header section */
		$("div[id='pg-header']:not([data-id])").each(function(index){
		   $(this).addClass("pg-header");
		});           
		$("div").find('[data-id="myHeaderDiscover"]').each(function(index){
		   $(this).addClass("pg-header");
		});              
		$('[data-position=fixed]').fixedtoolbar({ tapToggle:false, transition: "none" });  
	}catch(err)	{ showSysException(err)	}
});

/* remove background color from selected header*/
$("#cardLogin-pg, #publicError-pg,#sendMoneyOverlay-pg, #globalErrorPage,#forceUpgrade-pg,#optionalUpgrade-pg, #loginErrorPage-Pg, #strongAuthLocked-Pg").live('pageinit',function(event){
	try {
		$("#cardLogin-pg #pg-header, #publicError-pg #pg-header,#sendMoneyOverlay-pg #pg-header,#globalErrorPage #pg-header,#forceUpgrade-pg #pg-header,#optionalUpgrade-pg #pg-header, #loginErrorPage-Pg  #pg-header, #strongAuthLocked-Pg #pg-header").css({background:"#1A1A1A",border:"0px"});
	}catch(err)	{ showSysException(err)	}
});
$('body').live('pagebeforeshow',function(event){
	try {
	 $('input[type="text"], textarea').attr('spellcheck','false');
               if(navigator.userAgent.match(/iPhone/i)){
		//var headerHTML="<div class='back-button' id='back-btn'><a data-rel='back' ><div class='back_over'><div class='back_btn_normal'></div></div></a></div><div class='logo-img'><div></div></div><div class='logout-button'><a href='#' class='ui-link' onclick='dfs.crd.lilo.logOutUser(\"LOGOUT\")'>Log Out</a></div>" ;
        
        // Push Notification change for Header
        var headerHTML="<div class='leftPart' id='back-btn'><a data-rel='back' onClick = 'changebackImage()' class='dataRelBack'><div class='back_btn_normal'></div><div class='back_btn_press'></div></a><a href='#' onclick = 'dfs.crd.sct.pushAlertNotification();navigation(\"../pushNotification/alertHistory\");' id='alert_history_page_link' class='ui-link'><div id='push_alert_icon'><div class='number-of-new-messages'></div></div></a></div><div class='centerPart'><a href='#' class='head-bg-logo'></a></div><div class='logout-button boldtext rightPart'><a href='#' class='ui-link' onclick='dfs.crd.lilo.logOutUser(\"LOGOUT\")'>Log Out</a></div> <div class='clearboth'></div>" ;
               
		//var headerHTML_nobackbtn="<div class='back-button' id='back-btn'></div><div class='logo-img'><div></div></div><div class='logout-button'><a href='#' class='ui-link' onclick='dfs.crd.lilo.logOutUser(\"LOGOUT\")'>Log Out</a></div>" ;
        
        var headerHTML_nobackbtn= "<div class='leftPart'><a href='#' onclick = 'dfs.crd.sct.pushAlertNotification();navigation(\"../pushNotification/alertHistory\");' id='alert_history_page_link' class='ui-link'><div id='push_alert_icon'><div class='number-of-new-messages'></div></div></a></div><div class='centerPart'><a href='#' class='head-bg-logo'></a></div><div class='logout-button boldtext rightPart'><a href='#' class='ui-link' onclick='dfs.crd.lilo.logOutUser(\"LOGOUT\")'>Log Out</a></div> <div class='clearboth'></div>" ;
               
		//var headerHTML_nologout="<div class='back-button' id='back-btn'><a data-rel='back'><div class='back_over'><div class='back_btn_normal'></div></div></a></div><div class='logo-img'><div></div></div>";
               
        var headerHTML_nologout="<div class='leftPart' id='back-btn'><a data-rel='back' onClick = 'changebackImage()' class='dataRelBack'><div class='back_btn_normal'></div><div class='back_btn_press'></div></a></div><div class='centerPart'><a href='#' class='head-bg-logo'></a></div><div class='clearboth'></div>" ;
               
        var headerHTML_nobtn= "<div class='centerPart'><a href='#' class='head-bg-logo'></a></div>" ;
               
		/*Adding for R5*/
		var headerHTML_overlayPage="<div class='done-button boldtext back-button'><a href='#' rel='external' data-rel='back'>Done</a></div><div class='logo-img'><div></div></div>";  
	}else{
		//var headerHTML="<div class='back-button' id='back-btn'></div><div class='logo-img'><div></div></div><div class='logout-button'><a href='#' class='ui-link' onclick='dfs.crd.lilo.logOutUser(\"LOGOUT\")'>Log Out</a></div>";
               
        var headerHTML = "<div class='leftPart'><a href='#' onclick = 'dfs.crd.sct.pushAlertNotification();navigation(\"../pushNotification/alertHistory\");' id='alert_history_page_link' class='ui-link'><div id='push_alert_icon'><div class='number-of-new-messages'></div></div></a></div><div class='centerPart'><a href='#' class='head-bg-logo'></a></div><div class='logout-button boldtext rightPart'><a href='#' class='ui-link' onclick='dfs.crd.lilo.logOutUser(\"LOGOUT\")'>Log Out</a></div> <div class='clearboth'></div>";
               
		//var headerHTML_nobackbtn="<div class='back-button' id='back-btn'></div><div class='logo-img'><div></div></div><div class='logout-button'><a href='#' class='ui-link' onclick='dfs.crd.lilo.logOutUser(\"LOGOUT\")'>Log Out</a></div>";
               
        var headerHTML_nobackbtn = headerHTML;
               
        var headerHTML_nobtn= "<div class='centerPart'><a href='#' class='head-bg-logo'></a></div>" ;
               
		//var headerHTML_nologout="<div class='back-button' id='back-btn'></div><div class='logo-img'><div></div></div>" ;
               
        //var headerHTML_nologout = "<div style='width:20%; display;inline-block; float:left; height:10px;'></div><div class='logo-img'><div></div></div>" ;
        var headerHTML_nologout = "<div class='centerPart'><a href='#' class='head-bg-logo'></a></div>" ;
		/*Adding for R5*/
		var headerHTML_overlayPage="<div class='done-button boldtext back-button'><a href='#' rel='external' data-rel='back'>Done</a></div><div class='logo-img'><div></div></div>";
	}
		
	/* header for all pages */
	$(".cmn_header").html(headerHTML);
	/* custome header page only */
	$(".cmn_header_nobackbtn").html(headerHTML_nobackbtn);
	$(".cmn_header_nologout").html(headerHTML_nologout);
	
	/*Adding for R5*/
	
	$(".cmn_header_overlays").html(headerHTML_overlayPage); 
	
    // Push Notification Change Begin
               
    $(".cmn_header_nobtn").html(headerHTML_nobtn);
    // hide the header badge icon
    $(".number-of-new-messages").hide();  
               
    // Push Notification Change End
               
	/* This function is adding foot note in footer */
		//footnote for after login pages
		var footnotesHtml="<p id='footer-links'><a href='javascript:void(0);'  data-rel='external' class='registerNow' onclick='navigation(\"../common/moreLanding\")'> Privacy & Terms |</a><a href='#' onclick='provideFeedBack();'> Provide Feedback </a></p><div class='footertemlinks'><a href='#' onclick='showallnemu();' class='footerlinks navigationicon'></a><a href='#' class='footerlinks backicon' data-rel='back' onClick = 'changebackImage()'></a><a href='#' class='footerlinks logouticon' onclick='dfs.crd.lilo.logOutUser(\"LOGOUT\")'></a></div><!-- p data-theme='e' class='footer-text-icon'><span><span id='copyRightYear'>&copy; 2013 </span>Discover Bank, Member FDIC<span class='secured'> | SECURED</span></span></p -->";
		$(".footnotes").html(footnotesHtml);


	


	//Footnote for index page
	var footnotesHtml="<p id='footer-links'><a href='javascript:void(0);' onclick='navigation(\"card/html/common/privacyPolicy\")'>Privacy Statement </a><a href='javascript:void(0);' onclick='navigation(\"card/html/common/termsUse\")' class='registerNow'>| Terms of Use</a></p><p data-theme='e' class='footer-text-icon'><span><span id='copyRightYear'>&copy; 2013 </span>Discover Bank, Member FDIC<span class='secured'> | SECURED</span></span></p>";
	$("#login-pg .footnotes").html(footnotesHtml);
	
		//resetview();
		$("input:disabled").removeClass("ui-btn-up-c");
	}catch(err)	{ showSysException(err)	}
})	

function showallnemu(){
		navigation('../common/allLinks');
}
	
$("#moreLanding-pg,#moreLandingrevised-pg").live("pagebeforeshow", function(){
	try {
		$('#showMoreLinks li').removeClass('ui-corner-top ui-corner-bottom');	
		$('#showMoreLinks li:first-child').addClass('ui-corner-top');
		$('#showMoreLinks li:last-child').addClass('ui-corner-bottom');
	}catch(err)	{	showSysException(err) }
});

//payments-History	
$('#paymentsHistory-pg').live("pagebeforeshow",function(){
	try {
		$('#paymentsHistory_paymentHistory li').removeClass('ui-corner-top ui-corner-bottom');
		$('#paymentsHistory_paymentHistory li:first-child').addClass('ui-corner-top');
		$('#paymentsHistory_paymentHistory li:last-child').addClass('ui-corner-bottom').css("paddingBottom","10px");	
	}catch(err)	{ showSysException(err)	}
});
//pending payments
$('#pendingPayments-pg').live('pagebeforeshow',function(){
	try {
		$('#pendingPayments_pendingList li').removeClass('ui-corner-top ui-corner-bottom');
		$('#pendingPayments_pendingList li:first-child').addClass('ui-corner-top');
		$('#pendingPayments_pendingList li:last-child').addClass('ui-corner-bottom').css("paddingBottom","10px");	
	}catch(err)	{ showSysException(err)	}
});

$('#cardhome-pg').live('pagebeforeshow',function(){
	try {
		$('ul.cardhome_ul li').removeClass('ui-corner-top ui-corner-bottom');	
		$('ul.cardhome_ul li:first-child').addClass('ui-corner-top');
		$('ul.cardhome_ul li:last-child').addClass('ui-corner-bottom');
	}catch(err)	{ showSysException(err)	}
});

$('ul.white-box-rd-corner li').live('pagebeforeshow',function(){
	try {
		$('ul.white-box-rd-corner li:first-child').addClass('ui-corner-top');
		$('ul.white-box-rd-corner li:last-child').addClass('ui-corner-bottom');	
	}catch(err)	{ showSysException(err) }
});

$('#redeemHistory-pg').live('pagebeforeshow',function(){
	try {
		$('ul.redeem_history li:first-child').addClass('ui-corner-top');
		$('ul.redeem_history li:last-child').addClass('ui-corner-bottom'); 	
	}catch(err)	{ showSysException(err) }
});

/* This function for collapsible text on Redeem Cashback Bonus: step 4 of 4 */
$(".collapsible li a.collapslink").live("click",function(){
	try {
		var hideshowID  = $(this).attr("rel");
		$(this).addClass("active");
		$(this).removeClass("deactive");
		$("#"+hideshowID).css("display","block");
		$("#"+hideshowID).css("paddingBottom","10px");
		$(this).toggle(
				function(){
					$(this).addClass("deactive");
					$(this).removeClass("active");
					$("#"+hideshowID).css("display","none");
				},
				function(){
					$(this).addClass("active");
					$(this).removeClass("deactive");
					$("#"+hideshowID).css("display","block");
					$("#"+hideshowID).css("paddingBottom","10px");
				}
		);	
	}catch(err)	{ showSysException(err)	}
}); 


/* reset footer at bottom on page resize or page load and define min height for middle content area */
/*function resetview(){
	var winh = $(window).height();
	var actualh = winh-139; // 139 means header height + footer height 
	$(".ui-content-internalpages").css("min-height",actualh+"px");
}*/


$(window).resize(function() {
	//resetview();
	setGridwpwidth();
});


/* desabled button */

function activebtn (btnID){
	try {
	var bttnId=$('#'+btnID);
		bttnId.removeAttr('disabled');
		bttnId.parent().removeClass('ui-disabled');
	}catch(err)	{ showSysException(err)	}
}

function deactiveBtn (btnID){
	try {
	var bttnId=$('#'+btnID);
		bttnId.attr('disabled','disabled');
		bttnId.parent().addClass('ui-disabled');
	}catch(err)	{ showSysException(err) }
}

function redeem_DD (btnID){
	try {
	var this_select_text = $('.selectMerchant_DD_1 .ui-btn-text').html();
	if(this_select_text != 'Select Merchant') {
		activebtn (btnID);
	}
	}catch(err)	{ showSysException(err)	}
};

$(".head-bg-logo").live("click",function(){
	try{
		var activePG = $(".ui-page-active").attr("id");

		switch(activePG){
		case	"cardLogin-pg":
		case	"publicError-pg":
		case	"exploreDiscover-pg":
		case	"commonBenefits-pg":
		case	"commonPrivacyPolicy-pg":
		case	"commonSecurity-pg":
		case	"customerService-pg":
		case	"coCustomerServiceFaqsOptimized-pg":
		case	"commonRewardsSeeDetails-pg":
		case	"loginErrorPage-Pg":
		case	"forceUpgrade-pg":
		case	"optionalUpgrade-pg":
		case	"forgot-uid-or-password-menu-pg":
		case	"forgot-both-step1-pg":
		case	"forgot-password-step1-pg":
		case	"forgot-both-step2-pg":
		case	"forgot-password-step2-pg":
		case	"forgot-uid-pg":
		case	"termsUse-pg":
		case	"privacyPolicy-pg":
		        navigation("../../../index");
				break;
		case	"strongAuthFirstQues-pg":
			if( activePG == "strongAuthFirstQues-pg" ){
				var strongAuthReferModuleArray= new Array("../p2p/sendMoney1","../profile/personalizeCashPin1");
				if(jQuery.inArray(dfs.crd.sa.referrerModule, strongAuthReferModuleArray) > -1 ){
					navigation('../achome/cardHome');
				}else{
					navigation("../../../index");
				}
			}else{
				navigation("../../../index");
			}			
			break;
        case	"cardHome-pg":
	case	"pushTermsAndConditions-pg":
                return false;
                break;
		default:
			navigation('../achome/cardHome');
		break;
		}
	}catch(err){
		showSysException(err)
	}
});

/* Native Spinner Plugin invocation only on Service Calls */
function showSpinner(){
	try{
	//	window.plugins.LoadingView.spinnerOn(null, null, "", '     Loading...');
		HybridControl.prototype.showSpinner();
	}catch(err)
	{
		//showSysException(err)
	}
}

function showSpinnerPageBeforeChange(){
	try{
		var deviceT="";
        	if (!isEmpty(deviceType)) deviceT=deviceType.toLowerCase();     //Change

		if (deviceT!="android"){               //Change
			HybridControl.prototype.showSpinner();
		//	window.plugins.LoadingView.spinnerOn(null, null, "", '     Loading...');
		}
	}catch(err)
	{
		//showSysException(err)
	}
}

function hideSpinner(){
	try {
	//	window.plugins.LoadingView.spinnerOff(null, null);
		HybridControl.prototype.dismissProgressBar();
	}catch(err)
	{
		//showSysException(err)
	}
}

/*** reseat popup blackout area on background as per the oriantation **/
function doOnOrientationChange()
{   
	try{
		setGridwpwidth() // This function is to set grid view wraper width
		var winORIN = window.orientation;
		if(!isEmpty(winORIN)){  
			
			var winWID = $(window).width();
			var winHIT = $(window).height();
			
			switch(winORIN) 
			{ 
				case -90:
				case 90:/* fixes for landscape */

					if($.mobile.activePage.attr('id') == "edoLandingPage-pg" || $.mobile.activePage.attr('id') == "edoHistory-pg")

					{
					$('.overlay_wraper').css({top:26 + "px"

												 });
					}
                    if($.mobile.activePage.attr('id') == "viewMap-pg" || $.mobile.activePage.attr('id') == "mapDirections-pg")
                        
						{
                        var netHeight=$(window).height()-$("#pg-header").outerHeight();
                        $("#map_canvas,#map_canvas2").height(netHeight);
                        
						}

					/* landscape screen */                
					$('[data-role="header"],[data-role="footer"]').css("width", winWID+"px !important");
					break; 
					
					/* portrait screen */
				default:
				if($.mobile.activePage.attr('id') == "edoLandingPage-pg" || $.mobile.activePage.attr('id') == "edoHistory-pg")

					{

						$('.overlay_wraper').css({ top: ($(window).height() - $('.overlay_wraper').height())/2 + "px"});                        

					}
					                    if($.mobile.activePage.attr('id') == "viewMap-pg" || $.mobile.activePage.attr('id') == "mapDirections-pg")
                        
									{
                        var netHeight=$(window).height()-$("#pg-header").outerHeight();
                        $("#map_canvas,#map_canvas2").height(netHeight);
                        
													}
					$('[data-role="header"],[data-role="footer"]').css("width", winWID+"px !important");
					break; 
			} 
		} 
	}catch(err){
		showSysException(err)
	}
}
window.onorientationchange = function() { 
try{
		var deviceT="";
        	if (!isEmpty(deviceType)) deviceT=deviceType.toLowerCase();     //Change

		if (!isEmpty(deviceT) && deviceT!="android"){                //Change
		doOnOrientationChange()
		}
	}catch(err){
		showSysException(err)
	}
};

function lockUI()
{
    try{
     /*   $("body").append('<div id="dummyDiv"></div>');
        var winWID = $(window).width();
        var winHIT = $(window).height();
        //console.log(winWID +"-"+winHIT);
        $("#dummyDiv").css("width", winWID+"px !important");
        $("#dummyDiv").css("height", winHIT+"px !important"); */
        locked=true;
    }catch(err){}
}

function unLockUI()
{
    try{
        $("#dummyDiv").remove();
        locked=false
    }catch(err){}
}
$().ready(function(){
          document.ontouchmove = function(e){
          if (locked)
          e.preventDefault();
          }
          });
          
function defaultImage(elem) {
	// Unbind the error handler so that it won't get called in a loop
	$(elem).unbind('error');
	
	var image = $(elem).attr('data-default-image');
    var src = $(elem).attr('src');
    
    // Detect and handle using the same image
    if (image == src) {
        console.log("Same source error " + src);
        return false;
    }
    
	if (!!image) {
		$(elem).attr('src', image);
	}
	return false;
}
/* css changes for all landing pages */
$(document).bind("pageshow", function(event,ui){
	$('#accountLanding-pg,#paymentsLanding-pg,#paymentsLanding-pg,#cashbackBonusLanding-pg,#sendMoneyLanding-pg, #exploreDiscoverLanding-pg, #customerServiceLanding-pg,#profileLanding-pg, #manageAlertsOverride-pg, #browse-landing, #browse-all-ecertificates').bind('click',function(){		
		$(this +" .ui-listview .ui-btn-up-d").addClass('ui-btn-down-d');
	});
	
	$('#browse-landing .suggestions li, #browse-all-ecertificates .partners li').bind('click',function(){		
		$('#browse-landing .suggestions li, #browse-all-ecertificates .partners li').removeClass('ui-btn-down-d');
		$(this).addClass('ui-btn-down-d');
	});
});





var changebackImage = function(){
    var backbuttonPressDiv = $(".back_btn_press");
    var backbuttonDiv = $(".back_btn_normal");
    backbuttonDiv.hide();
    backbuttonPressDiv.css("display","inline-block");
}
/*$('.clickinternal').live("click",function(){
    var jump = $(this).attr('href');
    var new_position = $('#'+jump).offset();
    window.scrollTo(new_position.left,new_position.top);
    return false;
});
*/
var jumpToChoices = function() {
 $('.cls_choices').click(function(){
    var jump = $(this).attr('href');
    var new_position = $("#choices").offset();
    window.scrollTo(new_position.left,new_position.top);
    return false;
});
}
$("#privacyPolicyMore-pg,#privacyPolicy-pg").live('pageshow', function (event){
 jumpToChoices();  
});

$('#moreLanding-pg .ui-listview .ui-btn-up-d,#moreLandingrevised-pg .ui-listview .ui-btn-up-d').live('click',function(){
    $(this).addClass('ui-btn-down-d');
});

function updateValcrltDefAmt (parmVal){
		$("#crltDefAmtsetVal").text("$"+parmVal);
}

$('input.picker-input').live("click", function (e){
					  e.preventDefault();
			      $(this).focus();
				  });
/*13.3 global change starts */

/* Global Changes : Select Box*/
var callDropdownFn = function(){
	$("select").msDropdown();
	$('div.dd.ddcommon.borderRadius').parent().find('a.ui-btn').remove();
	$('div.dd.ddcommon.borderRadius').parent().find('input.text.shadow.borderRadius').remove();
	/*script for custom drop down plugin*/
	/*$("select").change(function(){
		$(this).parent(".ddOutOfVision").siblings(".dd").css("border-bottom","1px solid #8a9499");
		$(this).parent(".ddOutOfVision").siblings(".dd").find(".arrow").css({"border-bottom": "12px solid #8a9499"});
	});*/
}

$("#search, #statementLanding, #redeem-gift-card, #strongAuthFirstQues-pg, #sendMoneyStep1-pg" ).live("pagebeforeshow", function(){
	callDropdownFn();	
});

$("#account-activity").live("pagebeforeshow", function(){
	var activitySelect = $("#activity-selection").msDropdown({"visibleRows": 8}).data("dd");
	$('div.dd.ddcommon.borderRadius').parent().find('a.ui-btn').remove();
	$('div.dd.ddcommon.borderRadius').parent().find('input.text.shadow.borderRadius').remove();
});
$("#manageAlerts-pg").live("pagebeforeshow", function(){
	$("#addnumberSelect").msDropdown({"visibleRows": 6}).data("dd");   /* 13.3 global change 26/07/13 */
	$("#changenumberSelect").msDropdown({"visibleRows": 8}).data("dd");
	//$("select#VALUE").msDropdown({"visibleRows": 8}).data("dd");
	$('div.dd.ddcommon.borderRadius').parent().find('a.ui-btn').remove();
	$('div.dd.ddcommon.borderRadius').parent().find('input.text.shadow.borderRadius').remove();
});
/* script for cross image on input text field */
$("div[data-role='page']").live("pageshow",function(){
	/**********  Global Change  22-July [STARTS HERE] C1**************/
	$(".customsearchfilter .textInput").focus(function(){
		// $(this).parent(".wrapperSpan").siblings("a.ui-btn").css("visibility","visible")
	}).blur(function(){
	//	$(this).parent(".wrapperSpan").siblings("a.ui-btn").css("visibility","hidden")
	});/**********  Global Change  22-July [ENDS HERE] **************/
 
	/*script for new textbox*/
	$(".textInput,.numInput").focus(function(){
		$(this).parent(".wrapperSpan").css({"border-bottom": "solid 1px #303030","border-left": "solid 1px #303030","border-right": "solid 1px #303030"});
		$(this).parent(".wrapperSpan").siblings(".detailName").css({"color":"#293033"});

		if($(this).val().toString().length == 0){
			console.log('keyup if');
			$(this).siblings(".emptyText").css({"visibility": "hidden"});	
			//$(this).parent(".wrapperSpan").css("width","100%");
		}else{
			console.log('keyup else');
				$(this).siblings(".emptyText").css({"visibility": "visible"});
				//$(this).parent(".wrapperSpan").css("width","89%");
		}
	});
	
	$(".emptyText").mousedown(function(e){
		var parentVar = $(this).parent(".wrapperSpan");
		e.preventDefault();
		$(this).siblings(".textInput,.numInput").val("");	
		$(this).siblings(".errormsg").hide();
		parentVar.children(".textInput,.numInput").focus();
		$(this).css({"visibility": "hidden"});
		//siblingsVar.css("width","100%");
});
	
	$(".textInput,.numInput").blur(function(){
		var parentVar = $(this).parent(".wrapperSpan");
		parentVar.css({"border-bottom": "solid 1px #b3b3b3","border-left": "solid 1px #b3b3b3","border-right": "solid 1px #b3b3b3"});
		parentVar.siblings(".detailName").css({"color":"#777777"});
		$(this).siblings(".emptyText").css({"visibility": "hidden"});
		//parentVar.css("width","100%");
	});
	
	$(".textInput,.numInput").keyup(function(){
		if($(this).val().toString().length == 0){
			console.log('keyup if');
			$(this).siblings(".emptyText").css({"visibility": "hidden"});;	
			//$(this).parent(".wrapperSpan").css("width","100%");
		}else{
			console.log('keyup else');
				$(this).siblings(".emptyText").css({"visibility": "visible"});
				//$(this).parent(".wrapperSpan").css("width","89%");
		}
	});
});

$("#manageAlerts-pg").live("pageshow",function(){
	$('#notifsettings .hidden-element .ui-controlgroup-controls').jqTransform({imgPath:'images/'});
});
/* Global Changes ends here */