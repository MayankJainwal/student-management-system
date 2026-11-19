package com.example.studentmanagement.service;

import com.example.studentmanagement.model.Student;
import com.example.studentmanagement.repository.StudentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

@Service
public class StudentService {

    private final StudentRepository studentRepository;

    @Autowired
    public StudentService(StudentRepository studentRepository) {
        this.studentRepository = studentRepository;
    }

    // 1. Add a new student with validation
    public Student addStudent(Student student) {
        if (student.getId() == null) {
            throw new IllegalArgumentException("Student ID is required.");
        }

        // Rule: Prevent duplicate student IDs
        if (studentRepository.existsById(student.getId())) {
            throw new IllegalArgumentException("Student with ID " + student.getId() + " already exists.");
        }

        // Rule: Validate scores between 0 and 100
        validateScores(student.getProgrammingScore(), student.getMathematicsScore());

        return studentRepository.save(student);
    }

    // 2. Retrieve all students
    public List<Student> getAllStudents() {
        return studentRepository.findAll();
    }

    // 3. Search student by ID
    public Student getStudentById(Long id) {
        return studentRepository.findById(id).orElse(null);
    }

    // 4. Search student by Email
    public Student searchByEmail(String email) {
        return studentRepository.findByEmail(email).orElse(null);
    }

    // 5. Search students by Course
    public List<Student> searchByCourse(String course) {
        return studentRepository.findByCourseIgnoreCase(course);
    }

    // 6. Update student scores and skills
    public Student updateStudent(Long id, Student updatedData) {
        Optional<Student> optionalStudent = studentRepository.findById(id);
        if (optionalStudent.isEmpty()) {
            return null; // Controller will return 404 Not Found
        }

        // Validate updated scores
        validateScores(updatedData.getProgrammingScore(), updatedData.getMathematicsScore());

        Student existingStudent = optionalStudent.get();
        existingStudent.setProgrammingScore(updatedData.getProgrammingScore());
        existingStudent.setMathematicsScore(updatedData.getMathematicsScore());

        if (updatedData.getTechnicalSkills() != null) {
            existingStudent.setTechnicalSkills(updatedData.getTechnicalSkills());
        }

        // Also update personal details if provided
        if (updatedData.getName() != null && !updatedData.getName().trim().isEmpty()) {
            existingStudent.setName(updatedData.getName());
        }
        if (updatedData.getEmail() != null && !updatedData.getEmail().trim().isEmpty()) {
            existingStudent.setEmail(updatedData.getEmail());
        }
        if (updatedData.getCourse() != null && !updatedData.getCourse().trim().isEmpty()) {
            existingStudent.setCourse(updatedData.getCourse());
        }

        return studentRepository.save(existingStudent);
    }

    // 7. Delete student by ID
    public boolean deleteStudent(Long id) {
        if (!studentRepository.existsById(id)) {
            return false;
        }
        studentRepository.deleteById(id);
        return true;
    }

    // 8. Generate merit list sorted by average score descending
    public List<Student> getMeritList() {
        List<Student> students = studentRepository.findAll();

        // Sort students using Comparator in descending order of average score
        students.sort(new Comparator<Student>() {
            @Override
            public int compare(Student s1, Student s2) {
                return Double.compare(s2.calculateAverage(), s1.calculateAverage());
            }
        });

        return students;
    }

    // 9. Identify students who have the same technical skills (order independent)
    public List<List<Student>> getStudentsWithSameTechnicalSkills() {
        List<Student> allStudents = studentRepository.findAll();
        Map<Set<String>, List<Student>> skillGroupMap = new HashMap<>();

        for (Student student : allStudents) {
            if (student.getTechnicalSkills() != null && !student.getTechnicalSkills().isEmpty()) {
                // Normalize skill strings: trim spaces and convert to lowercase for fair comparison
                Set<String> normalizedSkills = new HashSet<>();
                for (String skill : student.getTechnicalSkills()) {
                    if (skill != null && !skill.trim().isEmpty()) {
                        normalizedSkills.add(skill.trim().toLowerCase());
                    }
                }

                if (!normalizedSkills.isEmpty()) {
                    if (!skillGroupMap.containsKey(normalizedSkills)) {
                        skillGroupMap.put(normalizedSkills, new ArrayList<>());
                    }
                    skillGroupMap.get(normalizedSkills).add(student);
                }
            }
        }

        // Return only groups that contain 2 or more students sharing the exact skills
        List<List<Student>> result = new ArrayList<>();
        for (List<Student> group : skillGroupMap.values()) {
            if (group.size() > 1) {
                result.add(group);
            }
        }

        return result;
    }

    // 10. Display students who need improvement in programming (< 50) and have math score >= 75
    public List<Student> getStudentsNeedingProgrammingImprovementWithMathStrength() {
        List<Student> allStudents = studentRepository.findAll();
        List<Student> filteredStudents = new ArrayList<>();

        for (Student student : allStudents) {
            // Programming < 50 (Needs Improvement) and Mathematics >= 75
            if (student.getProgrammingScore() < 50.0 && student.getMathematicsScore() >= 75.0) {
                filteredStudents.add(student);
            }
        }

        return filteredStudents;
    }

    // Helper validation method
    private void validateScores(double programmingScore, double mathematicsScore) {
        if (programmingScore < 0 || programmingScore > 100) {
            throw new IllegalArgumentException("Programming score must be between 0 and 100. Provided: " + programmingScore);
        }
        if (mathematicsScore < 0 || mathematicsScore > 100) {
            throw new IllegalArgumentException("Mathematics score must be between 0 and 100. Provided: " + mathematicsScore);
        }
    }
}
