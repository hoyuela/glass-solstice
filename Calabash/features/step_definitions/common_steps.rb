# Common steps that can be useful throughout the Discover application

# Navigates to the home screen
Given /^I (?:navigate|go) to home$/ do
	macro 'I press the menu button'
	performAction('wait', 1)
	performAction('click_on_text',"Home")
end

# Asserts a View with the given ID exists
Given /^I(?: should)? see view with id "([^\"]*)"$/ do |id|
	performAction('has_view', id)
end

# Scrolls down (uses screen percents)
Given /^I scroll down$/ do
	performAction("drag", 50, 50, 50, 0, 1)
end

# Scrolls up (uses screen percents)
Given /^I scroll up$/ do
	performAction("drag", 50, 50, 0, 50, 1)
end

# Saves the text contained within a text view
Given /^I save text from view with id "([^\"]*)"$/ do |id|
	@savedText = query("textview marked:'#{id}'")[0]["text"]
	puts "Saved: " + @savedText
end

# Saves the text contained within a text view that has a corresponding text view label
Given /^I save text with label "([^\"]*)"$/ do |label|
	text = query('textview', 'text') # Creates an array of all the text (in TextViews) on the screen (in order)
	index = text.index("#{label}")

	if index == nil then
		raise 'Label ' + label + " Not Found"
	else
		@savedText = text[index + 1]
	end
end

# Determines if the previously saved text is currently visible on the screen
Given /^I see the saved text$/ do
	macro 'I see "#{@savedText}"'
end

# Determines if the previously saved text is currently visible on the screen
Given /^I (?:don't|should not) see the saved text$/ do
	macro 'I should not see "#{@savedText}"'
	#'
end

# Toggles the main sliding menu
Given /^I (?:press|touch) the menu button$/ do
  	performAction('wait_for_view_by_id', 'navigation_button')
	performAction('click_on_view_by_id', 'navigation_button')
	performAction('wait', 2) # Time for menu animation
end

# Presses the orange help button
Given /^I (?:press|touch) the help button$/ do
	performAction('click_on_view_by_id', 'help')
end

# Opens the menu, selects the specified category, and selects the subcategory
# MENU SHOULD BE CLOSED
Given /^I (?:navigate|go) to "([^\"]*)" under "([^\"]*)"$/ do |subtitle, title|
	macro 'I press the menu button'

	# Check if the subtitle is already visible and that its size is greater than 0
	# then we do not need to tap on the title
	if ((query("textView marked:'#{subtitle}'").size > 0 && subtitle != title) || (query("textView marked:'#{subtitle}'").size > 1))
		# Just tap the subtitle item (which is the second index)
		# Using touch() to tap the second index of the title results in the same text being tapped (calabash bug?)
		# so we just get the coordinates of the second index and tap that
		if(subtitle == title)
			x = query("textview marked:'#{subtitle}'")[1]["rect"]["center_x"]
			y = query("textview marked:'#{subtitle}'")[1]["rect"]["center_y"]
		else
			x = query("textview marked:'#{subtitle}'")[0]["rect"]["center_x"]
			y = query("textview marked:'#{subtitle}'")[0]["rect"]["center_y"]
		end
		
		performAction("touch_coordinate", x, y)

	elsif subtitle == title
		# Tap the first occurence of title then tap the second occurence
		touch("textView marked:'#{title}'")[0]

		x = query("textview marked:'#{subtitle}'")[1]["rect"]["center_x"]
		y = query("textview marked:'#{subtitle}'")[1]["rect"]["center_y"]
		performAction("touch_coordinate", x, y)
	else
		# Otherwise tap the title then the subtitle
		performAction('click_on_text',"#{title}")
		performAction('click_on_text',"#{subtitle}")
	end
	performAction('wait', 4) # Time for menu animation
end

# Enters a random dollar amount in a Discover (custom) currency input field
Given /^I (?:put|enter) a random (?:number|amount) between (\d+) and (\d+) (?:in|into) field (?:number )?(\d+)$/ do |num1, num2, field|
	num1 = num1.to_i
	num2 = num2.to_i
	amount = (rand(num2) + num1).to_s # random number (num1-num2)
	
	puts 'Random amount generated: ' + amount.to_s

	# Enter the random payment amount
	# TODO: Find better workaround if possible...
	# ISSUE: The first number you enter will show up in the 100th column (9 -> $0.09)
	# Even a higher number will only enter one digit like above
	# Need to add the number three times, which will move it over 3 decimals to reach $1 minimum
	# Has something to do with the non-standard EditText being restricted to numbers
	if amount.to_i >= 10
		amountFirstDigit = amount.to_i / 10
		amountSecondDigit = amount.to_i % 10

		#macro %Q[I enter #{amount.to_s} into input field number 1]

		# Current workaround - will only enter the first digit and zero's into the field / no way to enter second digit
		performAction('enter_text_into_numbered_field', amountFirstDigit,field)
		performAction('enter_text_into_numbered_field', amountSecondDigit, field)
		performAction('enter_text_into_numbered_field', 0, field)
		performAction('enter_text_into_numbered_field', 0, field)
	else
		performAction('enter_text_into_numbered_field', amount, field)
		performAction('enter_text_into_numbered_field', 0, field)
		performAction('enter_text_into_numbered_field', 0, field)
	end
end

# For an sso user, switches to either the card or bank part of the app
Given /^I switch to (card|bank)$/ do |which|
	x = query("imageview marked:'#{"cardBankIcon"}'")[0]["rect"]["center_x"]
	y = query("imageview marked:'#{"cardBankIcon"}'")[0]["rect"]["center_y"]
	performAction("touch_coordinate", x, y)

	# Tap on the appropriate icon to switch to the other service
	if which == 'card'
		performAction('click_on_view_by_id', 'card_gray_icon')
	else
		performAction('click_on_view_by_id', 'bank_gray_icon')
	end
end

# Clicks the "Log Out" button and asserts return to the Login screen
Given /^I (?:am logged|log) out$/ do
	performAction('click_on_view_by_id', "logout_button")
	performAction('wait_for_dialog_to_close') # Wait for Loading dialog to close
	performAction('wait_for_screen', "LoginActivity", DEFAULT_SERVICE_TIMEOUT)
end

# Not currently working
# Need to create java file containing the appropriate robotium command
Then /^I set the screen to landscape$/ do
	performAction('set_landscape')
end
