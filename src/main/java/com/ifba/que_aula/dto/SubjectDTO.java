package com.ifba.que_aula.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class SubjectDTO {
    @NotBlank
    private String code;

    @NotBlank
    private String name;

    @NotNull
    @Min(0)
    @Max(6)
    private Integer semester;

    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public Integer getSemester() { return semester; }
    public void setSemester(Integer semester) { this.semester = semester; }
}