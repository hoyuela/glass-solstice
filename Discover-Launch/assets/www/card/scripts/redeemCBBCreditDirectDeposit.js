
//**** statement_credit_1 page Page start ****/

/** Name Space**/
dfs.crd.cbb = dfs.crd.cbb || {};
/** **/

function statementCredit1Load(){
	try{		
		var validPriorPagesOfStat1= new Array("redemptionLanding","statementCredit2","statementCredit3","moreLanding");
		if((jQuery.inArray(fromPageName, validPriorPagesOfStat1) > -1) || isLhnNavigation ){			
			dfs.crd.cbb.populateStatementCredit1Page('REDEEMCBBCREDITINFO');
			isLhnNavigation  = false;
		}else{
			cpEvent.preventDefault();
			history.back();
		}		
	}catch(err){
		showSysException(err);
	}
}

dfs.crd.cbb.populateStatementCredit1Page = function(pageName){
	try{
		var redeemCbbCreditData =dfs.crd.cbb.getRedeemCbbCreditData(pageName);
		if (!jQuery.isEmptyObject(redeemCbbCreditData)){
			dfs.crd.cbb.populateStatementCredit1PageDiv(redeemCbbCreditData);
		}
	}catch(err){
		showSysException(err);
	}
}

dfs.crd.cbb.getRedeemCbbCreditData = function(pageId){
	try{
		var redeemCbbCreditData=getDataFromCache(pageId);
		var newDate = new Date();
		var RedeemCbbCreditDirectURL=RESTURL+"rewards/v2/redeemoption?"+newDate+"";	
		if(jQuery.isEmptyObject(redeemCbbCreditData)){
            showSpinner();
			$.ajax({
				type : "GET",
				url : RedeemCbbCreditDirectURL,
				async:false,
				dataType : 'json',
				data : {
					'modeCode' : 'CRD1'
				},
				headers: prepareGetHeader(),
				success : function(responseData, status, jqXHR) {
                   hideSpinner();
					if (!validateResponse(responseData,"statementCreditValidation")){ // Pen Test Validation
                	   errorHandler("SecurityTestFail","","");
						return;
					}
					if (jqXHR.status != 200 & jqXHR.status != 204) {
						var code=getResponseStatusCode(jqXHR);
						errorHandler(code,'','statementCredit1');
					}
					else{
						redeemCbbCreditData=responseData;
						redeemCbbCreditData['modeCode']='CRD1';
						putDataToCache(pageId,redeemCbbCreditData);
					}
				},
				error : function(jqXHR, textStatus, errorThrown) {
                   hideSpinner();
					cpEvent.preventDefault();
					var code=getResponseStatusCode(jqXHR);	
					cpEvent.preventDefault();
					switch (code){
					case "1607": case"1608":						
						var errorMsgData=getResponsErrorData(jqXHR);
						var errorMinDistBalance;
						if(!isEmpty(errorMsgData)){
							errorMinDistBalance=errorMsgData.minDistBalance;
							if(errorMinDistBalance.indexOf('.') != -1 ){
								errorMinDistBalance=errorMinDistBalance.split(".")[0];
							}
						}
						var errorMessage=errorCodeMap.INSUFFICIENT_CBB_BALANCE_FOR_REDEEM_STATEMENT_CREDIT;
						var promoCodeTextData = [];							
						promoCodeTextData['ACHome_CashBackBonusBalance'] = "$"+globalEarnRewardAmount;
						promoCodeTextData['statement_credit_minDisbIncrAmt'] = "$"+errorMinDistBalance;

						var parseContentText=parseContent(errorMessage,promoCodeTextData);
						if(!isEmpty(parseContentText)){
							errorHandler(code,parseContentText,'statementCredit1');
						}else{
							errorHandler('0','','statementCredit1');
						}
						break;
					default:errorHandler(code,'','statementCredit1');
					break;
					}
				}
			});
		}else {
		}
		return redeemCbbCreditData;
	}catch(err){
		showSysException(err);
	}
}

dfs.crd.cbb.populateStatementCredit1PageDiv = function(redeemCashBackInfo){
	try{
		if(!jQuery.isEmptyObject(redeemCashBackInfo)){	

			var disbAmts=redeemCashBackInfo.disbAmts;
			var minDisbIncrAmt=redeemCashBackInfo.minDisbIncrAmt;
			if(minDisbIncrAmt.indexOf('.') != -1 ){
				minDisbIncrAmt=minDisbIncrAmt.split(".")[0];
			}			
			$("#statement_credit_minDisbIncrAmt,#statement_credit_minDisbIncrAmt").html(minDisbIncrAmt);
			var dyanmicSelectFieldForAmount="<select id='redeemCashbackCredit_DirectDeposit1_Amount' onchange='dfs.crd.cbb.changeStatCreditAmtDropDownLabel();'><option value=''>Choose a $"+minDisbIncrAmt+" increment...</option>";
			for(disbAmtsVal in disbAmts){
				var disbAmtval=disbAmts[disbAmtsVal];
				if(!isEmpty(disbAmtval)){
					disbAmtval=disbAmtval.split(".")[0];
				}
				dyanmicSelectFieldForAmount+="<option value="+disbAmtval+">$"+disbAmtval+" Statement Credit</option>";
			}		
			dyanmicSelectFieldForAmount+="</select>";
			$("#statementCredit1_AmoutSelectField").html(dyanmicSelectFieldForAmount);
			$("#statementCredit1_CreditCardEnding").html(globalLastFourAcctNbr);
			$("#statementCredit1_CashBackBonusBalance").html("$"+globalEarnRewardAmount);
			if(!isEmpty(redeemCashBackInfo.programTerms)){
				$("#statementCredit1_TermsConditions").html(redeemCashBackInfo.programTerms);
			}
		}
	}catch(err){
		showSysException(err);
	}
}

