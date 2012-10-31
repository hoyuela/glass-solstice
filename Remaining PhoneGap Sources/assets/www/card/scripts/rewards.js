//~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~ CBB SIGNUP START ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
//************cashbackBonusLanding start*************
/** Name Space**/
dfs.crd.rwds = dfs.crd.rwds || {};
/** **/

//for ecert logos
var AVAILABLE_PARTNERS_IMAGE_BASE_URL=HREF_URL+"mobile/rewards/advanced/images/merchant-logos/listing/available/";
var UNAVAILABLE_PARTNERS_IMAGE_BASE_URL=HREF_URL+"mobile/rewards/advanced/images/merchant-logos/listing/unavailable/";
var AVAILABLE_PARTNERS_IMAGE_CONFIRM_URL=HREF_URL+"mobile/rewards/advanced/images/merchant-logos/ecert/";
function cashbackBonusLandingLoad() {
	trafficSource = MOBILE_SPLASH_NAV;

}

//*********** cashbackSignup1 start ******************

function cashbackBonusSignup1Load(){
	try{	
        if(acLiteModeFlag)
			{
				cpEvent.preventDefault();					
				errorHandler("acLiteOutageMode_ACL","","cashbackBonusSignup1");
			}
	      else{
		dfs.crd.rwds.populateCashBackBonusSignup1('CASHBACK_BONUS_SIGNUP_DETAILS');
		}
	}catch(err){
		showSysException(err);
	}
}

dfs.crd.rwds.populateCashBackBonusSignup1 = function(pageName){
	try{
		var cashbackbonusSignupDetails=dfs.crd.rwds.getCashBackSignupData(pageName);
		if (!jQuery.isEmptyObject(cashbackbonusSignupDetails)){
			dfs.crd.rwds.populateCBBLandingPromitionsList(cashbackbonusSignupDetails);
		}
	}catch(err){
		showSysException(err);
	}
}

dfs.crd.rwds.getCashBackSignupData = function(pageId){
	try{
		var cashbackbonusSignupDetails;	
		var newDate = new Date();	
		var CASHBACKBONUSURL = RESTURL + "rewards/v2/promotions?"+newDate+"";
		var cashbackbonusSignupDetails=getDataFromCache(pageId);
		if (jQuery.isEmptyObject(cashbackbonusSignupDetails)) {
			showSpinner();
			$.ajax({
				type : "GET",
				url : CASHBACKBONUSURL,
				async : false,
				dataType : 'json',
				headers:prepareGetHeader(),
				success : function(responseData, status, jqXHR) {
					hideSpinner();
					if (jqXHR.status != 200) {
						var code=getResponseStatusCode(jqXHR);
						errorHandler(code,'','cashbackBonusSignup1');
					} else {
						cashbackbonusSignupDetails = responseData;
						putDataToCache(pageId,cashbackbonusSignupDetails);
					}
				},
				error : function(jqXHR, textStatus, errorThrown) {
					hideSpinner();
					var code=getResponseStatusCode(jqXHR);
					errorHandler(code,'','cashbackBonusSignup1');
				}
			});
		}else{
		}
		return cashbackbonusSignupDetails;
	}catch(err){
		showSysException(err);
	}
}


dfs.crd.rwds.populateCBBLandingPromitionsList = function(cashbackbonusSignupDetails){

	try{		
		if (!jQuery.isEmptyObject(cashbackbonusSignupDetails)) {
			$("#cashbackSignup1_CashBackbonusBalance").html("$"+ globalEarnRewardAmount);

			var promoCodeJson=dfs.crd.rwds.getRewardsPCCData("cashbackSignup1");
			/*var promoCodeJson;
			var rewardsJsonDetails=getDataFromCache("REWARDS_JSON");
			if (jQuery.isEmptyObject(rewardsJsonDetails)) {
				promoCodeJson = getContentJson("rewards"); 
				putDataToCache("REWARDS_JSON", promoCodeJson);
			}else{
				promoCodeJson=rewardsJsonDetails;
			}*/


			var jsonPromoTextMissingCounter=0;
			var promoCodeText="<ul data-inset='true' data-theme='d' data-role='listview'>";
			var promotionOutputList= cashbackbonusSignupDetails.promotionOutputList;

			dfs.crd.rwds.appendCommingSoonInfoPCC(cashbackbonusSignupDetails);

			if (!jQuery.isEmptyObject(promotionOutputList)) {
				for(promotionOutputListCounter in promotionOutputList){
					var promotionInfo=promotionOutputList[promotionOutputListCounter];
					var resPromoCode=promotionInfo.promoCode;
					var isEnrolled=promotionInfo.isEnrolled;
					var isBdayPromo=promotionInfo.isBdayPromo;
					var promoEffPeriod=promotionInfo.promoEffPeriod;
					var incentiveTypeCodeR=cashbackbonusSignupDetails.incentiveTypeCode;
					var incentiveCodeR=cashbackbonusSignupDetails.incentiveCode;

					var promoCodeTextData = [];	
					var isDoubleDaysPromo=promotionInfo.isDoubleDaysPromo;
					if(isBdayPromo){
						promoCodeTextData['bdayMonthYear'] = promotionInfo.bdayPromoMonthYear;
						resPromoCode="BDAY";
						incentiveCodeR="000001";	
					}else{

					}

					var jsonPromoCodeText;
					if(!jQuery.isEmptyObject(promoCodeJson)){
						var key=resPromoCode+"_INFO"+"_"+incentiveTypeCodeR+"_"+incentiveCodeR;
						jsonPromoCodeText=promoCodeJson[key];
					}
					if (isEmpty(jsonPromoCodeText)) {
						jsonPromoTextMissingCounter++;
						continue;
					}

					var signupStatusText;	
					var promotionInfoString=JSON.stringify(promotionInfo);
					var bdayPromoText;
					if(isEnrolled){
						signupStatusText="<span class='signin orangetxt'>You&#39;re Signed Up!</span>";
					}else{
						signupStatusText="<a href='#' data-role='button' class='ui-block-b ui-btn-up-c' onclick='dfs.crd.rwds.showCBBDetailsForPromocode("+promotionInfoString+");'><span class='ui-btn-inner ui-btn-corner-all' aria-hidden='true'><span class='ui-btn-text'>Learn&nbsp;More</span></span></a>";
					}
					promoCodeTextData['signupStatusText'] = signupStatusText;
					promoCodeTextData['CBBPromotionObj'] = promotionInfoString;	
					if(!isEmpty(promoEffPeriod)){
						promoCodeTextData['promoEffPeriod'] = promoEffPeriod;						
					}
					var parseContentText=parseContent(jsonPromoCodeText,promoCodeTextData);
					if (typeof parseContentText != 'undefined' &&  parseContentText != "") {
						promoCodeText += parseContentText;
					}				
				}			
				if(jsonPromoTextMissingCounter == promotionOutputList.length){
					promoCodeText += "<li class='DD ui-li ui-li-static ui-body-d'><span><ul class='fontsize13px'><li>There are currently no promotions available for enrollment.</li></ul></li>"
				}			
			}else{
				promoCodeText += "<li class='DD ui-li ui-li-static ui-body-d'><span><ul class='fontsize13px'><li>There are currently no promotions available for enrollment.</li></ul></li>"
			}
			promoCodeText += "</ul>";
			$("#cashbackBonusSignup_promotionList").html(promoCodeText);
		} 
	}catch(err){
		showSysException(err);
	}
}

