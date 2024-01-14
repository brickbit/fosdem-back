package com.snap.fosdem.model

import java.util.*

data class Room(
        val id: String = UUID.randomUUID().toString(),
        val name: String,
        val capacity: String,
        val building: Building,
        val location: String,
        val video: String,
        val chat: String,
)
