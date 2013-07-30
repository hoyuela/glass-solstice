
var orientation = '';

$("#redeem-dtlview").live('pagebeforeshow', function (event){ 
     if ((navigator.userAgent.toLowerCase().match(/android/))){  
  	$('.inc .ui-btn-inner').css('padding-top','6px !important');
 	 $('.inc .ui-btn-inner').css('padding-bottom','7px !important');
  	$('.dec .ui-btn-inner').css('padding-top','6px !important');
 	 $('.dec .ui-btn-inner').css('padding-bottom','7px !important');
    }
});

$('#browse-landing, #redemption-landing, #redeem-gift-card, #gift-card-verify, #cofirm-gift-card, #browse-all-ecertificates, #browse-all-giftcards, #browse-all-partners, #partner-category,#best-value, #redeem-dtlview, #gift-card-verify-partner, #gift-card-verify-partner, #redm-error-pg,#ecart-ios,#cofirm-gift-card-partner').live("pagebeforeshow",function(){
 
        // alert("hello")
 
       $(".ui-page-header-fixed").css("padding-top","81px");
 
});

	var Redeem  = { 
	
	 getOsVersion: function() { 
	 	
		  var agent = window.navigator.userAgent,
	      start = agent.indexOf( 'OS ' );
	
	      if( ( agent.indexOf( 'iPhone' ) > -1 || agent.indexOf( 'iPad' ) > -1 ) && start > -1 ){
	        return window.Number( agent.substr( start + 3, 3 ).replace( '_', '.' ) );
	      }else if( agent.indexOf( 'Android' ) > -1 || agent.indexOf( 'android' ) > -1 ){
			var idx = agent.indexOf('Android') + 7;
	        return parseFloat(agent.substr( idx, 4));
	    	} else {
	        return 0;
	     };
	 	
	   }
	};


/* CBB overlapping Issue: Modified/Added by MObile UI script Team :*/
	window.addEventListener("orientationchange", function() {
		  $('#pgPadding').hide();
		}, false);
	
	$("#redeem-gift-card, #gift-card-verify, #redeem-dtlview, #gift-card-verify-partner, #cofirm-gift-card-partner, #ecart-ios").live('pagebeforeshow', function (event){
		 if(globalCache['FROM_PAGE']=="giftcardTerms" || globalCache['FROM_PAGE']=="giftcardselectdesign" || globalCache['FROM_PAGE']=="redeemMerchantTerms")
		 $('#pgPadding').hide();	
	});

/*----------*/
$(document).bind("pageinit", function(){	
	/*redeem-best-value script*/
	$("#amnt20").addClass("tabs-click");
	$("#amnt20 p").addClass("navtab-pcolor");
	$(".navtabs .bv-tabs").click(function(){
		var a = $(this).attr("id");
		$(this).addClass("tabs-click").siblings().removeClass("tabs-click");
		$(this).addClass("tabs-click").find("p").addClass("navtab-pcolor");
		$(this).siblings().find("p").removeClass("navtab-pcolor");
		$('.partner-list').css('display','none');
		$("#partner-"+a).css('display','block');
		
	});
                 
                 //Click Highlight
                 $(".access_btn").click(function(){
                                        $(this).removeClass('ui-btn-up-c').addClass('ui-btn-down-c');
                                        
                                        });
	
	
});

