/** Name Space* */
dfs.crd.pymt = dfs.crd.pymt || {};
/** * */

dfs.crd.pymt.validDays = new Array();
dfs.crd.pymt.afterConfirmFlag = false;
dfs.crd.pymt.messageAftrConfrm;
dfs.crd.pymt.afterMakePayFlag = false;
dfs.crd.pymt.messageAftrmakePay;
dfs.crd.pymt.outstandingbalance = false;
dfs.crd.pymt.selectvar;
dfs.crd.pymt.globalOpenAmount 		= "" ;
dfs.crd.pymt.isLinkClicked = false;
/** *******************Payments Summary******************* */
function paymentsLandingLoad(){
	try {
		killDataFromCache("OPTION_SELECTED_ON_MAP1");
	}catch(err){
		showSysException(err);
	}
}

function paymentsSummaryLoad()
{
	try {
		dfs.crd.pymt.populatePaymentSummary("PAYMENTSUMMARY");
	} catch (err) {
		showSysException(err);
	}
}

dfs.crd.pymt.populatePaymentSummary = function(pageName)
{
	try {
		var pmtSummary = dfs.crd.pymt.getPaymentSummaryData(pageName);

		if (!jQuery.isEmptyObject(pmtSummary))
			dfs.crd.pymt.populatePaymentSummaryPageDivs(pmtSummary, pageName);

	} catch (err) {
		showSysException(err);
	}
}

dfs.crd.pymt.getPaymentSummaryData = function(pageId)
{
	try {
		var newDate = new Date();
		var PAYMENTSSUMMARYURL = RESTURL + "pymt/v1/paymentsummary?" + newDate
		+ "";
		var pmtSummary = getDataFromCache(pageId);

		showSpinner();
		$.ajax(
				{
					type : "GET",
					url : PAYMENTSSUMMARYURL,
					async : false,
					dataType : "json",
					headers : prepareGetHeader(),
					success : function(responseData, status, jqXHR)
					{
						hideSpinner();
               if (!validateResponse(responseData,"paymentSummaryValidation")) // Pen Test Validation
               {
                errorHandler("SecurityTestFail","","");
               return;
               }
						pmtSummary = responseData;
						putDataToCache(pageId, pmtSummary);
					},
					error : function(jqXHR, textStatus, errorThrown)
					{
						hideSpinner();
						cpEvent.preventDefault();
						var code = getResponseStatusCode(jqXHR);
						errorHandler(code, "", "paymentsHistory");
					}
				});
		return pmtSummary;
	} catch (err) {
		showSysException(err);
	}
}

dfs.crd.pymt.populatePaymentSummaryPageDivs = function(payDataObj, pageId){


	try{
		if(!isEmpty(payDataObj)){

			var inLineError = $("#paymentsSummary_Inline_Error");
			var minPaymentDue = payDataObj.minPaymentDue;
			var paymentDueDate = payDataObj.paymentDueDate;
			var overLimitAmount = payDataObj.overLimitAmount;
			var currentBalance = payDataObj.currentBalance;
			var statementBalance = payDataObj.lastStatementBalance;
			var scheduledPayments = payDataObj.scheduledPayments;
			dfs.crd.pymt.globalOpenAmount = payDataObj.openAmount;
			var defaultValue = "0.00";
			var daysDelinquentVal=payDataObj.daysDelinquent;

			var currentBalanceVal= !isEmpty(currentBalance) ? numberWithCommas(currentBalance) : defaultValue;
			$("#paymentsSummary_currentBalance").text("$" + currentBalanceVal);		

			var statementBalanceVal= !isEmpty(statementBalance) ? numberWithCommas(statementBalance) : defaultValue;
			$("#paymentsSummary_statementBalance").text("$" + statementBalanceVal);

			if((payDataObj.isHaMode)){
				$("#paymentsSummaryScheduledPayments_Li").remove();
			}else{
				var scheduledPaymentsVal= !isEmpty(scheduledPayments) ? numberWithCommas(scheduledPayments) : defaultValue;
				$("#paymentsSummary_scheduledPayments").text("$" + scheduledPaymentsVal);
			}

			if(daysDelinquentVal > 0){
				inLineError.css("display", "block");
				inLineError.append(errorCodeMap["Days_Delinquent"]);
			}

			if(!isEmpty(overLimitAmount)){
				if (parseFloat(overLimitAmount) > 0) {
					var overLimitAmt = [];
					var errMsg = errorCodeMap["Over_Limit"];
					overLimitAmt["overLimitAmount"] = overLimitAmount;
					inLineError.css("display", "block");
					inLineError.append(parseContent(errMsg, overLimitAmt));
				}
			}	

			if((parseFloat(minPaymentDue) > 0) && (!isEmpty(paymentDueDate))){
				$("#paymentsSummary_paymentDueDate").text(formatPaymentDueDate(paymentDueDate));
				$("#paymentsSummary_minimumPaymentDue").text("$" + numberWithCommas(minPaymentDue));
				if(payDataObj.isAccountOverDue){				
					$("#paymentDueDate_Li").addClass("redtext boldtext");
					$("#paymentsSummary_paymentDueDate").removeClass('amt_bold');
     				$("#paymentsSummary_paymentDueDate").addClass('payment_summary_due_date_red');
		
				}
			}else{
				$("#minPaymentDue_Li").remove();
				$("#paymentDueDate_Li").remove();
				inLineError.css("display", "block");
				inLineError.html(errorCodeMap["NO_MIN_PAY"]);
			}

			if (parseFloat(payDataObj.currentBalance) < 0.01) {
				inLineError.css("display", "block");
				inLineError.html("\n" + errorCodeMap["PARM_PAY_ZERO"]);
				$("#paymentsummaryval_btn").remove();
			}else if (parseFloat(payDataObj.openAmount) <= 0.00) {
				var details = [];
				var errorMessage = errorCodeMap["1217"];
				details["pendingPayments"] = payDataObj.scheduledPayments;
				details["currentBalance"] = payDataObj.currentBalance
				errorMessage = parseContent(errorMessage, details);
				inLineError.css("display", "block");
				inLineError.html(errorMessage);
				$("#paymentsummaryval_btn").remove();
			}

			if(dfs.crd.pymt.afterMakePayFlag){
				inLineError.css("display", "block");
				inLineError.html(dfs.crd.pymt.messageAftrmakePay);
			}
		}

	}catch(err){
		showSysException(err);
	}
}

/** *******************Pending Payments ******************* */
function pendingPaymentsLoad()
{
	try {
		dfs.crd.pymt.populatePendingPayment("PENDINGPAYMENTS");
	} catch (err) {
		showSysException(err);
	}
}

dfs.crd.pymt.populatePendingPayment = function(pageName)
{
	try {
		var pendingPymt = dfs.crd.pymt.getPendingPaymentData(pageName);

		if (!jQuery.isEmptyObject(pendingPymt))
			dfs.crd.pymt.populatePendingPaymentPageDivs(pendingPymt, pageName);

	} catch (err) {
		showSysException(err);
	}
}

dfs.crd.pymt.getPendingPaymentData = function(pageId)
{
	try {

		var newDate = new Date();
		var PENDINGPAYMENTSURL = RESTURL + "pymt/v1/pendingpayments?" + newDate
		+ "";
		var pendingPymt = getDataFromCache(pageId);
		showSpinner();
		$.ajax(
				{
					type : "GET",
					url : PENDINGPAYMENTSURL,
					async : false,
					dataType : "json",
					headers : prepareGetHeader(),
					success : function(responseData, status, jqXHR)
					{
						hideSpinner();
               if (!validateResponse(responseData,"pendingPaymentsValidation"))      // Pen Test Validation
               {
               errorHandler("SecurityTestFail","","");
               return;
               }
						pendingPymt = responseData;
						putDataToCache(pageId, pendingPymt);
					},
					error : function(jqXHR, textStatus, errorThrown)
					{
						hideSpinner();
						cpEvent.preventDefault();
						var code = getResponseStatusCode(jqXHR);
						switch (code)
						{
						case "1233":
							var errorText  = errorCodeMap["1233"];
							errorHandler(code, errorText, "pendingPayments");
							break;
						default:
							errorHandler(code, "", "pendingPayments");
						}
					}
				});
		return pendingPymt;
	} catch (err) {
		showSysException(err);
	}
}

dfs.crd.pymt.populatePendingPaymentPageDivs = function(pendingTrans, pageId)
{
	try {
			if(!isEmpty(pendingTrans)){
				var innerPenTransVal = pendingTrans["pendingPayments"];
				var pendingPaymentDataActivityText = "<ul class='payment_history ui-listview ui-listview-inset ui-corner-all ui-shadow' data-inset='true' data-role='listview'><li class='rh_header ui-li ui-li-static ui-body-c ui-corner-top'><div class='col1'>Posting Date</div><div class='col2'>Method</div><div style='text-align: center;' class='col3'>Amount</div></li>";
				var pendingPaymentDataActivityMain = "";
		
				var HAMode 			 			   = pendingTrans["isHaMode"];
		
				var htmlText                           = "";
				if(HAMode){
					if (innerPenTransVal.length != 0) {
						var bank = dfs.crd.pymt
						.truncateBankName(innerPenTransVal[0].bankName);
						var maskedAcctNumber = dfs.crd.pymt
						.truncateAccountNumber(innerPenTransVal[0].maskedBankAccountNumber);
		
						if (innerPenTransVal[0].bankName.length > 20) {
							var truncatedBankName = bank + "****" + maskedAcctNumber;
						}
						else {
							var truncatedBankName = innerPenTransVal[0].bankName
							+ "****" + maskedAcctNumber;
						}
		
						if (innerPenTransVal[0].maskedBankAccountNumber == "") {
							var truncatedBankName = bank;
						}
		
						var pendingBankName        = truncatedBankName;
						var paymentAmount          = innerPenTransVal[0].paymentAmount;
						if (!isEmpty(paymentAmount)){
							var result=parseFloat(paymentAmount);
							if(!isNaN(result)){
								paymentAmount="$"+numberWithCommas(paymentAmount);
							}
							paymentAmount            = paymentAmount;
						}else{
							paymentAmount            = "0.00";
						}
						var pendingPaymentDate    = innerPenTransVal[0].paymentDate;
						var pendingPaymentMethod  = innerPenTransVal[0].paymentMethod;
		
						var payPendingDetails = [];
		
						var errorMessage                                         = errorCodeMap["pendingPayHA"];
						payPendingDetails["errorMessage"]                  = errorCodeMap["pendingPayErrMsg"];
						payPendingDetails["payAmount"]                     = paymentAmount;
						payPendingDetails["pendingPayDate"]                = formatPostingDueDate_MakePaymentStep2(pendingPaymentDate);
						payPendingDetails["pendingPayBank"]            = pendingBankName;
						payPendingDetails["pendingAccountNumber"]        = maskedAcctNumber;
						payPendingDetails["pendingConfmNumber"]    = innerPenTransVal[0].confirmationNumber;
						var parseContentText = parseContent(errorMessage, payPendingDetails);
		
						$("#pendingPayments-pg").find("[data-role='content']").html(parseContentText);
						$("#pendingPayments_makeapaymentbutton").remove();
					}
					else {
						htmlText = "<span class='errormsg2'>There are no pending payments to view.</span><br/>";
						$("#errorPendingPaymentDiv").html(htmlText);
					}
				}
				else{
					if (innerPenTransVal.length != 0) {
						var pendingPaymentDataActivityJSON = getPageContentMin("payments",
								"pendingPayments", "", "");
		
						for (i = 0; i < innerPenTransVal.length; i++) {
							var paymentPendingVal = [];
							var bank = dfs.crd.pymt
							.truncateBankName(innerPenTransVal[i].bankName);
							var maskedAcctNumber = dfs.crd.pymt
							.truncateAccountNumber(innerPenTransVal[i].maskedBankAccountNumber);
		
							if (innerPenTransVal[i].bankName.length > 20) {
								var truncatedBankName = bank + "****" + maskedAcctNumber;
							}
							else {
								var truncatedBankName = innerPenTransVal[i].bankName
								+ "****" + maskedAcctNumber;
							}
		
							if (innerPenTransVal[i].maskedBankAccountNumber == "") {
								var truncatedBankName = bank;
							}
		
							paymentPendingVal["bank"] = truncatedBankName;
							var paymentAmount = innerPenTransVal[i].paymentAmount;
							if (!isEmpty(paymentAmount)){
								//paymentPendingVal["paymentAmount"] = numberWithCommas(paymentAmount);
								var result=parseFloat(paymentAmount);
								if(!isNaN(result)){
									paymentAmount="$"+numberWithCommas(paymentAmount);
								}
								paymentPendingVal["paymentAmount"]=paymentAmount;
							}else{
								paymentPendingVal["paymentAmount"] = "0.00";
							}
							paymentPendingVal["paymentDate"] = innerPenTransVal[i].paymentDate;
							paymentPendingVal["paymentMethod"] = innerPenTransVal[i].paymentMethod;
		
							var pendingPaymentDataActivity = parseContent(
									pendingPaymentDataActivityJSON, paymentPendingVal);
							pendingPaymentDataActivityMain += pendingPaymentDataActivity;
						}
		
						pendingPaymentDataActivityText += pendingPaymentDataActivityMain;
		
						pendingPaymentDataActivityText += "</ul>";
		
						$("#pendingPayments_pendingList").html(
								pendingPaymentDataActivityText);
						$("#pendingPayments_makeapaymentbutton").remove();
		
					}
					else {
						htmlText = "<span class='errormsg2'>There are no pending payments to view.</span><br/>";
						$("#errorPendingPaymentDiv").html(htmlText);
					}
				}
				if (pendingTrans.pendingPayments.length > 0) {
		
					htmlText = "\"Online\" payment method includes mobile payments.";
					$("#pendingPayments_onlinepaymentmethodmsg").html(htmlText);
				}
		}	
	} catch (err) {
		showSysException(err);
	}
}

