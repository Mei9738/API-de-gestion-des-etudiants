package org.example.project2.repository;

import org.example.project2.domain.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface StudentRepository extends JpaRepository<Student, UUID> {
    // JpaRepository already provides findAll(Pageable pageable) method
    Optional<Student> findByEmail(String email);
    boolean existsByEmail(String email);
}
