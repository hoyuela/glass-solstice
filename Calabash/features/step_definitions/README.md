Discover Steps
==============
Calabash Android comes with a set of predefined steps which can be found [here](https://github.com/calabash/calabash-android/blob/master/ruby-gem/lib/calabash-android/canned_steps.md).  
Below are the steps defined specifically for the Discover project.

Syntax
------
Here are some notes for reading step definitions:

    Given /^I do something$/
Specifies the expected text to be seen in a scenario between the /^ ... $/.  Note that "Given" could instead be "And" or "Then" within the scenario itself.  It is the text between /^ ... $/ that matters.

    "([^\"]*)"
Specifies any String (quotes required).

    (\d+)
Specifies any digit (non-decimal number)(no quotes).

    (one|two)
Specifies the expected text at this point _must_ be "one" or "two" (quotes should be omitted). The chosen text is then passed as an argument.

    (?:the|a)
Same as above, however, the ?: simply denotes that it does not matter if "the" or "a" is chosen since the chosen text will not be passed as an argument. However, one of them **must** be specified.  

    (?:the|a)?
Same as above, but the entire group (within the parentheses) is optional meaning you can use "the", "a", or nothing "" and the outcome of the step will not be affected.

    Then /^the users? should receive an email$/
A step definition where the "s" in "users" is optional (can use "user" or "users")

Common Steps
-------------
The following steps are not specific to Discover, but did not come with the default Calabash steps.

    Then /^I(?: should)? see view with id "([^\"]*)"$/ do |id|
Will pass if the view corresponding to the given id exists on the screen.

    Then /^I scroll down$/ 
Scrolls the page down.

    Then /^I scroll up$/ 
Scrolls the page up.

    Then /^I save text from view with id "([^\"]*)"$/ do |id|
Saves the text contained in a text view with the given id (saves for the currently running scenario only).  Meant to be used with the below steps.

    Then /^I see the saved text$/
Passes when the previously saved text is visible on the screen. (Note that you will need to scroll down prior to this call if the text is at the bottom of the screen and not visible).  
✓ You must have previously saved text

    Then /^I (?:don't|should not) see the saved text$/
Same as above but passes when the previously saved text is not visible.  
✓ You must have previously saved text

General Discover Steps
----------------------
The following are steps specific to Discover, but not necesarrily to any feature.  

    Then /^I (?:press|touch) the menu button$/
Presses the orange button in the top/left corner of most Discover screens.  May open or close the sliding menu depending on its current state.

    Then /^I (?:press|touch) the help button$/  
Presses the round, orange "help" button often found in the top/right corner of Discover screens.  Should display a list of help options.  You may select the proper help option by utilizing default Calabash steps (eg. "I touch the 'All FAQs' text").

    Given /^I (?:navigate|go) to "([^\"]*)" under "([^\"]*)"$/ do |subtitle, title|
Attempts to press the specified menu items.  Opens the menu presses the title, then the subtitle.  
✓ Menu should be closed  
✓ You should not already be on the category level (eg. "Manage Payees" & "Review Payments" are both under category "Pay Bills" so you cannot go from one to the other with this call [Working on fixing this]. HOWEVER, if you know you are already in the category you need, open the menu & navigate with the default Calabash steps eg. "I touch the 'Review Payments' text")

    Given /^I (?:put|enter) a random (?:number|amount) between (\d+) and (\d+) (?:in|into) field (?:number )?(\d+)$/ do |num1, num2, field|

Enters a random dollar amount into a numerical field at the specified field index (eg. if it is the only input field on the screen, it is at field index = 1).

Login steps
-----------

To assert that specified text can be found use any of the following steps.

    Given /^The splash screen is finished$/
Waits for the splash screen to dismiss.

    Given /^I am on the (card|bank) tab$/ do |which|
Selects the card or bank tab.  
✓ You must already be on the Login screen.

    Then /^I enter credentials "([^\"]*)", "([^\"]*)"$/ do |user, password|    
 Enters a user ID and password into the two Login fields.  
✓ You must already be on the Login screen.

    Given /^I am logged in as(?: a| the)? default (bank|card) user$/ do |which|
Performs a login for the bank or card user specified in config.yml. Fills-out the Enhanced Security screen as well if strongauth = true in the config.yml file.  
✓ You must already be on the Login screen.  
 

    Given /^I am logged in as(?: a| the)? default sso user on the (card|bank) tab$/ do |which|
Performs a login on the given tab for the sso user specified in config.yml. Fills-out the Enhanced Security screen as well if strongauth is specified in the config.yml file.
✓ You must already be on the Login screen.  

    Given /^I am on a public device$/
Completes the Enhanced Security Screen but does not save the device (specifies public device).  Retrieves the security answer from config.yml.  
✓ You must already be in the process of logging in (Login loading dialog) or already on the Enhanced Security screen.
    
    Then /^I press remember me$/
Toggles the "Remember Me" button on the Login screen.  
✓ You must already be on the Login screen.  

    Given /^I see the (bank|card) greeting$/ do |which|
Asserts the presence of the greeting "greeting(based on time of day), [first name]". Uses the bank or card default credentials and the hours for greetings from config.yml.  
✓ You must already be on the Account screen.

    Then /^I (?:am logged|log) out$/
Clicks the "Log Out" button and asserts return to the Login screen.  Note because of the syntax, you may type "I am logged out" or "I log out".

Account Steps
-------------
The following are steps specific to the "Account" pages.

    Then /^I select an account$
Selects the first account in a list of accouts.  You may instead select a specific account by using default Calabash steps to click on the specific account name text.  
✓ Must already be on a screen with a list of accounts.

    Then /^I select a transaction$/
Selects the first account transaction from a list of transactions.  
✓ Must already be on a screen with a list of account transactions.

    Then /^I (?:select|toggle|open) account details?$/
Selects the down arrow next to the Account name which toggles various details (eg. balance). You may accomplish the same task if you know the name of the Account (as you can click on the text using default steps).  
✓ Must already be on the "Account Activity" screen.

Pay Bills Steps
---------------
The following are steps meant to be used for the features corresponding to the menu category "Pay Bills".

    Then /^I select a payment$/ 
Selects the first payment from a list.  
✓ You must already be on a screen with a loaded list of payments.   

    Then /^I review (Scheduled|Completed|Cancelled) Payments$/ do |which|
Selects the specified Payments toggle, asserts data is loaded and the details screen appears on selection of a payment.  
✓ You must aready be on "Review Payments" screen

    Then /^I select a payee$/
Selects the first Payee in a list ("Pay Bills", "Manage Payees" screens)  
✓ You must already be on a screen with a list of payees.

    Given /^I(?: can)? schedule a payment$/
Schedules a payment (of a random amount $1-$9), then verifies that it appears on the "Scheduled Payments" screen (matches the confirmation number).  
✓ Must already have a payment form open on the "Pay Bills" screen.  
✓ Must have at least $9 in the default/top bank account.  
✓ Must have at least one Payee  

    Given /^I see(?: a| the)? payment with confirmation(?: number)? "([^\"]*)"$/ do |num|
Verifies the existence of a specific payment based on a unique cofirmation number.  
✓ Must already be on the "Review Payments" screen.  
✓ Must already be on whichever tab the payment applies to (eg. scheduled, completed)  

Transfer Steps
--------------
The following are steps meant to be used for the transfer feature

    Given /^I select a(?: from| to) and(?: to| from)? account$/ do
Selects a From and To account for the transfer. Uses the caret imageview index to differentiate between the To and From accounts due to the textview not being queryable.  
✓ Must have at least two accounts where at least one is internal.  

    Given /^I schedule a transfer$/ do
Generates a random amount ($30 - $40) and inputs it into the amount field and completes the transfer. The transfer is then verified by looking at the account activity and verifying the amount in the most recent transaction.  
✓ Must be able to transfer $40.00  

    Given /^I verify (?:the)? transfer$/ do
Verifies that the transfer amount appears in the most recent account transaction.





