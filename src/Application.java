
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.Date;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Random;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collector;
import java.util.stream.Collectors;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author Fadejimi
 */
public class Application {
    private int N = 10000000;
    private List<Student> studentList;
    private Random random = new Random();
    private final static int PARALLEL = 1;
    private final static int SERIAL = 0;
    
    public Application() {
        init();
        serial();
        parallel();
    }
    
    public static void main(String[] args) {
        new Application();
    }
    
    private void init() {
        int year = 2017;
        studentList = new ArrayList<>();
        for (int i = 0; i < N; i++) {
            int studentId = random.nextInt(1000);
            int deptId = random.nextInt(10);
            int courseNumber = random.nextInt(199);
            int credits = random.nextInt(5);
            int grade = random.nextInt(5);
            int month = random.nextInt(11) + 1;
            
            LocalDate ldate = LocalDate.of(year, month, 1);
            Date date = Date.from(ldate.atStartOfDay(ZoneId.systemDefault()).toInstant());
            
            studentList.add(new Student(studentId, deptId, courseNumber, date, 
                    credits, grade));
        }
        
        //studentList.forEach(Student::toString);
    }
    
    private void serial() {
        System.out.println("Serial String Evaluation\n");
        Date start, end, begin, finished;
        begin = new Date();
        start = new Date();
        Map<Integer, Double> gpa = getStudentGpa(SERIAL);
        end = new Date();
        System.out.println(".............................................");
        System.out.println("Average Gpa Time is : " + 
                (end.getTime() - start.getTime()));
        start = new Date();
        Student lowGpa = getMinStudentGpa(SERIAL);
        end = new Date();
        System.out.println("Low Gpa Time is : " + 
                (end.getTime() - start.getTime()));
        start = new Date();
        Student maxGpa = getMaxStudentGpa(SERIAL);
        end = new Date();
        System.out.println("Max Gpa Time is : " + 
                (end.getTime() - start.getTime()));
        start = new Date();
        Map<Integer, Double> avgGpa = getGradeAverageGpa(SERIAL);
        end = new Date();
        System.out.println("Average Grade Gpa Time is : " + 
                (end.getTime() - start.getTime()));
        start = new Date();
        Map<Integer, Double> avgCourseGpa = getCourseAverageGpa(SERIAL);
        end = new Date();
        System.out.println("Average Course Gpa Time is : " + 
                (end.getTime() - start.getTime()));
        finished = new Date();
        System.out.println("........................................");
        System.out.println();
        System.out.println("Evaluation time for the serial stream is : " + 
                (finished.getTime() - begin.getTime()));
    }
    
    private void parallel() {
        System.out.println("Parallel String Evaluation\n");
        Date start, end, begin, finished;
        begin = new Date();
        start = new Date();
        Map<Integer, Double> gpa = getStudentGpa(PARALLEL);
        end = new Date();
        System.out.println(".............................................");
        System.out.println("Average Gpa Time is : " + 
                (end.getTime() - start.getTime()));
        start = new Date();
        Student lowGpa = getMinStudentGpa(PARALLEL);
        end = new Date();
        System.out.println("Low Gpa Time is : " + 
                (end.getTime() - start.getTime()));
        start = new Date();
        Student maxGpa = getMaxStudentGpa(PARALLEL);
        end = new Date();
        System.out.println("Max Gpa Time is : " + 
                (end.getTime() - start.getTime()));
        start = new Date();
        Map<Integer, Double> avgGpa = getGradeAverageGpa(PARALLEL);
        end = new Date();
        System.out.println("Average Grade Gpa Time is : " + 
                (end.getTime() - start.getTime()));
        start = new Date();
        Map<Integer, Double> avgCourseGpa = getCourseAverageGpa(PARALLEL);
        end = new Date();
        System.out.println("Average Course Gpa Time is : " + 
                (end.getTime() - start.getTime()));
        finished = new Date();
        System.out.println("........................................");
        System.out.println();
        System.out.println("Evaluation time for the parallel stream is : " + 
                (finished.getTime() - begin.getTime()));
    }
    
