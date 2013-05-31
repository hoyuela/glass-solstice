dfs.crd.p2p = dfs.crd.p2p || {};
dfs.crd.p2p.counter = 25;
dfs.crd.p2p.basicLoadIndex = "0";
dfs.crd.p2p.loadMoreHit = 1;
dfs.crd.p2p.loadMoreHitFlag = false;
dfs.crd.p2p.maxTransactionCount = 0;
dfs.crd.p2p.sendMoney1Flag = false;
dfs.crd.p2p.viewDetailsResponse;
dfs.crd.p2p.SA_SKIPPED_ON_SENDMONEY = "skipped";
dfs.crd.p2p.errorCode = "";
dfs.crd.p2p.filterFlag = false;
dfs.crd.p2p.phoneOrEmail = "";
dfs.crd.p2p.autoResponseData;
dfs.crd.p2p.sendMoreMoneyPage3 = false;
dfs.crd.p2p.hIWScreenFlag = false;
dfs.crd.p2p.cancelledTransaction = false;
dfs.crd.p2p.lastStatusSelected = "";
dfs.crd.p2p.loadTransaction = "25";
dfs.crd.p2p.objArray = new Array();
dfs.crd.p2p.objEmailArray = new Array();
dfs.crd.p2p.heightSet;
dfs.crd.p2p.count = 0;

dfs.crd.p2p.validatePriorPage = function(validPriorPages) {
try{
	for ( var i in validPriorPages) {
			if (fromPageName == validPriorPages[i])
				return true;
		}
		cpEvent.preventDefault();
		history.back();
		return false;
	} catch (err) {
		showSysException(err);
	}
}
$("#sendMoney1-pg").live('click', function() {
	try {
		var target = event.target;
		var srcElementId = event.srcElement.id;
		dfs.crd.p2p.hideNameList();
		dfs.crd.p2p.hideEmailList();
	} catch (err) {
		showSysException(err)
	}
});
function sendMoney1Load() {
	try {	
		if(fromPageName!="sendMoney2" && fromPageName!="cancelTransaction")
		{
			killDataFromCache("SM1_DATA");
			dfs.crd.p2p.phoneOrEmail="";
		}
		dfs.crd.p2p.validPriorPagesOfStep1 = new Array("moreLanding",
				"sendMoneyLanding", "strongAuthFirstQues", "howItWorks",
				"sendMoney2", "pageError", "transactionHistory",
				"confirmCancelTransaction", "updateEmail", "cancelTransaction","sendMoney3");
		if (acLiteModeFlag) {
			cpEvent.preventDefault();
			errorHandler("acLiteOutageMode_ACL", "", "sendMoney1");
		} else {
			if (dfs.crd.p2p.sendMoreMoneyPage3
					|| dfs.crd.p2p.errorCode == "noTransactions") {
				dfs.crd.p2p.errorCode = "";
				dfs.crd.p2p.showSendMoneyPage();
				console.log("SEnd Money Load-1");
			} else if(isLhnNavigation || dfs.crd.p2p
					.validatePriorPage(dfs.crd.p2p.validPriorPagesOfStep1)){					
					console.log("SEnd Money Load-2");
				dfs.crd.p2p.showSendMoneyPage();
				//isLhnNavigation  = false;
			}else if (!dfs.crd.p2p
					.validatePriorPage(dfs.crd.p2p.validPriorPagesOfStep1)) {
					console.log("SEnd Money Load-3");
				return;
			} 
			//isLhnNavigation  = false;
		}
	} catch (err) {
		showSysException(err);
	}
}
dfs.crd.p2p.showSendMoneyPage = function() {
	try {
	    console.log("SEnd money - dfs.crd.p2p.showSendMoneyPage");
		if (!dfs.crd.p2p.hIWScreenFlag) {
			 console.log("SEnd money - if (!dfs.crd.p2p.hIWScreenFlag)");
			var responseData = dfs.crd.p2p.getSendMoneyData();
			if (!jQuery.isEmptyObject(responseData)) {
				var transactionAvailable = responseData.transactionAvailable;
				var allTransactionsFailedOrUnknown = responseData.allTransactionsFailedOrUnknown;
				if (!transactionAvailable && !allTransactionsFailedOrUnknown) {
				console.log("SEnd money - first");
					cpEvent.preventDefault();
					navigation("../p2p/howItWorks", false);
				} else if (transactionAvailable
						&& allTransactionsFailedOrUnknown) {
					cpEvent.preventDefault();
					console.log("SEnd money - second");
					navigation("../p2p/howItWorks", false);
				} else if (transactionAvailable
						&& !allTransactionsFailedOrUnknown) {
						console.log("SEnd money - bakar");
					dfs.crd.p2p.sendMoreMoneyPage3 = false;
					dfs.crd.p2p.displayNone();
					dfs.crd.p2p.populateSendMoney1(responseData);
				} else if (!transactionAvailable
						&& allTransactionsFailedOrUnknown) {
					cpEvent.preventDefault();
					console.log("SEnd money - third");
					navigation("../p2p/howItWorks", false);
				}
			}
		} else {
			console.log("SEnd money - else {");
			dfs.crd.p2p.hIWScreenFlag = false;
			var responseData = dfs.crd.p2p.getSendMoneyData();
			dfs.crd.p2p.sendMoreMoneyPage3 = false;
			dfs.crd.p2p.displayNone();
			dfs.crd.p2p.populateSendMoney1(responseData);
		}
	} catch (err) {
		showSysException(err);
	}
}
dfs.crd.p2p.noCancelTransaction = function() {
	try {
		if (!dfs.crd.p2p.sendMoney1Flag){
			navigation("../p2p/sendMoney2");
		}else{			
			dfs.crd.p2p.hIWScreenFlag = true;
			navigation("../p2p/sendMoney1");
		}
	} catch (err) {
		showSysException(err);
	}
}
dfs.crd.p2p.getSendMoneyData = function() {
	try {
		var newDate = new Date();
		var sendMoney = getDataFromCache("SENDMONEY");
		var SENDMONEYURL = RESTURL + "p2p/v1/makesendmoney?" + newDate + "";
		var code = "";
		if (jQuery.isEmptyObject(sendMoney)) {
			showSpinner();
			$
					.ajax({
						type : "GET",
						url : SENDMONEYURL,
						async : false,
						dataType : "json",
						headers : prepareGetHeader(),
						success : function(responseData, status, jqXHR) {
							sendMoney = responseData;
							dfs.crd.p2p.autoResponseData = responseData;
							putDataToCache("SENDMONEY", sendMoney);
							dfs.crd.p2p.hIWScreenFlag = false;
							hideSpinner();
                                         if (!validateResponse(responseData,"getSendMoneyDataValidation"))      // Pen Test Validation
                          {
                           errorHandler("SecurityTestFail","","");
                          return;
                          }
						},
						error : function(jqXHR, textStatus, errorThrown) {
							hideSpinner();
							code = getResponseStatusCode(jqXHR);
							cpEvent.preventDefault();
							if (jqXHR.status == 401) {
								var indexChallenge = jqXHR.getResponseHeader(
										"WWW-Authenticate")
										.indexOf("challenge");
								if (indexChallenge > 0)
									dfs.crd.sa
											.checkForStrongAuth("../p2p/sendMoney1");
								var indexSkipped = jqXHR.getResponseHeader(
										"WWW-Authenticate").indexOf("skipped");
								if (indexSkipped > 0)
									errorHandler(
											dfs.crd.p2p.SA_SKIPPED_ON_SENDMONEY,
											"", "sendMoney1");
							} else if (code == "1703") {
								navigation("../p2p/updateEmail", false);
							} else if (code == "1704") {
								var monthlyLimitArray = [];
								var Data = getResponsErrorData(jqXHR);
								var monthlyLimit = Data[0].monthlyLimit;
								monthlyLimitArray["Monthlylimit"] = Math
										.floor(monthlyLimit);
								var monthlylimitData = errorCodeMap["1704"];
								var monthlyLimitContextText = parseContent(
										monthlylimitData, monthlyLimitArray);
								errorHandler("", monthlyLimitContextText,
										"sendMoney1");
							} else if (code == "1705") {
								var dailyLimitArray = [];
								var Data = getResponsErrorData(jqXHR);
								var dailyLimit = Data[0].dailyLimit;
								dailyLimitArray["Dailylimit"] = Math
										.floor(dailyLimit);
								var dailylimitData = errorCodeMap["1705"];
								var dailyLimitContextText = parseContent(
										dailylimitData, dailyLimitArray);
								errorHandler("", dailyLimitContextText,
										"sendMoney1");
							} else
								errorHandler(code, "", "sendMoney1");
						}
					});
		}
		return sendMoney;
	} catch (err) {
		showSysException(err);
	}
}
dfs.crd.p2p.populateSendMoney1 = function(responseData) {
	try {
		if (!jQuery.isEmptyObject(responseData)) {
			var dLimit = responseData.dailyLimit;
			var dailyLimit1 = dLimit.split(".");
			var mLimit = responseData.monthlyLimit;
			var monthlyLimit1 = mLimit.split(".");
			$("#daylimit").html(dailyLimit1[0]);
			$("#monthLimit").html(monthlyLimit1[0]);
			$("#bonusTypes").html(responseData.bonusType);
			var selectHtml = "<select name='select-choice-0' onchange='dfs.crd.p2p.changeTransTypeText(),dfs.crd.p2p.handleTransactionType(this.value);' id='TransactionType'>"
					+ "<option id ='friendsFamily' value='Friends or Family'>Friends or Family</option>"
					+ "<option id='goodsServices' value='Goods or Services'>Goods or Services</option></select>";

			$("#sendMoney1_TransTypeSelectField").html(selectHtml);
			dfs.crd.p2p.isSM1DataAvailable();
			dfs.crd.p2p.sendMoney1Flag = true;
		}
	} catch (err) {
		showSysException(err);
	}
}
dfs.crd.p2p.changeTransTypeText = function() {
	try {
		$("#selectedTransType").text($("#TransactionType").val());
		dfs.crd.p2p.hideNameList();
		dfs.crd.p2p.hideEmailList();
	} catch (err) {
		showSysException(err);
	}
}

