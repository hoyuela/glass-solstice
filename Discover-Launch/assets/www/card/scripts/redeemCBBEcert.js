//**** redeemLanding page Page start ****/

/** Name Space**/
dfs.crd.cbb = dfs.crd.cbb || {};
/** **/

function redeemLandingLoad(){
 try{
	if(acLiteModeFlag)
			{
				cpEvent.preventDefault();					
				errorHandler("acLiteOutageMode_ACL","","redeemLanding");
			}
			else{
	           $("#redeemLanding_CashBackBonusBalance").html("$"+ globalEarnRewardAmount);	
		    }
		}catch(err){
		showSysException(err);
	}
}

function redeemCashbackEcert1Load() {
	try{
		var validPriorPagesOfeCertPage1= new Array("redemptionLanding","redeemCashbackEcert2");
		if(jQuery.inArray(fromPageName, validPriorPagesOfeCertPage1) > -1 ){			
			dfs.crd.cbb.populateRedeemCashBackEcert1Page('ALLECERTPARTNERS');
		}else{			
			cpEvent.preventDefault();
			history.back();
		}
	}catch(err){
		showSysException(err);
	}
}

/*
 * Description:Function called on "pagebeforeshow" for RedeemCashBackEcert1Page.
 */
dfs.crd.cbb.populateRedeemCashBackEcert1Page = function(pageName) {
	try{
		var ecertPartners = dfs.crd.cbb.getAllEcertPartnerData(pageName);
		if (!jQuery.isEmptyObject(ecertPartners)) {
			dfs.crd.cbb.populateredeemCashbackEcert1PageDiv(ecertPartners);
		}
	}catch(err){
		showSysException(err);
	}
}

dfs.crd.cbb.getAllEcertPartnerData = function(pageId) {
	try{
		var ecertPartners;
		var newDate = new Date();
		var ALLECERTPARTNERSURL = RESTURL + "rewards/v2/ecertpartners?"
		+ newDate + "";
		ecertPartners = getDataFromCache(pageId);
		if (jQuery.isEmptyObject(ecertPartners)) {
			showSpinner();
			$
			.ajax({
				type : "GET",
				url : ALLECERTPARTNERSURL,
				async : false,
				dataType : 'json',
				headers : prepareGetHeader(),
				success : function(responseData, status, jqXHR) {
					hideSpinner();
					if (jqXHR.status != 200 & jqXHR.status != 204) {
						var code = getResponseStatusCode(jqXHR);
						errorHandler('0', '', 'redeemCashbackEcert1');
					} else {
						ecertPartners = responseData;
						putDataToCache(pageId, ecertPartners);
					}
				},
				error : function(jqXHR, textStatus, errorThrown) {
					hideSpinner();
					cpEvent.preventDefault();
					var code = getResponseStatusCode(jqXHR);
					switch (code) {
					case "1607":
					case "1608":
						var errorMessage = errorCodeMap.INSUFFICIENT_CBB_BALANCE_FOR_REDEEM_ECERT;
						var promoCodeTextData = [];
						promoCodeTextData['ACHome_CashBackBonusBalance'] = "$"
							+ globalEarnRewardAmount;
						var parseContentText = parseContent(
								errorMessage, promoCodeTextData);
						if (!isEmpty(parseContentText)) {
							errorHandler(code, parseContentText,
							'redeemCashbackEcert1');
						} else {
							errorHandler('0', '', 'redeemCashbackEcert1');
						}
						break;
					default:
						errorHandler(code, '', 'redeemCashbackEcert1');
					break;
					}
				}
			});

		} else {
		}
		return ecertPartners;
	}catch(err){
		showSysException(err);
	}
}

