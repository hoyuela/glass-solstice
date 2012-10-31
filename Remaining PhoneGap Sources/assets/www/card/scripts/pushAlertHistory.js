
/* Name Space */
dfs.crd.push.alert = dfs.crd.push.alert || {};

dfs.crd.push.alert.globalStrList="";

alertHistoryLoad = function ()
{
	try{		
        // Page Initialization for Alery History screen
        dfs.crd.push.alert.populateAlertHistory("ALERTHISTORY");
        
	}catch(err){
		showSysException(err);
	}
}


/*
 * Description:Function called on "pagebeforeshow" for Alert History screen
 */
dfs.crd.push.alert.populateAlertHistory = function(pageName)
{	
	try{
		var alertHistory = dfs.crd.push.alert.getAlertHistoryData(pageName,0,11,false);
		if (!jQuery.isEmptyObject(alertHistory))
        {
			dfs.crd.push.alert.populateAlertHistoryPageDivs(alertHistory, pageName);
		}
        else
        {
            $(".main_content").hide();
            $("#red_textfont").html('<br/>You have no alerts at this time.<br/>Click above to setup or modify your alert settings').show();
            $('.main_heading_paragraph').hide();
            $(".deleted_messages").hide(); 
            $("#specialNotes").hide(); 
        }
	}catch(err)
	{
		showSysException(err);
	}
}


/* Description: Function to call Alert History GET Service
 */
dfs.crd.push.alert.getAlertHistoryData = function(pageId,dataStartPosition,dataSize,isMoreData)
{
    try
    {
        var alertHistory = getDataFromCache(pageId);
        if (jQuery.isEmptyObject(alertHistory) || isMoreData) 
		{
            var newDate = new Date();	
            var ALERTHISTORYURL = RESTURL + "msghist/v1/notification/history?" + newDate + "&start="+ dataStartPosition +"&size="+ dataSize ;
            //alert(ALERTHISTORYURL + "from alert history GET, more data: " + isMoreData);
            
            var isAsync = false;
            //if(isMoreData)
              //  isAsync = true;
            
            showSpinner();
            $.ajax
             ({
             type : "GET",
             url : ALERTHISTORYURL,
             async : false,
             dataType : 'json',
             headers:prepareGetHeader(),
             
              success : function(responseData, status, jqXHR) 
              {
              hideSpinner();
              if (jqXHR.status != 200) {
              e.preventDefault();
              var code=getResponseStatusCode(jqXHR);
              errorHandler(code,'','pushAlertHistory');
              } else {
             
              alertHistory = responseData["notifications"];
            
              /*/ JSON File read - needs to be removed once calling the service
               hideSpinner();
               pushPreference = getContentJson("pushPreference");  
               if (jQuery.isEmptyObject(pushPreference)){
                return;
               }
               alert("push preferences putting in cache");*/
            
              //if(!isMoreData)
                //putDataToCache(pageId, alertHistory);
            
              }                   
              },
              error : function(jqXHR, textStatus, errorThrown) {
              hideSpinner();
              var code=getResponseStatusCode(jqXHR);
              errorHandler(code,'','pushAlertHistory');
              }
             });
        }
        return alertHistory;
    }
    catch(err)
	{
        hideSpinner();
		showSysException(err);
	}
}


// Inserts message history data from Json to the Li elements of Alert History Screen