dfs.crd.cbb.changeStatCreditAmtDropDownLabel = function (){
	try{
		/* this function call from commonUI js */
		var redeemCashbackCreditDirectDeposit1Amount=$('#redeemCashbackCredit_DirectDeposit1_Amount');
		if(!jQuery.isEmptyObject(redeemCashbackCreditDirectDeposit1Amount)){
			var amountSelected=redeemCashbackCreditDirectDeposit1Amount.val();
			if( amountSelected != "" ){
				activebtn('statement_Credit_continue_btn');
			}else{
				deactiveBtn('statement_Credit_continue_btn');			
			}
		}

		var amountSelected=redeemCashbackCreditDirectDeposit1Amount.val();
		$('#ui-btn-text').text($("#redeemCashbackCredit_DirectDeposit1_Amount option:selected").text());
	}catch(err){
		showSysException(err);
	}
}

//validating the input and adding it to the cache
dfs.crd.cbb.redeemStatementCredit2 = function(){
	try{
		var redeemCashbackCredit_DirectDeposit1_Amount=$('#redeemCashbackCredit_DirectDeposit1_Amount');
		if(!jQuery.isEmptyObject(redeemCashbackCredit_DirectDeposit1_Amount)){
			var amountSelected=redeemCashbackCredit_DirectDeposit1_Amount.val();
			if( amountSelected != "" && typeof amountSelected != 'undefined' ){
				var cacheRedeemCBBDetails=getDataFromCache("REDEEMCBBCREDITINFO");
				if(!jQuery.isEmptyObject(cacheRedeemCBBDetails)){
					cacheRedeemCBBDetails["amountSelected"]=amountSelected
					cacheRedeemCBBDetails["display_date"]=display_date;
					putDataToCache("REDEEMCBBCREDITINFO",cacheRedeemCBBDetails);
					navigation('../rewards/statementCredit2');
				}
			}
		}
	}catch(err){
		showSysException(err);
	}
}
//**** statementCredit1 page Page end ****/


//**** statementCredit2 page Page start ****/
function statementCredit2Load(){
	try{
		var validPriorPagesOfStat1= new Array("statementCredit1","statementCreditCancelTrans","moreLanding");
		if(jQuery.inArray(fromPageName, validPriorPagesOfStat1) > -1 ){
			var redeemCbbCreditData = getDataFromCache("REDEEMCBBCREDITINFO");
			if (isEmpty(redeemCbbCreditData)) {
				cpEvent.preventDefault();
				history.back();
			}else{	
				dfs.crd.cbb.populateStatementCredit2Page();
			}
		}else{
			cpEvent.preventDefault();
			history.back();
		}		
	}catch(err){
		showSysException(err);
	}
}

dfs.crd.cbb.populateStatementCredit2Page = function (){
	try{
		dfs.crd.cbb.populateStatementCredit2PageDiv();
	}catch(err){
		showSysException(err);
	}
}

dfs.crd.cbb.populateStatementCredit2PageDiv = function(){
	try{
		var cacheRedeemCBBDetails=getDataFromCache("REDEEMCBBCREDITINFO");
		if(!jQuery.isEmptyObject(cacheRedeemCBBDetails)){
			if(!isEmpty(cacheRedeemCBBDetails.rewardType)){
				$("#statementCredit2_RewardType").html(cacheRedeemCBBDetails.rewardType);	
			}
			if(!isEmpty(cacheRedeemCBBDetails.amountSelected)){
				$("#statementCredit2_RedeemAmount").html(cacheRedeemCBBDetails.amountSelected+".00");
			}
			if(!isEmpty(cacheRedeemCBBDetails.display_date)){
				$("#statementCredit2_RedeemDate").html(cacheRedeemCBBDetails.display_date);
			}		
		}
		$("#statementCredit2_CreditCardEnding").html(globalLastFourAcctNbr);
		$("#statementCredit2_CashBackBonusBalance").html("$"+globalEarnRewardAmount);
	}catch(err){
		showSysException(err);
	}
}

