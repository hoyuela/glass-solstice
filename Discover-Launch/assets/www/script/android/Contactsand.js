var ContactsView = function() {};

showAllContacts = function(successCallback, failureCallback) {   


    return PhoneGap.exec(function(args) {
        document.getElementById("fullname").value = args.name;
        document.getElementById("emailOrphone").value = args.value;
         }, failureCallback, 'ContactsViewPlugin', 'invokecontacts', ["jsonformat"]);
};

showAllContactsForRAF = function(successCallback, failureCallback) {   
    return PhoneGap.exec(function(args) {
        console.log("args.value == " + args.value);
        var to_email = $('.email-inputs').val(); 
        if (to_email != null && to_email.length > 0) {
          $('.email-inputs').val(to_email + " " + args.value);
          $('.email-inputs').css("height","auto")                             
        } else {
          $('.email-inputs').val(args.value); 
		}
		$('.email-inputs').focus();
		}, failureCallback, 'ContactsViewPlugin', 'invokecontacts', ["jsonformat"]);
};

PhoneGap.addConstructor(function() {
    PhoneGap.addPlugin("contactsview", new Contacts());
});