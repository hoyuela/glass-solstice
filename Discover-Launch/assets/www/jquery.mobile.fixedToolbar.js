/*!
 jQuery Mobile fixedtoolbar polyfill for blacklisted browsers that don't natively support position:fixed
 Author @scottjehl
 Copyright 2012 Filament Group, Inc.
 License Dual MIT or GPLv2
 */
(function( $, undefined ) {
	try {
	// If the supportBlacklist is returning true, it's a blacklisted browser. 
	if( $.mobile.fixedtoolbar.prototype.options.supportBlacklist() && $.support.scrollTop ){
		// Keep a reference to the original _create method
		var oldcreate = $.mobile.fixedtoolbar.prototype._create,
		// Additional scripting to add to the _create method for polyfilling unsupported browsers
		createPolyfill = function(){
			if( this.options.polyfillSupport === true ){
				var toolbar = this.element,
				tbType = toolbar.hasClass( "ui-header-fixed") ? "header" : "footer",
						 page = toolbar.closest( ":jqmData(role='page')");
				/* set margin for top while fix header for ios 4.3 */
                // Add faux support class to toolbar
				toolbar.addClass( "ui-fixed-faux" );
				function clearHeaderPosition(){
                    $(".pg-header").css("marginTop","0");
                    $(".pg-footer").css("bottom","0");
                }
				/* set header footer in ios 4.3 */
				function showHeaderFooter(){
					$(".pg-header").css("marginTop",$( window).scrollTop()+"px !important");
					var setfooterPos = $( window).scrollTop();
					$(".pg-footer").css("bottom", -setfooterPos+"px !important");
					$(".pg-header, .pg-footer").css("display","block");					
				}
				function hideHeaderFooter(){
					$(".pg-header, .pg-footer").css("display","none");					
				}

				/* reseat page position after transition completion */
$("body").live("pageshow",function(){
showHeaderFooter();
$(document).scrollTop(0);
});




				/* orientation */
				$(window).resize(function() {
					showHeaderFooter();
				});
				/* orientation */
 
                $("select, input, textarea").unbind("focus").bind("focus",function(){				
                    showHeaderFooter();                      
                });
                
                /* on drop down change event reset header footer*/
                $("select").live("change",function(){
                  showHeaderFooter();                                 
                });
                $(".ui-select").live("click",function(){
                  showHeaderFooter();                                 
                }); 
                
                $(".ui-select").focusout(function(){
                    showHeaderFooter();                                 
                });
 
                $("select, input, textarea").unbind("blur").bind("blur",function(){                                                                 
                    showHeaderFooter(); 
                });

                page.unbind( "pageshow").bind( "pageshow", function( e ){
                    setTimeout('clearHeaderPosition();',100);
                });
 
                //hide show calender on payment page 
                $(".date-picker").live("click", function() {
					$(".wraper1, .wraper2").css("width","100% !important");
					//$(".wraper1").css("height","500px !important");
					$(".wraper2").css("min-height","310px !important");
					$(".wraper1").hide();
					$(".wraper2").show(function(){
					showHeaderFooter();
					$(document).scrollTop(5);
					});
                });
 
                    $("a#calendar-cancel").live("click", function() {
					$(".wraper1, .wraper2").css("width","100% !important");
					$(".wraper1").css("min-height","310px !important");
					//$(".wraper2").css("height","310px !important");
					$(".wraper2").hide();
					$(".wraper1").show(function(){
					showHeaderFooter();                                                
					$(document).scrollTop(5);
					});
                });
 
				//hide and show loading image for send money step-2 
 $(".adjustHeader").live("click", function() {
					$("#EnteredInfo, #loaderDiv").css("width","100% !important");
					$("#EnteredInfo").css("height","500px !important");
					$("#loaderDiv").css("height","310px !important");
					$("#EnteredInfo").hide();
					$("#loaderDiv").show(function(){
					showHeaderFooter();
					$(document).scrollTop(5);
					});
				});
                // Per page show, re-set up the event handling
                page.unbind( "pagebeforeshow").bind( "pagebeforeshow", function( e ){
                   var visible;
                   // Normalize proper object for scroll event
                   (( $( document ).scrollTop() === 0 ) ? $( window ) : $( document ) )
                     .unbind( "scrollstart.fixedtoolbarpolyfill").bind( "scrollstart.fixedtoolbarpolyfill", function(){
                          hideHeaderFooter();                        
                   })
                   .unbind( "scrollstop.fixedtoolbarpolyfill").bind( "scrollstop.fixedtoolbarpolyfill", function(){
                          showHeaderFooter();                      
                   });
              
					// on pagehide, unbind the event handlers
					page.one( "pagehide", function(){
						$( this ).add( this ).add( document ).unbind( ".fixedtoolbarpolyfill" );
					});                    
					// align for pageshow
					//resetPos();
				});                
			}
		};

		// Set the blacklist test return false, letting any normally-blacklisted browsers in to be polyfilled
		$.mobile.fixedtoolbar.prototype.options.supportBlacklist = function(){
			return false;
		};

		// Define a new option for polyfillSupport, which can be disabled per call or via data attr data-polyfill-support
		$.mobile.fixedtoolbar.prototype.options.polyfillSupport = true;

		// Overwrite the _create method with the old and the new
		$.mobile.fixedtoolbar.prototype._create = function(){

			// Call the old _create method.
			oldcreate.call( this );

			// Call the polyfill scripting
			createPolyfill.call( this );
		};
	}
}catch(err)	{ showSysException(err)	}
})( jQuery );