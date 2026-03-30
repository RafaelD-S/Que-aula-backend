package com.ifba.que_aula.dto;

import java.util.List;

public class SubjectFullDTO {
    private final String code;
    private final String name;
    private final int semester;
    private final List<SectionFullDTO> sections;

    public SubjectFullDTO(String code, String name, int semester, List<SectionFullDTO> sections) {
        this.code = code;
        this.name = name;
        this.semester = semester;
        this.sections = sections;
    }

    public String getCode() { return code; }
    public String getName() { return name; }
    public int getSemester() { return semester; }
    public List<SectionFullDTO> getSections() { return sections; }
}
