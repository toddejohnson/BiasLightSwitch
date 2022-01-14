package net.toddejohnson.biaslightswitch

import android.content.Context
import android.content.Intent
import android.hardware.usb.UsbManager
import android.os.Bundle
import android.widget.Toast
import androidx.fragment.app.FragmentActivity


/**
 * .
 */
class BiasUSBActivity : FragmentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }


    override fun onResume() {
        super.onResume()
        when (this.intent?.action) {
            UsbManager.ACTION_USB_DEVICE_ATTACHED -> {
                val usbDevice = this.intent.getParcelableArrayExtra(UsbManager.EXTRA_DEVICE)
                val brodcastIntent = Intent(ACTION_BIAS_USB_PERMISSION)
                brodcastIntent.putExtra(UsbManager.EXTRA_DEVICE, usbDevice)
            }
        }
        finish()
    }

}



