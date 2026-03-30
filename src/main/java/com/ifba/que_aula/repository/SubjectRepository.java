package com.ifba.que_aula.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.ifba.que_aula.models.entities.Subject;

public interface SubjectRepository extends JpaRepository<Subject, String> {
    @EntityGraph(attributePaths = {"sections"})
    @Query("SELECT s FROM Subject s")
    List<Subject> findAllWithSections();

    @EntityGraph(attributePaths = {"sections", "sections.courses"})
    @Query("SELECT s FROM Subject s")
    List<Subject> findAllWithSectionsAndCourses();

    @EntityGraph(attributePaths = {"sections"})
    @Query("SELECT s FROM Subject s WHERE s.code = :code")
    Optional<Subject> findByCodeWithSections(@Param("code") String code);

    @EntityGraph(attributePaths = {"sections", "sections.courses"})
    @Query("SELECT s FROM Subject s WHERE s.code = :code")
    Optional<Subject> findByCodeWithSectionsAndCourses(@Param("code") String code);
}
