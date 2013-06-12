var Print = function() {};

Print.prototype.print = function(successCallback, failCallback,  printHtml, dialogLeftPos, dialogRightPos) {
    if (dialogLeftPos == null) {
        dialogLeftPos = "0";
    }
    if (dialogRightPos == null) {
        dialogRightPos = "0";
    }
    cordova.exec(successCallback, failCallback, 'PrintPlugin', 'print', [printHtml, dialogLeftPos, dialogRightPos]);
}

Print.prototype.isPrintingAvailable = function(callback) {
	alert('PrintPlugin.prototype.isPrintingAvailable');
    cordova.exec(callback, function(e){alert(e);}, 'PrintPlugin', 'isPrintingAvailable', []);
};

PhoneGap.addConstructor(function() {
	PhoneGap.addPlugin("Print", new Print());
});