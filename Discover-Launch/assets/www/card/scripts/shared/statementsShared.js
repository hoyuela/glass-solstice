/**
 * @author jcoyne
 * @version 1.0
 * 
 * This file includes the functionality that is shared between Handset and Table for Statements pages
 */

var ssns = namespace('dfs.crd.stmt.shared');
dfs.crd.stmt.shared.constant = dfs.crd.stmt.shared.constant || {};

/**
 * General Statements constants
 */
ssns.constant = {
	STMT_WS_VERS   : 'v1',
	CTD_OPTION     : 'ctd',
	DASH_OPTION    : '<option value="default">- - - - -</option>',
	MSG_NO_PEND    : 'You have no new pending transactions.',
	MSG_PEND_MAINT : 'Pending transactions are unavailable due to scheduled maintenance.',
	MSG_PEND_TD    : 'We are experiencing technical difficulties with pending transactions. Please check back later.',
	MSG_NO_TXN_CTD : 'No activity has posted to your account since your last statement.',
	MSG_NO_TXN     : 'There was no activity for the selected period.'
};

/**
 * Statements URL constants
 */
ssns.constant.url = {
	ACTSELLIST_URL  : RESTURL+"stmt/"+dfs.crd.stmt.shared.constant.STMT_WS_VERS+"/identifiers",
	TRANS_URL       : RESTURL+"stmt/"+dfs.crd.stmt.shared.constant.STMT_WS_VERS+"/transaction",
	CTGSELLIST_URL  : RESTURL+"stmt/"+dfs.crd.stmt.shared.constant.STMT_WS_VERS+"/category",
	SEARCH_URL      : RESTURL+"stmt/"+dfs.crd.stmt.shared.constant.STMT_WS_VERS+"/search?startIndex=0",
	LATEPAY_URL     : RESTURL+"stmt/"+dfs.crd.stmt.shared.constant.STMT_WS_VERS+"/paymentwarning",
	STATEMENTS_URL  : RESTURL+"stmt/"+dfs.crd.stmt.shared.constant.STMT_WS_VERS+"/statements",
	LATEPAY_TXT_URL : BASEURL + "/json/statements/latePayWarn.json",
	STMT_TXT_URL    : BASEURL + "/json/statements/content.json",
	STMT_URL        : HREF_URL + "cardmembersvcs/statements/app/stmt"
};

/**
 * Statements Cache variable constants
 */
ssns.constant.cache = {
	IDENTIFIERS  : "IDENTIFIERS",
	CATEGORIES   : "CATEGORIES",
	LOAD_MORE    : "LOAD_MORE",
	CHOSEN_DATE  : "CHOSEN_DATE",
	AA_LASTOPT   : "AA_LASTOPT",
	SR_LASTOPT   : "SR_LASTOPT",
	STMT_CONTENT : "STMT_CONTENT",
	STATEMENTS   : "STATEMENTS",
	PUBSUBSTATE  : "STATEMENTS_PUBSUB"
};


/**
 *  AJAX calls made to Statement REST Service / Apache server
 */
