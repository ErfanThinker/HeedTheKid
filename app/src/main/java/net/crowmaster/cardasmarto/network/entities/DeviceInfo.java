package net.crowmaster.cardasmarto.network.entities;

import com.google.gson.JsonArray;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * This class will be converted to the root of json data used for posting the data to the cloud
 * The {@link net.crowmaster.cardasmarto.network.entities.ChildData} will be direct child node of this class in the json data
 * and each ChildData will contain a list of {@link net.crowmaster.cardasmarto.network.entities.TestRecord}s
 */
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
