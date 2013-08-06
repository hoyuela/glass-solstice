function accountSummary()
{
	preventBack = false;	
	populateGlobalCache();
	isLhnNavigation  = true;
	navigation('../achome/accountSummary');   
}

function recentActivity()
{
	preventBack = false;
	navigation('../statements/accountActivity');	
}

function searchTransactions()
{
	preventBack = false;
	navigation('../statements/searchTrans');	
}

function statements()
{	
	preventBack = false;
	var statements = dfs.crd.stmt.shared.util.getStatements(); 
	if ( statements.isEmpty() ) {
			navigation('../achome/accountLanding');
	}else{
			navigation('../statements/statementLanding');
	}
	
}

function handleNativeFrame(activePage)
{	 
	if(activePage!="login-pg" && activePage!="loadingPage-pg" && activePage!="dummy-pg"  && activePage!="cardHome-pg") 
	{	 
		HybridControl.prototype.popPhoneGapToFront(null, pageTitle[activePage]);  
	}
}

function dismissProgressBar()
{
	HybridControl.prototype.dismissProgressBar();
}

function makeaPayment()
{
	preventBack = false;
	populateGlobalCache();
	isLhnNavigation  = true;
	navigation('../payments/paymentStep1');
}

function managePayments()
{
	preventBack = false;
	populateGlobalCache();
	navigation('../payments/paymentsSummary');
}
/* 13.4 change start */
/*function manageBankInformation()
{
preventBack = false;
populateGlobalCache();
	navigation('../payments/manageBankInformation');
}*/

function manageBankAccounts()
{
	preventBack = false;
	populateGlobalCache();
	navigation('../payments/manageBankAccounts');
}

/* 13.4 change end */
function sendMoney()
{	
	preventBack = false;
	populateGlobalCache();
	isLhnNavigation  = true;
	navigation('../p2p/sendMoney1');
}

function sendMoneyHistory()
{
	preventBack = false;
	populateGlobalCache();
	navigation('../p2p/transactionHistory');
}

function cashbackBonusPromos()
{
	preventBack = false;
	populateGlobalCache();
	s.prop1='HANDSET_CBB_SIGNUP_BTN';
	navigation('../rewards/cashbackBonusSignup1');
}

function extras()
{
	preventBack = false;
	populateGlobalCache();
	navigation('../edo/edoLandingPage');
}

function referaFriend()
{
	preventBack = false;
	populateGlobalCache();
	s.prop1='HANDSET_CBB_RAF_BTN';
	if(accountEarnsCBB(incentiveTypeCode)){	
	  dfs.crd.raf.navFromCBBLanding();
	}
	else{ 
	 dfs.crd.raf.navFromMilesLanding();
	}
}

function partnerGiftCardseCerts()
{
	preventBack = false;
	populateGlobalCache();
    dfs.crd.rwd.getAllPartners();
	if(!rewardErrorFlag){ // Fix for defect 96754
	s.prop1 = 'HANDSET_REDEEM_PGC_BTN'; // campaign code	
	var insuffErr = dfs.crd.rwd.insuficientErrorPresent;
   if (!isEmpty(insuffErr) && insuffErr === 'true') {
    navigation('../rewards/redeemPartnerInsufficientError');
	errorPartnerGiftCardSCVariables();
   } else if(!errorFlag) {  
    navigation('../rewards/browseLanding');
   }
   rewardErrorFlag = false;
   }
}

function discoverGiftCard()
{	
	preventBack = false;
	populateGlobalCache();
	dfs.crd.rwd.getAllPartners();
	if(!rewardErrorFlag){ // Fix for defect 96754
	s.prop1 = 'HANDSET_REDEEM_DGC_BTN'; // campaign code
	var insuffErr = dfs.crd.rwd.insuficientErrorPresent;
   	if (!isEmpty(insuffErr) && insuffErr === 'true') {
    navigation('../rewards/redeemDGCInsufficientError');
	errorDiscoverGiftCardSCVariables();
   } else if(!errorFlag){
    navigation('../rewards/giftcard1');
   }
   rewardErrorFlag = false;
   }
}

function statementCredit()
{
	preventBack = false;
	populateGlobalCache();
	dfs.crd.rwd.getAllPartners();
	if(!rewardErrorFlag){ // Fix for defect 96754
	s.prop1 = 'HANDSET_REDEEM_STATE_CREDIT_BTN'; // campaign code
	isLhnNavigation  = true;
	navigation('../rewards/statementCredit1');	
	}
	rewardErrorFlag = false;
}

