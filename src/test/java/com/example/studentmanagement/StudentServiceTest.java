package com.example.studentmanagement;

import com.example.studentmanagement.model.Student;
import com.example.studentmanagement.repository.StudentRepository;
import com.example.studentmanagement.service.StudentService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class StudentServiceTest {

    private StudentRepository studentRepository;
    private StudentService studentService;

    @BeforeEach
    public void setup() {
        studentRepository = Mockito.mock(StudentRepository.class);
        studentService = new StudentService(studentRepository);
    }

    @Test
    public void testAverageScoreAndPerformanceCategories() {
        Student s1 = new Student(1L, "Alice", "alice@example.com", "CSE", 90.0, 90.0, List.of("Java"));
        assertEquals(90.0, s1.calculateAverage());
        assertEquals("Excellent", s1.getPerformanceCategory());

        Student s2 = new Student(2L, "Bob", "bob@example.com", "CSE", 75.0, 75.0, List.of("Python"));
        assertEquals(75.0, s2.calculateAverage());
        assertEquals("Good", s2.getPerformanceCategory());

        Student s3 = new Student(3L, "Charlie", "charlie@example.com", "CSE", 60.0, 60.0, List.of("C++"));
        assertEquals(60.0, s3.calculateAverage());
        assertEquals("Satisfactory", s3.getPerformanceCategory());

        Student s4 = new Student(4L, "David", "david@example.com", "CSE", 40.0, 40.0, List.of("HTML"));
        assertEquals(40.0, s4.calculateAverage());
        assertEquals("Needs Improvement", s4.getPerformanceCategory());
    }

    @Test
    public void testStrongerSubject() {
        Student s1 = new Student(1L, "Alice", "alice@example.com", "CSE", 90.0, 80.0, List.of("Java"));
        assertEquals("Programming", s1.getStrongerSubject());

        Student s2 = new Student(2L, "Bob", "bob@example.com", "CSE", 70.0, 85.0, List.of("Python"));
        assertEquals("Mathematics", s2.getStrongerSubject());

        Student s3 = new Student(3L, "Charlie", "charlie@example.com", "CSE", 80.0, 80.0, List.of("C++"));
        assertEquals("Both subjects are equal", s3.getStrongerSubject());
    }

    @Test
    public void testValidationDuplicateId() {
        Student s1 = new Student(101L, "Alice", "alice@example.com", "CSE", 80.0, 85.0, List.of("Java"));
        when(studentRepository.existsById(101L)).thenReturn(true);

        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            studentService.addStudent(s1);
        });
        assertTrue(exception.getMessage().contains("already exists"));
    }

    @Test
    public void testValidationInvalidScores() {
        Student s1 = new Student(101L, "Alice", "alice@example.com", "CSE", 105.0, 85.0, List.of("Java"));
        when(studentRepository.existsById(101L)).thenReturn(false);

        assertThrows(IllegalArgumentException.class, () -> {
            studentService.addStudent(s1);
        });

        Student s2 = new Student(102L, "Bob", "bob@example.com", "CSE", 85.0, -5.0, List.of("Java"));
        assertThrows(IllegalArgumentException.class, () -> {
            studentService.addStudent(s2);
        });
    }

    @Test
    public void testMeritListSortingDescending() {
        Student s1 = new Student(1L, "Alice", "alice@example.com", "CSE", 70.0, 70.0, List.of("Java")); // Avg = 70
        Student s2 = new Student(2L, "Bob", "bob@example.com", "CSE", 95.0, 95.0, List.of("Python")); // Avg = 95
        Student s3 = new Student(3L, "Charlie", "charlie@example.com", "CSE", 85.0, 85.0, List.of("C++")); // Avg = 85

        when(studentRepository.findAll()).thenReturn(new ArrayList<>(Arrays.asList(s1, s2, s3)));

        List<Student> meritList = studentService.getMeritList();
        assertEquals(3, meritList.size());
        assertEquals("Bob", meritList.get(0).getName());
        assertEquals("Charlie", meritList.get(1).getName());
        assertEquals("Alice", meritList.get(2).getName());
    }

    @Test
    public void testSameTechnicalSkillsOrderIndependent() {
        Student s1 = new Student(1L, "Alice", "alice@example.com", "CSE", 80.0, 80.0, List.of("Java", "MySQL"));
        Student s2 = new Student(2L, "Bob", "bob@example.com", "CSE", 80.0, 80.0, List.of("mysql", "java")); // different order and case
        Student s3 = new Student(3L, "Charlie", "charlie@example.com", "CSE", 80.0, 80.0, List.of("Python"));

        when(studentRepository.findAll()).thenReturn(Arrays.asList(s1, s2, s3));

        List<List<Student>> groups = studentService.getStudentsWithSameTechnicalSkills();
        assertEquals(1, groups.size());
        assertEquals(2, groups.get(0).size());
    }

    @Test
    public void testProgrammingImprovementWithMathStrength() {
        Student s1 = new Student(1L, "Alice", "alice@example.com", "CSE", 45.0, 80.0, List.of("Java")); // Prog < 50, Math >= 75 -> matches
        Student s2 = new Student(2L, "Bob", "bob@example.com", "CSE", 55.0, 80.0, List.of("Python")); // Prog >= 50 -> no
        Student s3 = new Student(3L, "Charlie", "charlie@example.com", "CSE", 40.0, 70.0, List.of("C++")); // Math < 75 -> no

        when(studentRepository.findAll()).thenReturn(Arrays.asList(s1, s2, s3));

        List<Student> result = studentService.getStudentsNeedingProgrammingImprovementWithMathStrength();
        assertEquals(1, result.size());
        assertEquals("Alice", result.get(0).getName());
    }
}