/*-------Final overlay -------*/
function fnOverlay(customSettings){	
	var isOpen = false;	
	var defaultSettings = {"ContentHolder": "", "PageUrl": "", "marginLeft": "0", "marginRight": "0", "marginTop": "0","marginBottom": "0","paddingLeft": "0", "paddingRight": "0", "paddingTop": "0","paddingBottom": "0", "Top": "0", "Left": "0", "width":"400", "height":"500", "portraitWidth":"0", "portraitHeight":"0", "resize": true};
	$.extend(defaultSettings, customSettings);
	
	var contentHolder = defaultSettings.ContentHolder;
	var scrollOffsetHeight = $(window).scrollTop();
	var scrollOffsetLeft = $(window).scrollLeft();
	var parentPageId =  '';
	var parentPageIdObj = '';
	var ovrLayWrapperParent = $(contentHolder).parent();
	var resizeWin = false;
		
	if( $("div[data-role='page']:visible") && $("div[data-role='page']:visible").attr("id") != 'undefined'){
		parentPageId =  $("div[data-role='page']:visible").attr("id");
		parentPageIdObj = $("#"+parentPageId);
	}	
	//function to display the box
	function showOverlayBox() {
		//if box is not set to open then don't do anything
		if( isOpen == false ) return;
		var tmpHeight = 0;
		var ovrLayHeight = 0; // because iphone is behaving differently in full screen and small screen cases
		var pgHeight = $("div[data-role=page]:visible").outerHeight();
		var calculatedHeight  = 0;
		var calculatedWidth = 0;
		var ww, wh = 0;
		scrollOffsetHeight = $(window).scrollTop();
		scrollOffsetLeft = $(window).scrollLeft();
		/* 
		 * The below code is for browser only. Browser Doesn't have orientation property ; so it
		 * shows overlay popup wayward. To fix this, simply define this property.
 		 */ 
		if(window.orientation == "" || window.orientation == "undefined") {
			window.orientation = 90;
		}		
		if(defaultSettings.resize){	
						
			wh = $(window).height() - ( parseInt(defaultSettings.marginTop) + parseInt(defaultSettings.marginBottom) + parseInt(defaultSettings.paddingTop) + parseInt(defaultSettings.paddingBottom));
			ww = $("body").innerWidth() - ( parseInt(defaultSettings.marginLeft) + parseInt(defaultSettings.marginRight) + parseInt(defaultSettings.paddingLeft) + parseInt(defaultSettings.paddingRight) );		

			calculatedHeight = wh;
			calculatedWidth = ww;				
			ovrLayHeight = "100%";
			defaultSettings.Left = 0;
			defaultSettings.Top = 0; 
			$(window).scrollTop(0);
		}else{
			
			//Check for orientation		
			if( orientation==0 || orientation ==180) {
				//If user has specified portrait height/width the use that
				// else user the same width for both cases
				if( parseInt(defaultSettings.portraitHeight) >0){
					calculatedHeight = defaultSettings.portraitHeight;
				}else{
					calculatedHeight = defaultSettings.height;
				}
				if( parseInt(defaultSettings.portraitWidth) >0){
					calculatedWidth = defaultSettings.portraitWidth;
				}else{
					calculatedWidth = defaultSettings.width;
				}			
			}else if(orientation==90 || orientation ==-90){
				calculatedHeight = defaultSettings.height;
				calculatedWidth = defaultSettings.width;
			}else{
				calculatedHeight = defaultSettings.height;
				calculatedWidth = defaultSettings.width;	
			}
			
			//Calculate the final height/width
			wh = parseInt(calculatedHeight) + ( parseInt(defaultSettings.marginTop) + parseInt(defaultSettings.marginBottom) + parseInt(defaultSettings.paddingTop) + parseInt(defaultSettings.paddingBottom) );
			ww = parseInt(calculatedWidth) + ( parseInt(defaultSettings.marginLeft) + parseInt(defaultSettings.marginRight) + parseInt(defaultSettings.paddingLeft) + parseInt(defaultSettings.paddingRight) );

			//calculate the left and top				
			defaultSettings.Left = ($(window).width() - ww)/2;
			defaultSettings.Top = ( $(window).height()- wh )/2 + scrollOffsetHeight; 
			ovrLayHeight = pgHeight+"px";
		}		
		
		if(resizeWin == true)
		{
			resizeWin = false;
			$(contentHolder).css({
				marginLeft: defaultSettings.marginLeft+"px",
				marginRight: defaultSettings.marginRight+"px",
				marginTop: defaultSettings.marginTop+"px",
				marginBottom: defaultSettings.marginBottom+"px",
				paddingLeft: defaultSettings.paddingLeft+"px",
				paddingRight: defaultSettings.paddingRight+"px",
				paddingTop: defaultSettings.paddingTop+"px",
				paddingBottom: defaultSettings.paddingBottom+"px",
				top: defaultSettings.Top+"px",
				left: defaultSettings.Left+"px",
				position:'absolute',
				width: calculatedWidth+"px",
				height: calculatedHeight+"px",					
			});
		}else{
			// set the properties of the overlay box, the left and top positions
			$(contentHolder).css({
				marginLeft: defaultSettings.marginLeft+"px",
				marginRight: defaultSettings.marginRight+"px",
				marginTop: defaultSettings.marginTop+"px",
				marginBottom: defaultSettings.marginBottom+"px",
				paddingLeft: defaultSettings.paddingLeft+"px",
				paddingRight: defaultSettings.paddingRight+"px",
				paddingTop: defaultSettings.paddingTop+"px",
				paddingBottom: defaultSettings.paddingBottom+"px",
				top: defaultSettings.Top+"px",
				left: defaultSettings.Left+"px",
				position:'absolute',
				width: calculatedWidth+"px",
				height: calculatedHeight+"px",	
				"-webkit-perspective": "1000",
				"-webkit-transform-style": "preserve-3d",
				"-webkit-transform": "scale(0)",
				"-webkit-transition": "all 0.3s ease-in",
				'display':'block'		
			});
		}
		
		// set the window background for the overlay. i.e the body becomes darker
		$('.overlay').css({
			display:'block',
			width: '100%',
			height: ovrLayHeight,
			opacity: '0.7',
		});
	}
	function doOverlayOpen() {		
		var ovrLay = $("<div class='overlay'></div>");		
		var ovrLayWrapper = $(contentHolder).detach();
	
		if(isOpen == false){			
			//set status to open
			isOpen = true;
			$("body").append(ovrLayWrapper);
			if( $(".overlay").length == 0)
				$("body").append(ovrLay);
			$("body").css("overflow", "hidden");
			$(contentHolder).removeAttr("style");
			showOverlayBox();		
					
			$('.overlay').show(function(){ 			
				if( parentPageId !='' && defaultSettings.resize){
					//$('.tooltip').hide();  /* backlog changes */
					parentPageIdObj.hide();
					tmpHeight = $("html").css("min-height");
					$("html").css("min-height", "100%");
					$(contentHolder).css('-webkit-transform','scale(1)').fadeIn(100, function(){
						$("body").animate({scrollTop: 0}, 500);	
					});
				}else{
					$(contentHolder).css('-webkit-transform','scale(1)');
				}			
			});
		}		
	}
	function doOverlayClose() {	
		$("body").css("overflow", "auto");	
		if(isOpen == true){		
			//set status to closed
			isOpen = false;
			if( parentPageId !='' && defaultSettings.resize){
				$('.rd-faq-main').css("opacity", "1");			
				if(tmpHeight >0){
					$("html").css("min-height", tmpHeight+"px");
				}			
			}
			$(contentHolder).css('-webkit-transform','scale(0)').fadeOut(200, function(){
				$('.overlay').hide().remove();				
				var ovrLayWrapper = $(contentHolder).detach();
				
				parentPageIdObj.fadeIn(400, function(){					
					if( parentPageIdObj.find(ovrLayWrapperParent).length > 0){
						parentPageIdObj.find(ovrLayWrapperParent).append(ovrLayWrapper);
					}else if( parentPageIdObj.length > 0){
						parentPageIdObj.append(ovrLayWrapper);
					}else{
						$("body").append(ovrLayWrapper);
					}
					$("body").animate({scrollTop: scrollOffsetHeight}, 500);
				}).css("display", "");
			});			
		}				
	}
	// if window is resized then reposition the overlay box
	$(window).resize(function(){
		resizeWin = true;
		showOverlayBox();
	});
	$(window).bind('orientationchange', function(){		
		//$(contentHolder).css({'left': '-1000px'});
		$(contentHolder).hide(10, function(){
			$(contentHolder).fadeIn(100, function(){
				resizeWin = true;
				showOverlayBox();				
			});	
		});					
	});
			
	//Open the pop-up on click
	var pgUrl = customSettings.PageUrl;	
	if( typeof(pgUrl) !='undefined' && pgUrl != ''){
		$(contentHolder).load(pgUrl, function(){
			if( $(contentHolder).find("div[data-role='page']").length >0){
				var pgId =  $(contentHolder).find("div[data-role='page']");					
				$(contentHolder).find("*").trigger("create");
				pgId.show();
			}		
		});
	}
							
	// close it when closeLink is clicked
	$('.close-overlay').live("click", doOverlayClose );	
	
	//open overlay
	doOverlayOpen();
}
/* Final overlay*/

