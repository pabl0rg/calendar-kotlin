package warszawascala.simple

import com.fasterxml.jackson.module.kotlin.*
import warszawascala.*
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

class GoogleCalendarApi(val apiClient: GoogleApiClient) {
    /**
     * Returns a list of calendar id's
     * TODO: return a Future
     */
    fun getCalendars(): List<String> {
        println("getting calendar list")
        val response = apiClient.call("/calendar/v3/users/me/calendarList")
        val json = response.body().string()
        val calendarList: CalendarList = apiClient.JACKSON.readValue(json)
        return calendarList.items.map { it.id }
    }

    /**
     * Returns a list of events between two dates
     * TODO: return a Future
     */
    fun getEvents(fromDate: ZonedDateTime, toDate: ZonedDateTime): List<Event> {
        val from = fromDate.format(DateTimeFormatter.ISO_OFFSET_DATE_TIME)
        val to = toDate.format(DateTimeFormatter.ISO_OFFSET_DATE_TIME)
        val calendarId = getCalendars().first()

        println("getting events from $calendarId between $from and $to")

        val response = apiClient.call("/calendar/v3/calendars/$calendarId/events?timeMin=$from&timeMax=$to")
        val json = response.body().string()
        val events: EventList = apiClient.JACKSON.readValue(json)
        return events.items
    }

}