dfs.crd.cbb.populateredeemCashbackEcert1PageDiv = function(ecertPartners) {
	try{
		responseData2 = ecertPartners;
		if (!jQuery.isEmptyObject(responseData2)) {
			$("#redeemECert1_CashBackBonusBalance").html("$"+ globalEarnRewardAmount);	
			var activePartnerList = responseData2["availPartners"];
			if (typeof activePartnerList != 'undefined') {
				var listLength = activePartnerList.length;
				var activePartnerInfo = "<ul>";
				for (activePartnerCounter in activePartnerList) {
					var modeAmt=activePartnerList[activePartnerCounter].modeAmt;
					modeAmt = modeAmt.split(".")[0];
					var disbAmt=activePartnerList[activePartnerCounter].disbAmt;
					disbAmt = disbAmt.split(".")[0];
					var activeInfo = "";
					activeInfo += "<li>";
					activeInfo += "<div class='comp-logo'>";
					activeInfo += "<a href='#' onClick=dfs.crd.cbb.renderRedeemCashBackEcert2('"
						+ activePartnerList[activePartnerCounter].modeCode + "')>";
					activeInfo += "<img width='74' height='35' src='"
						+ AVAILABLE_PARTNERS_IMAGE_BASE_URL + ""
						+ activePartnerList[activePartnerCounter].modeCode + ".png'";
					activeInfo += "alt='" + activePartnerList[activePartnerCounter].modeDesc
					+ "' class='ecert-redemption-logo'></a>";
					activeInfo += "</div>";
					activeInfo += "<div class='yellow-bg'>";
					activeInfo += "<span class='white-doller'>";
					activeInfo += "$"+modeAmt;
					activeInfo += "</span>";
					activeInfo += "<span class='yellow-doller'>";
					activeInfo += "$"+disbAmt;
					activeInfo += "</span>";
					activeInfo += "</div>";
					activeInfo += "</li>";

					activePartnerInfo += activeInfo;
				}
				activePartnerInfo+="</ul>"
					$("#redeemECert1_availPartnerInfo").html(activePartnerInfo);	
			}

			var unavailPartnerList = responseData2["unavailPartners"];
			if (!isEmpty(unavailPartnerList)) {

				var listLength = unavailPartnerList.length;

				var inactivePartnerInfo = "<div class='comp-logo-list-deactive margintop20px'><h3>Keep building for these eCertificates!</h3> <br/><ul>"; 
				for (activePartnerCounter in unavailPartnerList) {
					var inactiveInfo = "";
					inactiveInfo += "<li>";
					inactiveInfo += "<div class='comp-logo'>";
					inactiveInfo += "<img width='74' height='35' src='"
						+ UNAVAILABLE_PARTNERS_IMAGE_BASE_URL + ""
						+ unavailPartnerList[activePartnerCounter].modeCode + ".png'";
					inactiveInfo += "alt='" + unavailPartnerList[activePartnerCounter].modeDesc
					+ "' class='ecert-redemption-logo'>";
					inactiveInfo += "</div>";
					inactiveInfo += "<div class='yellow-bg'>";
					inactiveInfo += "<span class='white-doller'>";
					inactiveInfo += unavailPartnerList[activePartnerCounter].modeAmt;
					inactiveInfo += "</span>";
					inactiveInfo += "<span class='yellow-doller'>";
					inactiveInfo += unavailPartnerList[activePartnerCounter].disbAmt;
					inactiveInfo += "</span>";
					inactiveInfo += "</div>";
					inactiveInfo += "</li>";
					inactivePartnerInfo += inactiveInfo;
				}

				if (null != listLength && listLength > 0) {
					inactivePartnerInfo+="</ul>";
					var partenerUnavial = '';
					partenerUnavial = '</div>';
					inactivePartnerInfo = partenerUnavial+inactivePartnerInfo;
					$("#redeemECert1_unavailPartnersInfo").html(inactivePartnerInfo);
				}	

			}
		}
	}catch(err){
		showSysException(err);
	}
}

dfs.crd.cbb.renderRedeemCashBackEcert2 = function(modeCode) {
  try{
	modeCodeT = modeCode;
	navigation('../rewards/redeemCashbackEcert2');
	}catch(err){
		showSysException(err);
	}
}

//**** redeemCashbackEcert1 page Page end ****/

//**** redeemCashbackEcert2 Page start ****/
function redeemCashbackEcert2Load() {
	try{
		dfs.crd.cbb.populateRedeemCashBackEcert2Page();
	}catch(err){
		showSysException(err);
	}
}

/*
 * Description:Function called on "pagebeforeshow" for redeemCashbackEcert2.
 */
