package com.ifba.que_aula.models.entities;

import java.util.List;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;

@Entity
@Table(name = "subject")
public class Subject {
    @Id
    @Column(unique = true, nullable = false, name = "code")
    private String code;

    @Column(unique = true, nullable = false, name = "name")
    private String name;

    @Column(nullable = false, name = "semester")
    @Min(0)
    @Max(6)
    private int semester;

    @OneToMany(mappedBy = "subject")
    private List<Section> sections;

    public Subject() {}

    public Subject(String code, String name, int semester) {
        this.code = code;
        this.name = name;
        this.semester = semester;
    }

    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public int getSemester() { return semester; }
    public void setSemester(int semester) { this.semester = semester; }

    public List<Section> getSections() { return sections; }
    public void setSections(List<Section> sections) { this.sections = sections; }
}
