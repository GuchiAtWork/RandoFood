package com.example.randofood

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

class RestaurantActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_restaurant)

        val restaurants: ArrayList<HashMap<String, String?>> = intent.getSerializableExtra("restaurants") as ArrayList<HashMap<String, String?>>
    }


}