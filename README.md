# Bias Light Switch

Bias Light Switch is an application and suggested hardware hack that will let Android TV/Google TV
turn on/off the Bias lights with the screen.

**You are modifying your Bias light, USB Relay, and plugging that into your TV. You do so at your own risk!**

### History(Long Version)

As usual Bias Light Switch was solution to a problem I had. Well perhaps overengineering with software or WAF
of me not taking apart our new TV. I've used Bias lights on our TV for a while to help when watching 
in a dark environment. 2 TVs ago we had LEDs and a IR remote for them. Finding both remotes stunk and 
we didn't change the color. Eventually we used a Harmony 650 to control the system. As we went to Google 
TV we replaced the Bias LEDs with USB white ones and used the "dumb" TV's USB port that turned off with 
the display. 

Fast forward to "Smart" Google TV and the USB port stays on and flickers when the TV is off. I remembered
another project that used USB relays and I figured I could jumper the +5V and relay the GND of the 
USB relay to a female USB A connection. 

## Requirements

You need:
* 5v/USB LED strip that isn't multi color(although some might work and I haven't tested).
* USB relay that uses `a0 01 01 a2` for on and `a0 01 00 a1` for off. I  look forward to a PR to make this configurable

## Hardware Hack

Take the USB relay and:
1. Make a jumper From USB Ground pin relay COM pin.
2. Then connect LED +5V to the +5V pin of the USB.  
3. Connect the LED Ground to the NO pin.
4. Plug USB relay into TV USB Port

## Installation

Install apk(Perhaps from Play Store?). 
Open the app and click turn on and turn off to test if it will control your lights. If you replace 
the device or something goes wrong it might ask for permission to open the USB device. 

## Usage

When the TV screen is off the relay will open(turn off). When you turn the TV screen on the relay will close(turn on). 

## Contributing
Pull requests are welcome. For major changes, please open an issue first to discuss what you would like to change.

Please make sure to update tests as appropriate.

## License
[GPL3](https://choosealicense.com/licenses/gpl-3.0/)