/*This script is used for retrive image from session for gift card flow*/

$("#redeem-gift-card").live('pageshow', function (event){
	//pop-up
	$(".open-overlay").click(function(){
		var customSettings = {"ContentHolder": "#overlay_wraper"};
		fnOverlay(customSettings);		
	});
	
	/*Allow only alphabets for first name and last name */
	$("#first-name, #last-name").bind("keydown", function(evt){
		var charCode = (evt.which) ? evt.which : event.keyCode;
		if ((charCode > 64 && charCode < 91) || (charCode > 96 && charCode < 123) || charCode == 127 || charCode == 8 || charCode == 32) {
			return true;
		}else {
			return false;
		}
	});
			
	var newImgSrc = $(document).jqmData("selectImgSrc");	
	if(typeof newImgSrc != 'undefined' && newImgSrc != null){
		$(".current-img").attr("src", newImgSrc);
	}else{
		// Set current image src in jqmdata
		$(document).jqmData("selectImgSrc", $(".current-img").attr("src"));
	}
	/*Script to change the design name*/
	var getGcName = $(document).jqmData("gcName");
	$(".classic-heading").html(getGcName);
	
	var _firstname = ($(document).jqmData("rdFname")=='undefined')?"":$(document).jqmData("rdFname"); 
	var _lastname = ($(document).jqmData("rdLname")=='undefined')?"":$(document).jqmData("rdLname");  
	var _amount = ($(document).jqmData("rdAmt")=='undefined')?"":$(document).jqmData("rdAmt");
	//Set values
	if(typeof _firstname != 'undefined' && _firstname != '')
		$("#first-name").val(_firstname);
	if(typeof _lastname != 'undefined' && _lastname != '')
		$("#last-name").val(_lastname);
	if(typeof _amount != 'undefined' && _amount != '')
		$("#select-amount").val(_amount).selectmenu("refresh");
		
	var nameError = $(document).jqmData("nameErr");
	if( typeof nameError != 'undefined' && nameError == "1" ){
		$(".gift-card-error-msg").show();
	}

	//Initially set error as none
	$(document).jqmData("nameErr", 0);	
	
	/*This function is for redeem gift card error message*/
    $("#dgcard-continue-btn").click(function(event){
		var isError = 0;
		var firstname = $("#first-name").val();
		var lastname = $("#last-name").val();
		var amount = $("#select-amount").val();
		
		if(firstname=="")
		{
			$(document).jqmRemoveData("rdFname",firstname);
		}else{
			$(document).jqmData("rdFname", firstname);
		}
		if($("#last-name").val()=="")
		{	    
			$(document).jqmRemoveData("rdLname",lastname);
		}else{
			$(document).jqmData("rdLname", lastname);
		}
		$(document).jqmData("rdAmt", amount);
		
	});
	
});


