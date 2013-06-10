Calabash - Discover Android
===========================

Install (OSX)
-------------

Please see the [Official Documentation](https://github.com/calabash/calabash-android/blob/master/documentation/installation.md) and this [Testing Guide](https://github.com/akvo/akvo-flow/wiki/Calabash-testing) which also includes installation instructions.  

1. Install the [Android SDK ](http://developer.android.com/sdk/index.html)+ a device or emulator and add the following two paths to your .bash_profile or .zshenv for zsh (both are hidden in the home user folder) [show hidden files in Finder](http://osxdaily.com/2009/02/25/show-hidden-files-in-os-x/)

    The first path may slightly differ depending on where you installed the Android SDK     
    `export ANDROID_HOME="$HOME/android-sdk-macosx"`  
    `export PATH=$PATH:$ANDROID_HOME:$ANDROID_HOME/tools:$ANDROID_HOME/platform-tools:$ANDROID_HOME/add-on`  

2. Install Calabash through the terminal:  
`sudo gem install calabash-android`

3. Verify that you have a somewhat recent version of ruby installed (anything higher than 1.8.x)  
    `ruby -v`

4. Place the .apk file you are using inside the project's calabash folder.  
    
    If the .apk file was **NOT** built on your computer place the keystore that was used inside the Discover calabash folder. Then navigate to the project's calabash folder and [Configure Calabash](https://github.com/akvo/akvo-flow/wiki/Calabash-testing#configuration) using the appropriate keystore settings.

5. Navigate to the project calabash folder and run: (apk file name may differ)  
`calabash-android run LoginActivity.apk`

6. To run specific tagged scenarios run:  
`calabash-android run LoginActivity.apk --tags @name_of_tag1,@name_of_tag2`  

Scenarios, Steps, and Features  
------------------------------
* Scenarios consist of series of steps and are defined and tagged in .feature files.  
* Steps are defined in .rb (ruby) files and can be used in the different scenarios you create.  
* You are free to use a combination of the [Calabash pre-defined steps](https://github.com/calabash/calabash-android/blob/master/ruby-gem/lib/calabash-android/canned_steps.md) and any custom steps defined in the [step_definitions folder](features/step_definitions/). 

Testing Notes
-------------
* The .apk and .keystore (if needed) files just go inside the project's calabash folder
* App installation occurs before each .feature
* App is fully restarted between each scenerio within a .feature
* An end of file reached (EOFError) in the console often means that the app crashed

Links
-----

[Official Calabash Site](http://calaba.sh)  
[Calabash-Android](https://github.com/calabash/calabash-android)  
[Using the Calabash Console](https://github.com/calabash/calabash-android/blob/master/documentation/ruby_api.md)  
[Query Syntax](https://github.com/calabash/calabash-android/wiki/05-Query-Syntax)  
[Cucumber Tags](https://github.com/cucumber/cucumber/wiki/Tags)  
[Compartmentalization/Customization](http://fangmobile.com/2013/03/04/four-levels-of-customization-in-calabash-android/)  
[Feature File Tips](http://docs.behat.org/guides/1.gherkin.html)  
[Tips for writing Cucumber Steps](http://coryschires.com/ten-tips-for-writing-better-cucumber-steps/)  
[Calabash-Android Google Group](https://groups.google.com/forum/?fromgroups#!forum/calabash-android)  