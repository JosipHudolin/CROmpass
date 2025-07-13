package com.example.crompass.model

import com.google.firebase.firestore.GeoPoint

data class Destination(
    val id: String = "",
    val name: String = "",
    val description: String = "",
    val category: String = "",
    val location: GeoPoint = GeoPoint(0.0, 0.0),
    val imageUrl: String = ""
)
