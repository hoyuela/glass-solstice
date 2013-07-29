# Steps specific to the Android bank side of the Discover App

# Steps that are specific to the bank part of the Discover app

# Tap the first account in the account screen, verify the list is displayed, and verify swipe 
Given /^I verify transactions$/ do
	#Tap the first account in the list
	# Calabash does not recognize the list view so must tap on the "acct_carat" icon
	performAction('click_on_view_by_id', "acct_carat")
	performAction('wait', 3)

	performAction('wait_for_text', "Transactions")

	# Tap the first transaction
	# Calabash does not recognize the list view so must tap on the first occurence of "/" which selects the first transactions
	performAction('click_on_text', "/")

	# Get the first balance
	firstBalance = query("textView marked:'balance_cell'", "text")[0]

	# Test swipe
	performAction('wait', 2)
  	performAction('swipe', 'right')
  	performAction('wait', 4)

  	# Now compare the balances from the two transactions
	secondBalance = query("textView marked:'balance_cell'", "text")[0]

	if (firstBalance == secondBalance)
		screenshot_and_raise "After swipe balances are the same"
	end
end

# Verify that external browser modal is being displayed
Given /^I verify external browser modals$/ do
	# Verify Menu items that link to browser
	macro 'I press the menu button'
	performAction('click_on_text', "Open New Account")
  	performAction('assert_text', "You're Leaving this Application", true) 
  	performAction('go_back')

	performAction('click_on_text', "Transfer Money")
	performAction('click_on_text', "Manage External Accounts")
  	performAction('assert_text', "You're Leaving this Application", true) 
  	performAction('go_back')

	performAction('click_on_text', "Customer Service")
	performAction('click_on_text', "Secure Message Center")
  	performAction('go_back')

	performAction('click_on_text', "Profile")
  	performAction('go_back')

	macro 'I press the menu button'
end

# Verify payee swipping in manage payees
Given /^I verify managing payees$/ do
	macro %Q[I navigate to "Manage Payees" under "Pay Bills"]

	# Use "carrot" images as a reference for row since Calabash can't detect this list view
	# Tap first payee
	performAction('wait_for_view_by_id', "carrot")

	touch("imageView marked:'carrot'")[0]
  	performAction('wait', 2)

	# Get the first payee name and then verify that it is not equal to the next payee
	labelQuery = query("textView", "text")
	payeeIndex = labelQuery.index("Payee Name")+1
	firstPayeeName = labelQuery[payeeIndex]

	# Test swipe
  	performAction('swipe', 'right')
  	performAction('wait', 3)
  	performAction('swipe', 'right')
  	performAction('wait', 3)

	# Now compare the two payee names
	labelQuery = query("textView", "text")
	payeeIndex = labelQuery.index("Payee Name")+1
	secondPayeeName = labelQuery[payeeIndex]

	if (firstPayeeName == secondPayeeName)
		screenshot_and_raise "After swipe payees are the same"
	end
	performAction('go_back')
	performAction('wait', 2)
end

# Test the features in adding a payee but don't actually add a payee
Given /^I verify adding a payee$/ do
	buttons = query("button marked:'Add New Payee'")
	if (buttons.size != 0)
		touch("button marked:'Add New Payee'")
	else
		# Scroll until button is found (max of 10 scrolls)
		counter = 0
		while buttons.size == 0 && counter <= 10 do
			counter = counter + 1
			performAction('scroll_down')
			buttons = query("button marked:'Add New Payee'")
		end
		touch("button marked:'Add New Payee'")
	end

	# Try invalid characters
	performAction('enter_text_into_id_field', "!!@@##$$%%", "search_field")
	touch("button marked:'Continue'")

  	performAction('assert_text', "Invalid characters", true) 
	performAction('clear_id_field',"search_field")

	# Type in Valid Name
	performAction('enter_text_into_id_field', "Long character length payee name", "search_field")
	touch("button marked:'Continue'")

	performAction('wait_for_dialog_to_close')
	performAction('wait',4)
	touch("textView marked:'Enter Payee Details'")

	element_exists("view {text LIKE '*A 32 character length payee name*'}")

	# Verify landscape
	# ROTATION DOES NOT WORK IN CALABASH ANDROID
	#rotate_phone(90)

	element_exists("view {text LIKE '*A 32 character length payee*'}")
	performAction('wait',2)

	performAction('scroll_down')
  	performAction('scroll_down')

	touch("button marked:'Cancel'")
	# macro %Q[I rotate device right]
	touch("button marked:'Cancel This Action'")
end

Given /^I add a payee$/ do
	touch("button marked:'Add New Payee'")

	# Type in Valid Name
	touch("textField")[0]
	keyboard_enter_text ("Calabash Test Payee")

	sleep(STEP_PAUSE)
	touch("button marked:'Continue'")
	performAction('wait_for_dialog_to_close')
	performAction('wait',4)

	touch("label marked:'Enter Payee Details'")

	# Enter details for new payee
	# Use the blue_arrow icon to select the nickname field
	touch("imageView marked:'blue_arrow'")
	touch("button marked:'Next'")
	keyboard_enter_text ("0000000000")

	touch("button marked:'Next'")

	keyboard_enter_text ("641 West Lake St")

	touch("button marked:'Next'")

	touch("button marked:'Next'")

	keyboard_enter_text ("Chicago")

	# TODO find way to use spinner wheel through calabash
	# (right now just leave as first state 'AL Alabama')
	touch("button marked:'Next'")

	touch("button marked:'Next'")

	keyboard_enter_text ("60661")

	touch("button marked:'Next'")

	touch("button marked:'Done'")

	touch("button marked:'Add Payee'")
end

