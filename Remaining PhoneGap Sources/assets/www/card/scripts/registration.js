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
    forgotBothStep1 : {
    	load : function() {
    		dfs.crd.reg.nav.handleNav(this);
			dfs.crd.reg.ui.prepareFooter();
    		//set title
    		var title = getDataFromCache(dfs.crd.reg.shared.constant.cache.FORGOT_BOTH_TITLE);
    		if (!isEmpty(title))	{
    			$('#forgotBothTitleStep1').html(title.step1);	
    		}
    		
    		dfs.crd.reg.util.accountValidation.validateForm('#forgot-both-step1-form');
    		
    		//submit handler
    		var form = $('#forgot-both-step1-form');
    		form.unbind('submit').submit(function(event) {
				dfs.crd.reg.forgotBoth.step1.submit(event);
			});
			$('#forgot-both-step1-form #js-submit').unbind('click').click(function(event) {
				form.submit();
			});
			//back button handler
			$('#forgot-both-step1-form #js-back').unbind('click').click(function(event) {
                dfs.crd.reg.nav.skipBack();
            });
    	},
    	allowableNav : ['forgotUserIdOrPasswordMenu', 'publicError'],
    	backNavToHome : ['forgotBothConfirmation']
    },
    forgotBothStep2 : {     
		load : function() {
			dfs.crd.reg.nav.handleNav(this);	
			dfs.crd.reg.ui.prepareFooter();	
				
			//set title
			var title = getDataFromCache(dfs.crd.reg.shared.constant.cache.FORGOT_BOTH_TITLE);
    		if (!isEmpty(title)){
    			$('#forgotBothTitleStep2').html(title.step2);  
    		}    	
    		
			var userIDMeter = new dfs.crd.reg.shared.util.InputStrengthMeter('.userid-field', '.user-id', true);
			var passwordMeter = new dfs.crd.reg.shared.util.InputStrengthMeter('.password-field', '.password', false, '.user-id');	
			dfs.crd.reg.forgotBoth.step2.validateForm('#forgot-both-step2-form');
			//call after validate form
			dfs.crd.reg.shared.util.populateFormFromCache(dfs.crd.reg.shared.constant.cache.FORGOT_BOTH_STEP2_REQ);
						
			//submit handler
			var form = $('#forgot-both-step2-form');
    		form.unbind('submit').submit(function(event) {
				dfs.crd.reg.forgotBoth.step2.submit(event);
			});
			$('#forgot-both-step2-form #js-submit').unbind('click').click(function(event) {
				form.submit();
			});
			$('#forgot-both-step2-form #js-back').unbind('click').click(function(event) {
                dfs.crd.reg.nav.skipBack();
            });
            
			//password strength meter handler
			$('#forgot-both-step2-form #passwordStrengthMeter').unbind('click').click(function(event) {
				dfs.crd.reg.nav.cacheAndGo("passwordStrengthMeter", "forgot-both-step2-form", dfs.crd.reg.shared.constant.cache.FORGOT_BOTH_STEP2_REQ);
			});	
			//user id strength meter handler
			$('#forgot-both-step2-form #userIdStrengthMeter').unbind('click').click(function(event) {
				dfs.crd.reg.nav.cacheAndGo("userIdStrengthMeter", "forgot-both-step2-form", dfs.crd.reg.shared.constant.cache.FORGOT_BOTH_STEP2_REQ);
			});
		},
		allowableNav : ['forgotBothStep1', 'strongAuthFirstQues', 'userIdStrengthMeter', 'passwordStrengthMeter'],
		backNavToHome : ['forgotBothConfirmation', 'accountLanding']
    }, 
    forgotBothConfirmation : {     
		load : function() {
			dfs.crd.reg.nav.handleNav(this);
			
			//set title
			var title = getDataFromCache(dfs.crd.reg.shared.constant.cache.FORGOT_BOTH_TITLE);
    		if (!isEmpty(title)){
    			$('#forgotBothTitleConfirmation').html(title.confirmation);  
    			$('#forgotBothBodyConfirmation').html(title.confirmationBody);  
    		}    	
    		
    		dfs.crd.reg.shared.util.populateConfirmation('email', 'last4', 'userId', dfs.crd.reg.shared.constant.cache.FORGOT_BOTH_CONFIRMATION_RESP);
    		dfs.crd.reg.shared.constant.cache.removeAll();
			$('#forgot-both-confirmation-pg #js-home-link').unbind('click').click(function(event) {
				dfs.crd.reg.nav.goToHome();
			});
		},
		allowableNav : ['forgotBothStep2'],
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
    },
    passwordStrengthMeter : {
    	load : function() {
    		dfs.crd.reg.nav.handleNav(this);
			dfs.crd.reg.ui.prepareFooter();
    	}
    
    },
    userIdStrengthMeter : {
    	load : function (){
    		dfs.crd.reg.nav.handleNav(this);
			dfs.crd.reg.ui.prepareFooter();
    	}
    }
}