dfs.crd.cbb.populateRedeemCashBackEcert2Page = function() {
	try{
		var validPriorPagesOfeCertPage2= new Array("redeemCashbackEcert1","redeemCashbackEcert3");
		if(jQuery.inArray(fromPageName, validPriorPagesOfeCertPage2) > -1 ){
			var getAllEcertPartners = getDataFromCache("ALLECERTPARTNERS");
			if (isEmpty(getAllEcertPartners)) {
				cpEvent.preventDefault();
				history.back();
			}else{	
				dfs.crd.cbb.populateredeemCashbackEcert2PageDiv(modeCodeT);
			}
		}else{
			cpEvent.preventDefault();
			history.back();
		}		
	}catch(err){
		showSysException(err);
	}
}

dfs.crd.cbb.populateredeemCashbackEcert2PageDiv = function(modeCodeT) {
	try{
		var ecertPartnerDetails = dfs.crd.cbb.getEcertPartnerData(modeCodeT);
		var merchantList;
		var response = ecertPartnerDetails;
		if (typeof response != 'undefined') {
			// adding merchant details to global cache
			var merchantDetails = [];
			merchantDetails['merchantName'] = new Object();
			merchantDetails["merchantName"] = response.modeDesc;
			merchantDetails['modeCode'] = new Object();
			merchantDetails["modeCode"] = response.modeCode;
			merchantDetails['modeAmount'] = new Object();
			merchantDetails['disbAmount'] = new Object();
			merchantDetails['CashBackBonus'] = new Object();
			merchantDetails["CashBackBonus"] = response.currentBalance;
			merchantDetails['rewardType'] = new Object();
			merchantDetails["rewardType"] = response.rewardType;

			$("#redeemECert2_CashBackBonusBalance").html("$"+ globalEarnRewardAmount);
			var partTerms = response.partnerTerms;
			merchantDetails['partTerms'] = new Object();
			merchantDetails["partTerms"] = partTerms;

			putDataToCache("ECERTMERCHANTDETAILS", merchantDetails);
			merchantList = response.availPartners;
			var merchantSelectFieldText = "<select name='merchantSelector' id='merchantSelector' onchange='dfs.crd.cbb.fillOutAmoutCapsul(this),dfs.crd.cbb.populateredeemCashbackEcert2PageDiv(this.value),setvalDD1(this.value)' >";

			var amountSelectFieldText = "<select name='merchantAmountSelector' id='merchantAmountSelector' onchange='dfs.crd.cbb.fillOutAmoutCapsul(this),dfs.crd.cbb.setvalDD2(this.value);' >";
			amountSelectFieldText += "<option value='' selected='selected'>Select eCertificate value</option>";

			if (typeof merchantList != 'undefined') {
				var merchantListLength = merchantList.length;
				for (merchantListCounter in merchantList) {
					if (merchantList[merchantListCounter].modeCode == modeCodeT) {
						merchantSelectFieldText += "<option value='"
							+ merchantList[merchantListCounter].modeCode
							+ "' selected='selected' >"
							+ merchantList[merchantListCounter].modeDesc + "</option>";
						// $("#dd1").text(merchantList[i].modeCode);
						$('#DD1').html(merchantList[merchantListCounter].modeDesc);

						var merchantDisbAmtsList = response.disbAmts;
						var merchantDisbAmtsListLength = merchantDisbAmtsList.length;
						if (typeof merchantDisbAmtsList != 'undefined') {
							for (merchantDisbAmtsListCounter in merchantDisbAmtsList) {
								var modeAmount=merchantDisbAmtsList[merchantDisbAmtsListCounter].modeAmount;
								modeAmount = modeAmount.split(".")[0];
								var disbAmount=merchantDisbAmtsList[merchantDisbAmtsListCounter].disbAmount;
								disbAmount = disbAmount.split(".")[0];

								amountSelectFieldText += "<option title='"
									+ merchantDisbAmtsList[merchantDisbAmtsListCounter].modeAmount
									+ "|"
									+ merchantDisbAmtsList[merchantDisbAmtsListCounter].disbAmount
									+ "' value='"
									+ merchantDisbAmtsList[merchantDisbAmtsListCounter].modeAmount
									+ "'>$"
									+ modeAmount
									+ " for a $"
									+ disbAmount
									+ "&nbsp;eCertificate</option>";
							}
							amountSelectFieldText += "</select>";
							$("#redeemECert2_MerchantAmountSelectField").html(amountSelectFieldText);
						}
					} else {
						merchantSelectFieldText += "<option value='"
							+ merchantList[merchantListCounter].modeCode + "'>"
							+ merchantList[merchantListCounter].modeDesc + "</option>";
					}
				}
				merchantSelectFieldText += "</select>";
			}
			$("#redeemECert2_MerchantSelectField").html(merchantSelectFieldText);
			$("#redeemECert2_TermAndConditions").html(partTerms);
			$("#redeemECert2_MerchantModeAmount").html("$");
			$("#redeemECert2_MerchantDisbAmount").html("$$");
			deactiveBtn('redeemECert2_continue_btn');
		}
	}catch(err){
		showSysException(err);
	}
}

