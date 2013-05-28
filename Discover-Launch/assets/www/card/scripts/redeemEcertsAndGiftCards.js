//var d = d || {};
//dfs.crd= dfs.crd || {};
dfs.crd.rwd = dfs.crd.rwd || {};

dfs.crd.rwd.availablePartners = HREF_URL
+ "mobile/rewards/advanced/images/partners/available/";
dfs.crd.rwd.unavailablePartners = HREF_URL
+ "mobile/rewards/advanced/images/partners/unavailable/";
dfs.crd.rwd.REDEMPTION_OPTIONS_URL = EXT_HREF_URL
+ "cardmembersvcs/rewards/app/redeem?ICMPGN=ACH_TAB_CBB_BTN_RDM";
dfs.crd.rwd.insuficientErrorPresent;
dfs.crd.rwd.insuficientErrorPresentSubmit;

var errAvailCBB = 0;
var PASSBOOKSVCURL = RESTURL + "rewards/v1/ecert.pkpass";

/** *************** REDEMPTION LANDING LOAD **************** */

function redemptionLandingLoad() {
	try {
		// console.log("********redemptionLandingLoad********");
		dfs.crd.rwd.loadredemptionLanding();
		redemptionLandingSCVariables();// passing sitecatalyst variable for
		// Mobile Rewards Home Page
	} catch (err) {
		showSysException(err)
	}

}

/** *************** LOAD REDEMPTION LANDING PAGE **************** */

dfs.crd.rwd.loadredemptionLanding = function() {

	try {
		var allPartersDetails = dfs.crd.rwd.getAllPartners();

		var totalPartnersCount;
		var totalPartnersDisplay = "Partner Gift Cards and eCerts ";//changing partners to partner
		if (!jQuery.isEmptyObject(allPartersDetails)) {
			if (!isEmpty(allPartersDetails.totalCount)) {
				totalPartnersCount = allPartersDetails.totalCount;
			}
			$("#ecertsAndGiftCardsCount").html(
					totalPartnersDisplay + "(" + totalPartnersCount + ")");
	        /*Create a hidden div containing partner images to improve the image loading on the subsequent pages*/
			var allPartnersHiddenList = "<ul>";
			var allPartners = allPartersDetails.partners;
			$.each(allPartners,	function(partnerCount) {
					var allPartnersHiddenLI = "";
					if(partnerCount < 100){
						var partnerModeCde = allPartners[partnerCount].modeCode;						
						if (allPartners[partnerCount].isPartnerAvail === true) {
							allPartnersHiddenLI += "<li>";
							allPartnersHiddenLI += "<a href='#'>";
							allPartnersHiddenLI += "<img src='"
								+ dfs.crd.rwd.availablePartners
								+ ""
								+ partnerModeCde
								+ ".png'";
							allPartnersHiddenLI += "></a>";
							allPartnersHiddenLI += "</li>";
							allPartnersHiddenList += allPartnersHiddenLI;
						}else{
							allPartnersHiddenLI += "<li>";
							allPartnersHiddenLI += "<a href='#'>";
							allPartnersHiddenLI += "<img src='"
								+ dfs.crd.rwd.unavailablePartners
								+ ""
								+ partnerModeCde
								+ ".png'";
							allPartnersHiddenLI += "></a>";
							allPartnersHiddenLI += "</li>";

							allPartnersHiddenList += allPartnersHiddenLI;						
						
						}
					}
			});

			allPartnersHiddenList += "</ul>";
			$("#partnerImgs").html(allPartnersHiddenList);
			$("#partnerImgs").trigger("create");
				
		} else {

			$("#ecertsAndGiftCardsCount").html(totalPartnersDisplay);
		}

		/** ****************** FOR DISCOVER GIFT CARD ******************* */
		killDataFromCache("GCSelectedInput");
		$(document).removeData();
		/** ************************************************************ */

		killDataFromCache("REDEEMSELCETED");
		/** *********************************************************** */

		$("#rewardslandingCBB").html("$" + globalEarnRewardAmount);

		// console.log("********getting var value!!!********");

		var insuffErr = dfs.crd.rwd.insuficientErrorPresent;
		// console.log("insuffErr :: " + insuffErr);

		$("#ecertsAndGiftCardsLanding").click(function() {
			s.prop1 = 'HANDSET_REDEEM_PGC_BTN'; // campaign code
			if (!isEmpty(insuffErr) && insuffErr === 'true') {
				navigation('../rewards/redeemPartnerInsufficientError');
			} else {
				navigation('../rewards/browseLanding');
			}
		});
		$("#discoverGiftCardsLanding").click(function() {
			s.prop1 = 'HANDSET_REDEEM_DGC_BTN'; // campaign code
			if (!isEmpty(insuffErr) && insuffErr === 'true') {
				navigation('../rewards/redeemDGCInsufficientError');
			} else {
				navigation('../rewards/giftcard1');
			}
		});

		$("#stmtCreditLanding").click(function() {
			s.prop1 = 'HANDSET_REDEEM_STATE_CREDIT_BTN'; // campaign code

			// console.log("ST clicked");
			navigation('../rewards/statementCredit1');
		});
		$("#directDepositLanding").click(function() {
			s.prop1 = 'HANDSET_REDEEM_DIRECT_DEP_BTN'; // campaign code
			// console.log("DD clicked");
			navigation('../rewards/directDeposit1');
		});
		$("#paywithCBBLanding").click(function() {
			s.prop1 = 'HANDSET_REDEEM_PAY_CBB_BTN'; // campaign code
			navigation('../rewards/redeem_pay_with_cbb');
		});
		$("#historyLanding").click(function() {
			s.prop1 = 'HANDSET_REDEEM_HIST_BTN'; // campaign code
			navigation('../rewards/redemption_History');
		});

		$("#redemptionFaqs").click(function() {
			// console.log("faqs clicked");
			navigation('../rewards/redeemFaqs');
		});

		$("#redemption-opts").click(
				function() {
					window.plugins.childBrowser
					.showWebPage(dfs.crd.rwd.REDEMPTION_OPTIONS_URL);
				});

		dfs.crd.rwd.insuficientErrorPresent = 'false';

	} catch (err) {
		showSysException(err)
	}

}

/**
 * *************** AJAX CALL TO GET ALL THE PARTNERS TO STORE IN THE GLOBAL
 * CACHE****************
 */

dfs.crd.rwd.getAllPartners = function() {

	// console.log("********in getAllPartners********");

	try {

		var allPartersData;
		var newData = new Date();
		var ALLECERTSPARTNERSURL = RESTURL
		+ "rewards/v2/partners?rewardType=ALL&" + newData + "";
		dfs.crd.rwd.insuficientErrorPresent = 'false';
		// console.log("ALLECERTPARTNERSURL ::"+ ALLECERTSPARTNERSURL);
		allPartersData = getDataFromCache("ALLPARTNERS");
		if (jQuery.isEmptyObject(allPartersData)) {

			showSpinner();
			$
			.ajax({
				type : "GET",
				url : ALLECERTSPARTNERSURL,
				async : false,
				dataType : 'json',
				headers : prepareGetHeader(),
				success : function(responseData, status, jqXHR) {
					hideSpinner();
					// console.log("jqXHR status ::"+ jqXHR.status);
					if (jqXHR.status != 200 & jqXHR.status != 204) {
						var code = getResponseStatusCode(jqXHR);
						errorHandler('0', '', 'redemptionLanding');
					} else {
						// console.log("****** success *****************
						// ");
						allPartersData = responseData;
						putDataToCache("ALLPARTNERS", allPartersData);
					}
				},

				error : function(jqXHR, textStatus, errorThrown) {
					// console.log("****** error ***************** ");
					hideSpinner();
					var code = getResponseStatusCode(jqXHR);
					// console.log("****** error ***************** " +
					// code);

					switch (code) {

					case "1608":
						dfs.crd.rwd.insuficientErrorPresent = 'true';
						// console.log("error 1608");
						break;
					case "1629":
						cpEvent.preventDefault();
						rewardErrorFlag = true; // Fix for defect 96754
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
							errorHandler('0', '', 'redemptionLanding');
						}
						break;
					case "1656":
						// console.log("error 1656");
						cpEvent.preventDefault();
						rewardErrorFlag = true; // Fix for defect 96754
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
							errorHandler('0', '', 'redemptionLanding');
						}
						break;
					default:
						cpEvent.preventDefault();
					errorHandler(code, '', 'redemptionLanding');
					break;
					}
				}

			});
		}

		return allPartersData;

	} catch (err) {
		showSysException(err)
	}

}

/** *************** BROWSE LANDING LOAD **************** */

function browseLandingLoad() {

	try {
		showSpinner();
		killDataFromCache("REDEEMPARTNERDATA"); // kill any partner data if
		// present if user click back to
		// browse landing to select
		// another merchant
		killDataFromCache("REDEEMSELCETED");
		/** *********************************************************** */
		var cachedAllPartnerData = getDataFromCache("ALLPARTNERS");
		var totalPartnersCount;
		var totalEcertsCount;
		var totalPartnerGCCount;
		var browseAllPartnersStr = "Browse All Partners ";
		var browseEcertsStr = "eCertificates ";
		var browsegiftCardsStr = "Gift Cards ";		
		if (!jQuery.isEmptyObject(cachedAllPartnerData)) {
			// console.log("cachedAllPartnerData not empty !!");
			totalPartnersCount = cachedAllPartnerData.totalCount;
			totalEcertsCount = cachedAllPartnerData.eCertCount;
			totalPartnerGCCount = cachedAllPartnerData.giftCardCount;
			$("#allpartners").html(
					browseAllPartnersStr + "(" + totalPartnersCount + ")");
			$("#ecerts").html(browseEcertsStr + "(" + totalEcertsCount + ")");
			$("#partnerGC").html(
					browsegiftCardsStr + "(" + totalPartnerGCCount + ")");
		}
		// console.log("totalPartnersCount:: " + totalPartnersCount +
		// "::totalEcertsCount::"+ totalEcertsCount);

		$("#browseLanding_CBB").html("$" + globalEarnRewardAmount);

		/*
		 * $("#browse-all-partners").click(function(){
		 * //console.log("*********************** browse-all-partners click
		 * ************************ " );
		 * navigation('../rewards/browseAllPartners'); });
		 */
		$("#browse-all-ecert").click(function() {
			s.prop1 = 'HANDSET_REDEEM_PGC_ECERT_ECERT_BTN'; // campaign code
			navigation('../rewards/browse_all_ecert');
		});
		$("#browse-all-gcard").click(function() {
			s.prop1 = 'HANDSET_REDEEM_PGC_ECERT_GIFT_CARD_BTN'; // campaign code
			navigation('../rewards/browse_all_gcard');
		});
		$("#redeem-best-value").click(function() {
			s.prop1 = 'HANDSET_REDEEM_PGC_ECERT_BEST_VALUE_BTN';// campaign code
			navigation('../rewards/redeemBestValue');
		});

		$("#RST").click(function() {
			s.prop1 = 'HANDSET_REDEEM_PGC_ECERT_REST_BTN'; // campaign code
			dfs.crd.rwd.browseCategory('RST');

		});
		$("#HOA").click(function() {
			s.prop1 = 'HANDSET_REDEEM_PGC_ECERT_HOME_BTN'; // campaign code
			dfs.crd.rwd.browseCategory('HOA');

		});
		$("#FSN").click(function() {
			s.prop1 = 'HANDSET_REDEEM_PGC_ECERT_FASH_BTN'; // campaign code
			dfs.crd.rwd.browseCategory('FSN');

		});
		$("#DEP").click(function() {
			s.prop1 = 'HANDSET_REDEEM_PGC_ECERT_DEPT_STORE_BTN'; // campaign
			// code
			dfs.crd.rwd.browseCategory('DEP');

		});
		$("#ENT").click(function() {
			s.prop1 = 'HANDSET_REDEEM_PGC_ECERT_ENTERTAIN_BTN'; // campaign code
			dfs.crd.rwd.browseCategory('ENT');

		});
		$("#BTY").click(function() {
			s.prop1 = 'HANDSET_REDEEM_PGC_ECERT_HLTH_BEAU_BTN'; // campaign code
			dfs.crd.rwd.browseCategory('BTY');

		});
		$("#SPR").click(function() {
			s.prop1 = 'HANDSET_REDEEM_PGC_ECERT_SPRTS_REC_BTN'; // campaign code
			dfs.crd.rwd.browseCategory('SPR');

		});
		$("#GFI").click(function() {
			s.prop1 = 'HANDSET_REDEEM_PGC_ECERT_GIFTS_BTN'; // campaign code
			dfs.crd.rwd.browseCategory('GFI');

		});
		$("#TVL").click(function() {
			s.prop1 = 'HANDSET_REDEEM_PGC_ECERT_TRAVEL_BTN'; // campaign code
			dfs.crd.rwd.browseCategory('TVL');

		});
		hideSpinner();

	} catch (err) {
		hideSpinner();
		showSysException(err)
	}

}

$("#browse-landing").live('pageshow', function() {
	$("#browse-all-partners").bind('click', function() {
		s.prop1 = 'HANDSET_REDEEM_PGC_ECERT_BROWSE_ALL_BTN'; // campaign code
		// console.log("*********************** browse-all-partners click
		// ************************ " );
		navigation('../rewards/browseAllPartners');

	});
});

/**
 * ***************************** Browsing categories
 * *********************************
 */

dfs.crd.rwd.browseCategory = function(categoryCode) {

	try {

		// console.log("browseCategory :: " + categoryCode);
		killDataFromCache("REDEEMPARTNERDATA"); // kill any partner data if
		// present if user click back to
		// browse landing to select
		// another merchant
		killDataFromCache("REDEEMSELCETED");

		/** *********************************************************** */
		var partnerCategoryDetails = dfs.crd.rwd
		.getPartnerCategoryData(categoryCode);
		if (!jQuery.isEmptyObject(partnerCategoryDetails)) {
			putDataToCache("CATEGORYDATA", partnerCategoryDetails);
			navigation('../rewards/browseCategory');
		}

	} catch (err) {
		showSysException(err)
	}

}

dfs.crd.rwd.getPartnerCategoryData = function(categoryCode) {
	try {
		// console.log("getPartnerCategoryData :: " + categoryCode);
		var newData = new Date();
		var getCategoryDataURL = RESTURL
		+ "rewards/v2/partners?rewardType=ALL&category=" + categoryCode;
		// console.log("getCategoryDataURL :: " + getCategoryDataURL);
		var partnerCatgData = new Object();
		showSpinner();
		$.ajax({

			type : "GET",
			url : getCategoryDataURL,
			async : false,
			dataType : 'json',
			headers : prepareGetHeader(),
			success : function(responseData, status, jqXHR) {
				hideSpinner();
				if (!validateResponse(responseData,"partnersCategoryDataValidation")){ // Pen Test Validation
					errorHandler("SecurityTestFail","","");
					return;
				}
				if (jqXHR.status != 200 & jqXHR.status != 204) {
					var code = getResponseStatusCode(jqXHR);
					errorHandler('0', '', 'redemptionLanding');
				} else {
					// console.log("success !!");
					partnerCatgData = responseData;
				}

			},
			error : function(jqXHR, textStatus, errorThrown) {
				// console.log("****** error ***************** ");
				hideSpinner();
				cpEvent.preventDefault();
				var code = getResponseStatusCode(jqXHR);
				// console.log("****** error ***************** " + code);
				switch (code) {

				case "1608":
					navigation('../rewards/redeemPartnerInsufficientError');
					break;
				case "1629":
					var errorMessage = errorCodeMap.REDEEM_BAD_ACCOUNT_STATUS;
					var promoCodeTextData = [];
					promoCodeTextData['ACHome_CashBackBonusBalance'] = "$"
						+ globalEarnRewardAmount;
					var parseContentText = parseContent(errorMessage,
							promoCodeTextData);
					if (!isEmpty(parseContentText)) {
						errorHandler(code, parseContentText,
						'redemptionLanding');
					} else {
						errorHandler('0', '', 'browseCategory');
					}
					break;
				case "1656":
					// console.log("error 1656");
					cpEvent.preventDefault();
					var errorMessage = errorCodeMap.REDEEM_BAD_ACCOUNT_STATUS;
					var promoCodeTextData = [];
					promoCodeTextData['ACHome_CashBackBonusBalance'] = "$"
						+ globalEarnRewardAmount;
					var parseContentText = parseContent(errorMessage,
							promoCodeTextData);
					if (!isEmpty(parseContentText)) {
						errorHandler(code, parseContentText,
						'redemptionLanding');
					} else {
						errorHandler('0', '', 'redemptionLanding');
					}
					break;
				default:
					errorHandler(code, '', 'browseCategory');
				break;
				}
			}
		});
		return partnerCatgData;
	} catch (err) {
		showSysException(err)
	}

}

