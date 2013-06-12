
/* Name Space */
dfs.crd.push.manage = dfs.crd.push.manage || {};

dfs.crd.push.manage.userOverride = null;
dfs.crd.push.manage.pushEnabled;
dfs.crd.push.manage.validatePushTnC  = false;
dfs.crd.push.manage.validateTextNumber = false;
dfs.crd.push.manage.changeTextNumber = false;
dfs.crd.push.manage.addnumberChange = false;

dfs.crd.push.manage.smsChecked = [];
dfs.crd.push.manage.pushChecked = [];
dfs.crd.push.manage.smsCheckedPost = [];
dfs.crd.push.manage.pushCheckedPost = [];

dfs.crd.push.manage.smsCheckedCached = [];
dfs.crd.push.manage.pushCheckedCached= [];
dfs.crd.push.manage.pageCachingVal = [];

dfs.crd.push.manage.isChangeMade = false;
dfs.crd.push.manage.orientation = "";
dfs.crd.push.manage.phoneCarrier="";

dfs.crd.push.manage.balaMaxAmt; 
dfs.crd.push.manage.balaMinAmt; 
dfs.crd.push.manage.crltMaxAmt; 
dfs.crd.push.manage.mlrwMaxAmt; 
dfs.crd.push.manage.mlrwMinAmt; 
dfs.crd.push.manage.mrrwMaxAmt; 
dfs.crd.push.manage.mrrwMinAmt; 
dfs.crd.push.manage.tamtMinAmt;

var changeNumberselect ="";
var selectedText = "";
manageAlertsOverrideLoad = function()
{
    try
    {
    // Page Initialization for Manage Alerts Override screen
    
	vendorID= getVID();
    // call push registration async service in case the XID was not retrieved properly
    if(vendorID == "")
    {
        dfs.crd.achome.pushRegistrationServiceAsync();
    }
    
     // check if the vendorID is null for iPhone, if it is, then user has turned off notification, display the message
    	var deviceT="";
        if (!isEmpty(deviceType)) deviceT=deviceType.toLowerCase();     //Change

	if(vendorID == "" && deviceT!="android")
    {
        // stop navigation to this page and clear cache to remove back functionality
        errorText = "<br/><br/>Notification for the Discover App is turned off on your mobile device. <br/>Please go into your device settings and enable notification for the Discover app in order to receive Push Alerts.";
        errorHandler("",errorText,"pushManageAlerts");
    }
    
    // check the Global Variable otherUser and show the Override Settings page as overlay
        //alert("User Override Scenario: " + otherUser);

    if (!globalOtherUser)
    {
        // stop navigation to this page and clear cache to remove back functionality
        cpEvent.preventDefault();
        navigation('../pushNotification/manageAlerts');
    }
    else
    {
        // show Override overlay 
        // Store the user selection (Override or Ignore) from Override Overlay in cash 
        
        $('#id-override-keep').click(function()
                         {
						 s.prop1='PUSH_HANDSET_ANT_ACCT_SETUP_KEEP_TXT';
                         dfs.crd.push.manage.userOverride = false;
                         navigation('../pushNotification/manageAlerts');
                         });
        $('#id-override-remove').click(function()
                         {
						 s.prop1='PUSH_HANDSET_ANT_ACCT_SETUP_REMOVE_TXT';
                         dfs.crd.push.manage.userOverride = true;
                         navigation('../pushNotification/manageAlerts');
                         });
        
    }
    }catch(err){
		//showSysException(err);
	}
}
manageAlertsTermsAndConditionsLoad = function ()
{
	dfs.crd.push.manage.CachePageData();
}

faqDeviceAlertsLoad =  function ()
{
	try{
		//Store the manage Alert page state
		dfs.crd.push.manage.CachePageData();
	   //	Hide all the Answers of all Questions
		$(".rdfaq-hidden-element").hide();
		$(".rdfaq-plus,.ui-block-a .rdfaq-que-link").toggle(function(){
		$(this).parents().parents("#faqDeviceAlerts-pg  .ui-content-internalpages ul li").css("background","url('../../images/minus.png') left top no-repeat");
				$(this).parents().children(".ui-block-b").children(".rdfaq-plus").css("background-position","bottom center");
				$(this).parents().children(".ui-block-a").children(".rdfaq-hidden-element").show()
		},
		function(){
		$(this).parents().parents("#faqDeviceAlerts-pg  .ui-content-internalpages ul li").css("background","url('../../images/pluse.png') left top no-repeat");
			$(this).parents().children(".ui-block-b").children(".rdfaq-plus").css("background-position","top center");
			$(this).parents().children(".ui-block-a").children(".rdfaq-hidden-element").hide();
		});
	}catch(err){
		//showSysException(err);
	}
}
dfs.crd.push.manage.CachePageData = function()
{
 try{
		dfs.crd.push.manage.smsCheckedCached=[];
		dfs.crd.push.manage.pushCheckedCached=[];
		dfs.crd.push.manage.pageCachingVal = [];
		
	// Select carrier values-- showing different values	
		if(dfs.crd.push.manage.changeTextNumber)
		{
		 var phonenumber= $("#txt-change-phone-number").val();
		 dfs.crd.push.manage.pageCachingVal["changeno_phone"] = phonenumber;	
		 
		 var phonenoPlaceholder= $("#txt-change-phone-number").attr('placeholder');
		 dfs.crd.push.manage.pageCachingVal["phoneno_placeholder"] = phonenoPlaceholder;
		 
		 var carrierSelect = $("#changenumber option:selected").text();
         dfs.crd.push.manage.pageCachingVal["changeno_carrierselect"] = carrierSelect;                            
                                 
         var carrierSelectVal = $("#changenumber option:selected").val();
         dfs.crd.push.manage.pageCachingVal["changeno_carrierselectVal"] = carrierSelectVal;
		 
		 var indexChangeNumber = document.getElementById("changenumberSelect");
		 var strUser = indexChangeNumber.selectedIndex;			 
		 dfs.crd.push.manage.pageCachingVal["select_index"] = strUser;		 
		 
		 var carrier = $("#changenumber #custom-carrier").val();		 
		 dfs.crd.push.manage.pageCachingVal["changeno_carrier"] = carrier;			 
		 dfs.crd.push.manage.pageCachingVal["changenumber_flag"] = "true";
		}
		
		var addnumberInput = $("#addnumber #txt-phone-number").val();
		if(!isEmpty(addnumberInput))		
		{
		  dfs.crd.push.manage.pageCachingVal["addnumber_phone"] = addnumberInput;		  
		}
		
		 var carrSelAddnumber = $("#addnumber option:selected").text();
         dfs.crd.push.manage.pageCachingVal["addno_carrierselect"] = carrSelAddnumber;
        		 
         var carrSelValAddnumber = $("#addnumber option:selected").val();
         dfs.crd.push.manage.pageCachingVal["addno_carrierselectVal"] = carrSelValAddnumber;

		var addCarrier = $("#addnumber #custom-carrier").val();	
		dfs.crd.push.manage.pageCachingVal["addno_carrier"] = addCarrier;
		 
		 var indexaddNumber = document.getElementById("addnumberSelect");
		 var strUser = indexaddNumber.selectedIndex;	
		 dfs.crd.push.manage.pageCachingVal["select_index_add"] = strUser;
		
	//Checkboxes	
		var checkedElements = $("input.alert-chk:checked");
        for (var i=0;i<checkedElements.length;i++)
        {
            var alertType = $(checkedElements[i]).attr('id');
            var alertName = $(checkedElements[i]).parent().parent().parent().parent().parent().attr('id');
            if (alertType == "SMRM")
            {
                dfs.crd.push.manage.smsCheckedCached.push(alertName);               
            }
            if (alertType == "PNRM")
            {
                dfs.crd.push.manage.pushCheckedCached.push(alertName);
                varCustAccptInd = "Y";
            }
        }
		var crltVal = $("#CRLT #VALUE option:selected").val();
		dfs.crd.push.manage.pageCachingVal["CRLT"] = crltVal;
		
	// save Slider status	
		pnSliderParam = $('select#alertsetting').val();
        if (pnSliderParam == "off")
        {
            dfs.crd.push.manage.pageCachingVal["pushSlider"] = "P";
        }
        else
        {
           dfs.crd.push.manage.pageCachingVal["pushSlider"] = "Y";
        }
	//	Save Values in textboxes	
		 if(!$('#TAMT').hasClass('hideLi'))
			{
		     var tamtVal=$("#TAMT #VALUE").val();
			 dfs.crd.push.manage.pageCachingVal['TAMT']= tamtVal;
			}
		  if(!$('#MRRW').hasClass('hideLi'))
			{
		     var mrrwVal=$("#MRRW #VALUE").val();
			 dfs.crd.push.manage.pageCachingVal['MRRW']= mrrwVal;
			}
		  if(!$('#MLRW').hasClass('hideLi'))
			{
			 var mlrwVal=$("#MLRW #VALUE").val();
			 dfs.crd.push.manage.pageCachingVal['MLRW']= mlrwVal;
			}
		  if(!$('#BALA').hasClass('hideLi'))
			{
		     var balaVal=$("#BALA #VALUE").val();
			 dfs.crd.push.manage.pageCachingVal['BALA']= balaVal;
			}
		if(!$('#CRLT').hasClass('hideLi'))
			{
		     var balaVal=$("#CRLT #VALUE").val();
			 dfs.crd.push.manage.pageCachingVal['CRLT']= balaVal;
			}	
	}catch(err){
		//showSysException(err);
	}
}

