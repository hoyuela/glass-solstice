/** HANDSET
 * @author jcoyne, sgoff0
 * @version 1.1 10/26/12
 * 
 * This  file includes the functionality to render following Statements pages: 
 *  - Account Activity ( Recent Activity, Statement Periods, and Summary Periods)
 *  - Search Transactions
 *  - Late Payment Warning (Learn More link following the Min. Payment Due on Account Summary Page)
 */
dfs.crd.stmt = dfs.crd.stmt || {};

/**
 * Account Activity Presentation Functions
 **/
dfs.crd.stmt.acctactiv = (function () {

	/**
	 * Initialize the Account Activity Page
	 */
	init = function() {
		dfs.crd.stmt.shared.common.initShowHide();
		var activitySelect = $("#activity-selection");
		// Bind events
		activitySelect.change(function(){ dfs.crd.stmt.acctactiv.getAndDisplayActivityTransactions(this.value,true); });   
		$("#load-more").click(function(){ dfs.crd.stmt.common.loadMoreTrans("js-trans-list","load-more-button"); });
		$("#js-search-transactions-link").click(function(){ navigation('../statements/searchTrans'); });
		// Populate the Selection list
		dfs.crd.stmt.shared.acctactiv.populateActivitySelection(dfs.crd.stmt.shared.constant.CTD_OPTION,activitySelect);
		// Fetch Transactions from Server and display
		getAndDisplayActivityTransactions(dfs.crd.stmt.shared.constant.CTD_OPTION, false);
		// Had to do this because JQM was not applying these styles when page first loads
		var pendChildren = $('#js-pending-list').children();
		var transChildren = $('#js-trans-list').children();
		pendChildren.first().addClass('ui-corner-top');
		transChildren.first().addClass('ui-corner-top'); 
		pendChildren.last().addClass('ui-corner-bottom');
		transChildren.last().addClass('ui-corner-bottom'); 
		// Had to do this because JQM for some reason was applying style when page first loads- padding-top: 8px; 
		var aaPage = $('#account-activity');
		aaPage.css('padding-top', '');
		aaPage.css('padding-bottom', '');
	};//==>init

	/**
	 * Fetch Activity Transactions and update the DOM to display the Transactions
	 */
	getAndDisplayActivityTransactions = function(option, async) {
		// If user selected default option (the dashes) change the option back to the prior selection
		if(option==="default"){
			var opt = getDataFromCache(dfs.crd.stmt.shared.constant.cache.AA_LASTOPT);
			if(notEmpty(opt)){
				$("#activity-selection").val( opt ).selectmenu("refresh");
			}		
			return;
		}

		// Function to pass into AJAX call so that it can be performed async
		var displayTrans = function(transData, option) {
			// If user is viewing Recent Activity (eg CTD) then we check if they can see the pending section
			var showPending = false;
			if(option === dfs.crd.stmt.shared.constant.CTD_OPTION){
				showPending = transData.canSeePending;
				$("#js-transactions-title").html("Posted Transactions");
			}else{
				$("#js-transactions-title").html("Transactions");
			}

			var pendingList = $("#js-pending-list");
			if(showPending){
				// Render Pending Txns Section
				var pendingTransHtml;
				if(transData.pendingReturnCode === 0){
					pendingTransHtml = dfs.crd.stmt.common.generateTransHtml(transData.pendingTransactions);
				}else{
					var pendMsg;
					var isError = false;
					if(transData.pendingReturnCode === 1){ // No Transactions
						pendMsg = dfs.crd.stmt.shared.constant.MSG_NO_PEND;
					}else if(transData.pendingReturnCode === 3){ // Maintenance
						pendMsg = dfs.crd.stmt.shared.constant.MSG_PEND_MAINT;
						isError = true;
					}else{
						// Return code of 2 should be for tech diff., but just make it the default in case it was not properly set.
						pendMsg = dfs.crd.stmt.shared.constant.MSG_PEND_TD;
						isError = true;
					}
					pendingTransHtml = dfs.crd.stmt.common.getNoTransHtml(pendMsg,isError);
				}
				pendingList.html(pendingTransHtml).listview("refresh");
				$("#pending-transactions").show();
			}else{
				//Hide Pending Txns Section
				pendingList.html("");
				$("#pending-transactions").hide();
			}

			if(transData.postedTransactions !== null && transData.postedTransactions.length > 0){
				$("#js-trans-list").html( dfs.crd.stmt.common.generateTransHtml(transData.postedTransactions) ).listview("refresh");
			}else{
				var msg;
				if(option === dfs.crd.stmt.shared.constant.CTD_OPTION){
					msg = dfs.crd.stmt.shared.constant.MSG_NO_TXN_CTD;
				}else{
					msg = dfs.crd.stmt.shared.constant.MSG_NO_TXN;
				}
				$("#js-trans-list").html( dfs.crd.stmt.common.getNoTransHtml(msg,false) ).listview("refresh");
			}

			if( isEmpty(transData.loadMoreLink) ){
				$('#load-more-button').hide();
				putDataToCache(dfs.crd.stmt.shared.constant.cache.LOAD_MORE, null);
			}else{
				$('#load-more-button').show();
				putDataToCache(dfs.crd.stmt.shared.constant.cache.LOAD_MORE, transData.loadMoreLink);
			}
			// Store the current chosen option in case user choses 'default' we need to switch back to this option
			putDataToCache(dfs.crd.stmt.shared.constant.cache.AA_LASTOPT, option);
		};

		dfs.crd.stmt.shared.ajax.getTransactions(option, displayTrans, async);

	};//==>getAndDisplayActivityTransactions

	//public functions
	return {
		init : init,
			getAndDisplayActivityTransactions: getAndDisplayActivityTransactions	
	};//==> return

})();//==> acctactiv

