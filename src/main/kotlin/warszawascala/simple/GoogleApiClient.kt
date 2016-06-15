package warszawascala.simple

import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.module.kotlin.*
import okhttp3.*
import warszawascala.TokenInfo
import java.time.ZonedDateTime

/**
 * Use some other means to obtain a refresh token
 * - the google api quickstart https://developers.google.com/google-apps/calendar/quickstart/java
 */
class GoogleApiClient(val clientId: String, val clientSecret: String, val refreshToken: String) {
    internal var accessToken: String? = null
    internal var accessTokenExpires = ZonedDateTime.now().minusDays(1)

    private val httpClient = OkHttpClient()

    internal enum class BodyFormat { JSON, FORM }

    /**
     * Generates a new accessToken if the old one is expired
     */
    fun genAccessToken(): String {
        if (ZonedDateTime.now().isAfter(accessTokenExpires)) {
            val parameters = mapOf("client_secret" to clientSecret,
                    "grant_type" to "refresh_token",
                    "refresh_token" to refreshToken,
                    "client_id" to clientId)
            val response = call("/oauth2/v4/token", parameters, BodyFormat.FORM, false)
            val json = response.body().string()
            val tokenInfo: TokenInfo = JACKSON.readValue(json)
            accessTokenExpires = ZonedDateTime.now().plusSeconds(tokenInfo.expires_in)
            accessToken = tokenInfo.token_type + " " + tokenInfo.access_token
        }
        return accessToken!!
    }

    private fun buildPostBody(payload: Map<String, String>, format: BodyFormat): RequestBody {
        if (format == BodyFormat.FORM) {
            val builder = FormBody.Builder()
            payload.forEach { builder.add(it.key, it.value) }
            return builder.build()
        }
        return RequestBody.create(Companion.JSON, JACKSON.writeValueAsString(payload))
    }

    /**
     * Makes (blocking) call to google and returns Response
     */
    internal fun call(path: String, payload: Map<String, String>? = null, format: BodyFormat = BodyFormat.JSON,
                      addAuthHeader: Boolean = true): Response {
        //println("https://www.googleapis.com" + path)
        val builder = Request.Builder().url("https://www.googleapis.com" + path)
        if (payload != null) {
            builder.post(buildPostBody(payload, format))
        }
        if (addAuthHeader) {
            builder.header("Authorization", genAccessToken())
        }
        return httpClient.newCall(builder.build()).execute()
    }

    companion object {
        internal val JACKSON = jacksonObjectMapper()
        private val JSON = MediaType.parse("application/json; charset=utf-8")
        init {
            JACKSON.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
        }
    }
}