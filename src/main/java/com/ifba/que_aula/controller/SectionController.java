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
import com.ifba.que_aula.dto.SectionDTO;
import com.ifba.que_aula.dto.SectionFullDTO;
import com.ifba.que_aula.dto.SectionResponseDTO;
import com.ifba.que_aula.models.entities.Section;
import com.ifba.que_aula.service.SectionService;
import com.ifba.que_aula.utils.ExpandField;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/sections")
public class SectionController {

    private final SectionService service;

    public SectionController(SectionService service) {
        this.service = service;
    }

    @GetMapping
    public List<?> getAll(@RequestParam(required = false) String expand) {
        boolean includeCourses = ExpandField.has(expand, ExpandField.COURSES);

        if (!includeCourses) {
            return service.findAll(expand).stream()
                    .map(this::toResponseDTO)
                    .toList();
        }

        return service.findAll(expand).stream()
                .map(this::toFullDTO)
                .toList();
    }

    @GetMapping("/{subjectCode}/{code}")
    public Object getByCode(
            @PathVariable String subjectCode,
            @PathVariable String code,
            @RequestParam(required = false) String expand
    ) {
        Section section = service.findBySubjectAndCode(subjectCode, code, expand);
        if (ExpandField.has(expand, ExpandField.COURSES)) {
            return toFullDTO(section);
        }
        return toResponseDTO(section);
    }

    @PostMapping
    public SectionResponseDTO create(@Valid @RequestBody SectionDTO dto) {
        Section s = new Section(dto.getCode(), dto.getIsStrike(), null);
        Section created = service.create(s, dto.getSubjectCode());
        return new SectionResponseDTO(
            created.getCode(),
            created.getIsStrike(),
            created.getSubject().getCode()
        );
    }

    @PutMapping("/{subjectCode}/{code}")
    public Section update(
            @PathVariable String subjectCode,
            @PathVariable String code,
            @Valid @RequestBody SectionDTO dto
    ) {
        Section section = new Section(dto.getCode(), dto.getIsStrike(), null);
        return service.update(code, subjectCode, section, dto.getSubjectCode());
    }

    @DeleteMapping("/{subjectCode}/{code}")
    public void delete(@PathVariable String subjectCode, @PathVariable String code) {
        service.delete(code, subjectCode);
    }

    private SectionFullDTO toFullDTO(Section section) {
        List<CourseResponseDTO> courses = section.getCourses() == null
            ? List.of()
            : section.getCourses().stream()
                .map(c -> new CourseResponseDTO(
                    c.getIdCourse(),
                    c.getSection() != null ? c.getSection().getCode() : null,
                    (c.getSection() != null && c.getSection().getSubject() != null)
                        ? c.getSection().getSubject().getCode()
                        : null,
                    c.getTeacher(),
                    c.getClassroom(),
                    c.getWeekday(),
                    c.getPeriodStart(),
                    c.getPeriodEnd()
                ))
                .toList();

        return new SectionFullDTO(
            section.getCode(),
            section.getIsStrike(),
            section.getSubject() != null ? section.getSubject().getCode() : null,
            courses
        );
    }

    private SectionResponseDTO toResponseDTO(Section section) {
        return new SectionResponseDTO(
                section.getCode(),
                section.getIsStrike(),
                section.getSubject() != null ? section.getSubject().getCode() : null
        );
    }
}
