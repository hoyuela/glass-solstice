Calabash - Discover Android
===========================

Install (OSX)
-------------

Please see the [Official Documentation](https://github.com/calabash/calabash-android/blob/master/documentation/installation.md) and this [Testing Guide](https://github.com/akvo/akvo-flow/wiki/Calabash-testing) which also includes installation instructions.  

1. Install the [Android SDK ](http://developer.android.com/sdk/index.html)+ a device or emulator    
and Add the following two paths to your .bash_profile (found in home user folder).  
The first path may slightly differ depending on where you installed the Android SDK     
`export ANDROID_HOME="$HOME/android-sdk-macosx"`  
`export PATH=$PATH:$ANDROID_HOME:$ANDROID_HOME/tools:$ANDROID_HOME/platform-tools:$ANDROID_HOME/add-on`


2. Install Calabash through the terminal:  
`sudo gem install calabash-android`

3. Navigate to Discover Calabash folder & [Configure Calabash](https://github.com/akvo/akvo-flow/wiki/Calabash-testing#configuration) (if needed).  This is necessary if the apk file you are using was **NOT** built on your computer.  It is required that you have the keystore used to build the apk.  

4. Navigate to Discover Calabash folder and run: (apk file name may differ)  
`calabash-android run Discover-Launch.apk`

Steps
-----
Scenarios are made up of a series of steps.  You are free to use a combination of the [Calabash pre-defined steps](https://github.com/calabash/calabash-android/blob/master/ruby-gem/lib/calabash-android/canned_steps.md) and the custom [Discover steps](features/step_definitions/steps.md).

Testing Notes
-------------

* App installation occurs before each .feature
* App is fully restarted between each scenerio within a .feature

Links
-----

[Official Calabash Site](http://calaba.sh)  
[Using the Calabash Console](https://github.com/calabash/calabash-android/blob/master/documentation/ruby_api.md)  
[Cucumber Tags](https://github.com/cucumber/cucumber/wiki/Tags)  
[Compartmentalization/Customization](http://fangmobile.com/2013/03/04/four-levels-of-customization-in-calabash-android/)  
[Tips for writing Cucumber Steps](http://coryschires.com/ten-tips-for-writing-better-cucumber-steps/)s