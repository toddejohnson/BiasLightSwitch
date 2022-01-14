package net.toddejohnson.biaslightswitch

import android.app.*
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Color
import android.hardware.usb.UsbManager
import android.os.IBinder
import android.os.PowerManager
import android.util.Log
import com.hoho.android.usbserial.driver.UsbSerialDriver
import com.hoho.android.usbserial.driver.UsbSerialPort
import com.hoho.android.usbserial.driver.UsbSerialProber

const val ACTION_BIAS_ON = "net.toddejohnson.biaslightswitch.BIAS_ON"
const val ACTION_BIAS_OFF = "net.toddejohnson.biaslightswitch.BIAS_OFF"
const val ACTION_BIAS_USB_PERMISSION = "net.toddejohnson.biaslightswitch.USB_PERMISSION"

class BiasService : Service() {
    private val vTag = "BiasService"
    private val channelID = "BiasServiceNotify"
    private val setRelayOn = byteArrayOf(
        0xa0.toByte(),
        0x01.toByte(),
        0x01.toByte(),
        0xa2.toByte()
    )

    private val setRelayOff = byteArrayOf(
        0xa0.toByte(),
        0x01.toByte(),
        0x00.toByte(),
        0xa1.toByte()
    )
    private var isDeviceConnected: Boolean = false
    private lateinit var usbManager: UsbManager
    private lateinit var usbSerialDriver: UsbSerialDriver
    private val biasReceiver = BiasReceiver()

    override fun onBind(intent: Intent): IBinder? = null

    override fun onCreate() {
        super.onCreate()
        setupForegroundService()
        setupUSB()
    }

    private fun setupForegroundService(){
        val mChannel = NotificationChannel(channelID, getString(R.string.channel_name), NotificationManager.IMPORTANCE_DEFAULT)
        mChannel.description = getString(R.string.channel_description)
        mChannel.lightColor = Color.BLUE
        mChannel.importance = NotificationManager.IMPORTANCE_NONE
        mChannel.lockscreenVisibility = Notification.VISIBILITY_PRIVATE
        val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(mChannel)

        val pendingIntent: PendingIntent =
            Intent(this, BiasActivity::class.java).let { notificationIntent ->
                PendingIntent.getActivity(this, 0, notificationIntent, PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT)
            }
        val notification: Notification = Notification.Builder(this, channelID)
            .setContentTitle(getText(R.string.notification_title))
            .setContentText(getText(R.string.notification_message))
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentIntent(pendingIntent)
            .setTicker(getText(R.string.notification_ticker))
            .build()
        startForeground(101,notification)
        Log.d(vTag,"Service Created")
    }

    private fun checkCurrentState(){
        if(!isDeviceConnected){
            Log.e(vTag,"Can't do anything with check current state without device")
            return
        }
        if(!setupUSBPermissions()){
            Log.e(vTag,"Can't do anything with check current state without permissions")
            return
        }
        val pm = this.getSystemService(Context.POWER_SERVICE) as PowerManager
        if(pm.isInteractive){
            Log.i(vTag,"Screen on during Autostart")
            switchRelay(true)
        }else{
            Log.i(vTag,"Screen off during Autostart")
            switchRelay(false)
        }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when (intent?.action) {
            Intent.ACTION_SCREEN_ON -> {
                Log.d(vTag,"Screen On Event")
                switchRelay(true)
            }
            Intent.ACTION_SCREEN_OFF -> {
                Log.d(vTag,"Screen Off Event")
                switchRelay(false)
            }
            Intent.ACTION_BOOT_COMPLETED -> {
                checkCurrentState()
                startReceiver()
                Log.d(vTag,"Finished boot completed")
            }
            UsbManager.ACTION_USB_DEVICE_ATTACHED -> {
                if (!isDeviceConnected) {
                    setupUSB()
                }
            }
            ACTION_BIAS_USB_PERMISSION -> {
                setupUSBPermissions()
                checkCurrentState()
                Log.d(vTag, "USB Permission Service Intent Complete")
            }
            ACTION_BIAS_OFF ->{switchRelay(false)}
            ACTION_BIAS_ON ->{switchRelay(true)}
            else -> {
                startReceiver()
            }
        }
        Log.d(vTag, "Service Running or action executed")
        return START_STICKY
    }
    private fun startReceiver(){
        val intentFilter = IntentFilter()
        intentFilter.addAction(Intent.ACTION_SCREEN_ON)
        intentFilter.addAction(Intent.ACTION_SCREEN_OFF)
        intentFilter.addAction(ACTION_BIAS_USB_PERMISSION)
        registerReceiver(biasReceiver,intentFilter)
        Log.d(vTag, "Receiver Listening")
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d(vTag, "Service destroy")
        //mHandler.removeCallbacks(mRunnable)
        unregisterReceiver(biasReceiver)
    }

    private fun setupUSB() {
        usbManager = getSystemService(Context.USB_SERVICE) as UsbManager
        if (usbManager.deviceList.isEmpty()) {
            Log.e(vTag, "No Device Found")
            return
        }
        Log.d(vTag, "Found ${usbManager.deviceList.size} USB devices")

        val availableDrivers = UsbSerialProber.getDefaultProber().findAllDrivers(usbManager)
        if (availableDrivers.isEmpty()) {
            Log.e(vTag, "No drivers found")
            return
        }
        Log.d(vTag,"Found ${availableDrivers.size} devices with drivers")
        // user first driver.
        usbSerialDriver = availableDrivers[0]
        Log.d(vTag, "Using Driver $usbSerialDriver")
        Log.d(vTag, "Using Device ${usbSerialDriver.device}")
        isDeviceConnected = true
        setupUSBPermissions()
        checkCurrentState()
    }
    private fun setupUSBPermissions():Boolean {
        if(!isDeviceConnected){
            return false
        }
        if (!usbManager.hasPermission(usbSerialDriver.device)) {
            val intent = Intent(this,BiasService::class.java)
            intent.action = ACTION_BIAS_USB_PERMISSION
            val pendingIntent = PendingIntent.getService(this,0,intent,PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT)
            usbManager.requestPermission(usbSerialDriver.device, pendingIntent)
            Log.w(vTag, "Requested permissions")
            return false
        }
        return true
    }

    private fun switchRelay(on:Boolean){
        if(!isDeviceConnected){
            Log.e(vTag,"Device not connected when calling switchRelay")
            return
        }
        if(!setupUSBPermissions()){
            Log.e(vTag,"Can't switchRelay without permissions")
            return
        }
        val usbConnection = usbManager.openDevice(usbSerialDriver.device)
        val serialPort = usbSerialDriver.ports[0]
        serialPort.open(usbConnection)
        serialPort.setParameters(
            9600,
            UsbSerialPort.DATABITS_8,
            UsbSerialPort.STOPBITS_1,
            UsbSerialPort.PARITY_NONE
        )
        if (on) {
            Log.d(vTag, "Switching light on")
            serialPort.write(setRelayOn, 500)
        } else {
            Log.d(vTag, "Switching light off")
            serialPort.write(setRelayOff, 500)
        }
        serialPort.close()
    }
}