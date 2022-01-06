package net.toddejohnson.biaslightswitch

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.fragment.app.FragmentActivity


/**
 * .
 */
class BiasActivity : FragmentActivity() {

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
        Intent(this, BiasService::class.java).also { intent ->
            intent.action = ACTION_BIAS_ON
            startForegroundService(intent)
        }
        toast("Bias Light On")
    }

    @Suppress("UNUSED_PARAMETER")
    fun turnOff(view: android.view.View) {
        Intent(this, BiasService::class.java).also { intent ->
            intent.action = ACTION_BIAS_OFF
            startForegroundService(intent)
        }
        toast("Bias Light Off")
    }

}


fun Context.toast(message:String){
    Toast.makeText(applicationContext,message,Toast.LENGTH_SHORT).show()
}



