Personal Transportation
=======

Description
-----------

The purpose of this project is to make an electric vehicle user dashboard which 
will be implemented in the electric vehicle being produced by PTS (Personal 
Transportation Solutions). The user will interact with the interface from an 
android tablet located on the machine. Functions which the user can perform from
the Android table interface include activating or deactivating the windshield 
wipers, the front car lights, the hazard lights, the air conditioning, and the 
turn signals. The interface will also display the changes in the state of 
certian devices in the car, such as the state of the car's lights, the car's 
speed, and its battery life.

This code is being developed in Java using Android Studio.

Group Members
-----------

* Timothy Rast
* Joseph O'Banion
* Richard Barella
* Justin Williamson
* Ming Xia

Running the Application
-----------

### Testing Purposes

To run the application, one needs to have a Windows, Linux, or Mac OS computer 
system which is capable of running Android Studio. After installing 
Android Studio and providing it access to a local copy of the repository (pulled
from Gitlab), the application can be simulated on a Nexus 15 tablet or 
equivalent running Android version 5.0 or greater. The user can interact with 
the application via the mouse to simulate touch input on the android device.

The user can additionally connect an Android device via usb cable through the 
host computer to run the application. The user can then interact with the device 
using touch inputs on the Android device.

### Production Unit

The actual device can be programmed through Android Studio to the host Android 
device via a usb cable. The application loaded on the host android device 
should act like an app. 

Additional Notes
-----------

### Message data encoding:


Lights: off = 0, on = 1, hi-beams off = 2, hi-beams on = 3
turn signals: off = 0, left = 1, right = 2
wipers/defrost: off = 0, low = 1, medium = 2, high = 3
hazards: off = 0, on = 1

- data is sent asynchronously from the tablet when a button push modifies that button's state (specifically for modifiable buttons)
- the screen is updated asynchronously when a command is sent to change (for non-modifiable buttons (speed, battery))
- the turn signals are modifiable both through user input and communication protocol (using the message data encoding mentioned above)
