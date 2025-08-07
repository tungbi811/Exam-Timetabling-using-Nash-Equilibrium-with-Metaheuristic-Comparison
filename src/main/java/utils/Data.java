package utils;

import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvValidationException;
import entity.Invigilator;
import entity.Student;
import entity.Subject;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

public class Data {
    public int Nm; // Number of students
    public int Ni; // Number of invigilators
    public int Nr; // Number of rooms
    public int Ns; // Number of subjects
    public int Nd; // Number of days
    public int alpha; // Maximum number of students in each room
    public int beta; // Number of timeslot each day
    public int numberStudentMoreThanOneSubject;
    public int[][] A; // Student - Subject
    public int[] L; // Length of subject
    public int[][] C; // Subject - Invigilator
    public int[] Q; // Number of slot invigilator need to supervise
    public ArrayList<Student> students;
    public ArrayList<Invigilator> invigilators;
    public ArrayList<Subject> subjects;
    public int Nt;
    public int[] E;
    public int[] F;
    public int[] G;

    public Data() throws CsvValidationException, IOException {
        students = loadStudent();
        invigilators = loadInvigilator();
        subjects = loadSubject();
        Nm = students.size(); // 975;
        Ni = invigilators.size(); // 100;
        Ns = subjects.size(); // 50;
        Nr = 100; // 100;
        Nd = 9; // 7;
        this.alpha = 22;
        this.beta = 6;
        A = loadStudentSubject(Nm, Ns);
        L = loadSubjectLength(subjects);
        C = loadSubjectInvigilator(Ns, Ni);
        Q = loadInvigilatorQuota(invigilators);
        F = loadNumberStudentPerSubject();
        Nt = Nd * beta;

        E = new int[Nm];
        for (int m = 0; m < Nm; m++) {
            int numberOfSubjects = 0;
            for (int s = 0; s < Ns; s++) {
                numberOfSubjects += A[m][s];
            }
            E[m] = numberOfSubjects;
        }

        for (int m = 0; m < Nm; m++) {
            int numberSubjects = 0;
            if (E[m] > 1) {
                numberStudentMoreThanOneSubject += 1;
            }
        }

//        F = new int[Ns];
//        for (int s = 0; s < Ns; s++) {
//            int numberOfStudents = 0;
//            for (int m = 0; m < Nm; m++) {
//                numberOfStudents += A[m][s];
//            }
//            F[s] = numberOfStudents;
//        }

        G = new int[Ns];
        for (int s = 0; s < Ns; s++) {
            G[s] = Math.ceilDiv(F[s], alpha);
        }
    }

    private int[] loadNumberStudentPerSubject() throws IOException, CsvValidationException {
        int[] numberStudentPerSubject = new int[Ns];
        CSVReader numberStudentPerSubjectReader = readCSV("data/NumberStudentPerSubject.csv");
        String[] nextLine;
        for (int s = 0; s < Ns; s++) {
            nextLine = numberStudentPerSubjectReader.readNext();
            numberStudentPerSubject[s] = Integer.parseInt(nextLine[0]);
        }
        return numberStudentPerSubject;
    }

    public static CSVReader readCSV(String filePath) throws FileNotFoundException {
        FileReader fileReader = new FileReader(filePath);
        return new CSVReader(fileReader);
    }

    private int[][] loadStudentSubject(int nm, int ns) throws IOException, CsvValidationException {
        int[][] studentSubject = new int[nm][ns];
        CSVReader studentSubjectReader = readCSV("data/StudentSubject.csv");
        String[] nextLine;
        for (int m = 0; m < nm; m++) {
            nextLine = studentSubjectReader.readNext();
            for (int s = 0; s < ns; s++) {
                studentSubject[m][s] = Integer.parseInt(nextLine[s]);
            }
        }
//        int i =0;
//        while ((nextLine = studentSubjectReader.readNext()) != null) {
//            int j = 0;
//            for (String value : nextLine) {
//                studentSubject[i][j] = Integer.parseInt(value);
//                j += 1;
//            }
//            i += 1;
//        }
        return studentSubject;
    }

    private int[] loadSubjectLength(ArrayList<Subject> subjects) {
        int[] subjectLength = new int[subjects.size()];
        int durationPerTimeslot = 90;
        for (int i = 0; i < subjects.size(); i++) {
            subjectLength[i] = Math.ceilDiv(subjects.get(i).getDuration(), durationPerTimeslot);
        }
        return subjectLength;
    }

    private int[][] loadSubjectInvigilator(int ns, int ni) throws IOException, CsvValidationException {
        int[][] subjectInvigilator = new int[ns][ni];
        CSVReader subjectInvigilatorReader = readCSV("data/SubjectInvigilator.csv");
        String[] nextLine;
//        for (int s = 0; s < ns; s++) {
//            nextLine = subjectInvigilatorReader.readNext();
//            for (int i = 0; i < ni; i++) {
//                subjectInvigilator[s][i] = Integer.parseInt(nextLine[i]);
//            }
//        }
        int i = 0;
        while ((nextLine = subjectInvigilatorReader.readNext()) != null) {
            int j = 0;
            for (String value : nextLine) {
                subjectInvigilator[i][j] = Integer.parseInt(value);
                j += 1;
            }
            i += 1;
        }
        return subjectInvigilator;
    }

    private int[] loadInvigilatorQuota(ArrayList<Invigilator> invigilators) {
        int[] quota = new int[invigilators.size()];
        for (int i = 0; i < invigilators.size(); i++) {
            quota[i] = (int) Math.ceil(invigilators.get(i).getNumberOfClass() * 1.5);
        }
        return quota;
    }

    private ArrayList<Student> loadStudent() throws IOException, CsvValidationException {
        CSVReader studentReader = readCSV("data/Student.csv");
        studentReader.readNext(); // skip header

        ArrayList<Student> students = new ArrayList<>();
        String[] nextLine;
        while ((nextLine = studentReader.readNext()) != null) {
            int id = Integer.parseInt(nextLine[0]);
            String rollNumber = nextLine[1];
            String memberCode = nextLine[2];
            String email = nextLine[3];
            String fullName = nextLine[4];
            students.add(new Student(id, rollNumber, memberCode, email, fullName));
        }
        return students;
    }

    private ArrayList<Invigilator> loadInvigilator() throws IOException, CsvValidationException {
        CSVReader invigilatorReader = readCSV("data/Invigilator.csv");
        invigilatorReader.readNext(); // skip header

        ArrayList<Invigilator> invigilators = new ArrayList<>();
        String[] nextLine;
        while ((nextLine = invigilatorReader.readNext()) != null) {
            int id = Integer.parseInt(nextLine[0]);
            String code = nextLine[1];
            int numberOfClass = Integer.parseInt(nextLine[2]);
            invigilators.add(new Invigilator(id, code, numberOfClass));
        }
        return invigilators;
    }

    private ArrayList<Subject> loadSubject() throws IOException, CsvValidationException {
        CSVReader subjectReader = readCSV("data/Subject.csv");
        subjectReader.readNext(); // skip header

        ArrayList<Subject> subjects = new ArrayList<>();
        String[] nextLine;
        while ((nextLine = subjectReader.readNext()) != null) {
            int id = Integer.parseInt(nextLine[0]);
            String subCode = nextLine[1];
            int duration = Integer.parseInt(nextLine[2]);
            subjects.add(new Subject(id, subCode, duration));
        }
        return subjects;
    }
}