manageAlertsLoad = function ()
{
	try{
        // Page Initialization for Manage Alerts screen
        if(fromPageName == "faqDeviceAlerts" || fromPageName == "manageAlertsTermsAndConditions")
		{
		  dfs.crd.push.manage.rePopulateManageNotification("MANAGEALERTS");
		  // Warns user about saving preferences before redirecting to other screen
         $('#id-notify-cancel').click(function(){
                                     if(dfs.crd.push.manage.isChangeMade)
                                     {
                                        // redirect the user to without saving alert 
                                        $('.ui-loader').hide();
                                            var customSettings = {"ContentHolder": "#overlay_wraper_saveConfirm","height":"215","resize":false,"width":"300"};
                                        dfs.crd.push.manage.fnOverlay(customSettings);
										trackchangesNotSavedError();
                                     }
                                     else
                                     {
                                        // back button functionality, go back to Profile page
                                        //navigation('../profile/profileLanding');
									 gotoAchome();
                                     }
                                     });
        //  Click Save preference Call Push Preference POST Service
        $('#id-notify-save').click(function()
                               {
									$('#global-errors').html('');
									s.prop1='PUSH_HANDSET_MANAGE_ALERTS_SAVE_BTN';
									dfs.crd.push.manage.CachePageData();
                                    dfs.crd.push.manage.postPushPreferenceData();
                               });
		}
		else
		{
		 dfs.crd.push.manage.populateManageNotification("MANAGEALERTS");
         // Warns user about saving preferences before redirecting to other screen
         $('#id-notify-cancel').click(function(){
                                     if(dfs.crd.push.manage.isChangeMade)
                                     {
                                        // redirect the user to without saving alert 
                                        $('.ui-loader').hide();
                                            var customSettings = {"ContentHolder": "#overlay_wraper_saveConfirm","height":"215","resize":false,"width":"300"};
                                        dfs.crd.push.manage.fnOverlay(customSettings);
										trackchangesNotSavedError();
                                     }
                                     else
                                     {
                                        // back button functionality, go back to Profile page
                                        //navigation('../profile/profileLanding');
										 gotoAchome();
                                     }
                                     });
        //  Click Save preference Call Push Preference POST Service
        $('#id-notify-save').click(function()
                               {
                                   $('#global-errors').html('');
								   dfs.crd.push.manage.postPushPreferenceData();
                               });
		}
	}catch(err){
		//showSysException(err);
	}
}
// called for population of page data on coming back from "FAQ" or "Terms and Conditions" page.
dfs.crd.push.manage.rePopulateManageNotification = function(pageId)
{
		try{
			var pushPreference = dfs.crd.push.manage.getPushPreferenceData(pageId);
			if (!jQuery.isEmptyObject(pushPreference))
				{
					dfs.crd.push.manage.populateManageNotificationPageDivs(pushPreference, pageId);
				}
		}catch(err){
		//showSysException(err);
	}
}

dfs.crd.push.manage.validateFields = function(){
    try
    {
var errEmptyPhoneNumber = 'Please enter a valid mobile number.';
		var errFraudAlert = 'Please enter a valid mobile number and carrier to receive fraud alerts.';
		var errExceedAmount  = 'Please enter an amount greater than or equal to your minimum notification amount (shown above).';
		var errMinNotifAmount = 'The minimum notification amount is MINIMUM_AMT. Please enter an amount of MINIMUM_AMT or more.';
		var errMaxNotifAmount = 'The maximum notification amount is MAXIMUM_AMT. Please enter an amount of MAXIMUM_AMT or less.';
		var errOtherCustomCarrier='Please enter a valid mobile carrier.';
		if($.trim($('.pn-page #changenumber select').find(":selected").text())== 'Other'){
			var changeNumbercarrier = $("#changenumber #custom-carrier").val();
			if(isEmpty(changeNumbercarrier) || !dfs.crd.push.manage.validateCustomCarrier(changeNumbercarrier))
			{
				$('#global-errors').html(errOtherCustomCarrier).addClass('redtext boldtext').show();
				$('body').animate({scrollTop:0}, 'slow');
				$("#changenumber #custom-carrier").addClass("input_hightlight_error");
				return false;
			}
			else
			{
				if($('#changenumber #custom-carrier').hasClass('input_hightlight_error'))
					$('#changenumber #custom-carrier').removeClass("input_hightlight_error");
			}
		}
if( $('[name=balaDefAmt]').val() < dfs.crd.push.manage.balaMinAmt && ($.inArray("BALA",dfs.crd.push.manage.pushCheckedPost)>=0 || $.inArray("BALA",dfs.crd.push.manage.smsCheckedPost)>= 0)) {
		$('#global-errors').html(errMinNotifAmount.replace(/MINIMUM_AMT/g, dfs.crd.push.manage.balaMinAmt )).addClass('redtext boldtext').show();
		$('body').animate({scrollTop:0}, 'slow');
  	    $('[name=balaDefAmt]').addClass("input_hightlight_error");
		return false;
	}
    else
    {
        if($('[name=balaDefAmt]').hasClass('input_hightlight_error'))
            $('[name=balaDefAmt]').removeClass("input_hightlight_error");
    }
	
	if( $('[name=balaDefAmt]').val() >  dfs.crd.push.manage.balaMaxAmt && ($.inArray("BALA",dfs.crd.push.manage.pushCheckedPost)>=0 || $.inArray("BALA",dfs.crd.push.manage.smsCheckedPost)>= 0)) {
        
		$('#global-errors').html(errMaxNotifAmount.replace(/MAXIMUM_AMT/g, dfs.crd.push.manage.balaMaxAmt )).addClass('redtext boldtext').show();
		$('body').animate({scrollTop:0}, 'slow');
		$('[name=balaDefAmt]').addClass("input_hightlight_error");
		return false;	
	}
    else
    {
        if($('[name=balaDefAmt]').hasClass('input_hightlight_error'))
            $('[name=balaDefAmt]').removeClass("input_hightlight_error"); 
    }
	
	if(( $('[name=crltMaxAmt]').val() >  dfs.crd.push.manage.crltMaxAmt  || $('[name=crltMaxAmt]').val() < 0) && ($.inArray("CRLT",dfs.crd.push.manage.pushCheckedPost)>=0 || $.inArray("CRLT",dfs.crd.push.manage.smsCheckedPost)>= 0)) {
		$('#global-errors').html(errMaxNotifAmount.replace(/MAXIMUM_AMT/g, dfs.crd.push.manage.crltMaxAmt )).addClass('redtext boldtext').show();
		$('body').animate({scrollTop:0}, 'slow');
   	    $('[name=crltMaxAmt]').addClass("input_hightlight_error");
		return false;	
	}
    else
    {
        if($('[name=crltMaxAmt]').hasClass('input_hightlight_error'))
            $('[name=crltMaxAmt]').removeClass("input_hightlight_error"); 
    }
	
	if( $('[name=mlrwDefAmt]').val() < dfs.crd.push.manage.mlrwMinAmt && ($.inArray("MLRW",dfs.crd.push.manage.pushCheckedPost)>=0 || $.inArray("MLRW",dfs.crd.push.manage.smsCheckedPost)>= 0) ) { 
		$('#global-errors').html(errMinNotifAmount.replace(/MINIMUM_AMT/g, dfs.crd.push.manage.mlrwMinAmt )).addClass('redtext boldtext').show();
		$('body').animate({scrollTop:0}, 'slow');
		$('[name=mlrwDefAmt]').addClass("input_hightlight_error");
		return false;	
	}
    else
    {
        if($('[name=mlrwDefAmt]').hasClass('input_hightlight_error'))
            $('[name=mlrwDefAmt]').removeClass("input_hightlight_error"); 
    }
	
	if( $('[name=mlrwDefAmt]').val() >  dfs.crd.push.manage.mlrwMaxAmt && ($.inArray("MLRW",dfs.crd.push.manage.pushCheckedPost)>=0 || $.inArray("MLRW",dfs.crd.push.manage.smsCheckedPost)>= 0)) {
		$('#global-errors').html(errMaxNotifAmount.replace(/MAXIMUM_AMT/g, dfs.crd.push.manage.mlrwMaxAmt )).addClass('redtext boldtext').show();
		$('body').animate({scrollTop:0}, 'slow');
		$('[name=mlrwDefAmt]').addClass("input_hightlight_error");
		return false;	
	}
    else
    {
        if($('[name=mlrwDefAmt]').hasClass('input_hightlight_error'))
            $('[name=mlrwDefAmt]').removeClass("input_hightlight_error"); 
    }
	
	if( $('[name=mrrwDefAmt]').val() < dfs.crd.push.manage.mrrwMinAmt && ($.inArray("MRRW",dfs.crd.push.manage.pushCheckedPost)>=0 || $.inArray("MRRW",dfs.crd.push.manage.smsCheckedPost)>= 0)) {
		$('#global-errors').html(errMinNotifAmount.replace(/MINIMUM_AMT/g, dfs.crd.push.manage.mlrwMinAmt )).addClass('redtext boldtext').show();
		$('body').animate({scrollTop:0}, 'slow');
		$('[name=mlrwMinAmt]').addClass("input_hightlight_error");
		return false;	
	}
    else
    {
        if($('[name=mlrwMinAmt]').hasClass('input_hightlight_error'))
            $('[name=mlrwMinAmt]').removeClass("input_hightlight_error"); 
    }
	
	if( $('[name=mrrwDefAmt]').val() >  dfs.crd.push.manage.mrrwMaxAmt && ($.inArray("MRRW",dfs.crd.push.manage.pushCheckedPost)>=0 || $.inArray("MRRW",dfs.crd.push.manage.smsCheckedPost)>= 0)) { 
		$('#global-errors').html(errMaxNotifAmount.replace(/MAXIMUM_AMT/g, dfs.crd.push.manage.mlrwMaxAmt )).addClass('redtext boldtext').show();
		$('body').animate({scrollTop:0}, 'slow');
		$('[name=mrrwDefAmt]').addClass("input_hightlight_error");
		return false;	
	}
    else
    {
        if($('[name=mrrwDefAmt]').hasClass('input_hightlight_error'))
            $('[name=mrrwDefAmt]').removeClass("input_hightlight_error"); 
    }
	
	if( $('[name=tamtDefAmt]').val() <  dfs.crd.push.manage.tamtMinAmt && ($.inArray("TAMT",dfs.crd.push.manage.pushCheckedPost)>=0 || $.inArray("TAMT",dfs.crd.push.manage.smsCheckedPost)>= 0)) { 
		$('#global-errors').html(errMinNotifAmount.replace(/MINIMUM_AMT/g, dfs.crd.push.manage.tamtMinAmt )).addClass('redtext boldtext').show();
		$('body').animate({scrollTop:0}, 'slow');
		$('[name=tamtDefAmt]').addClass("input_hightlight_error");
		return false;	
	}
    else
    {
        if($('[name=tamtDefAmt]').hasClass('input_hightlight_error'))
            $('[name=tamtDefAmt]').removeClass("input_hightlight_error"); 
    }

    if( dfs.crd.push.manage.phoneCarrier == "" && $.inArray("FRAD",dfs.crd.push.manage.smsCheckedPost)>=0 ) { 
        $('#global-errors').html(errFraudAlert).addClass('redtext boldtext').show();
        $('body').animate({scrollTop:0}, 'slow');
        return false;	
    }
if($.trim($('.pn-page #addnumber select').find(":selected").text()) == 'Other'){
			var addNumbercarrier = $("#addnumber #custom-carrier").val();
			if(isEmpty(addNumbercarrier) || !dfs.crd.push.manage.validateCustomCarrier(addNumbercarrier))
			{
				$('#global-errors').html(errOtherCustomCarrier).addClass('redtext boldtext').show();
				$('body').animate({scrollTop:0}, 'slow');
				$("#addnumber #custom-carrier").addClass("input_hightlight_error");
				return false;
			}
			else
			{
				if($('#addnumber #custom-carrier').hasClass('input_hightlight_error'))
					$('#addnumber #custom-carrier').removeClass("input_hightlight_error");
			}
		}
    if(dfs.crd.push.manage.validateTextNumber)
    {
        var str = $("#txt-phone-number").val();
        var index = $("#addnumber select").get(0).selectedIndex;
	 	/*96028*/
		if(isEmpty(str)){
		$('.add_phone_number').addClass("input_hightlight_error");
		$('#global-errors').html(errEmptyPhoneNumber).addClass('redtext boldtext').show();		
		 return false;
		}
        if(str.length > 0)
        {
            if (!dfs.crd.push.manage.validatePhoneNumbers(str))
                { 
                    $('#global-errors').html(errEmptyPhoneNumber).addClass('redtext boldtext').show();
                    $('body').animate({scrollTop:0}, 'slow');
                    $("#txt-phone-number").addClass("input_hightlight_error");
                    return false;
                }
            else
                {
                    if($("#txt-phone-number").hasClass('input_hightlight_error'))
                        $("#txt-phone-number").removeClass("input_hightlight_error"); 
                }
        }
    }
    /*if()
	{
	 dfs.crd.push.manage.changeTextNumber= true;
	}*/
    if(dfs.crd.push.manage.changeTextNumber)
    {
        if($("#txt-phone-number").hasClass('input_hightlight_error'))
            $("#txt-phone-number").removeClass("input_hightlight_error");
        
        var str = $("#txt-change-phone-number").val();
        var index = $("#changenumber select").get(0).selectedIndex;
        /*96028*/
		if(isEmpty(str)){
		$('.phone_number').addClass("input_hightlight_error");
		 return false;
		}
        if(str.length > 0)
		{
            if (!dfs.crd.push.manage.validatePhoneNumbers(str))
                {
                    $('#global-errors').html(errEmptyPhoneNumber).addClass('redtext boldtext').show();
                    $('body').animate({scrollTop:0}, 'slow');
                    $("#txt-change-phone-number").addClass("input_hightlight_error");
                    return false;
					trackInlineError();
                }
            else
                {
                    if($("#txt-change-phone-number").hasClass('input_hightlight_error'))
                        $("#txt-change-phone-number").removeClass("input_hightlight_error"); 
					//$('#global-errors').html("");
					$('#global-errors').html(errEmptyPhoneNumber).hide();
                }
        }
    }

    if(dfs.crd.push.manage.addnumberChange)
        {
            if($("#txt-phone-number").hasClass('input_hightlight_error'))
                $("#txt-phone-number").removeClass("input_hightlight_error");
            var str = $("#txt-phone-number").val();
            var index = $("#addnumber select").get(0).selectedIndex;
			/*96028*/
            if(isEmpty(str)){
			$('.add_phone_number').addClass("input_hightlight_error");
			return false;
			}
		            if(str.length > 0)
            {
                if (!dfs.crd.push.manage.validatePhoneNumbers(str))
                {
                    $('#global-errors').html(errEmptyPhoneNumber).addClass('redtext boldtext').show();
                    $("#txt-phone-number").addClass("input_hightlight_error");
                    return false;
					trackInlineError();
                }
                else
                {
                    if($("#txt-phone-number").hasClass('input_hightlight_error'))
                        $("#txt-phone-number").removeClass("input_hightlight_error"); 
                }
            }
        }
        
    //$('#global-errors').html('');

    return true;
    }
    catch(err)
	{
		//showSysException(err);
        return false;
	}
}

