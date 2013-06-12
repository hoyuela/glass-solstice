function exploreDiscoverLandingLoad(){
	try{
		var showRewards=true;
		var cardHome = getDataFromCache("ACHOME");
		if(!jQuery.isEmptyObject(cardHome)){
			var cardProductGroupCodeVal=cardHome.cardProductGroupCode
			if(!isEmpty(cardProductGroupCodeVal)){
				// is for Motiva and OPR is for Open road
				if( cardProductGroupCodeVal == "MTV" || cardProductGroupCodeVal == "OPR" ){
					showRewards=false;
				}
			}
		}
		var exploreLinks = "";	
		if(showRewards){
			exploreLinks = getPageContentMin("exploreDiscover","explore_discover_landing_page",incentiveTypeCode,incentiveCode);
		}else{
			exploreLinks=getPageContentCn("exploreDiscover","explore_discover_landing_page");
		}
		document.getElementById('showAllLinks').innerHTML = exploreLinks;

	}catch(err){
		showSysException(err)
	}
}

function benefitsLoad(){
	try{
		var benefits = "";	
		benefits = getPageContentMin("exploreDiscover","explore_benefits",incentiveTypeCode,incentiveCode);
		document.getElementById('showBenefits').innerHTML = benefits;
	}catch(err){
		showSysException(err)
	}
}

function communityLoad(){}

function myBenefitsLoad(){}

function privacyPolicyLoad(){}

function rewardsLoad(){}

function securityLoad(){}

function termsUseLoad(){}

function customerServiceLandingLoad(){}