dfs.crd.rwds.showCBBDetailsForPromocode = function(cashbackbonusSignupSelected){
	try{
		if (!jQuery.isEmptyObject(cashbackbonusSignupSelected)) {
			putDataToCache("CASHBACK_BONUS_SIGNUP_SELECTED",cashbackbonusSignupSelected);
			navigation('../rewards/cashbackBonusSignup2');		
		}
	}catch(err){
		showSysException(err);
	}
}
//*********** cashbackSignup1 end ******************




//*********** cashbackSignup2 start ******************
function cashbackBonusSignup2Load(){
	try{
		var validPriorPagesOfCBBSignup2= new Array("cashbackBonusSignup1");
		if(jQuery.inArray(fromPageName, validPriorPagesOfCBBSignup2) > -1 ){			
			dfs.crd.rwds.populateCashBackBonusSignup2();
		}else{
			cpEvent.preventDefault();
			history.back();
		}		
	}catch(err){
		showSysException(err);
	}
}
dfs.crd.rwds.populateCashBackBonusSignup2 = function(){
	try{
		dfs.crd.rwds.populatecashbackSignup2Div();
	}catch(err){
		showSysException(err);
	}
}
dfs.crd.rwds.populatecashbackSignup2Div = function(){
	try{
		var cashbackbonusSignupSelected=getDataFromCache("CASHBACK_BONUS_SIGNUP_SELECTED");
		var cashbackbonusSignupDetails=getDataFromCache("CASHBACK_BONUS_SIGNUP_DETAILS");

		var promoCodeJson=dfs.crd.rwds.getRewardsPCCData("cashbackBonusSignup2");
		/*var promoCodeJson;
		var rewardsJsonDetails=getDataFromCache("REWARDS_JSON");
		if (jQuery.isEmptyObject(rewardsJsonDetails)) {
			promoCodeJson = getContentJson("rewards"); 
			putDataToCache("REWARDS_JSON", promoCodeJson);
		}else{
			promoCodeJson=rewardsJsonDetails;
		}*/


		if (!jQuery.isEmptyObject(cashbackbonusSignupSelected) && !jQuery.isEmptyObject(cashbackbonusSignupDetails)) {
			var resPromoCode=cashbackbonusSignupSelected.promoCode;
			var isEnrolled=cashbackbonusSignupSelected.isEnrolled;
			var isBdayPromo=cashbackbonusSignupSelected.isBdayPromo; 
			var incentiveTypeCodeR=cashbackbonusSignupDetails.incentiveTypeCode;
			var incentiveCodeR=cashbackbonusSignupDetails.incentiveCode;
			var promoEffectiveDate=cashbackbonusSignupSelected.promoEffectiveDate;
			var promoExpirationDate=cashbackbonusSignupSelected.promoExpirationDate;
			var promoCodeTextData = [];	
			var promoEffPeriod=cashbackbonusSignupSelected.promoEffPeriod;

			if(!isEmpty(promoEffectiveDate) && !isEmpty(promoExpirationDate)){
				promoCodeTextData['promoEffectiveDate'] = promoEffectiveDate;
				promoCodeTextData['promoExpirationDate'] = promoExpirationDate;
			}
			if(!isEmpty(promoEffPeriod)){
				promoCodeTextData['promoEffPeriod'] = promoEffPeriod;						
			}

			if(isBdayPromo){
				var firstName=cashbackbonusSignupDetails.firstName;
				var bdayPromoMonth=cashbackbonusSignupDetails.bdayPromoMonth;
				promoCodeTextData['bdayMonth'] = cashbackbonusSignupSelected.bdayPromoMonth;
				promoCodeTextData['firstName'] = firstName;
				resPromoCode="BDAY";
				incentiveCodeR="000001";
			}else{

			}

			var promoCodeText;
			if(isEnrolled){
				if(!jQuery.isEmptyObject(promoCodeJson)){
					var key=resPromoCode+"_CONFIRM_BODY"+"_"+incentiveTypeCodeR+"_"+incentiveCodeR;
					promoCodeText=promoCodeJson[key];
					killDataFromCache('CASHBACK_BONUS_SIGNUP_SELECTED');
				}
			}else{
				if(!jQuery.isEmptyObject(promoCodeJson)){
					var key=resPromoCode+"_BODY"+"_"+incentiveTypeCodeR+"_"+incentiveCodeR;
					promoCodeText=promoCodeJson[key];
				}
				var signupStatusTextButton="<div id='signupStatusText'><div class='txtcenteraln r3matchbtn'><a data-theme='c' data-role='button' onclick='dfs.crd.rwds.postCashbackSignupData()' href='#' class='ui-btn ui-btn-corner-all ui-shadow ui-btn-up-c'><span class='ui-btn-inner ui-btn-corner-all' aria-hidden='true'><span class='ui-btn-text'>Sign Up</span></span></a></div></div>";
				promoCodeTextData['signupStatusTextButton'] = signupStatusTextButton;
			}

			promoCodeText= parseContent(promoCodeText,promoCodeTextData);
			$("#cashBackBonusSignUp_Div").html(promoCodeText);
		}
	}catch(err){
		showSysException(err);
	}
}

dfs.crd.rwds.postCashbackSignupData = function(){
	try{
		var cashbackbonusSignupSelected=getDataFromCache("CASHBACK_BONUS_SIGNUP_SELECTED");
		if (!jQuery.isEmptyObject(cashbackbonusSignupSelected)) {
			var promoCode=cashbackbonusSignupSelected.promoCode;			
			var updatedDataAfterEnrollment=dfs.crd.rwds.postDataenrollForSelectedCBBPromotion(promoCode);
			if (!jQuery.isEmptyObject(updatedDataAfterEnrollment)) {
				navigation('../rewards/cashbackBonusSignup3',false);
			}		
		}
	}catch(err){
		showSysException(err);
	}
}

