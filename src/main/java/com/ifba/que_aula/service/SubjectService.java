package com.ifba.que_aula.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.ifba.que_aula.exception.ResourceNotFoundException;
import com.ifba.que_aula.models.entities.Subject;
import com.ifba.que_aula.repository.SectionRepository;
import com.ifba.que_aula.repository.SubjectRepository;
import com.ifba.que_aula.utils.ExpandField;

@Service
public class SubjectService {

    private final SubjectRepository repository;
    private final SectionRepository sectionRepository;

    public SubjectService(SubjectRepository repository, SectionRepository sectionRepository) {
        this.repository = repository;
        this.sectionRepository = sectionRepository;
    }

    public List<Subject> findAll() {
        return repository.findAll();
    }

    public List<Subject> findAll(String expand) {
        if (ExpandField.has(expand, ExpandField.SECTIONS)
                && ExpandField.has(expand, ExpandField.COURSES)) {
            List<Subject> subjects = repository.findAllWithSections();
            subjects.forEach(subject -> subject.setSections(
                    sectionRepository.findAllBySubjectCodeWithCourses(subject.getCode())
            ));
            return subjects;
        }
        if (ExpandField.has(expand, ExpandField.SECTIONS)) {
            return repository.findAllWithSections();
        }
        return repository.findAll(); // mínimo
    }

    public Subject findById(String code) {
        return repository.findById(code)
                .orElseThrow(() -> new ResourceNotFoundException("Subject não encontrado: " + code));
    }

    public Subject findByCode(String code, String expand) {
        if (ExpandField.has(expand, ExpandField.SECTIONS)
                && ExpandField.has(expand, ExpandField.COURSES)) {
            Subject subject = repository.findByCodeWithSections(code)
                .orElseThrow(() -> new ResourceNotFoundException("Subject não encontrado: " + code));
            subject.setSections(sectionRepository.findAllBySubjectCodeWithCourses(code));
            return subject;
        }
        if (ExpandField.has(expand, ExpandField.SECTIONS)) {
            return repository.findByCodeWithSections(code)
                .orElseThrow(() -> new ResourceNotFoundException("Subject não encontrado: " + code));
        }
        return repository.findById(code)
            .orElseThrow(() -> new ResourceNotFoundException("Subject não encontrado: " + code));
    }

    public Subject create(Subject subject) {
        return repository.save(subject);
    }

    public Subject update(String code, Subject subjectDetails) {
        Subject subject = findById(code);
        subject.setName(subjectDetails.getName());
        subject.setSemester(subjectDetails.getSemester());
        return repository.save(subject);
    }

    public void delete(String code) {
        Subject subject = findById(code);
        repository.delete(subject);
    }
}