/** ******************* Payment History ******************* */

function paymentsHistoryLoad()
{
	try {
		dfs.crd.pymt.populatePaymentHistory("PAYMENTHISTORY");
	} catch (err) {
		showSysException(err);
	}
}

dfs.crd.pymt.populatePaymentHistory = function(pageName)
{
	try {
		var paymentHistory = dfs.crd.pymt.getPaymentHistoryData(pageName);

		if (!jQuery.isEmptyObject(paymentHistory))
			dfs.crd.pymt.populatePaymentHistoryPageDivs(paymentHistory, pageName);

	} catch (err) {
		showSysException(err);
	}
}

dfs.crd.pymt.getPaymentHistoryData = function(pageId)
{
	try {

		var newDate = new Date();
		var PAYMENTSHISTORYURL = RESTURL + "pymt/v1/paymenthistory?" + newDate
		+ "";
		var paymentHistory = "";

		showSpinner();
		$.ajax(
				{
					type : "GET",
					url : PAYMENTSHISTORYURL,
					async : false,
					dataType : "json",
					headers : prepareGetHeader(),
					success : function(responseData, status, jqXHR)
					{
						hideSpinner();
               if (!validateResponse(responseData,"paymentHistoryValidation"))      // Pen Test Validation
               {
               errorHandler("SecurityTestFail","","");
               return;
               }
						paymentHistory = responseData;
						putDataToCache(pageId, paymentHistory);
					},
					error : function(jqXHR, textStatus, errorThrown)
					{
						hideSpinner();
						var code = getResponseStatusCode(jqXHR);
						switch (code)
						{
						case "1219":
							var errorText  = errorCodeMap["1233"];
							errorHandler(code, errorText, "paymentsHistory");
							break;
						default:
							errorHandler(code, "", "paymentsHistory");
						}

					}
				});
		return paymentHistory;
	} catch (err) {
		showSysException(err);
	}
}

dfs.crd.pymt.populatePaymentHistoryPageDivs = function(paymentHistory, pageId)
{
	try {
		if(!isEmpty(paymentHistory)){
			
			var innerHisTransVal = paymentHistory["paymentHistory"];
			var paymentHistoryDataActivityText = "<ul class='payment_history ui-listview ui-listview-inset ui-corner-all ui-shadow' data-inset='true' data-role='listview'><li class='rh_header ui-li ui-li-static ui-body-c ui-corner-top'><div class='col1'>Posting Date</div><div class='col2'>Method</div><div style='text-align: center;' class='col3'>Amount</div></li>";
			var paymentHistoryDataActivityMain = "";
			var counter = 5;
			var len = innerHisTransVal.length;
			var htmlText                           = "";
	
			if (len <= 5)
				counter = len;
	
			if (innerHisTransVal.length != 0) {
				
				for (i = 0; i < counter; i++) {
					var paymentHistoryVal = [];
					var bank = dfs.crd.pymt
					.truncateBankName(innerHisTransVal[i].bankName);
					var maskedAcctNumber = dfs.crd.pymt
					.truncateAccountNumber(innerHisTransVal[i].maskedBankAccountNumber);
	
					if (innerHisTransVal[i].bankName.length > 20) {
						var truncatedBankName = bank + "****" + maskedAcctNumber;
					}
					else {
						var truncatedBankName = innerHisTransVal[i].bankName
						+ "****" + maskedAcctNumber;
					}
	
					if (innerHisTransVal[i].maskedBankAccountNumber == "") {
						var truncatedBankName = bank;
					}
	
					paymentHistoryVal["bank"] = truncatedBankName;
					var paymentAmount = innerHisTransVal[i].paymentAmount;
					if (!isEmpty(paymentAmount))
						paymentHistoryVal["paymentAmount"] = numberWithCommas(paymentAmount);
					else
						paymentHistoryVal["paymentAmount"] = "0.00";
					paymentHistoryVal["paymentDate"] = innerHisTransVal[i].paymentDate;
					paymentHistoryVal["paymentMethod"] = innerHisTransVal[i].paymentMethod;
					var paymentHistoryDataActivityJSON = "<li class ='borderbottom'><div class='accountsearchresult'><div class='toprow1'>!~bank~!</div><div><div class='col1'>!~paymentDate~!</div><div class='col2'>!~paymentMethod~!</div><div class='col3'>$!~paymentAmount~!</div></div></div></li>";

					var paymentHistoryDataActivity = parseContent(
							paymentHistoryDataActivityJSON, paymentHistoryVal);
					paymentHistoryDataActivityMain += paymentHistoryDataActivity;
				}
	
				paymentHistoryDataActivityText += paymentHistoryDataActivityMain;
	
				paymentHistoryDataActivityText += "</ul>";
	
				$("#paymentsHistory_paymentHistory").html(
						paymentHistoryDataActivityText);
	
			}
			else {
				$("#errorPaymentsHistoryDiv")
				.html(
				"<span class='errormsg2'>There are no recent payments to view.</span><br/>");
			}
	
			if (paymentHistory.paymentHistory.length > 0) {
				htmlText = "\"Online\" payment method includes mobile payments.";
				$("#paymentsHistory_onlinepaymentmethodmsg").html(htmlText);
			}
		}
	} catch (err) {
		showSysException(err);
	}
}


