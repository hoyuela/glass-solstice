
/* Name Space */
dfs.crd.push.manage = dfs.crd.push.manage || {};

dfs.crd.push.manage.userOverride = null;
dfs.crd.push.manage.pushEnabled;
dfs.crd.push.manage.validatePushTnC = false;
dfs.crd.push.manage.validateTextNumber = false;
dfs.crd.push.manage.changeTextNumber = false;
dfs.crd.push.manage.smsChecked = [];
dfs.crd.push.manage.pushChecked = [];
dfs.crd.push.manage.smsCheckedPost = [];
dfs.crd.push.manage.pushCheckedPost = [];
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

manageAlertsOverrideLoad = function()
{
    try
    {
    // Page Initialization for Manage Alerts Override screen
    
    // call push registration async service in case the XID was not retrieved properly
    if(vendorID == "")
    {
        dfs.crd.achome.pushRegistrationServiceAsync();
    }
    
     // check if the vendorID is null for iPhone, if it is, then user has turned off notification, display the message
    if(vendorID == "" && getClientPlat()=="iPhone")
    {
        // stop navigation to this page and clear cache to remove back functionality
        errorText = "<br/><br/>Notification for the Discover App is turned off on your mobile device. <br/>Please go into your device settings and enable notification for the Discover app in order to receive Push Alerts.";
        errorHandler("",errorText,"pushManageAlerts");
        //cpEvent.preventDefault();
        //navigation('../pushNotification/manageAlertsError');
    }
    
    // check the Global Variable otherUser and show the Override Settings page as overlay
        //alert("User Override Scenario: " + otherUser);
    if (!dfs.crd.lilo.otherUser)
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
                         dfs.crd.push.manage.userOverride = false;
                         //alert(userOverride);
                         navigation('../pushNotification/manageAlerts');
                         });
        $('#id-override-remove').click(function()
                         {
                         dfs.crd.push.manage.userOverride = true;
                         navigation('../pushNotification/manageAlerts');
                         });
        
    }
    }catch(err){
		showSysException(err);
	}
}


faqDeviceAlertsLoad =  function ()
{

    //	Hide all the Answers of all Questions		
    $(".rdfaq-hidden-element").hide();
    $(".rdfaq-plus,.ui-block-a .rdfaq-que-link").toggle(function(){
            $(this).parents().children(".ui-block-b").children(".rdfaq-plus").css("background-position","bottom center");
            $(this).parents().children(".ui-block-a").children(".rdfaq-hidden-element").show()
    },
    function(){
        $(this).parents().children(".ui-block-b").children(".rdfaq-plus").css("background-position","top center");
        $(this).parents().children(".ui-block-a").children(".rdfaq-hidden-element").hide();
    });
}