dfs.crd.p2p.isSM1DataAvailable = function() {
	try {
		var phonenumber;
		var retrievedData = new Array();
		retrievedData = getDataFromCache("SM1_DATA");
		if (!jQuery.isEmptyObject(retrievedData)) {
			if (validateEmailFormat(retrievedData[1]))
				phonenumber = (retrievedData[1]);
			else
				phonenumber = formatPhoneNumber(retrievedData[1]);
			$("#fullname").val(retrievedData[0]);
			$("#emailOrphone").val(phonenumber);
			$("#Amount").val(retrievedData[2]);
			var selectedTrans = retrievedData[3];
			$("#selectedTransType").text(selectedTrans);
			if (selectedTrans == "Friends or Family") {
				$("#friendsFamily").attr("selected", "selected");
				$("#sendMoney1_labelText").text("Fast. Easy.");
			} else {
				$("#goodsServices").attr("selected", "selected");
				$("#sendMoney1_labelText").text(
						"Free for you, PayPal charges recipient a fee");
			}
			$("#noteId").html(retrievedData[4]);
			dfs.crd.p2p.handleTransactionType(selectedTrans);
			dfs.crd.p2p.enableContinueBtn();
		}
	} catch (err) {
		showSysException(err);
	}
}
dfs.crd.p2p.enableContinueBtn = function() {
	try {
		var emailOrphone = $("#emailOrphone").val();
		var amount = $("#Amount").val();
		if (((amount != "0.00") && (amount > 0)) && emailOrphone.length != "")
			activebtn("continueBtn");
		else
			deactiveBtn("continueBtn");
	} catch (err) {
		showSysException(err);
	}
}
function isNumeric(input) {
	try {
		return /^[-+]?[0-9]+(\.[0-9]+)?$/.test(input);
	} catch (err) {
		showSysException(err);
	}
}
dfs.crd.p2p.changeAmountTo2Decimal = function() {
	try {
		var amount = $("#Amount").val();
		var amountDOM = $("#Amount");
		var amountMsgDOM = $("#amount_msg");

		if (isNumeric(amount)) {
			$("#Amount").val(convertTo2DecimalPoints(amount));
		} else if ($("#Amount").val() == "") {
			amountDOM.css("placeholder", "0.00");
		} else {
			amountDOM.val("");
			amountDOM.css("placeholder", "0.00");
			amountMsgDOM.css("display", "block");
			amountMsgDOM.html(errorCodeMap["1718"]);
			amountDOM.focus();
			//amountDOM.css("border", "2px solid #ff0000");
			amountDOM.addClass("errormsg");
			$("#errorTitle").css("display", "block");
		}
	} catch (err) {
		showSysException(err);
	}
}
dfs.crd.p2p.enableEmailBtn = function(fieldText) {
	try {
		var fieldTextVal = fieldText.value;
		if (!isEmpty(fieldTextVal))
			activebtn("updateContinue");
		else
			deactiveBtn("updateContinue");
	} catch (err) {
		showSysException(err);
	}
}
dfs.crd.p2p.editInformationCall = function() {
	try {
		dfs.crd.p2p.hIWScreenFlag = true;
		navigation("../p2p/sendMoney1");
	} catch (err) {
		showSysException(err);
	}
}
dfs.crd.p2p.postUpdateEmailAddress = function() {
	try {
		var updateEmailResponseData = "";
		var emailRequiredDOM = $("#errorEmailRequired");
		var emailId = $("#updateEmail").val();
		if (!isEmpty(emailId)) {
			if (validateEmailFormat(emailId)) {
				var UPDATEURL = RESTURL + "p2p/v1/collectemail";
				var emailAddress = emailId;
				var dataJSON = {
					"emailAddress" : "" + emailAddress + ""
				};
				var dataJSONString = JSON.stringify(dataJSON);
				showSpinner();
				$.ajax({
					type : "POST",
					url : UPDATEURL,
					async : false,
					dataType : "json",
					data : dataJSONString,
					headers : preparePostHeader(),
					success : function(responseData, status, jqXHR) {
						hideSpinner();
						if (jqXHR.status != 200 & jqXHR.status != 204) {
							var code = getResponseStatusCode(jqXHR);
							errorHandler(code, "", "updateEmail");
						} else {
							updateEmailResponseData = responseData;
							navigation("../p2p/sendMoney1", true);
						}
					},
					error : function(jqXHR, textStatus, errorThrown) {
						hideSpinner();
						var code = getResponseStatusCode(jqXHR);
						errorHandler(code, "", "updateEmail");
					}
				});
			} else {
				emailRequiredDOM.html(errorCodeMap["1708"]);
				emailRequiredDOM.focus();
			}
		} else {
			emailRequiredDOM.html(errorCodeMap["1707"]);
			emailRequiredDOM.focus();
		}
		return updateEmailResponseData;
	} catch (err) {
		showSysException(err);
	}
}
dfs.crd.p2p.handleTransactionType = function(val) {
	try {
		var textTransType = $("#related_content");
		if (val == "Friends or Family")
			textTransType.text("Fast. Easy.");
		else
			textTransType.text("Free for you, PayPal charges recipient a fee.");
	} catch (err) {
		showSysException(err);
	}
}
dfs.crd.p2p.killDataSendMoney = function() {
	try {
		killDataFromCache("SM1_DATA");
		navigation("../p2p/sendMoney1");
	} catch (err) {
		showSysException(err);
	}
}
dfs.crd.p2p.goToSendMoney1 = function() {
	try {
		dfs.crd.p2p.sendMoreMoneyPage3 = true;
		navigation("../p2p/sendMoney1");
	} catch (err) {
		showSysException(err);
	}
}
dfs.crd.p2p.callingAchome = function() {
	try {
		killDataFromCache("SM1_DATA");
		gotoAchome();
	} catch (err) {
		showSysException(err);
	}
}
dfs.crd.p2p.formatAmount = function() {
	try {
		var amount = $("#Amount").val();
		if (!isEmpty(amount)) {
			var test = (amount * 1);
			var formattedAmount = convertTo2DecimalPoints(amount);
			$("#Amount").val(formattedAmount);
		}
	} catch (err) {
		showSysException(err);
	}
}
dfs.crd.p2p.callSendMoney1 = function() {
	try {
		navigation("../p2p/sendMoney1", false);
	} catch (err) {
		showSysException(err);
	}
}
dfs.crd.p2p.callSendMoney2 = function() {
	try {
		navigation("../p2p/sendMoney2", true);
	} catch (err) {
		showSysException(err);
	}
}
dfs.crd.p2p.getSendMoney1Data = function() {
	try {
		var fullName = $("#fullname").val();
		var decimalAmount = convertTo2DecimalPoints($("#Amount").val());
		var transTypeValue = $("#TransactionType").val();
		var noteVal = $("#noteId").val();
		var transTypeContent = $("#related_content").text();
		var sendMoney = getDataFromCache("SENDMONEY");
		var sendmoneyRetrieve = new Array();
		if(isEmpty(dfs.crd.p2p.phoneOrEmail)){
            dfs.crd.p2p.phoneOrEmail = $("#emailOrphone").val();
		}
		sendmoneyRetrieve[0] = fullName;
		sendmoneyRetrieve[1] = dfs.crd.p2p.phoneOrEmail;
		sendmoneyRetrieve[2] = decimalAmount;
		sendmoneyRetrieve[3] = transTypeValue;
		sendmoneyRetrieve[4] = noteVal;
		sendmoneyRetrieve[5] = transTypeContent
		sendmoneyRetrieve[6] = sendMoney.bonusType;
		putDataToCache("SM1_DATA", sendmoneyRetrieve);
		
		} catch (err) {
		showSysException(err);
	}
}
function sendMoney2Load() {
	try {
		dfs.crd.p2p.validPriorPagesOfStep2 = new Array("sendMoney1",
				"cancelTransaction", "termsConditions");
		if (dfs.crd.p2p.errorCode == "1730_confirmTransaction"
				&& fromPageName == "pageError")
			dfs.crd.p2p.populateSendMoneyPage2();
		else if (!dfs.crd.p2p
				.validatePriorPage(dfs.crd.p2p.validPriorPagesOfStep2))
			return;
		else
			dfs.crd.p2p.populateSendMoneyPage2();
	} catch (err) {
		showSysException(err);
	}
}
function confirmCancelTransactionLoad() {
	try {
		dfs.crd.p2p.validPriorConfirmCancelTransaction = new Array(
				"cancelTransaction", "sendMoney1");
		if (!dfs.crd.p2p
				.validatePriorPage(dfs.crd.p2p.validPriorConfirmCancelTransaction))
			return;
	} catch (err) {
		showSysException(err);
	}
}
function cancelTransactionLoad() {
	try {
		dfs.crd.p2p.validatePriorcancelTransaction = new Array("sendMoney1",
				"sendMoney2");
		if (!dfs.crd.p2p
				.validatePriorPage(dfs.crd.p2p.validatePriorcancelTransaction)) {
			return;
		}
	} catch (err) {
		showSysException(err);
	}
}
dfs.crd.p2p.populateSendMoneyPage2 = function() {
	try {
		var cacheSendMoneyDetails = getDataFromCache("SM1_DATA");
		dfs.crd.p2p.showSendMoney2Data(cacheSendMoneyDetails);
	} catch (err) {
		showSysException(err);
	}
}
dfs.crd.p2p.showSendMoney2Data = function(cacheSendMoneyDetails) {
	try {
		dfs.crd.p2p.sendMoney1Flag = false;
		if (!jQuery.isEmptyObject(cacheSendMoneyDetails)) {
			$("#nickname").html(cacheSendMoneyDetails[0]);
			if ($("#nickname").is(":empty"))
				$("#recipientName").remove();
			if (validateEmailFormat(cacheSendMoneyDetails[1]))
				$("#mail_id").html(cacheSendMoneyDetails[1]);
			else
				$("#mail_id").html(formatPhoneNumber(cacheSendMoneyDetails[1]));
			$("#amount").html("$" + (cacheSendMoneyDetails[2]));
			$("#transaction_Type").html(cacheSendMoneyDetails[3]);
			$("#confirm_note").html(cacheSendMoneyDetails[4]);
			if ($("#confirm_note").is(":empty"))
				$("#note").remove();
			$("#related_content1").html(cacheSendMoneyDetails[5]);
			$("#bonusType").html(cacheSendMoneyDetails[6]);
		} else {
			cpEvent.preventDefault();
		}
	} catch (err) {
		showSysException(err);
	}
}
dfs.crd.p2p.sendingMoney = function() {
	try {
		$("#EnteredInfo").css("display", "none");
		$("#loaderDiv").css("display", "block");
	} catch (err) {
		showSysException(err);
	}
}