ssns.ajax = (function () {

	/**
	 * Fetch the Statement Identifiers, eg the data used to populate the Select List,
	 * either from cache or RESTful service call
	 *
	 * @param - isAsync - flag indicating if call should be made asynchronously
	 */	
	getIdentifiers = function(isAsync) {
	    try{
	      var actvSelListData = new Object();
	      actvSelListData = getDataFromCache(dfs.crd.stmt.shared.constant.cache.IDENTIFIERS);
	      if (jQuery.isEmptyObject(actvSelListData)) {
	            $.ajax
	            ({
	                  type : "GET",
	                  url : dfs.crd.stmt.shared.constant.url.ACTSELLIST_URL + dfs.crd.stmt.shared.common.dtParm(true),
	                  async : isAsync,
	                  dataType : 'json',
	                  headers :prepareGetHeader(),                  
	                  success : function(responseData, status, jqXHR) {           
	                  	actvSelListData = responseData;
	                 	putDataToCache(dfs.crd.stmt.shared.constant.cache.IDENTIFIERS, actvSelListData);
	                  },
	                  error : function(jqXHR, textStatus, errorThrown) {  
	                  		// Don't show error for the async call.
	                  		if(!isAsync){
	                        	cpEvent.preventDefault();
	                        	var code=getResponseStatusCode(jqXHR);
	                        	dfs.crd.stmt.shared.errorHandlerHelper(code,'','cardHome-pg','HM');	
	                  		}
	                  }
	            });
	      }
	      return actvSelListData;
	    }catch(err){
	        showSysException(err);
	    }		
	
	};//==>getIdentifiers

	/**
	 * Get Account Activity Transactions for the given option
	 *
	 * @param - option - This option represents which transaction set to fetch
	 * @param - successFunc - Function to invoke after a successful response to display the transactions
	 * @param - async - flag to indicate if the call should be made asynchronous or not
	 */
	getTransactions = function(option, successFunc, isAsync) {
		showSpinner();
		// Form the RESTful URL and make AJAX call
		var fullTransURL = dfs.crd.stmt.shared.constant.url.TRANS_URL + "?stmtDate="+option + dfs.crd.stmt.shared.common.dtParm(false);
		//var transData = new Object();
		$.ajax ({    
			type : "GET",
			url : fullTransURL,
			async : isAsync,
			dataType : 'json',
			headers :prepareGetHeader(),                  
			success : function(responseData, status, jqXHR) {           
				//transData = responseData;
				hideSpinner();
				successFunc(responseData, option);
	       	},
	    	error : function(jqXHR, textStatus, errorThrown) {     
				hideSpinner();
	   			cpEvent.preventDefault();
	   	 		var code=getResponseStatusCode(jqXHR);
	 			dfs.crd.stmt.shared.errorHandlerHelper(code,'','cardHome-pg','HM');
			}
	 	});
		//return transData;
	};//==>getTransactions


	/**
	 * Get Statement Categories, either from cache or RESTful service
	 */
	getCategories = function() {	
	    try{
	      var categories = new Object();
	      categories = getDataFromCache(dfs.crd.stmt.shared.constant.cache.CATEGORIES);
	      if (jQuery.isEmptyObject(categories)) {
	      		showSpinner();
	            $.ajax
	            ({    
	                  type : "GET",
	                  url : dfs.crd.stmt.shared.constant.url.CTGSELLIST_URL,
	                  async : false,
	                  dataType : 'json',
	                  headers :prepareGetHeader(),                  
	                  success : function(responseData, status, jqXHR) 
	                  {           
	                  	categories = responseData;
	                 	putDataToCache(dfs.crd.stmt.shared.constant.cache.CATEGORIES, categories);
	                    hideSpinner();
	                  },
	                  error : function(jqXHR, textStatus, errorThrown) 
	                  {     
	                        hideSpinner();
	                        cpEvent.preventDefault();
	                        var code=getResponseStatusCode(jqXHR);
	                        dfs.crd.stmt.shared.errorHandlerHelper(code,'','cardHome-pg','HM');
	                  }
	            });
	      }
	      return categories;
	    }catch(err){
	        showSysException(err);
	    }
	};//==>getCategories
	
	/**
	 * Get Search Results
	 */
	getSearchResults = function(searchOptions, successFunc) {
		var fullSearchURL = encodeURI ( dfs.crd.stmt.shared.constant.url.SEARCH_URL + searchOptions + dfs.crd.stmt.shared.common.dtParm(false) );
		//console.log("submitSearchForm.fullSearchURL (after encodeURI)="+fullSearchURL);
		//var searchResultData = new Object();
		showSpinner();
		$.ajax({    
			type : "GET",
			url : fullSearchURL,
			async : true,
			dataType : 'json',
			headers :prepareGetHeader(),                  
			success : function(responseData, status, jqXHR) 
			{           
				hideSpinner();
				//searchResultData = responseData;
				successFunc(responseData);
			},
			error : function(jqXHR, textStatus, errorThrown) 
			{     
				hideSpinner();
				cpEvent.preventDefault();
				var code=getResponseStatusCode(jqXHR);
				dfs.crd.stmt.shared.errorHandlerHelper(code,'','cardHome-pg','HM');
			}
		});
		//return searchResultData;
	};//==>getSearchResults

	/**
	 * Fetch transactions for the 'Load More' link.
	 * Can be utilized by Account Activity and Search pages
	 * Assumption of this method is that the URI for Load more is in cache
	 */
	getMoreTrans = function(txnListId, elemToHideId, successFunc) {
		var loadMoreLink = getDataFromCache(dfs.crd.stmt.shared.constant.cache.LOAD_MORE);
		if( isEmpty (loadMoreLink) ){
			dfs.crd.stmt.shared.errorHandlerHelper(500,'','cardHome-pg','HM');
		}
		var moreTransURL = BASEURL+loadMoreLink;
		//var transData = new Object();
		showSpinner();
		$.ajax ({    
			type : "GET",
			url : moreTransURL,
			async : true,
			dataType : 'json',
			headers :prepareGetHeader(),                  
			success : function(responseData, status, jqXHR) {           
				hideSpinner();
				//transData = responseData;
				return successFunc(responseData,txnListId,elemToHideId);
	       	},
	    	error : function(jqXHR, textStatus, errorThrown) {     
	       		hideSpinner();
	   			cpEvent.preventDefault();
	   	 		var code=getResponseStatusCode(jqXHR);
	 			dfs.crd.stmt.shared.errorHandlerHelper(code,'','cardHome-pg','HM');
			}
	 	});
	 	//return transData;
	};//==>getMoreTrans

	/**
	 * Get Payment Warning dynamic data
	 */
	getPayWarningData = function() {	
		var latePayData = {};
		// Make AJAX call to fetch the CM's Late Pay Data
		$.ajax ({    
			type : "GET",
			url : dfs.crd.stmt.shared.constant.url.LATEPAY_URL + dfs.crd.stmt.shared.common.dtParm(true),
			async : false,
			dataType : 'json',
			headers :prepareGetHeader(),                  
			success : function(responseData, status, jqXHR) {           
				latePayData = responseData;
	       	},
	    	error : function(jqXHR, textStatus, errorThrown) {
	    		//console.log(jqXHR);
	    		if(jqXHR.status === 400){
	    			// 400 error is returned when no lay payment msg exists for user.
					latePayData = false;
	    		}else{
	   				latePayData = null;
	       			hideSpinner();
	   				cpEvent.preventDefault();
	   				var code=getResponseStatusCode(jqXHR);
	 				dfs.crd.stmt.shared.errorHandlerHelper(code,'','cardHome-pg','HM');
	    		}
			}
	 	});	
	 	return latePayData;	
	};//==>getPayWarningData		


	/**
	 * Get Payment Warning static text
	 */
	getPayWarningTxt = function() {		
		var latePayTxt = {};
		// Make AJAX call to Web Server to fetch the Warning text templates
		$.ajax ({    
			type : "GET",
			url : dfs.crd.stmt.shared.constant.url.LATEPAY_TXT_URL + dfs.crd.stmt.shared.common.dtParm(true),
			async : false,
			dataType : 'json',
			headers :prepareGetHeader(),                  
			success : function(responseData, status, jqXHR) {
				latePayTxt = responseData;
	       	},
	    	error : function(jqXHR, textStatus, errorThrown) { 
	    		latePayTxt = null;    
	       		hideSpinner();
	   			cpEvent.preventDefault();
	   	 		var code=getResponseStatusCode(jqXHR);
	 			dfs.crd.stmt.shared.errorHandlerHelper(code,'','cardHome-pg','HM');
			}
	 	});
	 	return latePayTxt;	
	};//==>getPayWarningTxt

	/**
	 * Get Statements Content from Web Server or Cache
	 */
	getStmtsContent = function() {	
		var stmtJson = getDataFromCache(dfs.crd.stmt.shared.constant.cache.STMT_CONTENT);
		if( isEmpty (stmtJson) ){
			stmtJson = {};
			// Make AJAX call to Web Server to fetch the Statement content
			$.ajax ({    
				type : "GET",
				url : dfs.crd.stmt.shared.constant.url.STMT_TXT_URL + dfs.crd.stmt.shared.common.dtParm(true) ,
				async : false,
				dataType : 'json',
				headers :prepareGetHeader(),                  
				success : function(responseData, status, jqXHR) {
					stmtJson = responseData;
		       	},
		    	error : function(jqXHR, textStatus, errorThrown) {     
		       		hideSpinner();
		   			cpEvent.preventDefault();
		   	 		var code=getResponseStatusCode(jqXHR);
		 			dfs.crd.stmt.shared.errorHandlerHelper(code,'','cardHome-pg','HM');
				}
		 	});
		 	putDataToCache(dfs.crd.stmt.shared.constant.cache.STMT_CONTENT, stmtJson);
		}
	 	return stmtJson;	
	};//==>getStmtsContent	

	fetchStatements = function() {
		var statements = getDataFromCache(dfs.crd.stmt.shared.constant.cache.STATEMENTS);
		if (!_.isEmpty(statements)) { return; }

		//TODO temp
		//For now return hard coded list to test against
	//	var testResult = {"statements":[{"date":"20101223","pageCount":4},{"date":"20091223","pageCount":4},{"date":"20120123","pageCount":4},{"date":"20121123","pageCount":4},{"date":"20121023","pageCount":4},{"date":"20120923","pageCount":4},{"date":"20120823","pageCount":4},{"date":"20120723","pageCount":4},{"date":"20120623","pageCount":4},{"date":"20120523","pageCount":4},{"date":"20111223","pageCount":4}]};		
	//	return;
		//end temp

		var url = dfs.crd.stmt.shared.constant.url.STATEMENTS_URL;
		try {
			showSpinner();

			$.ajax({
				type : "GET",
				url : url,
				async : false,
				dataType : 'json',
				headers : prepareGetHeader(),
				success : function(data, textStatus, jqXHR) {				
					console.log("Fetch Statement Success!");
					//render statements drop down				
					hideSpinner();
					var result = new dfs.crd.stmt.shared.util.StatementsCache(data.statements);
					putDataToCache(dfs.crd.stmt.shared.constant.cache.STATEMENTS, result);
					return;
				},
				error : function(jqXHR, textStatus, errorThrown) {
					console.log("Fetch statement error");
					hideSpinner();
					var result = new dfs.crd.stmt.shared.util.StatementsCache()
					putDataToCache(dfs.crd.stmt.shared.constant.cache.STATEMENTS, result);		
					return;
				}
			});
		} catch(err) {
			showSysException(err);
		}
	};

	/*fetchStatementsV2 = function() {
		$.ajax({
			type : "GET",
			url : BASEURL + dfs.crd.stmt.shared.constant.url.STATEMENTS_URL,
			async : false,
			dataType : 'json',
			success: function(a,b,c) { 
				start();
			},
			error:function(a,b,c) {}
		});
	};*/

	//public functions
	return {
		getIdentifiers    : getIdentifiers,
		getTransactions   : getTransactions,
		getCategories     : getCategories,
		getSearchResults  : getSearchResults,
		getMoreTrans      : getMoreTrans,
		getPayWarningData : getPayWarningData,
		getPayWarningTxt  : getPayWarningTxt,
		getStmtsContent   : getStmtsContent,
		fetchStatements   : fetchStatements
		//fetchStatementsV2 : fetchStatementsV2
	};//==> return
	
})();//==> ajax