/*This function is for gift card flow select image and validate first name and last name*/
$("#gift-card-select").live('pageshow', function (event){
	$(".card").click(function(){
		var gcardName = $(this).parent(".imgWrapper").find('p.gName').html();
		gcardName = gcardName + " Design";
		var gcStore = $(document).jqmData("gcName", gcardName);
		
		var firstname = $("#first-name").val();
		var lastname = $("#last-name").val();
		var amount = $("#select-amount").val();
		
		$("div.active").removeClass("active");
		$(this).parent().addClass("active clicked").removeClass("blur");
		$(this).parent().parent().siblings().find(".imgWrapper").addClass("activeImg");
		$("#gift-card-select .imgWrapper p").css("color","#000");	
		$("div.imgWrapper:not('.active')").addClass("blur");
		var selectedImg = $(this).attr("src"); 
		$(document).jqmData("selectImgSrc", selectedImg);
		setTimeout(function(){
			//$.mobile.changePage( history.go(-1), {"transition":"pop"});
			/*history.back();*/
		},1000);
	});
	
	var imgSrc = $(document).jqmData("selectImgSrc");
	$('img[src*="'+imgSrc+'"]').parent().addClass("active");
	$('img[src*="'+imgSrc+'"]').parent().parent().siblings().find(".imgWrapper").addClass("activeImg");
	$('img[src*="'+imgSrc+'"]').parent().children("p").css("color","#000");

});

$("#gift-card-verify").live('pageshow', function (event){
	//pop-up
	$("#popFullTac").click(function(){
		var customSettings = {"ContentHolder": "#full_tac"};
		fnOverlay(customSettings);		
	});
	$("#popTacGCUse").click(function(){
		var customSettings = {"ContentHolder": "#tac_gcuse"};
		fnOverlay(customSettings);		
	});

	var getGcName = $(document).jqmData("gcName");
	$(".gcard-name").html(getGcName);
	
	var newImgSrc = $(document).jqmData("selectImgSrc");	
	if(typeof newImgSrc != 'undefined' && newImgSrc != null){
		$(".current-img").attr("src", newImgSrc);
	}
	var _firstname = ($(document).jqmData("rdFname")=='undefined')?"":$(document).jqmData("rdFname"); 
	var _lastname = ($(document).jqmData("rdLname")=='undefined')?"":$(document).jqmData("rdLname");
	
	/*if(typeof _firstname != 'undefined' && _firstname != ''){
		$(".add-name-txt span").html(_firstname+" "+_lastname);
		$(".add-name-txt a").html("Edit Name");
	}else{
		$(".add-name-txt span").html("No Name Entered");
		$(".add-name-txt a").html("Add Name");
	}*/
});

