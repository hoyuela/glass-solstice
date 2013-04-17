# THE FOLLOWING STEPS ARE NOT SPECIFIC TO THE DISCOVER APPLICATION

# Asserts a View with the given ID exists
Given /^I(?: should)? see view with id "([^\"]*)"$/ do |id|
	performAction('has_view', id)
end

# Scrolls down (uses screen percents)
Given /^I scroll down$/ do
	performAction("drag", 50, 50, 50, 0, 1)
end

# Scrolls up (uses screen percents)
Given /^I scroll up$/ do
	performAction("drag", 50, 50, 0, 50, 1)
end

# Saves the text contained within a text view
Given /^I save text from view with id "([^\"]*)"$/ do |id|
	@savedText = query("textview marked:'#{id}'")[0]["text"]
	puts "Saved: " + @savedText
end

# Saves the text contained within a text view that has a corresponding text view label
Given /^I save text with label "([^\"]*)"$/ do |label|
	text = query('textview', 'text') # Creates an array of all the text (in TextViews) on the screen (in order)
	index = text.index("#{label}")

	if index == nil then
		raise 'Label ' + label + " Not Found"
	else
		@savedText = text[index + 1]
	end
end

# Determines if the previously saved text is currently visible on the screen
Given /^I see the saved text$/ do
	macro 'I see "#{@savedText}"'
end

# Determines if the previously saved text is currently visible on the screen
Given /^I (?:don't|should not) see the saved text$/ do
	macro 'I should not see "#{@savedText}"'
end
