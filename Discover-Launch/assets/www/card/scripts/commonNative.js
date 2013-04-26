function accountSummary()
{	
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
	isLhnNavigation  = true;
	navigation('../payments/paymentStep1');
}

function managePayments()
{
	navigation('../payments/paymentsLanding');
}

function manageBankInformation()
{
	navigation('../payments/manageBankInformation');
}

function sendMoney()
{	isLhnNavigation  = true;
	navigation('../p2p/sendMoney1');
}

function sendMoneyHistory()
{
	navigation('../p2p/transactionHistory');
}

function signupfor()
{
	navigation('../rewards/cashbackBonusSignup1');
}

function extras()
{
	navigation('../edo/edoLandingPage');
}

function referAFriend()
{
	if(accountEarnsCBB(incentiveTypeCode)){	
	  dfs.crd.raf.navFromCBBLanding();
	}
	else{ 
	 dfs.crd.raf.navFromMilesLanding();
	}
}

function partnerGiftCardseCerts()
{
    dfs.crd.rwd.getAllPartners();	
	var insuffErr = dfs.crd.rwd.insuficientErrorPresent;
   if (!isEmpty(insuffErr) && insuffErr === 'true') {
    navigation('../rewards/redeemPartnerInsufficientError');
   } else if(!errorFlag) {  
    navigation('../rewards/browseLanding');
   }
}

function discoverGiftCards()
{	
	dfs.crd.rwd.getAllPartners();	
	var insuffErr = dfs.crd.rwd.insuficientErrorPresent;
   	if (!isEmpty(insuffErr) && insuffErr === 'true') {
    navigation('../rewards/redeemDGCInsufficientError');
   } else if(!errorFlag){
    navigation('../rewards/giftcard1');
   }
}

function statementCredit()
{
	isLhnNavigation  = true;
	navigation('../rewards/statementCredit1');
	
}

function directDeposit()
{
	isLhnNavigation  = true;
	navigation('../rewards/directDeposit1');
	
}

function paywithCashbackBonus()
{
	//navigation('../rewards/redeemCashbackEcert1');
	navigation('../rewards/redeem_pay_with_cbb');
}

function redemptionHistory()
{
	navigation('../rewards/redemption_History');
}

function manageTextAlerts()
{
	navigation('../pushNotification/manageAlertsOverride');
}

function alertsHistory()
{
	navigation('../pushNotification/alertHistory');
}

function createCashPIN()
{
	navigation('../pushNotification/pushDiagonstic');
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
	navigation('../rewards/milesHome');
}

function signupforMiles()
{
	navigation('../rewards/milesSignup1');
}

function redeemMiles()
{
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