# Make sure payments are displayed for each category 
Given /^I verify review payments$/ do
	macro %Q[I navigate to "Review Payments" under "Pay Bills"]
	# Select the first scheduled payment
	# Calabash does not recognize the list view so must tap on the first occurence of "/" which selects the first transactions
	performAction('wait_for_text', "/")
	performAction('click_on_text', "/")
  	performAction('assert_text', "Scheduled Payment", true) 
  	performAction('go_back')

	# Verify Completed Payments
	touch("textView marked:'Completed\nPayments'")
	# Tap first payment
	performAction('wait_for_text', "/")
	performAction('click_on_text', "/")
  	performAction('assert_text', "Completed Payment", true) 
	performAction('go_back')

	# Verify Cancelled Payments
	touch("textView marked:'Cancelled\nPayments'")
	# Tap first payment
	performAction('wait_for_text', "/")
	performAction('click_on_text', "/")
  	performAction('assert_text', "Cancelled Payment", true) 
	performAction('go_back')
	performAction('wait',2)
end

Given /^I make a payment$/ do
	macro %Q[I navigate to "Pay Bills" under "Pay Bills"]

	# Select first payee
	performAction('wait_for_view_by_id', "carrot")
	touch("imageView marked:'carrot'")[0]
	
	@amount = "3"
	performAction('enter_text_into_id_field', "#{@amount}", query("com.discover.mobile.bank.ui.widgets.AmountValidatedEditField","id")[0])
	performAction('enter_text_into_id_field', "0", query("com.discover.mobile.bank.ui.widgets.AmountValidatedEditField","id")[0])
	performAction('enter_text_into_id_field', "0", query("com.discover.mobile.bank.ui.widgets.AmountValidatedEditField","id")[0])
	
	performAction('wait',2)

	# Try to tap earlier day than the currently selected day 3..4 gives us only the day (e.g. 08/05/2013 -> 5)
	selectedDay = query("textView marked:'date_text'","text")[0][3..4].to_i

	# Touch calendar icon
	touch("android.widget.RelativeLayout marked:'date_item'")
	performAction('wait',2)

	if(selectedDay != 1)
		previousDay = selectedDay - 1
		touch("com.caldroid.SquareTextView marked:'#{previousDay}'")[0]

		# Verify we are still on the calendar screen
		performAction('assert_text', "Deliver by Date", true) 
	end
	
	performAction('go_back')
	performAction('scroll_down')

	touch("button marked:'Schedule Payment'")

	performAction('wait_for_dialog_to_close')

	# Verify the confirmation number exists
	performAction('wait',2)
	performAction('scroll_down')
	performAction('scroll_down')
	performAction('scroll_down')
	
	performAction('assert_text', "Confirmation Number", true) 
end

# Delete the payment we just made by looking up the amount
# NOTE this will work better when we use a calabash specific user
Given /^I verify I can delete a payment$/ do
	macro %Q[I navigate to "Review Payments" under "Pay Bills"]

	searchString = "$" + "#{@amount}" + ".00"
	amounts = query("textView marked:'#{searchString}'")
	if (amounts.size != 0)
		touch(query("textView marked:'#{searchString}'")[0])
	else
		# Scroll until amount is found (max of 10 scrolls)
		counter = 0
		while amounts.size == 0 && counter <= 10 do
			counter = counter + 1
			performAction('scroll_down')
			amounts = query("textView marked:'#{searchString}'")
		end
		touch(query("textView marked:'#{searchString}'")[0])
	end

	performAction("scroll_down")
	performAction('wait',2)
	performAction("scroll_down")
	performAction('wait',2)

	if element_exists("button marked:'delete_payment_button'")
		performAction('click_on_view_by_id',"delete_payment_button")
		touch("button marked:'Yes, Delete'")
		performAction('wait_for_dialog_to_close')
  		
  		performAction('wait_for_text', "Scheduled Payment Deleted")
  		performAction('assert_text', "Scheduled Payment Deleted", true) 
  	else
		screenshot_and_raise "Payment Cannot Be Deleted"
	end
end

# Start a weekly transfer and verify in the calendar that 
# TODO Find good way of testing the calendar
Given /^I verify the calendar in transfer money$/ do
	macro %Q[I navigate to "Transfer Money" under "Transfer Money"]
	performAction('wait_for_text', "Transfer Money")

	touch("textView marked:'Frequency'")
	touch("textView marked:'Weekly'")
	
	performAction('wait',2)
 	performAction('scroll_down')
	performAction('wait',2)

	touch("textView marked:'Select A Date'")
	
	performAction('go_back')
	performAction('scroll_down')
	performAction('wait',1)

	touch("button marked:'Cancel'")
	touch("button marked:'Cancel This Action'")
end

Given /^I verify the bank FAQ$/ do
	macro %Q[I navigate to "Frequently Asked Questions" under "Customer Service"]

	# General
	touch("textView marked:'text1'")[0]
  	performAction('assert_text', "General", true) 
	performAction('go_back')

	# Online Bill Pay FAQ
	x = query("textView marked:'text1'")[1]["rect"]["center_x"]
	y = query("textview marked:'text1'")[1]["rect"]["center_y"]
	performAction("touch_coordinate", x, y)
	performAction('go_back')

	# Deposit a Check
	x = query("textView marked:'text1'")[2]["rect"]["center_x"]
	y = query("textview marked:'text1'")[2]["rect"]["center_y"]
	performAction("touch_coordinate", x, y)
	performAction('go_back')

	# ATM Locator
	x = query("textView marked:'text1'")[3]["rect"]["center_x"]
	y = query("textview marked:'text1'")[3]["rect"]["center_y"]
	performAction("touch_coordinate", x, y)
	performAction('go_back')
end