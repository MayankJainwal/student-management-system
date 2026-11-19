package com.example.studentmanagement.controller;

import com.example.studentmanagement.model.Student;
import com.example.studentmanagement.service.StudentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/students")
public class StudentController {

    private final StudentService studentService;

    @Autowired
    public StudentController(StudentService studentService) {
        this.studentService = studentService;
    }

    // 1. Add a student
    @PostMapping
    public ResponseEntity<?> addStudent(@RequestBody Student student) {
        try {
            Student createdStudent = studentService.addStudent(student);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdStudent);
        } catch (IllegalArgumentException e) {
            Map<String, String> response = new HashMap<>();
            response.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    // 2. Retrieve all students
    @GetMapping
    public ResponseEntity<List<Student>> getAllStudents() {
        List<Student> students = studentService.getAllStudents();
        return ResponseEntity.ok(students);
    }

    // 3. Display merit list (sorted by average score descending)
    // NOTE: Placed before /{id} so Spring Boot doesn't confuse "merit-list" with a student ID
    @GetMapping("/merit-list")
    public ResponseEntity<List<Student>> getMeritList() {
        List<Student> meritList = studentService.getMeritList();
        return ResponseEntity.ok(meritList);
    }

    // 4. Identify students who have the same technical skills
    @GetMapping("/same-skills")
    public ResponseEntity<List<List<Student>>> getSameSkills() {
        List<List<Student>> groups = studentService.getStudentsWithSameTechnicalSkills();
        return ResponseEntity.ok(groups);
    }

    // 5. Display students needing programming improvement (< 50) and math >= 75
    @GetMapping("/programming-improvement")
    public ResponseEntity<List<Student>> getProgrammingImprovement() {
        List<Student> students = studentService.getStudentsNeedingProgrammingImprovementWithMathStrength();
        return ResponseEntity.ok(students);
    }

    // 6. Search students using query parameters (/students/search?email=... or /students/search?course=...)
    @GetMapping("/search")
    public ResponseEntity<?> searchStudents(
            @RequestParam(required = false) String email,
            @RequestParam(required = false) String course) {

        if (email != null && !email.trim().isEmpty()) {
            Student student = studentService.searchByEmail(email);
            if (student == null) {
                Map<String, String> response = new HashMap<>();
                response.put("error", "No student found with email: " + email);
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            }
            return ResponseEntity.ok(student);
        }

        if (course != null && !course.trim().isEmpty()) {
            List<Student> students = studentService.searchByCourse(course);
            return ResponseEntity.ok(students);
        }

        Map<String, String> response = new HashMap<>();
        response.put("error", "Please provide 'email' or 'course' query parameter for search.");
        return ResponseEntity.badRequest().body(response);
    }

    // 7. Search by email via path variable (/students/search/email/{email})
    @GetMapping("/search/email/{email}")
    public ResponseEntity<?> searchByEmail(@PathVariable String email) {
        Student student = studentService.searchByEmail(email);
        if (student == null) {
            Map<String, String> response = new HashMap<>();
            response.put("error", "No student found with email: " + email);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
        return ResponseEntity.ok(student);
    }

    // 8. Search by course via path variable (/students/search/course/{course})
    @GetMapping("/search/course/{course}")
    public ResponseEntity<List<Student>> searchByCourse(@PathVariable String course) {
        List<Student> students = studentService.searchByCourse(course);
        return ResponseEntity.ok(students);
    }

    // 9. Search for a student by ID
    @GetMapping("/{id}")
    public ResponseEntity<?> getStudentById(@PathVariable Long id) {
        Student student = studentService.getStudentById(id);
        if (student == null) {
            Map<String, String> response = new HashMap<>();
            response.put("error", "Student not found with ID: " + id);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
        return ResponseEntity.ok(student);
    }

    // 10. Update student scores and skills
    @PutMapping("/{id}")
    public ResponseEntity<?> updateStudent(@PathVariable Long id, @RequestBody Student updatedData) {
        try {
            Student updatedStudent = studentService.updateStudent(id, updatedData);
            if (updatedStudent == null) {
                Map<String, String> response = new HashMap<>();
                response.put("error", "Student not found with ID: " + id);
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            }
            return ResponseEntity.ok(updatedStudent);
        } catch (IllegalArgumentException e) {
            Map<String, String> response = new HashMap<>();
            response.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    // 11. Delete a student record
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteStudent(@PathVariable Long id) {
        boolean deleted = studentService.deleteStudent(id);
        if (!deleted) {
            Map<String, String> response = new HashMap<>();
            response.put("error", "Student not found with ID: " + id);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
        Map<String, String> response = new HashMap<>();
        response.put("message", "Student with ID " + id + " has been deleted successfully.");
        return ResponseEntity.ok(response);
    }
}
