# Steps pertaining to the Android Discover app login screen

# Wait for Splash Screen to dismiss
Given /^The splash screen is finished$/ do
	performAction('wait_for_text', "User ID")
end

# Logs in to the user with the given environment, type, and username paramaters and verifies the salutation
# (e.g. I log in as ASYS Card user 6011000017009840)
Given /^I log in as (?:a|an) "([^\"]*)" "([^\"]*)" "([^\"]*)" user$/ do |type, env, cardBank|	
	macro 'The splash screen is finished'

	performAction('scroll_down')

	if cardBank == 'card'
		macro %Q[I am on the card tab]
	elsif cardBank == 'bank'
		macro  %Q[I am on the bank tab]
	end

	performAction('scroll_up')

	# Now we set the following instance variables so we can reference the user's information later in any step
	@userEnv = env
	@userType = type
	@userCardBank = cardBank

	@userName = CONFIG[env.to_sym][cardBank.to_sym][type.to_sym][:username]
	@password = CONFIG[env.to_sym][cardBank.to_sym][type.to_sym][:password]

	performAction('enter_text_into_id_field', "#{@userName}", "username_field")
	performAction('enter_text_into_id_field', "#{@password}", "password_field")

	# Press Login then wait for the loading dialog to close so we can check for strong auth
	performAction('click_on_view_by_id', "login_button")
	performAction('wait_for_dialog_to_close')

	# Check if strong auth is needed
	if (query("textView marked:'Enhanced Account Security'")).size > 0 || CONFIG[env.to_sym][cardBank.to_sym][type.to_sym][:strongauth] == true
		macro %Q[I answer the strong auth prompt]
		performAction('wait_for_dialog_to_close')
	end

	# Check if "What's new screen" is showing, if it is tap the close image button
	if !(query("imageButton marked:'close'").empty?) 
		touch("imageButton marked:'close'")
	end

	# Now look for the salutation
	macro %Q[I see my salutation]
end

# Tries to login with random characters and verifies that text containing sorry appears
Given /^I cannot login with garbage$/ do
	performAction('scroll_up')

	performAction('enter_text_into_id_field', "asdfasdasdf", "username_field")
	performAction('enter_text_into_id_field', "asdfasdasdf", "password_field")

	performAction('click_on_view_by_id', "login_button")

	performAction('assert_text',"Sorry",true)
end

# Switches to the Card or Bank tab on the Login screen
Given /^I am on the (card|bank) tab$/ do |which|
	id = which + "_login_toggle"
	performAction('click_on_view_by_id', "#{id}")
end

# Answers the strong auth prompt using the security answer for the user in the config file
Given /^I answer the strong auth prompt$/ do
	performAction('wait_for_screen', "EnhancedAccountSecurityActivity", DEFAULT_SERVICE_TIMEOUT)

	# Enter the security answer for the current user
	performAction('enter_text_into_numbered_field', CONFIG[@userEnv.to_sym][@userCardBank.to_sym][@userType.to_sym][:security_answer], 1)
	
	# Select public device radio
	performAction('click_on_view_by_id', "account_security_choice_two_radio") # "No. This is a public device."
	
	# Press continue button and wait
	performAction('click_on_view_by_id', "account_security_continue_button") # "Continue" button
	performAction('wait_for_dialog_to_close') # Wait for Loading dialog to close
end

# Toggles the "Remember Me" button on the Login screen
Given /^I press remember me$/ do
	performAction('click_on_view_by_id', "remember_user_id_button")
end

# Taps the ATM Locator button on the login screen
Given /^I view the ATM Locator$/ do
	wait_for(WAIT_TIMEOUT) { element_exists( "button marked:'RegisterNowATMLocatorButton'" ) }
	touch "button marked:'RegisterNowATMLocatorButton'"
end

# Go through forgot user id/password flow
Given /^I forgot my user id and password$/ do
	performAction('scroll_down')
	performAction('click_on_view_by_id', "forgot_uid_or_pass_text")
	performAction('wait_for_dialog_to_close')
	performAction('wait',1)

	# touch forgot User ID and go back
	touch("textView marked:'text1'")[0]
	performAction('wait',1)
	performAction('go_back')

	# touch forgot password and go back
   	performAction('wait',1)
   	x = query("textView marked:'text1'")[1]["rect"]["center_x"]
	y = query("textview marked:'text1'")[1]["rect"]["center_y"]
	performAction("touch_coordinate", x, y)
	performAction('wait',1)
	performAction('go_back')

	# touch forgot both and go back
   	performAction('wait',1)
   	x = query("textView marked:'text1'")[2]["rect"]["center_x"]
	y = query("textview marked:'text1'")[2]["rect"]["center_y"]
	performAction("touch_coordinate", x, y)
	performAction('wait',1)
	performAction('go_back')

	performAction('wait',1)
	performAction('go_back')
end