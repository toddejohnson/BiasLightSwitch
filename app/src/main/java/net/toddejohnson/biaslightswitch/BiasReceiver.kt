package net.toddejohnson.biaslightswitch

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.Intent.ACTION_SCREEN_OFF
import android.content.Intent.ACTION_SCREEN_ON
import android.util.Log

class BiasReceiver : BroadcastReceiver() {
    private val TAG = "BiasReceiver"

    override fun onReceive(context: Context, intent: Intent) {
        when (intent.action) {
            ACTION_SCREEN_ON -> {
                Log.d(TAG,"Screen On Event")
                context.switchRelay(true)
            }
            ACTION_SCREEN_OFF -> {
                Log.d(TAG,"Screen Off Event")
                context.switchRelay(false)
            }
        }
    }

}