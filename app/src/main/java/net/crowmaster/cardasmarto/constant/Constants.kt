package net.crowmaster.cardasmarto.constant

/**
 * Constants used as keys for reading data to/from the SharedPreferences regarding saved user data
 */
class Constants {
    companion object {
        const val SP_CHILD_NAME = "childName"
        const val SP_CHILD_AGE = "childAge"
        const val SP_PHONE = "phone"
        const val SP_EMAIL = "email"
        const val SP_CHILD_GENDER = "gender"
        const val SP_AUTISM_RELATIVES = "autism_relative"
        //This session serial contains the time in millis at the beginning of each record session
        const val SP_SESSION_SERIAL = "COLUMN_SESSION_SERIAL"
        const val SP_NAME__SERVER_STATUS = "server_status"
    }

}
