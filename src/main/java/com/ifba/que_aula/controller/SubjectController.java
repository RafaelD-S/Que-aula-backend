package com.ifba.que_aula.controller;

import java.util.List;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ifba.que_aula.dto.SectionResponseDTO;
import com.ifba.que_aula.dto.SubjectDTO;
import com.ifba.que_aula.dto.SubjectFullDTO;
import com.ifba.que_aula.dto.SubjectResponseDTO;
import com.ifba.que_aula.models.entities.Subject;
import com.ifba.que_aula.service.SubjectService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/subjects")
public class SubjectController {

    private final SubjectService service;

    public SubjectController(SubjectService service) {
        this.service = service;
    }

    @GetMapping
    public List<SubjectFullDTO> getAll() {
        return service.findAll().stream()
            .map(s -> new SubjectFullDTO(
                s.getCode(), 
                s.getName(),
                s.getSemester(),
                s.getSections().stream()
                    .map(section -> new SectionResponseDTO(
                        section.getCode(), 
                        section.getIsStrike(), 
                        section.getSubject().getCode()))
                    .toList()
            ))
            .toList();
    }

    @GetMapping("/{code}")
    public SubjectResponseDTO getById(@PathVariable String code) {
        Subject s = service.findById(code);
        return new SubjectResponseDTO(s.getCode(), s.getName(), s.getSemester());
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
}