dfs.crd.p2p.postSendMoneyData3 = function() {
	try {
		dfs.crd.p2p.sendingMoney();
		var sendMoneyHistory = "";
		var cacheSendMoneyDetails = getDataFromCache("SM1_DATA");
		if (!jQuery.isEmptyObject(cacheSendMoneyDetails)) {
			var SENDMONEYURL3 = RESTURL + "p2p/v1/confirmtransfer";
			var name = cacheSendMoneyDetails[0];
			var contact_info = cacheSendMoneyDetails[1];
			var amount = cacheSendMoneyDetails[2];
			var transType = cacheSendMoneyDetails[3];
			var note = cacheSendMoneyDetails[4];
			var dataJSON = {
				"name" : "" + name + "",
				"amount" : "" + amount + "",
				"phoneOrEmail" : "" + contact_info + "",
				"transactionType" : "" + transType + "",
				"note" : "" + note + ""
			};
			var dataJSONString = JSON.stringify(dataJSON);
			$.ajax({
				type : "POST",
				url : SENDMONEYURL3,
				async : true,
				dataType : "json",
				data : dataJSONString,
				headers : preparePostHeader(),
				success : function(responseData, status, jqXHR) {
                     if (!validateResponse(responseData,"sendMoneyConfirmValidation"))      // Pen Test Validation
                       {
                        errorHandler("SecurityTestFail","","");
                        return;
                        }
					if (jqXHR.status != 200 & jqXHR.status != 204) {
						var code = getResponseStatusCode(jqXHR);
						errorHandler(code, "", "sendMoney3");
					} else {
						putDataToCache("SENDMONEY_SUMMARY", responseData);
						navigation("../p2p/sendMoney3");
					}
				},
				error : function(jqXHR, textStatus, errorThrown) {
					var code = getResponseStatusCode(jqXHR);
					if (code == "1730") {
						errorHandler("1730_confirmTransaction", "",
								"sendMoney3");
						dfs.crd.p2p.errorCode = "1730_confirmTransaction";
					} else if (code == "1706") {
						killDataFromCache("SM1_DATA");
						killDataFromCache("SENDMONEY");
						dfs.crd.p2p.errorCode = "1706";
						errorHandler(code, "", "sendMoney3");
					} else {
						dfs.crd.p2p.errorCode = code;
						errorHandler(code, "", "sendMoney3");
					}
				}
			});
		}
		return sendMoneyHistory;
	} catch (err) {
		showSysException(err);
	}
}
dfs.crd.p2p.checkRegistration = function(sendMoneyHistory) {
	try {
		var canSendMoreMoney = sendMoneyHistory.canSendMoreMoney;
		var isPayPalRegistered = sendMoneyHistory.isPayPalRegistered;
		if (canSendMoreMoney)
			$("#sendmoremoney").css("display", "block");
		else
			$("#sendmoremoney").css("display", "none");
	} catch (err) {
		showSysException(err);
	}
}
function sendMoney3Load() {
	try {
		dfs.crd.p2p.validPriorPagesOfStep3 = new Array("sendMoney2");
		if (!dfs.crd.p2p.validatePriorPage(dfs.crd.p2p.validPriorPagesOfStep3)) {
			return;
		} else {
			var sendMoneySummary = getDataFromCache("SENDMONEY_SUMMARY");
			dfs.crd.p2p.checkRegistration(sendMoneySummary);
			dfs.crd.p2p.populateSendMoney3(sendMoneySummary);
		}
	} catch (err) {
		showSysException(err);
	}
}
dfs.crd.p2p.populateSendMoney3 = function(sendMoneyHistory) {
	try {
		dfs.crd.p2p.setSM3Data(sendMoneyHistory);
		killDataFromCache("SM1_DATA");
		killDataFromCache("SENDMONEY");
	} catch (err) {
		showSysException(err);
	}
}

