var menuItems = {
	HM:"<div id='home' data-icon='custom' class='ui-link' onclick='showHM();'></div>",
	AC:"<div id='account' data-icon='custom' class='ui-link' onclick='showAC();'></div>",
	PY:"<div id='payments' data-icon='custom' class='ui-link' onclick='showPY();'></div>",
	CB:"<div id='cashback' data-icon='custom' class='ui-link' onclick='showCB();'></div>",
	MI:"<div id='miles' data-icon='custom' class='ui-link' onclick='showMI();'></div>",
	MM:"<div id='sendmoney' data-icon='custom' class='ui-link' onclick='showMM();'></div>",
	ED:"<div id='expdiscover' data-icon='custom' class='ui-link' onclick='showED();'></div>",
	CS:"<div id='custservice' data-icon='custom' class='ui-link' onclick='showCS();'></div>",
	MP:"<div id='profile' data-icon='custom' class='ui-link' onclick='showMP();'></div>",
	MR:"<div id='more' data-icon='custom' class='ui-link' onclick='showMR();'></div>",
	HM_ACTIVE:"<div id='home' data-icon='custom' class='ui-btn-active ui-link' onclick='showHM();'></div>",
	AC_ACTIVE:"<div id='account' data-icon='custom' class='ui-btn-active ui-link' onclick='showAC();'></div>",
	PY_ACTIVE:"<div id='payments' data-icon='custom' class='ui-btn-active ui-link' onclick='showPY();'></div>",
	CB_ACTIVE:"<div id='cashback' data-icon='custom' class='ui-btn-active ui-link' onclick='showCB();'></div>",
	MI_ACTIVE:"<div id='miles' data-icon='custom' class='ui-btn-active ui-link' onclick='showMI();'></div>",
	MM_ACTIVE:"<div id='sendmoney' data-icon='custom' class='ui-btn-active ui-link' onclick='showMM();'></div>",
	ED_ACTIVE:"<div id='expdiscover' data-icon='custom' class='ui-btn-active ui-link' onclick='showED();'></div>",
	CS_ACTIVE:"<div id='custservice' data-icon='custom' class='ui-btn-active ui-link' onclick='showCS();'></div>",
	MP_ACTIVE:"<div id='profile' data-icon='custom' class='ui-btn-active ui-link' onclick='showMP();'></div>",
	MR_ACTIVE:"<div id='more' data-icon='custom' class='ui-link ui-btn-active' onclick='showMR();'></div>",	
	HM_MORE:"<li data-icon='false' onclick='showHM();'><a href='#'  class='bluelink'>Home</a> <span class='floatright arrow'> &gt; </span></li>",
	AC_MORE:"<li data-icon='false' onclick='showAC();'><a href='#'  class='bluelink'>Account</a> <span class='floatright arrow'> &gt; </span></li>",
	PY_MORE:"<li data-icon='false' onclick='showPY();'><a href='#'  class='bluelink'>Payments</a> <span class='floatright arrow'> &gt; </span></li>",
	CB_MORE:"<li data-icon='false' onclick='showCB();'><a href='#'  class='bluelink'>Cashback Bonus</a> <span class='floatright arrow'> &gt; </span></li>",
	MI_MORE:"<li data-icon='false' onclick='showMI();'><a href='#'  class='bluelink'>Miles</a> <span class='floatright arrow'> &gt; </span></li>",
	/*13.3 Global Change:Starts*/ MM_MORE:"<li data-icon='false'class='ui-btn ui-btn-icon-right ui-li ui-corner-top ui-btn-up-d'><div class='ui-btn-inner ui-li' aria-hidden='true'><div class='ui-btn-text'><a href='#' onclick='showMM();' class='amt_bold ui-link-inherit'>Send Money</a><span class='floatright payArr'></span></div></div></li>",
	ED_MORE:"<li data-icon='false' onclick='showED();'class='ui-btn ui-btn-icon-right ui-li ui-btn-up-d'><div class='ui-btn-inner ui-li' aria-hidden='true'><div class='ui-btn-text'><a href='#' class='amt_bold ui-link-inherit'>Explore Discover</a><span class='floatright payArr'> </span></div></div></li>",
	CS_MORE:"<li data-icon='false'class='ui-btn ui-btn-icon-right ui-li ui-btn-up-d'><div class='ui-btn-inner ui-li' aria-hidden='true'><div class='ui-btn-text'><a href='#' onclick='showCS();' class='amt_bold ui-link-inherit'>Customer Service</a><span class='floatright payArr'> </span></div></div></li>",
	MP_MORE:"<li data-icon='false' class='ui-btn ui-btn-icon-right ui-li ui-btn-up-d'><div class='ui-btn-inner ui-li'><div class='ui-btn-text'><a href='#' onclick='showMP();' class='amt_bold ui-link-inherit'>Profile and Settings</a><span class='floatright payArr'></span></div></div></li>",
	PS_MORE:"<li data-icon='false' class='ui-btn ui-btn-icon-right ui-li ui-btn-up-d'><div class='ui-btn-inner ui-li' aria-hidden='true'><div class='ui-btn-text'><a href='#' onclick='navigation(\"privacyPolicyMore\");' class='amt_bold ui-link-inherit'>Privacy Statement</a><span class='floatright payArr'> </span></div></div></li>",
	TU_MORE:"<li data-icon='false' class='ui-btn ui-btn-icon-right ui-li ui-btn-up-d ui-corner-bottom'><div class='ui-btn-inner ui-li' aria-hidden='true'><div class='ui-btn-text'><a href='#' onclick='navigation(\"termsUseMore\");' class='amt_bold ui-link-inherit'>Terms of Use</a><span class='floatright payArr'> </span></div></div></li>"/*13.3 Global Change:Ends*/
}

var menuConfig = {
	bottomnavitems:"HM,AC,PY,CB,MI,CS,MM,MP,PS,TU",
	CB:{
		code:"CB",
		its:"CBB",
		ics:"000001,000011,000013,000014,000015,000016",
		docs:""
	},
	MI:{
		code:"MI",
		its:"MI2",
		ics:"000002,000003",
		docs:""
	},
	MM:{
		code:"MM",
		its:"CBB,MI2",
		ics:"000001,000011,000013,000014,000015,000016,000002,000003",
		docs:"27,28"
	}
}
