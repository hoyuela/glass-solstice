
dfs.crd.edo = dfs.crd.edo || {};
var edoCampaignId;
var orientation = '';
var edoMaps = {};
edoMaps.lastZoom = null;
edoMaps.mapGlobal = null; // to keep map object
edoMaps.currpoint = null; // to keep current point seen in blue..
edoMaps.currImage = null; // to keep image for marker for current point on map
edoMaps.currentMarker = null; // to show marker for current point on map.
edoMaps.markers = []; // keeps all markers, selected as part of search.
edoMaps.pinImage = null; // to keep destination image seein in red.
edoMaps.phoneType = null;
edoMaps.point = null; // keeps clicked marker location on map.
edoMaps.isBack = false; // flag to keep page track.
edoMaps.directionsRenderer = null; // used for rendering directions on gmap.
edoMaps.directionsService = null; // used for getting directions from gmap.
edoMaps.searchString = null; // searched string/pin code.
edoMaps.routeClicked = null; // clicked route step index.
edoMaps.routeSummary = null; // clicked route step summary.
edoMaps.infoLoc = null; // to keep currently clicked step location on map.
edoMaps.defaultZoom = 10;
edoMaps.mapGlobalDir = null;
edoMaps.response = null;
edoMaps.options = null;
edoMaps.flag = false;
edoMaps.zipCode = null;
edoMaps.latitudeVal = null;  // to keep the current latitude
edoMaps.longitudeVal = null; // to keep the current longitude

dfs.crd.edo.showMap=function(){
	try{
		edoMaps.initialize();
		var currentPosition=edoMaps.getPos();
	}catch(err){
		showSysException(err);
	}
}

edoMaps.initialize = function() {
	try{
		edoMaps.flag=false;
		edoMaps.phoneType = deviceType; // Android or IOS.
		edoMaps.currImage = new google.maps.MarkerImage('../../images/curr-pin.png', new google.maps.Size(16, 16), new google.maps.Point(0,0));
		edoMaps.pinImage = new google.maps.MarkerImage('../../images/mapOrangePin.png', new google.maps.Size(15, 36), new google.maps.Point(0,0));
		edoMaps.currentStepImage = new google.maps.MarkerImage('../../images/pin.png', new google.maps.Size(15, 36), new google.maps.Point(0,0));
		edoMaps.shadowImage = new google.maps.MarkerImage('../../images/pinShadow.png', new google.maps.Size(30, 26), new google.maps.Point(0,0), new google.maps.Point(2, 26));
		edoMaps.shape = {
        coord: [1, 1, 1, 20, 18, 20, 18 , 1],
        type: 'poly'
		};
        var myOptions = {
            'disableAutoPan': false,
            'showCloseButton':false,
            'pixelOffset': new google.maps.Size(-71, -116),
            'zIndex': null,
            'boxClass':"downarrowdiv",
            'boxStyle': {
                'background': "white",
                "padding": "5px",
                "border-radius":"5px 5px",
                "min-width":"135px",
                "-moz-box-shadow":" 2px 2px 5px rgba(50, 50, 50, 0.67)",
                "-webkit-box-shadow":" 2px 2px 5px rgba(50, 50, 50, 0.67)",
                "box-shadow":" 2px 2px 5px rgba(50, 50, 50, 0.67)",
                'width':"auto",
                "height": "auto"
            },
            'closeBoxMargin': "0 0 0 0",
            'closeBoxURL': "",
            'infoBoxClearance': new google.maps.Size(1, 1),
            'isHidden': false,
            'pane': "floatPane",
            'enableEventPropagation': false
        };
        
        edoMaps.infoWindow = new InfoBox(myOptions);
		edoMaps.options = {
        zoom: edoMaps.defaultZoom,
        disableDefaultUI: true,
        mapTypeId: google.maps.MapTypeId.ROADMAP
		};
        
		edoMaps.directionsRenderer = new google.maps.DirectionsRenderer();
		edoMaps.directionsService = new google.maps.DirectionsService();
		google.maps.event.addListener(edoMaps.infoWindow, 'domready', function() {
                                      $('li.get_dir_btn').click(function(){
                                                                edoMaps.infoWindow.close();
                                                                dfs.crd.edo.getMapDirection();
                                                                });
                                      });
	}catch(err){
		showSysException(err);
	}
}


dfs.crd.edo.getMapDirection=function(){
	try{
		edoMaps.getDirections(edoMaps.currpoint, edoMaps.point, edoMaps.markerAddress);
	}catch(err){
		showSysException(err);
	}
}


/***Following code makes prompts the user to either his/her current position or not. - Arupesh/Chayan *********************** */
edoMaps.getPos = function(){
    try{
        var noGeoCode=true;
        if(navigator.geolocation){
            var timeoutVal = 10 * 1000 * 1000;
            killDataFromCache("edoDetailsMapOffers");
            showSpinner();
            window.setTimeout(function() {
                              hideSpinner();
                              if (noGeoCode == true)
                              {
                              navigator.notification.alert('Your location could not be determined',function dissmissAlert() {navigation("viewMap");},'Discover','OK');
                              }
                              }, 30000);
            navigator.geolocation.getCurrentPosition(function(position) {
                                                     noGeoCode = false;
                                                     edoMaps.currpoint = new google.maps.LatLng(position.coords.latitude, position.coords.longitude);
                                                     edoMaps.latitudeVal=	position.coords.latitude ;
                                                     edoMaps.longitudeVal= position.coords.longitude;
                                                     /*edoMaps.currpoint = new google.maps.LatLng(42.194,-88.136);
                                                      
                                                      edoMaps.latitudeVal= 42.194;
                                                      
                                                      edoMaps.longitudeVal= -88.136;*/
                                                     
                                                     edoMaps.displayMap();
                                                     }, function(error) {
                                                     noGeoCode = false;
                                                     navigation("viewMap");
                                                     }, {
                                                     enableHighAccuracy : false,
                                                     timeout : timeoutVal,
                                                     maximumAge : 50000
                                                     });
        }else{
            return;
        }
        
    }catch(err){
        
        // TODO: handle exception
        
        //showSysException(err);
    }
};

edoMaps.displayMap = function(){
	try{
		edoMaps.getOffers();
	}catch(err){
		showSysException(err);
	}
};

/***Following code makes the required ajax call depending on the status of the global flag edoMaps.flag . - Arupesh *** */
edoMaps.getOffers = function() {
	try{
		var geocood=getDataFromCache("edoDetailsMapOffers");
		var newDate = new Date();
		var COORDSURL;
		var postalGeoLoc = getDataFromCache("defaultUserZip");
		COORDSURL = RESTURL+"extras/v1/extrasbygeocoords?latitude="+edoMaps.latitudeVal+"&longitude="+edoMaps.longitudeVal+"&campaignId="+edoCampaignId;
		$.ajax(
               {
               type : "GET",
               url : COORDSURL,
               async : true,
               dataType : "json",
               headers : prepareGetHeader(),
               success : function(responseData, status, jqXHR)
               {
               if (!validateResponse(responseData,"edoDetailsMapOffersValidation")){ // Pen Test Validation
               errorHandler("SecurityTestFail","","");
               return;
               }
               geocood = responseData;
               putDataToCache("edoDetailsMapOffers",geocood);
               
               if(edoMaps.flag){
               dfs.crd.edo.plotOffersOnMap();
               google.maps.event.addListenerOnce(edoMaps.mapGlobal, 'tilesloaded', function(){
                                                 google.maps.event.trigger(edoMaps.mapGlobal, "resize");
                                                 edoMaps.mapGlobal.setCenter(edoMaps.currpoint);
                                                 
                                                 hideSpinner();
                                                 });
               }else{
               navigation("viewMap");
               }
               
               },
               error : function(jqXHR, textStatus, errorThrown)
               {
               hideSpinner();
               cpEvent.preventDefault();
               var code = getResponseStatusCode(jqXHR);
               dfs.crd.edo.edoErrorHandler(code, "", "viewMap");
               }
               });
	}catch(err){
		// TODO: handle exception
		showSysException(err);
	}
};


dfs.crd.edo.plotOffersOnMap=function(){
	try{
		var geocood=getDataFromCache("edoDetailsMapOffers");
		if (!jQuery.isEmptyObject(geocood)) {
            
			
			edoMaps.currentMarker = edoMaps.createMarker({
                                                         icon:edoMaps.currImage
                                                         });
			edoMaps.currentMarker.setPosition(edoMaps.currpoint);
			edoMaps.currentMarker.setMap(edoMaps.mapGlobal);
			edoMaps.mapGlobal.setCenter(edoMaps.currpoint);
			edoMaps.plotOffers(geocood);
            
			google.maps.event.trigger(edoMaps.mapGlobal, "resize");
		}
	}catch(err){
		showSysException(err);
	}
}

