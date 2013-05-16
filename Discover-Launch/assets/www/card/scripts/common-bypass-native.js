var customData;
var appState;
var sdkVersion;
var vendorID = "" ;//"123412341234123412341234";
var deviceLocation;
var badgeCount;
var returnCode;
var deviceID;
var deviceOS;
var osVersion;
var pageCode; // = "payment";
var reqID;
var pushRedirection = false;


function getDID () {
	try{
		var did ;
		/*if (device.platform == "Android")
		{
			did = "%&(()!12["+ Custom.getDeviceId();
		}
		else
		{
			did	 = "%&(()!12["+ device.uuid;
		}
        //alert("DID: " + did);
		did = Sha256.hash(did);
        //alert("DID: " + did);*/
		return "3f3db60896ac7454d960099fcc1aeaf851893a09dea858a15cffd18993e668d0";
	}catch(err){
		//showSysException(err)
	}
}

function getSID () {
	try{
		var sid 
		/*if (device.platform == "Android")
		{
			sid = "%&(()!12[" + Custom.getSimSerialNumber();
		}
		else
		{
			sid= "%&(()!12[" + "iPhone OS";
		}
        //alert("SID: " + sid);
		sid = Sha256.hash(sid);
        //alert("SID: " + sid);*/
		return "0860c0999586148537c98e84f05d4d5faf8c917277971e04aeba2fae0e1575b6";
	}catch(err){
		//showSysException(err)
	}
}

function getOID () {
	try{
		var oid;
		/*if (device.platform == "Android")
		{ 
			oid = "%&(()!12[" + device.uuid;
		}
		else
		{
			oid = "%&(()!12[" + device.version; //using PG API
		}
        //alert("OID: " + oid);
		oid = Sha256.hash(oid);
        //alert("OID: " + oid);*/
		return "993c77ca1ff92e7457b865b4514121234a2470c6463a35cb315a43cb1432a98e";
	}catch(err){
		//showSysException(err)
	}
}

// Begin Push Notification Change to add a method to retreive unhashed device OS & Device ID
function getOsVersion () {
	try{
		var oid;
		/*if (device.platform == "Android")
		{ 
			oid = device.uuid;
		}
		else
		{
			oid = device.version; //using PG API
		}*/
		return "5.1";
	}catch(err){
		//showSysException(err)
	}
}

function getDeviceID () {
	try{
		var did ;
		/*if (device.platform == "Android")
		{
			did = Custom.getDeviceId();
		}
		else
		{
			did	 = device.uuid;
		}*/
		return "84559A16-5723-4F10-82C5-BFA77989B8E0";
	}catch(err){
		//showSysException(err)
	}
}

// End Push Notification change

//Added below code to make request headers completely accurate 
function getClientPlat ()
{
	try{
        //alert("Platform: " + device.platform);
		return "iPhone" //device.platform;
	}catch(err){}
}

function getXidFromNative()
{
    vendorID = "5004723487242167C690E0E0";
}
