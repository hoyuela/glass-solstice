/*
* Includes code for AJAX calls, page population and business logic for Manage bank accounts module (13.4)
*
*/

/** Name Space* */
dfs.crd.pymt = dfs.crd.pymt || {};
/** * */

/** Global variable* */
var fromConfirmationPage = false;
var addBankAccDataPosted = false;
/** * */
/*******start code for populating bank accounts list - Brainy*******/

function manageBankAccountsLoad(){
try{
		//In order to display the account list
		$(document).jqmData("editFlag",false); //to reset the edit bank account flag to default
		dfs.crd.pymt.getManageBankAccountsList();
		accountsList = getDataFromCache("MANAGE_BANK_ACCOUNT_LIST");
		if(!jQuery.isEmptyObject(accountsList))	{
		dfs.crd.pymt.populateManageBankAccounts(accountsList);
		}
	}catch(err){
		showSysException(err);
	}
}

dfs.crd.pymt.populateManageBankAccounts = function(accountsList){
try{
		if(!jQuery.isEmptyObject(accountsList))	{
			if(accountsList.banks.length != 0)	{ 
			var badAccountsAlertValue = accountsList.badAccountsAlert;
				if(badAccountsAlertValue == "none")	{
					//In case of no bad accounts
					dfs.crd.pymt.populateManageBankAccountsPageDivs(accountsList);
					$(".errorText").hide();
				}else if(badAccountsAlertValue == "few"){
					//In case of few bad accounts
					dfs.crd.pymt.populateManageBankAccountsPageDivs(accountsList);
					$("#globalAlert1").html("Some of your bank accounts for Online Payments have an issue. To make an online payment using these accounts they must be updated first.");
				}else if(badAccountsAlertValue == "all"){
					//In case of all bad accounts
					dfs.crd.pymt.populateManageBankAccountsPageDivs(accountsList);
					$("#globalAlert1").html("All of your bank accounts for Online Payments have an issue. To make an online payment you must update at least one of these bank accounts.");
				}
			}else{
				//navigate to NO accounts set up page if no accounts are available
				cpEvent.preventDefault();
				navigation('../payments/manageBankAccNoAcc');
			}
		}
	}catch(err){
		showSysException(err);
	}
}

dfs.crd.pymt.getManageBankAccountsList = function(){
try{
		var newDate = new Date();
		var MANAGE_BANK_ACCOUNT_URL = RESTURL + "pymt/v1/banks?"+newDate;
			showSpinner();
			var localJSON = {
					"serviceURL": MANAGE_BANK_ACCOUNT_URL,
					"successHandler": "dfs.crd.pymt.getManageBankAccountsListSuccessHandler",
					"errorHandler":"dfs.crd.pymt.getManageBankAccountsListErrorHandler",
					"isASyncServiceCall": false			
			};
			dfs.crd.disnet.doServiceCall(localJSON);
	}catch(err){
		showSysException(err);
	}
}

dfs.crd.pymt.getManageBankAccountsListSuccessHandler = function(responseData) {
try{
		hideSpinner();
		var accountsList = responseData;
		console.log("accountsList: "+JSON.stringify(accountsList));
		putDataToCache("MANAGE_BANK_ACCOUNT_LIST", accountsList);
	}catch(err)	{
		showSysException(err);
	}	
}

dfs.crd.pymt.getManageBankAccountsListErrorHandler = function(jqXHR) {
try{
		hideSpinner();
		var code=getResponseStatusCode(jqXHR);
		if(code == "1219"){
			cpEvent.preventDefault();
			navigation('../payments/manageBankAccHaMode');
			featuresUnavailableSCVariables();
		}else{
			errorHandler(code,'','paymentsHistory');
		}
	}catch(err)	{
		showSysException(err);
	}
}