//*********** cashbackSignup3 start ******************

function cashbackBonusSignup3Load(){
	try{
		var cashbackbonusSignupSelected=getDataFromCache("CASHBACK_BONUS_SIGNUP_SELECTED");
		var cashbackbonusSignupDetails=getDataFromCache("CASHBACK_BONUS_SIGNUP_DETAILS");

		var promoCodeJson=dfs.crd.rwds.getRewardsPCCData("cashbackBonusSignup2");
		/*var promoCodeJson;
		var rewardsJsonDetails=getDataFromCache("REWARDS_JSON");
		if (jQuery.isEmptyObject(rewardsJsonDetails)) {
			promoCodeJson = getContentJson("rewards"); 
			putDataToCache("REWARDS_JSON", promoCodeJson);
		}else{
			promoCodeJson=rewardsJsonDetails;
		}*/


		if (!jQuery.isEmptyObject(cashbackbonusSignupSelected) && !jQuery.isEmptyObject(cashbackbonusSignupDetails)) {
			var resPromoCode=cashbackbonusSignupSelected.promoCode;
			var incentiveTypeCodeR=cashbackbonusSignupDetails.incentiveTypeCode;
			var incentiveCodeR=cashbackbonusSignupDetails.incentiveCode;
			var promoCodeText;
			var promoCodeTextData = [];	
			var promoEffPeriod=cashbackbonusSignupSelected.promoEffPeriod;
			var isBdayPromo=cashbackbonusSignupSelected.isBdayPromo;
			
			if(isBdayPromo){
				resPromoCode="BDAY";
				incentiveCodeR="000001";
			}
			if(!isEmpty(promoEffPeriod)){
				promoCodeTextData['promoEffPeriod'] = promoEffPeriod;						
			}

			if(!jQuery.isEmptyObject(promoCodeJson)){
				var key=resPromoCode+"_CONFIRM"+"_"+incentiveTypeCodeR+"_"+incentiveCodeR;
				promoCodeText=promoCodeJson[key];
			}			
			promoCodeText= parseContent(promoCodeText,promoCodeTextData);			
			$("#cashBackBonusSignUpConfirm_Div").html(promoCodeText);
			killDataFromCache('CASHBACK_BONUS_SIGNUP_DETAILS');
			killDataFromCache('CASHBACK_BONUS_SIGNUP_SELECTED');
		}
	}catch(err){
		showSysException(err);
	}
}

//*********** cashbackSignup3 end ******************

dfs.crd.rwds.postDataenrollForSelectedCBBPromotion = function(promoCode) {
	try{
		var CASHBACKBONUSURL = RESTURL + "rewards/v2/promotions";
		var postData;
		var dataJSON={"offerCode":promoCode};
		var dataJSONString=JSON.stringify(dataJSON);
		var dataJSONString=JSON.stringify(dataJSON);
		showSpinner();
		$
		.ajax({
			type : "POST",
			url : CASHBACKBONUSURL,
			async : false,
			dataType : 'json',
			data :dataJSONString,
			headers:preparePostHeader(),
			success : function(response, status, jqXHR) {
				hideSpinner();
				if (jqXHR.status != 200 & jqXHR.status != 204) {
					var code=getResponseStatusCode(jqXHR);
					errorHandler(code,'','cashbackBonusSignup2');
				} else {
					postData=response;
				}
			},
			error : function(jqXHR, textStatus, errorThrown) {
				hideSpinner();
				var code=getResponseStatusCode(jqXHR);
				errorHandler(code,'','cashbackBonusSignup2');
			}
		});
		return postData;
	}catch(err){
		showSysException(err);
	}
}

//~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~ CBB SIGNUP END ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~





//~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~ MILES SIGNUP START ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
//*********** miles Landing************************

function milesRedeemLoad(){
try{
if(acLiteModeFlag)
			{
				cpEvent.preventDefault();					
				errorHandler("acLiteOutageMode_ACL","","milesHome");
			}
			}catch(err){
		showSysException(err);
	}
}
function milesHomeLoad(){
	trafficSource = MOBILE_SPLASH_NAV;
}

//*********** milesSignup1 start ******************

function milesSignup1Load(){
	try{	
 if(acLiteModeFlag)
			{
				cpEvent.preventDefault();					
				errorHandler("acLiteOutageMode_ACL","","milesSignup1");
			}
         else{			
		dfs.crd.rwds.populateMilesSignup1('MILES_SIGNUP_DETAILS');
		    }	
	}catch(err){
		showSysException(err);
	}
}

dfs.crd.rwds.populateMilesSignup1 = function(pageName){
	try{
		var milesSignupDetails=dfs.crd.rwds.getMilesSignupData(pageName);
		if (!jQuery.isEmptyObject(milesSignupDetails)){
			dfs.crd.rwds.populateMilesLandingPromitionsList(milesSignupDetails);
		}
	}catch(err){
		showSysException(err);
	}
}

dfs.crd.rwds.getMilesSignupData = function(pageId){
	try{
		var newDate = new Date();	
		var REWARDS_PROMOTIONS_URL = RESTURL + "rewards/v2/promotions?"+newDate+"";
		var milesSignupDetails=getDataFromCache(pageId);
		if (jQuery.isEmptyObject(milesSignupDetails)) {
			showSpinner();
			$.ajax({
				type : "GET",
				url : REWARDS_PROMOTIONS_URL,
				async : false,
				dataType : 'json',
				headers:prepareGetHeader(),
				success : function(responseData, status, jqXHR) {
					hideSpinner();
					if (jqXHR.status != 200) {
						var code=getResponseStatusCode(jqXHR);
						errorHandler(code,'','milesSignup1');
					} else {
						milesSignupDetails = responseData;
						putDataToCache(pageId,milesSignupDetails);
					}
				},
				error : function(jqXHR, textStatus, errorThrown) {
					hideSpinner();
					var code=getResponseStatusCode(jqXHR);
					errorHandler(code,'','milesSignup1');
				}
			});
		}else{
		}
		return milesSignupDetails;
	}catch(err){
		showSysException(err);
	}
}