/*For Redemption History*/
$("#redeem-history").live('pageshow', function (event){
	if ($("#rd-history-list li").length==0){
			$("#rd-history-list").hide();
			$(".errormsg-all").show();
			$(".errormsg-ecert").hide();
	}
	else{
			$("#rd-history-list ").show();
			$("#rd-history-list li").show();
	}
	$("#rd-history-ecert").click(function(){
		$(this).addClass("curnt").siblings().removeClass("curnt"); /* 13.3 Global Change */
		 if($('.rd-ecert').length==0){
					$("#rd-history-list").hide();
					$(".errormsg-ecert").show();
					$(".errormsg-all").hide();
		 }
		else{
			$("#rd-history-list li").hide();
			$(".rd-ecert").show();
		}
	});
	$("#rd-history-all").click(function(){ 
		$(this).addClass("curnt").siblings().removeClass("curnt"); /* 13.3 Global Change */
		$(".errormsg-ecert").hide();
		if ($("#rd-history-list li").length==0){
					$("#rd-history-list").hide();
					$(".errormsg-all").show();
					$(".errormsg-ecert").hide();
		}
		else{
			$("#rd-history-list ").show();
			$("#rd-history-list li").show();
		}
	});
	
	/** For Redemption overview Overlay **/
	var isOpen = false;
	var ver = 0;
		//function to display the box
		function showOverlayBox() {
		//if box is not set to open then don't do anything
			if( isOpen == false ) return;
			// set the properties of the overlay box, the left and top positions
			$('.overlay_wraper').css({
				display:'block',
				marginLeft:'5%',
				marginRight:'5%',
				top:'75px',
				position:'absolute'
			});
			// set the window background for the overlay. i.e the body becomes darker
			$('.overlay').css({
				display:'block',
				width: '100%',
				height:'100%',
				opacity: '0.7',
				backgroundColor:'#000'
			});
			
			if(ver == 4)
			{				
				$("#pg-header, #pg-footer").addClass("pointer-events-none");
				$(".overlay").css("zIndex","0");
			}
		}
		function doOverlayOpen() {
			//set status to open
			ver = Redeem.getOsVersion();
			isOpen = true;
			showOverlayBox();
			//$('.overlay').css({opacity:0}).animate( {opacity:0.5, backgroundColor:'#000'} );
			// dont follow the link : so return false.
			return false;
		}
		function doOverlayClose() {
			$("#pg-header, #pg-footer").removeClass("pointer-events-none");
			//set status to closed
			isOpen = false;
			$('.overlay_wraper').css( 'display', 'none' );
			// now animate the background to fade out to opacity 0
			// and then hide it after the animation is complete.
			$('.overlay').animate( {opacity:0}, null, null, function() { $(this).hide(); } );			
		}
		// if window is resized then reposition the overlay box
		$(window).bind('resize',showOverlayBox);
		
		// activate when the link with class launchLink is clicked
		$('#open-overview-li').unbind("click").bind("click", function(){
			doOverlayOpen();
		} );
		// close it when closeLink is clicked
		$('#close-overlay, #closebtn').click(doOverlayClose );
		
		$("#getStarted").click( function(){
			doOverlayClose();
		});
		
		//$('.tooltip').hide();  /* backlog changes */
		/*
         * Click Handler for "Notification Icon". 
         */

       /* $('.rd-faq-main').click(function (e)
        {
			$('.tooltip').fadeIn(400).show();
			$('.rd-faq-main').css("opacity","0.5");
            return false;
        });    backlog changes */

		/* 
         * Close tooltip if user clicks outside 
         */
       /* $('body').click(function (){
			if ($('.tooltip').css("display") == "block") {
				$('.tooltip').fadeOut(300);
				$('.rd-faq-main').css("opacity","1");
			}
        });  backlog changes */
	/** For Redemption overview Overlay End **/
});

