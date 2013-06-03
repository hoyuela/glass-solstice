        
Given /^I select a (?:from|to)? account$/ do
        # Transfer from external account
        performAction('click_on_text', "Select Account")
        
        accounts = query("textview id:'top_label'", 'id')

        # Tap the first account that is in the list (will be external account unless no external accounts exist)
        performAction('click_on_view_by_id', accounts[0])
end

# Enter an amount to be transfered (greater than $25) then schedule the transfer
Given /^I schedule a transfer$/ do
        macro 'I enter a random amount between 30 and 40 into field 1'

        # Grab the random amount eqntered
        @savedAmount = query('field', 'text')[0] # Creates an array of all the text in fields (should only have 1)

        performAction('click_on_text', "Schedule Transfer")
end

# Navigates to the account summary and verifies the transaction appears in scheduled transactions
Given /^I verify (?:the)? transfer$/ do
        macro 'I navigate to "Account Summary" under "Account"'
        macro 'I wait for dialog to close'
        macro 'I wait'

        # Tap the first account so we can view activity
        x = query("textview marked:'acct_nickname'")[0]["rect"]["center_x"]
        y = query("textview marked:'acct_nickname'")[0]["rect"]["center_y"]     
        performAction("touch_coordinate", x, y)

        # Verify that our transaction is in scheduled transaction based on the amount
        macro 'I wait to see "Scheduled"'
        macro 'I touch the "Scheduled" text'
        macro 'I wait for 2 seconds'

        # TODO find better way to keep scrolling until savedAmount text found
        begin
                performAction("drag", 50, 50, 50, 0, 1) # Scroll down (uses screen percents)
                matchingAmounts = query("textview marked:'#{@savedAmount}'")
                found = matchingAmounts.length != 0

        end while found

        macro 'I wait to see "#{@savedAmount}"'
end