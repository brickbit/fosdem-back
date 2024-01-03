package com.snap.fosdem.model

data class Event(
        val day: String,
        val talk: Talk?,
        val speaker: Speaker?,
        val startHour: String,
        val startHourLink: String,
        val endHour: String,
        val endHourLink: String,
        val color: String?,
)
