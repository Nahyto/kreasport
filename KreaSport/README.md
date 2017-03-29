KreaSport
=========


----------

## TODO ##

 - **MainActivity**
	 - Permissions
		 - custom frag/activity
		 - request + request receiver
	 - Transfer downloaded races to `ExploreFragment`
 - **HomeFragment**
	 - Check key format before download
 - **ExploreFragment**
	 - Everything:
		 - Map
		 - CustomIconOverlay
			 - Custom icons
			 - Proper adding w/o reloading
		 - Save state
		 - Callbacks for interaction w/ `BottomSheetFragment`
 - **OfflineAreaActivity**
	 - Everything:
		 - Activity Layout
		 - Map
		 - Overlay
			 - Size estimation
		 - Aysnc download
			 - Notification w/ stop
		 - ArrayAdapter & Model
		 - Acitivity for downloaded areas
## DONE ##
 - **MainActivity**
	 - Navigation drawer
		 - Switch fragments
		 - Save fragment state
		 - Backstack
	 - Callback for downloads from `HomeFragment`
 - **HomeFragment**
	 - Layout
		 - Text & buttons

----------


Project description
-------------------

The Mapsv3 app was started as a personal initiative to learn how to implement osmdroid in an app with multiple fragments, bottom sheets, custom overlays created from a list of races...

KreaSport aims to rebuild Mapsv3 with a new perspective. The goals for this app are the following:

 - Rebuild the app with the same features
 - Rebuild these features, this time hopefully being able to see the bigger picture and anticipate future expansion problems
 - Use clean code, separate classes properly, use callbacks, intents...
 - Implement a cleaner interface

Finally, once these goals are reached, we may be able to use this app as a clean start, replace Mapsv3 and be able to continue forwards according to the original goal of KreaSport which is to create an app for orienteering races.


----------
