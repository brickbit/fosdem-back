package com.snap.fosdem.model

import java.util.*

data class Building(
        val id: String = UUID.randomUUID().toString(),
        val name: String,
        val online: Boolean,
        val map: String,
)
