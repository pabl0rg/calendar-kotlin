package warszawascala

import warszawascala.simple.*
import java.time.ZonedDateTime

fun main(args: Array<String>) {

    val googleApiClient = GoogleApiClient("CLIENT_ID", "CLIENT_SECRET", "REFRESH_TOKEN")
    val calendarApiClient = GoogleCalendarApi(googleApiClient)
    do {
        println("Menu:\n1)refresh access token\n2)list calendars\n3)get events for next year")
        val input = readLine()!!.trim().toInt()
        when(input) {
            1 -> println(googleApiClient.genAccessToken())
            2 -> println(calendarApiClient.getCalendars())
            3 -> println(calendarApiClient.getEvents(ZonedDateTime.now(), ZonedDateTime.now().plusDays(365)))
        }
    } while (true)
}
