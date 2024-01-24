package com.snap.fosdem.controller

import com.snap.fosdem.model.Stand
import com.snap.fosdem.model.Track
import com.snap.fosdem.model.Version
import com.snap.fosdem.service.FosdemScraper
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.core.io.ResourceLoader
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.io.File
import java.time.Instant
import java.util.*

@RestController
@RequestMapping("/api/v1/")
class FosdemScheduleController(
        private val scraper: FosdemScraper
) {
    @Autowired
    lateinit var resourceLoader: ResourceLoader

    @GetMapping("tracks/")
    fun getTracks(): List<Track> {
        return scraper.updateTracks()
    }

    @GetMapping("stands/")
    fun getStands(): List<Stand> {
        return scraper.getStands()
    }

    @GetMapping("version/")
    fun getVersion() : Version {
        return Version(
            version = "0.0.1",
            date = Date.from(Instant.now()).toString()
        )
    }

    @GetMapping("policy/")
    fun getPrivacyPolicy(): String {
        val resource = resourceLoader.getResource("classpath:policy.html")
        val file: File = resource.file

        return file.readText()
    }
}