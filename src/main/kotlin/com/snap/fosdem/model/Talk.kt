package com.snap.fosdem.model

data class Talk(
        val title: String,
        val description: String,
        val track: String,
        val room: Room,
        val day: String,
        val start: String,
        val end: String
)
