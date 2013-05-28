/**
 * This JS is for SHARED (HANDSET AND TABLET) Personalize Pin purpose
 * hlin0, May 2012
 */
 
// define namespace for dfs.crd.profile
dfs.crd.profile = dfs.crd.profile || {};

/********  Begin Profile Init/Load Functions Called by Common, Global Scope *********/
function profileLandingLoad(){
	try {
		$("#pcp-lnk").click(function(){navigation('personalizeCashPin1');});
		$("#mc-lnk").click(function(){navigation('profileManageContact');});
		$("#eir-lnk").click(function(){navigation('profileEnroll');});
	} catch (err) {
		showSysException(err);
	}
}

function profileEnrollLoad() {
	try {
		$("#contBtn").click(function(){dfs.crd.profile.moveToDiscoverSiteReminder();});
	} catch (err) {
		showSysException(err);
	}
}

function profileManageContactLoad() {
	try {
		$("#contBtn").click(function(){dfs.crd.profile.moveToDiscoverSiteProfile();});
	} catch (err) {
		showSysException(err);
	}
}

function personalizeCashPin1Load() {
	if (!dfs.crd.profile.verifyPriorPageForStep1()) return;
    if (!dfs.crd.profile.isCardTypeEligible()) {
		cpEvent.preventDefault();
		navigation("../profile/personalizeCashPinError");
		return;
	}
	if (acLiteModeFlag) {
		cpEvent.preventDefault();
		dfs.crd.profile.handleAcLite();
		return;
	}
	try {
		showSpinner();
		$.ajax({
				type: "POST",
				url: dfs.crd.profile.createPersonalPinUrl,
				async : false,
				dataType: 'json',
				data: JSON.stringify({"enterPin":"0000", "confirmPin":"0000"}),
				headers: preparePostHeader(),
				success : function(responseData, status, jqXHR) {
					hideSpinner();
					// since we haven't collected pins yet, the REST should NOT succeed.
					cpEvent.preventDefault();
					navigation("../profile/personalizeCashPinError");
				},
				error : function(jqXHR, textStatus, errorThrown) {
					hideSpinner();
					var saAuth = jqXHR.getResponseHeader("WWW-Authenticate");
					if (jqXHR.status === 401 && saAuth != null && saAuth.indexOf("challenge") >= 0) { // sa challenge
						cpEvent.preventDefault();
						dfs.crd.sa.checkForStrongAuth('../profile/personalizeCashPin1');
					} else if (jqXHR.status === 401 && saAuth != null && saAuth.indexOf("skipped") >= 0){ // sa skipped
						dfs.crd.profile.handleSASkipped();				
					} else if (jqXHR.status === 400) { // since we haven't collected pins yet, this is expected, proceed to page 2
						$("#errormsg").hide();
						dfs.crd.profile.addCancelHandler();
						$("#updateBtn").click(function(){dfs.crd.profile.personalizeCashPin2();});
					} else {
						cpEvent.preventDefault();
						navigation("../profile/personalizeCashPinError");
					}
				}
		});
	} catch (err) {
		showSysException(err);
	}
}

function personalizeCashPin2Load() {
	var validPriorPagesOfStep2 = new Array("personalizeCashPin1");
	try {
		if ($.inArray(fromPageName, validPriorPagesOfStep2) >= 0) {
			var cardHome = dfs.crd.profile.getCardHomeData();
			var last4 = cardHome["lastFourAcctNbr"];
			var template = $("#last4").html();
			var index = template.indexOf("0000");
			var content = template.substring(0, index).concat(last4).concat(template.substring(index+4));
			$("#last4").html(content);
		} else {
			cpEvent.preventDefault();
			history.back();
		}
	} catch (err) {
		showSysException(err);
	}
}
/********  End Profile Init/Load Functions Called by Common, Global Scope *********/


/********  Begin Profile Custom Functions, Scope dfs.crd.profile *********/
dfs.crd.profile.createPersonalPinUrl = RESTURL + "personalprofile/v1/personalizepin";

