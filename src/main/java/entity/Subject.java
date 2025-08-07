package entity;

public class Subject {
    private int id;
    private String subCode;
    private int duration;

    public Subject(int id, String subCode, int duration) {
        this.id = id;
        this.subCode = subCode;
        this.duration = duration;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getSubCode() {
        return subCode;
    }

    public void setSubCode(String subCode) {
        this.subCode = subCode;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }
}