function viewMapLoad(){
	try{
		HybridControl.prototype.enableSlidingMenu(null,false);	
		var validPriorPagesOfViewMap= new Array("edoDetail");
		if(jQuery.inArray(fromPageName, validPriorPagesOfViewMap) > -1 ){
			var geocood=getDataFromCache("edoDetailsMapOffers");
			if (!jQuery.isEmptyObject(geocood)) {
                
				edoMaps.mapGlobal = new google.maps.Map(document.getElementById("map_canvas"), edoMaps.options);
				edoMaps.currentMarker = edoMaps.createMarker({
                                                             icon:edoMaps.currImage
                                                             });
                
				google.maps.event.addListener(edoMaps.mapGlobal, "click", function(){
                                              if(typeof edoMaps.infoWindow != "undefined"){
                                              edoMaps.infoWindow.close();
                                              }
                                              });
                
				edoMaps.currentMarker.setPosition(edoMaps.currpoint);
				edoMaps.currentMarker.setMap(edoMaps.mapGlobal);
				edoMaps.mapGlobal.setCenter(edoMaps.currpoint);
				edoMaps.plotOffers(geocood);
				google.maps.event.addListenerOnce(edoMaps.mapGlobal, 'bounds_changed', function(){
                                                  google.maps.event.trigger(edoMaps.mapGlobal, "resize");
                                                  edoMaps.mapGlobal.setCenter(edoMaps.currpoint);
                                                  });
				window.setTimeout(function() {
                                  hideSpinner();
                                  }, 30000);
                
				google.maps.event.addListenerOnce(edoMaps.mapGlobal, 'tilesloaded', function(){
                                                  google.maps.event.trigger(edoMaps.mapGlobal, "resize");
                                                  edoMaps.mapGlobal.setCenter(edoMaps.currpoint);
                                                  hideSpinner();
                                                  });
			}else{
                var myOptions = {disableDefaultUI: true,
                    mapTypeId: google.maps.MapTypeId.ROADMAP };
                var map = new google.maps.Map(document.getElementById("map_canvas"), myOptions);
                var geocoder = new google.maps.Geocoder();
                var currpoint = new google.maps.LatLng(39.6395,-99.4921);
                geocoder.geocode({'address': 'US'}, function (results, status) {
                                 map.fitBounds(results[0].geometry.viewport);
                                 });
                google.maps.event.addListenerOnce(map, 'bounds_changed', function(){
                                                  google.maps.event.trigger(map, "resize");
                                                  map.setCenter(centerpoint);
                                                  });
                window.setTimeout(function() {
                                  hideSpinner();
                                  }, 30000);
                google.maps.event.addListenerOnce(map, 'tilesloaded', function(){
                                                  google.maps.event.trigger(map, "resize");
                                                  map.setCenter(currpoint);
                                                  hideSpinner();
                                                  });
			}
		}else{
			cpEvent.preventDefault();
			history.back();
		}
	}catch(err){
		showSysException(err);
	}
}

edoMaps.plotOffers = function(offers) {
	try{
		edoMaps.markers = [];
		var offerType="extrasByGeoCoords";
		if(offers['extrasByZip']){
			offerType="extrasByZip";
		}
		if(offers[offerType].length == 0){
            navigator.notification.alert('At this time, no merchant locations can be found within a 15 mile radius of the search location. Please search another city or zip to find a merchant location.',function dissmissAlert() {},'Discover','OK');
            return;
		}
		for (var key in offers[offerType]) {
            // addressString =null;
            if(isEmpty(offers[offerType][key].address2)){
                addressString=offers[offerType][key].address1+","+offers[offerType][key].city+","+offers[offerType][key].state+","+offers[offerType][key].zipcode;
            }
            else
            {
                addressString=offers[offerType][key].address1+","+offers[offerType][key].address2+","+offers[offerType][key].city+","+offers[offerType][key].state+","+offers[offerType][key].zipcode;
            }
			var myLatLng = new google.maps.LatLng(offers[offerType][key].latitude, offers[offerType][key].longitude);
			if(typeof (offers[offerType][key].alias) === 'undefined')
			{
                offers[offerType][key].alias=offers[offerType][key].merchantName;
			}
			var marker = edoMaps.createMarker({
                                              title: offers[offerType][key].alias,
                                              position: myLatLng,
                                              map: edoMaps.mapGlobal,
                                              icon: edoMaps.pinImage,
                                              shadow: edoMaps.shadowImage,
                                              addressLoc: addressString
                                              });
            
			edoMaps.markers.push(marker);
			google.maps.event.addListener(marker, 'click',function(event){
                                          var html='<div id="get-dir"><ul><li class="merchant"><span>'+ this.title +'</span></li><li class="get_dir_btn">Get Directions</li></ul></div>';
                                          edoMaps.infoWindow.setContent(html);
                                          edoMaps.infoWindow.open(edoMaps.mapGlobal, this);
                                          edoMaps.point= this.position;
                                          edoMaps.markerAddress = this.addressLoc;
                                          });
		}
        // addressString=null;
	}catch(err){
		showSysException(err);
	}
};

edoMaps.createMarker = function(obj){
	try{
		return new google.maps.Marker(obj);
	}catch(err){
		showSysException(err);
	}
};
edoMaps.initialize2 = function() {
	try{
		edoMaps.mapGlobalDir = new google.maps.Map(document.getElementById("map_canvas2"), edoMaps.options);
	}catch(err){
		showSysException(err);
	}
};

$("#edo-faqs,#customerServiceFaqs-pg,#qvFaqs-pg, #sendMoneyFAQ-pg, #rd-faqs,#updateEmail-pg,#personalizeCashPin1-pg").live('pageshow',function(event) {
	try{
//		Hide all the Answers of all Questions
		$(".rdfaq-hidden-element").hide();
		$(".rdfaq-que-link-whole").click(function(){


		if($(this).children(".ui-grid-a").find(".rdfaq-hidden-element").css("display") =="block")

		{
			$(this).children(".ui-grid-a").find(".rdfaq-hidden-element").css("display","none");
			$(this).children(".ui-grid-a").children(".ui-block-a").find(".rdfaq-plus").css("background-position","top center");



		}
		else 
		{
			$(this).children(".ui-grid-a").find(".rdfaq-hidden-element").css("display","block");
			$(this).children(".ui-grid-a").children(".ui-block-a").find(".rdfaq-plus").css("background-position","bottom center");


		}
	});
                    }catch(err){
                    showSysException(err);
                    }
                    });
$(".edo-page").live('pageshow', function(event) {
                    try{
                    /*
                     $('.edo-page .tooltip ul li:eq(1)').click(function() {
                     $.mobile.changePage("edoFaqs.html");
                     });
                     */
                    }catch(err){
                    showSysException(err);
                    }
                    });


edoMaps.getDirections = function(src,dest,markerAddress) {
	try{
		edoMaps.directionsRenderer.suppressMarkers = true;
		var request = {
        origin: src,
        destination: dest,
        travelMode: google.maps.DirectionsTravelMode.DRIVING,
        unitSystem: google.maps.DirectionsUnitSystem.IMPERIAL
		};
        
		edoMaps.directionsService.route(request, function(response, status) {
                                        if (status == google.maps.DirectionsStatus.OK) {
                                        edoMaps.response = response;
                                        var route = response.routes[0]; // assuaming there are no alternate routes requested..
                                        // For each route, display summary information.
                                        edoMaps.steps = route.legs[0].steps;  // assuaming no way points where given in request..
                                        var button = '';
                                        var list = '<ul id="directionsList">';
                                        button = '<li class="items"><div class="icon">'+
                                        '<img src="../../images/arrowIcon.png" alt="Starting address" width="25" height="25" />'+
                                        '</div><div class="routeInfo"> <strong>Route overview</strong> </br>'+ route.legs[0].distance.text + '</div><div class="clearboth"></div></li>';
                                        
                                        button += '<li class="items"><div class="icon">'+
                                        '<img src="../../images/listBluePin.png" alt="Starting address" width="10" height="20" />'+
                                        '</div><div class="routeInfo">'+ route.legs[0].start_address + '</div><div class="clearboth"></div></li>';
                                        
                                        for (var i = 0; i < edoMaps.steps.length; i++) {
                                        var routeInnerHTML=edoMaps.steps[i].instructions;
                                        var routeInnerHTMLPostReplace=routeInnerHTML.replace('<div style=\"font-size:0.9em\">',' <div style=\"font-size:0.9em\">');
                                        edoMaps.steps[i].instructions=routeInnerHTMLPostReplace;
                                        button += '<li id="'+ i +'" class="items clickable" onClick = "edoMaps.PageDirection(this);"> <div class="icon">'+ (parseInt(i)+1) +'</div><div class="routeInfo">'+ edoMaps.steps[i].instructions +'</div><div class="rightArrow">'
                                        + '<img src="../../images/edoBlueArrow.png" alt="right arrow" width="6" height="8" /></div><div class="clearboth"></div></li>';
                                        }
                                        button += '<li class="items"><div class="icon">'+
                                        '<img src="../../images/listOrangePin.png" alt="End address" width="10" height="20" />'+
                                        '</div><div class="routeInfo">'+ markerAddress + '</div><div class="clearboth"></div></li>';
                                        list = list + button + '</ul>';
                                        
                                        $('#dirPanel-pg').live('pagebeforeshow',function(){
                                                               $('#directionsPanel').html(list);
                                                               });
                                        navigation('../edo/dirPanel');
                                        
                                        }else{
                                        cpEvent.preventDefault();
                                        alert('Error: No Direction Found');
                                        
                                        }
                                        });
	}catch(err){
		showSysException(err);
	}
}

