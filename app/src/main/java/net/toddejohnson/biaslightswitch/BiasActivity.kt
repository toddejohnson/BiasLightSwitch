package net.toddejohnson.biaslightswitch

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.hardware.usb.UsbManager
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.fragment.app.FragmentActivity
import com.hoho.android.usbserial.driver.UsbSerialProber
import com.hoho.android.usbserial.driver.UsbSerialPort


/**
 * .
 */
class BiasActivity : FragmentActivity() {
    @Suppress("Unused")
    private val TAG = "BiasActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        Intent(this, BiasService::class.java).also { intent ->
            startForegroundService(intent)
        }
    }

    @Suppress("UNUSED_PARAMETER")
    fun stopStart(view: android.view.View) {
        Intent(this, BiasService::class.java).also { intent ->
            stopService(intent)
            Thread.sleep(1000)
            startForegroundService(intent)
        }
    }

    @Suppress("UNUSED_PARAMETER")
    fun turnOn(view: android.view.View) {
        switchRelay(true)
        toast("Bias Light On")
    }

    @Suppress("UNUSED_PARAMETER")
    fun turnOff(view: android.view.View) {
        switchRelay(false)
        toast("Bias Light Off")
    }

}


fun Context.toast(message:String){
    Toast.makeText(applicationContext,message,Toast.LENGTH_SHORT).show()
}
fun Context.switchRelay(on: Boolean){
    val TAG = "BiasSwitchRelay"
    val SET_RELAY_ON = byteArrayOf(
        0xa0.toByte(),
        0x01.toByte(),
        0x01.toByte(),
        0xa2.toByte()
    )

    val SET_RELAY_OFF = byteArrayOf(
        0xa0.toByte(),
        0x01.toByte(),
        0x00.toByte(),
        0xa1.toByte()
    )

    val manager = getSystemService(Context.USB_SERVICE) as UsbManager
    val usbdevices =  manager.deviceList

    if(usbdevices.isEmpty()){
        Log.e(TAG,"No Device Found")
        return
    }
    Log.d(TAG,"Found ${usbdevices.size} devices")
    val availableDrivers = UsbSerialProber.getDefaultProber().findAllDrivers(manager)
    if(availableDrivers.isEmpty()){
        Log.e(TAG,"No drivers found")
        return
    }
    // user first driver.
    val driver = availableDrivers[0]
    Log.d(TAG,"Using Driver $driver")
    Log.d(TAG,"Using Device ${driver.device}")
    if(!manager.hasPermission(driver.device)){
        val pendingIntent: PendingIntent =
            Intent(this, BiasActivity::class.java).let { notificationIntent ->
                PendingIntent.getActivity(this, 0, notificationIntent, PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT)
            }
        manager.requestPermission(driver.device,pendingIntent)
        Log.w(TAG,"Requested permissions")
        return
    }
    val connection = manager.openDevice(driver.device)
    if(connection == null){
        Log.e(TAG,"Error opening device connection")
        return
    }
    val port = driver.ports[0]
    port.open(connection)
    port.setParameters(9600,UsbSerialPort.DATABITS_8, UsbSerialPort.STOPBITS_1, UsbSerialPort.PARITY_NONE)
    if(on){
        Log.d(TAG, "Switching light on")
        port.write(SET_RELAY_ON, 500)
    }else{
        Log.d(TAG, "Switching light off")
        port.write(SET_RELAY_OFF, 500)
    }
}