/**
 * Shared Account Activity page functionality
 */
ssns.acctactiv = (function () {

	/**
	 * Create the Options for the activity selection list and insert into the DOM
	 *
	 * @param - option - which option should be selected
	 * @param - activitySelect - DOM Element variable of Activity select list
	 */
	populateActivitySelection = function(option,activitySelect) {
		try{
			var actvSelListData = {};
			actvSelListData = dfs.crd.stmt.shared.ajax.getIdentifiers(false);
			if (!jQuery.isEmptyObject(actvSelListData) && actvSelListData.dates.length > 0){
				/* Regular expression for Date Format, since we want to put in a break before the Actual Statements*/
				var DATE_RE = /^\d{8}$/;
				var CURRENT_RE = /(Current)/;
				/* Flag used to indicate if we have not yet displayed the break */
				var beforeBreak = true;
				var part1HTML = "";
				var part2HTML = "";
				var part3HTML = "";
				var firstDate = true;
				for(var idx in actvSelListData.dates){
					var desc = actvSelListData.dates[idx].displayDate;
					var value = actvSelListData.dates[idx].stmtDate;
					var isDate = DATE_RE.test(value);
					var selected = option === value;
					if(value === dfs.crd.stmt.shared.constant.CTD_OPTION){
						part1HTML += dfs.crd.stmt.shared.common.createOption(value, desc, selected);
					}else if(isDate && firstDate){
						firstDate = false;
						if(!CURRENT_RE.test(desc)){
							desc += " (Current)";
						}
						part1HTML += dfs.crd.stmt.shared.common.createOption(value, desc, selected);
					}else if(isDate){
						part3HTML += dfs.crd.stmt.shared.common.createOption(value, desc, selected);
					}else{ 
						part2HTML += dfs.crd.stmt.shared.common.createOption(value, desc, selected);
					}
				}
				var finalHTML = part1HTML;
				if( notEmpty(part2HTML) ){
					finalHTML += (dfs.crd.stmt.shared.constant.DASH_OPTION + part2HTML);
				}
				if( notEmpty(part3HTML) ){
					finalHTML +=  (dfs.crd.stmt.shared.constant.DASH_OPTION + part3HTML);
				}
				activitySelect.html(finalHTML).selectmenu("refresh");
			}
		}catch(err){
			showSysException(err);
		}
	};//==>populateActivitySelection	

	//public functions
	return {
		populateActivitySelection : populateActivitySelection
	};//==> return

})();//==> acctactiv