/** ******************* Make Payment-1 ******************* */
function paymentStep1Load()
{
	try {
		
		var validPriorPagesOfpayStep1 = new Array("paymentStep2",
				"paymentsSummary", "paymentsLanding", "pendingPayments",
				"paymentsHistory", "accountSummary", "cardHome","pageError","paymentStep3","confirmCancelPayment","paymentInformation","lateMinPayWarn1","lateMinPayWarnNoMinPay");
		if (jQuery.inArray(fromPageName, validPriorPagesOfpayStep1) > -1 || isLhnNavigation) {
		
			
			var minPayStepOne = $("#minpaystepone_other");
			var radioChoice3 = $("#radio-choice-3");
			var errorPaymentAmountExceed = $("#errorPaymentAmountExceed");
			activebtn("bank_info");
			deactiveBtn("postingDateStepOne");
			deactiveBtn("makePaymentOneContinue");
			if(fromPageName != "paymentInformation" && fromPageName != "lateMinPayWarn1" && fromPageName != "lateMinPayWarnNoMinPay"){
				killDataFromCache("OPTION_SELECTED_LINK_CLICK");
			}
			
			if(fromPageName != "paymentStep2" ){
				killDataFromCache("OPTION_SELECTED_ON_MAP1");
			}
			
			$(".date-picker").live("click", function()
					{
				$(".wraper1").hide();
				$(".footnotes").hide(function(){
				  $(this).trigger('updatelayout');
				});
				
				$(".wraper2").show(); 
				var currentpageback=$.mobile.activePage.find("#back-btn a");
				currentpageback.removeAttr("data-rel");

				currentpageback.bind("click", function()
						{
					$("a#calendar-cancel").click();
						});
					});

			$("a#calendar-cancel").live("click", function()
					{
				$(".wraper2").hide();
				$(".footnotes").show();
				$(".wraper1").show();
				 
				 
				var currentpage=$.mobile.activePage;
				setTimeout(function(){currentpage.find("#back-btn a").attr("data-rel","back");
				$(".back-button div.back_btn_normal").css("visibility","visible");},200);
					});
			$("#button-selectdate").click(function()
					{
				if ($(this).hasClass("disabled")) {
					return false;
				}
					});
			$("#calendar").datepicker(
					{
						dayNamesMin:["Sun","Mon","Tue","Wed","Thu","Fri","Sat"],
						hideIfNoPrevNext : true,
						showOtherMonths : true,
						selectOtherMonths : false,
						constrainInput : true,
						onSelect : function(dateText, inst)
						{
							dfs.crd.pymt.paymentDateSet(dateText);
						},
						beforeShowDay : dfs.crd.pymt.enableSpecificDates
					});

			/*minPayStepOne.focus(function()
					{
				errorPaymentAmountExceed.html("");
				radioChoice3.attr("checked", "checked");
				radioChoice3.change();
				$("input[type='radio']").checkboxradio("refresh");
				minPayStepOne.removeClass("errormsg");
				dfs.crd.pymt.changeDropDownLabel()
					});                   

			$("input:radio").change(function()
					{
				if ($(this).attr("id") == "radio-choice-3") {           	
					minPayStepOne.select();                    
					deactiveBtn("makePaymentOneContinue");                  
				}
				else {
					if (minPayStepOne.not(".optional")) {
						minPayStepOne.addClass("optional");
					}

					minPayStepOne.val('');
					minPayStepOne.attr("placeholder", "0.00");                   
				}       			
				dfs.crd.pymt.changeDropDownLabel();
					});			
			 */          
			$("#minpaystepone_other").click(function() {
				minPayStepOne.val("");
				errorPaymentAmountExceed.html("");
				$('#minpaystepone_other').attr("placeholder", '');
				$("#radio-choice-3").attr("checked","checked");
				$("#radio-choice-3").change();
				$("input[type='radio']").checkboxradio("refresh"); 
				minPayStepOne.removeClass("errormsg");
				dfs.crd.pymt.changeDropDownLabel();									
			});
			
			$("#radio-choice-3").checkboxradio('disable');			
				
			$("input:radio").bind("click", function (event)
					{
				if($(this).attr("id")=="radio-choice-3")
				{
					/* $("#minpaystepone_other").removeClass("optional");
					$("#minpaystepone_other").focus();
					$('#minpaystepone_other').attr("placeholder", '');
					if(device.version < "5.0") {e.stopPropagation();}*/

				}
				else
				{ 
					if($("#minpaystepone_other").not(".optional"))
						$("#minpaystepone_other").addClass("optional");
						minPayStepOne.blur();  
						minPayStepOne.val('');
						minPayStepOne.attr("placeholder", "0.00");     
						//if(device.version < "5.0") {e.stopPropagation();} 

				}
				dfs.crd.pymt.changeDropDownLabel();								  
					});

			minPayStepOne.blur(function() {

				if (radioChoice3.attr('checked')) 
				{     

					var payAmount           = minPayStepOne.val();
					var valueOfText  = minPayStepOne.val();
					if(valueOfText.indexOf('.') != -1){
						valueOfText = valueOfText.substring(0,(valueOfText.indexOf('.')+3));
					}
					payAmount = valueOfText;
					if (isEmpty(payAmount)) 
					{
						minPayStepOne.attr("placeholder","0.00");
						return;
					} else if (parseFloat(payAmount) < 0.01 || parseFloat(payAmount) > 99999.99) 
					{
						$("#commonErrorInMakePaymentStepOneDiv").html(errorCodeMap["Update_HighLighted"]);
						errorPaymentAmountExceed.text(errorCodeMap["1207"]);
						errorPaymentAmountExceed.css("display","block");
						minPayStepOne.addClass('errormsg');
						$("#minpaystepone_other").val('');
						$('#minpaystepone_other').attr("placeholder", '0.00');
						return;
					}
					else if (!isEmpty(payAmount) && !dfs.crd.pymt.checkCurrency(payAmount)) 
					{
						$("#commonErrorInMakePaymentStepOneDiv").html(errorCodeMap["Update_HighLighted"]);
						errorPaymentAmountExceed.text(errorCodeMap["1210"]);
						errorPaymentAmountExceed.css("display","block");
						minPayStepOne.addClass("errormsg");
						$("#minpaystepone_other").val('');
						$('#minpaystepone_other').attr("placeholder", '0.00');


						return;
					}else{

						if(!isEmpty(payAmount)){

							payAmount = payAmount.replace(/^0+/, '');


							if(payAmount.indexOf('.') == -1)
							{
								payAmount = payAmount+".00";
								minPayStepOne.val(payAmount);
							}
							else{

								if (payAmount.split(".")[0].length >= 1){
									minPayStepOne.val(payAmount);                                           
								}
								else
								{
									payAmount="0"+payAmount;
									minPayStepOne.val(payAmount); 
								}

							}
						}
					}
				}

				dfs.crd.pymt.changeDropDownLabel();
			});

			minPayStepOne.keydown(function(event) {
				var isBankSelected = false;
				var isDateSelected = false;
				if ($("#bankDropDownStepOne").find("option:selected").index() != 0)        
					isBankSelected = true;


/*				if ($("#date-compare").val() != "") 
					isDateSelected = true; */
				
				if((event.which > 47 && event.which < 58) || (event.which > 95 && event.which < 106)){
					if(isBankSelected /*&& isDateSelected*/){
						activebtn("makePaymentOneContinue");
					}
				}else if(event.which == 190 || event.which == 110){
					if(minPayStepOne.val().indexOf('.') == -1){
						if(isBankSelected /*&& isDateSelected*/){
							activebtn("makePaymentOneContinue");
						}
					}else{
						deactiveBtn("makePaymentOneContinue");
					}
				}else{
					deactiveBtn("makePaymentOneContinue");
				}
			});
			dfs.crd.pymt.populateMakePaymentOne("MAKEPAYMENTONE");
			var duedate = new Date($("#minpaystepone_paymentduedate").text());
			//alert(duedate);
		/*	$("#calendar").datepicker(
					{
						dayNamesMin:["Sun","Mon","Tue","Wed","Thu","Fri","Sat"],
						hideIfNoPrevNext : true,
						showOtherMonths : true,
						selectOtherMonths : false,
						constrainInput : true,
						dueDate:duedate,
						onSelect : function(dateText, inst)
						{
						 dfs.crd.pymt.paymentDateSet(dateText);
						},
						beforeShowDay : dfs.crd.pymt.enableSpecificDates
				}); */
//				isLhnNavigation  = false;
		}
		else {
			killDataFromCache("OPTION_SELECTED_ON_MAP1");
			cpEvent.preventDefault();
			history.back();
		}
	} catch (err) {
		showSysException(err);
	}
}

function focusOtherAmount()
{
    $("#minpaystepone_other").focus();
    $("#minpaystepone_other").click();
    return false;
}

dfs.crd.pymt.populateMakePaymentOne = function(pageName)
{
	try {
		var stepOne = dfs.crd.pymt.makePaymentStepOneAjaxCall(pageName);

		if (!jQuery.isEmptyObject(stepOne))
			dfs.crd.pymt.populateMakePaymentPageDivs(stepOne, pageName);

	} catch (err) {
		showSysException(err);
	}
}

dfs.crd.pymt.enableSpecificDates = function(date)
{
	try {
		var newDate = new Date(date);
		var month = newDate.getMonth() + 1;
		var day = newDate.getDate();
		var year = newDate.getFullYear();
		if (month < 10)
			month = "0" + month;

		if (day < 10)
			day = "0" + day;

		for (i = 0; i < dfs.crd.pymt.validDays.length; i++) {
			if ($.inArray(month + '/' + day + '/' + year, dfs.crd.pymt.validDays) != -1)
				return [ true ];

		}
		return [ false ];
	} catch (err) {
		showSysException(err);
	}

}

dfs.crd.pymt.makePaymentStepOneAjaxCall = function(pageId)
{
	try {

		var newDate = new Date();
		var MAKEPAYMENTSTEPONEURL = RESTURL+"pymt/v1/makepayment?"+newDate+"" ;
		var stepOne = getDataFromCache(pageId);
//		showSpinner();
		$
		.ajax(
				{
					type : "GET",
					url : MAKEPAYMENTSTEPONEURL,
					async : false,
					dataType : "json",
					headers : prepareGetHeader(),
					success : function(responseData, status, jqXHR)
					{

						hideSpinner();
              if (!validateResponse(responseData,"makeaPaymentstep1Validation"))      // Pen Test Validation
              {
              errorHandler("SecurityTestFail","","");
              return;
              }
						stepOne = responseData;							
						putDataToCache(pageId, stepOne);
					},
					error : function(jqXHR, textStatus, errorThrown)
					{
						hideSpinner();
						var code = getResponseStatusCode(jqXHR);
						cpEvent.preventDefault();
						switch (code)
						{
						case "1216":
							var errorMessage = errorCodeMap["1216"];
							dfs.crd.pymt.afterMakePayFlag = true;
							dfs.crd.pymt.messageAftrmakePay = errorMessage;
							cpEvent.preventDefault();
							navigation("../payments/paymentsSummary");
							break;
						case "1217":
							var errorMsgData = getResponsErrorData(jqXHR);
							var currentBalance;
							var pendingPayments;
							if (!isEmpty(errorMsgData[0])) {
								pendingPayments = dfs.crd.pymt
								.returnCorrectValue(
										errorMsgData,
								"pendingPayments");
								currentBalance = dfs.crd.pymt
								.returnCorrectValue(
										errorMsgData,
								"currentBalance");
							}
							var errorMessage = errorCodeMap["1217"];
							var payDetails = [];
							payDetails["currentBalance"] = currentBalance;
							payDetails["pendingPayments"] = pendingPayments;
							var parseContentText = parseContent(
									errorMessage, payDetails);
							dfs.crd.pymt.afterMakePayFlag = true;
							dfs.crd.pymt.messageAftrmakePay = parseContentText;
							cpEvent.preventDefault();
							navigation("../payments/paymentsSummary");
							break;
						case "1218":
							var errorMsgData = getResponsErrorData(jqXHR);
							var bankName;
							var payAmount;
							var postingDate;
							var confirmationCode;
							var maskedAccNumber;
							if (!isEmpty(errorMsgData[0])) {
								bankName = dfs.crd.pymt.returnCorrectValue(errorMsgData,"bankName");
								payAmount = dfs.crd.pymt.returnCorrectValue(errorMsgData,"paymentAmount");
								postingDate = dfs.crd.pymt.returnCorrectValue(errorMsgData,"paymentDate");
								maskedAccNumber = dfs.crd.pymt.returnCorrectValue(errorMsgData,"maskedBankAccountNumber");
								confirmationCode = dfs.crd.pymt.returnCorrectValue(errorMsgData,"confirmationNumber");
							}
							var errorMessage = errorCodeMap.Make_Payment_1218;
							var payDetails = [];
							payDetails["errormessage"] = errorCodeMap["1218"];
							if (!isEmpty(bankName)
									&& !isEmpty(maskedAccNumber))
								bankAccountDetails = dfs.crd.pymt.truncateBankDetails(maskedAccNumber,bankName)
								if (!isEmpty(bankAccountDetails.bankName))
									payDetails["bankName"] = bankAccountDetails.bankName;
								else
									payDetails["bankName"] = "";
							if (!isEmpty(bankAccountDetails.accountNumber))
								payDetails["maskedAccNumber"] = bankAccountDetails.accountNumber;
							else
								payDetails["maskedAccNumber"] = "";
							payDetails["payAmount"] = payAmount;
							payDetails["postingDate"] = formatPostingDueDate_MakePaymentStep2(postingDate);
							payDetails["confirmationCode"] = confirmationCode;
							var parseContentText = parseContent(
									errorMessage, payDetails);
							if (!isEmpty(parseContentText)) {
								errorHandler(code, parseContentText,
								"paymentStep1");
							}
							else {
								errorHandler("0", "", "paymentStep1");
							}
							break;
						case "1232":
							errorHandler(code, errorCodeMap["1232"],
							"paymentStep1");
							break;
						default:
							errorHandler(code, "", "paymentStep1");
						}
					}
				});

		return stepOne;
	} catch (err) {
		showSysException(err);
	}
}

