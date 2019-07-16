package net.crowmaster.cardasmarto.contracts;

/**
 * Created by root on 7/14/16.
 */
public class DBContract {

    public static final String DATABASE_NAME = "heed_the_kid.db";
    public static final int DATABASE_VERSION = 3;

    public class DataTable {
        public static final String TABLE_NAME = "SensorData";
        public static final String COLUMN_ID = "_id";
        public static final String COLUMN_CLIENT_TIME = "client_time";
        public static final String BATTERY_LVL = "battery_lvl";
        public static final String SYNCED = "synced";
        public static final String COLUMN_SERVER_TIME = "server_time";
        public static final String COLUMN_AC_X = "ac_x";
        public static final String COLUMN_AC_Y = "ac_y";
        public static final String COLUMN_AC_Z = "ac_z";
        public static final String COLUMN_ENCODER_1 = "encoder_1";
        public static final String COLUMN_ENCODER_2 = "encoder_2";
        public static final String COLUMN_SESSION_SERIAL = "session_serial";
        public static final String COLUMN_CHILD_AGE = "child_age";
        public static final String COLUMN_CHILD_NAME = "child_name";
        public static final String COLUMN_EMAIL = "child_email";
        public static final String COLUMN_PHONE = "child_phone";
        public static final String COLUMN_GENDER = "child_gender";
        public static final String COLUMN_AUTISM_RELATIVES = "child_has_autism_relatives";

    }

    public class DerivedColumns {
        public static final String COLUMN_SEVER_TIME_DURATION = "server_time_duration";
    }
}