/**
 * Shared Search page functionality
 */
ssns.search = (function () {

	/**
	* Populate the Search Categories in the Category Selection List
	* @param - selectId - The Element ID of the Selection List
	*/
	populateCategories = function(selectId) {
		var data = dfs.crd.stmt.shared.ajax.getCategories();
		if (isEmpty(data)) {
			return;
		}
		$catSelect =  $('#'+selectId);		
		for(var idx in data.category){
			$catSelect.append(new Option(data.category[idx].categoryDesc, data.category[idx].categoryCode));
		}
		$catSelect.append(dfs.crd.stmt.shared.constant.DASH_OPTION);
		for(var idx in data.otherCategory){
			$catSelect.append(new Option(data.otherCategory[idx].categoryDesc, data.otherCategory[idx].categoryCode));
		}
	};//==>populateCategories

	/**
	 * function: getCategoryDesc
	 * Return the category description based on the code.
	 */
	getCategoryDesc = function(code) {
		var data = dfs.crd.stmt.shared.ajax.getCategories();
		var desc = null;
		for(var idx in data.category){
			if(code === data.category[idx].categoryCode){
				desc = data.category[idx].categoryDesc;
				break;
			}
		}
		if(desc === null){
			for(var idx in data.otherCategory){
				if(code === data.otherCategory[idx].categoryCode){
					desc = data.otherCategory[idx].categoryDesc;
					break;
				}
			}
		}
		return desc;
		
	};//==>getCategoryDesc


	/**
	 *
	 */
	validateFormat = function(val) {
		var regExDatePattern = /^(\d{1,2}|\D{3,4})(\-|\/|\.|\,|\s)(\d{1,2}|\d{1,2}\,)(\-|\/|\.|\,|\s)(\d{2}|\d{4})$/;
		//check to se if the date passes the format
		if( regExDatePattern.test( val ) ){
			//once the format has been passed we need to validate the text
			var regExSeperator = /[.,\/ -]/;
			var splitString = val.split( regExSeperator );
			//remove the uneeded characters from the string for validation
			$.each(splitString, 
					function removeSeperator(index, objVal){
						//if the value of the current array index matches a separator we want to remove it.
						if( regExSeperator.test(objVal) || objVal === "" ){
							splitString.splice(index, 1);
						}
					}
				  );		
			// splitString[0] = month
			// splitString[1] = day
			// splitString[2] = year

			//Validate the MONTH input
			var monthValidation = validateUserMonth( splitString[0] );

			//Validate the YEAR
			var yearValidation = validateUserYear( splitString[2] );

			//Validate the DAY input
			var dayValidation = validateUserDay( monthValidation, splitString[1], yearValidation );	

			//check to see if all the input fields came back without any errors.  if so reformat the date

			if( !monthValidation[0] && !dayValidation[0] && !yearValidation[0] ){
				var reformattedDate = monthValidation[1] + "/" + dayValidation[1] + "/" + yearValidation[1];
				return [true, reformattedDate]; 
			}		
		}
		return [false];		
	};//==>validateFormat

	/**
	 *
	 */
	validateUserDay = function(month, day, year) {
		//check to see if the user has entered a valid day (01 - 31)
		if(parseInt(day, 10) > 0 && parseInt(day, 10) <= 31 ){
			//if the value for the day is a single digit number we need to append a 0 to it
			if(day.length == 1	){
				day = "0" + day;
			}				
			//validateDays
			if(!month[0] && !year[0]){
				var thirtyDayMonthsArray = ["04", "06", "09", "11"];		
				var monthDayLimit;
				if(month[1] == "02"){
					monthDayLimit = (year[1] % 4 === 0) ? 29 : 28;	
				}else{
					monthDayLimit = ( $.inArray(month[1],thirtyDayMonthsArray) != -1 )? 30 : 31;
				}	
				if(parseInt(day, 10) > monthDayLimit){
					return [true];	
				}	
			}else{
				return [false];
			}
		}else{
			//if the day is passed as 0 or higher than 31 the day is invalid and so is the date
			return [true];
		}
		return [false, day];		
	};//==>validateUserDay		

	/*
	 * @function validateUserMonth
	 * determine if the month portion of the user entered date is valid
	 */
	validateUserMonth = function(month) {
		var monthsShort = ['JAN', 'FEB', 'MAR', 'APR', 'MAY', 'JUN', 'JUL', 'AUG', 'SEPT', 'OCT', 'NOV', 'DEC'];
		//check to see if the user has entered a text value for the month
		if( month.length > 2 ){
			//convert the useri nput value to all caps
			month = month.toUpperCase();
			//check to see if the user entered value in the acceptable months list
			var monthLookupIndex = $.inArray(month,monthsShort) ;
			if( monthLookupIndex != -1 ){
				//set the new value to be the index of the lookup string plus 1 since hte array is zero indexed
				month = (monthLookupIndex + 1 < 10) ? "0" + (monthLookupIndex + 1) : monthLookupIndex + 1;
			}else{
				//the user entered a month, but the string was invalid so the date is invalid
				return [true];
			}
		}
		//check for a single digit input for reformatting
		if( month.length == 1){
			month = "0" + month;
		}else if( month.length == 2 && month > 12){
			return [true];
		}
		return [false, month];			
	};//==>validateUserMonth


	/**
	 *
	 */
	validateUserYear = function(year) {
		//check to see if hte user entered a full year value
		if( year.length == 4 ){
			//make sure that we have a valid year..the range is from 1900 - 2099
			if(parseInt(year) < 1900 || parseInt(year) > 2099){
				return [true];
			}
		}	
		return [false, year];		
	};//==>validateUserYear


	//public functions
	return {
		populateCategories : populateCategories,
		getCategoryDesc : getCategoryDesc,
		validateFormat : validateFormat
	};//==> return
	
})();//==> search

