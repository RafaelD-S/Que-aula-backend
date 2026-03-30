package com.ifba.que_aula.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.ifba.que_aula.exception.ResourceNotFoundException;
import com.ifba.que_aula.models.entities.Course;
import com.ifba.que_aula.models.entities.Section;
import com.ifba.que_aula.models.entities.SectionId;
import com.ifba.que_aula.repository.CourseRepository;
import com.ifba.que_aula.repository.SectionRepository;
import com.ifba.que_aula.utils.ExpandField;

@Service
public class CourseService {

    private final CourseRepository courseRepository;
    private final SectionRepository sectionRepository;

    public CourseService(CourseRepository courseRepository, SectionRepository sectionRepository) {
        this.courseRepository = courseRepository;
        this.sectionRepository = sectionRepository;
    }

    public List<Course> findAll() {
        return courseRepository.findAll();
    }

    public Course findById(Long id) {
        return courseRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Course não encontrado: " + id));
    }

    public List<Course> findAll(String expand) {
        if (ExpandField.has(expand, ExpandField.SECTION) || ExpandField.has(expand, ExpandField.SUBJECT)) {
            return courseRepository.findAllWithSectionAndSubject();
        }
        return courseRepository.findAll(); 
    }

    public Course findById(Long id, String expand) {
        if (ExpandField.has(expand, ExpandField.SECTION) || ExpandField.has(expand, ExpandField.SUBJECT)) {
            return courseRepository.findByIdWithSectionAndSubject(id)
                    .orElseThrow(() -> new ResourceNotFoundException("Course não encontrado: " + id));
        }
        return courseRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Course não encontrado: " + id));
    }

    public Course create(Course course, String sectionCode, String subjectCode) {
        SectionId id = new SectionId(sectionCode, subjectCode);
        Section section = sectionRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException(
                "Section não encontrada: " + sectionCode + " / " + subjectCode
            ));
        course.setSection(section);
        return courseRepository.save(course);
    }

    public Course update(Long id, Course courseDetails, String sectionCode, String subjectCode) {
        Course course = findById(id);
        SectionId sectionId = new SectionId(sectionCode, subjectCode);
        Section section = sectionRepository.findById(sectionId)
            .orElseThrow(() -> new ResourceNotFoundException(
                "Section não encontrada: " + sectionCode + " / " + subjectCode
            ));

        course.setSection(section);
        course.setTeacher(courseDetails.getTeacher());
        course.setClassroom(courseDetails.getClassroom());
        course.setWeekday(courseDetails.getWeekday());
        course.setPeriodStart(courseDetails.getPeriodStart());
        course.setPeriodEnd(courseDetails.getPeriodEnd());

        return courseRepository.save(course);
    }

    public void delete(Long id) {
        Course course = findById(id);
        courseRepository.delete(course);
    }
}
