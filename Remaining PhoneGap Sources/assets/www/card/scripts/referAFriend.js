dfs.crd.raf = dfs.crd.raf || {};
dfs.crd.raf.emailShared = false;
$(document).bind("pagechange", function () {	
	$('.prv_msg').html($(document).jqmData("subject"));
	$('.msg').html($(document).jqmData("msg"));
	$('.cont-editable-prv').html($(document).jqmData("mailtext"));
	$('.cont-editable-prv-from').html($(document).jqmData("mailtext-from"));
});

$("#share-mail-app").live("pageshow", function () {	
	if( typeof $(document).jqmData('redirectToPreview') == "undefined" || $(document).jqmData('redirectToPreview') == false) { 
		$(document).jqmData('redirectToPreview', false);
	}

	if($(document).jqmData('redirectToPreview') == true    ) { 
		
		if ($(document).jqmData('mailtext') != null || $.trim($(document).jqmData('mailtext')).length > 0) {
			$(".share-prv").show();
			$(".share-send-email").removeClass('ui-disabled');	
		} else {
			$(document).jqmData('redirectToPreview', false);
			$(".share-prv").hide();
			$(".share-send-email").addClass('ui-disabled');	
		}
		if($("#err:visible").length) { 
			$(document).jqmData('redirectToPreview', false);
			$(".share-prv").hide();
			$(".share-send-email").addClass('ui-disabled');	
		
		}
	} else { 
		$(".share-prv").hide();
		$(".share-send-email").addClass('ui-disabled');	
	}
});