dfs.crd.pymt.populateManageBankAccountsPageDivs = function(accountsList) {
try{
		var accountDetailsList = "";
		var bankList = accountsList.banks;
			for(bankElement in bankList){
				var bankElementInfo = bankList[bankElement];
				accntEndingNum = bankElementInfo.last4BankAcctNbr;
				accntNickName = bankElementInfo.nickName;
				bankName = bankElementInfo.bankName;
				accountType = bankElementInfo.accountType;
				var accountSelected= bankElementInfo;
				var userAccount=JSON.stringify(accountSelected);
				if(accntNickName == "" || isEmpty(accntNickName)){
					if(bankName == "" || isEmpty(bankName))
							bankName = "N/A";
					accntNickName = bankName;
				}
				accountsListData="";
				var	accountsListData =	"<li onclick='dfs.crd.pymt.displaySelectedBankAccountDetails("+userAccount+");'><div class='ui-grid-a'><div class='ui-block-a'><span class='accEnding'>"+accountType+" Ending in  " 
				+ accntEndingNum+"</span><span class='accName'>"+ accntNickName
				+ "</span></div><div class='ui-block-b'><a href='#'></a></div></div></li>";
				
				accountDetailsList += accountsListData;					
				}
		$("#bank_accounts").html(accountDetailsList);
		if(!accountsList.addAnotherBank)
			$("#addAcctBtn").hide();
		else
			$("#addAcctBtn").show();
	}catch(err){
		showSysException(err);
	}
}
/*******end code for populating bank accounts list - Brainy*******/

/**** start code for Account Details - Brainy*******/
dfs.crd.pymt.displaySelectedBankAccountDetails = function(userAccount) {
try{
		//userAccount contains details of clicked account
		dfs.crd.pymt.getSelectedAccountDetail(userAccount);
	}catch(err){
		showSysException(err);
	}
}

dfs.crd.pymt.getSelectedAccountDetail = function(userAccount){
try{
		var newDate = new Date();
		var	BANK_DETAILS_URL = RESTURL+"pymt/v1/bankinfo?bankShortName="+userAccount.bankShortName
		+"&hashedBankAcctNbr="+userAccount.hashedBankAcctNbr+"&hashedBankRoutingNbr="+userAccount.hashedBankRoutingNbr ;

		showSpinner();
		var localJSON = {
				"serviceURL": BANK_DETAILS_URL,
				"successHandler": "dfs.crd.pymt.getSelectedAccountDetailSuccessHandler",
				"errorHandler":"dfs.crd.pymt.getSelectedAccountDetailErrorHandler",
				"isASyncServiceCall": false		
		}	
		dfs.crd.disnet.doServiceCall(localJSON);
	}catch(err){
		showSysException(err);
	}
}

dfs.crd.pymt.getSelectedAccountDetailSuccessHandler = function(responseData) {
try {
		hideSpinner();
		var accountDetails = responseData ;
		putDataToCache("SELECTED_BANK_ACCOUNT_SERVICE_RESPONSE",accountDetails );
		navigation('../payments/manageBankAccountsDetails');		
	}catch(err) {
		showSysException(err);
	}	
}

dfs.crd.pymt.getSelectedAccountDetailErrorHandler = function(jqXHR) {
try {
		hideSpinner();
		var code=getResponseStatusCode(jqXHR);
		if(code == "1219"){
			cpEvent.preventDefault();
			navigation('../payments/manageBankAccHaMode');
			featuresUnavailableSCVariables();
		}else{
			errorHandler(code,'','');
		}
	} catch (err) {
		showSysException(err);
	}
}

//To populate the accnt details on page
function manageBankAccountsDetailsLoad(){
try{
		var validPriorPages = new Array("manageBankAccConfirmDetails","updateComplete","manageBankAccounts",
		"removeComplete","removeAccountConfirmPending","removeAccountConfirmNoPending","manageBankAccUpdateDetails","moreLanding");
		if (jQuery.inArray(fromPageName, validPriorPages) > -1) {
			var accountDetails = getDataFromCache("SELECTED_BANK_ACCOUNT_SERVICE_RESPONSE");
			if(!jQuery.isEmptyObject(accountDetails)){
				dfs.crd.pymt.populateAccountDetailPageDivs();
			}else{
				cpEvent.preventDefault();
				navigation('../payments/manageBankAccounts');
			}
		}else {
			cpEvent.preventDefault();
			navigation('../payments/manageBankAccounts');
		}
	}catch(err){
		showSysException(err);
	}
}

dfs.crd.pymt.populateAccountDetailPageDivs  = function(){
try{
		var accountDetails = getDataFromCache("SELECTED_BANK_ACCOUNT_SERVICE_RESPONSE");
		var accntNickName = accountDetails.nickName;
		var hasPendingPayments = accountDetails.hasPendingPayments;

		if(accntNickName == "" || isEmpty(accntNickName)){
			accntNickName = "N/A";
		}	
		$("#editRouting").text(accountDetails.maskedBankRoutingNbr);
		$("#editAccount").text(accountDetails.maskedBankAcctNbr);
		$("#editAccType").text(accountDetails.accountType);
		$("#editAccNickname").text(accntNickName);

		//In order to show the message if account has pending payments	
		if (hasPendingPayments!=true){
			$(".pendingPaymentMsg").hide();	
		}else{
			$(".pendingPaymentMsg").show();	
		}
	}catch(err){
		showSysException(err);
	}
}
/**** end code for Account Details - Brainy*******/