/**
* Search Functions
**/
dfs.crd.stmt.search = (function () {
	
	// DOM Elements
	var dateTypeSelection;
	var amountTypeSelection;	
	var categoryTypeSelection;
	var pageHideWrap;
	var calHideWrap;
	var footnotes;
	var searchForm;
	var dateSpecific;
	var dateRange;
	var amountSpecific;
	var amountRange;
	var searchResultsContainer;
	var loadMoreTransactionsButton;
	var loadMoreTransactionsButtonContainer;
	var loadMoreTransactionsListContainer;
	var userDateInput;
	
	var errorCount = 0;
	var searchPerformed = false;
	
	/**
	 * Perform initialization of the Search Page
	 */
	init = function() {
		// Used to track the first time user is performing a search after loading the page
		searchPerformed = false;
		//$.mobile.zoom.enabled = false;
		$('[data-role=button]').button();

		// Create the Options for Selection List and insert into the DOM
		dfs.crd.stmt.shared.search.populateCategories("js-categoryType-selection");
		
		// Put Chosen Option in Cache in the event user selects the Dashes
		putDataToCache(dfs.crd.stmt.shared.constant.cache.SR_LASTOPT, "all");
		
		var stmtsContent = dfs.crd.stmt.shared.ajax.getStmtsContent();
		$("#js-search-history").html(stmtsContent.stmtHistoryAvail);

		$("#calendar-cancel").click(function(){  
			pageHideWrap.show();
			footnotes.show();
			calHideWrap.hide();
		});

		// Create Dynamic bindinds for the Calendar Date Picker UI
		$(".js-date-picker-input").click(function() {
			//set the current instance of the input field to be filled out by the date picker
			var chosenDate = $(this).siblings("label").find("input");
			putDataToCache(dfs.crd.stmt.shared.constant.cache.CHOSEN_DATE, chosenDate);
			var dateText = null;
			switch (chosenDate.attr('id')) {
				case "specificDate":
					dateText = "Select A Date";
					break;
				case "fromDate":
					dateText = "Select Start Date";
					break;
				case "toDate":
					dateText = "Select End Date";
					break;
				default:
					dateText = "Select Date";
					break;
			}
			$("#js-date-title").html(dateText);
			var dateToSelect = chosenDate.val();
			if( isEmpty ( dateToSelect ) ) {
				dateToSelect = new Date();
			}
			$("#calendar").datepicker( "setDate" , dateToSelect );
			// Initialize DOM element at this point since we know it will be used later when user closes the Calendar
			footnotes = $(".footnotes");
			footnotes.hide();
			pageHideWrap.hide();
			calHideWrap.show();
		});
		
		$("#button-specDate").click(function() {
			if ($(this).hasClass("disabled")) {
				return false;
			}
		});
		
		$("#button-fromDate").click(function() {
			if ($(this).hasClass("disabled")) {
				return false;
			}
		});		
		
		$("#button-toDate").click(function() {
			if ($(this).hasClass("disabled")) {
				return false;
			}
		});		

		var nbrMos = 24;
		if( notEmpty(stmtsContent.searchMonths) ){
			nbrMos = stmtsContent.searchMonths;
		}
		
		$("#calendar").datepicker({
			hideIfNoPrevNext : true,
			showOtherMonths : true,
			selectOtherMonths : false,
			constrainInput : true,
			minDate : "-"+ nbrMos +"m",
			maxDate : "0",
			onSelect : function(dateText, inst) {
				dfs.crd.stmt.search.setTheDate(dateText,inst);
			}
		});
		
		// Initialize DOM Elements
		dateTypeSelection = $("#js-dateType-selection", "#searchParams");
		amountTypeSelection = $("#js-amountType-selection", "#searchParams");	
		categoryTypeSelection = $("#js-categoryType-selection", "#searchParams");
		pageHideWrap = $("#js-pageHideWrap");
		calHideWrap = $(".js-calHideWrap");
		searchForm = $("#js-search-form","#pageContent");
		dateSpecific = $("#js-date-picker-specific", "#searchParams");
		dateRange = $("#js-date-picker-range","#searchParams" );
		amountSpecific = $("#js-amount-picker-specific", "#searchParams");
		amountRange = $("#js-amount-picker-range", "#searchParams");
		searchResultsContainer = $("#js-searchResults-container", "#pageContent");
		loadMoreTransactionsButtonContainer = $(".js-loadTransactions-container");
		loadMoreTransactionsListContainer = $("#js-transactionsResults-list", "#js-searchResults-container");
		userDateInput = $(".js-date-input", "#searchParams");
		
		/*
		 * Bind DOM events
		 */
		//change events
		(dateTypeSelection).unbind("change").bind("change", handleDateTypeSelectionChange );
		(amountTypeSelection).unbind("change").bind("change", handleAmountTypeSelectionChange );
		(categoryTypeSelection).unbind("change").bind("change", handleCategoryTypeSelectionChange );	
		//blur events
		(userDateInput).unbind("blur").bind("blur", handleUserManualDateInput );	
		//submit events
		(searchForm).unbind("submit").bind("submit", handleSearchFormSubmit );
		//bind scroll events  - commenting out don't think it's needed
		//$(document).unbind("scrollstop").bind("scrollstop", handleScrollStop )

		var sPage = $('#search');
		sPage.css('padding-top', '');
		sPage.css('padding-bottom', '');
	};//==>init

	/**
	 * Handle the Sorting of Results by performing the search again, but passing the value of the selected sort choice
	 */
	sortResults = function() {
		dfs.crd.stmt.search.performSearch( $("#js-search-results-sortby").val() );
	};//==>sortResults
	
	/**
	 * Set the date from Date Picker into the appropriate text input field
	 */
	setTheDate = function(theDate) {
		try {
			pageHideWrap.show();
			footnotes.show();
			calHideWrap.hide();
			var chosenDate = getDataFromCache(dfs.crd.stmt.shared.constant.cache.CHOSEN_DATE);
			if(chosenDate!==null){
				chosenDate.val(theDate);
				clearValidationError( chosenDate );
				putDataToCache(dfs.crd.stmt.shared.constant.cache.CHOSEN_DATE, null);
			}
		} catch (err) {
			showSysException(err);
		}
	};//==>setTheDate
	
	/**
	 *  Validate the form, before submission
	 */
	searchSubmitValidation = function() {
		var dateValid = false;
		//check if our inputs are visible and valid
		var specific = checkInputValues( dateSpecific );
		var range = checkInputValues( dateRange );	
		if(specific === false || range === false){
			dateValid = false;
		}else{
			dateValid = true;
		}
		return dateValid;
	};//==>searchSubmitValidation

	/**
	 * Validate a date input field
	 */
	dateInputValidation = function(value, input) {
		var dateValid = false;
		//check for empty value
		if(value === ""){
			dateValid = true;
			clearValidationError( input );
			return dateValid;
		}
		//if the form inputs are not visible then we will return true
		var inputValidation = dfs.crd.stmt.shared.search.validateFormat( value );
					
		dateValid = inputValidation[0];
					
		//is the date valid
		if(dateValid){
			//remove any existing error message for the input field
			clearValidationError( input );
			//reset the input text with the reformatted data
			input.val( inputValidation[1] );
		}else{
			//add the display error neesage
			displayValidationError( input );
		}
		//if we didn't validate we need to throw an error message'
		return dateValid;
	};//==>dateInputValidation

	/**
	 * Check the input values that are shown on the page.
	 */
	checkInputValues = function(inputContainer) {
		//check to see if the container of the input has a hide class.  If it does we are automatically valid as there's no value to validate
		if( !inputContainer.hasClass("hide") ){
			//get all the instances of the inputs found in the container
			var inputFields = inputContainer.find("input");
			//an array to store our input validation results
			var inputFieldResults = [];
						
			//loop through all the found inputs and pass their validation result to our local array
			inputFields.each(
				function each(index){
					inputFieldResults.push( dateInputValidation( $(this).val(), $(this) ) );
				}
			);
						
			//if any of the inputs were invalid we will return a false flag to warn our calling method
			var inputsValid = ( $.inArray(false, inputFieldResults) != -1 ) ? false : true;
						
			//return our validation results
			return inputsValid;
		}
		//if we have the hidden class return true
		return true;		
	};//==>checkInputValues

	/**
	 *
	 */
	displayValidationError = function(input, errorMessage) {
		if(input){
			if(input.siblings(".error-message").html() === ""){
				errorCount++;
			}
			input.siblings(".error-message").html( "Invalid value" );
			input.parent().parent().parent().find(".error-message").css("height", "20px");
			input.addClass('invalid');
		}
	};//==>displayValidationError	

	/**
	 *
	 */
	clearValidationError = function(input) {
		if(input && input.siblings('.error-message').html().length > 0){
			input.siblings('.error-message').html( "" );
			input.removeClass('invalid');
			if(errorCount <= 1){
				input.parent().parent().parent().find(".error-message").css("height", "auto");
			}
			if(errorCount > 0){
				errorCount--;
			}
		}
	};//==>clearValidationError
	
	/**
	 *
	 */
	resetInputValue = function(container) {
		//find each input and set it's value to an empty string
		var input = container.find("input");
		input.each(function each(index){
			$(this).val("");	
		});
	};//==>resetInputValue
	
   /*
	* @function toggleHideClass
	* add or remove the hid class of a DOM element
	* 
	* @param input
	* the DOM element that we want to check if it has a hide class already
	* 
	* @displayType
	* the type of class we want to put on the @input element
	*/	
	toggleHideClass = function(input, displayType) {
		if(displayType == "hide"){
			if(input.hasClass("hide")){
				return;
			}else{
				input.addClass("hide");
			}	
		}else if(displayType == "show"){
			if(!input.hasClass("hide")){
				return;
			}else{
				input.removeClass("hide");
			}	
		}
	};//==>toggleHideClass	
	
			/*
			 * @function toggleSearch
			 * hide and or show the appropriate date/amount input field based on the selected select option
			 * 
			 * @param selectedIndex
			 * the current index of hte selected input
			 * 
			 * @param inputTypeSelector
			 * the type of input we want to show
			 */
	toggleSearchFilterInputs = function(selectedIndex, inputTypeSelector) {
		var $specific = null;
		var $range = null;
		switch(inputTypeSelector){
			case "date":
				$specific = dateSpecific;
				$range = dateRange;
				break;
			case "amount":
				$specific = amountSpecific;
				$range = amountRange;
				break;
		}
	
		switch( selectedIndex ){
			case 0:
				toggleHideClass( $specific, "hide");
				toggleHideClass( $range, "hide");
				resetInputValue( $specific );
				resetInputValue( $range );
				if( errorCount > 0 && inputTypeSelector === "date"){
					clearValidationError ( $("#specificDate") );
					clearValidationError ( $("#fromDate") );
					clearValidationError ( $("#toDate") );
				}
				break;
			
			case 1:
				toggleHideClass( $specific, "show");
				toggleHideClass( $range, "hide");
				resetInputValue( $range ); 
				if( errorCount > 0 && inputTypeSelector === "date"){
					clearValidationError ( $("#fromDate") );
					clearValidationError ( $("#toDate") );
				}
				break;
			
			case 2:
				toggleHideClass( $specific, "hide");
				toggleHideClass( $range, "show");
				resetInputValue( $specific );
				if( errorCount > 0 && inputTypeSelector === "date"){
					clearValidationError ( $("#specificDate") );
				}
				break;
			
			default:
				break;
			}	
	};//==>toggleSearchFilterInputs	
	
	/**
	 * This function will gather the input data from the page, form the Search URL, 
	 * submit the request to the server, and display the results on the page.
	 *
	 * @param  - sortOrder - optional parameter to perform sorting of the search results
	 */
	submitSearchForm = function(sortOrder) {
		if(searchSubmitValidation()){
			var keywords = $("#keywords").val();
			var dateOption = $("#js-dateType-selection").val();
			var amountOption = $("#js-amountType-selection").val();
			var catgOption = $("#js-categoryType-selection").val();

			var searchOptions = "";
			if(keywords === null || keywords===""){
				keywords = "--------";
			}else{
				searchOptions += "&keyword="+keywords;
			}
			if(dateOption === "specific"){
				var fromDate = $("#specificDate").val();
				searchOptions += "&dateType=one";
				if (notEmpty(fromDate)){
					searchOptions += "&fromDate="+fromDate;
				}
				dateOption = "On " +fromDate;
			}else if(dateOption === "range"){
				var fromDate = $("#fromDate").val();
				var toDate = $("#toDate").val();
				searchOptions += "&dateType=range";
				if (notEmpty(fromDate)){
					searchOptions += "&fromDate="+fromDate;
				}
				if (notEmpty(toDate)){
					searchOptions += "&toDate="+toDate;
				}
				
				if(notEmpty(fromDate) && notEmpty(toDate) ){
					dateOption = "Between " +fromDate + " and " + toDate ;
				}else if(notEmpty(fromDate)){
					dateOption = "On or after " +fromDate;
				}else if(notEmpty(toDate)){
					dateOption = "On or before " +toDate;
				}else{
					dateOption = "All Available Dates";
				}
			}else{
				dateOption = "All Available Dates";
			}
			if(amountOption === "specific"){
				var fromAmount = $("#specificAmount").val();
				searchOptions += "&amountType=one";
				if (notEmpty(fromAmount)){
					searchOptions += "&fromAmount="+fromAmount;
				}
				amountOption = " $"+fromAmount;
			}else if(amountOption === "range"){
				var fromAmount = $("#fromAmount").val();
				var toAmount = $("#toAmount").val();
				searchOptions += "&amountType=range";
				if (notEmpty(fromAmount)){
					searchOptions += "&fromAmount="+fromAmount;
				}
				if (notEmpty(toAmount)){
					searchOptions += "&toAmount="+toAmount;
				}
				
				if(notEmpty(fromAmount) && notEmpty(toAmount) ){
					amountOption = "Between $" + fromAmount + " and $" + toAmount;
				}else if(notEmpty(fromAmount)){
					amountOption = "Greater than or equal to $" + fromAmount;
				}else if(notEmpty(toAmount)){
					amountOption = "Less than or equal to $" + toAmount;
				}else{
					amountOption = "All Amounts";
				}
			}else{
				amountOption = "All Amounts";
			}
			if(catgOption != "all"){
				searchOptions += "&category="+catgOption;
				catgOption = dfs.crd.stmt.shared.search.getCategoryDesc(catgOption);
			}else{
				catgOption = "All Categories";
			}
			var isSort = false;
			if(sortOrder !== null && sortOrder !== "default"){
				searchOptions += "&sort="+sortOrder;
				isSort = true;
			}

			// This ensures the keyboard is hidden if user hit Go/Enter to submit form
			document.activeElement.blur();
			
			if(!isSort && searchPerformed){
				// For Subsequent searches, Blank out HTML first so use can see the top
				(searchResultsContainer).html("");
			}
		
			var displaySearchResults = function(searchResultData){
					
				var totalTransactions = searchResultData.totalTransactions;
				var totalTransactionAmount = searchResultData.totalTxnAmt;
							 
				var searchResults = "<h1>Search Results</h1>";
				searchResults += "<div class=\"searchresults-data\">";
				var loadMore = false;
				if(totalTransactions === 0){
					searchResults += "<p><span class=\"inlineLabel\">We did not find any matching transactions.</span></p>";
					searchResults += "<p><span class=\"inlineLabel\">Please allow 1-3 days for recent transactions to post.</span></p>";
					searchResults += "</div>";	
				}else{
					var tranTxt = "transactions";
					//var matchTxt = "match";
					var matchTxt = "result";
					if(totalTransactions === 1){
						tranTxt =  "transaction";
						//matchTxt = "matches";
						matchTxt = "results";
					}
					//searchResults += "<p class=\"boldtext\">Found "+totalTransactions+" ";
					searchResults += "<div class='searchResultsStat'><p class='hideLiMain'><span class='pn-plus'></span><span class='boldtext'>"+totalTransactions+"</span> ";
					//searchResults += tranTxt+" totalling "+totalTransactionAmount+" that "+matchTxt+":</p>";
					searchResults += matchTxt+" totalling <span class='boldtext'>"+totalTransactionAmount+"</span></p>";
					
					searchResults += "<div class='hidden-element'>";
					searchResults += "<p><span class=\"inlineLabel boldtext\">Keywords:</span>"+keywords+"</p>";
					searchResults += "<p><span class=\"inlineLabel boldtext\">Date:</span>"+dateOption+"</p>";
					searchResults += "<p><span class=\"inlineLabel boldtext\">Amount:</span>"+amountOption+"</p>";
					searchResults += "<p><span class=\"inlineLabel boldtext\">Category:</span>"+catgOption+"</p>";
					searchResults += "</div></div>";
					
					searchResults += "<p class=\"notes\">Pending Transactions will not appear in search results.</p>";
					searchResults += "</div>";
					searchResults += "<div id=\"search-sortContainer\">";
					searchResults += "	<label>";
					searchResults += "		<p id=\"sortByLabel\">Sort By</p>";
					searchResults += "			<select data-theme=\"d\" name=\"searchResultsSortBy\" id=\"js-search-results-sortby\">";
					if(sortOrder=="dateNewest"){
						searchResults += "					<option selected value=\"dateNewest\">Date: New to Old</option>";
					}else{
						searchResults += "					<option value=\"dateNewest\">Date: New to Old</option>";
					}
					if(sortOrder=="dateOldest"){
						searchResults += "					<option selected value=\"dateOldest\">Date: Old to New</option>";
					}else{
						searchResults += "					<option value=\"dateOldest\">Date: Old to New</option>";
					}
					if(sortOrder=="a-z"){
						searchResults += "					<option selected value=\"a-z\">Description: A to Z</option>";
					}else{
						searchResults += "					<option value=\"a-z\">Description: A to Z</option>";
					}
					if(sortOrder=="z-a"){
						searchResults += "					<option selected value=\"z-a\">Description: Z to A</option>";
					}else{
						searchResults += "					<option value=\"z-a\">Description: Z to A</option>";
					}			
					if(sortOrder=="lowAmt"){
						searchResults += "					<option selected value=\"lowAmt\">Amount: Low to High</option>";	
					}else{
						searchResults += "					<option value=\"lowAmt\">Amount: Low to High</option>";	
					}			
					if(sortOrder=="highAmt"){
						searchResults += "					<option selected value=\"highAmt\">Amount: High to Low</option>";	
					}else{
						searchResults += "					<option value=\"highAmt\">Amount: High to Low</option>";	
					}
					searchResults += "			</select>";
					searchResults += "	</label>";
					searchResults += "</div>";	
					searchResults += "<ul id=\"js-transactionsResults-list\" class=\"transaction-list\" data-role=\"listview\" data-theme=\"d\" data-inset=\"true\">";
					searchResults += generateTransHtml(searchResultData.postedTransactions);
					searchResults += "</ul>";
					searchResults += "<div id=\"loadTransactions-container\" class=\"js-loadTransactions-container\">";
					if(searchResultData.loadMoreLink !=null && searchResultData.loadMoreLink.length > 0){
						searchResults += '<a href="#" data-role="button" class="small_btn" id="js-loadMoreTransactions-button" data-transition="none"  data-ajax="false">Load More Transactions</a>';
						loadMore = true;
						putDataToCache(dfs.crd.stmt.shared.constant.cache.LOAD_MORE, searchResultData.loadMoreLink);
					}
					searchResults += "</div>";
				}
				
				//append the return data to the DOM
				(searchResultsContainer).html(searchResults);
				//add jQuery Mobile styles
				(searchResultsContainer).trigger('create');

				//set the instance of the sorty by select menu
				searchResultsSortyBy = $("#js-search-results-sortby", "#search-sortContainer");							
				//now BIND IT!
				(searchResultsSortyBy).unbind("change").bind("change", handleSearchResultsSortByChange );					
				//set the instance of the load more transactions button
				loadMoreTransactionsButton = $("#js-loadMoreTransactions-button", "#loadTransactions-container");
				(loadMoreTransactionsButton).unbind("click").bind("click", handleLoadMoreTransactionsClick);
				//set the instance of the loaded transactions list DOM element
				loadMoreTransactionsListContainer = $("#js-transactionsResults-list", "#js-searchResults-container");
				(loadMoreTransactionsButton).button();

				 // The header is pushed down on iOS 4.3.3, this fixes it
				$('#pg-header').css('top', '0px');

				//set the flag to indicate a search has been performed.
				searchPerformed = true;
			};
			dfs.crd.stmt.shared.ajax.getSearchResults(searchOptions, displaySearchResults);
		}
        searchTransFiltersSCVariables(dateOption,amountOption,catgOption);//passing site catalyst variables for Search Transactions - use filters
	};//==>submitSearchForm	
	
	/**
	 *
	 */
	handleScrollStop = function(e) {
		//fire orientation change event
		$(document).trigger('orientationchange.simpledialog', true);		
	};//==>handleScrollStop	

	/**
	 *
	 */
	handleDateTypeSelectionChange = function(e) {
		//toggle the inputs
		toggleSearchFilterInputs( this.selectedIndex,"date");
		//refresh JQM select widget display
		$(this).selectmenu('refresh');		
	};//==>handleDateTypeSelectionChange	

	/**
	 *
	 */
	handleAmountTypeSelectionChange = function(e) {
		//toggle the inputs
		toggleSearchFilterInputs( this.selectedIndex,"amount");
		//refresh JQM select widget display
		$(this).selectmenu('refresh');
	};//==>handleAmountTypeSelectionChange	
	
	/**
	 *
	 */
	handleUserManualDateInput = function(e) {
		$(this).removeClass('ui-focus');
		var val = $(this).val();
		dateInputValidation( val, $(this) );
	};//==>handleUserManualDateInput	
	
	/**
	 *
	 */
	handleCategoryTypeSelectionChange = function(e) {
		// if default go back to prior selection
		var list = $(this);
		if(list.val()==="default"){
			var opt = getDataFromCache(dfs.crd.stmt.shared.constant.cache.SR_LASTOPT);
			if(notEmpty(opt)){
				list.val( opt );
			}
		}else{
			putDataToCache(dfs.crd.stmt.shared.constant.cache.SR_LASTOPT, list.val());
		}
		//refresh JQM select widget display
		list.selectmenu('refresh');		
	};//==>handleCategoryTypeSelectionChange				

	/**
	 *
	 */
	handleSearchFormSubmit = function(e) {
		//prevent default form submission 
		e.preventDefault();
		submitSearchForm( "default" );
	};//==>handleSearchFormSubmit	
	
	/**
	 *
	 */
	handleLoadMoreTransactionsClick = function(e) {
		//prevent the default link click action
		e.preventDefault();
		dfs.crd.stmt.common.loadMoreTrans("js-transactionsResults-list", "loadTransactions-container");
	};//==>handleLoadMoreTransactionsClick	
	
	/**
	 *
	 */
	handleSearchResultsSortByChange = function(e) {
		e.preventDefault();
		//send a request for new search results
		submitSearchForm( $("#js-search-results-sortby").val() );		
	};//==>handleSearchResultsSortByChange		
															
	//public functions
	return {
		init : init,
		sortResults: sortResults,
		setTheDate: setTheDate
	};//==> return

})();//==> search