dfs.crd.cbb.renderStatementCredit3Page = function(){
	try{
		var updatedDetails;
		var cacheRedeemCBBDetails;
		cacheRedeemCBBDetails=getDataFromCache("REDEEMCBBCREDITINFO");
		if(!jQuery.isEmptyObject(cacheRedeemCBBDetails)){
			updatedDetails=dfs.crd.cbb.postStatementCreditCBB(cacheRedeemCBBDetails);
			if(!jQuery.isEmptyObject(updatedDetails)){
				killDataFromCache("ACHOME");
				killDataFromCache("REDEEM_HISTORY");
				killDataFromCache("ECERT_REDEEM_HISTORY_DETAILS");	
				if(!isEmpty(updatedDetails.rewardsBalance)){
					globalEarnRewardAmount = numberWithCommas(updatedDetails.availToRedeem);
				}
				cacheRedeemCBBDetails['updatedPostDetails']=updatedDetails;		
				putDataToCache("REDEEMCBBCREDITINFO",cacheRedeemCBBDetails);
				navigation('../rewards/statementCredit3',false);
			}
		}
	}catch(err){
		showSysException(err);
	}
}
//**** statementCredit2 page Page end ****/

//**** statementCredit3 page Page start ****/
function statementCredit3Load(){
	try{
		dfs.crd.cbb.populateStatementCredit3Page();
	}catch(err){
		showSysException(err);
	}
}

dfs.crd.cbb.populateStatementCredit3Page = function(){
	try{
		var cacheRedeemCBBDetails=getDataFromCache("REDEEMCBBCREDITINFO");
		if(!jQuery.isEmptyObject(cacheRedeemCBBDetails)){
			dfs.crd.cbb.populateStatementCredit3PageDiv(cacheRedeemCBBDetails);
		}
	}catch(err){
		showSysException(err);
	}
}

dfs.crd.cbb.populateStatementCredit3PageDiv = function(cacheRedeemCBBDetails){
	try{
		if(!jQuery.isEmptyObject(cacheRedeemCBBDetails)){
			$("#statementCredit3_RewardType").html(cacheRedeemCBBDetails.updatedPostDetails.rewardType);
			$("#statementCredit3_RedeemAmount").html(cacheRedeemCBBDetails.updatedPostDetails.redemptionAmt);
			$("#statementCredit3_RedeemDate").html(cacheRedeemCBBDetails.updatedPostDetails.requestDate);
			$("#statementCredit3_CashBackBonusBalance").html("$"+globalEarnRewardAmount);
		}
		//document.getElementById("statementCredit3_CashBackBonusBalance").innerHTML="$"+globalEarnRewardAmount;
		$("#statementCredit3_CreditCardEnding").html(globalLastFourAcctNbr);
		killDataFromCache("REDEEMCBBCREDITINFO");
	}catch(err){
		showSysException(err);
	}
}
//**** statementCredit3 page Page end ****/

//Description: posting data for Statement Credit
dfs.crd.cbb.postStatementCreditCBB = function(cacheRedeemCBB){
	try{
		var redeemDetails;
		if(!jQuery.isEmptyObject(cacheRedeemCBB)){			
			var RedeemCbbCreditDirectURL=RESTURL+"rewards/v2/redeem";
			var modeCode=cacheRedeemCBB.modeCode;
			var amount=cacheRedeemCBB.amountSelected;
			var dataJSON={"modeCode":modeCode,"amount":amount};
			var dataJSONString=JSON.stringify(dataJSON);
			showSpinner();
			$.ajax({
				type : "POST",
				url : RedeemCbbCreditDirectURL,
				async:false,
				dataType : 'json',
				data :dataJSONString,
				headers:preparePostHeader(),
				success : function(responseData, status, jqXHR) {
                   hideSpinner();
					if (!validateResponse(responseData,"statementCreditPostValidation")) // Pen Test Validation
                   {
					   errorHandler("SecurityTestFail","","");
					   return;
                   }
					if (jqXHR.status != 200 & jqXHR.status != 204) {
						var code=getResponseStatusCode(jqXHR);
						errorHandler(code,'','statementCredit3');
					}
					else{
						redeemDetails=responseData;					
					}
				},
				error : function(jqXHR, textStatus, errorThrown) {
                   hideSpinner();
					cpEvent.preventDefault();
					var code=getResponseStatusCode(jqXHR);
					switch (code){
					case "1607": case"1608":
						var errorMessage=errorCodeMap.INSUFFICIENT_CBB_BALANCE_FOR_REDEEM_STATEMENT_CREDIT;
						var promoCodeTextData = [];							
						promoCodeTextData['ACHome_CashBackBonusBalance'] = "$"+globalEarnRewardAmount;	
						var parseContentText=parseContent(errorMessage,promoCodeTextData);
						if(!isEmpty(parseContentText)){
							errorHandler(code,parseContentText,'','statementCredit1');
						}else{
							errorHandler('0','','statementCredit3');
						}			
						break;
					default:errorHandler(code,'','statementCredit3');
					break;
					}
				}
			});
		}
		return redeemDetails;
	}catch(err){
		showSysException(err);
	}

}