dfs.crd.p2p.setSM3Data = function(sendMoneyHistory) {
	try {
		$("#trans_id").html(sendMoneyHistory.transactionId);
		$("#transdate").html(sendMoneyHistory.transactionDate);
		$("#recve_name").html(sendMoneyHistory.name);
		$("#trans_name").html(sendMoneyHistory.name);
		$("#trans_email").html(sendMoneyHistory.phoneOrEmail);
		$("#mail_id_3").html(sendMoneyHistory.phoneOrEmail);
		$("#trans_amount").html("$" + sendMoneyHistory.amount);
		$("#confirm_note_3").html(sendMoneyHistory.note);
		$("#trans_type").html(sendMoneyHistory.transactionType);
		var bonusType = getDataFromCache("SM1_DATA");
		if (!jQuery.isEmptyObject(bonusType))
			$("#bonustypes").html(bonusType[6]);
		if ($("#recve_name").is(":empty"))
			$("#recve_name").remove();
		if ($("#trans_name").is(":empty"))
			$("#recipient_name").remove();
		if ($("#confirm_note_3").is(":empty"))
			$("#trans_note").remove();
	} catch (err) {
		showSysException(err);
	}
}
dfs.crd.p2p.validatePhoneNumber = function(phone) {
	try {
		if (!phone.match(phoneRegex)) {
			$("#emailorphone_msg").html(errorCodeMap["Invalid_phone"]);
			$("#emailOrphone").focus();
			//$("#emailOrphone").css("border", "2px solid #ff0000");
			$("#emailOrphone").addClass("errormsg");
			return false;
		} else {
			return true;
		}
		return true;
	} catch (err) {
		showSysException(err);
	}
}
dfs.crd.p2p.notPhoneNumber = function(phoneNumber) {
	try {
		for ( var i = 0; i < phoneNumber.length; i++) {
			if (!phoneNumber.charAt(i).match(regExp))
				return false;
			else
				return true;
		}
		return true;
	} catch (err) {
		showSysException(err);
	}
}
dfs.crd.p2p.verifyEmailCondition = function(email) {
	try {
		if (email.search(emailFormat)) {
			$("#emailorphone_msg").html(errorCodeMap["Invalid_email"]);
			dfs.crd.p2p.focusEmailOrPhoneBlock();
			return false;
		} else {
			var emailid = "";
			var cacheSendMoney = getDataFromCache("SENDMONEY");
			if (!jQuery.isEmptyObject(cacheSendMoney)) {
				emailid = cacheSendMoney.emailAddress;
				if (!isEmpty(emailid)) {
					if (email == emailid) {
						$("#emailorphone_msg").html(
								errorCodeMap["No_Money_Yourself"]);
						dfs.crd.p2p.focusEmailOrPhoneBlock();
						return false;
					} else {
						return true;
					}
				} else {
					return false;
				}
			}
		}
	} catch (err) {
		showSysException(err);
	}
}
dfs.crd.p2p.validateRecipientEmailOrPhone = function() {
	try {
		$("#emailorphone_msg").html("");
		dfs.crd.p2p.phoneOrEmail = $("#emailOrphone").val();
		if ((isEmpty(dfs.crd.p2p.phoneOrEmail))
				|| (dfs.crd.p2p.phoneOrEmail == "E-Mail or Mobile Phone")) {
			$("#emailorphone_msg").html(errorCodeMap["Email_phone_required"]);
			dfs.crd.p2p.focusEmailOrPhoneBlock();
			dfs.crd.p2p.showPageTitleError();
			return false;
		} else if (!isEmpty(dfs.crd.p2p.phoneOrEmail)
				&& dfs.crd.p2p.notPhoneNumber(dfs.crd.p2p.phoneOrEmail)) {
			if (!dfs.crd.p2p.verifyEmailCondition(dfs.crd.p2p.phoneOrEmail))
				return false;
		} else {
			if (!isEmpty(dfs.crd.p2p.phoneOrEmail)) {
				if (dfs.crd.p2p.phoneOrEmail.charAt(0) == "+") {
					dfs.crd.p2p.phoneOrEmail = dfs.crd.p2p.phoneOrEmail
							.replace(dfs.crd.p2p.phoneOrEmail.charAt(0), "");
				}
				dfs.crd.p2p.phoneOrEmail = dfs.crd.p2p.phoneOrEmail.trim();
				dfs.crd.p2p.phoneOrEmail = dfs.crd.p2p.phoneOrEmail.replace(
						/\D/g, "");
				if (dfs.crd.p2p.phoneOrEmail.length == 11) {
					var substring = dfs.crd.p2p.phoneOrEmail.substr(0, 1);
					if (substring == "1") {
						var phoneOrEmailsubstr = dfs.crd.p2p.phoneOrEmail
								.substr(0, 1);
						dfs.crd.p2p.phoneOrEmail = dfs.crd.p2p.phoneOrEmail
								.replace(phoneOrEmailsubstr, "");
						dfs.crd.p2p.phoneOrEmail = dfs.crd.p2p.phoneOrEmail
								.trim();
					} else {
						$("#emailorphone_msg").html(
								errorCodeMap["Ten_Digit_Number"]);
						dfs.crd.p2p.focusEmailOrPhoneBlock();
						dfs.crd.p2p.showPageTitleError();
						return false;
					}
				}
				if (dfs.crd.p2p.phoneOrEmail.length == 10) {
					var numphoneOrEmail = dfs.crd.p2p.phoneOrEmail.replace(
							/\D/g, "");
					var charatfirstposition = numphoneOrEmail.charAt(0);
					if (charatfirstposition == "0") {
						$("#emailorphone_msg").html(
								errorCodeMap["Number_cannot_start0"]);
						dfs.crd.p2p.focusEmailOrPhoneBlock();
						dfs.crd.p2p.showPageTitleError();
						return false;
					} else {
						if (charatfirstposition == "1") {
							$("#emailorphone_msg").html(
									errorCodeMap["Number_cannot_start1"]);
							dfs.crd.p2p.focusEmailOrPhoneBlock();
							dfs.crd.p2p.showPageTitleError();
							return false;
						} else {
							if (!(dfs.crd.p2p
									.validatePhoneNumber(dfs.crd.p2p.phoneOrEmail))) {
								$("#emailorphone_msg").html(
										errorCodeMap["Ten_Digit_Number"]);
								dfs.crd.p2p.focusEmailOrPhoneBlock();
								dfs.crd.p2p.showPageTitleError();
								return false;
							}
						}
					}
				} else if ((dfs.crd.p2p.phoneOrEmail.length == 14 || dfs.crd.p2p.phoneOrEmail.length == 13)
						&& dfs.crd.p2p.phoneOrEmail.substring(0, 1) == "("
						&& (dfs.crd.p2p.phoneOrEmail.substring(4, 5) == ")" || dfs.crd.p2p.phoneOrEmail
								.substring(3, 4) == ")")
						&& (dfs.crd.p2p.phoneOrEmail.substring(9, 10) == "-" || dfs.crd.p2p.phoneOrEmail
								.substring(8, 9) == "-")) {
					dfs.crd.p2p.phoneOrEmail = dfs.crd.p2p.phoneOrEmail
							.replace(/\D/g, "");
					return true;
				} else {
					$("#emailorphone_msg").html(
							errorCodeMap["Ten_Digit_Number"]);
					dfs.crd.p2p.focusEmailOrPhoneBlock();
					dfs.crd.p2p.showPageTitleError();
					return false;
				}
			}
		}
		return true;
	} catch (err) {
		showSysException(err);
	}
}
dfs.crd.p2p.highlightAmountField = function() {
	try {
		$("#Amount").focus();
		//$("#Amount").css("border", "2px solid #ff0000");
		$("#Amount").addClass("errormsg");
		$("#errorTitle").css("display", "block");
		$("#amount_msg").css("display", "block");
	} catch (err) {
		showSysException(err);
	}
}
dfs.crd.p2p.validateAmount = function() {
	try {
		$("#amount_msg").html("");
		var dailyLimit = "";
		var monthlyLimit = "";
		var cacheSendMoney = getDataFromCache("SENDMONEY");
		if (!jQuery.isEmptyObject(cacheSendMoney)) {
			dailyLimit = cacheSendMoney.dailyLimitAvailable;
			monthlyLimit = cacheSendMoney.monthlyLimitAvailable;
			if (!isEmpty(dailyLimit) && !isEmpty(monthlyLimit)) {
				var amount = $("#Amount").val();
				if (isEmpty(amount) || amount.length == 0 || amount == "0") {
					$("#amount_msg").html(errorCodeMap["Amount_Required"]);
					dfs.crd.p2p.highlightAmountField();
					return false;
				} else {
					if (!isDecimal(amount)) {
						$("#amount_msg").html(errorCodeMap["Only_Numbers"]);
						dfs.crd.p2p.highlightAmountField();
						return false;
					} else {
						if ((amount.indexOf(".") != -1)
								&& (!(amountHasonly2Decimals(amount)))) {
							$("#amount_msg")
									.html(errorCodeMap["Decimal_Point"]);
							dfs.crd.p2p.highlightAmountField();
							return false;
						} else {
							var amount = convertTo2DecimalPoints(amount);
							var dailyLimitFloat = parseFloat(dailyLimit);
							var monthlyLimitFloat = parseFloat(monthlyLimit);
							var dailyLimitInDecimals = (dailyLimitFloat)
									.toFixed(2);
							var monthlyLimitInDecimals = (monthlyLimitFloat)
									.toFixed(2);
							if (amount == "0.0" || amount == "0.00") {
								$("#amount_msg").html(
										errorCodeMap["Amount_Required"]);
								dfs.crd.p2p.highlightAmountField();
								return false;
							} else if (parseFloat(amount) > parseFloat(dailyLimitInDecimals)) {
								$("#amount_msg").html("<b>Amount entered will cause you to exceed your daily limit.</b>");
								dfs.crd.p2p.highlightAmountField();
								return false;
							} else if (parseFloat(amount) > parseFloat(monthlyLimitInDecimals)) {
								dfs.crd.p2p.highlightAmountField();
								$("#amount_msg").html("<b>Amount entered will cause you to exceed your monthly limit.</b>");       
								return false;
							}
						}
					}
				}
				return true;
			}
		}
	} catch (err) {
		showSysException(err);
	}
}
dfs.crd.p2p.validateData = function() {
	try {
		$("#name_msg").html("");
		$("#amount_msg").html("");
		/*$("#fullname").css("border", "none");
		$("#Amount").css("border", "none");
		$("#emailOrphone").css("border", "none");*/
		$("#fullname").removeClass("errormsg");
        $("#Amount").removeClass("errormsg");
        $("#emailOrphone").removeClass("errormsg");
		var name = $("#fullname").val();
		var valid1 = dfs.crd.p2p.validateRecipientEmailOrPhone();
		var valid2 = dfs.crd.p2p.validateAmount();
		var valid3 = validateName(name);
		if (!valid3) {
			$("#name_msg").html(errorCodeMap["No_special_characters"]);
			$("#fullname").focus();
			$("#errorTitle").css("display", "block");
			//$("#fullname").css("border", "2px solid #ff0000");
			$("#fullname").addClass("errormsg");
		}
		if (valid1 && valid2 && valid3) {
			dfs.crd.p2p.getSendMoney1Data();
			navigation("../p2p/sendMoney2");
		}
	} catch (err) {
		showSysException(err);
	}
}
dfs.crd.p2p.getTransactionHistoryData = function(size, status, index) {
	try {
		var history = "";
		var newDate = new Date();
		var TRANSACTIONHISTORYURL = RESTURL
				+ "p2p/v1/transactionhistory?startIndex=" + index + "&size="
				+ size + "&statusType=" + status + "&showStatus=true&"
				+ newDate + "";
		showSpinner();
		$.ajax({
			type : "GET",
			url : TRANSACTIONHISTORYURL,
			async : false,
			dataType : "json",
			headers : prepareGetHeader(),
			success : function(responseData, status, jqXHR) {
				hideSpinner();
				if (jqXHR.status != 200 & jqXHR.status != 204) {
					var code = getResponseStatusCode(jqXHR);
					errorHandler(code, "", "transactionHistory");
				} else {
					history = responseData;
				}
			},
			error : function(jqXHR, textStatus, errorThrown) {
				hideSpinner();
				cpEvent.preventDefault();
				var code = getResponseStatusCode(jqXHR);
				errorHandler(code, "", "transactionHistory");
				dfs.crd.p2p.errorCode = code;
			}
		});
		return history;
	} catch (err) {
		showSysException(err);
	}
}
function transactionHistoryLoad() {
	try {
		if (acLiteModeFlag) {
			cpEvent.preventDefault();
			errorHandler("acLiteOutageMode_ACL", "", "transactionHistory");
		} else {
			dfs.crd.p2p.sendMoney1Flag = false;
			dfs.crd.p2p.counter = 25;
			dfs.crd.p2p.loadMoreHit = 1;
			dfs.crd.p2p.loadMoreHitFlag = false;
			var responseData;					
			if (dfs.crd.p2p.cancelledTransaction) {
				if ((dfs.crd.p2p.lastStatusSelected.substring(0,
						dfs.crd.p2p.lastStatusSelected.indexOf("("))) == "Pending ")
					responseData = dfs.crd.p2p.getTransactionHistoryData(
							dfs.crd.p2p.loadTransaction, "pendingtransactions",
							dfs.crd.p2p.basicLoadIndex);
				else if ((dfs.crd.p2p.lastStatusSelected.substring(0,
						dfs.crd.p2p.lastStatusSelected.indexOf("("))) == "All Transactions ")
					responseData = dfs.crd.p2p.getTransactionHistoryData(
							dfs.crd.p2p.loadTransaction, "alltransactions",
							dfs.crd.p2p.basicLoadIndex);
			} else
				responseData = dfs.crd.p2p.getTransactionHistoryData(
						dfs.crd.p2p.loadTransaction, "alltransactions",
						dfs.crd.p2p.basicLoadIndex);
			if (!jQuery.isEmptyObject(responseData)) {
				var entity = responseData.sendMoneyHistory;
				if (entity.length > 0) {
					if (dfs.crd.p2p.cancelledTransaction) {
						if ((dfs.crd.p2p.lastStatusSelected.substring(0,
								dfs.crd.p2p.lastStatusSelected.indexOf("("))) == "Pending ")
							dfs.crd.p2p.populateHistoryData("Pending ",
									responseData);
						else if ((dfs.crd.p2p.lastStatusSelected.substring(0,
								dfs.crd.p2p.lastStatusSelected.indexOf("("))) == "All Transactions ")
							dfs.crd.p2p.populateHistoryData("alltransactions",
									responseData);
					} else {
						dfs.crd.p2p.populateHistoryData("alltransactions",
								responseData);
					}
				} else {
					if (dfs.crd.p2p.cancelledTransaction) {
						dfs.crd.p2p.populateHistoryData("Pending ",
								responseData);
					} else {
						dfs.crd.p2p.errorCode = "noTransactions";
						dfs.crd.p2p.cancelledTransaction = false;
						errorHandler("noTransactions", "", "transactionHistory");
					}
				}
			}
		}
	} catch (err) {
		showSysException(err);
	}
}
dfs.crd.p2p.populateHistoryData = function(statusSelected, responseData) {
	try {
		var entityList = "";
		var listTransactionHistory = "";
		dfs.crd.p2p.clearIndexJSONArray();				
			
		if (!jQuery.isEmptyObject(responseData)) {
			entityList = responseData.sendMoneyHistory;
			var allCount = responseData.allCount;
			var pendingCount = responseData.pendingCount;
			var cancelCount = responseData.cancelledCount;
			var completeCount = responseData.completedCount;
			var partialcount = responseData.partialRefundCount;
			var refundCount = responseData.refundedCount;
			var canSendMoney = responseData.canSendMoney;

			if (statusSelected == "alltransactions")
				dfs.crd.p2p.maxTransactionCount = allCount;
			else if (statusSelected == "Cancelled ")
				dfs.crd.p2p.maxTransactionCount = cancelCount;
			else if (statusSelected == "Claimed ")
				dfs.crd.p2p.maxTransactionCount = completeCount;
			else if (statusSelected == "Partially Refunded ")
				dfs.crd.p2p.maxTransactionCount = partialcount;
			else if (statusSelected == "Pending ")
				dfs.crd.p2p.maxTransactionCount = pendingCount;
			else if (statusSelected == "Refunded ")
				dfs.crd.p2p.maxTransactionCount = refundCount;
			if (dfs.crd.p2p.maxTransactionCount >= 0) {
				if (dfs.crd.p2p.maxTransactionCount < dfs.crd.p2p.counter)
					dfs.crd.p2p.counter = dfs.crd.p2p.maxTransactionCount;
				for (loopCount in entityList) {
					var status = entityList[loopCount].currentTransStatus;
					var transactionDate = entityList[loopCount].transactionDate;
					var transactionId = entityList[loopCount].transactionId;
					var transactionType = entityList[loopCount].transactionType;
					var amount = entityList[loopCount].amount;
					var recepientname = entityList[loopCount].recipientName;
					if (isEmpty(recepientname))
						recepientname = "";
					var recepientId = entityList[loopCount].recipientId;
					var note = entityList[loopCount].note;
					selectedDataInJson[loopCount] = transactionId;
					var divForStatus = "";
					var statushtml = "";
					var detailshtml = "<li class='noborder topmargin'><a href='#' class='bluelink boldtext collapsdata' id='aId"
							+ loopCount
							+ "' rel='data_trns_his"
							+ loopCount
							+ "' onclick='dfs.crd.p2p.getRefreshDetails(this)'>Show Details</a><div class='clearboth'></div><div class='floatleft' id='loaderIcon"
							+ loopCount
							+ "' style='display:none'><img width='23' height='16' src='../../images/loader-small.gif' /></div></li><li class='noborder transdata' id='data_trns_his"
							+ loopCount
							+ "' style='display:none'><div>sent to<span>"
							+ recepientId
							+ "</span></div><div>Transaction Type<span>"
							+ transactionType
							+ "</span></div><div>Note<span>"
							+ note
							+ "</span></div><div>Transaction Date <span>"
							+ transactionDate
							+ "</span></div><div class='floatleft'>Status<span class='boldtext'>"
							+ status + "</span></div></li>";
					if (status == "PENDING") {
						divForStatus = "<div class='floatright boldtext' id='divRefreshCancel"
								+ loopCount
								+ "'><a href='#' class='bluelink boldtext' id ='arefresh"
								+ loopCount
								+ "' onclick='dfs.crd.p2p.getRefreshStatus(this)'> Refresh </a> | <a href='#' class='bluelink boldtext' id ='acancell"
								+ loopCount
								+ "' onclick='dfs.crd.p2p.goToNav(this)'>Cancel</a></div>"
						status = entityList[loopCount].displayStatus;
						statushtml = "<li class='trans_history noborder'><div class='floatleft'>Status</div><div>"
								+ divForStatus
								+ "<div class='floatright boldtext' style='margin-right:5px;' id='divStatus"
								+ loopCount
								+ "'>"
								+ status
								+ " |"
								+ " </div></div></li>";
					} else {
						status = entityList[loopCount].displayStatus;
						statushtml = "<li class='trans_history noborder'><div class='floatleft'>Status</div><div>"
								+ divForStatus
								+ "<div class='floatright boldtext' id='divStatus"
								+ loopCount
								+ "'>"
								+ status
								+ " </div></div></li>";
					}
					var UL = "<ul class='whitebg-list-view collapsible' id='historyId' ><li> Transaction Date<span class='amt_bold' id='spanTransDate"
							+ loopCount
							+ "'>"
							+ transactionDate
							+ "</span></li><li>Amount<span class='amt_bold' id='spanAmt"
							+ loopCount
							+ "'>$"
							+ amount
							+ "</span> </li><li>Transaction ID<span class='amt_bold' id='spanTransId"
							+ loopCount
							+ "'>"
							+ transactionId
							+ "</span></li><li>Recepient's Name<span class='amt_bold' id='spanRecpNme"
							+ loopCount
							+ "'>"
							+ recepientname
							+ "</span></li>"
							+ statushtml + detailshtml + "<!--<li></li>--></ul>";
					listTransactionHistory += UL;
				}
				$("#historyFilter").html(listTransactionHistory);
				if (!dfs.crd.p2p.filterFlag) {
					var dropdown = "<div class='ui-select'><div data-corners='true' data-shadow='true' data-iconshadow='true' data-wrapperels='span' data-icon='arrow-d' data-iconpos='right' data-theme='d' data-inline='false' data-mini='false' class='ui-btn ui-shadow ui-btn-corner-all ui-fullsize ui-btn-block ui-btn-icon-right ui-btn-up-d'><span class='ui-btn-inner ui-btn-corner-all'><span class='ui-btn-text'  id='transactionDataAvail'>All Transactions ("
							+ allCount
							+ ")</span><span class='ui-icon ui-icon-arrow-d ui-icon-shadow'>&nbsp;</span></span>"
							+ "<select name='select-choice-0' id='select-choice-1' onchange='dfs.crd.p2p.getFilteredTransactionHistory(this.value);'>"
							+ "<option id='allTrans' value='All Transactions ("
							+ allCount
							+ ")'>All Transactions ("
							+ allCount
							+ ")</option>"
							+ "<option value='Cancelled ("
							+ cancelCount
							+ ")'>Cancelled ("
							+ cancelCount
							+ ")</option>"
							+ "<option value='Claimed ("
							+ completeCount
							+ ")'>Claimed ("
							+ completeCount
							+ ")</option>"
							+ "<option value='Partially Refunded ("
							+ partialcount
							+ ")'>Partially Refunded ("
							+ partialcount
							+ ")</option>"
							+ "<option id='pendingTrans' value='Pending ("
							+ pendingCount
							+ ")'>Pending ("
							+ pendingCount
							+ ")</option>"
							+ "<option value='Refunded ("
							+ refundCount
							+ ")'>Refunded ("
							+ refundCount
							+ ")</option>" + "</select></div></div>";
					$("#showTransactionsStatus").html(dropdown);
					dfs.crd.p2p.setHistoryDropDownSelection(pendingCount,
							allCount);
				}
				dfs.crd.p2p.filterFlag = false;
				dfs.crd.p2p.toggleLoadMoreButton(
						dfs.crd.p2p.maxTransactionCount, dfs.crd.p2p.counter);
				dfs.crd.p2p.canSendMoneyButton(canSendMoney);
			}
		}
	} catch (err) {
		showSysException(err);
	}
}

