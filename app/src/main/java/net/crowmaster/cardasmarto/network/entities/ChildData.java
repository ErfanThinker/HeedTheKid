package net.crowmaster.cardasmarto.network.entities;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class ChildData {

    public ChildData(String childName, String phone, String email, int childAge, boolean gender, boolean autismRelative, List<TestRecord> records) {
        this.childName = childName;
        this.phone = phone;
        this.email = email;
        this.childAge = childAge;
        this.gender = gender;
        this.autismRelative = autismRelative;
        this.records = records;
    }

    @SerializedName("child_name")
    private String childName;

    @SerializedName("phone")
    private String phone;

    @SerializedName("email")
    private String email;

    @SerializedName("child_age")
    private int childAge;

    @SerializedName("gender")
    private boolean gender;

    @SerializedName("autism_relative")
    private boolean autismRelative;

    @SerializedName("records")
    private List<TestRecord> records;
}
