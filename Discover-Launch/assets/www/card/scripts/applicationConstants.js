/*
 * This file contains application level constants for URLs used within application.
 */
var APPVER;
var BANK_URL;
var BANK_LUA_URL;
var BANK_REG_URL;
var BANK_SSO_URL;
var BANK_SSO_SSN_NOT_MATCHED_URL;
var BASEURL;
var RESTURL;
var HREF_URL;
var s_account;
if (navigator.userAgent.match(/iPhone/))
 {
  var shareURL = "https://secure.opinionlab.com/ccc01/o.asp?id=yuNPawGE";
 }
else {
  var shareURL = "https://secure.opinionlab.com/ccc01/o.asp?id=OJMQyJQI";  
 }

/**
 *  Environment function controls app version and environment variables
 */
env = (function () {
	/**
	 * Load the environment specific variables fron environment config file.
	 */
	load = function(envFile) {
		var xmlhttp=new XMLHttpRequest();
		xmlhttp.open("GET",envFile,false);
		xmlhttp.send();
		xmlDoc=xmlhttp.responseXML; 
		APPVER = xmlDoc.getElementsByTagName("appvers")[0].childNodes[0].nodeValue;
		var cardNode = xmlDoc.getElementsByTagName("cardbaseurl")[0].childNodes[0];
		if(cardNode != null){
			BASEURL = cardNode.nodeValue;
		}else{
			BASEURL = "";
		}
		RESTURL = BASEURL + "/cardsvcs/acs/";
		HREF_URL = xmlDoc.getElementsByTagName("cardhrefurl")[0].childNodes[0].nodeValue;
		BANK_URL =  xmlDoc.getElementsByTagName("bankbaseurl")[0].childNodes[0].nodeValue + "/m/accounts/login?view=Touch/Handset&DiscoverMobileVersion="+APPVER;
		BANK_LUA_URL = xmlDoc.getElementsByTagName("bankbaseurl")[0].childNodes[0].nodeValue + "/m/accounts/login?cardstat=LUA&view=Touch/Handset&DiscoverMobileVersion="+APPVER;
		BANK_REG_URL = xmlDoc.getElementsByTagName("bankbaseurl")[0].childNodes[0].nodeValue + "/bankac/loginreg/regone?view=Touch/Handset&DiscoverMobileVersion="+APPVER;
		BANK_SSO_URL = xmlDoc.getElementsByTagName("bankbaseurl")[0].childNodes[0].nodeValue + "/m/accounts/bankdeeplink?view=Touch/Handset&DiscoverMobileVersion="+APPVER+"&payload=";
		BANK_SSO_SSN_NOT_MATCHED_URL = xmlDoc.getElementsByTagName("bankbaseurl")[0].childNodes[0].nodeValue + "/m/accounts/errors/contact-customer-service-sso?view=Touch/Handset&DiscoverMobileVersion="+APPVER;
		var array1 = HREF_URL.split("//");
		var array2 = array1[1].split(".");
		if (array2[0]==="m" || array2[0]==="www")
			s_account="discoverglobalprod,discovercardmobileprod";
		else 
			s_account="discoverglobaldev,discovercardmobiledev";
	};//==>load	
	//public functions
	return {
		load: load
	};//==> return
})();//==> env

//From rewards.js file
var  CBB_INCENTIVE_TYPE = "CBB";
var  SBC_INCENTIVE_TYPE = "SBC";
var  MILES_INCENTIVE_TYPE = "MI2";
var  BUS_MILES_INCENTIVE_TYPE = "SML";

// Send Money
var phoneRegex = /^[0-9]{10}$/;
var regExp = /^[a-zA-Z]$/;
var emailFormat = /^([A-Za-z0-9_\-\.])+\@([A-Za-z0-9_\-\.])+\.([A-Za-z]{2,4})$/;
var regExpressionName=/^[a-zA-Z0-9- ]+$/;

