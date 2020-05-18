package com.rahbarbazaar.hamyab.ui

//import android.support.v7.app.AppCompatActivity

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.location.LocationManager
import android.net.ConnectivityManager
import android.os.Bundle
import android.provider.Settings
import android.view.View
import android.widget.Toast
import com.rahbarbazaar.hamyab.BuildConfig
import com.rahbarbazaar.hamyab.R
import com.rahbarbazaar.hamyab.models.dashboard.ProjectList
import com.rahbarbazaar.hamyab.network.ServiceProvider
import com.rahbarbazaar.hamyab.utilities.*
import com.rahbarbazaar.hamyab.utilities.DialogFactory.DialogFactoryInteraction
import kotlinx.android.synthetic.main.activity_splash.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*

class SplashActivity : CustomBaseActivity() {

    private var connectivityReceiver: BroadcastReceiver? = null

    var access_token: String? = null

    private var gpsTracker: GpsTracker? = null
    var strLat = ""
    var strLng= ""

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


        access_token = Cache.getString(this, "access_token")
        if (access_token.equals(null)) {

            Timer().schedule(object : TimerTask() {
                override fun run() {
                    startActivity(Intent(this@SplashActivity, LoginActivity::class.java))
                    overridePendingTransition(R.anim.slide_in, R.anim.slide_out)
                    this@SplashActivity.finish()

                }
            }, 2700)


        }else {
           if(checkGpsON()){
               getProjectList()
           }else{
               avi_splash.hide()
               gpsDialog2()
           }
        }

        txtVersion.setText(BuildConfig.VERSION_NAME)

    }

    private fun gpsDialog2() {
        val dialogFactory = DialogFactory(this)
        dialogFactory.createGpsDialog(object : DialogFactoryInteraction {
            override fun onAcceptButtonClicked(vararg strings: String) {
                val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                startActivityForResult(intent, 17)
            }

            override fun onDeniedButtonClicked(cancel_dialog: Boolean) {
            }
        }, rl_root)
    }

    private fun checkGpsON(): Boolean {
            val manager = this@SplashActivity.getSystemService(Context.LOCATION_SERVICE) as LocationManager
            return manager.isProviderEnabled(LocationManager.GPS_PROVIDER)
    }

    private fun getProjectList() {

        avi_splash.show()

        val service = ServiceProvider(this).getmService()
        val call = service.getProjectList(access_token)
        call.enqueue(object : Callback<ProjectList> {
            override fun onResponse(call: Call<ProjectList>, response: Response<ProjectList>) {
                if (response.code() == 200) {

                    var projectList: ProjectList? = ProjectList()
                    projectList = response.body()
                    val intent = Intent(this@SplashActivity, MainActivity::class.java)
                    intent.putExtra("projectList", projectList)
                    startActivity(intent)
                    finish()
                    overridePendingTransition(R.anim.slide_in, R.anim.slide_out)



                } else {
                    Toast.makeText(this@SplashActivity, resources.getString(R.string.serverFaield), Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<ProjectList>, t: Throwable) {
                Toast.makeText(this@SplashActivity, resources.getString(R.string.connectionFaield), Toast.LENGTH_SHORT).show()

            }
        })
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 17) {
            getProjectList()
            getLocation()
        }
    }

    var a =0
    private fun getLocation() {
        gpsTracker = GpsTracker(this)
        if (gpsTracker!!.canGetLocation()) {
            val latitude = gpsTracker!!.latitude
            val longitude = gpsTracker!!.longitude
            strLat = latitude.toString()
            strLng = longitude.toString()
            // to handle getting gps in first calculate after turning on gps
            if (a < 2) {
                a++
                getLocation()
            }

        } else {
//            gpsTracker!!.showSettingsAlert()
        }
    }




    override fun onResume() {
        super.onResume()
        registerReceiver(connectivityReceiver, IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION))
    }

    override fun onStop() {
        super.onStop()
        unregisterReceiver(connectivityReceiver)
    }
}
