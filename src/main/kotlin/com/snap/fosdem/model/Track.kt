package com.snap.fosdem.model

import java.util.*

data class Track(
        val id: String = UUID.randomUUID().toString(),
        val name: String,
        val events: List<Event>,
        val stands: List<Stand>,
)