function statementCreditCancelTransLoad(){
	
	try{		
		var validPriorPagesOfCancelStatementCredit= new Array("statementCredit2","statementCreditConfirmCancelTrans");
		if(!(jQuery.inArray(fromPageName, validPriorPagesOfCancelStatementCredit) > -1) ){	
			cpEvent.preventDefault();
			history.back();
		}	
	}catch(err){
		showSysException(err);
	}
}


function statementCreditConfirmCancelTransLoad(){}

//##################################################### Statement Credit changes end   ###################################





//##################################################### Direct Deopsit changes start   ###################################

//**** directDeposit1 page Page start ****/
function directDeposit1Load() {
	try{		
		var validPriorPagesOfDirect1= new Array("redemptionLanding","directDeposit2","customerServiceUpdateAccount","directDeposit3","moreLanding");
		if((jQuery.inArray(fromPageName, validPriorPagesOfDirect1) > -1) || isLhnNavigation ){			
			dfs.crd.cbb.populateDirectDepositPage1('REDEEMCBBDEPOSITINFO');
			isLhnNavigation  = false;
		}else{
			cpEvent.preventDefault();
			history.back();
		}		
	}catch(err){
		showSysException(err);
	}
}

dfs.crd.cbb.populateDirectDepositPage1 = function(pageName) {
	try{
		var redeemCbbDirectData=dfs.crd.cbb.getRedeemCbbDirectData(pageName);
		if (!jQuery.isEmptyObject(redeemCbbDirectData)) {
			dfs.crd.cbb.populateDirectDeposit1PageDiv(redeemCbbDirectData, pageName);
		}
	}catch(err){
		showSysException(err);
	}
}

dfs.crd.cbb.getRedeemCbbDirectData = function(pageId){
	try{
		var redeemCbbDirectData=getDataFromCache(pageId);
		var newDate = new Date();	
		if(jQuery.isEmptyObject(redeemCbbDirectData)){
			var RedeemCbbCreditDirectURL=RESTURL+"rewards/v2/redeemoption?"+newDate+"";	
            showSpinner();
			$.ajax({
				type : "GET",
				url : RedeemCbbCreditDirectURL,
				async:false,
				dataType : 'json',
				data : {
					'modeCode' : 'EFT1'
				},
				headers: prepareGetHeader(),
				success : function(responseData, status, jqXHR) {
                   hideSpinner();
					if (!validateResponse(responseData,"directDepositValidation")){ // Pen Test Validation
                	   errorHandler("SecurityTestFail","","");
						return;
					}
					if (jqXHR.status != 200 & jqXHR.status != 204) {
						var code=getResponseStatusCode(jqXHR);
						errorHandler(code,'','directDeposit1');
					}
					else{
						redeemCbbDirectData=responseData;
						redeemCbbDirectData['modeCode']='EFT1';
						putDataToCache(pageId,redeemCbbDirectData);
					}
				},
				error : function(jqXHR, textStatus, errorThrown) {
                   hideSpinner();
					cpEvent.preventDefault();
					var code=getResponseStatusCode(jqXHR);
					switch (code){
					case "1607": case"1608":
						var errorMsgData=getResponsErrorData(jqXHR);
						var errorMinDistBalance;
						if(!isEmpty(errorMsgData)){
							errorMinDistBalance=errorMsgData.minDistBalance;
							if(errorMinDistBalance.indexOf('.') != -1 ){
								errorMinDistBalance=errorMinDistBalance.split(".")[0];
							}
						}
						var errorMessage=errorCodeMap.INSUFFICIENT_CBB_BALANCE_FOR_REDEEM_DIRECT_DEPOSIT;
						var promoCodeTextData = [];							
						promoCodeTextData['ACHome_CashBackBonusBalance'] = "$"+globalEarnRewardAmount;
						promoCodeTextData['directDeposit1_minDisbIncrAmt'] = "$"+errorMinDistBalance;
						var parseContentText=parseContent(errorMessage,promoCodeTextData);
						if(!isEmpty(parseContentText)){
							errorHandler(code,parseContentText,'directDeposit1');
						}else{
							errorHandler('0','','directDeposit1');
						}			
						break;	

					case "1609": navigation('../rewards/bankAccountForDirectDeposit');
					break;	

					default:errorHandler(code,'','directDeposit1');
					break;
					}
				}
			});

		}else {
		}
		return redeemCbbDirectData;
	} catch(err) {
		showSysException(err);
	}
}