dfs.crd.p2p.setHistoryDropDownSelection = function(pendingCount, allCount) {
	try {
		if (dfs.crd.p2p.cancelledTransaction) {
			if ((dfs.crd.p2p.lastStatusSelected.substring(0,
					dfs.crd.p2p.lastStatusSelected.indexOf("("))) == "Pending ") {
				$("#pendingTrans").attr("selected", "selected");
				$("#transactionDataAvail").html(
						"Pending (" + pendingCount + ")");
			} else if ((dfs.crd.p2p.lastStatusSelected.substring(0,
					dfs.crd.p2p.lastStatusSelected.indexOf("("))) == "All Transactions ") {
				$("#allTrans").attr("selected", "selected");
				$("#transactionDataAvail").html(
						"All Transactions (" + allCount + ")");
			}
			dfs.crd.p2p.cancelledTransaction = false;
		}
	} catch (err) {
		showSysException(err);
	}
}

dfs.crd.p2p.loadMoreTransactions = function() {
	try {
		var basicCount = 25;
		dfs.crd.p2p.loadMoreHit += 1;
		dfs.crd.p2p.counter = (basicCount * dfs.crd.p2p.loadMoreHit);
		dfs.crd.p2p.loadMoreHitFlag = true;
		var indexValue = (basicCount * (dfs.crd.p2p.loadMoreHit - 1));
		var selectedStatus = $("#select-choice-1").val();
		if ((selectedStatus.substring(0, selectedStatus.indexOf("("))) == "All Transactions ") {
			var responseData = dfs.crd.p2p.getTransactionHistoryData(
					dfs.crd.p2p.loadTransaction, "alltransactions", indexValue);
			if (!jQuery.isEmptyObject(responseData))
				dfs.crd.p2p.displayMoreTransactions("alltransactions",
						indexValue, responseData);
		} else if ((selectedStatus.substring(0, selectedStatus.indexOf("("))) == "Cancelled ") {
			var responseData = dfs.crd.p2p.getTransactionHistoryData(
					dfs.crd.p2p.loadTransaction, "canceltransactions",
					indexValue);
			if (!jQuery.isEmptyObject(responseData))
				dfs.crd.p2p.displayMoreTransactions("Cancelled ", indexValue,
						responseData);
		} else if ((selectedStatus.substring(0, selectedStatus.indexOf("("))) == "Claimed ") {
			var responseData = dfs.crd.p2p.getTransactionHistoryData(
					dfs.crd.p2p.loadTransaction, "completedtransactions",
					indexValue);
			if (!jQuery.isEmptyObject(responseData))
				dfs.crd.p2p.displayMoreTransactions("Claimed ", indexValue,
						responseData);
		} else if ((selectedStatus.substring(0, selectedStatus.indexOf("("))) == "Partially Refunded ") {
			var responseData = dfs.crd.p2p.getTransactionHistoryData(
					dfs.crd.p2p.loadTransaction, "partialtransactions",
					indexValue);
			if (!jQuery.isEmptyObject(responseData))
				dfs.crd.p2p.displayMoreTransactions("Partially Refunded ",
						indexValue, responseData);
		} else if ((selectedStatus.substring(0, selectedStatus.indexOf("("))) == "Pending ") {
			var responseData = dfs.crd.p2p.getTransactionHistoryData(
					dfs.crd.p2p.loadTransaction, "pendingtransactions",
					indexValue);
			if (!jQuery.isEmptyObject(responseData))
				dfs.crd.p2p.displayMoreTransactions("Pending ", indexValue,
						responseData);
		} else if ((selectedStatus.substring(0, selectedStatus.indexOf("("))) == "Refunded ") {
			var responseData = dfs.crd.p2p.getTransactionHistoryData(
					dfs.crd.p2p.loadTransaction, "refundedtransactions",
					indexValue);
			if (!jQuery.isEmptyObject(responseData))
				dfs.crd.p2p.displayMoreTransactions("Refunded ", indexValue,
						responseData);
		}
	} catch (err) {
		showSysException(err);
	}
}

