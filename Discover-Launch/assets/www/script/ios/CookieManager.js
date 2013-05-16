var CookieManager = function() {};

CookieManager.prototype.getCookie = function (retrieveCookieCallback, failCallback, cookieName) {
	cordova.exec(retrieveCookieCallback, failCallback, "CookieManagerPlugin", "getCookie", [cookieName]);
}

PhoneGap.addConstructor(function() {
	PhoneGap.addPlugin("CookieManager", new CookieManager());
});