/**** Add/Edit Bank Account Start - Kanika*******/
/***Following code makes the AJAX call to add bank account after it has been verified *** */

$("#addAcctBtn, #addBtnAddConfirm, #addBtnRemoveConfirm, #addBtnUpdateConfirm, #addBtnNoAcc").live("click", function(){
try {
		fromConfirmationPage = true;
	}catch (err) {
		showSysException(err);
	}
});

dfs.crd.pymt.addBankAccountPost = function(accountdetails){
try {
		var addBankAccountURL = RESTURL+ "pymt/v1/addbank";	
		if(!isEmpty(accountdetails)){
			var dataJSONString = JSON.stringify(accountdetails);
			var localArray ={
					"serviceURL" : addBankAccountURL,
					"successHandler" : "dfs.crd.pymt.addBankAccountPostSuccessHandler",
					"errorHandler" : "dfs.crd.pymt.addEditErrorHandler",
					"requestType" : "POST",
					"serviceData" : dataJSONString
			};
			dfs.crd.disnet.doServiceCall(localArray);
			killDataFromCache('MANAGE_BANK_ACCOUNT_LIST');
			killDataFromCache('POST_ADD_UPDATED_ACCOUNT_LIST');
		}
	}catch(err) {
		showSysException(err);
	}
}

/***Success Handler for add bank account AJAX call *** */
dfs.crd.pymt.addBankAccountPostSuccessHandler = function(responseData){
try {
		putDataToCache("POST_ADD_UPDATED_ACCOUNT_LIST",responseData);
		addBankAccDataPosted = true;
		navigation("../payments/manageBankAccConfirmDetails");
	}catch (err) {
		showSysException(err);
	}
}

/***Following code makes the AJAX call to verify bank account *** */
dfs.crd.pymt.verifyBankAccountPost = function(accountdetails/*, isEditBankAccount*/){
try {
		var verifyBankAccountURL = RESTURL+ "pymt/v1/verifybank";
		putDataToCache("VERIFY_BANK_ACCOUNT_REQUEST_DATA",accountdetails);
		var dataJSONString = JSON.stringify(accountdetails);
		console.log("verifyBankAccount dataJSONString"+dataJSONString);
		var localArray ={
				"serviceURL" : verifyBankAccountURL,
				"successHandler" : "dfs.crd.pymt.verifyBankAccountPostSuccessHandler",
				"errorHandler" : "dfs.crd.pymt.addEditErrorHandler",
				"requestType" : "POST",
				"serviceData" : dataJSONString
		};
		dfs.crd.disnet.doServiceCall(localArray);		
	}catch (err) {
		showSysException(err);
	}
}

/***Success Handler for verify bank account AJAX call *** */
dfs.crd.pymt.verifyBankAccountPostSuccessHandler = function(){
try {
		var accountdetails = getDataFromCache("VERIFY_BANK_ACCOUNT_REQUEST_DATA");
		console.log("accountdetails.isEditBankAccount"+accountdetails.isEditBankAccount);
		if(!jQuery.isEmptyObject(accountdetails)){
			if(!accountdetails.isEditBankAccount)
				dfs.crd.pymt.addBankAccountPost(accountdetails);
			else	
				dfs.crd.pymt.editBankAccountPost(accountdetails);	
		}
	}catch (err) {
		showSysException(err);
	}
}

/***Following code makes the AJAX call to edit bank account after it has been verified *** */
dfs.crd.pymt.editBankAccountPost = function(accountdetails){
try {
		var editBankAccountURL = RESTURL+ "pymt/v1/updatebank";	
		if(!isEmpty(accountdetails)){
			var dataJSONString = JSON.stringify(accountdetails);
			console.log("editBankAccount dataJSONString"+dataJSONString);
			var localArray ={
					"serviceURL" : editBankAccountURL,
					"successHandler" : "dfs.crd.pymt.editBankAccountPostSuccessHandler",
					"errorHandler" : "dfs.crd.pymt.addEditErrorHandler",
					"requestType" : "POST",
					"serviceData" : dataJSONString
			};
			dfs.crd.disnet.doServiceCall(localArray);
			
			killDataFromCache('MANAGE_BANK_ACCOUNT_LIST');
			killDataFromCache('SELECTED_BANK_ACCOUNT_SERVICE_RESPONSE');
			killDataFromCache('VERIFY_BANK_ACCOUNT_REQUEST_DATA');
			killDataFromCache('POST_EDIT_UPDATED_ACCOUNT_LIST');
		}
	} catch (err) {
		showSysException(err);
	}
}

