Given /^I select a(?: from| to) and(?: to| from)? account$/ do
        # The textviews that are present in the cells for selecting accounts cannot be referenced directly
        # so we are tapping on the caret imageviews

         # Select From account
        performAction('click_on_view_by_id', query("imageview marked:'caret'")[0]["id"])
        # Selects the first account shown
        performAction('click_on_text', query("textview marked:'top_label'")[0]["text"])
        
        # Select To account
        performAction('click_on_view_by_id', query("imageview marked:'caret'")[1]["id"])
        # Selects the second account shown
        performAction('click_on_text', query("textview marked:'top_label'")[1]["text"])

end

# Enter an amount to be transfered (greater than $25) then schedule the transfer
Given /^I schedule a transfer$/ do
        macro 'I enter a random amount between 30 and 40 into field 1'

        performAction('click_on_text', "Schedule Transfer")
        performAction('wait_for_dialog_to_close')
        performAction('wait',5)

        # Grab amount
        @transferAmount = query("textview marked:'middle_label'")[2]['text']
        puts "Transfer amount: " + @transferAmount

        macro 'I verify the transfer'
end

# Navigates to the account summary and verifies the transaction appears in scheduled transactions
Given /^I verify(?: the)? transfer$/ do
        macro 'I navigate to "Account Summary" under "Account"'
        performAction('wait_for_dialog_to_close')
        performAction('wait',5) # Wait for views to load

        # Tap the first account so we can view activity
        x = query("textview marked:'acct_nickname'")[0]["rect"]["center_x"]
        y = query("textview marked:'acct_nickname'")[0]["rect"]["center_y"]     
        performAction("touch_coordinate", x, y)

        # Look for transaction
        performAction('wait',2) #wait for 2 seconds for transactions to load
        macro 'I select a payment' # Go the the most recent transaction (which should match our transfer amount) 
        performAction('wait',2) #wait for transaction details to load
        # Verify that the amount is the same
        lookupAmount = query("textview marked:'amount_cell'")[0]['text']
        if @transferAmount != lookupAmount
            raise 'TRANSFER NOT FOUND'
        end
end