/**
 * This JS is for HANDSET Personalize Pin purpose
 * hlin0, May 2012
 */
 
/********  Begin Profile Init/Load Functions Called by Common, Global Scope *********/
function profileLandingLoad(){
	try {
		$("#pcp-lnk").click(function(){navigation('personalizeCashPin1');});
		$("#mc-lnk").click(function(){navigation('profileManageContact');});
		
        // Begin Push Notification Change for Profile Links
		//$("#eir-lnk").click(function(){navigation('profileEnroll');});
        $("#ma-lnk").click(function(){s.prop1='HANDSET_APP_PROFILE_ALERTS_BTN';navigation('../pushNotification/manageAlertsOverride');});
        $("#ah-lnk").click(function(){navigation('../pushNotification/alertHistory');});
        // End Push Notification change
        
	} catch (err) {
		showSysException(err);
	}
}
/********  End Profile Init/Load Functions Called by Common, Global Scope *********/


/********  Begin Profile Custom Functions, Scope dfs.crd.profile *********/
dfs.crd.profile.addCancelHandler = function() {
	$("#cancelBtn").click(function(){gotoAchome();});
}

dfs.crd.profile.verifyPriorPageForStep1 = function() {
	var passVerification = true;
	var validPriorPagesOfStep1 = new Array("profileLanding", "strongAuthFirstQues","personalizeCashPin2");
	try {
		if ($.inArray(fromPageName, validPriorPagesOfStep1) < 0 && (!isLhnNavigation)) {
			cpEvent.preventDefault();
			history.back();
			passVerification = false;
		}
	} catch (err) {
        showSysException(err);
    }
    return passVerification;
}

dfs.crd.profile.handleSASkipped = function() {
	errorHandler("skipped","","personalizeCashPin1");
}
dfs.crd.profile.handleAcLite = function() {
	errorHandler("acLiteOutageMode_ACL","","personalizeCashPin1");
}
/********  End Profile Custom Functions, Scope dfs.crd.profile *********/




