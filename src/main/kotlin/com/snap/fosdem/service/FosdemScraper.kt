package com.snap.fosdem.service

import com.snap.fosdem.model.*
import org.jsoup.Jsoup
import org.springframework.stereotype.Service
import java.lang.Exception

@Service
class FosdemScraper {

    fun updateTracks(): List<Track> {
        val document = Jsoup.connect("https://fosdem.org/2024/schedule/").get()

        val tracks = document.select(
                "#main .container-fluid .row-fluid .span3"
        )[0].select("ul").map { it.select("li a") }.first()

        return tracks.map {
            Track(
                    name = it.text(),
                    events = getEventsForTrack(it.attr("href"))
            )
        }
    }

    fun getEventsForTrack(url: String): List<Event> {
        val document = Jsoup.connect("https://fosdem.org$url").get()
        val elements = document
                .select("#main table")
                .select(".table.table-striped.table-bordered.table-condensed tbody")[1].select("tr")

        val colors = elements.select("td").filter { node ->
            node.text() == ""
        }.map {
            it.className()
        }

        val day = elements.select("h3").text()
        val list = elements.select("tr a")
        val events = list.mapIndexed { index, element ->
            if(index < list.size - 3 && index % 4 == 0) {
                val indexForColor = if (index > 0) index / 4 else 0
                Event(
                    day = day,
                    talk = getTalks(list[index].attr("href")),
                    speaker = getSpeaker(list[index+1].attr("href")),
                    startHour = list[index+2].text(),
                    startHourLink = list[index+2].attr("href"),
                    endHour = list[index+3].text(),
                    endHourLink = list[index+3].attr("href"),
                    color = colors.getOrNull(indexForColor)
                )
            } else null
        }.filterNotNull()


        return events
    }

    fun getSpeaker(url: String): Speaker? {
        val document = Jsoup.connect("https://fosdem.org$url").get()
        try {
            return Speaker(
                    name = document.select("#main")[0].select("#pagetitles h1")[0].text(),
                    image = document.select("#main").getOrNull(0)?.select("img")?.getOrNull(0)?.attr("src")?.let { "https://fosdem.org$it" },
                    description = document.select("#main").getOrNull(0)?.select("p")?.joinToString("\n") {
                        it.text()
                    }
            )
        } catch (e: Exception) {
            return null
        }
    }

    fun getTalks(url: String): Talk? {
        val document = Jsoup.connect("https://fosdem.org$url").get()

        return try {
            Talk(
                    title = document.select("#main")[0].select("#pagetitles h1").text(),
                    description = document.select("#main")[0].select(".event-blurb p").text(),
                    track = document.select("#main")[0].select(".side-box a")[0].text(),
                    room = getRooms(document.select("#main")[0].select(".side-box a")[1].attr("href")),
                    day = document.select("#main")[0].select(".side-box a")[2].text(),
                    start = document.select("#main")[0].select(".side-box a")[3].text(),
                    end = document.select("#main")[0].select(".side-box a")[4].text()
            )
        } catch (e: Exception) {
            return null
        }

    }

    fun getRooms(url: String): Room {
        val document = Jsoup.connect("https://fosdem.org$url").get()

        return Room(
                name = document.select("#main")[0].select("#pagetitles").text(),
                capacity = document.select("#main")[0].select(".side-box")[0].select("li")[0].text().removeSuffix("Capacity: "),
                building = getBuildings(
                        url = document.select("#main")[0].select(".side-box")[0].select("li a")[0].attr("href"),
                        name = document.select("#main")[0].select("#pagetitles").text()
                ),
                location = document.select("#main")[0].select(".side-box")[0].select("li a")[1].attr("href"),
                video = document.select("#main")[0].select(".side-box")[0].select("li a")[2].attr("href"),
                chat = document.select("#main")[0].select(".side-box")[0].select("li a")[3].attr("href")
        )
    }

    fun getBuildings(url: String, name: String): Building {
        val document = Jsoup.connect("https://fosdem.org$url").get()
        val image = document.select(".map img").attr("src")
        val onlineBuildings = document.select(".table.table-striped.table-bordered.table-condensed")[1].select("tbody a").map {
            it.attr("id")
        }
        return Building(
                name = name,
                online = if(onlineBuildings.firstOrNull { it.equals(name[0].toString(), ignoreCase = true) } != null) true else false,
                map = "https://fosdem.org$image"
        )
    }
}