package com.snap.fosdem.model

data class Room(
        val name: String,
        val capacity: String,
        val building: Building,
        val location: String,
        val video: String,
        val chat: String,
)
