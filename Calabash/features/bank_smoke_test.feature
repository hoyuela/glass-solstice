# Android Bank Smoke Test
# Meant to restart after every scenario

@bank_smoke_test
Feature: Android Bank Smoke Test
	
	@bank_login_smoke
	Scenario: 
		Given The splash screen is finished
		
		Then I log in as a "normal_checking" "asys" "bank" user

	@transaction_smoke
	Scenario: Transactions and External Browser Modal
		Given I log in as a "normal_checking" "asys" "bank" user

		Then I verify transactions
		And I verify external browser modals

	@payees_smoke
	Scenario: Payees
		Given I log in as a "normal_checking" "asys" "bank" user

		Then I verify managing payees
		Then I verify adding a payee

	@bank_payment_smoke
	Scenario: Payments
		Given I log in as a "normal_checking" "asys" "bank" user
		
		Then I verify review payments
		Then I make a payment
		And I verify I can delete a payment
		And I verify the calendar in transfer money

	@bank_faq_smoke
	Scenario: Bank FAQ
		Given I log in as a "normal_checking" "asys" "bank" user
		
		Then I verify the bank FAQ

	@atm_locator_smoke
	Scenario: ATM Locator
		Given I log in as a "normal_checking" "asys" "bank" user
		
		Then I verify ATM locator from the menu
		Then I log out
		And I wait for 5 seconds
		Then I am on the bank tab
		And I verify ATM locator from the login screen

	# Calabash Android Does Not Currently Support Rotation
	#
	#@bank_landscape_smoke
	#Scenario: Landscape Smoke Steps
	#	# Runs through some of the tests from above that need to be tested in landscape
	#	Given I rotate device right
	#
	#	Then I log in as a "normal_checking" "asys" "bank" user
	#
	#	And I verify transactions in landscape
	#	Then I verify external browser modals
	#
	#	Then I verify managing payees in landscape
	#	And I verify adding a payee in landscape
	#
	#	Then I verify review payments in landscape
	#	Then I make a payment in landscape
	#	And I verify I can delete a payment in landscape
	#	Then I verify the calendar in transfer money in landscape
	#	Then I log out

	@bank_sso
	Scenario: SSO		
		Given The splash screen is finished
]
		Then I log in as a "normal" "asys" "sso" user
		
		Then I switch to card
		And I wait to see "card_icon"
		Then I log out