function browseCategoryLoad() {
	try {

		// console.log("browseCategoryLoad !!");
		var cachedCategoryData = getDataFromCache("CATEGORYDATA");
		if (!jQuery.isEmptyObject(cachedCategoryData)) {
			// console.log("partner category data present in cache");
			$("#browseCategory_CBB").html("$" + globalEarnRewardAmount);
			var partnerCatgList = cachedCategoryData.partners;
			// console.log("got partnerCatgList");
			if (!isEmpty(partnerCatgList) && partnerCatgList.length !== 0) {
				// console.log(" partnerCatgList present");

				var catgCounts = cachedCategoryData.ctgyCounts;
				var catgCount;
				var catgCd;
				var catgDs;
				// console.log(" catgCounts ::" + catgCounts);
				/*
				 * For handset only one category data will be presented . So
				 * there will be only one category in the ctgyCounts list
				 */
				if (!isEmpty(catgCounts) && catgCounts.length !== 0) {
					// console.log("empty check");
					catgCount = catgCounts[0].ctgyCount;
					catgCd = catgCounts[0].ctgyCode;
					catgDs = catgCounts[0].ctgyDesc;
					// console.log("catgCount ::" + catgCount + ":: catgCd::"+
					// catgCd);
					$("#categoryCount").html(catgCount);
					$("#categoryDesc").html(
							catgDs + " " + "<span>(" + catgCount + ")</span>");

				}

				var partnerCatgListUL = "<ul class='partners' data-filter-placeholder='Search "
					+ catgDs
					+ " Partners' data-icon='search' data-role='listview' data-filter='true' data-theme='d' data-inset='true' data-filter-theme='d' data-icon='search'>";

				$
				.each(
						partnerCatgList,
						function(category) {
							var partnerCatgModeCode = partnerCatgList[category].modeCode;
							var partnerCatgModeDesc = partnerCatgList[category].modeDesc;
							var ctgmissingAmt = partnerCatgList[category].amtAwayToRedeem;
							var ctgminRedemptionAmount = partnerCatgList[category].minModeAmt;
							var ctgminWhatYouGetAmount = partnerCatgList[category].minDisbAmt;
							var catgUnavailableStr = "<p> You're only $"
								+ ctgmissingAmt
								+ " <i>Cashback Bonus</i> away from being able to get $"
								+ ctgminRedemptionAmount
								+ " for $"
								+ ctgminWhatYouGetAmount + ".</p>";
							var catgpg = "fromALL";
							var activeCatgLi = "";
							if (partnerCatgList[category].isPartnerAvail === true) { // for
								// available
								// partners

								activeCatgLi += "<li data-filtertext='"
									+ partnerCatgModeDesc + "'>";
								activeCatgLi += "<a href='#' onClick=dfs.crd.rwd.renderPartnerDetails('"
									+ partnerCatgModeCode
									+ "','"
									+ catgpg + "')>";
								activeCatgLi += "<img width='75' height='36' src='"
									+ dfs.crd.rwd.availablePartners
									+ ""
									+ partnerCatgModeCode
									+ ".png' data-default-image='../../images/DefaultImage_87X55.png' onerror='defaultImage(this);'";
								activeCatgLi += "><h3>"
									+ partnerCatgModeDesc
									+ "</h3></a>";
								activeCatgLi += "</li>";
								partnerCatgListUL += activeCatgLi;

							} else { // for Unavailable partners
								activeCatgLi += "<li class='disabled-partnerlist' data-filtertext='"
									+ partnerCatgModeDesc + "'>";
								activeCatgLi += "<a href='#'>";
								activeCatgLi += "<img width='75' height='36' src='"
									+ dfs.crd.rwd.unavailablePartners
									+ ""
									+ partnerCatgModeCode
									+ ".png' data-default-image='../../images/DefaultImage_87X55.png' onerror='defaultImage(this);'";
								activeCatgLi += "><h3>"
									+ partnerCatgModeDesc + "</h3>";
								activeCatgLi += catgUnavailableStr;
								activeCatgLi += "</a>";
								activeCatgLi += "</li>";

								partnerCatgListUL += activeCatgLi;

							}

						});

				partnerCatgListUL += "</ul>";
				$("#allPartnerCatgList").html(partnerCatgListUL);
				$("#allPartnerCatgList").trigger("create");

				// console.log("build partnerCatgListUL");

			}

		}

	} catch (err) {
		showSysException(err)
	}

}

/** ********************************************************************************** */

function browseAllPartnersLoad() {
	try {
		showSpinner();
		// console.log("*********************** browseAllPartnersLoad
		// ************************ " );
		killDataFromCache("REDEEMPARTNERDATA"); // kill any partner data if
		// present if user click back to
		// browse landing to select
		// another merchant
		killDataFromCache("REDEEMSELCETED");
		/** *********************************************************** */
		var cachedallPartnerData = getDataFromCache("ALLPARTNERS");
		if (!jQuery.isEmptyObject(cachedallPartnerData)) {
			$("#browseAll_CBB").html("$" + globalEarnRewardAmount);

			var allPartnersULList = "<ul class='partners' data-filter-placeholder='Search All Partners' data-role='listview' data-filter='true' data-theme='d' data-inset='true' data-filter-theme='d' data-icon='search'>";
			var allPartnersList = cachedallPartnerData.partners;
			// console.time('TestEach');

			if (allPartnersList !== null && allPartnersList !== 'undefined'
				&& allPartnersList !== '' && allPartnersList.length !== 0) {
				$
				.each(
						allPartnersList,
						function(partner) {
							var partnerModeCode = allPartnersList[partner].modeCode;
							var partnerModeDesc = allPartnersList[partner].modeDesc;
							var missingAmt = allPartnersList[partner].amtAwayToRedeem;
							var minRedemptionAmount = allPartnersList[partner].minModeAmt;
							var minWhatYouGetAmount = allPartnersList[partner].minDisbAmt;
							var unavailableStr = "<p> You're only $"
								+ missingAmt
								+ " <i>Cashback Bonus</i> away from being able to get $"
								+ minRedemptionAmount + " for $"
								+ minWhatYouGetAmount + ".</p>";
							// var unavailableStr = "<p >You don't have
							// enough <i><i>Cashback Bonus</i></i> to
							// redeem at this merchant.Keep
							// earning!</p>";
							var pg = "fromALL";
							var activePartnerLi = "";
							if (allPartnersList[partner].isPartnerAvail === true) { // for
								// available
								// partners

								activePartnerLi += "<li data-filtertext='"
									+ partnerModeDesc + "'>";
								activePartnerLi += "<a href='#' onClick=dfs.crd.rwd.renderPartnerDetails('"
									+ partnerModeCode
									+ "','"
									+ pg
									+ "')>";
								activePartnerLi += "<img width='75' height='36' src='"
									+ dfs.crd.rwd.availablePartners
									+ ""
									+ partnerModeCode
									+ ".png' data-default-image='../../images/DefaultImage_87X55.png' onerror='defaultImage(this);'";
								activePartnerLi += "><h3>"
									+ partnerModeDesc + "</h3></a>";
								activePartnerLi += "</li>";
								allPartnersULList += activePartnerLi;

							} else { // for Unavailable partners
								activePartnerLi += "<li class='disabled-partnerlist' data-filtertext='"
									+ partnerModeDesc + "'>";
								activePartnerLi += "<a href='#'>";
								activePartnerLi += "<img width='75 height='36' src='"
									+ dfs.crd.rwd.unavailablePartners
									+ ""
									+ partnerModeCode
									+ ".png' data-default-image='../../images/DefaultImage_87X55.png' onerror='defaultImage(this);'";
								activePartnerLi += "><h3>"
									+ partnerModeDesc + "</h3>";
								activePartnerLi += unavailableStr;
								activePartnerLi += "</a>";
								activePartnerLi += "</li>";

								allPartnersULList += activePartnerLi;

							}
						});

				allPartnersULList += "</ul>";
				// console.timeEnd('TestEach');

				$("#allPartnerList").html(allPartnersULList);
				$("#allPartnerList").trigger("create");
			}

			/**
			 * ******************************* 526 ms
			 * *******************************************
			 */
			/*
			 * console.time('TestjQuery'); if (typeof allPartnersList !==
			 * 'undefined') { var partnerListLen = allPartnersList.length;
			 * //console.log(partnerListLen); for(i=0;i<partnerListLen;i++){
			 * 
			 * var partnerData = allPartnersList[i]; var partnerModeCode =
			 * partnerData.modeCode; var partnerModeDesc = partnerData.modeDesc;
			 * var missingAmt = partnerData.amtAwayToRedeem; var
			 * minRedemptionAmount = partnerData.minModeAmt; var
			 * minWhatYouGetAmount = partnerData.minDisbAmt; var unavailableStr = "<p>
			 * You're only "+missingAmt+" <i>Cashback Bonus</i> away from being
			 * able to get "+minRedemptionAmount+" for "+minWhatYouGetAmount+".</p>";
			 * 
			 * var activePartnerLi = ""; if(partnerData.isPartnerAvail ===
			 * true){ // for available partners
			 * 
			 * activePartnerLi += "<li>"; activePartnerLi += "<a href='#'
			 * onClick=renderRedeemPartner1('"+ partnerModeCode + "')>";
			 * activePartnerLi += "<img width='104' height='56' src='"+ "" +
			 * ""+ partnerModeCode + ".png'"; activePartnerLi += "><h3>"+partnerModeDesc+"</h3></a>";
			 * activePartnerLi += "</li>"; allPartnersULList +=
			 * activePartnerLi;
			 * 
			 * }else{ //for Unavailable partners activePartnerLi += "<li class='disabled-partnerlist'>";
			 * activePartnerLi += "<a href='#'>"; activePartnerLi += "<img
			 * width='104' height='56' src='"+ "" + ""+ partnerModeCode +
			 * ".png'"; activePartnerLi += "><h3>"+partnerModeDesc+"</h3>";
			 * activePartnerLi += unavailableStr; activePartnerLi += "</a>";
			 * activePartnerLi += "</li>";
			 * 
			 * allPartnersULList += activePartnerLi; } } allPartnersULList +="</ul>";
			 * console.timeEnd('TestjQuery');
			 * 
			 * $("#allPartnerList").html(allPartnersULList);
			 * $("#allPartnerList").trigger("create");
			 * ////console.log("allPartnersULList :: " + allPartnersULList); }
			 */

		}
		hideSpinner();
	} catch (err) {
		hideSpinner();
		showSysException(err)
	}

}

function browse_all_ecertLoad() {
	showSpinner();
	killDataFromCache("REDEEMPARTNERDATA"); // kill any partner data if present
	// if user click back to browse
	// landing to select another
	// merchant
	killDataFromCache("REDEEMSELCETED");
	/** *********************************************************** */
	// console.log("browse_all_ecertLoad");
	try {
		var allPartnersData = getDataFromCache("ALLPARTNERS");
		if (!jQuery.isEmptyObject(allPartnersData)) {
			$("#browseAllEcert_CBB").html("$" + globalEarnRewardAmount);

			var allEcertsULList = "<ul id='browseEcertificates' class='partners' data-filter-placeholder='Search eCertificate Partners' data-role='listview' data-filter='true' data-theme='d' data-inset='true' data-filter-theme='d' data-icon='search'>";
			var allpartnersList = allPartnersData.partners;
			// console.time('TestBrowseEcerts');

			var availEcertsCount = 0;
			var unavailEcertsCount = 0;
			var pgECT = "fromECT";
			if (allpartnersList !== null && allpartnersList !== 'undefined'
				&& allpartnersList !== '' && allpartnersList.length !== 0) {
				$
				.each(
						allpartnersList,
						function(partner) {
							var epartnerModeCode = allpartnersList[partner].modeCode;
							var epartnerModeDesc = allpartnersList[partner].modeDesc;
							var emissingAmt = allpartnersList[partner].amtAwayToRedeem;
							var eminRedemptionAmount = allpartnersList[partner].minModeAmt;
							var eminWhatYouGetAmount = allpartnersList[partner].minDisbAmt;
							var isAvailablePartner = allpartnersList[partner].isPartnerAvail;
							var unavailableEcertStr = "<p> You're only $"
								+ emissingAmt
								+ " <i>Cashback Bonus</i> away from being able to get $"
								+ eminRedemptionAmount
								+ " for $"
								+ eminWhatYouGetAmount + ".</p>";
							// var unavailableEcertStr = "<p >You don't
							// have enough <i><i>Cashback Bonus</i></i>
							// to redeem at this merchant.Keep
							// earning!</p>";
							var browseEcertLi = "";
							if (allpartnersList[partner].hasECert === true) { // for
								// available
								// partners

								if (allpartnersList[partner].isPartnerAvail === true) {
									browseEcertLi += "<li data-filtertext='"
										+ epartnerModeDesc + "'>";
									browseEcertLi += "<a href='#' onClick=dfs.crd.rwd.renderPartnerDetails('"
										+ epartnerModeCode
										+ "','"
										+ pgECT + "')>";
									browseEcertLi += "<img width='75' height='36' src='"
										+ dfs.crd.rwd.availablePartners
										+ ""
										+ epartnerModeCode
										+ ".png' data-default-image='../../images/DefaultImage_87X55.png' onerror='defaultImage(this);'";
									browseEcertLi += "><h3>"
										+ epartnerModeDesc
										+ "</h3></a>";
									browseEcertLi += "</li>";
									allEcertsULList += browseEcertLi;
									availEcertsCount = availEcertsCount + 1;
								} else {
									browseEcertLi += "<li class='disabled-partnerlist' data-filtertext='"
										+ epartnerModeDesc + "'>";
									browseEcertLi += "<a href='#' onClick=renderRedeemPartner1('"
										+ epartnerModeCode + "')>";
									browseEcertLi += "<img width='75' height='36' src='"
										+ dfs.crd.rwd.unavailablePartners
										+ ""
										+ epartnerModeCode
										+ ".png' data-default-image='../../images/DefaultImage_87X55.png' onerror='defaultImage(this);'";
									browseEcertLi += "><h3>"
										+ epartnerModeDesc
										+ "</h3>"
										browseEcertLi += unavailableEcertStr;
									browseEcertLi += "</a>";
									browseEcertLi += "</li>";
									allEcertsULList += browseEcertLi;
									unavailEcertsCount = unavailEcertsCount + 1;

								}

							}
						});

				allEcertsULList += "</ul>";
				// console.timeEnd('TestBrowseEcerts');

				// console.log("availEcertsCount :: "+ availEcertsCount + "::
				// unavailEcertsCount:: " + unavailEcertsCount);

				$("#ALLECERTS").html(allEcertsULList);
				$("#ALLECERTS").trigger("create");
				// $("#browseEcertificates").trigger("create");
				// $("#browse-all-ecertificates").trigger("create");

			}
		}

		hideSpinner();
	} catch (err) {
		hideSpinner();
		showSysException(err)
	}

}

function browse_all_gcardLoad() {
	showSpinner();
	killDataFromCache("REDEEMPARTNERDATA"); // kill any partner data if present
	// if user click back to browse
	// landing to select another
	// merchant
	killDataFromCache("REDEEMSELCETED");
	/** *********************************************************** */
	// console.log("browse_all_gcardLoad");
	try {
		var allPartnerDetails = getDataFromCache("ALLPARTNERS");
		if (!jQuery.isEmptyObject(allPartnerDetails)) {

			// console.log("globalEarnRewardAmount :: "+
			// globalEarnRewardAmount);

			$("#browseAllGiftCard_CBB").html("$" + globalEarnRewardAmount);

			var allGcardULList = "<ul id='browseGiftCards' class='partners' data-filter-placeholder='Search Gift Card Partners' data-icon='search' data-role='listview' data-filter='true' data-theme='d' data-inset='true' data-filter-theme='d' data-icon='search'>";
			var allPrtnrsList = allPartnerDetails.partners;
			// console.time('TestBrowseGcards');

			var availGcardsCount = 0;
			var unavailGcardsCount = 0;
			var pgGCD = "fromGCD";
			if (allPrtnrsList !== null && allPrtnrsList !== 'undefined'
				&& allPrtnrsList !== '' && allPrtnrsList.length !== 0) {
				$
				.each(
						allPrtnrsList,
						function(partner) {
							var gcpartnerModeCode = allPrtnrsList[partner].modeCode;
							var gcpartnerModeDesc = allPrtnrsList[partner].modeDesc;
							var gcmissingAmt = allPrtnrsList[partner].amtAwayToRedeem;
							var gcminRedemptionAmount = allPrtnrsList[partner].minModeAmt;
							var gcminWhatYouGetAmount = allPrtnrsList[partner].minDisbAmt;
							var unavailableGcardtStr = "<p> You're only $"
								+ gcmissingAmt
								+ " <i>Cashback Bonus</i> away from being able to get $"
								+ gcminRedemptionAmount
								+ " for $"
								+ gcminWhatYouGetAmount + ".</p>";
							// var unavailableGcardtStr = "<p >You don't
							// have enough <i><i>Cashback Bonus</i></i>
							// to redeem at this merchant.Keep
							// earning!</p>";
							var browseCardLi = "";
							if (allPrtnrsList[partner].hasGiftCard === true) {

								if (allPrtnrsList[partner].isPartnerAvail === true) {// for
									// available
									// partners

									browseCardLi += "<li data-filtertext='"
										+ gcpartnerModeDesc + "'>";
									browseCardLi += "<a href='#' onClick=dfs.crd.rwd.renderPartnerDetails('"
										+ gcpartnerModeCode
										+ "','"
										+ pgGCD + "')>";
									browseCardLi += "<img width='75' height='36' src='"
										+ dfs.crd.rwd.availablePartners
										+ ""
										+ gcpartnerModeCode
										+ ".png' data-default-image='../../images/DefaultImage_87X55.png' onerror='defaultImage(this);'";
									browseCardLi += "><h3>"
										+ gcpartnerModeDesc
										+ "</h3></a>";
									browseCardLi += "</li>";
									allGcardULList += browseCardLi;
									availGcardsCount = availGcardsCount + 1;

								} else {
									browseCardLi += "<li class='disabled-partnerlist' data-filtertext='"
										+ gcpartnerModeDesc + "'>";
									browseCardLi += "<a href='#' onClick=renderRedeemPartner1('"
										+ gcpartnerModeCode + "')>";
									browseCardLi += "<img width='75' height='35' src='"
										+ dfs.crd.rwd.unavailablePartners
										+ ""
										+ gcpartnerModeCode
										+ ".png' data-default-image='../../images/DefaultImage_87X55.png' onerror='defaultImage(this);'";
									browseCardLi += "><h3>"
										+ gcpartnerModeDesc
										+ "</h3>";
									browseCardLi += unavailableGcardtStr;
									browseCardLi += "</a>";
									browseCardLi += "</li>";
									allGcardULList += browseCardLi;
									unavailGcardsCount = unavailGcardsCount + 1;

								}

							}
						});

				allGcardULList += "</ul>";
				// console.timeEnd('TestBrowseGcards');

				// console.log("availGcardsCount :: "+ availGcardsCount + "::
				// unavailGcardsCount:: " + unavailGcardsCount);

				$("#ALLGCARDS").html(allGcardULList);
				$("#ALLGCARDS").trigger("create");
				$("#browseGiftCards").trigger("create");
				$("#browse-all-giftcards").trigger("create");
			}

		}
		hideSpinner();
	} catch (err) {
		hideSpinner();
		showSysException(err)
	}

}

