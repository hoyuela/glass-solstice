# STEPS SPECIFIC TO THE "PAY BILLS" PAGES

# Selects the first payment from a list of payments
Given /^I select a payment$/ do
	# TEMPORARY WORKAROUND
	# Calabash console sometimes finds the listviews, eg. performAction("get_list_item_properties", 1)
	# but the same queries within the steps files fail with "list not found" (for any index)
	# Workaround: select the first date in the list
	performAction('click_on_text', "/")
	#macro 'I press list item number 1'

end

# Selects the specified Payments toggle, asserts data is loaded 
# and the details screen appears on selection of a payment
Given /^I review (Scheduled|Completed|Cancelled) Payments$/ do |which|
	# Must aready be on "Review Payments" screen
	performAction('click_on_text', "#{which}") # Select the proper toggle
	performAction('wait_for_dialog_to_close')
	macro 'I select a payment'

	# Asserts a basic payment detail screen populates with the proper title and some payee
	performAction('wait_for_text', "Payment Details")
	performAction('wait_for_text', "Payee")

	performAction('go_back')
end

# Selects the first Payee in a list (Pay Bills, Manage Payees screen)
# Must already be on a screen with a list of payees.
# This is a special case since Calabash is not recognizing it as a ListView
# Helps to prevent hardcoding Payee names.
Given /^I select a payee$/ do
	performAction('click_on_view_by_id', "carrot")
end

# Must already have a payment form open
# Uses a random amount between $1.00 - $9.00
Given /^I(?: can)? schedule a payment$/ do
	macro 'I enter a random amount between 1 and 9 into field 1'

	# Schedule the payment & wait for the confirmation screen
	
	#press schedule payment button
	performAction('click_on_view_by_id', "pay_now")

	performAction('wait_for_dialog_to_close')
	performAction('wait', 2)
	performAction("drag", 50, 50, 50, 0, 1) # Scroll down
	performAction('wait_for_text', "Confirmation Number")

	# Grab confirmation number
	text = query('textview', 'text') # Creates an array of all the text (in TextViews) on the screen (in order)
	index = text.index('Confirmation Number') + 1
	confirmation = text[index]
	puts "Scheduled Payment: " + confirmation

	# Navigate to Review Payments
	macro 'I press the menu button'
	performAction('click_on_text', "Review Payments")
	performAction('wait_for_dialog_to_close') # Wait for Loading dialog to close

	# Verify the payment is scheduled
	macro %Q[I see a payment with confirmation number "#{confirmation}"]
end

# Verify the existence of a specific payment based on a unique cofirmation number
# Must already be on review payments and whichever tab this applied to (eg. scheduled, completed)
Given /^I see(?: a| the)? payment with confirmation(?: number)? "([^\"]*)"$/ do |num|
	macro 'I select a payment' # Go the the first payment details
	performAction('wait_for_text', "Payment Details")
	performAction("drag", 50, 50, 50, 0, 1) # Scroll down (uses screen percents)
	performAction('wait_for_text', "Confirmation Number")
	continue = true
	found = false

	# Swipe through the payment details
	# until we find the confirmation number or reach the end
	begin
		text = query('textview', 'text') # Creates an array of all the text (in TextViews) on the screen (in order)
		found = text.index("#{num}") != nil

		break if found # We found the confirmation number -> leave the while loop

		# Determine if there are more payments to review
		isNext = element_exists("imageview marked:'next_button'") # True if there is a "next" button
		isNextEnabed = query("imageview marked:'next_button'", :enabled).first # True if the "next" button is enabled
		continue = isNext && isNextEnabed

		if continue then # Go to next payment
			performAction('click_on_view_by_id', 'next_button')
			performAction('wait_for_text', "Payment Details")
			performAction("drag", 50, 50, 50, 0, 1) # Scroll down (uses screen percents)
			performAction('wait_for_text', "Confirmation Number")
		end
	end while continue

	if !found
		raise 'Could not find Payment: ' + num
	end
end
