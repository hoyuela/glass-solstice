# Android Bank Smoke Test based off of script TC4205
# MUST BE RUN WITH FLAG NO_STOP=1
@bank_smoke_test
Feature: Android Bank Smoke Test
	
	@bank_login_smoke
	Scenario: Login
		Given The splash screen is finished
		And I am on the bank tab
		# Logs in, checks for strong auth verification, and then verifies correct salutation based on the users name in the config file
		Then I log in as "asys" "bank" user "asystest502"

	@transaction_smoke
	Scenario: Transactions and External Browser Modal
		Given I verify transactions
		Then I verify external browser modals

	@payees_smoke
	Scenario: Payees
		Then I verify managing payees
		Then I verify adding a payee

	@bank_payment_smoke
	Scenario: Payments
		Then I make a payment
		And I verify I can delete a payment

	@review_and_transfer_smoke
	Scenario: Review Payments and Transfer Calendar
		Given I verify review payments
		And I verify the calendar in transfer money

	@bank_faq
	Scenario: Bank FAQ
		Given I verify the bank FAQ

	@atm_locator
	Scenario: ATM Locator
		Given I verify ATM locator from the menu
		Then I log out
		And I wait for 5 seconds
		Then I am on the bank tab
		And I verify ATM locator from the login screen

	# Landscape is currently not supported by calabash android
	# It is possible to create a performAction() command for rotate by creating a java file with robotium commands and building a new calabash gem
	#
 	# Log out before this scenario
	#@bank_landscape_smoke
	#Scenario: Landscape Smoke Steps
		# Runs through some of the tests from above that need to be tested in landscape
		#Given The splash screen is finished
		#Then I rotate device right

		#Then I log in as "asys" "bank" user "asystest505"

		#And I verify transactions in landscape
		#Then I verify external browser modals

		#Then I verify managing payees in landscape
		#And I verify adding a payee in landscape

		#Then I verify review payments in landscape
		#Then I make a payment in landscape
		#And I verify I can delete a payment in landscape
		#Then I verify the calendar in transfer money in landscape

	@bank_sso
	@restart
	Scenario: SSO
		Given The splash screen is finished
		And I am on the bank tab
		Then I log in as "asys" "sso" user "asysssocred3"
		Then I switch to card
		# The gray card icon will be visible once we are in the card part of the app
		# Can easily check salutation, but names for bank and card are different for most users
		And I wait to see "card_icon"
