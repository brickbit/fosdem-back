package com.snap.fosdem.model

import java.util.*

data class Speaker(
        val id: String = UUID.randomUUID().toString(),
        val name: String,
        val image: String?,
        val description: String?,
)
