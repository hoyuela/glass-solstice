# STEPS SPECIFIC TO THE "ACCOUNT" PAGES

# Asserts the presence of the gretting "greeting(based on time of day), [first name]"
# Uses the default credentials from config.yml
Given /^I see my salutation$/ do
	name = CONFIG[@userEnv.to_sym][@userType.to_sym][@userName.to_sym][:name]
	
	greeting = "Hi," + " " + name
	currentHour = Time.new.hour

	if currentHour < CONFIG[:afternoon_hour]
		salutation = "Good morning,"
	elsif currentHour < CONFIG[:evening_hour]
		salutation = "Good afternoon,"
	else
		salutation = "Good evening,"
	end
	
	greeting = salutation + " " + name
	performAction('wait_for_text', greeting)
end

# Selects the first Account in a list
# Must already be on a screen with a list of accounts.
# This is a special case since Calabash is not recognizing it as a ListView
# You may instead select by using default Calabash steps 
# to click on the specific account name text.
Given /^I select an account$/ do
	performAction('click_on_view_by_id', "acct_carat")
end

# Selects the first transaction from a list of transactions
Given /^I select a transaction$/ do
	# TEMPORARY WORKAROUND
	# Calabash console sometimes finds the listviews, eg. performAction("get_list_item_properties", 1)
	# but the same queries within the steps files fail with "list not found" (for any index)
	# Workaround: select the first date in the list
	performAction('click_on_text', "/")

	#get first item of the list
	#first_row = performAction('get_list_item_text', '1', '1')
	#peorformAction("click_on_view_by_id", first_row)
end

# Selects the down arrow next to the Account name which toggles details (eg. balance).
# You may accomplish the same task if you know the name of the Account 
# (as you can click on the text using default steps)
# Must already be on an Account Activity screen
Given /^I (?:select|toggle) account details?$/ do
	performAction("click_on_view_by_id", 'title_text')
end