package com.ifba.que_aula.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.ifba.que_aula.models.entities.Course;

public interface CourseRepository extends JpaRepository<Course, Long> {
    @EntityGraph(attributePaths = {"section", "section.subject"})
    @Query("SELECT c FROM Course c")
    List<Course> findAllWithSectionAndSubject();

    @EntityGraph(attributePaths = {"section", "section.subject"})
    @Query("SELECT c FROM Course c WHERE c.idCourse = :id")
    Optional<Course> findByIdWithSectionAndSubject(@Param("id") Long id);
}