dfs.crd.rwds.populateMilesLandingPromitionsList = function(milesSignupDetails){

	try{		
		if (!jQuery.isEmptyObject(milesSignupDetails)) {	
			$("#milesSignup1_CashBackbonusBalance").html(globalEarnRewardAmount);

			var promoCodeJson=dfs.crd.rwds.getRewardsPCCData("milesSignup1");			
			/*var promoCodeJson;
			var rewardsJsonDetails=getDataFromCache("REWARDS_JSON");
			if (jQuery.isEmptyObject(rewardsJsonDetails)) {
				promoCodeJson = getContentJson("rewards"); 
				putDataToCache("REWARDS_JSON", promoCodeJson);
			}else{
				promoCodeJson=rewardsJsonDetails;
			}*/


			var jsonPromoTextMissingCounter=0;
			var promoCodeText="<ul data-inset='true' data-theme='d' data-role='listview'>";
			var promotionOutputList= milesSignupDetails.promotionOutputList;
			dfs.crd.rwds.appendCommingSoonInfoPCC(milesSignupDetails);
			if (!jQuery.isEmptyObject(promotionOutputList)) {
				for(promotionOutputListCounter in promotionOutputList){
					var promotionInfo=promotionOutputList[promotionOutputListCounter];
					var resPromoCode=promotionInfo.promoCode;
					var isEnrolled=promotionInfo.isEnrolled;
					var isBdayPromo=promotionInfo.isBdayPromo;
					var incentiveTypeCodeR=milesSignupDetails.incentiveTypeCode;
					var incentiveCodeR=milesSignupDetails.incentiveCode;
					var promoEffPeriod=promotionInfo.promoEffPeriod;

					var promoCodeTextData = [];				
					if(isBdayPromo){
						promoCodeTextData['bdayMonthYear'] = promotionInfo.bdayPromoMonthYear;
						resPromoCode="BDAY";
						incentiveCodeR="000002";
					}else{

					}

					var jsonPromoCodeText;
					if(!jQuery.isEmptyObject(promoCodeJson)){
						var key=resPromoCode+"_INFO"+"_"+incentiveTypeCodeR+"_"+incentiveCodeR;
						jsonPromoCodeText=promoCodeJson[key];
					}
					if (isEmpty(jsonPromoCodeText)) {
						jsonPromoTextMissingCounter++;
						continue;
					}

					var signupStatusText;	
					var promotionInfoString=JSON.stringify(promotionInfo);
					var bdayPromoText;
					if(isEnrolled){
						signupStatusText="<span class='signin orangetxt'>You&#39;re Signed Up!</span>";
					}else{
						signupStatusText="<a href='#' data-role='button' class='ui-block-b ui-btn-up-c' onclick='dfs.crd.rwds.showMI2DetailsForPromocode("+promotionInfoString+");'><span class='ui-btn-inner ui-btn-corner-all' aria-hidden='true'><span class='ui-btn-text'>Learn&nbsp;More</span></span></a>";
					}
					promoCodeTextData['signupStatusText'] = signupStatusText;
					promoCodeTextData['CBBPromotionObj'] = promotionInfoString;			
					if(!isEmpty(promoEffPeriod)){
						promoCodeTextData['promoEffPeriod'] = promoEffPeriod;						
					}
					var parseContentText=parseContent(jsonPromoCodeText,promoCodeTextData);
					if (!isEmpty(parseContentText)) {
						promoCodeText += parseContentText;
					}				
				}			
				if(jsonPromoTextMissingCounter == promotionOutputList.length){
					promoCodeText += "<li class='DD ui-li ui-li-static ui-body-d'><span class='redtext'><ul><li class='boldtext'>There are currently no promotions available for enrollment.</li></ul></li>"
				}			
			}else{
				promoCodeText += "<li class='DD ui-li ui-li-static ui-body-d'><span class='redtext'><ul><li class='boldtext'>There are currently no promotions available for enrollment.</li></ul></li>"
			}
			promoCodeText += "</ul>";
			$("#milesSignup1_promotionList").html(promoCodeText);
		}
	}catch(err){
		showSysException(err);
	}
}

dfs.crd.rwds.showMI2DetailsForPromocode = function(milesSignupSelected){
	try{
		if (!jQuery.isEmptyObject(milesSignupSelected)) {
			putDataToCache("MILES_SIGNUP_SELECTED",milesSignupSelected);
			navigation('../rewards/milesSignup2');		
		}
	}catch(err){
		showSysException(err);
	}
}
//*********** milesSignup1 end ******************


//*********** milesSignup2 start ******************
function milesSignup2Load(){
	try{

		var validPriorPagesOfMI2Signup2= new Array("milesSignup1");
		if(jQuery.inArray(fromPageName, validPriorPagesOfMI2Signup2) > -1 ){			
			dfs.crd.rwds.populateMilesSignup2();
		}else{
			cpEvent.preventDefault();
			history.back();
		}
	}catch(err){
		showSysException(err);
	}
}

dfs.crd.rwds.populateMilesSignup2 = function(){
	try{
		dfs.crd.rwds.populateMilesSignup2Div();
	}catch(err){
		showSysException(err);
	}
}