dfs.crd.push.alert.insertJsonData = function(json, totalElementsVisible,isMoreData) {
    
    var strList = "";
    var cls = "";
    var dynamicId = "da-";
    $.each(json, function (k, v)
           {
           var strDynamicId = v["reqId"];
           
           if (k < totalElementsVisible) {
           cls = 'showElm';
           }
           else {
           //cls = 'hideElm';
           $('#id_load_btn').show();
           return strList;
           }
           
           if(isMoreData)
           cls = 'hideElm';
           
           var comment = v["notificationText"];
           
           var sentDate = v["sentDate"].split(" ")[0];
           var sentTime = v["sentDate"].split(" ")[1] + " " + v["sentDate"].split(" ")[2].toLowerCase();
           
           var pageID, viewLinkLabel;
           
           var keyName = v["customData"].split(",")[1].split("=")[0].toLowerCase();
           
           switch(keyName){
           case "pagecode":
           pageID =  v["customData"].split(",")[1].split("=")[1];
           break;
           case "buttontext":
           viewLinkLabel =  v["customData"].split(",")[1].split("=")[1];
           break;
           }
           
           keyName = v["customData"].split(",")[0].split("=")[0].toLowerCase();
           
           switch(keyName){
           case "pagecode":
           pageID =  v["customData"].split(",")[0].split("=")[1];
           break;
           case "buttontext":
           viewLinkLabel =  v["customData"].split(",")[0].split("=")[1];
           break;
           }
                      
           if (v["msgReadInd"] == "Y") {
           
           strList += '<li id="' + strDynamicId + '" class="' + cls + '"><div class="ui-grid-c" style="font-weight:normal"><div class="ui-block-a" style="font-weight:normal"><fieldset data-role="controlgroup"><input type="checkbox" class="chkbox custom" /></fieldset></div><div style="font-weight:normal" class="ui-block-b"><span>' + sentDate + '</span><br/><div>'+ sentTime +'</div> </div><div style="font-weight:normal" class="ui-block-c"><span class="cls_description">' + v["subject"] + '</span><br/><span id="sub_detail">'+ comment +'</span></div><div class="ui-block-d" id="no_wrap"><div class="right_arrow deactive_down_arrow"></div><span class="links boldtext"><a href="#" onclick="dfs.crd.push.alert.pushHistoryMessageRedirect(\'' + pageID + '\',\'' + strDynamicId + '\')">' + viewLinkLabel.toUpperCase() + '</a></span></div></div></li>';
           }
           else {
           
           strList += '<li id="' + strDynamicId + '" class="' + cls + '"><div class="ui-grid-c"><div class="ui-block-a"><fieldset data-role="controlgroup"><input  type="checkbox" class="chkbox custom"/></fieldset></div><div class="ui-block-b"><span>' + sentDate + '</span><br/> <div>'+ sentTime +'</div> </div><div class="ui-block-c"><span class="cls_description">' + v["subject"] + '</span><br/><span id="sub_detail">'+ comment +'</span></div><div class="ui-block-d" id="no_wrap"><div class="right_arrow deactive_down_arrow"></div><span class="links boldtext"><a href="#" onclick="dfs.crd.push.alert.pushHistoryMessageRedirect(\'' + pageID + '\',\'' + strDynamicId + '\')">' + viewLinkLabel.toUpperCase()  + '</a></span></div></div></li>';
           }
           
           });
    return strList;
}

// Function to mark the message as Read and redirect user to the page
dfs.crd.push.alert.pushHistoryMessageRedirect = function(pageToRedirect,msgID)
{
    var listToBeMarkRead = [];
    listToBeMarkRead.push(msgID);                                                                                        
    var statusReturned = dfs.crd.push.alert.postAlertHistoryMessage('markRead',listToBeMarkRead);
    
    switch (pageToRedirect.toLowerCase()) {
        case "acstmt":
            navigation('../statements/statements');
            break;
        case "payment":
            navigation('../payments/paymentsLanding');
            break;
        case "payhist":
            navigation('../payments/paymentsHistory');
            break;
        case "acact":
            navigation('../statements/accountActivity');
            break;
        case "cbbrem":
            navigation('../rewards/cashbackBonusSignup1');
            break;
        case "redeemcbb":
            navigation('../rewards/redemptionLanding');
            break;
        case "redeemmiles":
            navigation('../rewards/milesRedeem');
            break;   
        default:
            navigation('../pushNotification/alertHistory');
            break;
    }
}