dfs.crd.rwd.renderPartnerDetails = function(modeCode, pageName) {

	try {
		pageNameT = pageName;
		var partnerDetails = dfs.crd.rwd.getPartnerDetails(modeCode);
		if (!jQuery.isEmptyObject(partnerDetails)) {
			putDataToCache("REDEEMPARTNERDATA", partnerDetails);
			navigation('../rewards/redeemPartner1');
		}
	} catch (err) {
		showSysException(err)
	}

}

dfs.crd.rwd.getPartnerDetails = function(modeCode) {
	try {
		// AJAX CALL
		var partnerData; // = getDataFromCache("REDEEMPARTNERDATA");;
		var newData = new Date();
		var PARTNERSURL = RESTURL + "rewards/v2/redeemoption?modeCode="
		+ modeCode + "&" + newData + "";
		showSpinner();
		// console.log("getPartnerDetails :: URL :: "+ PARTNERSURL);
		$.ajax({
			type : "GET",
			url : PARTNERSURL,
			async : false,
			dataType : 'json',
			headers : prepareGetHeader(),
			success : function(responseData, status, jqXHR) {
				hideSpinner();
				if (!validateResponse(responseData,"redeemPartnerDetailsValidation")){ // Pen Test Validation
					errorHandler("SecurityTestFail","","");
					return;
				}
				// console.log("jqXHR.status ::" + jqXHR.status);
				if (jqXHR.status != 200 & jqXHR.status != 204) {
					var code = getResponseStatusCode(jqXHR);
					errorHandler(code, '', 'redeemPartner1');
				} else {
					// console.log("got partner data ::");
					partnerData = responseData;
				}
			},
			error : function(jqXHR, textStatus, errorThrown) {
				hideSpinner();
				var code = getResponseStatusCode(jqXHR);
				// console.log("error ::" + code);
				cpEvent.preventDefault();

				switch (code) {
				case "1608":
                    errorPartnerGiftCardSCVariables();//passing site catalystvariable for Error - Partner Gift Cards/eCertificates - less than $20 available to redeem
					navigation('../rewards/redeemPartnerInsufficientError');
					break;
				case "1629":
					var errorMessage = errorCodeMap.REDEEM_BAD_ACCOUNT_STATUS;
					var promoCodeTextData = [];
					promoCodeTextData['ACHome_CashBackBonusBalance'] = "$"
						+ globalEarnRewardAmount;
					var parseContentText = parseContent(errorMessage,
							promoCodeTextData);
					if (!isEmpty(parseContentText)) {
						errorHandler(code, parseContentText,
						'redemptionLanding');
					} else {
						errorHandler('0', '', 'redeemPartner1');
					}
					break;
				case "1656":
					// console.log("error 1656");
					cpEvent.preventDefault();
					var errorMessage = errorCodeMap.REDEEM_BAD_ACCOUNT_STATUS;
					var promoCodeTextData = [];
					promoCodeTextData['ACHome_CashBackBonusBalance'] = "$"
						+ globalEarnRewardAmount;
					var parseContentText = parseContent(errorMessage,
							promoCodeTextData);
					if (!isEmpty(parseContentText)) {
						errorHandler(code, parseContentText,
						'redemptionLanding');
					} else {
						errorHandler('0', '', 'redemptionLanding');
					}
					errorGiftCardRedemptionSCVariables();//passing site catalyst variable for Error Tracking - Gift Cards - Unable to Redeem (Regulation)
					break;
				case "1613":
					var errorMessage = errorCodeMap.REDEEM_EMC_STATUS;
					var promoCodeTextData = [];
					promoCodeTextData['ACHome_CashBackBonusBalance'] = "$"
						+ globalEarnRewardAmount;
					var parseContentText = parseContent(errorMessage,
							promoCodeTextData);
					if (!isEmpty(parseContentText)) {
						errorHandler(code, parseContentText,
						'redemptionLanding');
					} else {
						errorHandler('0', '', 'redeemPartner1');
					}
					break;
				default:
					errorHandler(code, '', 'redeemPartner1');
				break;
				}

			}
		});

		return partnerData;

	} catch (err) {
		showSysException(err)
	}

}

function redeemPartner1Load() {
	try {

		killDataFromCache("ECERT_REDEEM_HISTORY_DETAILS");
		var partnerDataForRedeem = getDataFromCache("REDEEMPARTNERDATA");

		$("#redeemPartner1_CBB").html("$" + globalEarnRewardAmount);
		if (!jQuery.isEmptyObject(partnerDataForRedeem)) {

			var hasEcert = partnerDataForRedeem.hasECert;
			var hasGiftCard = partnerDataForRedeem.hasGiftCard;
			var pmodeCode = partnerDataForRedeem.modeCode;
			var pmodeDesc = partnerDataForRedeem.modeDesc;
			var billingDetails = '';
			var rewardsBalance = partnerDataForRedeem.currentBalance;
			if (partnerDataForRedeem.billingAddr !== null
					&& partnerDataForRedeem.billingAddr !== 'undefined'
						&& partnerDataForRedeem.billingAddr !== '') {
				billingDetails = partnerDataForRedeem.billingAddr;
			}
			$("#pmodeDesc").html(pmodeDesc);
			// console.log("Merchant description ::::: " +
			// partnerDataForRedeem.partnerDesc);
			$("#merchantDesc").html(partnerDataForRedeem.partnerDesc);

			$("#stepper1").val("1"); // set the default quantity

			/*
			 * If call is coming from Browse ALL,Browse all ECertificates, Best
			 * value, categories then ecertificates will be selected by default.
			 * If call is coming from Browse all gift cards then gift card will
			 * be selected by default
			 */

			if (hasEcert === true && hasGiftCard === true) {
				$("#ecertFloat").addClass("floatright");

				if (pageNameT === "fromALL") {// Call coming from Browse ALL
					// console.log("all");
					$("#redeem-dtlview").jqmData('ECT', 'true');
					$("#redeem-dtlview").jqmData('GCD', '');
					$("#ecertFloat")
					.html(
					"<span class='amt-btn'><a href='#' class='blk-wht-btn r5-btn ui-link' id='btn-eCertificate'><span class='chk chk-icon'></span>eCertificate</a></span>");
					$("#giftCardPresent")
					.html(
					"<a href='#' class='drkgry-wht-btn r5-btn ui-link' id='btn-giftCard'><span class='chk'></span>Gift Card</a>");
				} else if (pageNameT === "fromECT") {// Call coming from
					// Browse Ecerts
					$("#redeem-dtlview").jqmData('ECT', 'true');
					$("#redeem-dtlview").jqmData('GCD', '');
					$("#ecertFloat")
					.html(
					"<span class='amt-btn'><a href='#' class='blk-wht-btn r5-btn ui-link' id='btn-eCertificate'><span class='chk chk-icon'></span>eCertificate</a></span>");
					$("#giftCardPresent")
					.html(
					"<a href='#' class='drkgry-wht-btn r5-btn ui-link' id='btn-giftCard'><span class='chk'></span>Gift Card</a>");

				} else if (pageNameT === "fromGCD") {// Call coming from
					// Browse Gcards
					$("#redeem-dtlview").jqmData('GCD', 'true');
					$("#redeem-dtlview").jqmData('ECT', '');
					$("#ecertFloat")
					.html(
					"<span class='amt-btn'><a href='#' class='drkgry-wht-btn r5-btn ui-link' id='btn-eCertificate'><span class='chk'></span>eCertificate</a></span>");
					$("#giftCardPresent")
					.html(
					"<a href='#' class='blk-wht-btn r5-btn ui-link' id='btn-giftCard'><span class='chk chk-icon'></span>Gift Card</a>");

				}

			} else if (hasEcert === true && hasGiftCard === false) {
				$("#redeem-dtlview").jqmData('ECT', 'true');
				$("#redeem-dtlview").jqmData('GCD', 'false');
				// console.log("only Ecert present");
				$("#giftCardPresent")
				.html(
				"<a href='#' class='blk-wht-btn r5-btn ui-link'><span class='chk chk-icon'></span>eCertificate</a>");
				$("#ecertFloat").html("");

			} else if (hasEcert === false && hasGiftCard === true) {
				// console.log("hasGiftCard true");
				$("#redeem-dtlview").jqmData('GCD', 'true');
				$("#redeem-dtlview").jqmData('ECT', 'false');
				// console.log("only gift present");
				$("#giftCardPresent")
				.html(
				"<a href='#' class='blk-wht-btn r5-btn ui-link' id='btn-giftCard'><span class='chk chk-icon'></span>Gift Card</a>");

			}

			$("#btn-giftCard").click(function() {
				$("#redeem-dtlview").jqmData('GCD', 'true');
				$("#redeem-dtlview").jqmData('ECT', '');
			});

			$("#btn-eCertificate").click(function() {
				$("#redeem-dtlview").jqmData('GCD', '');
				$("#redeem-dtlview").jqmData('ECT', 'true');
			});

			/** Building the disbursement amount sections * */

			var disbAmts = partnerDataForRedeem.disbAmts;
			var defaultAmount;
			var defaultModeAmt;
			var defaultDisbAmt;

			if (disbAmts !== null && disbAmts !== 'undefined'
				&& disbAmts !== '') {
				var amountCount = disbAmts.length;
				// console.log("amountCount ::" + amountCount);

				defaultAmount = disbAmts[0];
				defaultModeAmt = (defaultAmount.modeAmt).split(".")[0];
				defaultDisbAmt = (defaultAmount.disbAmt).split(".")[0];
				var defaultQuantity = defaultAmount.maxQty;
				var defaultSpan = '';
				defaultSpan += "<p class='bold-text'>Amount:</p>";
				defaultSpan += "<section data-amount-pay=" + defaultModeAmt
				+ " data-amount-get=" + defaultDisbAmt
				+ " data-max-qty=" + defaultQuantity + ">";
				defaultSpan += "<span class='amt-btn'> <a href='#' id='default' class='blk-wht-btn yellow-bg r5-btn ui-link'> <span class='chk chk-icon'></span><span> <span class='white-doller'>$"
					+ defaultModeAmt
					+ "</span> <span class='yellow-doller'>$"
					+ defaultDisbAmt + "</span> </span> </a> </span>"
					defaultSpan += "</section>"

						var amtSpan = '';
				for (i = 1; i < amountCount; i++) {
					var disbAmounts = disbAmts[i];
					var modeAmt = (disbAmounts.modeAmt).split(".")[0];
					var disbAmt = (disbAmounts.disbAmt).split(".")[0];
					var isAvailToRedeem = disbAmounts.isAvailableToRedeem;
					var maxQuantity = disbAmounts.maxQty;

					if (isAvailToRedeem === true) {
						amtSpan += "<section data-amount-pay=" + modeAmt
						+ " data-amount-get=" + disbAmt
						+ " data-max-qty=" + maxQuantity + ">";
						amtSpan += "<span class='amt-btn'><a href='#' id='"
							+ modeAmt
							+ "' class='yellow-bg r5-btn ui-link'> <span class='chk'></span><span> <span class='white-doller'>$"
							+ modeAmt
							+ "</span> <span class='yellow-doller'>$"
							+ disbAmt + "</span> </span></a></span>";
						amtSpan += "</section>";

					} else {
						amtSpan += "<section data-amount-pay='' data-amount-get='' data-max-qty=''>";
						amtSpan += "<span><a href='#' class='r5-btn disabled-btn ui-link'> <span class='chk'></span><span> <span class='white-doller'>$"
							+ modeAmt
							+ "</span> <span class='yellow-doller'>$"
							+ disbAmt + "</span> </span> </a></span>"
							amtSpan += "</section>";
					}
				}
				amtSpan = defaultSpan + amtSpan;

				$("#pills").html(amtSpan);
				$("#pills").trigger("create");

			}
			$("#partnerImg").attr('src',
					(dfs.crd.rwd.availablePartners + "" + pmodeCode + ".png"));

			$("#redeemPartner1Cancel").click(function() {
				navigation('../rewards/browseLanding');
			});

			/**
			 * ******************************* LOGIC TO IMPLEMENT BACK ON STEP1
			 * *****************************************
			 */

			var trySelected = getDataFromCache("REDEEMSELCETED");
			if (!jQuery.isEmptyObject(trySelected)) {
				// console.log("showing already selected now");
				var selGCD = trySelected.isGCD;
				var selECT = trySelected.ECT;
				var selectedMdAmt = trySelected.selectedModeAmount;
				var selAmtID = trySelected.selectedAmountId;
				var amtIdD = trySelected.alreadyCheckedAmount;
				// console.log("trySelected.selectedQuantity" +
				// trySelected.selectedQuantity);

				$("#stepper1").val(trySelected.selectedQuantity);
				/*
				 * if(trySelected.isGCD === true && trySelected.ECT === false){
				 * //console.log("only gift card"); $("#ecertFloat").html("<span
				 * class='amt-btn'><a href='#' class='drkgry-wht-btn r5-btn
				 * ui-link' id='btn-eCertificate'><span class='chk'></span>eCertificate</a></span>");
				 * $("#giftCardPresent").html("<a href='#' class='blk-wht-btn
				 * r5-btn ui-link' id='btn-giftCard'><span class='chk
				 * chk-icon'></span>Gift Card</a>");
				 * 
				 * }else if(trySelected.isGCD === false && trySelected.ECT ===
				 * true){ //console.log("only ECT");
				 * $("#giftCardPresent").html("<a href='#' class='blk-wht-btn
				 * r5-btn ui-link'><span class='chk chk-icon'></span>eCertificate</a>");
				 * $("#ecertFloat").html("<p class='text ecart-txt ecart-padd'>eCertificates
				 * can be used <br />instantly online or in stores.</p>");
				 * 
				 * }else
				 */

				if (selGCD === 'true' && !selECT.length > 0 && hasEcert === true) {

					$("#redeem-dtlview").jqmData('GCD', 'true');
					$("#redeem-dtlview").jqmData('ECT', '');
					$("#ecertFloat")
					.html(
					"<span class='amt-btn'><a href='#' class='drkgry-wht-btn r5-btn ui-link' id='btn-eCertificate'><span class='chk'></span>eCertificate</a></span>");
					$("#giftCardPresent")
					.html(
					"<a href='#' class='blk-wht-btn r5-btn ui-link' id='btn-giftCard'><span class='chk chk-icon'></span>Gift Card</a>");

				} else if (!selGCD.length > 0 && selECT === 'true') {

					$("#redeem-dtlview").jqmData('ECT', 'true');
					$("#redeem-dtlview").jqmData('GCD', '');
					$("#ecertFloat")
					.html(
					"<span class='amt-btn'><a href='#' class='blk-wht-btn r5-btn ui-link' id='btn-eCertificate'><span class='chk chk-icon'></span>eCertificate</a></span>");
					$("#giftCardPresent")
					.html(
					"<a href='#' class='drkgry-wht-btn r5-btn ui-link' id='btn-giftCard'><span class='chk'></span>Gift Card</a>");

				}

				$("#btn-giftCard").click(function() {
					$("#redeem-dtlview").jqmData('GCD', 'true');
					$("#redeem-dtlview").jqmData('ECT', '');
				});

				$("#btn-eCertificate").click(function() {
					$("#redeem-dtlview").jqmData('GCD', '');
					$("#redeem-dtlview").jqmData('ECT', 'true');
				});
				var amount_selected_id = '#' + amtIdD;

				if (amtIdD !== 'default') {
					// console.log(" amount_selected_id " + amount_selected_id);
					$(amount_selected_id).removeClass("drkgry-wht-btn")
					.addClass("blk-wht-btn").find("span.chk").addClass(
					"chk-icon");
					$("#default").removeClass("blk-wht-btn").addClass(
					"drkgry-wht-btn").find("span.chk").removeClass(
					"chk-icon");
				}
				/*
				 * if(selAmtID === undefined){ amount_selected_id = "#"+ amtIdD;
				 * //console.log(" amount_selected_id when undefined" +
				 * amount_selected_id);
				 * $(amount_selected_id).removeClass("drkgry-wht-btn").addClass("blk-wht-btn").find("span.chk").addClass("chk-icon");
				 * $("#default").removeClass("blk-wht-btn").addClass("drkgry-wht-btn").find("span.chk").removeClass("chk-icon"); }
				 */

			}

			/**
			 * ******************* handle insufficient error landing back to
			 * setp1 *****************************
			 */

			if (dfs.crd.rwd.insuficientErrorPresentSubmit === 'true') {
				// redemption took place from some other channel. Have to update
				// the global CBB
				killDataFromCache("ACHOME");
				globalEarnRewardAmount = numberWithCommas(errAvailCBB);
				$("#redeemPartner1_CBB").html("$" + errAvailCBB);
				// console.log("error after submit" + errAvailCBB +
				// "globalEarnRewardAmount :: " + globalEarnRewardAmount);
				$("#inSuffError").show();
				$("#common-continue-btn").removeClass("common-btn");
				$("#common-continue-btn").addClass("common-btn ui-disabled");

			} else {
				$("#inSuffError").hide();
			}

			/**
			 * ************************************************CLICK CONTINUE OR
			 * TERMS********************************************
			 */

			$("#common-continue-btn")
			.click(
					function() {

						var selectedModeAmount = $('#redeem-dtlview')
						.jqmData('redeemAmountPay');
						var selectedDisbAMount = $('#redeem-dtlview')
						.jqmData('redeemAmountGet');
						var maxQuantity = $('#redeem-dtlview').jqmData(
						'redeemMaxQty');
						var selectedQuantity = $("#stepper1").val();
						var GCD = $('#redeem-dtlview').jqmData('GCD');
						var ECT = $('#redeem-dtlview').jqmData('ECT');
						var getSelectedAmtId = $("#redeem-dtlview")
						.jqmData('selectedAmtId');
						var alreadyCheckedAmount = $("#pills").find(
						"a.blk-wht-btn").attr("id");
						// console.log("getSelectedAmtId ::: " +
						// getSelectedAmtId + ":: alreadyCheckedAmount
						// :;" + alreadyCheckedAmount +
						// "::selectedQuantity::"+ selectedQuantity);

						var selectedRedemptionDetls = dfs.crd.rwd
						.createSelectedRedeemDataCache(
								selectedModeAmount,
								selectedDisbAMount,
								maxQuantity, selectedQuantity,
								GCD, ECT, pmodeCode, pmodeDesc,
								getSelectedAmtId,
								alreadyCheckedAmount);

						// //console.log("GCD ::"+ GCD + "ECT :::" +
						// ECT);
						if (GCD === 'true' && GCD.length > 0) {
							// console.log("navigate to gift card verify
							// page");
							if (billingDetails !== null
									&& billingDetails !== 'undefined'
										&& billingDetails !== '') {
								// console.log("creating billing addr");
								var billingAddr = '';
								billingAddr += billingDetails.fullName;
								billingAddr += '<br />';
								billingAddr += billingDetails.addrLine1;
								if (billingDetails.addrLine2 !== ''
									&& billingDetails.addrLine2 !== 'undefined') {
									billingAddr += billingDetails.addrLine2;
								}
								if (billingDetails.addrLine3 !== ''
									&& billingDetails.addrLine3 !== 'undefined') {
									billingAddr += billingDetails.addrLine3;
								}
								billingAddr += '<br />';
								billingAddr += billingDetails.city;
								billingAddr += ', ';
								billingAddr += billingDetails.state
								+ ' ';
								billingAddr += billingDetails.zipCode;

								// console.log("billing address string
								// == " + billingAddr);
								selectedRedemptionDetls["billAddr"] = new Object();
								selectedRedemptionDetls["billAddr"] = billingAddr;
								selectedRedemptionDetls["mediaCode"] = 'GCD';
							}
							putDataToCache("REDEEMSELCETED",
									selectedRedemptionDetls);
							navigation('../rewards/redeemPartnerGCD2');
						} else if (ECT === 'true' && ECT.length > 0) {

							// console.log("navigate to ecertificate
							// verify page");
							selectedRedemptionDetls["mediaCode"] = 'ECT';
							putDataToCache("REDEEMSELCETED",
									selectedRedemptionDetls);
							redeemPartner1SCVariables();//passing site catalyst variables for Redemption - Step 1 (All Types)
							navigation('../rewards/redeemPartnerECT2');
						}

					});

			$("#redeemPartner1_terms")
			.click(
					function() {
						// console.log("terms clicked");
						var selectedModeAmount = $('#redeem-dtlview')
						.jqmData('redeemAmountPay');
						var selectedDisbAMount = $('#redeem-dtlview')
						.jqmData('redeemAmountGet');
						var maxQuantity = $('#redeem-dtlview').jqmData(
						'redeemMaxQty');
						var selectedQuantity = $("#stepper1").val();
						var GCD = $('#redeem-dtlview').jqmData('GCD');
						var ECT = $('#redeem-dtlview').jqmData('ECT');
						var getSelectedAmtId = $("#redeem-dtlview")
						.jqmData('selectedAmtId');
						var alreadyCheckedAmount = $("#pills").find(
						"a.blk-wht-btn").attr("id");
						var seltdRedeemDetails = dfs.crd.rwd
						.createSelectedRedeemDataCache(
								selectedModeAmount,
								selectedDisbAMount,
								maxQuantity, selectedQuantity,
								GCD, ECT, pmodeCode, pmodeDesc,
								getSelectedAmtId,
								alreadyCheckedAmount);

						putDataToCache("REDEEMSELCETED",
								seltdRedeemDetails);
						navigation('../rewards/redeemMerchantTerms');

					});

		}

		dfs.crd.rwd.insuficientErrorPresentSubmit = 'false'; // after the
		// error page is
		// laoded set it
		// to false

	} catch (err) {
		showSysException(err)
	}

}