$("#paymentStep1-pg").live("pagebeforeshow", function() {
	if(dfs.crd.pymt.isLinkClicked){
				
		var payOnBackData = getDataFromCache("OPTION_SELECTED_LINK_CLICK");
		dfs.crd.pymt.isLinkClicked = false;
		if(!isEmpty(payOnBackData)){
			console.log("WE are in pagebefore show from Information page");
			var selectedPostingDate=payOnBackData.PAYMENTPOSTINGDATE;
			var selectedFieldVal=payOnBackData.SELECTFIELDVAL;
			var selectedBankName=payOnBackData.BANKNAME;
		    var selectedExistingDate=payOnBackData.SELECTEDEXISTINGDATE; 	
			var selectedIndex=payOnBackData.SELECTEDINDEX;
			console.log("WE are in pagebefore show from Information page with values"+selectedPostingDate+" -- "+selectedFieldVal+"  --  "+selectedBankName);
			if(!isEmpty(selectedBankName)){
				console.log("WE are in pagebefore show from Information page SelectedBankName"+selectedBankName);
				$("#DD2.ui-btn-text").text(selectedBankName);
				dfs.crd.pymt.bankSelected();
			}
			if(!isEmpty(selectedPostingDate)){
				console.log("WE are in pagebefore show from Information page SelectedPostingDate"+selectedPostingDate);
				$("#datepicker-val").html(selectedPostingDate);
				$("#datepicker-val").css("display","block");
				$("#datepicker-value").val(selectedPostingDate);
				$("#date-compare").val(selectedExistingDate);
				dfs.crd.pymt.paymentDateSet(selectedPostingDate);
			}
			if(!isEmpty(selectedExistingDate)){
				console.log("WE are in pagebefore show from Information page SelectedExistingDate"+selectedExistingDate);
				$("#date-compare").val(selectedExistingDate);
			}
			
			
			if(!isEmpty(selectedFieldVal)){
				if(selectedFieldVal.indexOf("$") >= -1 &&  selectedFieldVal.indexOf(".00") >= -1){
					selectedFieldVal=selectedFieldVal.replace("$","");
				}
				console.log("WE are in pagebefore show from Information page SelectedFieldVal"+selectedFieldVal);
			}
			if(!isEmpty(selectedIndex)){
				if( selectedIndex == "choice-3"){
					$("#minpaystepone_other").val(selectedFieldVal);	
				}else{
					payOnBackData["SELECTFIELDVAL"]="";
				}
				console.log("WE are in pagebefore show from Information page selectedIndex"+selectedIndex
				);
			}
			dfs.crd.pymt.changeDropDownLabel();
		}
	
	}else{	
		console.log("WE are in pagebefore show from Page 2");
		var paymentStep1SeletecFields = getDataFromCache("OPTION_SELECTED_ON_MAP1");
		if(!jQuery.isEmptyObject(paymentStep1SeletecFields)){
			var selectedPostingDate=paymentStep1SeletecFields.PAYMENTPOSTINGDATE;
			var selectedFieldVal=paymentStep1SeletecFields.SELECTFIELDVAL;
			var selectedBankName=paymentStep1SeletecFields.BANKNAME;
		    var selectedExistingDate=paymentStep1SeletecFields.SELECTEDEXISTINGDATE; 	
			var selectedIndex=paymentStep1SeletecFields.SELECTEDINDEX;		
			$("#DD2.ui-btn-text").text(selectedBankName);
			dfs.crd.pymt.bankSelected();
			
			var selectedFieldVal=paymentStep1SeletecFields.SELECTFIELDVAL;
			if(!isEmpty(selectedFieldVal)){
				if(selectedFieldVal.indexOf("$") >= -1 &&  selectedFieldVal.indexOf(".00") >= -1){
					selectedFieldVal=selectedFieldVal.replace("$","");
				}
			}
			if(!isEmpty(selectedIndex)){
				if( selectedIndex == "choice-3"){
					$("#minpaystepone_other").val(selectedFieldVal);	
				}else{
					paymentStep1SeletecFields["SELECTFIELDVAL"]="";
				}
			}
			$("#datepicker-val").html(selectedPostingDate);
			$("#datepicker-val").css("display","block");
			$("#date-compare").val(selectedExistingDate);
			$("#datepicker-value").val(selectedPostingDate);
			activebtn("makePaymentOneContinue");
		}	
	}
});

$("#paymentStep1-pg").live("pagecreate", function() {
	var paymentStep1SeletecFields = "";
	if(dfs.crd.pymt.isLinkClicked){
		paymentStep1SeletecFields = getDataFromCache("OPTION_SELECTED_LINK_CLICK");
	}else{
		paymentStep1SeletecFields = getDataFromCache("OPTION_SELECTED_ON_MAP1");
	}
	if(!jQuery.isEmptyObject(paymentStep1SeletecFields)){
	
		var selectedIndex=paymentStep1SeletecFields.SELECTEDINDEX;  
		if ( !isEmpty(selectedIndex)){
		
			var selectedId="radio-"+selectedIndex+"";
			document.getElementById(selectedId).checked=true;			
		}    
	}
});

dfs.crd.pymt.populateMakePaymentPageDivs = function(stepOne, pageId)
{
	try {

		if (!isEmpty(stepOne)) {
			var bank_name_info = new Array();
			var minimumPayment = stepOne.minimumPayment;
			var paymentDueDate = stepOne.paymentDueDate;
			var zeroMinPay = (minimumPayment == 0);
			var haveSchedulePayments = stepOne.haveSchedulePayments;
			var hasDpPendingPayment = stepOne.hasDpPendingPayment;
			var defaultValue = "$0.00";
			var minPayStepOnePayDueDate = $("#minpaystepone_paymentduedate");
			var commonErrorMAP1 = $("#commonErrorInMakePaymentStepOneDiv");
			var paymentStep1 = $("#paymentStep1-pg");
			if(isEmpty(dfs.crd.pymt.globalOpenAmount)){
				var paySummary 				= dfs.crd.pymt.getPaymentSummaryData("PAYMENTSUMMARY");
				if (!isEmpty(paySummary)) {
					if(!isEmpty(paySummary.openAmount))
						dfs.crd.pymt.globalOpenAmount = paySummary.openAmount;
				}
			}

			if(dfs.crd.pymt.globalOpenAmount <0)
				dfs.crd.pymt.globalOpenAmount = dfs.crd.pymt.globalOpenAmount.substring(1);



			if (stepOne.isHaMode)
				$("#manage_Bank_Information").css('display', 'none');
			if (stepOne.isHaMode) {
				$("#ha_MakePaymentStepOne_Error").html(errorCodeMap["Pay_func_unavail"]);
				var postingDate = stepOne.bankInfo[0].openDates[0];
				var currentDate = new Date(postingDate);
				$("#date-compare").val(postingDate);
				var dateValue = $("#date-compare").val();
				$("#datepicker-value").val(
						formatPaymentDueDate_MakePaymentStep1(currentDate));
				$("#datepicker-val").show().text(
						formatPaymentDueDate_MakePaymentStep1(currentDate));
				$("#button-selectdate").remove();
				$("#paymentStep1-pg .bluelink:eq(1)").css("display","none");
			}

			globalLastStatementBalance = stepOne.lastStatementBalance;

			if (!isEmpty(stepOne.bankInfo) && stepOne.bankInfo.length > 0) {
				var bankInformation = stepOne.bankInfo;
				if (!isEmpty(bankInformation[0].bankName))
					bank_name_info = dfs.crd.pymt.getBankList(stepOne);
				else if (!isEmpty(bankInformation[0].bankShortName))
					bank_name_info = dfs.crd.pymt.getBankList(stepOne);
			}
			else {
				cpEvent.preventDefault();
				var code = "1201";
				var parseContentText = errorCodeMap["1201"];
				errorHandler(code, parseContentText,
				"paymentStep1");
			}
			$("#bank_info").html(bank_name_info);

			if (stepOne.daysDelinquent > 0)
				commonErrorMAP1.append(errorCodeMap["Days_Delinquent"]);

			if (!(zeroMinPay || haveSchedulePayments || hasDpPendingPayment))
				paymentStep1.find("input[type='radio']:eq(0)").checkboxradio(
				'enable');

			if (!(haveSchedulePayments || hasDpPendingPayment))
				paymentStep1.find("input[type='radio']:eq(1)").checkboxradio(
				'enable');

			if (!isEmpty(minimumPayment))
				$("#minpaystepone_minpayment").text(
						"$" + numberWithCommas(minimumPayment));
			else
				$("#minpaystepone_minpayment").text(defaultValue);

			if (parseFloat(stepOne.overLimitAmount) > 0.00) {
				var overLimitAmt = [];
				var errMsg = errorCodeMap["Over_Limit"];
				overLimitAmt["overLimitAmount"] = stepOne.overLimitAmount;
				commonErrorMAP1.html("\n" + parseContent(errMsg, overLimitAmt));
			}

			if (!isEmpty(paymentDueDate)) {
				var currentDate = new Date();
				var formatDate = new Date(paymentDueDate);
				if (stepOne.isAccountOverDue
						&& (parseFloat(minimumPayment) > 0.00)) {

					minPayStepOnePayDueDate.addClass("redtext boldtext");
					minPayStepOnePayDueDate.prev().addClass("redtext boldtext");
					$(".errormsg").css("display", "block");
				}
				minPayStepOnePayDueDate
				.text(formatPaymentDueDate_MakePaymentStep1(formatDate));
				$("#calendar").datepicker( "option", { minDueDate: new Date(dfs.crd.pymt.validDays[0]) } );
				$("#calendar").datepicker( "option", { dueDate: paymentDueDate } );
			}

			if (parseFloat(stepOne.lastStatementBalance) > 0
					&& (parseFloat(stepOne.lastStatementBalance) < parseFloat(stepOne.currentBalance))) {
				$("#variable_lable").html("Statement Balance");
				if (!isEmpty(stepOne.lastStatementBalance))
					$("#minpaystepone_laststatementbalance")
					.text(
							"$"
							+ numberWithCommas(stepOne.lastStatementBalance));
				else
					$("#minpaystepone_laststatementbalance").text(defaultValue);
			}
			else {
				if (!isEmpty(stepOne.currentBalance))
					$("#minpaystepone_laststatementbalance").text(
							"$" + numberWithCommas(stepOne.currentBalance));
				else
					$("#minpaystepone_laststatementbalance").text(defaultValue);

			}

			if (dfs.crd.pymt.afterConfirmFlag) {
				if (dfs.crd.pymt.outstandingbalance) {
					$("#errorPaymentAmountExceed").text(
							dfs.crd.pymt.messageAftrConfrm);
					$("#minpaystepone_other").addClass("errormsg");
				}
				else {
					commonErrorMAP1.html(dfs.crd.pymt.messageAftrConfrm);
				}
			}
		}
		else {
			cpEvent.preventDefault();
			var code = "0";
			var parseContentText = errorCodeMap["0"];
			errorHandler(code, parseContentText, "paymentStep1");
		}
	} catch (err) {
		showSysException(err);
	}
}

dfs.crd.pymt.getBankList = function(stepOne)
{
	try {
		var isCutOffMakePaymentOne = stepOne.isCutOffAvailable;
		var selectedBankKey;
		var paymentStep1SeletecFields = getDataFromCache("OPTION_SELECTED_ON_MAP1");
			

		if(dfs.crd.pymt.isLinkClicked){
				paymentStep1SeletecFields = getDataFromCache("OPTION_SELECTED_LINK_CLICK")
		}
		if(!jQuery.isEmptyObject(paymentStep1SeletecFields)){
			selectedBankKey=paymentStep1SeletecFields.BANKVAL;			
		}
		var calendar = $("#calendar");
		var accountNumber=stepOne.bankInfo[0].maskedBankAcctNbr;
		var bankName = stepOne.bankInfo[0].bankName;
		var truncatedBankName=dfs.crd.pymt.truncateBankDetails(accountNumber,bankName);
		dfs.crd.pymt.selectvar = (stepOne.bankInfo.length == 1) ? truncatedBankName["bankName"]+truncatedBankName["accountNumber"] : "- Select Bank Account -";
		var fieldtext = '<div class="ui-select"><div data-theme="d" class="ui-btn ui-btn-icon-right ui-btn-corner-all ui-shadow ui-btn-hover-d ui-btn-up-d"><span class="ui-btn-inner ui-btn-corner-all" aria-hidden="true"><span class="ui-btn-text" id="DD2" style="width:250px;">'
			+ dfs.crd.pymt.selectvar
			+ '</span><span class="ui-icon ui-icon-arrow-d ui-icon-shadow"></span></span><select class="ui-select" name="BankStepOne" id="bankDropDownStepOne" onchange="dfs.crd.pymt.bankSelected(); ">';
		fieldtext += "<option value='- Select Bank Account -'  selected='selected' >- Select Bank Account -</option>";

		if (typeof stepOne != 'undefined') {
			var selected = stepOne.bankInfo.length == 1 ? "selected = 'selected'": '';			
			
			
			if (!isEmpty(stepOne.bankInfo)) {
				for ( var key in stepOne.bankInfo) {
					if (!isEmpty(stepOne.bankInfo[key])) {
						var truncatedBankName = dfs.crd.pymt.truncateBankDetails(
								stepOne.bankInfo[key].maskedBankAcctNbr,
								stepOne.bankInfo[key].bankName);
						var optionVal=stepOne.bankInfo[key].hashedBankAcctNbr + "," + stepOne.bankInfo[key].hashedBankRoutingNbr + "," + stepOne.bankInfo[key].bankShortName;
						
						if(!isEmpty(selectedBankKey)){							
							if(selectedBankKey == optionVal){
								selected="selected = 'selected'";
							}
						}
						fieldtext += "<option value=\""
							+ optionVal
							+ "\"" + selected + " >"
							+ truncatedBankName.bankName
							+ truncatedBankName.accountNumber + "</option>";
						selected='';
					}
				}
			}


			fieldtext += "</select></div></div>";



			if (!(stepOne.isHaMode) /*&& isCutOffMakePaymentOne*/) {
				if(projectBeyondCard)
					$("#cuttOff_Note_MakePaymentOne").text(errorCodeMap["Is_cutoffPB"]);
				else
					$("#cuttOff_Note_MakePaymentOne").text(errorCodeMap["Is_cutoff"]);
			}


			var openDates = stepOne.bankInfo[0].openDates;
			if (!isEmpty(openDates))
				dfs.crd.pymt.validDays = stepOne.bankInfo[0].openDates;

			if (stepOne.bankInfo.length == 1) {
				if (!isEmpty(dfs.crd.pymt.validDays)) {	
					calendar.datepicker( "setDate" , new Date(dfs.crd.pymt.validDays[0]));

					calendar.datepicker("option", "minDate",
							dfs.crd.pymt.validDays[0]);
					calendar.datepicker(
							"option",
							"maxDate",
							dfs.crd.pymt.validDays[dfs.crd.pymt.validDays.length - 1]);
					dfs.crd.pymt.paymentDateSet(dfs.crd.pymt.validDays[0]);
				}
				$("#button-selectdate").removeClass('disabled');
			}
			return fieldtext;
		}
	} catch (err) {
		showSysException(err);
	}
}

