PROBLEMS ENCOUNTERED
-----------

* Problems setting up Android Studio
  * Make sure that all the packages listed in the README are downloaded and installed.
* Issues with Android emulation
  * Make sure that there is enough RAM available in the computer
  * Pick a tablet with the correct screen size
* We had trouble finding software on the PC that can communicate with the Android over USB.
  * an example was found using "libusb" that worked for communication (our code for this is found in the USBHostLinux project)
* There were no simple USB examples available on the web for Android
  * We fixed this through trial and error until we had code that worked.
* Most of the USB examples were for USB-Host mode, and we originally did not know the difference between the two modes.
  * The Official Android Documentation was useful in understanding how the USB protocol code worked, and understanding the difference between the two modes.
