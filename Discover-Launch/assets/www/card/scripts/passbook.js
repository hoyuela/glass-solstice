
var orientation = '';
var count = 1;

$("#redemption-landing").live('pageshow', function (event){
		 var ver = parseInt(deviceVersion);		
	 	  //show it ONLY for IPOD, IPHONE IOS > 6
		 if(!(deviceType === "iPad") && !(deviceType === "Android") && ver >= 6){
	   	 	doOverlayNewOpen();	
         }else{
            return;
         }
		//function to display the box
		function showNewOverlayBox() {
		//if box is not set to open then don't do anything		
			
			//if the overlay has opened once do not open again
			if (localStorage.getItem("PASSOverlay") != null) return;
			
			localStorage.setItem("PASSOverlay", "true");
			$('.overlay_wraper1').css({
				display:'block',
				marginLeft:'5%',
				marginRight:'5%',
				top:'75px',
				position:'absolute'
			});
			// set the window background for the overlay. i.e the body becomes darker
			$('.overlay1').css({
				display:'block',
				width: '100%',
				height:'100%',
				opacity: '0.7',
				backgroundColor:'#000'
			});
			
		}
		function doOverlayNewOpen() {
			//set status to open
			ver = Redeem.getOsVersion();
			//isOpen1 = true;
			
			showNewOverlayBox();
			return false;
		}
		function doNewOverlayClose() {
			//set status to closed
			//isOpen1 = false;
			$('.overlay_wraper1').css( 'display', 'none' );
			// now animate the background to fade out to opacity 0
			// and then hide it after the animation is complete.
			$('.overlay1').animate( {opacity:0}, null, null, function() { $(this).hide(); } );
		}
		// if window is resized then reposition the overlay box
		$(window).bind('resize',showNewOverlayBox);
			
		// close it when closeLink is clicked		
		$('.close-overlay1').click( function(){ doNewOverlayClose(); } );
		
});


