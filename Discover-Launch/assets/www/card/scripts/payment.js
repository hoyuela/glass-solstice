
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
dfs.crd.pymt.pendingPaymentEdit = false;
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
		dfs.crd.pymt.getPaymentSummaryData(pageName);
		var pmtSummary = getDataFromCache(pageName);
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
		var paymentSummaryJSON ={
				"serviceURL" : PAYMENTSSUMMARYURL,
				"isASyncServiceCall" :false,
				"successHandler": "dfs.crd.pymt.getPaymentSummarySuccessHandler",
				"errorHandler" : "dfs.crd.pymt.populatePaymentSummaryErrorDivs"
		};
		dfs.crd.disnet.doServiceCall(paymentSummaryJSON);
	} catch (err) {
		showSysException(err);
	}
}

dfs.crd.pymt.getPaymentSummarySuccessHandler =function(pmtSummary, pageId)
{
	try {
		if(!isEmpty(pmtSummary)){
			putDataToCache("PAYMENTSUMMARY", pmtSummary);
		}
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
			inLineError.html("");
			/*if(payDataObj.isHaMode){
				$("#paySummary_HAMode_Div").removeClass("hidden");
				$("#paySummary_HAMode_Div p").html(errorCodeMap["Pay_func_unavail"]);
			}*/
			var currentBalanceVal= !isEmpty(currentBalance) ? numberWithCommas(currentBalance) : defaultValue;
			/* To show negative balance format */
			if(currentBalanceVal < 0){
				$("#paymentsSummary_currentBalance").html("-$"+ splitNegativeBalance(currentBalanceVal));
			}else{
				$("#paymentsSummary_currentBalance").text("$" + currentBalanceVal);	
			}			

			var statementBalanceVal= !isEmpty(statementBalance) ? numberWithCommas(statementBalance) : defaultValue;
			/* To show negative balance format */
			if(statementBalanceVal < 0){
				$("#paymentsSummary_statementBalance").html("-$"+ splitNegativeBalance(statementBalanceVal));
			}else{
				$("#paymentsSummary_statementBalance").text("$" + statementBalanceVal);
			}

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
$("#paymentStep1-pg").live("pagehide", function() {
	$("#paymentsSummary_Inline_Error").text("");
})
dfs.crd.pymt.populatePaymentSummaryErrorDivs = function(jqXHR){
	try {
		hideSpinner();
		var code=getResponseStatusCode(jqXHR);
		errorHandler(code, "", "paymentsSummary");			
	} catch (err) {
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
		dfs.crd.pymt.getPendingPaymentData(pageName);
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
		var pendingPaymentJSON ={
				"serviceURL" : PENDINGPAYMENTSURL,
				"isASyncServiceCall" :false,
				"successHandler": "dfs.crd.pymt.getPendingPaymentSuccessHandler",
				"errorHandler" : "dfs.crd.pymt.getPendingPaymentErrorHandler"
		};
		dfs.crd.disnet.doServiceCall(pendingPaymentJSON);

	} catch (err) {
		showSysException(err);
	}
}

dfs.crd.pymt.getPendingPaymentSuccessHandler = function(pendingTrans, pageId)
{
	try {
		if(!isEmpty(pendingTrans)){
			var pendingPymt = pendingTrans;
			putDataToCache("PENDINGPAYMENTS", pendingPymt);
			putDataToCache("pendingPaymentError",false);
			var pendingPymt = getDataFromCache("PENDINGPAYMENTS");
			if (!jQuery.isEmptyObject(pendingPymt))
				dfs.crd.pymt.populatePendingPaymentDivs(pendingPymt,pageId);

		}
	} catch (err) {
		showSysException(err);
	}
}

dfs.crd.pymt.getPendingPaymentErrorHandler = function(jqXHR){
	try {
		hideSpinner();
		var code=getResponseStatusCode(jqXHR);
//		errorHandler(code, "", "pendingPayments");
		putDataToCache("pendingPaymentError",true);
		dfs.crd.pymt.populatePendingPaymentDivs(errorCodeMap[code]);
	} catch (err) {
		showSysException(err);
	}
}
dfs.crd.pymt.populatePendingPaymentDivs = function(pendingTrans, pageId)
{
	try {
		if(getDataFromCache("pendingPaymentError")){
			$("#errorPendingPaymentDiv").html(pendingTrans);
		}else if(!isEmpty(pendingTrans)){
		
			var innerPenTransVal = pendingTrans["pendingPayments"];
			var pendingPaymentDataActivityText ="";
			pendingPaymentDataActivityText +="<ul class='transactionTable font12' data-role='listview' data-filter='false' data-filter-placeholder='Search these results'>";
			pendingPaymentDataActivityText +="<li><div class='ui-grid-b'>";
			pendingPaymentDataActivityText +="<div class='ui-block-a'> <span>Date</span> </div>";
			pendingPaymentDataActivityText +="<div class='ui-block-b'> <span>Method</span> </div>";
			pendingPaymentDataActivityText +="<div class='ui-block-c'> <span>Amount</span> </div>";
			pendingPaymentDataActivityText +="</div></li>"
					var pendingPaymentDataActivityMain = "";

			var HAMode = pendingTrans["isHaMode"];
			var htmlText = "";
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
					var pendingPaymentMethod  = innerPenTransVal[0].paymentMethod.toUpperCase();
					var payPendingDetails = [];
					var errorMessage                                         = errorCodeMap["pendingPayHA"];
					payPendingDetails["errorMessage"]                  = errorCodeMap["pendingPayErrMsg"];
					payPendingDetails["payAmount"]                     = paymentAmount;
					payPendingDetails["pendingPayDate"]                = formatPostingDueDate_MakePaymentStep2(pendingPaymentDate);
					payPendingDetails["pendingPayBank"]            = pendingBankName;
					payPendingDetails["pendingAccountNumber"]        = maskedAcctNumber;
					payPendingDetails["pendingConfmNumber"]    = innerPenTransVal[0].confirmationNumber;
					var parseContentText = parseContent(errorMessage, payPendingDetails);

					$("#errorPendingPaymentDiv").html(parseContentText);
					$("#pendingPayments_makeapaymentbutton").remove();
/*					var text="Pending payments may be edited or canceled until <card specific timing information>";
					$("#editPaymentMsg").text(text);*/
				}
				else {
					htmlText = "<br/>You have no pending payments.<br/>";
					/*htmlText = "<div class='noPayments'><img src='../../images/noPaymentsImg.png' width='133' height='151' /></div><p class='noPendingMsg'>You have no pending payments.</p><p class='small11pxfont' id='pendingPayments_onlinepaymentmethodmsg'></p>";*/
					$("#errorPendingPaymentDiv").html(htmlText);
				}
			}
			else{
				if (innerPenTransVal.length != 0) {
					var pendingPaymentDataActivityJSON = getPageContentMin("payments",
							"pendingPayments", "", "");


					for (i = 0; i < innerPenTransVal.length; i++) {
						var paymentPendingVal = [];
						var pendingDataDetail=innerPenTransVal[i];
						var pendingPaymentObj=JSON.stringify(pendingDataDetail);
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
						paymentPendingVal["paymentDate"] = innerPenTransVal[i].paymentDate;
						paymentPendingVal["paymentMethod"] = innerPenTransVal[i].paymentMethod.toUpperCase();

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
						paymentPendingVal["bank"] = truncatedBankName;
						paymentPendingVal["PendingPaymentObj"]= pendingPaymentObj;

						var pendingPaymentDataActivity = parseContent(
								pendingPaymentDataActivityJSON, paymentPendingVal);
						pendingPaymentDataActivityMain += pendingPaymentDataActivity;
/*						var text="Pending payments may be edited or canceled until <card specific timing information>";
						$("#editPaymentMsg").text(text);*/
					}

					pendingPaymentDataActivityText += pendingPaymentDataActivityMain;

					pendingPaymentDataActivityText += "</ul>";
					$("#pendingPayments_pendingList").html(
							pendingPaymentDataActivityText);
					$("#pendingPayments_makeapaymentbutton").remove();

				}
				else {
					htmlText = "<br/>You have no pending payments.<br/>";
					/*htmlText = "<div class='noPayments'><img src='../../images/noPaymentsImg.png' width='133' height='151' /></div><p class='noPendingMsg'>You have no pending payments.</p><p class='small11pxfont' id='pendingPayments_onlinepaymentmethodmsg'></p>";*/
					$("#errorPendingPaymentDiv").html(htmlText);
				}
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
		dfs.crd.pymt.getPaymentHistoryData(pageName);
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

		var paymentHistoryJSON ={
				"serviceURL" : PAYMENTSHISTORYURL,
				"isASyncServiceCall" :false,
				"successHandler": "dfs.crd.pymt.getPaymentHistorySuccessHandler",
				"errorHandler" : "dfs.crd.pymt.getPaymentHistoryErrorHandler"
		};
		dfs.crd.disnet.doServiceCall(paymentHistoryJSON);

	} catch (err) {
		showSysException(err);
	}
}

dfs.crd.pymt.getPaymentHistorySuccessHandler = function(paymentHistory, pageId)
{ 
	try {
		if(!isEmpty(paymentHistory)){
			var pendingPymt = paymentHistory;
			putDataToCache("PAYMENTHISTORY", paymentHistory);
			putDataToCache("paymentHistoryError",false);
			var paymentHistory = getDataFromCache("PAYMENTHISTORY");
			if (!jQuery.isEmptyObject(paymentHistory))
				dfs.crd.pymt.populatePaymentHistoryDivs(paymentHistory, pageId);

			
		}
	} catch (err) {
		showSysException(err);
	}
}

dfs.crd.pymt.getPaymentHistoryErrorHandler = function(jqXHR){
	try {
		hideSpinner();
		var code=getResponseStatusCode(jqXHR);
		putDataToCache("paymentHistoryError",true);
//		errorHandler(code, "", "paymentsHistory");
		dfs.crd.pymt.populatePaymentHistoryDivs(errorCodeMap[code]);
	} catch (err) {
		showSysException(err);
	}
}

dfs.crd.pymt.populatePaymentHistoryDivs = function(paymentHistory, pageId)
{
	try {
		if(getDataFromCache("paymentHistoryError")){
			$("#errorPaymentsHistoryDiv").html(paymentHistory);
		}else if(!isEmpty(paymentHistory)){

			var innerHisTransVal = paymentHistory["paymentHistory"];
			var paymentHistoryDataActivityText="";

			paymentHistoryDataActivityText +='<ul class="transactionTable font12" data-role="listview" data-filter="false" data-filter-placeholder="Search these results">';
			paymentHistoryDataActivityText +='<li class="">';
			paymentHistoryDataActivityText +='<div class="ui-grid-b">';
			paymentHistoryDataActivityText +='<div class="ui-block-a"> <span>Date</span> </div>';
			paymentHistoryDataActivityText +='<div class="ui-block-b"> <span>Method</span> </div>';
			paymentHistoryDataActivityText +='<div class="ui-block-c"> <span>Amount</span> </div>';
			paymentHistoryDataActivityText +='</div></li>';
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
					paymentHistoryVal["paymentMethod"] = innerHisTransVal[i].paymentMethod.toUpperCase();
					//var paymentHistoryDataActivityJSON = "<li class ='borderbottom'><div class='accountsearchresult'><div class='toprow1'>!~bank~!</div><div><div class='col1'>!~paymentDate~!</div><div class='col2'>!~paymentMethod~!</div><div class='col3'>$!~paymentAmount~!</div></div></div></li>";
					var paymentHistoryDataActivityJSON = "";
					paymentHistoryDataActivityJSON +="<li class='listLi'><div class='ui-grid-b'><div class='ui-block-a'> <span>!~paymentDate~!</span> </div><div class='ui-block-b'> <span>!~paymentMethod~!</span> </div><div class='ui-block-c'> <span class='greenValues'>$!~paymentAmount~!</span> </div><div class='ui-block-d '> <span>!~bank~!</span><span class='payArr'></span></div></div></li>";
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
				"paymentsHistory", "accountSummary", "cardHome","pageError","paymentsEligible","paymentStep3","confirmCancelPayment","confirmCancelPayment1","paymentInformation","lateMinPayWarn1","lateMinPayWarnNoMinPay");
		if (jQuery.inArray(fromPageName, validPriorPagesOfpayStep1) > -1  || isLhnNavigation) {
			var validPriorPagesforEdit = new Array("paymentStep2","paymentsEligible","paymentInformation","lateMinPayWarn1","lateMinPayWarnNoMinPay");
					
			if(isLhnNavigation){
//				if(fromPageName == "paymentStep2" && dfs.crd.pymt.pendingPaymentEdit){
					dfs.crd.pymt.pendingPaymentEdit = false;
					putDataToCache("editPaymentFailFlag", false);
					putDataToCache("editPaymentFailMsg","");
//				}
			}else if(dfs.crd.pymt.pendingPaymentEdit && (jQuery.inArray(fromPageName, validPriorPagesforEdit) == -1)){
				dfs.crd.pymt.pendingPaymentEdit = false;
			}

			if(getDataFromCache("editPaymentFailFlag")){
				console.log("FromPAge NAMe is given as ************ "+fromPageName)
				var validPriorPagesforFailEdit = new Array("paymentStep2","paymentInformation","lateMinPayWarn1","lateMinPayWarnNoMinPay");
				 if(jQuery.inArray(fromPageName, validPriorPagesforFailEdit) == -1){
					console.log("******We are here in the ErroCode returned from PaymenrtStep3"+fromPageName) ;
					putDataToCache("editPaymentFailFlag", false);
					putDataToCache("editPaymentFailMsg","");
				 }
			}
//			$(".rdoBtn").parents(".radioWrap").removeClass("radioSelected");
			//Site-catalyst 13.4 Implementation start
			if(dfs.crd.pymt.pendingPaymentEdit){
				var pendignPaymentEditData = getDataFromCache("pendingPageDetailData");
				if(!isEmpty(pendignPaymentEditData)){
					if(pendignPaymentEditData.SELECTEDINDEX == "choice-1"){
						dfs.crd.sct.onClickEligibleForEditReviewEditPage("MinimumDue");
					}else if(pendignPaymentEditData.SELECTEDINDEX == "choice-2"){
						dfs.crd.sct.onClickEligibleForEditReviewEditPage("LastStatement");
					}else{
						dfs.crd.sct.onClickEligibleForEditReviewEditPage("OtherBalance");
					}
				}
			}
			//Site-catalyst 13.4 Implementation end
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
			
			$(".date-picker, #datepicker-val").live("click", function()
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
			$(".mapcalendar#calendar").datepicker(/*13.3 global change 29/07/13 */
			        {
					    dayNamesMin:["Sun","Mon","Tue","Wed","Thu","Fri","Sat"],
//						hideIfNoPrevNext : true,
//						showOtherMonths : true,
						selectOtherMonths : false,
						constrainInput : true,
						onSelect : function(dateText, inst)
						{
							dfs.crd.pymt.paymentDateSet(dateText);
						},
						beforeShowDay : dfs.crd.pymt.enableSpecificDates
					});
      
			$("#minpaystepone_other").click(function() {
				minPayStepOne.val("");
				errorPaymentAmountExceed.html("");
				$('#minpaystepone_other').attr("placeholder", '');
				$("#radio-choice-3").attr("checked","checked");
				$("#radio-choice-3").change();
				$("input[type='radio']").checkboxradio("refresh"); 
				minPayStepOne.parent(".wrapperSpan").removeClass("errormsg");
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
						$("#alertImageSpan").css("display","block");
						$("#alertImageSpan").removeClass("alertGreyImage");
						$("#alertImageSpan").addClass("alertImage");
						$("#commonErrorInMakePaymentStepOneDiv").html(errorCodeMap["Update_HighLighted"]);
						errorPaymentAmountExceed.text(errorCodeMap["1207"]);
						errorPaymentAmountExceed.css("display","block");
						minPayStepOne.parent(".wrapperSpan").addClass('errormsg');
						$("#minpaystepone_other").val('');
						$('#minpaystepone_other').attr("placeholder", '0.00');
						return;
					}
					else if (!isEmpty(payAmount) && !dfs.crd.pymt.checkCurrency(payAmount)) 
					{
						$("#alertImageSpan").css("display","block");
						$("#alertImageSpan").removeClass("alertGreyImage");
						$("#alertImageSpan").addClass("alertImage");
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
				if((event.which > 47 && event.which < 58) || (event.which > 95 && event.which < 106)){
					if(isBankSelected /*&& isDateSelected*/){
						activebtn("makePaymentOneContinue");
						$("#makePaymentOneContinue").removeClass("paymtBtnDisable");
					}
				}else if(event.which == 190 || event.which == 110){
					if(minPayStepOne.val().indexOf('.') == -1){
						if(isBankSelected /*&& isDateSelected*/){
							activebtn("makePaymentOneContinue");
							$("#makePaymentOneContinue").removeClass("paymtBtnDisable");
						}
					}else{
						deactiveBtn("makePaymentOneContinue");
						$("#makePaymentOneContinue").addClass("paymtBtnDisable");
					}
				}else{
					deactiveBtn("makePaymentOneContinue");
					$("#makePaymentOneContinue").addClass("paymtBtnDisable");
				}
			});
			if(dfs.crd.pymt.pendingPaymentEdit){
				var paymentDetail = getDataFromCache("PENDINGPAYSLTDATA");
				if(!isEmpty(paymentDetail)){
					dfs.crd.pymt.populateMakePaymentPageDivs(paymentDetail, "PENDINGPAYSLTDATA");
				}else{
					dfs.crd.pymt.populateMakePaymentOne("MAKEPAYMENTONE");
				}
			}else{
				dfs.crd.pymt.populateMakePaymentOne("MAKEPAYMENTONE");
			}
			var duedate = new Date($("#minpaystepone_paymentduedate").text());
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
		dfs.crd.pymt.makePaymentStepOneAjaxCall(pageName);
		var stepOne = getDataFromCache(pageName);

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
$("#bankDropDownStepOne").live( "change", function(){
	dfs.crd.pymt.bankSelected();
});

dfs.crd.pymt.makePaymentStepOneAjaxCall = function(pageId)
{
	try {

		var newDate = new Date();
		var MAKEPAYMENTSTEPONEURL = RESTURL+"pymt/v1/makepayment?"+newDate+"" ;
		var stepOne = getDataFromCache(pageId);
		
		var MAPStep1JSON ={
				"serviceURL" : MAKEPAYMENTSTEPONEURL,
				"isASyncServiceCall" :false,
				"successHandler": "dfs.crd.pymt.mapStep1SuccessHandler",
				"errorHandler" : "dfs.crd.pymt.mapStep1ErrorHandler"
		};
		dfs.crd.disnet.doServiceCall(MAPStep1JSON);
	} catch (err) {
		showSysException(err);
	}
}

dfs.crd.pymt.mapStep1SuccessHandler = function(responseData){
 if (!validateResponse(responseData,"makeaPaymentstep1Validation"))      // Pen Test Validation
  {
  errorHandler("SecurityTestFail","","");
  return;
  }
	if(!isEmpty(responseData)){
		putDataToCache("MAKEPAYMENTONE", responseData);
	};							
}

dfs.crd.pymt.mapStep1ErrorHandler = function(jqXHR){
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

$("#paymentStep1-pg").live("pagebeforeshow", function() {
	if(fromPageName == "paymentStep2"){
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
			dfs.crd.pymt.paymentDateSet(selectedPostingDate);
/*			$("#datepicker-val").html(selectedPostingDate);
			$("#datepicker-val").css("display","block");
			$("#date-compare").val(selectedExistingDate);
			$("#datepicker-value").val(selectedPostingDate);
*/			activebtn("makePaymentOneContinue");
			$("#makePaymentOneContinue").removeClass("paymtBtnDisable");
		}
	}else if(dfs.crd.pymt.isLinkClicked){
		var payOnBackData = getDataFromCache("OPTION_SELECTED_LINK_CLICK");
		dfs.crd.pymt.isLinkClicked = false;
		if(!isEmpty(payOnBackData)){
			var selectedPostingDate=payOnBackData.PAYMENTPOSTINGDATE;
			var selectedFieldVal=payOnBackData.SELECTFIELDVAL;
			var selectedBankName=payOnBackData.BANKNAME;
		    var selectedExistingDate=payOnBackData.SELECTEDEXISTINGDATE; 	
			var selectedIndex=payOnBackData.SELECTEDINDEX;
			if(!isEmpty(selectedBankName)){
				$("#DD2.ui-btn-text").text(selectedBankName);
				dfs.crd.pymt.bankSelected();
			}
			if(!isEmpty(selectedPostingDate)){
/*				$("#datepicker-val").html(selectedPostingDate);
				$("#datepicker-val").css("display","block");
				$("#datepicker-value").val(selectedPostingDate);
				$("#date-compare").val(selectedExistingDate);*/
				dfs.crd.pymt.paymentDateSet(selectedPostingDate);
			}
			if(!isEmpty(selectedExistingDate)){
				$("#date-compare").val(selectedExistingDate);
			}
			
			
			if(!isEmpty(selectedFieldVal)){
				if(selectedFieldVal.indexOf("$") >= -1 &&  selectedFieldVal.indexOf(".00") >= -1){
					selectedFieldVal=selectedFieldVal.replace("$","");
				}
			}
			if(!isEmpty(selectedIndex)){
				if( selectedIndex == "choice-3"){
					$("#minpaystepone_other").val(selectedFieldVal);	
				}else{
					payOnBackData["SELECTFIELDVAL"]="";
				}
//				);
			}
			
			dfs.crd.pymt.changeDropDownLabel();
		}

	}else if(dfs.crd.pymt.pendingPaymentEdit){
		
		var pendingPaymentDetail = getDataFromCache("pendingPageDetailData");
		if(!isEmpty(pendingPaymentDetail)){
			var selectedPostingDate=pendingPaymentDetail.PAYMENTPOSTINGDATE;
			var selectedFieldVal=pendingPaymentDetail.SELECTFIELDVAL;
			var selectedBankName=pendingPaymentDetail.BANKNAME;
		    var selectedExistingDate=pendingPaymentDetail.SELECTEDEXISTINGDATE; 	
			var selectedIndex=pendingPaymentDetail.SELECTEDINDEX;
			if(!isEmpty(selectedBankName)){
				$("#DD2.ui-btn-text").text(selectedBankName);
				dfs.crd.pymt.bankSelected();
			}
			if(!isEmpty(selectedPostingDate)){
/*				$("#datepicker-val").html(selectedPostingDate);
				$("#datepicker-val").css("display","block");
				$("#datepicker-value").val(selectedPostingDate);
				$("#date-compare").val(selectedExistingDate);*/
				dfs.crd.pymt.paymentDateSet(selectedPostingDate);
			}
			if(!isEmpty(selectedExistingDate)){
				$("#date-compare").val(selectedExistingDate);
			}
			
			
			if(!isEmpty(selectedFieldVal)){
				if(selectedFieldVal.indexOf("$") >= -1 &&  selectedFieldVal.indexOf(".00") >= -1){
					selectedFieldVal=selectedFieldVal.replace("$","");
				}
			}
			if(!isEmpty(selectedIndex)){
				if( selectedIndex == "choice-3"){
					$("#minpaystepone_other").val(selectedFieldVal);	
				}else{
					pendingPaymentDetail["SELECTFIELDVAL"]="";
				}
//				);
			}
				
			dfs.crd.pymt.changeDropDownLabel();
			
		}
	}else{
		$(".rdoBtn").removeAttr("checked");
		$(".rdoBtn").checkboxradio("refresh");
	}
});


$("#paymentStep1-pg").live("pagecreate", function() {
	var paymentStep1SeletecFields = "";
	
	if(!isEmpty(getDataFromCache("OPTION_SELECTED_ON_MAP1"))){
		paymentStep1SeletecFields = getDataFromCache("OPTION_SELECTED_ON_MAP1");
	}else if(dfs.crd.pymt.pendingPaymentEdit){
		paymentStep1SeletecFields = getDataFromCache("pendingPageDetailData");
	}else if(dfs.crd.pymt.isLinkClicked){
		paymentStep1SeletecFields = getDataFromCache("OPTION_SELECTED_LINK_CLICK");
	}
	
	if(!jQuery.isEmptyObject(paymentStep1SeletecFields)){
		var selectedIndex=paymentStep1SeletecFields.SELECTEDINDEX; 
		if ( !isEmpty(selectedIndex)){
			console.log("Selected Index is given as :-"+selectedIndex)
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
				var paySummary 				= getDataFromCache("PAYMENTSUMMARY");
				
				if(isEmpty(paySummary)){
					dfs.crd.pymt.getPaymentSummaryData("PAYMENTSUMMARY");
					paySummary 				= getDataFromCache("PAYMENTSUMMARY");
				}
				
				if (!isEmpty(paySummary)) {
					if(!isEmpty(paySummary.openAmount)){
						dfs.crd.pymt.globalOpenAmount = paySummary.openAmount;
					}
				}
			}
			if(getDataFromCache("editPaymentFailFlag")){
				$("#alertImageSpan").css("display","block");
				$("#alertImageSpan").addClass("alertGreyImage");
				$("#amounterror").html(getDataFromCache("editPaymentFailMsg"));
				$("#amounterror").css("display","block");
			}
			if(dfs.crd.pymt.globalOpenAmount <0)
				dfs.crd.pymt.globalOpenAmount = dfs.crd.pymt.globalOpenAmount.substring(1);
			
				if (stepOne.isHaMode)
					$("#manage_Bank_Information").css('display', 'none');
				if (stepOne.isHaMode) {
					if(dfs.crd.pymt.pendingPaymentEdit){
						cpEvent.preventDefault();
						navigation("../payments/pendingPayments");
					}else{
                        $("#alertImageSpan").css("display","block");
						$("#alertImageSpan").removeClass("alertGreyImage");
						$("#alertImageSpan").addClass("alertImage");
						$("#ha_MakePaymentStepOne_Error").css("display","block");
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
				}
			
			

			globalLastStatementBalance = stepOne.lastStatementBalance;
			var paymentBankInfo = stepOne.bankInfo;
			if(dfs.crd.pymt.pendingPaymentEdit){
				paymentBankInfo = getDataFromCache("PENDINGPAYSLTDATA").editBankInfo;
			}
			
			if (!isEmpty(paymentBankInfo) && paymentBankInfo.length > 0) {
				var bankInformation = stepOne.bankInfo;
				if(dfs.crd.pymt.pendingPaymentEdit){
					bankInformation = stepOne.editBankInfo;
				}
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
			console.log("The Bank Info to be added to drop down :- "+bank_name_info);
			var dropDownMAP1 = $("#bankDropDownStepOne").msDropdown().data("dd");
			dropDownMAP1.destroy();
			$("#bankDropDownStepOne").html("");
			$("#bankDropDownStepOne").html(bank_name_info);
			var dropDownMAP = $("#bankDropDownStepOne").msDropdown().data("dd");
			/* Hides JQM DD Important - Dont remove */
			$('div.dd.ddcommon.borderRadius').parent().find('a.ui-btn').remove();
			
			if (stepOne.daysDelinquent > 0){
				$("#alertImageSpan").css("display","block");
				$("#alertImageSpan").removeClass("alertGreyImage");
				$("#alertImageSpan").addClass("alertImage");
				commonErrorMAP1.append(errorCodeMap["Days_Delinquent"]);
			}	

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
				$("#alertImageSpan").css("display","block");
				$("#alertImageSpan").removeClass("alertGreyImage");
				$("#alertImageSpan").addClass("alertImage");
				commonErrorMAP1.html("\n" + parseContent(errMsg, overLimitAmt));
			}
			if (!isEmpty(paymentDueDate)) {
				var currentDate = new Date();
				var formatDate = new Date(paymentDueDate);
				if (stepOne.isAccountOverDue
						&& (parseFloat(minimumPayment) > 0.00)) {

					minPayStepOnePayDueDate.addClass("redtext boldtext");
					minPayStepOnePayDueDate.prev().addClass("payment_summary_due_date_red");
//					$(".errormsg").css("display", "block");
				}
				minPayStepOnePayDueDate
				.text(formatPaymentDueDate_MakePaymentStep1(formatDate));
				$(".mapcalendar#calendar").datepicker( "option", { minDueDate: new Date(dfs.crd.pymt.validDays[0]) } );/*13.3 global change 29/07/13 */
				$(".mapcalendar#calendar").datepicker( "option", { dueDate: paymentDueDate } );/*13.3 global change 29/07/13 */
			}
			if (parseFloat(stepOne.lastStatementBalance) > 0
					&& (parseFloat(stepOne.lastStatementBalance) < parseFloat(stepOne.currentBalance))) {
				$("#variable_lable").html("Last Statement Balance");
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
					$("#alertImageSpan").css("display","block");
					$("#alertImageSpan").removeClass("alertGreyImage");
					$("#alertImageSpan").addClass("alertImage");
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
		$(".dd").removeClass("ddHighlightError");
		$(".arrow").removeClass("ddHighlightErrorArrow");
		var isCutOffMakePaymentOne = stepOne.isCutOffAvailable;
		var selectedBankKey;
		var paymentStep1SeletecFields = getDataFromCache("OPTION_SELECTED_ON_MAP1");
			

		if(dfs.crd.pymt.isLinkClicked){
				paymentStep1SeletecFields = getDataFromCache("OPTION_SELECTED_LINK_CLICK");
		}else if(dfs.crd.pymt.pendingPaymentEdit){
			paymentStep1SeletecFields = getDataFromCache("pendingPageDetailData");
		}
		
		if(!jQuery.isEmptyObject(paymentStep1SeletecFields)){
			selectedBankKey=paymentStep1SeletecFields.BANKVAL;			
		}
		var calendar = $(".mapcalendar#calendar");/*13.3 global change 29/07/13 */
		var paymentBankInfo = stepOne.bankInfo;
		var pendingDataFromEditPage = getDataFromCache("PENDINGPAYSLTDATA");
		if(dfs.crd.pymt.pendingPaymentEdit && !isEmpty(pendingDataFromEditPage)){
			paymentBankInfo = pendingDataFromEditPage.editBankInfo;
		}
		
		
		var accountNumber=paymentBankInfo[0].maskedBankAcctNbr;
		var bankName = paymentBankInfo[0].bankName;
		var truncatedBankName=dfs.crd.pymt.truncateBankDetails(accountNumber,bankName);
		dfs.crd.pymt.selectvar = (paymentBankInfo.length == 1) ? paymentBankInfo[0].bankName : "Select a Bank Account";
		var fieldtext ="";
			fieldtext += "<option value='- Select Bank Account -'  selected='selected' >Select a Bank Account</option>";
		/* Hides JQM DD Important - Dont remove */
		$('div.dd.ddcommon.borderRadius').parent().find('a.ui-btn').remove();
		if (typeof stepOne != 'undefined') {
			var selected = paymentBankInfo.length == 1 ? "selected = 'selected'": '';			
			
			
			if (!isEmpty(paymentBankInfo)) {
				for ( var key in paymentBankInfo) {
					if (!isEmpty(paymentBankInfo[key])) {
						var truncatedBankName = dfs.crd.pymt.truncateBankDetails(
								paymentBankInfo[key].maskedBankAcctNbr,
								paymentBankInfo[key].bankName);
						var optionVal=paymentBankInfo[key].hashedBankAcctNbr + "," + paymentBankInfo[key].hashedBankRoutingNbr + "," + paymentBankInfo[key].bankShortName;
						
						if(!isEmpty(selectedBankKey)){							
							if(selectedBankKey == optionVal){
								selected="selected = 'selected'";
							}
						}
						var accountNumber = paymentBankInfo[key].maskedBankAcctNbr;

                        var accountNumber = jQuery.trim(accountNumber).split('*');
						accountNumber = accountNumber[accountNumber.length - 1];
						var selectBoxBankName = paymentBankInfo[key].bankName;
						if(isEmpty(selectBoxBankName) || selectBoxBankName ==
								"-"){
							selectBoxBankName = paymentBankInfo[key].bankShortName;
						}
						if(dfs.crd.pymt.pendingPaymentEdit){
							selectBoxBankName = isEmpty(paymentBankInfo[key].nickName)?paymentBankInfo[key].bankName:paymentBankInfo[key].nickName;
						}
						if(isEmpty(selectBoxBankName)){
							selectBoxBankName = " ";
						}
						console.log("Selected BAn k Name :- "+selectBoxBankName)
						fieldtext += "<option data-description=\"Account Ending in "+accountNumber+"\" value=\""
							+ optionVal
							+ "\"" + selected + ">"

							+ selectBoxBankName
							+ "</option>";
						selected='';
					}
				}
			}
			console.log("The Bank Selected Drop Down is :-"+fieldtext);
			if (!(stepOne.isHaMode) /*&& isCutOffMakePaymentOne*/) {
				if(projectBeyondCard)
					$("#cuttOff_Note_MakePaymentOne").text(errorCodeMap["Is_cutoffPB"]);
				else
					$("#cuttOff_Note_MakePaymentOne").text(errorCodeMap["Is_cutoff"]);
			}
			var openDates = paymentBankInfo[0].openDates;
			if (!isEmpty(openDates))
				dfs.crd.pymt.validDays = paymentBankInfo[0].openDates;
			
			if (paymentBankInfo.length == 1) {
				if (!isEmpty(dfs.crd.pymt.validDays)) {	
					calendar.datepicker("option", "minDate",
							dfs.crd.pymt.validDays[0]);
					calendar.datepicker(
							"option",
							"maxDate",
							dfs.crd.pymt.validDays[dfs.crd.pymt.validDays.length - 1]);
					
					var numberOfMonths = new Date(dfs.crd.pymt.validDays[dfs.crd.pymt.validDays.length - 1]).getMonth() - new Date(dfs.crd.pymt.validDays[0]).getMonth();	
					console.log("NUMBER OF MONTHS FOR THE USER is :- "+numberOfMonths);
					if(numberOfMonths <0){
						numberOfMonths = (12+numberOfMonths);
					}
					numberOfMonths = numberOfMonths+1;
					console.log("@@@@@NUMBER OF MONTHS FOR THE USER is :- "+numberOfMonths);
					calendar.datepicker( "option", "numberOfMonths", [ numberOfMonths, 1 ] );
						if(dfs.crd.pymt.pendingPaymentEdit && fromPageName != "paymentStep2"){
							calendar.datepicker( "setDate" , new Date(pendingDataFromEditPage.editPaymentDate));
							dfs.crd.pymt.paymentDateSet(pendingDataFromEditPage.editPaymentDate);
						}else{
							if(!isEmpty(getDataFromCache("OPTION_SELECTED_ON_MAP1"))){
								calendar.datepicker( "setDate" , new Date(getDataFromCache("OPTION_SELECTED_ON_MAP1").PAYMENTPOSTINGDATE));
								dfs.crd.pymt.paymentDateSet(getDataFromCache("OPTION_SELECTED_ON_MAP1").PAYMENTPOSTINGDATE);
							}else{
								calendar.datepicker( "setDate" , new Date(dfs.crd.pymt.validDays[0]));
								dfs.crd.pymt.paymentDateSet(dfs.crd.pymt.validDays[0]);
							}
						}
					}
					
					
				}
				$("#button-selectdate").removeClass('disabled');
		}
			return fieldtext;
		
	} catch (err) {
		showSysException(err);
	}
}

dfs.crd.pymt.bankSelected = function()
{
	try {
		var dateButton = $("#button-selectdate");
		var calendar = $(".mapcalendar#calendar");/*13.3 global change 29/07/13 */
		var bankDropDownStepOne = $("#bankDropDownStepOne");
		var errorPostingDate = $("#errorPostingDate");
		var commonErrorMap1 = $("#commonErrorInMakePaymentStepOneDiv");
		var errorMsgClass = $(".errormsg");
		$("#radio2Error").text("");
		$("#radio1Error").text("");
		$("#errorPaymentAmountExceed").text("");
		$("#minpaystepone_other").parent(".wrapperSpan").removeClass('errormsg');
		commonErrorMap1.html("");
		$(".dd").removeClass("ddHighlightError");
		$(".arrow").removeClass("ddHighlightErrorArrow");
		$("#errorbankinfoDiv").html("");
		if(isEmpty($("#ha_MakePaymentStepOne_Error").text()) && isEmpty($("#amounterror").text())){
			if($("#alertImageSpan").hasClass("alertGreyImage"))
				$("#alertImageSpan").removeClass("alertGreyImage");
			else if($("#alertImageSpan").hasClass("alertGreyImage"))
				$("#alertImageSpan").removeClass("alertImage");
		}

		errorPostingDate.text("");
		$("#datepicker-val").removeClass("redtext");
		$("#minpaystepone_paymentduedate").removeClass('errormsg');
		$("#minpaystepone_paymentduedate").prev().removeClass('errormsg');

		dateButton.removeClass("ui-btn-up-errormsg");
		dateButton.removeClass("disabled");
		bankDropDownStepOne.removeClass("ui-btn-up-errormsg");
		var stepOne = getDataFromCache("MAKEPAYMENTONE");
		if(dfs.crd.pymt.pendingPaymentEdit){
			stepOne = getDataFromCache("PENDINGPAYSLTDATA");
			stepOne['isHaMode'] = false;
		}
		if (!isEmpty(stepOne)) {
			dateButton.removeClass("disabled ui-btn-up-errormsg");
			var option = bankDropDownStepOne.find("option:selected");
			var optionValue = option.val();
			var newtxt = option.text();
			var paymentBankInfo = stepOne.bankInfo;
			if(dfs.crd.pymt.pendingPaymentEdit){
				paymentBankInfo = getDataFromCache("PENDINGPAYSLTDATA").editBankInfo;
			}
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
				else if (!isEmpty(paymentBankInfo[option.index() - 1].openDates)) {
					if (paymentBankInfo[option.index() - 1].openDates.length < 1) {
						$("#alertImageSpan").css("display","block");
						$("#alertImageSpan").removeClass("alertGreyImage");
						$("#alertImageSpan").addClass("alertImage");
						commonErrorMap1.html(errorCodeMap["Update_HighLighted"]);
						//errorMsgClass.css("display", "block");
						$("#errorbankinfoDiv").html(errorCodeMap["NoDates_for_Bank"]);
						$(".dd").addClass("ddHighlightError");
						$(".arrow").addClass("ddHighlightErrorArrow");
						$("#bankDropDownStepOne").addClass('errormsg');
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
						dfs.crd.pymt.validDays = paymentBankInfo[option.index() - 1].openDates;
						calendar.datepicker( "setDate" , new Date(dfs.crd.pymt.validDays[0]));
						dateButton.removeClass("disabled ui-btn-up-errormsg");
//						dfs.crd.pymt.paymentDateSet(dfs.crd.pymt.validDays[0]);
					}
					var existDate = $("#date-compare").val();
					if(!isEmpty(existDate)) {
						if (!(jQuery.inArray(existDate, dfs.crd.pymt.validDays) > -1)){
							var errorMessage = errorCodeMap["BAD_DATE"];
							$("#alertImageSpan").css("display","block");
							$("#alertImageSpan").removeClass("alertImage");
							$("#alertImageSpan").addClass("alertGreyImage");
							commonErrorMap1.html(errorMessage);
//							deactiveBtn("makePaymentOneContinue");
//							$("#makePaymentOneContinue").addClass("paymtBtnDisable");
							calendar.datepicker( "setDate" , new Date(dfs.crd.pymt.validDays[0]));
							$("#datepicker-value").val(dfs.crd.pymt.validDays[0]);
							$("#datepicker-val").val(dfs.crd.pymt.validDays[0]);
							$("#datepicker-val").css("margin","4px 0px");
							$("#datepicker-val").css("display","block");
							$("#date-compare").val(dfs.crd.pymt.validDays[0]);
							return ;
						}	                            
					}

				}
				if (!isEmpty(dfs.crd.pymt.validDays)) {
					calendar.datepicker("option", "minDate",
							dfs.crd.pymt.validDays[0]);
					calendar.datepicker("option","maxDate",dfs.crd.pymt.validDays[dfs.crd.pymt.validDays.length - 1]);
					var numberOfMonths = new Date(dfs.crd.pymt.validDays[dfs.crd.pymt.validDays.length - 1]).getMonth() - new Date(dfs.crd.pymt.validDays[0]).getMonth();	
					console.log("NUMBER OF MONTHS FOR THE USER is :- "+numberOfMonths);
					if(numberOfMonths <0){
						numberOfMonths = (12+numberOfMonths);
					}
					numberOfMonths = numberOfMonths+1;
					console.log("*****NUMBER OF MONTHS FOR THE USER is ****:- "+numberOfMonths);
					calendar.datepicker( "option", "numberOfMonths", [ numberOfMonths, 1 ] );
					if(dfs.crd.pymt.pendingPaymentEdit){
						var dataForPendingPay = getDataFromCache("PENDINGPAYSLTDATA");
						if(!isEmpty(dataForPendingPay)){
							calendar.datepicker( "setDate" , new Date(dataForPendingPay.editPaymentDate));
							//dfs.crd.pymt.paymentDateSet(paymentBankInfo.editPaymentDate);
						}
					}else{
						if(!isEmpty(getDataFromCache("OPTION_SELECTED_ON_MAP1"))){
							calendar.datepicker( "setDate" , new Date(getDataFromCache("OPTION_SELECTED_ON_MAP1").PAYMENTPOSTINGDATE));
							if(!isEmpty(getDataFromCache("OPTION_SELECTED_ON_MAP1").PAYMENTPOSTINGDATE))
							dfs.crd.pymt.paymentDateSet(getDataFromCache("OPTION_SELECTED_ON_MAP1").PAYMENTPOSTINGDATE);
						}else{
							calendar.datepicker( "setDate" , new Date(dfs.crd.pymt.validDays[0]));
							if(!isEmpty(dfs.crd.pymt.validDays[0]))
							dfs.crd.pymt.paymentDateSet(dfs.crd.pymt.validDays[0]);
						}//						dfs.crd.pymt.paymentDateSet(dfs.crd.pymt.validDays[0]);
					}
					
				}
			}else{
				dfs.crd.pymt.validDays = paymentBankInfo[option.index() - 1].openDates;
				if(!isEmpty(dfs.crd.pymt.validDays[0]))
				dfs.crd.pymt.paymentDateSet(dfs.crd.pymt.validDays[0]);
//				paymentBankInfo[option.index() - 1].openDates
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
		var stepOne = getDataFromCache("MAKEPAYMENTONE");
		if( paymentDate == "NaN/NaN/NaN"){
			paymentDate = stepOne.paymentDueDate;
		}
		var selectedDate = paymentDate.split("/");
		console.log("Selected  Date sent :- "+selectedDate);
		var month = selectedDate[0];
		var day = parseInt(selectedDate[1], 10);
		day = day.toString();
		if (day < 10)
			day = "0" + day;
		var formattedDate = month+"/"+day+"/" + selectedDate[2];
		$("#datepicker-value").val(formattedDate);
		$("#datepicker-val").val(formattedDate);
		$("#datepicker-val").css("margin","4px 0px");
		$("#datepicker-val").css("display","block");
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
		var userBankDetail = "";
		if(dfs.crd.pymt.pendingPaymentEdit){
			bankAccInfo = getDataFromCache("PENDINGPAYSLTDATA");
			userBankDetail = bankAccInfo.editBankInfo;
		}else{
			userBankDetail = bankAccInfo.bankInfo;
		}
		var isHaMode = false;
		if(!isEmpty(getDataFromCache("MAKEPAYMENTONE"))){
			isHaMode = getDataFromCache("MAKEPAYMENTONE").isHaMode;
		}
		var PCT_OVER = "1.1";
		var isCutOff = bankAccInfo.isCutOffAvailable;		
		var currValue 			= bankAccInfo.currentBalance;
		var option = $("#bankDropDownStepOne").find("option:selected")
		var bank = userBankDetail[option.index() - 1].bankName;
		var maskedAccountNumber = userBankDetail[option.index() - 1].maskedBankAcctNbr;
		var keyValue = option.val();
		var existingDate = $("#date-compare").val();
		var payDateValue = $("#datepicker-value").val();
		var stepone_value_radio = $("input:checked").text();
		var stepone_name_radio = $("input:checked").next().text();
		var paymentStep1SeletecFields = {};
		radio2Error.text("");
		$("#radio1Error").text("");
		$("#errorPaymentAmountExceed").text("");
		commonErrorMap1.text("");
		if(isEmpty($("#ha_MakePaymentStepOne_Error").text()) && isEmpty($("#amounterror").text())){
			$("#alertImageSpan").css("display","none");
			if($("#alertImageSpan").hasClass("alertGreyImage"))
				$("#alertImageSpan").removeClass("alertGreyImage");
			else if($("#alertImageSpan").hasClass("alertGreyImage"))
				$("#alertImageSpan").removeClass("alertImage");
		}
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
				$("#makePaymentOneContinue").removeClass("paymtBtnDisable");
				return;
			}
		}
		else if ($("#radio-choice-3").attr('checked')) {
			ischecked_radio = true;
			var value = currValue.substring(0);
			var payAmount = $("#minpaystepone_other").val();
			var pctvalue=parseFloat(PCT_OVER * value);
			
			if (isEmpty(payAmount)) {
				$("#alertImageSpan").css("display","block");
				$("#alertImageSpan").removeClass("alertGreyImage");
				$("#alertImageSpan").addClass("alertImage");
				commonErrorMap1.html(errorCodeMap["Update_HighLighted"]);
				$("#errorPaymentAmountExceed").text(errorCodeMap["1222"]);
				$("#minpaystepone_other").parent(".wrapperSpan").addClass('errormsg');
				$("#minpaystepone_other").val('');
				$('#minpaystepone_other').attr("placeholder", '0.00');

				return;
			}
			else if (!dfs.crd.pymt.checkCurrency(payAmount)) {
				$("#alertImageSpan").css("display","block");
				$("#alertImageSpan").removeClass("alertGreyImage");
				$("#alertImageSpan").addClass("alertImage");
				$("#commonErrorInMakePaymentStepOneDiv").html(
				errorCodeMap["Update_HighLighted"]);
				$("#minpaystepone_other").parent(".wrapperSpan").addClass('errormsg');
				$("#minpaystepone_other").val('');
				$('#minpaystepone_other').attr("placeholder", '0.00');

				return;
			}
			else if (parseFloat(payAmount) < 0.01
					|| parseFloat(payAmount) > 99999.99) {
				$("#alertImageSpan").css("display","block");
				$("#alertImageSpan").removeClass("alertGreyImage");
				$("#alertImageSpan").addClass("alertImage");
				commonErrorMap1.html(errorCodeMap["Update_HighLighted"]);
				$("#errorPaymentAmountExceed").text(errorCodeMap["1207"]);
				$("#minpaystepone_other").parent(".wrapperSpan").addClass('errormsg');
				$("#minpaystepone_other").val('');
				$('#minpaystepone_other').attr("placeholder", '0.00');

				return;
			}else if ((!bankAccInfo.haveSchedulePayments)
					&& (parseFloat(payAmount) > (PCT_OVER * parseFloat(value)))) {
				$("#alertImageSpan").css("display","block");
				$("#alertImageSpan").removeClass("alertGreyImage");
				$("#alertImageSpan").addClass("alertImage");
				commonErrorMap1.html(errorCodeMap["Update_HighLighted"]);
				var errMsg = errorCodeMap["1208"];
				var curntValue = [];
				curntValue["currentBalance"] = value;

				$("#errorPaymentAmountExceed").text(
						parseContent(errMsg, curntValue));
				$("#minpaystepone_other").parent(".wrapperSpan").addClass('errormsg');
				
				return;
			}else if ((dfs.crd.pymt.globalOpenAmount != "0.00") && parseFloat(payAmount) > (PCT_OVER * parseFloat(dfs.crd.pymt.globalOpenAmount))) 
			{
				$("#alertImageSpan").css("display","block");
				$("#alertImageSpan").removeClass("alertGreyImage");
				$("#alertImageSpan").addClass("alertImage");
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
			if ((userBankDetail[option.index() - 1].hashedBankAcctNbr != ("" || null))) {
				if ((userBankDetail[option.index() - 1].hashedBankRoutingNbr != ("" && null))) {
					if ((userBankDetail[option.index() - 1].bankShortName != ("" && null))) {
						isBankSelected = true;
					}
				}
			}
		}
		else {
			$("#alertImageSpan").css("display","block");
			$("#alertImageSpan").removeClass("alertGreyImage");
			$("#alertImageSpan").addClass("alertImage");
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
			$("#alertImageSpan").css("display","block");
			$("#alertImageSpan").removeClass("alertGreyImage");
			$("#alertImageSpan").addClass("alertImage");
			commonErrorMap1.html(errorTxt);
			$("#bankDropDownStepOne").addClass("ui-btn-up-errormsg");
		}
		else if (!validDate && (!bankAccInfo.isHaMode)) {
			var errorMessage = errorCodeMap["BAD_DATE"];
			$("#alertImageSpan").css("display","block");
			$("#alertImageSpan").removeClass("alertGreyImage");
			$("#alertImageSpan").addClass("alertImage");
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
		$("#minpaystepone_other").parent(".wrapperSpan").removeClass('errormsg');
		$("#commonErrorInMakePaymentStepOneDiv").html("");
		
		if(isEmpty($("#ha_MakePaymentStepOne_Error").text()) && isEmpty($("#amounterror").text())){
			$("#alertImageSpan").css("display","none");
			if($("#alertImageSpan").hasClass("alertGreyImage"))
				$("#alertImageSpan").removeClass("alertGreyImage");
			else if($("#alertImageSpan").hasClass("alertGreyImage"))
				$("#alertImageSpan").removeClass("alertImage");
		}
		var isBankSelected = false;
		var isDateSelected = false;
		var radioflag = false;
		var isOtherAmount = false;
		var ischecked_radio = false;
		var option = $("#bankDropDownStepOne").find("option:selected")

		if (option.index() != 0)
			isBankSelected = true;
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
				$("#makePaymentOneContinue").addClass("paymtBtnDisable");
			}
			else
			{
				activebtn("makePaymentOneContinue");
				$("#makePaymentOneContinue").removeClass("paymtBtnDisable");
			}
		}
		else {
			deactiveBtn("makePaymentOneContinue");
			$("#makePaymentOneContinue").addClass("paymtBtnDisable");
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
		"cancelPayment","paymentInformation");
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
//		console.log("MAKE PAY STEP @ DATA :- "+mapStepTwoData.isHAMode);
		var HASPAYMENTSURL 	= RESTURL + "pymt/v1/paymentverification?paymentDate=" + mapStepTwoData.PostingDate+"&isHaMode="+mapStepTwoData.isHAMode ;
		var payVerifyJSON = {
				"serviceURL" : HASPAYMENTSURL,
				"isASyncServiceCall" :false,
				"successHandler": "dfs.crd.pymt.getPaymentVerifSuccessHandler",
				"errorHandler" : "dfs.crd.pymt.getPaymentVerifErrorHandler"
		}
		dfs.crd.disnet.doServiceCall(payVerifyJSON);
	} catch (err) {
		showSysException(err);
	}

}

dfs.crd.pymt.getPaymentVerifSuccessHandler = function(responseData){
	try{
		hideSpinner();
	    if (!validateResponse(responseData,"paymentVerificationStep2Validation"))      // Pen Test Validation
	     {
	      errorHandler("SecurityTestFail","","");
	    return;
	     }
		putDataToCache("PAYSTEP2VERIFY",responseData);
	} catch (err) {
		showSysException(err);
	}
}

dfs.crd.pymt.getPaymentVerifErrorHandler = function(jqXHR){
	try{
		hideSpinner();
		cpEvent.preventDefault();
		var code = getResponseStatusCode(jqXHR);
		errorHandler(code, "", "paymentStep2");
	}catch (err) {
		showSysException(err);
	}
	
}

dfs.crd.pymt.populateMakePaymenttwoActivity = function(pageName)
{
	try {
		var steptwo = getDataFromCache(pageName);
		dfs.crd.pymt.getPaymentVerifiacation("PAYSTEP2VERIFY");
		var hasRecentPayment = getDataFromCache("PAYSTEP2VERIFY");
		if(dfs.crd.pymt.pendingPaymentEdit){
			if(!isEmpty(steptwo)){
				dfs.crd.sct.onClickEligibleForEditVerifyPage(steptwo.Amount);
			}
		}
		if (!jQuery.isEmptyObject(steptwo)) {
			var newBankDetails = dfs.crd.pymt.truncateBankDetails(steptwo.maskedAccountNumber, steptwo.BankName);
			var shortAccountText = jQuery.trim(steptwo.maskedAccountNumber).split('*');
			shortAccountText = shortAccountText[shortAccountText.length - 1];
			$("#bankname").text(newBankDetails.bankName);
			$("#accountnumber").text("Account Ending in "+shortAccountText);
			if(!isEmpty(hasRecentPayment)){
				if(hasRecentPayment.hasRecentActivity && !hasRecentPayment.isHaMode){
					$("#safeSecureDiv").text(errorCodeMap["safeSecure"]);
					$("#paymentStep2-pg .hidden").removeClass("hidden");
				}

				if (!hasRecentPayment.isHaMode  ) {
					if(projectBeyondCard){
						$("#cuttOff_Note").text(errorCodeMap["Is_cutoffPB"]);
					}else{
						$("#cuttOff_Note").text(errorCodeMap["Is_cutoff"]);
					}
				}

				steptwo.isHAMode = hasRecentPayment.isHaMode;
				putDataToCache("MAKEPAYMENTTWO", steptwo);
			}
			$("#paymentOption_value").text(steptwo.PaymentOption.trim());
			if (!isEmpty(steptwo.Amount))
				$("#amount_value").text(numberWithCommas(steptwo.Amount));
			else
				$("#amount_value").text("$ 0.00");
			$("#paymentStep2_posting_date").text(steptwo.PostingDate);
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
		if(dfs.crd.pymt.pendingPaymentEdit){
			var paymentSelectedData = getDataFromCache("selected_Pending_payments");
			if(!isEmpty(paymentSelectedData)){
				delete dataJSON["isHaMode"];
				dataJSON["sequenceNumber"] = paymentSelectedData.sequenceNumber;
			}
			MAKEPAYMENTTHREEPOSTCALLURL = RESTURL+ "pymt/v1/editpayment";
			dfs.crd.sct.onClickEligibleForEditVerifyConfmBtn();
		}
		
		var dataJSONString = JSON.stringify(dataJSON);
		killDataFromCache("OPTION_SELECTED_ON_MAP1");
		showSpinner();
		var payStep3SON = {
				"serviceURL" : MAKEPAYMENTTHREEPOSTCALLURL,
				"isASyncServiceCall" :false,
				"successHandler": "dfs.crd.pymt.payStep3ConfirmSuccessHandler",
				"errorHandler" : "dfs.crd.pymt.payStep3ConfirmErrorHandler",
				"serviceData":dataJSONString,
				"requestType":"POST"
		}
		putDataToCache("paymentStep3JsonData",dataJSON);
		dfs.crd.disnet.doServiceCall(payStep3SON);
	} catch (err) {
		showSysException(err);
	}
}

dfs.crd.pymt.payStep3ConfirmSuccessHandler = function(responseData){
try{
						
	hideSpinner();
	  if (!validateResponse(responseData,"paymentConfirmationstep3Validation"))      // Pen Test Validation
	  {
		 errorHandler("SecurityTestFail","","");
	  return;
	  }
				confirmdatastep3 = responseData;
				putDataToCache("MAKEPAYMENTTHREE", confirmdatastep3);
				navigation("../payments/paymentStep3",false);
				killDataFromCache("MAKEPAYMENTONE");
				killDataFromCache("PAYMENTSUMMARY");
				killDataFromCache("PENDINGPAYMENTS");
				killDataFromCache("PAYMENTHISTORY");
				killDataFromCache("ACHOME");
				killDataFromCache("pendingPageDetailData");
				killDataFromCache("PENDINGPAYSLTDATA");
				killDataFromCache("selected_Pending_payments");
				dfs.crd.pymt.pendingPaymentEdit = false;
				
				
	}catch (err) {
		showSysException(err);
	}
}

dfs.crd.pymt.payStep3ConfirmErrorHandler = function(jqXHR){
 try{
		hideSpinner();						
		var code = getResponseStatusCode(jqXHR);
		$("#confirmSuccessPay3").css('disabled', 'disable');
//		code = "1257";
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
                    if(dfs.crd.pymt.pendingPaymentEdit){
                           cpEvent.preventDefault();
                           navigation("../payments/pendingPayments");
                    }else{
                           errorHandler(code, dfs.crd.pymt.messageAftrConfrm,
                                        "paymentStep2");
                    }
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
			var dataJSON = getDataFromCache("paymentStep3JsonData");
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
		case "1258":
			var errorMsgData = getResponsErrorData(jqXHR);
			var bankName;
			var payAmount;
			var postingDate;
			var maskedAccNumber;
			var bankAccountDetails;
			var confirmationNumber;
			if (!isEmpty(errorMsgData[0])) {
				bankName = dfs.crd.pymt.returnCorrectValue(errorMsgData, "bankName");
				payAmount = dfs.crd.pymt.returnCorrectValue(errorMsgData, "paymentAmount");
				postingDate = dfs.crd.pymt.returnCorrectValue(errorMsgData,"paymentDate");
				maskedAccNumber = dfs.crd.pymt.returnCorrectValue(errorMsgData,"maskedBankAccountNumber");
				confirmationNumber = dfs.crd.pymt.returnCorrectValue(errorMsgData,"confirmationNumber");
			}

			var errorMessage = errorCodeMap.pendingPaymentConfltPay;
			var payDetails = [];
			payDetails["errormessage"] = errorCodeMap["1258"];
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
			payDetails["postingDate"] = postingDate;
			payDetails["confirmationNumber"] = confirmationNumber;
			var parseContentText = parseContent(
					errorMessage, payDetails);

			if (!isEmpty(parseContentText))
				errorHandler(code, parseContentText,
				"paymentStep1");
			else
				errorHandler(code, "", "paymentStep1");

			break;
		case "1259":
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
				confirmationNumber = dfs.crd.pymt.returnCorrectValue(errorMsgData,"confirmationNumber");
			}

			var errorMessage = errorCodeMap.pendingPaymentConfltPay;
			var payDetails = [];
			payDetails["errormessage"] = errorCodeMap["1259"];
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
			payDetails["postingDate"] = postingDate;
			payDetails["confirmationNumber"] = confirmationNumber;
			var parseContentText = parseContent(
					errorMessage, payDetails);

			if (!isEmpty(parseContentText))
				errorHandler(code, parseContentText,
				"paymentStep1");
			else
				errorHandler(code, "", "paymentStep1");

			break;
		case "1261":
			var errorMsgData = getResponsErrorData(jqXHR);
			var postingDate;
			if (!isEmpty(errorMsgData[0])) {
				postingDate = dfs.crd.pymt.returnCorrectValue(errorMsgData,"paymentDate");
			}

			var errorMessage = errorCodeMap["1261"];
			var payDetails = [];
			payDetails["changedPostingDate"] = postingDate;
			var parseContentText = parseContent(
					errorMessage, payDetails);
			cpEvent.preventDefault();
			putDataToCache("editPaymentFailFlag", true);
			putDataToCache("editPaymentFailMsg",parseContentText);
			navigation("paymentStep1");

			break;
		case "1257":
			var errorMsgData = getResponsErrorData(jqXHR);
			var errorMessage = errorCodeMap["1257"];
			if(projectBeyondCard)
				errorMessage = errorCodeMap["1257_PB"];
			if (!isEmpty(errorMessage))
				errorHandler(code, errorMessage,
				"paymentStep1");
			else
				errorHandler(code, "", "paymentStep1");

			break;

			default:
			errorHandler(code, "", "paymentStep1");
		}
	}catch (err) {
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
		var validPriorPagesOfpayStep3 = new Array("paymentStep2","pageError","paymentSaveToPhotos");
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
		$("#payStep3AmountValue").text("$" + stepThree.paymentAmount);
		$("#paymentStep3PostingDate").text(stepThree.paymentDate);
		var shortAccountText = jQuery.trim(stepThree.maskedBankAccountNumber).split('*');
		shortAccountText = shortAccountText[shortAccountText.length - 1];
		$("#accountEndingNum").text("Account Ending in "+shortAccountText);
		
		$("#payStep3BankDtl").text(newBankDetails.bankName);
		$("#paystep3Confirm").text(stepThree.confirmationNumber);
		if(dfs.crd.pymt.pendingPaymentEdit){
			$("#makePaymentEditConfirmBtn").show();
			$("#makePaymentFreshCall").hide();
		}else{
			$("#makePaymentEditConfirmBtn").hide();
			$("#makePaymentFreshCall").show();
		}
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
            var validPriorPagesOfCancelPayment= new Array("paymentStep2","confirmCancelPayment1");
            if(!(jQuery.inArray(fromPageName, validPriorPagesOfCancelPayment) > -1 )){         
                  cpEvent.preventDefault();
                  history.back();
            }              
      }catch(err){
            showSysException(err);
      }                
}
 
function confirmCancelPayment1Load(){
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
	if(dfs.crd.pymt.pendingPaymentEdit){
		MAPStep1Data = getDataFromCache("PENDINGPAYSLTDATA");
	}
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
				$("#latePayWarnPayDueDate").text(lateMinPayWarn1Data.paymentDueDate);
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
				$("#latePayWarnPayDueDate").text(lateMinPayWarn1Data.paymentDueDate);
            }
      }catch(err){
            showSysException(err);
      }                
}



dfs.crd.pymt.getPaymentWarningData = function(pageId){
try {
		var newDate = new Date();
		var PAYMENTSLATEWARN = RESTURL + "stmt/v2/paymentwarning?" + newDate
		+ "";
		 var pmtWarnData = getDataFromCache("LatePayWarnMAPStep1");
		if(!isEmpty(pmtWarnData)){
			return pmtWarnData;
		}
		
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
			if(incentiveCode == "000016" && incentiveTypeCode == "CBB"){
					var latePayWarnText='If we do not receive your minimum payment by the date listed above, you may have to pay a late fee of up to $'+payWarnData.lateFeeWarningAmount+".";
					$("#latePayWarnText").text(latePayWarnText);
					}
			if(showVariable){
			$("#lateFeeWarningAmount").text(payWarnData.lateFeeWarningAmount);
			$("#latePayAprRate").text(payWarnData.penaltyWarningMerchantAPR);
				if(payWarnData.isNegativeAmortization){
					$("#totalMonthsOrYears").text(payWarnData.totalMonthsOrYears);
					$("#totalAmountToPay").text(numberWithCommas(payWarnData.totalAmountToPay));
					$("#latePayWarnRow2").remove();
				}else
					{
					 $("#totalMonthsOrYears").text(payWarnData.totalMonthsOrYears);
					 $("#totalAmountToPay").text(numberWithCommas(payWarnData.totalAmountToPay));
					 if(payWarnData.needTwoRowWarning){						
						$("#defaultTermsPaymentAmount").text(payWarnData.defaultTermsPaymentAmount);
						$("#defaultTermYears").text(payWarnData.defaultTermYears);
						$("#defaultTermTotalAmount").text(numberWithCommas(payWarnData.defaultTermTotalAmount));
						$("#defaultTermSavingsAmount").text(numberWithCommas(payWarnData.defaultTermSavingsAmount));
					}else{
					$("#latePayWarnRow2").remove();
					}
				}
			}else{
				$("#noMinPaylateFeeWarningAmount").text(payWarnData.lateFeeWarningAmount);
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

/************pending Payment details**********/
dfs.crd.pymt.verifyPendingPayment = function(pendingPaySltdData){
	try{
		putDataToCache("selected_Pending_payments",pendingPaySltdData);

		if(!isEmpty(pendingPaySltdData)){
			if(!pendingPaySltdData.isAutoPayPayment){
				if(!pendingPaySltdData.isHaMode){
					if(pendingPaySltdData.paymentMethod == "Online"){
						if(projectBeyondCard){
							if(!(getDataFromCache("PENDINGPAYMENTS").isCutoffAvailable)){
								if((pendingPaySltdData.status == "Edit|Cancel" )|| (pendingPaySltdData.status == "Cancel")){
									navigation('../payments/paymentsEligible');		
								}else{
									navigation('../payments/paymentsNotEligible');
								}
							}else{
								navigation('../payments/paymentsNotEligible');
							}
						}else{
							if((pendingPaySltdData.status == "Edit|Cancel" )|| (pendingPaySltdData.status == "Cancel")){
								navigation('../payments/paymentsEligible');		
							}else{
								navigation('../payments/paymentsNotEligible');
							}
						}
					}else{
						navigation('../payments/paymentsNotEligible');
					}
				}else{
					navigation('../payments/paymentsNotEligible');
				}

			}else{
				navigation('../payments/paymentsNotEligible');

			}
		}
	}catch(err){
		showSysException(err);
	}   	
}

dfs.crd.pymt.verifyPendingPaymentData = function(pendingDataPage,sequenceNumber){
	try {
		var newDate = new Date();
		var PAYMENTVERIFICATION = RESTURL + "pymt/v1/editpayment?sequenceNumber="+sequenceNumber+ "";
		var pmtSummary ="";
		showSpinner();
		var payStep3SON = {
				"serviceURL" : PAYMENTVERIFICATION,
				"isASyncServiceCall" :false,
				"successHandler": "dfs.crd.pymt.verifyPendPaySuccessHandler",
				"errorHandler" : "dfs.crd.pymt.verifyPendPayErrorHandler",
		}
		dfs.crd.disnet.doServiceCall(payStep3SON);
	} catch (err) {
		showSysException(err);
	}
}

dfs.crd.pymt.verifyPendPaySuccessHandler = function(responseData){
try{
		hideSpinner();
	   if (!validateResponse(responseData,"paymentSummaryValidation")) // Pen Test Validation
	   {
		errorHandler("SecurityTestFail","","");
	   return;
	   }
	   if(!isEmpty(responseData)){
			pmtSummary = responseData;
			dfs.crd.pymt.pendingPaymentEdit = true;
			putDataToCache("PENDINGPAYSLTDATA",responseData);
		}
	} catch (err) {
		showSysException(err);
	}
}

dfs.crd.pymt.verifyPendPayErrorHandler = function(jqXHR){
try{
		hideSpinner();
		cpEvent.preventDefault();
		var code = getResponseStatusCode(jqXHR);
		errorHandler(code, "", "pendingPayment");
	} catch (err) {
		showSysException(err);
	}
}

function paymentsEligibleLoad(){
	var pendingPaymentDetailData=getDataFromCache("selected_Pending_payments");

	var validPriorPagesOfpaymentsEligible = new Array("pendingPayments","cancelPayment1Error","cancelPayment1","paymentStep1");
	if ((jQuery.inArray(fromPageName, validPriorPagesOfpaymentsEligible) > -1)) {
		if(!isEmpty(pendingPaymentDetailData)){
			dfs.crd.pymt.populatePendingDetailPageDivs(pendingPaymentDetailData,true);
		}
	}else{
		cpEvent.preventDefault();
		history.back();				
	}
	
}

function paymentsNotEligibleLoad(){
var pendingPaymentDetailData=getDataFromCache("selected_Pending_payments");
	if(!isEmpty(pendingPaymentDetailData)){
		dfs.crd.pymt.populatePendingDetailPageDivs(pendingPaymentDetailData,false);
	}
}


dfs.crd.pymt.populatePendingDetailPageDivs =function(pendingPaymentDetailData,isPayEditable)
{
try{	
			if(!isPayEditable){

		var confirmationNumber = pendingPaymentDetailData.confirmationNumber;
		var bankName = pendingPaymentDetailData.bankName;
		var shortAccountText = jQuery.trim( pendingPaymentDetailData.maskedBankAccountNumber).split('*');
		shortAccountText = shortAccountText[shortAccountText.length - 1];
		//var maskedBankAccountNumber = pendingPaymentDetailData.maskedBankAccountNumber;
		var scheduledSource = pendingPaymentDetailData.scheduledSource;
		var isAutoPayPayment = pendingPaymentDetailData.isAutoPayPayment;
		var paymentAmount = pendingPaymentDetailData.paymentAmount;
		var paymentDate = pendingPaymentDetailData.paymentDate;
		var paymentMethod = pendingPaymentDetailData.paymentMethod;
		var sequenceNumber = pendingPaymentDetailData.sequenceNumber;
		var status = pendingPaymentDetailData.status;
		var transactionDate = pendingPaymentDetailData.transactionDate;
		$('#ne_pendingPayBankName').text(bankName);
		$('#ne_paymentDate').text(paymentDate);
		if(!(isNaN(paymentAmount)))
			$('#ne_amount').text("$"+paymentAmount);
		else
			$('#ne_amount').text(paymentAmount);
		$('#ne_accountNo').text("Account Ending in "+shortAccountText);
		$('#ne_confirmationNumber').text(confirmationNumber);
		$('#ne_method').text(paymentMethod);

		
				if(pendingPaymentDetailData.isAutoPayPayment){		
					var htmlErrorText = "This payment can't be edited because it's an automatic payment.";
					$("#payNonEligibleAutoPay").html(htmlErrorText);
				}else if(pendingPaymentDetailData.paymentMethod != "Online"){
					var textNonOnline = "This Payment cannot be edited because it was not scheduled online.";
					$("#payNonEligibleNonOnlinePay").html(textNonOnline);
				}
		if(projectBeyondCard){
			$("#payNonEligibleCutOff").html(errorCodeMap["Is_cutoffPB"]);
		}else{
			$("#payNonEligibleCutOff").html(errorCodeMap["Is_cutoff"]);
		}


	}else{
			var pendingPaymentDetail=getDataFromCache("selected_Pending_payments");
			var confirmationNumber = pendingPaymentDetail.confirmationNumber;		
			var bankName = pendingPaymentDetailData.bankName;
			var paymentAmount = pendingPaymentDetailData.paymentAmount;
			var paymentDate = pendingPaymentDetailData.paymentDate;
			var paymentMethod = pendingPaymentDetail.paymentMethod;
			var sequenceNumber = pendingPaymentDetailData.sequenceNumber;
			var status = pendingPaymentDetailData.status;
			var transactionDate = pendingPaymentDetailData.transactionDate;
			var shortAccountText = jQuery.trim( pendingPaymentDetailData.maskedBankAccountNumber).split('*');
			shortAccountText = shortAccountText[shortAccountText.length - 1];
			//var maskedBankAccountNumber = pendingPaymentDetailData.maskedBankAccountNumber;
			$('#pendingPayMaskedAccNumber').text("Account Ending in "+shortAccountText);
			$('#paymentDate').text(paymentDate);
			$('#amount').text("$"+paymentAmount);
			$('#accountNo').text(bankName);
			$('#confirmationNumber').text(confirmationNumber);
			$('#method').text(paymentMethod);
			
			if(status=="Cancel"){
				$("#editBtn").addClass("hidden");
			}
		}
	}catch (err) {
		showSysException(err);
	}
}

dfs.crd.pymt.editPaymentToMAPStep1 = function(){
	try{	
		var pendingPaySltdData = getDataFromCache("selected_Pending_payments");
		dfs.crd.pymt.verifyPendingPaymentData("PENDINGPAYSLTDATA",pendingPaySltdData.sequenceNumber);
		var pendingPaymentDetail = getDataFromCache("PENDINGPAYSLTDATA");
		var pendingPaymentList = getDataFromCache("PENDINGPAYMENTS");
		var isAutoPayPayment = false;
		for(var i = 0;i<pendingPaymentList.length;i++){
			if(pendingPaySltdData.sequenceNumber == pendingPaymentList[i].sequenceNumber)
				isAutoPayPayment = pendingPaymentList[i].isAutoPayPayment;
		}
		var pendingPayDtlData = [];
		var conditionOne = false;
		var conditionTwo = false;
		if(!isEmpty(pendingPaymentDetail)){
			var selectedFieldVal = pendingPaymentDetail.editPaymentAmount;
			if(pendingPaymentList.length >1){
				pendingPayDtlData["SELECTEDINDEX"] = "choice-3";
			}else{
				 if(pendingPaymentDetail.minimumPayment <= 0 || pendingPaymentDetail.haveSchedulePayments || isAutoPayPayment){
					 conditionOne = true;
				 }
				 if(pendingPaymentDetail.haveSchedulePayments || isAutoPayPayment || pendingPaymentDetail.currentBalance < 0.01) {
					 conditionTwo = true;
				 }
				 if(conditionOne && conditionTwo){
					 pendingPayDtlData["SELECTEDINDEX"] = "choice-3";
				 }else if(conditionOne && !conditionTwo){
					if(selectedFieldVal == pendingPaymentDetail.currentBalance || selectedFieldVal == pendingPaymentDetail.lastStatementBalance){
							pendingPayDtlData["SELECTEDINDEX"] = "choice-2";
					}else{
							pendingPayDtlData["SELECTEDINDEX"] = "choice-3";
					}
				 }else if(!conditionOne && !conditionTwo){
					 if(selectedFieldVal == pendingPaymentDetail.minimumPayment){
							pendingPayDtlData["SELECTEDINDEX"] = "choice-1";
						}else if(selectedFieldVal == pendingPaymentDetail.currentBalance || selectedFieldVal == pendingPaymentDetail.lastStatementBalance){
							pendingPayDtlData["SELECTEDINDEX"] = "choice-2";
						}else{
							pendingPayDtlData["SELECTEDINDEX"] = "choice-3";
						}
				 }else{
					 pendingPayDtlData["SELECTEDINDEX"] = "choice-3";
				 }
				
			}
			
			for(var i = 0;i<pendingPaymentDetail.editBankInfo.length;i++){
				var bankDetail = pendingPaymentDetail.editBankInfo[i];
				if(bankDetail.isSelectedBank){
					pendingPayDtlData["BANKNAME"] = isEmpty(bankDetail.nickName)?bankDetail.bankName:bankDetail.nickName;
					pendingPayDtlData["BANKVAL"] = bankDetail.hashedBankAcctNbr + "," + bankDetail.hashedBankRoutingNbr + "," + bankDetail.bankShortName;;
					break;
				}
			}
			pendingPayDtlData["SELECTFIELDVAL"] = selectedFieldVal;
			pendingPayDtlData["PAYMENTPOSTINGDATE"] = pendingPaymentDetail.editPaymentDate;
			pendingPayDtlData["SELECTEDEXISTINGDATE"] = pendingPaymentDetail.editPaymentDate;
			putDataToCache("pendingPageVerification", pendingPayDtlData);
//			delete pendingPaymentDetail["editBankInfo"];
//			pendingPaymentDetail["bankInfo"]=editBankDetails;
			putDataToCache("pendingPageDetailData", pendingPayDtlData);
			navigation("../payments/paymentStep1");
		}
	}catch(err){
		showSysException(err);
	}
}	
/******************Cancel Payament***************/

function cancelPayment1Load(){
	try{
		var validPriorPagesOfcancelPayment1 = new Array("paymentsEligible");
		if ((jQuery.inArray(fromPageName, validPriorPagesOfcancelPayment1) > -1)) {
			dfs.crd.pymt.populateCancelPayment1("CANCELPAYMENT1");
		}else{
			cpEvent.preventDefault();
			history.back();			
		}

	}catch(err) {
		showSysException(err);
	}
}

dfs.crd.pymt.populateCancelPayment1= function(pageName){
	try {
		dfs.crd.pymt.getCancelPayment1Data(pageName);
		var cancelPaymentData = getDataFromCache("CANCELPAYMENT1");
		if (!jQuery.isEmptyObject(cancelPaymentData))
			dfs.crd.pymt.populateCancelPayment1Divs(cancelPaymentData, pageName);

	} catch (err) {
		showSysException(err);
	}
}

dfs.crd.pymt.getCancelPayment1Data =function(pageName){

	try {
		var newDate = new Date();
		var pendingDetails = getDataFromCache("selected_Pending_payments");
		var sequenceNumber = pendingDetails.sequenceNumber;
		var CANCELPAYMENTURL = RESTURL + "pymt/v1/cancelpayment?" + newDate +"&sequenceNumber="+sequenceNumber
				+ "";
		var cancelPaymentData;

		var cancelPayment1JSON ={
				"serviceURL" : CANCELPAYMENTURL,
				"isASyncServiceCall" :false,
				"successHandler": "dfs.crd.pymt.getCancelPayment1SuccessHandler",
				"errorHandler" : "dfs.crd.pymt.populateCancelPayment1ErrorDivs"
		};
		dfs.crd.disnet.doServiceCall(cancelPayment1JSON);
	} catch (err) {
		showSysException(err);
	}
}

dfs.crd.pymt.getCancelPayment1SuccessHandler = function(cancelPaymentData, pageId)
{
	try {
		if(!isEmpty(cancelPaymentData)){
			putDataToCache("CANCELPAYMENT1", cancelPaymentData);
		}

	} catch (err) {
		showSysException(err);
	}
}

dfs.crd.pymt.populateCancelPayment1ErrorDivs = function(jqXHR){
	try{
		hideSpinner();
		var code=getResponseStatusCode(jqXHR);
		if(code =="1219")
		{
			cpEvent.preventDefault();
			navigation("../payments/pendingPayments");
		}else{
			cpEvent.preventDefault();
			errorHandler(code, "", "cancelPayment1");
		}
	}catch(err){
		showSysException(err);
	}
}
dfs.crd.pymt.populateCancelPayment1Divs= function(cancelPayObj, pageId){
                try{
                                if(!isEmpty(cancelPayObj))
                                {              
                                                var maskedBankAccountNumber = jQuery.trim(cancelPayObj.maskedBankAccountNumber).split('*');
                                                maskedBankAccountNumber = maskedBankAccountNumber[maskedBankAccountNumber.length - 1];
                                                var paymentDate =cancelPayObj.paymentDate;
                                                var bankName =cancelPayObj.bankName;
                                                var paymentAmount =cancelPayObj.paymentAmount;
                                }
                                $('#ne_accountNo').text("Account Ending in "+maskedBankAccountNumber);
                                $('#cancel_bankName').text(bankName);
                                $('#cancel_amount_value').text("$"+paymentAmount);
                                $('#cancel_paymentStep2_posting_date').text(paymentDate);
 
                }catch(err){
                                showSysException(err);
 
                }
}


/******************confirm Cancel Payment***************/

function confirmCancelPaymentLoad()
{
	try {
					
	}catch (err) {
		showSysException(err);
	}
}

dfs.crd.p2p.postConfirmCancel = function() 
{
	try {
		var pendingDetails = getDataFromCache("selected_Pending_payments");
		var sequenceNumber = pendingDetails.sequenceNumber;
		if (!isEmpty(sequenceNumber)) {
			var dataJSON = {
					"sequenceNumber" : "" + sequenceNumber + ""
			};
			var dataJSONString = JSON.stringify(dataJSON);
			var CONFIRMCANCELPAYMENTURL = RESTURL + "pymt/v1/cancelpayment";	

			var postConfirmCancelJSON ={
					"serviceURL" : CONFIRMCANCELPAYMENTURL,
					"isASyncServiceCall" :false,
					"requestType":"POST",
					"serviceData":dataJSONString,
					"successHandler": "dfs.crd.pymt.populatePostConfirmCancelDivs",
					"errorHandler" : "dfs.crd.pymt.populatePostConfirmCancelErrorDivs"
			};
			dfs.crd.disnet.doServiceCall(postConfirmCancelJSON);
		}
	} catch (err) {
		showSysException(err);
	}
}


dfs.crd.pymt.populatePostConfirmCancelDivs = function(responseData){
	try {
		if (!isEmpty(responseData)) {
			navigation("../payments/confirmCancelPayment", false);
		}
	} catch (err) {
		showSysException(err);
	}
}

dfs.crd.pymt.populatePostConfirmCancelErrorDivs = function(jqXHR){
	try {
		hideSpinner();
		var code=getResponseStatusCode(jqXHR);
		if(code == "1219"){
			var pendingPaymentsData=getDataFromCache("PENDINGPAYMENTS");
			var errorText  = errorCodeMap["1219_PAYMENT_HA_OUTAGE"];
				cpEvent.preventDefault();
				navigation("../payments/pendingPayments");
		}else if(code == "1256"){
			var errorMessage = errorCodeMap["1256"];
			if(projectBeyondCard){
				errorMessage = errorCodeMap["1256_PB"];
			}
			errorHandler(code,errorMessage, "confirmCancelPayment");
		}else{
			errorHandler(code, "", "confirmCancelPayment");
		} 
	}catch (err) {
		showSysException(err);
	}
}

function cancelPayment1ErrorLoad(){
	try{

		dfs.crd.pymt.populatecancelPayment1ErrorPageDivs();
	}catch (err) {
		showSysException(err);
	}
}

dfs.crd.pymt.populatecancelPayment1ErrorPageDivs = function(){
	try{
		if(projectBeyondCard){
			$("#CutoffMsg").html("Your payment was not canceled. We were unable to cancel this payment as your was not submitted before today's cut-off time.");

		}else{
			$("#CutoffMsg").html("Your payment was not canceled. We were unable to cancel this payment as your edits were not submitted before today's cut-off time of 5:00 p.m. Eastern Time.");
		}
	}catch (err) {
		showSysException(err);
	}
}
//Save To Photos Changes //

function paymentSaveToPhotosLoad(){
     try {
            var validPriorPagesOfSaveToPhotos = new Array("paymentStep3");
            if (jQuery.inArray(fromPageName, validPriorPagesOfSaveToPhotos) > -1) {
                   dfs.crd.pymt.populatPaymentSaveToPhotos("MAKEPAYMENTTHREE");
            }
            else {
                   cpEvent.preventDefault();
                   history.back();
            }
            
            
     }catch (err){
            showSysException(err);
            
     }
     
}

dfs.crd.pymt.populatPaymentSaveToPhotos=function (pageName){
     
     try{
     var saveToPhotosPageData = getDataFromCache(pageName);
     if (!jQuery.isEmptyObject(saveToPhotosPageData))
            dfs.crd.pymt.populateConfirmSaveToPhotos(saveToPhotosPageData, pageName);
     }catch (err) {
            showSysException(err);
            
     }
     
}

dfs.crd.pymt.populateConfirmSaveToPhotos = function(saveTophotosData, pageName)
{
     try {
            var newBankDetails = dfs.crd.pymt.truncateBankDetails(
                         saveTophotosData.maskedBankAccountNumber, saveTophotosData.bankName);
            $("#savePaymentPhotosConfirmationNumber").text(saveTophotosData.confirmationNumber);
            $("#savePaymentPhotosAmount").text("$" + saveTophotosData.paymentAmount);
            $("#savePaymentPhotosPosting_date").text(saveTophotosData.paymentDate);
            $("#savePaymentPhotosAccountEndingNum").text(newBankDetails.accountNumber);
            $("#savePaymentPhotosBankDetail").text(newBankDetails.bankName);
            
     } catch (err) {
            showSysException(err);
     }
}



function ClickPaymentPhoto()
{
     try {
     Screenshot.prototype.takeScreenshot(function success() {}, null);
     history.back();
     }catch (err){
            
            showSysException(err);
     }
}