$("#share-mail-app,#share-mail-app-prv").live("pageshow", function (event) {	
	var currentTarget = event.currentTarget.id;
	$('.txtarea').watermark('<i>Include a personal message to your friend.<br/> You know them better than anyone else.</i>', {fallback: false});

// check whether the user's email is already srored in database
  isSet = true;
  if(isSet)
  {
	  $(".email-inputs-from").attr("disabled",true);
  }
 
var x = $(".email-inputs-from").attr("disabled") 


$(".share-edit-email").click(function(){
	  
//	  $(document).jqmData('subject', $(".prv_msg").html());
//		$(document).jqmData('msg',  $(".msg").html());	
//		$(document).jqmData('mailtext',  $(".cont-editable-prv").html());	
});


$(".share-prv").click(function(){
	
		$(document).jqmData('redirectToPreview', true);
		$(document).jqmData('subject', $(".edit-it").html());
		$(document).jqmData('msg',  $(".txtarea").val());	
		$(document).jqmData('mailtext',  $(".email-inputs").val());	

	  if(isSet)
	  {
	  }	
	  else
	  {
	    $(document).jqmData('mailtext-from',  $(".email-inputs-from").val());	
	 				
	  }
	});


/* Validate email address code starts */
/*var sEmail = $.trim($('.email-inputs').val()); 
var fEmail = $.trim($('.email-inputs-from').val()); 
    if ($.trim(sEmail).length == 0 ||$.trim(fEmail).length == 0) {
		$("#share-mail-web .share-send-email,#share-mail-app .share-send-email").addClass('ui-disabled');
		$(".share-prv").hide();
	}
	else {function validateEmail(sEmail) {
    //var regex=/\b[A-Z0-9._%+-]+@[A-Z0-9.-]+\.[A-Z]{2,4}\b/i;
    	var regex=/^([A-Za-z0-9_\-\.])+\@([A-Za-z0-9_\-\.])+\.([A-Za-z]{2,4})$/;
	    return (regex.test(sEmail)) ? true : false;
	}
	function validateMultipleEmailsCommaSeparated(value) {

    	var result = value.split(",");
	    for(var i = 0;i < result.length;i++)
    		if(!validateEmail($.trim(result[i]))) 
            return false;               
	    return true;
	}
}*/

function validateEmail(sEmail) {
    //var regex=/\b[A-Z0-9._%+-]+@[A-Z0-9.-]+\.[A-Z]{2,4}\b/i;
    	var regex=/^([A-Za-z0-9_\-\.])+\@([A-Za-z0-9_\-])+\.([A-Za-z]{2,4})$/;
	    return (regex.test(sEmail)) ? true : false;
	}
function validateMultipleEmailsCommaSeparated(value) {
		var result = $.trim(value).split(" ");
	    for(var i=0;i<result.length;i++) {

			if($.trim(result[i]) != "") {
	    		if(!validateEmail($.trim(result[i]))) {
           			 return false;               
				}
			} else {
				return false;
			}
	  	 }
	    return true;
	}

/*
	var sEmail = $.trim($('.email-inputs').val()); 
    if ($.trim(sEmail).length == 0) {
		$("#share-mail-web .share-send-email,#share-mail-app .share-send-email").addClass('ui-disabled');
		$(".share-prv").hide();
	}*/
if(x!="disabled")
{
 $(".email-inputs").focus(function() {
	 	var sEmail = $.trim($('.email-inputs').val());  /* Added to avoid sEmail undefined error */
		$(".share-send-email").addClass('ui-disabled');
		$(".share-prv").hide();
         $("#err").hide();$("#err2").hide();
		$(".ui-content-internalpages .white-box-div .share-box .to #to-clr").css('color','#a4a4a4');
		 
		 if ($.trim(sEmail).length == 0) {
			$(".share-send-email").addClass('ui-disabled');
		   $(".share-prv").hide();
			$(".ui-content-internalpages .white-box-div .share-box .to #to-clr").css('color','#a4a4a4');
			
  		}else if (validateMultipleEmailsCommaSeparated(sEmail)) {
           	$("#err").hide();$("#err2").hide();
			$(".share-send-email").removeClass('ui-disabled');
		  //	$(".share-prv").show();	
        }    
    });


 $(".email-inputs").blur(function(e) {


		var sEmail = $('.email-inputs').val();
		var fEmail = $('.email-inputs-from').val();
       	if ($.trim(sEmail).length == 0) {
			$(".share-send-email").addClass('ui-disabled');
		   	$(".share-prv").hide();
          	$(".ui-content-internalpages .white-box-div .share-box .to #to-clr").css('color','#F00');
          	$("#err2").show();
		   	$("#err").hide();
			}
		else if (validateMultipleEmailsCommaSeparated(sEmail) && $.trim(fEmail).length != 0 && validateEmail(fEmail)) {
           	$("#err").hide();
			$("#err2").hide();
			$(".share-send-email").removeClass('ui-disabled');
		  	$(".share-prv").show();	
        } else if (!validateMultipleEmailsCommaSeparated(sEmail)) {
            $("#err").show();
			$(".ui-content-internalpages .white-box-div .share-box .to #to-clr").css('color','#F00');
            $(".share-send-email").addClass('ui-disabled');
        	} 
	 }); 


	 $(".email-inputs-from").focus(function() {
		var sEmail = $('.email-inputs-from').val(); //added on 08.21.2012 to avoid undefined error 
		 $(".share-send-email").addClass('ui-disabled');
		$(".share-prv").hide();
		 $(".ui-content-internalpages .white-box-div .share-box .to #frm-clr").css('color','#a4a4a4');
		 if ($.trim(sEmail).length == 0) {
			$(".share-send-email").addClass('ui-disabled');
		   $(".share-prv").hide();
			$(".ui-content-internalpages .white-box-div .share-box .to #frm-clr").css('color','#a4a4a4');
  		}else if (validateMultipleEmailsCommaSeparated(sEmail)) {
			$(".share-send-email").removeClass('ui-disabled');
		  	$(".share-prv").show();	
        } 
    });
	
	$(".email-inputs-from").blur(function(e) {

		var sEmail = $('.email-inputs').val();
		var fEmail = $('.email-inputs-from').val();
       	if ($.trim(sEmail).length == 0) {
			$(".share-send-email").addClass('ui-disabled');
		   $(".share-prv").hide();
			$(".ui-content-internalpages .white-box-div .share-box .to #frm-clr").css('color','#F00');
			}
		else if (validateEmail(fEmail) && $.trim(sEmail).length != 0 && validateMultipleEmailsCommaSeparated(sEmail) ) {
			$(".share-send-email").removeClass('ui-disabled');
		  	$(".share-prv").show();	
        } else if (!validateEmail(fEmail)) {
            
			$(".ui-content-internalpages .white-box-div .share-box .to #frm-clr").css('color','#F00');
           $(".share-send-email").addClass('ui-disabled');
		   $(".share-prv").hide();
		   	$(".share-send-email").addClass('ui-disabled');
        	} 
	}); 
 }



if(x=="disabled")
{
 $(".email-inputs").focus(function() {
	 	var sEmail = $.trim($('.email-inputs').val());
	 	$(".share-send-email").addClass('ui-disabled');
		$(".share-prv").hide();
         $("#err").hide();$("#err2").hide();
		 $(".ui-content-internalpages .white-box-div .share-box .to #to-clr").css('color','#a4a4a4');
		 
		 if ($.trim(sEmail).length == 0) {
			$(".share-send-email").addClass('ui-disabled');
		   $(".share-prv").hide();
			$(".ui-content-internalpages .white-box-div .share-box .to #to-clr").css('color','#a4a4a4');
  		}else if (validateMultipleEmailsCommaSeparated(sEmail)) {
           	$("#err").hide();$("#err2").hide();
			$(".share-send-email").removeClass('ui-disabled');
		  	$(".share-prv").show();	
        } 
		
		if($(document).jqmData('redirectToPreview') == true) {
			$(".share-prv").hide();
			$(".share-send-email").addClass('ui-disabled');
        } 
    });


 $(".email-inputs").blur(function(e) {
		$(".share-send-email").addClass('ui-disabled');
		$(".share-prv").hide();
        $("#err").hide();$("#err2").hide();
		var sEmail = $('.email-inputs').val();
		
       	if ($.trim(sEmail).length == 0) {
          	$(".ui-content-internalpages .white-box-div .share-box .to #to-clr").css('color','#F00');
			$("#err2").show();
			$(".share-send-email").addClass('ui-disabled');
		   $(".share-prv").hide();
		   	$("#err").hide();
			}
		else if (validateMultipleEmailsCommaSeparated(sEmail)) {
           	$("#err").hide();$("#err2").hide();
			$(".share-send-email").removeClass('ui-disabled');
		  	$(".share-prv").show();	
        } else if (!validateMultipleEmailsCommaSeparated(sEmail)) {
            $("#err").show();
			$(".ui-content-internalpages .white-box-div .share-box .to #to-clr").css('color','#F00');
           
        	} 
	 }); 
 }	 
	

 
	 /* Validate email code ends */
	 
	 /* Edit subject Code starts */
	 
	$('.subj-edit').click(function(){
		$(this).hide();
		$(this).prev().hide();
		$(this).next().show();
		$(this).next().select();
	});
	
	
	$('input[name="subject"]').blur(function() {  
         if ($.trim(this.value) == ''){  
			 this.value = (this.defaultValue ? this.defaultValue : '');  
		 }
		 else{
			 $(this).prev().prev().html(this.value);
		 }
		 
		 $(this).hide();
		 $(this).prev().show();
		 $(this).prev().prev().show();
     });
	  
	  $('input[name="subject"]').keypress(function(event) {
		  if (event.keyCode == '13') {
			  if ($.trim(this.value) == ''){  
				 this.value = (this.defaultValue ? this.defaultValue : '');  
			 }
			 else
			 {
				 $(this).prev().prev().html(this.value);
			 }
			 
			 $(this).hide();
			 $(this).prev().show();
			 $(this).prev().prev().show();
		  }
	  });
	
	   /* Edit subject Code ends */
	
	/* Toggle expand code starts */
	
	$(".openit").toggle(function(){
	
		$("#plus-expand-btn").css('background-image','url(../../images/minus-expand.png)');
		$("#show-hide").show();
		
	},function(){		
		$("#plus-expand-btn").css('background-image','url(../../images/plus-expand.png)');
		$("#show-hide").hide();		
	});
	$(".subj-edit").click(function(){	
		$(".edit-it").attr('contentEditable','true');
		$(".edit-it").focus();
	});
					
	/* Toggle expand code ends */
	
	$('.share-send-email').click(function() {
		showSpinner();
		dfs.crd.raf.emailShared = true;
		var to_emails = new Array();
		var sEmail = $(document).jqmData('mailtext');
		if (sEmail == null) {
			sEmail = $(".email-inputs").val()
		}
		sEmail = $.trim(sEmail);
		if (sEmail.indexOf(" ") > -1)
			to_emails = sEmail.split(" ");
		else
			to_emails.push(sEmail);
		
		$(document).jqmData('mailtext',  to_emails);		
		setTimeout('dfs.crd.raf.postShareNavigation()',5000);
	});
	
	$('#contactListLink').click(function() {
		showAllContactsForRAF();
	
	});
	
	$('#navToRafPreviewEmailMessageAdded').click(function() {
		dfs.crd.raf.navToRafPreviewEmailMessageAdded();
	});
	$('.terms-n-condi').click(function() {
		if ("share-mail-app-prv" == currentTarget) {
			$(document).jqmData('subject', $(".prv_msg").html());		
		} else {
			console.log("email inputs== " + $(".email-inputs").val());
			if ($.trim($(".email-inputs").val()).length > 0) {
				$(document).jqmData('redirectToPreview', true);		
			}
			$(document).jqmData('subject', $(".edit-it").html());		
		}
		$(document).jqmData('msg',  $(".txtarea").val());	
		$(document).jqmData('mailtext',  $(".email-inputs").val());	

		dfs.crd.raf.navToReferralTermsAndConditions();
	});
	
});
	
	
$("#raf-status-miles,#raf-status,#raf-status-dollar").live("pageshow", function (event,ui) {	
	
  $('#close-referral-detail').click(function()
  {
	dfs.crd.raf.clearJQMDataForEmail();
	putDataToCache('REFER_A_FRIEND_SPLASH_BUTTON_EVENT',null);		
  	$('.referral-detail').slideUp();
  });
  
	var toEmails = $(document).jqmData('mailtext');
	dfs.crd.raf.clearJQMDataForEmail();
	var valueMap=[];
	var htmlText = '';
	
	if (toEmails != null && toEmails.length > 0 && dfs.crd.raf.emailShared == true) {
		dfs.crd.raf.emailShared = false;
		if (toEmails.length > 5) {
			valueMap["emailFriendsParm"] = toEmails.length + " friends ";
		} else {
			var holdEmails ='';
			var commaCounter = 0;
			while(toEmails.length > 0) {
				holdEmails += toEmails.shift();
				if (toEmails.length >= 2) {
					holdEmails += ", "
					commaCounter++;
				} else if (toEmails.length == 1) {
					if (commaCounter > 0) {
						holdEmails += "," ;
					} 
					holdEmails += " and " ;					
				} 
			}
			valueMap["emailFriendsParm"] = holdEmails;
		}
		var jsonText =getPageContentMin("referAFriend","EMAIL_FRIENDS_PARM",incentiveTypeCode);
		var jsonTextWithValue='';
		if (!isVarEmpty(jsonText)) {
			jsonTextWithValue=parseContent(jsonText,valueMap);
			htmlText += jsonTextWithValue;			
		} else {
			htmlText += jsonText;
		}

		$('.referral-detail > span').html(htmlText);
	
		$('.referral-detail').slideDown();
	} else {
		var previousPage = $(ui.prevPage).attr("id");
		if (previousPage == "raf-dollar" || previousPage == "raf-miles") {
			var splashButtonEventID=getDataFromCache('REFER_A_FRIEND_SPLASH_BUTTON_EVENT');
			if (splashButtonEventID != null) {
				dfs.crd.raf.getConfirmationMessage(splashButtonEventID);	
			}
		}
	
	}

  $('#fbBtn,#twBtn,#lnBtn').click(function(event)
  {
      showSpinner();
	  console.log("inside the "+ event.currentTarget.id + " event");
	  dfs.crd.raf.getConfirmationMessage(event.currentTarget.id);
  });
  $('#navToRafShareWithEmail').click(function() {
 	showSpinner();
 	dfs.crd.raf.navToRafShareWithEmail();
  });
  
});

