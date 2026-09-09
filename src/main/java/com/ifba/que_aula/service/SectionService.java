package com.ifba.que_aula.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.ifba.que_aula.exception.ResourceNotFoundException;
import com.ifba.que_aula.models.entities.Section;
import com.ifba.que_aula.models.entities.SectionId;
import com.ifba.que_aula.models.entities.Subject;
import com.ifba.que_aula.repository.SectionRepository;
import com.ifba.que_aula.repository.SubjectRepository;
import com.ifba.que_aula.utils.ExpandField;

@Service
public class SectionService {

    private final SectionRepository sectionRepository;
    private final SubjectRepository subjectRepository;

    public SectionService(SectionRepository sectionRepository, SubjectRepository subjectRepository) {
        this.sectionRepository = sectionRepository;
        this.subjectRepository = subjectRepository;
    }

    public List<Section> findAll() {
        return sectionRepository.findAll();
    }

    public Section findById(String code, String subjectCode) {
        SectionId id = new SectionId(code, subjectCode);
        return sectionRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException(
                "Section não encontrada: " + code + " / " + subjectCode
            ));
    }

    public List<Section> findAll(String expand) {
        if (ExpandField.has(expand, ExpandField.COURSES)) {
            return sectionRepository.findAllWithCourses();
        }
        return sectionRepository.findAll(); // mínimo
    }

    public Section findBySubjectAndCode(String subjectCode, String code, String expand) {
        if (ExpandField.has(expand, ExpandField.COURSES)) {
            return sectionRepository.findBySubjectCodeAndCodeWithCourses(subjectCode, code)
                .orElseThrow(() -> new ResourceNotFoundException(
                    "Section não encontrada: " + code + " / " + subjectCode
                ));
        }
        return findById(code, subjectCode);
    }

    public Section create(Section section, String subjectCode) {
        Subject subject = subjectRepository.findById(subjectCode)
                .orElseThrow(() -> new ResourceNotFoundException("Subject não encontrado: " + subjectCode));
        section.setSubject(subject);
        return sectionRepository.save(section);
    }

    public Section update(String code, String subjectCode, Section sectionDetails, String newSubjectCode) {
        Section section = findById(code, subjectCode);
        section.setIsStrike(sectionDetails.getIsStrike());
        Subject subject = subjectRepository.findById(newSubjectCode)
                .orElseThrow(() -> new ResourceNotFoundException("Subject não encontrado: " + newSubjectCode));
        section.setSubject(subject);
        return sectionRepository.save(section);
    }

    public void delete(String code, String subjectCode) {
        Section section = findById(code, subjectCode);
        sectionRepository.delete(section);
    }
}