dfs.crd.rwd.createSelectedRedeemDataCache = function(selectedModeAmount,
		selectedDisbAMount, maxQuantity, selectedQuantity, GCD, ECT, pmodeCode,
		pmodeDesc, getSelectedAmtId, alreadyCheckedAmount) {
	try {

		var selectedRedemptionDetails = [];
		selectedRedemptionDetails['selectedModeAmount'] = new Object();
		selectedRedemptionDetails['selectedModeAmount'] = selectedModeAmount;
		selectedRedemptionDetails['selectedDisbAMount'] = new Object();
		selectedRedemptionDetails['selectedDisbAMount'] = selectedDisbAMount;
		selectedRedemptionDetails['selectedQuantity'] = new Object();
		selectedRedemptionDetails['selectedQuantity'] = selectedQuantity;
		selectedRedemptionDetails['isGCD'] = new Object();
		selectedRedemptionDetails['isGCD'] = GCD;
		selectedRedemptionDetails['ECT'] = new Object();
		selectedRedemptionDetails['ECT'] = ECT;
		selectedRedemptionDetails['modeCode'] = new Object();
		selectedRedemptionDetails['modeCode'] = pmodeCode;
		selectedRedemptionDetails['modeDesc'] = new Object();
		selectedRedemptionDetails['modeDesc'] = pmodeDesc;
		selectedRedemptionDetails['selectedAmountId'] = new Object();
		selectedRedemptionDetails['selectedAmountId'] = getSelectedAmtId;
		selectedRedemptionDetails['alreadyCheckedAmount'] = new Object();
		selectedRedemptionDetails['alreadyCheckedAmount'] = alreadyCheckedAmount;
		return selectedRedemptionDetails;

	} catch (err) {
		showSysException(err)
	}

}

/**
 * ********************* MERCHANT TERMS AND CONDITIONS PAGE
 * *****************************************
 */

function redeemMerchantTermsLoad() {
	try {

		// console.log("merchant terms ");
		var partnerredeemData = getDataFromCache("REDEEMPARTNERDATA");
		// console.log(" mode desc : " + partnerredeemData.modeDesc);
		// console.log(" terms : " + partnerredeemData.partnerTerms);
		if (!jQuery.isEmptyObject(partnerredeemData)) {
			$("#partnerModeDesc").html(partnerredeemData.modeDesc);
			$("#merchantTerms").html(partnerredeemData.partnerTerms);
		}

	} catch (err) {
		showSysException(err)
	}
}

/**
 * ********************* GIFT CARDS VERIFY PAGE
 * *****************************************
 */

function redeemPartnerGCD2Load() {
	try {
		// console.log("redeemPartnerGCD2Load");
		var validPriorPagesOfRenderGCD2 = new Array("redeemPartner1",
		"redeemMerchantTerms");
		if (jQuery.inArray(fromPageName, validPriorPagesOfRenderGCD2) > -1) {

			$("#redeemPartnerGCD_CBB").html("$" + globalEarnRewardAmount);
			var selectedRedemptionData = getDataFromCache("REDEEMSELCETED");
			if (!jQuery.isEmptyObject(selectedRedemptionData)) {
				var gcSelectdQty = selectedRedemptionData.selectedQuantity;
				var gcSelectdModeAmt = selectedRedemptionData.selectedModeAmount;

				if (gcSelectdQty > 1) {
					gcSelectdModeAmt = gcSelectdModeAmt * gcSelectdQty;
					$("#gcSelectdQty").html(gcSelectdQty + " - ");
					$("#gift-card-verify-partner").find(".pluralTxt")
					.removeClass("pluralTxt");
				}

				$("#redeemCurrent_CBB").html("$" + globalEarnRewardAmount);
				$("#billingAddr").html(selectedRedemptionData.billAddr);
				$("#giftCardDisbAmt").html(
						selectedRedemptionData.selectedDisbAMount);
				$("#gcmodeDesc").html(selectedRedemptionData.modeDesc);
				$("#giftCardModeAmt").html(
						"$" + (numberWithCommas(gcSelectdModeAmt)) + ".00");
				// //console.log("mode amt " +
				// selectedRedemptionData.selectedModeAmount);
				var amt1 = globalEarnRewardAmount.replace(",", "");
				var remainingCBB = "$" + ((amt1 - gcSelectdModeAmt).toFixed(2));
				$("#remaingCBB").html(numberWithCommas(remainingCBB));

				// console.log("selected GCD::::" + selectedRedemptionData.isGCD
				// + " :: ECT ::" + selectedRedemptionData.ECT);

				$("#redeemGCD2Cancel").click(function() {
					s.prop1 = 'HANDSET_REDEEM_PGC_MAIL_MAIL_CANCEL_BTN'; //camapaign code
					//navigation('../rewards/redemptionLanding');
					 partnerGiftCardseCerts();
				});

				$("#mercntTerms").click(function() {
					navigation('../rewards/redeemMerchantTerms');
				});

				$("#redeemGCD2").click(function() {
					s.prop1 = 'HANDSET_REDEEM_PGC_MAIL_MAIL_REDEEM_BTN'; //campaign code
					dfs.crd.rwd.renderGCDRedeemConfirmationPage()
				});

			}

		} else {
			// console.log("no valid route for step 2 goto landing");
			cpEvent.preventDefault();
			// history.back();
			// console.log("goto redemption landing");
			//navigation('../rewards/browseLanding');
			partnerGiftCardseCerts();
		}

	} catch (err) {
		showSysException(err)
	}

}

/**
 * ********************* ECERTS VERIFY PAGE
 * *****************************************
 */

function redeemPartnerECT2Load() {
	// console.log("redeemPartnerECT2Load");

	try {
		var validPriorPagesOfRenderECT2 = new Array("redeemPartner1",
		"redeemMerchantTerms");
		if (jQuery.inArray(fromPageName, validPriorPagesOfRenderECT2) > -1) {
			// console.log( "redeemPartnerECT2Load");
			$("#redeemPartnerECT_CBB").html("$" + globalEarnRewardAmount);
			var selectdRedemptionData = getDataFromCache("REDEEMSELCETED");
			if (!jQuery.isEmptyObject(selectdRedemptionData)) {

				var slctdModeAmt = selectdRedemptionData.selectedModeAmount;
				// console.log('slctdModeAmt' + slctdModeAmt);
				if (selectdRedemptionData.selectedQuantity > 1) {
					// console.log("quantity more than 1" +
					// selectdRedemptionData.selectedQuantity);
					$("#selectdQty").html(
							selectdRedemptionData.selectedQuantity + " - ");
					slctdModeAmt = slctdModeAmt
					* (selectdRedemptionData.selectedQuantity);
					$("#gift-card-verify-partner").find(".pluralTxt")
					.removeClass("pluralTxt");

				}
				// console.log('slctdModeAmt after' + slctdModeAmt);

				$("#redeemCurrentECT_CBB").html("$" + globalEarnRewardAmount);
				$("#ectCardDisbAmt").html(
						selectdRedemptionData.selectedDisbAMount);
				$("#ectmodeDesc").html(selectdRedemptionData.modeDesc);
				$("#ectCardModeAmt").html(
						"$" + (numberWithCommas(slctdModeAmt)) + ".00");

				var amt1 = globalEarnRewardAmount.replace(",", "");
				var remainingECTCBB = "$"
					+ ((amt1 - (slctdModeAmt)).toFixed(2));
				$("#remainingECTCBB").html(numberWithCommas(remainingECTCBB));

				$("#redeemECT2Cancel").click(function() {
					s.prop1 = 'HANDSET_REDEEM_PGC_ECERT_ECERT_CANCEL_BTN'; //campaign code
					//navigation('../rewards/redemptionLanding');
					partnerGiftCardseCerts();
				});

				$("#mercntTerms").click(function() {
					// console.log("go to terms from ect 2");
					navigation('../rewards/redeemMerchantTerms');
				});

				$("#redeemECT2").click(function() {
					s.prop1 = 'HANDSET_REDEEM_PGC_ECERT_ECERT_REDEEM_BTN'; //campaign code
					// console.log("redeem clicked");
					dfs.crd.rwd.renderECTRedeemConfirmationPage()
				});

			}
		} else {
			cpEvent.preventDefault();
			// history.back();
			// console.log("goto redemption landing");
			//navigation('../rewards/browseLanding');
			partnerGiftCardseCerts();
		}

	} catch (err) {
		showSysException(err)
	}

}

/**
 * ********************* ECERTS CONFIRMATION PAGE
 * *****************************************
 */

dfs.crd.rwd.renderECTRedeemConfirmationPage = function() {
	try {
		// console.log("renderECTRedeemConfirmationPage");
		var updatedPostDetails;
		var merchantDetailsToPost = getDataFromCache("REDEEMSELCETED");
		if (!jQuery.isEmptyObject(merchantDetailsToPost)) {
			updatedPostDetails = dfs.crd.rwd.postCBB(merchantDetailsToPost);
			// console.log("after post");
			if (!jQuery.isEmptyObject(updatedPostDetails)) {
				// console.log("updatedPostDetails no empty");
				/* DELETE ALL CACHE HERE */
				killDataFromCache("ACHOME");
				killDataFromCache("REDEEM_HISTORY");
				killDataFromCache("ALLPARTNERS");
				killDataFromCache("REDEEMPARTNERDATA");
				killDataFromCache("ECERT_REDEEM_HISTORY_DETAILS");
				killDataFromCache("CATEGORYDATA");

				if (!isEmpty(updatedPostDetails.availToRedeem)) {
					// //console.log("updated CBB after redeem ::" +
					// updatedPOSTDetails.availToRedeem);
					globalEarnRewardAmount = numberWithCommas(updatedPostDetails.availToRedeem);
				}
				killDataFromCache("REDEEMSELCETED"); // kill the selected
				// data from step1 now

				merchantDetailsToPost['updatedPostDetails'] = updatedPostDetails;
				putDataToCache("REDEEMSELCETED", merchantDetailsToPost);
				// console.log("navigate to ECT step3");
				navigation('../rewards/redeemPartnerECT3');

			}
		}

	} catch (err) {
		showSysException(err)
	}
}

