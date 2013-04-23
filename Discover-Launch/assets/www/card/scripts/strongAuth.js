dfs.crd.sa = dfs.crd.sa || {};
dfs.crd.sa.referrerModule;
dfs.crd.sa.questionId;
dfs.crd.sa.questionText;
dfs.crd.sa.answerText;
dfs.crd.sa.bindDevice;
dfs.crd.sa.attemptCount;
dfs.crd.sa.errorCode;
dfs.crd.sa.jqXHRResponse = [];
dfs.crd.sa.ssoSADone = false;

/*
 * function to decide whether user is given challenge question for strong auth or is directly navigated to send money page
 */
dfs.crd.sa.checkForStrongAuth = function(navigatePage) {

	try {
		dfs.crd.sa.referrerModule = navigatePage;
		dfs.crd.sa.attemptCount = 0;
		var isSAChallenge = dfs.crd.sa.getStrongAuth();
		if (isEmpty(isSAChallenge)) {
			return;
		}

		if (isSAChallenge) {
			navigation("../strongAuth/strongAuthFirstQues", false);
		} else {
		    hideSpinner();
			cpEvent.preventDefault();
			errorHandler("", "", "strongAuthFirstQues"); 
			// navigation(dfs.crd.sa.referrerModule, false);
		}
	} catch (err) {
		showSysException(err);
	}
}

/*
 * Ajax call for strong auth challenge service
 */
dfs.crd.sa.getStrongAuth = function() {

	try {
		var newDate = new Date();
		var StrongAuthChallengeURL = RESTURL + "strongauth/v1/challenge?"+ newDate + "";
		var strongAuthFlag;
		$.ajax({
			type : "GET",
			url : StrongAuthChallengeURL,
			async : false,
			dataType : "json",
			headers : prepareGetHeader(),
			success : function(data, status, jqXHR) {
			
			 if (!validateResponse(data,"strongAuthChallengeValidation"))      // Pen Test Validation
               {
               errorHandler("SecurityTestFail","","");
               return;
               }
				if (data.hasOwnProperty("questionId")) {
					dfs.crd.sa.questionId = data.questionId;
					dfs.crd.sa.questionText = data.questionText;
					strongAuthFlag = true;
				} else if (data.hasOwnProperty("saStatus")
						&& data.saStatus == "ALLOW") {
					strongAuthFlag = false;
				}
			},
			error : function(jqXHR, textStatus, errorThrown) {
				if (jqXHR.status == "403") {
					dfs.crd.sa.extractjqXHR(jqXHR);
					dfs.crd.sa.handleSAError();
				} else {
					cpEvent.preventDefault();
					errorHandler(jqXHR.status, "", "strongAuthFirstQues");
				}
			}
		});

		return strongAuthFlag;
	} catch (err) {
		showSysException(err);
	}
}

/*
 * Ajax call for strong auth challenge service for SSO User
 */
dfs.crd.sa.getStrongAuthForSSO = function(navigatePage) {
	try {
	    dfs.crd.sa.referrerModule = navigatePage;
		var StrongAuthChallengeURL = RESTURL + "strongauth/v1/challenge";
		var strongAuthFlag;
		$.ajax({
			type : "GET",
			url : StrongAuthChallengeURL,
			async : false,
			dataType : "json",
			headers : prepareGetHeader(),
			success : function(data, status, jqXHR) {
				if (data.hasOwnProperty("questionId")) {
					dfs.crd.sa.questionId = data.questionId;
					dfs.crd.sa.questionText = data.questionText;
					strongAuthFlag = true;
				} else if (data.hasOwnProperty("saStatus")
						&& (data.saStatus == "ALLOW" || data.saStatus == "SKIPPED")) {
					strongAuthFlag = false;
				}
			},
			error : function(jqXHR, textStatus, errorThrown) {
				if (jqXHR.status == "403") {
					dfs.crd.sa.extractjqXHR(jqXHR);
					if(dfs.crd.sa.errorCode === "1403" || dfs.crd.sa.errorCode === "1404"){					
						strongAuthFlag = false;//IGNORE the SKIPPED STATUS and continue
					} else {
						dfs.crd.sa.handleSAError();
					}
				} else {
					cpEvent.preventDefault();
					var code=getResponseStatusCode(jqXHR);
					if(code === "1403" || code === "1404"){	
						strongAuthFlag = false;//IGNORE the SKIPPED STATUS and continue
					}else{
						errorHandler("Enhanced Account Security",code,"","strongAuthFirstQues");
					}
				}
			}
		});
		return strongAuthFlag;
	} catch (err) {
		showSysException(err);
	}
}