/*
Description:Populates the page divs for the Manage Notification page. Sets the Dynamic content 
*/
dfs.crd.push.alert.populateAlertHistoryPageDivs = function(responseData, pageId)
{     
	try
    {
        //Cotains Alert History Utility Functions
        
        function blank(a) {
            if (a.value == a.defaultValue) a.value = "";
        }
        
        function unblank(a) {
            if (a.value == "") a.value = a.defaultValue;
        }
        
        var callToJqTransform = function() { 
            $('#comments-list').jqTransform({imgPath:'images/'});
            $('#notifsettings').jqTransform({imgPath:'images/'});
            $('#tnc').jqTransform({imgPath:'images/'});	
            
        }
        
        var records_to_display = 10;
        //var totalElementsVisible = 4;
    
        $('#id_load_btn').hide();
        dfs.crd.push.alert.globalStrList = dfs.crd.push.alert.insertJsonData(responseData, records_to_display,false);
        
        $("#comments-list").html('');
        $("#comments-list").data("edit", "0");
        
        if(dfs.crd.push.alert.globalStrList!="")
            $("#comments-list").html(dfs.crd.push.alert.globalStrList);
        else
        {   
            $(".main_content").hide();
            $("#red_textfont").html('<br/>You have no alerts at this time.<br/>Click above to setup or modify your alert settings').show();
            $('.main_heading_paragraph').hide();
            $(".deleted_messages").hide();
            $("#specialNotes").hide();
        }

        /*Alert History SCRIPT*/
        
        $(".red_textfont").hide();
        $("#no_settings_saved").hide();     // Div for "your changes have not been saved text."
        
        /*
         * Load More button/link  Handler.
         */
        $('.loadbutton').click(function () {
                $('#id_load_btn').hide();
                var lastVisibleIndex = $("#comments-list li.showElm").length - 1;
                
                var moreJSON = dfs.crd.push.alert.getAlertHistoryData("ALERTHISTORY",lastVisibleIndex+1,records_to_display+1,true);
                var localStrList =  dfs.crd.push.alert.insertJsonData(moreJSON, records_to_display,false);
                
                if(localStrList!="")
                {
                    dfs.crd.push.alert.globalStrList += localStrList;
                    $("#comments-list").html(dfs.crd.push.alert.globalStrList);
                }
                
                dfs.crd.push.alert.alertHistoryListViewEvents();
        });
        
        /*
         * Delete Button Event Handler.
         */
        
        $('#id_del_btn').click(function (event) {
                               var listToBeDeleted = [];
                               var lastVisibleIndex = $("#comments-list li.showElm").length;
                               $('.chkbox').each(function (i) {
                                        if (this.checked) 
                                            {
                                            var elementId = $(this).parents('li').attr("id");
                                            $(this).parents("li:last").remove();
                                            listToBeDeleted.push(elementId);
                                            lastVisibleIndex--;
                                            }
                                });
                                // call delete REST with this id
                                var statusReturned;
                                if(listToBeDeleted.length > 0)
                                    statusReturned = dfs.crd.push.alert.postAlertHistoryMessage('delete',listToBeDeleted);
                                        
                                if(true)
                                {
                                $('#id_del_btn').removeClass('ui-btn-hover-c').addClass('ui-disabled');
                                $('body').animate({scrollTop:0}, 'slow');
                                $("#deleted_messages").addClass('deleted_messages').html("Your message(s) has been deleted").show();
                               
                                if (lastVisibleIndex == 0) {
                                    //var lastVisibleIndex = $("#comments-list li.showElm").length - 1;
                                    $('#id_load_btn').hide();
                                    var moreJSON = dfs.crd.push.alert.getAlertHistoryData("ALERTHISTORY",0,records_to_display+1,true);
                               
                                    if(!jQuery.isEmptyObject(moreJSON))
                                    {
                                        var localStrList =  dfs.crd.push.alert.insertJsonData(moreJSON, records_to_display,false);
                                        dfs.crd.push.alert.globalStrList = localStrList;
                                        $("#comments-list").html(dfs.crd.push.alert.globalStrList);
                                        dfs.crd.push.alert.alertHistoryListViewEvents();
                                    }
                                    else
                                    {
                                        $(".main_content").hide();
                                        $("#red_textfont").html('<br/>You have no alerts at this time.<br/>Click above to setup or modify your alert settings').show();
                                        $('.main_heading_paragraph').hide();
                                        $(".deleted_messages").hide();
                                        $("#specialNotes").hide();
                                    }
                                }
                               dfs.crd.push.alert.alertHistoryDefaultView();
                               }
                               else
                               {
                               $('body').animate({scrollTop:0}, 'slow');
                               $("#deleted_messages").addClass('deleted_messages').html("Your message(s) could not be deleted because of server outage").show();
                               dfs.crd.push.alert.alertHistoryDefaultView();
                               }
        });
        
        /*
         * Cancel Button Event Handler.
         */
        $('#id_can_btn').click(function () {
                               dfs.crd.push.alert.alertHistoryDefaultView();
                               });
        
        /*
         * Edit Button Event Handler.
         */
        $('#id_edit_btn').click(function () {
                                $(".ui-checkbox").removeClass("ui-checkbox"); 
                                dfs.crd.push.alert.alertHistoryEditView();
                                
                                $('#comments-list .cls_description,#comments-list .right_arrow,#comments-list .ui-grid-c .ui-block-b').toggle(
                                        function () {
                                                    $(this).parents('.ui-grid-c').children('.ui-block-d').children('.right_arrow').removeClass('deactive_down_arrow').addClass('active_down_arrow');
                                                                                                        
                                                    $(this).parents('.ui-grid-c').children('.ui-block-c').children('#sub_detail').slideDown(500).show();
                                                    $(this).parents('.ui-grid-c').children('.ui-block-c').css({'height': 'auto','font-weight': 'normal'});
                                                                                                        
                                                    $(this).parents('.ui-grid-c').children('.ui-block-b').css('font-weight', 'normal');
                                                    }, 
                                                                                                        
                                        function () {
                                                    $(this).parents('.ui-grid-c').children('.ui-block-d').children('.right_arrow').removeClass('active_down_arrow').addClass('deactive_down_arrow');
                                                                                                        
                                                    $(this).parents('.ui-grid-c').children('.ui-block-c').children('#sub_detail').slideUp(500).show();
                                                                                                        
                                });
        });
        
        
        
        dfs.crd.push.alert.alertHistoryListViewEvents();
        
        $('#sub_detail').hide();
        $("#deleted_messages").html('');
        $('#id_del_btn').addClass('ui-disabled');
        
        dfs.crd.push.alert.alertHistoryDefaultView();

	}
    catch(err)
	{
		showSysException(err);
	}
}