function directDeposit()
{
	preventBack = false;
	populateGlobalCache();
	dfs.crd.rwd.getAllPartners();
	if(!rewardErrorFlag){ // Fix for defect 96754
	s.prop1 = 'HANDSET_REDEEM_DIRECT_DEP_BTN'; // campaign code
	isLhnNavigation  = true;
	navigation('../rewards/directDeposit1');
	}
	rewardErrorFlag = false;
}

function paywithCashbackBonus()
{
	preventBack = false;
	populateGlobalCache();
	dfs.crd.rwd.getAllPartners();
	if(!rewardErrorFlag){ // Fix for defect 96754
	s.prop1 = 'HANDSET_REDEEM_PAY_CBB_BTN'; // campaign code
	//navigation('../rewards/redeemCashbackEcert1');
	navigation('../rewards/redeem_pay_with_cbb');
	}
	rewardErrorFlag = false;
}

function redemptionHistory()
{
	preventBack = false;
	populateGlobalCache();
	s.prop1 = 'HANDSET_REDEEM_HIST_BTN'; // campaign code
	navigation('../rewards/redemption_History');
}

function manageTextAlerts()
{
	preventBack = false;
	navigation('../pushNotification/manageAlertsOverride');
}

function pushAlertsHistory()
{
	populateGlobalCache();	
	preventBack = false;
	navigation('../pushNotification/alertHistory');
}

function createCashPIN()
{
	preventBack = false;
	isLhnNavigation  = true;
	navigation('../profile/personalizeCashPin1');
}

function contactUs()
{
	preventBack = false;
	//13.3 changes-start
	populateGlobalCache();
	if(cardProductGroupCode == "CRP"){
		navigation('../custormerService/contactUsLoggedOut');
	}else{
		 navigation('../custormerService/contactUs');
	}
	//13.3 changes-end
}

function frequentlyAskedQuestions()
{
	preventBack = false;
	//13.3 changes-start
	populateGlobalCache();
	if(accountEarnsCBB(incentiveTypeCode)) {
     if(cardProductGroupCode == "DIT")
			navigation('../custormerService/customerServiceFaqs_ITCard');
		else
	        navigation('../custormerService/customerServiceFaqs_CBB');
	} else if(accountEarnsMiles(incentiveTypeCode)){	
		navigation('../custormerService/customerServiceFaqs_Miles');
	} else{
		navigation('../custormerService/customerServiceFaqs_Others');
	  }
	  
	/*if(cardProductGroupCode == "DBC"){
		navigation('../custormerService/faqDBC');
	}else if(cardProductGroupCode == "CRP"){
		navigation('../custormerService/faqCorp');
	}else{
		navigation('../custormerService/customerServiceFaqs');
	}*/
	//13.3 changes-end
}

function earnMoreMilesRewards()
{
	preventBack = false;
	populateGlobalCache();
	navigation('../rewards/milesHome');
}

function milesPromotions()
{
	preventBack = false;
	populateGlobalCache();
	navigation('../rewards/milesSignup1');
}

function redeemMiles()
{
	preventBack = false;
	populateGlobalCache();
	navigation('../rewards/milesRedeem');
}

function privacyTerms()
{
	preventBack = false;
	//navigation('../common/moreLandingRevised');
	//Changed to solve defect from 13.4
	navigation('../common/moreLanding');
}

function noTitle(){
	console.log("WE are in no title function");
	window.history.back(-2);
}

function acHome(){
	preventBack = true;
	console.log("inside acHome and preventback is "+preventBack);
	navigation('../achome/cardHome',false);
	activePage = "cardHome-pg";
	currentActivePage = "cardHome-pg";
}

function paymentHistory()
{
	preventBack = false;
	populateGlobalCache();
	navigation('../payments/paymentsHistory');
}

function redemptionOptions()
{
	preventBack = false;
	populateGlobalCache();
	navigation('../rewards/redemptionLanding');
}

// Defect id 96085
function home()
{
	console.log("home function is called");
	navigation('../common/dummy',false);
}
// Defect id 96085

//13.3 quickview start
function quickView()
{
	console.log("quickView function is called");
	//navigation('../common/dummy',false);
	activePage = "dummy-pg";
	currentActivePage = "dummy-pg";
	isLhnNavigation = true;
}
function qvFaq()
{
	preventBack = false;
	populateGlobalCache();
	console.log("quickView function is called");
	navigation('../custormerService/faqQuickView');
}
//13.3 quickview end


