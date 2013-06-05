require 'YAML'
CONFIG = YAML::load_file(File.join(File.dirname(File.expand_path(__FILE__)), 'config.yml'))
DEFAULT_SERVICE_TIMEOUT = 60; # Seconds to wait for a service call to complete

# Wait for Splash Screen to dismiss
Given /^The splash screen is finished$/ do
	performAction('wait_for_text', "User ID")
end

# Switches to the Card or Bank tab on the Login screen
Given /^I am on the (card|bank) tab$/ do |which|
	id = which + "_login_toggle"
	performAction('click_on_view_by_id', id)
end

# Allows for custom credentials on the login screen
Given /^I enter credentials "([^\"]*)", "([^\"]*)"$/ do |user, password|	
	performAction('clear_numbered_field', 1)
	performAction('clear_numbered_field', 2)
	performAction('enter_text_into_numbered_field', user, 1)
	performAction('enter_text_into_numbered_field', password, 2)
end

# Performs a basic login and completes the Enhanced Security Screen if needed
# Uses credentials from config.yml
Given /^I am logged in as(?: a| the)? default (bank|card) user$/ do |which|	
	userType = "default_" + which + "_user"

	username = CONFIG[userType.to_sym][which.to_sym][:username]
	password = CONFIG[userType.to_sym][which.to_sym][:password]

	macro 'The splash screen is finished'
	macro %Q[I am on the #{which} tab]

	macro %Q[I enter credentials "#{username}", "#{password}"]

	performAction('click_on_view_by_id', "login_button") # Presess the "Login" Button
	
	# Check if strongauth will be shown
	if CONFIG[userType.to_sym][:strongauth] == true
		macro 'I am on a public device' # Complete enhanced account security screen
	end

	performAction('wait_for_screen', "BankNavigationRootActivity", DEFAULT_SERVICE_TIMEOUT) # Wait for the home screen. 	
end

#Login as sso user
Given /^I am logged in as(?: a| the)? default sso user on the (card|bank) tab$/ do |which|
	username = CONFIG[:default_sso_user][which.to_sym][:username]
	password = CONFIG[:default_sso_user][which.to_sym][:password]

	macro 'The splash screen is finished'
	macro %Q[I am on the #{which} tab]

	macro %Q[I enter credentials "#{username}", "#{password}"]

	performAction('click_on_view_by_id', "login_button") # Press Login button

	# Check if strongauth will be shown
	if CONFIG[:default_sso_user][:strongauth] == true
		macro 'I am on a public device' # Complete enhanced account security screen
	end
end

# Completes the Enhanced Security Screen but does not save the device
# Meant to be called during Login if the Enhanced Security Screen is to be shown
Given /^I am on a public device$/ do
	# Wait for the security screen (up to 60 seconds)
	performAction('wait_for_screen', "EnhancedAccountSecurityActivity", DEFAULT_SERVICE_TIMEOUT)

	# Security answer
	security = CONFIG[:securityAnswer]
	performAction('enter_text_into_numbered_field', security, 1)

	performAction('click_on_view_by_id', "account_security_choice_two_radio") # "No. This is a public device."
	performAction('click_on_view_by_id', "account_security_continue_button") # "Continue" button
	performAction('wait_for_dialog_to_close') # Wait for Loading dialog to close
end

# Toggles the "Remember Me" button on the Login screen
Given /^I press remember me$/ do
	performAction('click_on_view_by_id', "remember_user_id_button")
end

# Asserts the presence of the gretting "greeting(based on time of day), [first name]"
# Uses the default credentials from config.yml
Given /^I see the (bank|card) greeting for the default (bank|card|sso) user$/ do |which, userType|	
	user = "default_" + userType + "_user"
	name = CONFIG[user.to_sym][which.to_sym][:name]
	
	greeting = "Hi," + " " + name

	# Check if bank because greetings are different
	if which == "bank"
		currentHour = Time.new.hour

		if currentHour < CONFIG[:afternoon_hour]
	   		salutation = "Good morning,"
		elsif currentHour < CONFIG[:evening_hour]
			salutation = "Good afternoon,"
		else
			salutation = "Good evening,"
		end
		greeting = salutation + " " + name
	end
	performAction('wait_for_text', greeting)
end

# Clicks the "Log Out" button and asserts return to the Login screen
Given /^I (?:am logged|log) out$/ do
	performAction('click_on_view_by_id', "logout_button")
	performAction('wait_for_dialog_to_close') # Wait for Loading dialog to close
	performAction('wait_for_screen', "LoginActivity", DEFAULT_SERVICE_TIMEOUT)
end
