
/**
 * @author nverma
 * @version 1.0
 * 
 * This  file includes the functionality to render following Rewards Discover Gift card pages pages: 
 *  - Redeem Gift cards Step1, Step2 and Step3. It also includes the other fuctionality 
 *  specific to Discover Gift Cards
 */

	//var d = d || {};
	//d.crd= d.crd || {};
	dfs.crd.rwd= dfs.crd.rwd || {};
	dfs.crd.rwd.dgc = dfs.crd.rwd.dgc || {};	
	
	dfs.crd.rwd.dgc.giftCardImgUrl = HREF_URL + "mobile/rewards/advanced/images/gift-cards/";
	dfs.crd.rwd.giftcardInvalidNames;
	
	/*Gift Card Step1 page before change */
	
	function giftcard1Load() {
		try {
			//console.log("gift card 1 load");
			if(fromPageName!="giftcard2" && fromPageName!="giftcardselectdesign" && fromPageName!="giftcardTerms"){
               killDataFromCache("GCSelectedInput");
               $(document).removeData();
            }
			
			killDataFromCache("GIFTDATATOPOST");// if some error came up from step2 to step3, POST data should be deleted before logging out also.
			dfs.crd.rwd.dgc.populateGiftCard1Page('GIFTCARD1');
		} catch (err) {
			showSysException(err)
		}
	}
	
	dfs.crd.rwd.dgc.populateGiftCard1Page = function(pageName){
		try {
			//console.log(" in populateGiftCard1Page :: " + pageName);
			var gftCrdDetails = dfs.crd.rwd.dgc.getAllGiftCardData(pageName);
			//console.log(" got all data by ajax call :: " + pageName);
			if (!jQuery.isEmptyObject(gftCrdDetails)) {
				//console.log(" going to populateGiftCard1Details :: " + pageName);
				dfs.crd.rwd.dgc.populateGiftCard1Details(gftCrdDetails);
			}
		} catch (err) {
			showSysException(err)
		}
	}
	
	/*Ajax call to get the discover gift cards details */

	dfs.crd.rwd.dgc.getAllGiftCardData = function(pageName){	
		try{
			
			//console.log(" in getAllGiftCardData :: " + pageName);			
			var gftDetails;
			//console.log(" gftDetails :::  " + gftDetails);
			showSpinner();
			if (jQuery.isEmptyObject(gftDetails)) {
				//console.log(" calling VST0 for gift cards :: " + pageName);
				var newData = new Date();
				var ALLGIFTCARDDATAURL = RESTURL + "rewards/v3/redeemoption?modeGroupCode=DGC&"
				+ newData + "";	
								
				$.ajax({
					url : ALLGIFTCARDDATAURL,
					async : false,
					dataType : 'json',
					headers : prepareGetHeader(),
					success : function(responseData, status, jqXHR) {
						hideSpinner();
						if (!validateResponse(responseData,"getDiscoverGiftCardValidation")) // Pen Test Validation
                       {
                       errorHandler("SecurityTestFail","","");
                       return;
                       }
						//console.log("jqXHR.status ::" + jqXHR.status);
						if (jqXHR.status != 200 & jqXHR.status != 204) {
							var code = getResponseStatusCode(jqXHR);
							errorHandler(code, '', 'giftcard1');
						} else {
							//console.log("got gift card 1 data ::");
							gftDetails = responseData;
							putDataToCache(pageName, gftDetails);
						}
					},			
					error : function(jqXHR, textStatus, errorThrown) {
						//console.log("ERROR gift card 1 data ::");
						hideSpinner();
						var code = getResponseStatusCode(jqXHR);
						//console.log("error ::" + code);
						cpEvent.preventDefault();
						switch (code) {						
							case "1646":								
								navigation('../rewards/IneligibleError_discoverGiftCards');
								break;	
								
							default:								
								errorHandler(code, '', 'giftcard1');
								break;
						}		
					}			
				});	
			}
			return gftDetails;
		} catch (err) {
			showSysException(err)
		}
	
	}
	
	/******** populateGiftCard1Details *****************/
	
	dfs.crd.rwd.dgc.populateGiftCard1Details = function(gftCrdDetails){
		try{
			//console.log("in populateGiftCard1Details!");			
			var giftCardResData = gftCrdDetails;						
			if (!jQuery.isEmptyObject(giftCardResData)) {
				$("#giftCard1_CashBackBonusBalance").html("$"+ globalEarnRewardAmount);	
				var gcRedemptionAmount = giftCardResData["disbAmts"];
				var gcDesignList = giftCardResData["discoverGiftCards"];
				var dynamicSelectForGCAmt = "<select data-theme='d' id='select-amount' data-native-menu='false'>"; /* 13.3 Global change */
				var defaultGiftCardAmount = '$'+ (gcRedemptionAmount[0].split(".")[0]);
				/*Dynamic creation of the amount select dropdown*/				
				for(i=0;i<gcRedemptionAmount.length;i++){
					var gcRedmptionAmtval=gcRedemptionAmount[i];
					
					gcRedmptionAmtval=gcRedmptionAmtval.split(".")[0];	
					dynamicSelectForGCAmt+="<option value='"+gcRedmptionAmtval+"'>"+"$"+gcRedmptionAmtval+"</option>";
				}
				
				dynamicSelectForGCAmt+="</select>";
				
				$("#giftcard1_AmountSelectField").html(dynamicSelectForGCAmt);					
				$("#defaultAmount").html(defaultGiftCardAmount);
				
				
				/*Select the default discover gift card image and descriptiotn*/
				for(i=0;i<gcDesignList.length;i++){
					var isDefaultDesign=gcDesignList[i].isDefault;
					/////console.log("isDefaultDesign :: " + isDefaultDesign);
					if(isDefaultDesign != "" && isDefaultDesign == true){
						var giftCardDesign = gcDesignList[i].designDesc;
						var giftCardDesignCode = gcDesignList[i].designCode;
						var giftcardImg = dfs.crd.rwd.dgc.giftCardImgUrl+'gift-card-'+giftCardDesignCode+'.png';						
						$("#giftCardImage").attr("src", giftcardImg);
					    $("#giftCard1_Design").html(giftCardDesign+" design");
					}					
				}
				
				var selectedcachedata = getDataFromCache("GCSelectedInput");
				if (!jQuery.isEmptyObject(selectedcachedata)) {			
					//console.log("selected Cache present ::::" + selectedcachedata.selectAmt);
					$('#select-amount').val(selectedcachedata.selectAmt);
					$('#first-name').val(selectedcachedata.firstNm);
					$('#last-name').val(selectedcachedata.lastNm);				
				}
				
				/*Code to show error if Invaid names are entered. In case of invalid names, after submit in case of error
					step 1 will be shown with the error selected
					*/
				
				if(!isEmpty(dfs.crd.rwd.giftcardInvalidNames) && dfs.crd.rwd.giftcardInvalidNames === 'true'){
					//console.log("invalid names error after submit");
					$(".gift-card-error-msg").show();
					$("#first-name").addClass("error-box");
					$("#last-name").addClass("error-box");
				}
				
				// Put step 1 selected data into cache to be used later, while coming back from all overlays			
				var selectedAmt;
				var firstNm;
				var lastNm;
				var slctedDesignDesc;
				var slctedDesignImg;
				/**Continue link clicked*/
				$("#dgcard-continue-btn").click(function(){	
					s.prop1 = 'HANDSET_REDEEM_DGC_CONTINUE_BTN'; //campaign code
					selectedAmt = $('#select-amount').val();
					firstNm = $('#first-name').val();
					lastNm = $('#last-name').val();
					slctedDesignDesc=$("#giftCard1_Design").html();
					slctedDesignImg=$('#giftCardImage').attr("src"); 
					dfs.crd.rwd.dgc.createSelectedDataCache(selectedAmt,firstNm,lastNm,slctedDesignDesc,slctedDesignImg);				
					navigation('../rewards/giftcard2')
				});
				
				$("#giftCardImgLink").click(function(){ 
					selectedAmt = $('#select-amount').val();
					firstNm = $('#first-name').val();
					lastNm = $('#last-name').val();
					slctedDesignDesc=$("#giftCard1_Design").html();
					slctedDesignImg=$('#giftCardImage').attr("src"); 
					
					dfs.crd.rwd.dgc.createSelectedDataCache(selectedAmt,firstNm,lastNm,slctedDesignDesc,slctedDesignImg);
					dfs.crd.rwd.dgc.renderGiftCardDesigns() 
				});
				
				/**Terms and condition link clicked*/
				$("#giftcard1terms").click(function(){	
					selectedAmt = $('#select-amount').val();
					firstNm = $('#first-name').val();
					lastNm = $('#last-name').val();
					slctedDesignDesc=$("#giftCard1_Design").html();
					slctedDesignImg=$('#giftCardImage').attr("src"); 
					dfs.crd.rwd.dgc.createSelectedDataCache(selectedAmt,firstNm,lastNm,slctedDesignDesc,slctedDesignImg);
						
					navigation('../rewards/giftcardTerms')
				});
				
				$("#redeem-gift-card").trigger("create");
				dfs.crd.rwd.giftcardInvalidNames = 'false';
					
			}			
			
		}catch(err){
			showSysException(err)
		}
	}
	
	function giftcardTermsLoad(){
		try{
			
			var giftCardData = getDataFromCache("GIFTCARD1");
			if (!jQuery.isEmptyObject(giftCardData)) {
				var giftCardTerms = giftCardData["partnerTerms"];
				if(giftCardTerms !== null){
					$("#tnc-content").html(giftCardTerms);
					
				}
			}
			
			
		}catch(err){
			showSysException(err)
		}
		
	}
	
	dfs.crd.rwd.dgc.createSelectedDataCache = function(amount,firstnm,lastnm,slctedDesignDesc,slctedDesignImg){
		try {
				//console.log(" in createSelectedDataCache " + slctedDesignDesc);
				var selectedData = [];
				selectedData['selectAmt'] = new Object();
				selectedData["selectAmt"] = amount;
				selectedData['firstNm'] = new Object();
				selectedData["firstNm"] = firstnm;
				selectedData['lastNm'] = new Object();
				selectedData["lastNm"] = lastnm;
				selectedData['designDesc'] = new Object();
				selectedData["designDesc"] = slctedDesignDesc;
				selectedData['designImg'] = new Object();
				selectedData["designImg"] = slctedDesignImg;				
				putDataToCache("GCSelectedInput",selectedData);
								
		} catch (err) {
			showSysException(err)
		}
	}
	
	/** Render all the discover gift card designs */
	
	dfs.crd.rwd.dgc.renderGiftCardDesigns = function(){
		try {
			navigation('../rewards/giftcardselectdesign');
		} catch (err) {
			showSysException(err)
		}
	}
	
	/** Discover gift card design overlay page befpre change method*/
	
	function giftcardselectdesignLoad() {
		try {
			var giftCardDetails = getDataFromCache("GIFTCARD1");
			if (!jQuery.isEmptyObject(giftCardDetails)) {
				dfs.crd.rwd.dgc.populateGiftCardDesigns(giftCardDetails);
			}
		} catch (err) {
			showSysException(err)
		}
	}
	
	dfs.crd.rwd.dgc.populateGiftCardDesigns = function(giftCardDetails){
		try{
			//console.log(" populating designs");		
			var gcdesigns = giftCardDetails["discoverGiftCards"];
			var isDefaultDesign;
			var giftCardDesignA;
			var giftCardDesignCodeA;
			var giftcardImgA;
			var giftCardDesignB;
			var giftCardDesignCodeB;
			var giftcardImgB;
			var count = (gcdesigns.length)-1;
			var designsListLayout=""; 
			for(i=0;i<gcdesigns.length;i++){
			
				if(i<count){
					isDefaultDesign= gcdesigns[i].isDefault;
					giftCardDesignA = gcdesigns[i].designDesc;
					giftCardDesignCodeA = gcdesigns[i].designCode;
					giftcardImgA = dfs.crd.rwd.dgc.giftCardImgUrl+'gift-card-'+giftCardDesignCodeA+'.png';
					giftCardDesignB = gcdesigns[i+1].designDesc;
					giftCardDesignCodeB = gcdesigns[i+1].designCode;
					giftcardImgB = dfs.crd.rwd.dgc.giftCardImgUrl+'gift-card-'+giftCardDesignCodeB+'.png';
					
					designsListLayout += "<div class='ui-grid-a '><div class='ui-block-a'><div class='imgWrapper floatleft'><img id='classic-card' src='" + giftcardImgA + "' width='110px' height='69px' class='card' /><div class='clearboth'></div><p class='gName'>"+giftCardDesignA+"</p></div></div><div class='ui-block-b'><div class='imgWrapper floatright'><img id='platinum-card' src='"+giftcardImgB+"' width='110px' height='69px' class='card'/><div class='clearboth'></div><p class='gName'>"+giftCardDesignB+"</p></div></div></div>"
				}else{
					isDefaultDesign= gcdesigns[i].isDefault;
					giftCardDesignA = gcdesigns[i].designDesc;
					giftCardDesignCodeA = gcdesigns[i].designCode;
					giftcardImgA = dfs.crd.rwd.dgc.giftCardImgUrl+'gift-card-'+giftCardDesignCodeA+'.png';
					designsListLayout += "<div class='ui-grid-a'><div class='ui-block-a'><div class='imgWrapper floatleft'><img id='classic-card'"+ "src='" + giftcardImgA + "' width='110px'height='69px' class='card' /><div class='clearboth'></div><p class='gName'>"+giftCardDesignA+"</p></div></div></div>"
					
				}		
				i=i+1;				
			}		
			$("#displaydesignList").html(designsListLayout);
			$("#goto-prevpg").click(function(){
				navigation('../rewards/giftcard1');
				});
				
				
		} catch (err) {
			showSysException(err)
		}
		
	}
	
	
	
	/****************************LOAD gift card step 2*****************************/
	
	function giftcard2Load(){
	
		try {
			dfs.crd.rwd.dgc.populateGiftCard2Page();
		} catch (err) {
			showSysException(err)
		}	
	
	}
	
	/****************************Populate gift card step 2*****************************/
	
	dfs.crd.rwd.dgc.populateGiftCard2Page = function(){
		try{		
			var validPriorPagesOfgiftcar2= new Array("giftcard1","giftcardPurchaseAgrmnt","giftcardTerms","moreLanding");
			if(jQuery.inArray(fromPageName, validPriorPagesOfgiftcar2) > -1 ){		
		
				var amountSelected;
				var firstName;
				var lastName;
				var selectedDesignImg;
				var selectedDesignDesc;
				var selectedDesignCode;
				var splitImgPath;
				
				var cacheRedeemGCDetails=getDataFromCache("GIFTCARD1");	
				if (!jQuery.isEmptyObject(cacheRedeemGCDetails)) {	
					//console.log("cacheRedeemGCDetails present");
					var billAddress = cacheRedeemGCDetails.billingAddr;
					var shipTo = '';
					
					// creating the billing address display
					if(!isEmpty(billAddress)){
						
						if(!isEmpty(billAddress.fullName)){
							shipTo += billAddress.fullName+" <br />";
						}
						if(!isEmpty(billAddress.addrLine1)){
							shipTo += billAddress.addrLine1+" <br />";
						}
						if(!isEmpty(billAddress.addrLine2)){
							shipTo += billAddress.addrLine2+" <br />";
						}
						if(!isEmpty(billAddress.addrLine3)){
							shipTo += billAddress.addrLine3+" <br />";
						}
						if(!isEmpty(billAddress.city)){
							shipTo += billAddress.city+", ";
						}
						if(!isEmpty(billAddress.state)){
							shipTo += billAddress.state+" ";
						}
						if(!isEmpty(billAddress.zipCode)){
							shipTo += billAddress.zipCode;
						}
					}
					$("#shippingAddr").html(shipTo);	
				}
				
				// getting the selected data from step1, to show when step 2 gets reloaded from terms and conditions.
				
				var selectedinputdata = getDataFromCache("GCSelectedInput");
				if (!jQuery.isEmptyObject(selectedinputdata)) {						
					amountSelected = selectedinputdata.selectAmt;
			    	firstName = selectedinputdata.firstNm;
			    	lastName = selectedinputdata.lastNm;
					selectedDesignDesc = selectedinputdata.designDesc;
					selectedDesignDesc = selectedDesignDesc.replace('design','');
					selectedDesignDesc = selectedDesignDesc.replace('Design','');
					//console.log("selectedDesignDesc ::: " + selectedDesignDesc);
					selectedDesignImg = selectedinputdata.designImg;
				}
				
				//console.log("selectedDesignImg :: "+ selectedDesignImg);
				
				if(!(isEmpty(selectedDesignImg)) && selectedDesignImg.length >0){				
						splitImgPath= selectedDesignImg.substring(selectedDesignImg.lastIndexOf("/") + 1);
						selectedDesignCode=splitImgPath.split(".")[0];
						selectedDesignCode = selectedDesignCode.substring(10);
				}		
				
				var fullName = firstName + " " + lastName;
				
				if(!isEmpty(fullName) && !$.trim(fullName) == ''){						
					$("#add-name-txt").html(fullName);	
				}else if(!isEmpty(firstName) && !$.trim(firstName) == ''){				
					$("#add-name-txt").html(firstName);	
				}else if(!isEmpty(lastName) && !$.trim(lastName) == ''){				
					$("#add-name-txt").html(lastName);	
				}
				else{						
					var addName = '<span>No Name Entered</span><div class="learnmore"><a href="#" class="ui-link" id="addNm">Add Name</a></div>';			
					$("#add-name-txt").html(addName);	
				}
				
				$("#giftCard2Image").attr("src", selectedDesignImg);
				$("#selected-redeem-cbb").html("<span>$</span>"+amountSelected);
				$("#cbb-redem").html("$"+amountSelected+".00");
				$("#cbb-curr-balance").html("$"+globalEarnRewardAmount);
				$("#giftCard2_CashBackBonusBalance").html("$"+globalEarnRewardAmount);
				$("#giftCardImgDesc").html(selectedDesignDesc);
						
				var calcAmountSelected = amountSelected.replace('$', '');
				calcAmountSelected = calcAmountSelected + ".00";				
				var amt1 = globalEarnRewardAmount.replace(",", "");								
				var cbbBalance = "$" + ((amt1 - calcAmountSelected).toFixed(2));
				$("#cbb-balance").html(numberWithCommas(cbbBalance));
				
				//put the data to be posted in global cache	
				var giftDetailsToPost = [];
				giftDetailsToPost['designCode'] = new Object();
				giftDetailsToPost["designCode"] = selectedDesignCode;
				giftDetailsToPost['customizedFirstName'] = new Object();
				giftDetailsToPost["customizedFirstName"] = firstName;
				giftDetailsToPost['customizedLastName'] = new Object();
				giftDetailsToPost["customizedLastName"] = lastName;
				giftDetailsToPost['selectedAmount'] = new Object();
				giftDetailsToPost["selectedAmount"] = calcAmountSelected;
				putDataToCache("GIFTDATATOPOST", giftDetailsToPost);
				
				$("#addNm").click(function(){
						navigation('../rewards/giftcard1');
				});
					
				$("#giftCard2Redeem").click(function(){
						s.prop1 = 'HANDSET_REDEEM_DGC_MAIL_MAIL_REDEEM_BTN'; //campaign code
						dfs.crd.rwd.dgc.renderGiftCardConfirmation();		
				});
					
				$("#giftCard2Cancel").click(function(){
					s.prop1 = 'HANDSET_REDEEM_DGC_MAIL_MAIL_CANCEL_BTN'; //campaign code
					discoverGiftCard();
					//navigation('../rewards/redemptionLanding');
				});
					
				$("#gcPurchaseTerms").click(function(){
					navigation('../rewards/giftcardPurchaseAgrmnt');
				});
					
				$("#gcTerms").click(function(){
					navigation('../rewards/giftcardTerms');
				});
				$("#gcTerms1").click(function(){
					navigation('../rewards/giftcardTerms');
				});
				
			}else{
				cpEvent.preventDefault();
				//history.back();
				//console.log("goto redemption landing");
					//navigation('../rewards/redemptionLanding');
				   navigation('../rewards/giftcard1');
			
			}	
					
		}catch(err) {
			showSysException(err)
		}
	
	}
	
	/****************************LOAD gift card step 3*****************************/
	
	function giftcard3Load(){	
		try {
			//console.log("giftcard3Load");
			trafficSource = MOBILE_GIFT_CARD;
			dfs.crd.rwd.dgc.populateGiftCard3();
		} catch (err) {
			showSysException(err)
		}	
		
	}
	
	/****************************RENDER gift card step 3*****************************/
	
	dfs.crd.rwd.dgc.renderGiftCardConfirmation = function(){
	
		//console.log("renderGiftCardConfirmation !!");
		try{
			var submitGiftCard;
			var selectedGiftDetails = getDataFromCache("GIFTDATATOPOST");
			if (!jQuery.isEmptyObject(selectedGiftDetails)) {
				submitGiftCard = dfs.crd.rwd.dgc.postGiftCard_CBB(selectedGiftDetails);
				
				if (!jQuery.isEmptyObject(submitGiftCard)) {
					killDataFromCache("ACHOME");
					killDataFromCache("REDEEM_HISTORY");
					killDataFromCache("ALLPARTNERS");
					killDataFromCache("REDEEMPARTNERDATA");
					killDataFromCache("ECERT_REDEEM_HISTORY_DETAILS");
					killDataFromCache("CATEGORYDATA");
					killDataFromCache("GIFTCARD1");
					killDataFromCache("GIFTDATATOPOST");
					killDataFromCache("GCSelectedInput");
					if(!isEmpty(submitGiftCard.availToRedeem)){
						//console.log("updated CBB after redeem ::" + submitGiftCard.availToRedeem); 
						globalEarnRewardAmount = numberWithCommas(submitGiftCard.availToRedeem);
					}				
					putDataToCache("GIFTDATATOPOST", submitGiftCard);
					navigation('../rewards/giftcard3');
					
				}
			
			}
		}catch(err) {
			showSysException(err)
		}
	
	}
	
	/**************************** POST AJAX STEP 3*****************************/
	dfs.crd.rwd.dgc.postGiftCard_CBB = function(selectedGiftDetails){
		//console.log("postGiftCard_CBB !!");
		try{
			var updatedGiftCardPOSTData;
			if (!jQuery.isEmptyObject(selectedGiftDetails)) {
				var RedeemDGCURL = RESTURL + "rewards/v2/redeem";	
				//console.log("RedeemDGCURL :: " + RedeemDGCURL)
				
				var modeGroupCode = "DGC";
				var dgcAmount = selectedGiftDetails.selectedAmount;
				var dgcDesignCode = selectedGiftDetails.designCode;
				var firstNameOnCard;
				var lastNameOnCard;
				
				if (!isEmpty(dgcAmount) && dgcAmount.indexOf('.') != -1) {
							dgcAmount = dgcAmount.split(".")[0];
				}				
				if(!isEmpty(selectedGiftDetails.customizedFirstName) && !$.trim(selectedGiftDetails.customizedFirstName) == ''){
					firstNameOnCard = selectedGiftDetails.customizedFirstName;			
				}
				if(!isEmpty(selectedGiftDetails.customizedLastName) && !$.trim(selectedGiftDetails.customizedLastName) == ''){
					lastNameOnCard = selectedGiftDetails.customizedLastName;			
				}
				var dataJSON={"modeGroupCode":modeGroupCode,"amount":dgcAmount,"designCode":dgcDesignCode,"firstNameOnCard":firstNameOnCard,"lastNameOnCard":lastNameOnCard};
				var dataJSONString=JSON.stringify(dataJSON);
				
				//console.log("dataJSONString :: " + dataJSONString)
				showSpinner();
								
					$.ajax({
					type : "POST",
					url : RedeemDGCURL,
					async : false,
					dataType : 'json',
					data :dataJSONString,
					headers:preparePostHeader(),
					success : function(responseData, status, jqXHR) {
						hideSpinner();
						if (!validateResponse(responseData,"redeemGiftCardPostValidation")) // Pen Test Validation
                           {
                           errorHandler("SecurityTestFail","","");
                           return;
                           }
						//console.log("success :: ")
						if (jqXHR.status != 200 & jqXHR.status != 204) {
							var code = getResponseStatusCode(jqXHR);
							errorHandler(code, '','giftcard3');
						} else {
							updatedGiftCardPOSTData = responseData;
						}
					},
					error : function(jqXHR, textStatus, errorThrown) {						
						hideSpinner();
						cpEvent.preventDefault();
						var code = getResponseStatusCode(jqXHR);						
						//console.log("ERROR :: " + code);
						dfs.crd.rwd.giftcardInvalidNames = 'false';
						switch (code) {
							case "1608":
								errorDiscoverGiftCardSCVariables();//passing site catalystvariable for Error - Discover Gift Cards - less than $20 available to redeem	
								navigation('../rewards/redeemDGCInsufficientError');
								break;
							case "1629":
								var errorMessage = errorCodeMap.REDEEM_BAD_ACCOUNT_STATUS;
								var promoCodeTextData = [];
								promoCodeTextData['ACHome_CashBackBonusBalance'] = "$"
									+ globalEarnRewardAmount;
								var parseContentText = parseContent(
										errorMessage, promoCodeTextData);
								if (!isEmpty(parseContentText)) {
									errorHandler(code, parseContentText,
									'redemptionLanding');
								} else {
									errorHandler('0', '', 'browseLanding');
								}
								break;
							case "1652":
								dfs.crd.rwd.giftcardInvalidNames = 'true';
								//console.log("error 1652");					
								discoverGiftCardNameValidationSCVariables();//passing site catalyst variables for Error Tracking - Gift Card - First/Last Name not Valid	
								navigation('../rewards/giftcard1');									
								break;
							default:
								errorHandler(code, '', 'browseLanding');
							break;
						}
					}
				});
			
			    
			}
			
			return updatedGiftCardPOSTData;
			
		}catch(err) {
			showSysException(err)
		}
	}
	
	/**************************** POPULATE STEP 3*****************************/
	
	dfs.crd.rwd.dgc.populateGiftCard3 = function(){
        
        try{
            
            var validPriorPagesOfgiftcar3= new Array("giftcard2","moreLanding");
            if(jQuery.inArray(fromPageName, validPriorPagesOfgiftcar3) > -1 ){
                
                var giftCardPostData = getDataFromCache("GIFTDATATOPOST");
                if (!jQuery.isEmptyObject(giftCardPostData)) {
                    
                    //console.log("getting data finally");
					var orderId = giftCardPostData.orderId;
					var redeemedAmt = giftCardPostData.redemptionAmt;
					if (!isEmpty(redeemedAmt) && redeemedAmt.indexOf('.') != -1) {
						redeemedAmt = redeemedAmt.split(".")[0];
                    }
                    //console.log("got above data");
					var cashBackBalance = giftCardPostData.currentBalance;
					var designCd = giftCardPostData.designCode;
                    
                    //console.log("got above data ag");
					var designImgSrc = dfs.crd.rwd.dgc.giftCardImgUrl+'gift-card-'+designCd+'.png';
                    //console.log("designImgSrc :: " + designImgSrc);
					
					$("#giftCard3_CashBackBonusBalance").html("$"+cashBackBalance);
					$("#redeemedAmt").html("$"+redeemedAmt);
					$("#giftCardOrder").html("Order #:"+ orderId);
					$("#giftCard3Img").attr("src", designImgSrc);
					
                    //console.log("orderId ::"+orderId + ":: redeemedAmt ::"+ redeemedAmt + ":: designCd:: "+ designCd + ":: designImgSrc :: "+ designImgSrc);
                    $("#rdRafLearnMore").click(function(){	
					 		dfs.crd.raf.cbbReferAFriend.call();
					 });
                    
                    
                }
                
                
                
            }else{
				cpEvent.preventDefault();
				//history.back();
				//console.log("goto redemption landing");
				//navigation('../rewards/redemptionLanding');			
                   navigation('../rewards/giftcard1');                
            }
        }
        catch(err) {
			showSysException(err)
		}
        
    }
	