function redeemPartnerECT3Load() {
	try {
		// console.log("redeemPartnerECT3Load");

		var validPriorPagesOfECT3 = new Array("redeemPartnerECT2",
				"redeemInstructions", "redeem_ecert_printphotos",
				"redeem_ecart_savetophotos_pin", "redeemMerchantSubmitTerms");
		if (jQuery.inArray(fromPageName, validPriorPagesOfECT3) > -1) {
			$("#redeemPartnerECT3_CBB").html("$" + globalEarnRewardAmount);
			var redeemedDATA = getDataFromCache("REDEEMSELCETED");
			if (!jQuery.isEmptyObject(redeemedDATA)) {

				var ECTDisbAmt = redeemedDATA.updatedPostDetails.disbAmt;
				var ECTRedeemedAmt = redeemedDATA.updatedPostDetails.redemptionAmt;
				var ECTOrderId = redeemedDATA.updatedPostDetails.orderId;
				var ECTModeDesc = redeemedDATA.updatedPostDetails.modeDescShort;
				var ECTModeCd = redeemedDATA.updatedPostDetails.modeCode;
				var ECTQty = redeemedDATA.updatedPostDetails.orderQty;
				var ECTOrderDate = redeemedDATA.updatedPostDetails.redeemDate;
				var ECTRedeemIns = redeemedDATA.updatedPostDetails.redeemInstruct;

				if (!isEmpty(ECTQty) && ECTQty > 1) {
					$("#QtyOne").hide();
				} else {
					$("#QtyTwo").hide();
				}

				if (!isEmpty(ECTDisbAmt) && ECTDisbAmt.indexOf('.') != -1) {
					ECTDisbAmt = ECTDisbAmt.split(".")[0];
				}
				if (!isEmpty(ECTRedeemedAmt)
						&& ECTRedeemedAmt.indexOf('.') != -1) {
					ECTRedeemedAmt = ECTRedeemedAmt.split(".")[0];
				}

				// var eCertDetails =
				// redeemedDATA.updatedPostDetails.eCertDetails;
				var ECTExiprationDt = redeemedDATA.updatedPostDetails.eCertExpDate;
				var ECTNumber = redeemedDATA.updatedPostDetails.eCertNumber;
				var ECTPin = redeemedDATA.updatedPostDetails.eCertPin;
				var emailBody = '';
				var emailSubject = "A $" + ECTDisbAmt
				+ " eCertificate for you!"

				var ecrtificateDetails = '';

				if (!isEmpty(ECTModeDesc)) {
					$("#ECTdesc").html(ECTModeDesc);
					if (device.platform == "Android") {
						emailBody += "Merchant Name: " + ECTModeDesc;
					} else {
						emailBody += "Merchant Name: " + ECTModeDesc + "<br>";
					}
				}

				if (!isEmpty(ECTNumber)) {
					ecrtificateDetails += "<p class='ecert-code-lbl'>eCertificate Code:</p><p class='ecert-code-num marginbottom10px' id='ECTnum'>"
						+ ECTNumber + "</p>"
						$("#ecertTouchInfo").html(
						"TIP: Touch the eCertificate number to copy it.");
					if (device.platform == "Android") {
						emailBody += "%0AeCertificate Code: " + ECTNumber;
					} else {
						emailBody += "eCertificate Code: " + ECTNumber + "<br>";
					}

				}
				if (!isEmpty(ECTPin)) {
					ecrtificateDetails += " <p class='ecert-code-lbl'>PIN:</p><p class='ecert-code-num' id='pintouch'>"
						+ ECTPin + "</p>"
						$("#ecertTouchInfo")
						.html(
						"TIP: Touch the eCertificate or the PIN number to copy it.");
					if (device.platform == "Android") {
						emailBody += "%0APIN: " + ECTPin;
					} else {
						emailBody += "PIN: " + ECTPin + "<br>";
					}
				}

				$("#ecrtificateDetails").html(ecrtificateDetails);
				$("#ectdisbAmt").html("$" + ECTDisbAmt);

				if (!isEmpty(ECTDisbAmt) && !isEmpty(ECTRedeemedAmt)) {
					$("#ECTdisbAmts").html(
							"$" + ECTDisbAmt + " eCertificate for $"
							+ ECTRedeemedAmt);
					if (device.platform == "Android") {
						emailBody += "%0AeCertificate Value: $" + ECTDisbAmt;
					} else {
						emailBody += "eCertificate Value: $" + ECTDisbAmt
						+ "<br>";
					}
				}

				if (!isEmpty(ECTOrderId)) {
					$("#ECTOrder").html("Order #: " + ECTOrderId);
					if (device.platform == "Android") {
						emailBody += "%0AOrder#: " + ECTOrderId;
					} else {
						emailBody += "Order#: " + ECTOrderId + "<br>";
					}
				}
				if (!isEmpty(ECTOrderDate)) {
					if (device.platform == "Android") {
						emailBody += "%0AOrdered: " + ECTOrderDate;
					} else {
						emailBody += "Ordered: " + ECTOrderDate + "<br>";
					}
				}

				if (!isEmpty(ECTExiprationDt)) {
					$("#ECTExpr").html("Expiration: " + ECTExiprationDt);
					if (device.platform == "Android") {
						emailBody += "%0AExpiration: " + ECTExiprationDt;
					} else {
						emailBody += "Expiration: " + ECTExiprationDt + "<br>";
					}
				}
				if (!isEmpty(ECTRedeemIns)) {
					ECTRedeemIns = ECTRedeemIns.replace(/<\/?[^>]+(>|$)/g, "");// removing
					// html
					// tags
					// from
					// EcertConfInstruction
					if (device.platform == "Android") {
						emailBody += "%0ARedemption Instructions: %0A"
							+ ECTRedeemIns;
					} else {
						emailBody += "Redemption Instructions: <br>"
							+ ECTRedeemIns;
					}
				}

				// console.log((dfs.crd.rwd.availablePartners + "" + ECTModeCd +
				// ".png"));
				$("#partnerImg")
				.attr(
						'src',
						(dfs.crd.rwd.availablePartners + "" + ECTModeCd + ".png"));

				// console.log("ECTDisbAmt::"+ECTDisbAmt+"::ECTRedeemedAmt::"+ECTRedeemedAmt+"::ECTOrderId::"+
				// ECTOrderId + "::ECTModeDesc::"+ECTModeDesc+
				// "ECTExiprationDt :: "+ ECTExiprationDt+"::ECTNumber:; "+
				// ECTNumber+"::ECTPin:: "+ ECTPin);

				$("#redeemIns").click(function() {
					navigation('../rewards/redeemInstructions');
				});

				$("#redeemInsAgn").click(function() {
					// console.log("go to ins ag");
					navigation('../rewards/redeemInstructions');
				});
				$("#viewHistory").click(function() {
					// console.log("go to history");
					navigation('../rewards/redemption_History');
				});

				$("#redeemPrtnerTerms").click(function() {
					// console.log("go to redeemPrtnerTerms");
					navigation('../rewards/redeemMerchantSubmitTerms');
				});

				$("#redeemPrtnerTNC").click(function() {
					// console.log("go to redeemPrtnerTNC");
					navigation('../rewards/redeemMerchantSubmitTerms');
				});

				if (device.platform === "Android") {
					$("#passBookEcert").hide();
				}

				$("#passErr").hide();

				if (!isEmpty(deviceVersion)) {
					var deviceVer = parseInt(deviceVersion);
					// show it ONLY for IPOD, IPHONE IOS > 6
					if (!(deviceType === "iPad") && !(deviceType === "Android")
							&& deviceVer >= 6) {
						dfs.crd.rwd.getEcertPassCountsTag(ECTModeDesc);
						var failCallback = function(e) {
							console.log("save to passbook failed with error "
									+ e)
									$("#passErr").show();
						};
						$("#passBookEcert").click(
								function() {
									dfs.crd.rwd
									.getEcertPassClickTag(ECTModeDesc);
									PassKit.prototype.firePass(failCallback,
											PASSBOOKSVCURL, "orderId",
											ECTOrderId, "modeCode", ECTModeCd,
											"orderDate", ECTOrderDate,
											"eCertNumber", ECTNumber);
								});
					} else {
						$("#passBookEcert").hide(); // Hide Add to Passbook
						// button for all others
					}
				}

				$("#saveEcert").click(function() {
					// console.log("save clicked");
					navigation('../rewards/redeem_ecart_savetophotos_pin');
				});

				if (device.platform == "Android") {
					emailBody = emailBody.replace(/#/g, "%23");
					emailBody = emailBody.replace(/&/g, "%26");
					emailBody = emailBody.replace(/;/g, "%3B");
					// console.log("emailBody :: " + emailBody);
					var mailToStr = "mailto:?subject=" + emailSubject
					+ "&body=" + emailBody;
					$("#emailEcert").attr("href", mailToStr);

				} else {
					// console.log("for IOS add native plugin call");
					$("#emailEcert").click(
							function() {
								// console.log("emailllll" + emailBody);
								// console.log("email");
								EmailComposer.prototype.showEmailComposer(
										emailSubject, emailBody, "", null,
										null, "true");

							});
				}
			}
		} else {
			cpEvent.preventDefault();
			// history.back();
			// console.log("goto redemption landing");
			//navigation('../rewards/browseLanding');
			partnerGiftCardseCerts();
		}

	} catch (err) {
		showSysException(err)
	}
}

$("#ecart-ios").live('pageshow', function() {

	if (device.platform == "Android") {
		// console.log("android clipboard call");
		$("p.ecert-code-num").click(function() {
			var pinText = $(this).html();
			// console.log("clip" + pinText);
			clipboardPlugin.prototype.setText(pinText);
		});

	} else {
		// console.log("ios clipboard call");
		$("p.ecert-code-num").click(function() {
			var pinText = $(this).html();
			// console.log("clip" + pinText);
			clipboardPlugin.prototype.setText(function(s) {
				console.log('copied to clipboard');
			}, pinText);
		});
	}
});

/**
 * ********************* PRINT ECERTS FOR CONFIRMATION PAGE
 * *****************************************
 */

function redeem_ecert_printphotosLoad() {

	try {
		// console.log("redeem_ecert_printphotosLoad");
		var printData;
		var redeemECT3Data = getDataFromCache("REDEEMSELCETED");
		var redeemECTConfDatafromHistory = getDataFromCache("ECERT_REDEEM_HISTORY_DETAILS");
		var printDisbAmt;
		if (!jQuery.isEmptyObject(redeemECT3Data)) {
			// console.log("ECT 3 data to print");
			printData = redeemECT3Data.updatedPostDetails;
			printDisbAmt = printData.disbAmt;
		} else {
			// console.log("history print data");
			printData = redeemECTConfDatafromHistory;
			printDisbAmt = printData.whatYouGetAmt;
			var ECTConfOrderDate = printData.orderDate;
			if (!isEmpty(ECTConfOrderDate)) {
				$("#pectConfOrderDt").html("Ordered: " + ECTConfOrderDate);
			}

		}
		var validPriorPagesOfPrint = new Array("redeemPartnerECT3",
		"redeemCashbackEcertConfDetails");
		if (jQuery.inArray(fromPageName, validPriorPagesOfPrint) > -1) {
			if (!jQuery.isEmptyObject(printData)) {

				var printRedeemedAmt = printData.redemptionAmt;
				var printOrderId = printData.orderId;
				var printModeDesc = printData.modeDescShort;
				var printModeCd = printData.modeCode;
				var printExiprationDt = printData.eCertExpDate;
				var printEertNumber = printData.eCertNumber;
				var printECTPin = printData.eCertPin;

				if (!isEmpty(printDisbAmt) && printDisbAmt.indexOf('.') != -1) {
					printDisbAmt = printDisbAmt.split(".")[0];
				}
				if (!isEmpty(printRedeemedAmt)
						&& printRedeemedAmt.indexOf('.') != -1) {
					printRedeemedAmt = printRedeemedAmt.split(".")[0];
				}

				if (!isEmpty(printExiprationDt)) {
					$("#pECTExpr").html("Expiration: " + printExiprationDt);
				}
				if (!isEmpty(printOrderId)) {
					$("#pECTOrder").html("Order #: " + printOrderId);
				}
				if (!isEmpty(printDisbAmt) && !isEmpty(printRedeemedAmt)) {
					$("#pECTdisbAmts").html(
							"$" + printDisbAmt + " eCertificate for $"
							+ printRedeemedAmt);
				}
				if (!isEmpty(printModeDesc)) {
					$("#pECTdesc").html(printModeDesc);
				}
				$("#pImg")
				.attr(
						'src',
						(dfs.crd.rwd.availablePartners + ""
								+ printModeCd + ".png"));

				var ecrtificateDetails = '';
				if (!isEmpty(printEertNumber)) {
					ecrtificateDetails += "<p class='ecert-code-lbl-savetophotos'>eCertificate Code:</p><p class='ecert-code-num-savetophotos' id='ECTpin'>"
						+ printEertNumber + "</p>"
				}
				if (!isEmpty(printECTPin)) {
					ecrtificateDetails += " <p class='ecert-code-lbl-savetophotos margintop5px'>PIN:</p><p class='ecert-code-num-savetophotos'>"
						+ printECTPin + "</p>"
				}

				$("#printecrtificateDetails").html(ecrtificateDetails);
				$("#pectdisbAmt").html("$" + printDisbAmt);
				$("#cancelPrint").click(function() {
					// navigation('../rewards/redeemPartnerECT3');
					history.back()
				});

				$("#printPhoto").click(function() {
					// add native handling for save plugin
					// console.log("print");
					var printDivHtmlContent = $("#printContent").html();
					// console.log(printDivHtmlContent);
					// var printDivHtmlContent1 =
					// document.getElementById("ecart-print-pin").innerHTML;
					// alert(printDivHtmlContent1);
					window.plugins.Print.print(function(s) {
						console.log("success");
					}, function(e) {
						console.log("ERROR");
					}, printDivHtmlContent, null, null);
					// //console.log("print done");

				});

			}
		} else {
			cpEvent.preventDefault();
			// history.back();
			// console.log("goto redemption landing");
			//navigation('../rewards/browseLanding');
			partnerGiftCardseCerts();
		}
	} catch (err) {
		showSysException(err)
	}

}

/**
 * ********************* SAVE TO PHOTOS FOR CONFIRMATION PAGE
 * *****************************************
 */

function redeem_ecart_savetophotos_pinLoad() {
	// console.log("redeem_ecert_savetophotos_pinLoad");
	try {

		var printData;
		var redeemECT3Data = getDataFromCache("REDEEMSELCETED");
		var redeemECTConfDatafromHistory = getDataFromCache("ECERT_REDEEM_HISTORY_DETAILS");
		var printDisbAmt;
		if (!jQuery.isEmptyObject(redeemECT3Data)) {
			// console.log("ECT 3 data to print");
			printData = redeemECT3Data.updatedPostDetails;
			printDisbAmt = printData.disbAmt;
		} else {
			// console.log("history print data");
			printData = redeemECTConfDatafromHistory;
			printDisbAmt = printData.whatYouGetAmt;
			var ECTConfOrderDate = printData.orderDate;
			if (!isEmpty(ECTConfOrderDate)) {
				$("#sectConfOrderDt").html("Ordered: " + ECTConfOrderDate);
			}
		}
		var validPriorPagesOfSave = new Array("redeemPartnerECT3",
		"redeemCashbackEcertConfDetails");
		if (jQuery.inArray(fromPageName, validPriorPagesOfSave) > -1) {
			if (!jQuery.isEmptyObject(printData)) {

				var printRedeemedAmt = printData.redemptionAmt;
				var printOrderId = printData.orderId;
				var printModeDesc = printData.modeDescShort;
				var printModeCd = printData.modeCode;
				var printExiprationDt = printData.eCertExpDate;
				var printEertNumber = printData.eCertNumber;
				var printECTPin = printData.eCertPin;

				if (!isEmpty(printDisbAmt) && printDisbAmt.indexOf('.') != -1) {
					printDisbAmt = printDisbAmt.split(".")[0];
				}
				if (!isEmpty(printRedeemedAmt)
						&& printRedeemedAmt.indexOf('.') != -1) {
					printRedeemedAmt = printRedeemedAmt.split(".")[0];
				}

				if (!isEmpty(printExiprationDt)) {
					$("#sECTExpr").html("Expiration: " + printExiprationDt);
				}
				if (!isEmpty(printOrderId)) {
					$("#sECTOrder").html("Order #: " + printOrderId);
				}
				if (!isEmpty(printDisbAmt) && !isEmpty(printRedeemedAmt)) {
					$("#sECTdisbAmts").html(
							"$" + printDisbAmt + " eCertificate for $"
							+ printRedeemedAmt);
				}
				if (!isEmpty(printModeDesc)) {
					$("#sECTdesc").html(printModeDesc);
				}
				$("#spImg")
				.attr(
						'src',
						(dfs.crd.rwd.availablePartners + ""
								+ printModeCd + ".png"));
				var ecrtificateDetails = '';
				if (!isEmpty(printEertNumber)) {
					ecrtificateDetails += "<p class='ecert-code-lbl-savetophotos'>eCertificate Code:</p><p class='ecert-code-num-savetophotos' id='ECTpin'>"
						+ printEertNumber + "</p>"
				}
				if (!isEmpty(printECTPin)) {
					ecrtificateDetails += " <p class='ecert-code-lbl-savetophotos'>PIN:</p><p class='ecert-code-num-savetophotos'>"
						+ printECTPin + "</p>"
				}

				$("#printecrtificateDetails").html(ecrtificateDetails);
				$("#sectdisbAmt").html("$" + printDisbAmt);
				$("#cancelSave").click(function() {
					// navigation('../rewards/redeemPartnerECT3');
					history.back()
				});

				$("#savePhoto")
				.click(
						function() {
							// add native handling for save plugin
							if (device.platform == "Android") {
								// console.log("android native save");
								$("#removethisdiv").hide();
								if (!$('#removethisdiv').is(':visible')) {
									// console.log("hidden");
									Screenshot.prototype.takeScreenshot(function success() {}, null);
								}
							} else {
								// console.log("IOS native save");
								$("#removethisdiv").hide();
								if (!$('#removethisdiv').is(':visible')) {
									// console.log("hidden");
									window.plugins.Screenshot
									.takeScreenshot();
								}

							}

							history.back();

						});

			}
		}
	} catch (err) {
		showSysException(err)
	}

}

/**
 * ********************* REDEMPTION INSTRUCTIONS FOR CONFIRMATION PAGE
 * *****************************************
 */

function redeemInstructionsLoad() {
	try {
		// console.log("redeemInstructionsLoad");
		var postedRedeemedData = getDataFromCache("REDEEMSELCETED");
		var ECTHistConfMerchantInstructions = getDataFromCache("ECERT_REDEEM_HISTORY_DETAILS");
		var instructionData;
		if (!jQuery.isEmptyObject(postedRedeemedData)) {
			// console.log("ECT 3 conf instructions");
			instructionData = postedRedeemedData.updatedPostDetails;
		} else {
			// console.log("history conf instructions");
			instructionData = ECTHistConfMerchantInstructions;

		}

		if (!isEmpty(instructionData)) {
			var redemptionInstruction = instructionData.redeemInstruct;
			var partnerDesc = instructionData.modeDescShort;

			if (!isEmpty(partnerDesc)) {
				$("#merchantNm").html(partnerDesc);
			}

			if (!isEmpty(redemptionInstruction)) {
				$("#redeemInstructions").html(redemptionInstruction);
			}
			// removed click to call and mobilized functionality as part of 13.1
			$("#callable")
			.html(
			"<a href='#' data-role='button' data-inline='false' data-rel='back' class='common-btn'>Done</a>");

		}

		$("#callable").trigger("create");

	} catch (err) {
		showSysException(err)
	}

}

function redeemMerchantSubmitTermsLoad() {

	try {
		// console.log("redeemInstructionsLoad");
		var postedRedeemedData = getDataFromCache("REDEEMSELCETED");
		var ECTHistConfMerchantInstructions = getDataFromCache("ECERT_REDEEM_HISTORY_DETAILS");
		var termsData;
		if (!jQuery.isEmptyObject(postedRedeemedData)) {
			// console.log("ECT 3 conf terms");
			termsData = postedRedeemedData.updatedPostDetails;
		} else {
			// console.log("history conf terms");
			termsData = ECTHistConfMerchantInstructions;

		}
		if (!isEmpty(termsData)) {
			var redeemptionTerms = termsData.partnerTerms;
			$("#partnerModeDesc").html(termsData.modeDesc);
			$("#merchantTerms").html(redeemptionTerms);

		}// merchantTerms

	} catch (err) {
		showSysException(err)
	}

}

/**
 * ********************* GIFT CARD CONFIRMATION PAGE
 * *****************************************
 */

dfs.crd.rwd.renderGCDRedeemConfirmationPage = function() {
	try {
		// console.log("renderGCDRedeemConfirmationPage");
		var updatedPOSTDetails;
		var merchantDetailsToPOST = getDataFromCache("REDEEMSELCETED");
		if (!jQuery.isEmptyObject(merchantDetailsToPOST)) {
			updatedPOSTDetails = dfs.crd.rwd.postCBB_partnerGiftCards(merchantDetailsToPOST);
			if (!jQuery.isEmptyObject(updatedPOSTDetails)) {
				killDataFromCache("ACHOME");
				killDataFromCache("REDEEM_HISTORY");
				killDataFromCache("ALLPARTNERS");
				killDataFromCache("REDEEMPARTNERDATA");
				killDataFromCache("ECERT_REDEEM_HISTORY_DETAILS");
				killDataFromCache("CATEGORYDATA");
				/* DELETE ALL CACHE HERE */
				if (!isEmpty(updatedPOSTDetails.availToRedeem)) {
					// //console.log("updated CBB after redeem ::" +
					// updatedPOSTDetails.availToRedeem);
					globalEarnRewardAmount = numberWithCommas(updatedPOSTDetails.availToRedeem);
				}
				killDataFromCache("REDEEMSELCETED"); // kill the selected
				// data from step1 now

				merchantDetailsToPOST['updatedPostDetails'] = updatedPOSTDetails;
				putDataToCache("REDEEMSELCETED", merchantDetailsToPOST);
				// console.log("navigate to step3");
				navigation('../rewards/redeemPartnerGCD3');

			}

		}
	} catch (err) {
		showSysException(err)
	}

}

function redeemPartnerGCD3Load() {
	try {
		// console.log("redeemPartnerGCD3Load");
		trafficSource = MOBILE_PARTNER_CARD;
		var validPriorPagesOfGCD3 = new Array("redeemPartnerGCD2");
		if (jQuery.inArray(fromPageName, validPriorPagesOfGCD3) > -1) {

			var redeemedData = getDataFromCache("REDEEMSELCETED");
			if (!jQuery.isEmptyObject(redeemedData)) {
				// console.log("post detials present");
				var disbAmt = redeemedData.updatedPostDetails.disbAmt;
				var redeemedAmt = redeemedData.updatedPostDetails.redemptionAmt;
				var GCQty = redeemedData.updatedPostDetails.orderQty;
				var gcdModeCode = redeemedData.updatedPostDetails.modeCode;
				if (disbAmt.indexOf('.') != -1) {
					disbAmt = disbAmt.split(".")[0];
				}
				if (redeemedAmt.indexOf('.') != -1) {
					redeemedAmt = redeemedAmt.split(".")[0];
				}

				$("#redeemGCD3CBB").html("$" + globalEarnRewardAmount);
				// console.log("GCQty :: " + GCQty);
				if (GCQty > 1) {
					// console.log("GCQty > 1:: " + GCQty);
					// redeemedAmt = redeemedAmt * GCQty;
					$("#gcdDisbAmt").html("$" + disbAmt);
					$("#gcdDisbAmt1").html(GCQty + " - " + "$" + disbAmt);
					$("#pluraltxt").html("s");
					$("#pluraltext").html("are");
				} else {
					$("#gcdDisbAmt").html("$" + disbAmt);
					$("#gcdDisbAmt1").html("$" + disbAmt);
					$("#pluraltext").html("is");
				}

				$("#gcdModeAmt").html("$" + redeemedAmt);
				if (!isEmpty(redeemedData.updatedPostDetails.modeDesc)) {
					$("#gcdModeDesc").html(
							redeemedData.updatedPostDetails.modeDesc);
				}
				if (!isEmpty(redeemedData.updatedPostDetails.orderId)) {
					$("#gcdOrderId").html(
							"Order #:"
							+ redeemedData.updatedPostDetails.orderId);
				}
				if (!isEmpty(redeemedData.updatedPostDetails.expDate)) {
					$("#gcdExpDate").html(
							"Expiration: "
							+ redeemedData.updatedPostDetails.expDate)
				}
				// console.log((dfs.crd.rwd.availablePartners + "" + gcdModeCode
				// + ".png"));
				$("#partnerImg")
				.attr(
						'src',
						(dfs.crd.rwd.availablePartners + ""
								+ gcdModeCode + ".png"));

				$("#rdRafLearnMore").click(function() {
					dfs.crd.raf.cbbReferAFriend.call();
				});

			}
		} else {
			cpEvent.preventDefault();
			// history.back();
			// console.log("goto redemption landing");
			//navigation('../rewards/browseLanding');
			partnerGiftCardseCerts();
		}

	} catch (err) {
		showSysException(err)
	}

}

dfs.crd.rwd.postCBB = function(merchantDetailsToPOST) {
	// console.log("postCBB");
	var redeemDetails;
	try {
		if (!jQuery.isEmptyObject(merchantDetailsToPOST)) {
			var newData = new Date();
			var redeemMerchantURL = RESTURL + "rewards/v3/redeem";// with Rewards V3 version, isMobilized and partnerPhone field is removed
			var amount = merchantDetailsToPOST.selectedModeAmount;
			var mediaCode = merchantDetailsToPOST.mediaCode;
			var modeCode = merchantDetailsToPOST.modeCode;
			var orderQty = merchantDetailsToPOST.selectedQuantity;
			var dataJSON = {
					"amount" : amount,
					"mediaCode" : mediaCode,
					"modeCode" : modeCode,
					"orderQty" : orderQty
			};
			var dataJSONString = JSON.stringify(dataJSON);
			var errPost;
			// console.log("dataJSONString :: " + dataJSONString);

			showSpinner();
			// var redeemDetails = getContentJson("RedeemECertificatePartner");

			$
			.ajax({
				type : "POST",
				url : redeemMerchantURL,
				async : false,
				dataType : 'json',
				data : dataJSONString,
				headers : preparePostHeader(),
				success : function(responseData, status, jqXHR) {
					// console.log("success in redeeem ");
					hideSpinner();
					if (!validateResponse(responseData,"redeemPartnerPostValidation")) // Pen Test Validation
                  {
                  errorHandler("SecurityTestFail","","");
                  return;
                  }
					if (jqXHR.status != 200 & jqXHR.status != 204) {
						var code = getResponseStatusCode(jqXHR);
						errorHandler(code, '', 'redeemPartnerECT3');
					} else {
						redeemDetails = responseData;
					}
				},
				error : function(jqXHR, textStatus, errorThrown) {
					hideSpinner();
					var code = getResponseStatusCode(jqXHR);
					cpEvent.preventDefault();
					dfs.crd.rwd.insuficientErrorPresentSubmit = 'false';
					// console.log("error in redeeem " + code);

					switch (code) {
					case "1608":
						dfs.crd.rwd.insuficientErrorPresentSubmit = 'true';
						errPost = jQuery.parseJSON(jqXHR.responseText);
						errAvailCBB = errPost.data.availBalance;
						// console.log("errAvailCBB :: " + errAvailCBB);
						// console.log("error 1608");
						navigation('../rewards/redeemPartner1');
						// console.log("navigated");
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
							errorHandler('0', '', 'redemptionLanding');
						}
						break;
					case "1656":
						// console.log("error 1656");
						cpEvent.preventDefault();
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
							errorHandler('0', '', 'redemptionLanding');
						}
						break;
					case "1613":
						var errorMessage = errorCodeMap.REDEEM_EMC_STATUS;
						var promoCodeTextData = [];
						promoCodeTextData['ACHome_CashBackBonusBalance'] = "$"
							+ globalEarnRewardAmount;
						var parseContentText = parseContent(
								errorMessage, promoCodeTextData);
						if (!isEmpty(parseContentText)) {
							errorHandler(code, parseContentText,
							'redemptionLanding');
						} else {
							errorHandler('0', '', 'redeemPartner1');
						}
						break;
					default:
						errorHandler(code, '', 'redeemPartnerECT3');
					break;
					}
				}
			})
		}
		// console.log("before return");
		return redeemDetails;

	} catch (err) {
		showSysException(err)
	}

}