function scroll_top(){
	$.mobile.silentScroll(0);
	setTimeout(function(){$('.referral-detail').slideUp();},'180000')
}

$("#raf-status-miles,#raf-status,#raf-status-dollar").live("pagebeforeshow", function () {
	$('.referral-detail').hide();
	
});

$("#raf-dollar,#raf-miles").live("pageshow", function (event) {
	$('#fbBtn,#twBtn,#lnBtn').click(function(event)
	{
		showSpinner();
		var cacheKey = "REFER_A_FRIEND_SPLASH_BUTTON_EVENT";
		putDataToCache(cacheKey,event.currentTarget.id);
		setTimeout('dfs.crd.raf.postShareNavigation()',5000);		
	});
	$('.terms-n-condi').click(function() {
		dfs.crd.raf.navToReferralTermsAndConditions();
	});
	$('#navToRafShareWithEmail').click(function() {
		dfs.crd.raf.navToRafShareWithEmail();
	});

});

$("#share-mail-app,#share-mail-app-prv,#raf-status,#raf-dollar,#raf-status-dollar,#raf-miles,#raf-status-miles").live("pageinit", function (event) {
	console.log("inside pageinit for: " + event.currentTarget.id);
	dfs.crd.raf.getStrongMailScript(event.currentTarget.id);
});

/*
* Load functions
*
*/
function referAFriendDollarLoad() {
	console.log("inside referAFriendDollarLoad");
	try {
		var shareContent = dfs.crd.raf.getStrongMailShareContent();
		$('#raf-dollar-strongMail_share').html(shareContent);

	} catch(err) {
		console.log("referAFriendDollarLoad(): problem loading StrongMail share content " + err);
	
	}	
}
function referralStatusDollarSuccessLoad() {
	try {
		//get status data
		var statusData = dfs.crd.raf.getStrongMailStatusParms();
		if (statusData != null) {
			// populate summary at top of page
			var successCount = statusData.statuspg_api_rsp.stats.actions_by_user;
			var successDollar = numberWithCommas(successCount * 50);
			$('#count-success-dynamicfield').html(successCount);
			$('#dollar-success-dynamicfield').html(successDollar);
			dfs.crd.raf.populateStatusPages("referralStatusDollarSuccessLoad",statusData,"raf-status-dollar");
		}
	} catch(err) {
		console.log("referralStatusDollarSuccessLoad(): problem  " + err);
	}
}