/*
 Check for Phone number repeat.. returns false if number is 111-1111-111... for 13.1 Change
 */
function chkForNumberRepeat(number) {
	try
		{
		if(number == 'undefined') {
			return false;
		}
		var charCnt = 0 ;
		var charLength = number.length;
		for(var i=0; i<= charLength;i++) {
			if(number[i] == number.charAt(0)) {
				charCnt++;
			}
		}
		if(charCnt == charLength) {
			return true;
		}
		return false;
	}catch(err){
		//showSysException(err);
	}
}

dfs.crd.push.manage.validatePhoneNumbers = function(phoneNumber){
	try
	{ 
		var numberLength = phoneNumber.length;
		var numberStartsWith = phoneNumber.substring(0,1);
		var areaCode = phoneNumber.substring(0,3);
		var numberCode = phoneNumber.substring(3,numberLength - 3);
		
		if(numberLength != 10 || numberStartsWith < 2 || numberCode.substring(0,3) == 555 || numberCode.substring(0,1) == 0 || chkForNumberRepeat(phoneNumber))
		{
			return false;
		}
		else
		{
			return true;
		}
	}catch(err){
		//showSysException(err);
	}
}

dfs.crd.push.manage.validateCarrier = function(carrier){
	try
	 {    
		if($.inArray("FRAD",dfs.crd.push.manage.smsCheckedPost)>=0 && carrier == 0)
		{
			return false;
		}
		else
		{
			return true;
		}
	}catch(err){
		//showSysException(err);
	}
}

dfs.crd.push.manage.checkLengthofTextbox = function(){
	try
	{    
		var length_of_number = $('.phone_number').val().length;
		if(length_of_number==0){
			if($('#phonenumber a').length >0){
				var default_number = $('#phonenumber a').html();
			}else{
				var default_number = $('#phonenumber').html();
			}
			default_number = default_number.substr(0,3)+default_number.substr(4,3)+default_number.substr(8,4);
			$('.phone_number').attr("placeholder",default_number);
		}
	}catch(err){
		//showSysException(err);
	}
}
/*
 * Description:Function called on "pagebeforeshow" for Manage Notification screen
 */