/***Success Handler for edit bank account AJAX call *** */
dfs.crd.pymt.editBankAccountPostSuccessHandler = function(responseData){
try {
		var editBankAccountData = responseData;
		putDataToCache("POST_EDIT_UPDATED_ACCOUNT_LIST", editBankAccountData);
		console.log("BankAccount editlog: "+ JSON.stringify(editBankAccountData));
		navigation("../payments/updateComplete");	
	} catch (err) {
		showSysException(err);
	}
}

function manageBankAccUpdateDetailsLoad(){
try {
		var validPriorPages = new Array("manageBankAccountsDetails","moreLanding");
		if (jQuery.inArray(fromPageName, validPriorPages) > -1) {
			if($(document).jqmData("editFlag") == true){
				//if the acc exists and user wants to edit account
				var bankInfoData = getDataFromCache("SELECTED_BANK_ACCOUNT_SERVICE_RESPONSE");
				if(!jQuery.isEmptyObject(bankInfoData)){
					$("#routing").val(bankInfoData.maskedBankRoutingNbr);
					$("#accountNumber,#confirmAccountNumber").val(bankInfoData.maskedBankAcctNbr);					
					$('select option[value='+bankInfoData.accountType+']').attr("selected", "selected");
					$(".ddTitleText").find('.ddlabel').text($('select option[value='+bankInfoData.accountType+']').text());					
					$("#accountNickname").val(bankInfoData.nickName);
				}
			}
		}else {
			cpEvent.preventDefault();
			history.back();
		}
	}catch(err) {
		showSysException(err);
	}
}

function manageBankAccEnterDetailsLoad(){
try {
		var invalidPriorPages = new Array("manageBankAccEnterDetails","manageBankAccUpdateDetails","removeAccountConfirmPending",
		"removeAccountConfirmNoPending","manageBankAccountsDetails");
		if ((!fromConfirmationPage) || jQuery.inArray(fromPageName, invalidPriorPages) > -1) {
			cpEvent.preventDefault();
			navigation('../payments/manageBankAccounts');
		}
		fromConfirmationPage = false;
		var accountsList = getDataFromCache("MANAGE_BANK_ACCOUNT_LIST");
		if(!jQuery.isEmptyObject(accountsList)){
			dfs.crd.sct.addBankAccount(accountsList.badAccountsAlert);
		}
	}catch (err) {
		showSysException(err);
	}
}

function updateCompleteLoad(){
try {
		var validPriorPages = new Array("manageBankAccUpdateDetails","moreLanding");
		if (jQuery.inArray(fromPageName, validPriorPages) > -1) {
			var accountsList = getDataFromCache("POST_EDIT_UPDATED_ACCOUNT_LIST");
			if(!jQuery.isEmptyObject(accountsList)){
				var banksList = dfs.crd.pymt.populateConfirmationPage(accountsList, false);
				$("#edit_bank_account_list").html(banksList);
			}
		}else {
			cpEvent.preventDefault();
			navigation('../payments/manageBankAccounts');
		}
	}catch (err) {
		showSysException(err);
	}
}

function manageBankAccConfirmDetailsLoad(){
try {
		var validPriorPages = new Array("manageBankAccEnterDetails","moreLanding");
		if (addBankAccDataPosted && (jQuery.inArray(fromPageName, validPriorPages) > -1)) {
			var accountsList = getDataFromCache("POST_ADD_UPDATED_ACCOUNT_LIST");
			addBankAccDataPosted = false;
			if(!jQuery.isEmptyObject(accountsList)){
				var banksList = dfs.crd.pymt.populateConfirmationPage(accountsList, false);
				$("#add_bank_account_list").html(banksList);
			}
		}else {
			cpEvent.preventDefault();
			navigation('../payments/manageBankAccounts');
		}
	}catch(err) {
		showSysException(err);
	}
}

