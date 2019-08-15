# Remote Client

This application is using the java-remote-object (https://github.com/vladaeloaiei/byte-remote-object) framework to control an object on a server side in a remote way.

The screen sharing is done using pipeline architecture which provides 20-30 FPS.

An implementation for server for Windows can be found at: https://github.com/vladaeloaiei/remote-server

## Features
- real time screen sharing
- keyboard and mouse control
- image transfer to server (image picker can be found here: https://github.com/siralam/BSImagePicker)
- host's audio volume control using the phone's audio buttons
- option to choose the communication protocol (TCP/UDP)
