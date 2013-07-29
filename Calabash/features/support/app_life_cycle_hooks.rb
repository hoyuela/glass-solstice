require 'calabash-android/management/adb'
require 'calabash-android/operations'

Before do |scenario|
	# Only start a new test server when NO_STOP == 0, otherwise the app does not close
    if(ENV["NO_STOP"] == 0)
		start_test_server_in_background
    end
end

# If a scenario is tagged with @restart the app will exit and reinstall
Before ("@restart") do |scenario|
	# Restart the app, but do not reinstall
	start_test_server_in_background
end

After do |scenario|
	if scenario.failed?
		screenshot_embed
	end
	
	# Only shutdown the test server when NO_STOP == 0
	if(ENV["NO_STOP"] == 0)
  		shutdown_test_server
  	end
end