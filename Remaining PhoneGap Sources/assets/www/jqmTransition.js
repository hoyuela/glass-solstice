$("body").live('pageinit', function () {
                                $.mobile.defaultTransitionHandler = function( name, reverse, $to, $from ) {
                                
                                var deferred = new $.Deferred(),
                                sequential = false,
                                reverseClass = reverse ? " reverse" : "",
                                active	= $.mobile.urlHistory.getActive(),
                                toScroll = active.lastScroll || $.mobile.defaultHomeScroll,
                                screenHeight = $.mobile.getScreenHeight(),
                                maxTransitionOverride = $.mobile.maxTransitionWidth !== false && $( window ).width() > $.mobile.maxTransitionWidth,
                                none = !$.support.cssTransitions || maxTransitionOverride || !name || name === "none",
                                toggleViewportClass = function(){
                                $.mobile.pageContainer.toggleClass( "ui-mobile-viewport-transitioning viewport-" + name );
                                },
                                scrollPage = function(pos,t){
                                // By using scrollTo instead of silentScroll, we can keep things better in order
                                // Just to be precautios, disable scrollstart listening like silentScroll would
                                $.event.special.scrollstart.enabled = false;
                                //$.mobile.silentScroll(0);
                                //window.scrollTo( 0, pos );
                                $(document).scrollTop(0);
                                if (t) {
                                //$from.css(“-webkit-backface-visibility”,”hidden”);
                                //$from.css("top",-t);
                                }
                                
                                // reenable scrollstart listening like silentScroll would
                                setTimeout(function() {
                                           $.event.special.scrollstart.enabled = true;
                                           }, 150 );
                                },
                                cleanFrom = function(){
                                $from
                                .removeClass( $.mobile.activePageClass + " out in reverse " + name )
                                .height( "" );
                                },
                                startOut = function(){
                                // if it's not sequential, call the doneOut transition to start the TO page animating in simultaneously
                                if( !sequential ){
                                scrollPage(0,$(window).scrollTop());
                                doneOut();
                                }
                                else {
                                $from.animationComplete( doneOut );	
                                }
                                
                                // Set the from page's height and start it transitioning out
                                // Note: setting an explicit height helps eliminate tiling in the transitions
                                
                                
                                $from
                                .height( screenHeight + $(window ).scrollTop() )
                                .addClass( name + " out" + reverseClass );
                                },
                                
                                doneOut = function() {
                                
                                if ( $from && sequential ) {
                                cleanFrom();
                                }
                                
                                startIn();
                                },
                                
                                startIn = function(){	
                                $to.hide();
                                $to.addClass( $.mobile.activePageClass );				
                                
                                // Send focus to page as it is now display: block
                                $.mobile.focusPage( $to );
                                
                                // Set to page height
                                $to.height( screenHeight + toScroll );
                                
                                //scrollPage(toScroll);
                                
                                if( !none ){
                                $to.animationComplete( doneIn );
                                }
                                $to.show();
                                $to.addClass( name + " in" + reverseClass );
                                
                                if( none ){
                                doneIn();
                                }
                                
                                },
                                
                                doneIn = function() {
                                
                                if ( !sequential ) {
                                
                                if( $from ){
                                cleanFrom();
                                $from.css("top",0);
                                }
                                }
                                
                                $to
                                .removeClass( "out in reverse " + name )
                                .height( "" );
                                
                                toggleViewportClass();
                                
                                // In some browsers (iOS5), 3D transitions block the ability to scroll to the desired location during transition
                                // This ensures we jump to that spot after the fact, if we aren't there already.
                                //if( $( window ).scrollTop() !== toScroll ){
                                //	scrollPage(toScroll);
                                //}
                                
                                deferred.resolve( name, reverse, $to, $from, true );
                                };
                                
                                toggleViewportClass();
                                
                                if ( $from && !none ) {
                                startOut();
                                }
                                else {
                                doneOut();
                                }
                                return deferred.promise();
                                };
                                $.mobile.transitionHandlers = { "default" : $.mobile.defaultTransitionHandler };
//                                $.extend($.mobile, {
//                                         defaultPageTransition: "slidec"
//                                         });					
                                });