/**
 * Since there aren't good footers in common to use I can overwrite the common footer with my own here.
**/
dfs.crd.reg.ui = {
	prepareFooter : function() {
		/*var pubFootnotes = "<div class='need-help'><span>Need Help?</span> <a href='tel:18003472683'>Call 1-800-347-2683</a></div>";
		//var myFootnotesHtml="<p id='footer-links'><a href='#'  onclick='cbInvoke();'>Provide Feedback </a><a href='#' onclick='dfs.crd.reg.nav.goToRegistration();'  data-rel='external' class='registerNow'>| Register Now</a></p><p data-theme='e' class='footer-text-icon'><span>&copy;2012 Discover Bank, Member FDIC.</span></p>";
		var myFootnotesHtml="<a href='#' onclick='cbInvoke();' id='footer-links'>Provide Feedback</a><span class='divspace' style='font-size:15px'>|</span><p id='footer-text'>&copy;2012 Discover Bank, Member FDIC <span id='wordSecured'>| SECURED</span></p>";
		$(".reg-footnotes").html(pubFootnotes + myFootnotesHtml);*/
		var footnotesHtml="<div class='need-help'><span>Need Help?</span> <a href='tel:18003472683'>Call 1-800-347-2683</a></div><p id='footer-links'><a href='#'  onclick='provideFeedBack();'>Provide Feedback </a><a href='#' onclick='dfs.crd.reg.nav.goToRegistration();'  data-rel='external' class='registerNow'>| Register Now</a></p><p data-theme='e' class='footer-text-icon'><span>&copy; 2012 Discover Bank, Member FDIC<span class='secured'> | SECURED</span></p>";
		$(".footnotes-reg").html(footnotesHtml);
	}
};

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
	
	goToRegistration : function(pagename){     
		putDataToCache(dfs.crd.reg.shared.constant.cache.FORGOT_BOTH_TITLE, 
			{step1 : "Register", step2 : "Register", confirmation : "Registration Confirmation", confirmationBody : "Your registration is complete, you are now logged in and an email has been sent to you."});
		
		dfs.crd.reg.forceNavAllow = true;
		
		if(pagename=='cardLogin') {
			navigation('../registration/forgotBothStep1');
		} else {
			navigation('card/html/registration/forgotBothStep1');
		}
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

/**
 * Used for both forgot pass and forgot both 
 */
dfs.crd.reg.util.accountValidation = {

	validateForm : function(form) {
	    
		var init = function() {
			var $ccNum = $('.credit-card-number', form), ccNumPattern = /^6011\d{12}$/, $ccOrAccountId = $('.credit-card-account-number', form), validForm = false, $expMonth = $(".expiration-month", form), $expYear = $(".expiration-year", form), $birthMonth = $('.birth-month', form), $birthDate = $('.birth-date', form), $ssnEnding = $('.ssn-ending', form), $dobYear = $('.dob-year', form), fourDigitPattern = /^[0-9]{4}$/, $submitButton = $('#js-submit', form);
			
			dfs.crd.reg.shared.util.formValidation.setCurrentForm(form);
			
			// If 'credit card or user id' field exists,
			// perform this validation and attach the handler
			if ($ccOrAccountId.length) {
				dfs.crd.reg.shared.util.formValidation.validateCcOrAccountId($ccOrAccountId);
				dfs.crd.reg.shared.util.formValidation.ccOrAccountIdHandlers($ccOrAccountId);
			}

			// Forgot Both flow only:
			// If 'credit card or user id' field exists,
			// perform this validation and attach the handler
			if ($ccNum.length) {
				dfs.crd.reg.shared.util.formValidation.validateCcNum($ccNum);
				dfs.crd.reg.shared.util.formValidation.ccNumHandlers($ccNum);
			}

			// shared validation functions
			dfs.crd.reg.shared.util.formValidation.validateExpirationMonth($expMonth);
			dfs.crd.reg.shared.util.formValidation.validateExpirationYear($expYear);
			dfs.crd.reg.shared.util.formValidation.validateBirthMonth($birthMonth);
			dfs.crd.reg.shared.util.formValidation.validateBirthDate($birthDate);
			dfs.crd.reg.shared.util.formValidation.validateSsnEnding($ssnEnding);
			dfs.crd.reg.shared.util.formValidation.validateDobYear($dobYear);

			// shared event handlers
			dfs.crd.reg.shared.util.formValidation.onChangeExpirationMonth($expMonth);
			dfs.crd.reg.shared.util.formValidation.onChangeExpirationYear($expYear);
			dfs.crd.reg.shared.util.formValidation.onChangeBirthMonth($birthMonth);
			dfs.crd.reg.shared.util.formValidation.onChangeBirthDate($birthDate);
			dfs.crd.reg.shared.util.formValidation.ssnEndingHandlers($ssnEnding);
			dfs.crd.reg.shared.util.formValidation.dobYearHandlers($dobYear);
		}();
	}
	//==> validateForm

};
//==>dfs.crd.reg.util.accountValidation

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
	//==>submit

	validateForm : function(form) {
		var init = function() {
		    var $passwordField = $('.password', form), $confirmPasswordField = $('.confirm-password', form), $submitButton = $('#js-submit', form);
            dfs.crd.reg.shared.util.formValidation.setCurrentForm(form);            
            dfs.crd.reg.shared.util.formValidation.validatePassword($passwordField, $confirmPasswordField);
			dfs.crd.reg.shared.util.formValidation.passwordHandlers($passwordField, $confirmPasswordField);
			dfs.crd.reg.shared.util.formValidation.confirmPasswordHandlers($passwordField, $confirmPasswordField);
		}();
	}
	//==>validateForm
};
//==> forgotPassword.step2

