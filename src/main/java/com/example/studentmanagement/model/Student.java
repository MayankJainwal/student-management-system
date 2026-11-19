package com.example.studentmanagement.model;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Table;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "students")
public class Student {

    @Id
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String course;

    @Column(nullable = false)
    private double programmingScore;

    @Column(nullable = false)
    private double mathematicsScore;

    @ElementCollection
    @CollectionTable(name = "student_skills", joinColumns = @JoinColumn(name = "student_id"))
    @Column(name = "skill")
    private List<String> technicalSkills = new ArrayList<>();

  
    public Student() {
    }

    // Parameterized constructor
    public Student(Long id, String name, String email, String course, double programmingScore, double mathematicsScore, List<String> technicalSkills) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.course = course;
        this.programmingScore = programmingScore;
        this.mathematicsScore = mathematicsScore;
        this.technicalSkills = technicalSkills != null ? technicalSkills : new ArrayList<>();
    }

    // getterssetters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getCourse() {
        return course;
    }

    public void setCourse(String course) {
        this.course = course;
    }

    public double getProgrammingScore() {
        return programmingScore;
    }

    public void setProgrammingScore(double programmingScore) {
        this.programmingScore = programmingScore;
    }

    public double getMathematicsScore() {
        return mathematicsScore;
    }

    public void setMathematicsScore(double mathematicsScore) {
        this.mathematicsScore = mathematicsScore;
    }

    public List<String> getTechnicalSkills() {
        return technicalSkills;
    }

    public void setTechnicalSkills(List<String> technicalSkills) {
        this.technicalSkills = technicalSkills != null ? technicalSkills : new ArrayList<>();
    }

    // business Logic / object methods

    // 1. Calculate average score
    public double calculateAverage() {
        return (this.programmingScore + this.mathematicsScore) / 2.0;
    }

    // return avg
    public double getAverageScore() {
        return calculateAverage();
    }

    // 2. aassign performance category
    public String getPerformanceCategory() {
        double avg = calculateAverage();
        if (avg >= 85.0) {
            return "Excellent";
        } else if (avg >= 70.0) {
            return "Good";
        } else if (avg >= 50.0) {
            return "Satisfactory";
        } else {
            return "Needs Improvement";
        }
    }

    // 3. Identify stronger subject
    public String getStrongerSubject() {
        if (this.programmingScore > this.mathematicsScore) {
            return "Programming";
        } else if (this.mathematicsScore > this.programmingScore) {
            return "Mathematics";
        } else {
            return "Both subjects are equal";
        }
    }
// parent class to exist
    @Override
    public String toString() {
        return "Student{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", email='" + email + '\'' +
                ", course='" + course + '\'' +
                ", programmingScore=" + programmingScore +
                ", mathematicsScore=" + mathematicsScore +
                ", technicalSkills=" + technicalSkills +
                ", averageScore=" + calculateAverage() +
                ", performanceCategory='" + getPerformanceCategory() + '\'' +
                ", strongerSubject='" + getStrongerSubject() + '\'' +
                '}';
    }
}
