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

import com.ifba.que_aula.dto.CourseResponseDTO;
import com.ifba.que_aula.dto.SectionFullDTO;
import com.ifba.que_aula.dto.SubjectDTO;
import com.ifba.que_aula.dto.SubjectFullDTO;
import com.ifba.que_aula.dto.SubjectResponseDTO;
import com.ifba.que_aula.models.entities.Subject;
import com.ifba.que_aula.service.SubjectService;
import com.ifba.que_aula.utils.ExpandField;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/subjects")
public class SubjectController {

    private final SubjectService service;

    public SubjectController(SubjectService service) {
        this.service = service;
    }

    @GetMapping
    public List<?> getAll(@RequestParam(required = false) String expand) {
        boolean includeSections = ExpandField.has(expand, ExpandField.SECTIONS);
        boolean includeCourses = ExpandField.has(expand, ExpandField.COURSES);

        if (!includeSections) {
            return service.findAll(expand).stream()
                    .map(this::toDTO)
                    .toList();
        }

        return service.findAll(expand).stream()
                .map(subject -> toFullDTO(subject, includeCourses))
                .toList();
    }

    @GetMapping("/{code}")
    public Object getByCode(
            @PathVariable String code,
            @RequestParam(required = false) String expand
    ) {
        boolean includeSections = ExpandField.has(expand, ExpandField.SECTIONS);
        boolean includeCourses = ExpandField.has(expand, ExpandField.COURSES);
        Subject subject = service.findByCode(code, expand);

        if (!includeSections) {
            return toDTO(subject);
        }

        return toFullDTO(subject, includeCourses);
    }

    @PostMapping
    public SubjectResponseDTO create(@Valid @RequestBody SubjectDTO dto) {
        Subject created = service.create(new Subject(dto.getCode(), dto.getName(), dto.getSemester()));
        return new SubjectResponseDTO(created.getCode(), created.getName(), created.getSemester());
    }

    @PutMapping("/{code}")
    public Subject update(@PathVariable String code, @Valid @RequestBody SubjectDTO dto) {
        Subject subject = new Subject(dto.getCode(), dto.getName(), dto.getSemester());
        return service.update(code, subject);
    }

    @DeleteMapping("/{code}")
    public void delete(@PathVariable String code) {
        service.delete(code);
    }

    private SubjectResponseDTO toDTO(Subject subject) {
        return new SubjectResponseDTO(subject.getCode(), subject.getName(), subject.getSemester());
    }

        private SubjectFullDTO toFullDTO(Subject subject, boolean includeCourses) {
        List<SectionFullDTO> sections = subject.getSections() == null
            ? List.of()
            : subject.getSections().stream()
                .map(section -> new SectionFullDTO(
                    section.getCode(),
                    section.getIsStrike(),
                    section.getSubject() != null ? section.getSubject().getCode() : null,
                    includeCourses && section.getCourses() != null
                        ? section.getCourses().stream()
                            .map(course -> new CourseResponseDTO(
                                course.getIdCourse(),
                                course.getSection() != null ? course.getSection().getCode() : null,
                                (course.getSection() != null && course.getSection().getSubject() != null)
                                    ? course.getSection().getSubject().getCode()
                                    : null,
                                course.getTeacher(),
                                course.getClassroom(),
                                course.getWeekday(),
                                course.getPeriodStart(),
                                course.getPeriodEnd()
                            ))
                            .toList()
                        : List.of()
                ))
                .toList();

        return new SubjectFullDTO(subject.getCode(), subject.getName(), subject.getSemester(), sections);
        }
}