edoMaps.clearMap = function(){
	try{
		if(edoMaps.markers.length > 0) {
			for(var i=0;i<edoMaps.markers.length;i++){
				edoMaps.markers[i].setMap(null);
			}
			edoMaps.markers = [];
		}
		if(edoMaps.directionsRenderer) {
			edoMaps.directionsRenderer.setMap(null);
		}
		if(edoMaps.infoWindow) {
			edoMaps.infoWindow.setContent('');
			edoMaps.infoWindow.close();
		}
		if(typeof edoMaps.tempMarker != "undefined"){
			edoMaps.tempMarker.setMap(null);
		}
	}catch(err){
		showSysException(err);
	}
}

//Returns location based on search string.
edoMaps.getSearchedLocation = function(address){
	try{
	    edoMaps.mapGlobal = new google.maps.Map(document.getElementById("map_canvas"), edoMaps.options);
		edoMaps.isBack = false;
		edoMaps.clearMap();
		var geoCoder = new google.maps.Geocoder();
		var request = {
            "address": address
		};
		geoCoder.geocode(request, function(results, status){
                         if(status == google.maps.GeocoderStatus.OK){
                         edoMaps.currpoint = results[0].geometry.location;
                         edoMaps.latitudeVal=results[0].geometry.location.lat();
                         edoMaps.longitudeVal=results[0].geometry.location.lng();
                         
                         edoMaps.flag = true;
                         edoMaps.displayMap();
                         } else if(status == google.maps.GeocoderStatus.ZERO_RESULTS){
                         dfs.crd.edo.edoErrorHandler("2004", "", "viewMap");
                         }
                         });
	}catch(err){
		showSysException(err);
	}
}

edoMaps.drawMarker = function() {
	try{
		var obj = edoMaps.obj;
		currentStep = $(obj).attr("id");
		edoMaps.routeSummary = $(".routeInfo", obj).text();
		edoMaps.infoLoc = new google.maps.LatLng(edoMaps.steps[currentStep].end_location.lat(), edoMaps.steps[currentStep].end_location.lng());
		edoMaps.tempMarker = edoMaps.createMarker({
                                                  position: edoMaps.infoLoc,
                                                  map: edoMaps.mapGlobalDir,
                                                  icon: edoMaps.currentStepImage,
                                                  shadow: edoMaps.shadowImage,
                                                  zIndex: -10
                                                  });
		$("#routes").text(edoMaps.routeSummary);
		edoMaps.mapGlobalDir.setCenter(edoMaps.infoLoc);
	}catch(err){
		showSysException(err);
	}
};

edoMaps.PageDirection = function(obj){
	try{
		edoMaps.obj = obj;
		edoMaps.routeClicked = $(obj);
		navigation('../edo/mapDirections');
	}catch(err){
		showSysException(err);
	}
};

/*$("#searchLocation").live("blur", function(event){
 try{
 if(isEmpty(event.currentTarget.value)){
 return;
 }
 showSpinner();
 window.setTimeout(function() {
 hideSpinner();
 
 }, 30000);
 killDataFromCache("edoDetailsMapOffers");
 edoMaps.searchString = $(this).val();
 edoMaps.getSearchedLocation(edoMaps.searchString);
 }catch(err){
 showSysException(err);
 }
 });*/

// To enable search on enter key press

$("#searchLocation").live("keypress", function(event){
                          try {
                          if(isEmpty(event.currentTarget.value)){
                          return;
                          }
                          if(event.keyCode == 13){ // works only on enter key press..
                          if($("*:focus").attr("id") =="searchLocation") {
                          $("#searchLocation").blur();
                          }
                          showSpinner();
                          window.setTimeout(function() {
                                            hideSpinner();
                                            }, 30000);
                          killDataFromCache("edoDetailsMapOffers");
                          edoMaps.searchString = $(this).val();
                          $("#map_canvas").html("");
                          edoMaps.getSearchedLocation(edoMaps.searchString);
                          }
                          }
                          catch(err){
                          showSysException(err);
                          }
                          });
// enter key press ends here..
$("#mapDirections-pg").live("pagecreate",function(){
                            try{
                            edoMaps.initialize2();
                            }catch(err){
                            showSysException(err);
                            }
                            });

$("#mapDirections-pg").live("pageshow",function(){
                            try{
                            google.maps.event.trigger(edoMaps.mapGlobalDir, "resize");
                            edoMaps.drawMarker();
                            }catch(err){
                            showSysException(err);
                            }
                            });

edoMaps.createMarkForDir = function(){
	try{
		// create source marker.
		var sourceMark = edoMaps.createMarker({
                                              position: edoMaps.currpoint,
                                              map: edoMaps.mapGlobalDir,
                                              icon: edoMaps.currImage,
                                              shadow: edoMaps.shadowImage,
                                              zIndex: -99
                                              });
		// create dest marker.
		var destMark = edoMaps.createMarker({
                                            position: edoMaps.point,
                                            map: edoMaps.mapGlobalDir,
                                            icon: edoMaps.pinImage,
                                            shadow: edoMaps.shadowImage,
                                            zIndex: -99
                                            });
		// create clicked point marker.
	}catch(err){
		showSysException(err);
	}
};

$("#mapDirections-pg").live("pagebeforeshow",function(){
                            try{
                            // set map direction here..
                            edoMaps.createMarkForDir();
                            edoMaps.directionsRenderer.suppressMarkers = true;
                            edoMaps.directionsRenderer.setMap(edoMaps.mapGlobalDir);
                            edoMaps.directionsRenderer.setDirections(edoMaps.response);
                            }catch(err){
                            showSysException(err);
                            }
                            });

$("#viewMap-pg").live("pageshow",function(){
	
                //console.log("$(window).height() : " + $(window).height() + " => " + $("#pg-header").outerHeight());
  var netHeight=$(window).height()-$("#pg-header").outerHeight() - $(".searchInput").outerHeight();
                                        $("#map_canvas,#map_canvas2").height(netHeight)
                                        });	
$("#mapDirections-pg").live("pageshow", function(){
                var netHeight=$(window).height()-$("#pg-header").outerHeight() - $(".navigation").outerHeight();
                $("#map_canvas2").height(netHeight);
});                                                                                                           

$(window).resize(function(){
                var netHeight=$(window).height()-$("#pg-header").outerHeight() - $(".searchInput").outerHeight();
                $("#map_canvas").height(netHeight);
                
var netHeight1=$(window).height()-$("#pg-header").outerHeight() - $(".navigation").outerHeight();
                $("#map_canvas2").height(netHeight1);
});						
							
edoMaps.triggerClick = function(type){
	try{
		var obj;
		if(typeof edoMaps.tempMarker != "undefined"){
			edoMaps.tempMarker.setMap(null);
		}
		if(type == "forward") {
			obj = edoMaps.routeClicked.next();
		}else{
			obj = edoMaps.routeClicked.prev();
		}
		edoMaps.obj = obj;
		edoMaps.routeClicked = obj;
		$("#routes").text($(".routeInfo", obj).text());
		edoMaps.drawMarker();
	}catch(err){
		showSysException(err);
	}
};

$("#rightBtn").live("click", function(event){
                    try{
                    currentStep = $(edoMaps.obj).attr("id");
                    if(currentStep < edoMaps.steps.length-1){
                    edoMaps.triggerClick("forward");
                    }
                    }catch(err){
                    showSysException(err);
                    }
                    });

$("#leftBtn").live("click", function(event){
                   try{
                   currentStep = $(edoMaps.obj).attr("id");
                   if(currentStep > 0){
                   edoMaps.triggerClick("backward");
                   }
                   }catch(err){
                   showSysException(err);
                   }
                   });

///////////////////////////// Map ends here ////////////////////////////////////

$("#openViewMap").live("click", function(event){
                       try{
                       event.preventDefault();
                       navigation('../edo/viewMap');
                       }catch(err){
                       showSysException(err);
                       }
                       });


$("#detailCCBtn", "#detail-with-cc").live("click", function(){
                                          try{
                                          if(window.locaStorage){
                                          window.localStorage.beforeMapPage = "detailWithCC.html";
                                          }
                                          }catch(err){
                                          showSysException(err);
                                          }
                                          });


$("#closeMapBtn", "#viewMap-pg").live("click", function(){
                                      try{
                                      event.preventDefault();
                                      if(window.localStorage){
                                      edoMaps.isBack = false;
                                      $.mobile.changePage(window.localStorage.beforeMapPage);
                                      }
                                      }catch(err){
                                      showSysException(err);
                                      }
                                      });