dfs.crd.pymt.bankSelected = function()
{
	try {

		var dateButton = $("#button-selectdate");
		var calendar = $("#calendar");
		var bankDropDownStepOne = $("#bankDropDownStepOne");
		var errorPostingDate = $("#errorPostingDate");
		var commonErrorMap1 = $("#commonErrorInMakePaymentStepOneDiv");
		var errorMsgClass = $(".errormsg");
		$("#radio2Error").text("");
		$("#radio1Error").text("");
		$("#errorPaymentAmountExceed").text("");
		$("#minpaystepone_other").removeClass('errormsg');
		commonErrorMap1.html("");
		errorPostingDate.text("");
		$("#datepicker-val").removeClass("redtext");
		$("#minpaystepone_paymentduedate").removeClass('errormsg');
		$("#minpaystepone_paymentduedate").prev().removeClass('errormsg');

		dateButton.removeClass("ui-btn-up-errormsg");
		dateButton.removeClass("disabled");
		bankDropDownStepOne.removeClass("ui-btn-up-errormsg");
		var stepOne = getDataFromCache("MAKEPAYMENTONE");
		if (!isEmpty(stepOne)) {
			dateButton.removeClass("disabled ui-btn-up-errormsg");
			var option = bankDropDownStepOne.find("option:selected");
			var optionValue = option.val();
			var newtxt = option.text();

			$("#DD2.ui-btn-text").text(newtxt);
			if ((!stepOne.isHaMode)) {
				if (option.index() == 0) {
					dateButton.addClass("disabled");
					dateButton.removeClass("ui-btn-up-errormsg");
					$("#datepicker-val").hide().text("");
					$("#datepicker-value").hide().val("");
					$("#date-compare").val("");
					dfs.crd.pymt.validDays = "";
					$.mobile.activePage.find("#makePaymentOneContinue").attr(
							"disabled", "true").parent()
							.addClass("ui-disabled");
					return false;
				}
				else if (!isEmpty(stepOne.bankInfo[option.index() - 1].openDates)) {
					if (stepOne.bankInfo[option.index() - 1].openDates.length < 1) {
						commonErrorMap1
						.html(errorCodeMap["Update_HighLighted"]);
						errorMsgClass.css("display", "block");
						errorPostingDate.html(errorCodeMap["NoDates_for_Bank"]);
						$("#datepicker-val").hide().text("");
						$("#datepicker-value").hide().val("");
						$("#date-compare").val("");
						dateButton.addClass("disabled");
						dateButton.addClass("ui-btn-up-errormsg")
						$.mobile.activePage.find("#makePaymentOneContinue")
						.attr("disabled", "true").parent().addClass(
						"ui-disabled");
						return false;
						// otherwise enable button & remove error
					}else {                 
						dfs.crd.pymt.validDays = stepOne.bankInfo[option.index() - 1].openDates;
						dfs.crd.pymt.paymentDateSet(dfs.crd.pymt.validDays[0]);
						dateButton.removeClass("disabled ui-btn-up-errormsg");
					}
					var existDate = $("#date-compare").val();
					if(!isEmpty(existDate)) {
						if (!(jQuery.inArray(existDate, dfs.crd.pymt.validDays) > -1)){
							var errorMessage = errorCodeMap["BAD_DATE"];
							commonErrorMap1
							.html(errorCodeMap["Update_HighLighted"]);
							$(".errormsg").css("display", "block");
							$("#button-selectdate").addClass("ui-btn-up-errormsg");
							$("#datepicker-val").addClass("redtext");
							$("#errorPostingDate").text(errorMessage);
							deactiveBtn("makePaymentOneContinue");
							return ;
						}	                            
					}

				}
				if (!isEmpty(dfs.crd.pymt.validDays)) {
					calendar.datepicker( "setDate" , new Date(dfs.crd.pymt.validDays[0]));
					calendar.datepicker("option", "minDate",
							dfs.crd.pymt.validDays[0]);
					calendar.datepicker("option","maxDate",dfs.crd.pymt.validDays[dfs.crd.pymt.validDays.length - 1]);
				}
			}
			dfs.crd.pymt.changeDropDownLabel();
		}
	} catch (err) {
		showSysException(err)
	}
}

dfs.crd.pymt.paymentDateSet = function(paymentDate)
{
	try {
		$("a#calendar-cancel").click();
		var selectedDate = paymentDate.split("/");
		var month = selectedDate[0];
		
		/*
		.replace("01", "Jan ").replace("02", "Feb ")
		.replace("03", "Mar ").replace("04", "Apr ").replace("05",
		"May ").replace("06", "Jun ").replace("07", "Jul ")
		.replace("08", "Aug ").replace("09", "Sep ").replace("10",
		"Oct ").replace("11", "Nov ").replace("12", "Dec ");*/

		var day = parseInt(selectedDate[1], 10);

		day = day.toString();


/**/

		if (day < 10)
			day = "0" + day;

/*var date = new Date('2010-10-11T00:00:00+05:30');
alert(date.getMonth().toString() + '/' + date.getDate().toString() + '/' +  date.getFullYear().toString());*/

		/*var date = dateFormat(new Date("Thu Oct 14 2010 00:00:00 GMT 0530 (India Standard Time)"), 'dd/mm/yyyy');*/

		var formattedDate = month+"/"+day+"/" + selectedDate[2];
		//paymentDate = formattedDate.replace("Thu Oct 14 2010 00:00:00 GMT 0530 (India Standard Time)", 'dd/mm/yyyy')
		//alert(paymentDate);
		/*paymentDate = formattedDate.replace(", ", "/").replace("Jan ", "01/")
		.replace("Feb ", "02/").replace("Mar ", "03/").replace("Apr ",
		"04/").replace("May ", "05/").replace("Jun ", "06/")
		.replace("Jul ", "07/").replace("Aug ", "08/").replace("Sep ",
		"09/").replace("Oct ", "10/").replace("Nov ", "11/")
		.replace("Dec ", "12/");*/

		$("#datepicker-value").val(formattedDate);
		$("#datepicker-val").val(formattedDate);
		$("#datepicker-val").css("margin","4px 0px");
		$("#date-compare").val(formattedDate);
		
		if ((jQuery.inArray(($("#date-compare").val()), dfs.crd.pymt.validDays) > -1)){	                                           
			$("#errorPostingDate").css("display", "none");
			$("#button-selectdate").removeClass("ui-btn-up-errormsg");
			$("#datepicker-val").removeClass("redtext");
//			$("#errorPostingDate").text("");
			dfs.crd.pymt.changeDropDownLabel();
		}
		dfs.crd.pymt.changeDropDownLabel();
	} catch (err) {
		showSysException(err);
	}
};

dfs.crd.pymt.checkCurrency = function(value)
{
	try {
		return value != "" ? /^\s*?((\d+(\.\d\d)?)|(\d+(\.\d)?)|(\.\d\d)|(\.\d))\s*$/
				.test(value)
				: false;
	} catch (err) {
		showSysException(err);
	}
}