dfs.crd.profile.moveToDiscoverSiteReminder = function() {
	var strURLToOpen="";
	strURLToOpen = EXT_HREF_URL + "cardmembersvcs/loginlogout/app/ac_main?link=/cardmembersvcs/emailreminder/showEmailProfile";
	HybridControl.prototype.showOnBrowser(function successToken(args){ showOnBrowser = args;},function failCallBack(args){},strURLToOpen); 
	//window.location.href = EXT_HREF_URL + "cardmembersvcs/loginlogout/app/ac_main?link=/cardmembersvcs/emailreminder/showEmailProfile";
}

dfs.crd.profile.moveToDiscoverSiteProfile = function() {
	var strURLToOpen="";
	strURLToOpen = EXT_HREF_URL + "cardmembersvcs/loginlogout/app/ac_main?link=/cardmembersvcs/personalprofile/pp/GetInitialInfo";
	HybridControl.prototype.showOnBrowser(function successToken(args){ showOnBrowser = args;},function failCallBack(args){},strURLToOpen);
	//window.location.href = EXT_HREF_URL + "cardmembersvcs/loginlogout/app/ac_main?link=/cardmembersvcs/personalprofile/pp/GetInitialInfo";
}

dfs.crd.profile.personalizeCashPin2 = function(enterPin, confirmPin) {
	var enterPin = $("#enterPin").val();
	var confirmPin = $("#confirmPin").val();
	var validationError = dfs.crd.profile.validatePins(enterPin, confirmPin);
	if (validationError != null) {
		dfs.crd.profile.displayPinValidationError(validationError);//validation error, stay on the page
		return;
	}
	try {
		showSpinner();
		$.ajax({
				type: "POST",
				url: dfs.crd.profile.createPersonalPinUrl,
				async : false,
				dataType: 'json',
				data: JSON.stringify({"enterPin":enterPin, "confirmPin":confirmPin}),
				headers: preparePostHeader(),
				success : function(responseData, status, jqXHR) {
					hideSpinner();
					navigation("../profile/personalizeCashPin2");
				},
				error : function(jqXHR, textStatus, errorThrown) {
					hideSpinner();
					navigation("../profile/personalizeCashPinError");
				}
			});
	}  catch (err) {
		showSysException(err);
	}
}

dfs.crd.profile.validatePins = function(enterPin, confirmPin) {
	var use4NumbersOnlyMsg = "Your PIN must be exactly 4 numbers-no letters.";
	var pinMustMatchMsg = "Your new PIN and re-entered new PIN must match.";
	var noZeroPinMsg = "Your may not select 0000 as a pin.";
	var validationError = null;
	var intRegex = /^\d+$/;
	if (enterPin == null || enterPin.length !== 4 || !intRegex.test(enterPin)
		|| confirmPin == null || confirmPin.length !== 4 || !intRegex.test(confirmPin)) {
		validationError = use4NumbersOnlyMsg;
	} else if (enterPin !== confirmPin) validationError = pinMustMatchMsg;
	else if (enterPin === "0000" || confirmPin === "0000") validationError = noZeroPinMsg;
	return validationError;
}

dfs.crd.profile.isCardTypeEligible = function() {
	var cardHome = dfs.crd.profile.getCardHomeData();
	var cardType = cardHome["cardType"];
	if (cardType === "000002") return false;
	return true;
}

dfs.crd.profile.displayPinValidationError = function(validationError) {
	$("#enterPin").val("");
	$("#confirmPin").val("");
	$("#errormsg").html(validationError);
	$("#errormsg").show();
	$("#enterPin").removeClass("inputtext").addClass("input_error");
	$("#confirmPin").removeClass("inputtext").addClass("input_error");
}

dfs.crd.profile.getCardHomeData = function() {
	var cardHome = new Object();
    try{
    	cardHome = dfs.crd.achome.getACHomeData('ACHOME');
    }catch(err) {
        showSysException(err);
    }
    return cardHome;
}
/********  End Profile Custom Functions, Scope dfs.crd.profile *********/