function referralStatusNoReferralsLoad() {
	console.log("inside referralStatusNoReferralsLoad");
	try {
		//get status data
		var statusData = dfs.crd.raf.getStrongMailStatusParms();
		dfs.crd.raf.populateStatusPages("referralStatusNoReferralsLoad",statusData,"raf-status");
	} catch (err) {
		console.log("referralStatusNoReferralsLoad(): problem  " + err);
	}
}

function rafShareWithEmailLoad() {
	try {
		var account = dfs.crd.raf.fetchAccountData();
		console.log(account);
		var valueMap = [];
		valueMap["fromEmailAddress"] = account.primaryCardmember.emailAddress;
		
		var htmlText='';
		
		var shareContent = dfs.crd.raf.getStrongMailShareContent();
		console.log(shareContent);
		$('#share-mail-app-strongMail_share').html(shareContent);
		$('#shareVariables').attr('id','share-mail-app-shareVariables');
		htmlText += getPageContentMin("referAFriend","ULEMAIL_FORM_START",incentiveTypeCode);
		var jsonText =getPageContentMin("referAFriend","EMAIL_FORM",incentiveTypeCode);
		var jsonTextWithValue='';
		if (!isVarEmpty(jsonText)) {
			jsonTextWithValue=parseContent(jsonText,valueMap);
			htmlText += jsonTextWithValue;			
		} else {
			htmlText += jsonText;
		}
		htmlText += getPageContentMin("referAFriend","UL_END",incentiveTypeCode);
		$('#share-mail-app-emailInfo').html(htmlText);
		$('.email-inputs').val($(document).jqmData("mailtext"));
		if ($(document).jqmData("subject") != null && $.trim($(document).jqmData("subject")).length > 0) {
			$('.edit-it').html($(document).jqmData("subject"));
			$('.input-edit').val($(document).jqmData("subject"));	
		}
		$('.txtarea').val($(document).jqmData("msg"));
		$('#share-mail-app-emailInfo').trigger("create");
			

	} catch(err) {
		console.log("rafShareWithEmailLoad(): problem loading StrongMail share content " + err);
	}
}

function rafPreviewEmailMessageAddedLoad() {
	try {
		var account = dfs.crd.raf.fetchAccountData();
		console.log(account);
		var valueMap = [];
		valueMap["previewEmailFrom"] = account.primaryCardmember.emailAddress;;
		valueMap["previewEmailTo"] = $(document).jqmData('mailtext');
		var htmlText='';
		
		var shareContent = dfs.crd.raf.getStrongMailShareContent();
		$('#a').val($(document).jqmData('mailtext'));
        $('#email-input #subject').val($(document).jqmData('subject'));
		$('#share-mail-app-prv-strongMail_share').html(shareContent);
		htmlText += getPageContentMin("referAFriend","ULPRVWEMAIL_FORM_START",incentiveTypeCode);
		var jsonText =getPageContentMin("referAFriend","PREV_EMAIL_FORM",incentiveTypeCode);
		var jsonTextWithValue='';
		if (!isVarEmpty(jsonText)) {
			jsonTextWithValue=parseContent(jsonText,valueMap);
			htmlText += jsonTextWithValue;			
		} else {
			htmlText += jsonText;
		}
		htmlText += getPageContentMin("referAFriend","UL_END",incentiveTypeCode);
		$('#share-mail-app-prv-emailInfo').html(htmlText);
		$('.prv_msg').html($(document).jqmData("subject"));
		$('#share-mail-app-prv-emailInfo').trigger("create");
			
	
	} catch (err) {
		console.log("rafPreviewEmailMessageAddedLoad(): problem loading StrongMail share content " + err);
	}

}
function referAFriendMilesLoad() {
	try {
		console.log("inside referAFriendMilesLoad()");
		var shareContent = dfs.crd.raf.getStrongMailShareContent();
		$('#raf-miles-strongMail_share').html(shareContent);

	} catch(err) {
		console.log("referAFriendMilesLoad(): problem loading StrongMail share content " + err);
		
	}
}

function referralStatusMilesSuccessLoad() {
	try {
		//get status data
		var statusData = dfs.crd.raf.getStrongMailStatusParms();
		if (statusData != null) {
			// populate summary at top of page
			var successCount = statusData.statuspg_api_rsp.stats.actions_by_user;
			var successMiles = numberWithCommas(successCount * 5000);
			$('#count-success-dynamicfield').html(successCount);
			$('#miles-success-dynamicfield').html(successMiles + "<span class='miles-size'>miles</span>");
		
			dfs.crd.raf.populateStatusPages("referralStatusMilesSuccessLoad",statusData,"raf-status-miles");
		}
	} catch(err) {
		console.log("referralStatusMilesSuccessLoad(): problem  " + err);
	}
}

function referralTermsAndConditionsLoad() {
	try {
		var termsAndCond = dfs.crd.raf.getTermsAndConditions();
		console.log(termsAndCond);	
		$('#pcc-static').html(termsAndCond.termsAndConditionsHTML);
		$('#pcc-static').trigger("create");	
	} catch(err) {
		console.log("referralTermsAndConditionsLoad(): problem loading Terms and Conditions " + err);
	}
}

