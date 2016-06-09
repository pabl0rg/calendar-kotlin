package com.example

import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.module.kotlin.*
import okhttp3.*
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

/**
 * Uses the google api in a very nuts-and-bolts fashion using only the okhttp library
 */
object HomeMade {
    val CLIENT_ID = ""
    val CLIENT_SECRET = ""
    val REFRESH_TOKEN = ""

    var accessToken: String? = null
    var accessTokenExpires: ZonedDateTime = ZonedDateTime.now().minusDays(1)

    val JACKSON = jacksonObjectMapper()
    val JSON = MediaType.parse("application/json; charset=utf-8")
    val client = OkHttpClient()

    init {
        JACKSON.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
    }

    fun genAccessToken(): String {
        if (ZonedDateTime.now().isAfter(accessTokenExpires)) {
            val parameters = mapOf("client_secret" to CLIENT_SECRET,
                    "grant_type" to "refresh_token",
                    "refresh_token" to REFRESH_TOKEN,
                    "client_id" to CLIENT_ID)
            val response = apiCall("/oauth2/v4/token", parameters, BodyFormat.FORM, false)
            val json = response.body().string()
            val tokenInfo: TokenInfo = JACKSON.readValue(json)
            accessTokenExpires = ZonedDateTime.now().plusSeconds(tokenInfo.expires_in)
            accessToken = tokenInfo.token_type + " " + tokenInfo.access_token
        }
        return accessToken!!
    }

    private enum class BodyFormat { JSON, FORM }

    private fun getPostBody(payload: Map<String, String>, format: BodyFormat): RequestBody {
        if (format == BodyFormat.FORM) {
            val builder = FormBody.Builder()
            payload.forEach { builder.add(it.key, it.value) }
            return builder.build()
        }
        return RequestBody.create(JSON, JACKSON.writeValueAsString(payload))
    }

    private fun apiCall(path: String, payload: Map<String, String>? = null, format: BodyFormat = BodyFormat.JSON,
                        addAuthHeader: Boolean = true): Response {
        //println("https://www.googleapis.com" + path)
        val builder = Request.Builder().url("https://www.googleapis.com" + path)
        if (payload != null) {
            builder.post(getPostBody(payload, format))
        }
        if (addAuthHeader) {
            builder.header("Authorization", genAccessToken())
        }
        return client.newCall(builder.build()).execute()
    }

    fun getCalendars(): List<String> {
        println("getting calendar list")
        val response = apiCall("/calendar/v3/users/me/calendarList")
        val json = response.body().string()
        val calendarList: CalendarList = JACKSON.readValue(json)
        return calendarList.items.map { it.id }
    }

    fun getEvents(fromDate: ZonedDateTime, toDate: ZonedDateTime): List<Event> {
        val from = fromDate.format(DateTimeFormatter.ISO_OFFSET_DATE_TIME)
        val to = toDate.format(DateTimeFormatter.ISO_OFFSET_DATE_TIME)
        val calendarId = getCalendars().first()

        println("getting events from $calendarId between $from and $to")

        val response = apiCall("/calendar/v3/calendars/$calendarId/events?timeMin=$from&timeMax=$to")
        val json = response.body().string()
        val events: EventList = JACKSON.readValue(json)
        return events.items
    }
}