# Steps for testing the ATM Locator
#
# Everything below is just iOS calabash code that needs to be modified for Android
# STILL IN PROGRESS
# DOES NOT WORK IN EMULATOR

# Goes to ATM Locator either from login screen or from the side menu
# then searches an address and views the streetview of a specific pin
Given /^I verify ATM locator from the (login screen|menu)$/ do |where|
	#if where == "menu"
	#	macro %Q[I navigate to "Search By Location" under "ATM Locator"]
	#	macro %Q[I verify ATM locator search a location]
	#
	#	macro %Q[I navigate to "Find Nearby" under "ATM Locator"]
	#	macro %Q[I verify ATM locator find nearby]
	#else
	#	touch("button marked:'RegisterNowATMLocatorButton'")
	#	macro %Q[I verify ATM locator search a location]
	#	macro %Q[I press the back button]
	#
	#	touch("button marked:'RegisterNowATMLocatorButton'")
	#	macro %Q[I verify ATM locator find nearby]
	#end
end

# Searches for solstices address and views street view of nearby pin
# **Does not press the allow button**
Given /^I verify ATM locator search a location$/ do
	# Check if modal appears prompting user to press Allow
	# Close the modal because we are just searching for a location
	if (element_exists("view marked:'modal_close_button'"))	
		touch("view marked:'modal_close_button'")
	else

	end

	touch("textField")
	keyboard_enter_text("641 West Lake St. Chicago, IL")
	#keyboard_enter_text(done)
	keyboard_enter_char("Return")

	# Recorded playback that zooms in on a pin and views the streetview
	playback "map_view_pin_test"

	# Now go to the list view
	touch("imageView marked:'atm_list_view_button'")

	# Scroll up and down
	scroll("scrollView","down")
	scroll("scrollView","down")
	scroll("scrollView","up")
	scroll("scrollView","up")
end

# Searches for solstices address and views street view of nearby pin
# **Presses the allow button**
Given /^I verify ATM locator find nearby$/ do
	# Check if modal appears prompting user to press Allow
	if element_exists("button marked:'Allow'")	
		touch("button marked:'Allow'")
	end
	
	touch("textField")
	keyboard_enter_text("641 West Lake St. Chicago, IL")
	#keyboard_enter_text(done)
	keyboard_enter_char("Return")

	# Recorded playback that zooms in on a pin and views the streetview
	playback "map_view_pin_test"

	# Now go to the list view
	touch("imageView marked:'atm_list_view_button'")

	# Scroll up and down
	scroll("scrollView","down")
	scroll("scrollView","down")
	scroll("scrollView","up")
	scroll("scrollView","up")
end