/***Following code populates confirmation page after add/edit/remove bank account AJAX call *** */
dfs.crd.pymt.populateConfirmationPage = function(accountsList,isFromRemovalPage){
try {
		var getBankAccountData = accountsList;
		console.log("getBankAccountData "+JSON.stringify(getBankAccountData));
		if(getBankAccountData.banks.length > 0){
			var retrievedBankList = getBankAccountData.banks;
			var banksList = "";
			var banksListData = "";
			for(bankElement in retrievedBankList){
						var bankElementInfo = retrievedBankList[bankElement];
						var accountType = bankElementInfo.accountType;
						var last4BankAcctNbr = bankElementInfo.last4BankAcctNbr;
						var nickName = bankElementInfo.nickName;
						var bankName = bankElementInfo.bankName;
						var getBankDetailsData = bankElementInfo;
						var getBankDetailsDataJSONString = JSON.stringify(getBankDetailsData);
						if(nickName == "" || isEmpty(nickName)){
							if(bankName == "" || isEmpty(bankName))
								bankName="N/A";
							nickName = bankName;
						}
						if(bankElement == 0 && (!isFromRemovalPage)){
						banksListData = "<li onclick='dfs.crd.pymt.displaySelectedBankAccountDetails("+ getBankDetailsDataJSONString
						+ ")'><span>"+accountType+" Ending in "+last4BankAcctNbr+
						" </span><br/><span class='amt_bold addUpdate' id='amount_value'>"+nickName+
						"</span><span class='payArr'></span></li>";
						banksList += banksListData;
						}else{
						banksListData = "<li onclick='dfs.crd.pymt.displaySelectedBankAccountDetails("+ getBankDetailsDataJSONString
						+ ")'><span>"+accountType+" Ending in "+last4BankAcctNbr+
						" </span><br/><span class='amt_bold' id='amount_value'>"+nickName+
						"</span><span class='payArr'></span></li>";
						banksList += banksListData;
						}
					}
			$(document).jqmData("editFlag",false); //to reset the edit bank account flag to default
			if(!getBankAccountData.addAnotherBank)
				$('.addAcoountButton').hide();
			else
				$('.addAcoountButton').show();
			return banksList;
		}
	}catch(err) {
		showSysException(err);
	}
}

/***Following code is the Error Handler for verify/add/edit bank account AJAX calls *** */
dfs.crd.pymt.addEditErrorHandler = function(jqXHR){
try {
		hideSpinner();
		cpEvent.preventDefault();
		var code = getResponseStatusCode(jqXHR);
		var activePage=$.mobile.activePage.attr('id');
		var validErrorCodes = new Array("1220","1234","1235","1237","1238","1239","1240","1241","1242",
		"1243","1244","1245","1246","1247","1248","1249","1250","1251");
		if (jQuery.inArray(code, validErrorCodes) > -1) {
			var staticMessage = errorCodeMap[code];
		/*	if(code == "1239"){
				$("#"+activePage+" #routingErr").html(staticMessage);
				$("#"+activePage+" #routing").parent(".wrapperSpan").siblings("#"+activePage+" #routingErr").show();
				$("#"+activePage+" #routing").parent(".wrapperSpan").css({"border-bottom": "solid 1px #ed1c24","border-left": "solid 1px #ed1c24","border-right": "solid 1px #ed1c24"});
			}else*/ if(code == "1245"){
				$("#"+activePage+" #accountNickname").parent(".wrapperSpan").siblings(".errormsg").show();
				$("#"+activePage+" #accountNickname").parent(".wrapperSpan").css({"border-bottom": "solid 1px #ed1c24","border-left": "solid 1px #ed1c24","border-right": "solid 1px #ed1c24"});
			}else{
				$("#"+activePage+" #errorMsg").html(staticMessage);			
				$(".errorText").show();
			}
			$(window).scrollTop(0);
		}else{
			if(code == "1219"){
				cpEvent.preventDefault();
				navigation('../payments/manageBankAccHaMode');
				featuresUnavailableSCVariables();
			}else if(code == "1255"){
				cpEvent.preventDefault();
				navigation('../payments/manageBankAccMaxAcctLimitMode');
				maximumBanksReachedSCVariables();
			}else{
				errorHandler(code, "", "paymentsHistory");
			}
		}
	} catch (err) {
		showSysException(err);
	}
}
/**** Add/Edit Bank Account End - Kanika*******/

/*******Start code for account removal - Brainy********/
dfs.crd.pymt.removeSelectedBankAccount  = function(){
try{
		var accountDetails = getDataFromCache("SELECTED_BANK_ACCOUNT_SERVICE_RESPONSE");	
		if (!jQuery.isEmptyObject(accountDetails)) {
		var hasPendingPayments = accountDetails.hasPendingPayments;
			if(hasPendingPayments==true){
				navigation('removeAccountConfirmPending');
			}else{
				navigation('removeAccountConfirmNoPending');
			}
		}
	}catch(err){
		showSysException(err);
	}
}