//StrongMail status api url
var STRONG_MAIL_STATUS_API_URL = "https://www.popularmedia.net/status_page.js?";
//Refer A Friend Traffic Source codes
var MOBILE_SPLASH_NAV = 29;
var MOBILE_GIFT_CARD = 30;
var MOBILE_PARTNER_CARD = 31;
//common ajax request header
//var ajaxRequestHeaderContentTypeAsUrlEncoded={'X-Application-Version':APPVER,'X-Client-Platform':'Android','Content-Type':'application/x-www-form-urlencoded'};
//var ajaxRequestHeaderContentTypeAsJSON={'X-Application-Version':APPVER,'X-Client-Platform':'Android','Content-Type':'application/json'};
//var ajaxRequestHeaderWithAuthorization={'X-Application-Version':APPVER,'X-Client-Platform':'Android','Content-Type':'application/json','Authorization':'AuthorizationString','X-DID':'DeviceId','X-SID':'SimID','X-OID':'OtherID'};
//var ajaxPostRequestHeaderContentTypeAsJSON={'X-Application-Version':APPVER,'X-Client-Platform':'Android','Content-Type':'application/json','X-SEC-Token':'TokenValue'};

var pageMenuHglt={'cardHome':'HM','accountSummary':'AC','accountLanding':'AC','accountActivity':'AC',
		'searchTrans':'AC','statements':'AC','accountCreditLineAvail':'AC',
		'statementLanding':'AC','miles-signup_1':'MI',
		'miles-signup_2':'MI','paymentsLanding':'PY','cashbackSignup1':'CB',
		'cashbackSignup2':'CB','cashbackSignup1':'CB','moreLanding':'MR',
		'bank_account_for_direct_deposit':'CB','cashbackBonusLanding':'CB','cashbackBonusSignup1':'CB',
		'cashbackBonusSignup2':'CB','cashbackBonusSignup3':'CB','cashbackBonusSignupDetails':'CB','directDeposit1':'CB',
		'directDeposit2':'CB','directDeposit3':'CB','directDepositCancelTrans':'CB','customerServiceUpdateAccount':'CB',
		'directDepositConfirmCancelTrans':'CB','milesSignup1':'MI',
		'milesSignup2':'MI','milesSignup3':'MI','milesHome':'MI','milesRedeem':'MI',
		'redeemCashbackEcert1':'CB','redeemCashbackEcert2':'CB','redeemCashbackEcert3':'CB',
		'redeemCashbackEcert4':'CB','redeemCashbackEcertDetails':'CB',
		'redemptionHistory':'CB','statementCredit1':'CB','statementCredit2':'CB',
		'statementCredit3':'CB','statementCreditCancelTrans':'CB',
		'statementCreditConfirmCancelTrans':'CB','redeemLanding':'CB','creditLineLearnMore':'AC','latePayWarn':'AC',
		'applyDiscover':'ED|MR','benefits':'ED|MR','community':'ED|MR','exploreDiscoverLanding':'ED|MR',
		'myBenefits':'ED|MR','privacyPolicy':'ED|MR','rewards':'MR','security':'ED|MR','termsUse':'ED|MR',
		'customerServiceLanding':'CS|MR','contactUs':'CS|MR','customerServiceFaqs':'CS|MR','customerServiceFaqsOptimized':'CS|MR',
		'rewardsSeeDetails':'MR', 'sendMoney1':'MM|MR', 'sendMoney2':'MM|MR','sendMoney3':'MM|MR','sendMoneyFAQ':'MM|MR',
		'sendMoneyLanding':'MM|MR','termsConditions':'MM|MR','transactionHistory':'MM|MR','updateEmail':'MM|MR',
		'validateSendMoneyTransactions':'MM|MR','cancelTransaction':'MM|MR','cancelTransactionHistory':'MM|MR',
		'confirmCancelTransaction':'MM|MR','noTransactionHistory':'MM|MR','howtoWorkUpdateEmail':'MM|MR',
		'howItWorks':'MM|MR','personalizeCashPin1':'MR','personalizeCashPin2':'MR',
		'personalizeCashPinError':'MR','profileEnroll':'MR','profileLanding':'MR','profileManageContact':'MR',
		'manageBankInformation':'PY','paymentStep1':'PY','paymentStep2':'PY','paymentStep3':'PY','paymentInformation':'PY',
		'paymentsHistory':'PY','paymentsLanding':'PY','paymentsSummary':'PY','pendingPayments':'PY','cancelPayment':'PY',
		'confirmCancelPayment':'PY','bankAccountForDirectDeposit':'CB','referAFriendShareErrorCBB':'CB','referAFriendStatusParmsErrorCBB':'CB',
		'referAFriendStatusErrorCBB':'CB','strongMailScriptErrorCBB':'CB','referAFriendTermsErrorCBB':'CB','referAFriendTermsErrorMI2':'MI',
		'strongMailScriptErrorMI2':'MI','referAFriendStatusErrorMI2':'MI','referAFriendStatusParmsErrorMI2':'MI','referAFriendShareErrorMI2':'MI',
		'redemptionLanding':'CB','browse_all_ecert':'CB','browse_all_gcard':'CB','browseAllPartners':'CB','browseCategory':'CB',
		'browseLanding':'CB','giftcard1':'CB','giftcard2':'CB','giftcard3':'CB','giftcardPurchaseAgrmnt':'CB',
		'giftcardselectdesign':'CB','giftcardTerms':'CB','IneligibleError_discoverGiftCards':'CB','redeem_ecart_savetophotos_pin':'CB','redeem_ecert_printphotos':'CB',
		'redeem_pay_with_cbb':'CB','redeemBestValue':'CB','redeemCashbackEcertConfDetails':'CB','redeemDGCInsufficientError':'CB','redeemFaqs':'CB|MI',
		'redeemInstructions':'CB','redeemMerchantTerms':'CB','redeemPartner1':'CB','redeemPartnerECT2':'CB','redeemPartnerECT3':'CB',
		'redeemPartnerGCD2':'CB','redeemPartnerGCD3':'CB','redeemPartnerInsufficientError':'CB','redemption_History':'CB','referAFriendDollar':'CB',
		'referAFriendMiles':'MI','referralStatusDollarSuccess':'CB','referralStatusMilesSuccess':'MI','referralStatusNoReferrals':'CB|MI','rafShareWithEmail':'CB|MI','rafPreviewEmailMessageAdded':'CB|MI',
		'rafNoEmailError':'CB|MI', 'alertHistory':'MR','faqDeviceAlerts':'MR','manageAlerts':'MR','manageAlertsOverride':'MR','manageAlertsTermsAndConditions':'MR','manageAlertsError':'MR',
		'edoDetail':'CB|MI','edoLandingPage':'CB|MI','edoHistory':'CB|MI', 'privacyPolicyMore':'MR','termsUseMore':'MR' ,'viewMap':'CB|MI','edoFaqs':'CB|MI'};

var moduleSpecTimeOut={'abcd':'600000'};  
var noTransitionPages=["redeemCashbackEcert1", "accountActivity","browseAllPartners","browse_all_ecert","browse_all_gcard","redeemBestValue","browseCategory",
"rafNoEmailError","rafPreviewEmailMessageAdded","rafShareWithEmail","referAFriendDollar","referAFriendMiles","referralStatusDollarSuccess",
"referralStatusMilesSuccess","referralStatusNoReferrals","referralTermsAndConditions","browseLanding","giftcard1","redemption_History",
"redeemCashbackEcertConfDetails","redeemFaqs","redeemPartnerInsufficientError","redeemPartner1","redeemDGCInsufficientError","giftcardselectdesign",
"giftcardPurchaseAgrmnt","IneligibleError_discoverGiftCards","manageAlerts","manageAlertsOverride","redeem_ecart_savetophotos_pin","redeem_ecert_printphotos"];