dfs.crd.cbb.populateDirectDeposit1PageDiv = function(redeemCbbDirectData){
	try{
		if(!jQuery.isEmptyObject(redeemCbbDirectData)){	
			var minDisbIncrAmt=redeemCbbDirectData.minDisbIncrAmt;
			var disbAmts=redeemCbbDirectData.disbAmts;
			var bankDetails=redeemCbbDirectData.bankInfo;

			if(fromPageName != "directDeposit2"){
				redeemCbbDirectData["amountSelected"]=null;
				redeemCbbDirectData["bankAccountTitle"]=null;
				putDataToCache("REDEEMCBBDEPOSITINFO",redeemCbbDirectData);
			}

			var amountSelected=redeemCbbDirectData.amountSelected;
			var bankAccountTitleSelected=redeemCbbDirectData.bankAccountTitle;
			var showselectedValue=false;


			if(!isEmpty(amountSelected) && !isEmpty(bankAccountTitleSelected)){
				showselectedValue=true;
			}

			if(showselectedValue){
				activebtn('direct1_continue_btn');
			}
			if(minDisbIncrAmt.indexOf('.') != -1 ){
				minDisbIncrAmt=minDisbIncrAmt.split(".")[0];
			}			
			$("#directDeposit1_minDisbIncrAmt,#directDeposit1_minDisbIncrAmt").html(minDisbIncrAmt);	
			var dyanmicSelectFieldForAmount="<select id='directDeposit1_DirectDeposit1_Amount' onchange='dfs.crd.cbb.changeDirectDepoAmtDropDownLabel();'><option value=''>Choose a $"+minDisbIncrAmt+" increment...</option>";
			for(disbAmtsVal in disbAmts){
				var disbAmtval=disbAmts[disbAmtsVal];
				if(!isEmpty(disbAmtval)){
					disbAmtval=disbAmtval.split(".")[0];
				}
				if(showselectedValue && (disbAmtval == amountSelected)){
					dyanmicSelectFieldForAmount+="<option selected='selected' value="+disbAmtval+">$"+disbAmtval+" Direct Deposit</option>";
					$('#DD1').text("$"+disbAmtval+" Direct Deposit");
				}else{
					dyanmicSelectFieldForAmount+="<option value="+disbAmtval+">$"+disbAmtval+" Direct Deposit</option>";	
				}
			}		
			dyanmicSelectFieldForAmount+="</select>";
			$("#directDeposit1_AmoutSelectField").html(dyanmicSelectFieldForAmount);
			var responseBankData=getDataFromCache("REDEEMCBBDEPOSITINFO");
            responseBankData["bankDetailsLength"]=bankDetails.length;
            if( responseBankData["bankDetailsLength"] == 1){
            	var singleAccountDetails=" to Bank Account at "+redeemCbbDirectData.bankInfo[0].bankName+" ending in "+redeemCbbDirectData.bankInfo[0].bankAcctNbr.replace(/\*/g," ")+"";
            	$("#singleAccountDetails").html(singleAccountDetails);
            	$("#bankAmountLi").addClass("ui-corner-bottom").css("border-bottom-width","1px");
            	$("#bankAccountLi").css("display","none");
            }
            else
            {
            $("#bankAccountLi").css("display","block");

			var dropDownForAcctNbrAndBankName="<select id='direct_DirectDeposit1_AccntnbrAndBankName' onchange='dfs.crd.cbb.changeDirectDepoAmtDropDownLabel();'><option value=''>Select a Bank Account</option>";
			for (bankDetailsVal in bankDetails) {
				var bankInfoObj = bankDetails[bankDetailsVal];
				var bankName = bankInfoObj.bankName.trim();
				if (!isEmpty(bankName)){
			        if (bankName.length > 20){
						bankName = jQuery.trim(bankName).substring(0, 20) + "...";
					}
				}
				var bankAcctNbr = (bankInfoObj.bankAcctNbr).trim();
				var bankNameAndAcctnbr = "";
				var maskedAccntnumber=dfs.crd.pymt.truncateAccountNumber(bankAcctNbr);
				bankNameAndAcctnbr = "" + bankName + "****"+ maskedAccntnumber + "";
				if(showselectedValue && (bankDetailsVal == bankAccountTitleSelected)){
					dropDownForAcctNbrAndBankName += "<option selected='selected' title="+bankDetailsVal+" value=" + bankNameAndAcctnbr + ">"+bankNameAndAcctnbr+"</option>";
					$('#DD2').text(bankNameAndAcctnbr);					
				}else{
					dropDownForAcctNbrAndBankName += "<option title="+bankDetailsVal+" value=" + bankNameAndAcctnbr + ">"+bankNameAndAcctnbr+"</option>";
				}				
			}
            }
			var directDepositAccountNumber =redeemCbbDirectData.bankInfo[0].bankAcctNbr;
			if(!isEmpty(directDepositAccountNumber)){
				$("#directDeposit1_BankAccountNumber").html(directDepositAccountNumber.replace(/\*/g," "));
			}else{
				$("#directDeposit1_BankAccountNumber").html();
			}

			$("#directDeposit1_AccountNumberList").html(dropDownForAcctNbrAndBankName);	
			//$("#directDeposit1_BankAccountNumber").html(redeemCbbDirectData.bankInfo[0].bankAcctNbr);

			$("#directDeposit1_BankName").html(redeemCbbDirectData.bankInfo[0].bankName);
			//$("#directDeposit1_CreditCardEnding").html(globalLastFourAcctNbr);
			$("#directDeposit1_CashBackBonusBalance").html("$"+globalEarnRewardAmount);
			$("#directDeposit1_TermsConditions").html(redeemCbbDirectData.programTerms);
		}
	}catch(err) {
		showSysException(err);
	}
}