/***
*  Common Functions
*    - loadMoreTrans
* 	 - generateTransHtml
*    - getNoTransHtml
***/
dfs.crd.stmt.common = (function () {
	
	/**
	 * This function will load more transactions using the Load more URI which 
	 * is asssumed to be stored in a cached variable.
	 */
	loadMoreTrans = function(txnListId, elemToHideId) {
		
		var displayMoreTrans = function(transData,txnListId,elemToHideId) {
			var morePostedTrans = dfs.crd.stmt.common.generateTransHtml(transData.postedTransactions);
			$txnList = $('#'+txnListId);
			// Append HTML
			$txnList.append( morePostedTrans );
			// Apply JQM Style
			$txnList.listview('refresh');
			if( isEmpty(transData.loadMoreLink) ){
				$('#'+elemToHideId).hide();
				putDataToCache(dfs.crd.stmt.shared.constant.cache.LOAD_MORE, null);
			}else{
				putDataToCache(dfs.crd.stmt.shared.constant.cache.LOAD_MORE, transData.loadMoreLink);
			}
		};

		dfs.crd.stmt.shared.ajax.getMoreTrans(txnListId, elemToHideId, displayMoreTrans);
	
	};//==>loadMoreTrans
	
	/**
	 *  Generate the HTML for a List of Transactions
     *
	 * @param - txns - Array of Transactions
	 */
	generateTransHtml = function(txns) {
		var transHtml = "";
		for(var idx in txns){
			transHtml += "<li>";
			transHtml += "<div class=\"fl\">";
			transHtml += "	<p class=\"transaction-result-date\">"+txns[idx].txnDate+"</p>";
			transHtml += "	<p class=\"transaction-results-location\">"+txns[idx].txnDesc+"</p>";
			transHtml += "</div>";
			transHtml += "<div class=\"fr\">";
			transHtml += "	<p class=\"transaction-results-amount js-transaction-results-amount\">"+txns[idx].txnAmt+"</p>";
			transHtml += "</div>";
			transHtml += "<div class=\"clr\"><!-- --></div>";
			transHtml += "</li>";
	     }
	     return transHtml;
	};//==>generateTransHtml
	
	/**
	 * Generate HTML for when no transactions exist.
	 */
	getNoTransHtml = function(msg,isError){
		if(isError){
			return "<li><p class=\"error-message\">" + msg + "</p></li>";
		}else{
			return "<li><p class=\"zero-results-message\">" + msg + "</p></li>";
		}
	};//==>getNoTransHtml

	//public functions
	return {
		loadMoreTrans: loadMoreTrans,
		generateTransHtml: generateTransHtml,
		getNoTransHtml: getNoTransHtml
	};//==> return
	
})();//==> common


