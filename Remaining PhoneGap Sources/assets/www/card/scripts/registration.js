namespace("dfs.crd.reg");
dfs.crd.reg.forgotPassword = {};
dfs.crd.reg.forgotBoth = {};
dfs.crd.reg.util = {};

//relative to back button handling allows navigation to next registration page without custom navigation prevention
dfs.crd.reg.forceNavAllow = false;

dfs.crd.reg.flow = {
	forgotUserId : {
		load : function() {
			dfs.crd.reg.nav.handleNav(this);
			dfs.crd.reg.ui.prepareFooter();
			dfs.crd.reg.forgotUserId.validateForm('#forgot-uid-form');
			
			var form = $('#forgot-uid-form');
    		form.unbind('submit').submit(function(event) {
				dfs.crd.reg.forgotUserId.submit(event);
			});
			$('#forgot-uid-form #js-submit').unbind('click').click(function(event) {
				form.submit();
			});
			$('#forgot-uid-form #js-back').unbind('click').click(function(event) {
                dfs.crd.reg.nav.skipBack();
            });
		},
    	allowableNav : ['forgotUserIdOrPasswordMenu', 'publicError'],
    	backNavToHome : ['forgotUserIdConfirmation', 'accountLanding']
	},    
    forgotUserIdConfirmation : {
    	load : function() {
    		dfs.crd.reg.nav.handleNav(this);
    		dfs.crd.reg.shared.util.populateConfirmation('fuid-email', 'fuid-last4', 'fuid-userId', dfs.crd.reg.shared.constant.cache.FORGOT_UID_CONFIRMATION_RESP);
    		dfs.crd.reg.shared.constant.cache.removeAll();
			$('#forgot-uid-confirmation-pg #js-home-link').unbind('click').click(function(event) {
				dfs.crd.reg.nav.goToHome();
			});
    	},
    	allowableNav : ['forgotUserId'],
    	backNavToHome : ['accountLanding']
    },    
	forgotPasswordStep1 : {
		load : function() {
			dfs.crd.reg.nav.handleNav(this);
			dfs.crd.reg.ui.prepareFooter();
			dfs.crd.reg.util.accountValidation.validateForm('#forgot-password-step1-form');
			
			//submit handler
			var form = $('#forgot-password-step1-form');
			form.unbind('submit').submit(function(event) {
				dfs.crd.reg.forgotPassword.step1.submit(event);
			});
			$('#forgot-password-step1-form #js-submit').unbind('click').click(function(event) {
				form.submit();
			});
			$('#forgot-password-step1-form #js-back').unbind('click').click(function(event) {
			    dfs.crd.reg.nav.skipBack();
            });
		},
    	allowableNav : ['forgotUserIdOrPasswordMenu', 'publicError'],
    	backNavToHome : ['forgotPasswordConfirmation']
	},
    forgotPasswordStep2 : {     
		load : function() {
		    dfs.crd.reg.nav.handleNav(this);
			dfs.crd.reg.ui.prepareFooter();
			var passwordMeter = new dfs.crd.reg.shared.util.InputStrengthMeter('.password-field', '.password');			
			dfs.crd.reg.forgotPassword.step2.validateForm('#forgot-password-step2-form');
			//keep after validate form
			dfs.crd.reg.shared.util.populateFormFromCache(dfs.crd.reg.shared.constant.cache.FORGOT_PASSWORD_STEP2_REQ);
			
			//submit handler
			var form = $('#forgot-password-step2-form');			
			form.unbind('submit').submit(function(event) {
				dfs.crd.reg.forgotPassword.step2.submit(event);
			});	
			$('#forgot-password-step2-form #js-submit').unbind('click').click(function(event) {
				form.submit();
			});			
			$('#forgot-password-step2-form #js-back').unbind('click').click(function(event) {
			    dfs.crd.reg.nav.skipBack();
            });
			//password strength meter handler
			$('#forgot-password-step2-form #passwordStrengthMeter').unbind('click').click(function(event) {
				dfs.crd.reg.nav.cacheAndGo("passwordStrengthMeter", "forgot-password-step2-form", dfs.crd.reg.shared.constant.cache.FORGOT_PASSWORD_STEP2_REQ);
			});
		},
		allowableNav : ['forgotPasswordStep1', 'strongAuthFirstQues', 'userIdStrengthMeter', 'passwordStrengthMeter'],
		backNavToHome : ['forgotPasswordConfirmation', 'accountLanding']
    }, 
    forgotPasswordConfirmation : {     
		load : function() {
			dfs.crd.reg.nav.handleNav(this);
			dfs.crd.reg.shared.util.populateConfirmation('fp-email', 'fp-last4', 'fp-userId', dfs.crd.reg.shared.constant.cache.FORGOT_PASSWORD_CONFIRMATION_RESP);
			dfs.crd.reg.shared.constant.cache.removeAll();
			$('#forgot-password-confirmation-pg #js-home-link').unbind('click').click(function(event) {
				dfs.crd.reg.nav.goToHome();
			});
		},
		allowableNav : ['forgotPasswordStep2'],
		backNavToHome : ['accountLanding']
    },    
    forgotUserIdOrPasswordMenu : {
    	load : function (){
    		dfs.crd.reg.ui.prepareFooter();
    		dfs.crd.reg.shared.constant.cache.removeAllButErrorText();			
    		$('#js-forgot-uid-link').unbind('click').click(function(event) {
				dfs.crd.reg.nav.goToForgotUID();
			});
			$('#js-forgot-password-link').unbind('click').click(function(event) {
				dfs.crd.reg.nav.goToForgotPassword();
			});
			$('#js-forgot-both-link').unbind('click').click(function(event) {
				dfs.crd.reg.nav.goToForgotBoth();
			});
    	}
    }
   
}

