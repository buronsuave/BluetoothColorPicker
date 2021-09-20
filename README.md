# Bluetooth Color Picker
## Installation
Download the file called bluetooth-color-picker.apk on your phone and proceed with the installation. Make sure it installs correctly even with Android warnings.

## Preparation of the HC05 module
Before connecting, it is necessary to make sure that the Arduino HC05 module is turned on and configured with the appropriate name. The current code will recognize the device only if it has the name "HC05" (many by default have this name). You can verify the name by trying to connect to the device first.

## Using the app
When starting the app for the first time, it will be requested to turn on the mobile's Bluetooth if it is off; It is important that it is turned on for the operation of the app. Once turned on, a toast-type message should appear showing a MAC address: it is the network address of the device that it recognized under the name HC05 from the list of devices previously seen by the mobile. If you do not see this message, it is likely that there is an error not controlled by the application or the module was not configured correctly. When this message disappears, you have to press the "connect" button. A message should appear indicating that the button has been clicked, it may take a few seconds. Now you can press any part of the RGB circle and the application will send a raster to the corresponding module with the following information:
- "R"
- an integer with the value of R (0, 255)
- "G"
- an integer with the value of G (0, 255)
- "B"
- an integer with the value of B (0, 255)

So 6 bytes must be received in each call. They are sent every time there is a click event on the RGB wheel or when it is dragged on it (a drag event).
Check the implementation of the Arduino client part in the link: https://github.com/buronsuave/sistemas-embebidos-II/tree/main/producto-integrador for an example of reading data