ssns.errorHandlerHelper = function(errorCode,customErrorMessage,menuHglt) {
	if (!isEmpty(dfs.crd.stmt.platform) && dfs.crd.stmt.platform === "tablet") {
		//tablet method signature for common errorHandler
		errorHandler(null, errorCode, customErrorMessage, null);
	} else {
		//handset method signature for common errorHandler
		errorHandler(errorCode,customErrorMessage,menuHglt);	  
	}	
}

/**
 *   Fetch the dynamic and static data and form the paragraph for the 
 *   Late Payment Warning Message and insert into the DOM
 *
 *  @param - msgId - Element ID of where the HTML should be inserted on the page
 */
ssns.displayLatePayMsg = function(msgId) {
	showSpinner();
	var latePayData = dfs.crd.stmt.shared.ajax.getPayWarningData();
	var latePayTxt = dfs.crd.stmt.shared.ajax.getPayWarningTxt();
	if( notEmpty(latePayData) && notEmpty(latePayTxt)  ){
		var latePayMsg = null;
		if( typeof latePayData == 'boolean' ){
			latePayMsg = latePayTxt.NO_STMT;
		}else{
			latePayTxt = dfs.crd.stmt.shared.ajax.getPayWarningTxt();
			// Now, we combine the dynamic and static data to create a paragraph 
			var merchantAPR = latePayData.penaltyWarningMerchantAPR + "%";
			var cashAPR = latePayData.penaltyWarningCashAPR + "%";
			if ("V" === latePayData.penaltyVariableFixedInd ) {
				merchantAPR += " variable";
				cashAPR += " variable";
			}
			var mapping = [];
			mapping["late_fee"] = latePayData.lateFeeWarningAmount;
			mapping["purch_penalty_apr"] = merchantAPR;
			mapping["cash_penalty_apr"] = cashAPR;
			mapping["defOrPen"] = latePayData.aprTitle;
			latePayMsg = parseContent(latePayTxt[latePayData.penaltyWarningAPRCode], mapping);
		}
		// Insert the paragraph into the page using the element ID passed in
		$('#'+msgId).html(latePayMsg);
	}
	hideSpinner();
};

