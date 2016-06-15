package warszawascala.simple

import com.google.api.client.auth.oauth2.Credential
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport
import com.google.api.client.http.HttpTransport
import com.google.api.client.json.jackson2.JacksonFactory
import com.google.api.client.util.store.FileDataStoreFactory
import com.google.api.client.util.DateTime

import com.google.api.services.calendar.CalendarScopes

import java.io.IOException
import java.io.InputStreamReader
import java.util.Arrays

object CalendarQuickstart {
    /** Application name.  */
    private val APPLICATION_NAME = "Google Calendar API Java Quickstart"

    /** Directory to store user credentials for this application.  */
    private val DATA_STORE_DIR = java.io.File(
            System.getProperty("user.home"), ".credentials/calendar-java-quickstart.json")

    /** Global instance of the [FileDataStoreFactory].  */
    private var DATA_STORE_FACTORY: FileDataStoreFactory? = null

    /** Global instance of the JSON factory.  */
    private val JSON_FACTORY = JacksonFactory.getDefaultInstance()

    /** Global instance of the HTTP transport.  */
    private var HTTP_TRANSPORT: HttpTransport? = null

    /** Global instance of the scopes required by this quickstart.

     * If modifying these scopes, delete your previously saved credentials
     * at ~/.credentials/calendar-java-quickstart.json
     */
    private val SCOPES = Arrays.asList(CalendarScopes.CALENDAR_READONLY)

    init {
        try {
            HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport()
            DATA_STORE_FACTORY = FileDataStoreFactory(DATA_STORE_DIR)
        } catch (t: Throwable) {
            t.printStackTrace()
            System.exit(1)
        }

    }

    /**
     * Creates an authorized Credential object.
     * @return an authorized Credential object.
     * *
     * @throws IOException
     */
    @Throws(IOException::class)
    fun authorize(): Credential {
        // Load client secrets.
        val `in` = CalendarQuickstart::class.java.getResourceAsStream("/client_secret.json")
        val clientSecrets = GoogleClientSecrets.load(JSON_FACTORY, InputStreamReader(`in`))

        // Build flow and trigger user authorization request.
        val flow = GoogleAuthorizationCodeFlow.Builder(
                HTTP_TRANSPORT, JSON_FACTORY, clientSecrets, SCOPES).setDataStoreFactory(DATA_STORE_FACTORY!!).setAccessType("offline").build()
        val credential = AuthorizationCodeInstalledApp(
                flow, LocalServerReceiver()).authorize("user")
        println("Credentials saved to " + DATA_STORE_DIR.absolutePath)
        return credential
    }

    /**
     * Build and return an authorized Calendar client service.
     * @return an authorized Calendar client service
     * *
     * @throws IOException
     */
    val calendarService: com.google.api.services.calendar.Calendar
        @Throws(IOException::class)
        get() {
            val credential = authorize()
            return com.google.api.services.calendar.Calendar.Builder(
                    HTTP_TRANSPORT!!, JSON_FACTORY, credential).setApplicationName(APPLICATION_NAME).build()
        }

    @Throws(IOException::class)
    @JvmStatic fun main(args: Array<String>) {
        // Build a new authorized API client service.
        // Note: Do not confuse this class with the
        //   com.google.api.services.calendar.model.Calendar class.
        val service = calendarService

        // List the next 10 events from the primary calendar.
        val now = DateTime(System.currentTimeMillis())
        val events = service.events().list("primary").setMaxResults(10).setTimeMin(now).setOrderBy("startTime").setSingleEvents(true).execute()
        val items = events.items
        if (items.size == 0) {
            println("No upcoming events found.")
        } else {
            println("Upcoming events")
            for (event in items) {
                var start: DateTime? = event.start.dateTime
                if (start == null) {
                    start = event.start.date
                }
                System.out.printf("%s (%s)\n", event.summary, start)
            }
        }
    }

}