/**************  Start Page Load Functions **************************/

/**
 *  This function will be called pagebeforechangeevent of accountActivity
 */
function accountActivityLoad(){
	try{
		dfs.crd.stmt.acctactiv.init();
	}catch(err){
		hideSpinner();
		showSysException(err);
	}
}

/**
 * This function is called on pagebeforechangeevent of searchTrans
 */
function searchTransLoad(){
    try{
		dfs.crd.stmt.search.init();
		searchTransSCVariables();//passing sitecatalyst variable for Search Transactions 
    }catch(err){
        showSysException(err);
    }
}

/**  
 * This function will be called pagebeforechangeevent of statements
 **/ 
//TODO remove with R13.1
/*function statementsLoad(){
    try{
		$("#js-stmts-continue").click(function(){ window.plugins.childBrowser.showWebPage(dfs.crd.stmt.shared.constant.url.STMT_URL); });
		$("#js-stmts-cancel").click(function(){ navigation('../achome/accountLanding'); });
		var stmtsContent = dfs.crd.stmt.shared.ajax.getStmtsContent();
		$("#js-stmt-msg").html(stmtsContent.stmtMessage);	
    }catch(err){
        showSysException(err);
    }
}*/

/**
 * This function is called on pagebeforechangeevent of latePayWarn to fetch two dynamic variables
 *  needed to render the message on the page
 */