function removeAccountConfirmPendingLoad(){
try {
		var validPriorPages = new Array("manageBankAccountsDetails","moreLanding");
		if (jQuery.inArray(fromPageName, validPriorPages) > -1) {
			dfs.crd.pymt.populateRemoveAccountPendingPageDivs();
		}else {
			cpEvent.preventDefault();
			navigation('../payments/manageBankAccounts');
		}
	}catch(err) {
		showSysException(err);
	}
}

dfs.crd.pymt.populateRemoveAccountPendingPageDivs = function() {
try{
		var pendingPaymentsDetails = getDataFromCache("SELECTED_BANK_ACCOUNT_SERVICE_RESPONSE");
		if (!jQuery.isEmptyObject(pendingPaymentsDetails)) {
			var pendingPayments = pendingPaymentsDetails.pendingPayments ;
			var bankName = pendingPaymentsDetails.bankName;
			var	accntEndingNum = pendingPaymentsDetails.last4BankAcctNbr;
			if(pendingPayments.length != 0){
				var pendingPaymentsList = "";
				var removeAccountPendingDataRow = "";
				var removeAccountPendingDataTable = "";
				var removeAccountPendingTableHeader = "<li class='listLi'><div class='ui-grid-b'><div class='ui-block-a'> <span class='leftMargin'>Date</span> </div><div class='ui-block-b'> <span>Method</span> </div><div class='ui-block-c'> <span class='amountMargin'>Amount</span> </div></div></li>";
				for(pendingPaymentElement in pendingPayments){
							var pendingPaymentInfo=pendingPayments[pendingPaymentElement];
							var paymentDate = pendingPaymentInfo.paymentDate;
							var paymentMethod= "ONLINE";
							var paymentAmount= pendingPaymentInfo.paymentAmount;																
							removeAccountPendingDataRow = "<li class='listLi'><div class='ui-grid-b'><div class='ui-block-a'><span class='leftMargin'>"
								+paymentDate
								+"</span></div><div class='ui-block-b'><span>"
								+paymentMethod
								+"</span></div><div class='ui-block-c'> <span class='amountMargin'>$"
								+paymentAmount
								+"</span></div><div class='ui-block-d '> <span class='leftMargin'>"
								+bankName +" ****"+accntEndingNum
								+"</span> </div></div></li>";
							removeAccountPendingDataTable += removeAccountPendingDataRow;	 
						}
						pendingPaymentsList = removeAccountPendingTableHeader + removeAccountPendingDataTable;
				$(".transactionTable").html(pendingPaymentsList);	
			}else{
				//alert("no pending payments");
			}
		}
	}catch(err){
		showSysException(err);
	}
}

function removeAccountConfirmNoPendingLoad(){
try {
		var validPriorPages = new Array("manageBankAccountsDetails","moreLanding");
		if (!(jQuery.inArray(fromPageName, validPriorPages) > -1)) {
			cpEvent.preventDefault();
			navigation('../payments/manageBankAccounts');
		}
	}catch (err) {
		showSysException(err);
	}
}

/****start code for account removal on click of remove button *****/
function removeCompleteLoad(){
try{
		var validPriorPages = new Array("removeAccountConfirmNoPending","removeAccountConfirmPending");
		if (jQuery.inArray(fromPageName, validPriorPages) > -1) {
			dfs.crd.pymt.deleteBankAccountPost();
			var deleteAccntDetails = getDataFromCache("POST_DELETE_UPDATED_ACCOUNT_LIST");
			
			if(!jQuery.isEmptyObject(deleteAccntDetails)){
				dfs.crd.pymt.populateAccountRemovalPageDivs();
			}
		}else {
			cpEvent.preventDefault();
			navigation('../payments/manageBankAccounts');
		}
	}catch(err){
		showSysException(err);
	}
}

