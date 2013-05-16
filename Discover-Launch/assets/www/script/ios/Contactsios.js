function showAllContacts(){
    
    window.plugins.ContactsList.showContacts(function(result1,result2) {
                                             //alert("We got a barcode: " + result1+"--"+result2);
                                             document.getElementById("fullname").value = result1;
                                             document.getElementById("emailOrphone").value = result2;
                                             }, function(error) {
                                             alert(error);
                                             }
                                             );
    
    
}

function showAllContactsForRAF(){
    console.log("inside showAllContactsForRAF");
	window.plugins.ContactsList.showContacts(function(result1,result2) {
        console.log("result2 == " + result2);
        var to_email = $('.email-inputs').val();
        
        if (to_email != null && to_email.length > 0) {
          $('.email-inputs').val(to_email + " " + result2);
          $('.email-inputs').css("height","auto");                             
        } else {
          $('.email-inputs').val(result2); 
		}
		$('.email-inputs').focus();
		}, function(error) {
		console.log(error);
		}
	);    
}

var ContactsList = function() {
    
}

ContactsList.prototype.callbackMap = {};
ContactsList.prototype.callbackIdx = 0;



ContactsList.prototype.showContacts = function(success, fail) {
    var plugin = window.plugins.ContactsList,
    cbMap = plugin.callbackMap,
    key = 'showContacts' + plugin.callbackIdx++;
    
    cbMap[key] = {
    success: function(result1,result2) {
        delete cbMap[key];
        success(result1,result2);
    },
    fail: function(result) {
        delete cbMap[key];
        fail(result);
    }
    };
    
    var cbPrefix = 'window.plugins.ContactsList.callbackMap.' + key;
    
    return PhoneGap.exec("ContactsList.showContacts", cbPrefix + ".success", cbPrefix + ".fail");
};

PhoneGap.addConstructor(function()
                        {
                        if(!window.plugins)
                        {
                        window.plugins = {};
                        }
                        window.plugins.ContactsList = new ContactsList();
                        });