dfs.crd.cbb.getEcertPartnerData = function(modeCodeT) {
	try{
		var ecertPartnerDetails;
		var newDate = new Date();
		var ECERTPARTNERSURL = RESTURL + "rewards/v2/redeemoption?" + newDate
		+ "";
		showSpinner();
		$.ajax({
			type : "GET",
			url : ECERTPARTNERSURL,
			async : false,
			dataType : 'json',
			data : {
				'modeCode' : modeCodeT
			},
			headers : prepareGetHeader(),
			success : function(responseData, status, jqXHR) {
				hideSpinner();
				if (jqXHR.status != 200 & jqXHR.status != 204) {
					var code = getResponseStatusCode(jqXHR);
					errorHandler(code, '', 'redeemCashbackEcert2');
				} else {
					ecertPartnerDetails = responseData;
				}
			},
			error : function(jqXHR, textStatus, errorThrown) {
				hideSpinner();
				var code = getResponseStatusCode(jqXHR);
				errorHandler(code, '', 'redeemCashbackEcert2');

			}
		});
		return ecertPartnerDetails;
	}catch(err){
		showSysException(err);
	}
}

function setvalDD1(dd1val) {
 try{
	var dd2=$("#DD2");
	var DD1txt = $("#merchantSelector option[value='" + dd1val + "']").text()
	$("#DD1").text(DD1txt);
	dd2.text('Select eCertificate value');
	//dd2.val() = '';
	}catch(err){
		showSysException(err);
	}
}

dfs.crd.cbb.setvalDD2 = function(dd2val) {
try{
	var DD2txt = $("#merchantAmountSelector option[value='" + dd2val + "']").text();
	$("#DD2").text(DD2txt);
	}catch(err){
		showSysException(err);
	}
}

dfs.crd.cbb.fillOutAmoutCapsul = function() {
try{
	/* this function call from commonUI js */
	var merchantAmountSelector=$('#merchantAmountSelector');
	if (!jQuery.isEmptyObject(merchantAmountSelector)) {
		var amountSelected = merchantAmountSelector.val();
		if (amountSelected != "") {
			activebtn('redeemECert2_continue_btn');
		} else {
			deactiveBtn('redeemECert2_continue_btn');
		}
	}

	var select_box = document.getElementById("merchantAmountSelector");
	var option_value = select_box[select_box.selectedIndex].value;
	var option_title = select_box[select_box.selectedIndex].title;

	var index = option_title.indexOf("|");
	if (-1 != index) {
		var modeAmount = option_title.substring(0, index);
		modeAmount = modeAmount.split(".")[0];
		$("#redeemECert2_MerchantModeAmount").html("$"+ modeAmount);
		var disbAmount = option_title.substring(++index);
		disbAmount = disbAmount.split(".")[0];
		$("#redeemECert2_MerchantDisbAmount").html("$"+ disbAmount);		
		var ecertDetails = getDataFromCache("ECERTMERCHANTDETAILS");
		ecertDetails["modeAmount"] = modeAmount;
		ecertDetails["disbAmount"] = disbAmount;
		putDataToCache("ECERTMERCHANTDETAILS", ecertDetails);
	}else{
		$("#redeemECert2_MerchantModeAmount").html("$"+ option_value);
		$("#redeemECert2_MerchantDisbAmount").html("$$"+ option_title);	
	}
 }catch(err){
		showSysException(err);
	} 
}