/* For partner gift cards the evrsion would be V2 only. Restfull service change to make it V3 will go later. So adding a new method
 * only for partner gift cards 
 */

dfs.crd.rwd.postCBB_partnerGiftCards = function(merchantDetailsToPOST) {
	// console.log("postCBB");
	var redeemDetails;
	try {
		if (!jQuery.isEmptyObject(merchantDetailsToPOST)) {
			var newData = new Date();
			var redeemMerchantURL = RESTURL + "rewards/v2/redeem";// with Rewards V2 version,
			var amount = merchantDetailsToPOST.selectedModeAmount;
			var mediaCode = merchantDetailsToPOST.mediaCode;
			var modeCode = merchantDetailsToPOST.modeCode;
			var orderQty = merchantDetailsToPOST.selectedQuantity;
			var dataJSON = {
					"amount" : amount,
					"mediaCode" : mediaCode,
					"modeCode" : modeCode,
					"orderQty" : orderQty
			};
			var dataJSONString = JSON.stringify(dataJSON);
			var errPost;
			// console.log("dataJSONString :: " + dataJSONString);

			showSpinner();
			// var redeemDetails = getContentJson("RedeemECertificatePartner");

			$
			.ajax({
				type : "POST",
				url : redeemMerchantURL,
				async : false,
				dataType : 'json',
				data : dataJSONString,
				headers : preparePostHeader(),
				success : function(responseData, status, jqXHR) {
					// console.log("success in redeeem ");
					hideSpinner();
					if (!validateResponse(responseData,"redeemPartnerPostValidation")) // Pen Test Validation
                  {
                  errorHandler("SecurityTestFail","","");
                  return;
                  }
					if (jqXHR.status != 200 & jqXHR.status != 204) {
						var code = getResponseStatusCode(jqXHR);
						errorHandler(code, '', 'redeemPartnerECT3');
					} else {
						redeemDetails = responseData;
					}
				},
				error : function(jqXHR, textStatus, errorThrown) {
					hideSpinner();
					var code = getResponseStatusCode(jqXHR);
					cpEvent.preventDefault();
					dfs.crd.rwd.insuficientErrorPresentSubmit = 'false';
					// console.log("error in redeeem " + code);

					switch (code) {
					case "1608":
						dfs.crd.rwd.insuficientErrorPresentSubmit = 'true';
						errPost = jQuery.parseJSON(jqXHR.responseText);
						errAvailCBB = errPost.data.availBalance;
						// console.log("errAvailCBB :: " + errAvailCBB);
						// console.log("error 1608");
						navigation('../rewards/redeemPartner1');
						// console.log("navigated");
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
							errorHandler('0', '', 'redemptionLanding');
						}
						break;
					case "1656":
						// console.log("error 1656");
						cpEvent.preventDefault();
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
							errorHandler('0', '', 'redemptionLanding');
						}
						break;
					case "1613":
						var errorMessage = errorCodeMap.REDEEM_EMC_STATUS;
						var promoCodeTextData = [];
						promoCodeTextData['ACHome_CashBackBonusBalance'] = "$"
							+ globalEarnRewardAmount;
						var parseContentText = parseContent(
								errorMessage, promoCodeTextData);
						if (!isEmpty(parseContentText)) {
							errorHandler(code, parseContentText,
							'redemptionLanding');
						} else {
							errorHandler('0', '', 'redeemPartner1');
						}
						break;
					default:
						errorHandler(code, '', 'redeemPartnerGCD3');
					break;
					}
				}
			})
		}
		// console.log("before return");
		return redeemDetails;

	} catch (err) {
		showSysException(err)
	}

}

/**
 * ********************************************* BEST value partners
 * ******************************************************
 */