dfs.crd.p2p.displayMoreTransactions = function(statusSelected, indexValue,
		responseData) {
	try {		
		var entityList = "";
		var listTransactionHistory = "";
		var tempCnt = 0;
		if (!jQuery.isEmptyObject(responseData)) {
			entityList = responseData.sendMoneyHistory;
			var allCount = responseData.allCount;
			var pendingCount = responseData.pendingCount;
			var cancelCount = responseData.cancelledCount;
			var completeCount = responseData.completedCount;
			var partialcount = responseData.partialRefundCount;
			var refundCount = responseData.refundedCount;
			var canSendMoney = responseData.canSendMoney;
			if (statusSelected == "alltransactions")
				dfs.crd.p2p.maxTransactionCount = allCount;
			else if (statusSelected == "Cancelled ")
				dfs.crd.p2p.maxTransactionCount = cancelCount;
			else if (statusSelected == "Claimed ")
				dfs.crd.p2p.maxTransactionCount = completeCount;
			else if (statusSelected == "Partially Refunded ")
				dfs.crd.p2p.maxTransactionCount = partialcount;
			else if (statusSelected == "Pending ")
				dfs.crd.p2p.maxTransactionCount = pendingCount;
			else if (statusSelected == "Refunded ")
				dfs.crd.p2p.maxTransactionCount = refundCount;

			if (dfs.crd.p2p.maxTransactionCount >= 0) {
				if (dfs.crd.p2p.maxTransactionCount < dfs.crd.p2p.counter)
					tempCnt = (dfs.crd.p2p.maxTransactionCount - ((dfs.crd.p2p.loadMoreHit - 1) * 25));
				else
					tempCnt = 25;
				for (loopCount in entityList) {
					var status = entityList[loopCount].currentTransStatus;
					var transactionDate = entityList[loopCount].transactionDate;
					var transactionId = entityList[loopCount].transactionId;
					var transactionType = entityList[loopCount].transactionType;
					var amount = entityList[loopCount].amount;
					var recepientname = entityList[loopCount].recipientName;
					if (isEmpty(recepientname))
						recepientname = "";
					var recepientId = entityList[loopCount].recipientId;
					var note = entityList[loopCount].note;
					var loopElements = ((parseInt(loopCount)) + indexValue + 1);
					selectedDataInJson[loopElements] = transactionId;
					var divForStatus = "";
					var statushtml = "";
					var detailshtml = "<li class='noborder topmargin'><a href='#' class='bluelink boldtext collapsdata' id='aId"
							+ loopElements
							+ "' rel='data_trns_his"
							+ loopElements
							+ "' onclick='dfs.crd.p2p.getRefreshDetails(this)'>Show Details</a><div class='clearboth'></div><div class='floatleft' id='loaderIcon"
							+ loopElements
							+ "' style='display:none'><img width='23' height='16' src='../../images/loader-small.gif'/></div></li><li class='noborder transdata' id='data_trns_his"
							+ loopElements
							+ "' style='display:none'><div>sent to<span>"
							+ recepientId
							+ "</span></div><div>Transaction Type<span>"
							+ transactionType
							+ "</span></div><div>Note<span>"
							+ note
							+ "</span></div><div>Transaction Date <span>"
							+ transactionDate
							+ "</span></div><div class='floatleft'>Status<span class='boldtext'>"
							+ status + "</span></div></li>";
					if (status == "PENDING") {
						divForStatus = "<div class='floatright boldtext' id='divRefreshCancel"
								+ loopElements
								+ "'><a href='#' class='bluelink boldtext' id ='arefresh"
								+ loopElements
								+ "' onclick='dfs.crd.p2p.getRefreshStatus(this)'> Refresh </a> | <a href='#' class='bluelink boldtext' id ='acancell"
								+ loopElements
								+ "' onclick='dfs.crd.p2p.goToNav(this)'>Cancel</a></div>"
						status = entityList[loopCount].displayStatus;
						statushtml = "<li class='trans_history noborder'><div class='floatleft'>Status</div><div>"
								+ divForStatus
								+ "<div class='floatright boldtext' style='margin-right:5px;' id='divStatus"
								+ loopElements
								+ "'>"
								+ status
								+ " |"
								+ " </div></div></li>";
					} else {
						status = entityList[loopCount].displayStatus;
						statushtml = "<li class='trans_history noborder'><div class='floatleft'>Status</div><div>"
								+ divForStatus
								+ "<div class='floatright boldtext' id='divStatus"
								+ loopElements
								+ "'>"
								+ status
								+ " </div></div></li>";
					}
					var UL = "<ul class='whitebg-list-view collapsible' id='historyId' ><li> Transaction Date<span class='amt_bold' id='spanTransDate"
							+ loopElements
							+ "'>"
							+ transactionDate
							+ "</span></li><li>Amount<span class='amt_bold' id='spanAmt"
							+ loopElements
							+ "'>$"
							+ amount
							+ "</span> </li><li>Transaction ID<span class='amt_bold' id='spanTransId"
							+ loopElements
							+ "'>"
							+ transactionId
							+ "</span></li><li>Recepient's Name<span class='amt_bold' id='spanRecpNme"
							+ loopElements
							+ "'>"
							+ recepientname
							+ "</span></li>"
							+ statushtml
							+ detailshtml
							+ "<li></li></UL>";
					listTransactionHistory += UL;
				}
				$("#historyFilter").append(listTransactionHistory);
				dfs.crd.p2p.toggleLoadMoreButton(
						dfs.crd.p2p.maxTransactionCount, dfs.crd.p2p.counter);
				dfs.crd.p2p.canSendMoneyButton(canSendMoney);
			}
		}
	} catch (err) {
		showSysException(err);
	}
}

dfs.crd.p2p.getFilteredTransactionHistory = function(statusValue) {
	try {
		dfs.crd.p2p.counter = 25;
		dfs.crd.p2p.loadMoreHitFlag = false;

		listTransactionHistory = "";
		dfs.crd.p2p.filterFlag = true;
		dfs.crd.p2p.loadMoreHit = 1;
		var indexStart = 0;
		var selectedStatus = $("#select-choice-1").val();
		if ((selectedStatus.substring(0, selectedStatus.indexOf("("))) == "All Transactions ") {
			var responseData = dfs.crd.p2p.getTransactionHistoryData(
					dfs.crd.p2p.loadTransaction, "alltransactions",
					dfs.crd.p2p.basicLoadIndex);
			if (!jQuery.isEmptyObject(responseData)) {
				dfs.crd.p2p
						.populateHistoryData("alltransactions", responseData);
				$("#transactionDataAvail").html(selectedStatus);
			}
		} else if ((selectedStatus.substring(0, selectedStatus.indexOf("("))) == "Cancelled ") {
			var responseData = dfs.crd.p2p.getTransactionHistoryData(
					dfs.crd.p2p.loadTransaction, "canceltransactions",
					dfs.crd.p2p.basicLoadIndex);
			if (!jQuery.isEmptyObject(responseData)) {
				dfs.crd.p2p.populateHistoryData("Cancelled ", responseData);
				$("#transactionDataAvail").html(selectedStatus);
			}
		} else if ((selectedStatus.substring(0, selectedStatus.indexOf("("))) == "Claimed ") {
			var responseData = dfs.crd.p2p.getTransactionHistoryData(
					dfs.crd.p2p.loadTransaction, "completedtransactions",
					dfs.crd.p2p.basicLoadIndex);
			if (!jQuery.isEmptyObject(responseData)) {
				dfs.crd.p2p.populateHistoryData("Claimed ", responseData);
				$("#transactionDataAvail").html(selectedStatus);
			}
		} else if ((selectedStatus.substring(0, selectedStatus.indexOf("("))) == "Partially Refunded ") {
			var responseData = dfs.crd.p2p.getTransactionHistoryData(
					dfs.crd.p2p.loadTransaction, "partialtransactions",
					dfs.crd.p2p.basicLoadIndex);
			if (!jQuery.isEmptyObject(responseData)) {
				dfs.crd.p2p.populateHistoryData("Partially Refunded ",
						responseData);
				$("#transactionDataAvail").html(selectedStatus);
			}
		} else if ((selectedStatus.substring(0, selectedStatus.indexOf("("))) == "Pending ") {
			var responseData = dfs.crd.p2p.getTransactionHistoryData(
					dfs.crd.p2p.loadTransaction, "pendingtransactions",
					dfs.crd.p2p.basicLoadIndex);
			if (!jQuery.isEmptyObject(responseData)) {
				dfs.crd.p2p.populateHistoryData("Pending ", responseData);
				$("#transactionDataAvail").html(selectedStatus);
			}
		} else if ((selectedStatus.substring(0, selectedStatus.indexOf("("))) == "Refunded ") {
			var responseData = dfs.crd.p2p.getTransactionHistoryData(
					dfs.crd.p2p.loadTransaction, "refundedtransactions",
					dfs.crd.p2p.basicLoadIndex);
			if (!jQuery.isEmptyObject(responseData)) {
				dfs.crd.p2p.populateHistoryData("Refunded ", responseData);
				$("#transactionDataAvail").html(selectedStatus);
			}
		}
	} catch (err) {
		showSysException(err);
	}
}
dfs.crd.p2p.getRefreshDetails = function(obj) {
	try {
		var aId = obj.id;
		var length = aId.length;
		var offset = aId.lastIndexOf("aId");
		var current_iteration = +aId.substring(3, length);

		var objId = "aId" + current_iteration;
		var liId = "data_trns_his" + current_iteration;
		var transactionId = (selectedDataInJson[current_iteration]);
		if (obj.text == "Show Details") {
			dfs.crd.p2p.getDetailsFromServer(transactionId, obj,
					current_iteration);
		} else if (obj.text == "Hide Details") {
			$("#" + objId).html("Show Details");
			$("#" + liId).css("display", "none");
			$("#" + liId).html("");
		}
	} catch (err) {
	}
}
dfs.crd.p2p.getDetailsFromServer = function(transactionId, obj,
		current_iteration) {
	try {
		var newDate = new Date();
		var TRANSACTIONHISTORYURL = RESTURL
				+ "p2p/v1/refreshtransaction?transactionId=" + transactionId
				+ "&requestType=details&" + newDate + "";
		dfs.crd.p2p.showLoader(current_iteration);
		$
				.ajax({
					type : "GET",
					url : TRANSACTIONHISTORYURL,
					dataType : "json",
					headers : prepareGetHeader(),
					success : function(responseData, status, jqXHR) {
						dfs.crd.p2p.hideLoader(current_iteration);
						if (jqXHR.status != 200 & jqXHR.status != 204) {
							var code = getResponseStatusCode(jqXHR);
							errorHandler(code, "", "transactionHistory");
						} else {
							dfs.crd.p2p.hideLoader(current_iteration);
							dfs.crd.p2p.viewDetailsResponse = responseData;
							dfs.crd.p2p.updateDataDetails(
									dfs.crd.p2p.viewDetailsResponse,
									current_iteration);
						}
					},
					error : function(jqXHR, textStatus, errorThrown) {
						dfs.crd.p2p.hideLoader(current_iteration);
						cpEvent.preventDefault();
						var code = getResponseStatusCode(jqXHR);
						errorHandler(code, "", "transactionHistory");
					}
				});
	} catch (err) {
	}
}

