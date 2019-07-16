package net.crowmaster.cardasmarto.network.entities;

import com.google.gson.annotations.SerializedName;

public class TestRecord {

    public TestRecord(long id, long acX, long acY, long acZ, long battery, long encoder1, long encoder2, long carTime, long serverTime, long sessionId) {
        this.id = id;
        this.acX = acX;
        this.acY = acY;
        this.acZ = acZ;
        this.battery = battery;
        this.encoder1 = encoder1;
        this.encoder2 = encoder2;
        this.carTime = carTime;
        this.serverTime = serverTime;
        this.sessionId = sessionId;
    }

    @SerializedName("id")
    private long id;

    @SerializedName("ac_x")
    private long acX;

    @SerializedName("ac_y")
    private long acY;

    @SerializedName("ac_z")
    private long acZ;

    @SerializedName("battery")
    private long battery;

    @SerializedName("encoder1")
    private long encoder1;

    @SerializedName("encoder2")
    private long encoder2;

    @SerializedName("car_time")
    private long carTime;

    @SerializedName("universal_time")
    private long serverTime;

    @SerializedName("session_id")
    private long sessionId;

}
