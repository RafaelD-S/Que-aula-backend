package com.ifba.que_aula.dto;

public class SubjectResponseDTO {
    private final String code;
    private final String name;
    private final int semester;

    public SubjectResponseDTO(String code, String name, int semester) {
        this.code = code;
        this.name = name;
        this.semester = semester;
    }

    public String getCode() { return code; }
    public String getName() { return name; }
    public int getSemester() { return semester; }
}