/*script to show error message when search result is empty*/
$("#browse-all-partners, #partner-category, #browse-all-giftcards, #browse-all-ecertificates").live('pageshow', function (event){
	//var listview = $('#browse-all-partners').find('ul').hasClass('partners');
	$(this).delegate('input[data-type="search"]', 'keyup', function () {
        var searchText = $(this).val();
		if ($('.partners').children(':visible').not('#no-results').length === 0) 
		{
           	$('#no-results').show();
			$("#no-results #searchTerm").html(searchText);
        }
		 else 
		 {
            $('#no-results').hide();
         }
	});
	$("#browse-all-partners div[data-role='content'] form span.ui-icon-delete, #partner-category div[data-role='content'] form span.ui-icon-delete,#browse-all-giftcards div[data-role='content'] form span.ui-icon-delete, #browse-all-ecertificates div[data-role='content'] form span.ui-icon-delete").click(function(){
		$("#no-results").hide();
	});
	
});

/*
*FAQ page Script.
*/
$("#rd-faqs").live('pagebeforeshow', function (event){
     $(".rdfaq-hidden-element").hide();  
});
/*To scroll to RAF FAQs section only for RAF pages
$("#rd-faqs").live('pageshow', function (event, data){
	var arrSilentScrollPages = new Array("raf-dollar", "raf-status-miles", "raf-status", "raf-status-dollar", "raf-miles");
	var prvPageId = data.prevPage.attr('id');
	if( $.inArray(prvPageId, arrSilentScrollPages) >= 0 ){
		setTimeout(function(){ $.mobile.silentScroll(700);}, 100);
	}
});
*/
$("#rd-faqs, #redemption-landing, #cashbackBonusLanding-pg").live('pageshow', function (event){
	//	Hide all the Answers of all Questions		
	$(".rdfaq-hidden-element").hide();
	$(".rd-faq-question-list .rdfaq-que-link-whole").toggle(function(){	 
		var cbbfaqnum = $(this).children().find(".rdfaq-que-link").html();
        dfs.crd.sct.cashbackBonusFAQ(cbbfaqnum);//site catalyst
		$(this).parents().children(".ui-block-b").children(".rdfaq-plus").css("background-position","bottom center");
		$(this).parents().children(".ui-block-a").children(".rdfaq-hidden-element").show()
	},function(){
		$(this).parents().children(".ui-block-b").children(".rdfaq-plus").css("background-position","top center");
		$(this).parents().children(".ui-block-a").children(".rdfaq-hidden-element").hide();
		});
	$(".rdfaq-que-link-whole").toggle(function(){
		$(this).children(".ui-grid-a").children(".ui-block-b").children(".rdfaq-plus").css("background-position","bottom center");
		$(this).children(".ui-grid-a").children(".ui-block-a").children(".rdfaq-hidden-element").show()
	},function(){
		$(this).children(".ui-grid-a").children(".ui-block-b").children(".rdfaq-plus").css("background-position","top center");
		$(this).children(".ui-grid-a").children(".ui-block-a").children(".rdfaq-hidden-element").hide();
		});	
		
	/** For redemption overview overlay **/
		var isOpen = false;
		var ver = 0;
		//function to display the box
		function showOverlayBox() {
		//if box is not set to open then don't do anything
			if( isOpen == false ) return;
			// set the properties of the overlay box, the left and top positions
			$('.overlay_wraper').css({
				display:'block',
				marginLeft:'5%',
				marginRight:'5%',
				top:'75px',
				position:'absolute'
			});
			// set the window background for the overlay. i.e the body becomes darker
			$('.overlay').css({
				display:'block',
				width: '100%',
				height:'100%',
				opacity: '0.7',
				backgroundColor:'#000'
			});
			if(ver == 4)
			{
				$("#pg-header, #pg-footer").addClass("pointer-events-none");
				$(".overlay").css("zIndex","0");
			}
		}
		function doOverlayOpen() {
			//set status to open
			ver = Redeem.getOsVersion();
			isOpen = true;
			showOverlayBox();
			dfs.crd.sct.redemptionOverviewOverlay();//passing site catalyst variable for Redemption Overview Overlay Click
			//$('.overlay').css({opacity:0}).animate( {opacity:0.5, backgroundColor:'#000'} );
			// dont follow the link : so return false.
			return false;
		}
		function doOverlayClose() {
			$("#pg-header, #pg-footer").removeClass("pointer-events-none");
			//set status to closed
			isOpen = false;
			$('.overlay_wraper').css( 'display', 'none' );
			// now animate the background to fade out to opacity 0
			// and then hide it after the animation is complete.
			$('.overlay').animate( {opacity:0}, null, null, function() { $(this).hide(); } );
		}
		// if window is resized then reposition the overlay box
		$(window).bind('resize',showOverlayBox);
		// activate when the link with class launchLink is clicked
		$('#open-overview-rdl').die("click").live("click",  doOverlayOpen );
		// close it when closeLink is clicked
		$('.close-overlay').click( function(){ doOverlayClose(); } );
		//$('.tooltip').hide(); /* backlog changes */
		/*
         * Click Handler for "Notification Icon". 
         */

      /*  $('.rd-faq-main').click(function (e)
        {
			dfs.crd.sct.redemptionHelpQuestionIcon();//passing sitecatalyst variables for Help with Redemption (Question Button) Click
			$('.tooltip').fadeIn(400).show();
			$('.rd-faq-main').css("opacity","0.5");
            return false;
        });  backlog changes */

		/* 
         * Close tooltip if user clicks outside 
         */
      /*  $('body').click(function ()
        {
			if ($('.tooltip').css("display") == "block") {
				$('.tooltip').fadeOut(300);
				$('.rd-faq-main').css("opacity","1");
			}
        });	backlog changes */
	/** For redemption overview overlay End **/
});