dfs.crd.p2p.updateDataDetails = function(responseData, curr_iteration) {
	try {
		var datesDetails = "";
		var objId = "aId" + curr_iteration;
		var obj = document.getElementById(objId);
		var liId = "data_trns_his" + curr_iteration;
		var detailsHtml = "";
		var dateLoop;
		if (!jQuery.isEmptyObject(obj)) {
			if (obj.text == "Show Details") {
				if (!jQuery.isEmptyObject(responseData)) {
					var recipientId = responseData.recipientId;
					var transactionType = responseData.transactionType;
					var note = responseData.note;
					var statusDetails = responseData.statusDetails;
					var transDateslength = statusDetails.length;
					for (dateLoop in statusDetails) {
						var date = statusDetails[dateLoop].statusDate;
						var transactionDate = date.substr(0, 3)
								+ date.substr(3, 3) + date.substr(8, 10);
						var status = statusDetails[dateLoop].status;
						var amount = statusDetails[dateLoop].amount;
						datesDetails += "<div class='floatleft'>"
								+ transactionDate
								+ "</div><div class='floatleft marginleft10px'>"
								+ status + "</div><div class='floatright'>$"
								+ amount + "</div>";
					}
					$("#" + objId).html("Hide Details");
					$("#" + liId).css("display", "block");
					detailsHtml = "<div>Sent to<span class='breakword'><b>"
							+ recipientId
							+ "</b></span></div><div class='clearboth'>Transaction Type<span><b>"
							+ transactionType
							+ "</b></span></div><div>Note<span class='wordWrap'><b>"
							+ note
							+ "</b></span></div><div class='clearboth'></div><div><b>Transaction Date: </b></div><div>"
							+ datesDetails + "</div>";
					$("#" + liId).html(detailsHtml);
				}
			}
		}
	} catch (err) {
		showSysException(err);
	}
}
dfs.crd.p2p.goToNav = function(obj) {
	try {
		var aId = obj.id;
		var length = aId.length;
		var offset = aId.lastIndexOf("acancell");
		var current_iteration = +aId.substring(8, length);
		var transactionId = (selectedDataInJson[current_iteration]);
		current_iteration_for_cancel = current_iteration;
		trans_id_for_cancel = transactionId;
		dfs.crd.p2p.lastStatusSelected = $("#select-choice-1").val();
		navigation("../p2p/cancelTransactionHistory", false);
	} catch (err) {
		showSysException(err);
	}
}

dfs.crd.p2p.postCancelStatus = function() 
{
	try {
		var transactionId = "";
		var updatedStatus = "";
		var yesCancelDiv=$("#yes_cancel");
		var loaderImgDiv=$("#loader_img");
		dfs.crd.p2p.cancelledTransaction = true;
		if (!isEmpty(trans_id_for_cancel)) {
			transactionId = trans_id_for_cancel;
			var dataJSON = {
					"transactionId" : "" + transactionId + ""
			};
			var dataJSONString = JSON.stringify(dataJSON);
			var CANCELSTATUSURL = RESTURL + "p2p/v1/canceltransaction";			
			yesCancelDiv.css("display", "none");
			loaderImgDiv.css("display", "block");
			$.ajax({
				type : "POST",
				url : CANCELSTATUSURL,
				dataType : "json",
				data : dataJSONString,
				headers : preparePostHeader(),
				success : function(responseData, status, jqXHR) {
					loaderImgDiv.css("display", "none");
					yesCancelDiv.css("display", "block");
					if (jqXHR.status != 200 & jqXHR.status != 204) {
						var code = getResponseStatusCode(jqXHR);
						errorHandler(code, "", "transactionHistory");
					} else {
						if (!isEmpty(responseData)) {
							updatedStatus = responseData;
							navigation("../p2p/transactionHistory", false);
						}
					}
				},
				error : function(jqXHR, textStatus, errorThrown) {
					loaderImgDiv.css("display", "none");
					yesCancelDiv.css("display", "block");
					var code = getResponseStatusCode(jqXHR);
					if (code == "1730")
						errorHandler("1730_cancelTransaction", "",
						"cancelTransaction");
					else
						errorHandler(code, "", "cancelTransaction");
				}
			});
		}
	} catch (err) {
		showSysException(err);
	}
}
dfs.crd.p2p.goBackToTransHistory = function() {
	try {
		dfs.crd.p2p.cancelledTransaction = true;
		navigation("../p2p/transactionHistory", false);
	} catch (err) {
		showSysException(err);
	}
}
dfs.crd.p2p.getRefreshStatus = function(obj) {
	try {
		var transactionId = "";
		var aId = obj.id;
		var length = aId.length;
		var offset = aId.lastIndexOf("arefresh");
		var current_iteration = +aId.substring(8, length);
		dfs.crd.p2p.showLoader(current_iteration);
		var transactionId = (selectedDataInJson[current_iteration]);
		if (!isEmpty(transactionId)) {
			var newDate = new Date();
			var TRANSACTIONHISTORYURL = RESTURL
					+ "p2p/v1/refreshtransaction?transactionId="
					+ transactionId + "&requestType=refresh&" + newDate + "";
			$.ajax({
				type : "GET",
				url : TRANSACTIONHISTORYURL,
				dataType : "json",
				headers : prepareGetHeader(),
				success : function(responseData, status, jqXHR) {
					dfs.crd.p2p.hideLoader(current_iteration);
					if (jqXHR.status != 200 & jqXHR.status != 204) {
						var code = getResponseStatusCode(jqXHR);
						errorHandler(code, "", "transactionHistory");
					} else {
						var statusValue = responseData.status;
						dfs.crd.p2p.updateDataRefresh(statusValue,
								current_iteration, transactionId);
					}
				},
				error : function(jqXHR, textStatus, errorThrown) {
					dfs.crd.p2p.hideLoader(current_iteration);
					cpEvent.preventDefault();
					var code = getResponseStatusCode(jqXHR);
					errorHandler(code, "", "transactionHistory");
				}
			});
		}
	} catch (err) {
	}
}
dfs.crd.p2p.updateDataRefresh = function(statusValue, curr_iteration,
		transactionId) {
	try {
		if (!jQuery.isEmptyObject(statusValue)) {
			var idToreplace = "divStatus" + curr_iteration;
			if (statusValue == "Pending")
				$("#" + idToreplace).html(statusValue + " |");
			else
				$("#" + idToreplace).html(statusValue);

			var idToHide = "divRefreshCancel" + curr_iteration;
			if (statusValue.toUpperCase() != "PENDING")
				$("#" + idToHide).css("display", "none");
		}
	} catch (err) {
		showSysException(err);
	}
}
dfs.crd.p2p.clearIndexJSONArray = function() {
	try {
		selectedDataInJson = new Array();
	} catch (err) {
		showSysException(err);
	}
}
dfs.crd.p2p.getFaqHowItWorksData = function(pageName) {
	try {
		var eligibilitChkData = "";
		var newDate = new Date();
		var SENDMONEYURL = RESTURL + "p2p/v1/eligibilitylimits?" + newDate + "";
		var code = "";
		showSpinner();
		$.ajax({
			type : "GET",
			url : SENDMONEYURL,
			async : false,
			dataType : "json",
			headers : prepareGetHeader(),
			success : function(responseData, status, jqXHR) {
				
				eligibilitChkData = responseData;
				console.log("WE ar ein succes of FAQ ajax call");
				hideSpinner();
			},
			error : function(jqXHR, textStatus, errorThrown) {
				hideSpinner();
				code = getResponseStatusCode(jqXHR);
				console.log("WE ar ein FAIL of FAQ ajax call");
				cpEvent.preventDefault();
				errorHandler(code, "", pageName);
			}
		});
		return eligibilitChkData;
	} catch (err) {
		showSysException(err);
	}
}
function howItWorksLoad() {
	try {
		console.log("We are in howItWorksLoad ");
		dfs.crd.p2p.validPriorPagesOfHowIt = new Array(
				"strongAuthFirstQues", "updateEmail", "sendMoney1",
				"pageError", "sendMoney2", "confirmCancelTransaction");
		if (acLiteModeFlag) {
			cpEvent.preventDefault();
			errorHandler("acLiteOutageMode_ACL", "", "howItWorks");
		} else {	
			if (!(jQuery.inArray(fromPageName,dfs.crd.p2p.validPriorPagesOfHowIt) > -1) && !isLhnNavigation) {	
					console.log("WE are RETURNING FROM HOE IT WORKS");				
				return;
			}
			console.log("SEnd money - populate HowItWorks");
			dfs.crd.p2p.hIWScreenFlag = true;
			dfs.crd.p2p.populateHowItWorksPage();		
			console.log("SEnd - populateHowItWorksPage CALL:-" );	
		}
	} catch (err) {
		showSysException(err);
	}
}
dfs.crd.p2p.populateHowItWorksPage = function() {
	try {
		console.log("SEnd money - inside populate HowItWorks");
		var pageName = "howItWorks";
		var eligibilitChkData = dfs.crd.p2p.getFaqHowItWorksData(pageName);
		console.log("SEnd - eligibilitChkData:-" + eligibilitChkData);
		if (!jQuery.isEmptyObject(eligibilitChkData)) {
			var dLimit = eligibilitChkData.dailyLimit;
			var mLimit = eligibilitChkData.monthlyLimit;
			var dailyLimit = dLimit.split(".");
			var monthlyLimit = mLimit.split(".");
			var bonustypes = eligibilitChkData.bonusType;
			$("#daylimitHiw").html("$" + dailyLimit[0]);
			$("#monthLimitHiw").html("$" + monthlyLimit[0]);
			$("#bonusTypesHiw").html(bonustypes);
			console.log("InsideHOwItWorks:-");
		}
		var deviceVer = parseInt(deviceVersion);
		if (deviceType=="Android" && deviceVer>=4)
			{
			$("#sendMoneyFrame").css("display", "none");
			$("#sendMoneyVideo").css("display", "block");
			}
		else
			{$("#sendMoneyFrame").css("display", "block");
			 $("#sendMoneyVideo").css("display", "none");
			}
			
		console.log("InsideHOwItWorks - End & LHN FLAG IS :- "+isLhnNavigation);	
			
	} catch (err) {
		showSysException(err);
	}
}
function sendMoneyFAQLoad() {
	try {
		if (acLiteModeFlag) {
			cpEvent.preventDefault();
			errorHandler("acLiteOutageMode_ACL", "", "sendMoneyFAQ");
		} else {
			var pageName = "sendMoneyFAQ";
			var eligibilitChkData = dfs.crd.p2p.getFaqHowItWorksData(pageName);
			if (!jQuery.isEmptyObject(eligibilitChkData)) {
				var dLimit = eligibilitChkData.dailyLimit;
				var mLimit = eligibilitChkData.monthlyLimit;
				var dailyLimit = dLimit.split(".");
				var monthlyLimit = mLimit.split(".");
				$("#daylimitFaq").html("$" + dailyLimit[0]);
				$("#monthLimitFaq").html("$" + monthlyLimit[0]);
			}
		}
	} catch (err) {
		showSysException(err);
	}
}
function howtoWorkUpdateEmailLoad() {
	try {
		var pageName = "howtoWorkUpdateEmail";
		var eligibilitChkData = dfs.crd.p2p.getFaqHowItWorksData(pageName);
		if (!jQuery.isEmptyObject(eligibilitChkData)) {
			var dLimit = eligibilitChkData.dailyLimit;
			var mLimit = eligibilitChkData.monthlyLimit;
			var bonustypes = eligibilitChkData.bonusType;
			var dailyLimit = dLimit.split(".");
			var monthlyLimit = mLimit.split(".");
			$("#daylimitHiwEmail").html("$" + dailyLimit[0]);
			$("#monthLimitHiwEmail").html("$" + monthlyLimit[0]);
			$("#bonusTypesHiwEmail").html(bonustypes);
		}
	} catch (err) {
		showSysException(err);
	}
}