function redeemFaqsLoad() {
	try {
		var jsonText = '';
	  	jsonText =getPageContentMin("referAFriend","REFER_WHAT_FAQ",incentiveTypeCode);
		$('#refer-what').html(jsonText);
	  	jsonText =getPageContentMin("referAFriend","REFER_INCLUDE_FAQ",incentiveTypeCode);
		$('#refer-include').html(jsonText);
	  	jsonText =getPageContentMin("referAFriend","REFER_TIME_HEADER_FAQ",incentiveTypeCode);
		$('#refer-time-header').html(jsonText);
	  	jsonText =getPageContentMin("referAFriend","REFER_TIME_FAQ",incentiveTypeCode);
		$('#refer-time').html(jsonText);
	  	jsonText =getPageContentMin("referAFriend","REFER_MAX_EARN_FAQ",incentiveTypeCode);
		$('#refer-max-earn').html(jsonText);
	  	jsonText =getPageContentMin("referAFriend","REFER_CONFRM_HEADER_FAQ",incentiveTypeCode);
		$('#refer-confirm-header').html(jsonText);
		
		if (fromPageName == 'referAFriendDollar' ||
			fromPageName == 'referAFriendMiles' ||
			fromPageName == 'referralStatusDollarSuccess' ||
			fromPageName == 'referralStatusNoReferrals' ||
			fromPageName == 'referAFriendMiles' ||
			fromPageName == 'referralStatusMilesSuccess') {
			$('#rdmp-header').remove();
			$('#rdmp-list').remove();	
		}
		
		if (MILES_INCENTIVE_TYPE == incentiveTypeCode) {
			$('#faq-title').html("<strong>Miles</strong> FAQs");
		}
		$('#faq-content').trigger("create");
		
	} catch (err) {
		console.log("redeemFaqsLoad(): problem loading FAQs " + err);
	}
}
/*
* Navigation functions
*
*/
dfs.crd.raf.navToRafShareWithEmail = function() {
	navigation("../referAFriend/rafShareWithEmail")

}

dfs.crd.raf.navToReferralStatusDollarSuccess = function() {
	navigation("../referAFriend/referralStatusDollarSuccess")
	
}

dfs.crd.raf.navToReferralStatusMilesSuccess = function() {
	navigation("../referAFriend/referralStatusMilesSuccess")	

}

dfs.crd.raf.navToReferralStatusNoReferrals = function() {
	navigation("../referAFriend/referralStatusNoReferrals")

}

dfs.crd.raf.navToRafPreviewEmailMessageAdded = function() {
	navigation("../referAFriend/rafPreviewEmailMessageAdded")

}

dfs.crd.raf.navToReferralTermsAndConditions = function() {
	navigation("../referAFriend/referralTermsAndConditions")
}

dfs.crd.raf.postShareNavigation = function() {
	var result = dfs.crd.raf.referralStatus();

	if (result != null) {
		if ("SHARES" == result) {
			if (CBB_INCENTIVE_TYPE == incentiveTypeCode) {
				dfs.crd.raf.navToReferralStatusDollarSuccess();
			} else {
				dfs.crd.raf.navToReferralStatusMilesSuccess();
			}
		} else if ("INVITES_ONLY" == result){
				dfs.crd.raf.navToReferralStatusNoReferrals();		
		} else {
			dfs.crd.raf.clearJQMDataForEmail();
			if (CBB_INCENTIVE_TYPE == incentiveTypeCode) {
				dfs.crd.raf.navToReferAFriendDollar();
			} else {
				dfs.crd.raf.navToReferAFriendMiles();
			}	
		}
	} 
}

dfs.crd.raf.navFromCBBLanding = function() {
	try {
		dfs.crd.raf.cbbReferAFriend();
	} catch(err) {
		console.log("navFromCBBLanding(): problem obtaining StrongMail status info " + err);
		
	}
}

dfs.crd.raf.navFromMilesLanding = function() {
	try {
		dfs.crd.raf.milesReferAFriend();
	} catch(err) {
		console.log("navFromMilesLanding(): problem obtaining StrongMail status info " + err);
		
	}
}

dfs.crd.raf.navToReferAFriendDollar = function() {
	navigation("../referAFriend/referAFriendDollar")
}

dfs.crd.raf.navToReferAFriendMiles = function() {
	navigation("../referAFriend/referAFriendMiles")
}
dfs.crd.raf.navToNoEmailError = function() {
	navigation("../referAFriend/rafNoEmailError");
}

/*
*	Utility functions and vars
*/
dfs.crd.raf.referralStatus = function() {
	var strongMailStatusData = dfs.crd.raf.getStrongMailStatusParms();
	if (strongMailStatusData != null && strongMailStatusData != 501) {
		if(strongMailStatusData.statuspg_api_rsp.stats.rewardtotal_user > 0) {
			return "SHARES";
		} else if ( strongMailStatusData.statuspg_api_rsp.stats.rewardtotal_user == 0 && strongMailStatusData.statuspg_api_rsp.stats.invites_by_user > 0) {
			return "INVITES_ONLY";
		} 
	} else if (strongMailStatusData == 501){
		return "SPLASH";
	}
	return null;	
}