/**
 * Registration related navigation
**/
dfs.crd.reg.nav = {
	goToForgotUID : function() {
		navigation("forgotUserId");
	},

	goToForgotPassword : function() {
		navigation('forgotPasswordStep1');
	},

	goToForgotBoth : function() {
		navigation('forgotBothStep1');
	},

	goToHome : function() {
		navigation(dfs.crd.reg.shared.constant.url.HOME);
	},
	
	goToLanding : function() {
		navigation(dfs.crd.reg.shared.constant.url.FORGOT_LANDING);
	},

	//store form in cache and navigate
	cacheAndGo : function(destination, formId, cacheLoc) {
		var formData = $('#' + formId).serializeObject();
		putDataToCache(cacheLoc, formData);
		navigation(destination);
	},

	goToForgotUidOrPassMenu : function(){
		navigation('../registration/forgotUserIdOrPasswordMenu');
	},
	
	handleNav : function(o) {	
	 
		//check force allow status and allow if true
		if (dfs.crd.reg.forceNavAllow === true)	{
			dfs.crd.reg.forceNavAllow = false;
			return;
		}
			   
	    if (isEmpty(o)) return;
	        
	    //If exists in backNav to home go home
	    if (!isEmpty(o.backNavToHome) && $.inArray(fromPageName, o.backNavToHome) !== -1) {
	    	dfs.crd.reg.nav.goHome();
	    } 
	    //otherwise if not part of allowed flow skip back
	    else if (!isEmpty(o.allowableNav) && $.inArray(fromPageName, o.allowableNav) === -1) {
	    	dfs.crd.reg.nav.skipBack();
	    }
	},
	
	goHome : function() { cpEvent.preventDefault(); dfs.crd.reg.nav.goToHome(); },
	
	skipBack : function() { cpEvent.preventDefault(); history.back(); }
};



/** Logic involved in the forgot password flow **/
dfs.crd.reg.forgotPassword.step1 = {
	submit : function(event) {
		try {
			event.preventDefault();
			event.stopPropagation();
		    if (!dfs.crd.reg.shared.util.formValidation.isValidForm()) {
                var $ccOrAccountId = $('[name=userId]');
                var $expMonth =  $('[name=expirationMonth]');
                var $expYear =  $('[name=expirationYear]');
                var $birthMonth =  $('[name=dateOfBirthMonth]');
                var $birthDate =  $('[name=dateOfBirthDay]');
                var $ssnEnding =  $('[name=socialSecrityNumber]');
                var $dobYear = $('[name=dateOfBirthYear]');   
                dfs.crd.reg.shared.util.formValidation.forgotPasswordStep1Validation($ccOrAccountId, $expMonth, $expYear, $birthMonth, $birthDate, $ssnEnding, $dobYear);        
                return;           
            }
		    
			var url = dfs.crd.reg.shared.constant.url.FORGOT_PASSWORD_AUTH;
			var formData = $('#forgot-password-step1-form').serializeObject();
			var requestBody = JSON.stringify(formData);
			var cacheName = dfs.crd.reg.shared.constant.cache.FORGOT_PASSWORD_STEP1_REQ;
			var nav = dfs.crd.reg.shared.constant.url.FORGOT_PASSWORD_STEP2;
            dfs.crd.reg.shared.http.step1Post(url, requestBody, nav, cacheName, formData);
		} catch(err) {
			showSysException(err);
		}
	}
};//==> forgotPassword.step1

