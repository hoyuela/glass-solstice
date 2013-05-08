var HybridControl = function() {};

// Show/Hide Menu
HybridControl.prototype.showMenu = function () {
	cordova.exec(null, null, 'HybridControlPlugin', 'showMenu', []);
};

HybridControl.prototype.hideMenu = function () {
	cordova.exec(null, null, 'HybridControlPlugin', 'hideMenu', []);
};

// Status Bar
HybridControl.prototype.showHeaderStatusBar = function() {
    cordova.exec(null, null, 'HybridControlPlugin', 'showHeaderStatusBar', []);
}

HybridControl.prototype.hideHeaderStatusBar = function() {
    cordova.exec(null, null, 'HybridControlPlugin', 'hideHeaderStatusBar', []);
}

HybridControl.prototype.changeStatusText = function (newTitle) {
	cordova.exec(null, null, 'HybridControlPlugin', 'changeStatusText', [newTitle]);
};

// Navigation Bar
HybridControl.prototype.showHeaderNavBar = function() {
    cordova.exec(null, null, 'HybridControlPlugin', 'showHeaderNavBar', []);
}

HybridControl.prototype.hideHeaderNavBar = function() {
    cordova.exec(null, null, 'HybridControlPlugin', 'hideHeaderNavBar', []);
}

// iPhone/Android
// if no title is passed moves to logo view
HybridControl.prototype.setTitleView = function(title) {
    if (title != null) {
        cordova.exec(null, null, 'HybridControlPlugin', 'setTitleView', [title]);
    } else {
        cordova.exec(null, null, 'HybridControlPlugin', 'setTitleView', []);
    }
}

HybridControl.prototype.popPhoneGapToFront = function(successCallback, title) {
	if (title != null) {
	cordova.exec(successCallback, null, 'HybridControlPlugin', 'popPhoneGapToFront', [title]);
	} else {
		title = "No Title";
	    cordova.exec(successCallback, null, 'HybridControlPlugin', 'popPhoneGapToFront', [title]);
	}
}

HybridControl.prototype.pushFragment = function(successCallback, failureCallback, fragmentName) {
	cordova.exec(successCallback, failureCallback, 'HybridControlPlugin', 'pushFragment', [fragmentName]);
}

HybridControl.prototype.popCurrentFragment = function(successCallback, failureCallback) {
	cordova.exec(successCallback, failureCallback, 'HybridControlPlugin', 'popCurrentFragment', []);
}

HybridControl.prototype.getAccountDetails = function(successCallback, failureCallback) {
	cordova.exec(successCallback, failureCallback, 'HybridControlPlugin', 'getAccountDetails', []);
}

HybridControl.prototype.dismissProgressBar = function(successCallback, failureCallback) {
//alert("dismiss");
	
	cordova.exec(successCallback, failureCallback, 'HybridControlPlugin', 'dismissProgressBar', []);
}

HybridControl.prototype.getSecToken = function(successCallback, failureCallback) {
	cordova.exec(successCallback, failureCallback, 'HybridControlPlugin', 'getSecToken', []);
}

HybridControl.prototype.updatedAccountDetails = function(successCallback, failureCallback) {
	cordova.exec(successCallback, failureCallback, 'HybridControlPlugin', 'updatedAccountDetails', []);
}
HybridControl.prototype.getStrongAuthSvcs = function(successCallback, failureCallback) {
	cordova.exec(successCallback, failureCallback, 'HybridControlPlugin', 'getStrongAuthSvcs', []);
}
HybridControl.prototype.logOutUser = function() {
    cordova.exec(null, null, 'HybridControlPlugin', 'logOutUser', []);
}
HybridControl.prototype.gotoAchome = function() {
    cordova.exec(null, null, 'HybridControlPlugin', 'popCurrentFragment', []);
}
HybridControl.prototype.getSID = function(successCallback, failureCallback) {
	cordova.exec(successCallback, failureCallback, 'HybridControlPlugin', 'getSID', []);
}

HybridControl.prototype.getDID = function(successCallback, failureCallback) {
	cordova.exec(successCallback, failureCallback, 'HybridControlPlugin', 'getDID', []);
}
HybridControl.prototype.getOID = function(successCallback, failureCallback) {
	cordova.exec(successCallback, failureCallback, 'HybridControlPlugin', 'getOID', []);
}
HybridControl.prototype.showSpinner = function(successCallback, failureCallback) {
	cordova.exec(successCallback, failureCallback, 'HybridControlPlugin', 'showSpinner', []);
}
HybridControl.prototype.getVID = function(successCallback, failureCallback) {
	cordova.exec(successCallback, failureCallback, 'HybridControlPlugin', 'getVID', []);
}
HybridControl.prototype.getOtherUserFlag = function(successCallback, failureCallback) {	
	cordova.exec(successCallback, null, 'HybridControlPlugin', 'getOtherUserFlag', []);	
}
HybridControl.prototype.setOtherUserFlag = function(successCallback, OtherUserFlag) {	
	cordova.exec(successCallback, null, 'HybridControlPlugin', 'setOtherUserFlag', [OtherUserFlag]);	
}
HybridControl.prototype.enableSlidingMenu = function(successCallback, enable) {
	cordova.exec(successCallback, null, 'HybridControlPlugin', 'enableSlidingMenu', [enable]);
}