dfs.crd.cbb.renderRedeemCashBackEcert3 = function() {
try{
	navigation('../rewards/redeemCashbackEcert3');
	}catch(err){
		showSysException(err);
	}
}
//**** redeemCashbackEcert2 page end ****/

//**** redeemCashbackEcert3 page start ****/

/*
 * This functiona will be called pagebeforechangeevent of redeemCashbackEcert3(e
 * is the event objet)***
 */

function redeemCashbackEcert3Load() {
	try{
		var validPriorPagesOfeCertPage3= new Array("redeemCashbackEcert2");
		if(jQuery.inArray(fromPageName, validPriorPagesOfeCertPage3) > -1 ){
			var getAllEcertPartners = getDataFromCache("ALLECERTPARTNERS");
			if (isEmpty(getAllEcertPartners)) {
				cpEvent.preventDefault();
				history.back();
			}else{	
				dfs.crd.cbb.populateRedeemCashBackEcert3Page();
			}
		}else{
			cpEvent.preventDefault();
			history.back();
		}		
	}catch(err){
		showSysException(err);
	}
}
/*
 * Description:Function called on "pagebeforeshow" for redeemCashbackEcert3.
 */
dfs.crd.cbb.populateRedeemCashBackEcert3Page = function() {
	try{
		dfs.crd.cbb.populateredeemCashbackEcert3PageDiv();
	}catch(err){
		showSysException(err);
	}
}

dfs.crd.cbb.populateredeemCashbackEcert3PageDiv = function() {
	try{
		var eCertMerchantDetails1 = getDataFromCache("ECERTMERCHANTDETAILS");
		if (typeof eCertMerchantDetails1 != 'undefined') {			
			$("#redeemECert3_CashBackBonusBalance").html("$"+ globalEarnRewardAmount);
			$("#redeemECert3_MerchantName").html(eCertMerchantDetails1["merchantName"]);
			$("#redeemECert3_Redemption_Amount").html("$"+ eCertMerchantDetails1["modeAmount"]);
			$("#redeemECert3_DisbAmount").html("$"+ eCertMerchantDetails1["disbAmount"]);
			$("#redeemECert3_rewardType").html(eCertMerchantDetails1["rewardType"]);
			$("#redeemECert3_Date").html(display_date);
		}
	}catch(err) {
		showSysException(err);
	}
}

dfs.crd.cbb.renderRedeemCashBackEcert4 = function() {
	try{
		var updatedDetails;
		var eCertMerchantDetails2 = getDataFromCache("ECERTMERCHANTDETAILS");
		if (!jQuery.isEmptyObject(eCertMerchantDetails2)) {
			updatedDetails = dfs.crd.cbb.postECertCBB(eCertMerchantDetails2);
			if (!jQuery.isEmptyObject(updatedDetails)) {
				killDataFromCache("ACHOME");
				killDataFromCache("REDEEM_HISTORY");
				killDataFromCache("ECERT_REDEEM_HISTORY_DETAILS");
				killDataFromCache("ALLECERTPARTNERS");	
				modeCodeT='';
				if(!isEmpty(updatedDetails.rewardsBalance)){
					globalEarnRewardAmount = numberWithCommas(updatedDetails.availToRedeem);
				}
				killDataFromCache("ECERTMERCHANTDETAILS");
				eCertMerchantDetails2['updatedPostDetails']=updatedDetails;					
				putDataToCache("ECERTMERCHANTDETAILS", eCertMerchantDetails2);
				navigation('../rewards/redeemCashbackEcert4',false);
			}
		}
	}catch(err) {
		showSysException(err);
	}
}
//**** redeemCashbackEcert3 page end ****/

//**** redeemCashbackEcert4 page start ****/
function redeemCashbackEcert4Load() {
	try{
		dfs.crd.cbb.populateRedeemCashBackEcert4Page();
	}catch(err) {
		showSysException(err);
	}
}