    private Map<Integer,Double> getStudentGpa(int flag) {
        Map<Integer, Double> gpa = new HashMap<>();
        
        if (flag == SERIAL) {
            
            gpa = studentList.stream()
                    .collect(Collectors.groupingBy(Student::getStudentId,
                            //Collectors.reducing(Student::getStudentId, )
                            Collectors.averagingInt(Student::getGrade)));            
            
            //gpa.forEach((s, g) -> System.out.println(s + " gpa is  " + g));
        }
        else {
            gpa = studentList.parallelStream()
                      .collect(Collectors.groupingBy(Student::getStudentId,
                              Collectors.averagingInt(Student::getGrade)));
        }
        
        return gpa;
    }
    
    public Student getMinStudentGpa(int flag) {
        Student student;
        
        if (flag == SERIAL) {
            student = studentList.stream()
                    .min(Comparator.comparing(Student::getGrade))
                    .orElseThrow(NoSuchElementException::new);
                    
        } 
        else {
            student = studentList.parallelStream()
                    .min(Comparator.comparing(Student::getGrade))
                    .orElseThrow(NoSuchElementException::new);
        }
        
        return student;
    }
    
    public Student getMaxStudentGpa(int flag) {
        Student student;
        if (flag == SERIAL) {
            student = studentList.stream()
                    .max(Comparator.comparing(Student::getGrade))
                    .orElseThrow(NoSuchElementException::new);
        }
        else {
            student = studentList.parallelStream()
                    .max(Comparator.comparing(Student::getGrade))
                    .orElseThrow(NoSuchElementException::new);
        }
        return student;
    }
    
    public Map<Integer, Double> getGradeAverageGpa(int flag) {
        Map<Integer, Double> stdList = new HashMap<>();
        if (flag == SERIAL) {
            stdList = studentList.stream()
                    .collect(Collectors.groupingBy(Student::getDeptId,
                            Collectors.averagingInt(Student::getGrade)));
        }
        else {
            stdList = studentList.parallelStream()
                    .collect(Collectors.groupingBy(Student::getDeptId,
                            Collectors.averagingInt(Student::getGrade)));
        }
        
        return stdList;
    }
    
    public Map<Integer, Double> getCourseAverageGpa(int flag) {
        Map<Integer, Double> stdList = new HashMap<>();
        if (flag == SERIAL) {
            stdList = studentList.stream()
                    .collect(Collectors.groupingBy(Student::getCourseNumber,
                            Collectors.averagingInt(Student::getGrade)));
        }
        else {
            stdList = studentList.parallelStream()
                    .collect(Collectors.groupingBy(Student::getCourseNumber,
                            Collectors.averagingInt(Student::getGrade)));
        }
        
        return stdList;
    }
}

class Student {
    int studentId, deptId, courseNumber, credits, grade;
    Date courseDate;
    
    
    public Student(int studentId, int deptId, int courseNumber, Date courseDate,
            int credits, int grade) {
        setStudentId(studentId);
        setDeptId(deptId);
        setCourseNumber(courseNumber);
        setCourseDate(courseDate);
        setCredits(credits);
        setGrade(grade);
    }

    public int getStudentId() {
        return studentId;
    }

    public void setStudentId(int studentId) {
        this.studentId = studentId;
    }

    public int getDeptId() {
        return deptId;
    }

    public void setDeptId(int deptId) {
        this.deptId = deptId;
    }

    public int getCourseNumber() {
        return courseNumber;
    }

    public void setCourseNumber(int courseNumber) {
        this.courseNumber = courseNumber;
    }

    public int getCredits() {
        return credits;
    }

    public void setCredits(int credits) {
        this.credits = credits;
    }

    public int getGrade() {
        return grade;
    }

    public void setGrade(int grade) {
        this.grade = grade;
    }

    public Date getCourseDate() {
        return courseDate;
    }

    public void setCourseDate(Date courseDate) {
        this.courseDate = courseDate;
    }

    @Override
    public String toString() {
        return "Student{" + "studentId=" + studentId + ", credits=" + credits + ", grade=" + grade + '}';
    }
    
    
}
