function accountSummary()
{	
	populateGlobalCache();
	isLhnNavigation  = true;
	navigation('../achome/accountSummary');   
}

function recentActivity()
{
	navigation('../statements/accountActivity');	
}

function searchTransactions()
{
	navigation('../statements/searchTrans');	
}

function statements()
{	
	var statements = dfs.crd.stmt.shared.util.getStatements(); 
	if ( statements.isEmpty() ) {
			navigation('../achome/accountLanding');
	}else{
			navigation('../statements/statementLanding');
	}
	
}

function handleNativeFrame(activePage)
{	 
	if(activePage!="login-pg" && activePage!="loadingPage-pg") 
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
populateGlobalCache();
	isLhnNavigation  = true;
	navigation('../payments/paymentStep1');
}

function managePayments()
{
populateGlobalCache();
	navigation('../payments/paymentsLanding');
}

function manageBankInformation()
{
populateGlobalCache();
	navigation('../payments/manageBankInformation');
}

function sendMoney()
{	
	populateGlobalCache();
	isLhnNavigation  = true;
	navigation('../p2p/sendMoney1');
}

function sendMoneyHistory()
{
	populateGlobalCache();
	navigation('../p2p/transactionHistory');
}

function cashbackBonusPromos()
{
	populateGlobalCache();
	s.prop1='HANDSET_CBB_SIGNUP_BTN';
	navigation('../rewards/cashbackBonusSignup1');
}

function extras()
{
	populateGlobalCache();
	navigation('../edo/edoLandingPage');
}

function referAFriend()
{
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
	populateGlobalCache();
    dfs.crd.rwd.getAllPartners();
	s.prop1 = 'HANDSET_REDEEM_PGC_BTN'; // campaign code	
	var insuffErr = dfs.crd.rwd.insuficientErrorPresent;
   if (!isEmpty(insuffErr) && insuffErr === 'true') {
    navigation('../rewards/redeemPartnerInsufficientError');
	errorPartnerGiftCardSCVariables();
   } else if(!errorFlag) {  
    navigation('../rewards/browseLanding');
   }
}

function discoverGiftCard()
{	
	populateGlobalCache();
	dfs.crd.rwd.getAllPartners();
	s.prop1 = 'HANDSET_REDEEM_DGC_BTN'; // campaign code
	var insuffErr = dfs.crd.rwd.insuficientErrorPresent;
   	if (!isEmpty(insuffErr) && insuffErr === 'true') {
    navigation('../rewards/redeemDGCInsufficientError');
	errorDiscoverGiftCardSCVariables();
   } else if(!errorFlag){
    navigation('../rewards/giftcard1');
   }
}

function statementCredit()
{
	populateGlobalCache();
	s.prop1 = 'HANDSET_REDEEM_STATE_CREDIT_BTN'; // campaign code
	isLhnNavigation  = true;
	navigation('../rewards/statementCredit1');	
}

function directDeposit()
{
	populateGlobalCache();
	s.prop1 = 'HANDSET_REDEEM_DIRECT_DEP_BTN'; // campaign code
	isLhnNavigation  = true;
	navigation('../rewards/directDeposit1');	
}

function paywithCashbackBonus()
{
	populateGlobalCache();
	s.prop1 = 'HANDSET_REDEEM_PAY_CBB_BTN'; // campaign code
	//navigation('../rewards/redeemCashbackEcert1');
	navigation('../rewards/redeem_pay_with_cbb');
}

function redemptionHistory()
{
	populateGlobalCache();
	s.prop1 = 'HANDSET_REDEEM_HIST_BTN'; // campaign code
	navigation('../rewards/redemption_History');
}

function manageTextAlerts()
{
	
	navigation('../pushNotification/manageAlertsOverride');
}

function pushAlertsHistory()
{
	navigation('../pushNotification/alertHistory');
}

function createCashPIN()
{
	isLhnNavigation  = true;
	navigation('../profile/personalizeCashPin1');
}

function contactUs()
{
   navigation('../custormerService/contactUs');
}

function frequentlyAskedQuestions()
{
	navigation('../custormerService/customerServiceFaqs');
}

function earnMoreMilesRewards()
{
	populateGlobalCache();
	navigation('../rewards/milesHome');
}

function milesPromotions()
{
	populateGlobalCache();
	navigation('../rewards/milesSignup1');
}

function redeemMiles()
{
	populateGlobalCache();
	navigation('../rewards/milesRedeem');
}

function privacyTerms()
{
	navigation('../common/moreLanding');
}

function noTitle(){
	console.log("WE are in no title function");
	window.history.back(-2);
}

function acHome(){
	navigation('../achome/cardHome');
}

function paymentHistory()
{
populateGlobalCache();
navigation('../payments/paymentsHistory');
}

function redemptionOptions()
{
populateGlobalCache();
navigation('../rewards/redemptionLanding');
}