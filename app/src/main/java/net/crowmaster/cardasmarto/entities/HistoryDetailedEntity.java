package net.crowmaster.cardasmarto.entities;

public class HistoryDetailedEntity {

    public HistoryDetailedEntity(long clientTime, long serverTime, long batteryLvl,
                                 long acX, long acY, long acZ, long encoder1, long encoder2) {
        this.clientTime = clientTime;
        this.serverTime = serverTime;
        this.batteryLvl = batteryLvl;
        this.acX = acX;
        this.acY = acY;
        this.acZ = acZ;
        this.encoder1 = encoder1;
        this.encoder2 = encoder2;
    }

    public HistoryDetailedEntity(long id, long clientTime, long serverTime, long batteryLvl,
                                 long acX, long acY, long acZ, long encoder1, long encoder2,
                                 String name, String phone, String email, boolean autismRelatives,
                                 boolean gender, long sessionSerial, int age) {
        this.id = id;
        this.clientTime = clientTime;
        this.serverTime = serverTime;
        this.batteryLvl = batteryLvl;
        this.acX = acX;
        this.acY = acY;
        this.acZ = acZ;
        this.encoder1 = encoder1;
        this.encoder2 = encoder2;
        this.childName = name == null ? "" : name;
        this.phone = phone == null ? "" : phone;
        this.email = email == null ? "" : email;
        this.hasAutismRelatives = autismRelatives;
        this.gender = gender;
        this.sessionSerial = sessionSerial;
        this.age = age;
    }

    public long getClientTime() {
        return clientTime;
    }

    public long getServerTime() {
        return serverTime;
    }

    public long getBatteryLvl() {
        return batteryLvl;
    }

    public long getAcX() {
        return acX;
    }

    public long getAcY() {
        return acY;
    }

    public long getAcZ() {
        return acZ;
    }

    public long getEncoder1() {
        return encoder1;
    }

    public long getEncoder2() {
        return encoder2;
    }

    public String getChildName() {
        return childName;
    }

    public String getPhone() {
        return phone;
    }

    public String getEmail() {
        return email;
    }

    public long getSessionSerial() {
        return sessionSerial;
    }

    public boolean hasAutismRelatives() {
        return hasAutismRelatives;
    }

    public boolean isMale() {
        return gender;
    }

    public long getId() {
        return id;
    }

    public int getAge() {
        return age;
    }

    private long id;
    private long clientTime;//time in the car
    private long serverTime;//time by the phone
    private long batteryLvl;
    private long acX;
    private long acY;
    private long acZ;
    private long encoder1;
    private long encoder2;
    private String childName;
    private String phone;
    private String email;
    private int age;
    private long sessionSerial;
    private boolean hasAutismRelatives; //true for positive, false for negative
    private boolean gender; //true for male, false for female


}