dfs.crd.pymt.continuePaymentStep1ToStep2 = function()
{
	try {
		var commonErrorMap1 = $("#commonErrorInMakePaymentStepOneDiv");
		var radio2Error = $("#radio2Error");
		var isAmountSelected = false;
		var isBankSelected = false;
		var ischecked_radio = false;
		var validDate = true;
		var bankAccInfo = getDataFromCache("MAKEPAYMENTONE");
		var isHaMode = bankAccInfo.isHaMode;
		var PCT_OVER = "1.1";
		var isCutOff = bankAccInfo.isCutOffAvailable;		
		var currValue 			= bankAccInfo.currentBalance;
		var option = $("#bankDropDownStepOne").find("option:selected")
		var bank = bankAccInfo.bankInfo[option.index() - 1].bankName;
		var maskedAccountNumber = bankAccInfo.bankInfo[option.index() - 1].maskedBankAcctNbr;
		var keyValue = $("#paymentStep1-pg").find(
		"#bankDropDownStepOne option:selected").val();
		var existingDate = $("#date-compare").val();
		var payDateValue = $("#datepicker-value").val();
		var stepone_value_radio = $("input:checked").text();
		var stepone_name_radio = $("input:checked").next().text();
		var paymentStep1SeletecFields = {};
		radio2Error.text("");
		$("#radio1Error").text("");
		$("#errorPaymentAmountExceed").text("");
		commonErrorMap1.text("");

		if (existingDate != "") {
			if (!(jQuery.inArray(existingDate, dfs.crd.pymt.validDays) > -1)) {
				$("#button-selectdate").addClass("ui-btn-up-errormsg");
				$("#datepicker-val").addClass("errormsg");
				validDate = false;
			}
			else {
				$("#button-selectdate").removeClass("ui-btn-up-errormsg");
				$("#datepicker-val").removeClass("errormsg");
			}
		}

		if ($("#radio-choice-1").attr('checked')) {
			ischecked_radio = true;
			stepone_value_radio = $("#minpaystepone_minpayment").text();
		}
		else if ($("#radio-choice-2").attr('checked')) {
			ischecked_radio = true;
			stepone_value_radio = $("#minpaystepone_laststatementbalance")
			.text();
			if (parseFloat(stepone_value_radio) > 99999.99
					|| parseFloat(stepone_value_radio) < 0.01) {
				radio2Error.css("display", "block");
				radio2Error.text(errorCodeMap["1207"]);
				deactiveBtn("makePaymentOneContinue");
				return;
			}
		}
		else if ($("#radio-choice-3").attr('checked')) {
			ischecked_radio = true;
			var value = currValue.substring(0);
			var payAmount = $("#minpaystepone_other").val();
			var pctvalue=parseFloat(PCT_OVER * value);

			if (isEmpty(payAmount)) {
				commonErrorMap1.html(errorCodeMap["Update_HighLighted"]);
				$("#errorPaymentAmountExceed").text(errorCodeMap["1222"]);
				$("#minpaystepone_other").addClass("errormsg");
				$("#minpaystepone_other").val('');
				$('#minpaystepone_other').attr("placeholder", '0.00');

				return;
			}
			else if (!dfs.crd.pymt.checkCurrency(payAmount)) {
				$("#commonErrorInMakePaymentStepOneDiv").html(
						errorCodeMap["Update_HighLighted"]);

				// $("#errorPaymentAmountExceed").text(errorCodeMap["1210"]);
				$("#minpaystepone_other").addClass("errormsg");

				$("#minpaystepone_other").val('');
				$('#minpaystepone_other').attr("placeholder", '0.00');

				return;
			}
			else if (parseFloat(payAmount) < 0.01
					|| parseFloat(payAmount) > 99999.99) {
				commonErrorMap1.html(errorCodeMap["Update_HighLighted"]);
				$("#errorPaymentAmountExceed").text(errorCodeMap["1207"]);
				$("#minpaystepone_other").addClass('errormsg');
				$("#minpaystepone_other").val('');
				$('#minpaystepone_other').attr("placeholder", '0.00');

				return;
			}
			else if ((!bankAccInfo.haveSchedulePayments)
					&& (parseFloat(payAmount) > (PCT_OVER * parseFloat(value)))) {
				commonErrorMap1.html(errorCodeMap["Update_HighLighted"]);
				var errMsg = errorCodeMap["1208"];
				var curntValue = [];
				curntValue["currentBalance"] = value;

				$("#errorPaymentAmountExceed").text(
						parseContent(errMsg, curntValue));
				$("#minpaystepone_other").addClass('errormsg');
				
				return;
			}else if ((dfs.crd.pymt.globalOpenAmount != "0.00") && parseFloat(payAmount) > (PCT_OVER * parseFloat(dfs.crd.pymt.globalOpenAmount))) 
			{
				commonErrorMap1.html(errorCodeMap["Update_HighLighted"]);
				var errorMessage = errorCodeMap["1209"];
				var payDetail = [];
				payDetail["outstandingBalance"] = dfs.crd.pymt.globalOpenAmount;
				var parseContentText = parseContent(errorMessage, payDetail);
				$("#errorPaymentAmountExceed").text(
						parseContent(parseContentText, curntValue));
				$("#minpaystepone_other").addClass('errormsg');
				return;
			} 
			else {
				stepone_value_radio = "$" + payAmount;
			}
		}

		if (option.index() != 0) {
			if ((bankAccInfo.bankInfo[option.index() - 1].hashedBankAcctNbr != ("" || null))) {
				if ((bankAccInfo.bankInfo[option.index() - 1].hashedBankRoutingNbr != ("" && null))) {
					if ((bankAccInfo.bankInfo[option.index() - 1].bankShortName != ("" && null))) {
						isBankSelected = true;
					}
				}
			}
		}
		else {
			commonErrorMap1.html(errorCodeMap["Select_Bank"]);
			return;
		}
		if (stepone_value_radio != ("" && null))
			isAmountSelected = true;

		if (!isBankSelected) {
			var invalidBank = [];
			invalidBank["bankAccount"] = bank;
			var errorMessage = errorCodeMap["BAD_BANK"];
			var errorTxt = parseContent(errorMessage, invalidBank);
			commonErrorMap1.html(errorTxt);
			$("#bankDropDownStepOne").addClass("ui-btn-up-errormsg");
		}
		else if (!validDate && (!bankAccInfo.isHaMode)) {
			var errorMessage = errorCodeMap["BAD_DATE"];
			commonErrorMap1.html(errorCodeMap["Update_HighLighted"]);
			$(".errormsg").css("display", "block");
			$("#errorPostingDate").text(errorMessage);
		}
		else {
			var paymentsarray =
			{
					"BankName" : bank,
					"PaymentOption" : stepone_name_radio,
					"maskedAccountNumber" : maskedAccountNumber,
					"Amount" : stepone_value_radio,
					"PostingDate" : existingDate,
					"bankKey" : keyValue,
					"postDateValue" : payDateValue,
					"isHAMode" : isHaMode,
					"isCutOff" : isCutOff
			};
			var select_box = document.getElementById("bankDropDownStepOne");
			if(!isEmpty(select_box)){
				bankSelectFieldtitle = select_box[select_box.selectedIndex].text;
			}
			 var selectIndex;
             var selectedRadioButtons = document.getElementsByName("radio-choice-1");
             if(!isEmpty(selectedRadioButtons)){
            	 var radioLength=selectedRadioButtons.length;
            	 for(var i = 0; i < radioLength; i++) {
            		 if(selectedRadioButtons[i].checked) {
            			 selectIndex= selectedRadioButtons[i].value;

            		 }
            	 }
             }
			paymentStep1SeletecFields["BANKVAL"]=keyValue;
			paymentStep1SeletecFields["BANKNAME"]=bankSelectFieldtitle;
			paymentStep1SeletecFields["SELECTFIELDVAL"]=stepone_value_radio;
			paymentStep1SeletecFields["PAYMENTPOSTINGDATE"]=payDateValue;
			paymentStep1SeletecFields["SELECTEDINDEX"]=selectIndex;             
			paymentStep1SeletecFields["SELECTEDEXISTINGDATE"]=existingDate; 
			putDataToCache("OPTION_SELECTED_ON_MAP1",paymentStep1SeletecFields);
			
			putDataToCache("MAKEPAYMENTTWO", paymentsarray);
			killDataFromCache("OPTION_SELECTED_LINK_CLICK");
			navigation("paymentStep2");
		}
	} catch (err) {
		showSysException(err);
	}
}

dfs.crd.pymt.changeDropDownLabel = function()
{
	try {
		$("#radio2Error").text("");
		$("#radio1Error").text("");
		$("#errorPaymentAmountExceed").text("");
		$("#minpaystepone_other").removeClass('errormsg');
		$("#commonErrorInMakePaymentStepOneDiv").html("");
		var isBankSelected = false;
		var isDateSelected = false;
		var radioflag = false;
		var isOtherAmount = false;
		var ischecked_radio = false;
		var option = $("#bankDropDownStepOne").find("option:selected")

		if (option.index() != 0)
			isBankSelected = true;

/*		if ($("#date-compare").val() != "")
			isDateSelected = true; */

		var radio_btn = $("[name='radio-choice-1']");

		var otherAmountValue = $("#minpaystepone_other").val();

		for ( var i = 0; i < 3; i++) {
			if (radio_btn[i].checked) {
				ischecked_radio = true;
				if (i == 2)  {              
					isOtherAmount = true;
				}

			}
		}

		if (/*isDateSelected && */isBankSelected && ischecked_radio) {

			if (isOtherAmount && ($("#minpaystepone_other").val() == "")){
				deactiveBtn("makePaymentOneContinue");
			}
			else
			{
				activebtn("makePaymentOneContinue");
			}
		}
		else {
			deactiveBtn("makePaymentOneContinue");
		}
	} catch (err) {
		showSysException(err)
	}
}

dfs.crd.pymt.truncateBankDetails = function(accountNumber, bankName)
{
	try {
		var shortText = [];
		if (!isEmpty(accountNumber)) {
			var shortAccountText = jQuery.trim(accountNumber).split('*');
			accountNumber = "****"
				+ shortAccountText[shortAccountText.length - 1];
		}
		else
			accountNumber = accountNumber;
		if (!isEmpty(bankName)) {
			if (bankName.length > 20)
				bankName = jQuery.trim(bankName).substring(0, 20) + "....";
			else
				bankName = bankName;
		}
		else
			bankName = bankName;

		shortText["accountNumber"] = accountNumber;
		shortText["bankName"] = bankName;
		return shortText;
	} catch (err) {
		showSysException(err);
	}
}

/** ******************* Make Payment-2 ******************* */
function paymentStep2Load()
{
	try {
		var validPriorPagesOfpayStep2 = new Array("paymentStep1",
		"cancelPayment");
		if (jQuery.inArray(fromPageName, validPriorPagesOfpayStep2) > -1) {
			dfs.crd.pymt.populateMakePaymenttwoActivity("MAKEPAYMENTTWO");
		}else {
			cpEvent.preventDefault();
			history.back();
		}

	} catch (err) {
		showSysException(err);
	}
}


dfs.crd.pymt.getPaymentVerifiacation = function(pageName){

	try {
		var mapStepTwoData 	  = getDataFromCache("MAKEPAYMENTTWO");
		var HASPAYMENTSURL 	= RESTURL + "pymt/v1/paymentverification?paymentDate=" + mapStepTwoData.PostingDate+"&isHaMode="+mapStepTwoData.isHAMode ;
		var hasPymt ;

		showSpinner();
		$.ajax({
			type 		: "GET",
			url 		: HASPAYMENTSURL,
			async 		: false,
			dataType 	: "json",
			headers 	: prepareGetHeader(),
			success 	: function(responseData, status, jqXHR) {
				hideSpinner();
               if (!validateResponse(responseData,"paymentVerificationStep2Validation"))      // Pen Test Validation
                {
                 errorHandler("SecurityTestFail","","");
               return;
                }
				hasPymt = responseData;
			},
			error 		: function(jqXHR, textStatus, errorThrown) {
				hideSpinner();
				cpEvent.preventDefault();
				var code = getResponseStatusCode(jqXHR);
				errorHandler(code, "", "paymentStep2");
			}
		});

		return hasPymt;
	} catch (err) {
		showSysException(err);
	}

}

dfs.crd.pymt.populateMakePaymenttwoActivity = function(pageName)
{
	try {
		var steptwo = getDataFromCache(pageName);

		var hasRecentPayment = dfs.crd.pymt.getPaymentVerifiacation();
		if (!jQuery.isEmptyObject(steptwo)) {
			var newBankDetails = dfs.crd.pymt.truncateBankDetails(
					steptwo.maskedAccountNumber, steptwo.BankName);
			$("#bankname").text(
					dfs.crd.pymt.truncateBankName(newBankDetails.bankName));
			$("#accountnumber").text(newBankDetails.accountNumber);


			if(!isEmpty(hasRecentPayment)){
				if(hasRecentPayment.hasRecentActivity && !hasRecentPayment.isHaMode)
					$("#safeSecureDiv").text(errorCodeMap["safeSecure"]);

				if (!hasRecentPayment.isHaMode && hasRecentPayment.isCutOffAvailable ) {
					if(projectBeyondCard)
						$("#cuttOff_Note").text(errorCodeMap["Is_cutoffPB"]);
					else
						$("#cuttOff_Note").text(errorCodeMap["Is_cutoff"]);

				}
				
				steptwo.isHAMode = hasRecentPayment.isHaMode;
				putDataToCache("MAKEPAYMENTTWO", steptwo);
			}
			$("#paymentOption_value").text(steptwo.PaymentOption.trim());
			if (!isEmpty(steptwo.Amount))
				$("#amount_value").text(numberWithCommas(steptwo.Amount));
			else
				$("#amount_value").text("$ 0.00");
			
			$("#paymentStep2_posting_date").text(formatPaymentDueDate(steptwo.PostingDate));
			
		}
	} catch (err) {
		showSysException(err);
	}

}

