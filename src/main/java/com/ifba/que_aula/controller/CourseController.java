package com.ifba.que_aula.controller;


import java.util.List;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.ifba.que_aula.dto.CourseDTO;
import com.ifba.que_aula.dto.CourseResponseDTO;
import com.ifba.que_aula.models.entities.Course;
import com.ifba.que_aula.service.CourseService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/courses")
public class CourseController {

    private final CourseService service;

    public CourseController(CourseService service) {
        this.service = service;
    }

    @GetMapping
    public List<CourseResponseDTO> getAll(@RequestParam(required = false) String expand) {
        return service.findAll(expand).stream()
            .map(this::toDTO)
            .toList();
    }

    @GetMapping("/{id}")
    public CourseResponseDTO getById(
            @PathVariable Long id,
            @RequestParam(required = false) String expand
    ) {
        return toDTO(service.findById(id, expand));
    }

    @PostMapping
    public Course create(@Valid @RequestBody CourseDTO dto) {
        Course course = new Course(
                null,
                dto.getTeacher(),
                dto.getClassroom(),
            dto.getWeekday(),
            dto.getPeriodStart(),
            dto.getPeriodEnd()
        );

        return service.create(course, dto.getSectionCode(), dto.getSubjectCode());
    }

    @PutMapping("/{id}")
    public Course update(@PathVariable Long id, @Valid @RequestBody CourseDTO dto) {

        Course course = new Course(
                null,
                dto.getTeacher(),
                dto.getClassroom(),
            dto.getWeekday(),
            dto.getPeriodStart(),
            dto.getPeriodEnd()
        );

        return service.update(id, course, dto.getSectionCode(), dto.getSubjectCode());
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        service.delete(id);
    }

    private CourseResponseDTO toDTO(Course course) {
        String sectionCode = course.getSection() != null ? course.getSection().getCode() : null;
        String subjectCode = (course.getSection() != null && course.getSection().getSubject() != null)
                ? course.getSection().getSubject().getCode()
                : null;

        return new CourseResponseDTO(
                course.getIdCourse(),
                sectionCode,
                subjectCode,
                course.getTeacher(),
                course.getClassroom(),
                course.getWeekday(),
                course.getPeriodStart(),
                course.getPeriodEnd()
        );
    }
}
