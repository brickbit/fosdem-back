package com.snap.fosdem.model

import java.util.*

data class Event(
        val id: String = UUID.randomUUID().toString(),
        val day: String,
        val talk: Talk,
        val speaker: List<Speaker>,
        val startHour: String,
        val endHour: String,
        val color: String?,
)