dfs.crd.pymt.confirmfromstep2tostep3 = function(isErrorForSubmit,nextAvalDateToPost)
{

	try {
		var confirmdatastep3;
		var MAKEPAYMENTTHREEPOSTCALLURL = RESTURL+ "pymt/v1/paymentconfirmation";	
		
		
		var bankDetails = getDataFromCache("MAKEPAYMENTTWO");
		var bankKeyDetails = bankDetails.bankKey.split(",");
		var paymentAmountdata = bankDetails.Amount;
		var split = paymentAmountdata.split("$");
		 split = split[1].split(",");
		 if(split.length > 1)
		 split = split[0]+split[1];
		 else
			 split = split[0];
		var paymentDatedata = bankDetails.PostingDate;
		var isHaModedata = bankDetails.isHAMode;
		
		var dataJSON =
		{
				"bankShortName" : bankKeyDetails[2].trim(),
				"paymentAmount" : split,
				"paymentDate" : paymentDatedata,
				"isHaMode" : isHaModedata,
				"hashedAcctNbr" : bankKeyDetails[0],
				"hashedRoutingNumber" : bankKeyDetails[1]
		};
		
		if(isErrorForSubmit){
			dataJSON.paymentDate = nextAvalDateToPost;
		}
		
		var dataJSONString = JSON.stringify(dataJSON);
		killDataFromCache("OPTION_SELECTED_ON_MAP1");
		showSpinner();
		$
		.ajax(
				{
					type : "POST",
					url : MAKEPAYMENTTHREEPOSTCALLURL,
					async : false,
					dataType : "json",
					data : dataJSONString,
					headers : preparePostHeader(),
					success : function(responseData, status, jqXHR)
					{
						
						hideSpinner();
              if (!validateResponse(responseData,"paymentConfirmationstep3Validation"))      // Pen Test Validation
              {
                 errorHandler("SecurityTestFail","","");
              return;
              }
						if (jqXHR.status != 200 & jqXHR.status != 204) {
							cpEvent.preventDefault();
							var code = getResponseStatusCode(jqXHR);
						}
						else {
							confirmdatastep3 = responseData;
							putDataToCache("MAKEPAYMENTTHREE", confirmdatastep3);
							navigation("../payments/paymentStep3",false);
						}

						killDataFromCache("MAKEPAYMENTONE");
						killDataFromCache("PAYMENTSUMMARY");
						killDataFromCache("PENDINGPAYMENTS");
						killDataFromCache("PAYMENTHISTORY");
						killDataFromCache("ACHOME");
						
						
					},
					error : function(jqXHR, textStatus, errorThrown)
					{
						hideSpinner();						
						var code = getResponseStatusCode(jqXHR);
						$("#confirmSuccessPay3").css('disabled', 'disable');
						switch (code)
						{
						case "1209":
							var errorMsgData = getResponsErrorData(jqXHR);
							var outstandingBalance;
							if (!isEmpty(errorMsgData[0])) {
								outstandingBalance = errorMsgData[0].outstandingBalance;
							}
							var errorMessage = errorCodeMap["1209"];
							var payDetail = [];
							payDetail["outstandingBalance"] = outstandingBalance;
							var parseContentText = parseContent(
									errorMessage, payDetail);
							dfs.crd.pymt.afterConfirmFlag = true;
							dfs.crd.pymt.outstandingbalance = true;
							dfs.crd.pymt.messageAftrConfrm = parseContentText;
							navigation("paymentStep1");
							break;
						case "1216":
							var errorMsgData = getResponsErrorData(jqXHR);
							var phoneNumber;

							if (!isEmpty(errorMsgData[0]))
								phoneNumber = errorMsgData[0].phoneNum;

							var errorMessage = errorCodeMap["1216_Confirm_Payment"];
							var phoneDetail = [];
							phoneDetail["phoneNumber"] = phoneNumber;

							var parseContentText = parseContent(errorMessage, phoneDetail);

							if (!isEmpty(parseContentText))
								errorHandler(code, parseContentText,"paymentStep1");
							else
								errorHandler(code, "", "paymentStep1");

							break;
						case "1217":
							var errorMsgData = getResponsErrorData(jqXHR);
							var currentBalance;
							var pendingPayments;

							if (!isEmpty(errorMsgData[0])) {
								pendingPayments = dfs.crd.pymt.returnCorrectValue(errorMsgData,"pendingPayments");
								currentBalance = dfs.crd.pymt.returnCorrectValue(errorMsgData,"currentBalance");
							}

							var errorMessage = errorCodeMap["1217_Confirm_Payment"];
							var payDetails = [];
							payDetails["currentBalance"] = currentBalance;
							payDetails["pendingPayments"] = pendingPayments;
							var parseContentText = parseContent(
									errorMessage, payDetails);

							if (!isEmpty(parseContentText))
								errorHandler(code, parseContentText,"paymentStep1");
							else
								errorHandler("0", "", "paymentStep1");

							break;
						case "1218":
							var errorMsgData = getResponsErrorData(jqXHR);
							var bankName;
							var payAmount;
							var postingDate;
							var confirmationCode;
							var maskedAccNumber;
							var bankAccountDetails;
							if (!isEmpty(errorMsgData[0])) {
								bankName = dfs.crd.pymt.returnCorrectValue(
										errorMsgData, "bankName");
								payAmount = dfs.crd.pymt.returnCorrectValue(
										errorMsgData, "paymentAmount");
								postingDate = dfs.crd.pymt.returnCorrectValue(errorMsgData,"paymentDate");
								maskedAccNumber = dfs.crd.pymt.returnCorrectValue(errorMsgData,"maskedBankAccountNumber");
								confirmationCode = dfs.crd.pymt.returnCorrectValue(errorMsgData,"confirmationNumber");
							}

							var errorMessage = errorCodeMap.Make_Payment_1218;
							var payDetails = [];
							payDetails["errormessage"] = errorCodeMap["1218"];
							if (!isEmpty(bankName)&& !isEmpty(maskedAccNumber))
								bankAccountDetails = dfs.crd.pymttruncateBankDetails(maskedAccNumber, bankName);
							if (!isEmpty(bankAccountDetails.bankName))
								payDetails["bankName"] = bankAccountDetails.bankName;
							else
								payDetails["bankName"] = "";
							if (!isEmpty(bankAccountDetails.accountNumber))
								payDetails["maskedAccNumber"] = bankAccountDetails.accountNumber;
							else
								payDetails["maskedAccNumber"] = "";
							payDetails["payAmount"] = payAmount;
							payDetails["postingDate"] = formatPostingDueDate_MakePaymentStep2(postingDate);
							payDetails["confirmationCode"] = confirmationCode;
							var parseContentText = parseContent(errorMessage, payDetails);

							if (!isEmpty(parseContentText))
								errorHandler(code, parseContentText,"paymentStep1");
							else
								errorHandler(code, "", "paymentStep1");

							break;

						case "1219":
							dfs.crd.pymt.messageAftrConfrm = errorCodeMap["1219"];
							errorHandler(code, dfs.crd.pymt.messageAftrConfrm,
							"paymentStep2");
							break;

						case "1228":
							var parseContentText	= errorCodeMap["1228"];
							if(projectBeyondCard)
								parseContentText = errorCodeMap["1228PB"];
							errorHandler(code, parseContentText, "paymentStep2");


							break;
						case "1229":
							var parseContentText	= errorCodeMap["1229"];
							if(projectBeyondCard)
								parseContentText = errorCodeMap["1229PB"];
							errorHandler(code, parseContentText, "paymentStep2");
							break;
						case "1230":
							var errorMsgData = getResponsErrorData(jqXHR);
							var nextAvailableDate ;

							if (!isEmpty(errorMsgData[0]))
								nextAvailableDate = errorMsgData[0].nextAvailableDate;

							var errorMessage = errorCodeMap["1230"];
							if(projectBeyondCard)
								errorMessage = errorCodeMap["1230PB"];

							var cutOff = [];
							if (!isEmpty(dataJSON.paymentAmount))
								cutOff["payAmount"] = dataJSON.paymentAmount;
							else
								cutOff["payAmount"] = "0.00";
							cutOff["rePostingDate"] = nextAvailableDate;
							cutOff["nextAvailableDate"] = formatPaymentDueDate_MakePaymentStep1(new Date(nextAvailableDate));
							var parseContentText = parseContent(errorMessage, cutOff);
							if (!isEmpty(parseContentText))
								$("#paymentStep2-pg").find(".wraper").html(parseContentText);
							else
								errorHandler(code, "", "paymentStep2");

							break;



						case "1227":
							var errorMsgData = getResponsErrorData(jqXHR);
							var bankName;
							var payAmount;
							var postingDate;
							var maskedAccNumber;
							var bankAccountDetails;
							if (!isEmpty(errorMsgData[0])) {
								bankName = dfs.crd.pymt.returnCorrectValue(errorMsgData, "bankName");
								payAmount = dfs.crd.pymt.returnCorrectValue(errorMsgData, "paymentAmount");
								postingDate = dfs.crd.pymt.returnCorrectValue(errorMsgData,"paymentDate");
								maskedAccNumber = dfs.crd.pymt.returnCorrectValue(errorMsgData,"maskedBankAccountNumber");
							}

							var errorMessage = errorCodeMap.Conflict_Payment;
							var payDetails = [];
							payDetails["errormessage"] = errorCodeMap["1227"]
							if (!isEmpty(bankName)&& !isEmpty(maskedAccNumber))
								bankAccountDetails = dfs.crd.pymt.truncateBankDetails(maskedAccNumber, bankName);
							if (!isEmpty(bankAccountDetails.bankName))
								payDetails["bankName"] = bankAccountDetails.bankName;
							else
								payDetails["bankName"] = "";
							if (!isEmpty(bankAccountDetails.accountNumber))
								payDetails["maskedAccNumber"] = bankAccountDetails.accountNumber;
							else
								payDetails["maskedAccNumber"] = "";
							payDetails["payAmount"] = payAmount;
							payDetails["postingDate"] = formatPostingDueDate_MakePaymentStep2(postingDate);
							var parseContentText = parseContent(errorMessage, payDetails);

							if (!isEmpty(parseContentText))
								errorHandler(code, parseContentText,
								"paymentStep1");
							else
								errorHandler(code, "", "paymentStep1");

							break;

						case "1226":
							var errorMsgData = getResponsErrorData(jqXHR);
							var bankName;
							var payAmount;
							var postingDate;
							var maskedAccNumber;
							var bankAccountDetails;
							if (!isEmpty(errorMsgData[0])) {
								bankName = dfs.crd.pymt.returnCorrectValue(errorMsgData, "bankName");
								payAmount = dfs.crd.pymt.returnCorrectValue(errorMsgData, "paymentAmount");
								postingDate = dfs.crd.pymt.returnCorrectValue(errorMsgData,"paymentDate");
								maskedAccNumber = dfs.crd.pymt.returnCorrectValue(errorMsgData,"maskedBankAccountNumber");
							}

							var errorMessage = errorCodeMap.Conflict_Payment;
							var payDetails = [];
							payDetails["errormessage"] = errorCodeMap["1226"];
							if (!isEmpty(bankName)
									&& !isEmpty(maskedAccNumber))
								bankAccountDetails = dfs.crd.pymt
								.truncateBankDetails(
										maskedAccNumber, bankName);
							if (!isEmpty(bankAccountDetails.bankName))
								payDetails["bankName"] = bankAccountDetails.bankName;
							else
								payDetails["bankName"] = "";
							if (!isEmpty(bankAccountDetails.accountNumber))
								payDetails["maskedAccNumber"] = bankAccountDetails.accountNumber;
							else
								payDetails["maskedAccNumber"] = "";
							payDetails["payAmount"] = payAmount;
							payDetails["postingDate"] = formatPostingDueDate_MakePaymentStep2(postingDate);
							var parseContentText = parseContent(
									errorMessage, payDetails);

							if (!isEmpty(parseContentText))
								errorHandler(code, parseContentText,
								"paymentStep1");
							else
								errorHandler(code, "", "paymentStep1");

							break;
						default:
							errorHandler(code, "", "paymentStep1");
						}
					}
				});
		return confirmdatastep3;
	} catch (err) {
		showSysException(err);
	}
}

dfs.crd.pymt.returnCorrectValue = function(errorMsgData, keyValue)
{
	for (i = 0; i < errorMsgData.length; i++) {
		var retievedObj = errorMsgData[i];
		var selectedValue = Object.keys(retievedObj);

		if (Object.keys(retievedObj) == keyValue)
			return retievedObj[selectedValue];
	}
}

