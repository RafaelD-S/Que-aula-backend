package com.ifba.que_aula.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.ifba.que_aula.models.entities.Section;
import com.ifba.que_aula.models.entities.SectionId;

public interface SectionRepository extends JpaRepository<Section, SectionId> {
    @EntityGraph(attributePaths = {"courses"})
    @Query("SELECT s FROM Section s")
    List<Section> findAllWithCourses();

    @EntityGraph(attributePaths = {"courses"})
    @Query("SELECT s FROM Section s WHERE s.id.subjectCode = :subjectCode AND s.id.code = :code")
    Optional<Section> findBySubjectCodeAndCodeWithCourses(
            @Param("subjectCode") String subjectCode,
            @Param("code") String code
    );

    @EntityGraph(attributePaths = {"courses"})
    @Query("SELECT s FROM Section s WHERE s.id.subjectCode = :subjectCode")
    List<Section> findAllBySubjectCodeWithCourses(@Param("subjectCode") String subjectCode);
}
