/******R13.3 js starts here******/

/* 13.3 global change starts 21-08-13 */	 
	 $(".textbox_alertspage").live("keypress",function(event) {
			var controlKeys = [8, 9, 13, 35, 36, 37, 39];
			var isControlKey = controlKeys.join(",").match(new RegExp(event.which));
			if (!event.which || (48 <= event.which && event.which <= 57) || isControlKey) { 
				if($(this).parent().hasClass('inputOnError')) {
			 		$(this).parent().removeClass("inputOnError");
				}
				return;
			} else {
				 $(this).parent().addClass("inputOnError");
				 event.preventDefault();
			}                                            
	});  
/* 13.3 global change ends 21-08-13 */

$("div[data-role='page']").live("pageshow",function(){

/* fix for white background on focus of input starts (13.3 global changes 29/07/13) */
	$("input[type='number'],input[type='tel']").bind('focus', function() {
	   //$(this).css('-webkit-user-modify', 'read-write-plaintext-only !important');
	   /*13.3 global changes 20/08/2013*/
	   if(!$(this).hasClass("textbox_alertspage")) {
		  $(this).addClass("textbox_alertspage");
	   }
	});	
	/* fix for white background on focus of input ends (13.3 global changes 29/07/13) */
	/*script for new textbox*/
	$(".textInput,.numInput").focus(function(){
		if($(this).attr('id') == 'datepicker-val'){return;}
		$(this).parent(".wrapperSpan").css({"border-bottom": "solid 1px #303030","border-left": "solid 1px #303030","border-right": "solid 1px #303030"});
		$(this).parent(".wrapperSpan").siblings(".detailName").css({"color":"#293033"});
		
		if($(this).val().toString().length == 0){
			console.log('keyup if');
			$(this).siblings(".emptyText").css({"visibility": "hidden"});	
			//$(this).parent(".wrapperSpan").css("width","100%");
		}else{
			console.log('keyup else');
				$(this).siblings(".emptyText").css({"visibility": "visible"});
				//$(this).parent(".wrapperSpan").css("width","89%");
		}
	});
	$(".emptyText").mousedown(function(e){
		var parentVar = $(this).parent(".wrapperSpan");
		e.preventDefault();
		parentVar.children(".textInput,.numInput").val("");
		$(this).siblings(".errormsg").hide();
		parentVar.children(".textInput,.numInput").focus();
		parentVar.children(".emptyText").css({"visibility": "hidden"});
		//siblingsVar.css("width","100%");
	});
	$(".textInput,.numInput").blur(function(){
		var parentVar = $(this).parent(".wrapperSpan");
		parentVar.css({"border-bottom": "solid 1px #b3b3b3","border-left": "solid 1px #b3b3b3","border-right": "solid 1px #b3b3b3"});
		parentVar.siblings(".detailName").css({"color":"#777777"});
		parentVar.children(".emptyText").css({"visibility": "hidden"});
		//parentVar.css("width","100%");
	});
	$(".textInput,.numInput").keyup(function(){
		if($(this).val().toString().length == 0){
			console.log('keyup if');
			$(this).siblings(".emptyText").css({"visibility": "hidden"});;	
			//$(this).parent(".wrapperSpan").css("width","100%");
		}else{
			console.log('keyup else');
				$(this).siblings(".emptyText").css({"visibility": "visible"});
				//$(this).parent(".wrapperSpan").css("width","89%");
		}
	});
	
	
});
/*****function to call the custom dropdown plugin functionality******/
var callDropdownFn = function(){
	$("select").msDropdown();
	$('div.dd.ddcommon.borderRadius').parent().find('a.ui-btn').remove();
	$('div.dd.ddcommon.borderRadius').parent().find('input.text.shadow.borderRadius').remove();
	/*script for custom drop down plugin*/
	$("select").change(function(){
		$(this).parent(".ddOutOfVision").siblings(".dd").css("border-bottom","1px solid #8a9499");
		$(this).parent(".ddOutOfVision").siblings(".dd").find(".arrow").css({"border-bottom": "12px solid #8a9499"});
	});
}
$("#manageBankAccDetails-pg").live("pageshow",function(){
		$("a#editAccount").bind("click",function(){
		$(document).jqmData("editFlag",true);
		navigation('../payments/manageBankAccUpdateDetails');
		dfs.crd.sct.editBankAccount();
	});
});
$("#manageBankAccEnterDetails-pg, #manageBankAccUpdateDetails-pg").live("pagebeforeshow", function(){
	var activePage=$.mobile.activePage.attr('id');
	$("#"+activePage+" select").msDropdown();
	$('div.dd.ddcommon.borderRadius').parent().find('a.ui-btn').remove();
	$('div.dd.ddcommon.borderRadius').parent().find('input.text.shadow.borderRadius').remove();
	/*script for custom drop down plugin*/
	$("#"+activePage+" select").change(function(){
		$(this).parent(".ddOutOfVision").siblings(".dd").css("border-bottom","1px solid #8a9499");
		$(this).parent(".ddOutOfVision").siblings(".dd").find(".arrow").css({"border-bottom": "12px solid #8a9499"});
	});
});
$("#manageBankAccEnterDetails-pg, #manageBankAccUpdateDetails-pg").die("pageshow").live("pageshow",function(){
try{
	var imgExpanded = true;
	var activePage=$.mobile.activePage.attr('id');
	setTimeout(function(){
		$(".helpImg").find("img").attr({
				"src" : "../../images/helpImgCollapsed.png",
				"height" : "55px",
				"width" : "296px"
			});
			imgExpanded = false;
	},2000);
	
	$(".helpImg").click(function(e){
		if(e.srcElement.nodeName == 'AREA'){
			if(e.srcElement.alt == "routing number"){
				$(this).find("img").attr({
							"src" : "../../images/helpImgCollapsedRouting.png",
							"height" : "55px",
							"width" : "296px"
				});
				$("#"+activePage+" #routing").focus();
			}else{
				$(this).find("img").attr({
							"src" : "../../images/helpImgCollapsedAccount.png",
							"height" : "55px",
							"width" : "296px"
				});
				$("#"+activePage+" #accountNumber").focus();
			}
			
		}else{
			if(imgExpanded == false){
				$(this).find("img").attr({
						"src" : "../../images/helpImgExpanded.png",
						"height" : "135px",
						"width" : "296px"
				});
				imgExpanded = true;
			}else{
				$(this).find("img").attr({
						"src" : "../../images/helpImgCollapsed.png",
						"height" : "55px",
						"width" : "296px"
				});
				imgExpanded = false;
			}
		}
	});
	
	$("#"+activePage+" #routing").focus(function(){
		$(".helpImg").find("img").attr({
						"src" : "../../images/helpImgCollapsedRouting.png",
						"height" : "55px"
				});
   if(activePage == "manageBankAccUpdateDetails-pg")
			$("#"+activePage+" #routing").val("");
	});
	$("#"+activePage+" #accountNumber").focus(function(){
		$(".helpImg").find("img").attr({
						"src" : "../../images/helpImgCollapsedAccount.png",
						"height" : "55px"
				});
		if(activePage == "manageBankAccUpdateDetails-pg")
			$("#"+activePage+" #accountNumber").val("");
	});
$("#"+activePage+" #confirmAccountNumber").focus(function(){
		if(activePage == "manageBankAccUpdateDetails-pg")
			$("#"+activePage+" #confirmAccountNumber").val("");
	});
	$("#"+activePage+" #routing, #"+activePage+" #accountNumber").blur(function(){
		$(".helpImg").find("img").attr({
						"src" : "../../images/helpImgCollapsed.png",
						"height" : "55px"
				});
	});
	
	}catch(err){
		showSysException(err);
	}
});

