Feature: Android Bank Smoke Test

	# Tests login and navigation to FAQ and that the "+"/"-" show/reveal FAQ text
	@greeting_and_faq
	Scenario: Greeting & FAQ
		Given I am logged in as default bank user
		And I see the bank greeting

		Then I navigate to "Frequently Asked Questions" under "Customer Service"
		# Need 1 second for the fragment animation
		And I wait
		Then I touch the "General" text

		# Open a question
		Then I touch the "Is Discover.com Mobile Secure?" text
		And I see "Discover.com Mobile uses the same security standards as discover.com."

		# Hide a question
		Then I touch the "Is Discover.com Mobile Secure?" text
		And I should not see "Discover.com Mobile uses the same security standards as discover.com."

	# Tests that when viewing a transaction detail, swiping left results in a differing transaction.
	@transaction_swipe
	Scenario: Transaction Swipe
		Given I am logged in as default bank user
		Then I select an account
		And I wait for dialog to close

		# Wait for a transaction
		And I select a transaction
		And I wait

		# Get the first amount and swipe to the next one
		Then I save text from view with id "amount_cell"
		Then I swipe right
		And I wait

		# Verify a differing amount
		Then I should not see the saved text

	#Tests that browser prompt modal appears when leaving app
	@external_browser_modal
	Scenario: External Browser Modal
		Given I am logged in as default bank user

		# Verify Menu items that link to browser
		Then I press the menu button
		And I touch the "View Statements" text
		# Wait for External browser modal
		Then I wait to see "You're Leaving this Application"
		And I go back

		# Should still be in the menu
		Then I touch the "Open New Account" text
		Then I wait to see "You're Leaving this Application"
		And I go back

		#Should still be in the menu & under "Transfer Money"
		Then I touch the "Transfer Money" text
		And I touch the "Review Transfers" text
		And I wait to see "You're Leaving this Application"
		And I go back

		# Should still be in the menu but under "Account"
		Then I touch the "Manage External Accounts" text
		And I wait to see "You're Leaving this Application"
		And I go back

	@review_payments
	Scenario: Review Payments
		Given I am logged in as default bank user
		And I navigate to "Review Payments" under "Pay Bills"
		And I wait for dialog to close

		# Test that the detail screens load for each payment type
		# NOTE that the following calls require prior navigation to "Review Payments"
		# First selects the specified payment toggle,
		# followed by the top row item checking to see if it navigates to details
		Then I review Scheduled Payments
		And I review Completed Payments
		And I review Cancelled Payments

	@swiping_through_payees
	Scenario: Swiping Through Payees
		Given I am logged in as default bank user
		And I navigate to "Manage Payees" under "Pay Bills"
		And I wait for dialog to close

		# Select the first payee & wait for details
		Then I select a payee
		And I wait to see "Payee Name"

		# Store the account number
		Then I save text with label "Account #"
		And I swipe right
		Then I should not see the saved text

	@scheduling_a_payment
	Scenario: Scheduling A Payment
		Given I am logged in as default bank user
		Then I navigate to Pay Bills
		And I select a payee
		Then I can schedule a payment

	# !! THIS ONLY WORKS IF BUILD PERMISSION INCLUDES:
	# 		ACCESS_MOCK_LOCATION
	# !! AND DEVICE HAS ENABLED MOCK LOCATIONS (Developer Settings)
	@atm_locator_logged_out
	Scenario: ATM Locator - Logged Out
		Given The splash screen is finished
		And I am on the bank tab

		# Set location programatically (to Chicago)
		# Setting prior to starting ATM Locator to ensure consistant dialogs
		#Given I am at 41.8781136, -87.62979819

		# Press "Atm Locator" button
		Then I press view with id "register_now_or_atm_button"

		# Use Current Location Dialog
		
		Then I wait for the "Allow" button to appear
		And I press the "Allow" button

		# Wait for map to populate
		
		Then I wait to see "Loading"
		And I wait for dialog to close
		And I should not see "Your Request Could Not Be Completed"
		
		# Ensure locations populated in the list
		# Navigates to the list and asserts the "get directions" button
		# Note: Calabash is not recognizing that there is a List nor a Map as their proper type 
		# (due to the Fragment + FrameLayout) but it is correctly finding all the buttons
		# So, the current workaround is to check for at least one directions button
		
		Then I press view with id "list_nav"
		And I see view with id "directions"

		Then I wait