dfs.crd.rwds.populateMilesSignup2Div = function(){
	try{
		var milesSignupSelected=getDataFromCache("MILES_SIGNUP_SELECTED");
		var milesSignupDetails=getDataFromCache("MILES_SIGNUP_DETAILS");
		if (!jQuery.isEmptyObject(milesSignupSelected) && !jQuery.isEmptyObject(milesSignupDetails)) {

			var promoCodeJson=dfs.crd.rwds.getRewardsPCCData("milesSignup2");
			/*var promoCodeJson;
			var rewardsJsonDetails=getDataFromCache("REWARDS_JSON");
			if (jQuery.isEmptyObject(rewardsJsonDetails)) {
				promoCodeJson = getContentJson("rewards"); 
				putDataToCache("REWARDS_JSON", promoCodeJson);
			}else{
				promoCodeJson=rewardsJsonDetails;
			}*/


			var resPromoCode=milesSignupSelected.promoCode;
			var isEnrolled=milesSignupSelected.isEnrolled;
			var isBdayPromo=milesSignupSelected.isBdayPromo;
			var incentiveTypeCodeR=milesSignupDetails.incentiveTypeCode;
			var incentiveCodeR=milesSignupDetails.incentiveCode;

			var promoEffectiveDate=milesSignupSelected.promoEffectiveDate;
			var promoExpirationDate=milesSignupSelected.promoExpirationDate;
			var promoCodeTextData = [];	
			var promoEffPeriod=milesSignupSelected.promoEffPeriod;
			if(!isEmpty(promoEffectiveDate) && !isEmpty(promoExpirationDate)){
				promoCodeTextData['promoEffectiveDate'] = promoEffectiveDate;
				promoCodeTextData['promoExpirationDate'] = promoExpirationDate;
			}
			if(!isEmpty(promoEffPeriod)){
				promoCodeTextData['promoEffPeriod'] = promoEffPeriod;						
			}
			if(isBdayPromo){
				var firstName=milesSignupDetails.firstName;
				var bdayPromoMonth=milesSignupDetails.bdayPromoMonth;
				promoCodeTextData['bdayMonth'] = milesSignupSelected.bdayPromoMonth;
				promoCodeTextData['firstName'] = firstName;
				resPromoCode="BDAY";
				incentiveCodeR="000002";
			}else{

			}

			var promoCodeText;
			if(isEnrolled){
				if(!jQuery.isEmptyObject(promoCodeJson)){
					var key=resPromoCode+"_CONFIRM_BODY"+"_"+incentiveTypeCodeR+"_"+incentiveCodeR;
					promoCodeText=promoCodeJson[key];
				}
			}else{
				if(!jQuery.isEmptyObject(promoCodeJson)){
					var key=resPromoCode+"_BODY"+"_"+incentiveTypeCodeR+"_"+incentiveCodeR;
					promoCodeText=promoCodeJson[key];
				}
				var signupStatusTextButton="<div id='signupStatusText'><div class='txtcenteraln r3matchbtn'><a data-theme='c' data-role='button' onclick='dfs.crd.rwds.postMilesSignupData()' href='#' class='ui-btn ui-btn-corner-all ui-shadow ui-btn-up-c'><span class='ui-btn-inner ui-btn-corner-all' aria-hidden='true'><span class='ui-btn-text'>Sign Up</span></span></a></div></div>";
				promoCodeTextData['signupStatusTextButton'] = signupStatusTextButton;
			}
			promoCodeText= parseContent(promoCodeText,promoCodeTextData);
			$("#milesSignup2_Div").html(promoCodeText);
		}
	}catch(err){
		showSysException(err);
	}
}
dfs.crd.rwds.postMilesSignupData = function(){
	try{
		var milesSignupDetails=getDataFromCache("MILES_SIGNUP_SELECTED");
		if (!jQuery.isEmptyObject(milesSignupDetails)) {
			var promoCode=milesSignupDetails.promoCode;
			var updatedDataAfterEnrollment=dfs.crd.rwds.postDataenrollForSelectedMI2Promotion(promoCode);
			if (!jQuery.isEmptyObject(updatedDataAfterEnrollment)) {
				navigation('../rewards/milesSignup3',false);
			}		
		}
	}catch(err){
		showSysException(err);
	}
}
//*********** milesSignup2 end ******************

//*********** milesSignup3 start ******************
function milesSignup3Load(){
	try{
		var milesSignupSelected=getDataFromCache("MILES_SIGNUP_SELECTED");
		var milesSignupDetails=getDataFromCache("MILES_SIGNUP_DETAILS");
		if (!jQuery.isEmptyObject(milesSignupSelected) && !jQuery.isEmptyObject(milesSignupDetails)) {
			var promoCodeJson=dfs.crd.rwds.getRewardsPCCData("cashbackBonusSignup2");
			/*var promoCodeJson;
			var rewardsJsonDetails=getDataFromCache("REWARDS_JSON");
			if (jQuery.isEmptyObject(rewardsJsonDetails)) {
				promoCodeJson = getContentJson("rewards"); 
				putDataToCache("REWARDS_JSON", promoCodeJson);
			}else{
				promoCodeJson=rewardsJsonDetails;
			}*/

			var resPromoCode=milesSignupSelected.promoCode;
			var incentiveTypeCodeR=milesSignupDetails.incentiveTypeCode;
			var incentiveCodeR=milesSignupDetails.incentiveCode;
			var promoCodeText;
			var promoCodeTextData = [];	
			var promoEffPeriod=milesSignupSelected.promoEffPeriod;
			var isBdayPromo=milesSignupSelected.isBdayPromo;
			
			if(isBdayPromo){
				resPromoCode="BDAY";
				incentiveCodeR="000002";
			}

			if(!isEmpty(promoEffPeriod)){
				promoCodeTextData['promoEffPeriod'] = promoEffPeriod;						
			}			
			if(!jQuery.isEmptyObject(promoCodeJson)){
				var key=resPromoCode+"_CONFIRM"+"_"+incentiveTypeCodeR+"_"+incentiveCodeR;
				promoCodeText=promoCodeJson[key];
			}			
			promoCodeText= parseContent(promoCodeText,promoCodeTextData);
			$("#milesSignupConfirm_Div").html(promoCodeText);
			killDataFromCache('MILES_SIGNUP_SELECTED');
			killDataFromCache('MILES_SIGNUP_DETAILS');			
		}			
	}catch(err){
		showSysException(err);
	}
}
//*********** milesSignup3 end ******************

dfs.crd.rwds.postDataenrollForSelectedMI2Promotion = function(promoCode) {
	try{
		var MILURL = RESTURL + "rewards/v2/promotions";
		var postData;
		var dataJSON={"offerCode":promoCode};
		var dataJSONString=JSON.stringify(dataJSON);
		showSpinner();
		$
		.ajax({
			type : "POST",
			url : MILURL,
			async : false,
			dataType : 'json',
			data :dataJSONString,
			headers:preparePostHeader(),
			success : function(response, status, jqXHR) {
				hideSpinner();
				if (jqXHR.status != 200 & jqXHR.status != 204) {
					var code=getResponseStatusCode(jqXHR);
					errorHandler(code,'','milesSignup2');
				} else {
					postData=response;
				}
			},
			error : function(jqXHR, textStatus, errorThrown) {
				hideSpinner();
				var code=getResponseStatusCode(jqXHR);
				errorHandler(code,'','milesSignup2');
			}
		});
		return postData;
	}catch(err){
		showSysException(err);
	}
}

//~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~ Miles SIGNUP END ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~


//~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~ REDEEM HISTORY START ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

function redemptionHistoryLoad(){
	try{
 if(acLiteModeFlag)
			{
				cpEvent.preventDefault();					
				errorHandler("acLiteOutageMode_ACL","","redemptionHistory");
			}
			else{
		         dfs.crd.rwds.renderRedeemHistoryPage("REDEEM_HISTORY");
		        }
	}catch(err){
		showSysException(err);
	}
}


dfs.crd.rwds.renderRedeemHistoryPage=function(pageName){
	try{
		var redeemHistory =dfs.crd.rwds.getDirectReedemData(pageName);
		if (!jQuery.isEmptyObject(redeemHistory)){
			dfs.crd.rwds.populatRedeemptionHistory(redeemHistory);
		}
	}catch(err){
		showSysException(err);
	}
}

