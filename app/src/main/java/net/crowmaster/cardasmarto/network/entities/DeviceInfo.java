package net.crowmaster.cardasmarto.network.entities;

import com.google.gson.JsonArray;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class DeviceInfo {

    public DeviceInfo(String firebaseToken, String uuid, String os, String osVersion, List<ChildData> data) {
        this.firebaseToken = firebaseToken;
        this.uuid = uuid;
        this.os = os;
        this.osVersion = osVersion;
        this.data = data;
    }

    @SerializedName("firebase_token")
    private String firebaseToken;

    @SerializedName("uuid")
    private String uuid;

    @SerializedName("operating_system")
    private String os;

    @SerializedName("os_version")
    private String osVersion;

    @SerializedName("data")
    private List<ChildData> data;




}