/*
 Event handlers for Alert Message List View item
 */
dfs.crd.push.alert.alertHistoryListViewEvents = function()
{
    // SlideUp/SlideDown the alert message
    $('#comments-list .cls_description,#comments-list .right_arrow,#comments-list .ui-grid-c .ui-block-b').toggle(
                        function () {
                                    $(this).parents('.ui-grid-c').children('.ui-block-d').children('.right_arrow').removeClass('deactive_down_arrow').addClass('active_down_arrow');
                                                                                                                  
                                    $(this).parents('.ui-grid-c').children('.ui-block-c').children('#sub_detail').slideDown(500).show();
                                                                                                                  
                                    var elementId = $(this).parents('li').attr("id");
                                    //localStorage[elementId] = 1;                                                                            
                                    //alert("mark read row id: " + elementId);
                                                                                                                  
                                    // call mark Read REST with this row id and update the view on success
                                    var listToBeMarkRead = [];
                                    listToBeMarkRead.push(elementId);                                                                                        
                                                                                                                  
                                    var statusReturned = dfs.crd.push.alert.postAlertHistoryMessage('markRead',listToBeMarkRead);
                                    
                                    $(this).parents('.ui-grid-c').children('.ui-block-c').css({'height': 'auto','font-weight': 'normal'});
                                    $(this).parents('.ui-grid-c').children('.ui-block-b').css('font-weight', 'normal');
                                                                                                                  
                                    }, 
                                    function () {
                                    $(this).parents('.ui-grid-c').children('.ui-block-d').children('.right_arrow').removeClass('active_down_arrow').addClass('deactive_down_arrow');
                                    $(this).parents('.ui-grid-c').children('.ui-block-c').children('#sub_detail').slideUp(500).show();
                                                                                                                  
                                    }); 
    
    // Tracks alert message checkboxes
    $(".chkbox").change(function (e) {
                        var selectedCount = $("input['class=chkbox']:checked").length;
                        if (selectedCount) {
                        $('#id_del_btn').removeClass('ui-disabled');
                        } else {
                        $('#id_del_btn').addClass('ui-disabled');
                        }
                        return false;
                        });
}

