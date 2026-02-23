package com.ifba.que_aula.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.ifba.que_aula.exception.ResourceNotFoundException;
import com.ifba.que_aula.models.entities.Subject;
import com.ifba.que_aula.repository.SubjectRepository;

@Service
public class SubjectService {

    private final SubjectRepository repository;

    public SubjectService(SubjectRepository repository) {
        this.repository = repository;
    }

    public List<Subject> findAll() {
        return repository.findAll();
    }

    public Subject findById(String code) {
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
