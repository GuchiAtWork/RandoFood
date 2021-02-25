package com.example.randofood

import android.content.Context
import android.content.Intent
import android.location.LocationManager
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.beust.klaxon.JsonObject
import com.beust.klaxon.Klaxon
import java.io.StringReader

class MainActivity : AppCompatActivity() {

    private val restaurants: ArrayList<HashMap<String, String?>> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    fun showRestaurant(view: View) {
        sendAndRequestResponse()
    }

    private fun sendAndRequestResponse() {

        //RequestQueue initialized
        val mRequestQueue = Volley.newRequestQueue(this)

        val url = "https://maps.googleapis.com/maps/api/place/nearbysearch/json?location=35.6580,139.7276&radius=1650&types=restaurant&key=${BuildConfig.GOOGLE_KEY}"

        //String Request initialized
        val mStringRequest = StringRequest(Request.Method.GET, url, Response.Listener { response ->

            val klaxon = Klaxon()
            val parsed = klaxon.parseJsonObject(StringReader(response))

            val resultsArray = parsed.array<JsonObject>("results")

            if (resultsArray != null) {
                for (i in 0 until resultsArray.size) {
                    restaurants.add(hashMapOf())
                    restaurants[i]["lat"] = resultsArray?.get("geometry")?.get("location")?.get("lat")?.get(i).toString()
                    restaurants[i]["lng"] = resultsArray?.get("geometry")?.get("location")?.get("lng")?.get(i).toString()
                    restaurants[i]["icon"] = resultsArray?.get("icon")?.get(i).toString()
                    restaurants[i]["name"] = resultsArray?.get("name")?.get(i).toString()
                    restaurants[i]["open_now"] = resultsArray?.get("opening_hours")?.get("open_now")?.get(i).toString()
                    restaurants[i]["photo_reference"] = resultsArray?.get("photos")?.get("photo_reference")?.get(i).toString()
                    restaurants[i]["photo_height"] = resultsArray?.get("photos")?.get("height")?.get(i).toString()
                    restaurants[i]["photo_width"]= resultsArray?.get("photos")?.get("width")?.get(i).toString()
                    restaurants[i]["vicinity"] = resultsArray?.get("vicinity")?.get(i).toString()
                }
            }

            val intent = Intent(this, RestaurantActivity::class.java).apply {
                putExtra("restaurants", restaurants)
            }
            startActivityForResult(intent, 500)

        }, Response.ErrorListener { error ->
            Log.i(MainActivity::class.java.name, "Error :$error")
        })
        mRequestQueue.add(mStringRequest)
    }
}