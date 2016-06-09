package com.example

import java.time.ZonedDateTime

fun main(args: Array<String>) {
    do {
        println("Menu:\n1)refresh access token\n2)list calendars\n3)get events for next two months")
        val input = readLine()!!.trim().toInt()
        when(input) {
            1 -> println(HomeMade.genAccessToken())
            2 -> println(HomeMade.getCalendars())
            3 -> println(HomeMade.getEvents(ZonedDateTime.now(), ZonedDateTime.now().plusDays(365)))
        }
    } while (true)
}