$("#paymentStep1-pg").live("pageshow",function(){console.log("pageshow " +  $("#bankDropDownStepOne").html())
	$(".errOther,.errOther1,.errorText1,.errorText2,.errorText3,.errorText4").hide();
	
	$(".rdoBtn").click(function(){
//		$("#otherAmt").attr("disabled", "disabled");
		$(".rdoBtn").removeAttr("checked");
		$(".rdoBtn").parents(".radioWrap").removeClass("radioSelected");
		$(this).attr("checked","checked");
		$(this).checkboxradio("refresh");
		$(this).parents(".radioWrap").addClass("radioSelected");
//		$(this).parents(".radioWrap").find(".numInput").removeAttr("disabled").focus();
	});
	
		$("#radio-choice-3").removeAttr("disabled","disabled").removeClass("ui-disabled");
		$("#radio-choice-3").parent().removeClass("ui-disabled");

	
});

$("#pendingPayments-pg").live("pageshow",function(){
	
			$(".goToPage1").click(function(){
			  $.mobile.changePage("paymentsEligible.html",{transition:"slide"});
			})
			$(".goToPage2").click(function(){
			   $.mobile.changePage("paymentsNotEligible.html",{transition:"slide"});
			})
});	
	
/*function for validating number*/
function validateNumber(inputVal){
	var regEx = /^\d+$/;
	return (regEx.test(inputVal)) ? true : false;
}

/*R13.3 global change 08/08/2013 starts*/
/*Tapping issue of Manage Payment Module fixed*/
$("ul.paymentsMenu li").live("click", function(){
	$("ul.paymentsMenu li").removeClass("curnt");
	$(this).addClass("curnt");
});
/*R13.3 global change 08/08/2013 ends*/

/******R13.3 js ends here******/