

/*
 * Description: formats the last Payment and payment Due date
 */
function formatDate(paymentDueDate)
{
	try{
		var paymentMonth = paymentDueDate.substring(0,2);
		var paymentDay = paymentDueDate.substring(2,4);
		var paymentYear = paymentDueDate.substring(4);
		var paymentDateFormat = paymentYear+"\/"+paymentMonth+"\/"+paymentDay;
		var date = new Date(paymentDateFormat);
		date.toString("yyyy-mm-dd");
		//one is added to month as the function getMonth returns the month (from 0-11)
		return date;
	}catch(err){
		showSysException(err)
	}
}

function convertDate(dateObj) {
	try{
		var dd = dateObj.getDate();
		if (dd < 10)
			dd = '0' + dd;
		var mm = dateObj.getMonth() + 1;
		if (mm < 10)
			mm = '0' + mm;
		return String(mm + "\/" + dd);
	}catch(err){
		showSysException(err)
	}
}

/*
 * Description: Calculates the Difference betwenn the Current Date and the last Payment Date.
 */
function calculateDuration(lastPaymentDate)
{
	try{
		var date = new Date();
		var duration = date.getTime()- lastPaymentDate.getTime();
		//one is added to month as the function getMonth returns the month (from 0-11)
		payDueDate = duration/(24*60*60*1000);
		return payDueDate;
	}catch(err){
		showSysException(err)
	}
}

function accountEarnsCBB(incentTypeCode){
	return (CBB_INCENTIVE_TYPE == incentTypeCode || SBC_INCENTIVE_TYPE == incentTypeCode);
}

function accountEarnsMiles(incentTypeCode){
	return (MILES_INCENTIVE_TYPE == incentTypeCode || BUS_MILES_INCENTIVE_TYPE == incentTypeCode );	
}

/**
 * Format 10 digit phone number in (xxxx)xx-xxxx
 */
function formatPhoneNumber(phone)
{
	try{
		var formattedPhone = "";
		if(phone.length == 10)
		{
			formattedPhone = "(" + phone.substring(0,3) + ") " + phone.substring(3,6) + "-" + phone.substring(6);
			return formattedPhone;
		}
		else if (phone.length==14 || phone.length==13)
		{
			return phone;
		}	
	}catch(err){
		showSysException(err)
	}
}

/**
 * Verify amount in decimal format or not?
 * @param amount
 * @returns
 */
function isDecimal( amount )
{
	return amount != "" ? /^\d*\.?\d*$/.test( amount ) : false;
}
/**
 * Verify amount has 2 decimal or not
 * @param amount
 * @returns
 */
function amountHasonly2Decimals(amount)
{
	return amount !="" ? /^\d+\.\d{0,2}$/.test(amount) : false;
}

/** 
 * @param email - Verfiy the email format with Regular 
 * expression emailFormat in applicationConstant.js
 * @returns {Boolean}
 */
function validateEmailFormat(email)
{
	try{
		if(email.search(emailFormat))
		{
			return false;
		}
		else
		{
			return true;
		}
	}catch(err){
		showSysException(err)
	}
}


/**
 *  Verify that name should not have any special character
 * @returns {Boolean}
 */
function validateName(name)
{
	try{
		if(!isEmpty(name))
		{		
			if(regExpressionName.test(name) == false)
			{
				return false;
			}
			else
			{
				return true;
			}
		}else{
			return true;
		}
	}catch(err){
		showSysException(err)
	}
}
function convertTo2DecimalPoints(amount)
{
	try{
		var amtFloat=parseFloat(amount);
		var amoutInDecimals=(amtFloat).toFixed(2);
		var amount=amoutInDecimals;
		return amount;
	}catch(err){
		showSysException(err)
	}
}

$.fn.serializeObject = function()
{
	var o = {};
	var a = this.serializeArray();
	$.each(a, function() {
		if (o[this.name] !== undefined) {
			if (!o[this.name].push) {
				o[this.name] = [o[this.name]];
			}
			o[this.name].push(this.value || '');
		} else {
			o[this.name] = this.value || '';
		}
	});
	return o;
}

function formatPaymentDueDate(paymentDueDate){
	try{
		if(!isEmpty(paymentDueDate)){
			var monthArray=['Jan','Feb','Mar','Apr','May','Jun','Jul',
							'Aug','Sep','Oct','Nov','Dec'];
			var paymentMonth = paymentDueDate.substring(0,2);
			var paymentDay = paymentDueDate.substring(3,5);
			var paymentYear = paymentDueDate.substring(6);
			var paymentDateFormat = paymentYear+"\/"+paymentMonth+"\/"+paymentDay;
			var date = new Date(paymentDateFormat);
			//one is added to month as the function getMonth returns the month (from 0-11)
			date= monthArray[date.getMonth()]+" "+ paymentDay;
			return date;
		}
	}catch(err){
		showSysException(err)
	}
}


function formatPaymentDueDate_MakePaymentStep1(paymentDueDate){
	try{
		var monthArray=['Jan','Feb','Mar','Apr','May','Jun','Jul',
		                'Aug','Sep','Oct','Nov','Dec'];
		date= monthArray[paymentDueDate.getMonth()] +" "+ paymentDueDate.getDate()+", " + paymentDueDate.getFullYear();
		return date;
	}catch(err){
		showSysException(err)
	}
}


function formatPostingDueDate_MakePaymentStep2(paymentDueDate){
	try{
		var monthArray=['Janurary','Feburary','March','April','May','June','July',
		                'August','September','October','November','December'];
		var paymentMonth = paymentDueDate.substring(0,2);
		var paymentDay = paymentDueDate.substring(3,5);
		var paymentYear = paymentDueDate.substring(6);
		var paymentDateFormat = paymentYear+"\/"+paymentMonth+"\/"+paymentDay;
		var date = new Date(paymentDateFormat);
		date.toString("yyyy-mm-dd");
		//one is added to month as the function getMonth returns the month (from 0-11)
		date= monthArray[date.getMonth()] +" "+ paymentDay;
		return date;
	}catch(err){
		showSysException(err)
	}
}