/*
 * function to populate the question in strong auth page
 */
function strongAuthFirstQuesLoad() {
	try {
		fetchBottomNavData();
		$("#firstQues").html(dfs.crd.sa.questionText);
		var referrerPage = dfs.crd.sa.referrerModule.substring(dfs.crd.sa.referrerModule.lastIndexOf("/") + 1);
		showBottomMenu(referrerPage, referrerPage);
	} catch (err) {
		showSysException(err);
	}
}

/*
 * function to check for text field entry and binding option selected or not
 */

dfs.crd.sa.validateSAParam = function() {
	try {
		dfs.crd.sa.clearErrorFromDiv();
		dfs.crd.sa.answerText = $("#answer").val();
		if ($("#radio-choice-4").prop("checked")) {
			dfs.crd.sa.bindDevice = true;
		} else {
			dfs.crd.sa.bindDevice = false;
		}
		dfs.crd.sa.strongAuthenticateUser();
	} catch (err) {
		showSysException(err)
	}
}

/*
 * function to make ajax call for post request of strong auth
 */
dfs.crd.sa.strongAuthenticateUser = function() {
	try {
		var StrongAuthURL = RESTURL + "strongauth/v1/authenticate";
		var devID;
		var simID;
		var otherID;
		var dataJSON = {
			"questionId" : "2",
			"questionAnswer" : "abc",
			"bindDevice" : "false",
			"sid" : "  ",
			"did" : "  ",
			"oid" : "  "

		};
		dataJSON.questionId = dfs.crd.sa.questionId;
		dataJSON.questionAnswer = dfs.crd.sa.answerText;
		dataJSON.sid = getSID();
		dataJSON.did = getDID();
		dataJSON.oid = getOID();
		dataJSON.bindDevice = dfs.crd.sa.bindDevice.toString();
		var dataJSONArg = JSON.stringify(dataJSON);
		showSpinner();
		$.ajax({
			type : "POST",
			url : StrongAuthURL,
			async : false,
			datatype : "json",
			data : dataJSONArg,
			headers : preparePostHeader(),
			success : function(responseData, status, jqXHR) {
				hideSpinner();
				 if (!validateResponse(responseData,"strongAuthAuthenticatePOSTValidation"))      // Pen Test Validation
               {
               errorHandler("SecurityTestFail","","");
               return;
               }
				navigation(dfs.crd.sa.referrerModule);
			},
			error : function(jqXHR, textStatus, errorThrown) {
				hideSpinner();
				if (jqXHR.status == "403") {
					dfs.crd.sa.attemptCount++;
					dfs.crd.sa.extractjqXHR(jqXHR);
					dfs.crd.sa.handleSAError();

				} else {
					errorHandler(jqXHR.status, "", "strongAuthFirstQues");
				}

			}
		});
		dfs.crd.sa.answerText = "";
	} catch (err) {
		hideSpinner();
		showSysException(err);
	}
}

/*
 * function to parse jqXHR response and get the error code and jqXHR response text
 */
dfs.crd.sa.extractjqXHR = function(jqXHR) {
	try {
		dfs.crd.sa.errorCode = getResponseStatusCode(jqXHR);
		dfs.crd.sa.jqXHRResponse[dfs.crd.sa.errorCode] = getResponsErrorData(jqXHR);
	} catch (err) {
		showSysException(err);
	}
}

/*
 * function to refresh the divs for text field inline message and radio button inline message
 */
dfs.crd.sa.clearErrorFromDiv = function() {
	try {
		var wrongAns = $("#wrongAnswer");
		wrongAns.html("");
		wrongAns.css("display", "none");
	} catch (err) {
		showSysException(err);
	}
}

dfs.crd.sa.errors = {};
dfs.crd.sa.errors.SKIPPED_CAN_NOT_AUTHENTICATE = "1403";
dfs.crd.sa.errors.SKIPPED_ERROR = "1404";
dfs.crd.sa.errors.RECHALLENGE = "1405";
dfs.crd.sa.errors.SKIPPED_ERROR = "1404";

dfs.crd.sa.isSkipped = function() {
	return (dfs.crd.sa.errorCode === dfs.crd.sa.errors.SKIPPED_CAN_NOT_AUTHENTICATE || dfs.crd.sa.errorCode === dfs.crd.sa.errors.SKIPPED_ERROR);
}

/*
 * function to handle errors
 */
