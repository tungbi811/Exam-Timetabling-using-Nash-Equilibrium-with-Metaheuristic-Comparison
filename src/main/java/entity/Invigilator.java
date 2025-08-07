package entity;

public class Invigilator {
    private int id;
    private String code;
    private int numberOfClass;

    public Invigilator(int id, String code, int numberOfClass) {
        this.id = id;
        this.code = code;
        this.numberOfClass = numberOfClass;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public int getNumberOfClass() {
        return numberOfClass;
    }

    public void setNumberOfClass(int numberOfClass) {
        this.numberOfClass = numberOfClass;
    }
}