/***
*  Common Shared Functions
*    - createOption
*    - initShowHide
***/
ssns.common = (function () {

	var ShowHide = {
		$link : null,
		$content : null,

		init : function() {
			if ($('.show-hide').length !== 0) {
				var self = this;
				self.$link    = $('.show-hide-link');
				self.$content = $('.show-hide-content');
				self.bind();
			}
		},

		bind : function() {
			var self = this,
			onText = self.$link.attr('data-text-on'),
			offText = self.$link.attr('data-text-off');

			self.$content.hide();
			self.$link.toggle(
				function(e){
					e.preventDefault();
					$(this).text(onText);
					self.$content.show();
				},
				function(e) {
					e.preventDefault();
					$(this).text(offText);
					self.$content.hide();
				}
			);
		}
	};
	
	/**
	 * Create Option HTML for selection list
	 */
	createOption = function(value, desc, selected) {
		var option = '<option value="'+value+'"';
		if(selected){
			option += " selected";
		}
		option += '>'+desc+'</option>';
		return option;
	};//==>createOption 
	
	dtParm = function(isStart){
		var parm = null;
		if(isStart){
			parm = "?";
		}else{
			parm = "&";
		}
		var dt = new Date();
		parm += ("d="+dt.getTime());
		return parm;
	};//dtParm
	
	/**
	 * Used to initialize a section that can toggle showing/hiding content based on a clicked link.
	 */
	initShowHide = function() {
		ShowHide.init();
	};//==>initShowHide 

	//public functions
	return {
		createOption: createOption,
		initShowHide : initShowHide,
		dtParm : dtParm
	};//==> return
	
})();//==> common

