package com.rahbarbazaar.hamyab.ui

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.ConnectivityManager
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.rahbarbazaar.hamyab.R
import com.rahbarbazaar.hamyab.utilities.CustomBaseActivity
import com.rahbarbazaar.hamyab.utilities.GeneralTools
import kotlinx.android.synthetic.main.activity_splash.*
import java.util.*

class SplashActivity : CustomBaseActivity() {

    private var connectivityReceiver: BroadcastReceiver? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        //check network broadcast reciever
        val tools = GeneralTools.getInstance()
        connectivityReceiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context, intent: Intent) {
                tools.doCheckNetwork(this@SplashActivity, findViewById<View>(R.id.rl_root))
            }
        }
        startAnim()


        Timer().schedule(object : TimerTask() {
            override fun run() {
                startActivity(Intent(this@SplashActivity,LoginActivity::class.java))
                overridePendingTransition(R.anim.slide_in, R.anim.slide_out)
                this@SplashActivity.finish()

            }
        }, 2700)

    }


    fun startAnim(){
        avi_splash.show()
    }

    override fun onResume() {
        super.onResume()
        registerReceiver(connectivityReceiver, IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION))
        startAnim()
    }

    override fun onStop() {
        super.onStop()
        unregisterReceiver(connectivityReceiver)
    }
}