dfs.crd.rwds.getDirectReedemData=function(pageId) {
	try{
		var newDate = new Date();	
		var HISTORYURL = RESTURL + "rewards/v2/orderhistory?"+newDate+"";
		var redeemHistory =getDataFromCache(pageId);
		if (jQuery.isEmptyObject(redeemHistory)) {
			showSpinner();
			$.ajax({
				type : "GET",
				url : HISTORYURL,
				async : false,
				dataType : 'json',
				headers:prepareGetHeader(),
				success : function(responseData, status, jqXHR){
					hideSpinner();
					redeemHistory = responseData;
					//putDataToCache(pageId, redeemHistory);
				},
				error : function(jqXHR, textStatus, errorThrown){
					hideSpinner();
					cpEvent.preventDefault();
					var code=getResponseStatusCode(jqXHR);
					errorHandler(code,'','redemptionHistory');
				}
			});
		}
		return redeemHistory;
	}
	catch(err){
		showSysException(err);
	}
}


dfs.crd.rwds.populatRedeemptionHistory=function(responseData){
	try{
		if (!jQuery.isEmptyObject(responseData)){
			var orderHistoryList = responseData.orderHistory;
			var rewardinfoHistoryData = [];
			var htmlText = "";
			var jsonFile="";			
			var rewardsHistoryJSON={
					"DEFAULT_HISTORY":"<li class='rh_header ui-li ui-li-static ui-body-c'><div class='col1'><div>!~requestDate~!</div><div>!~itemDesc~!</div></div><div class='col2'>!~fullfillMedDesc~!</div><div class='col3'>!~dsbrstAmt~!</div><div class='col4'><span class='boldtext'>!~dsbrstCashAmt~!</span></div></li>",
					"PARTNER_HISTORY":"<li class='rh_header ui-li ui-li-static ui-body-c'><div class='col1'><div>!~requestDate~!</div><div>!~itemDesc~!</div></div><div class='col2'>!~fullfillMedDesc~!</div><div class='col3'>!~dsbrstAmt~!</div><div class='col4'><span class='boldtext'>!~dsbrstCashAmt~!</span></div></li>",
					"AMAZON_HISTORY_CBB":"<li class='rh_header ui-li ui-li-static ui-body-c'><div class='col1'><div>!~requestDate~!</div><div>!~itemDesc~!</div></div><div class='col2'>!~fullfillMedDesc~!</div><div class='col3'>!~dsbrstAmt~!</div><div class='col4'><span class='boldtext'>!~dsbrstCashAmt~!</span></div></li>",
					"AMAZON_HISTORY_MI2":"<li class='rh_header ui-li ui-li-static ui-body-c'><div class='col1'><div>!~requestDate~!</div><div>!~itemDesc~!</div></div><div class='col2'>!~fullfillMedDesc~!</div><div class='col3'>!~dsbrstAmt~!&nbsp;Miles</div><div class='col4'><span class='boldtext'>!~dsbrstCashAmt~!</span></div></li>",
					"CHARITY_HISTORY":"<li class='rh_header ui-li ui-li-static ui-body-c'><div class='col1'><div>!~requestDate~!</div><div>!~itemDesc~!</div></div><div class='col2'>!~fullfillMedDesc~!</div><div class='col3'>!~dsbrstAmt~!</div><div class='col4'><span class='boldtext'>!~dsbrstCashAmt~!</span></div></li>",
					"VENDOR_MERCHANDISE_HISTORY":"<li class='rh_header ui-li ui-li-static ui-body-c'><div class='col1'><div>!~requestDate~!</div><div>!~itemDesc~!</div></div><div class='col2'>!~fullfillMedDesc~!</div><div class='col3'>!~dsbrstAmt~!</div><div class='col4'><span class='boldtext'>!~dsbrstCashAmt~!</span></div></li>",
					"REVERSED_AMAZON_HISTORY_CBB":"<li class='rh_header ui-li ui-li-static ui-body-c'><div class='col1'><div>!~requestDate~!</div><div>!~itemDesc~!</div></div><div class='col2'>!~fullfillMedDesc~!</div><div class='col3'>!~dsbrstAmt~!</div><div class='col4'><span class='boldtext'>!~dsbrstCashAmt~!</span></div></li>",
					"REVERSED_AMAZON_HISTORY_MI2":"<li class='rh_header ui-li ui-li-static ui-body-c'><div class='col1'><div>!~requestDate~!</div><div>!~itemDesc~!</div></div><div class='col2'>!~fullfillMedDesc~!</div><div class='col3'>!~dsbrstAmt~!&nbsp;Miles</div><div class='col4'><span class='boldtext'>!~dsbrstCashAmt~!</span></div></li>"
			}
			
			if(orderHistoryList.length!=0){				
				var htmlTextHeader = "<ul class='redeem_history ui-listview ui-listview-inset ui-corner-all ui-shadow' data-inset='true' data-role='listview'>";
				htmlTextHeader += "<li class='rh_header ui-li ui-li-static ui-body-c ui-corner-top'><div class='col1'>&nbsp;</div><div class='col2'>Reward Type</div><div style='text-align: center;' class='col3'>Redemption Amount</div> <div style='text-align: center;' class='col4'>What You Get</div></li>";
				for (orderHistoryListCounter in orderHistoryList) {
					var valueMap=[];
					var requestDate=orderHistoryList[orderHistoryListCounter].orderDate;
					valueMap["requestDate"]=requestDate;
					var itemDesc=orderHistoryList[orderHistoryListCounter].itemDesc;
					valueMap["itemDesc"]=itemDesc;
					var firstLastName=orderHistoryList[orderHistoryListCounter].firstLastName;
					valueMap["firstLastName"]=firstLastName;
					var fullfillMedDesc=orderHistoryList[orderHistoryListCounter].mediaDesc;
					valueMap["fullfillMedDesc"]=fullfillMedDesc;
					var dsbrstAmt=orderHistoryList[orderHistoryListCounter].redemptionAmt;
					valueMap["dsbrstAmt"]="$"+dsbrstAmt;
					var dsbrstCashAmt=orderHistoryList[orderHistoryListCounter].whatYouGetAmt;
					valueMap["dsbrstCashAmt"]="$"+dsbrstCashAmt;
					var userModeCode = orderHistoryList[orderHistoryListCounter].modeCode;
					valueMap["userModeCode"]=userModeCode;

					if("MCH" ==  userModeCode ){					
						valueMap["dsbrstCashAmt"]="Item";
					}else if(dsbrstAmt < 0 ){						
						valueMap["dsbrstCashAmt"]="N/A";
					}	

					var jsonByOrderType="DEFAULT_HISTORY";
					var redeemOptionId=orderHistoryList[orderHistoryListCounter].redeemOptionId;

					switch(redeemOptionId){

					case "1": // for partners	
						var orderId = orderHistoryList[orderHistoryListCounter].orderId;
						var eCertNum = orderHistoryList[orderHistoryListCounter].eCertNumber;
						if(fullfillMedDesc.indexOf("Electronic Certificate") == 0){
							fullfillMedDesc = "<a href='#' class='eCertBlueLink' onClick=dfs.crd.rwds.getEcetDetailsFromModeCode('"+userModeCode+"','"+orderId+"','"+eCertNum+"','"+requestDate+"')>eCertificate</a>";						
							valueMap["fullfillMedDesc"]=fullfillMedDesc;
						}
						jsonByOrderType="PARTNER_HISTORY";
						break;

					case "2":// for DISCOVER GIFT CARD using DEFAULT_HISTORY as jsonByOrderType
						break;

					case "3": // for STATEMENT CREDIT 						
						if(userModeCode == "CRD1"){
							valueMap["fullfillMedDesc"]="Cash";
							valueMap["itemDesc"]="Statement Credit";
						}
						break;

					case "4":// for DIRECT DEPOSIT 
						if(userModeCode == "EFT1"){
							valueMap["fullfillMedDesc"]="Cash";
							valueMap["itemDesc"]="Direct Deposit";
						}
						break;

					case "5":	// 	for CHARITY 				
						valueMap["fullfillMedDesc"]="Charitable Donation";
						jsonByOrderType="CHARITY_HISTORY";						
						break;

					case "6":// for TRAVEL JSON is not avilable
						break;

					case "7":// for DFS MERCHANDISE 
						jsonByOrderType="PARTNER_HISTORY";
						break;

					case "8": // for VENDOR MERCHANDISE 						
						valueMap["fullfillMedDesc"]="Merchandise";
						jsonByOrderType="VENDOR_MERCHANDISE_HISTORY";						
						break;

					case "9": // for Amazon						
						jsonByOrderType="AMAZON_HISTORY";
						valueMap["fullfillMedDesc"]="Amazon.com Purchase";
						break;

					case "10":// for REVERSED AMAZON 						
						jsonByOrderType="REVERSED_AMAZON_HISTORY";
						valueMap["fullfillMedDesc"]="Amazon.com Purchase";
						break;

					case "0":// for OTHER using DEFAULT_HISTORY as jsonByOrderType
						break;

					default:// for default using DEFAULT_HISTORY as jsonByOrderType
						break;

					}	

					var jsonPromoCodeText;
					if (!jQuery.isEmptyObject(rewardsHistoryJSON)) {
						if ((redeemOptionId != null)
								&& ((redeemOptionId == '9' || redeemOptionId == '10'))) {
							var key = jsonByOrderType + "_"
									+ globalIncentiveTypeCode;
							jsonPromoCodeText = rewardsHistoryJSON[key];
						} else {
							var key = jsonByOrderType;
							jsonPromoCodeText = rewardsHistoryJSON[key];
						}
					}

					var jsonTextWithValue='';
					if (!isEmpty(jsonPromoCodeText)) {
						jsonTextWithValue=parseContent(jsonPromoCodeText,valueMap);
					}
					htmlText += jsonTextWithValue;
				} 

				htmlTextHeader += htmlText;				
				htmlText = htmlTextHeader;
				htmlText += "</ul><span><p>Click on the eCertificate link to view your eCertificate.</p></span>";
				$("#redemptionHistory_List").html(htmlText);	
			}
			else{
				htmlText = "<span class='errormsg2'>There are no transaction history to view.</span><br/>";
				$("#redemptionHistory_List").html(htmlText);
			}          
		}
	}
	catch(err){
		showSysException(err);
	}
}