/*
 * Description:Function called on "pagebeforeshow" for redeemCashbackEcert3.
 */
dfs.crd.cbb.populateRedeemCashBackEcert4Page = function() {
	try{
		var eCertMerchantDetails2 = getDataFromCache("ECERTMERCHANTDETAILS");
		if (!jQuery.isEmptyObject(eCertMerchantDetails2)) {
			dfs.crd.cbb.populateredeemCashbackEcert4PageDiv(eCertMerchantDetails2);
		}
	}catch(err){
		showSysException(err);
	}
}
var modeDescription;


dfs.crd.cbb.populateredeemCashbackEcert4PageDiv = function(eCertMerchantDetails2) {
	try{
		if (!jQuery.isEmptyObject(eCertMerchantDetails2)) {

			var redeemECert4_DynamicText='';
			var shortmodeDesc;
			var mobilzedmessageText;
			var noteToCashierText='';
			var callableMsgText='';
			if (!isEmpty(eCertMerchantDetails2.updatedPostDetails.modeDescShort)) {
				shortmodeDesc = eCertMerchantDetails2.updatedPostDetails.modeDescShort;
			}

			if (!isEmpty(globalLastFourAcctNbr)) {
				$("#redeemECert4_CardEnding4Number").html(globalLastFourAcctNbr);
			}
			if (!isEmpty(eCertMerchantDetails2.updatedPostDetails.modeDesc)) {
				modeDescription=eCertMerchantDetails2.updatedPostDetails.modeDesc;		
				$("#redeemECert4_MerchantName1").html(eCertMerchantDetails2.updatedPostDetails.modeDesc);
			}
			if (!isEmpty(eCertMerchantDetails2.updatedPostDetails.disbAmt)) {
				var disbAmt=eCertMerchantDetails2.updatedPostDetails.disbAmt;
				if (disbAmt.indexOf('.') != -1) {
					disbAmt = disbAmt.split(".")[0];
				}
				$("#redeemECert4_disbAmt").html("$"+ disbAmt);
			}

			var imagePath = "<img width='90' height='45' src='"
				+ AVAILABLE_PARTNERS_IMAGE_CONFIRM_URL + ""
				+ eCertMerchantDetails2.updatedPostDetails.modeCode + ".png'>";
			$("#redeemECert4_logoImagePath").html(imagePath);

			if(!isEmpty(eCertMerchantDetails2.updatedPostDetails.eCertNumber)){
				redeemECert4_DynamicText += "<li>Mobile eCertificate ID:<b>"+eCertMerchantDetails2.updatedPostDetails.eCertNumber+"</b></li>";
			}
			if(!isEmpty(eCertMerchantDetails2.updatedPostDetails.eCertPin)){
				redeemECert4_DynamicText += "<li><div class='clearboth hrow'></div></li><li>Mobile eCertificate PIN:<b>"+eCertMerchantDetails2.updatedPostDetails.eCertPin+"</b></li>";
			}
			if(!isEmpty(eCertMerchantDetails2.updatedPostDetails.eCertExpDate)){
				redeemECert4_DynamicText += "<li><div class='clearboth hrow'></div></li><li>eCertificate ExpDate:<b>"+eCertMerchantDetails2.updatedPostDetails.eCertExpDate+"</b></li>";
			}			

			if (eCertMerchantDetails2.updatedPostDetails.isMobilized){
				noteToCashierText = "<p><span><b>Note to Cashier:</b></span> Please process as a store gift card.</p>";
				mobilzedmessageText = "<li><div class='clearboth hrow'></div></li><li><span class='orangetxt boldtext'>No Printing Necessary!</span></li><li><span>"+eCertMerchantDetails2.updatedPostDetails.modeDesc+"</span>eCertificates are able to be used in-store right from your mobile device! Please show this eCertificate to the Cashier at time of purchase.<div class='clearboth'></li>";
				redeemECert4_DynamicText += mobilzedmessageText;
			}

			$("#redeemECert4_DynamicText").html("<ul>"+redeemECert4_DynamicText+"</ul>");

			if (!isEmpty(eCertMerchantDetails2.updatedPostDetails.partnerPhone)) {
				callableMsgText = "<center><a href='tel:"+eCertMerchantDetails2.updatedPostDetails.partnerPhone+"' onClick='trackThis(); return true' class='ui-btn ui-btn-corner-all ui-shadow ui-btn-up-c' style='line-height: 20px;border:1px solid #FB4C0D'><span class='ui-btn-inner ui-btn-corner-all' ><span class='ui-btn-text'>Click to Call "+modeDescription+" Now</span></span></a></center>";
			}

			$("#redeemECert4_noteToCashier").html(noteToCashierText);
			$("#redeemECert4_callableMsg").html(callableMsgText);

			if (!isEmpty(eCertMerchantDetails2.updatedPostDetails.modeDesc)) {
				$("#redeemECert4_MerchantName3").html(eCertMerchantDetails2.updatedPostDetails.modeDesc);
				$("#redeemECert4_MerchantName2").html(eCertMerchantDetails2.updatedPostDetails.modeDesc);
			}
			if (!isEmpty(eCertMerchantDetails2.updatedPostDetails.partnerTerms)) {
				$("#redeemECert4_MerchantTerms").html(eCertMerchantDetails2.updatedPostDetails.partnerTerms);
			}	

			if (!isEmpty(eCertMerchantDetails2.updatedPostDetails.redeemInstruct)) {
				$("#redeemECert4_redeemInstruct").html(eCertMerchantDetails2.updatedPostDetails.redeemInstruct);				
			}	
		}
	}catch(err){
		showSysException(err);
	}
}
//**** redeemCashbackEcert4 page end ****/

