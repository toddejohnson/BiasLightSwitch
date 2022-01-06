package net.toddejohnson.biaslightswitch

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.Intent.ACTION_BOOT_COMPLETED

class BiasAutoStart : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        when (intent.action) {
            ACTION_BOOT_COMPLETED -> {
                Intent(context, BiasService::class.java).also { srvIntent ->
                    srvIntent.action = ACTION_BOOT_COMPLETED
                    context.startForegroundService(srvIntent)
                }
            }
        }
    }
}