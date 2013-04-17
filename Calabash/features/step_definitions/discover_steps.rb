# STEPS SPECIFIC TO DISCOVER, BUT NOT TO A PARTICULAR PAGE/FEATURE

# Toggles the main sliding menu
Given /^I (?:press|touch) the menu button$/ do
	performAction('click_on_view_by_id', 'navigation_button')
	macro 'I wait for a second' # Time for menu animation
end

# Presses the orange help button
Given /^I (?:press|touch) the help button$/ do
	performAction('click_on_view_by_id', 'help')
end

# Opens the menu, selects the specified category, and selects the subcategory
# MENU SHOULD BE CLOSED AND CURRENT SCREEN SHOULD BE OF A DIFFERING CATEGORY
Given /^I (?:navigate|go) to "([^\"]*)" under "([^\"]*)"$/ do |subtitle, title|
	macro 'I press the menu button'
	macro %Q[I touch the "#{title}" text]
	performAction('wait_for_text', "#{subtitle}")
	performAction('click_on_text', "#{subtitle}")
end

# Enters a random dollar amount in a Discover (custom) currency input field
Given /^I (?:put|enter) a random amount (?:in|into) field (?:number )?(\d+)$/ do |field|
	amount = rand(9) + 1 # random number (1-9)

	# Enter the random payment amount
	# TODO: Find better workaround if possible...
	# ISSUE: The first number you enter will show up in the 100th column (9 -> $0.09)
	# Even a higher number will only enter one digit like above
	# Need to add the number three times, which will move it over 3 decimals to reach $1 minimum
	# Has something to do with the non-standard EditText being restricted to numbers
	performAction('enter_text_into_numbered_field', amount, field)
	performAction('enter_text_into_numbered_field', amount, field)
	performAction('enter_text_into_numbered_field', amount, field)
end
