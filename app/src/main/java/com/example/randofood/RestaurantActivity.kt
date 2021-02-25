package com.example.randofood

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.beust.klaxon.JsonObject
import com.beust.klaxon.Klaxon
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.PhotoMetadata
import com.google.android.libraries.places.api.net.FetchPhotoRequest
import com.google.android.libraries.places.api.net.FetchPhotoResponse
import java.io.StringReader
import java.lang.Exception
import kotlin.collections.ArrayList
import kotlin.collections.HashMap
import kotlin.random.Random

class RestaurantActivity : AppCompatActivity() {

    private var restaurants: ArrayList<HashMap<String, String?>> = ArrayList()
    private val random: Random = Random(10)
    private var chosenRestaurant: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_restaurant)

        Places.initialize(this, BuildConfig.GOOGLE_KEY)

        restaurants = intent.getSerializableExtra("restaurants") as ArrayList<HashMap<String, String?>>
        renderRestaurant(restaurants)
    }

    fun renderDirection(view: View) {
        val intent = Intent(android.content.Intent.ACTION_VIEW,
            Uri.parse("http://maps.google.com/maps?saddr=35.6580,139.7276&daddr=${restaurants[chosenRestaurant]["lat"] ?: "35.6580"},${restaurants[chosenRestaurant]["lng"] ?: "139.7276"}"));
        startActivity(intent)
    }

    fun renderAnotherRestaurant(view: View) {
        renderRestaurant(restaurants)
    }

    private fun renderRestaurant(restaurants: ArrayList<HashMap<String, String?>>) {
        if (restaurants.size != 0) {
            val restNum = determineRestaurant(restaurants.size)
            chosenRestaurant = restNum
            display(restaurants[restNum])
        }
    }

    private fun display(restaurant: HashMap<String, String?>) {
        val restName = findViewById<TextView>(R.id.restName).apply {
            text = restaurant["name"] ?: "N/A"
        }

        val restLocation = findViewById<TextView>(R.id.restLocation).apply {
            text = restaurant["vicinity"] ?: "N/A"
        }

        val restLat = findViewById<TextView>(R.id.restLat).apply {
            text = "Latitude: " + restaurant["lat"] ?: "N/A"
        }

        val restLng = findViewById<TextView>(R.id.restLng).apply {
            text = "Longitude: " + restaurant["lng"] ?: "N/A"
        }

        val restOpen = findViewById<TextView>(R.id.restOpen).apply {
            val restOpen = restaurant["open_now"]
            if (restOpen != null) {
                if (restOpen.equals("true", ignoreCase = true)) {
                    text = "Is It Open? YES!"
                } else {
                    text = "Is It Open? NO!"
                }
            } else {
                text = "Is It Open? NO!"
            }
        }

        if (restaurant["photo_reference"] != null) {
            getRestaurantPicture(restaurant)
        } else {
            val imageView = findViewById<ImageView>(R.id.imageView)
            imageView.setImageResource(R.mipmap.ic_launcher_foreground)
        }
    }

    private fun getRestaurantPicture(restaurant: HashMap<String, String?>) {
        val placesClient = Places.createClient(this)

        val photoData = PhotoMetadata.builder(restaurant["photo_reference"] ?: "").build()

        val photoRequest = FetchPhotoRequest.builder(photoData)
                .setMaxWidth(291)
                .setMaxHeight(227)
                .build()
        placesClient.fetchPhoto(photoRequest)
                .addOnSuccessListener { fetchPhotoResponse: FetchPhotoResponse ->
                    val bitmap = fetchPhotoResponse.bitmap
                    val imageView = findViewById<ImageView>(R.id.imageView)
                    imageView.setImageBitmap(bitmap)
                }.addOnFailureListener { exception: Exception ->
                    println(exception)
                    val imageView = findViewById<ImageView>(R.id.imageView)
                    imageView.setImageResource(R.mipmap.ic_launcher_foreground)
                }
    }

    private fun determineRestaurant(size: Int): Int {
        return random.nextInt(size)
    }
}