dfs.crd.cbb.changeDirectDepoAmtDropDownLabel = function(){
	/* this function call from commonUI js */
	try{
		var isAmountSelected=false;
		var isBankSelected=false;
		var directDeposit1DirectDeposit1Amount=$('#directDeposit1_DirectDeposit1_Amount');
        var directDeposit1AccntnbrAndBankName=$('#direct_DirectDeposit1_AccntnbrAndBankName');
		if(!jQuery.isEmptyObject(directDeposit1DirectDeposit1Amount)){
			var amountSelected=directDeposit1DirectDeposit1Amount.val();
			if( !isEmpty(amountSelected) ){
				isAmountSelected=true;
			}else{
				isAmountSelected=false;
			}
			$('#DD1').text($("#directDeposit1_DirectDeposit1_Amount option:selected").text());
		}

		if(!jQuery.isEmptyObject(directDeposit1AccntnbrAndBankName)){
			var bankSelected=directDeposit1AccntnbrAndBankName.val();
			if( !isEmpty(bankSelected) ){				
				isBankSelected=true;
			}else{				
				isBankSelected=false;
			}
			$('#DD2').text($("#direct_DirectDeposit1_AccntnbrAndBankName option:selected").text());
		}
	     var responseBankData = getDataFromCache("REDEEMCBBDEPOSITINFO");
		if(responseBankData["bankDetailsLength"] == 1)
        {
               isBankSelected=true;
        }
		if(isAmountSelected && isBankSelected ){
			activebtn('direct1_continue_btn');
		}else{
			deactiveBtn('direct1_continue_btn');
		}
	}catch(err) {
		showSysException(err);
	}
}

//validating the input and adding it to the cache
dfs.crd.cbb.redeemDirectDeposit2 = function(){
    try{
          var amountSelected=$('#directDeposit1_DirectDeposit1_Amount').val();
          var select_box = document.getElementById("direct_DirectDeposit1_AccntnbrAndBankName");
          var bankSelectFieldvalue=$('#direct_DirectDeposit1_AccntnbrAndBankName').val();
          var bankSelectFieldtitle;
          var bankAcctNbr;
       if(!isEmpty(select_box)){
           bankSelectFieldtitle = select_box[select_box.selectedIndex].title;
           bankAcctNbr = select_box[select_box.selectedIndex].label;
           bankAcctNbr = bankAcctNbr.substr(-4);                
       }

       var cacheRedeemCBBDetails=getDataFromCache("REDEEMCBBDEPOSITINFO");
       if( !isEmpty(amountSelected) && (!isEmpty(bankSelectFieldtitle) || cacheRedeemCBBDetails["bankDetailsLength"]==1) ){
                       
                       if(!jQuery.isEmptyObject(cacheRedeemCBBDetails)){
                                       cacheRedeemCBBDetails["amountSelected"]=amountSelected;
                                       cacheRedeemCBBDetails["display_date"]=display_date;
                                       if(cacheRedeemCBBDetails["bankDetailsLength"]==1){
bankAcctNbr = cacheRedeemCBBDetails.bankInfo[0].bankAcctNbr;

cacheRedeemCBBDetails["bankAccountNumber"]=bankAcctNbr;
cacheRedeemCBBDetails["bankAccountTitle"]=cacheRedeemCBBDetails.bankInfo[0].bankName;
}else{
       bankAcctNbr = cacheRedeemCBBDetails.bankInfo[bankSelectFieldtitle].bankAcctNbr;
cacheRedeemCBBDetails["bankAccountNumber"]=bankAcctNbr;
cacheRedeemCBBDetails["bankAccountTitle"]=bankSelectFieldtitle;
cacheRedeemCBBDetails["bankAccountValue"]=bankSelectFieldvalue;
}

                                       putDataToCache("REDEEMCBBDEPOSITINFO",cacheRedeemCBBDetails);
                                       navigation('../rewards/directDeposit2');
                      }
       }
}catch(err) {
       showSysException(err);
}
}


//**** directDeposit1 page Page end ****/


//**** directDeposit2 page Page start ****/
function directDeposit2Load() {
	try{	
		var validPriorPagesOfDirect1= new Array("directDeposit1","directDepositCancelTrans","moreLanding");
		if(jQuery.inArray(fromPageName, validPriorPagesOfDirect1) > -1 ){
			var redeemCbbDirectData = getDataFromCache("REDEEMCBBDEPOSITINFO");
			if (isEmpty(redeemCbbDirectData)) {
				cpEvent.preventDefault();
				history.back();
			}else{	
				dfs.crd.cbb.populateDirectDeposit2Page();
			}
		}else{
			cpEvent.preventDefault();
			history.back();
		}				
	}catch(err){
		showSysException(err);
	}
}