/** ******************* Make Payment-3 ******************* */
function paymentStep3Load()
{
	try {
		var validPriorPagesOfpayStep3 = new Array("paymentStep2","pageError");
		if (jQuery.inArray(fromPageName, validPriorPagesOfpayStep3) > -1) {
			dfs.crd.pymt.populateMakePaymentthreeActivity("MAKEPAYMENTTHREE");
		}
		else {
			cpEvent.preventDefault();
			history.back();
		}
	} catch (err) {
		showSysException(err);
	}
}

dfs.crd.pymt.populateMakePaymentthreeActivity = function(pageName)
{
	try {
		var stepThree = getDataFromCache(pageName);

		if (!jQuery.isEmptyObject(stepThree))
			dfs.crd.pymt.populateConfirmthreeActivity(stepThree, pageName);

	} catch (err) {
		showSysException(err);
	}
}

dfs.crd.pymt.populateConfirmthreeActivity = function(stepThree, pageName)
{
	try {
		var newBankDetails = dfs.crd.pymt.truncateBankDetails(
				stepThree.maskedBankAccountNumber, stepThree.bankName);
		$("#stepthree_amount").text("$" + stepThree.paymentAmount);
		$("#stepthree_date").text(
				formatPostingDueDate_MakePaymentStep2(stepThree.paymentDate));
		$("#stepthree_bankname_type").text(newBankDetails.bankName);
		$("#stepthree_maskedbank_accountnumber").text(
				newBankDetails.accountNumber);
		$("#stepthree_confirmation_number").text(stepThree.confirmationNumber);
	} catch (err) {
		showSysException(err);
	}
}



/** ******************* truncate functions ******************* */
dfs.crd.pymt.truncateAccountNumber = function(title)
{
	var shortText = jQuery.trim(title).substring(dfs.crd.pymt.count(title), 20);
	return shortText;
}

dfs.crd.pymt.truncateBankName = function(title)
{
	var shortText = jQuery.trim(title).substring(0, 20) + "...";
	return shortText;
}

dfs.crd.pymt.count = function(title)
{
	var shortText = jQuery.trim(title).split('*').length - 1;
	return shortText;
}

function cancelPaymentLoad(){
      try{                         
            var validPriorPagesOfCancelPayment= new Array("paymentStep2","confirmCancelPayment");
            if(!(jQuery.inArray(fromPageName, validPriorPagesOfCancelPayment) > -1 )){         
                  cpEvent.preventDefault();
                  history.back();
            }              
      }catch(err){
            showSysException(err);
      }                
}
 
function confirmCancelPaymentLoad(){
      try{                         
            var validPriorPagesOfConfirmCancelPayment= new Array("cancelPayment");
            if(!(jQuery.inArray(fromPageName, validPriorPagesOfConfirmCancelPayment) > -1 )){         
                  cpEvent.preventDefault();
                  history.back();
            }              
			confirmCancelPaymentSCVariables();//passing sitecatalyst variable for Payment Cancellation Page - Complete
      }catch(err){
            showSysException(err);
      }
}

function paymentInformationLoad(){
 try{
		var staticContentJson=getStaticContentData(toPageName,false,false);
		var staticContentText="";		
		dfs.crd.pymt.prepareMAPStep1Data();
		var cardTypeTerm = "paymentTerm_"+incentiveTypeCode+"_"+incentiveCode;
		if(projectBeyondCard){
			staticContentText=staticContentJson["paymentTerm_ITCard"];
		}
		if(isEmpty(staticContentText)){
				var cardTypeTerm = "paymentTerm_"+incentiveTypeCode+"_"+incentiveCode;
				if(!jQuery.isEmptyObject(staticContentJson)){
				staticContentText = isEmpty(staticContentJson[cardTypeTerm])?(isEmpty(staticContentJson["paymentTerm_"+incentiveTypeCode])?staticContentJson["paymentTerm"]:staticContentJson["paymentTerm_"+incentiveTypeCode]):staticContentJson[cardTypeTerm];			
			}
		}
			$("#paymentsTermsOfUse").html(staticContentText);
	}catch(err){
		showSysException(err);
	}
}

dfs.crd.pymt.MAPStep1PayWarnClick = function(){
	var MAPStep1Data = getDataFromCache("MAKEPAYMENTONE");
	if(!isEmpty(MAPStep1Data)){
	 dfs.crd.pymt.prepareMAPStep1Data();
	 var lateMinPayWarn1Data = dfs.crd.pymt.getPaymentWarningData("LatePayWarnMAPStep1");
   	if(MAPStep1Data.currentBalance > 0){
		if(!lateMinPayWarn1Data.needPaymentWarning){
			navigation("lateMinPayWarnNoMinPay");
		}else{
			navigation("lateMinPayWarn1");
		}
      }
   	}
}

function lateMinPayWarnNoMinPayLoad(){
      try{                         
		 
	      var lateMinPayWarn1Data = dfs.crd.pymt.getPaymentWarningData("LatePayWarnMAPStep1");
		    if(!isEmpty(lateMinPayWarn1Data)){
				dfs.crd.pymt.populatePayWarningPage(lateMinPayWarn1Data,false);            	
		    }
      }catch(err){
            showSysException(err);
      }                
} 

function lateMinPayWarn1Load(){
      try{                         
	  
      var lateMinPayWarn1Data = dfs.crd.pymt.getPaymentWarningData("LatePayWarnMAPStep1");
            if(!isEmpty(lateMinPayWarn1Data)){
				dfs.crd.pymt.populatePayWarningPage(lateMinPayWarn1Data,true);            	
            }
      }catch(err){
            showSysException(err);
      }                
}



dfs.crd.pymt.getPaymentWarningData = function(pageId){
try {
		var newDate = new Date();
		var PAYMENTSLATEWARN = RESTURL + "stmt/v1/paymentwarning?" + newDate
		+ "";
		 var pmtWarnData = getDataFromCache("LatePayWarnMAPStep1");
		if(!isEmpty(pmtWarnData)){
			return pmtWarnData;
		}
		console.log("Payment URL  : -  "+PAYMENTSLATEWARN)
		
		showSpinner();
		$.ajax(
				{
					type : "GET",
					url : PAYMENTSLATEWARN,
					async : false,
					dataType : "json",
					headers : prepareGetHeader(),
					success : function(responseData, status, jqXHR)
					{
						hideSpinner();
               if (!validateResponse(responseData,"paymentSummaryValidation")) // Pen Test Validation
               {
                errorHandler("SecurityTestFail","","");
               return;
               }
						pmtWarnData = responseData;
						putDataToCache(pageId, pmtWarnData);
					},
					error : function(jqXHR, textStatus, errorThrown)
					{
						hideSpinner();
						var code = getResponseStatusCode(jqXHR);
						errorHandler(code, "", "paymentStep1");
					}
				});
		return pmtWarnData;
	} catch (err) {
		showSysException(err);
	}
}

dfs.crd.pymt.populatePayWarningPage = function(payWarnData,showVariable){
	try{
		var MAPStep1Data = getDataFromCache("MAKEPAYMENTONE");
		if(!isEmpty(payWarnData)){
			console.log("PayWarning Data aprTitle"+payWarnData.aprTitle);
			console.log("PayWarning Data penaltyWarningCashAPR"+payWarnData.penaltyWarningCashAPR);
			console.log("PayWarning Data penaltyWarningAPRCode"+payWarnData.penaltyWarningAPRCode);
			console.log("PayWarning Data penaltyVariableFixedInd"+payWarnData.penaltyVariableFixedInd);
			console.log("PayWarning Data lateFeeWarningAmount"+payWarnData.lateFeeWarningAmount);
			console.log("PayWarning Data defaultTermSavingsAmount"+payWarnData.defaultTermSavingsAmount);
			if(showVariable){
			$("#lateFeeWarningAmount").text(payWarnData.lateFeeWarningAmount);
			$("#latePayAprRate").text(payWarnData.penaltyWarningMerchantAPR);
				if(payWarnData.isNegativeAmortization){
					$("#totalMonthsOrYears").text(payWarnData.totalMonthsOrYears);
					$("#totalAmountToPay").text(payWarnData.totalAmountToPay);
					$("#latePayWarnRow2").remove();
				}else if(payWarnData.needTwoRowWarning){
					$("#totalMonthsOrYears").text(payWarnData.totalMonthsOrYears);
					$("#totalAmountToPay").text(payWarnData.totalAmountToPay);
					$("#defaultTermsPaymentAmount").text(payWarnData.defaultTermsPaymentAmount);
					$("#defaultTermYears").text(payWarnData.defaultTermYears);
					$("#defaultTermTotalAmount").text(payWarnData.defaultTermTotalAmount);
					$("#defaultTermSavingsAmount").text(payWarnData.defaultTermSavingsAmount);
				}
			}else{
				$("#noMinPaylateFeeWarningAmount").text(payWarnData.lateFeeWarningAmount  );
				$("#noMinPaylatePayAprRate").text(payWarnData.penaltyWarningMerchantAPR);
			}
		}
	}catch(err){
       showSysException(err);
    }      
}

dfs.crd.pymt.prepareMAPStep1Data = function(){
try{
		var keyValue = $("#paymentStep1-pg").find(
			"#bankDropDownStepOne option:selected").val();
		var existingDate = $("#date-compare").val();
		var payDateValue = $("#datepicker-value").val();
		var stepone_value_radio = $("input:checked").text();
		var select_box = document.getElementById("bankDropDownStepOne");
		if(!isEmpty(select_box)){
			bankSelectFieldtitle = select_box[select_box.selectedIndex].text;
		}
		 var selectIndex;
	     var selectedRadioButtons = document.getElementsByName("radio-choice-1");
	     if(!isEmpty(selectedRadioButtons)){
	    	 var radioLength=selectedRadioButtons.length;
	    	 for(var i = 0; i < radioLength; i++) {
	    		 if(selectedRadioButtons[i].checked) {
	    			 selectIndex= selectedRadioButtons[i].value;
	
	    		 }
	    	 }
	     }
	    var  paymentStep1SeletecFields={};
		var payAmount = $("#minpaystepone_other").val();
		if ($("#radio-choice-1").attr('checked')) {
				stepone_value_radio = $("#minpaystepone_minpayment").text();
			}
			else if ($("#radio-choice-2").attr('checked')) {
				stepone_value_radio = $("#minpaystepone_laststatementbalance")
				.text();
			}else if ($("#radio-choice-3").attr('checked')) {
				stepone_value_radio = "$" + payAmount;
			}
	    if(!isEmpty(keyValue)){
	    	paymentStep1SeletecFields["BANKVAL"]=keyValue;
	    }
	    if(!isEmpty(bankSelectFieldtitle)){
			paymentStep1SeletecFields["BANKNAME"]=bankSelectFieldtitle;    
	    }
	    if(!isEmpty(stepone_value_radio)){
	    	paymentStep1SeletecFields["SELECTFIELDVAL"]=stepone_value_radio;
	    }
	    if(!isEmpty(payDateValue)){
	    	paymentStep1SeletecFields["PAYMENTPOSTINGDATE"]=payDateValue;
	    }
	    if(!isEmpty(selectIndex)){
	    	paymentStep1SeletecFields["SELECTEDINDEX"]=selectIndex;   
	    }
	    if(!isEmpty(existingDate)){
	    	paymentStep1SeletecFields["SELECTEDEXISTINGDATE"]=existingDate;
	    } 
		putDataToCache("OPTION_SELECTED_LINK_CLICK",paymentStep1SeletecFields);
		dfs.crd.pymt.isLinkClicked = true;
	}catch(err){
       showSysException(err);
    }         
}