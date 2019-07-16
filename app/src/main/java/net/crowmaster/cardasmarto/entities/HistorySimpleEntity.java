package net.crowmaster.cardasmarto.entities;

/**
 * Created by root on 7/16/16.
 */
public class HistorySimpleEntity {
    private long sessionSerial;
    private String childName;
    private long duration;
    public long getSessionSerial() {
        return sessionSerial;
    }

    public String getchildName() {
        return childName;
    }

    public long getDuration() {
        return duration;
    }




    public HistorySimpleEntity(long sessionSerial, String childName, long duration) {
        this.sessionSerial = sessionSerial;
        this.childName = childName;
        this.duration = duration;
    }
}