dfs.crd.sa.handleSAError = function() {
	try {
		var SKIPPED_CAN_NOT_AUTHENTICATE = dfs.crd.sa.errors.SKIPPED_CAN_NOT_AUTHENTICATE;
		var SKIPPED_ERROR = dfs.crd.sa.errors.SKIPPED_ERROR;
		var RECHALLENGE = dfs.crd.sa.errors.RECHALLENGE;
		var SKIPPED_ERROR = dfs.crd.sa.errors.SKIPPED_ERROR;
		var DEFAULT_DENY_MESSAGE = "The answer does  not match your Account information.";
		dfs.crd.sa.clearErrorFromDiv();
		$("#answer").val("");
		switch (dfs.crd.sa.errorCode) {
		case SKIPPED_CAN_NOT_AUTHENTICATE:
			errorHandler(SKIPPED_CAN_NOT_AUTHENTICATE, "","strongAuthFirstQues");
			break;
		case SKIPPED_ERROR:
			errorHandler(SKIPPED_ERROR, "", "strongAuthFirstQues");
			break;
		case RECHALLENGE:
			$("#wrongAnswer").css("display", "block");
			$("#answer").addClass("error");
			$("#firstQues").html((dfs.crd.sa.jqXHRResponse[RECHALLENGE])[0].questionText);
			if (!((dfs.crd.sa.questionId == ((dfs.crd.sa.jqXHRResponse[RECHALLENGE])[1].questionId)))) {
				$("#answer").removeClass("errormsg");
				dfs.crd.sa.showHideContinueBtn(this);
			}else if (dfs.crd.sa.attemptCount == "1"|| dfs.crd.sa.attemptCount == "2"|| dfs.crd.sa.attemptCount == "4"|| dfs.crd.sa.attemptCount == "5") {
				dfs.crd.sa.showHideContinueBtn(this);
				$("#wrongAnswer").html(DEFAULT_DENY_MESSAGE);
				$("#answer").addClass("errormsg");
			}
			dfs.crd.sa.questionId = ((dfs.crd.sa.jqXHRResponse[RECHALLENGE])[1].questionId);
			break;
		case LOCK_OUT:
			strongAuthLockedUnloadExecute = false;
			$("#strongAuthLocked-Pg").live("pagebeforeshow", function() {
				$("#errorLabel").html("Enhanced Account Security");
				$("#globalErrorDiv").html(errorCodeMap[LOCK_OUT + "_LOCKOUT"]);
			});
			navigation("../strongAuth/strongAuthLocked");
			break;
		default:
			errorHandler(dfs.crd.sa.errorCode, "", "strongAuthFirstQues");
			break;
		}

	} catch (err) {
		showSysException(err);
	}
}

dfs.crd.sa.showHideContinueBtn = function(textFieldElement) {
	try {
		if (!isEmpty(textFieldElement.value)) {
			activebtn("continue");
		}else {
			deactiveBtn("continue");
		}
	} catch (err) {
		showSysException(err);
	}
}

dfs.crd.sa.SSOStrongAuth = function(path) {
	try{
		var cardHome = dfs.crd.achome.getACHomeData('ACHOME');
		if (!jQuery.isEmptyObject(cardHome)){
			console.log(cardHome.isSSOUser);
			if (typeof(cardHome.isSSOUser) === "boolean" && cardHome.isSSOUser && !dfs.crd.sa.ssoSADone) {
				dfs.crd.sa.checkForStrongAuth(path, true);
				dfs.crd.sa.ssoSADone = true;
			}
		}
	}catch(err)
	{
		showSysException(err);
	}
}

function strongAuthLockedUnload() {
	if (strongAuthLockedUnloadExecute) {
		return;
	}
	strongAuthLockedUnloadExecute = true;
	cpEvent.preventDefault();
	dfs.crd.lilo.logOutUser('LOGOUT');
	return true;
}

dfs.crd.sa.doSSOCheck = function(page) {
		var cardHome = dfs.crd.achome.getACHomeData('ACHOME');
		if (!jQuery.isEmptyObject(cardHome)){
			if (typeof(cardHome.isSSOUser) === "boolean" && cardHome.isSSOUser) {	
				var doStrongAuth = dfs.crd.sa.getStrongAuthForSSO(page);
				if (doStrongAuth !== undefined && doStrongAuth) {
					cpEvent.preventDefault();
					navigation('../strongAuth/strongAuthFirstQues', true);
					return true;
				}
			}
		}
}

dfs.crd.sa.ssoStrongAuthCheckAndNavigate = function(page) {
	if (!dfs.crd.sa.doSSOCheck(page)) {
		navigation(page);
	}
}