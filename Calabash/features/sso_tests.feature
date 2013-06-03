Feature: Android SSO Specific tests

	# Tests logging in and then switching between card and bank
	@sso
	@greeting_and_faq
	@strongauth
	@accounts
	Scenario: Switch Accounts
	Given I am logged in as a default sso user on the bank tab
	And I see the bank greeting for the default sso user
	Then I switch to card
	Then I wait for 5 seconds
	Then I switch to bank