dfs.crd.p2p.autoFetchNames = function() {
	try {
		var autoName = "";
		var textInput = "";
		var setVal = "";
		dfs.crd.p2p.count = 0;
		var nameList = "";
		var list1DOM = $("#list1");
		var popupDOM = $("#internalPopup");
		textInput = $("#fullname").val();
		dfs.crd.p2p.hideNameEmailList();
		for (autoName in dfs.crd.p2p.autoResponseData["recipientContactList"]) {
			dfs.crd.p2p.objArray[autoName] = dfs.crd.p2p.autoResponseData["recipientContactList"][autoName].name;
			dfs.crd.p2p.objEmailArray[autoName] = dfs.crd.p2p.autoResponseData["recipientContactList"][autoName].emailOrphone;
			if (textInput != ""
					&& (dfs.crd.p2p.objArray[autoName] != null || dfs.crd.p2p.objArray[autoName] != "null")) {
				if (!isEmpty(dfs.crd.p2p.objArray[autoName])) {
					if (dfs.crd.p2p.objArray[autoName].indexOf(textInput) >= 0) {
						dfs.crd.p2p.count++;
						list1DOM.css("display", "block");
						popupDOM.css("display", "block");
						dfs.crd.p2p.heightSet = (dfs.crd.p2p.count * 30);
						setVal = dfs.crd.p2p.heightSet + "px";
						$(".internalPopup").css("height", setVal);
						nameList += "<li class='autoLI' onclick='dfs.crd.p2p.updateFullNameOrEmail(this);'><span id='autoname'>"
								+ dfs.crd.p2p.objArray[autoName]
								+ "</span><span id='autoemail'>"
								+ dfs.crd.p2p.objEmailArray[autoName]
								+ "</span></li>";
					}
					$("#list1").html(nameList);
				}
			} else {
				dfs.crd.p2p.hideNameList();
			}
		}
	} catch (err) {
		showSysException(err);
	}
}
dfs.crd.p2p.autoFetchEmails = function() {
	try {
		var autoEmail = "";
		var textInputEmail = "";
		var setVal = "";
		var emailList = "";
		dfs.crd.p2p.count = 0;
		var list2DOM = $("#list2");
		var popup1DOM = $("#internalPopup1");
		textInputEmail = $("#emailOrphone").val();
		dfs.crd.p2p.hideNameEmailList();
		for (autoEmail in dfs.crd.p2p.autoResponseData["recipientContactList"]) {
			dfs.crd.p2p.objEmailArray[autoEmail] = dfs.crd.p2p.autoResponseData["recipientContactList"][autoEmail].emailOrphone;
			dfs.crd.p2p.objArray[autoEmail] = dfs.crd.p2p.autoResponseData["recipientContactList"][autoEmail].name;
			if (dfs.crd.p2p.objArray[autoEmail] == ""
					|| dfs.crd.p2p.objArray[autoEmail] == "null"
					|| dfs.crd.p2p.objArray[autoEmail] == null)
				dfs.crd.p2p.objArray[autoEmail] = " ";
			if (textInputEmail != "") {
				if (!isEmpty(dfs.crd.p2p.objEmailArray[autoEmail])) {
					if (dfs.crd.p2p.objEmailArray[autoEmail]
							.indexOf(textInputEmail) >= 0) {
						dfs.crd.p2p.count++;
						list2DOM.css("display", "block");
						popup1DOM.css("display", "block");
						dfs.crd.p2p.heightSet = (dfs.crd.p2p.count * 30);
						setVal = (dfs.crd.p2p.heightSet + "px");
						$(".internalPopup1").css("height", setVal);
						emailList += "<li class='autoLI' onclick='dfs.crd.p2p.updateFullNameOrEmail(this);'><span id='autoname'>"
								+ dfs.crd.p2p.objArray[autoEmail]
								+ "</span><span id='autoemail'>"
								+ dfs.crd.p2p.objEmailArray[autoEmail]
								+ "</span></li>";
					}
					$("#list2").html(emailList);
				}
			} else {
				dfs.crd.p2p.hideEmailList();
			}
		}
	} catch (err) {
		showSysException(err);
	}
}
dfs.crd.p2p.updateFullNameOrEmail = function(obj) {
	try {
		var splitNameEmail = obj.innerHTML.split("><");
		var autoname = splitNameEmail[0];
		var autoemail = splitNameEmail[1];
		autoname = autoname.split(">");
		autoname[0] = autoname[1].split("<");
		autoemail = autoemail.split(">");
		autoemail[0] = autoemail[1].split("<");
		if (autoname[0][0] == null || autoname[0][0] == "null"
				|| autoname[0][0] == " ") {
			autoname[0][0] = "";
			$("#fullname").val(autoname[0][0]);
		} else {
			$("#fullname").val(autoname[0][0]);
		}
		$("#emailOrphone").val(autoemail[0][0]);
		dfs.crd.p2p.hideNameEmailList();
	} catch (err) {
		showSysException(err);
	}
}
dfs.crd.p2p.displayNone = function() {
	try {
		$("#internalPopup").css("display", "none");
		$("#internalPopup1").css("display", "none");
	} catch (err) {
		showSysException(err);
	}
}
dfs.crd.p2p.hideNameList = function() {
	try {
		$("#list1").css("display", "none");
		$("#list1").html("");
		$("#internalPopup").css("display", "none");
	} catch (err) {
		showSysException(err);
	}
}
dfs.crd.p2p.hideEmailList = function() {
	try {
		$("#list2").css("display", "none");
		$("#list2").html("");
		$("#internalPopup1").css("display", "none");
	} catch (err) {
		showSysException(err);
	}
}
dfs.crd.p2p.hideNameEmailList = function() {
	try {
		$("#list1").html("");
		$("#list2").html("");
		$("#internalPopup").css("display", "none");
		$("#internalPopup1").css("display", "none");
	} catch (err) {
		showSysException(err);
	}
}
dfs.crd.p2p.focusEmailOrPhoneBlock = function() {
	try {
		$("#emailOrphone").focus();
		//$("#emailOrphone").css("border", "2px solid #ff0000");
		$("#emailOrphone").addClass("errormsg");
	} catch (err) {
		showSysException(err);
	}
}
dfs.crd.p2p.showPageTitleError = function() {
	try {
		$("#errorTitle").css("display", "block");
	} catch (err) {
		showSysException(err);
	}
}
dfs.crd.p2p.showLoader = function(index) {
	try {
		$("#loaderIcon" + index).css("display", "block");
	} catch (err) {
		showSysException(err);
	}
}

dfs.crd.p2p.hideLoader = function(index) {
	try {
		$("#loaderIcon" + index).css("display", "none");
	} catch (err) {
		showSysException(err);
	}
}

dfs.crd.p2p.toggleLoadMoreButton = function(maxTransactionCount1, counter1) {
	try {
		if (maxTransactionCount1 > counter1)
			$("#loadmore").css("display", "block");
		else
			$("#loadmore").css("display", "none");
	} catch (err) {
		showSysException(err);
	}

}

dfs.crd.p2p.canSendMoneyButton = function(canSendMoney) {
	try {
		if (canSendMoney)
			$("#moremoney").css("display", "block");
	} catch (err) {
		showSysException(err);
	}
}
function sendMoneyLandingLoad() 
{
	try {
		killDataFromCache("SM1_DATA");
		dfs.crd.p2p.hIWScreenFlag = false;
	} catch (err) {
		showSysException(err);
	}
}
