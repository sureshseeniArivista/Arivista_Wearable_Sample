package com.arivista.wearabletest

import android.Manifest
import android.content.Intent
import android.os.Bundle
import android.speech.RecognizerIntent
import android.support.wearable.activity.WearableActivity
import android.util.Log
import android.widget.TextView
import com.arivista.commonprogram.network.Api
import com.arivista.commonprogram.network.NetworkUtils
import com.arivista.wearabletest.pojo.Example
import kotlinx.android.synthetic.main.activity_main.*
import org.w3c.dom.Text
import retrofit2.Response
import android.speech.SpeechRecognizer
import android.Manifest.permission
import android.Manifest.permission.RECORD_AUDIO
import android.content.pm.PackageManager
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import android.support.constraint.solver.widgets.WidgetContainer.getBounds
import android.view.MotionEvent
import android.view.View
import android.view.View.OnTouchListener




class MainActivity : WearableActivity() {

    var temp_Min: Float? = null
    var temp_Max: Float? = null
    var detailsTextView:TextView?=null

    private val speechRecognizer: SpeechRecognizer? = null
    private val TAG = "MainActivity"
    private val REQUEST_RECORD_AUDIO_PERMISSION_CODE = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Enables Always-on
        setAmbientEnabled()

         detailsTextView = findViewById<TextView>(R.id.details)

        var btnSpeak = findViewById<Button>(R.id.btSpeak)

        var city_name=findViewById<EditText>(R.id.etTextHint)


        btnSpeak.setOnClickListener {
            if(city_name.text!=null)
                if(!city_name.text.toString().equals(""))
                    getDetails(city_name.getText().toString()+",in")
                 else
                    getDetails("Chennai,in")
            else
                getDetails("Chennai,in")
        }

        city_name.setOnTouchListener(object : OnTouchListener {
            override fun onTouch(v: View, event: MotionEvent): Boolean {
                val DRAWABLE_LEFT = 0
                val DRAWABLE_TOP = 1
                val DRAWABLE_RIGHT = 2
                val DRAWABLE_BOTTOM = 3

                if (event.action == MotionEvent.ACTION_UP) {
                    if (event.rawX >= city_name.getRight() - city_name.getCompoundDrawables()[DRAWABLE_RIGHT].getBounds().width()) {
                        // your action here
                        Toast.makeText(this@MainActivity,"Hi",Toast.LENGTH_LONG).show()
                        startRecognition()
                        return true
                    }
                }
                return false
            }
        })

    }

    private fun getDetails(toString: String) {
            NetworkUtils().getApiService()?.create(Api::class.java)!!.getGetLists(toString,"f9c07293f168d281e8dc501ccf507d9b")
                .enqueue(object : retrofit2.Callback<Example> {
                    override fun onFailure(call: retrofit2.Call<Example>, t: Throwable) {
                        Log.d("Fail Message", "" + t.message)
                    }

                    override fun onResponse(call: retrofit2.Call<Example>, response: Response<Example>) {

                        Log.d(
                            "Success Mess",
                            "" + response.message() + " " + response.raw() + "   " + response.errorBody()
                        )

                        var data = response.body()
                        if (data != null) {

                            Log.d("Success Message", "" + data!!.main.temp)
                            tempratureConvertion(data!!.main.temp)
                            detailsTextView!!.setText(
                                "Temp :" + tempratureConvertion(data!!.main.temp)
                                        + "\nHumidity : " + data.main.humidity
                                        + "\nWind Speed : " + data.wind.speed
                                        + "\nPlace : " + data.name
                            )
                        } else {
                            Toast.makeText(applicationContext, "Empty Result", Toast.LENGTH_LONG).show()
                        }
                    }

                })
    }

    private fun requestPermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(
                this,
                Manifest.permission.RECORD_AUDIO
            )
        ) {
            Toast.makeText(this, "Requires RECORD_AUDIO permission", Toast.LENGTH_SHORT).show()
        } else {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.RECORD_AUDIO),
                REQUEST_RECORD_AUDIO_PERMISSION_CODE
            )
        }
    }


    private fun startRecognition() {
        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
        intent.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE, packageName)
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, "en")
        speechRecognizer!!.startListening(intent)
    }

    private fun showResults(results: Bundle) {
        val matches = results
            .getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
        Toast.makeText(this, matches!![0], Toast.LENGTH_LONG).show()
    }


    override fun onDestroy() {
        speechRecognizer?.destroy()
        super.onDestroy()
    }

    fun tempratureConvertion(value: Float): Float {
        return ((value - 32) * 5) / 9
    }


}