function redeemBestValueLoad() {

	try {
		// console.log("redeemBestValueLoad");
		killDataFromCache("REDEEMPARTNERDATA"); // kill any partner data if
		// present if user click back to
		// browse landing to select
		// another merchant
		killDataFromCache("REDEEMSELCETED");
		/** *********************************************************** */
		var bestValuePartnersData = dfs.crd.rwd.getAllBestValuePartnerDetails();
		$("#redeemBestValue_CBB").html("$" + globalEarnRewardAmount);
		if (!jQuery.isEmptyObject(bestValuePartnersData)) {

			var extra20OrMoreList = bestValuePartnersData.extra20OrMore;
			// var unavailExtraStr = "<p class='disabledTxt ui-li-desc'>You
			// don't have enough <i>Cashback Bonus</i> to redeem at this
			// merchant.Keep earning!</p>";
			var pg_extra = "fromALL";

			if (extra20OrMoreList !== null && extra20OrMoreList !== 'undefined'
				&& extra20OrMoreList !== '') {

				// console.log("20 list length" + extra20OrMoreList.length);
				var extra20ULList = "<ul class='suggestions' data-role='listview' >";
				$
				.each(
						extra20OrMoreList,
						function(extraTwenty) {
							var pModeCode = extra20OrMoreList[extraTwenty].modeCode;
							var pModeDesc = extra20OrMoreList[extraTwenty].modeDesc;
							var activeExtraLi = "";
							var extra20missingAmt = extra20OrMoreList[extraTwenty].amtAwayToRedeem;
							var extra20minRedemptionAmount = extra20OrMoreList[extraTwenty].minModeAmt;
							var extra20minWhatYouGetAmount = extra20OrMoreList[extraTwenty].minDisbAmt;
							var unavailExtraStr = "<p> You're only $"
								+ extra20missingAmt
								+ " <i>Cashback Bonus</i> away from being able to get $"
								+ extra20minRedemptionAmount
								+ " for $"
								+ extra20minWhatYouGetAmount
								+ ".</p>";

							if (extra20OrMoreList[extraTwenty].isPartnerAvail === true) { // for
								// available
								// partners
								activeExtraLi += "<li>";
								activeExtraLi += "<span class='fLeft'>";
								activeExtraLi += "<a href='#' onClick=dfs.crd.rwd.renderPartnerDetails('"
									+ pModeCode
									+ "','"
									+ pg_extra
									+ "')>";
								activeExtraLi += "<img src='"
									+ dfs.crd.rwd.availablePartners
									+ ""
									+ pModeCode
									+ ".png' width='75' height='35' data-default-image='../../images/DefaultImage_87X55.png' onerror='defaultImage(this);'/>";
								activeExtraLi += "</span>";
								activeExtraLi += " <span class='giftName'>"
									+ pModeDesc + "</span></a>";
								activeExtraLi += "<div class='cBoth'></div>";
								activeExtraLi += "</li>";
								extra20ULList += activeExtraLi;

							} else {// unavailable

								activeExtraLi += "<li class='disabled-suggestionsList'>";
								activeExtraLi += "<span class='fLeft'>";
								activeExtraLi += "<a href='#' class='ui-link'>";
								activeExtraLi += "<img src='"
									+ dfs.crd.rwd.unavailablePartners
									+ ""
									+ pModeCode
									+ ".png' width='75' height='35' data-default-image='../../images/DefaultImage_87X55.png' onerror='defaultImage(this);'/>";
								activeExtraLi += "</span>";
								activeExtraLi += " <span class='giftName'>"
									+ pModeDesc + "</span>";
								// activeExtraLi += unavailExtraStr;
								activeExtraLi += "<p class='disabledTxt'>You're only $"
									+ extra20missingAmt
									+ " <i>Cashback Bonus</i> away from being able to get $"
									+ extra20minRedemptionAmount
									+ " for $"
									+ extra20minWhatYouGetAmount
									+ ".</p>";
								activeExtraLi += "</a><div class='cBoth'></div>";
								activeExtraLi += "</li>";
								extra20ULList += activeExtraLi;

							}

						});

				// console.log("outside loop");

				extra20ULList += "</ul>";
				$("#partner-amnt20").html(extra20ULList);
				$("#partner-amnt20").trigger("create");
				dfs.crd.sct.redemptionBestValueTabTracking("Extra $20+"); //sitecatalyst
			}// extra 20

			$("#amnt20")
						.click(
								function() {
								dfs.crd.sct.redemptionBestValueTabTracking("Extra $20+"); //sitecatalyst
								});
			$("#amnt15")
			.click(
					function() {
						// console.log("extra 15 clicked");
						var extra15List = bestValuePartnersData.extra15;
						if (extra15List !== null
								&& extra15List !== 'undefined'
									&& extra15List !== '') {

							// console.log("15 list length :::::::" +
							// extra15List.length);
							var extra15ULList = "<ul class='suggestions' data-role='listview' >";
							$
							.each(
									extra15List,
									function(extra15) {
										var p15ModeCode = extra15List[extra15].modeCode;
										var p15ModeDesc = extra15List[extra15].modeDesc;
										var p15missingAmt = extra15List[extra15].amtAwayToRedeem;
										var p15minRedemptionAmount = extra15List[extra15].minModeAmt;
										var p15minWhatYouGetAmount = extra15List[extra15].minDisbAmt;

										var activeExtra15Li = "";

										if (extra15List[extra15].isPartnerAvail === true) { // for
											// available
											// partners
											activeExtra15Li += "<li>";
											activeExtra15Li += "<span class='fLeft'>";
											activeExtra15Li += "<a href='#' onClick=dfs.crd.rwd.renderPartnerDetails('"
												+ p15ModeCode
												+ "','"
												+ pg_extra
												+ "')>";
											activeExtra15Li += "<img src='"
												+ dfs.crd.rwd.availablePartners
												+ ""
												+ p15ModeCode
												+ ".png' width='75' height='35' data-default-image='../../images/DefaultImage_87X55.png' onerror='defaultImage(this);'/>";
											activeExtra15Li += "</span>";
											activeExtra15Li += " <span class='giftName'>"
												+ p15ModeDesc
												+ "</span></a>";
											activeExtra15Li += "<div class='cBoth'></div>";
											activeExtra15Li += "</li>";
											extra15ULList += activeExtra15Li;

										} else {// unavailable
											activeExtra15Li += "<li class='disabled-suggestionsList'>";
											activeExtra15Li += "<span class='fLeft'>";
											activeExtra15Li += "<a href='#' class='ui-link'>";
											activeExtra15Li += "<img src='"
												+ dfs.crd.rwd.unavailablePartners
												+ ""
												+ p15ModeCode
												+ ".png' width='75' height='35' data-default-image='../../images/DefaultImage_87X55.png' onerror='defaultImage(this);'/>";
											activeExtra15Li += "</span>";
											activeExtra15Li += " <span class='giftName'>"
												+ p15ModeDesc
												+ "</span>";
											activeExtra15Li += "<p class='disabledTxt'>You're only $"
												+ p15missingAmt
												+ " <i>Cashback Bonus</i> away from being able to get $"
												+ p15minRedemptionAmount
												+ " for $"
												+ p15minWhatYouGetAmount
												+ ".</p>";
											// /activeExtra15Li
											// += "<p
											// class='disabledTxt'>You
											// don't have enough
											// <i>Cashback
											// Bonus</i> to
											// redeem at this
											// merchant.Keep
											// earning!</p>";
											activeExtra15Li += "</a><div class='cBoth'></div>";
											activeExtra15Li += "</li>";
											extra15ULList += activeExtra15Li;

										}

									});

							// console.log("outside loop");

							extra15ULList += "</ul>";
							// console.log(" 15 waali extraULList " +
							// extra15ULList)
							$("#partner-amnt15").html(extra15ULList);
							$("#partner-amnt15").trigger("create");

						}// end extra15

					});

			$("#amnt10")
			.click(
					function() {
						// console.log("extra 10 clicked");
						var extra10List = bestValuePartnersData.extra10;

						if (extra10List !== null
								&& extra10List !== 'undefined'
									&& extra10List !== '') {

							// console.log("10 list length :::::::" +
							// extra10List.length);
							var extra10ULList = "<ul class='suggestions' data-role='listview' >";
							$
							.each(
									extra10List,
									function(extra10) {
										var p10ModeCode = extra10List[extra10].modeCode;
										var p10ModeDesc = extra10List[extra10].modeDesc;
										var p10missingAmt = extra10List[extra10].amtAwayToRedeem;
										var p10minRedemptionAmount = extra10List[extra10].minModeAmt;
										var p10minWhatYouGetAmount = extra10List[extra10].minDisbAmt;
										var activeExtra10Li = "";

										if (extra10List[extra10].isPartnerAvail === true) { // for
											// available
											// partners
											activeExtra10Li += "<li>";
											activeExtra10Li += "<span class='fLeft'>";
											activeExtra10Li += "<a href='#' onClick=dfs.crd.rwd.renderPartnerDetails('"
												+ p10ModeCode
												+ "','"
												+ pg_extra
												+ "')>";
											activeExtra10Li += "<img src='"
												+ dfs.crd.rwd.availablePartners
												+ ""
												+ p10ModeCode
												+ ".png' width='75' height='35' data-default-image='../../images/DefaultImage_87X55.png' onerror='defaultImage(this);'/>";
											activeExtra10Li += "</span>";
											activeExtra10Li += " <span class='giftName'>"
												+ p10ModeDesc
												+ "</span></a>";
											activeExtra10Li += "<div class='cBoth'></div>";
											activeExtra10Li += "</li>";
											extra10ULList += activeExtra10Li;

										} else {// unavailable
											activeExtra10Li += "<li class='disabled-suggestionsList'>";
											activeExtra10Li += "<span class='fLeft'>";
											activeExtra10Li += "<a href='#' class='ui-link'>";
											activeExtra10Li += "<img src='"
												+ dfs.crd.rwd.unavailablePartners
												+ ""
												+ p10ModeCode
												+ ".png' width='75' height='35' data-default-image='../../images/DefaultImage_87X55.png' onerror='defaultImage(this);'/>";
											activeExtra10Li += "</span>";
											activeExtra10Li += " <span class='giftName'>"
												+ p10ModeDesc
												+ "</span>";
											activeExtra10Li += "<p class='disabledTxt'>You're only $"
												+ p10missingAmt
												+ " <i>Cashback Bonus</i> away from being able to get $"
												+ p10minRedemptionAmount
												+ " for $"
												+ p10minWhatYouGetAmount
												+ ".</p>";
											// /activeExtra10Li
											// += "<p
											// class='disabledTxt'>You
											// don't have enough
											// <i>Cashback
											// Bonus</i> to
											// redeem at this
											// merchant.Keep
											// earning!</p>";
											activeExtra10Li += "</a><div class='cBoth'></div>";
											activeExtra10Li += "</li>";

											extra10ULList += activeExtra10Li;

										}

									});

							// console.log("outside loop");

							extra10ULList += "</ul>";
							$("#partner-amnt10").html(extra10ULList);
							$("#partner-amnt10").trigger("create");

						}// end extra10
						dfs.crd.sct.redemptionBestValueTabTracking("Extra $10"); // sitecatalyst
					});// if 10 clicked

			$("#amnt5")
			.click(
					function() {
						// console.log("extra 5 clicked");
						var extra5List = bestValuePartnersData.extra5;
						if (extra5List !== null
								&& extra5List !== 'undefined'
									&& extra5List !== '') {

							// console.log("5 list present");
							// console.log("5 list length :::::::" +
							// extra5List.length);
							var extra5ULList = "<ul class='suggestions' data-role='listview' >";
							$
							.each(
									extra5List,
									function(extra5) {
										var p5ModeCode = extra5List[extra5].modeCode;
										var p5ModeDesc = extra5List[extra5].modeDesc;
										var p5missingAmt = extra5List[extra5].amtAwayToRedeem;
										var p5minRedemptionAmount = extra5List[extra5].minModeAmt;
										var p5minWhatYouGetAmount = extra5List[extra5].minDisbAmt;
										var activeExtra5Li = "";

										if (extra5List[extra5].isPartnerAvail === true) { // for
											// available
											// partners
											activeExtra5Li += "<li>";
											activeExtra5Li += "<span class='fLeft'>";
											activeExtra5Li += "<a href='#' onClick=dfs.crd.rwd.renderPartnerDetails('"
												+ p5ModeCode
												+ "','"
												+ pg_extra
												+ "')>";
											activeExtra5Li += "<img src='"
												+ dfs.crd.rwd.availablePartners
												+ ""
												+ p5ModeCode
												+ ".png' width='75' height='35' data-default-image='../../images/DefaultImage_87X55.png' onerror='defaultImage(this);'/>";
											activeExtra5Li += "</span>";
											activeExtra5Li += " <span class='giftName'>"
												+ p5ModeDesc
												+ "</span></a>";
											activeExtra5Li += "<div class='cBoth'></div>";
											activeExtra5Li += "</li>";
											extra5ULList += activeExtra5Li;

										} else {// unavailable
											activeExtra5Li += "<li class='disabled-suggestionsList'>";
											activeExtra5Li += "<span class='fLeft'>";
											activeExtra5Li += "<a href='#' class='ui-link'>";
											activeExtra5Li += "<img src='"
												+ dfs.crd.rwd.unavailablePartners
												+ ""
												+ p5ModeCode
												+ ".png' width='75' height='35' data-default-image='../../images/DefaultImage_87X55.png' onerror='defaultImage(this);'/>";
											activeExtra5Li += "</span>";
											activeExtra5Li += " <span class='giftName'>"
												+ p5ModeDesc
												+ "</span>";
											activeExtra5Li += "<p class='disabledTxt'>You're only $"
												+ p5missingAmt
												+ " <i>Cashback Bonus</i> away from being able to get $"
												+ p5minRedemptionAmount
												+ " for $"
												+ p5minWhatYouGetAmount
												+ ".</p>";
											// /activeExtra5Li
											// += "<p
											// class='disabledTxt'>You
											// don't have enough
											// <i>Cashback
											// Bonus</i> to
											// redeem at this
											// merchant.Keep
											// earning!</p>";
											activeExtra5Li += "</a><div class='cBoth'></div>";
											activeExtra5Li += "</li>";

											extra5ULList += activeExtra5Li;

										}

									});

							// console.log("outside loop");

							extra5ULList += "</ul>";
							$("#partner-amnt5").html(extra5ULList);
							$("#partner-amnt5").trigger("create");

						}// end extra5

					dfs.crd.sct.redemptionBestValueTabTracking("Extra $5");//site catalyst
					});// if 5 clicked

		}// end if bestValuePartnersData

	} catch (err) {
		showSysException(err);
	}

}

dfs.crd.rwd.getAllBestValuePartnerDetails = function() {
	// console.log("getAllBestValuePartnerDetails");

	try {
		// FIRST CHECK IF ALREADY PRESENT IN CACHE OR NOT
		// var allPartersData = getContentJson("allPartners");
		// return allPartersData;
		// DO THE ACTUAL AJAX CALL HERE

		var bestValuePartnersList;
		var newData = new Date();
		var ALLBESTVALUEPARTNERSURL = RESTURL
		+ "rewards/v2/bestvalue?rewardType=ALL&" + newData + "";

		// console.log("ALLBESTVALUEPARTNERSURL ::"+ ALLBESTVALUEPARTNERSURL);
		// allPartersData = getDataFromCache("ALLPARTNERS");
		// if (jQuery.isEmptyObject(allPartersData)) {
		// console.log("doing ajax call ::");
		showSpinner();
		$.ajax({
			type : "GET",
			url : ALLBESTVALUEPARTNERSURL,
			async : false,
			dataType : 'json',
			headers : prepareGetHeader(),
			success : function(responseData, status, jqXHR) {
				hideSpinner();
				if (!validateResponse(responseData,"allBestValueValidation")){ // Pen Test Validation
					errorHandler("SecurityTestFail","","");
					return;
				}
				// console.log("jqXHR status ::"+ jqXHR.status);
				if (jqXHR.status != 200 & jqXHR.status != 204) {
					var code = getResponseStatusCode(jqXHR);
					errorHandler('0', '', 'redemptionLanding');
				} else {
					// console.log("****** success ***************** ");
					bestValuePartnersList = responseData;
					// /putDataToCache("ALLPARTNERS", allPartersData);
				}
			},

			error : function(jqXHR, textStatus, errorThrown) {
				// console.log("****** error ***************** ");
				hideSpinner();
				cpEvent.preventDefault();
				var code = getResponseStatusCode(jqXHR);
				// console.log("****** error ***************** " + code);
				switch (code) {

				case "1608":
					navigation('../rewards/redeemPartnerInsufficientError');
					break;
				case "1629":
					var errorMessage = errorCodeMap.REDEEM_BAD_ACCOUNT_STATUS;
					var promoCodeTextData = [];
					promoCodeTextData['ACHome_CashBackBonusBalance'] = "$"
						+ globalEarnRewardAmount;
					var parseContentText = parseContent(errorMessage,
							promoCodeTextData);
					if (!isEmpty(parseContentText)) {
						errorHandler(code, parseContentText,
						'redemptionLanding');
					} else {
						errorHandler('0', '', 'browseLanding');
					}
					break;
				case "1656":
					// console.log("error 1656");
					cpEvent.preventDefault();
					var errorMessage = errorCodeMap.REDEEM_BAD_ACCOUNT_STATUS;
					var promoCodeTextData = [];
					promoCodeTextData['ACHome_CashBackBonusBalance'] = "$"
						+ globalEarnRewardAmount;
					var parseContentText = parseContent(errorMessage,
							promoCodeTextData);
					if (!isEmpty(parseContentText)) {
						errorHandler(code, parseContentText,
						'redemptionLanding');
					} else {
						errorHandler('0', '', 'redemptionLanding');
					}
					break;
				default:
					errorHandler(code, '', 'browseLanding');
				break;
				}
			}

		});
		// }

		return bestValuePartnersList;

	} catch (err) {
		showSysException(err)
	}

}

/**
 * *************************** GET REDEMPTION HISTORY
 * ********************************
 */

function redemption_HistoryLoad() {
	try {
		dfs.crd.rwd.renderRedeemHistoryPage("REDEEM_HISTORY");
	} catch (err) {
		showSysException(err)
	}
}

dfs.crd.rwd.renderRedeemHistoryPage = function(pageName) {
	try {
		var redeemHistory = dfs.crd.rwd.getRedemptionHistory(pageName);
		if (!jQuery.isEmptyObject(redeemHistory)) {
			dfs.crd.rwd.populatRedeemptionHistory(redeemHistory);
		}
	} catch (err) {
		showSysException(err)
	}

}

dfs.crd.rwd.getRedemptionHistory = function(pageId) {
	try {
		var newData = new Date();
		var HISTORYURL = RESTURL + "rewards/v3/orderhistory?" + newData + "";
		var redeemHistory = getDataFromCache(pageId);
		// redeemHistory = getContentJson("orderhistory");
		if (jQuery.isEmptyObject(redeemHistory)) {

			showSpinner();
			$.ajax({
				type : "GET",
				url : HISTORYURL,
				async : false,
				dataType : 'json',
				headers : prepareGetHeader(),
				success : function(responseData, status, jqXHR) {
					hideSpinner();
					if (!validateResponse(responseData,"redeemHistoryValidation")){ // Pen Test Validation
						errorHandler("SecurityTestFail","","");
						return;
					}
					// console.log("success in getting redemption history");
					if (jqXHR.status != 200 & jqXHR.status != 204) {
						var code = getResponseStatusCode(jqXHR);
						errorHandler('0', '', 'redemption_History');
					} else {
						redeemHistory = responseData;

					}

				},
				error : function(jqXHR, textStatus, errorThrown) {

					hideSpinner();
					cpEvent.preventDefault();
					var code = getResponseStatusCode(jqXHR);
					// console.log("error in getting redemption history" +
					// code);
					errorHandler(code, '', 'redemption_History');
				}
			});
		}
		return redeemHistory;
	} catch (err) {
		showSysException(err)
	}
}

