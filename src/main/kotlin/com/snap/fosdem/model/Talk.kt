package com.snap.fosdem.model

import java.util.*

data class Talk(
        val id: String = UUID.randomUUID().toString(),
        val title: String,
        val description: String,
        val track: String,
        val room: Room,
        val day: String,
        val start: String,
        val end: String
)
