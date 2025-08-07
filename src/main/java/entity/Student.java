package entity;

public class Student {
    private int id;
    private String rollNumber;
    private String memberCode;
    private String email;
    private String fullName;

    public Student(int id, String rollNumber, String memberCode, String email, String fullName) {
        this.id = id;
        this.rollNumber = rollNumber;
        this.memberCode = memberCode;
        this.email = email;
        this.fullName = fullName;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getRollNumber() {
        return rollNumber;
    }

    public void setRollNumber(String rollNumber) {
        this.rollNumber = rollNumber;
    }

    public String getMemberCode() {
        return memberCode;
    }

    public void setMemberCode(String memberCode) {
        this.memberCode = memberCode;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }
}
