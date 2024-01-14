package com.snap.fosdem.model

import java.util.*

data class Event(
        val id: String = UUID.randomUUID().toString(),
        val day: String,
        val talk: Talk?,
        val speaker: Speaker?,
        val startHour: String,
        val startHourLink: String,
        val endHour: String,
        val endHourLink: String,
        val color: String?,
)
