/*********************** Customer Service Pages Start*************************/
function contactUsLoad(){}

function customerServiceFaqsLoad(){
    try{
    	console.log("WE are here in LOAD of FAQ with Card Product Group 0 :-"+cardProductGroupCode);
		if(!isEmpty(cardProductGroupCode)){
            var htmlFileName ="";
            console.log("WE are here in LOAD of FAQ with Card Product Group 1 :-"+cardProductGroupCode);
            switch(cardProductGroupCode){
                case "MOR": case "MTV": case "OPR":
                    htmlFileName = "customerServiceFaqs_CBB";
                    break;
                case "DIT":
                    htmlFileName = "customerServiceFaqs_ITCard";
                    break;
                case "MLS": case "ESC":
                    htmlFileName = "customerServiceFaqs_Miles";
                    break;
                default:
                    htmlFileName = "customerServiceFaqs_Others";
                    break;
            }
            
            if(isEmpty(htmlFileName)){
                errorHandler('Frequently Asked Question','0','','customerServiceFaqs');
            }
            console.log("WE are here in LOAD of FAQ with Card Product Group 2 :-"+htmlFileName);
            var customerServiceFaqsText = getcustomerServiceFaqsData(false,htmlFileName);
            console.log("We are the customer FAQ :- "+customerServiceFaqsText);
            if(!isEmpty(customerServiceFaqsText)){
                $("#customerServiceFaqsTextDiv").html("");
                $("#customerServiceFaqsTextDiv").html(customerServiceFaqsText);
            }else{
                errorHandler('Frequently Asked Question','0','','customerServiceFaqs');
            }
        }else{
            errorHandler('Frequently Asked Question','0','','customerServiceFaqs');
        }
		
    }catch(err){
		showSysException(err);
	}
    
    
}

function getcustomerServiceFaqsData(accessLocalFile,htmlFileName){
    try{
        
		var customerServiceFaqsDetails = "";
		console.log("THE Boolean value of if we have tio acees the local file"+accessLocalFile);
		if(accessLocalFile){
            customerServiceFaqsDetails = getContentHtml("../../html/customerService/"+htmlFileName);
			return customerServiceFaqsDetails;
		}
		
		var newDate = new Date();
		var customerServiceFaqsURL = HREF_URL + "json/faq_android/"+htmlFileName+".html?"+newDate+"";
		console.log("In ajax call for FAQ :- "+customerServiceFaqsURL);
		showSpinner();
        $.ajax({
               type : "GET",
               url : customerServiceFaqsURL,
               async : false,
               dataType : 'html',
               success : function(responseData, status, jqXHR) {
               hideSpinner();
               if (jqXHR.status != 200) {
               var code=getResponseStatusCode(jqXHR);
               errorHandler('Frequently Asked Question',code,'','customerServiceFaqs');
               }else{
               customerServiceFaqsDetails = responseData;
               customerServiceFaqsDetails = $(responseData).find('div[data-role="content"]').html();
               console.log("In success of ajax call for FAQ with data :- "+JSON.stringify(customerServiceFaqsDetails));
               }
               },
               error : function(jqXHR, textStatus, errorThrown) {
               hideSpinner();
               var code=getResponseStatusCode(jqXHR);
               console.log("WE are in error with jqxhr :- "+code)
               errorHandler('Frequently Asked Question',code,'','customerServiceFaqs');
               }
               });
		
		return customerServiceFaqsDetails;
	}catch(err){
		showSysException(err);
	}
}

/***********************  Customer Service Pages End*************************/