dfs.crd.pymt.deleteBankAccountPost= function(userAccount){
try{
		var accountSelected = getDataFromCache("SELECTED_BANK_ACCOUNT_SERVICE_RESPONSE");
		var ACCOUNT_REMOVAL_URL = RESTURL + "pymt/v1/removebank";
		if(!jQuery.isEmptyObject(accountSelected)){
			var dataJSON = {
				"bankShortName": accountSelected.bankShortName,
				"hashedBankRoutingNbr": accountSelected.hashedBankRoutingNbr,
				"hashedBankAcctNbr":accountSelected.hashedBankAcctNbr
			};
			var dataJSONString = JSON.stringify(dataJSON);
		
			showSpinner();
			var localJSON = {
					"serviceURL": ACCOUNT_REMOVAL_URL,
					"successHandler": "dfs.crd.pymt.deleteAccntDetailsPageSuccessHandler",
					"errorHandler":"dfs.crd.pymt.deleteAccntDetailsPageErrorHandler",
					"requestType":"POST",
					"isASyncServiceCall": false	,
					"serviceData"	: dataJSONString	
			}	
			dfs.crd.disnet.doServiceCall(localJSON);
			killDataFromCache('MANAGE_BANK_ACCOUNT_LIST');
			killDataFromCache('SELECTED_BANK_ACCOUNT_SERVICE_RESPONSE');
		}
	}catch(err){
		showSysException(err);
	}
}

dfs.crd.pymt.deleteAccntDetailsPageSuccessHandler = function(responseData) {
try {
		hideSpinner();
		putDataToCache("POST_DELETE_UPDATED_ACCOUNT_LIST", responseData);
	}catch(err) {
		showSysException(err);
	}
}

dfs.crd.pymt.deleteAccntDetailsPageErrorHandler = function(jqXHR) {
try {
		hideSpinner();
		var code=getResponseStatusCode(jqXHR);	
		if(code == "1219"){
			cpEvent.preventDefault();
			navigation('../payments/manageBankAccHaMode');
			featuresUnavailableSCVariables();
		}else{			
			errorHandler(code,'','');
		}
	} catch (err) {
		showSysException(err);
	}
}

dfs.crd.pymt.populateAccountRemovalPageDivs = function(){
try	{
		var accountRemoval = getDataFromCache("POST_DELETE_UPDATED_ACCOUNT_LIST");
		if(!accountRemoval.noMoreBanks) {
			var banksList = dfs.crd.pymt.populateConfirmationPage(accountRemoval,true);
			$("#remove_bank_account_list").html(banksList);
		}else{
			//show default message with Add Bank button
		}
	}catch(err) {
		showSysException(err);
	}
}
/****end code for account removal on click of remove button *****/

