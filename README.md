### About the project

This is an attempt to demonstrate how the Warszawa Scala users group could implement a google calendar api
client in Kotlin

It leaves out the most complicated part, which is getting the OAuth2 refresh token.  It was decided that another
part of the system would do this.  See #4 below.

### How to edit/run this project in intellij

1. Install the kotlin plugin for intellij
2. Install the kobalt plugin for intellij
3. Import the project as a kobalt project into intellij
4. Use some other means (eg. the google api quickstart) to obtain a refresh token
     https://developers.google.com/google-apps/calendar/quickstart/java
5. Update these vals in HomeMade.kt with info from step 4
    - CLIENT_ID
    - CLIENT_SECRET
    - REFRESH_TOKEN
6. Run