/****Truncate function****/
dfs.crd.cbb.truncateBankName = function(bankTitle)
{
   try
   {
        var emptyVar = "";
        if(!isEmpty(bankTitle)){
                
        var truncatedBankName = jQuery.trim(bankTitle).substring(0, 20) + "...";
        return truncatedBankName;
        }
        else{
                return emptyVar;
        }
        }catch(err){
         showSysException(err);
        }
}
dfs.crd.cbb.populateDirectDeposit2Page= function(){
	try{
        var cacheRedeemCBBDetails=getDataFromCache("REDEEMCBBDEPOSITINFO");
        if(!jQuery.isEmptyObject(cacheRedeemCBBDetails)){
        if(!isEmpty(cacheRedeemCBBDetails.bankAccountTitle)){
         var bankTitle=cacheRedeemCBBDetails.bankAccountTitle;
         //var bankName=dfs.crd.cbb.truncateBankName(cacheRedeemCBBDetails.bankInfo[bankTitle].bankName);
         //var accountNumber = cacheRedeemCBBDetails.bankInfo[bankTitle].bankAcctNbr;
         var bankName;
         if(cacheRedeemCBBDetails["bankDetailsLength"] ==1){                
                  bankName=dfs.crd.cbb.truncateBankName(bankTitle);
                  $("#directDeposit2_BankName").html(bankName);
                  }
             else
             {
                       bankName=dfs.crd.cbb.truncateBankName(cacheRedeemCBBDetails.bankInfo[bankTitle].bankName);
                        $("#directDeposit2_BankName").html(bankName);
                  }
                  
                  var bankAcctNbr=cacheRedeemCBBDetails["bankAccountNumber"];
                  if(!isEmpty(bankAcctNbr))
                  {                                                                                     
                       $("#directDeposit2_accNumber").html(bankAcctNbr.replace(/\*/g," "));
                  
                  }
                  else
                  {
                      $("#directDeposit2_accNumber").html();
                  }
                  $("#directDeposit2_BankName").html(bankName);
                              
                  }

			if(!isEmpty(cacheRedeemCBBDetails.rewardType)){
				$("#directDeposit2_RewardType").html(cacheRedeemCBBDetails.rewardType);	
			}
			if(!isEmpty(cacheRedeemCBBDetails.amountSelected)){
				$("#directDeposit2_RedeemAmount").html(cacheRedeemCBBDetails.amountSelected+".00");	
			}
			if(!isEmpty(cacheRedeemCBBDetails.display_date)){
				$("#directDeposit2_RedeemDate").html(cacheRedeemCBBDetails.display_date);	
			}
		}
		$("#directDeposit2_CashBackBonusBalance").html("$"+globalEarnRewardAmount);
		//$("#directDeposit2_CreditCardEnding").html(globalLastFourAcctNbr);
	}catch(err){
		showSysException(err);
	}
}

dfs.crd.cbb.renderDirectDeposit3Page = function(){
	try{
		var updatedDetails;
		var cacheRedeemCBBDetails;
		cacheRedeemCBBDetails=getDataFromCache("REDEEMCBBDEPOSITINFO");
		if(!jQuery.isEmptyObject(cacheRedeemCBBDetails)){
			updatedDetails=dfs.crd.cbb.postCreditDirectDepositCBB(cacheRedeemCBBDetails);
			if(!jQuery.isEmptyObject(updatedDetails)){
				killDataFromCache("ACHOME");
				killDataFromCache("REDEEM_HISTORY");
				killDataFromCache("ECERT_REDEEM_HISTORY_DETAILS");				
				if(!isEmpty(updatedDetails.rewardsBalance)){
					globalEarnRewardAmount = numberWithCommas(updatedDetails.availToRedeem);
				}
				cacheRedeemCBBDetails['updatedPostDetails']=updatedDetails;		
				putDataToCache("REDEEMCBBDEPOSITINFO",cacheRedeemCBBDetails);
				navigation('../rewards/directDeposit3',false);
			}
		}
	}catch(err){
		showSysException(err);
	}
}
//**** directDeposit2 page Page end ****/


//**** directDeposit3 page Page start ****/
function directDeposit3Load() {
	try{
		dfs.crd.cbb.populateDirectDeposit3Page();
	}catch(err){
		showSysException(err);
	}
}

