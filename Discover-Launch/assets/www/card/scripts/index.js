$("#login-pg").live("pagebeforeshow", function() {
	var localVersionInfoMsgDate=localStorage.getItem("VersionInfoMsgDate");
	var versionInfoMsg=localStorage.getItem("VersionInfoMsg");
	if(!isEmpty(localVersionInfoMsgDate) &&  !isEmpty(versionInfoMsg)){
		$(".new_version").html("A new version of the App is available! <a href='#' onclick='redirectToPageAfterConfirm(\"updateAppVersion\")' class='ui-link'>Update Now</a></div>");
	}else{
		$(".new_version").html("");	
	}
});

function termsUseLoad(){
	try{
	var staticContentJson=getStaticContentData(toPageName,false,true);
		if(!jQuery.isEmptyObject(staticContentJson)){
			var staticContentText = staticContentJson.HandsetTermsUse;
			$("#termsUseText").html(staticContentText);
			$("#termsUse-pg .redirectPrvStat").live("click",function(){
				navigation("../common/privacyPolicy");
			})
		}
	}catch(err){
		showSysException(err);
	}
}


function termsUseMoreLoad(){
	try{
	var staticContentJson=getStaticContentData(toPageName,false,false);
		if(!jQuery.isEmptyObject(staticContentJson)){
			var staticContentText = staticContentJson.HandsetTermsUse;
			$("#termsUseText").html(staticContentText);
			$("#termsUseMore-pg .redirectPrvStat").live("click",function(){
				navigation("../common/privacyPolicyMore");
			})
		}
	}catch(err){
		showSysException(err);
	}
}

function privacyPolicyLoad(){
	try{
		var staticContentJson=getStaticContentData(toPageName,false,true);
		if(!jQuery.isEmptyObject(staticContentJson)){
			var staticContentText = staticContentJson.HandsetPrivacyStatement;
			$("#privacyPolicyText").html(staticContentText);
		}
	}catch(err){
		showSysException(err);
	}
}


function privacyPolicyMoreLoad(){
	try{
		var staticContentJson=getStaticContentData(toPageName,false,false);
		if(!jQuery.isEmptyObject(staticContentJson)){
			var staticContentText = staticContentJson.HandsetPrivacyStatement;
			$("#privacyPolicyText").html(staticContentText);
		}
	}catch(err){
		showSysException(err);
	}
}

// getting static content json from apache server
function getStaticContentData (page,accessLocalFile,preLoginErrorHandler){
	try{
		var staticContentJson;
		if(accessLocalFile){
			staticContentJson = getContentJson("staticContent");
			return staticContentJson;
		}
		var newDate = new Date();	
		var pageId="STATIC_CONTENT_JSON";
		var STATICCONTENTURL = HREF_URL + "json/privacy/staticContent.json?"+newDate+"";
		var staticContent=getDataFromCache(pageId);
		if(jQuery.isEmptyObject(staticContent)){
			showSpinner();
			$.ajax({
				type : "GET",
				url : STATICCONTENTURL,
				async : false,
				dataType : 'json',
				success : function(responseData, status, jqXHR) {
					hideSpinner();
					if (jqXHR.status != 200) {
						var code=getResponseStatusCode(jqXHR);
						errorHandler(code,'',page);
					} else {
						staticContentJson = responseData;
						putDataToCache(pageId,staticContentJson);
					}
				},
				error : function(jqXHR, textStatus, errorThrown) {
					if(preLoginErrorHandler){
						cpEvent.preventDefault();
						dfs.crd.lilo.errorHandlerPreLogin('500');
					}else{
						errorHandler('500','',page);
					}
				}
			});
		}else{
			staticContentJson=staticContent;
		}
		return staticContentJson;
	}catch(err){
		showSysException(err);
	}
}