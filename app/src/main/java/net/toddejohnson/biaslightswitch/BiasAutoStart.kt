package net.toddejohnson.biaslightswitch

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.Intent.ACTION_BOOT_COMPLETED
import android.os.PowerManager
import android.util.Log

class BiasAutoStart : BroadcastReceiver() {
    private val TAG = "BiasAutoStart"

    override fun onReceive(context: Context, intent: Intent) {
        when (intent.action) {
            ACTION_BOOT_COMPLETED -> {
                Intent(context, BiasService::class.java).also { srvIntent ->
                    context.startForegroundService(srvIntent)
                }
                val pm = context.getSystemService(Context.POWER_SERVICE) as PowerManager
                if(pm.isInteractive){
                    Log.i(TAG,"Screen on during Autostart")
                    context.switchRelay(true)
                }else{
                    Log.i(TAG,"Screen off during Autostart")
                    context.switchRelay(false)
                }
            }
        }
    }
}