dfs.crd.rwds.getEcetDetailsFromModeCode=function(userModeCode,orderId,eCertNo,date){
try{	
	var eCertData=dfs.crd.rwds.getEcertPartnerRedeemHistory(userModeCode,orderId,eCertNo,date);
	if (!jQuery.isEmptyObject(eCertData)){
		navigation('../rewards/redeemCashbackEcertDetails');
	}
	}catch(err){
		showSysException(err);
	}
}


dfs.crd.rwds.getEcertPartnerRedeemHistory=function(userModeCode,orderId,eCertNo,date){
	try{
		var ecertPartnerDetails;
		var newDate = new Date();	
		var ECERTPARTNERSURL =RESTURL+"rewards/v2/orderhistory?modeCode="+userModeCode+"&eCertNumber="+eCertNo+"&orderId="+orderId+"&orderDate="+date+"&"+newDate;
		showSpinner();
		$.ajax({
			type : "GET",
			url : ECERTPARTNERSURL,
			async:false,
			dataType : 'json',
			headers:prepareGetHeader(),
			success : function(responseData, status, jqXHR) {
				hideSpinner();
				if (jqXHR.status != 200 & jqXHR.status != 204) {
					var code=getResponseStatusCode(jqXHR);
					errorHandler(code,'');
				}
				else{
					ecertPartnerDetails=responseData;
					putDataToCache("ECERT_REDEEM_HISTORY_DETAILS", ecertPartnerDetails);
				}
			},
			error : function(jqXHR, textStatus, errorThrown) {
				hideSpinner();
				var code=getResponseStatusCode(jqXHR);
				errorHandler(code,'','redemptionHistory');
			}
		});
		return ecertPartnerDetails;
	}catch(err){
		showSysException(err);
	}
}

function redeemCashbackEcertDetailsLoad(){
	try{
		dfs.crd.rwds.populatredeemCashbackEcertDetails();
	}catch(err){
		showSysException(err);
	}
}

