package net.crowmaster.cardasmarto.constant

/**
 * Constants used as keys for reading data from the incoming packets from the car and saving the states of the app in SharedPreferences
 */
class Constants {
    companion object {
        const val KEY_FB_TOKEN = "firebase_token"
        const val SP_CHILD_NAME = "childName"
        const val SP_CHILD_AGE = "childAge"
        const val SP_PHONE = "phone"
        const val SP_EMAIL = "email"
        const val SP_CHILD_GENDER = "gender"
        const val SP_AUTISM_RELATIVES = "autism_relative"
        const val SP_SESSION_SERIAL = "COLUMN_SESSION_SERIAL"
        const val SP_NAME__SERVER_STATUS = "server_status"
    }

}