/*Ecertificate flow Toggle function for see/hide description*/
$("#redeem-dtlview, #redeem-ecert-dtlview, #redeem-ecert-error, #redeem-ecert-error-sample, #redeem-gcard-dtlview").live('pageshow', function (event){
	$("ul.collapsible li a").click(function(event) {
		var curntClass = $(this).attr('class');
		if(curntClass.match(/deactive/)) {
		  $("ul.collapsible li a").html("Hide partner description");  
		}
		else if(curntClass.match(/active/))  {
		  $("ul.collapsible li a").html("See partner description");  
		}
	});
	
	if($("#stepper1").val()=="1") {
		$(".ui-corner-left").addClass("disabled-btn");
	}else{
		$(".ui-corner-left").removeClass("disabled-btn");
	}
});

$("#redeem-dtlview, #gift-card-verify-partner, #ecert-verify-partner, #redeem-ecert-dtlview, #redeem-ecert-error, #redeem-ecert-error-sample, #redeem-gcard-dtlview, #cofirm-gift-card-partner").live('pageshow', function (event){
	
	var curAmtId = $(this).find("a").attr("id"); 
	$("#redeem-dtlview").jqmData("selectedAmtId",curAmtId); 
	
	/*Stored current quantity using jqmData*/	
	$("#common-continue-btn").click(function(){
		var selectedQty = $("#stepper1").val();
		$("#redeem-dtlview").jqmData("selectedQty", selectedQty);
	});
		
	/* On 11/7 to support dynamic minimym/maximum values */
	/* as per the requirement hard coded the minimum value 1 */
	
	//Get the default selected value and set Min and Max qty accordingly
	var redeemMaxQty = $("a.blk-wht-btn").parents("section").attr("data-max-qty");
	var redeemAmountPay = $("a.blk-wht-btn").parents("section").attr("data-amount-pay");
	var redeemAmountGet = $("a.blk-wht-btn").parents("section").attr("data-amount-get");
	var redeemSelectedQty = $("#redeem-dtlview").jqmData("selectedQty");
	var selectedQty = $("#stepper1").val();
	if(redeemSelectedQty==undefined || redeemSelectedQty == '')
		{
			if(selectedQty == redeemMaxQty)
            {
                $(".ui-corner-right").addClass("disabled-btn");
            }
			$("#stepper1").val(selectedQty);
		}
		
	if(redeemSelectedQty > 1)
		{
			$("#gift-card-verify-partner").find(".pluralTxt").removeClass("pluralTxt");
			$("#ecert-verify-partner").find(".pluralTxt").removeClass("pluralTxt");
		}
	
	$("#redeem-dtlview").jqmData("redeemMaxQty", redeemMaxQty);
	$("#redeem-dtlview").jqmData("redeemAmountPay", redeemAmountPay);
	$("#redeem-dtlview").jqmData("redeemAmountGet", redeemAmountGet);
	
	
	$("#stepper1").attr({"max":redeemMaxQty, "value": redeemSelectedQty});
	
	if(redeemMaxQty==1){
		$(".ui-corner-right").addClass("disabled-btn");
	}
	if(redeemMaxQty<=1)
	{
		$(".ui-corner-left").addClass("disabled-btn");
	}

	/*Amount Buttons Toggle Classes for Ecertificate flow*/	
	
	$(".r5-buttons span.amt-btn").click(function(event){
		$(".ui-corner-right").removeClass("disabled-btn");
		$(".ui-corner-left").addClass("disabled-btn");
	});
	
	$("span.amt-btn").click(function(event){
		$(this).find("a").removeClass("drkgry-wht-btn").addClass("blk-wht-btn").find("span.chk").addClass("chk-icon");
		$(this).parent().siblings().find(".amt-btn a").removeClass("blk-wht-btn").addClass("drkgry-wht-btn").find("span.chk").removeClass("chk-icon");
		
		if( $(this).parents("section").length >0){
			var $this = $(this);
		}else{
			var $this = $("section a.blk-wht-btn");
		}

		/*Script to get the selected value to the next page*/		
		redeemMaxQty = $this.parents("section").attr("data-max-qty");
		redeemAmountPay = $this.parents("section").attr("data-amount-pay");
		redeemAmountGet = $this.parents("section").attr("data-amount-get");

		$("#redeem-dtlview").jqmData("redeemMaxQty", redeemMaxQty);
		$("#redeem-dtlview").jqmData("redeemAmountPay", redeemAmountPay);
		$("#redeem-dtlview").jqmData("redeemAmountGet", redeemAmountGet);
		
		/*Reset amount buttons on selection Gift card/eCertificate selection change*/
		
		if($(this).find("a").attr("id")=="btn-giftCard"){
			$(".r5-buttons section a").removeClass("blk-wht-btn").addClass("drkgry-wht-btn").find("span.chk").removeClass("chk-icon");
			$(".r5-buttons section:eq(0) a").removeClass("drkgry-wht-btn").addClass("blk-wht-btn").find("span.chk").addClass("chk-icon");
			$("#common-continue-btn").attr("href","rdGcardVerify.html");
			
		}else if($(this).find("a").attr("id")=="btn-eCertificate"){
			$(".r5-buttons section a").removeClass("blk-wht-btn").addClass("drkgry-wht-btn").find("span.chk").removeClass("chk-icon");
			$(".r5-buttons section:eq(0) a").removeClass("drkgry-wht-btn").addClass("blk-wht-btn").find("span.chk").addClass("chk-icon");
			$("#common-continue-btn").attr("href","rdEcertVerify.html");
		}
		/* On 11/7 to support dynamic minimym/maximum values */
		// reset the min and max attributes of quantity stepper		
		$("#stepper1").attr({"max":redeemMaxQty, "value": 1});
		if(redeemMaxQty==1){
			$(".ui-corner-right").addClass("disabled-btn");
		}else if(redeemMaxQty > 1){
			$(".ui-corner-right").removeClass("disabled-btn");
		}
		
	});
	
});


