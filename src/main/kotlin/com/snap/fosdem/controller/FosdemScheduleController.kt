package com.snap.fosdem.controller

import com.snap.fosdem.model.Stand
import com.snap.fosdem.model.Track
import com.snap.fosdem.service.FosdemScraper
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1/")
class FosdemScheduleController(
        private val scraper: FosdemScraper
) {

    @GetMapping("tracks/")
    fun getTracks(): List<Track> {
        return scraper.updateTracks()
    }

    @GetMapping("stand/")
    fun test2(): List<Stand> {
        return scraper.getStands()
    }
}