/** Logic involved in the forgot both flow **/
dfs.crd.reg.forgotBoth.step1 = {
	submit : function(event) {
		try {
		    event.preventDefault();
			event.stopPropagation();
		    if (!dfs.crd.reg.shared.util.formValidation.isValidForm()) {
                event.preventDefault();
                event.stopPropagation();     
                var $ccNum = $('[name=acctNbr]');
                var $expMonth =  $('[name=expirationMonth]');
                var $expYear =  $('[name=expirationYear]');
                var $birthMonth =  $('[name=dateOfBirthMonth]');
                var $birthDate =  $('[name=dateOfBirthDay]');
                var $ssnEnding =  $('[name=socialSecrityNumber]');
                var $dobYear = $('[name=dateOfBirthYear]');               
                dfs.crd.reg.shared.util.formValidation.forgotBothStep1Validation($ccNum, $expMonth, $expYear, $birthMonth, $birthDate, $ssnEnding, $dobYear); 
                return;           
            }
		    
			var url = dfs.crd.reg.shared.constant.url.FORGOT_BOTH_AUTH;
			var formData = $('#forgot-both-step1-form').serializeObject();
			var requestBody = JSON.stringify(formData);
			var cacheName = dfs.crd.reg.shared.constant.cache.FORGOT_BOTH_STEP1_REQ;
			var nav = dfs.crd.reg.shared.constant.url.FORGOT_BOTH_STEP2;
			dfs.crd.reg.shared.http.step1Post(url, requestBody, nav, cacheName, formData);
		} catch(err) {
			showSysException(err);
		}
	}
	//==>submit
}
//==> forgotBoth.step1

