Feature: Android SSO Specific tests

	# Tests logging in and then switching between card and bank
	@greeting_and_faq
	@accounts
	@sso_user
	Scenario: Switch Accounts
		Given I am logged in as a default sso user on the bank tab
		And I see the bank greeting for the default sso user
		Then I switch to card
		Then I see the card greeting for the default sso user
		Then I switch to bank
		And I see the bank greeting for the default sso user
		Then I log out