$("#gift-card-verify-partner").live('pageshow',function(event){
	// uncomment this to see the selected value alert
	// alert($("#redeem-dtlview").jqmData("redeemAmountPay"));
});

/* Click Highlighting functionality */

$(document).bind("pageshow", function(event,ui){
                 
$('#cardHome-pg .cardhome_ul a').bind('click',function(){
                                                       $(this).addClass('ui-btn-down-d');
                                                       });
                 
                 
                 
                 $("#login-submit,.common-btn,#bankAccountForDirectDeposit_pg a[data-role='button'],#customerServiceUpdateAccount-pg #continueButton,#accSumm_makePayBtn,#cashbackSignup1-pg a[data-role='button'],#cashbackSignup2-pg a[data-role='button']").click(function(){
                                                                                                                                                                                                                                                                     
                                                                                                                                                                                                                                                                     $(this).addClass('ui-btn-down-c');
                                                                                                                                                                                                                                                                     });
                 $("#js-search-submit").click(function(){
                                             /* $(this).parent().addClass('ui-btn-down-c')*/
                                              
                                              })
                 
                 $('ul.suggestions a,ul.partners a,#redemption-landing ul.ui-listview a,.account-list-view a').bind('click',function(){
                                                                                                                    
                                                                                                                    $(this).parents('li').addClass('ui-btn-down-d');
                                                                                                                    });
                 
                 });
$("#login-pg,#cardLogin-pg").live("pagebeforeshow",function(){
                                  
                                  $(".access_btn").removeClass('ui-btn-down-c').addClass('ui-btn-up-c');
                                  
                                  });
$("#best-value").live('pageshow',function(){
                      $('ul.suggestions a').bind('click',function(){
                                                 $(this).parents('li').css('-webkit-box-shadow','none !important').addClass('ui-btn-down-c');
                                                 
                                                 });
                      });

/*Click highlight End */