//------------------------------------------------------------------------------
//BEGIN CHANGES FOR R13.1
//-----------------------------------------------------------------------------
ssns.util = {

	getXMonthsAgo:  (function(monthsBack) {
		var today = new Date();
		today.setMonth(today.getMonth() - monthsBack);
		return today.prettyPrint();
	}),

	getStatements: (function() {
		console.log("getStatements called...");
		dfs.crd.stmt.shared.ajax.fetchStatements();
		var statements = getDataFromCache(dfs.crd.stmt.shared.constant.cache.STATEMENTS);
		/*var statements = getDataFromCache(dfs.crd.stmt.shared.constant.cache.STATEMENTS);
		if (!_.isEmpty(statements)) { return statements; }	
		*/
		//TODO for local testing just return imaginary data;
		//var data = {"statements":[{"date":"20101223","pageCount":4},{"date":"20091223","pageCount":4},{"date":"20120123","pageCount":4},{"date":"20121123","pageCount":4},{"date":"20121023","pageCount":4},{"date":"20120923","pageCount":4},{"date":"20120823","pageCount":4},{"date":"20120723","pageCount":4},{"date":"20120623","pageCount":4},{"date":"20120523","pageCount":4},{"date":"20111223","pageCount":4}]};	
		//return new dfs.crd.stmt.shared.util.StatementsCache(data.statements);
		
		
		//var statements = getDataFromCache(dfs.crd.stmt.shared.constant.cache.STATEMENTS);
		return statements;
	}),

	getFormattedDate: (function(date) {
		var allMonths = ["January","February","March","April","May","June","July","August","September","October","November","December"];
		var o = {};
		o.year = date.substr(0, 4);
		o.month = date.substr(4, 2);
		o.monthName = allMonths[o.month - 1];
		o.day = date.substr(6, 2);
		//o.dayPadded = o.day < 10 ? "0" + o.day : o.day;
		o.toString = (function() { return o.monthName + " " + o.day + ", " + o.year; });
		return o;
	}),

	getIndex: (function(date, statements) {
		var index = -1;
		//loop through array pertaining to this year for the specified document index
		//we need to maintain index so we can increment/decrement when user navigates
		for (var i = 0; i < statements.array.length; i++) {
			if (statements.array[i].date === date) {
				index = i;
				break;
			}
		}	
		return index;
	}),

	StatementsCache: (function(stmtArr) {
		var today = new Date().prettyPrint();
		//var stmtArr = _.filter(stmtArr, function(o){ return o.date <= today });

		this.array = _.sortBy(stmtArr, function(o){ return o.date; }).reverse();
		this.map = _.groupBy(this.array, function(o){ return o.date.substring(0, 4); });
		this.sixMonthArray = _.chain(stmtArr)
			.filter(function(o){ return o.date >= dfs.crd.stmt.shared.util.getXMonthsAgo(6) })
			.sortBy(function(o){ return o.date; })
			.reverse()
			.value();
		
		var yrArr = [];
		$.each(this.map, function(k,v) { yrArr.push(k); });
		this.yearArray = _.sortBy(yrArr, function(o){ return o; }).reverse();

		this.isEmpty = function(){ 
			return (isEmpty(this.array) || this.array.length === 0);
		};
	}),

//statements zoom functionality (from FE team but modified by me)
/*	initZoomer: (function(){
		var zoomConfigOrientation = {};
		var originalBackground = "";
		var render = (function(zoomTo) {  
			console.log("Rendering");
			var iv2;
			$(this).delegate(".lazy", "dblclick doubletap", function(){
				var imgUrl = $(this).attr("src");	
				$(this).hide();
				iv2 = $(this).parent("div.viewer").iviewer({
					src: imgUrl,
					ui_disabled :true,
					zoom:zoomTo									
				});	
				//TODO do we want some sort of background color set when toggled in zoom/pan mode?
				originalBackground = iv2.css("background-color");
				iv2.css("background-color", "#ffddaa");
			});
			$(this).delegate(".viewer .iviewerImg", "dblclick doubletap", function(){
				$(this).hide().siblings("img.lazy").show();
				iv2.iviewer('destroy');
				iv2.css("background-color", originalBackground);
			});
		});

		var detectOrientation = (function(){
			console.log("Detecting orientation:", orientation);
			if(orientation === 0 || orientation === 180) {
				render(zoomConfigOrientation.portrait);
			} else {
				render(zoomConfigOrientation.landscape);
			}
		});

		var init = (function() {
			console.log("Init zoomer");
			if (navigator.userAgent.toLowerCase().indexOf("android") > -1) {
				zoomConfigOrientation = { portrait: 100, landscape: 100 };
			} else {
				zoomConfigOrientation = { portrait: 35, landscape: 60 };
			}
			render();
			window.onorientationchange = detectOrientation;
		})();
	})
	*/
};

