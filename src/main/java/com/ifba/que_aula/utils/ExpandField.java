package com.ifba.que_aula.utils;

public enum ExpandField {
    SECTIONS,
    COURSES,
    SUBJECT,
    SECTION;

    public static boolean has(String expand, ExpandField field) {
        if (expand == null || expand.isBlank()) {
            return false;
        }

        for (String e : expand.split(",")) {
            String token = e.trim().toUpperCase();
            if (token.equals(field.name())) {
                return true;
            }

            if (field == SECTIONS && token.equals("SECTION")) {
                return true;
            }
            if (field == SECTION && token.equals("SECTIONS")) {
                return true;
            }
            if (field == COURSES && token.equals("COURSE")) {
                return true;
            }
            if (field == SUBJECT && token.equals("SUBJECTS")) {
                return true;
            }
        }

        return false;
    }
}