dfs.crd.reg.forgotPassword.step2 = {
	submit : function(event) {
	    try {
    		event.preventDefault();
    		event.stopPropagation();    		
    		
            if (!dfs.crd.reg.shared.util.formValidation.isValidForm()) {   
                var $passwordField = $('[name=password]'), $confirmPasswordField = $('[name=passwordConfirm]');                
                dfs.crd.reg.shared.util.formValidation.forgotPasswordStep2Validation($passwordField, $confirmPasswordField);
                return;            
            }
            var formData = $('#forgot-password-step2-form').serializeObject();
            dfs.crd.reg.shared.http.step2Post(event, formData, dfs.crd.reg.shared.constant.url.FORGOT_PASSWORD);
		} catch(err) {
			showSysException(err);
		}
	},


//==> forgotBoth.step2

dfs.crd.reg.forgotBoth.step2 = 

            var formData = $('#forgot-both-step2-form').serializeObject();	
		    dfs.crd.reg.shared.http.step2Post(event, formData, dfs.crd.reg.shared.constant.url.FORGOT_BOTH);


	

/** Logic involved in the forgot user id flow **/
dfs.crd.reg.forgotUserId = {

	submit : function(event) {
		try {
			event.preventDefault();
	        event.stopPropagation();        
		    var $acctNbr = $('input[name=credit-card-account-number]');
	        var $pass = $('input[name=account-center-password]');
			var userIdAndPass = $acctNbr.val() + ': :' + $pass.val()
			var dcrdBasicAuthHeader = getAuthorizationString(userIdAndPass);
			
			if (dfs.crd.reg.shared.util.formValidation.isValidForm()) {		    
	            dfs.crd.reg.shared.http.forgotUserIdSubmit(dcrdBasicAuthHeader, event);
			} else {
			        
	            dfs.crd.reg.shared.util.formValidation.forgotUIDForceValidation($acctNbr, $pass);            
			}
		} catch(err) {
			showSysException(err);
		}
	},
	
	

/********  Registration Load Functions Called by Common +Load Framework *********/
function forgotUserIdOrPasswordMenuLoad() {
	try {
	    dfs.crd.reg.flow.forgotUserIdOrPasswordMenu.load();
	} catch (err) {
		showSysException(err);
	}
}

function forgotUserIdLoad() {
	try {
	    dfs.crd.reg.flow.forgotUserId.load();
	} catch (err) {
		showSysException(err);
	}
}

function forgotUserIdConfirmationLoad() {
	try {
	    dfs.crd.reg.flow.forgotUserIdConfirmation.load();
		dfs.crd.reg.fixBottomNav();
	} catch (err) {
		showSysException(err);
	}
}

function forgotBothStep1Load() {
	try {
	    dfs.crd.reg.flow.forgotBothStep1.load();
	} catch (err) {
		showSysException(err);
	}
}

function forgotBothStep2Load() {
	try {
		dfs.crd.reg.flow.forgotBothStep2.load();
	} catch (err) {
		showSysException(err);
	}
}

function forgotBothConfirmationLoad() {
	try {
		dfs.crd.reg.flow.forgotBothConfirmation.load();
		dfs.crd.reg.fixBottomNav();
	} catch (err) {
		showSysException(err);
	}
}

function forgotPasswordStep1Load() {
	try {
		dfs.crd.reg.flow.forgotPasswordStep1.load();
	} catch (err) {
		showSysException(err);
	}
}

function forgotPasswordStep2Load() {
	try {
		dfs.crd.reg.flow.forgotPasswordStep2.load();
	} catch (err) {
		showSysException(err);
	}
}

function forgotPasswordConfirmationLoad() {
	try {
		dfs.crd.reg.flow.forgotPasswordConfirmation.load();
		dfs.crd.reg.fixBottomNav();
	} catch (err) {
		showSysException(err);
	}
}

function userIdStrengthMeterLoad() {
	try {
		dfs.crd.reg.flow.userIdStrengthMeter.load();
	} catch (err) {
		showSysException(err);
	}
}

function passwordStrengthMeterLoad() {
	try {
		dfs.crd.reg.flow.passwordStrengthMeter.load();
	} catch (err) {
		showSysException(err);
	}
}

dfs.crd.reg.fixBottomNav = function() {
	//clears out bottom nav so menu can be pulled
	clearGlobalCache();
	//gets data required for menu
	dfs.crd.achome.populateACHome('ACHOME');
	//pulls menu items and populates bottom nav
	moreLandingLoad();
};