/*
 * Description: posting data to webservices
 */

dfs.crd.cbb.postECertCBB = function(eCertMerchantDetails2) {
	try{
		var redeemDetails;
		if (!jQuery.isEmptyObject(eCertMerchantDetails2)) {
			
			var RedeemCbbCreditDirectURL = RESTURL + "rewards/v2/redeem";
			var modeCode = eCertMerchantDetails2.modeCode;
			var amount = eCertMerchantDetails2.modeAmount;
			if (amount.indexOf('.') != -1) {
				amount = amount.split(".")[0];
			}

			var dataJSON={"modeCode":modeCode,"amount":amount,"orderQty":"1"};
			var dataJSONString=JSON.stringify(dataJSON);
			showSpinner();
			$.ajax({
				type : "POST",
				url : RedeemCbbCreditDirectURL,
				async : false,
				dataType : 'json',
				data :dataJSONString,
				headers:preparePostHeader(),
				success : function(responseData, status, jqXHR) {
					hideSpinner();
					if (jqXHR.status != 200 & jqXHR.status != 204) {
						var code = getResponseStatusCode(jqXHR);
						errorHandler(code, '','redeemCashbackEcert3');
					} else {
						redeemDetails = responseData;
					}
				},
				error : function(jqXHR, textStatus, errorThrown) {
					hideSpinner();
					cpEvent.preventDefault();
					var code = getResponseStatusCode(jqXHR);
					switch (code) {
					case "1607":
					case "1608":
						var errorMessage = errorCodeMap.INSUFFICIENT_CBB_BALANCE_FOR_REDEEM_ECERT;
						var promoCodeTextData = [];
						promoCodeTextData['ACHome_CashBackBonusBalance'] = "$" + globalEarnRewardAmount;
						var parseContentText = parseContent(
								errorMessage, promoCodeTextData);
						if (!isEmpty(parseContentText)) {
							errorHandler(code, parseContentText,
							'redeemCashbackEcert3');
						} else {
							errorHandler('0', '', 'redeemCashbackEcert3');
						}
						break;
					default:
						errorHandler(code, '', 'redeemCashbackEcert3');
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


function trackThis() {
try{
	var appnd='&MRRphone=' + modeDescription;
	vsTracking = new Image();
	vsTracking.src = '/images/zag.gif?Log=1&cb=' + new Date().getTime()
	+ '&dl=' + window.location.pathname + '&dd=discover.com&dr='
	+ document.referrer + '&MRRphone='+modeDescription;
	}catch(err){
		showSysException(err);
	}
}