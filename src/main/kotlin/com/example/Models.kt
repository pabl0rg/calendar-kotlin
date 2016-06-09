package com.example

data class TokenInfo(val access_token: String, val token_type: String, val expires_in: Long)

data class CalendarList(val kind: String, val items: List<CalendarListItem>)

data class CalendarListItem(val id: String, val summary: String)

data class EventList(val items: List<Event>)

data class Event(val summary: String, /*val description: String,*/ val start: EventDate, val end: EventDate)

data class EventDate(val dateTime: String)