function showOverlayBox() {
	try{
		//if box is not set to open then don't do anything
		var showEDOOverlay = localStorage.getItem("EdoExtraPopup");//Girish_Change
		if (!isEmpty(showEDOOverlay)) {
			return;
		}
		// set the properties of the overlay box, the left and top positions
		wH = $(window).height();
		oH= $('.overlay_wraper').height();
		wW = $(window).width();
		oW= $('.overlay_wraper').width();
		mL= (wW - oW)/2;
		/* fixes for landscape */
		if(wH<wW){
			mT= 26;
		}else{
			mT= (wH - oH)/2;
		}
        
		$('.overlay_wraper').css({
                                 display:'block',
                                 left:mL + "px",
                                 top:mT + "px",
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
		localStorage.setItem("EdoExtraPopup", true); //Girish_Change
	}catch(err){
		showSysException(err);
	}
}

function doOverlayOpen() {
	try{
		//set status to open
		ver = edo.getOsVersion();
		isOpen = true;
		showOverlayBox();
		return false;
	}catch(err){
		showSysException(err);
	}
}
function doOverlayClose() {
	try{
		$("#pg-header, #pg-footer").removeClass("pointer-events-none");
		$("body").css('overflow','auto');
		//set status to closed
		isOpen = false;
		$('.overlay_wraper').css( 'display', 'none' );
		// now animate the background to fade out to opacity 0
		// and then hide it after the animation is complete.
		$('.overlay').animate( {opacity:0}, null, null, function() { $(this).hide(); } );
	}catch(err){
		showSysException(err);
	}
}
/* Edo extra overlay flag reset */
function edoExtraOverlay() {
	try{
		localStorage.setItem("EdoExtraPopup","");
		doOverlayOpen();
		$("body").css('overflow','hidden');
	}catch(err){
		showSysException(err);
	}
}

var edo  = {
	
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
function setGridwpwidth(){
	var winWidth = $(window).width();
	var setWidthGridWraper = Math.floor(winWidth/123);
		
		setWidthGridWraper = setWidthGridWraper*141;
		if (winWidth<setWidthGridWraper) {
			setWidthGridWraper = setWidthGridWraper-141;
		}
		$(".edoDetailsDisplay_showGridview").css('width', setWidthGridWraper+'px')
}
$("#edoLandingPage-pg, #edoLandingWithoutOffer").live('pageshow',function(event){
                                                      try{
														setGridwpwidth();
                                                      var offerUl = $(".allItemList");
                                                      var liObject = offerUl.children("li").get();
                                                      
                                                      	/* $('#sortFeatures').live('blur',function(){
													$(".navBtns .ui-block-a .ui-select .ui-btn").removeClass("selectedTab");
													}); 
													$('#sortFeatures').live('click',function(){	
													$(".navBtns .ui-block-a .ui-select .ui-btn").toggleClass("selectedTab");
													});*/
													/*click handler for SORT option*/
                                                
                                                	 /*click handler for SORT option*/
													var flag = 1;
													var inside = true;
													
													$('#sortFeatures').click(function(){
														inside = true;
														flag = 1;
													});
													
													 $('#sortFeatures').blur(function(){
														if(inside){
														 if(flag==1){
															 $(".navBtns .ui-block-a .ui-select .ui-btn").addClass("selectedTab");
														 flag = 2;
														 }
													 }
													}); 
													
													 $('#sortFeatures').focus(function(e){
														
														 if(flag==2){
															 inside = false;
															 $(".navBtns .ui-block-a .ui-select .ui-btn").removeClass("selectedTab");
															 flag=1;
														}
													 }); 
													 
                                                      $('#sortFeatures').change(function(){
                                                                              
                                                                                var value = $(this).val();
                                                                                var b = new Array();
                                                                                if(value=="a-z"){
                                                                                alphabeticalSort();
                                                                                dfs.crd.sct.extrasSortFunctionality('Alphabetic');//passing site catalyst variable for All Sort Functionality
                                                                                 
																				}
                                                                                if(value=="featured"){
                                                                                featuredSort();
                                                                                
                                                                                
                                                                                }
                                                                                if(value=="expiringsoon"){
                                                                                sortExpirySoon();
                                                                                dfs.crd.sct.extrasSortFunctionality('ExpiringSoon');//passing site catalyst variable for All Sort Functionality
                                                                                }
                                                                                if(value=="mostrecent"){
                                                                                mostRecent();
                                                                                dfs.crd.sct.extrasSortFunctionality('MostRecent');//passing site catalyst variable for All Sort Functionality
                                                                                }
                                                                                
                                                                                /*$(".navBtns .ui-block-a .ui-select .ui-btn").removeClass("selectedTab"); */
                                                                                $("#edoLandingPage-pg, #edoLandingWithoutOffer").find(".ui-select .ui-btn-inner span.ui-btn-text").html(value);
                                                                                $('#edoLandingPage-pg #edoLandingSortOptions .ui-select .ui-btn-text').text('Sort');
                                                                                });
                                                      /*Click handler for Tooltip*/
                                                      $(".icon_faqs").click(function(){
                                                                            if(deviceType == "Android") {
                                                                            $("#edoLandingSortOptions #sortFeatures").attr("disabled","disabled");
                                                                            }
                                                                            $(".tooltip").fadeIn(300);
                                                                            return false;
                                                                            });
                                                      $("body").click(function(){
                                                                      if($(".tooltip").css("display")=='block'){
                                                                      $(".tooltip").fadeOut(300);
                                                                      if(deviceType == "Android") {
                                                                      $("#edoLandingSortOptions #sortFeatures").removeAttr("disabled");
                                                                      }
                                                                      $(".icon_faqs").css({"opacity":"1"});
                                                                      }
                                                                      });
                                                      
                                                      
                                                      /**Click handler for top tabs**/
                                                      $(".navtabs .ui-block-b").click(function(){
                                                                                      });
                                                      
                                                      /** For EDO Landing Page overlay **/
                                                      var isOpen = false;
                                                      var ver = 0;
                                                      //function to display the box
                                                      // if window is resized then reposition the overlay box
                                                      $(window).bind('resize',showOverlayBox);
                                                      // activate when the link with class launchLink is clicked
                                                      $('#open-overview-rdl').die("click").live("click",edoExtraOverlay);
                                                      // close it when closeLink is clicked
                                                      $('.close-overlay').click( function(){
													  doOverlayClose(); 
													  } );
                                                      $('.overlay').click( function(){
														/*doOverlayClose()*/
													  });
                                                      $('.tooltip').hide();
                                                      doOverlayOpen();
                                                      }catch(err){
                                                      showSysException(err);
                                                      }
                                                      });


$("#edoHistory-pg").live('pageshow',function(){
                         try{
                         /*Click handler for Tooltip*/
                         $('#closebtn').click( function(){
						 $("#overlay_wraper, .overlay").hide()});
                         $(".icon_faqs").click(function(){
                                               $(".tooltip").fadeIn(300);
                                               /*$(this).css({"opacity":"0.6"});*/
                                               return false;
                                               });
                         $("body").click(function(){
                                         if($(".tooltip").css("display")=='block'){
                                         $(".tooltip").fadeOut(300);
                                         $(".icon_faqs").css({"opacity":"1"});
                                         }
                                         });
                         
                         /** For EDO Landing Page overlay **/
                         var isOpen = false;
                         var ver = 0;
                         doOverlayOpen();
                         // if window is resized then reposition the overlay box
                         $(window).bind('resize',showOverlayBox);
                         // activate when the link with class launchLink is clicked
                         $('#open-overview-rdl').die("click").live("click",edoExtraOverlay);
                         // close it when closeLink is clicked
                         $('.close-overlay').click( function(){ 
						 doOverlayClose(); 
						 } );
                         $('.overlay').click( function(){
							/*doOverlayClose()*/
							});
                         $('.tooltip').hide();
                         
                         /**Click handler for top tabs**/
                         $(".navtabs .ui-block-a").click(function(){
                                                         $.mobile.changePage("edoLandingPage.html");
                                                         });
                         }catch(err){
                         showSysException(err);
                         }
                         });

/*Function for View More functionality on EDO landing page*/
var edoSeeMoreLinks = function(NumberOfElements,Obj,clsName){
	try{
		var element = '';
		if(clsName != 'undefined') {
			element = 'li.'+clsName;
		}
        
		$(Obj).parents().find(element).each(function(index) {
                                            if(index < NumberOfElements){
                                            $(this).removeClass(clsName).show();
                                            }
                                            });
		refreshList();
	}catch(err){
		showSysException(err);
	}
}


/**Function for Alphabetical Sorting**/
var alphabeticalSort = function(){
	try{
		$('ul.allItemList>li.items').tsort('div.offerDetailcontainer',{attr:'data-merchantname'});
		sortingOrder = "alphabeticalSort";
		refreshList();
	}catch(err){
		showSysException(err);
	}
};

/**Function for calculating number of Expiration Days**/
var daysExpiryCalculation = function(){
	try{
		var currentDate = new Date();
		$('ul.allItemList li').each(function(){
                                    var dateAvailable = $(this).find('.expCompareDate').text();
                                    var activeAvailable = $(this).find('.activeDate').text();
                                    var splitDate = dateAvailable.split("/");
                                    var avalMonth = splitDate[0];
                                    var avalDay  = splitDate[1];
                                    var avalYear = "20" + splitDate[2];
                                    var splitDateact = activeAvailable.split("/");
                                    var avalactMonth = splitDateact[0];
                                    var avalactDay  = splitDateact[1];
                                    var avalactYear = "20" + splitDateact[2];
                                    var newDate = new Date(avalYear,avalMonth -1, avalDay);
                                    var activeNewDate = new Date(avalactYear,avalactMonth -1, avalactDay);
                                    var daysLeft = dateDifference(currentDate,newDate);
                                    var mostRecentDiff = dateDifference(currentDate,activeNewDate);
                                    var mostRecentDiff = mostRecentDiff - 2;
                                    
                                    $(this).find('.actDaysLeft').html(''+mostRecentDiff+'');
                                    $(this).find('.expDaysLeft').html(''+daysLeft+'');
                                    
                                    /**Code for applying Color Dots**/
                                    if(daysLeft <= 3){
                                    $(this).find('.expiryDotInner').addClass('redDotColor');
                                    }
                                    if((daysLeft >= 4)&&(daysLeft <=10)){
                                    $(this).find('.expiryDotInner').addClass('yellowDotColor');
                                    }
                                    if(daysLeft > 10){
                                    $(this).find('.expiryDotInner').addClass('greenDotColor');
                                    }
                                    });
	}catch(err){
		showSysException(err);
	}
};

/**Function for calculating Date Difference**/
function dateDifference(date1,date2){
	try{
		var oneDay = 1000 * 60 * 60 * 24;
		var date1_ms = date1.getTime()
		var date2_ms = date2.getTime()
		var difference_ms = Math.abs(date1_ms - date2_ms);
		return Math.round(difference_ms/oneDay+1);
	}catch(err){
		showSysException(err);
	}
}

/**Function for Expiration Soon Sort**/
var sortExpirySoon = function(){
	try{
		$('ul.allItemList>li.items').tsort('span.edoDetailsDaysLeft');
		sortingOrder = "sortExpirySoon";
		refreshList();
	}catch(err){
		showSysException(err);
	}
}

/**Function for Most Recent Sort**/
var mostRecent = function(){
	try{
		$('ul.allItemList>li.items').tsort('span.actDaysLeft',{ order: 'desc'});
		sortingOrder = "mostRecent";
		refreshList();
	}catch(err){
		showSysException(err);
	}
}

/**Function for Featured Sort**/
var featuredSort = function(){
	try{
		$('ul.allItemList>li.items').tsort('span.featured');
		sortingOrder = "featuredSort";
		refreshList();
	}catch(err){
		showSysException(err);
	}
}
/**Function to refresh the list on change of view**/
var refreshList = function(){
	try{
		var visibleList = $('ul.allItemList>li.items').length - $(".edoDisplayNone").length;
		$('ul.allItemList>li.items').addClass('edoDisplayNone');
		/*if($('ul.allItemList').hasClass("showGridview")){
			$('ul.allItemList > li:nth-child(2n)').css("float", "right");
			$('ul.allItemList > li:nth-child(2n+1)').css("float", "left");
		}*/
		$('ul.allItemList > li.items').each(function(i){
                                            if(i < visibleList){
                                            $(this).removeClass('edoDisplayNone');
                                            }
                                            });
		return;
	}catch(err){
		showSysException(err);
	}
}


/** *********start code for edoLandingPageLoad- Sneha********* */
var lowerEDOOffset;
var upperEDOOffset;
var viewMoreHit;
var viewPopulatedEDODetails;
var viewMoreFlag = false;

function edoLandingPageLoad() {
	try{
		if (fromPageName == "edoDetail" && viewMoreFlag) {
			var edoDetails = getDataFromCache('EDO');
			lowerEDOOffset = 0;
			viewPopulatedEDODetails = [];
			if(edoDetails.extras.length > 10 ){
                upperEDOOffset = 10;
            }
            else{
                upperEDOOffset = edoDetails.extras.length;
                $(".btmBtns .viewMoreOffers").hide();
            }
            dfs.crd.edo.populateEDOLandingPageDiv(edoDetails, lowerEDOOffset,
                                                  upperEDOOffset);
		} else {
			viewMoreFlag = false;
			lowerEDOOffset = '';
			upperEDOOffset = '';
			sortingOrder = "";
			viewMoreHit = 1;
			viewPopulatedEDODetails = [];
			dfs.crd.edo.populateEDOLandingPage("EDO");
			dfs.crd.edo.populateEDOExclusiveOffer();
		}
	}catch(err){
		showSysException(err);
	}
}

dfs.crd.edo.populateEDOLandingPage = function(pageName) {
	try{
		lowerEDOOffset = 0;
		var edoDetails = dfs.crd.edo.getAllExtras(pageName);
		if (!jQuery.isEmptyObject(edoDetails)) {
			if (edoDetails.extras.length > 0){
				putDataToCache('EDO', edoDetails);
				if (edoDetails.extras.length > 10) {
					upperEDOOffset = 10;
				}else{
					upperEDOOffset = edoDetails.extras.length;
					$(".btmBtns .viewMoreOffers").hide();
				}
				dfs.crd.edo.populateEDOLandingPageDiv(edoDetails, lowerEDOOffset,
                                                      upperEDOOffset);
			}else{
				cpEvent.preventDefault();
				navigation('../edo/noOffer');
			}
		}
	}catch(err){
		showSysException(err);
	}
}


dfs.crd.edo.getAllExtras = function(pageId) {
	try{
		var edoDetails;
		var newDate = new Date();
		// we are passing hardcoded data tu url as parameter encoded into the url are required. once service team will remove required parameter we can use commented url.
		var ALLEXTRASURL = RESTURL + "extras/v1/extras?sort=featured&size=200&startIndex=0";
		edoDetails = getDataFromCache(pageId);
		if (jQuery.isEmptyObject(edoDetails)) {
			showSpinner();
			$.ajax({
                   type : "GET",
                   url : ALLEXTRASURL,
                   async : false,
                   dataType : 'json',
                   headers : prepareGetHeader(),
                   success : function(responseData, status, jqXHR) {
                   hideSpinner();
                   if (!validateResponse(responseData,"getAllExtraValidation")){ // Pen Test Validation
                   errorHandler("SecurityTestFail","","");
                   return;
                   }
                   edoDetails = responseData;
                   putDataToCache('EDO', edoDetails);
                   },
                   error : function(jqXHR, textStatus, errorThrown) {
                   hideSpinner();
                   var code=getResponseStatusCode(jqXHR);
                   dfs.crd.edo.edoErrorHandler(code,'','edoLandingPage');
                   }
                   });
		}
		return edoDetails;
	}catch(err){
		showSysException(err);
	}
}

dfs.crd.edo.populateEDOLandingPageDiv = function(edoDetails, lowerEDOOffset,
                                                 upperEDOOffset) {
    
	try{
		if (!isEmpty(edoDetails)) {
			var edoDetailsMain = "";
			var remainingEDOOfferLentgh;
			edoDetailsLength = edoDetails.extras.length;
			var ulClass = $("ul.allItemList").hasClass("showGridview") ? "class='allItemList showGridview'"
            : "class='allItemList showListview'";
            
			var edoDetailsText = "";
			var edoDetailsResponseData = edoDetails.extras;
			for(var key in edoDetailsResponseData){
				if(lowerEDOOffset != upperEDOOffset){
					var featured = lowerEDOOffset;
					var edoDetailsDaysLeft = edoDetails.extras[lowerEDOOffset].daysLeft;
					if (isEmpty(edoDetailsDaysLeft)) {
						edoDetailsDaysLeft = "";
					}
					var edoDetailsName = edoDetails.extras[lowerEDOOffset].name;
					if (isEmpty(edoDetailsName)) {
						edoDetailsName = "";
					}
					var edoRewardTypeData = edoDetails.extras[lowerEDOOffset].rewardType;
					if(edoRewardTypeData == "Statement Credit")
                    {
                        // Do Nothing
                    }
                    else{
                        edoRewardTypeData="<i>"+edoRewardTypeData+"</i>";
                    }
                    if(isEmpty(edoRewardTypeData)){
                        edoRewardTypeData = "";
                    }
					var edoDetailsMerchantName = edoDetails.extras[lowerEDOOffset].merchantName;
					if (isEmpty(edoDetailsMerchantName)) {
						edoDetailsMerchantName = "";
					}
					var edoDetailsExpires = edoDetails.extras[lowerEDOOffset].expires;
					if (isEmpty(edoDetailsExpires)) {
						edoDetailsExpires = "";
					}
					var edoDetailsLogo = edoDetails.extras[lowerEDOOffset].logo;
					if (isEmpty(edoDetailsLogo)) {
						edoDetailsLogo = "";
					}
					var edoDetailsCampaignId = edoDetails.extras[lowerEDOOffset].campaignId;
					if (isEmpty(edoDetailsCampaignId)) {
						edoDetailsCampaignId = "";
					}
                    
					var edoDetailsActualBeginDate = edoDetails.extras[lowerEDOOffset].actualBeginDate;
					if(isEmpty(edoDetailsActualBeginDate)){
						edoDetailsActualBeginDate = "";
					}
                    
					var addColorFlag = "";
					if (edoDetailsDaysLeft <= 3) {
						addColorFlag = "redDotColor";
					} else if ((edoDetailsDaysLeft >= 4)
                               && (edoDetailsDaysLeft <= 10)) {
						addColorFlag = "yellowDotColor";
					} else if (edoDetailsDaysLeft > 10) {
						addColorFlag = "greenDotColor";
					}
                    
					var exclusiveEdoDetailsJSON = "<li class='items' onclick=dfs.crd.edo.showEDODetailPage('"
                    + edoDetailsCampaignId
                    + "')><div class='partnerLogo'><img src='"
                    + edoDetailsLogo
                    + "' alt='partner logo' /></div><div class='offersInfo edoDetailsNameBlock''><div class='offerDetailcontainer' data-merchantName  = '"+edoDetailsMerchantName+"'>"
                    + edoDetailsName
                    + " at <span class='merchantName'>"
                    + edoDetailsMerchantName
                    + "</span></div><div class='expiryDot'><div class='expiryDotInner "
                    + addColorFlag
                    + "'></div></div><span class='expDate'><span class='spellChange'>Exp: </span><span class='expCompareDate'>"
                    + edoDetailsExpires
                    + "</span>"
                    +"<span class='forListview'> |  <span class='expDaysLeft'>"
                    +edoRewardTypeData
                    +"</span></span></span><span class='actDaysLeft'>"
                    + edoDetailsActualBeginDate
                    +"</span><span class='featured' style='display:none'>"
                    +featured
                    +"</span><span class='edoDetailsDaysLeft' style='display:none'>"
                    +edoDetailsDaysLeft
                    +"</span><span class='activeDate'></span></div><div class='rightArrow'><img src='../../images/edoBlueArrow.png' alt='right arrow'width='6' height='8' /></div></li>";
					edoDetailsMain += exclusiveEdoDetailsJSON;
					lowerEDOOffset++;
                    
				}
			}
			edoDetailsText += edoDetailsMain;
			if (isEmpty(viewPopulatedEDODetails["EDODETAILSDATA"])) {
				viewPopulatedEDODetails["EDODETAILSDATA"] = edoDetailsText;
                
			}else{
				edoDetailsTextPrepend = viewPopulatedEDODetails["EDODETAILSDATA"];
				edoDetailsTextPrepend += edoDetailsText;
				viewPopulatedEDODetails["EDODETAILSDATA"] = edoDetailsTextPrepend;
				edoDetailsText = edoDetailsTextPrepend;
			}
            
			var ulListTo = "<ul " + ulClass + ">" + edoDetailsText + "</ul>";
            
			$("#edoDetailsDisplay").html(ulListTo);
			if (sortingOrder == "mostRecent") {
				mostRecent();
			} else if (sortingOrder == "sortExpirySoon") {
				sortExpirySoon()
			} else if (sortingOrder == "alphabeticalSort") {
				alphabeticalSort();
			}
		}
		$(document).jqmData("landingOverlayFlag");
        $(document).jqmData("landingFlag",1);
        $(document).scroll(function(){
                           
                           if( $(document).jqmData("landingFlag") == 1 && inViewport($('.allItemList .items:visible:last')[0]) == true && !$('.allItemList .items:visible:last').hasClass("lastElement")){
                           $(document).jqmData("landingFlag", 0);
                           if(upperEDOOffset<=edoDetailsLength){
                           dfs.crd.edo.ViewMoreEDO();
                           if($(".edo-page .navBtns .ui-block-b").find('.changeViewBtn_listImg').length == 0){
                           $(".offerDetailcontainer").css("height","");
                           }
                           else{
                           
                           $(".offerDetailcontainer").css({"height":"28px","overflow":"hidden"});
                           $('.offerDetailcontainer').dotdotdot();
                           }
                           }
                           
                           }
                           });
	}catch(err){
		showSysException(err);
	}
    
}

/** *********end code for edoLandingPageLoad- Sneha********* */

function inViewport(el) {
    var r, html;
    if ( !el || 1 !== el.nodeType ) { return false; }
    html = document.documentElement;
    r = el.getBoundingClientRect();
    
    return ( !!r
            && r.bottom >= 0
            && r.right >= 0
            && r.top <= html.clientHeight
            && r.left <= html.clientWidth
            );
    
}
/** *********start code for edoHistoryPageLoad- Girish********* */
/*var edoHistoryDataActivityMain = "<div class='claimText'>Below are the Discover Extras that you have already claimed. Some Discover Extras offers may still be pending. Extras claimed via coupon are not tracked in your online history.</div> <ul class='historyList'>";*/
var edoHistoryDataActivityMain = "<div class='claimText'>Below are the Discover Extras that you have already claimed. Some Discover Extras offers may still be pending</div> <ul class='historyList'>";
var edoHistoryDataActivity = [];
var lowerEDOHistOffset;
var upperEDOHistOffset;

function edoHistoryLoad(){
	try{
		dfs.crd.edo.populateEdoHistory("EDOHISTORY");
	}catch(err){
		showSysException(err);
	}
}

dfs.crd.edo.populateEdoHistory = function(pageName){
	try{
		var edoHistory = dfs.crd.edo.getEdoHistoryData(pageName);
		lowerEDOHistOffset = 0;
		edoHistoryDataActivity = [];
		if(!jQuery.isEmptyObject(edoHistory)){
			if(edoHistory.history.length >10){
                upperEDOHistOffset = 10;
            }else{
				upperEDOHistOffset = edoHistory.history.length;
            }
            dfs.crd.edo.populateEdoHistoryPageDivs(edoHistory, lowerEDOHistOffset, upperEDOHistOffset);
		}else{
			var edoHistoryErrorText = "<div class='claimText'><p style='color:#566164;'>Thanks for visiting Extras. It looks like you haven't used any of your available offers yet. What are you waiting for? Start saving today!</p></div>";
			$("#edo_history").html(edoHistoryErrorText);
		}
	}catch(err){
		showSysException(err);
	}
}

dfs.crd.edo.getEdoHistoryData = function(pageId){
	try{
		var newDate = new Date();
		var EDOHISTORYURL = RESTURL + "extras/v1/extrashistory?"+newDate;
		var edoHistory = getDataFromCache(pageId);
		if(jQuery.isEmptyObject(edoHistory)){
			showSpinner();
			$.ajax({
                   type : "GET",
                   url : EDOHISTORYURL,
                   async : false,
                   dataType : "json",
                   headers : prepareGetHeader(),
                   success : function(responseData, status, jqXHR) {
                   hideSpinner();
                   if (!validateResponse(responseData,"extraHistoryValidation")){ // Pen Test Validation
                   errorHandler("SecurityTestFail","","");
                   return;
                   }
                   edoHistory = responseData;
                   putDataToCache(pageId, edoHistory);
                   },
                   error : function(jqXHR, textStatus, errorThrown) {
                   hideSpinner();
                   var code=getResponseStatusCode(jqXHR);
                   dfs.crd.edo.edoErrorHandler(code,'','edoHistory');
                   }
                   });
		}
		return edoHistory;
	}catch(err){
		showSysException(err);
	}
}

dfs.crd.edo.populateEdoHistoryPageDivs = function(edoHistory, pageId) {
	try{
		if (!jQuery.isEmptyObject(edoHistory)) {
			if(edoHistory.history.length != 0){
				var edoHistoryDataLocalActivity = "";
				var edoHistoryList = "";
				var amount;
				var merchant;
				var redeemedDate;
				$(edoHistory.history)
				.each(
                      function(i) {
                      if(lowerEDOHistOffset<upperEDOHistOffset){
                      amount = edoHistory.history[lowerEDOHistOffset].amount;
                      merchant = edoHistory.history[lowerEDOHistOffset].merchant;
                      redeemedDate = edoHistory.history[lowerEDOHistOffset].redeemedDate;
                      edoHistoryData = "<li class='listLi'><div class='ui-grid-a'><div class='ui-block-a'><span>$ "
                      + amount
					 + "</span></div> <div class='ui-block-b'><span>"
                      + merchant
                      + "</span></div> <div class='ui-block-c'><span>"
                      + redeemedDate
                      + "</span> </div></div></li>";
                      lowerEDOHistOffset++;
                      edoHistoryList += edoHistoryData;
                      }
                      });
					  var edoHistoryListULstart ="<ul><li class='listLi'><div class='ui-grid-a'><div class='ui-block-a'><span>Savings</span></div> <div class='ui-block-b'><span>Merchant</span></div><div class='ui-block-c'><span>Date</span> </div></div></li>";
				var edoHistoryListULend = "</ul>";
				
                if (isEmpty(edoHistoryDataActivity["EDOHISTORYTABLE"])) {
                    edoHistoryList = edoHistoryDataActivityMain+edoHistoryListULstart + edoHistoryList + edoHistoryListULend;
                    edoHistoryDataActivity["EDOHISTORYTABLE"] = edoHistoryList;
                }else{
                    var edoDetailsTextPrepend = edoHistoryDataActivity["EDOHISTORYTABLE"];
                    edoDetailsTextPrepend += edoHistoryList;
                    edoHistoryDataActivity["EDOHISTORYTABLE"] = edoDetailsTextPrepend;
                    edoHistoryList = edoDetailsTextPrepend;
                    
				}
				$("#edo_history").html(edoHistoryList);
			}else{
				var edoHistoryErrorText = "<div class='claimText'><p style='color:#566164;'>Thanks for visiting Extras. It looks like you haven't used any of your available offers yet. What are you waiting for? Start saving today!</p></div>";
				$("#edo_history").html(edoHistoryErrorText);
			}
		}
		$(document).jqmData("flag", 1);
        
        //for calling the lazy load function            
        $(document).scroll(function(){
                           if( !($("#overlay_wraper").css("display") == 'block') && $(document).jqmData("flag") == 1 && inViewport($('.historyList .listLi:visible:last')[0]) == true && !$('.historyList .listLi:visible:last').hasClass("lastElement")){   
                           $(document).jqmData("flag", 0);
                           if(upperEDOHistOffset<=edoHistory.history.length){
                           dfs.crd.edo.fnGetMoreData();
                           }	
                           }
                           });
	}catch(err){
		showSysException(err);
	}
}

dfs.crd.edo.fnGetMoreData = function(){
	var edoHistory = getDataFromCache('EDOHISTORY');
	lowerEDOHistOffset = upperEDOHistOffset;
	histRecordsLeft = edoHistory.history.length - lowerEDOHistOffset;
	
   	if(histRecordsLeft > 10){
		upperEDOHistOffset = upperEDOHistOffset+10;
		showViewMore = true;
	}else{
		upperEDOHistOffset = edoHistory.history.length ;
		showViewMore = false;
	}
    if(histRecordsLeft>0){
        dfs.crd.edo.populateEdoHistoryPageDivs(edoHistory, lowerEDOHistOffset, upperEDOHistOffset);
    }
    
}
/***********end code for edoHistoryPageLoad- Girish********* */

/** ********start code View more edoDetails - Amit************** */
dfs.crd.edo.ViewMoreEDO = function() {
	viewMoreFlag = true;
	viewMoreHit++;
	lowerEDOOffset = upperEDOOffset;
	recordsLeft = edoDetailsLength - lowerEDOOffset;
	if (recordsLeft <= 10) {
		$(".btmBtns .viewMoreOffers").hide();
	}
    
    
	if (recordsLeft > 10) {
		upperEDOOffset = upperEDOOffset+10;
		showViewMore = true;
	}else{
		upperEDOOffset = edoDetailsLength;
		showViewMore = false;
	}
	var edoDetails = getDataFromCache('EDO');
	if(recordsLeft>0){ 
        dfs.crd.edo.populateEDOLandingPageDiv(edoDetails, lowerEDOOffset,
                                              upperEDOOffset);
	}
    
}
/** **********************end code View more edoDetails - Amit*********************** */

/************Start code for edoDetail - Aditya **********/

dfs.crd.edo.showEDODetailPage = function(campaignId){
	try{
		dfs.crd.edo.getEDODetailPage(campaignId);
		edoCampaignId=campaignId;
	}catch(err){
		showSysException(err);
	}
}

dfs.crd.edo.getEDODetailPage = function(campaignId){
	try{
		if(isEmpty(campaignId)){
			return;
		}        
		var newDate = new Date();    
		var EDO_EXTRA_DETAIL_URL = RESTURL + "extras/v1/extradetail?"+newDate+"&campaignId="+campaignId;
		showSpinner();
		$.ajax({
               type : "GET",
               url : EDO_EXTRA_DETAIL_URL,
               async : true,
               dataType : 'json',
               headers:prepareGetHeader(),
               success : function(responseData, status, jqXHR) {
               hideSpinner();
               if (!validateResponse(responseData,"extraDetailValidation")){ // Pen Test Validation
               errorHandler("SecurityTestFail","","");
               return;
               }
               if (jqXHR.status != 200) {
               var code=getResponseStatusCode(jqXHR);			
               }else{					
               putDataToCache("EDO_EXTRA_DETAILS",responseData);
               navigation('edoDetail');
               }
               },
               error : function(jqXHR, textStatus, errorThrown) {
               hideSpinner();
               var code=getResponseStatusCode(jqXHR);
               dfs.crd.edo.edoErrorHandler(code,'','edoDetail');
               }
               });		
	}catch(err){
		showSysException(err);
	}
}

function edoDetailLoad(){
    try{
        if (deviceType=="Android" && parseInt(device.version)<4)
        {
            $("#calenderBtn").hide();
        }
        var edoExtraDetails=getDataFromCache("EDO_EXTRA_DETAILS");
        if (!jQuery.isEmptyObject(edoExtraDetails)) {
            dfs.crd.edo.populateEdoExtraDetailsPageDivs(edoExtraDetails);
        }
    }catch(err){
        showSysException(err);
    }
}

function saveToPhotosLoad(){
    try {
        var edoExtraDetails=getDataFromCache("EDO_EXTRA_DETAILS");
        $("#offerLogo").attr("src",edoExtraDetails.logo);
		$("#code").html(edoExtraDetails.barCodeValue);
		$("#expDate").html(edoExtraDetails.expires);
		$("#edoDetailHeading").html(edoExtraDetails.name+" at "+edoExtraDetails.merchantName);
    } catch (err) {}
}

dfs.crd.edo.populateEdoExtraDetailsPageDivs = function(edoExtraDetails){
	try{
		$("#edoDetailExpDate").html(edoExtraDetails.expires);
		$("#edoDetailName").html(edoExtraDetails.name+" at "+edoExtraDetails.merchantName);
		$("#edoDetailTerm").html(edoExtraDetails.terms);
		if((edoExtraDetails.awardUsageType)=="ONLINE")
		{
            $("#detailBtn").css("display", "none");
            $(".edoDetailInformation").css("display", "none");
		}
		$("#edoMerchantLogoImage").html("<img height='105px' width='105px' src='"+edoExtraDetails.logo+"'></img>");
		
		var couponDiv = "<div class='coupon-dtl'><div>Coupon Code: <span>"
        + edoExtraDetails.barCodeValue
        + "</span></div><p><a href='#' onclick=\"navigation('saveToPhotos');dfs.crd.sct.saveOfferToPhotos();\">Save to Your Photos</a></p></div>"
        if (edoExtraDetails.isCouponType) {
            $("#edoCouponDiv").css("display", "block");
            $("#edoCouponDiv").html(couponDiv);
        }
	}catch(err){
		showSysException(err);
	}
}
/************end code for edoDetail - Aditya **********/

/***********Exclusive EDO offer Start - Nishikant********* */
dfs.crd.edo.populateEDOExclusiveOffer = function(pageName){
	try{
		// for testing , if you willing to access local edoExclusive.jsonthen pass true as a second parameter in below method.
		var edoExclusiveJson=dfs.crd.edo.getEDOExclusiveOfferPCCData("edoLandingPage",true);
		if(!jQuery.isEmptyObject(edoExclusiveJson)){
			var edoExclusiveOfferText="";
			for (var key in edoExclusiveJson) {
				if (edoExclusiveJson.hasOwnProperty(key)) {					
					if(key == "EXCLUSIVE_OFFER_TEMPLATE"){
						continue;
					}
					var val=edoExclusiveJson[key];
					if(!isEmpty(val)){
						var edoOfferDetailsArray=val.split("||");
						if(edoOfferDetailsArray.length != 4){
							return;
						}
                        
						var edoOfferLogo=edoOfferDetailsArray[0];
						var edoOfferText1=edoOfferDetailsArray[1];
						var edoOfferText2=edoOfferDetailsArray[2];
						var edoOfferText3=edoOfferDetailsArray[3];
						var edoOfferCampaignId=edoOfferDetailsArray[4];
                        
						if(isEmpty(edoOfferLogo)){
							edoOfferLogo="";
						}
						if(isEmpty(edoOfferText1)){
							edoOfferText1="";
						}
						if(isEmpty(edoOfferText2)){
							edoOfferText2="";
						}
						if(isEmpty(edoOfferText3)){
							edoOfferText3="";
						}
						if(isEmpty(edoOfferCampaignId)){
							edoOfferCampaignId="";
						}
                        
						var exclusiveOfferDivTemplate="<div class='featuredOffers'><div class='ui-grid-b'>" +
						"<div class='ui-block-a'><img src='../../images/exclusive.png' alt='exclusive tag' id='exImg' " +
						"width='65' height='21' /></div><div class='ui-block-b'><img src="+edoOfferLogo+" alt='featured logo'" +
						" id='ftrdLogo' width='39' height='39' /></div><div class='ui-block-c'><span>" +
						"<span class='edoCbb'>"+edoOfferText1+"</span> "+edoOfferText2+"</span><br/>" +
						"<span class='offerDate'>"+edoOfferText3+"</span></div></div></div>";
                        
						edoExclusiveOfferText += exclusiveOfferDivTemplate;
					}
				}
			}
            
			$("#exclusiveEDOOffer").html(edoExclusiveOfferText);
		}
	}catch(err){
		showSysException(err);
	}
}


//getting edoExclusive.json from server
dfs.crd.edo.getEDOExclusiveOfferPCCData =function(page,accessLocalFile){
	try{
		if(accessLocalFile){
			var edoJsonDetails = getContentJson("edoExclusive");
			return edoJsonDetails;
		}
		var newDate = new Date();	
		var pageId="EDO_EXCLUSIVE_JSON";
		var EDOJSONURL = HREF_URL + "json/rewards/edoExclusive.json?"+newDate+"";
		var edoJsonDetails=getDataFromCache(pageId);
		if(jQuery.isEmptyObject(edoJsonDetails)){
			showSpinner();
			$.ajax({
                   type : "GET",
                   url : EDOJSONURL,
                   async : false,
                   dataType : 'json',
                   success : function(responseData, status, jqXHR) {
                   hideSpinner();
                   if (jqXHR.status != 200) {
                   var code=getResponseStatusCode(jqXHR);
                   dfs.crd.edo.edoErrorHandler(code,'',page);
                   }else{
                   edoJsonDetails = responseData;
                   putDataToCache(pageId,edoJsonDetails);
                   }
                   },
                   error : function(jqXHR, textStatus, errorThrown) {
                   hideSpinner();
                   var code=getResponseStatusCode(jqXHR);
                   dfs.crd.edo.edoErrorHandler(code,'',page);
                   }
                   });
		}
		return edoJsonDetails;
	}catch(err){
		showSysException(err);
	}
}
/***********Exclusive EDO offer End - Nishikant ********* */

/* Invokes Calendar Plugin- Abhinav */

function clickAddtoCalendar()
{
	try{
		var edoExtraDetails=getDataFromCache("EDO_EXTRA_DETAILS");
		if (!jQuery.isEmptyObject(edoExtraDetails)) {
			var datecurr = new Date(edoExtraDetails.currentDate);
			var dateexp = new Date(edoExtraDetails.expires);
			var datecal = new Date (edoExtraDetails.expires);
			datecal.setDate(dateexp.getDate()-5);
            
			if (dateexp < datecurr)
			{
				navigator.notification.alert('This Offer has Expired',function dissmissAlert() {},'Discover','OK');
				return;
			}
			
			var startDate = edoExtraDetails.currentDate;
			
			// if (datecal <= datecurr)
            //	{	
			// }
			// else {
            // var startDate = convertDateforCalendar(datecal);
			// }
            
			startDate = startDate.split('/').join('');
			var offerExpireDate = edoExtraDetails.expires;
			offerExpireDate = offerExpireDate.split('/').join('');
            
			eventAdd(startDate,offerExpireDate,edoExtraDetails.merchantName, edoExtraDetails.name,edoExtraDetails.merchantName,null);
		}
	}catch(err){
		showSysException(err);
	}
}

/* End of Calendar Plugin Code- Abhinav */

/* Code to add event to Calendar- Abhinav */

function eventAdd(startDate, expiryDate, title, description, location, hoursPrecedingEndDate)
{
	try {
		var title = title;
		var description = description;
		var location = location;
		var hoursPrecedingEndDate = hoursPrecedingEndDate;
        
		var startMonth = parseInt(startDate.substring(0,2));
		var startDay = parseInt(startDate.substring(2,4));
		var startYear = parseInt(startDate.substring(4));
		var expMonth = parseInt(expiryDate.substring(0,2));
		var expDay = parseInt(expiryDate.substring(2,4));
		var expYear = parseInt(expiryDate.substring(4));
        
		var startDateComponents = new Array(startYear, startMonth - 1, startDay, 12, 00);
		var endDateComponents = new Array(expYear, expMonth - 1, expDay, 12, 00);
        
        if (deviceType=="Android")
        {
            EventManager.prototype.createEvent(null,null,startDateComponents,endDateComponents,title,description,null);
        }
        else {
            EventManager.prototype.createEvent(startDateComponents, endDateComponents, title, description, "", hoursPrecedingEndDate);
        }
        
	} catch (err) {showSysException(err)}
}


/* Below function is for Extras Add to Photos functionality- Abhinav */

function addToPhotos()
{
	Screenshot.prototype.takeScreenshot(function success() {}, null);
	//window.plugins.Screenshot.takeScreenshot(function() {navigator.notification.alert("Offer Saved to Photos.",function success(){},"Discover","OK"); }, function(e) {console.log("error: " + e);} );

}

$("#edoLandingPage-pg").live('pageshow',function(event){
                             if($(".edo-page .navBtns .ui-block-b").find('.changeViewBtn_listImg').length == 0){
                             
                             $(".offerDetailcontainer").css("height","");
                             
                             }
                             else{
                             $(".offerDetailcontainer").css({"height":"28px","overflow":"hidden"});
                             $('.offerDetailcontainer').dotdotdot();
                             }
                             });

var fullPurchaseString = [];

$("#edoLandingPage-pg").live('pagebeforeshow',function(event){	
                             $(".edo-page .navBtns .ui-block-b").toggle(function(){
                                                                        
                                                                        $("#changeViewBtn").removeClass("changeViewBtn_gridImg").addClass("changeViewBtn_listImg");
                                                                        $("ul.allItemList").removeClass("showListview").addClass("showGridview"); /*For Changing from listview to gridview*/
                                                                        $("#edoDetailsDisplay").removeClass("edoDetailsDisplay_showListview").addClass("edoDetailsDisplay_showGridview");
																		setGridwpwidth();
																		$(".spellChange").html("Expires:");
                                                                        $(".partnerLogo img").css({"height":"97px","width":"97px"});
                                                                        dfs.crd.sct.extrasGridListSwitch('GridView');//passing sitecatalyst variable for Select Grid View Vs. List View
                                                                        putDataToCache("EDOOFFERVIEWTYPE","Grid");
                                                                        $(".offerDetailcontainer").css({"height":"28px","overflow":"hidden"});
                                                                        $('.offerDetailcontainer').dotdotdot();			
                                                                        },function(){
                                                                        
                                                                        $("#changeViewBtn").removeClass("changeViewBtn_listImg").addClass("changeViewBtn_gridImg");
                                                                        $("ul.allItemList").removeClass("showGridview").addClass("showListview");
                                                                        $("#edoDetailsDisplay").removeClass("edoDetailsDisplay_showGridview").addClass("edoDetailsDisplay_showListview");
                                                                        $(".edoDetailsDisplay_showListview").css('width','100%');
																		$(".spellChange").html("Exp: ");
                                                                        $(".partnerLogo img").css({"height":"49px","width":"49px"});
                                                                        
                                                                        if($('ul.allItemList').hasClass("showListview")){
                                                                        $('ul.allItemList>li:nth-child(even)').css("float", "");
                                                                        $('ul.allItemList>li:nth-child(odd)').css("float", "");
                                                                        }			
                                                                        dfs.crd.sct.extrasGridListSwitch('ListView');//passing sitecatalyst variable for Select Grid View Vs. List View
                                                                        putDataToCache("EDOOFFERVIEWTYPE","List");
                                                                        $(".offerDetailcontainer").each(function(index){
                                                                                                        $(this).trigger("originalContent", function( content ) {
                                                                                                                        $(this).html(content);
                                                                                                                        });
                                                                                                        });
                                                                        $(".offerDetailcontainer").css({"height":"","overflow":"visible"});
                                                                        });
                             
                             
                             var buttonView=getDataFromCache("EDOOFFERVIEWTYPE");
                             if(buttonView == undefined)
                             {
                             // Do Nothing
                             }
                             else{
                             if(buttonView == 'Grid')
                             {
                             $(".edo-page .navBtns .ui-block-b").trigger('click'); 
                             }
                             else{
                             // Do Nothing
                             }
                             }
                             $('#edoLandingPage-pg #edoLandingSortOptions .ui-select .ui-btn-text').text('Sort');
                             dotdotdotLoad();
                             
                             });


/*----code for navigating to edo landing page on click of edo overlay button----*/
$(".edo-page").live("pageshow",function(){
                    $("#overlay_wraper .overlayBtn .close-overlay").click(function(){
                                                                          $.mobile.changePage("edoLandingPage.html");
                                                                          });
                    });
$("#mapDirections-pg").live("pagebeforeshow",function(){
                                        var netHeight=$(window).height()-$("#pg-header").outerHeight();
                                        $("#map_canvas,#map_canvas2").height(netHeight)
                                        });


$("#edoLandingPage-pg").live('pagehide',function(event){
                             try{
                             delete dotdotdotLoad();
                             }catch(err){
                             showSysException(err);
                             }
                             });

$('#viewMap-pg').live('pageshow',function(){
                      //showSpinner();
                      });
dfs.crd.edo.edoErrorHandler = function(errorCode,customErrorMessage,menuHglt){
	try{
        if(errorCode == "2007"){
            noOfferSCVariables();//sitecatalyst for "No Offers Available" Page
        }else if(errorCode == "500"){
            errorCode= "500_EDO";
            techIssueSCVariables();//sitecatalyst for Technical Difficulties Page
        }
        
        errorHandler(errorCode,'',menuHglt);
        
	}catch(err){
		showSysException(err);
	}
}