dfs.crd.reg.forgotBoth.step2 = {
	submit : function(event) {
		try {
		    event.preventDefault();
			event.stopPropagation();
		    if (!dfs.crd.reg.shared.util.formValidation.isValidForm()) {
                var $userIdField = $('[name=userId]');
                var $confirmUserIdField = $('[name=userIdConfirm]');                
                var $passwordField = $('[name=password]');
                var $confirmPasswordField = $('[name=passwordConfirm]');             
                var $emailAddressField = $('[name=email]');                
                dfs.crd.reg.shared.util.formValidation.forgotBothStep2Validation($userIdField, $confirmUserIdField, $passwordField, $confirmPasswordField, $emailAddressField);
                return;            
            }
            var formData = $('#forgot-both-step2-form').serializeObject();	
		    dfs.crd.reg.shared.http.step2Post(event, formData, dfs.crd.reg.shared.constant.url.FORGOT_BOTH);
		} catch(err) {
			showSysException(err);
		}
	},
	//==>submit

	validateForm : function(form) {
	    
		var init = function() {
		    var $passwordField = $('.password', form), $userIdField = $('.user-id', form), $confirmUserIdField = $('.confirm-user-id', form), $confirmPasswordField = $('.confirm-password', form), $emailAddressField = $('.email-address', form), emailPattern = /^\s*[\w\-\+_]+(?:\.[\w\-\+_]+)*\@[\w\-\+_]+\.[\w\-\+_]+(?:\.[\w\-\+_]+)*\s*$/, validForm = false, $submitButton = $('#js-submit', form);

            dfs.crd.reg.shared.util.formValidation.setCurrentForm(form);
			dfs.crd.reg.shared.util.formValidation.validatePassword($passwordField, $confirmPasswordField);            
			dfs.crd.reg.shared.util.formValidation.validateUserId($userIdField, $confirmUserIdField);
			dfs.crd.reg.shared.util.formValidation.validateEmail($emailAddressField);
			dfs.crd.reg.shared.util.formValidation.passwordHandlers($passwordField, $confirmPasswordField);
            dfs.crd.reg.shared.util.formValidation.confirmPasswordHandlers($passwordField, $confirmPasswordField);
			dfs.crd.reg.shared.util.formValidation.userIdHandlers($userIdField, $confirmUserIdField);
			dfs.crd.reg.shared.util.formValidation.confirmUserIdHandlers($userIdField, $confirmUserIdField);
			dfs.crd.reg.shared.util.formValidation.emailHandlers($emailAddressField);
		}();
	}//==>validateForm()
};
//==> forgotBoth.step2

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
	
	validateForm : function(form) {

		var init = function() {
		    var $creditCardField = $('.credit-card-account-number', form), $accountCenterPassword = $('.account-center-password', form), $submitButton = $('#js-submit', form);

            dfs.crd.reg.shared.util.formValidation.setCurrentForm(form);
			dfs.crd.reg.shared.util.formValidation.validateCcNum($creditCardField);
			dfs.crd.reg.shared.util.formValidation.validateAccountCenterPassword($accountCenterPassword );
			dfs.crd.reg.shared.util.formValidation.ccNumHandlers($creditCardField);
			dfs.crd.reg.shared.util.formValidation.accountCenterPasswordHandlers($accountCenterPassword);
			//dfs.crd.reg.shared.util.formValidation.onFormSubmit(form);
		}();
	}
	//==> validateForm
}

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