dfs.crd.rwd.populatRedeemptionHistory = function(responseData) {
	// console.log("populatRedeemptionHistory");
	try {
		killDataFromCache("ECERT_REDEEM_HISTORY_DETAILS");
		killDataFromCache("REDEEMSELCETED");
		$("#redeemFaqs").click(function() {
			// console.log("faqs clicked");
			navigation('../rewards/redeemFaqs');
		});
		if (!jQuery.isEmptyObject(responseData)) {
			var orderHistoryList = responseData.orderHistory;
			if (orderHistoryList !== null && orderHistoryList !== 'undefined'
				&& orderHistoryList !== '' && orderHistoryList.length !== 0) {
				
				var redeemHistUL = "<ul id='rd-history-list'  class='details-section-container ui-btn-corner-all '  data-theme='d' data-inset='true'>";
				// console.log("has order count : " + orderHistoryList.length);
				$
				.each(
						orderHistoryList,
						function(order) {

							var orderHistory = orderHistoryList[order];
							var localIncentiveTypeCode = '';
							var localIncentiveCode = '';
							var orderDate = orderHistory.orderDate;
							var itemDesc = orderHistory.itemDesc;
							var firstLastName = orderHistory.firstLastName;
							var fullfillMedDesc = orderHistory.mediaDesc;
							var dsbrstAmt = orderHistory.redemptionAmt;
							var dsbrstCashAmt = orderHistory.whatYouGetAmt;
							var userModeCode = orderHistory.modeCode;
							var mediaCode = orderHistory.mediaCode;
							var orderId = orderHistory.orderId;
							var redeemOptionId = orderHistory.redeemOptionId;
							var orderhistIncentiveTypeCd = orderHistory.incentiveTypeCode;
							var eCertNum;
							var dsbrstAmtDisp = orderHistory.redemptionAmt;
							var dsbrstCashAmtDisp = orderHistory.whatYouGetAmt;
							var vendorOrderId = orderHistory.vendorOrderId;
							var isModeConfigured = orderHistory.isModeConfigured;

							if (!isEmpty(dsbrstAmt)
									&& dsbrstAmt.indexOf('.') != -1) {
								dsbrstAmt = dsbrstAmt.split(".")[0];
							}
							if (!isEmpty(dsbrstCashAmt)
									&& dsbrstCashAmt.indexOf('.') != -1) {
								dsbrstCashAmt = dsbrstCashAmt
								.split(".")[0];
							}
							// console.log("isModeConfigured :: " +
							// isModeConfigured);
							if (!isEmpty(isModeConfigured)
									&& isModeConfigured === true) {// adding
								// for
								// vendors
								// like
								// Amazon
								// and
								// Facebook,
								// Itunes
								orderId = vendorOrderId;
							}

							/*
							 * $("#ect").click(function(){
							 * $("#ect").attr('class', 'ui-btn-active
							 * ui-state-persist border-left');
							 * $("#all").attr('class', 'border-right');
							 * });
							 */

							var dgcItemDesc

							switch (redeemOptionId) {

							case "1": // for partners
								eCertNum = orderHistory.eCertNumber
								break;
							case "2":// for DISCOVER GIFT CARD
								dgcItemDesc = "Discover Gift Card <br /> Name on Card : "
									+ firstLastName
									+ "<br />Design : " + itemDesc;
								itemDesc = dgcItemDesc;
								break;
							case "3": // for STATEMENT CREDIT
								if (userModeCode == "CRD1") {
									itemDesc = "Statement Credit";
									fullfillMedDesc = "Cash";
								}
								break;
							case "4":// for DIRECT DEPOSIT
								if (userModeCode == "EFT1") {
									fullfillMedDesc = "Cash";
								}
								break;
							case "5": // for CHARITY
								itemDesc = "Charitable Donation";
								break;
							case "6":// for TRAVEL JSON is not
								// avilable
								break;
							case "7":// for DFS MERCHANDISE
								break;
							case "8": // for VENDOR MERCHANDISE
								break;
							case "9": // for Amazon
								fullfillMedDesc = "Amazon.com Purchase";
								break;
							case "10":// for REVERSED AMAZON
								fullfillMedDesc = "Amazon.com Purchase";
								break;
							case "0":// for OTHER using
								// DEFAULT_HISTORY as
								// jsonByOrderType
								break;
							default:// for default using DEFAULT_HISTORY
								// as jsonByOrderType
								break;

							}
							var redeemHistLi = '';
							// console.log("mediaCode :: " + mediaCode);
							// console.log("redeemOptionId :: " +
							// redeemOptionId);
							if (!isEmpty(mediaCode)
									&& mediaCode === "ECT"
										&& redeemOptionId === "1") {

								redeemHistLi += "<li class='rd-ecert' onClick=dfs.crd.rwd.getEcetDetailsFromModeCode('"
									+ mediaCode
									+ "','"
									+ userModeCode
									+ "','"
									+ orderId
									+ "','"
									+ eCertNum
									+ "','"
									+ orderDate
									+ "','"
									+ dsbrstAmt + "')>";
								redeemHistLi += "<div class='ui-grid-c'>";
								redeemHistLi += "<div class='ui-block-a'>";
								redeemHistLi += "<p class='ecert-icon'><b> "
									+ itemDesc + " </b></p>";
								redeemHistLi += "<a href='#' data-transition='slide' class='rd-right-arrow' id='EecrtConfirm' onClick=dfs.crd.rwd.getEcetDetailsFromModeCode('"
									+ mediaCode
									+ "','"
									+ userModeCode
									+ "','"
									+ orderId
									+ "','"
									+ eCertNum
									+ "','"
									+ orderDate
									+ "','"
									+ dsbrstAmt + "')></a>";
								if (accountEarnsMiles(orderhistIncentiveTypeCd)) {
									redeemHistLi += "<p>$"
										+ numberWithCommas(dsbrstCashAmtDisp)
										+ " eCertificate for "
										+ numberWithCommas(dsbrstAmtDisp)
										+ " Miles</p>";
								} else {
									redeemHistLi += "<p>$"
										+ numberWithCommas(dsbrstCashAmtDisp)
										+ " eCertificate for $"
										+ numberWithCommas(dsbrstAmtDisp)
										+ "</p>";
								}
								redeemHistLi += "</div>";
								redeemHistLi += "<div class='ui-block-b'>";
								redeemHistLi += "<span>Order: #"
									+ orderId + "</span><p>"
									+ orderDate + "</p>";
								redeemHistLi += "</div>";
								//redeemHistLi += "<div class='ui-block-c'><div class='ecert-icon'></div></div>";
								/*redeemHistLi += " <div class='ui-block-d'>";
								redeemHistLi += "<a href='#' data-transition='slide' class='rd-right-arrow' id='EecrtConfirm' onClick=dfs.crd.rwd.getEcetDetailsFromModeCode('"
									+ mediaCode
									+ "','"
									+ userModeCode
									+ "','"
									+ orderId
									+ "','"
									+ eCertNum
									+ "','"
									+ orderDate
									+ "','"
									+ dsbrstAmt + "')></a></div>"*/
									// redeemHistLi += "<div
									// class='ui-block-d rd-right-arrow'
									// id='EecrtConfirm'
									// onClick=dfs.crd.rwd.getEcetDetailsFromModeCode('"+
									// mediaCode + "','"+ userModeCode +
									// "','"+ orderId + "','"+ eCertNum +
									// "','"+ orderDate + "')></div>"
									redeemHistLi += "</div>";
								redeemHistLi += "</li>";
								redeemHistUL += redeemHistLi;

							} else if (!isEmpty(mediaCode)
									&& mediaCode === "GCD"
										&& redeemOptionId === "1") {

								// console.log("building gift cards hist
								// L:: " );
								redeemHistLi += "<li>";
								redeemHistLi += "<div class='ui-grid-c'>";
								redeemHistLi += "<div class='ui-block-a'>";
								redeemHistLi += "<p><b> "
									+ itemDesc + " </b></p>";
								if (accountEarnsMiles(orderhistIncentiveTypeCd)) {
									redeemHistLi += "<p>$"
										+ numberWithCommas(dsbrstCashAmtDisp)
										+ " Gift Card for "
										+ numberWithCommas(dsbrstAmtDisp)
										+ " Miles</p>";
								} else {
									redeemHistLi += "<p>$"
										+ numberWithCommas(dsbrstCashAmtDisp)
										+ " Gift Card for $"
										+ numberWithCommas(dsbrstAmtDisp)
										+ "</p>";
								}
								redeemHistLi += "</div>";
								redeemHistLi += "<div class='ui-block-b'>";
								redeemHistLi += "<span>Order: #"
									+ orderId + "</span><p>"
									+ orderDate + "</p>";
								redeemHistLi += "</div>";
								/*redeemHistLi += "<div class='ui-block-c'></div>";*/
								redeemHistLi += "</div>";
								redeemHistLi += "</li>";
								redeemHistUL += redeemHistLi;
							} else {

								redeemHistLi += "<li>";
								redeemHistLi += "<div class='ui-grid-c'>";
								redeemHistLi += "<div class='ui-block-a'>";
								redeemHistLi += "<p><strong> "
									+ itemDesc + " </strong></p>";
								if (accountEarnsMiles(orderhistIncentiveTypeCd)) {
									redeemHistLi += "<p>"
										+ numberWithCommas(dsbrstAmtDisp)
										+ " Miles</p>";
								} else {
									redeemHistLi += "<p>$"
										+ numberWithCommas(dsbrstAmtDisp)
										+ "</p>";
								}

								redeemHistLi += "</div>";
								redeemHistLi += "<div class='ui-block-b'>";
								redeemHistLi += "<span>Order: #"
									+ orderId + "</span><p>"
									+ orderDate + "</p>";
								redeemHistLi += "</div>";
								/*redeemHistLi += "<div class='ui-block-c'></div>";*/
								redeemHistLi += "</div>";
								redeemHistLi += "</li>";
								redeemHistUL += redeemHistLi;
							}

						});

				redeemHistUL += "</ul>";
				$("#redeemHist").html(redeemHistUL);

			} else {
				// no order history
			}
		}
	} catch (err) {
		showSysException(err)
	}
}

dfs.crd.rwd.getEcetDetailsFromModeCode = function(mediaCode, userModeCode,
		orderId, eCertNo, date, redeemAmt) {
	// console.log("getEcetDetailsFromModeCode");
	var eCertData = dfs.crd.rwd.getEcertPartnerRedeemHistory(mediaCode,
			userModeCode, orderId, eCertNo, date, redeemAmt);
	if (!jQuery.isEmptyObject(eCertData)) {
		navigation('../rewards/redeemCashbackEcertConfDetails');
	}
}

dfs.crd.rwd.getEcertPartnerRedeemHistory = function(mediaCode, userModeCode,
		orderId, eCertNo, date, redeemAmt) {
	// console.log("getEcertPartnerRedeemHistory redeem amount == " +
	// redeemAmt);
	try {
		var ecertPartnerConfDetails;
		var newData = new Date();
		// with Rewards V3 version, isMobilized and partnerPhone field is removed
		var ECERTPARTNERSCONFURL = RESTURL
		+ "rewards/v3/orderhistory?mediaCode=" + mediaCode
		+ "&modeCode=" + userModeCode + "&eCertNumber=" + eCertNo
		+ "&orderId=" + orderId + "&orderDate=" + date + "&" + newData;
		// console.log("ECERTPARTNERSCONFURL :: " + ECERTPARTNERSCONFURL);
		showSpinner();
		$.ajax({
			type : "GET",
			url : ECERTPARTNERSCONFURL,
			async : false,
			dataType : 'json',
			headers : prepareGetHeader(),
			success : function(responseData, status, jqXHR) {
				hideSpinner();
				if (!validateResponse(responseData,"redeemHistoryEconfValidation")){ // Pen Test Validation
               errorHandler("SecurityTestFail","","");
               return;
               }
				if (jqXHR.status != 200 & jqXHR.status != 204) {
					var code = getResponseStatusCode(jqXHR);
					errorHandler(code, '');
				} else {
					// console.log("got the details of ecrt confirmation");
					ecertPartnerConfDetails = responseData;
					ecertPartnerConfDetails['redemptionAmt'] = redeemAmt;
					putDataToCache("ECERT_REDEEM_HISTORY_DETAILS",
							ecertPartnerConfDetails);
				}
			},
			error : function(jqXHR, textStatus, errorThrown) {
				// console.log("ERROR got the details of ecrt confirmatio");
				hideSpinner();
				var code = getResponseStatusCode(jqXHR);
				errorHandler(code, '', 'redemptionHistory');
			}
		});
		return ecertPartnerConfDetails;
	} catch (err) {
		showSysException(err)
	}
}

function redeemCashbackEcertConfDetailsLoad() {
	try {
		var redeemECTConfData = getDataFromCache("ECERT_REDEEM_HISTORY_DETAILS");
		if (!jQuery.isEmptyObject(redeemECTConfData)) {

			// THIS LOGIC WILL CHANGE FROM LIST

			var ECTConfDisbAmt = redeemECTConfData.whatYouGetAmt;
			var ECTConfRedeemedAmt = redeemECTConfData.redemptionAmt;
			var ECTConfOrderId = redeemECTConfData.orderId;
			var ECTConfModeDesc = redeemECTConfData.modeDescShort;
			var ECTConfModeCd = redeemECTConfData.modeCode;
			var ECTConfQty = redeemECTConfData.orderQty;
			// console.log("ECTConfDisbAmt :: "+ ECTConfDisbAmt + "::
			// ECTConfRedeemedAmt ::"+ ECTConfRedeemedAmt);
			if (!isEmpty(ECTConfDisbAmt) && ECTConfDisbAmt.indexOf('.') != -1) {
				ECTConfDisbAmt = ECTConfDisbAmt.split(".")[0];
			}
			if (!isEmpty(ECTConfRedeemedAmt)
					&& ECTConfRedeemedAmt.indexOf('.') != -1) {
				ECTConfRedeemedAmt = ECTConfRedeemedAmt.split(".")[0];
			}

			var ECTConfExiprationDt = redeemECTConfData.eCertExpDate;
			var ECTConfNumber = redeemECTConfData.eCertNumber;
			var ECTConfPin = redeemECTConfData.eCertPin;
			var ECTConfOrderDate = redeemECTConfData.orderDate;
			var EcertConfInstruction = redeemECTConfData.redeemInstruct;

			var emailBody = '';
			var emailSubject = "A $" + ECTConfDisbAmt
			+ " eCertificate for you!"

			if (!isEmpty(ECTConfModeDesc)) {
				if (device.platform == "Android") {
					emailBody += "Merchant Name: " + ECTConfModeDesc;
				} else {
					emailBody += "Merchant Name: " + ECTConfModeDesc + "<br>";
				}
				$("#ECTConfdesc").html(ECTConfModeDesc);
			}

			$("#partnerImg")
			.attr(
					'src',
					(dfs.crd.rwd.availablePartners + "" + ECTConfModeCd + ".png"));

			var ecrtificateConfDetails = '';
			if (!isEmpty(ECTConfNumber)) {
				ecrtificateConfDetails += "<p class='ecert-code-lbl'>eCertificate Code:</p><p class='ecert-code-num' id='ECTpin'>"
					+ ECTConfNumber + "</p>"

					if (device.platform == "Android") {
						emailBody += "%0AeCertificate Code: " + ECTConfNumber;
					} else {
						emailBody += "eCertificate Code: " + ECTConfNumber + "<br>";
					}
				$("#ecertTouchInfo").html(
				"TIP: Touch the eCertificate number to copy it.");

			}
			if (!isEmpty(ECTConfPin)) {
				ecrtificateConfDetails += " <p class='ecert-code-lbl margintop5px'>PIN:</p><p class='ecert-code-num'>"
					+ ECTConfPin + "</p>"
					if (device.platform == "Android") {
						emailBody += "%0APIN: " + ECTConfPin;
					} else {
						emailBody += "PIN: " + ECTConfPin + "<br>";
					}

				$("#ecertTouchInfo")
				.html(
				"TIP: Touch the eCertificate or the PIN number to copy it.");
			}
			if (!isEmpty(ECTConfDisbAmt) && !isEmpty(ECTConfRedeemedAmt)) {
				$("#ECTConfdisbAmts").html(
						"$" + ECTConfDisbAmt + " eCertificate for $"
						+ ECTConfRedeemedAmt);
				if (device.platform == "Android") {
					emailBody += "%0AeCertificate Value: $" + ECTConfDisbAmt;
				} else {
					emailBody += "eCertificate Value: $" + ECTConfDisbAmt
					+ "<br>";
				}
			}

			if (!isEmpty(ECTConfOrderId)) {
				$("#ectConfOrderId").html("Order #: " + ECTConfOrderId);
				if (device.platform == "Android") {
					emailBody += "%0AOrder#: " + ECTConfOrderId;
				} else {
					emailBody += "Order#: " + ECTConfOrderId + "<br>";
				}
			}

			if (!isEmpty(ECTConfOrderDate)) {
				$("#ectConfOrderDt").html("Ordered: " + ECTConfOrderDate);
				if (device.platform == "Android") {
					emailBody += "%0AOrdered: " + ECTConfOrderDate;
				} else {
					emailBody += "Ordered: " + ECTConfOrderDate + "<br>";
				}
			}

			if (!isEmpty(ECTConfExiprationDt)) {
				$("#ectConfExpDt").html("Expiration: " + ECTConfExiprationDt);
				if (device.platform == "Android") {
					emailBody += "%0AExpiration: " + ECTConfExiprationDt;
				} else {
					emailBody += "Expiration: " + ECTConfExiprationDt + "<br>";
				}
			}

			if (!isEmpty(EcertConfInstruction)) {
				EcertConfInstruction = EcertConfInstruction.replace(
						/<\/?[^>]+(>|$)/g, "");// removing html tags from
				// EcertConfInstruction
				// EcertConfInstruction = EcertConfInstruction.replace(/#/g,"");
				// EcertConfInstruction = EcertConfInstruction.replace(/&/g,"");
				// EcertConfInstruction = EcertConfInstruction.replace(/;/g,"");
				if (device.platform == "Android") {
					emailBody += "%0ARedemption Instructions:%0A"
						+ EcertConfInstruction;
				} else {
					emailBody += "Redemption Instructions: <br>"
						+ EcertConfInstruction;
				}

			}

			// console.log("emailBody :: " + emailBody);
			// console.log("emailSubject :: " + emailSubject);

			$("#ecrtificateConfDetails").html(ecrtificateConfDetails);
			$("#ectConfdisbAmt").html("$" + ECTConfDisbAmt);

			$("#redeemConfIns").click(function() {
				navigation('../rewards/redeemInstructions');
			});

			$("#redeemConfTerms").click(function() {
				navigation('../rewards/redeemMerchantSubmitTerms');
			});

			if (device.platform == "Android") {
				$("#passBookEcert").hide();
			}

			$("#passErr").hide();

			if (!isEmpty(deviceVersion)) {
				var deviceVer = parseInt(deviceVersion);
				// show it ONLY for IPOD, IPHONE IOS > 6
				if (!(deviceType === "iPad") && !(deviceType === "Android")
						&& deviceVer >= 6) {
					dfs.crd.rwd.getEcertPassCountsTag(ECTConfModeDesc);
					var failCallback = function(e) {
						console.log("save to passbook failed with error " + e);
						$("#passErr").show();
					};
					$("#passBookEcert").click(
							function() {
								dfs.crd.rwd
								.getEcertPassClickTag(ECTConfModeDesc);
								PassKit.prototype.firePass(failCallback,
										PASSBOOKSVCURL, "orderId",
										ECTConfOrderId, "modeCode",
										ECTConfModeCd, "orderDate",
										ECTConfOrderDate, "eCertNumber",
										ECTConfNumber);
							});
				} else {
					$("#passBookEcert").hide();
				}
			}

			$("#saveEcert").click(function() {
				// console.log("save clicked");
				navigation('../rewards/redeem_ecart_savetophotos_pin');
			});

			if (device.platform == "Android") {

				emailBody = emailBody.replace(/#/g, "%23");
				emailBody = emailBody.replace(/&/g, "%26");
				emailBody = emailBody.replace(/;/g, "%3B");
				// console.log("emailBody :: " + emailBody);
				var mailToStr = "mailto:?subject=" + emailSubject + "&body="
				+ emailBody;
				$("#emailEcert").attr("href", mailToStr);
			} else {
				// console.log("for IOS add native plugi call");
				$("#emailEcert").click(
						function() {
							// add native handling for email composer plugin
							// console.log("email");
							EmailComposer.prototype.showEmailComposer(
									emailSubject, emailBody, "", null, null,
							"true");
						});
			}

		}

	} catch (err) {
		showSysException(err)
	}
}

$("#rd-history-ios").live('pageshow', function() {
	if (device.platform == "Android") {
		// console.log("android clipboard call");
		$("p.ecert-code-num").click(function() {
			var pinText = $(this).html();
			// console.log("clip" + pinText);
			clipboardPlugin.prototype.setText(pinText);
		});

	} else {
		// console.log("ios clipboard call");
		$("p.ecert-code-num").click(function() {
			var pinText = $(this).html();
			// console.log("clip" + pinText);
			clipboardPlugin.prototype.setText(function(s) {
				console.log('copied to clipboard');
			}, pinText);

		});
	}
});

/** ************************ error page loads ********************** */
function redeemDGCInsufficientErrorLoad() {
	try {
		$("#err_CBB_GC").html("$" + globalEarnRewardAmount);
		$("#learnMore").click(function() {
			navigation('../rewards/cashbackBonusSignup1');
		});

	} catch (err) {
		showSysException(err)
	}

}
function redeemPartnerInsufficientErrorLoad() {
	try {
		$("#err_CBB").html("$" + globalEarnRewardAmount);
		$("#learnMore").click(function() {
			navigation('../rewards/cashbackBonusSignup1');
		});

	} catch (err) {
		showSysException(err)
	}

}

/**
 * ************* Adding the below method to implement site catalyst requirements
 * for Passbook ****************
 */

dfs.crd.rwd.getEcertPassClickTag = function(merchantName) {
	s.linkTrackVars = s.linkTrackVars + 'products,events,eVar58';
	s.products = "CashbackBonus;CBB:" + merchantName + ";"
	s.events = 'event69';
	s.linkTrackEvents = 'event69';
	s.eVar58 = "DiscoverCard:Passbook";
	s.tl(this, 'e', 'Mobile Add to Passbook');
}

dfs.crd.rwd.getEcertPassCountsTag = function(merchantName) {
	s.products = "CashbackBonus;CBB:" + merchantName + ";"
	s.events = "event68";
	s.eVar58 = "DiscoverCard:Passbook";

}