dfs.crd.raf.populateStatusPages = function(methodName,statusData,divId) {
	try {
		var valueMap=[];
		var jsonByChannelShareType='';
		var htmlText='';
		var channelArray = [];
		var tempDivId = "#" + divId + "-";
		//get input and script tags to facilitate sharing
		var shareContent = dfs.crd.raf.getStrongMailShareContent();
		$(tempDivId + 'strongMail_share').html(shareContent);
		console.log(statusData);
		if (statusData != null && statusData != 501) {
		//populate number of shares and successes from status data
			if (("YES" == (statusData.statuspg_api_rsp.sharing.email_share_details.activity).toUpperCase()) && 
				dfs.crd.raf.haveInvitedEligibleEmail(statusData.statuspg_api_rsp.sharing.email_share_details)) {
				valueMap["emailShares"] = statusData.statuspg_api_rsp.sharing.email_share_details.invited_eligible.number;
				if (statusData.statuspg_api_rsp.sharing.email.actions > 0) {
					valueMap["emailSuccess"] = statusData.statuspg_api_rsp.sharing.email.actions;
					if (valueMap["emailSuccess"] < 2) {
						if (valueMap["emailShares"] < 2) {
							channelArray.push("EMAIL_SUCCESS_SHARE");
						} else {
							channelArray.push("EMAIL_SUCCESS_SHARES");
						}
					} else {
						channelArray.push("EMAIL_SUCCESSES");
					}
				} else {
					if (valueMap["emailShares"] == 1) {
						channelArray.push("EMAIL_SHARE");
					} else {
						channelArray.push("EMAIL_SHARES");				
					}
				}
			} else {
				channelArray.push("EMAIL_NEITHER");
			}
			if ("Y" == (statusData.statuspg_api_rsp.sharing.facebook.activity).toUpperCase()){
				valueMap["facebookShares"] = statusData.statuspg_api_rsp.sharing.facebook.clicks;
				if (statusData.statuspg_api_rsp.sharing.facebook.actions > 0) {
					valueMap["facebookSuccess"] = statusData.statuspg_api_rsp.sharing.facebook.actions;
					if (valueMap["facebookSuccess"] < 2) {
						if (valueMap["facebookShares"] < 2) {
							channelArray.push("FACEBOOK_SUCCESS_SHARE");							
						} else {
							channelArray.push("FACEBOOK_SUCCESS_SHARES");
						}
					} else {
						channelArray.push("FACEBOOK_SUCCESSES");
					}
				} else {
					if (valueMap["facebookShares"] == 1) {
						channelArray.push("FACEBOOK_SHARE");
					} else {
						channelArray.push("FACEBOOK_SHARES");
					}
				}
			} else {
				channelArray.push("FACEBOOK_NEITHER");
			}
			if ("Y" == (statusData.statuspg_api_rsp.sharing.twitter.activity).toUpperCase()){
				valueMap["twitterShares"] = statusData.statuspg_api_rsp.sharing.twitter.clicks;
				if (statusData.statuspg_api_rsp.sharing.twitter.actions > 0) {
					valueMap["twitterSuccess"] = statusData.statuspg_api_rsp.sharing.twitter.actions;
					if (valueMap["twitterSuccess"] < 2) {
						if (valueMap["twitterShares"] < 2) {
							channelArray.push("TWITTER_SUCCESS_SHARE");							
						} else {
							channelArray.push("TWITTER_SUCCESS_SHARES");					
						}
					} else {
						channelArray.push("TWITTER_SUCCESSES");				
					}
				} else {
					if (valueMap["twitterShares"] == 1) {
						channelArray.push("TWITTER_SHARE");			
					} else {
						channelArray.push("TWITTER_SHARES");							
					}
				}
			} else {
				channelArray.push("TWITTER_NEITHER");
			}
			if ("Y" == (statusData.statuspg_api_rsp.sharing.linked_in.activity).toUpperCase()){
				valueMap["linkdinShares"] = statusData.statuspg_api_rsp.sharing.linked_in.clicks;
				if (statusData.statuspg_api_rsp.sharing.linked_in.actions > 0) {
					valueMap["linkdinSuccess"] = statusData.statuspg_api_rsp.sharing.linked_in.actions;
					if (valueMap["linkdinSuccess"] < 2) {
						if (valueMap["linkdinShares"] < 2) {
							channelArray.push("LINKEDIN_SUCCESS_SHARE");				
						} else {
							channelArray.push("LINKEDIN_SUCCESS_SHARES");					
						}
					} else {
						channelArray.push("LINKEDIN_SUCCESSES");
					}
				} else {
					if (valueMap["linkdinShares"] == 1) {
						channelArray.push("LINKEDIN_SHARE");
					} else {
						channelArray.push("LINKEDIN_SHARES");				
					}
				}
			} else {
				channelArray.push("LINKEDIN_NEITHER");
			}
		} else {
			channelArray.push("EMAIL_NEITHER");
			channelArray.push("FACEBOOK_NEITHER");
			channelArray.push("TWITTER_NEITHER");
			channelArray.push("LINKEDIN_NEITHER");
		}
		htmlText += getPageContentMin("referAFriend","ULSTATUS_START",incentiveTypeCode);
		while (channelArray.length > 0) {
			var jsonText =getPageContentMin("referAFriend",channelArray.shift(),incentiveTypeCode);
			var jsonTextWithValue='';
			if (!isVarEmpty(jsonText)) {
				jsonTextWithValue=parseContent(jsonText,valueMap);
				htmlText += jsonTextWithValue;			
			} else {
				htmlText += jsonText;
			}
		}
		htmlText += getPageContentMin("referAFriend","UL_END",incentiveTypeCode);
		console.log(htmlText);
		$('#channelStatus_list').html(htmlText);
		$('#channelStatus_list').trigger("create");	
		
	} catch(err) {
		console.log(methodName + "(): problem loading StrongMail share content " + err);
		
	}
}
dfs.crd.raf.getConfirmationMessage = function(eventCurrentTargetID) {
  	var strongMailStatusData = dfs.crd.raf.getStrongMailStatusParms();
  	if (strongMailStatusData != null) {
		var myDivId = eventCurrentTargetID;
		var jsonText = '';
		var writeHtml = true;
	  	if (strongMailStatusData != null) {
			if (myDivId.indexOf("fbBtn") > -1) {
		  	  	jsonText =getPageContentMin("referAFriend","FACEBOOK_CONFRM",incentiveTypeCode);
			} else if (myDivId.indexOf("twBtn") > -1) {
	  				jsonText =getPageContentMin("referAFriend","TWITTER_CONFRM",incentiveTypeCode);
			} else if (myDivId.indexOf("lnBtn") > -1) {
				jsonText =getPageContentMin("referAFriend","LINKDIN_CONFRM",incentiveTypeCode);
			} else {
				writeHtml = false;
			}
		  	hideSpinner();
			if (writeHtml) {
				$('.referral-detail > span').html(jsonText);
				setTimeout("$('.referral-detail').slideDown();",5000);
				scroll_top();
			}
		}
	}
}

dfs.crd.raf.cbbReferAFriend = function(){
	showSpinner();
 	if(!dfs.crd.raf.hasEmailAddress()) {
 		console.log("navToEmailAddressMissing");
 		dfs.crd.raf.navToNoEmailError();
 	} else {
		var result = dfs.crd.raf.referralStatus();
		dfs.crd.raf.clearJQMDataForEmail();
		if (result != null) {
			if ("SHARES" == result) {
				dfs.crd.raf.navToReferralStatusDollarSuccess();
			} else if ("INVITES_ONLY" == result){
				dfs.crd.raf.navToReferralStatusNoReferrals();
			} else {
				dfs.crd.raf.navToReferAFriendDollar();
			}
		}
	}
}

