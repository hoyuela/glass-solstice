var PassKit = function() {};

PassKit.prototype.firePass = function(failCallback, url) {
    var regArr = new Array();
    var regArrIndex = 0;
    regArr[regArrIndex++] = url;
    for (var i = 2; i < arguments.length; i++) {
        if (arguments[i] != null ) {
            arguments[i] = arguments[i].replace(/\s/g, ''); // remove whitespace
            if (arguments[i] != '') {
                regArr[regArrIndex++] = arguments[i];
            }
        }        
    }
	return cordova.exec(null, failCallback, "PassKitPlugin", "firePass", regArr);
}

PhoneGap.addConstructor(function() {
	PhoneGap.addPlugin("PassKit", new PassKit());
});