manageAlertsLoad = function ()
{
	try{		
        // Page Initialization for Manage Alerts screen
        
        dfs.crd.push.manage.populateManageNotification("MANAGEALERTS");
    
        // Warns user about saving preferences before redirecting to other screen
         
        $('#id-notify-cancel').click(function(){
                                     if(dfs.crd.push.manage.isChangeMade)
                                     {
                                        // redirect the user to without saving alert 
                                        //alert("inside cancel");
                                        $('.ui-loader').hide();
                                            var customSettings = {"ContentHolder": "#overlay_wraper_saveConfirm","height":"200","resize":false,"width":"300"};
                                        dfs.crd.push.manage.fnOverlay(customSettings);
                                     }
                                     else
                                     {
                                        // back button functionality, go back to Profile page
                                        navigation('../profile/profileLanding');
                                     }
                                     });
        
        //  Click Save preference Call Push Preference POST Service
       
        $('#id-notify-save').click(function()
                               {
                                    dfs.crd.push.manage.postPushPreferenceData();
                               });
                
	}catch(err){
		showSysException(err);
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
    
        
    if(dfs.crd.push.manage.validateTextNumber)
    {
        var str = $("#txt-phone-number").val();
        var index = $("#addnumber select").get(0).selectedIndex;
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
    
    if(dfs.crd.push.manage.changeTextNumber)
    {
        var str = $("#txt-change-phone-number").val();
        var index = $("#changenumber select").get(0).selectedIndex;
        if (!dfs.crd.push.manage.validatePhoneNumbers(str)) 
        { 
			$('#global-errors').html(errEmptyPhoneNumber).addClass('redtext boldtext').show();
			$('body').animate({scrollTop:0}, 'slow');
			$("#txt-change-phone-number").addClass("input_hightlight_error");
            return false;
        }
        else
        {
            if($("#txt-change-phone-number").hasClass('input_hightlight_error'))
                $("#txt-change-phone-number").removeClass("input_hightlight_error"); 
        }
        /*if (!validateCarrier(index)) 
        { 
            $('#global-errors').html(errFraudAlert).addClass('redtext boldtext');
            $('body').animate({scrollTop:0}, 'slow');
            //$("#txt-phone-number").focus();
            return false;
        }*/
    }

	
    $('#global-errors').html('');

    return true;
    }
    catch(err)
	{
		showSysException(err);
        return false;
	}
}


dfs.crd.push.manage.validatePhoneNumbers = function(phoneNumber){
 
    var numberLength = phoneNumber.length;
    var numberStartsWith = phoneNumber.substring(0,1);
    var areaCode = phoneNumber.substring(0,3);
    var numberCode = phoneNumber.substring(3,numberLength - 3);
    
    if(numberLength != 10 || numberStartsWith < 2 || numberCode.substring(0,3) == 555 || numberCode.substring(0,1) == 0)
    {
        return false;
    }
    else
    {
        return true;
    }
}

dfs.crd.push.manage.validateCarrier = function(carrier){
    
    if($.inArray("FRAD",dfs.crd.push.manage.smsCheckedPost)>=0 && carrier == 0)
    {
        return false;
    }
    else
    {
        return true;
    }
}

dfs.crd.push.manage.checkLengthofTextbox = function(){
    
    var length_of_number = $('.phone_number').val().length;
    if(length_of_number==0){
        if($('#phonenumber a').length >0){
            var default_number = $('#phonenumber a').html();
        }else{
            var default_number = $('#phonenumber').html();
        }
        default_number = default_number.substr(0,3)+default_number.substr(4,3)+default_number.substr(8,4);
        $('.phone_number').val(default_number);
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
		showSysException(err);
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
            //alert(PUSHPREFERENCEURL + " GET from manage notif, VID: " + vendorID);
            
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
		showSysException(err);
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
        dfs.crd.push.manage.smsCheckedPost = [];
        dfs.crd.push.manage.pushCheckedPost = [];
        
        var preferenceDetails = responseData["remindersEnrollResultsVO"];
        
        var prefCategory = preferenceDetails["prefTypeCodesToDisplay"];
        
        var optinMessageError = preferenceDetails["optInMsgInd"];
        var phoneNumber = preferenceDetails["phoneNumber"];
        
        dfs.crd.push.manage.phoneCarrier = preferenceDetails["carrier"];
        if(dfs.crd.push.manage.phoneCarrier != "" && dfs.crd.push.manage.phoneCarrier != null)
            dfs.crd.push.manage.phoneCarrier = dfs.crd.push.manage.phoneCarrier.split("@")[1];
        
        var preferences = preferenceDetails["preferences"];
        var preferenceCodeOutage = preferenceDetails["cardProductGroupOutageMode"];
        
        dfs.crd.push.manage.pushEnabled = preferenceDetails["pnValidInd"];
        
        //alert("variables : " + phonenumber + pushEnabled + optinMessageError);
		
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
               //$("#idNearingAccCredLine").append(options);
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
            textglobalStatus = "<h3 class='greentext'>Previous account settings will be removed once your preferences are saved. </h3>";
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
            //$('#addnumber').attr('style',"");
            // add event handler to the SMS Checkboxes to show Addnumber div and add validation for save
            isNumberAdded = false;
            
        }
        else
        {
            //$('#phonenumber-block')..attr('style',"");
            if (phoneNumber.length == 10) 
            {
                // alert (" phone no value load " + phoneNumber.length);
                $('#phonenumber').html(phoneNumber.substring(0, 3) + '-' + phoneNumber.substring(3, 6) + '-' + phoneNumber.substring(6, 10))
            }
            isNumberAdded = true;
        }
        
        $('#global-errors').html(textglobalStatus);
        
        
        // Show the notification preference list items according to the json list which is based on diff card types
        for (i=0;i<prefCategory.length;i++)
        {
            $("#notifsettings").find('[id^='+prefCategory[i]+']').removeClass('hideLi').trigger('create');
        }
        
        // Hide the Reward Expandable Block if there are no line items to be displayed for essential and corp card members
        if ($.inArray("5CBB",prefCategory)== -1 && $.inArray("SHPD",prefCategory)== -1 && $.inArray("MRRW",prefCategory)== -1 && $.inArray("MLRW",prefCategory)== -1)
        {
            //$("#rewards-block").addClass('hideLi');
            $("#rewards-block").hide();
        }
        
        //$.mobile.activePage.trigger('updatelayout');
        
        // read the json preferences and check or set values accordingly
        dfs.crd.push.manage.smsChecked = [];
        dfs.crd.push.manage.pushChecked = [];
        
        for (i=0;i<preferences.length;i++)
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
            {   dfs.crd.push.manage.smsChecked.push(preferences[i]["prefTypeCode"]);
                dfs.crd.push.manage.smsChecked[preferences[i]["prefTypeCode"]] = [preferences[i]["custAccptInd"]];
            }
                                
            
            if (preferences[i]["categoryId"] == "PNRM")
                dfs.crd.push.manage.pushChecked.push(preferences[i]["prefTypeCode"]);
            
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
                                                         //.parents().find('#global-errors').html('').removeClass('redtext boldtext'); 
                                                        }
                                                        return;
                                                        } else {
                                                         $(this).addClass("input_hightlight_error");
                                                         //.parents().find('#global-errors').html('Please enter valid data.').addClass('redtext boldtext');
                                                        //$('body').animate({scrollTop:0}, 'slow');
                                                        event.preventDefault();
                                                        }
                                            
                                                        });
        
        $(".textbox_alertspage,.phone_number").focusout(function() {
                                                        dfs.crd.push.manage.isChangeMade = true;
                                                        if (dfs.crd.push.manage.validateFields()) 
                                                        {
                                                            return;
                                                        } else 
                                                        {
                                                            $('body').animate({scrollTop:0}, 'slow');
                                                            //event.preventDefault();
                                                        }
                                
                                                        });
        
        
        
        /*var stored_number;
        $('.phone_number').blur(function(){
                                stored_number = $(this).val();
                                $('.phone_number').attr("placeholder",stored_number);                                
                                //checkLengthofTextbox();
                                });*/
	var stored_number;
	$('.phone_number').blur(function(){
	stored_number = $(this).val();
	if($(".phone_number").val().length == 0){
		$('.phone_number').attr("placeholder","Phone number");
		}
	else{
	$('.phone_number').attr("placeholder",stored_number);
	}
	checkLengthofTextbox();
	});

        $('.phone_number').focus(function(){	
                                 checkLengthofTextbox();
                                 });
        
        
        $(".changenumber a").toggle(function () {
                                    $('.phone_number').val("");
                                    var a = $('#phonenumber').text();
                                    $('.phone_number').prop('placeholder',a);
                                    $("#changenumber select").val(dfs.crd.push.manage.phoneCarrier);
                                    $("#changenumber").slideDown(300);
                                    $(this).text("(cancel)");
                                    dfs.crd.push.manage.changeTextNumber = true;
                                    },
                                    function(){
                                    $("#changenumber").slideUp(300);
                                    $(this).text("(change)");
                                    dfs.crd.push.manage.changeTextNumber = false;
                                    });
        
        /*$("#changenumber select").change(function(){
                                         phoneCarrier = $(this).val(); //$("#changenumber select").get(0).selectedValue;
                                         });
        $("#addnumber select").change(function(){
                                         phoneCarrier = $(this).val();
                                         });*/
        
        //$('.phone_number').click(function(){
                                 //$('.phone_number').val("");
                                 //$('.phone_number').prop('placeholder',"");
                                 //});
        
        /*$('.savenumber').click(function () {
                               
                               var str = $('.phone_number').val();
                               var index = $("#changenumber select").get(0).selectedIndex;
                               if (str.length == 10 && index != 0) {
                               var a = $('#phonenumber').html(str.substring(0, 3) + '-' + str.substring(3, 6) + '-' + str.substring(6, 10));
                               $("#changenumber").slideUp(300);
                               $(".changenumber a").text("(change)");
                               $('body').animate({scrollTop:0}, 'slow');
                               }
                               });*/
        
        
        /*
         *Check the current state of the checkbox and Enable / disable accordingly.
         */
        var val = $('select#alertsetting').val();
        
        if (val == "off") {
            $("#notifsettings").find(':checkbox[id^=PNRM]').attr('disabled', true);
            //$("label[for='alerts']").css("color", "grey");
            $("#notifsettings .ui-grid-b .ui-block-b").css("opacity","0.3");
        } else if (val == "on") {
            $("#notifsettings").find(':checkbox[id^=PNRM]').attr('disabled', false);
            //$("label[for='alerts']").css("color", "black");
            $("#notifsettings .ui-grid-b .ui-block-b").css("opacity","1.0");
        }
        
        $('select#alertsetting').change(function () {
                                        if ($(this).val() == 'on') 
                                        {
                                        $("#notifsettings").find(':checkbox[id^=PNRM]').attr('disabled', false);
                                        $("#notifsettings .ui-grid-b .ui-block-b").css("opacity","1");
                                        
                                        // if user has not accepted Push T&C, show the Push T&C Check box and add validation for save
                                           /* if (pushEnabled == "P")
                                                {
                                                    $("#id-notify-save").addClass("ui-disabled");
                                                    $('#push-tnc').show();
                                                    validatePushTnC= true;
                                                    //$("#id-notify-save").attr("disabled", "true").parent().addClass("ui-disabled");
                                                    $('#reg-tnc-chkbox').change(function () {
                                                                        if ($(this).is(':checked')) 
                                                                                    $("#id-notify-save").removeClass("ui-disabled");
                                                                        else 
                                                                                    $("#id-notify-save").addClass("ui-disabled");
                                                    });
                                                }*/
                                        } 
                                        else 
                                        {
                                        $("#notifsettings").find(':checkbox[id^=PNRM]').attr('disabled', true);
                                        $("#notifsettings .ui-grid-b .ui-block-b").css("opacity","0.3");
                                        
                                        // if user has not accepted Push T&C, hide the Push T&C Check box and remove validation for save
                                            /*if (pushEnabled == "P")
                                                {
                                                    $('#push-tnc').hide();
                                                    validatePushTnC= false;
                                                    $("#id-notify-save").removeClass("ui-disabled");
                                                }*/
                                        }
                                        dfs.crd.push.manage.isChangeMade = true;
                                    });
        
        /*$("#notifsettings").find(':checkbox[id^=SMRM]').change(function () {
                                                                 if ($(this).is(':checked')) {
                                                                 $('#tnc input:checkbox').attr('checked', true);
                                                                 $("#id-notify-save").removeClass('ui-disabled');
                                                                 }
                                                                 });*/
        
        /* Enable or Disable Save button according to Push TNC checkbox if user has not accepeted TNC earlier i.e. pushEnabled = P
        if (pushEnabled == "P")
            $('#tnc input:checkbox').change(function () {
                                                    if ($(this).is(':checked')) 
                                                        $("#id-notify-save").removeClass("ui-disabled");
                                                    else 
                                                        $("#id-notify-save").addClass("ui-disabled");
                                                    isChangeMade = true;
                                                        });*/
        
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
        
        
        /*$('#tnc input:checkbox').change(function () {
                                        if(validatePushTnC)
                                        {
                                            if ($(this).is(':checked')) {
                                            $("#id-notify-save").attr("disabled", "false").parent().removeClass("ui-disabled");
                                            } 
                                            else {
                                            $("#id-notify-save").attr("disabled", "true").parent().addClass("ui-disabled");
                                            }
                                        }
                                        isChangeMade = true;
                                        });*/
                                        
        //idNearingAccCredLine  $("#idNearingAccCredLine").
        $("#notifsettings").find('[id^=CRLT]').find('[id^=VALUE]').change(function() {
                                          var errSel = 'You selected more than a maximum limit.';
                                          if( $(this).val() > dfs.crd.push.manage.crltMaxAmt) 
                                          { 
                                            $('#global-errors').html(errSel).addClass('standard_error_msg');
                                            $('body').animate({scrollTop:0}, 'slow');
                                            //$('[#idNearingAccCredLine]').focus();
                                            $("#notifsettings").find('[id^=CRLT]').find('[id^=VALUE]').focus();
                                            $("#id-notify-save").addClass("ui-disabled");
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
		showSysException(err);
	}
}


dfs.crd.push.manage.postPushPreferenceData = function() 
{
	try{
        dfs.crd.push.manage.pushCheckedPost = [];
        dfs.crd.push.manage.smsCheckedPost = [];
		var newDate = new Date();	
		var PUSHPREFERENCEURL = RESTURL + "contact/v1/preferences/enrollments?" + newDate;
        //alert(PUSHPREFERENCEURL + " POST from manage notif, VID: " + vendorID);
        
        var pnSliderParam, acctOverrideParam, phoneNumberParam, carrierParam;
        var pushEnabledChanged = false;
        
        var preferencesList = "";
        var preferenceJSON;
        var dataJSONString;
        
        var varCustAccptInd;
        
        var checkedElements = $("input.alert-chk:checked");
        for (i=0;i<checkedElements.length;i++)
        {
            // get the alert type SMS(SMRM) / Push(PNRM) from the ID of the check box which are marked as checked 
            var alertType = $(checkedElements[i]).attr('id');
            // get the alert Name Statement(STMT) etc from the ID of Li containing the above check box 
            var alertName = $(checkedElements[i]).parent().parent().parent().parent().attr('id');
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
            }
            
            if (alertType == "PNRM")
            {   
                dfs.crd.push.manage.pushCheckedPost.push(alertName);
                varCustAccptInd = "Y";
            }
            
            var alertValue = $(checkedElements[i]).parent().parent().parent().find('[id^=VALUE]').val();
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
        for (i=0;i<dfs.crd.push.manage.smsChecked.length;i++)
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
        
        for (i=0;i<dfs.crd.push.manage.pushChecked.length;i++)
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
                pnSliderParam = "N"; 
        }
        else
        {
            if(dfs.crd.push.manage.pushEnabled == "Y"  && !dfs.crd.push.manage.userOverride)
                pnSliderParam = "";  
            else
                pnSliderParam = "Y"; 
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
        }
        
        // get the phone number and carrier if user is changing his contact
        if (dfs.crd.push.manage.changeTextNumber)
        {
            phoneNumberParam = $("#txt-change-phone-number").val();
            dfs.crd.push.manage.phoneCarrier = $("#changenumber option:selected").val();
        }
        
        if(phoneNumberParam != "" && dfs.crd.push.manage.phoneCarrier != "")
            carrierParam = phoneNumberParam + "@" + dfs.crd.push.manage.phoneCarrier;
        
        dataJSONString = '{"preferences"' + ':[' + preferencesList + '],"vid":"' + vendorID + '","deviceOS":"' + getClientPlat() + '","osVersion":"' + getOsVersion()  + '","deviceID":"' + getDeviceID()  + '","regStatus":"' + pnSliderParam + '","accntOverrideInd":"' + acctOverrideParam + '","phoneNumber":"' + phoneNumberParam + '","carrier":"' + carrierParam + '"}';
        
        //alert("POST Pref JSON: " + dataJSONString);
         
        if (!dfs.crd.push.manage.validateFields())
        {   //alert("Validation failed");
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
                            //alert ("Preference save successful");
                            // user preference saved successfully, update the status text and scroll up
                            var textSuccessfullUpdate = "<h3 class='greentext'>Text and push alerts settings have been updated.</h3>";
                            $('#global-errors').html(textSuccessfullUpdate).show();
                            
                            var str = $('.phone_number').val();
                            var index = $("#changenumber select").get(0).selectedIndex;
                            if (str.length == 10) {
                            var a = $('#phonenumber').html(str.substring(0, 3) + '-' + str.substring(3, 6) + '-' + str.substring(6, 10));
                            $("#changenumber").slideUp(300);
                            $(".changenumber a").text("(change)");
                            }
                            
                            // kill the cached preference on successful post and set userOverride to false if user has already overriden
                            killDataFromCache("MANAGEALERTS");
                            if (acctOverrideParam == "T")
                                {
                                    dfs.crd.lilo.otherUser = false;
                                    dfs.crd.push.manage.userOverride = null;
                                }
                            dfs.crd.push.manage.isChangeMade = false;
                        }
				}
               },
            error : function(jqXHR, textStatus, errorThrown) {
                //alert ("Preference save failed " + jqXHR.responseText); 
                hideSpinner();
				var code=getResponseStatusCode(jqXHR);
				errorHandler(code,'','pushManageAlerts');
			}
		});
        //return postData;
	}catch(err)
    {
        hideSpinner();
		showSysException(err);
	}
}


/* For Cancel Overlay
$("#manageAlerts-pg").live('pageshow', function (event){
                           $('.notifcancel').click(function(){
                                                   $('.ui-loader').hide();
                                                   var customSettings = {"ContentHolder": "#overlay_wraper_saveConfirm","height":"200","resize":false,"width":"330"};
                                                   fnOverlay(customSettings);
                                                   });
                           });


*/

/*-------Final overlay -------*/
dfs.crd.push.manage.fnOverlay = function(customSettings){	
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
        navigation('../profile/profileLanding');
	}
    
	// if window is resized then reposition the overlay box
	$(window).resize(function(){
                     resizeWin = true;
                     showOverlayBox();
                     });
	$(window).bind('orientationchange', function(){		
                   //$(contentHolder).css({'left': '-1000px'});
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
}

/* Final overlay*/