function latePayWarnLoad(){
    try{
		dfs.crd.stmt.shared.displayLatePayMsg("latePayMsg");
	}catch(err){
        showSysException(err);
    }
}
//------------------------------------------------------------------------------
//BEGIN CHANGES FOR R13.1
//------------------------------------------------------------------------------
dfs.crd.stmt.statementLanding = {};
/* Renders drop down list of past 6month select up to 7 years ago */
dfs.crd.stmt.statementLanding.renderDateRangeDropdown = function($el, statements) {
	var optionElement = function(v) {
		var newOption = document.createElement("option");
		newOption.value = v;
		newOption.appendChild( document.createTextNode(v) );
		return newOption;
	};	
	
	var frag = document.createDocumentFragment();
	$.each(statements.yearArray, function(k,v){
		frag.appendChild( optionElement(v) );
	});	

	$el.append(frag.cloneNode(true));
	return frag;
};

/* Renders a drop down list of statements based on the specified date by appending to element $el.
 * $el - element to append statement list to
 * date - selected time frame to display statement list for (past 6 months or a specified year)
 * statements - statements object containing all the necessary statement dates
 */
dfs.crd.stmt.statementLanding.renderStatementNavList = function($el, date, statements) {
	var liElement = function(date) {
		var d = dfs.crd.stmt.shared.util.getFormattedDate(date);

		var li = document.createElement("li");
		li.setAttribute("data-icon", "false");
		var a = document.createElement("a");
		a.className = "bluelink ui-link-inherit";
		a.setAttribute("data-date", date);
		a.textContent = d.monthName + " " + d.year;
		var span = document.createElement("span");
		span.className = "floatright arrow";
		span.textContent = "&gt;";
		li.appendChild( a.cloneNode(true) );
		li.appendChild( span.cloneNode(true) );
		return li;	
	};

	var frag = document.createDocumentFragment();
	
	if (date === "past6months") {
		$.each(statements.sixMonthArray, function(k,v) {
			frag.appendChild( liElement(v.date) );
		});
	} else {
		$.each(statements.map[date], function(k,v) {
			frag.appendChild( liElement(v.date) );
		});
	}
	console.log("StatementListFrag: ", frag);
	$el.empty().append(frag.cloneNode(true));

	//TODO determine why initial load of page doesn't render properly (like refresh isn't happening) (has to do with timing, setting timeout to 1ms before calling after page load fixes this, get to bottom of issue though)
	$el.listview('refresh');
	return frag;
};