ssns.statementDetail = {
	splitButtonNav : function(dom, statements, indexFn) {
		var index = indexFn();
		console.log("Received request");
		if (index >= 0 && index < statements.array.length) {
			//change page
			globalCache.statements_index = index;
			dfs.crd.stmt.shared.statementDetail.renderSplitButtonNav(dom, index, statements);
			console.log("SEnding request");
			$.publish( '/statements/detail/index/changed' , [index, statements]);
		} else { 
			console.log("Index out of bound: ", index);
		}
	},
	renderSplitButtonNav : function(dom, index, statements) {
		if (index <= 0) {
			dom.$rightArrow.css("visibility", "hidden");
		} else {
			dom.$rightArrow.css("visibility", "visible");
		}
		if (index + 1 >= statements.array.length) {
			dom.$leftArrow.css("visibility", "hidden");
		} else {
			dom.$leftArrow.css("visibility", "visible");
		}
	}
};

//TODO move plugin to common area
/* jQuery Tiny Pub/Sub - v0.7 - 10/27/2011
 * http://benalman.com/
 * Copyright (c) 2011 "Cowboy" Ben Alman; Licensed MIT, GPL */
(function($){
  
  var o = $({});
  
  $.subscribe = function() {
    o.bind.apply( o, arguments );
  };
  
  $.unsubscribe = function() {
    o.unbind.apply( o, arguments );
  };
  
  $.publish = function() {
    o.trigger.apply( o, arguments );
  };
  
})(jQuery);

//TODO utility functions to move elsewhere
//
Date.prototype.getMonthPadded = function() {
    var m = this.getMonth() + 1;
    return m < 10 ? "0" + m : m;
};
Date.prototype.getDayPadded = function() {
    var d = this.getDate();
    return d < 10 ? "0" + d : d;
};
Date.prototype.prettyPrint = function() {
    return this.getFullYear() + "" + this.getMonthPadded() + "" + this.getDayPadded();
};