dfs.crd.raf.milesReferAFriend = function(){
 	showSpinner();
 	if(!dfs.crd.raf.hasEmailAddress()) {
 		console.log("navToEmailAddressMissing");
 		dfs.crd.raf.navToNoEmailError();
 	} else {
		var result = dfs.crd.raf.referralStatus();
		dfs.crd.raf.clearJQMDataForEmail();
		if (result != null) {
			if ("SHARES" == result) {
				dfs.crd.raf.navToReferralStatusMilesSuccess();
			} else if ("INVITES_ONLY" == result){
				dfs.crd.raf.navToReferralStatusNoReferrals();
			} else {
				dfs.crd.raf.navToReferAFriendMiles();
			}
		}
 	}
}

dfs.crd.raf.sortObject = function(o) {
    var sorted = {},
    key, a = [];

    for (key in o) {
        if (o.hasOwnProperty(key)) {
                a.push(key);
        }
    }

    a.sort();

    for (key = 0; key < a.length; key++) {
        sorted[a[key]] = o[a[key]];
    }
    return sorted;
}

dfs.crd.raf.hasEmailAddress = function() {
	var account = dfs.crd.raf.fetchAccountData();
	console.log(account);
	var emailAddress = account.primaryCardmember.emailAddress;
	if (emailAddress != null && $.trim(emailAddress).length > 0) {
		return true;
	}
	return false;
}

dfs.crd.raf.invokeStrongMailScript = function(aDivId) {
	console.log("inside invokeStrongMailScript(aDivId)");
	var customWidgetParm = new Object();
	customWidgetParm.shareVariables = $("#shareVariables").val();
	customWidgetParm.baseContainer = aDivId +"-customWidgetBaseContainer";
	initCustomWidget(customWidgetParm);

}

dfs.crd.raf.fetchAccountData = function() {
	var account = getDataFromCache('ACHOME');
	if (jQuery.isEmptyObject(account)) {
		console.log("ACHOME cache is empty");
		account = dfs.crd.profile.getCardHomeData();
	}
	return account;
}

dfs.crd.raf.clearJQMDataForEmail = function() {
	$(document).jqmData('mailtext',null);
	$(document).jqmData('subject',null);
	$(document).jqmData('msg',null);

}

dfs.crd.raf.haveInvitedEligibleEmail = function(emailShareDetails) {
	var haveInvitedEligible = false;
	$.each(emailShareDetails, function(key, value) { 
		if ("invited_eligible" == key) {
			haveInvitedEligible = true;
		}
	});
	return haveInvitedEligible;
}

/*
* Ajax call functions
*
*/
dfs.crd.raf.getStrongMailShareContent = function() {
	try {
		console.log("inside getStrongMailShareData()");
		var cacheKey="STRONGMAIL_SHARE_CONTENT";
		var newDate = new Date();
		var REWARDSJSONURL = RESTURL+"rewards/v2/referAFriendShare?"+newDate+"&trafficSource="+trafficSource;
		var strongMailShareContent = getDataFromCache(cacheKey);;
		if (jQuery.isEmptyObject(strongMailShareContent)) {
				console.log("strongMailShareContent is empty");
			$.ajax({
				type : "GET",
				url : REWARDSJSONURL,
				async : false,
				dataType : 'json',
				headers:prepareGetHeader(),
				timeout:5000,
				success : function(responseData, status, jqXHR) {
					if (jqXHR.status != 200) {
						cpEvent.preventDefault();
						var code=getResponseStatusCode(jqXHR);
						errorHandler(code,'',CBB_INCENTIVE_TYPE == incentiveTypeCode ? 'referAFriendShareErrorCBB':'referAFriendShareErrorMI2');
					} else {
						strongMailShareContent = responseData.inputTags;
						console.log(strongMailShareContent);
						putDataToCache(cacheKey,strongMailShareContent);
					}
				},
				error : function(jqXHR, textStatus, errorThrown) {
                   hideSpinner();
					var code=getResponseStatusCode(jqXHR);
					cpEvent.preventDefault();
					errorHandler(code,'',CBB_INCENTIVE_TYPE == incentiveTypeCode ? 'referAFriendShareErrorCBB':'referAFriendShareErrorMI2');
				}
			});
		}else{
		}
		return strongMailShareContent;
	}catch(err){
		showSysException(err);
	}
}


dfs.crd.raf.getStrongMailStatusParms = function() {
	try {
		console.log("inside getStrongMailStatusParms()");
		var newDate = new Date();
		var cacheKey="STRONGMAIL_STATUSAPI_PARMS";
		var REWARDSJSONURL = RESTURL+"rewards/v2/referAFriendStatus?"+newDate;
		var strongMailStatusParms=getDataFromCache(cacheKey);
		if (jQuery.isEmptyObject(strongMailStatusParms)) {
				console.log("getStrongMailStatusParms is empty");
			$.ajax({
				type : "GET",
				url : REWARDSJSONURL,
				async : false,
				dataType : 'json',
				headers:prepareGetHeader(),
				timeout:5000,
				success : function(responseData, status, jqXHR) {
					if (jqXHR.status != 200) {
						cpEvent.preventDefault();
						var code=getResponseStatusCode(jqXHR);
						errorHandler(code,'',CBB_INCENTIVE_TYPE == incentiveTypeCode ? 'referAFriendStatusParmsErrorCBB':'referAFriendStatusParmsErrorMI2');
					} else {
						strongMailStatusParms = responseData;
						strongMailStatusParms = dfs.crd.raf.sortObject(strongMailStatusParms);
						console.log(strongMailStatusParms);
						putDataToCache(cacheKey,strongMailStatusParms);
					}
				},
				error : function(jqXHR, textStatus, errorThrown) {
                   hideSpinner();
					var code=getResponseStatusCode(jqXHR);
					console.log("inside referAFriendStatusParms error");
					cpEvent.preventDefault();
					switch (code) {
						case "1629":
							errorHandler('REFER_A_FRIEND_ACCT_INELIGIBLE','','REFER_A_FRIEND_ACCT_INELIGIBLE')
							break
						default:
							errorHandler(code,'',CBB_INCENTIVE_TYPE == incentiveTypeCode ? 'referAFriendStatusParmsErrorCBB':'referAFriendStatusParmsErrorMI2')
							break
					}
				}
			});
		}else{
		}
		return dfs.crd.raf.callStrongMailStatusAPI(strongMailStatusParms);
	}catch(err){
		showSysException(err);
	}
}

