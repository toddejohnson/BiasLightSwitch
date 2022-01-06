package net.toddejohnson.biaslightswitch

import android.app.*
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Color
import android.os.IBinder
import android.util.Log


class BiasService : Service() {
    private val TAG = "BiasService"
    private val CHANNEL_ID = "BiasServiceNotify"

    private val biasReceiver = BiasReceiver()

    override fun onBind(intent: Intent): IBinder? = null

    override fun onCreate() {
        super.onCreate()
        val mChannel = NotificationChannel(CHANNEL_ID, getString(R.string.channel_name), NotificationManager.IMPORTANCE_DEFAULT)
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
        val notification: Notification = Notification.Builder(this, CHANNEL_ID)
            .setContentTitle(getText(R.string.notification_title))
            .setContentText(getText(R.string.notification_message))
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentIntent(pendingIntent)
            .setTicker(getText(R.string.notification_ticker))
            .build()
        startForeground(101,notification)
        Log.d(TAG,"Service Created")
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val intentFilter = IntentFilter()
        intentFilter.addAction(Intent.ACTION_SCREEN_ON)
        intentFilter.addAction(Intent.ACTION_SCREEN_OFF)
        registerReceiver(biasReceiver,intentFilter)
        Log.d(TAG, "Service Started")
        return START_STICKY
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d(TAG, "Service destroy")
        //mHandler.removeCallbacks(mRunnable)
        unregisterReceiver(biasReceiver)

    }
}