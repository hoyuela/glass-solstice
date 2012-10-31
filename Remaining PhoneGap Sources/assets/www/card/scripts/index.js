$("#login-pg").live("pagebeforeshow", function() {
	var localVersionInfoMsgDate=localStorage.getItem("VersionInfoMsgDate");
	var versionInfoMsg=localStorage.getItem("VersionInfoMsg");
	if(!isEmpty(localVersionInfoMsgDate) &&  !isEmpty(versionInfoMsg)){
		$(".new_version").html("A new version of the App is available! <a href='#' onclick='redirectToPageAfterConfirm(\"updateAppVersion\")' class='ui-link'>Update Now</a></div>");
	}else{
		$(".new_version").html("");	
	}
});