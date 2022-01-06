package net.toddejohnson.biaslightswitch

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log


class BiasReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        Log.d("BiasReceiver","Received Intent ${intent.action}")
        val serviceIntent = Intent(context, BiasService::class.java)
        serviceIntent.action = intent.action
        context.startForegroundService(serviceIntent)
    }

}