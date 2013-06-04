# STEPS SPECIFIC TO DISCOVER, BUT NOT TO A PARTICULAR PAGE/FEATURE

# Toggles the main sliding menu
Given /^I (?:press|touch) the menu button$/ do
  	performAction('wait_for_view_by_id', 'navigation_button')
	performAction('click_on_view_by_id', 'navigation_button')
	macro 'I wait for a second' # Time for menu animation
end

# Presses the orange help button
Given /^I (?:press|touch) the help button$/ do
	performAction('click_on_view_by_id', 'help')
end

# Opens the menu, selects the specified category, and selects the subcategory
# MENU SHOULD BE CLOSED
Given /^I (?:navigate|go) to "([^\"]*)" under "([^\"]*)"$/ do |subtitle, title|
	
	macro 'I press the menu button'

	if subtitle == title
		menus = query("textview text:'#{subtitle}'", 'id')
		
		# Finds the coordinates of the second occurance of "Pay Bills" and touches those coordinates
		x = query("textview marked:'#{subtitle}'")[1]["rect"]["center_x"]
		y = query("textview marked:'#{subtitle}'")[1]["rect"]["center_y"]
		performAction("touch_coordinate", x, y)
	else
		performAction('click_on_text', "#{subtitle}")
	end
end

# Enters a random dollar amount in a Discover (custom) currency input field
Given /^I (?:put|enter) a random (?:number|amount) between (\d+) and (\d+) (?:in|into) field (?:number )?(\d+)$/ do |num1, num2, field|
	num1 = num1.to_i
	num2 = num2.to_i
	amount = rand(num2) + num1 # random number (num1-num2)
	puts amount
	# Enter the random payment amount
	# TODO: Find better workaround if possible...
	# ISSUE: The first number you enter will show up in the 100th column (9 -> $0.09)
	# Even a higher number will only enter one digit like above
	# Need to add the number three times, which will move it over 3 decimals to reach $1 minimum
	# Has something to do with the non-standard EditText being restricted to numbers
	if amount >= 10
		amountFirstDigit = amount / 10
		amountSecondDigit = amount % 10
		performAction('enter_text_into_numbered_field', amountFirstDigit, field)
		performAction('enter_text_into_numbered_field', amountSecondDigit, field)
		performAction('enter_text_into_numbered_field', 0, field)
		performAction('enter_text_into_numbered_field', 0, field)
	else
		performAction('enter_text_into_numbered_field', amount, field)
		performAction('enter_text_into_numbered_field', 0, field)
	end


end