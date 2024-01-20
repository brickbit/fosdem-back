package com.snap.fosdem.model

data class Stand(
    val title: String,
    val image: String,
    val features: List<StandFeatures>
)

data class StandFeatures(
    val subtitle: String,
    val type: String,
    val companies: MutableList<String>
)