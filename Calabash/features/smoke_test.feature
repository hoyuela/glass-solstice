Feature: Android Bank Smoke Test

	# Tests login and navigation to FAQ and that the "+"/"-" show/reveal FAQ text
	@greeting_and_faq
	@accounts
	@bank_user
	Scenario: Greeting & FAQ
		Given I am logged in as default bank user
		And I see the bank greeting for the default bank user
		Then I navigate to "Frequently Asked Questions" under "Customer Service"
		# Need 1 second for the fragment animation
		And I wait
		Then I touch the "General" text

		# Open a question
		Then I touch the "Is the Discover Mobile App as secure as Discover.com" text
		And I see "uses the same security standards as Discover.com"

		# Hide a question
		Then I touch the "Is the Discover Mobile App as secure as Discover.com" text
		And I should not see "uses the same security standards as Discover.com"

	# Tests that when viewing a transaction detail, swiping left results in a differing transaction
	@transaction_swipe
	@accounts
	@account_activity
	@bank_user
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
	@accounts
	@bank_user
	Scenario: External Browser Modal
		Given I am logged in as a default bank user

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

		# Still be in the menu & under "Transfer Money"
		Then I touch the "Transfer Money" text
		And I touch the "Review Transfers" text
		And I wait to see "You're Leaving this Application"
		And I go back

		# Still in the menu but under "Account"
		Then I touch the "Manage External Accounts" text
		And I wait to see "You're Leaving this Application"
		And I go back

		Then I touch the "Customer Service" text

		#Still in menu under customer service
		And I touch the "Secure Message Center" text
		And I wait to see "You're Leaving this Application"
		And I go back

		#still in menu under customer service
		And I touch the "Profile" text
		And I wait to see "You're Leaving this Application"
		And I go back

	#user must have previous payments
	@review_payments
	@accounts
	@review_payments
	@bank_user
	Scenario: Review Payments
		Given I am logged in as a default bank user
		And I navigate to "Review Payments" under "Pay Bills"
		And I wait for dialog to close

		# Test that the detail screens load for each payment type
		# NOTE that the following calls require prior navigation to "Review Payments"
		# First selects the specified payment toggle,
		# followed by the top row item checking to see if it navigates to details
		Then I review Scheduled Payments
		And I review Completed Payments
		And I review Cancelled Payments

	#user must have payees
	@swiping_through_payees
	@accounts
	@manage_payees
	@bank_user
	Scenario: Swiping Through Payees
		Given I am logged in as a default bank user
		And I navigate to "Manage Payees" under "Pay Bills"
		And I wait for dialog to close

		# Select the first payee & wait for details
		Then I select a payee
		And I wait to see "Payee Name"

		# Store the account number
		Then I save text with label "Account #"
		And I swipe right
		Then I should not see the saved text

	#user must have payees
	@scheduling_a_payment
	@pay_bills
	@bank_user
	Scenario: Scheduling A Payment
		Given I am logged in as a default bank user
		Then I navigate to "Pay Bills" under "Pay Bills"
		And I select a payee
		Then I can schedule a payment

	#user must have transfer access
	@transfer_money
	@bank_user
	Scenario: Transfer Money
		Given I am logged in as a default bank user
		Then I navigate to "Transfer Money" under "Transfer Money"
		And I wait for "Select Account" to appear

		# Choose the From and To accounts for the transfer
		Then I select a to and from account

		# Complete the transfer with a randomized amount and verify the transfer is recorded
		Then I schedule a transfer

	@atm_locator_logged_out
	@atm_locator
	@bank_user
	Scenario: ATM Locator - Logged Out
		Given The splash screen is finished
		And I am on the bank tab

		# Press "Atm Locator" button
		Then I press view with id "register_now_or_atm_button"

		# Use Current Location Dialog
		Then I wait for the "Allow" button to appear
		And I press the "Allow" button

		# Wait for map to populate
		Then I wait to see "Loading"
		And I wait for dialog to close
		And I should not see "Your Request Could Not Be Completed"
		
		Then I press view with id "list_nav"
		And I see view with id "directions"

	@atm_locator_logged_in
	@accounts
	@atm_locator
	@bank_user
	Scenario: ATM Locator - Logged In
		Given I am logged in as a default bank user
		Then I navigate to "Find Nearby" under "ATM Locator"

		Then I wait for the "Allow" button to appear
		And I press the "Allow" button

		# Wait for map to populate
		Then I wait to see "Loading"
		And I wait for dialog to close
		And I should not see "Your Request Could Not Be Completed"

		Then I press view with id "list_nav"
		And I see view with id "directions"