dfs.crd.rwds.populatredeemCashbackEcertDetails = function(){
	try{	
		var eCertMerchantDetails2 =getDataFromCache("ECERT_REDEEM_HISTORY_DETAILS");
		if (!jQuery.isEmptyObject(eCertMerchantDetails2) ) {

			var redeemECert4_DynamicText='';
			var mobilzedmessageText;
			var noteToCashierText='';
			var callableMsgText='';
			var modeDescShort=eCertMerchantDetails2.eCertificates[0].modeDescShort;

			if (!isEmpty(globalLastFourAcctNbr)) {
				$("#redeemECertDetails_CardEnding4Number").html(globalLastFourAcctNbr);
			}
			if (!isEmpty(eCertMerchantDetails2.eCertificates[0].modeDesc)) {
				modeDescription=eCertMerchantDetails2.eCertificates[0].modeDesc;	
				$("#redeemECertDetails_MerchantName1").html(modeDescription);
			}
			if (!isEmpty(eCertMerchantDetails2.eCertificates[0].disbAmt)) {
				var disbAmt=eCertMerchantDetails2.eCertificates[0].disbAmt;
				if (disbAmt.indexOf('.') != -1) {
					disbAmt = disbAmt.split(".")[0];
				}
				$("#redeemECertDetails_disbAmt").html("$"+ disbAmt);
			}

			var imagePath = "<img width='90' height='45' src='"
				+ AVAILABLE_PARTNERS_IMAGE_CONFIRM_URL + ""
				+ eCertMerchantDetails2.eCertificates[0].modeCode + ".png'>";
			$("#redeemECertDetails_logoImagePath").html(imagePath);

			if(!isEmpty(eCertMerchantDetails2.eCertificates[0].eCertNumber)){
				redeemECert4_DynamicText += "<li>Mobile eCertificate ID:<b>"+eCertMerchantDetails2.eCertificates[0].eCertNumber+"</b></li>";
			}
			if(!isEmpty(eCertMerchantDetails2.eCertificates[0].eCertPin)){
				redeemECert4_DynamicText += "<li><div class='clearboth hrow'></div></li><li>Mobile eCertificate PIN:<b>"+eCertMerchantDetails2.eCertificates[0].eCertPin+"</b></li>";
			}
			if(!isEmpty(eCertMerchantDetails2.eCertificates[0].eCertExpDate)){
				redeemECert4_DynamicText += "<li><div class='clearboth hrow'></div></li><li>eCertificate ExpDate:<b>"+eCertMerchantDetails2.eCertificates[0].eCertExpDate+"</b></li>";
			}			

			if (eCertMerchantDetails2.eCertificates[0].isMobilized){
				noteToCashierText = "<p><span><b>Note to Cashier:</b></span> Please process as a store gift card.</p>";
				mobilzedmessageText = "<li><div class='clearboth hrow'></div></li><li><span class='orangetxt boldtext'>No Printing Necessary!</span></li><li><span>"+eCertMerchantDetails2.eCertificates[0].modeDesc+"</span>eCertificates are able to be used in-store right from your mobile device! Please show this eCertificate to the Cashier at time of purchase.<div class='clearboth'></li>";
				redeemECert4_DynamicText += mobilzedmessageText;
			}
			$("#redeemECertDetails_DynamicText").html("<ul>"+redeemECert4_DynamicText+"</ul>");

			if (!isEmpty(eCertMerchantDetails2.eCertificates[0].partnerPhone)) {
				callableMsgText = "<center><a href='tel:"+eCertMerchantDetails2.eCertificates[0].partnerPhone+"' onClick='trackThis(); return true' class='ui-btn ui-btn-corner-all ui-shadow ui-btn-up-c'><span class='ui-btn-inner ui-btn-corner-all' ><span class='ui-btn-text'>Click to Call "+modeDescription+" Now</span></span></a></center>";
			}

			$("#redeemECertDetails_noteToCashier").html(noteToCashierText);
			$("#redeemECertDetails_callableMsg").html(callableMsgText);

			if (!isEmpty(eCertMerchantDetails2.eCertificates[0].modeDesc)) {
				$("#redeemECertDetails_MerchantName3").html(eCertMerchantDetails2.eCertificates[0].modeDesc);
				$("#redeemECertDetails_MerchantName2").html(eCertMerchantDetails2.eCertificates[0].modeDesc);
			}
			if (!isEmpty(eCertMerchantDetails2.eCertificates[0].partnerTerms)) {
				$("#redeemECertDetails_MerchantTerms").html(eCertMerchantDetails2.eCertificates[0].partnerTerms);
			}	

			if (!isEmpty(eCertMerchantDetails2.eCertificates[0].redeemInstruct)) {
				$("#redeemECertDetails_redeemInstruct").html(eCertMerchantDetails2.eCertificates[0].redeemInstruct);
			}

		}
	}catch(err){
		showSysException(err);
	}
}


//~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~ REDEEM HISTORY END ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~


//getting rewards.json from server
dfs.crd.rwds.getRewardsPCCData =function(page){
	try{
		var newDate = new Date();	
		var pageId="REWARDS_JSON";
		var REWARDSJSONURL = HREF_URL + "json/rewards/rewards.json?"+newDate+"";
		var rewardsJsonDetails=getDataFromCache(pageId);
		if (jQuery.isEmptyObject(rewardsJsonDetails)) {
			showSpinner();
			$.ajax({
				type : "GET",
				url : REWARDSJSONURL,
				async : false,
				dataType : 'json',
				success : function(responseData, status, jqXHR) {
					hideSpinner();
					if (jqXHR.status != 200) {
						var code=getResponseStatusCode(jqXHR);
						errorHandler(code,'',page);
					} else {
						rewardsJsonDetails = responseData;
						putDataToCache(pageId,rewardsJsonDetails);
					}
				},
				error : function(jqXHR, textStatus, errorThrown) {
					hideSpinner();
					var code=getResponseStatusCode(jqXHR);
					errorHandler(code,'',page);
				}
			});
		}
		return rewardsJsonDetails;
	}catch(err){
		showSysException(err);
	}
}

//getting rewards.json from server Asynchronously
dfs.crd.rwds.getRewardsPCCDataAsync = function(){
	try{
		var newDate = new Date();	
		var pageId="REWARDS_JSON";
		var REWARDSJSONURL = HREF_URL + "json/rewards/rewards.json?"+newDate+"";
		var rewardsJsonDetails=getDataFromCache(pageId);
		if (jQuery.isEmptyObject(rewardsJsonDetails)) {
			$.ajax({
				type : "GET",
				url : REWARDSJSONURL,
				dataType : 'json',
				success : function(responseData, status, jqXHR) {
					if (jqXHR.status != 200) {

					}else{
						rewardsJsonDetails = responseData;
						putDataToCache(pageId,rewardsJsonDetails);
					}
				},
				error : function(jqXHR, textStatus, errorThrown) {}
			});
		}
	}catch(err){}
}


dfs.crd.rwds.appendCommingSoonInfoPCC =function(signupDetails){
try{
	if (jQuery.isEmptyObject(signupDetails)) {
		return;
	}
	var isCommingSoonIncluded=false;	
	isCommingSoonIncluded=signupDetails.commingSoonPCC;
	if(isCommingSoonIncluded){
		return;
	}else{
		var promotionOutputList= signupDetails.promotionOutputList;
		var promotionOutputListLength=promotionOutputList.length;
		promotionOutputList[promotionOutputListLength]={"promoCode":"COMINGSOON"};
		signupDetails["commingSoonPCC"]=true;
	}	
	}catch(err){
		showSysException(err);
	}
}