dfs.crd.cbb.populateDirectDeposit3Page=function(){
	try{
		var cacheRedeemCBBDetails=getDataFromCache("REDEEMCBBDEPOSITINFO");
		if(!jQuery.isEmptyObject(cacheRedeemCBBDetails)){

			$("#directDeposit3_RewardType").html(cacheRedeemCBBDetails.updatedPostDetails.rewardType);
			$("#directDeposit3_RedeemAmount").html(cacheRedeemCBBDetails.updatedPostDetails.redemptionAmt);
			$("#directDeposit3_RedeemDate").html(cacheRedeemCBBDetails.updatedPostDetails.requestDate);
			$("#directDeposit3_BankName").html(dfs.crd.cbb.truncateBankName(cacheRedeemCBBDetails.updatedPostDetails.bankName));
		}
		document.getElementById("directDeposit3_CashBackBonusBalance").innerHTML="$"+globalEarnRewardAmount;
		//$("#directDeposit3_CreditCardEnding").html(globalLastFourAcctNbr);
	    $("#directDeposit3_CreditCardEnding").html(
                cacheRedeemCBBDetails.updatedPostDetails.bankAcctNbr.replace(
                        /\*/g, " "));
		killDataFromCache("REDEEMCBBDEPOSITINFO");				
	}catch(err){
		showSysException(err);
	}
}
//**** directDeposit3 page Page end ****/


//Posting Data in case of Direct Deposite
dfs.crd.cbb.postCreditDirectDepositCBB = function(cacheRedeemCBB){
	try{
		var redeemDetails;
        if(!jQuery.isEmptyObject(cacheRedeemCBB)){
             
             var RedeemCbbCreditDirectURL=RESTURL+"rewards/v2/redeem";
             var modeCode=cacheRedeemCBB.modeCode;
             var amount=cacheRedeemCBB.amountSelected;
         var hashedBankAccountNo;
         var hashedRoutingtNo;
        
            /*var hashedBankAccountNo;
              var hashedRoutingtNo;*/
             if(!isEmpty(cacheRedeemCBB.bankAccountTitle)){
                 var bankTitle=cacheRedeemCBB.bankAccountTitle;
                 if(cacheRedeemCBB["bankDetailsLength"] == 1){
                                 hashedBankAccountNo=cacheRedeemCBB.bankInfo[0].hashedBankAcctNbr;
                                 hashedRoutingtNo= cacheRedeemCBB.bankInfo[0].hashedRoutingNumber;
                 }else{
                 hashedBankAccountNo=cacheRedeemCBB.bankInfo[bankTitle].hashedBankAcctNbr;
                 hashedRoutingtNo= cacheRedeemCBB.bankInfo[bankTitle].hashedRoutingNumber;
                 }
                 }


			
			var dataJSON={"modeCode":modeCode,"amount":amount,"hashedRoutingNumber":hashedRoutingtNo,"hashedAcctNbr":hashedBankAccountNo};
			var dataJSONString=JSON.stringify(dataJSON); 
            showSpinner();
			$.ajax({
				type : "POST",
				url : RedeemCbbCreditDirectURL,
				async:false,
				dataType : 'json',
				data : dataJSONString,
				headers:preparePostHeader(),
				success : function(responseData, status, jqXHR) {
                   hideSpinner();
					if (!validateResponse(responseData,"directDepositPostValidation")) // Pen Test Validation
                   {
                   errorHandler("SecurityTestFail","","");
                   return;
                   }
					if (jqXHR.status != 200 & jqXHR.status != 204) {
						var code=getResponseStatusCode(jqXHR);
						errorHandler(code,'','directDeposit3');
					}
					else{
						redeemDetails=responseData;					
					}
				},
				error : function(jqXHR, textStatus, errorThrown) {
                   hideSpinner();
					cpEvent.preventDefault();
					var code=getResponseStatusCode(jqXHR);
					switch (code){
					case "1607": case"1608":
						var errorMsgData=getResponsErrorData(jqXHR);
						var errorMinDistBalance;
						if(!isEmpty(errorMsgData)){
							errorMinDistBalance=errorMsgData.minDistBalance;
							if(errorMinDistBalance.indexOf('.') != -1 ){
								errorMinDistBalance=errorMinDistBalance.split(".")[0];
							}
						}
						var errorMessage=errorCodeMap.INSUFFICIENT_CBB_BALANCE_FOR_REDEEM_DIRECT_DEPOSIT;
						var promoCodeTextData = [];							
						promoCodeTextData['ACHome_CashBackBonusBalance'] = "$"+globalEarnRewardAmount;	
						promoCodeTextData['directDeposit1_minDisbIncrAmt'] = "$"+errorMinDistBalance;
						var parseContentText=parseContent(errorMessage,promoCodeTextData);
						if(!isEmpty(parseContentText)){
							errorHandler(code,parseContentText,'directDeposit3');
						}else{
							errorHandler('0','','directDeposit3');
						}			
						break;
					default:errorHandler(code,'','directDeposit3');
					break;
					}
				}
			});
		}
		return redeemDetails;
	}catch(err){
		showSysException(err);
	}

}

function directDepositCancelTransLoad(){
    try{                         
        var validPriorPagesOfCancelDirectDeposit= new Array("directDeposit2","directDepositConfirmCancelTrans");
            if(!(jQuery.inArray(fromPageName, validPriorPagesOfCancelDirectDeposit) > -1 )){         
                    cpEvent.preventDefault();
					history.back();
			}              
		}catch(err){
				showSysException(err);
        }                
}


function directDepositConfirmCancelTransLoad(){}

function bankAccountForDirectDepositLoad(){}