/**** code for inline validation for add/edit bank account *****/
dfs.crd.pymt.validateUserInput = function(){
try{
		var isRoutingInlineErr = false;
		var isAccNumbrInlineErr = false;
		var activePage=$.mobile.activePage.attr('id');
		/*hiding errors and highlights after server side error is displayed*/
		$(".errorText,.errormsg").hide();
		$(".dd").removeClass("ddHighlightError");
		$(".arrow").removeClass("ddHighlightErrorArrow");
		//$("#"+activePage+" #routing").parent(".wrapperSpan").css({"border-bottom": "solid 1px #b3b3b3","border-left": "solid 1px #b3b3b3","border-right": "solid 1px #b3b3b3"});
		$("#"+activePage+" #accountNickname").parent(".wrapperSpan").css({"border-bottom": "solid 1px #b3b3b3","border-left": "solid 1px #b3b3b3","border-right": "solid 1px #b3b3b3"});
		$("#"+activePage+" #accountNumber").parent(".wrapperSpan").css({"border-bottom": "solid 1px #b3b3b3","border-left": "solid 1px #b3b3b3","border-right": "solid 1px #b3b3b3"});
		
		/*validation for Routing text box*/
		var flag = true;
		if($("#"+activePage+" #routing").val() == ""){
			flag = false;
			$("#"+activePage+" #routing").parent(".wrapperSpan").css({"border-bottom": "solid 1px #ed1c24","border-left": "solid 1px #ed1c24","border-right": "solid 1px #ed1c24"});
		}else{
				if(((/^[0-9*]+$/.test($("#"+activePage+" #routing").val())) == false )|| ($("#"+activePage+" #routing").val().toString().length) !=9 ){
					//$(".servererrormsg").hide();
					$("#"+activePage+" #routing").parent(".wrapperSpan").siblings(".errormsg").show();
					$("#"+activePage+" #routing").parent(".wrapperSpan").css({"border-bottom": "solid 1px #ed1c24","border-left": "solid 1px #ed1c24","border-right": "solid 1px #ed1c24"});
					//$(".errorText").show();
					isRoutingInlineErr = true;
					flag = false;
				}else {
					$(".errorText,.errormsg").hide();
					$("#"+activePage+" #routing").parent(".wrapperSpan").siblings(".errormsg").hide();
					}		
		}
			
		/*validation for account and confirm account*/
		if($("#"+activePage+" #confirmAccountNumber").val() == ""){
			flag = false;
			$("#"+activePage+" #confirmAccountNumber").parent(".wrapperSpan").css({"border-bottom": "solid 1px #ed1c24","border-left": "solid 1px #ed1c24","border-right": "solid 1px #ed1c24"});
		}
		
		if($("#"+activePage+" #accountNumber").val() == ""){
			flag = false;
			$("#"+activePage+" #accountNumber").parent(".wrapperSpan").css({"border-bottom": "solid 1px #ed1c24","border-left": "solid 1px #ed1c24","border-right": "solid 1px #ed1c24"});
			//$(".errorText").show();
		}/*else{
			if(($("#"+activePage+" #accountNumber").val().toString().length) !=17){
				flag = false;
				$("#"+activePage+" #accNumErr").html("Bank Account Number must be 17 digits.");
				$("#"+activePage+" #accountNumber").parent(".wrapperSpan").siblings(".acctnumerrormsg").show();
				$("#"+activePage+" #accountNumber").parent(".wrapperSpan").css({"border-bottom": "solid 1px #ed1c24","border-left": "solid 1px #ed1c24","border-right": "solid 1px #ed1c24"});
			}*/
			else {
				if($("#"+activePage+" #accountNumber").val() != $("#"+activePage+" #confirmAccountNumber").val()){
					flag = false;
					//$(".acctnumerrormsg").hide();
					$("#"+activePage+" #accountNumber").parent(".wrapperSpan").siblings(".errormsg").show();
					$("#"+activePage+" #accountNumber").parent(".wrapperSpan").css({"border-bottom": "solid 1px #ed1c24","border-left": "solid 1px #ed1c24","border-right": "solid 1px #ed1c24"});
				isAccNumbrInlineErr = true;
			}else {
					$(".errorText").hide();
					$("#"+activePage+" #accountNumber").parent(".wrapperSpan").siblings(".errormsg").hide();
					}
				}
		
		/*validation for account type dropdown*/	
		if($(".ddTitleText").find('.ddlabel').text() == ""){
			flag = false;
			$(".dd").addClass("ddHighlightError");
			$(".arrow").addClass("ddHighlightErrorArrow");
		}else {
			$(".errorText").hide();
			}
		
		if(!flag){
			if(isRoutingInlineErr || isAccNumbrInlineErr)
				inlineErrorTrackingSCVariables(isRoutingInlineErr,isAccNumbrInlineErr);
			return false;
		}else{
			var routingNo = $("#"+activePage+" #routing").val();
			var accountNo = $("#"+activePage+" #accountNumber").val();
			var accountType = $('#'+activePage+' select option:selected').val();
			var accNickname = $("#"+activePage+" #accountNickname").val();
			if($(document).jqmData("editFlag") == true){
			dfs.crd.sct.updateEditedBankAccountDetails(); //site catalyst link tracking for edit account btn 
			var bankInfoData = getDataFromCache("SELECTED_BANK_ACCOUNT_SERVICE_RESPONSE");
				if(!jQuery.isEmptyObject(bankInfoData)){
				var bankAccountArray =
				{
					"bankRoutingNbr" : routingNo,
					"bankAcctNbr" : accountNo,
					"accountType" : accountType,
					"nickName" : accNickname,
					"oldBankShortName" : bankInfoData.bankShortName,
					"oldHashedBankAcctNbr" : bankInfoData.hashedBankAcctNbr,
					"oldHashedBankRoutingNbr" : bankInfoData.hashedBankRoutingNbr,
					"isEditBankAccount" : true
				};
				console.log("BankAccount check1: "+ JSON.stringify(bankAccountArray));
				dfs.crd.pymt.verifyBankAccountPost(bankAccountArray/*, true*/);
				}
			}else{
				dfs.crd.sct.confirmAddBankAccountBtn(accountType); //site catalyst link tracking for add account btn 
				var bankAccountArray =
				{
					"bankRoutingNbr" : routingNo,
					"bankAcctNbr" : accountNo,
					"accountType" : accountType,
					"nickName" : accNickname,
					"isEditBankAccount" : false
				};
				console.log("BankAccount check2: "+ JSON.stringify(bankAccountArray));
				dfs.crd.pymt.verifyBankAccountPost(bankAccountArray/*, false*/);
			}
		}
	}catch(err){
		showSysException(err);
	}
}