dfs.crd.raf.callStrongMailStatusAPI = function(strongMailStatusParms) {
	try {
		console.log("inside callStrongMailStatusAPI()");
		if (strongMailStatusParms == null) {
			return null;
		}
		var STRONGMAIL_STATUS_JSONURL = STRONG_MAIL_STATUS_API_URL;
		var strongMailStatusResponse=null;
		var holdForMD5 = strongMailStatusParms.secret_key;
		$.each(strongMailStatusParms, function(key, value) { 
  			if (key.indexOf("secret_key") < 0) {
  				holdForMD5 += (key + value);
  			}
		});
		//do MD5 hashing here...
		console.log(holdForMD5);
		var md5Hash = CryptoJS.MD5(holdForMD5);
		console.log(md5Hash.toString());
		//build URL
		STRONGMAIL_STATUS_JSONURL += ("api_sig=" + md5Hash + "&");
		$.each(strongMailStatusParms, function(key, value) { 
  			if (key.indexOf("secret_key") < 0) {
  				STRONGMAIL_STATUS_JSONURL += (key + "=" + value + "&");
  			}
		});
		STRONGMAIL_STATUS_JSONURL = STRONGMAIL_STATUS_JSONURL.slice(0,-1);

//		STRONGMAIL_STATUS_JSONURL = "http://www.popularmedia.net/status_page.js?api_sig=fb6a0bd8465fb4fd5c7833e3e0f3f5ff&campaign_uuid=39ae999fb2dbc02459a7ede2512af659&client_uuid=5060ac00-07da-012e-e060-f4e774cfc225&user_id=danqingqueen@gmail.com";
		console.log("the status api url == " + STRONGMAIL_STATUS_JSONURL);
		
		//call StrongMail Status API									
		if (jQuery.isEmptyObject(strongMailStatusResponse)) {
			$.ajax({
				type : "GET",
				url : STRONGMAIL_STATUS_JSONURL,
				async : false,
				dataType : 'json',
				timeout : 7500,
				success : function(responseData, status, jqXHR) {
					if (jqXHR.status != 200) {
						cpEvent.preventDefault();
						var code=getResponseStatusCode(jqXHR);
						errorHandler(code,'',CBB_INCENTIVE_TYPE == incentiveTypeCode ? 'referAFriendStatusErrorCBB':'referAFriendStatusErrorMI2');
					} else {
						strongMailStatusResponse = responseData;
						console.log(strongMailStatusResponse);
					}
				},
				error : function(jqXHR, textStatus, errorThrown) {
                   hideSpinner();
					if (jqXHR.status != 501) {
						cpEvent.preventDefault();
						errorHandler(0,'',CBB_INCENTIVE_TYPE == incentiveTypeCode ? 'referAFriendStatusErrorCBB':'referAFriendStatusErrorMI2');							
					} else {
						strongMailStatusResponse = jqXHR.status;
					}
				}
			});
		}else{
		}
		return strongMailStatusResponse;
	}catch(err){
		showSysException(err);
	}
}
dfs.crd.raf.getStrongMailScript = function(aDivId) {
	console.log("inside getStrongMailScript(aDivId)");
	$.strongMailScript = function(url, options) {

  	// allow user to set any option except for dataType, cache, and url
  	options = $.extend(options || {}, {
   	 dataType: "script",
   	 timeout: 7500,
   	 url: url
  	});
	  return $.ajax(options);
	};
	$.strongMailScript("https://www.popularmedia.net/javascripts/custom_mobile_app.js").done(function(script, textStatus) {
  		console.log( "fetch status of strongmail script is: " + textStatus );
  		dfs.crd.raf.invokeStrongMailScript(aDivId);
  		hideSpinner();
	})
	.fail(function(jqxhr, settings, exception) {
  		hideSpinner();
  		var code=getResponseStatusCode(jqxhr);
  		cpEvent.preventDefault();
  		errorHandler(code,'',CBB_INCENTIVE_TYPE == incentiveTypeCode ? 'strongMailScriptErrorCBB':'strongMailScriptErrorMI2');
	});
}
dfs.crd.raf.getTermsAndConditions = function() {
	try {
		var newDate = new Date();
		console.log("inside getTermsAndConditions()");
		var REWARDSJSONURL = RESTURL+"rewards/v2/referAFriendTerms?"+newDate;
		var terms ='';

		console.log("terms is empty");
        showSpinner();
		$.ajax({
			type : "GET",
			url : REWARDSJSONURL,
			async : false,
			dataType : 'json',
			headers:prepareGetHeader(),
			success : function(responseData, status, jqXHR) {
                  hideSpinner();
				if (jqXHR.status != 200) {
					cpEvent.preventDefault();
					var code=getResponseStatusCode(jqXHR);
					errorHandler(code,'',CBB_INCENTIVE_TYPE == incentiveTypeCode ? 'referAFriendTermsErrorCBB':'referAFriendTermsErrorMI2');
				} else {
					terms = responseData;
				}
			},
			error : function(jqXHR, textStatus, errorThrown) {
                  hideSpinner();
				var code=getResponseStatusCode(jqXHR);
				cpEvent.preventDefault();
				errorHandler(code,'',CBB_INCENTIVE_TYPE == incentiveTypeCode ? 'referAFriendTermsErrorCBB':'referAFriendTermsErrorMI2');
			}
		});

		return terms;
	}catch(err){
		showSysException(err);
	}
}