dfs.crd.push.manage.populateManageNotification = function(pageName)
{
	try{
		var pushPreference = dfs.crd.push.manage.getPushPreferenceData(pageName);
		if (!jQuery.isEmptyObject(pushPreference))
        {
			dfs.crd.push.manage.populateManageNotificationPageDivs(pushPreference, pageName);
		}
	}catch(err)
	{
		//showSysException(err);
	}
}
dfs.crd.push.manage.validateCustomCarrier =function (carrier){
	try{
		var filter = /^((\[[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\.)|(([\w-]+\.)+))([a-zA-Z]{2,4}|[0-9]{1,3})(\]?)$/;
		if (filter.test(carrier)){
			return true;
		}else{
			return false;
		}
	}catch(err)
	{
		//showSysException(err);
	}
}

/* Description: Function to call Push Preference GET Service
 */
dfs.crd.push.manage.getPushPreferenceData = function(pageId)
{
    try
    {
        var pushPreference = getDataFromCache(pageId);
        if (jQuery.isEmptyObject(pushPreference)) 
		{
            var newDate = new Date();	
            var PUSHPREFERENCEURL = RESTURL + "contact/v1/preferences/enrollments?" + newDate +"&vid="+ vendorID + "";
            showSpinner();
            $.ajax
             ({
             type : "GET",
             url : PUSHPREFERENCEURL,
             async : false,
             dataType : 'json',
             headers:prepareGetHeader(),
             success : function(responseData, status, jqXHR) 
             {
             hideSpinner();
             if (!validateResponse(responseData,"getPushPreferenceValidation")){ // Pen Test Validation
            	 errorHandler("SecurityTestFail","","");
            	 return;
             }
             if (jqXHR.status != 200) {
             e.preventDefault();
             var code=getResponseStatusCode(jqXHR);
             errorHandler(code,'','pushManageAlerts');
             } else {
             
             pushPreference = responseData;
                                   
             putDataToCache(pageId, pushPreference);
            
              }                     
             
              },
             error : function(jqXHR, textStatus, errorThrown) {
             hideSpinner();
             var code=getResponseStatusCode(jqXHR);
             errorHandler(code,'','pushManageAlerts');
              }
             });
        }
        return pushPreference;
    }
    catch(err)
	{
        hideSpinner();
		//showSysException(err);
	}
}
/*
 * Description:Populates the page divs for the Manage Notification page. Sets the Dynamic content 
 
 */
dfs.crd.push.manage.populateManageNotificationPageDivs = function(responseData, pageId)
{
	try
    {
        // Resetting all global variables
        dfs.crd.push.manage.isChangeMade = false;
        isNumberAdded = false;
        dfs.crd.push.manage.changeTextNumber = false;
        dfs.crd.push.manage.validateTextNumber = false;
    	rePopulateDivFlag = false;
		if(fromPageName == "faqDeviceAlerts" || fromPageName == "manageAlertsTermsAndConditions")
			rePopulateDivFlag = true;
	
        var preferenceDetails = responseData["remindersEnrollResultsVO"];
	    if(preferenceDetails == null)
        {
            errorHandler('','','pushManageAlerts');
            return;
        }
		
        var prefCategory = preferenceDetails["prefTypeCodesToDisplay"];
        var optinMessageError = preferenceDetails["optInMsgInd"];
        var phoneNumber = preferenceDetails["phoneNumber"];        
        dfs.crd.push.manage.phoneCarrier = preferenceDetails["carrier"];
        if(dfs.crd.push.manage.phoneCarrier != "" && dfs.crd.push.manage.phoneCarrier != null)
            dfs.crd.push.manage.phoneCarrier = dfs.crd.push.manage.phoneCarrier.split("@")[1];
        
        /* PN 13.1 Changes*/
        var arrMobCarrier = [];
        $(".pn-page #changenumber option").each(function()
                             {
                                arrMobCarrier.push($(this).val());
                             });
		if($.inArray(dfs.crd.push.manage.phoneCarrier,arrMobCarrier) < 0 && !isEmpty(dfs.crd.push.manage.phoneCarrier))
        {
            $("#changenumber #custom-carrier").val(dfs.crd.push.manage.phoneCarrier);
            dfs.crd.push.manage.phoneCarrier = "other";

			$("#changenumber select").val(dfs.crd.push.manage.phoneCarrier);
			
        }else{
		$("#changenumber select").val(dfs.crd.push.manage.phoneCarrier);
		}
			 changeNumberselect = document.getElementById("changenumberSelect");
			 selectedText = changeNumberselect.options[changeNumberselect.selectedIndex].text;
			$("#activitySelection .ui-btn-text").text(selectedText);
        
        var preferences = preferenceDetails["preferences"];
        var preferenceCodeOutage = preferenceDetails["cardProductGroupOutageMode"];
        dfs.crd.push.manage.pushEnabled = preferenceDetails["pnValidInd"];
        
		if(rePopulateDivFlag && dfs.crd.push.manage.pageCachingVal["pushSlider"] == "P")
			{
			  dfs.crd.push.manage.pushEnabled = "P";
			}
		else if(rePopulateDivFlag && dfs.crd.push.manage.pageCachingVal["pushSlider"] == "Y")
			{
			  dfs.crd.push.manage.pushEnabled = "Y";
			}
		else if(rePopulateDivFlag && dfs.crd.push.manage.pageCachingVal["pushSlider"] == "")
			{
			  dfs.crd.push.manage.pushEnabled = "P";
			}
			// Load Default, Min and Max Form Values for Manage Device alert preference
			var balaDefAmt = preferenceDetails["balaDefAmt"];    //   Default Value - Account Balance exceeds a specified amount 
			var crltDefAmt = preferenceDetails["crltDefAmt"];    //   Default Value - Nearing Account Credit Line 
			var mlrwDefAmt = preferenceDetails["mlrwDefAmt"];    //   Default Value - CashBack Bonus Reaches Specified amount 
			var mrrwDefAmt = preferenceDetails["mrrwDefAmt"];    //   Default Value - Miles Bonus Reaches Specified amount 
			var tamtDefAmt = preferenceDetails["tamtDefAmt"];    //   Default Value - A Purchase Exceeds a specific amount 
			var crltAmtOptions = preferenceDetails["crltAmtOptions"]; 
		   
			$('[name=balaDefAmt]').val(balaDefAmt);
			$('[name=crltDefAmt]').val(crltDefAmt);
			$('[name=mlrwDefAmt]').val(mlrwDefAmt);
			$('[name=mrrwDefAmt]').val(mrrwDefAmt);
			$('[name=tamtDefAmt]').val(tamtDefAmt);
			
			$('[name=balaDefAmt]').attr('placeholder',balaDefAmt);
			$('[name=crltDefAmt]').attr('placeholder',crltDefAmt);
			$('[name=mlrwDefAmt]').attr('placeholder',mlrwDefAmt);
			$('[name=mrrwDefAmt]').attr('placeholder',mrrwDefAmt);
			$('[name=tamtDefAmt]').attr('placeholder',tamtDefAmt);
			
			$.each(crltAmtOptions, function(index,value) {
               var options = '<option value='+value+'>$'+value+'</option>';
               $("#notifsettings").find('[id^=CRLT]').find('[id^=VALUE]').append(options);
               });
        		
	    dfs.crd.push.manage.balaMaxAmt = preferenceDetails["balaMaxAmt"];
        dfs.crd.push.manage.balaMinAmt = preferenceDetails["balaMinAmt"];
        dfs.crd.push.manage.crltMaxAmt = preferenceDetails["crltMaxAmt"]; 
        dfs.crd.push.manage.mlrwMaxAmt = preferenceDetails["mlrwMaxAmt"];
        dfs.crd.push.manage.mlrwMinAmt = preferenceDetails["mlrwMinAmt"];
        dfs.crd.push.manage.mrrwMaxAmt = preferenceDetails["mrrwMaxAmt"];
        dfs.crd.push.manage.mrrwMinAmt = preferenceDetails["mrrwMinAmt"]; 
        dfs.crd.push.manage.tamtMinAmt = preferenceDetails["tamtMinAmt"];
        
        $('#tamtDefAmt').html(dfs.crd.push.manage.tamtMinAmt);
        $('#balaDefAmt').html(dfs.crd.push.manage.balaMinAmt);
        
        // set/reset the global status, push on/off slider according to the Override Option selected by the 2nd user
        var textglobalStatus;
        	if (dfs.crd.push.manage.userOverride == true)
			{
				textglobalStatus = "<h3 class='redtext'>Previous account settings will be removed once your preferences are saved. </h3>";
				$("#alertsetting").attr('disabled', false);
				$("#alertsetting").val('on').slider('refresh'); 
				
			}
			else if (dfs.crd.push.manage.userOverride == false)
			{
				textglobalStatus = "<h3 class='redtext boldtext'>Another account is registered for this device. Override previous account to enable push alerts.</h3>";
				$("#alertsetting").attr('disabled', true);
				$("#alertsetting").val('off').slider('refresh');
			}
			else
			{
				// normal user login by registered user, remove any global error status
				textglobalStatus = "";				
				// set the push on/off slider as per json profile
				if (dfs.crd.push.manage.pushEnabled == "P")
				{
					// user has not accepted Push T&C, turn off push enable slider
					$("#alertsetting").val('off').slider('refresh');
					// move the T&C Links div from the top to bottom
					$('#registered-tnclinks').hide();
					$('#unregistered-tnclinks').show();
				}
				else
				{
					if (dfs.crd.push.manage.pushEnabled == "Y")
					{
						$("#alertsetting").val('on').slider('refresh');
					}
					else
					{
						$("#alertsetting").val('off').slider('refresh');
					}
				}
			}
// changes - 13.1
		if(rePopulateDivFlag)
		{
			if (dfs.crd.push.manage.pushEnabled == "P")
				{
					// user has not accepted Push T&C, turn off push enable slider
					$("#alertsetting").val('off').slider('refresh');
					// move the T&C Links div from the top to bottom
					$('#registered-tnclinks').hide();
					$('#unregistered-tnclinks').show();
				}
				else
				{
					if (dfs.crd.push.manage.pushEnabled == "Y")
					{
						$("#alertsetting").val('on').slider('refresh');
					}
					else
					{
						$("#alertsetting").val('off').slider('refresh');
					}
				}
		}
		
        // check json for sms double opt in and set the inline alerts 
        if(optinMessageError)
        {
            $('#doubleoptin-error').show();
			
        }
        
        
        // check json for phone number and show/hide phonenumber-block div and enable validation for sms check
        if(phoneNumber == "" || phoneNumber == null)
        {
            // hide the top phone no region and show the add new number region at the bottom 
            $('#phonenumber-block').hide();
            // add event handler to the SMS Checkboxes to show Addnumber div and add validation for save
            isNumberAdded = false;
            
        }
        else
        {
            if (phoneNumber.length == 10) 
            {
                $('#phonenumber').html(phoneNumber.substring(0, 3) + '-' + phoneNumber.substring(3, 6) + '-' + phoneNumber.substring(6, 10))
            }
            isNumberAdded = true;
        }
        
        $('#global-errors').html(textglobalStatus);
        trackInlineError();
        
        // Show the notification preference list items according to the json list which is based on diff card types
        for (var i=0;i<prefCategory.length;i++)
        {
            $("#notifsettings").find('[id^='+prefCategory[i]+']').removeClass('hideLi').trigger('create');
        }
        
        // Hide the Reward Expandable Block if there are no line items to be displayed for essential and corp card members
        if ($.inArray("5CBB",prefCategory)== -1 && $.inArray("SHPD",prefCategory)== -1 && $.inArray("MRRW",prefCategory)== -1 && $.inArray("MLRW",prefCategory)== -1)
        {
		//13.3 Change Defect Fix :97620
            $("#conditionalRewardHide").hide();
			//13.3 Change Defect Fix :97620
        }
   
		if(rePopulateDivFlag)
		{
			//checkboxes
			for(var i = 0;i<(dfs.crd.push.manage.smsCheckedCached.length);i++)
			{
				if(dfs.crd.push.manage.smsCheckedCached[i] == "5CBB")
					$(".5CBB .pn-txt-chk").attr('checked', 'checked');
				else
					$("#"+dfs.crd.push.manage.smsCheckedCached[i] + " #SMRM").attr('checked', 'checked');								
			}
			for(var i = 0;i < (dfs.crd.push.manage.pushCheckedCached.length);i++)
			{
				if(dfs.crd.push.manage.pushCheckedCached[i] == "5CBB")
					$(".5CBB .pn-push-chk").attr('checked', 'checked');
				else
					$("#"+dfs.crd.push.manage.pushCheckedCached[i] + " #PNRM").attr('checked', 'checked');
			}
			//textboxes
			if(!isEmpty(dfs.crd.push.manage.pageCachingVal['TAMT']))
			{
			 $("#TAMT #VALUE").val(dfs.crd.push.manage.pageCachingVal['TAMT']);	
			 $('[name=tamtDefAmt]').attr('placeholder',dfs.crd.push.manage.pageCachingVal['TAMT']);			 
			}
		  if(!isEmpty(dfs.crd.push.manage.pageCachingVal['MRRW']))
			{
		 	 $("#MRRW #VALUE").val(dfs.crd.push.manage.pageCachingVal['MRRW']);
			 $('[name=mrrwDefAmt]').attr('placeholder',dfs.crd.push.manage.pageCachingVal['MRRW']);
			}
		  if(!isEmpty(dfs.crd.push.manage.pageCachingVal['MLRW']))
			{
			 $("#MLRW #VALUE").val(dfs.crd.push.manage.pageCachingVal['MLRW']);
			 $('[name=mlrwDefAmt]').attr('placeholder',dfs.crd.push.manage.pageCachingVal['MLRW']);
			}
		  if(!isEmpty(dfs.crd.push.manage.pageCachingVal['BALA']))
			{
		     $("#BALA #VALUE").val(dfs.crd.push.manage.pageCachingVal['BALA']);
			 $('[name=balaDefAmt]').attr('placeholder',dfs.crd.push.manage.pageCachingVal['BALA']);
			}
		 if(!isEmpty(dfs.crd.push.manage.pageCachingVal['CRLT']))
			{
			 $('[name=crltDefAmt]').attr('placeholder',dfs.crd.push.manage.pageCachingVal['CRLT']);
			 $('[name=crltDefAmt]').val(dfs.crd.push.manage.pageCachingVal['CRLT']);
			}
		if((dfs.crd.push.manage.pageCachingVal["changenumber_flag"]) == "true")
			{
					if(!isEmpty(dfs.crd.push.manage.pageCachingVal["phoneno_placeholder"]))
					{
						var placeHolder = dfs.crd.push.manage.pageCachingVal["phoneno_placeholder"];
					   
					    $('#txt-change-phone-number').attr('placeholder',placeHolder);
					}
					if(!isEmpty(dfs.crd.push.manage.pageCachingVal["changeno_phone"]))
					{
						$("#txt-change-phone-number").val(dfs.crd.push.manage.pageCachingVal["changeno_phone"]);					  
					}
					var ind = (dfs.crd.push.manage.pageCachingVal["select_index"]);
					$('#changenumberSelect').get(0).selectedIndex = ind;

					 changeNumberselect = document.getElementById("changenumberSelect");
					 selectedText = changeNumberselect.options[changeNumberselect.selectedIndex].text;
					$("#activitySelection .ui-btn-text").text(selectedText);

					if(dfs.crd.push.manage.pageCachingVal["changeno_carrierselect"] == "Other")
					{
					  $(".change-msg-dot-com").show();
					  $("#changenumber #custom-carrier").val(dfs.crd.push.manage.pageCachingVal['changeno_carrier']);
					}
					else
					{
					  $(".change-msg-dot-com").hide();
					}
					$("#changenumber").slideDown(300);
                    $(".changenumber a").text("(cancel)");
                    dfs.crd.push.manage.changeTextNumber = true;
			}
			if(!isEmpty(dfs.crd.push.manage.pageCachingVal["addnumber_phone"]))
				{
					$("#txt-phone-number").val(dfs.crd.push.manage.pageCachingVal["addnumber_phone"]);					  
				}
			var ind = (dfs.crd.push.manage.pageCachingVal["select_index_add"]);
			$('#addnumberSelect').get(0).selectedIndex = ind;
			
			var addCarrierText = dfs.crd.push.manage.pageCachingVal["addno_carrierselect"];
			if(addCarrierText == "Other")
				{
					$(".add-msg-dot-com").show();
					var addCarrier = dfs.crd.push.manage.pageCachingVal["addno_carrier"];
					if(!isEmpty(addCarrier))
						{
						  $("#addnumber #custom-carrier").val(addCarrier);
						}
				}
				else
				{
					$(".add-msg-dot-com").hide();
				}			
		}
		else
		{
			dfs.crd.push.manage.smsChecked = [];
			dfs.crd.push.manage.pushChecked = [];
			for (var i=0;i<preferences.length;i++)
			{
				// mark check boxes as checked for each JSON objects for both SMS (SMRM) and PUSH (PNRM)
				if (preferences[i]["custAccptInd"] == "Y" || preferences[i]["custAccptInd"] == "P")
				{
					$("#notifsettings").find('[id^='+preferences[i]["prefTypeCode"]+']').find(':checkbox[id^='+preferences[i]["categoryId"]+']').attr('checked', true);
				}
				
				// set values in the preference textboxes for each JSON objects for both SMS (SMRM) and PUSH (PNRM)
				if (preferences[i]["params"].length > 0)
				{
					$("#notifsettings").find('[id^='+preferences[i]["prefTypeCode"]+']').find('[id^=VALUE]').val(preferences[i]["params"][0]["parmValue"]);
				}
				
				// save the preferences for SMS and Push in global variable
				if (preferences[i]["categoryId"] == "SMRM")
				{   
					dfs.crd.push.manage.smsChecked.push(preferences[i]["prefTypeCode"]);
					dfs.crd.push.manage.smsChecked[preferences[i]["prefTypeCode"]] = [preferences[i]["custAccptInd"]];
				}
				if (preferences[i]["categoryId"] == "PNRM")
					dfs.crd.push.manage.pushChecked.push(preferences[i]["prefTypeCode"]);				
			}
		}
        /*
         * Utility function to validate 'amount' field. 
         * This doesn't accepts character data in the input form. If User does, warn him by showing a standard error message.
         */
         $(".textbox_alertspage,.phone_number").keypress(function(event) {
                                                        dfs.crd.push.manage.isChangeMade = true;
                                                        var controlKeys = [8, 9, 13, 35, 36, 37, 39];
                                                        var isControlKey = controlKeys.join(",").match(new RegExp(event.which));
                                                        if (!event.which || (49 <= event.which && event.which <= 57) || (48 == event.which && $(this).attr("value")) ||  isControlKey) { 
                                                        if($(this).hasClass('input_hightlight_error')) {
                                                         $(this).removeClass("input_hightlight_error");
                                                        }
                                                        return;
                                                        } else {
                                                         $(this).addClass("input_hightlight_error");
                                                         event.preventDefault();
                                                        }                                            
                                                        });        
       $(".textbox_alertspage,.phone_number,#changenumber #custom-carrier,#addnumber #custom-carrier").focusout(function() {
														$('#global-errors').html('');
                                                        dfs.crd.push.manage.isChangeMade = true;
                                                        if (dfs.crd.push.manage.validateFields()) 
                                                        {														
                                                            return;
                                                        } else 
                                                        {
                                                            $('body').animate({scrollTop:0}, 'slow');
                                                        }                                
                                                        });        
        $(".add_phone_number").focusout(function() {
                                        $('#global-errors').html('');
                                        dfs.crd.push.manage.addnumberChange = true;
                                        if (dfs.crd.push.manage.validateFields())
                                        {											
                                        return;
                                        } else
                                        {
                                        $('body').animate({scrollTop:0}, 'slow');
                                        }
                                        });
        
    var stored_number;
	$('.phone_number').blur(function(){
	stored_number = $(this).val();
	if($(".phone_number").val().length == 0){
		$('.phone_number').attr("placeholder","Phone number");
		}
	else{
	$('.phone_number').attr("placeholder",stored_number);
	}
    if(chkForNumberRepeat(stored_number) == true) {        
        $('.phone_number').addClass("input_hightlight_error");
    }
	dfs.crd.push.manage.checkLengthofTextbox();
	});

        $('.phone_number').focus(function(){	
                                 dfs.crd.push.manage.checkLengthofTextbox();
                                 });
        
        function setValChangeNumClose(){
			$("#changenumber").slideUp(300);
			$(".changenumber a").text("(Edit)");
			dfs.crd.push.manage.changeTextNumber = false;
		}

		function setValChangeNumOpen(){
				s.prop1='PUSH_HANDSET_MANAGE_ALERTS_PH_CHANGE_TXT';
				$('.phone_number').val("");
				var a = $('#phonenumber').text();
				$('.phone_number').prop('placeholder',a);
				$("#changenumber select").val(dfs.crd.push.manage.phoneCarrier);                                    
				    changeNumberselect = document.getElementById("changenumberSelect");
					selectedText = changeNumberselect.options[changeNumberselect.selectedIndex].text;
					$("#activitySelection .ui-btn-text").text(selectedText);
				if(dfs.crd.push.manage.phoneCarrier == 'other')
				{
				$(".change-msg-dot-com").show();
                if(preferenceDetails["carrier"] != "" && preferenceDetails["carrier"] != null)
						var carrierVal = preferenceDetails["carrier"].split("@")[1];
					$("#changenumber #custom-carrier").val(carrierVal);	
				}
				else
				{
				$(".change-msg-dot-com").hide();
				}                                   
				$("#changenumber").slideDown(300);
				$(".changenumber a").text("(cancel)");									
				dfs.crd.push.manage.changeTextNumber = true;
		}
        $(".changenumber a").toggle(function () {
									if($(this).text() == "(cancel)")
									  { 
										setValChangeNumClose();
									  }
									  else
									  {
											setValChangeNumOpen();                  
									  }
								    },
                                    function(){
										setValChangeNumClose()
if($("#txt-change-phone-number").hasClass('input_hightlight_error'))
				$("#txt-change-phone-number").removeClass("input_hightlight_error"); 												
			if($("#changenumber #custom-carrier").hasClass('input_hightlight_error')){											
				$("#changenumber #custom-carrier").removeClass("input_hightlight_error");
				$('#global-errors').html('');
			}
			});
        
        // new email message script starts for 13.1 Change
        $('.pn-page #changenumber select').live('change', function(){
                                                var selecteddealer = $.trim($(this).find(":selected").text());
                                                if(selecteddealer == 'Other')
                                                {
                                                $(".change-msg-dot-com").show();
                                                }else
                                                {
                                                $(".change-msg-dot-com").hide();
                                                }
                                                });
        
        $('.pn-page #addnumber select').live('change', function(){
                                             var selecteddealer = $.trim($(this).find(":selected").text());
                                             if(selecteddealer == 'Other')
                                             {
                                                 $(".add-msg-dot-com").show();                                  			
                                             }else
                                             {
												$(".add-msg-dot-com").hide();
                                             }
                                             });
        /*
         *Check the current state of the checkbox and Enable / disable accordingly.
         */
        var val = $('select#alertsetting').val();
        
        if (val == "off") {
            $("#notifsettings").find(':checkbox[id^=PNRM]').attr('disabled', true);
            $("#notifsettings .ui-grid-b .ui-block-b").css("opacity","0.3");
        } else if (val == "on") {
            $("#notifsettings").find(':checkbox[id^=PNRM]').attr('disabled', false);
            $("#notifsettings .ui-grid-b .ui-block-b").css("opacity","1.0");
        }
        
        $('select#alertsetting').change(function () {
                                        if ($(this).val() == 'on')
                                        {
											$("#notifsettings").find(':checkbox[id^=PNRM]').attr('disabled', false);
											$("#notifsettings .ui-grid-b .ui-block-b").css("opacity","1");
                                        }
                                        else
                                        {
											$("#notifsettings").find(':checkbox[id^=PNRM]').attr('disabled', true);
											$("#notifsettings .ui-grid-b .ui-block-b").css("opacity","0.3");
                                        }
                                        dfs.crd.push.manage.isChangeMade = true;
                                    });
        /* 
         * Show / Hide the Phone Number block based on Text Type checkbox on manage Alert page if number is not registered.
         */
        // If there is no Contact No. assigned, validate everytime if any SMS Checkbox is checked or not and show / hide the addnumber
        $(".pn-txt-chk").change( function() {
                            if(!isNumberAdded)
                                {
                                    var checkedElements = $("input.pn-txt-chk:checked").length;
                                    if(checkedElements) {
                                    // show the phone container.
                                    $('#addnumber').show();
                                    dfs.crd.push.manage.validateTextNumber = true;
                                    }
                                    if(checkedElements == 0 ) {
                                    // hide the phone container.	
                                    $('#addnumber').hide();
                                    dfs.crd.push.manage.validateTextNumber = false;
                                    }
                                }
                                dfs.crd.push.manage.isChangeMade = true;
        });
        
        // enable save change on click of any push checkbox
        $(".alert-chk").change( function() {
                                    dfs.crd.push.manage.isChangeMade = true;
                               });
        
        // enable save change on click of any textbox for alerts
        // check initially if there are any SMS Checkboxes checked in the saved profile and if so, set validateTextNumber as true.
        if(!isNumberAdded)
        {
            var checkedElements = $("input.pn-txt-chk:checked").length;
            if(checkedElements) {
                // show the phone container.	
                $('#addnumber').show();
                dfs.crd.push.manage.validateTextNumber = true;
            }
            if(checkedElements == 0 ) {
                // hide the phone container.	
                $('#addnumber').hide();
                dfs.crd.push.manage.validateTextNumber = false;
            }
        }
        $("#notifsettings").find('[id^=CRLT]').find('[id^=VALUE]').change(function() {
                                          var errSel = 'You selected more than a maximum limit.';
                                          if( $(this).val() > dfs.crd.push.manage.crltMaxAmt) 
                                          { 
                                            $('#global-errors').html(errSel).addClass('standard_error_msg');
                                            $('body').animate({scrollTop:0}, 'slow');
                                            $("#notifsettings").find('[id^=CRLT]').find('[id^=VALUE]').focus();
                                            $("#id-notify-save").addClass("ui-disabled");
											trackInlineError();
                                          }
                                          else
                                          {
                                            $('#global-errors').html("").hide();
                                            $("#id-notify-save").removeClass("ui-disabled");
                                          }
                                          dfs.crd.push.manage.isChangeMade = true;
                                          });
		}
    catch(err)
	{
		//showSysException(err);
	}
}

dfs.crd.push.manage.postPushPreferenceData = function() 
{
	try{
        dfs.crd.push.manage.pushCheckedPost = [];
        dfs.crd.push.manage.smsCheckedPost = [];
		var newDate = new Date();
		var PUSHPREFERENCEURL = RESTURL + "contact/v1/preferences/enrollments?" + newDate;
        var pnSliderParam, acctOverrideParam, phoneNumberParam, carrierParam;
        var pushEnabledChanged = false;
        var preferencesList = "";
        var preferenceJSON;
        var dataJSONString;
        var varCustAccptInd;
        var checkedElements = $("input.alert-chk:checked");		
        for (var i=0;i < checkedElements.length;i++)
        {			
            // get the alert type SMS(SMRM) / Push(PNRM) from the ID of the check box which are marked as checked 
            var alertType = $(checkedElements[i]).attr('id');
            // get the alert Name Statement(STMT) etc from the ID of Li containing the above check box 
            //var alertName = $(checkedElements[i]).parent().parent().parent().parent().attr('id');
            var alertName = $(checkedElements[i]).parent().parent().parent().parent().parent().attr('id');
            // get the value (if exists) of the above check box 
            // save the current preference for SMS and Push 
            if (alertType == "SMRM")
            {
                dfs.crd.push.manage.smsCheckedPost.push(alertName);
                if(dfs.crd.push.manage.smsChecked[alertName] == undefined || dfs.crd.push.manage.smsChecked[alertName] == null)
                    varCustAccptInd = "P";
                else
                    varCustAccptInd = dfs.crd.push.manage.smsChecked[alertName][0];
                
                if(alertName == "FRAD")
                    varCustAccptInd = "Y";
				var textAlert = $("#"+alertName).children(".ui-grid-b").children(".ui-block-c").children('p').children('span').text();
				dfs.crd.sct.trackTxtCheckboxClicks(textAlert);
				}              
            if (alertType == "PNRM")
            {
                dfs.crd.push.manage.pushCheckedPost.push(alertName);
                varCustAccptInd = "Y";
				var deviceAlert = $("#"+alertName).children(".ui-grid-b").children(".ui-block-c").children('p').children('span').text();
				dfs.crd.sct.trackDeviceCheckboxClicks(deviceAlert);
            }
            
            var alertValue = $(checkedElements[i]).parent().parent().parent().parent().find('[id^=VALUE]').val();
            // TBD Dopdown list value
           
            // check for the value in the preference type and construct the JSON dynamically
            if (alertValue != null)
            {
                preferenceJSON = {
                "params": [
                           {
                           "parmCode": "AMT",
                           "parmValue": alertValue
                           }
                           ],
                "categoryId": alertType,
                "custAccptInd": varCustAccptInd,
                "prefTypeCode": alertName
                };
            }
            else
            {
                preferenceJSON = {
                    "params": [],
                    "categoryId": alertType,
                    "custAccptInd": varCustAccptInd,
                    "prefTypeCode": alertName
                };
            }
            
            
            if (preferencesList == "")
                preferencesList = JSON.stringify(preferenceJSON);
            else
                preferencesList = preferencesList + "," + JSON.stringify(preferenceJSON);
	    }
        
        // compare current with earlier preference to find the changes and include them in the JSON as opted out
        for (var i=0;i<dfs.crd.push.manage.smsChecked.length;i++)
        {
            if ($.inArray(dfs.crd.push.manage.smsChecked[i],dfs.crd.push.manage.smsCheckedPost)== -1)
            {
                preferenceJSON = {
                    "params": [],
                    "categoryId": "SMRM",
                    "custAccptInd": "N",
                    "prefTypeCode": dfs.crd.push.manage.smsChecked[i]
                };
            
            if (preferencesList == "")
                preferencesList = JSON.stringify(preferenceJSON);
            else
                preferencesList = preferencesList + "," + JSON.stringify(preferenceJSON);
            }
        }
        
        for (var i=0;i<dfs.crd.push.manage.pushChecked.length;i++)
        {
            if ($.inArray(dfs.crd.push.manage.pushChecked[i],dfs.crd.push.manage.pushCheckedPost)== -1)
            {
                preferenceJSON = {
                    "params": [],
                    "categoryId": "PNRM",
                    "custAccptInd": "N",
                    "prefTypeCode": dfs.crd.push.manage.pushChecked[i]
                };
                
            if (preferencesList == "")
                preferencesList = JSON.stringify(preferenceJSON);
            else
                preferencesList = preferencesList + "," + JSON.stringify(preferenceJSON);
            }
        }        
        // get the status of Push Notification slider and find if the user has changed his status
        // if so set the params as "" in JSON so that EContact Service will not be called by the REST Service         
        pnSliderParam = $('select#alertsetting').val();        
        if (pnSliderParam == "off")
        {
            if(dfs.crd.push.manage.pushEnabled != "Y" && !dfs.crd.push.manage.userOverride)
                pnSliderParam = ""; 
            else
				{
					pnSliderParam = "N"; 
					dfs.crd.sct.disableNotificationClick();
				}				
        }
        else
        {
            if(dfs.crd.push.manage.pushEnabled == "Y"  && !dfs.crd.push.manage.userOverride)
                pnSliderParam = "";  
            else
				{
					pnSliderParam = "Y"; 
					dfs.crd.sct.trackNotificationClick();
				}
        }
        
        // set the json value for user override option
        if(dfs.crd.push.manage.userOverride)
            acctOverrideParam = "T";
        else
            acctOverrideParam = "F";
        
        phoneNumberParam = "";
        carrierParam = "";
        
        // get the phone number and carrier if user is enetering the contact for the first time
        if (dfs.crd.push.manage.validateTextNumber)
        {
            phoneNumberParam = $("#txt-phone-number").val();
            dfs.crd.push.manage.phoneCarrier = $("#addnumber option:selected").val();
            if(dfs.crd.push.manage.phoneCarrier == "other")
            {
                dfs.crd.push.manage.phoneCarrier = $("#addnumber #custom-carrier").val();
            }
        }
        
        // get the phone number and carrier if user is changing his contact
        if (dfs.crd.push.manage.changeTextNumber)
        {
            phoneNumberParam = $("#txt-change-phone-number").val();
            dfs.crd.push.manage.phoneCarrier = $("#changenumber option:selected").val();
            if(dfs.crd.push.manage.phoneCarrier == "other")
            {
                dfs.crd.push.manage.phoneCarrier = $("#changenumber #custom-carrier").val();
            }			
        }
        
        if(phoneNumberParam != "" && dfs.crd.push.manage.phoneCarrier != "")
            carrierParam = phoneNumberParam + "@" + dfs.crd.push.manage.phoneCarrier;
        
        dataJSONString = '{"preferences"' + ':[' + preferencesList + '],"vid":"' + vendorID + '","deviceOS":"' + getClientPlat() + "_" + APPVER + '","osVersion":"' + getOsVersion()  + '","deviceID":"' + getDeviceID()  + '","regStatus":"' + pnSliderParam + '","accntOverrideInd":"' + acctOverrideParam + '","phoneNumber":"' + phoneNumberParam + '","carrier":"' + carrierParam + '"}';
        if (!dfs.crd.push.manage.validateFields())
        {  
            return;
        }        
        $('body').animate({scrollTop:0}, 'slow');        
        showSpinner();
		$.ajax({
			type : "POST",
			url : PUSHPREFERENCEURL,
			async : true,
			dataType : 'json',
			data :dataJSONString,
			headers:preparePostHeader(),
			success : function(response, status, jqXHR) {
              hideSpinner();
				if (jqXHR.status != 200 & jqXHR.status != 204) {
					var code=getResponseStatusCode(jqXHR);
					errorHandler(code,'','pushManageAlerts');
				} else {
                        var resultCode = response["resultCode"];
           
                        // If either both or any of the EComprof & EContact services succeed, update the form and scroll up
                        if (resultCode == "200" || resultCode == "201" || resultCode == "202"){
                            // user preference saved successfully, update the status text and scroll up
                            var textSuccessfullUpdate = "<h3 class='greentext'>Text and push alerts settings have been updated.</h3>";
                            $('#global-errors').html(textSuccessfullUpdate).show();
                            
                            var str = $('.phone_number').val();
                            var index = $("#changenumber select").get(0).selectedIndex;
                            if (str.length == 10) {
                            var a = $('#phonenumber').html(str.substring(0, 3) + '-' + str.substring(3, 6) + '-' + str.substring(6, 10));
                            $("#changenumber").slideUp(300);							
                            $(".changenumber a").text("(Edit)");
                            }                            
                            // kill the cached preference on successful post and set userOverride to false if user has already overriden
                            killDataFromCache("MANAGEALERTS");
                            if (acctOverrideParam == "T")
                                {

                                    //dfs.crd.lilo.otherUser = false;
									globalOtherUser = false;
                                    HybridControl.prototype.setOtherUserFlag(null,globalOtherUser);
                                    dfs.crd.push.manage.userOverride = null;
                                }
                            dfs.crd.push.manage.isChangeMade = false;
                        }
				}
               },
            error : function(jqXHR, textStatus, errorThrown) {                
                hideSpinner();
				var code=getResponseStatusCode(jqXHR);
				errorHandler(code,'','pushManageAlerts');
			}
		});
    }catch(err)
    {
        hideSpinner();
		//showSysException(err);
	}
}

/*-------Final overlay -------*/
dfs.crd.push.manage.fnOverlay = function(customSettings){
try{	
	var isOpen = false;	
	var defaultSettings = {"ContentHolder": "", "PageUrl": "", "marginLeft": "5", "marginRight": "0", "marginTop": "0","marginBottom": "0","paddingLeft": "0", "paddingRight": "0", "paddingTop": "0","paddingBottom": "0", "Top": "0", "Left": "0", "width":"400", "height":"500", "portraitWidth":"0", "portraitHeight":"0", "resize": true};
	$.extend(defaultSettings, customSettings);	
	var contentHolder = defaultSettings.ContentHolder;
	var scrollOffsetHeight = $(window).scrollTop();
	var scrollOffsetLeft = $(window).scrollLeft();
	var parentPageId =  '';
	var parentPageIdObj = '';
	var ovrLayWrapperParent = $(contentHolder).parent();
	var resizeWin = false;
    
	if( $("div[data-role='page']:visible") && $("div[data-role='page']:visible").attr("id") != 'undefined'){
		parentPageId =  $("div[data-role='page']:visible").attr("id");
		parentPageIdObj = $("#"+parentPageId);
	}	
	//function to display the boxy
	function showOverlayBox() {
		//if box is not set to open then don't do anything
		if( isOpen == false ) return;
		var tmpHeight = 0;
		var ovrLayHeight = 0; // because iphone is behaving differently in full screen and small screen cases
		var pgHeight = $("div[data-role=page]:visible").outerHeight();
		var calculatedHeight  = 0;
		var calculatedWidth = 0;
		var ww, wh = 0;
		scrollOffsetHeight = $(window).scrollTop();
		scrollOffsetLeft = $(window).scrollLeft();
        
		if(defaultSettings.resize){	
            
			wh = $(window).height() - ( parseInt(defaultSettings.marginTop) + parseInt(defaultSettings.marginBottom) + parseInt(defaultSettings.paddingTop) + parseInt(defaultSettings.paddingBottom));
			ww = $("body").innerWidth() - ( parseInt(defaultSettings.marginLeft) + parseInt(defaultSettings.marginRight) + parseInt(defaultSettings.paddingLeft) + parseInt(defaultSettings.paddingRight) );		
            
			calculatedHeight = wh;
			calculatedWidth = ww;				
			ovrLayHeight = "100%";
			defaultSettings.Left = 0;
			defaultSettings.Top = 0; 
			$(window).scrollTop(0);
		}else{
			
			//Check for orientation		
			if( dfs.crd.push.manage.orientation==0 || dfs.crd.push.manage.orientation ==180) {
				//If user has specified portrait height/width the use that
				// else user the same width for both cases
				if( parseInt(defaultSettings.portraitHeight) >0){
					calculatedHeight = defaultSettings.portraitHeight;
				}else{
					calculatedHeight = defaultSettings.height;
				}
				if( parseInt(defaultSettings.portraitWidth) >0){
					calculatedWidth = defaultSettings.portraitWidth;
				}else{
					calculatedWidth = defaultSettings.width;
				}			
			}else if(dfs.crd.push.manage.orientation==90 || dfs.crd.push.manage.orientation ==-90){
				calculatedHeight = defaultSettings.height;
				calculatedWidth = defaultSettings.width;
			}else{
				calculatedHeight = defaultSettings.height;
				calculatedWidth = defaultSettings.width;	
			}
			
			//Calculate the final height/width
			wh = parseInt(calculatedHeight) + ( parseInt(defaultSettings.marginTop) + parseInt(defaultSettings.marginBottom) + parseInt(defaultSettings.paddingTop) + parseInt(defaultSettings.paddingBottom) );
			ww = parseInt(calculatedWidth) + ( parseInt(defaultSettings.marginLeft) + parseInt(defaultSettings.marginRight) + parseInt(defaultSettings.paddingLeft) + parseInt(defaultSettings.paddingRight) );
            
			//calculate the left and top				
			defaultSettings.Left = ($(window).width() - ww)/2;
			defaultSettings.Top = ( $(window).height()- wh )/2 + scrollOffsetHeight; 
			ovrLayHeight = pgHeight+"px";
		}		
		
		if(resizeWin == true)
		{
			resizeWin = false;
			$(contentHolder).css({
                                 marginLeft: defaultSettings.marginLeft+"px",
                                 marginRight: defaultSettings.marginRight+"px",
                                 marginTop: defaultSettings.marginTop+"px",
                                 marginBottom: defaultSettings.marginBottom+"px",
                                 paddingLeft: defaultSettings.paddingLeft+"px",
                                 paddingRight: defaultSettings.paddingRight+"px",
                                 paddingTop: defaultSettings.paddingTop+"px",
                                 paddingBottom: defaultSettings.paddingBottom+"px",
                                 top: defaultSettings.Top+"px",
                                 left: defaultSettings.Left+"px",
                                 position:'absolute',
                                 width: calculatedWidth+"px",
                                 height: calculatedHeight+"px",					
                                 });
		}else{
			// set the properties of the overlay box, the left and top positions
			$(contentHolder).css({
                                 marginLeft: defaultSettings.marginLeft+"px",
                                 marginRight: defaultSettings.marginRight+"px",
                                 marginTop: defaultSettings.marginTop+"px",
                                 marginBottom: defaultSettings.marginBottom+"px",
                                 paddingLeft: defaultSettings.paddingLeft+"px",
                                 paddingRight: defaultSettings.paddingRight+"px",
                                 paddingTop: defaultSettings.paddingTop+"px",
                                 paddingBottom: defaultSettings.paddingBottom+"px",
                                 top: defaultSettings.Top+"px",
                                 left: defaultSettings.Left+"px",
                                 position:'absolute',
                                 width: calculatedWidth+"px",
                                 height: calculatedHeight+"px",	
                                 "-webkit-perspective": "1000",
                                 "-webkit-transform-style": "preserve-3d",
                                 "-webkit-transform": "scale(0)",
                                 "-webkit-transition": "all 0.3s ease-in",
                                 'display':'block'		
                                 });
		}
		
		// set the window background for the overlay. i.e the body becomes darker
		$('.overlay').css({
                          display:'block',
                          width: '100%',
                          height: ovrLayHeight,
                          opacity: '0.7',
                          });
	}
	function doOverlayOpen() {		
		var ovrLay = $("<div class='overlay'></div>");		
		var ovrLayWrapper = $(contentHolder).detach();
        
		if(isOpen == false){			
			//set status to open
			isOpen = true;
			$("body").append(ovrLayWrapper);
			if( $(".overlay").length == 0)
				$("body").append(ovrLay);
			$("body").css("overflow", "hidden");
			$(contentHolder).removeAttr("style");
			showOverlayBox();		
            
			$('.overlay').show(function(){ 			
                               if( parentPageId !='' && defaultSettings.resize){
                               $('.tooltip').hide();
                               parentPageIdObj.hide();
                               tmpHeight = $("html").css("min-height");
                               $("html").css("min-height", "100%");
                               $(contentHolder).css('-webkit-transform','scale(1)').fadeIn(100, function(){
                                                                 $("body").animate({scrollTop: 0}, 500);	
                                                                                           });
                               }else{
                               $(contentHolder).css('-webkit-transform','scale(1)');
                               }			
                               });
		}		
	}
	function doOverlayClose() {	
		s.prop1='PUSH_HANDSET_NOTSAVE_CHANGES_GOBACK_TXT';
		$("body").css("overflow", "auto");	
		if(isOpen == true){		
			//set status to closed
			isOpen = false;
			if( parentPageId !='' && defaultSettings.resize){
				$('.rd-faq-main').css("opacity", "1");			
				if(tmpHeight >0){
					$("html").css("min-height", tmpHeight+"px");
				}			
			}
			$(contentHolder).css('-webkit-transform','scale(0)').fadeOut(200, function(){
                                                                         $('.overlay').hide().remove();				
                                                                         var ovrLayWrapper = $(contentHolder).detach();
                                                                         
                                                                         parentPageIdObj.fadeIn(400, function(){					
                                                                                                if( parentPageIdObj.find(ovrLayWrapperParent).length > 0){
                                                                                                parentPageIdObj.find(ovrLayWrapperParent).append(ovrLayWrapper);
                                                                                                }else if( parentPageIdObj.length > 0){
                                                                                                parentPageIdObj.append(ovrLayWrapper);
                                                                                                }else{
                                                                                                $("body").append(ovrLayWrapper);
                                                                                                }
                                                                                                $("body").animate({scrollTop: scrollOffsetHeight}, 500);
                                                                                                }).css("display", "");
                                                                         });			
		}				
	}
    
    
    function doOverlayContinue() {	
		s.prop1='PUSH_HANDSET_NOTSAVE_CHANGES_CONTINUE_TXT';
		$("body").css("overflow", "auto");	
		if(isOpen == true){		
			//set status to closed
			isOpen = false;
			if( parentPageId !='' && defaultSettings.resize){
				$('.rd-faq-main').css("opacity", "1");			
				if(tmpHeight >0){
					$("html").css("min-height", tmpHeight+"px");
				}			
			}
			$(contentHolder).css('-webkit-transform','scale(0)').fadeOut(200, function(){
                                                                         $('.overlay').hide().remove();				
                                                                         var ovrLayWrapper = $(contentHolder).detach();
                                                                         
                                                                         parentPageIdObj.fadeIn(400, function(){					
                                                                                                if( parentPageIdObj.find(ovrLayWrapperParent).length > 0){
                                                                                                parentPageIdObj.find(ovrLayWrapperParent).append(ovrLayWrapper);
                                                                                                }else if( parentPageIdObj.length > 0){
                                                                                                parentPageIdObj.append(ovrLayWrapper);
                                                                                                }else{
                                                                                                $("body").append(ovrLayWrapper);
                                                                                                }
                                                                                                $("body").animate({scrollTop: scrollOffsetHeight}, 500);
                                                                                                }).css("display", "");
                                                                         });			
		}
        //navigation('../profile/profileLanding');
		gotoAchome();
	}
    
	// if window is resized then reposition the overlay box
	$(window).resize(function(){
                     resizeWin = true;
                     showOverlayBox();
                     });
	$(window).bind('orientationchange', function(){
                   $(contentHolder).hide(10, function(){
                                         $(contentHolder).fadeIn(100, function(){
                                                                 resizeWin = true;
                                                                 showOverlayBox();
                                                                 });
                                         });
                   });
    
	//Open the pop-up on click
	var pgUrl = customSettings.PageUrl;	
	if( typeof(pgUrl) !='undefined' && pgUrl != ''){
		$(contentHolder).load(pgUrl, function(){
                              if( $(contentHolder).find("div[data-role='page']").length >0){
                              var pgId =  $(contentHolder).find("div[data-role='page']");
                              $(contentHolder).find("*").trigger("create");
                              pgId.show();
                              }		
                              });
	}
    
	// close it when closeLink is clicked
	$('.close-overlay').live("click", doOverlayClose );	
	$('.continue-overlay').live("click", doOverlayContinue );
	//open overlay
	doOverlayOpen();
	}catch(err){
		//showSysException(err);
	}
}

/* Collapsible/ Expandible site catalyst code*/

/*$(".ui-collapsible-heading-toggle").live('click',function()
{
  try
	{
		dfs.crd.sct.trackDescriptionCollapseClick();
	}catch(err){
		//showSysException(err);
	}
});*/
$('#manage-accounts-block,#monitor-spending-block,#rewards-block').live('expand', function(){
    var expandTab = $(this).children(".bluetext").text();
	expandTab=expandTab.substring(0,expandTab.indexOf("click"));
	dfs.crd.sct.trackDescriptionCollapseClick(expandTab);
  }).live('collapse', function(){
	var collapsTab = $(this).children(".bluetext").text();
	collapsTab=collapsTab.substring(0,collapsTab.indexOf("click"));
    dfs.crd.sct.trackDescriptionCollapseClick(collapsTab);
});

$("#manageAlerts-pg").live("pageshow",function(event){
	try{     
		$(this).css("pointer-events","auto");
        /*This changed is for header and footer alignment*/
        $("input[type=text], input[type=tel], input[type=number], select").focus(function(){
                    $("#pg-header, #pg-footer").css("position","absolute");
        });
        $("input[type=text], input[type=tel], input[type=number], select").blur(function(){
                    $("#pg-header, #pg-footer").css("position","fixed");
        });
		}catch(err){
		showSysException(err);
	}
		$(".hidden-element").hide();
		$(".pn-plus").css("background-position","top left");
	$(".hideLiMain").toggle(function(){
		$(this).children(".pn-plus").css("background-position","0px -24px");
		$(this).parents().children(".hidden-element").show();
	
	},function(){
		$(this).children(".pn-plus").css("background-position","0 0");
		$(this).parents().children(".hidden-element").hide();
		
	});
});





