package com.snap.fosdem.service

import com.snap.fosdem.model.*
import org.jsoup.Jsoup
import org.springframework.cache.annotation.Cacheable
import org.springframework.stereotype.Service
import java.lang.Exception

@Service
class FosdemScraper {
    @Cacheable(value= arrayOf("tracks"))
    fun updateTracks(): List<Track> {
        val document = Jsoup.connect("https://fosdem.org/2024/schedule/").get()

        val tracksElements = document.select(
            "#main .container-fluid .row-fluid .span3"
        )
        val tracks1 = tracksElements[0].select("ul").map { it.select("li a") }.first()
        val tracks2 = tracksElements[1].select("ul").map { it.select("li a") }.first()
        val tracks3 = tracksElements[2].select("ul").map { it.select("li a") }.first()
        val tracks4 = tracksElements[3].select("ul").map { it.select("li a") }.first()

        val tracks = tracks1 + tracks2 + tracks3 + tracks4

        return tracks.map {
            Track(
                    name = it.text(),
                    events = getEventsForTrack(it.attr("href")),
                    stands = getStands()
            )
        }
    }

    fun getStands(): List<Stand> {
        val document = Jsoup.connect("https://fosdem.org/2024/stands/").get()

        val standElements = document.select(
            "#main .container-fluid .row-fluid"
        )
        val title = standElements.select("h3").map { it.text() }
        val image = standElements.select("img").map {  it.attr("src") }

        val features: MutableList<List<StandFeatures>> = mutableListOf()
        features.add(getStandsFeature(1)+getStandsFeature(2)+getStandsFeature(3))
        features.add(getStandsFeature(5))
        features.add(getStandsFeature(7))
        features.add(getStandsFeature(9))

        val stands = title.mapIndexed { index, item ->
            Stand(title = title[index], image = image[index], features = features[index].toList())
        }
        return stands
    }

    private fun getStandsFeature(index: Int): List<StandFeatures> {
        val document = Jsoup.connect("https://fosdem.org/2024/stands/").get()
        val features = mutableListOf<StandFeatures>()
        val standElements = document.select(
            "#main .container-fluid .row-fluid"
        )

        val subtitles = standElements[index].select("h4").text()

        standElements[index].select("tr").select("td").mapIndexed { index, item ->
            if(item.select("h5").text().isNotEmpty()){
                features.add(StandFeatures(type = item.select("h5").text(), subtitle = subtitles, companies = mutableListOf()))
            } else {
                features[features.size - 1].companies.add(item.select("td").text())
            }
        }
        features.map { feature -> feature.companies.removeIf { it.all { char -> char.isDigit() } } }
        return features.toList()
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

        val startHoursList = mutableListOf<String>()
        val endHoursList = mutableListOf<String>()
        val speakerLinkList: MutableList<MutableList<String>> = mutableListOf()
        val talkLinkList = mutableListOf<String>()

        val list = elements.select("tr td")
        val newlist = list.map { it }.toMutableList()
        newlist.removeFirst()
        newlist.removeIf { it.select("h3").text() == "Sunday" }

        newlist.mapIndexed { index, item ->
            if(url != "/2024/schedule/track/educational/") {
                if (index % 5 == 0 && index < list.size - 4) {
                    talkLinkList.add(newlist[index + 1].select("a").attr("href"))
                    val speakerItems = newlist[index + 2].select("a").map {
                        it.attr("href")
                    }
                    speakerLinkList.add(speakerItems.toMutableList())
                    startHoursList.add(newlist[index + 3].text())
                    endHoursList.add(newlist[index + 4].text())
                }
            } else {
                if (index % 6 == 0 && index < list.size - 5) {
                    talkLinkList.add(newlist[index + 1].select("a").attr("href"))
                    val speakerItems = newlist[index + 2].select("a").map {
                        it.attr("href")
                    }
                    speakerLinkList.add(speakerItems.toMutableList())
                    startHoursList.add(newlist[index + 4].text())
                    endHoursList.add(newlist[index + 5].text())
                }
            }
        }

        val indexForColor = mutableListOf<String?>()
        list.mapIndexed { index, element ->
            indexForColor.add(colors.getOrNull(if (index > 0) index / 4 else 0))
        }


        val events = startHoursList.mapIndexed { index, item ->
            Event(
                day = day,
                speaker = speakerLinkList[index].map { getSpeakers(it) },
                talk = getTalks(talkLinkList[index]),
                startHour = startHoursList[index],
                endHour = endHoursList[index],
                color = indexForColor[index]
            )
        }

        return events
    }

    fun getSpeakers(url: String): Speaker {
        val document = Jsoup.connect("https://fosdem.org$url").get()
        return Speaker(
                name = document.select("#main")[0].select("#pagetitles h1")[0].text(),
                image = document.select("#main").getOrNull(0)?.select("img")?.getOrNull(0)?.attr("src")?.let { "https://fosdem.org$it" },
                description = document.select("#main").getOrNull(0)?.select("p")?.joinToString("\n") {
                    it.text()
                }
        )
    }


    fun getTalks(url: String): Talk {
        val document = Jsoup.connect("https://fosdem.org$url").get()

        return Talk(
                title = document.select("#main")[0].select("#pagetitles h1").text(),
                description = document.select("#main")[0].select(".event-blurb p").text(),
                track = document.select("#main")[0].select(".side-box a")[0].text(),
                room = getRooms(document.select("#main")[0].select(".side-box a")[1].attr("href")),
                day = document.select("#main")[0].select(".side-box a")[2].text(),
                start = document.select("#main")[0].select(".side-box a")[3].text(),
                end = document.select("#main")[0].select(".side-box a")[4].text()
        )
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