/* Logic to render statement link or not based on statement data */
function accountLandingLoad() {
	try{
		var dom = {
			$statementsNav: $("#accountLanding-pg #statementsNav"),
				//TODO ideally clean this up so btn classes are generated automatically by JQM, prob spent ~2hrs playing around with .listview('refresh') and .trigger('create') but hardcoded like this for sake of time.		
			noStatements: '<div class="ui-btn-inner ui-li"><div class="ui-btn-text"><a href="#" class="graylink buttonBorderSupress ui-link-inherit">No statements are available</a></div></div>'
		};
		console.log("Account landing load calling getStatements");
		var statements = dfs.crd.stmt.shared.util.getStatements(); 
			if ( cardType === "000002" && statements.isEmpty() ) {
                    dom.$statementsNav.remove();
                } else if ( statements.isEmpty() ) {
			dom.$statementsNav.html(dom.noStatements);
		}
		//reset date selector so default item is displayed
		globalCache.statements_previousDateSelector = null;
	}catch(err){
		showSysException(err);
	}
}

/* Contains the date range drop down and lists off individual statements to drill down into */
function statementLandingLoad() {
	try{
		var statements = dfs.crd.stmt.shared.util.getStatements(); 
		var dom = {
			$statementList     : $("#statementLanding #statementList"),
			$dateRangeDropdown : $('#statementLanding #dateRangeDropdown')
		};

		dfs.crd.stmt.statementLanding.renderDateRangeDropdown(dom.$dateRangeDropdown, statements);
		dom.$dateRangeDropdown.change(function(){
			dfs.crd.stmt.statementLanding.renderStatementNavList(dom.$statementList, $(this).val(), statements);
			dom.$statementList.find('li').unbind('click').click(function(){
				var dateSelected = $(this).find('a').attr("data-date");
				globalCache.statements_previousDateSelector = dom.$dateRangeDropdown.val();
				globalCache.statements_selectedStatement = dateSelected;
				//navigation('./statementDetail');
				var index = dfs.crd.stmt.shared.util.getIndex(dateSelected, statements);
				/* when referencing dfs.crd.stmt.shared.constant.url.STATEMENTS_URL the LG Optimus L9 would interpolate each character 
				 * with a junk character (had an ascii value of 0). It must be a bug somewhere between the phone calling phonegap as
				 * when I construct the variable here it works as expected.
				 */
				var statementURL = RESTURL+"stmt/"+dfs.crd.stmt.shared.constant.STMT_WS_VERS+"/statements";
				RenderStatement.prototype.show(null,statements.array, index, statementURL);
				
			});
		});

		//remembers users previous date selection
		if (!_.isEmpty(globalCache.statements_previousDateSelector)) {
			dom.$dateRangeDropdown.val(globalCache.statements_previousDateSelector);
		}

		//TODO investigate why a 1ms timeout helps page render properly.  Without it list is rendered with square borders and page is shifted up behind header nav. Specifically padding top is 8px instead of 53px and ui-corner-top and ui-corner-bottom classes aren't on the first and last li and divs respectively.  
		setTimeout(function() { 
			dom.$dateRangeDropdown.trigger('change');
		}, 1);
	}catch(err){
        showSysException(err);
    }
}

/* hide show para in search result page */

$("#search .pn-plus").css("background-position","bottom left");
var searchResultStat = "open";
$("#search .hideLiMain").live("click",function(){
	if(searchResultStat=="open"){
		$(this).children(".pn-plus").css("background-position","0px -27px");
		$(this).parents().children(".hidden-element").show();
		searchResultStat = "close"
	}else{
		$(this).children(".pn-plus").css("background-position","0 0");
		$(this).parents().children(".hidden-element").hide();
		searchResultStat = "open";
	};
})

/*Statements 13.1 Script Ends Here*/
