var CookieManager = function() {};

CookieManager.prototype.getCookie = function (retrieveCookieCallback, failCallback, cookieName) {
    cordova.exec(retrieveCookieCallback, failCallback, "CookieManagerPlugin", "getCookie", [cookieName]);
}

CookieManager.prototype.getCookieFromBaseUrl = function(retrieveCookieCallback, failCallback, cookieName, cookieBaseUrl) {
    cordova.exec(retrieveCookieCallback, failCallback, "CookieManagerPlugin", "getCookie", [cookieName, cookieBaseUrl]);
}

PhoneGap.addConstructor(function() {
                        PhoneGap.addPlugin("CookieManager", new CookieManager());
                        });