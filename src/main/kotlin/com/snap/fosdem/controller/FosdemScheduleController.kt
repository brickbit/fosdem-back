package com.snap.fosdem.controller

import com.snap.fosdem.model.Stand
import com.snap.fosdem.model.Track
import com.snap.fosdem.model.Version
import com.snap.fosdem.service.FosdemScraper
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.time.Instant
import java.util.Date

@RestController
@RequestMapping("/api/v1/")
class FosdemScheduleController(
        private val scraper: FosdemScraper
) {

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
}