/*
* handling REST service calls.
*
*
*
*/

/** Name Space**/
dfs.crd.disnet = dfs.crd.disnet || {};
/** **/

/**
         * doServiceCAll method can be used for both GET and POST calls.
		 * On success or failure of service call, control will forwarded to respective success handler. This success handle function can be namespace function or normal function.
         *
         * @param method accepts JSON as a parameter.
		 * We need to pass below name w.r.t the value into the JSON. 
		 * 1)	serviceURL :- Required.
         * 2)	successHandler :- Required. On success of a service call, method will forward the control to successHandlder.
		 * 3)	errorHandler :- Required. If error occurs during service call, method will forward the control to errorHandler.
		 * 4)	requestType :- Not required. By default it's GET call, if you want to make POST call , please pass value as POST
		 * 5)	isASyncServiceCall :- Not Requied. By default service call will be ASync, if you want to make Sync call,
				Please pass boolean false into this parameter.
		 * 6)	serviceData :- Not Required. If we need to pass any data to service (normally we do it for POST call), 
				Please pass string format JSON(use JSON.stringify function).
		 *
*/

dfs.crd.disnet.doServiceCall = function(localJSON){
	try{
		if (!jQuery.isEmptyObject(localJSON)){		
		console.log("localJSON BankAccount"+JSON.stringify(localJSON));
		var localServiceURL = "";
		var localRequestType = "GET";
		var isASyncServiceCall = true;
		var localServiceData = "";
		var localSuccessHandler = "";
		var localErrorHandler = "";
		
		localServiceURL =  localJSON.serviceURL;
		if(isEmpty(localServiceURL)){
			return "";		
		}
		
		localSuccessHandler =  localJSON.successHandler;
		if(isEmpty(localSuccessHandler)){
			return "";		
		}
		
		localErrorHandler =  localJSON.errorHandler;
		if(isEmpty(localErrorHandler)){
			return "";		
		}
		
		var serviceRequestType = localJSON.requestType;
		if(!isEmpty(serviceRequestType)){
			localRequestType = serviceRequestType;
		}		
		
		var serviceSyncCall = localJSON.isASyncServiceCall;
		if(!isEmpty(serviceSyncCall)){
			isASyncServiceCall = serviceSyncCall;
		}	
		
		var serviceDataTemp = localJSON.serviceData;
		if(!isEmpty(serviceDataTemp)){
			localServiceData = serviceDataTemp;
		}

		showSpinner();
		 
			$.ajax({
				type : localRequestType,
				url : localServiceURL,
				async : isASyncServiceCall,
				dataType : 'json',
				headers : dfs.crd.disnet.prepareNetworkHeader(),
				data :localServiceData,
				success : function(responseData, status, jqXHR) {
					hideSpinner();
					
					if (jqXHR.status != 200 & jqXHR.status != 204) {
						executeDelegateFunction(localErrorHandler,window,jqXHR);
						
					} else {
						executeDelegateFunction(localSuccessHandler,window,responseData);
					}
				},
				error : function(jqXHR, textStatus, errorThrown) {
					hideSpinner();
					executeDelegateFunction(localErrorHandler,window,jqXHR);
					
				}
			});
			
			
		}else{
			return "";
		}
	}catch(err){
		showSysException(err);
	}

}

// common network header for both GET and POST call.
dfs.crd.disnet.prepareNetworkHeader = function(localJSON){
	try{
			var postHeader = {
				'X-Application-Version' : APPVER,
				'X-Client-Platform' : getClientPlat(),
				'Content-Type' : 'application/json',
				'X-SEC-Token' : sectoken
		};
		
		return postHeader;
	}catch(err){
		showSysException(err);
	}
}


//calling namespace function dynamically and passing response obj.
function executeDelegateFunction (functionName,window/*,responseObj*/){
	try{
	  if(isEmpty(functionName)){
				return;
		  }
		  var responseObj = Array.prototype.slice.call(arguments).splice(2);
		  var namespaces = functionName.split(".");
		  if(namespaces.length == 1){
			window[functionName].apply(this, responseObj);
		  }
		  var func = namespaces.pop();
		  for(var i = 0; i < namespaces.length; i++) {
			window = window[namespaces[i]];
		  }
		  window[func].apply(this, responseObj);
	 }catch(err){}
}