// Function to display default view of Alert History
dfs.crd.push.alert.alertHistoryDefaultView = function()
{
    $("#comments-list").data("edit", "0");
    $('.ui-grid-c').children('.ui-block-a').hide();
    $('#id-main-content .ui-grid-c').children('.ui-block-b').css("padding","5px 0px 5px 5px");
    $('#id_edit_btn').show();
    $('#id_can_btn').hide();
    $('#id_del_btn').hide();
}


// Function to display edit view of Alert History
dfs.crd.push.alert.alertHistoryEditView = function()
{
    $("#comments-list").data("edit", "1");
    $('.ui-grid-c').children('.ui-block-a').css("display", "block");
    $('#id-main-content .ui-grid-c').children('.ui-block-b').css("padding","5px 0px 5px 15px");
    $('#id_edit_btn').hide();
    $('#id_can_btn').show();
    $('#id_del_btn').show();
}


// Post Message Alert History to mark Read or Delete messages
dfs.crd.push.alert.postAlertHistoryMessage = function (msgAction, msgIdList) 
{
	try{
		var newDate = new Date();	
		var ALERTHISTORYURL = RESTURL + "msghist/v1/notification?" + newDate;
        //alert(ALERTHISTORYURL + " post alert history, action: " + msgAction + " msgID List: " + msgIdList);
        
        var dataJSON = {
			"action": msgAction,
			"reqId": msgIdList
		};
		var dataJSONString = JSON.stringify(dataJSON);
        
        //alert("AlertHist Message JSON: " + dataJSONString);
        var callAsync = true;
        if(msgAction == "delete")
            callAsync = false;
         
        showSpinner();
		$.ajax({
			type : "POST",
			url : ALERTHISTORYURL,
			async : true,
			dataType : 'json',
			data :dataJSONString,
			headers:preparePostHeader(),
			success : function(response, status, jqXHR) {
              hideSpinner();
				if (jqXHR.status != 200 & jqXHR.status != 204) {
					var code=getResponseStatusCode(jqXHR);
					//errorHandler(code,'','postAlertHistory');
                    return false;
				} else {
                    // Message updated / deleted successfully, return the status
                    //alert(response["resultCode"]);
                    if(response["resultCode"] == 200)
                    {
                        killDataFromCache("ALERTHISTORY");
                        return true;
                    }
                    else
                    {
                        return false;
                    }
				}
               },
            error : function(jqXHR, textStatus, errorThrown) {
                hideSpinner();
				var code=getResponseStatusCode(jqXHR);
				//errorHandler(code,'','postAlertHistory');
                
                return false;
			}
		});
	}catch(err)
    {
        hideSpinner();
        return false;
		//showSysException(err);
	}
}




